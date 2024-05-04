package com.shatteredpixel.shatteredpixeldungeon.editor;

import com.shatteredpixel.shatteredpixeldungeon.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.DefaultStatsCache;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditCompWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.EToolbar;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.FindInBag;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.WndEditorInv;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Items;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.CustomTileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.MobSpriteItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.CustomParticle;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.Zone;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.WndZones;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.dungeon.WndSelectDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.BlacksmithQuest;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.*;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.ZoneActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ToastWithButtons;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomTileLoader;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTerrainTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import com.watabou.noosa.*;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.*;

import java.io.IOException;
import java.util.*;

public class EditorScene extends DungeonScene {

    private static EditorScene scene;

    private DungeonTerrainTilemap tiles;


    private static CustomLevel customLevel;


    protected MenuPane menu;
    private UndoPane undo;
    private EToolbar toolbar;


    private ZoneView zoneGroup;



    private Group transitionIndicators;
    private Map<LevelTransition, BitmapText> transitionIndicatorsMap;
    private Group realMobs;

    private static boolean displayZones = false;


    public static void start() {
        Dungeon.quickslot.reset();
        QuickSlotButton.reset();
        BlacksmithQuest.reset();
        Statistics.reset();
        Dungeon.hero = null;
        Dungeon.branch = 0;
        GamesInProgress.curSlot = 0;
        FileUtils.setDefaultFileType(FileUtils.getFileTypeForCustomDungeons());
    }

    private static boolean firstTimeOpening = true;
    public static boolean openDifferentLevel = true;
    public static float setCameraZoomWhenOpen = -1;
    private static PointF mainCameraPos;

    public static void open(CustomLevel customLevel) {
        displayZones = false;
        if (customLevel != EditorScene.customLevel) {
            String oldLvlName;
            if (EditorScene.customLevel != null) {
                oldLvlName = EditorScene.customLevel.levelScheme.getCustomDungeon() == Dungeon.customDungeon ? EditorScene.customLevel.name : null;
                EditorScene.customLevel.levelScheme.unloadLevel();
            } else oldLvlName = null;
            if (openDifferentLevel) {
                mainCameraPos = null;
                Undo.reset();
                ZonePrompt.setSelectedZone(ZonePrompt.getFirstZoneAvailable(customLevel));
            }
            openDifferentLevel = true;
            Items.updateKeys(oldLvlName, customLevel.name);
        }
        EditorScene.customLevel = customLevel;
        Dungeon.levelName = customLevel.name;
        Dungeon.customDungeon.setLastEditedFloor(customLevel.name);
        PathFinder.setMapSize(customLevel.width(), customLevel.height());
        SandboxPixelDungeon.switchNoFade(EditorScene.class);
        firstTimeOpening = false;
    }

    public static void close() {
        if (customLevel != null) {
            EditorScene.customLevel.levelScheme.unloadLevel();
            customLevel = null;
        }
    }

    public static void resetCameraPos() {
        mainCameraPos = null;
    }

