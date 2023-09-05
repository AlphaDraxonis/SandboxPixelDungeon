package com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner;

public class SpinnerFloatModel extends SpinnerIntegerModel {

    public SpinnerFloatModel(float minimum, float maximum, float value, boolean includeInfinity) {
        super(convertToInt(minimum), convertToInt(maximum), convertToInt(value), 1, includeInfinity, includeInfinity ? INFINITY : null);
    }

    public static int convertToInt(float val) {
        return (int) (val * 10 + 0.01f);
    }

    public float getAsFloat() {
        if (getValue() == null) return -1;
        return ((int) getValue()) / 10f;
    }

    @Override
    public String getDisplayString() {
        return getValue() == null ? super.getDisplayString() : Float.toString(getAsFloat());
    }

    @Override
    public float getInputFieldWith(float height) {
        return height * 1.4f;
    }
}