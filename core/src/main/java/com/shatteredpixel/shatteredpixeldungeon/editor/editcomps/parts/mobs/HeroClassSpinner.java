package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerTextIconModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.StyledSpinner;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.Image;

public class HeroClassSpinner extends StyledSpinner {


    public HeroClassSpinner(Hero hero) {
        super(new HeroClassSpinnerModel(hero.heroClass), Messages.get(HeroClassSpinner.class, "label"), 7);
        addChangeListener(() -> hero.heroClass = (HeroClass) getValue());
    }

    private static class HeroClassSpinnerModel extends SpinnerTextIconModel {

        public HeroClassSpinnerModel(HeroClass clazz) {
            super(true, clazz.getIndex(), (Object[]) HeroClass.values());
        }

        @Override
        protected String displayString(Object value) {
            return Messages.titleCase(((HeroClass)value).title());
        }

        @Override
        protected Image displayIcon(Object value) {
            return null;
//            return BadgeBanner.image(((HeroClass) value).getIndex());
        }
    }

    public static class SubclassSpinner extends StyledSpinner {

        public SubclassSpinner(Hero hero) {
            super(new SubclassSpinnerModel(hero.subClass), Messages.get(SubclassSpinner.class, "label"), 7);
            addChangeListener(() -> hero.subClass = (HeroSubClass) getValue());
        }

        private static class SubclassSpinnerModel extends SpinnerTextIconModel {

            public SubclassSpinnerModel(HeroSubClass clazz) {
                super(true, clazz.getIndex()+1/*we include none*/, (Object[]) HeroSubClass.values());
            }

            @Override
            protected String displayString(Object value) {
                if (value == HeroSubClass.NONE) return Messages.get(SubclassSpinner.class, "none");
                return Messages.titleCase(((HeroSubClass)value).title());
            }

            @Override
            protected Image displayIcon(Object value) {
                return null;
//                if (value == HeroSubClass.NONE) return new Image();
//                return new HeroIcon((HeroSubClass) value);
            }
        }
    }

}