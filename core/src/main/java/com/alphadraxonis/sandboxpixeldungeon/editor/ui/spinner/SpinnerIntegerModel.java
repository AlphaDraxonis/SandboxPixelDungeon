package com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.alphadraxonis.sandboxpixeldungeon.Chrome;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.ui.Button;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndTextInput;
import com.watabou.noosa.Game;
import com.watabou.noosa.ui.Component;

public class SpinnerIntegerModel extends AbstractSpinnerModel {

    public static final String INFINITY = "∞";

    private Integer minimum, maximum, value, absoluteMinimum, absoluteMaximum;
    private int stepSize;
    private boolean cycle;
    private String showWhenNull;

    public SpinnerIntegerModel() {
        this(0);
    }

    public SpinnerIntegerModel(Integer value) {
        this(Integer.MIN_VALUE, Integer.MAX_VALUE, value, 1, true, INFINITY);
    }

    public SpinnerIntegerModel(Integer minimum, Integer maximum, Integer value, int stepSize) {
        this(minimum, maximum, value, stepSize, true, INFINITY);
    }

    public SpinnerIntegerModel(Integer value, int stepSize, boolean cycle) {
        this(Integer.MIN_VALUE, Integer.MAX_VALUE, value, stepSize, cycle, INFINITY);
    }

    public SpinnerIntegerModel(Integer minimum, Integer maximum, Integer value, int stepSize, boolean cycle, String showWhenNull) {
        this.maximum = maximum;
        this.minimum = minimum;
        this.value = value;
        this.stepSize = stepSize;
        this.cycle = cycle;
        this.showWhenNull = showWhenNull;

        absoluteMaximum = Integer.MAX_VALUE;
        absoluteMinimum = 0;
    }

    @Override
    public float getInputFieldWith(float height) {
        return height * 2;
    }

    @Override
    public Component createInputField(int fontSize) {
        inputField = new Spinner.SpinnerTextBlock(Chrome.get(Chrome.Type.TOAST_WHITE), fontSize) {
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
        return inputField;
    }


    protected void onClick() {//if textfield is clicked
        displayInputAnyNumberDialog();
    }

    @Override
    public Object getValue() {
        return value;
    }

    public String getDisplayString() {
        return value == null ? showWhenNull : value.toString();
    }

    @Override
    public void setValue(Object value) {
        if (value == null || value instanceof Integer) {
            boolean oneWasNull = value == null || this.value == null;
            boolean changed = (this.value == null && value != null) || this.value != null && !this.value.equals(value);
            changeValue(this.value, value);
            if (inputField instanceof Spinner.SpinnerTextBlock) {
                Spinner.SpinnerTextBlock casted = (Spinner.SpinnerTextBlock) inputField;
                casted.setText(getDisplayString());
                if (oneWasNull) {
                    changeOffsetOfTextWhenValueIsNull(casted, value == null);
                }
                casted.layout();
            } else
                System.out.println("failed show the value because the input field is not a Spinner.SpinnerTextBlock");
            if (changed) fireStateChanged();
        } else {
            throw new IllegalArgumentException("illegal value");
        }
    }

    public void changeValue(Object oldValue, Object newValue) {
        this.value = (Integer) newValue;
    }

    protected void changeOffsetOfTextWhenValueIsNull(Spinner.SpinnerTextBlock inputField, boolean nowNull) {
        inputField.setTextOffsetY((nowNull ? -1 : 0));
    }

    @Override
    public Object getNextValue() {
        if (value == null) return cycle ? minimum : goToNull(minimum);
        if (value.equals(maximum)) return cycle ? goToNull(minimum) : value;
        long newValue = value + stepSize;
        if (value < maximum && newValue > (long) maximum) return maximum;
        if (newValue > getAbsoluteMaximum()) return getAbsoluteMaximum();
        return (int) newValue;
    }

    @Override
    public Object getPreviousValue() {
        if (value == null) return cycle ? maximum : goToNull(maximum);
        if (value.equals(minimum)) return goToNull(cycle ? maximum : value);
        long newValue = value - stepSize;
        if (value > minimum && newValue < (long) minimum) return minimum;
        if (newValue < getAbsoluteMinimum()) return getAbsoluteMinimum();
        return (int) newValue;
    }

    private Object goToNull(Object alternative) {
        return showWhenNull != null ? null : alternative;
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

    public void setShowWhenNull(String showWhenNull) {
        this.showWhenNull = showWhenNull;
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

    public String getShowWhenNull() {
        return showWhenNull;
    }

    @Override
    public int getClicksPerSecondWhileHolding() {
        return 60;
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

    public Integer getAbsoluteMaximum() {
        return absoluteMaximum;
    }

    public Integer getAbsoluteMinimum() {
        return absoluteMinimum;
    }

    @Override
    public void enable(boolean value) {
        if (inputField instanceof Spinner.SpinnerTextBlock)
            ((Spinner.SpinnerTextBlock) inputField).enable(value);
    }

    public void displayInputAnyNumberDialog() {
        displayInputAnyNumberDialog(getAbsoluteMinimum(), getAbsoluteMaximum());
    }

    protected void displayInputAnyNumberDialog(float min, float max) {
        WndTextInput w = new WndTextInput(
                Messages.get(this, "input_dialog_title"),
                Messages.get(this, "input_dialog_body", String.valueOf((int) min), String.valueOf((int) max),
                        String.valueOf(getMinimum()), String.valueOf(getMaximum())),
                getValue().toString(), 12, false,
                Messages.get(this, "input_dialog_yes"),
                Messages.get(this, "input_dialog_no")
        ) {
            @Override
            public void onSelect(boolean positive, String text) {
                if (positive) {
                    try {
                        setValue((int) Math.max(min, Integer.parseInt(text)));
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
                if (!isVorzeichen(c, min, max)) return false;
                String txt = textField.getText();
                return txt.length() == 0 || textField.getCursorPosition() == 0 && !isVorzeichen(txt.charAt(0), min, max);
            }
        });

        w.getTextBox().convertStringToValidString = s -> {
            try {
                int val = Integer.parseInt(s);
//                if (val < min) return Integer.toString((int) min);
                if (val > max) return Integer.toString((int) max);
                return s;
            } catch (NumberFormatException ex) {
                char[] cs = s.toCharArray();
                if (cs.length == 0) return "";
                StringBuilder b = new StringBuilder();
                for (int i = 0; i < cs.length; i++) {
                    if (Character.isDigit(cs[i])
                            || i == 0 && isVorzeichen(cs[i], min, max)) b.append(cs[i]);
                }
                s = b.toString();
                while (true) {
                    try {
                        int val = Integer.parseInt(s);
                        if (val < min) return Integer.toString((int) min);
                        if (val > max) return Integer.toString((int) max);
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

    protected static boolean isVorzeichen(char c, float min, float max) {
        return c == '-' && min < 0 || c == '+' && max > 0;
    }
}