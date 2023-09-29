package com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner;

import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.messages.Languages;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndTextInput;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.watabou.noosa.Game;

public class SpinnerFloatModel extends SpinnerIntegerModel {

    public SpinnerFloatModel(float minimum, float maximum, float value, boolean includeInfinity) {
        super(convertToInt(minimum), convertToInt(maximum), convertToInt(value), 1, includeInfinity, includeInfinity ? INFINITY : null);
    }

    public static int convertToInt(float val) {
        return (int) (val * 10 + 0.01f);
    }

    public float getAsFloat() {
        return getAsFloat((Integer) getValue());
    }

    public float getAsFloat(Integer value) {
        if (value == null) return -1;
        return value / 10f;
    }

    @Override
    public String getDisplayString() {
        return getValue() == null ? super.getDisplayString() : String.format(Languages.getCurrentLocale(), "%.1f", getAsFloat());
    }

    @Override
    public float getInputFieldWith(float height) {
        return height * 1.4f;
    }


    private static char getNumberDecimalSeparator(){
        return String.format(Languages.getCurrentLocale(), "%.1f", 1.1f).charAt(1);
    }

    @Override
    public void displayInputAnyNumberDialog() {
        displayInputAnyNumberDialog(0f, 9_999_999f);
    }

    @Override
    public void displayInputAnyNumberDialog(float min, float max) {
        final char NUMBER_DECIMAL_SEPARATOR = getNumberDecimalSeparator();
        WndTextInput w = new WndTextInput(
                Messages.get(this, "input_dialog_title"),
                Messages.get(this, "input_dialog_body",
                        String.format(Languages.getCurrentLocale(), "%.1f", min),
                        String.format(Languages.getCurrentLocale(), "%.1f", max),
                        String.format(Languages.getCurrentLocale(), "%.1f", getAsFloat(getMinimum())),
                        String.format(Languages.getCurrentLocale(), "%.1f", getAsFloat(getMaximum()))),
                String.format(Languages.getCurrentLocale(), "%.1f", getAsFloat()), 11, false,
                Messages.get(this, "input_dialog_yes"),
                Messages.get(this, "input_dialog_no")
        ) {
            @Override
            public void onSelect(boolean positive, String text) {
                if (positive) {
                    try {
                        setValue(convertToInt(Float.parseFloat(text.replace(NUMBER_DECIMAL_SEPARATOR,'.'))));
                    } catch (NumberFormatException ex) {
                        //just ignore value
                    }
                }
            }
        };
        w.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter() {
            @Override
            public boolean acceptChar(TextField textField, char c) {
                if (super.acceptChar(textField, c)) return true;
                if (!isVorzeichen(c, min, max) && c != NUMBER_DECIMAL_SEPARATOR) return false;
                String txt = textField.getText();
                if (c == NUMBER_DECIMAL_SEPARATOR) {
                    return txt.length() == 0 || !txt.contains(Character.toString(NUMBER_DECIMAL_SEPARATOR));
                }
                return txt.length() == 0 || textField.getCursorPosition() == 0 && !isVorzeichen(txt.charAt(0), min, max);
            }
        });

        w.getTextBox().convertStringToValidString = s -> {
            try {
                float val = Float.parseFloat(s.replace(NUMBER_DECIMAL_SEPARATOR,'.'));
                if (val < min) return String.format(Languages.getCurrentLocale(), "%.1f", min);
                if (val > max) return String.format(Languages.getCurrentLocale(), "%.1f", max);
                return s;
            } catch (NumberFormatException ex) {
                char[] cs = s.toCharArray();
                if (cs.length == 0) return "";
                StringBuilder b = new StringBuilder();
                boolean decimalPointUsed = false;
                for (int i = 0; i < cs.length; i++) {
                    if (Character.isDigit(cs[i])
                            || i == 0 && isVorzeichen(cs[i], min, max)) b.append(cs[i]);
                    else if (!decimalPointUsed) {
                        if (cs[i] == NUMBER_DECIMAL_SEPARATOR) {
                            b.append(cs[i]);
                            decimalPointUsed = true;
                        }
                    }
                }
                while (true) {
                    try {
                        float val = Float.parseFloat(s.replace(NUMBER_DECIMAL_SEPARATOR,'.'));
                        if (val < min) return String.format(Languages.getCurrentLocale(), "%.1f", min);
                        if (val > max) return String.format(Languages.getCurrentLocale(), "%.1f", max);
                        return s;
                    } catch (NumberFormatException ex2) {
                        if (s.length() <= 1) return "";
                        s = s.substring(0, s.length() - 1);
                    }
                }
            }
        };

        if (Game.scene() instanceof EditorScene) EditorScene.show(w);
        else Game.scene().addToFront(w);
    }
}