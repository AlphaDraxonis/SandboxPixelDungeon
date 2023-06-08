package com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.parts;

import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Mob;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.ActionPart;

public /*sealed*/ abstract class MobActionPart implements ActionPart {

    private Mob mob, copyForUndo;
    private final int cell;

    private MobActionPart(Mob mob) {
        this.mob = mob;
        this.cell = mob.pos;
        copyForUndo = (Mob) mob.getCopy();

        redo();
    }

    protected void place() {
        EditorScene.add(copyForUndo);
        Dungeon.level.occupyCell(copyForUndo);
        mob = copyForUndo;
        copyForUndo = (Mob) mob.getCopy();
    }

    protected void remove() {
        Mob mobAtCell = EditorScene.customLevel().getMobAtCell(cell);
        if (mobAtCell != null) mob = mobAtCell;//This is because another place action could swap the actual mob with another copy
        mob.destroy();
        mob.sprite.hideEmo();
        mob.sprite.killAndErase();
    }

    public static final class Place extends MobActionPart {

        public Place(Mob mob) {
            super(mob);
        }

        @Override
        public void undo() {
            remove();
        }

        @Override
        public void redo() {
            place();
        }
    }

    public static final class Remove extends MobActionPart {
        public Remove(Mob mob) {
            super(mob);
        }

        @Override
        public void undo() {
            place();
        }

        @Override
        public void redo() {
            remove();
        }
    }
}