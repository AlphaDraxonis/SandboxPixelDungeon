package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerTextModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.StyledSpinner;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.ui.Component;

import java.util.Locale;

public class MobStateSpinner extends StyledSpinner {


    public MobStateSpinner(Mob mob) {
        super(new MobStateSpinnerModel(mob), Messages.get(MobStateSpinner.class, "label"), 9);
        setButtonWidth(9f);

        addChangeListener(() -> ((States) getValue()).applyChange(mob));
    }

    private enum States {
        SLEEPING,
        WANDERING,
        HUNTING,
        PASSIVE,
        FLEEING,
        FOLLOWING;

        public static int getIndex(Mob mob) {
            if (mob.following) return 5;
            if (mob.state == mob.SLEEPING) return 0;
            if (mob.state == mob.HUNTING) return 2;
            if (mob.state == mob.PASSIVE) return 3;
            if (mob.state == mob.FLEEING) return 4;
            return 1;//Wandering is default
        }

        public void applyChange(Mob mob) {
            mob.following = false;
            switch (this) {
                case SLEEPING:
                    mob.state = mob.SLEEPING;
                    break;
                case HUNTING:
                    mob.state = mob.HUNTING;
                    break;
                case WANDERING:
                    mob.state = mob.WANDERING;
                    break;
                case PASSIVE:
                    mob.state = mob.PASSIVE;
                    break;
                case FLEEING:
                    mob.state = mob.FLEEING;
                    break;
                case FOLLOWING:
                    mob.following = true;
                    mob.state = mob.HUNTING;
                    break;
            }
        }
    }

    private static class MobStateSpinnerModel extends SpinnerTextModel {

        public MobStateSpinnerModel(Mob mob) {
            super(true, States.getIndex(mob), (Object[]) States.values());
        }

        @Override
        protected String displayString(Object value) {
            States state = (States) value;
            return Messages.get(MobStateSpinner.class, state.name().toLowerCase(Locale.ENGLISH));
        }

        @Override
        public Component createInputField(int fontSize) {
            return super.createInputField(fontSize - 1);
        }
    }
}