package com.shatteredpixel.shatteredpixeldungeon.editor.ui;

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.ui.Component;

//Window where a component can be added, supports scroll pane
public class SimpleWindow extends Window {

    public static final int GAP = MultiWindowTabComp.GAP;

    protected Component title, body, outsideSp;
    protected ScrollPane sp;

    protected float contentAlignment;
    protected float titleAlignment;

    public SimpleWindow() {
        this(Math.min(WndTitledMessage.WIDTH_MAX, (int) (PixelScene.uiCamera.width * 0.9)), (int) (PixelScene.uiCamera.height * 0.8f));
    }

    public SimpleWindow(int width, int height) {
        super(width, height);
    }

    //need to make sure that the window's pointer area is created before!
    public void initComponents(Component title, Component body, Component outsideSp){
        initComponents(title, body, outsideSp, 0f, title instanceof RenderedTextBlock ? 0.5f : 0f);
    }

    public void initComponents(Component title, Component body, Component outsideSp, float alignment, float titleAlignmentX){
        initComponents(title, body, outsideSp, alignment, titleAlignmentX, new ScrollPane(body) {
            @Override
            protected void onScroll() {
                super.onScroll();
                SimpleWindow.this.onScroll(this);
            }
        });
    }

    public void initComponents(Component title, Component body, Component outsideSp, float alignment, float titleAlignmentX, ScrollPane sp){

        this.title = title;
        this.body = body;
        this.outsideSp = outsideSp;
        this.contentAlignment = alignment;
        this.titleAlignment = titleAlignmentX;

        this.sp = sp;
        add(sp);

        if (title != null) add(title);
        if (outsideSp != null) {
            add(outsideSp);
        }

        layout();
    }


    public void layout() {

        if (body == null || sp == null) return;

        float posY = 0;

        posY += GAP * 2;

        if (title != null) {
            if (title instanceof RenderedTextBlock) ((RenderedTextBlock) title).maxWidth(width);
            title.setRect(Math.max(GAP, (width - title.width()) * titleAlignment), posY, width, title.height());
            posY = title.bottom() + GAP * 3;
        }

        body.setSize(width, 0);

        float normalSpHeight;
        if (outsideSp != null) {
            outsideSp.setSize(width, 0);
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

        if (title instanceof RenderedTextBlock) ((RenderedTextBlock) title).maxWidth(width);
        else title.setSize(width, title.height());

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

    @Override
    public void resize(int w, int h) {
        super.resize(w, h);
        if (body != null) layout();
    }

    protected void onScroll(ScrollPane sp) {
    }
}