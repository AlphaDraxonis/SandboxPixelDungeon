package com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.parts.items;

import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.Spinner;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.items.artifacts.Artifact;
import com.alphadraxonis.sandboxpixeldungeon.items.wands.Wand;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;

public class LevelSpinner extends Spinner {


    public LevelSpinner(Item item) {
        super(new LevelSpinnerModel(item.level(), item instanceof Artifact ? ((Artifact) item).levelCap() : 100),
                " "+ Messages.get(LevelSpinner.class,"label") + ":", 10);
        addChangeListener(() -> {
            item.level((int) getValue());
            if (item instanceof Wand) ((Wand) item).curCharges = ((Wand) item).maxCharges;
            onChange();
        });
    }

    protected void onChange() {
    }

    private static class LevelSpinnerModel extends SpinnerIntegerModel {

        public LevelSpinnerModel(int level) {
            this(level, 100);
        }
        public LevelSpinnerModel(int level, int max) {
            super(0, max, level, 1, false, null);
        }

        @Override
        public float getInputFieldWith(float height) {
            return height * 1.3f;
        }

        @Override
        public int getClicksPerSecondWhileHolding() {
            return 12;
        }

        @Override
        public void displayInputAnyNumberDialog() {
            if (getMaximum() == 100) super.displayInputAnyNumberDialog();
            else super.displayInputAnyNumberDialog(getMinimum(), getMaximum());
        }
    }
}