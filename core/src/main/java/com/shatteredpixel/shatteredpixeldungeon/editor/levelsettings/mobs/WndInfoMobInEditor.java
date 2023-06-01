package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.Koord;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndEditorSettings;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.mobs.BuffIndicatorEditor;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoMob;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;

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