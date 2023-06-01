package com.shatteredpixel.shatteredpixeldungeon.editor;

import com.badlogic.gdx.Files;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.EditorCellSelector;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.TerrainFeaturesTilemapEditor;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.inv.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.inv.TileBar;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.inv.TileInventoryPane;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.inv.Tiles;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.inv.WndEditorItemsBag;
import com.shatteredpixel.shatteredpixeldungeon.effects.EmoIcon;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.editor.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.editor.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DiscardedItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTerrainTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTileSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonWallsTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.GridTileMap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.RaisedTerrainTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.TerrainFeaturesTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Toast;
import com.shatteredpixel.shatteredpixeldungeon.ui.Toolbar;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.glwrap.Blending;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Gizmo;
import com.watabou.noosa.Group;
import com.watabou.noosa.NoosaScript;
import com.watabou.noosa.NoosaScriptNoLighting;
import com.watabou.noosa.SkinnedBlock;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.PointF;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditorScene extends PixelScene {

    private static EditorScene scene;


    private static CustomLevel customLevel;


    private MenuPane menu;
    private TileBar tileBar;
//    private SwitchLevelIndicator switchLevelIndicator;


    private Toast prompt;

    private SkinnedBlock water;
    private DungeonTerrainTilemap tiles;
    private GridTileMap visualGrid;
    private TerrainFeaturesTilemap terrainFeatures;
    private RaisedTerrainTilemap raisedTerrain;
    private DungeonWallsTilemap walls;


    private static CellSelector cellSelector;


    private Group terrain;
    private Group customTiles;
    private Group levelVisuals;
    private Group customWalls;
    private Group ripples;
    private Group plants;
    private Group traps;
    private Group heaps;
    private Group transitionIndicators;
    private Map<LevelTransition, BitmapText> transitionIndicatorsMap;
    private Group mobs;
    private Group floorEmitters;
    private Group emitters;
    private Group effects;
    private Group gases;
    private Group spells;
    private Group statuses;
    private Group emoicons;
    private Group overFogEffects;
    private Group healthIndicators;


    private ArrayList<Gizmo> toDestroy = new ArrayList<>();
    private static Point lastOffset = null;

    private TileInventoryPane inventory;
    private static boolean invVisible = true;


    public static void start() {
        Dungeon.quickslot.reset();
        QuickSlotButton.reset();
        Toolbar.swappedQuickslots = false;//FIXME don't do this
        Dungeon.hero = null;
        CustomDungeonSaves.setFileType(Files.FileType.External);
    }

    public static void open(CustomLevel customLevel) {
        if (EditorScene.customLevel != null && customLevel != EditorScene.customLevel) {
            EditorScene.customLevel.levelScheme.unloadLevel();
        }
        EditorScene.customLevel = customLevel;
        Dungeon.levelName = customLevel.name;
        Dungeon.customDungeon.setLastEditedFloor(customLevel.name);
        PathFinder.setMapSize(customLevel.width(), customLevel.height());
        ShatteredPixelDungeon.switchNoFade(EditorScene.class);
    }
//    public static void start(Floor floor) {//unused
//        Dungeon.quickslot.reset();
//        QuickSlotButton.reset();
//        Toolbar.swappedQuickslots = false;//FIXME dont do this
//        Dungeon.hero = null;
//        EditorScene.floor = floor;
//        ShatteredPixelDungeon.switchNoFade(EditorScene.class);
//    }


//    ShatteredPixelDungeon.seamlessResetScene(new Game.SceneChangeCallback() {
//        @Override
//        public void beforeCreate() {
//            Game.platform.resetGenerators();
//        }
//        @Override
//        public void afterCreate() {
//            //do nothing
//        }
//    });

    @Override
    public void create() {

        Dungeon.level = customLevel();//so changes aren't required everywhere
        Dungeon.levelName = Dungeon.level.name;
        DungeonTileSheet.setupVariance(customLevel().length(), 1234567);

        for (Heap heap : customLevel().heaps.valueList()) {
            for (Item item : heap.items) {
                item.image = Dungeon.customDungeon.getItemSpriteOnSheet(item);
            }
        }

        super.create();

        Camera.main.zoom(GameMath.gate(minZoom, defaultZoom + SPDSettings.zoom(), maxZoom));
        Camera.main.edgeScroll.set(1);

        scene = this;

        terrain = new Group();
        add(terrain);

        water = new SkinnedBlock(
                customLevel().width() * DungeonTilemap.SIZE,
                customLevel().height() * DungeonTilemap.SIZE,
                customLevel().waterTex()) {

            @Override
            protected NoosaScript script() {
                return NoosaScriptNoLighting.get();
            }

            @Override
            public void draw() {
                //water has no alpha component, this improves performance
                Blending.disable();
                super.draw();
                Blending.enable();
            }
        };
        water.autoAdjust = true;
        terrain.add(water);

        tiles = new DungeonTerrainTilemap();
        terrain.add(tiles);

        visualGrid = new GridTileMap();
        terrain.add(visualGrid);

        terrainFeatures = new TerrainFeaturesTilemapEditor(customLevel());
        terrain.add(terrainFeatures);

        transitionIndicators = new Group();
        add(transitionIndicators);
        transitionIndicatorsMap = new HashMap<>();
        updateTransitionIndicators();

        heaps = new Group();
        add(heaps);

        for (Heap heap : customLevel().heaps.valueList()) {
            heap.destroySubicons();
            heap.initSubicons();
            heap.seen = true;
            addHeapSprite(heap);
        }

        emitters = new Group();

        mobs = new Group();
        add(mobs);

        for (Mob mob : customLevel().mobs) {
            addMobSprite(mob);
        }

        add(emitters);

        emoicons = new Group();
        add(emoicons);

        add(cellSelector = new EditorCellSelector(tiles));

        int uiSize = SPDSettings.interfaceSize();

        //

        menu = new MenuPane();
        menu.camera = uiCamera;
        menu.setPos(uiCamera.width - MenuPane.WIDTH, uiSize > 0 ? 0 : 1);
        add(menu);

        tileBar = new TileBar();
        tileBar.camera = uiCamera;
        add(tileBar);

        if (uiSize == 2) {
            inventory = new TileInventoryPane();
            inventory.camera = uiCamera;
            inventory.setPos(uiCamera.width - inventory.width(), uiCamera.height - inventory.height());
            add(inventory);
            inventory.visible = false;

            tileBar.setRect(0, uiCamera.height - tileBar.height() - inventory.height(), uiCamera.width, tileBar.height());
        } else {
            tileBar.setRect(0, uiCamera.height - tileBar.height(), uiCamera.width, tileBar.height());
        }

//        switchLevelIndicator = new SwitchLevelIndicator();
//        switchLevelIndicator.camera = uiCamera;
//        add(switchLevelIndicator);

        layoutTags();


        if (!invVisible) toggleInvPane();
        fadeIn();

        cellSelector.listener = defaultCellListener;
        cellSelector.enabled = true;

    }

    private synchronized void prompt(String text) {

        if (prompt != null) {
            prompt.killAndErase();
            toDestroy.add(prompt);
            prompt = null;
        }

        if (text != null) {
            prompt = new Toast(text) {
                @Override
                protected void onClose() {
                    cancel();
                }
            };
            prompt.camera = uiCamera;
            prompt.setPos((uiCamera.width - prompt.width()) / 2, uiCamera.height - 60);

            if (inventory != null && inventory.visible && prompt.right() > inventory.left() - 10) {
                prompt.setPos(inventory.left() - prompt.width() - 10, prompt.top());
            }

            add(prompt);
        }
    }

    public static void updateMap(int cell) {
        if (scene != null) {
            scene.tiles.updateMapCell(cell);
            scene.visualGrid.updateMapCell(cell);
            scene.terrainFeatures.updateMapCell(cell);
        }
    }

    private void addHeapSprite(Heap heap) {
        heap.sprite = (ItemSprite) heaps.recycle(ItemSprite.class);
        heap.sprite.revive();
        heap.linkSprite(heap);
        heap.addHeapComponents(heaps);
    }

    private void addDiscardedSprite(Heap heap) {
        heap.sprite = (DiscardedItemSprite) heaps.recycle(DiscardedItemSprite.class);
        heap.sprite.revive();
        heap.linkSprite(heap);
        heap.addHeapComponents(heaps);
    }

    private void addTransitionSprite(LevelTransition transition) {
        if (transitionIndicatorsMap.containsKey(transition)) return;
        BitmapText text = new BitmapText(PixelScene.pixelFont);
        transitionIndicators.add(text);
        transitionIndicatorsMap.put(transition, text);
        updateTransitionIndicator(transition);
    }

    public static void remove(LevelTransition transition) {
        if (scene == null) return;
        BitmapText text = scene.transitionIndicatorsMap.get(transition);
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
        text.text("To: " + (transition.destLevel == null ? "" : transition.destLevel));
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
        for (LevelTransition transition : customLevel().transitions.values()) {
            scene.addTransitionSprite(transition);
        }
    }

    public static void updateHeapImages() {
        if (scene == null) return;
        for (Heap heap : customLevel().heaps.valueList()) {
            updateHeapImage(heap);
        }
    }

    public static void updateHeapImage(Heap heap) {
        Item i = heap.peek();
        i.image = CustomDungeon.getDungeon().getItemSpriteOnSheet(i);
        heap.sprite.view(heap);
    }

    private void addMobSprite(Mob mob) {
        CharSprite sprite = mob.sprite();
        sprite.visible = true;
        mobs.add(sprite);
        sprite.link(mob);
    }

    public static void add(Mob mob) {
        customLevel().mobs.add(mob);
        if (scene != null) {
            scene.addMobSprite(mob);
            Actor.add(mob);
        }
    }

    public static void add(Heap heap) {
        if (scene != null) {
            scene.addHeapSprite(heap);
        }
    }

    public static void discard(Heap heap) {
        if (scene != null) {
            scene.addDiscardedSprite(heap);
        }
    }

    public static void add(EmoIcon icon) {
        scene.emoicons.add(icon);
    }


    @Override
    public synchronized Gizmo erase(Gizmo g) {
        Gizmo result = super.erase(g);
        if (result instanceof Window) {
            lastOffset = ((Window) result).getOffset();
        }
        return result;
    }

    public static Emitter emitter() {

        if (scene != null) {
            scene.emitters.visible = false;
            Emitter emitter = (Emitter) scene.emitters.recycle(Emitter.class);
            emitter.revive();
            return emitter;
        } else {
            return null;
        }
    }

    public static void handleCell(int cell) {
        cellSelector.select(cell, PointerEvent.LEFT);
    }

    public static void selectCell(CellSelector.Listener listener) {
        if (cellSelector.listener != null && cellSelector.listener != defaultCellListener) {
            cellSelector.listener.onSelect(null);
        }
        cellSelector.listener = listener;
        cellSelector.enabled = true;
        if (scene != null) {
            scene.prompt(listener.prompt());
        }
    }

    public static boolean cancelCellSelector() {
        if (cellSelector.listener != null && cellSelector.listener != defaultCellListener) {
            cellSelector.resetKeyHold();
            cellSelector.cancel();
            return true;
        } else {
            return false;
        }
    }

    public static void showEditCellWindow(int cell) {
        if (scene == null) return;

        CustomLevel f = customLevel();
        int terrainType = f.map[cell];
        if (terrainType == Terrain.TRAP || terrainType == Terrain.SECRET_TRAP || terrainType == Terrain.INACTIVE_TRAP)
            terrainType = Terrain.EMPTY;

        Trap trap = f.traps.get(cell);
        Heap heap = f.heaps.get(cell);
        Mob mob = f.getMobAtCell(cell);

        DefaultEditComp.showWindow(terrainType, DungeonTileSheet.getVisualWithAlts(Tiles.getPlainImage(terrainType), cell), heap, mob, trap, cell);
    }

    public static WndEditorItemsBag selectItem(WndBag.ItemSelector listener) {
        cancel();

        if (scene != null) {
            //TODO can the inventory pane work in these cases? bad to fallback to mobile window
            if (scene.inventory != null && scene.inventory.visible && !showingWindow()) {
                scene.inventory.setSelector(listener);
                return null;
            } else {
                WndEditorItemsBag wnd = WndEditorItemsBag.getBag(listener);
                show(wnd);
                return wnd;
            }
        }

        return null;
    }

    public static void show(Window wnd) {
        if (scene != null) {
            cancel();

            //If a window is already present (or was just present)
            // then inherit the offset it had
//            if (scene.inventory != null && scene.inventory.visible){
//            Point offsetToInherit = null;
//            for (Gizmo g : scene.members) {
//                if (g instanceof Window) offsetToInherit = ((Window) g).getOffset();
//            }
//            if (lastOffset != null) {
//                offsetToInherit = lastOffset;
//            }
//            if (offsetToInherit != null) {
//                wnd.offset(offsetToInherit);
//                wnd.boundOffsetWithMargin(3);
//            }
//            }

            scene.addToFront(wnd);
        }
    }

    public static boolean showingWindow() {
        if (scene == null) return false;

        for (Gizmo g : scene.members) {
            if (g instanceof Window) return true;
        }

        return false;
    }

    @Override
    public void update() {
        lastOffset = null;
        super.update();
    }

    public static void layoutTags() {

        if (scene == null) return;

        //move the camera center up a bit if we're on full UI and it is taking up lots of space
        if (scene.inventory != null && scene.inventory.visible
                && (uiCamera.width < 460 && uiCamera.height < 300)) {
            Camera.main.setCenterOffset(0, Math.min(300 - uiCamera.height, 460 - uiCamera.width) / Camera.main.zoom);
        } else {
            Camera.main.setCenterOffset(0, 0);
        }
        //Camera.main.panTo(Dungeon.hero.sprite.center(), 5f);

        //primarily for phones displays with notches
        //TODO Android never draws into notch atm, perhaps allow it for center notches?
//        RectF insets = DeviceCompat.getSafeInsets();
//        insets = insets.scale(1f / uiCamera.zoom);
//
//        boolean tagsOnLeft = SPDSettings.flipTags();
//        float tagWidth = Tag.SIZE + (tagsOnLeft ? insets.left : insets.right);
//        float tagLeft = tagsOnLeft ? 0 : uiCamera.width - tagWidth;
//
//        float pos = scene.toolbar.top();
//        if (tagsOnLeft && SPDSettings.interfaceSize() > 0){
//            pos = scene.status.top();
//        }
//
//        if (scene.tagAttack){
//            scene.attack.setRect( tagLeft, pos - Tag.SIZE, tagWidth, Tag.SIZE );
//            scene.attack.flip(tagsOnLeft);
//            pos = scene.attack.top();
//        }
//
//        if (scene.tagLoot) {
//            scene.loot.setRect( tagLeft, pos - Tag.SIZE, tagWidth, Tag.SIZE );
//            scene.loot.flip(tagsOnLeft);
//            pos = scene.loot.top();
//        }
//
//        if (scene.tagAction) {
//            scene.action.setRect( tagLeft, pos - Tag.SIZE, tagWidth, Tag.SIZE );
//            scene.action.flip(tagsOnLeft);
//            pos = scene.action.top();
//        }
//
//        if (scene.tagResume) {
//            scene.resume.setRect( tagLeft, pos - Tag.SIZE, tagWidth, Tag.SIZE );
//            scene.resume.flip(tagsOnLeft);
//        }

//        scene.switchLevelIndicator.setRect(0, 0, Tag.SIZE, Tag.SIZE);
    }

    public static void updateDepthIcon() {
        if (scene == null) return;
        scene.menu.updateDepthIcon();

    }

    public static boolean interfaceBlockingHero() {
        if (scene == null) return false;
        if (showingWindow()) return true;
        if (scene.inventory != null && scene.inventory.isSelecting()) {
            return true;
        }
        return false;
    }

    public static boolean cancel() {
        cellSelector.resetKeyHold();
        return cancelCellSelector();
    }

    public static void ready() {
        selectCell(defaultCellListener);
        QuickSlotButton.cancel();
//        InventoryPane.cancelTargeting();
//        if (scene != null && scene.toolbar != null) scene.toolbar.examining = false;
    }

    @Override
    public synchronized void onPause() {
        try {
            CustomDungeonSaves.saveLevel(EditorScene.customLevel());
            CustomDungeonSaves.saveDungeon(Dungeon.customDungeon);
        } catch (IOException e) {
            ShatteredPixelDungeon.reportException(e);
        }
    }

    @Override
    public void destroy() {

        //tell the actor thread to finish, then wait for it to complete any actions it may be doing.
//        if (!waitForActorThread( 4500, true )){
//            Throwable t = new Throwable();
//            t.setStackTrace(actorThread.getStackTrace());
//            throw new RuntimeException("timeout waiting for actor thread! ", t);
//        }
//
//        Emitter.freezeEmitters = false;

        scene = null;
        if (Dungeon.customDungeon.getFloor(customLevel.name) != null) {
            try {
                CustomDungeonSaves.saveDungeon(Dungeon.customDungeon);
                CustomDungeonSaves.saveLevel(customLevel);//only save if not already deleted
            } catch (IOException e) {
                ShatteredPixelDungeon.reportException(e);
            }
        }

        super.destroy();
    }

    public static CustomLevel customLevel() {
        return customLevel;
    }

    public static String formatTitle(String name, Koord koord) {
        return Messages.titleCase(name) + ": " + koord;
    }

    public static String formatTitle(CustomLevel.ItemWithPos item) {
        return formatTitle(item.item().title(), new Koord(item.pos()));
    }

    public static void toggleInvPane() {
        if (scene != null && scene.inventory != null) {
            if (scene.inventory.visible) {
                scene.inventory.visible = scene.inventory.active = invVisible = false;
                scene.tileBar.setPos(scene.tileBar.left(), uiCamera.height - scene.tileBar.height());
            } else {
                scene.inventory.visible = scene.inventory.active = invVisible = true;
                scene.tileBar.setPos(scene.tileBar.left(), scene.inventory.top() - scene.tiles.height());
            }
            layoutTags();
        }
    }

    public static void centerNextWndOnInvPane() {
        if (scene != null && scene.inventory != null && scene.inventory.visible) {
            lastOffset = new Point((int) scene.inventory.centerX() - uiCamera.width / 2,
                    (int) scene.inventory.centerY() - uiCamera.height / 2);
        }
    }


    private static final CellSelector.Listener defaultCellListener = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer cell) {
            if (cell == null) return;

            Item selected = TileBar.getSelectedItem();

            if (selected instanceof EditorItem) ((EditorItem) selected).place(cell);
        }

        @Override
        public void onRightClick(Integer cell) {
            if (cell == null || cell < 0 || cell > customLevel.length()) {
                return;
            }

            EditorScene.showEditCellWindow(cell);

        }

        @Override
        public String prompt() {
            return null;
        }
    };

}