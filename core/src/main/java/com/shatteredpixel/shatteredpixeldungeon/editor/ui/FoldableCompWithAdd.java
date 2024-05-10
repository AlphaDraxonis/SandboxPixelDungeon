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
    protected void createChildren(Object... params) {
        super.createChildren(params);
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

    protected void onAdd(Object toAdd, boolean initialAdding) {

        if (body != null) {
            remove(body);
            body.destroy();
        }
        body = createBody(toAdd);
        add(body);


        adder.setVisible(false);
        remover.enable(remover.visible = true);

        fold.enable(fold.visible = true);
        expand.setVisible(false);

        if (!initialAdding) layoutParent();
    }

    protected abstract Component createBody(Object param);

    protected void onRemove() {

        if (body != null) {
            body.destroy();
            remove(body);
            body = null;
        }

        adder.enable(adder.visible = true);
        remover.setVisible(false);

        fold.setVisible(false);
        expand.setVisible(false);

        layoutParent();
    }


    @Override
    protected void layout() {

        //maybe too much copy paste

        float posY = y;

        float posX = width - 2 - BUTTON_HEIGHT - BUTTON_GAP;

        IconButton last = reverseBtnOrder ?
                (fold.visible ? fold : expand):
                (remover.visible ? remover : (adder.visible ? adder: null));

        if (last != null) last.setRect(posX, posY + (BUTTON_HEIGHT - last.icon().height()) / 2f, BUTTON_HEIGHT, BUTTON_HEIGHT);

        IconButton next = !reverseBtnOrder ?
                (fold.visible ? fold : expand):
                (remover.visible ? remover : (adder.visible ? adder: null));

        if(next != null) {
            if (last == null) next.setRect(posX, posY + (BUTTON_HEIGHT - next.icon().height()) / 2f, BUTTON_HEIGHT, BUTTON_HEIGHT);
            else next.setRect(posX -= BUTTON_HEIGHT + BUTTON_GAP, posY + (BUTTON_HEIGHT - last.icon().height()) / 2f, BUTTON_HEIGHT, BUTTON_HEIGHT);
        }

        title.maxWidth((int) posX);
        title.setPos(x, (BUTTON_HEIGHT - title.height()) * 0.5f + posY + 1);

        posY += BUTTON_HEIGHT + 2;

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