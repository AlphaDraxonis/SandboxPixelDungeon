package com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner;

import java.util.Objects;

public class SpinnerTextModel extends AbstractSpinnerModel {

    private boolean cycle;
    private int index;
    private Object[] data;
    private Object value;

    public SpinnerTextModel(Object... data) {
        this(false, data);
    }

    public SpinnerTextModel(boolean cycle, Object... data) {
        this(cycle, 0, data);
    }

    public SpinnerTextModel(boolean cycle, int initValueIndex, Object... data) {
        this.cycle = cycle;
        this.data = data;
        index = initValueIndex;
        updateValue();
    }

    public void setData(Object[] data) {
        this.data = data;
        index = 0;
        if (data.length > 0) setValue(data[0]);
    }

    private void updateValue() {
        if (data.length - 1 >= index) {
            setValue(data[index]);
        } else setValue(null);
    }


    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        changeValue(this.value, value);
        for (int i = 0; i < data.length; i++) {
            if (Objects.equals(data[i], value)) {
                index = i;
                showValue();
                return;
            }
        }
        if (value != null) {
            index = data.length;
            showValue();
        }
    }

    private void showValue() {
        if (valueDisplay != null) {
            valueDisplay.showValue(getValue());
        }
        fireStateChanged();
    }

    public void changeValue(Object oldValue, Object newValue) {
        this.value = newValue;
    }

    @Override
    public Object getNextValue() {
        if (index >= data.length - 1) return isCycle() ? data[0] : null;
        return data[index + 1];
    }

    @Override
    public Object getPreviousValue() {
        if (index <= 0) return isCycle() ? data[data.length - 1] : null;
        return data[index - 1];
    }

    @Override
    public float getInputFieldWidth(float height) {
        return Spinner.FILL;
    }

    public boolean isCycle() {
        return cycle;
    }

    public void setCycle(boolean cycle) {
        this.cycle = cycle;
    }

    public int getCurrentIndex() {
        return index;
    }
}