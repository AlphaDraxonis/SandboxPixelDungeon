package com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.parts;

import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Mob;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.EditMobComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.ActionPart;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.ActionPartModify;

public /*sealed*/ abstract class MobActionPart implements ActionPart {

    private Mob mob, copyForUndo;
    protected final boolean bossMobBefore;//can ony restore if mob was removed, newly placed mobs can never be boss
    protected final int cell;

    private MobActionPart(Mob mob) {
        this.mob = mob;
        this.cell = mob.pos;
        copyForUndo = (Mob) mob.getCopy();
        bossMobBefore = Dungeon.level.bossmobAt == cell;

        redo();
    }

    protected void place() {
        place(copyForUndo);
        mob = copyForUndo;
        copyForUndo = (Mob) mob.getCopy();
    }

    protected void remove() {
        Mob mobAtCell = EditorScene.customLevel().findMob(cell);
        if (mobAtCell != null)
            mob = mobAtCell;//This is because another place action could swap the actual mob with another copy
        remove(mob);
    }

    private static void place(Mob mob) {
        EditorScene.add(mob);
        Dungeon.level.occupyCell(mob);
    }

    private static void remove(Mob mob) {
        mob.destroy();
        mob.sprite.hideEmo();
        mob.sprite.killAndErase();
        if (mob.pos == Dungeon.level.bossmobAt) Dungeon.level.bossmobAt = -1;
    }

    @Override
    public boolean hasContent() {
        return true;
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
            if (bossMobBefore) Dungeon.level.bossmobAt = cell;
        }

        @Override
        public void redo() {
            remove();
        }
    }

    public static final class Modify implements ActionPartModify {

        private final Mob before;
        private Mob after;

        public Modify(Mob mob) {
            before = (Mob) mob.getCopy();
            after = mob;
        }

        @Override
        public void undo() {

            Mob mobAtCell = EditorScene.customLevel().findMob(after.pos);

            remove(mobAtCell);

            place((Mob) before.getCopy());
        }

        @Override
        public void redo() {
            Mob mobAtCell = EditorScene.customLevel().findMob(after.pos);

            if (mobAtCell != null) remove(mobAtCell);

            place((Mob) after.getCopy());

        }

        @Override
        public boolean hasContent() {
            return !EditMobComp.areEqual(before, after);
        }

        @Override
        public void finish() {
            after = (Mob) after.getCopy();
        }
    }
}