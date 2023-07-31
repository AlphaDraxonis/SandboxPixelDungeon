package com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditTrapComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TrapItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPartModify;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;

public /*sealed*/ abstract class TrapActionPart extends TileItem.PlaceTileActionPart {

    private Trap trap;

    private TrapActionPart(Trap trap, int terrainType, boolean forceChange) {

        super(trap.pos, terrainType, forceChange);

        this.trap = trap;
    }

    protected void place() {
        place(trap.getCopy());
    }

    protected void remove() {
        remove(trap);
    }

    protected static void place(Trap trap) {
        Dungeon.level.setTrap(trap, trap.pos);
    }

    protected static void remove(Trap trap) {
        Dungeon.level.traps.remove(trap.pos);
    }

    public static final class Place extends TrapActionPart {

        public Place(Trap trap) {
            super(trap, TrapItem.getTerrain(trap), true);
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
            addActionPart(part);
            part.redo();
            EditorScene.updateMap(trap.pos);
        }
    }

    public static final class Remove extends TrapActionPart {
        public Remove(Trap trap) {
            super(trap, Terrain.EMPTY, false);
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
            addActionPart(part);
            part.redo();
            EditorScene.updateMap(trap.pos);
        }
    }

    public static final class Modify implements ActionPartModify {

        private final Trap before;
        private Trap after;
        private int oldTerrain;
        private PlaceCellActionPart placeCellActionPart;

        public Modify(Trap trap) {
            before = (Trap) trap.getCopy();
            after = trap;
            oldTerrain = TrapItem.getTerrain(trap);
        }

        @Override
        public void undo() {

            Trap trapAtCell = EditorScene.customLevel().traps.get(after.pos);

            remove(trapAtCell);

            place(before.getCopy());
            placeCellActionPart.undo();

            EditorScene.updateMap(before.pos);
        }

        @Override
        public void redo() {
            Trap trapAtCell = EditorScene.customLevel().traps.get(after.pos);

            if (trapAtCell != null) remove(trapAtCell);

            place(after.getCopy());
            placeCellActionPart.redo();

            EditorScene.updateMap(after.pos);

        }

        @Override
        public boolean hasContent() {
            return !EditTrapComp.areEqual(before, after);
        }

        @Override
        public void finish() {
            after = after.getCopy();
            placeCellActionPart = new PlaceCellActionPart(oldTerrain, TrapItem.getTerrain(after), after.pos, null, null);
        }
    }
}