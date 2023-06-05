package com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.items;

import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.Spinner;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;

public class LevelSpinner extends Spinner {


    public LevelSpinner(Item item) {
        super(new LevelSpinnerModel(item), " "+ Messages.get(LevelSpinner.class,"label") + ":", 10);
        addChangeListener(() -> {
            item.level((int) getValue());
            onChange();
        });
    }

    protected void onChange() {
    }

    private static class LevelSpinnerModel extends SpinnerIntegerModel {

        private Item item;

        public LevelSpinnerModel(Item item) {
            super(0, 100, item.level(), 1, false, null);

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