package com.shatteredpixel.shatteredpixeldungeon.editor.ui;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.noosa.ui.Component;

public abstract class ChooseObjectComp extends Component {


    private Object currentObject;

    protected IconButton changeObjBtn;
    protected RenderedTextBlock label;
    protected Spinner.SpinnerTextBlock display;

    public ChooseObjectComp(String label) {

        super();

        this.label = PixelScene.renderTextBlock(label, 8);
        add(this.label);

        display = new Spinner.SpinnerTextBlock(Chrome.get(Chrome.Type.TOAST_WHITE), 8);
        add(display);

        changeObjBtn = new IconButton(Icons.get(Icons.CHANGES)) {
            @Override
            protected void onClick() {
                doChange();
            }
        };
        add(changeObjBtn);
    }

    @Override
    protected void layout() {

        float displayWidth = getDisplayWidth();
        changeObjBtn.setRect(x + width - height, y, height, height);
        if (displayWidth == -1) {
            label.maxWidth((int) (changeObjBtn.left() - 1));
            displayWidth = (changeObjBtn.left() - x - label.width() - 3 - 2);
        }
        display.setRect(x + changeObjBtn.left() - 2 - displayWidth, y, displayWidth, height);

        label.maxWidth((int) (display.left() - 3));
        label.setPos(x, y + (height - label.height()) * 0.5f);

    }

    protected abstract void doChange();

    public void selectObject(Object object) {
        display.setText(objectToString(object));
        currentObject = object;
        layout();
    }

    public Object getObject() {
        return currentObject;
    }

    protected String objectToString(Object object) {
        if (object instanceof Class<?>) return ((Class<?>) object).getSimpleName();
        if (object == null) return " ";
        return object.toString();
    }

    protected float getDisplayWidth() {
        return -1;
    }

    public void enable(boolean value) {
        active = value;
        changeObjBtn.enable(value);
        label.alpha(value ? 1 : 0.3f);
        display.enable(value);
    }
}