package com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.mobs;

import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Mob;
import com.alphadraxonis.sandboxpixeldungeon.editor.Koord;
import com.alphadraxonis.sandboxpixeldungeon.ui.BuffIndicator;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndInfoMob;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndTitledMessage;

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
            return new BuffIndicatorEditor(mob, large,null);
        }

        protected String createTitle(Mob mob) {
            return super.createTitle(mob) + " " + new Koord(mob.pos);
        }
    }
}