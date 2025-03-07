package com.shatteredpixel.shatteredpixeldungeon.editor.ui;

import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.watabou.noosa.ui.Component;

public abstract class FoldableCompWithAdd extends FoldableComp {


    protected IconButton remover, adder;
    protected boolean reverseBtnOrder;
    
    {
        expandAndFold.setVisible(false);
    }

    public FoldableCompWithAdd() {
        super();
    }
    public FoldableCompWithAdd(String label) {
        super(label);
    }

    @Override
    protected void createChildren() {
        super.createChildren();
        remover = new IconButton(Icons.get(Icons.CLOSE)) {
            @Override
            protected void onClick() {
                onRemove();
            }
        };

        adder = new IconButton(Icons.get(Icons.PLUS)) {
            @Override
            protected void onClick() {
                onAddClick();
            }
        };

        remover.setVisible(false);
        add(remover);
        add(adder);
    }

    public void setReverseBtnOrder(boolean reverseBtnOrder) {
        this.reverseBtnOrder = reverseBtnOrder;
        layout();
    }

    protected abstract void onAddClick();

    protected void onAdd(Object toAdd, boolean layoutParent) {

        if (body != null) {
            remove(body);
            body.destroy();
        }
        body = createBody(toAdd);
        add(body);


        adder.setVisible(false);
        remover.setVisible(true);

        expanded = true;
        expandAndFold.setVisible(true);

        if (layoutParent) layoutParent();
    }

    protected abstract Component createBody(Object param);

    protected void onRemove() {

        if (body != null) {
            body.destroy();
            remove(body);
            body = null;
        }

        adder.setVisible(true);
        remover.setVisible(false);

        expanded = false;
        expandAndFold.setVisible(false);

        layoutParent();
    }

    @Override
    protected float requiredWidthForControlButtons() {
        float w = super.requiredWidthForControlButtons();
        if (adder.visible) w += BUTTON_HEIGHT + BUTTON_GAP;
        if (remover.visible) w += BUTTON_HEIGHT + BUTTON_GAP;
        return w;
    }

    @Override
    protected float layoutControlButtons(float posX, float posY, float titleHeight) {
        if (reverseBtnOrder) posX = super.layoutControlButtons(posX, posY, titleHeight);

        if (adder != null && adder.visible) {
            adder.setRect(posX -= BUTTON_HEIGHT + BUTTON_GAP, posY + (titleHeight - adder.icon().height()) / 2f, BUTTON_HEIGHT, BUTTON_HEIGHT);
        }
        if (remover != null && remover.visible) {
            remover.setRect(posX -= BUTTON_HEIGHT + BUTTON_GAP, posY + (titleHeight - remover.icon().height()) / 2f, BUTTON_HEIGHT, BUTTON_HEIGHT);
        }

        if (!reverseBtnOrder) posX = super.layoutControlButtons(posX, posY, titleHeight);

        return posX;
    }
}
