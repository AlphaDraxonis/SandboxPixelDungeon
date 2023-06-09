package com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.parts.mobs;

import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.parts.items.LevelSpinner;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.Spinner;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.alphadraxonis.sandboxpixeldungeon.items.wands.WandOfRegrowth;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;

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

    private static class LevelSpinnerModel extends SpinnerIntegerModel {

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