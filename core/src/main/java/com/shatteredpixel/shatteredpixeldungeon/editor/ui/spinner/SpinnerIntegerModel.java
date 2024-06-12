package com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.WndSetValue;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.watabou.noosa.ui.Component;

public class SpinnerIntegerModel extends AbstractSpinnerModel {

    public static final String INFINITY = "∞";

    private Integer minimum, maximum, value;
    private int absoluteMinimum, absoluteMaximum;
    private int stepSize;
    private boolean cycle;

    public SpinnerIntegerModel() {
        this(0);
    }

    public SpinnerIntegerModel(Integer value) {
        this(Integer.MIN_VALUE, Integer.MAX_VALUE, value);
    }

    public SpinnerIntegerModel(Integer minimum, Integer maximum, Integer value) {
        this(minimum, maximum, value, false);
    }

    public SpinnerIntegerModel(Integer minimum, Integer maximum, Integer value, boolean cycle) {
        this(minimum, maximum, value, 1, cycle);
    }

    public SpinnerIntegerModel(Integer minimum, Integer maximum, Integer value, int stepSize, boolean cycle) {
        this.maximum = maximum;
        this.minimum = minimum;
        this.value = value;
        this.stepSize = stepSize;
        this.cycle = cycle;

        absoluteMaximum = Integer.MAX_VALUE;
        absoluteMinimum = minimum;
    }

    @Override
    public float getInputFieldWidth(float height) {
        return height * 2;
    }

    @Override
    public Component createInputField(int fontSize) {
        valueDisplay = new Spinner.SpinnerTextBlock(Chrome.get(Chrome.Type.TOAST_WHITE), fontSize) {
            @Override
            public void showValue(Object value) {
                textBlock.text(displayString(value));
                layout();
            }

            private Button button;

            @Override
            protected void createChildren(Object... params) {
                super.createChildren(params);
                button = new Button() {
                    @Override
                    protected void onClick() {
                        SpinnerIntegerModel.this.onClick();
                    }
                };
                add(button);
            }

            @Override
            protected void layout() {
                super.layout();
                button.setRect(bg.x, bg.y, bg.width, bg.height);
            }
        };
        return (Component) valueDisplay;
    }


    protected void onClick() {//if textfield is clicked
        displayInputAnyNumberDialog();
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        if (value == null || value instanceof Integer) {
            boolean changed = (this.value == null && value != null) || this.value != null && !this.value.equals(value);
            changeValue(this.value, value);
            if (valueDisplay != null) {
                valueDisplay.showValue(value);
            }
            if (changed) fireStateChanged();
        } else {
//            if (value instanceof Float) setValue((int) (float) value);
            throw new IllegalArgumentException("illegal value");
        }
    }

    @Override
    public void changeValue(Object oldValue, Object newValue) {
        this.value = (Integer) newValue;
    }

    @Override
    public Object getNextValue() {
        if (value == null) return minimum;
        if (value.equals(maximum)) return cycle ? minimum : value;
        long newValue = value + stepSize;
        if (value < maximum && newValue > (long) maximum) return maximum;
        if (newValue > getAbsoluteMaximum()) return getAbsoluteMaximum();
        return (int) newValue;
    }

    @Override
    public Object getPreviousValue() {
        if (value == null) return maximum;
        if (value.equals(minimum)) return (cycle ? maximum : value);
        long newValue = value - stepSize;
        if (value > minimum && newValue < (long) minimum) return minimum;
        if (newValue < getAbsoluteMinimum()) return getAbsoluteMinimum();
        return (int) newValue;
    }

    public void setStepSize(int stepSize) {
        if (this.stepSize != stepSize) {
            this.stepSize = stepSize;
        }
    }

    public void setMaximum(Integer maximum) {
        if (maximum == null) this.maximum = Integer.MAX_VALUE;
        else {
            if (!this.maximum.equals(maximum)) {
                this.maximum = maximum;
//                if (value > maximum) setValue(maximum);
            }
        }
    }

    public void setMinimum(Integer minimum) {
        if (minimum == null) this.minimum = Integer.MIN_VALUE;
        else {
            if (!this.minimum.equals(minimum)) {
                this.minimum = minimum;
//                if (value < minimum) setValue(minimum);
            }
        }
    }

    public void setCycle(boolean cycle) {
        if (cycle != this.cycle) {
            this.cycle = cycle;
        }
    }

    public final Integer getMaximum() {
        return maximum;
    }

    public final Integer getMinimum() {
        return minimum;
    }

    public final int getStepSize() {
        return stepSize;
    }

    public boolean isCycle() {
        return cycle;
    }

    @Override
    public int getClicksPerSecondWhileHolding() {
        int diff = getMaximum() - getMinimum();
        if (diff < 20) return 40;
        if (diff > 10_000) {
            diff = diff > 2_000_000_000 ? 100 : 10_000;
        }
        return (int) Math.ceil(diff / (float) getStepSize() / 2);
    }

    public void setAbsoluteMaximum(float absoluteMaximum) {
        this.absoluteMaximum = (int) absoluteMaximum;
    }

    public void setAbsoluteMinimum(float absoluteMinimum) {
        this.absoluteMinimum = (int) absoluteMinimum;
    }

    public void setAbsoluteMinAndMax(float absoluteMinimum, float absoluteMaximum) {
        setAbsoluteMinimum(absoluteMinimum);
        setAbsoluteMaximum(absoluteMaximum);
    }

    public int getAbsoluteMaximum() {
        return absoluteMaximum;
    }

    public int getAbsoluteMinimum() {
        return absoluteMinimum;
    }

    public void displayInputAnyNumberDialog() {
        displayInputAnyNumberDialog(getAbsoluteMinimum(), getAbsoluteMaximum());
    }

    protected void displayInputAnyNumberDialog(float min, float max) {
        WndSetValue.enterInteger(min, max, (int)getValue(),
                Messages.get(this, "input_dialog_title"),
                Messages.get(this, "input_dialog_body", String.valueOf((int) min), String.valueOf((int) max),
                        String.valueOf(getMinimum()), String.valueOf(getMaximum())), this::setValue);
    }
}