package com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.parts;

import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.Sign;

import java.util.Objects;

public class SignEditPart {

    public static boolean areEqual(Sign a, Sign b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        if (a.getClass() != b.getClass()) return false;
        if (a.pos != b.pos) return false;
        return Objects.equals(a.text, b.text);
    }

    public static class ActionPart implements com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.ActionPart {

        private int cell;
        private Sign oldSign, newSign;

        public ActionPart(int cell, Sign oldSign, Sign newSign) {
            this.cell = cell;
            this.oldSign = oldSign;
            this.newSign = newSign;
        }


        @Override
        public void undo() {
            EditorScene.customLevel().signs.put(cell, oldSign);
        }

        @Override
        public void redo() {
            EditorScene.customLevel().signs.put(cell, newSign);
        }

        @Override
        public boolean hasContent() {
            return !SignEditPart.areEqual(newSign, oldSign);
        }
    }
}