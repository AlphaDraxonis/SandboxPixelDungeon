package com.alphadraxonis.sandboxpixeldungeon.editor;

import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.SPDSettings;
import com.alphadraxonis.sandboxpixeldungeon.SandboxPixelDungeon;
import com.alphadraxonis.sandboxpixeldungeon.actors.Actor;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Mob;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.DefaultEditComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.EToolbar;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.WndEditorInv;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.categories.Tiles;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.items.EditorItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomDungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomLevel;
import com.alphadraxonis.sandboxpixeldungeon.editor.overview.CustomDungeonSaves;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.EditorCellSelector;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.TerrainFeaturesTilemapEditor;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.UndoPane;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.Undo;
import com.alphadraxonis.sandboxpixeldungeon.effects.EmoIcon;
import com.alphadraxonis.sandboxpixeldungeon.items.Heap;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.levels.Terrain;
import com.alphadraxonis.sandboxpixeldungeon.levels.features.LevelTransition;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.Trap;
import com.alphadraxonis.sandboxpixeldungeon.scenes.CellSelector;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.sprites.CharSprite;
import com.alphadraxonis.sandboxpixeldungeon.sprites.DiscardedItemSprite;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSprite;
import com.alphadraxonis.sandboxpixeldungeon.tiles.DungeonTerrainTilemap;
import com.alphadraxonis.sandboxpixeldungeon.tiles.DungeonTileSheet;
import com.alphadraxonis.sandboxpixeldungeon.tiles.DungeonTilemap;
import com.alphadraxonis.sandboxpixeldungeon.tiles.DungeonWallsTilemap;
import com.alphadraxonis.sandboxpixeldungeon.tiles.GridTileMap;
import com.alphadraxonis.sandboxpixeldungeon.tiles.RaisedTerrainTilemap;
import com.alphadraxonis.sandboxpixeldungeon.tiles.TerrainFeaturesTilemap;
import com.alphadraxonis.sandboxpixeldungeon.ui.QuickSlotButton;
import com.alphadraxonis.sandboxpixeldungeon.ui.Toast;
import com.alphadraxonis.sandboxpixeldungeon.ui.Window;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndBag;
import com.badlogic.gdx.Files;
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
import com.watabou.utils.PointF;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditorScene extends PixelScene {

    private static EditorScene scene;


    private static CustomLevel customLevel;


    private MenuPane menu;
    private UndoPane undo;
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


    private static PointF mainCameraPos;


    public static void start() {
        Dungeon.quickslot.reset();
        QuickSlotButton.reset();
        Dungeon.hero = null;
        CustomDungeonSaves.setFileType(Files.FileType.External);
    }

    public static void open(CustomLevel customLevel) {
        if (customLevel != EditorScene.customLevel) {
            if (EditorScene.customLevel != null) {
                EditorScene.customLevel.levelScheme.unloadLevel();
            }
            mainCameraPos = null;
            Undo.reset();
        }
        EditorScene.customLevel = customLevel;
        Dungeon.levelName = customLevel.name;
        Dungeon.customDungeon.setLastEditedFloor(customLevel.name);
        PathFinder.setMapSize(customLevel.width(), customLevel.height());
        SandboxPixelDungeon.switchNoFade(EditorScene.class);
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

        Camera.main.zoom(GameMath.gate(minZoom, defaultZoom + SPDSettings.zoom(), maxZoom));
        Camera.main.edgeScroll.set(1);
        if (mainCameraPos != null) Camera.main.scroll = mainCameraPos;
        else {//FIXME point to middle! WICHTIG!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            PointF to = new PointF(Camera.main.zoom / 85f / 16f * 2f, Camera.main.zoom / 85f / 16f * 2);
            Camera.main.scroll.x = 85 * 8 / 2 - 200;
            Camera.main.scroll.y = Camera.main.width / 2f;
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

        menu = new MenuPane();
        menu.camera = uiCamera;
        menu.setPos(uiCamera.width - MenuPane.WIDTH, uiSize > 0 ? 0 : 0);
        add(menu);

        undo = new UndoPane();
        undo.camera = uiCamera;
        undo.setPos(0, 0);
        add(undo);

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
            prompt = new Toast(text) {
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
        if (terrainType == Terrain.TRAP || terrainType == Terrain.SECRET_TRAP || terrainType == Terrain.INACTIVE_TRAP)
            terrainType = Terrain.EMPTY;

        Trap trap = f.traps.get(cell);
        Heap heap = f.heaps.get(cell);
        Mob mob = f.getMobAtCell(cell);

        DefaultEditComp.showWindow(terrainType, DungeonTileSheet.getVisualWithAlts(Tiles.getPlainImage(terrainType), cell), heap, mob, trap, cell);
    }

    public static WndEditorInv selectItem(WndBag.ItemSelector listener) {
        cancel();

        if (scene != null) {
            WndEditorInv wnd = WndEditorInv.getBag(listener);
            show(wnd);
            return wnd;
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
        public String prompt() {
            return null;
        }

        @Override
        protected boolean dragClickEnabled() {
            return true;
        }
    };

}