package com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner;

import com.alphadraxonis.sandboxpixeldungeon.Chrome;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.NinePatch;

public class StyledSpinner extends Spinner {

    protected NinePatch bg;
    protected int spinnerHeight;


    public StyledSpinner(SpinnerModel model, String name, int textSize) {
        this(model, name, textSize, ItemSpriteSheet.SIZE);
    }

    public StyledSpinner(SpinnerModel model, String name, int textSize, int spinnerHeight) {
        super(model, name, textSize);
        setSpinnerHeight(spinnerHeight);
        setAlignmentSpinnerX(ALIGNMENT_CENTER);
        layout();
    }

    @Override
    protected void createChildren(Object... params) {
        bg = Chrome.get(Chrome.Type.GREY_BUTTON_TR);
        add(bg);
        super.createChildren(params);
        sendToBack(bg);
    }

    @Override
    protected void layout() {
        if (spinnerHeight == 0) return;

        height = Math.max(getMinimumHeight(width()), Math.max(Math.max(label.height(), 10), height()));

        bg.x = x;
        bg.y = y;
        bg.size(width(), height());

        float bw = getButtonWidth();
        if (bw == SQUARE) bw = spinnerHeight;
        else bw = Math.max(bw, 6);

        float txtWidth = getModel().getInputFieldWith(height);
        if (txtWidth == FILL) txtWidth = width - bw * 2 - 1;
        txtWidth = 23;

        float conW = bw * 2 - 1;
        float startX = x + (width - conW - txtWidth) * getAlignmentSpinnerX() + 1;
        if (getAlignmentSpinnerX() == ALIGNMENT_LEFT) startX = x + bg.marginHor() / 2f;
        else if (getAlignmentSpinnerX() == ALIGNMENT_RIGHT) startX -= bg.marginHor() / 4f + 1;
        float conY = y + (height() - spinnerHeight - label.height()) / 2f + 2 + label.height();

        leftButton.setRect(startX, conY, bw, spinnerHeight);
        PixelScene.align(leftButton);
        inputField.setRect(leftButton.right() - 1, conY, txtWidth, spinnerHeight);
        PixelScene.align(inputField);
        rightButton.setRect(inputField.right() - 1, conY, bw, spinnerHeight);
        PixelScene.align(rightButton);


        if (!label.text().equals("")) {
            label.maxWidth((int) width);
            label.setPos(
                    x + (width() - label.width() * 2 + 1) * getAlignmentSpinnerX() +
                            label.width() / 2f,// /2f is labels alignment!!!!
                    conY - 3 - label.height()
            );
            PixelScene.align(label);
        }
    }

    public float getMinimumHeight(float width) {
        label.maxWidth((int) width);
        return spinnerHeight + label.height() + 4 + bg.marginVer();
    }

    public void setSpinnerHeight(int spinnerHeight) {
        this.spinnerHeight = spinnerHeight;
    }

    public int getSpinnerHeight() {
        return spinnerHeight;
    }
}