    @Override
    public void create() {

        Dungeon.level = customLevel;//so changes aren't required everywhere,
        // but the preferred way to access the current level is still through the method
        Dungeon.levelName = Dungeon.level.name;

        for (Heap heap : Dungeon.level.heaps.valueList()) {
            for (Item item : heap.items) {
                item.image = Dungeon.customDungeon.getItemSpriteOnSheet(item);
            }
        }

        super.create();

        Dungeon.level.playLevelMusic();


        if (setCameraZoomWhenOpen >= 0) {
            Camera.main.zoom(GameMath.gate(minZoom, setCameraZoomWhenOpen, maxZoom));
            setCameraZoomWhenOpen = -1;
        } else Camera.main.zoom(GameMath.gate(minZoom, defaultZoom + SPDSettings.zoom(), maxZoom));
        Camera.main.edgeScroll.set(1);
        if (mainCameraPos != null) Camera.main.scroll = mainCameraPos;
        else {
            Camera.main.scroll.x = -(Camera.main.width - customLevel.width() * DungeonTilemap.SIZE) / 2f - DungeonTilemap.SIZE / 2f;
            Camera.main.scroll.y = -(Camera.main.height - customLevel.height() * DungeonTilemap.SIZE) / 2f;
            mainCameraPos = Camera.main.scroll;
        }

        scene = this;

        customBossTilemap = customBossWallsTilemap = null;

        initBasics();

        terrain.add(LevelColoring.getWall(true));

        transitionIndicators = new Group();
        add(transitionIndicators);
        transitionIndicatorsMap = new HashMap<>();
        updateTransitionIndicators();

        heaps = new Group();
        add(heaps);

        for (Heap heap : Dungeon.level.heaps.valueList()) {
            heap.destroySubicons();
            heap.initSubicons();
            heap.seen = true;
            addHeapSprite(heap);
        }

        emitters = new Group();

        mobs = new Group();
        add(mobs);

        realMobs = new Group();
        add(realMobs);

        for (Mob mob : Dungeon.level.mobs) {
            addMobSprite(mob);
        }

        customWalls = new Group();
        add(customWalls);

        for (CustomTilemap visual : Dungeon.level.customWalls) {
            addCustomWall(visual);
        }

        add(emitters);

        gases = new Group();
        add(gases);

        for (Blob blob : Dungeon.level.blobs.values()) {
            blob.emitter = null;
            addBlobSprite(blob);
        }

        for (CustomParticle particle : Dungeon.level.particles.values()) {
            particle.emitter = null;
            addParticleSprite(particle);
        }

        emoicons = new Group();
        add(emoicons);

        zoneGroup = new ZoneView();
        zoneGroup.visible = displayZones;
        add(zoneGroup);
        for (int cell = 0; cell < Dungeon.level.map.length; cell++) {
            zoneGroup.updateCell(cell, null);
        }

        add(cellSelector = new EditorCellSelector(tiles));

        int uiSize = SPDSettings.interfaceSize();

        menu = new MenuPane();
        menu.camera = uiCamera;
        menu.setPos(uiCamera.width - MenuPane.WIDTH, uiSize > 0 ? 0 : 0);
        add(menu);

        undo = new UndoPane();
        undo.camera = uiCamera;
        undo.setPos(0, 0);
        add(undo);

        sideControlPane = new SideControlPane(true);
        sideControlPane.camera = uiCamera;
        sideControlPane.setPos(0, undo.bottom() + (PixelScene.landscape() ? 5 : 10));
        add(sideControlPane);

        toolbar = new EToolbar();
        toolbar.camera = uiCamera;
        add(toolbar);

        toolbar.setRect(0, uiCamera.height - toolbar.height(), uiCamera.width, toolbar.height());


        fadeIn();

        cellSelector.listener = defaultCellListener;
        cellSelector.enabled = true;

        if (displayZones) {
            displayZones = false;
            setDisplayZoneState(true);
        }

        if (error != null) {
            EditorScene.show(new WndError(error));
            error = null;
        }
    }

    @Override
    protected void initAndAddDungeonTilemap() {
        tiles = new DungeonTerrainTilemap(0);
        terrain.add( tiles );
    }

    public static void setDisplayZoneState(boolean enable) {

        if (scene == null) {
            displayZones = enable;
            return;
        }

        if (displayZones == enable) return;

        scene.changeDisplayZoneModeButNoPrompt(enable);

        if (enable) {
            selectCell(zonesCellListener);
            scene.prompt(new ZonePrompt());
        } else scene.prompt((Component) null);
    }

    private void changeDisplayZoneModeButNoPrompt(boolean enable) {
        displayZones = enable;
        zoneGroup.visible = enable;
        ready();
    }

    public static void updateZoneColors() {
        if (scene != null) scene.zoneGroup.updateZoneColors();
    }

    public static boolean isDisplayZones() {
        return displayZones;
    }

    @Override
    public void update() {
        super.update();

        for (Gizmo g : toDestroy){
            g.destroy();
        }
        toDestroy.clear();
    }

    protected synchronized void prompt(Component newPrompt) {

        super.prompt(newPrompt);

        if (displayZones && !(newPrompt instanceof ToastWithButtons)) {
            changeDisplayZoneModeButNoPrompt(false);
            sideControlPane.setButtonEnabled(SideControlPane.ToggleZoneViewBtn.class, false);
        }
    }

