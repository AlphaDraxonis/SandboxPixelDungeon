package com.alphadraxonis.sandboxpixeldungeon.editor.ui;

import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.ui.IconButton;
import com.alphadraxonis.sandboxpixeldungeon.ui.Icons;
import com.alphadraxonis.sandboxpixeldungeon.ui.RenderedTextBlock;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.ui.Component;

public class FoldableComp extends Component {

    protected RenderedTextBlock title;
    protected ColorBlock line;

    protected IconButton expand, fold;

    protected Component body;


    protected FoldableComp() {
    }

    public FoldableComp(String label, Component body) {
        this.body = body;
        add(body);
        showBody(true);
        title.text(label);
    }

    @Override
    protected void createChildren(Object... params) {

        line = new ColorBlock(1, 1, 0xFF222222);
        add(line);

        title = PixelScene.renderTextBlock(9);
        add(title);


        expand = new IconButton(Icons.get(Icons.EXPAND)) {
            @Override
            protected void onClick() {
                expand();
            }
        };
        add(expand);

        fold = new IconButton(Icons.get(Icons.FOLD)) {
            @Override
            protected void onClick() {
                fold();
            }
        };
        add(fold);

        fold.visible = fold.active = false;
        expand.visible = expand.active = false;
    }


    //Warning: these methods layout the parent, so don't call them while layouting!
    public void expand() {
        showBody(true);
        layoutParent();
    }

    public void fold() {
        showBody(false);
        layoutParent();
    }

    protected void showBody(boolean flag) {
        fold.visible = fold.active = flag;
        expand.visible = expand.active = !flag;
        body.visible = body.active = flag;
    }

    protected static final int BUTTON_HEIGHT = 13, BUTTON_GAP = 1;

    @Override
    protected void layout() {

        float posY = y;

        float posX = width - 2;

        //insert part for add/remove buttons here

        if (fold.visible)
            fold.setRect(posX -= BUTTON_HEIGHT + BUTTON_GAP, posY + (BUTTON_HEIGHT - fold.icon().height()) / 2f, BUTTON_HEIGHT, BUTTON_HEIGHT);
        else if (expand.visible)
            expand.setRect(posX -= BUTTON_HEIGHT + BUTTON_GAP, posY+ (BUTTON_HEIGHT - expand.icon().height()) / 2f, BUTTON_HEIGHT, BUTTON_HEIGHT);

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

    protected void layoutParent() {
    }
}