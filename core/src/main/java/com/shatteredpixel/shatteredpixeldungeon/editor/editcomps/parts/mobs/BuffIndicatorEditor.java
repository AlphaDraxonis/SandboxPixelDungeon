package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditBuffComp;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

public class BuffIndicatorEditor extends BuffIndicator {

    private DefaultEditComp<?> editComp;

    public BuffIndicatorEditor(Char ch, boolean large, DefaultEditComp<?> editComp) {
        super(ch, large);
        this.editComp = editComp;
    }

    protected BuffButton createBuffButton(Buff buff, boolean large) {
        return new BuffButtonEditor(buff, large);
    }

    private class BuffButtonEditor extends BuffButton {

        public BuffButtonEditor(Buff buff, boolean large) {
            super(buff, large);
        }

        protected void onClick() {
            if (buff.icon() != NONE)
                DefaultEditComp.showSingleWindow(new EditBuffComp(buff, editComp), null);
        }

    }
}