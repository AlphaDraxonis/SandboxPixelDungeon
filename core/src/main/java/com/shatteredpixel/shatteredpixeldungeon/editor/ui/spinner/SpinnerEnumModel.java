package com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.Consumer;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.watabou.noosa.ui.Component;

import java.util.Locale;

public class SpinnerEnumModel<E extends Enum<E>> extends AbstractSpinnerModel {

    private final Class<E> enumClass;
    private final E[] values;

    private E value;
    private int curIndex;

    private Consumer<E> updater;

    public SpinnerEnumModel(Class<E> enumClass, E val, Consumer<E> updater) {
        this.enumClass = enumClass;
        values = enumClass.getEnumConstants();
        value = val;
        curIndex = findIndex(val);
        this.updater = updater;
    }

    private int findIndex(E obj) {
        for (int i = 0; i < values.length; i++) {
            if (values[i] == obj) return i;
        }
        return -1;
    }

    @Override
    public float getInputFieldWidth(float height) {
        return Spinner.FILL;
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
                        SpinnerEnumModel.this.onClick();
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
    }

    @Override
    public Object getValue() {
        return value;
    }

    public String getDisplayString() {
        return Messages.get(enumClass, value.name());
    }

    @Override
    public void setValue(Object value) {
        if (enumClass.isInstance(value)) {
            boolean changed = this.value == null || !this.value.equals(value);
            changeValue(this.value, value);
            if (valueDisplay != null) {
                valueDisplay.showValue(value);
            }
            if (changed) {
                fireStateChanged();
                updater.accept((E) value);
            }
        } else {
            throw new IllegalArgumentException("illegal value");
        }
    }

    @Override
    public void changeValue(Object oldValue, Object newValue) {
        this.value = (E) newValue;
        curIndex = findIndex((E) newValue);
    }

    @Override
    public Object getNextValue() {
        int nextIndex = curIndex+1;
        if (nextIndex >= values.length) nextIndex = 0;
        return values[nextIndex];
    }

    @Override
    public Object getPreviousValue() {
        int prevIndex = curIndex-1;
        if (prevIndex < 0) prevIndex = values.length - 1;
        return values[prevIndex];
    }

    @Override
    public int getClicksPerSecondWhileHolding() {
        int diff = values.length;
        if (diff < 20) return 40;
        if (diff > 10_000) {
            diff = diff > 2_000_000_000 ? 100 : 10_000;
        }
        return (int) Math.ceil(diff / (float) 1 / 2);
    }

    @Override
    protected String displayString(Object value) {
        return Messages.get(enumClass, ((E) value).name().toLowerCase(Locale.ENGLISH));
    }
}