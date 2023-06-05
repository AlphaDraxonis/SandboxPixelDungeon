package com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner;

import com.alphadraxonis.sandboxpixeldungeon.Chrome;
import com.watabou.noosa.ui.Component;

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

    protected void showValue() {
        if (inputField == null) return;
        if (inputField instanceof Spinner.SpinnerTextBlock) {
            Spinner.SpinnerTextBlock casted = (Spinner.SpinnerTextBlock) inputField;
            casted.setText(getAsString(value));
            casted.layout();
        } else
            System.out.println("failed show the value because the input field is not a Spinner.SpinnerTextBlock");
        fireStateChanged();
    }

    public void changeValue(Object oldValue, Object newValue) {
        this.value = newValue;
    }

    protected String getAsString(Object value) {
        return value.toString();
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
    public float getInputFieldWith(float height) {
        return Spinner.FILL;
    }

    @Override
    public Component createInputField(int fontSize) {
        inputField = new Spinner.SpinnerTextBlock(Chrome.get(getChromeType()), 10);
        return inputField;
    }

    public boolean isCycle() {
        return cycle;
    }

    public void setCycle(boolean cycle) {
        this.cycle = cycle;
    }

    protected Chrome.Type getChromeType() {
        return Chrome.Type.TOAST_WHITE;
    }

    @Override
    public void enable(boolean value) {
        if (inputField instanceof Spinner.SpinnerTextBlock)
            ((Spinner.SpinnerTextBlock) inputField).enable(value);
    }
}