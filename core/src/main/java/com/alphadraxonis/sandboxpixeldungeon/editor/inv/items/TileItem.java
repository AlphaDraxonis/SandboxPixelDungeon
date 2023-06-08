package com.alphadraxonis.sandboxpixeldungeon.editor.inv.items;

import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.EXIT;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.LOCKED_EXIT;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.UNLOCKED_EXIT;

import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Mob;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.DefaultEditComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.EditTileComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.DefaultListItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.EditorInventoryWindow;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.categories.Tiles;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomLevel;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.LevelScheme;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.ActionPart;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.ActionPartList;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.Undo;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.parts.PlaceCellActionPart;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.parts.RemoveHeapActionPart;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.parts.RemoveMobActionPart;
import com.alphadraxonis.sandboxpixeldungeon.editor.util.EditorUtilies;
import com.alphadraxonis.sandboxpixeldungeon.items.Heap;
import com.alphadraxonis.sandboxpixeldungeon.levels.Level;
import com.alphadraxonis.sandboxpixeldungeon.levels.Terrain;
import com.alphadraxonis.sandboxpixeldungeon.levels.features.LevelTransition;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSprite;
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

    @Override
    public void randomizeTexture() {
        image = Tiles.getVisualWithAlts(Tiles.getPlainImage(terrainType));
    }


    @Override
    public String name() {
        return getName(terrainType(), cell());
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
        return image() < 0 ? new Image() : new ItemSprite(this);
    }

    @Override
    public void place(int cell) {
        Undo.addActionPart(new PlaceTileActionPart(cell, terrainType()));
    }


    public static boolean isExitTerrainCell(int terrain) {//why is this not in class Tiles? because it crashes the game for no reason!
        return terrain == EXIT || terrain == LOCKED_EXIT || terrain == UNLOCKED_EXIT;
    }

    public static String getName(int terrainType, int cell) {
        return Messages.titleCase(EditorScene.customLevel().tileName(terrainType)) + EditorUtilies.appendCellToString(cell);
    }


    private static class PlaceTileActionPart extends ActionPartList {

        private final int cell;
        private final PlaceCellActionPart placeCell;

        /**
         * <b>Warning! Already performs a redo in constructor!</b>
         */
        public PlaceTileActionPart(int cell, int terrainType) {

            CustomLevel level = EditorScene.customLevel();

            this.cell = cell;
            int oldTerrain = level.map[cell];

            if (oldTerrain == terrainType) {
                placeCell = null;
                return;//no need to continue bc nothing changes at all
            }

            placeCell = new PlaceCellActionPart(oldTerrain, terrainType, cell, level.traps.get(cell));
            addActionPart(placeCell);
            placeCell.redo();

            //Transition logic

            final LevelTransition transition = level.transitions.get(cell);
            final LevelScheme levelScheme = level.levelScheme;
            final boolean wasExit = TileItem.isExitTerrainCell(oldTerrain);
            final boolean nowExit = TileItem.isExitTerrainCell(terrainType);

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
            };
            addActionPart(transPart);
            transPart.redo();

            EditorScene.updateMap(cell);

            for (int i : PathFinder.NEIGHBOURS9) {
                Mob m = level.getMobAtCell(i + cell);
                if (m != null && !MobItem.validPlacement(m, level, m.pos)) {
                    ActionPart p = new RemoveMobActionPart(m);
                    addActionPart(p);
                    p.redo();
                }
            }

            if (!level.passable[cell]) {
                Heap h = level.heaps.get(cell);
                if (h != null) {
                    ActionPart p = new RemoveHeapActionPart(h);
                    addActionPart(p);
                    p.redo();
                }
            }

        }

        @Override
        public void undo() {
            placeCell.undo();//always change terrain first
            super.undo();
            EditorScene.updateMap(cell);
        }

        @Override
        public void redo() {
            super.redo();
            EditorScene.updateMap(cell);
        }

        @Override
        protected void undoAction(ActionPart action) {
            if (action != placeCell) super.undoAction(action);
        }
    }

}