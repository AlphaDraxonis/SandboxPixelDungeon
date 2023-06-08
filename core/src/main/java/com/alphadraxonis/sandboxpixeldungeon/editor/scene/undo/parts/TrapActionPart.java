package com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.parts;

import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.items.TileItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.ActionPart;
import com.alphadraxonis.sandboxpixeldungeon.levels.Terrain;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.Trap;

public /*sealed*/ abstract class TrapActionPart extends TileItem.PlaceTileActionPart {

    private Trap trap;

    private TrapActionPart(Trap trap, int terrainType) {

        super(trap.pos, terrainType);

        this.trap = trap;
    }

    protected void place() {
        Dungeon.level.setTrap(trap, trap.pos);
    }

    protected void remove() {
        Dungeon.level.traps.remove(trap.pos);
    }

    public static final class Place extends TrapActionPart {

        public Place(Trap trap) {
            super(trap, trap.visible ? (trap.active ? Terrain.TRAP : Terrain.INACTIVE_TRAP) : Terrain.SECRET_TRAP);
            ActionPart part = new ActionPart() {
                @Override
                public void undo() {
                    remove();
                }

                @Override
                public void redo() {
                    place();
                }
            };
            addActionPart(part);
            part.redo();
            EditorScene.updateMap(trap.pos);
        }
    }

    public static final class Remove extends TrapActionPart {
        public Remove(Trap trap) {
            super(trap, Terrain.EMPTY);
            ActionPart part = new ActionPart() {
                @Override
                public void undo() {
                    place();
                }

                @Override
                public void redo() {
                    remove();
                }
            };
            addActionPart(part);
            part.redo();
            EditorScene.updateMap(trap.pos);
        }
    }
}