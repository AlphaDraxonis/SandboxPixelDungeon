package com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.watabou.noosa.TextInput;
import com.watabou.noosa.ui.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSpinnerModel implements SpinnerModel, Serializable {


    protected Component inputField;
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
        inputField = new TextInput(Chrome.get(Chrome.Type.TOAST_WHITE), false, fontSize, PixelScene.uiCamera.zoom);
        return inputField;
    }

    @Override
    public void setValue(Object value) {
        if (inputField instanceof TextInput)
            ((TextInput) inputField).setText(value == null ? "null" : value.toString());
    }

    @Override
    public int getClicksPerSecondWhileHolding() {
        return 0;
    }
}