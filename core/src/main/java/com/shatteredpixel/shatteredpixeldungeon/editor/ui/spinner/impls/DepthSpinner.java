package com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.impls;

import com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor.WndNewFloor;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.Function;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

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
        return createModel(depth, height -> height * 1.2f);
    }

    public static SpinnerModel createModel(int depth, Function<Float, Float> getInputFieldWith) {
        return new SpinnerIntegerModel(0, 26, depth, 1, true, null) {
            {
                setAbsoluteMaximum(999f);
            }
            @Override
            public float getInputFieldWidth(float height) {
                return getInputFieldWith.apply(height);
            }

            @Override
            public int getClicksPerSecondWhileHolding() {
                return 15;
            }
        };
    }
}