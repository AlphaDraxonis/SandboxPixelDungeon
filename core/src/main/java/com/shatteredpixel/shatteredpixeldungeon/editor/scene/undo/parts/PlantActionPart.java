package com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditPlantComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPartModify;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;

public /*sealed*/ abstract class PlantActionPart extends TileItem.PlaceTileActionPart {

    private Plant plant;

    private PlantActionPart(Plant plant, int terrainType, boolean forceChange) {

        super(plant.pos, terrainType, forceChange, null);

        this.plant = plant;
    }

    protected void place() {
        place(plant.getCopy());
    }

    protected void remove() {
        remove(plant);
    }

    protected static void place(Plant plant) {
        Dungeon.level.plants.put(plant.pos, plant);
        EditorScene.updateMap(plant.pos);
    }

    protected static void remove(Plant plant) {
        Dungeon.level.plants.remove(plant.pos);
        EditorScene.updateMap(plant.pos);
    }

    public static final class Place extends PlantActionPart {

        public Place(Plant plant) {
            super(plant, Terrain.GRASS, true);
            ActionPart part = new ActionPart() {
                @Override
                public void undo() {
                    // removal is done via PlaceCellActionPart which is also called before
                }

                @Override
                public void redo() {
                    place();
                }

                @Override
                public boolean hasContent() {
                    return true;
                }
            };
            addToMoreActions(part);
            part.redo();
            EditorScene.updateMap(plant.pos);
        }
    }

    public static final class Remove extends PlantActionPart {
        public Remove(Plant plant) {
            super(plant, Terrain.GRASS, false);
            ActionPart part = new ActionPart() {
                @Override
                public void undo() {
                    place();
                }

                @Override
                public void redo() {
                    remove();
                }

                @Override
                public boolean hasContent() {
                    return true;
                }
            };
            addToMoreActions(part);
            part.redo();
            EditorScene.updateMap(plant.pos);
        }
    }

    public static final class Modify implements ActionPartModify {

        private final Plant before;
        private Plant after;

        public Modify(Plant plant) {
            before = (Plant) plant.getCopy();
            after = plant;
        }

        @Override
        public void undo() {

            Plant plantAtCell = EditorScene.customLevel().plants.get(after.pos);

            remove(plantAtCell);

            place(before.getCopy());

            EditorScene.updateMap(before.pos);
        }

        @Override
        public void redo() {
            Plant plantAtCell = EditorScene.customLevel().plants.get(after.pos);

            if (plantAtCell != null) remove(plantAtCell);

            place(after.getCopy());

            EditorScene.updateMap(after.pos);

        }

        @Override
        public boolean hasContent() {
            return !EditPlantComp.areEqual(before, after);
        }

        @Override
        public void finish() {
            after = after.getCopy();
        }
    }
}