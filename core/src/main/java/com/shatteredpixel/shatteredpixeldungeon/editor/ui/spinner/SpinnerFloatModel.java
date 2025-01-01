package com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner;

import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.WndSetValue;

public class SpinnerFloatModel extends SpinnerIntegerModel {

    public static final float ABSOLUTE_MAXIMUM = 9_999_999f;

    //Number of allowed decimal places
    private final int precision;
    private final String precisionFormat;

    public SpinnerFloatModel(float minimum, float maximum, float value) {
        this(minimum, maximum, value, 1, 0.1f);
    }

    public SpinnerFloatModel(float minimum, float maximum, float value, int precision, float stepSize) {
        super(convertToInt(minimum, precision), convertToInt(maximum, precision), convertToInt(value, precision),
                convertToInt(stepSize, precision), false);
        this.precision = precision;
        precisionFormat = "%." + precision + "f";
        setAbsoluteMinAndMax(minimum, ABSOLUTE_MAXIMUM);
    }

    public static int convertToInt(float val, int precision) {
        return (int) (val * (int) Math.pow(10, precision) + 0.01f);
    }

    public static float convertToFloat(Integer val, int precision) {
        if (val == null) return -1;
        return val / (float) Math.pow(10, precision);
    }

    public float getAsFloat() {
        return convertToFloat((Integer) getValue(), precision);
    }

    public int getPrecision() {
        return precision;
    }

    @Override
    protected String displayString(Object value) {
        return value == null ? super.displayString(value) : String.format(Languages.getCurrentLocale(), precisionFormat, getAsFloat());
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof Float) value = convertToInt((Float) value, precision);
        super.setValue(value);
    }

    @Override
    public void setAbsoluteMinimum(float absoluteMinimum) {
        super.setAbsoluteMinimum(convertToInt(absoluteMinimum, precision));
    }

    @Override
    public void setAbsoluteMaximum(float absoluteMaxmimum) {
        super.setAbsoluteMaximum(convertToInt(absoluteMaxmimum, precision));
    }

    @Override
    public void displayInputAnyNumberDialog(float min, float max) {
        min = convertToFloat((int) min, precision);
        max = convertToFloat((int) max, precision);
        WndSetValue.enterFloat(min, max, convertToFloat((int) getValue(), precision), precision,
                Messages.get(this, "input_dialog_title"),
                Messages.get(this, "input_dialog_body",
                        String.format(Languages.getCurrentLocale(), precisionFormat, min),
                        String.format(Languages.getCurrentLocale(), precisionFormat, max),
                        String.format(Languages.getCurrentLocale(), precisionFormat, convertToFloat(getMinimum(), precision)),
                        String.format(Languages.getCurrentLocale(), precisionFormat, convertToFloat(getMaximum(), precision)),
                        String.format(Languages.getCurrentLocale(), precisionFormat, (float) Math.pow(10, -precision))),
                val -> setValue(convertToInt(val, precision)));
    }
}