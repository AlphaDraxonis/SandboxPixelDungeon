package com.shatteredpixel.shatteredpixeldungeon.editor.ui;

import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.watabou.noosa.ui.Component;

public abstract class FoldableCompWithAdd extends FoldableComp {


    protected IconButton remover, adder;
    protected boolean reverseBtnOrder;

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

        fold.setVisible(true);
        expand.setVisible(false);

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

        fold.setVisible(false);
        expand.setVisible(false);

        layoutParent();
    }


    @Override
    protected void layout() {

        //maybe too much copy paste

        float posY = y;

        float posX = width - 2;
        float titleWidth = posX;

        if (remover.visible || adder.visible) titleWidth -= BUTTON_HEIGHT + BUTTON_GAP;
        if (fold.visible || expand.visible) titleWidth -= BUTTON_HEIGHT + BUTTON_GAP;

        title.maxWidth((int) titleWidth);
        float titleHeight = Math.max(BUTTON_HEIGHT, title.height());

        IconButton last = reverseBtnOrder ?
                (fold.visible ? fold : expand):
                (remover.visible ? remover : (adder.visible ? adder : null));

        if (last != null) {
            last.setRect(posX -= BUTTON_HEIGHT + BUTTON_GAP, posY + (titleHeight - last.icon().height()) / 2f, BUTTON_HEIGHT, BUTTON_HEIGHT);
        }

        IconButton next = !reverseBtnOrder ?
                (fold.visible ? fold : expand):
                (remover.visible ? remover : (adder.visible ? adder : null));

        if (next != null) {
            next.setRect(posX -= BUTTON_HEIGHT + BUTTON_GAP, posY + (titleHeight - next.icon().height()) / 2f, BUTTON_HEIGHT, BUTTON_HEIGHT);
        }

        title.setPos(x, (titleHeight - title.height()) * 0.5f + posY + 1);

        posY += titleHeight + 2;

        if (body != null && body.visible) {
            body.setRect(x, posY, width, -1);
            posY = body.bottom();
        }
        height = posY - y + 1;

        line.size(width, 1);
        line.x = x;
        line.y = y + height;

    }
}