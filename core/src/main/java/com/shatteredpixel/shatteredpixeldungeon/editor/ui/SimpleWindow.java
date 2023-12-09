package com.shatteredpixel.shatteredpixeldungeon.editor.ui;

import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.ui.Component;

//Window where a component can be added, supports scroll pane
public class SimpleWindow extends Window {

    public static final int GAP = MultiWindowTabComp.GAP;

    protected Component title, body, outsideSp;
    protected ScrollPane sp;

    private float contentAlignment, titleAlignment;

    public SimpleWindow(int width, int height) {
        super(width, height);
    }

    //need to make sure that the window's pointer area is created before!
    public void initComponents(Component title, Component body, Component outsideSp, float alignment, float titleAlignmentX){

        this.title = title;
        this.body = body;
        this.outsideSp = outsideSp;
        this.contentAlignment = alignment;
        this.titleAlignment = titleAlignmentX;

        sp = new ScrollPane(body);
        add(sp);

        add(title);
        if (outsideSp != null) {
            add(outsideSp);
        }

        layout();
    }


    public void layout() {

        float posY = 0;

        posY += GAP * 2;
        if (title instanceof RenderedTextBlock) ((RenderedTextBlock) title).maxWidth(width);
        title.setRect(Math.max(GAP, (width - title.width()) * titleAlignment), posY, width - GAP, title.height());
        posY = title.bottom() + GAP * 3;

        body.setSize(width, -1);

        float normalSpHeight;
        if (outsideSp != null) {
            outsideSp.setSize(width, -1);
            float outsideSpH = outsideSp.height();
            outsideSp.setPos(0, height - outsideSpH);
            normalSpHeight = height - posY - (outsideSpH == 0 ? 1 : outsideSpH + GAP);
        } else {
            normalSpHeight = height - posY - 1;
        }
        float makeSpSmaller = Math.max(0, (normalSpHeight - body.height()) * contentAlignment);
        sp.setRect(0, posY + makeSpSmaller, width, normalSpHeight - makeSpSmaller);

        sp.scrollToCurrentView();
        sp.givePointerPriority();
    }

    public float preferredHeight() {
        float result;
        body.setSize(width, -1);
        result = GAP * 5 + title.height() + body.height() + 1;

        if (outsideSp != null) {
            outsideSp.setSize(width, -1);
            float outsideSpH = outsideSp.height();
            if (outsideSpH != 0) {
                result += outsideSpH + GAP - 1;
            }
        }
        return result;
    }

}