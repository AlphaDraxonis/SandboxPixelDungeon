package com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.parts.mobs;

import com.alphadraxonis.sandboxpixeldungeon.actors.Char;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.Buff;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.DefaultEditComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.EditBuffComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.EditMobComp;
import com.alphadraxonis.sandboxpixeldungeon.ui.BuffIndicator;

public class BuffIndicatorEditor extends BuffIndicator {

    private final EditMobComp editMobComp;

    public BuffIndicatorEditor(Char ch, boolean large, EditMobComp editMobComp) {
        super(ch, large);
        this.editMobComp = editMobComp;
    }

    protected BuffButton createBuffButton(Buff buff, boolean large) {
        return new BuffButtonEditor(buff, large, this);
    }

    public void updateBuffs() {
        if (editMobComp != null) editMobComp.updateObj();
        layout();
    }

    private static class BuffButtonEditor extends BuffButton {

        private final BuffIndicatorEditor buffIndicator;

        public BuffButtonEditor(Buff buff, boolean large, BuffIndicatorEditor buffIndicator) {
            super(buff, large);
            this.buffIndicator = buffIndicator;
        }

        protected void onClick() {
            if (buff.icon() != NONE)
                DefaultEditComp.showSingleWindow(new EditBuffComp(buff, buffIndicator), null);
        }

    }
}