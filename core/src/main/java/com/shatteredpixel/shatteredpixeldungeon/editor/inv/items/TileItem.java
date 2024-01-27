package com.shatteredpixel.shatteredpixeldungeon.editor.inv.items;

import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.ENTRANCE;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.EXIT;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.FURROWED_GRASS;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.GRASS;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.HIGH_GRASS;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.INACTIVE_TRAP;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.LOCKED_EXIT;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.SECRET_TRAP;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.SIGN;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.SIGN_SP;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.TRAP;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.UNLOCKED_EXIT;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.Sign;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditTileComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.DefaultListItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.EditorInventoryWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Tiles;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPartList;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPartModify;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.BarrierActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.BlobEditPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.HeapActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.MobActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.PlaceCellActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.SignEditPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTileSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.watabou.noosa.Image;
import com.watabou.utils.PathFinder;

import java.util.Collections;

public class TileItem extends EditorItem {


    private int terrainType;
    private final int cell;


    public TileItem(int terrainFeature, int cell) {
        this.terrainType = terrainFeature;
        this.cell = cell;
        randomizeTexture();
    }

    public TileItem(int terrainFeature, int image, int cell) {
        this.terrainType = terrainFeature;
        this.cell = cell;
        this.image = image;
    }

    public void setTerrainType(int terrainType) {
        this.terrainType = terrainType;
        randomizeTexture();
    }

    @Override
    public void randomizeTexture() {
        image = DungeonTileSheet.getVisualWithAlts(Tiles.getPlainImage(terrainType), -1);
    }


    @Override
    public String name() {
        return getName(terrainType(), cell());
    }

    @Override
    public Object getObject() {
        return terrainType();
    }

    public int terrainType() {
        return terrainType;
    }

    public int cell() {
        return cell;
    }

    @Override
    public ScrollingListPane.ListItem createListItem(EditorInventoryWindow window) {
        Level level = EditorScene.customLevel();
        return new DefaultListItem(this, window, level.tileName(terrainType()), getSprite());
    }


    @Override
    public DefaultEditComp<?> createEditComponent() {
        return new EditTileComp(this);
    }

    @Override
    public Image getSprite() {
        if (image() == Terrain.WATER)
            return new ItemSprite(EditorScene.customLevel().waterTex(), this);
        return image() < 0 ? new Image() : new ItemSprite(this);
    }

    @Override
    public void place(int cell) {
        Undo.addActionPart(place(cell, terrainType()));
    }

    public static ActionPart place(int cell, int terrainType) {
        return place(cell, terrainType, false, null);
    }

    public static ActionPart place(int cell, int terrainType, boolean forceChange, Boolean newCustomTileIsWall) {
        return new PlaceTileActionPart(cell, terrainType, forceChange, newCustomTileIsWall);
    }


    public static boolean isExitTerrainCell(int terrain) {//why is this not in class Tiles? because it crashes the game for no reason!
        return terrain == EXIT || terrain == LOCKED_EXIT || terrain == UNLOCKED_EXIT;
    }

    public static boolean isTrapTerrainCell(int terrain) {
        return terrain == TRAP || terrain == SECRET_TRAP || terrain == INACTIVE_TRAP;
    }

    public static boolean isGrassTerrainCell(int terrain) {
        return terrain == GRASS || terrain == HIGH_GRASS || terrain == FURROWED_GRASS;
    }

    public static boolean isSignTerrainCell(int terrain) {
        return terrain == SIGN || terrain == SIGN_SP;
    }

    public static String getName(int terrainType, int cell) {

        if (cell != -1) {
            CustomTilemap customTile = null;
            int x = cell % Dungeon.level.width();
            int y = cell / Dungeon.level.width();
            for (CustomTilemap i : Dungeon.level.customTiles) {
                if ((x >= i.tileX && x < i.tileX + i.tileW) &&
                        (y >= i.tileY && y < i.tileY + i.tileH)) {
                    if (i.image(x - i.tileX, y - i.tileY) != null) {
                        x -= i.tileX;
                        y -= i.tileY;
                        customTile = i;
                        break;
                    }
                }
            }
            if (customTile != null && customTile.name(x, y) != null) {
                return customTile.name(x, y) + EditorUtilies.appendCellToString(cell);
            }
        }
        return Messages.titleCase(EditorScene.customLevel().tileName(terrainType)) + EditorUtilies.appendCellToString(cell);
    }


    public static class PlaceTileActionPart extends PlaceCellActionPart {
        private /*final*/ ActionPartList moreActions;//should ONLY be changed if null