    @Override
    protected void updateMapImpl() {
		if (Dungeon.level != null) {
			System.arraycopy(Dungeon.level.map, 0, Dungeon.level.visualMap, 0, Dungeon.level.map.length);
			Arrays.fill(Dungeon.level.visualRegions, LevelScheme.REGION_NONE);
			for (CustomTilemap vis : Dungeon.level.customTiles) {
				if (vis instanceof CustomTileLoader.SimpleCustomTile) {
					int cell = vis.tileX + vis.tileY * Dungeon.level.width();
					Dungeon.level.visualMap[cell] = ((CustomTileLoader.SimpleCustomTile) vis).imageTerrain;
					Dungeon.level.visualRegions[cell] = ((CustomTileLoader.SimpleCustomTile) vis).region;
				}
			}
		}

		revalidateBossCustomTiles();
        if (scene != null) {
            scene.tiles.updateMap();
            scene.visualGrid.updateMap();
            scene.terrainFeatures.updateMap();
            scene.barriers.updateMap();
            scene.arrowCells.updateMap();
            LevelColoring.allUpdateMap();
        }
    }

    @Override
    protected void updateMapImpl(int cell) {
		if (Dungeon.level != null && Dungeon.level.visualRegions[cell] == LevelScheme.REGION_NONE)
			Dungeon.level.visualMap[cell] = Dungeon.level.map[cell];

		revalidateBossCustomTiles();
        if (scene != null) {
            scene.tiles.updateMapCell(cell);
            scene.visualGrid.updateMapCell(cell);
            scene.terrainFeatures.updateMapCell(cell);
            scene.barriers.updateMapCell(cell);
            scene.arrowCells.updateMapCell(cell);
            LevelColoring.allUpdateMapCell(cell);
        }
    }

    public static void updateZoneCell(int cell, Zone zoneBefore) {
        if (scene != null) {
            scene.zoneGroup.updateCell(cell, zoneBefore);
            scene.terrainFeatures.updateMapCell(cell);
        }
    }

    public static void revalidateHeaps() {
        if (scene == null) return;
        scene.heaps.parent.erase(scene.heaps);
        scene.heaps.destroy();
        scene.heaps = new Group();
        scene.add(scene.heaps);
        for (Heap heap : Dungeon.level.heaps.valueList()) {
            heap.destroySubicons();
            heap.initSubicons();
            heap.seen = true;
            scene.addHeapSprite(heap);
        }
    }

    @Override
    public void addCustomTile(CustomTilemap visual) {
        super.addCustomTile(visual);
        if (visual instanceof CustomTilemap.BossLevelVisuals)
            customBossTilemap = (CustomTilemap.BossLevelVisuals) visual;
    }

    @Override
    public void addCustomWall(CustomTilemap visual) {
        super.addCustomWall(visual);
        if (visual instanceof CustomTilemap.BossLevelVisuals)
            customBossWallsTilemap = (CustomTilemap.BossLevelVisuals) visual;
    }

    private static CustomTilemap.BossLevelVisuals customBossTilemap, customBossWallsTilemap;
    public static void revalidateBossCustomTiles() {
        if (scene == null || Dungeon.level == null) return;

        if (customBossTilemap != null) {
            customBossTilemap.updateState();
        }
        if (customBossWallsTilemap != null) {
            customBossWallsTilemap.updateState();
        }
    }

    @Override
    protected void removeImpl(CustomTilemap visual) {
        if (visual instanceof CustomTileLoader.SimpleCustomTile) {
            int pos = visual.tileX + visual.tileY * customLevel.width();
            customLevel.visualMap[pos] = customLevel.map[pos];
            customLevel.visualRegions[pos] = 0;
        }
    }

    private void addTransitionSprite(LevelTransition transition) {
        if (transitionIndicatorsMap.containsKey(transition)) return;
        BitmapText text = new BitmapText(PixelScene.pixelFont);
        transitionIndicators.add(text);
        transitionIndicatorsMap.put(transition, text);
        updateTransitionIndicator(transition);
    }

    public static void remove(LevelTransition transition) {
        if (scene == null || transition == null) return;
        BitmapText text = scene.transitionIndicatorsMap.get(transition);
        if (text == null) {
            for (LevelTransition trans : scene.transitionIndicatorsMap.keySet()) {
                if (trans.departCell == transition.departCell && trans.destCell == transition.destCell
                        && trans.destLevel.equals(transition.destLevel)) {
                    //just assume that this is effectively the same transition (relevant for undo working after level was reloaded)
					text = scene.transitionIndicatorsMap.get(trans);
					break;
				}
			}
		}
		scene.transitionIndicators.remove(text);
		if (text == null) return;
		text.destroy();
		scene.transitionIndicatorsMap.remove(transition);
	}

