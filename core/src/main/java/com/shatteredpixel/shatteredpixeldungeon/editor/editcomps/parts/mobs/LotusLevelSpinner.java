package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs;

import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.items.LevelSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

public class LotusLevelSpinner extends Spinner {


    public LotusLevelSpinner(WandOfRegrowth.Lotus lotus) {
        super(new LevelSpinnerModel(lotus.getLvl()), " "+ Messages.get(LevelSpinner.class,"label") + ":", 10);
        addChangeListener(() -> {
            lotus.setLevel((int) getValue());
            updateDesc(false);
        });
    }

    @Override
    protected void afterClick() {
        updateDesc(true);
    }

    protected void updateDesc(boolean foreceUpdate) {
    }

    public static class LevelSpinnerModel extends SpinnerIntegerModel {

        public LevelSpinnerModel(int level) {
            super(0, 200, level, 1, false, null);

        }

        @Override
        public float getInputFieldWith(float height) {
            return height * 1.3f;
        }

        @Override
        public int getClicksPerSecondWhileHolding() {
            return 70;
        }
    }
}