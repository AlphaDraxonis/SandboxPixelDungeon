package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerEnumModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.StyledSpinner;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.ui.Component;

public class MobStateSpinner extends StyledSpinner {


    public MobStateSpinner(Mob mob) {
        super(new MobStateSpinnerModel(mob), Messages.get(MobStateSpinner.class, "label"), 9);
        setButtonWidth(9f);

        addChangeListener(() -> ((States) getValue()).applyChange(mob));
    }

    public enum States {
        SLEEPING,
        WANDERING,
        HUNTING,
        PASSIVE,
        FLEEING,
        FOLLOWING;

        public static States get(Mob mob) {
            if (mob.following) return FOLLOWING;
            if (mob.state == mob.SLEEPING) return SLEEPING;
            if (mob.state == mob.HUNTING) return HUNTING;
            if (mob.state == mob.PASSIVE) return PASSIVE;
            if (mob.state == mob.FLEEING) return FLEEING;
            return WANDERING;//Wandering is default
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

    private static class MobStateSpinnerModel extends SpinnerEnumModel<States> {

        public MobStateSpinnerModel(Mob mob) {
            super(States.class, States.get(mob), state -> state.applyChange(mob));
        }

        @Override
        public Component createInputField(int fontSize) {
            return super.createInputField(fontSize - 1);
        }
    }
}