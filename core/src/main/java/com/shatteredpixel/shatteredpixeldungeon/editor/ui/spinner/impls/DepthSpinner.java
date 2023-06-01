package com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.impls;

import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerModel;

public abstract class DepthSpinner extends Spinner {


    public DepthSpinner(int depth,int textSize) {
        super(new SpinnerIntegerModel(0, 26, depth, 1, true, null) {
            @Override
            public float getInputFieldWith(float height) {
                return height * 1.2f;
            }

            @Override
            public int getClicksPerSecondWhileHolding() {
                return 15;
            }
        }, " Depth:", textSize);
        addChangeListener(() -> onChange((Integer) getValue()));
    }

    protected abstract void onChange(int newDepth);
}