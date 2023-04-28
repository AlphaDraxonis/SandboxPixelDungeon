package com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.watabou.noosa.ui.Component;

public class SpinnerTextModel extends AbstractSpinnerModel {

    private boolean cycle;
    private int index;
    private final Object[] data;
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
        this.value = value;
        for (int i = 0; i < data.length; i++) {
            if (data[i] == value) {
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
        if (inputField instanceof Spinner.SpinnerTextBlock) {
            Spinner.SpinnerTextBlock casted = (Spinner.SpinnerTextBlock) inputField;
            casted.setText(getAsString(value));
            casted.layout();
        } else
            System.out.println("failed show the value because the input field is not a Spinner.SpinnerTextBlock");
        fireStateChanged();
    }
    protected String getAsString(Object value){
        return  value.toString();
    }

    @Override
    public Object getNextValue() {
        if (index >= data.length - 1) return isCycle() ? 0 : null;
        return data[index+1];
    }

    @Override
    public Object getPreviousValue() {
        if (index <= 0) return isCycle() ? data.length - 1 : null;
        return data[index-1];
    }

    @Override
    public float getInputFieldWith(float height) {
        return Spinner.FILL;
    }

    @Override
    public Component createInputField(int fontSize) {
        inputField = new Spinner.SpinnerTextBlock(Chrome.get(Chrome.Type.TOAST_WHITE), 10);
        return inputField;
    }

    public boolean isCycle() {
        return cycle;
    }

    public void setCycle(boolean cycle) {
        this.cycle = cycle;
    }
}
