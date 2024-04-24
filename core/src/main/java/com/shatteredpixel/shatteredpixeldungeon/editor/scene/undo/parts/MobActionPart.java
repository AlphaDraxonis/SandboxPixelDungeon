package com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditMobComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPartModify;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;

public /*sealed*/ abstract class MobActionPart implements ActionPart {

    private Mob mob, copyForUndo;
    protected final boolean bossMobBefore;//can only restore if mob was removed, newly placed mobs can never be boss
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
        Mob mobAtCell = Dungeon.level.findMob(cell);
        if (mobAtCell != null)
            mob = mobAtCell;//This is because another place action could swap the actual mob with another copy
        remove(mob);
    }

    private static void place(Mob mob) {
        EditorScene.add(mob);
        Dungeon.level.occupyCell(mob);
        EditorScene.updateMap(mob.pos);
    }

    private static void remove(Mob mob) {
        mob.destroy();
        mob.sprite.hideEmo();
        mob.sprite.killAndErase();
        if (mob.pos == Dungeon.level.bossmobAt) Dungeon.level.bossmobAt = Level.NO_BOSS_MOB;
        EditorScene.updateMap(mob.pos);
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

        private final Mob realMob;
        private final Mob before;
        private Mob after;

        public Modify(Mob mob) {
            before = (Mob) mob.getCopy();
            realMob = mob;
        }

        @Override
        public void undo() {
            EditMobComp.setToMakeEqual(realMob, before);
        }

        @Override
        public void redo() {
            EditMobComp.setToMakeEqual(realMob, after);
        }

        @Override
        public boolean hasContent() {
            return !EditMobComp.areEqual(before, after);
        }

        @Override
        public void finish() {
            after = (Mob) realMob.getCopy();
        }
    }
}