        protected PlaceTileActionPart(int cell, int terrainType, boolean forceChange, Boolean newCustomTileIsWall) {

            super();

            CustomLevel level = EditorScene.customLevel();
            int oldTerrain = level.map[cell];

            if (oldTerrain == terrainType) {
                CustomTilemap customTilemap;
                if ((customTilemap = CustomTileItem.findCustomTileAt(cell, newCustomTileIsWall)) != null || forceChange)
                    init(oldTerrain, terrainType, cell,
                            level.traps.get(cell), level.plants.get(cell), customTilemap, newCustomTileIsWall);
                moreActions = null;
                return; //no need to continue bc nothing changes at all
            }
            init(oldTerrain, terrainType, cell,
                    level.traps.get(cell), level.plants.get(cell), CustomTileItem.findCustomTileAt(cell, newCustomTileIsWall), newCustomTileIsWall);

            moreActions = new ActionPartList();

            //Transition logic
            final boolean wasExit = TileItem.isExitTerrainCell(oldTerrain);
            final boolean nowExit = TileItem.isExitTerrainCell(terrainType);
            if (wasExit || nowExit || oldTerrain == ENTRANCE || terrainType == ENTRANCE) {

                final LevelTransition transition = level.transitions.get(cell);
                final LevelScheme levelScheme = level.levelScheme;

                String defaultBelowOrAbove;

                LevelTransition defaultTransition; //only if new exit or entrance was placed
                LevelScheme defaultDestlevelScheme;
                if (nowExit) {
                    defaultBelowOrAbove = levelScheme.getDefaultBelow();
                    defaultTransition = Level.SURFACE.equals(defaultBelowOrAbove) ? new LevelTransition(level, cell, LevelTransition.Type.SURFACE)
                            : ((defaultDestlevelScheme = (Dungeon.customDungeon.getFloor(defaultBelowOrAbove))) == null
                            || defaultDestlevelScheme.exitCells.isEmpty() ? null
                            : new LevelTransition(level, cell, defaultDestlevelScheme.entranceCells.get(0), defaultBelowOrAbove));
                } else if (terrainType == ENTRANCE) {
                    defaultBelowOrAbove = levelScheme.getDefaultAbove();
                    defaultTransition = Level.SURFACE.equals(defaultBelowOrAbove) ? new LevelTransition(level, cell, LevelTransition.Type.SURFACE)
                            : ((defaultDestlevelScheme = (Dungeon.customDungeon.getFloor(defaultBelowOrAbove))) == null
                            || defaultDestlevelScheme.entranceCells.isEmpty() ? null
                            : new LevelTransition(level, cell, defaultDestlevelScheme.entranceCells.get(0), defaultBelowOrAbove));
                } else defaultTransition = null;

                ActionPart transPart = new ActionPart() {

                    @Override
                    public void undo() {

                        level.transitions.remove(cell);
                        EditorScene.remove(defaultTransition);

                        if (oldTerrain == Terrain.ENTRANCE) {
                            levelScheme.entranceCells.add((Integer) cell);
                            levelScheme.exitCells.remove((Integer) cell);
                            if (transition != null) {
                                level.transitions.put(cell, transition);
                                EditorScene.add(transition);
                            }
                        } else {

                            if (terrainType == Terrain.ENTRANCE) {
                                levelScheme.entranceCells.remove((Integer) cell);
                                if (transition != null) {
                                    level.transitions.put(cell, transition);
                                    EditorScene.add(transition);
                                }
                            }

                            if (wasExit != nowExit) {
                                if (wasExit) {
                                    levelScheme.exitCells.add((Integer) cell);
                                    if (transition != null) {
                                        level.transitions.put(cell, transition);
                                        EditorScene.add(transition);
                                    }
                                } else {
                                    levelScheme.exitCells.remove((Integer) cell);
                                }

                            }
                        }
                        Collections.sort(levelScheme.entranceCells);
                        Collections.sort(levelScheme.exitCells);
                    }

                    @Override
                    public void redo() {//sorry for comments you don't understand...

                        if (terrainType == Terrain.ENTRANCE) {  //neu entrance
                            levelScheme.entranceCells.add((Integer) cell);  //füge entrance cell hinzu
                            levelScheme.exitCells.remove((Integer) cell);   // entferne ggf exit
                            if (transition != null) { //entferne ggf alte transition
                                level.transitions.remove(cell);
                                EditorScene.remove(transition);
                            }
                            //add default transition:
                            if (defaultTransition != null) {
                                level.transitions.put(cell, defaultTransition);
                                EditorScene.add(defaultTransition);
                            }
                        } else {

                            //es wird hier kein entrance sein (da terraintype verschieden sein muss)
                            if (oldTerrain == Terrain.ENTRANCE) {//wenn es mal entrance war, aber keiner mehr ist
                                levelScheme.entranceCells.remove((Integer) cell);//entferne entrance
                                if (transition != null) {//entferne ggf alte transitiom
                                    level.transitions.remove(cell);
                                    EditorScene.remove(transition);
                                }
                            }

                            if (wasExit != nowExit) {//bei exit änderung
                                if (wasExit) {//exit wurde entfernt, und nicht durch entrance ersetzt
                                    levelScheme.exitCells.remove((Integer) cell);//entferne exit
                                    if (transition != null) {//entferne ggf transition
                                        level.transitions.remove(cell);
                                        EditorScene.remove(transition);
                                    }
                                } else {//bei neuem exit: füge exit hinzu (falls es vorher entrance war, wurde zelle+transition schon entfernt)
                                    levelScheme.exitCells.add((Integer) cell);
                                    //add default transition:
                                    if (defaultTransition != null) {
                                        level.transitions.put(cell, defaultTransition);
                                        EditorScene.add(defaultTransition);
                                    }
                                }
                            }

                        }
                        Collections.sort(levelScheme.entranceCells);
                        Collections.sort(levelScheme.exitCells);
                    }

                    @Override
                    public boolean hasContent() {
                        return true;//already checked when adding this (nowExit||wasExit)
                    }
                };
                moreActions.addActionPart(transPart);
                transPart.redo();
            }
            if (isSignTerrainCell(oldTerrain) || isSignTerrainCell(terrainType)) {
                Sign newSign = new Sign();
                Sign oldSign = level.signs.get(cell);
                newSign.pos = cell;
                ActionPart signActionPart = new SignEditPart.ActionPart(cell, oldSign, newSign);
                moreActions.addActionPart(signActionPart);
                signActionPart.redo();
            }

            if (terrainType != Terrain.WELL) {
                ActionPartModify blobEditPart = new BlobEditPart.Modify(cell);
                if (level.solid[cell]) BlobEditPart.clearAllAtCell(cell);
                else BlobEditPart.clearWellWaterAtCell(cell);
                blobEditPart.finish();
                moreActions.addActionPart(blobEditPart);
            }

            for (int i : PathFinder.NEIGHBOURS9) {
                Mob m = level.findMob(i + cell);
                if (m != null && MobItem.invalidPlacement(m, level, m.pos)) {
                    ActionPart p = new MobActionPart.Remove(m);
                    moreActions.addActionPart(p);
                    p.redo();
                }
            }

            if (ItemItem.invalidPlacement(cell, level)) {
                Heap h = level.heaps.get(cell);
                if (h != null) {
                    ActionPart p = new HeapActionPart.Remove(h);
                    moreActions.addActionPart(p);
                    p.redo();
                }
            }

            if (BarrierItem.invalidPlacement(cell, level)) {
                Barrier b = level.barriers.get(cell);
                if (b != null) {
                    ActionPart p = new BarrierActionPart.Remove(b);
                    moreActions.addActionPart(p);
                    p.redo();
                }
            }

            if (PlantItem.invalidPlacement(cell, level) || terrainType != Terrain.GRASS) {
                Plant p = level.plants.get(cell);
                if (p != null) {
                    ActionPart part = new ActionPart() {
                        @Override
                        public void undo() {
                            Dungeon.level.plants.put(p.pos, p);
                        }

                        @Override
                        public void redo() {
                            Dungeon.level.plants.remove(p.pos);
                        }

                        @Override
                        public boolean hasContent() {
                            return true;
                        }
                    };
                    moreActions.addActionPart(part);
                    part.redo();
                }
            }

            EditorScene.updateMap(cell);
        }

        @Override
        public void undo() {
            super.undo();
            if (moreActions != null) moreActions.undo();
            EditorScene.updateMap(cell());
        }

        @Override
        public void redo() {
            super.redo();
            if (moreActions != null) moreActions.redo();
            EditorScene.updateMap(cell());
        }

        @Override
        public boolean hasContent() {
            return super.hasContent() || (moreActions != null && moreActions.hasContent());
        }

        protected void addToMoreActions(ActionPart actionPart){
            if (moreActions == null) moreActions = new ActionPartList();
            moreActions.addActionPart(actionPart);
        }
    }

}