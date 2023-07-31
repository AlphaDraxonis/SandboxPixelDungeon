package com.shatteredpixel.shatteredpixeldungeon.editor.inv.items;

import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.ENTRANCE;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.EXIT;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.FURROWED_GRASS;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.GRASS;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.HIGH_GRASS;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.INACTIVE_TRAP;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.LOCKED_EXIT;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.SECRET_TRAP;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.TRAP;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.UNLOCKED_EXIT;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditTileComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.DefaultListItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.EditorInventoryWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Tiles;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPartList;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.HeapActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.MobActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.PlaceCellActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
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

    public static ActionPartList place(int cell, int terrainType) {
        return new PlaceTileActionPart(cell, terrainType, false);
    }


    public static boolean isExitTerrainCell(int terrain) {//why is this not in class Tiles? because it crashes the game for no reason!
        return terrain == EXIT || terrain == LOCKED_EXIT || terrain == UNLOCKED_EXIT;
    }

    public static boolean isTrapTerrainCell(int terrain) {
        return terrain == TRAP || terrain == SECRET_TRAP || terrain == INACTIVE_TRAP;
    }

    public static boolean isGrassTerrainCell(int terrain){
        return terrain == GRASS || terrain == HIGH_GRASS || terrain == FURROWED_GRASS;
    }

    public static String getName(int terrainType, int cell) {
        return Messages.titleCase(EditorScene.customLevel().tileName(terrainType)) + EditorUtilies.appendCellToString(cell);
    }


    public static class PlaceTileActionPart extends ActionPartList {

        private final int cell;
        private final PlaceCellActionPart placeCell;

        public PlaceTileActionPart(int cell, int terrainType, boolean forceChange) {

            CustomLevel level = EditorScene.customLevel();

            this.cell = cell;
            int oldTerrain = level.map[cell];

            if (oldTerrain == terrainType) {
                if (forceChange)
                    addActionPart(placeCell = new PlaceCellActionPart(oldTerrain, terrainType, cell, level.traps.get(cell), level.plants.get(cell)));
                else placeCell = null;
                return;//no need to continue bc nothing changes at all
            }

            addActionPart(placeCell = new PlaceCellActionPart(oldTerrain, terrainType, cell, level.traps.get(cell), level.plants.get(cell)));

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
                addActionPart(transPart);
                transPart.redo();
            }

            EditorScene.updateMap(cell);

            for (int i : PathFinder.NEIGHBOURS9) {
                Mob m = level.getMobAtCell(i + cell);
                if (m != null && MobItem.invalidPlacement(m, level, m.pos)) {
                    ActionPart p = new MobActionPart.Remove(m);
                    addActionPart(p);
                    p.redo();
                }
            }

            if (ItemItem.invalidPlacement(cell, level)) {
                Heap h = level.heaps.get(cell);
                if (h != null) {
                    ActionPart p = new HeapActionPart.Remove(h);
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