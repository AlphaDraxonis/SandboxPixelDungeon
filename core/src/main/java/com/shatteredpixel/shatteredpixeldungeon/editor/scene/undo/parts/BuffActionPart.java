package com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditBuffComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPartModify;

public /*sealed*/ abstract class BuffActionPart implements ActionPart {

    private BuffActionPart() {
    }

    @Override
    public boolean hasContent() {
        return true;
    }

    public static final class Modify implements ActionPartModify {

        private final Buff realBuff;
        private final Buff before;
        private Buff after;

        public Modify(Buff buff) {
            before = (Buff) buff.getCopy();
            realBuff = buff;
        }

        @Override
        public void undo() {
            if (realBuff != null) realBuff.copyStats(before);
        }

        @Override
        public void redo() {
            if (realBuff != null) realBuff.copyStats(after);
        }

        @Override
        public boolean hasContent() {
            return !EditBuffComp.areEqual(before, after);
        }

        @Override
        public void finish() {
            after = (Buff) realBuff.getCopy();
        }
    }
}