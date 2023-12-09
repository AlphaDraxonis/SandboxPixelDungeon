package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.level;


import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerTextIconModel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.watabou.noosa.Image;

import java.util.Locale;

public class FeelingSpinner extends Spinner {

    public FeelingSpinner(Level.Feeling initialVal) {
        this(initialVal, true);
    }

    public FeelingSpinner(Level.Feeling initialVal, boolean includeRandom) {
        this(initialVal, 10, includeRandom);
    }

    public FeelingSpinner(Level.Feeling initialVal, int textSize, boolean includeRandom) {
        super(new FeelingSpinnerModel(initialVal, includeRandom), " " + Messages.get(FeelingSpinner.class, "label") + ":", textSize);
    }

    private static class FeelingSpinnerModel extends SpinnerTextIconModel {

        public FeelingSpinnerModel(Level.Feeling initialVal, boolean includeRandom) {
            super(true, getFeelingIndex(initialVal, includeRandom), (Object[]) getAllFeelings(includeRandom));
        }

        @Override
        public float getInputFieldWidth(float height) {
            return Spinner.FILL;
        }

        @Override
        public int getClicksPerSecondWhileHolding() {
            return 0;
        }

        @Override
        protected String getAsString(Object feeling) {
            String text = feeling == null ? Messages.get(FeelingSpinner.class, "random") :
                    Messages.get(FeelingSpinner.class, feeling.toString().toLowerCase(Locale.ENGLISH));
            return Messages.titleCase(text);
        }

        public Image getIcon(Object feeling) {
            return Icons.get((Level.Feeling) feeling);
        }

        public static Level.Feeling[] getAllFeelings(boolean includeRandom) {
            Level.Feeling[] fs = Level.Feeling.values();
            int startIndex = includeRandom ? 1 : 0;
            Level.Feeling[] ret = new Level.Feeling[fs.length + startIndex];
            for (int i = startIndex; i < fs.length + startIndex; i++) {
                ret[i] = fs[i - startIndex];
            }
//          ret[0] = null;
            return ret;
        }

        public static int getFeelingIndex(Level.Feeling feeling, boolean includeRandom) {
            int i = 0;
            for (Level.Feeling f : getAllFeelings(includeRandom)) {
                if (f == feeling) return i;
                i++;
            }
            return i;
        }
    }


}