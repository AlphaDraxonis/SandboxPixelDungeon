package com.alphadraxonis.sandboxpixeldungeon.editor.ui;

import com.alphadraxonis.sandboxpixeldungeon.ui.IconButton;
import com.alphadraxonis.sandboxpixeldungeon.ui.Icons;
import com.watabou.noosa.ui.Component;

public abstract class FoldableCompWithAdd extends FoldableComp {


    protected IconButton remover, adder;

    public FoldableCompWithAdd() {
        super();
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

        remover.visible = remover.active = false;
        add(remover);
        add(adder);
    }


    protected abstract void onAddClick();

    protected void onAdd(Object toAdd, boolean initialAdding) {

        if (body != null) {
            remove(body);
            body.destroy();
        }
        body = createBody(toAdd);
        add(body);


        adder.visible = adder.active = false;
        remover.visible = remover.active = true;

        fold.visible = fold.active = true;
        expand.visible = expand.active = false;

        if (!initialAdding) layoutParent();
    }

    protected abstract Component createBody(Object param);

    protected void onRemove() {

        if (body != null) {
            body.destroy();
            remove(body);
            body = null;
        }

        adder.visible = adder.active = true;
        remover.visible = remover.active = false;

        fold.visible = fold.active = false;
        expand.visible = expand.active = false;

        layoutParent();
    }


    @Override
    protected void layout() {

        //maybe too much copy paste

        float posY = y;

        float posX = width - 2 - BUTTON_HEIGHT - BUTTON_GAP;
        if (remover.visible)
            remover.setRect(posX, posY, BUTTON_HEIGHT, BUTTON_HEIGHT);
        else if (adder.visible)
            adder.setRect(posX, posY, BUTTON_HEIGHT, BUTTON_HEIGHT);

        if (fold.visible)
            fold.setRect(posX -= BUTTON_HEIGHT + BUTTON_GAP, posY, BUTTON_HEIGHT, BUTTON_HEIGHT);
        else if (expand.visible)
            expand.setRect(posX -= BUTTON_HEIGHT + BUTTON_GAP, posY, BUTTON_HEIGHT, BUTTON_HEIGHT);

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