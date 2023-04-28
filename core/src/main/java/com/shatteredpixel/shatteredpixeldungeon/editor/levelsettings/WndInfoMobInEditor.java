package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.Koord;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HealthBar;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoMob;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.ui.Component;

//from WndInfoMob
public class WndInfoMobInEditor extends WndTitledMessage {

    public WndInfoMobInEditor(Mob mob) {
        super(new MobTitleEd(mob), mob.info() );
    }

    private static class MobTitleEd extends WndInfoMob.MobTitle {

        public MobTitleEd(Mob mob) {
            super(mob, false);
        }

        @Override
        protected BuffIndicator createBuffIndicator(Mob mob, boolean large) {
            return new BuffIndicatorEditor(mob, large);
        }

        protected String createTitle(Mob mob) {
            return super.createTitle(mob) + ": " + new Koord(mob.pos);
        }
    }

    @Override
    public void hide() {
        super.hide();
        WndEditorSettings.showsInfo = false;
    }
}
