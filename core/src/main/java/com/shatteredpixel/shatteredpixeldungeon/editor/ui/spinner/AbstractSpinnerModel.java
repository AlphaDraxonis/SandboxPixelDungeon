package com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.watabou.noosa.ui.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSpinnerModel implements SpinnerModel, Serializable {


    protected ValueDisplay valueDisplay;
    private List<Runnable> listeners = new ArrayList<>();

    /**
     * Constructor for subclasses to call.
     */
    protected AbstractSpinnerModel() {
    }

    public void addChangeListener(Runnable listener) {
        listeners.add(listener);
    }

    public void removeChangeListener(Runnable listener) {
        listeners.remove(listener);
    }

    public Runnable[] getChangeListeners() {
        return listeners.toArray(new Runnable[0]);
    }

    protected void fireStateChanged() {
        for (Runnable r : listeners) {
            r.run();
        }
    }


    @Override
    public Component createInputField(int fontSize) {
        valueDisplay = new Spinner.SpinnerTextBlock(Chrome.get(getChromeType()), fontSize) {
            @Override
            public void showValue(Object value) {
                textBlock.text(displayString(value));
                layout();
            }
        };
        return ((Component) valueDisplay);
    }

    protected Chrome.Type getChromeType() {
        return Chrome.Type.TOAST_WHITE;
    }

    @Override
    public void setValue(Object value) {
        if (valueDisplay != null)
            valueDisplay.showValue(value);
    }

    protected String displayString(Object value) {
        return value == null ? "null" : value.toString();
    }

    @Override
    public void enable(boolean value) {
        if (valueDisplay != null)
            valueDisplay.enableValueField(value);
    }

    @Override
    public int getClicksPerSecondWhileHolding() {
        return 0;
    }

    public interface ValueDisplay {

        void showValue(Object value);
        void enableValueField(boolean flag);

    }
}