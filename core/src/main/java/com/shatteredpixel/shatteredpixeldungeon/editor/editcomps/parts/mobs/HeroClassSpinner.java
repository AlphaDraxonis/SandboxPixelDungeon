package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerEnumModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.StyledSpinner;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.Image;

public class HeroClassSpinner extends StyledSpinner {


    public HeroClassSpinner(Hero hero) {
        super(new HeroClassSpinnerModel(hero), Messages.get(HeroClassSpinner.class, "label"), 7);
    }

    private static class HeroClassSpinnerModel extends SpinnerEnumModel<HeroClass> {

        public HeroClassSpinnerModel(Hero hero) {
            super(HeroClass.class, hero.heroClass, v -> hero.heroClass = v);
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
            super(new SubclassSpinnerModel(hero), Messages.get(SubclassSpinner.class, "label"), 7);
        }
        
        private static class SubclassSpinnerModel extends SpinnerEnumModel<HeroSubClass> {
            
            public SubclassSpinnerModel(Hero hero) {
                super(HeroSubClass.class, hero.subClass, v -> hero.subClass = v);
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
