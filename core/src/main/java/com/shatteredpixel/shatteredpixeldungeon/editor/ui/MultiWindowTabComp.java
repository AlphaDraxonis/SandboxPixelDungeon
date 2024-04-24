package com.shatteredpixel.shatteredpixeldungeon.editor.ui;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.SandboxPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndEditorSettings;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.*;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndKeyBindings;
import com.watabou.input.GameAction;
import com.watabou.input.KeyBindings;
import com.watabou.input.KeyEvent;
import com.watabou.noosa.Game;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Signal;

import static com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndEditorSettings.ITEM_HEIGHT;

public abstract class MultiWindowTabComp extends WndEditorSettings.TabComp {


    public static final int GAP = 2, BIG_GAP = GAP * 3, BUTTON_HEIGHT = 18;


    protected Component title;
    protected ScrollPane sp;
    protected Component content;

    private SubMenuComp subMenuComp;

    protected Component[] mainWindowComps;

    public MultiWindowTabComp() {
        content = new Component();
        sp = new ScrollPane(content);

        sp.givePointerPriority();
        add(sp);
    }

    @Override
    public void layout() {

        float posY = y;

        if (layoutOwnMenu()) {
            if (title != null) {
                if (title instanceof RenderedTextBlock) ((RenderedTextBlock) title).maxWidth((int) width);
                title.setRect(x, posY, width, title.height());
                posY = title.bottom() + GAP;
            }

            layoutOwnContent();

            sp.setRect(x, posY, width, height - posY - 1 + y);

        } else {
            subMenuComp.setRect(x, posY, width, height);
        }
    }

    protected void layoutOwnContent() {
        content.setSize(width, 0);
        content.setSize(width, EditorUtilies.layoutStyledCompsInRectangles(GAP, width, content, mainWindowComps));
    }

    public float preferredHeight(){
        if (layoutOwnMenu()) {
            float result = title.height() + GAP + 1;//+1 is gap to bottom
            layoutOwnContent();
            result += content.height();
            return result;
        } else {
            return subMenuComp.preferredHeight();
        }
    }

    public void changeContent(Component titleBar, Component body, Component outsideSp) {
        changeContent(titleBar, body, outsideSp, 0.5f, 0.5f);
    }

    public void changeContent(Component titleBar, Component body, Component outsideSp, float contentAlignmentV, float titleAlignmentH) {
        changeContent(new SubMenuComp(titleBar, body, outsideSp, contentAlignmentV, titleAlignmentH));
    }

    public void changeContent(SubMenuComp subMenuComp) {

        if (this.subMenuComp != null) destroyCurrentSubMenu();

        title.visible   = title.active   = false;
        content.visible = content.active = false;
        sp.visible      = sp.active      = false;

        this.subMenuComp = subMenuComp;
        if (subMenuComp.buttonBack == null)
            subMenuComp.initBackButton(new ButtonBack());

        add(subMenuComp);

        layout();
    }

    //Update layout manually!!!
    public void setSubMenuContentAlignmentV(float alignment) {
        subMenuComp.contentAlignmentV = alignment;
    }

    public void closeCurrentSubMenu() {
        destroyCurrentSubMenu();

        title.visible   = title.active   = true;
        content.visible = content.active = true;
        sp.visible      = sp.active      = true;

        layout();
    }

    protected void destroyCurrentSubMenu() {
        subMenuComp.remove();
        subMenuComp.destroy();
        subMenuComp = null;
    }

    public final boolean layoutOwnMenu() {
        return subMenuComp == null || !subMenuComp.visible;
    }

    protected final SubMenuComp getSubMenuComp() {
        return subMenuComp;
    }

    public interface BackPressImplemented {
        boolean onBackPressed();
    }

    private class ButtonBack extends IconButton {
        public ButtonBack() {
            super(Icons.BACK.get());

            width = 20;
            height = 15;
        }

        @Override
        protected void onClick() {
            if (!(subMenuComp.body instanceof BackPressImplemented) || !((BackPressImplemented) subMenuComp.body).onBackPressed()) closeCurrentSubMenu();
        }

        @Override
        public GameAction keyAction() {
            return GameAction.BACK;
        }

        @Override
        protected String hoverText() {
            return Messages.titleCase(Messages.get(WndKeyBindings.class, "back"));
        }
    }


    public static class SubMenuComp extends Component {

        protected Component title, body, outsideSp;
        protected ScrollPane sp;
        private ButtonBack buttonBack;

        protected float contentAlignmentV, titleAlignmentH;

        public SubMenuComp(Component titleBar, Component body, Component outsideSp, float contentAlignmentV, float titleAlignmentH) {

            this.contentAlignmentV = contentAlignmentV;
            this.titleAlignmentH = titleAlignmentH;

            this.title = titleBar;
            this.body = body;
            this.outsideSp = outsideSp;
            sp = new ScrollPane(body);
            add(sp);
            add(titleBar);
            if (outsideSp != null) add(outsideSp);
        }

        private void initBackButton(ButtonBack buttonBack) {
            this.buttonBack = buttonBack;
            add(buttonBack);
        }

