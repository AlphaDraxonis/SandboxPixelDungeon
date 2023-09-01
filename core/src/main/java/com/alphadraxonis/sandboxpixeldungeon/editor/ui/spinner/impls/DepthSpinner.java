package com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.impls;

import com.alphadraxonis.sandboxpixeldungeon.editor.overview.floor.WndNewFloor;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.Spinner;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.SpinnerModel;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;

public abstract class DepthSpinner extends Spinner {


    public DepthSpinner(int depth, int textSize) {
        super(createModel(depth), " " + createLabel(), textSize);
        addChangeListener(() -> onChange((Integer) getValue()));
    }

    protected abstract void onChange(int newDepth);


    public static String createLabel() {
        return Messages.get(WndNewFloor.class, "depth");
    }

    public static SpinnerModel createModel(int depth) {
        return new SpinnerIntegerModel(0, 26, depth, 1, true, null) {
            @Override
            public float getInputFieldWith(float height) {
                return height * 1.2f;
            }

            @Override
            public int getClicksPerSecondWhileHolding() {
                return 15;
            }
        };
    }
}