	public static void add(LevelTransition transition) {
		if (scene == null) return;
		scene.addTransitionSprite(transition);
	}

	public static void updateTransitionIndicator(LevelTransition transition) {
        if (scene == null || transition == null) return;
        BitmapText text = scene.transitionIndicatorsMap.get(transition);
        if (text == null) return;
        text.text(Messages.get(LevelTransition.class, "to") + ": "
                + (transition.destLevel == null && transition.destBranch == 0 ? "" : EditorUtilies.getDispayName(transition)));
        text.hardlight(Window.TITLE_COLOR);
        text.scale.set(0.55f);
        text.measure();

        PointF pos = new PointF(
                PixelScene.align(Camera.main, ((transition.cell() % Dungeon.level.width()) + 0.5f) * DungeonTilemap.SIZE - text.width() * 0.5f),
                PixelScene.align(Camera.main, ((transition.cell() / Dungeon.level.width()) + 1.0f) * DungeonTilemap.SIZE - text.height() - DungeonTilemap.SIZE * 5 / 16f));
        text.point(pos);
        text.y += 5.5f;
    }

    public static void updateTransitionIndicators() {
        if (scene == null) return;
        scene.transitionIndicators.clear();
        scene.transitionIndicatorsMap.clear();
        for (LevelTransition transition : Dungeon.level.transitions.values()) {
            scene.addTransitionSprite(transition);
        }
    }

    @Override
    protected void addMobSprite(Mob mob) {
        CharSprite sprite = mob.sprite();
        sprite.visible = true;
        mobs.add(sprite);

        Mob defMob = DefaultStatsCache.getDefaultObject(mob.getClass());
        if (defMob == null && MobSpriteItem.canChangeSprite(mob)) defMob = Reflection.newInstance(mob.getClass());
        if (MobSpriteItem.isSpriteChanged(mob, sprite)) {
            sprite.realCharSprite = Reflection.newInstance(defMob.spriteClass);
            if (sprite.realCharSprite != null) {
                sprite.realCharSprite.subSprite = true;
                sprite.realCharSprite.scale.set(0.5f);
                realMobs.add(sprite.realCharSprite);
                sprite.realCharSprite.visible = true;
                sprite.realCharSprite.link(mob);
            }
        }
        sprite.link(mob);
        sortMobSprites();
    }

    // -------------------------------------------------------


    public static void add(Zone zone) {
        Dungeon.level.zoneMap.put(zone.getName(), zone);
        Dungeon.level.levelScheme.zones.add(zone.getName());
    }

    public static void remove(Zone zone) {
        Dungeon.level.zoneMap.remove(zone.getName());
        Dungeon.level.levelScheme.zones.remove(zone.getName());
        if (scene != null) scene.zoneGroup.updateZoneColors();
    }

    protected void selectCellImpl( CellSelector.Listener listener ) {
        if (cellSelector.listener != null && cellSelector.listener != defaultCellListener) {
            cellSelector.listener.onSelect(null);
        }
        cellSelector.listener = listener;
        cellSelector.enabled = true;
        if (listener.prompt() != null) prompt(listener.prompt());
        else prompt(listener.promptComp());
    }

    @Override
    protected boolean cancelCellSelectorImpl() {
        if (cellSelector.listener != null && cellSelector.listener != defaultCellListener) {
            cellSelector.resetKeyHold();
            cellSelector.cancel();
            return true;
        } else {
            return false;
        }
    }

    public static WndEditorInv selectItem(WndBag.ItemSelectorInterface listener) {
        cancel();

        WndEditorInv wnd = WndEditorInv.getBag(listener);
        if (scene != null) {
            show(wnd);
        } else {
            if (Game.scene() instanceof GameScene) GameScene.cancel();
            Game.scene().addToFront(wnd);
        }
        return wnd;
    }

	private static Throwable error;
	public static void catchError(Throwable t) {
		error = t;
		EditorScene.start();
		EditorScene.openDifferentLevel = false;
		WndSelectDungeon.openDungeon(Dungeon.customDungeon.getName());
	}

