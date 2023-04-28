package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoBuff;

public class BuffIndicatorEditor extends BuffIndicator {



    public BuffIndicatorEditor(Char ch, boolean large) {
        super(ch, large);
    }

    protected BuffButton createBuffButton(Buff buff, boolean large) {
        return new BuffButtonEditor(buff, large, this);
    }

    public  void  updateBuffs(){
        layout();
    }

    private static class BuffButtonEditor extends BuffButton {

        private final BuffIndicatorEditor buffIndicator;

        public BuffButtonEditor(Buff buff, boolean large, BuffIndicatorEditor buffIndicator) {
            super(buff, large);
            this.buffIndicator=buffIndicator;
        }

        protected void onClick() {
            if (buff.icon() != NONE) EditorScene.show(new WndInfoBuffEditor(buff, buffIndicator));//EditorScene.show();
        }

    }
}
