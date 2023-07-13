package com.shatteredpixel.shatteredpixeldungeon.editor;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.SandboxPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WellWater;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.EToolbar;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.WndEditorInv;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Items;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Mobs;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Plants;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Tiles;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Traps;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.CustomTileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.EditorCellSelector;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.SideControlPane;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.TerrainFeaturesTilemapEditor;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.UndoPane;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.BlobEditPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomTileLoader;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.EmoIcon;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DiscardedItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTerrainTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTileSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonWallsTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.GridTileMap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.RaisedTerrainTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.TerrainFeaturesTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Toast;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.glwrap.Blending;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Gizmo;
import com.watabou.noosa.Group;
import com.watabou.noosa.NoosaScript;
import com.watabou.noosa.NoosaScriptNoLighting;
import com.watabou.noosa.SkinnedBlock;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.FileUtils;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EditorScene extends PixelScene {

    private static EditorScene scene;


    private static CustomLevel customLevel;


    private MenuPane menu;
    private UndoPane undo;
    private SideControlPane sideControlPane;
    private EToolbar toolbar;


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


    private static PointF mainCameraPos;


    public static void start() {
        Dungeon.quickslot.reset();
        QuickSlotButton.reset();
        Dungeon.hero = null;
        FileUtils.setDefaultFileType(FileUtils.getFileTypeForCustomDungeons());
    }

    private static boolean firstTimeOpening = true;
    public static boolean openDifferentLevel = true;
    public static float setCameraZoomWhenOpen = -1;

    public static void open(CustomLevel customLevel) {
        if (customLevel != EditorScene.customLevel) {
            String oldLvlName;
            if (EditorScene.customLevel != null) {
                oldLvlName = EditorScene.customLevel.levelScheme.getCustomDungeon() == Dungeon.customDungeon ? EditorScene.customLevel.name : null;
                EditorScene.customLevel.levelScheme.unloadLevel();
            } else oldLvlName = null;
            if (openDifferentLevel) {
                mainCameraPos = null;
                Undo.reset();
            }
            openDifferentLevel = true;
            if (!firstTimeOpening) Items.updateKeys(oldLvlName, customLevel.name);
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

    @Override
    public void create() {

        Dungeon.level = customLevel();//so changes aren't required everywhere,
        // but the preferred way to access the current level is still through the method
        Dungeon.levelName = Dungeon.level.name;
        DungeonTileSheet.setupVariance(customLevel().length(), 1234567);//TODO use dungeon seed

        for (Heap heap : customLevel().heaps.valueList()) {
            for (Item item : heap.items) {
                item.image = Dungeon.customDungeon.getItemSpriteOnSheet(item);
            }
        }

        super.create();

        customLevel().playLevelMusic();


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

        customTiles = new Group();
        terrain.add(customTiles);

        for (CustomTilemap visual : customLevel().customTiles) {
            addCustomTile(visual);
        }

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

        emoicons = new Group();
        add(emoicons);

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

        layoutTags();


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
            prompt = new Toast(text, uiCamera.width) {
                @Override
                protected void onClose() {
                    cancel();
                }
            };
            prompt.camera = uiCamera;
            prompt.setPos((uiCamera.width - prompt.width()) / 2, uiCamera.height - 60);

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

    public static void revalidateHeaps() {
        if (scene == null) return;
        scene.heaps.parent.erase(scene.heaps);
        scene.heaps.destroy();
        scene.heaps = new Group();
        scene.add(scene.heaps);
        for (Heap heap : customLevel().heaps.valueList()) {
            heap.destroySubicons();
            heap.initSubicons();
            heap.seen = true;
            scene.addHeapSprite(heap);
        }
    }

    public void addCustomTile(CustomTilemap visual) {
        customTiles.add(visual.create());
    }

    public void addCustomWall(CustomTilemap visual) {
        customWalls.add(visual.create());
    }

    public static void add(CustomTilemap t, boolean wall) {
        if (scene == null) return;
        if (wall) {
            scene.addCustomWall(t);
        } else {
            scene.addCustomTile(t);
        }
    }

    public static void remove(CustomTilemap t, boolean wall) {
        if (scene == null) return;
        if (wall) scene.customWalls.remove(t.killVisual());
        else scene.customTiles.remove(t.killVisual());
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
        text.text(Messages.get(LevelTransition.class, "to") + ": " + (transition.destLevel == null ? "" : EditorUtilies.getDispayName(transition.destLevel)));
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

    public static void updateHeapImagesAndSubIcons() {
        if (scene == null) return;
        for (Heap heap : customLevel().heaps.valueList()) {
            if (heap.sprite == null) continue;
            updateHeapImage(heap);
            heap.updateSubicon();
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

    private void addBlobSprite(final Blob gas) {
        if (gas.emitter == null) {
            gases.add(new BlobEmitter(gas));
        }
    }

    private void addMobSprite(Mob mob) {
        CharSprite sprite = mob.sprite();
        sprite.visible = true;
        mobs.add(sprite);
        sprite.link(mob);
    }

    public static void add(Blob gas) {
        if (scene != null) {
            scene.addBlobSprite(gas);
        }
    }

    public static void add(Mob mob) {
        customLevel().mobs.add(mob);
        if (scene != null) {
            scene.addMobSprite(mob);
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
        cellSelector.select(cell, PointerEvent.LEFT, false);
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
        if (TileItem.isTrapTerrainCell(terrainType)) terrainType = Terrain.EMPTY;

        Trap trap = f.traps.get(cell);
        Heap heap = f.heaps.get(cell);
        Mob mob = f.findMob(cell);
        Plant plant = f.plants.get(cell);

        DefaultEditComp.showWindow(terrainType, DungeonTileSheet.getVisualWithAlts(Tiles.getPlainImage(terrainType), cell), heap, mob, trap, plant, cell);
    }

    public static WndEditorInv selectItem(WndBag.ItemSelectorInterface listener) {
        cancel();

        WndEditorInv wnd = WndEditorInv.getBag(listener);
        if (scene != null) {
            show(wnd);
        } else {
            Game.scene().addToFront(wnd);
        }
        return wnd;
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

    public static void layoutTags() {

        if (scene == null) return;

        //move the camera center up a bit if we're on full UI and it is taking up lots of space
        Camera.main.setCenterOffset(0, 0);
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

    private static Object getObjAtCell(int cell) {
        Mob mob = customLevel.findMob(cell);
        if (mob != null) return mob;
        Heap heap = customLevel.heaps.get(cell);
        if (heap != null && heap.peek() != null) return heap.peek();
        Plant plant = customLevel.plants.get(cell);
        if (plant != null) return plant;
        Trap trap = customLevel.traps.get(cell);
        if (trap != null) return trap;
        for (int i = 0; i < BlobEditPart.BlobData.BLOB_CLASSES.length; i++) {
            Blob b = Dungeon.level.blobs.get(BlobEditPart.BlobData.BLOB_CLASSES[i]);
            if (b != null && !(b instanceof WellWater) && b.cur != null && b.cur[cell] > 0) return b;
        }
        CustomTilemap customTile = CustomTileItem.findCustomTileAt(cell);
        if (customTile != null) return customTile;
        return customLevel.map[cell];
    }

    public static EditorItem getObjAsInBag(Object obj) {
        if (obj instanceof Integer || obj instanceof String) return (EditorItem) Tiles.bag.findItem(obj);
        if (obj instanceof CustomTileLoader.OwnCustomTile) return (EditorItem) Tiles.bag.findItem(((CustomTileLoader.OwnCustomTile) obj).fileName);
        return getObjAsInBagFromClass(obj.getClass());
    }

    public static EditorItem getObjAsInBagFromClass(Class<?> clazz) {
        if (Item.class.isAssignableFrom(clazz)) return (EditorItem) Items.bag.findItem(clazz);
        if (Mob.class.isAssignableFrom(clazz)) return (EditorItem) Mobs.bag.findItem(clazz);
//        if (obj instanceof Integer) return (EditorItem) Tiles.bag.findItem(obj);
        if (Trap.class.isAssignableFrom(clazz)) return (EditorItem) Traps.bag.findItem(clazz);
        if (Plant.class.isAssignableFrom(clazz)) return (EditorItem) Plants.bag.findItem(clazz);
        if (Blob.class.isAssignableFrom(clazz)) return (EditorItem) Tiles.bag.findItem(clazz);//Blobs
        if (CustomTilemap.class.isAssignableFrom(clazz)) return (EditorItem) Tiles.bag.findItem(clazz);//CustomTiles
        return null;
    }

    public static void updateDepthIcon() {
        if (scene == null) return;
        scene.menu.updateDepthIcon();
    }

    public static void updateUndoButtons() {
        if (scene == null) return;
        scene.undo.updateStates();
    }

    public static boolean interfaceBlockingHero() {
        if (scene == null) return false;
        return showingWindow();
    }

    public static boolean cancel() {
        if (cellSelector != null) {
            cellSelector.resetKeyHold();
            return cancelCellSelector();
        }
        return false;
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
            SandboxPixelDungeon.reportException(e);
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
                SandboxPixelDungeon.reportException(e);
            }
        }

        super.destroy();
    }

    public static CustomLevel customLevel() {
        return customLevel;
    }


    private static final CellSelector.Listener defaultCellListener = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer cell) {
            if (cell == null) return;

            Item selected = EToolbar.getSelectedItem();

            if (selected instanceof EditorItem) ((EditorItem) selected).place(cell);
        }

        @Override
        public void onRightClick(Integer cell) {
            if (cell != null && cell >= 0 && cell < customLevel.length()) {
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

    public static boolean dragClickEnabled() {
        Item selected = EToolbar.getSelectedItem();

        if (selected instanceof EditorItem) {
            if (selected instanceof CustomTileItem) {
                CustomTilemap customTile = ((CustomTileItem) selected).customTile();
                return customTile.tileW == 1 && customTile.tileH == 1;
            }
            return true;
        }
        return false;
    }

    public static void putInQuickslot(Integer cell) {
        if (cell != null && cell >= 0 && cell < customLevel.length()) {
            QuickSlotButton.set(getObjAsInBag(getObjAtCell(cell)));
        }
    }

    public static void fillAllWithOneTerrain(Integer cell) {
        if (cell != null && cell >= 0 && cell < customLevel.length()) {
            Item selected = EToolbar.getSelectedItem();

            if (selected instanceof EditorItem) {

                queue.add(cell);

                CustomLevel level = customLevel();
                EditorItem item = (EditorItem) selected;
                int lvlWidth = level.width();

                if (TileItem.isTrapTerrainCell(level.map[cell])) {

                    while (!queue.isEmpty()) {
                        int c = queue.iterator().next();
                        queue.remove(c);
                        fillAllWithOneTerrainQueue(c, level.map[c], item, level.map, level.traps.get(c).getClass(), lvlWidth);
                    }
                } else {
                    while (!queue.isEmpty()) {
                        int c = queue.iterator().next();
                        queue.remove(c);
                        fillAllWithOneTerrainQueue(c, level.map[c], item, level.map, lvlWidth);
                    }
                }
                changedCells.clear();
            }
        }
    }

    private static Set<Integer> changedCells = new HashSet<>();
    private static Set<Integer> queue = new HashSet<>();

    public static void fillAllWithOneTerrainQueue(int cell, int terrainClick, EditorItem place, int[] map, int lvlWidth) {

        changedCells.add(cell);
        for (int i : PathFinder.CIRCLE4) {
            int neighbor = i + cell;
            int xCoord = cell % lvlWidth;
            if (neighbor >= 0 && neighbor < map.length && !changedCells.contains(neighbor)
                    && (Math.abs(neighbor % lvlWidth - xCoord) <= 1) && map[neighbor] == terrainClick) {
                queue.add(neighbor);
            }
        }
        place.place(cell);

    }

    public static void fillAllWithOneTerrainQueue(int cell, int terrainClick, EditorItem place, int[] map, Class<? extends Trap> onTrapClicked, int lvlWidth) {

        changedCells.add(cell);
        for (int i : PathFinder.CIRCLE4) {
            int neighbor = i + cell;
            int xCoord = cell % lvlWidth;
            if (neighbor >= 0 && neighbor < map.length && !changedCells.contains(neighbor)
                    && map[neighbor] == terrainClick
                    && (Math.abs(neighbor % lvlWidth - xCoord) <= 1)
                    && customLevel().traps.get(neighbor).getClass() == onTrapClicked)
                queue.add(neighbor);
        }
        place.place(cell);

    }

}