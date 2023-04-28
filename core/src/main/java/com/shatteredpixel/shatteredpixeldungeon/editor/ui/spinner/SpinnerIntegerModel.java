package com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.TextInput;
import com.watabou.noosa.ui.Component;

public class SpinnerIntegerModel extends AbstractSpinnerModel {

    public static final String INFINITY = "∞";

    private Integer minimum, maximum, value;
    private int stepSize;
    private boolean cycle;
    private boolean includeNull;


    public SpinnerIntegerModel() {
        this(0);
    }

    public SpinnerIntegerModel(Integer value) {
        this(Integer.MIN_VALUE, Integer.MAX_VALUE, value, 1, true,true);
    }

    public SpinnerIntegerModel(Integer minimum, Integer maximum, Integer value, int stepSize) {
        this(minimum, maximum, value, stepSize, true,true);
    }

    public SpinnerIntegerModel(Integer value, int stepSize, boolean cycle) {
        this(Integer.MIN_VALUE, Integer.MAX_VALUE, value, stepSize, cycle,true);
    }

    public SpinnerIntegerModel(Integer minimum, Integer maximum, Integer value, int stepSize, boolean cycle, boolean includeNull) {
        this.maximum = maximum;
        this.minimum = minimum;
        this.value = value;
        this.stepSize = stepSize;
        this.cycle = cycle;
        this.includeNull = includeNull;
    }

    @Override
    public float getInputFieldWith(float height) {
        return height * 2;
    }

    @Override
    public Component createInputField(int fontSize) {
        inputField = new Spinner.SpinnerTextBlock(Chrome.get(Chrome.Type.TOAST_WHITE), 10);
        return inputField;
    }

    @Override
    public Object getValue() {
        return value;
    }

    public String getDisplayString() {
        return value == null ? INFINITY : value.toString();
    }

    @Override
    public void setValue(Object value) {
        if (value == null || value instanceof Integer) {
            boolean oneWasNull = value == null || this.value == null;
            this.value = (Integer) value;
            if (inputField instanceof Spinner.SpinnerTextBlock) {
                Spinner.SpinnerTextBlock casted = (Spinner.SpinnerTextBlock) inputField;
                casted.setText(getDisplayString());
                if (oneWasNull) {
                    changeOffsetOfTextWhenValueIsNull(casted, value == null);
                }
                casted.layout();
            } else
                System.out.println("failed show the value because the input field is not a Spinner.SpinnerTextBlock");
//            if ((this.value == null && value != null) || this.value != null && !this.value.equals(value))
            fireStateChanged();
        } else {
            throw new IllegalArgumentException("illegal value");
        }
    }

    protected void changeOffsetOfTextWhenValueIsNull(Spinner.SpinnerTextBlock inputField, boolean nowNull) {
        inputField.setTextOffsetY((nowNull ? -1 : 0));
    }

    @Override
    public Object getNextValue() {
        if (value == null) return cycle ? minimum : goToNull();
        if (value.equals(maximum)) return goToNull();
        long newValue = value + stepSize;
        if (newValue > (long) maximum) return maximum;
        return (int) newValue;
    }

    @Override
    public Object getPreviousValue() {
        if (value == null) return cycle ? maximum : goToNull();
        if (value.equals(minimum)) return goToNull();
        long newValue = value - stepSize;
        if (newValue < (long) minimum) return minimum;
        return (int) newValue;
    }

    private Object goToNull(){
      return  includeNull? null : value;
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
                if (value > maximum) setValue(maximum);
            }
        }
    }

    public void setMinimum(Integer minimum) {
        if (minimum == null) this.minimum = Integer.MIN_VALUE;
        else {
            if (!this.minimum.equals(minimum)) {
                this.minimum = minimum;
                if (value < minimum) setValue(minimum);
            }
        }
    }

    public void setCycle(boolean cycle) {
        if (cycle != this.cycle) {
            this.cycle = cycle;
        }
    }

    public void setIncludeNull(boolean includeNull) {
        this.includeNull = includeNull;
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

    public boolean isIncludeNull() {
        return includeNull;
    }

    @Override
    public int getClicksPerSecondWhileHolding() {
        return 60;
    }

}
