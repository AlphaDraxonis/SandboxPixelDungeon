package com.shatteredpixel.shatteredpixeldungeon.editor.ui;

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.ui.Component;

public class FoldableComp extends Component {

    protected RenderedTextBlock title;
    protected ColorBlock line;

    protected IconButton expand, fold;

    protected Component body;


    protected FoldableComp() {
    }

    public FoldableComp(String label) {
        title.text(label);
    }

    public FoldableComp(Component body) {
        setBody(body);
    }

    public FoldableComp(String label, Component body) {
        this(label);
        setBody(body);
    }

    @Override
    protected void createChildren() {

        line = new ColorBlock(1, 1, ColorBlock.SEPARATOR_COLOR);
        add(line);

        title = PixelScene.renderTextBlock(titleFontSize());
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

        fold.setVisible(false);
        expand.setVisible(false);
    }

    protected int titleFontSize() {
        return 9;
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
        fold.enable(fold.visible = flag);
        expand.enable(expand.visible = !flag);
        body.visible = body.active = flag;
    }

    public void setBody(Component body) {
        this.body = body;
        add(body);
        showBody(true);
    }

    protected static final int BUTTON_HEIGHT = 13, BUTTON_GAP = 1;

    @Override
    protected void layout() {

        float posY = y;

        float posX = width - 2;
        float titleWidth = posX - requiredWidthForControlButtons();

        title.maxWidth((int) titleWidth);
        float titleHeight = Math.max(BUTTON_HEIGHT, title.height());

        layoutControlButtons(posX, posY, titleHeight);

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

    protected float requiredWidthForControlButtons() {
        float w = 0;
        if (fold.visible) w += BUTTON_HEIGHT + BUTTON_GAP;
        if (expand.visible) w += BUTTON_HEIGHT + BUTTON_GAP;
        return w;
    }

    //posX is from right to left
    protected float layoutControlButtons(float posX, float posY, float titleHeight) {
        if (fold != null && fold.visible) {
            fold.setRect(posX -= BUTTON_HEIGHT + BUTTON_GAP, posY + (titleHeight - fold.icon().height()) / 2f, BUTTON_HEIGHT, BUTTON_HEIGHT);
        }
        if (expand != null && expand.visible) {
            expand.setRect(posX -= BUTTON_HEIGHT + BUTTON_GAP, posY + (titleHeight - expand.icon().height()) / 2f, BUTTON_HEIGHT, BUTTON_HEIGHT);
        }

        return posX;

    }

    protected void layoutParent() {
    }
}