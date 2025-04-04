package com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.Sign;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPartModify;

import java.util.Objects;

public class SignActionPart {

    public static boolean areEqual(Sign a, Sign b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        if (a.getClass() != b.getClass()) return false;
        if (a.pos != b.pos) return false;
        return Objects.equals(a.text, b.text);
    }

    public static class ActionPart implements com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPart {

        private int cell;
        private Sign oldSign, newSign;

        public ActionPart(int cell, Sign oldSign, Sign newSign) {
            this.cell = cell;
            this.oldSign = oldSign;
            this.newSign = newSign;
        }


        @Override
        public void undo() {
            Dungeon.level.signs.put(cell, oldSign);
        }

        @Override
        public void redo() {
            Dungeon.level.signs.put(cell, newSign);
        }

        @Override
        public boolean hasContent() {
            return !SignActionPart.areEqual(newSign, oldSign);
        }
    }

    public static final class Modify implements ActionPartModify {

        private Sign before, after;

        public Modify(Sign sign) {
            before = sign.getCopy();
            after = sign;
        }

        @Override
        public void undo() {
            Dungeon.level.signs.put(before.pos, before);
        }

        @Override
        public void redo() {
            Dungeon.level.signs.put(after.pos, after);
        }

        @Override
        public boolean hasContent() {
             return !SignActionPart.areEqual(before, after);
        }

        @Override
        public void finish() {
            after = after.getCopy();
        }
    }
}