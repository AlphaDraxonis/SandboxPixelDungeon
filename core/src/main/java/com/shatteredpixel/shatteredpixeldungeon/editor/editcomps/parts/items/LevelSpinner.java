package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.items;

import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

public class LevelSpinner extends Spinner {


    public LevelSpinner(Item item) {
        super(new LevelSpinnerModel(item.level(), item instanceof Artifact ? ((Artifact) item).levelCap() : 100),
                " " + Messages.get(LevelSpinner.class, "label") + ":", 10);
        SpinnerIntegerModel model = (SpinnerIntegerModel) getModel();
        if (item instanceof Artifact) {
            model.setAbsoluteMinAndMax((float) model.getMinimum(), (float) model.getMaximum());
        } else {
            model.setAbsoluteMinimum(-100f);
        }
        addChangeListener(() -> {
            item.level((int) getValue());
            onChange();
        });
    }

    protected void onChange() {
    }

    public static class LevelSpinnerModel extends SpinnerIntegerModel {

        public LevelSpinnerModel(int level) {
            this(level, 100);
        }

        public LevelSpinnerModel(int level, int max) {
            super(-10, max, level, 1, false, null);
        }

        @Override
        public float getInputFieldWith(float height) {
            return height * 1.3f;
        }

        @Override
        public int getClicksPerSecondWhileHolding() {
            return 12;
        }
    }
}