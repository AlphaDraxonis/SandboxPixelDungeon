package com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.parts;

import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Mob;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.items.MobItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomLevel;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.ActionPart;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.ActionPartList;
import com.alphadraxonis.sandboxpixeldungeon.items.Heap;
import com.watabou.utils.PathFinder;

public class PlaceTileActionPart extends ActionPartList {

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

//        //Transition logic
//        if (oldTerrain != terrainType) {
//
//            final LevelTransition transition = level.transitions.get(cell);
//            final LevelScheme levelScheme = level.levelScheme;
//
//            addActionPart(new ActionPart() {
//
//                @Override
//                public void undo() {
//                    if (terrainType == Terrain.ENTRANCE) {
//                        levelScheme.entranceCells.remove((Integer) cell);
//                        levelScheme.exitCells.add((Integer) cell);
//                        if (transition != null) {
//                            level.transitions.remove(cell);
//                            EditorScene.remove(transition);
//                        }
//
//                    }
//                }
//
//                @Override
//                public void redo() {
//                    if (terrainType == Terrain.ENTRANCE) {
//                        levelScheme.entranceCells.add((Integer) cell);
//                        levelScheme.exitCells.remove((Integer) cell);
//                        if (transition != null) {
//                            level.transitions.remove(cell);
//                            EditorScene.remove(transition);
//                        }
//                    } else {
//
//                        if (oldTerrain == Terrain.ENTRANCE) {
//                            levelScheme.entranceCells.remove((Integer) cell);
//                            if (transition != null) {
//                                level.transitions.remove(cell);
//                                EditorScene.remove(transition);
//                            }
//                        }
//
//                        boolean wasExit = TileItem.isExitTerrainCell(oldTerrain);
//                        boolean nowExit = TileItem.isExitTerrainCell(terrainType);
//                        if (wasExit != nowExit) {
//                            if (wasExit) {
//                                levelScheme.exitCells.remove((Integer) cell);
//                                if (transition != null) {
//                                    level.transitions.remove(cell);
//                                    EditorScene.remove(transition);
//                                }
//                            } else levelScheme.exitCells.add((Integer) cell);
//                        }
//
//                    }
//                    Collections.sort(levelScheme.entranceCells);
//                    Collections.sort(levelScheme.exitCells);
//                }
//            });
//
//
//        }
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