    public static void updateDepthIcon() {
        if (scene == null) return;
        scene.menu.updateDepthIcon();
    }

    public static void updateUndoButtons() {
        if (scene == null) return;
        scene.undo.updateStates();
    }

    @Override
    protected void readyImpl() {
        selectCell(displayZones ? zonesCellListener : defaultCellListener);
        QuickSlotButton.cancel();
    }

    @Override
    public synchronized void onPause() {
        try {
            CustomDungeonSaves.saveLevel(customLevel);
            CustomDungeonSaves.saveDungeon(Dungeon.customDungeon);
        } catch (IOException e) {
            SandboxPixelDungeon.reportException(e);
        }
    }

    @Override
    public void destroy() {

        if (scene == this) scene = null;

        //tell the actor thread to finish, then wait for it to complete any actions it may be doing.
//        if (!waitForActorThread( 4500, true )){
//            Throwable t = new Throwable();
//            t.setStackTrace(actorThread.getStackTrace());
//            throw new RuntimeException("timeout waiting for actor thread! ", t);
//        }
//
//        Emitter.freezeEmitters = false;

        if (Dungeon.customDungeon.getFloor(Dungeon.level.name) != null && error == null) {
            try {
                CustomDungeonSaves.saveDungeon(Dungeon.customDungeon);
                CustomDungeonSaves.saveLevel(Dungeon.level);//only save if not already deleted
            } catch (IOException e) {
                SandboxPixelDungeon.reportException(e);
            }
        }

        super.destroy();
    }

    public static CustomLevel getCustomLevel() {
        return customLevel;
    }


