package com.alphadraxonis.sandboxpixeldungeon.editor.inv.items;

import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.ENTRANCE;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.EXIT;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.FURROWED_GRASS;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.GRASS;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.HIGH_GRASS;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.INACTIVE_TRAP;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.LOCKED_EXIT;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.SECRET_TRAP;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.SIGN;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.SIGN_SP;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.TRAP;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.UNLOCKED_EXIT;

import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Mob;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.Sign;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.DefaultEditComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.EditTileComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.DefaultListItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.EditorInventoryWindow;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.categories.Tiles;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomLevel;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.LevelScheme;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.ActionPart;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.ActionPartList;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.ActionPartModify;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.Undo;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.parts.BlobEditPart;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.parts.HeapActionPart;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.parts.MobActionPart;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.parts.PlaceCellActionPart;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.parts.SignEditPart;
import com.alphadraxonis.sandboxpixeldungeon.editor.util.EditorUtilies;
import com.alphadraxonis.sandboxpixeldungeon.items.Heap;
import com.alphadraxonis.sandboxpixeldungeon.levels.Level;
import com.alphadraxonis.sandboxpixeldungeon.levels.Terrain;
import com.alphadraxonis.sandboxpixeldungeon.levels.features.LevelTransition;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.plants.Plant;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSprite;
import com.alphadraxonis.sandboxpixeldungeon.tiles.CustomTilemap;
import com.alphadraxonis.sandboxpixeldungeon.ui.ScrollingListPane;
import com.watabou.noosa.Image;
import com.watabou.utils.PathFinder;

import java.util.Collections;

public class TileItem extends EditorItem {


    private final int terrainType, cell;


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

    @Override
    public void randomizeTexture() {
        image = Tiles.getVisualWithAlts(Tiles.getPlainImage(terrainType));
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
        return place(cell, terrainType, false);
    }

    public static ActionPart place(int cell, int terrainType, boolean forceChange) {
        return new PlaceTileActionPart(cell, terrainType, forceChange);
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
        protected final ActionPartList moreActions;

        protected PlaceTileActionPart(int cell, int terrainType, boolean forceChange) {

            super();

            CustomLevel level = EditorScene.customLevel();
            int oldTerrain = level.map[cell];

            if (oldTerrain == terrainType) {
                if (forceChange)
                    init(oldTerrain, terrainType, cell,
                            level.traps.get(cell), level.plants.get(cell), CustomTileItem.findCustomTileAt(cell));
                moreActions = null;
                return; //no need to continue bc nothing changes at all
            }
            init(oldTerrain, terrainType, cell,
                    level.traps.get(cell), level.plants.get(cell), CustomTileItem.findCustomTileAt(cell));

            moreActions = new ActionPartList();

            //Transition logic
            final boolean wasExit = TileItem.isExitTerrainCell(oldTerrain);
            final boolean nowExit = TileItem.isExitTerrainCell(terrainType);
            if (wasExit || nowExit || oldTerrain == ENTRANCE || terrainType == ENTRANCE) {

                final LevelTransition transition = level.transitions.get(cell);
                final LevelScheme levelScheme = level.levelScheme;

                ActionPart transPart = new ActionPart() {

                    @Override
                    public void undo() {

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

            ActionPartModify blobEditPart = new BlobEditPart.Modify(cell);
            BlobEditPart.clearWellWaterAtCell(cell);
            blobEditPart.finish();
            moreActions.addActionPart(blobEditPart);

            EditorScene.updateMap(cell);

            for (int i : PathFinder.NEIGHBOURS9) {
                Mob m = level.getMobAtCell(i + cell);
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
    }

}