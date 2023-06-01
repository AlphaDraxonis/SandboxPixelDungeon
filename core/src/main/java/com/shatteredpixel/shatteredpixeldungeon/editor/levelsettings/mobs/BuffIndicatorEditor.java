package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.editcomps.EditMobComp;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

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
        if (editMobComp != null) editMobComp.updateItem();
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
                EditorScene.show(new WndInfoBuffEditor(buff, buffIndicator));
        }

    }
}