    private static final CellSelector.Listener defaultCellListener = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer cell) {
            if (cell == null) return;

            Item selected = EToolbar.getSelectedItem();

            if (selected instanceof EditorItem) ((EditorItem<?>) selected).place(cell);
        }

        @Override
        public void onRightClick(Integer cell) {
            if (cell != null && cell >= 0 && cell < Dungeon.level.length()) {
                EditorScene.showEditCellWindow(cell);
            }
        }

        @Override
        public void onMiddleClick(Integer cell) {
            putInQuickslot(cell);
        }

        @Override
        public String prompt() {
            return null;
        }

        @Override
        protected boolean dragClickEnabled() {
            return EditorScene.dragClickEnabled();
        }
    };

    private static final CellSelector.Listener zonesCellListener = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer cell) {
            if (cell == null || !Dungeon.level.insideMap(cell)) return;

            Zone selected = ZonePrompt.getSelectedZone();

            switch (ZonePrompt.mode) {
                case ADD:
                    if (selected == null) EditorScene.show(new WndZones.WndNewZone());
                    else Undo.addActionPart(new ZoneActionPart.Place(selected.getName(), cell));
                    break;
                case REMOVE:
                    Undo.addActionPart(new ZoneActionPart.Remove(null, cell));
//                    Undo.addActionPart(new ZoneActionPart.Remove(selected == null ? null : selected.getName(), cell));
                    break;
                case EDIT:
                    onRightClick(cell);
                    break;
            }
        }

        @Override
        public void onRightClick(Integer cell) {
            if (cell != null && cell >= 0 && cell < Dungeon.level.length()) {
                Zone zoneAt = Dungeon.level.zone[cell];
                if (zoneAt != null) EditorScene.show(new EditCompWindow(zoneAt));
            }
        }

        @Override
        public void onMiddleClick(Integer cell) {
            Zone zoneAt = Dungeon.level.zone[cell];
            if (zoneAt != null) ZonePrompt.setSelectedZone(zoneAt);
        }

        @Override
        public String prompt() {
            return null;
        }

        @Override
        public Component promptComp() {
            return new ZonePrompt();
        }

        @Override
        protected boolean dragClickEnabled() {
            return ZonePrompt.getSelectedZone() != null && ZonePrompt.mode != ZonePrompt.Mode.EDIT;
        }
    };

    public static boolean dragClickEnabled() {
        Item selected = EToolbar.getSelectedItem();

        if (selected instanceof EditorItem) {
            if (selected instanceof CustomTileItem) {
                CustomTilemap customTile = ((CustomTileItem) selected).getObject();
                return customTile.tileW == 1 && customTile.tileH == 1;
            }
            return true;
        }
        return false;
    }

    public static void putInQuickslot(Integer cell) {
        if (cell != null && cell >= 0 && cell < Dungeon.level.length()) {
            QuickSlotButton.set(new FindInBag(FindInBag.getObjAtCell(cell)).getAsInBag());
        }
    }

    public static void fillAllWithOneTerrain(Integer cell) {
        if (cell != null && cell >= 0 && cell < Dungeon.level.length()) {
            Item selected = EToolbar.getSelectedItem();

            if (selected instanceof EditorItem) {

                queue.add(cell);

                Level level = Dungeon.level;
                EditorItem<?> item = (EditorItem<?>) selected;
                int lvlWidth = level.width();

                CustomTilemap customTile = CustomTileItem.findAnyCustomTileAt(cell);

                if (customTile != null) {
                    while (!queue.isEmpty()) {
                        int c = queue.iterator().next();
                        queue.remove(c);
                        fillAllWithOneTerrainQueue(c, level.map[c], level.map, customTile, lvlWidth);
                    }
                } else if (TileItem.isTrapTerrainCell(level.map[cell]) && level.traps.get(cell) != null) {
                    while (!queue.isEmpty()) {
                        int c = queue.iterator().next();
                        queue.remove(c);
                        fillAllWithOneTerrainQueue(c, level.map[c], level.map, level.traps.get(c).getClass(), lvlWidth);
                    }
                } else {
                    while (!queue.isEmpty()) {
                        int c = queue.iterator().next();
                        queue.remove(c);
                        fillAllWithOneTerrainQueue(c, level.map[c], level.map, lvlWidth);
                    }
                }
                for (int c : changedCells)
                    item.place(c);
                changedCells.clear();
            }
        }
    }

    private static Set<Integer> changedCells = new HashSet<>();
    private static Set<Integer> queue = new HashSet<>();//avoid StackOverflowError

    public static void fillAllWithOneTerrainQueue(int cell, int terrainClick, int[] map, int lvlWidth) {

        changedCells.add(cell);
        for (int i : PathFinder.CIRCLE4) {
            int neighbor = i + cell;
            int xCoord = cell % lvlWidth;
            if (neighbor >= 0 && neighbor < map.length && !changedCells.contains(neighbor)
                    && (Math.abs(neighbor % lvlWidth - xCoord) <= 1) && map[neighbor] == terrainClick
                    && CustomTileItem.findAnyCustomTileAt(neighbor) == null)
                queue.add(neighbor);
        }
    }

    public static void fillAllWithOneTerrainQueue(int cell, int terrainClick, int[] map, Class<? extends Trap> onTrapClicked, int lvlWidth) {

        changedCells.add(cell);
        for (int i : PathFinder.CIRCLE4) {
            int neighbor = i + cell;
            int xCoord = cell % lvlWidth;
            if (neighbor >= 0 && neighbor < map.length && !changedCells.contains(neighbor)
                    && map[neighbor] == terrainClick
                    && (Math.abs(neighbor % lvlWidth - xCoord) <= 1)
                    && Dungeon.level.traps.get(neighbor).getClass() == onTrapClicked
                    && CustomTileItem.findAnyCustomTileAt(neighbor) == null)
                queue.add(neighbor);
        }
    }

    public static void fillAllWithOneTerrainQueue(int cell, int terrainClick, int[] map, CustomTilemap customTile, int lvlWidth) {

        changedCells.add(cell);
        for (int i : PathFinder.CIRCLE4) {
            int neighbor = i + cell;
            int xCoord = cell % lvlWidth;
            CustomTilemap found;
            if (neighbor >= 0 && neighbor < map.length && !changedCells.contains(neighbor)
                    && map[neighbor] == terrainClick
                    && (Math.abs(neighbor % lvlWidth - xCoord) <= 1)
                    && (found = CustomTileItem.findAnyCustomTileAt(neighbor)) != null
                    && found.getClass() == customTile.getClass()
            && (!(found instanceof CustomTileLoader.UserCustomTile)
                    || ((CustomTileLoader.UserCustomTile) found).identifier.equals(((CustomTileLoader.UserCustomTile) customTile).identifier)))
                queue.add(neighbor);
        }
    }

}