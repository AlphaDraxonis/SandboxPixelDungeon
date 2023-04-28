package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoBuff;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextInput;
import com.watabou.noosa.ui.Component;

public class TestWindow extends Window {

    protected static final int WIDTH_MIN = 120;
    protected static final int WIDTH_MAX = 220;
    protected static final int GAP = 2;

    protected final Component titlebar;TextInput body;

    private ScrollPane sp;

    public  TestWindow(){
        this(WndInfoBuff.createIconTitle(new Invisibility()),null);
    }

    public TestWindow(Component titlebar, Component bodyold) {
        super();

        this.titlebar = titlebar;
        this.body = new TextInput(Chrome.get(Chrome.Type.TOAST_WHITE), false, 6){
        };
this.body.active=true;


        int width = WIDTH_MIN;

        titlebar.setRect(0, 0, width, 0);
        add(titlebar);
        add(this.body);

        layout();
    }

    public void layout() {

        int width = WIDTH_MIN;
        body.setSize(width, 20);

//        while (PixelScene.landscape()
//                && body.bottom() > (PixelScene.MIN_HEIGHT_L - 10)
//                && width < WIDTH_MAX) {
//            width += 20;
//            titlebar.setRect(0, 0, width, 0);
//            body.setMaxWith(width);
//            body.layout();
//        }

        body.setSize(width, 20);

        int height = (int) (body.bottom() + titlebar.bottom());
        int maxHeight = (int) (PixelScene.uiCamera.height * 0.9);

        if (height > maxHeight) {//Needs scrollPane (Used code for debug scroll here as base)
//            height = maxHeight;
//            resize(width, height);
//            Component wrapper = new Component();
//            wrapper.setSize(body.width(), body.height() + GAP);
//            sp = new ScrollPane(wrapper) {
//                @Override
//                protected void layout() {
//                    super.layout();
//                    thumb.visible = false;
//                }
//                @Override
//                protected void redirectEventToButtons(PointerEvent event) {
//                    body.redirectEventToButtons(event);
//                }
//                @Override
//                protected void cancelButtonEvents() {
//                    body.cancelButtonEvents();
//                }
//            };
//            add(sp);
//            wrapper.add(body);
//            body.setPos(0, 1);
//            setPosAfterTitleBar(sp);
//            sp.setSize(width, height - titlebar.bottom() - 2 * GAP);
        } else {
            setPosAfterTitleBar(body);
//            add(body);
            resize(width, (int) body.bottom() + GAP);
        }
        bringToFront(titlebar);
    }

    private void setPosAfterTitleBar(Component comp) {
        comp.setPos(titlebar.left(), titlebar.bottom() + 2 * GAP);
    }

}