        @Override
        protected void layout() {

            float posY = y + GAP*2;

            float backW = buttonBack == null ? 0 : buttonBack.width() + GAP;
            float backH = buttonBack == null ? 0 : buttonBack.height();

            if (title instanceof RenderedTextBlock) ((RenderedTextBlock) title).maxWidth((int) (width - backW));
            title.setRect(x + Math.max(backW, (width - title.width()) * titleAlignmentH), posY,
                    width - backW, Math.max(title.height(), backH));

            if (buttonBack != null) buttonBack.setPos(x, posY + (title.height() - buttonBack.height()) * 0.5f);

            posY += Math.max(title.height(), buttonBack.height()) + GAP * 3;

            body.setSize(width, -1);

            float normalSpHeight;
            if (outsideSp != null) {
                outsideSp.setSize(width, -1);
                float outsideSpH = outsideSp.height();
                outsideSp.setPos(x, y + height - outsideSpH);
                normalSpHeight = height - posY - (outsideSpH == 0 ? 1 : outsideSpH + GAP);
            } else {
                normalSpHeight = height - posY - 1;
            }
            float makeSpSmaller = Math.max(0, (normalSpHeight - body.height()) * contentAlignmentV);
            sp.setRect(x, posY + makeSpSmaller, width, normalSpHeight - makeSpSmaller);

            sp.scrollToCurrentView();
            sp.givePointerPriority();
        }

        public float preferredHeight() {

            float backW = buttonBack == null ? 0 : buttonBack.width() + GAP;
            float backH = buttonBack == null ? 0 : buttonBack.height();

            if (title instanceof RenderedTextBlock) ((RenderedTextBlock) title).maxWidth((int) (width - backW));

            float result = GAP*2 + Math.max(title.height(), backH) + GAP*3;

            body.setSize(width, -1);

            if (outsideSp != null) {
                outsideSp.setSize(width, -1);
                float outsideSpH = outsideSp.height();
                if (outsideSpH != 0) {
                    result += outsideSpH + GAP - 1;
                }
            }

            return result;
        }

        public void scrollTo(float x, float y) {
            sp.scrollTo(x, y);
        }
    }


    public static abstract class OutsideSpSwitchTabs extends Component {

        private Signal.Listener<KeyEvent> keyListener;
        private GameAction curAction;
        private float time;
        private static final float INTERVAL = 0.3f;// 3 cps
        private boolean isHolding;

        protected TabControlButton[] tabs;
        protected int currentIndex;

        @Override
        protected void createChildren(Object... params) {
            KeyEvent.addKeyListener(keyListener = keyEvent -> {
                GameAction action = KeyBindings.getActionForKey(keyEvent);

                if (keyEvent.pressed) {
                    curAction = action;
                    return processKey();
                }
                curAction = null;
                time = 0;
                isHolding = false;
                return false;
            });
        }

        @Override
        protected void layout() {
            float buttonWidth = width() / tabs.length;
            for (int i = 0; i < tabs.length; i++) {
                tabs[i].setRect(x + i * buttonWidth, y, buttonWidth, ITEM_HEIGHT);
                PixelScene.align(tabs[i]);
            }
            height = ITEM_HEIGHT;
        }

        private boolean processKey() {
            if (curAction == SPDAction.E) {
                select((currentIndex + 1) % tabs.length);
                return true;
            }
            if (curAction == SPDAction.W) {
                int index = currentIndex - 1;
                if (index < 0) index = tabs.length - 1;
                select(index);
                return true;
            }
            return false;
        }

        @Override
        public synchronized void update() {
            super.update();
            if (curAction != null) {
                time += Game.elapsed;
                if (!isHolding) {
                    if (time >= Button.longClick) {
                        isHolding = true;
                        time -= Button.longClick;
                        SandboxPixelDungeon.vibrate(50);
                    }
                } else {
                    if (time >= INTERVAL) {
                        time -= INTERVAL;
                        processKey();
                    }
                }
            }
        }

        @Override
        public synchronized void destroy() {
            super.destroy();
            KeyEvent.removeKeyListener(keyListener);
        }

        public int getCurrentIndex() {
            return currentIndex;
        }

        public void select(int index) {
            currentIndex = index;
            for (int i = 0; i < tabs.length; i++) {
                tabs[i].setSelected(i == index);
            }
        }

        public abstract String getTabName(int index);

        public class TabControlButton extends StyledButton {
            private static final float SELECTED_R = 2f, SELECTED_G = 2f, SELECTED_B = 2f;

            private boolean selected;

            private final int index;

            public TabControlButton(int index) {
                super(Chrome.Type.GREY_BUTTON_TR, "", 7);
                this.index = index;

                bg.remove();
                bg.destroy();
                bg = new NinePatch(Assets.Interfaces.CHROME, 20, 9, 9, 9, 4) {//Chrome.Type.GREY_BUTTON_TR

                    @Override
                    public void resetColor() {
                        super.resetColor();
                        if (selected) hardlight(SELECTED_R, SELECTED_G, SELECTED_B);
                    }

                    @Override
                    public void brightness(float value) {
                        rm += value - 1f;
                        gm += value - 1f;
                        bm += value - 1f;
                    }
                };
                addToBack(bg);
            }

            @Override
            protected void onClick() {
                select(index);
            }

            @Override
            protected String hoverText() {
                return getTabName(index);
            }

            public void setSelected(boolean selected) {
                this.selected = selected;
                bg.resetColor();
            }

        }
    }

}