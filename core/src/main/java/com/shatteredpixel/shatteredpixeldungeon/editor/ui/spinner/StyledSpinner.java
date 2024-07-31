package com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;

public class StyledSpinner extends Spinner {

    protected static final int GAP_ICON = 1;

    protected NinePatch bg;
    protected int spinnerHeight;

    protected Image icon;


    public StyledSpinner(SpinnerModel model, String name) {
        this(model, name, 9);
    }

    public StyledSpinner(SpinnerModel model, String name, int textSize) {
        this(model, name, textSize, null);
    }

    public StyledSpinner(SpinnerModel model, String name, int textSize, Image icon) {
        this(model, name, textSize, ItemSpriteSheet.SIZE, icon);
    }

    public StyledSpinner(SpinnerModel model, String name, int textSize, int spinnerHeight, Image icon) {
        super(model, name, textSize);

        bg = Chrome.get(Chrome.Type.GREY_BUTTON_TR);
        addToBack(bg);

        setSpinnerHeight(spinnerHeight);
        setAlignmentSpinnerX(ALIGNMENT_CENTER);

        icon(icon);

        layout();
    }

    @Override
    protected void layout() {
        if (spinnerHeight == 0) return;

        height = Math.max(getMinimumHeight(width()), Math.max(Math.max(label.height(), 10), height()));

        bg.x = x;
        bg.y = y;
        bg.size(width(), height());

        float spaceForLabel = Math.max(label.height(), (icon == null ? 0 : icon.height()));

        float bw = getButtonWidth();
        if (bw == SQUARE) bw = spinnerHeight;
        else bw = Math.max(bw, 6);

        float txtWidth = getModel().getInputFieldWidth(spinnerHeight);
        if (txtWidth == FILL) txtWidth = width - bw * 2 - bg.marginHor();

        float conW = bw * 2 - 1;
        float startX = x + (width - conW - txtWidth) * getAlignmentSpinnerX() + 1;
        if (getAlignmentSpinnerX() == ALIGNMENT_LEFT) startX = x + bg.marginHor() / 2f;
        else if (getAlignmentSpinnerX() == ALIGNMENT_RIGHT) startX -= bg.marginHor() / 4f + 1;
        float conY = y + (height() - spinnerHeight - spaceForLabel) / 2f + 2 + spaceForLabel;

        leftButton.setRect(startX, conY, bw, spinnerHeight);
        PixelScene.align(leftButton);
        inputField.setRect(leftButton.right() - 1, conY, txtWidth, spinnerHeight);
        PixelScene.align(inputField);
        rightButton.setRect(inputField.right() - 1, conY, bw, spinnerHeight);
        PixelScene.align(rightButton);


        if (!label.text().equals("")) {

            if (icon == null) {

                label.maxWidth((int) width);
                label.setPos(
                        x + (width() - label.width() * 2 + 1) * getAlignmentSpinnerX() +
                                label.width() / 2f,// /2f is labels alignment!!!!
                        conY - 3 - label.height()
                );
            } else {
                label.maxWidth((int) (width - GAP_ICON - icon.width()));
                float posY = conY - 3 - spaceForLabel;
                float totalUsedWidth = label.width() + GAP_ICON + icon.width();
                icon.y = icon.height() == spaceForLabel ? posY : posY + (spaceForLabel - icon.height()) * 0.5f;
                icon.x = x + (width() - totalUsedWidth + 1) * getAlignmentSpinnerX();
                label.setPos(icon.x + icon.width() + GAP_ICON, label.height() == spaceForLabel ? posY : posY + (spaceForLabel - label.height()) * 0.5f);
            }
            PixelScene.align(label);
        }
    }

    public float getMinimumHeight(float width) {
        label.maxWidth((int) (width - (icon == null ? 0 : GAP_ICON + icon.width())));
        return spinnerHeight + Math.max(label.height(), (icon == null ? 0 : icon.height())) + 4 + bg.marginVer();
    }

    public void setSpinnerHeight(int spinnerHeight) {
        this.spinnerHeight = spinnerHeight;
    }

    public int getSpinnerHeight() {
        return spinnerHeight;
    }

    public Image icon() {
        return icon;
    }

    public void icon(Image icon) {
        if (this.icon != null) {
            this.icon.destroy();
            this.icon.remove();
        }
        this.icon = icon;
        if (icon != null) {
            add(icon);
        }
    }
}