package com.alphadraxonis.sandboxpixeldungeon.editor.ui;

import static com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.WndEditorSettings.ITEM_HEIGHT;

import com.alphadraxonis.sandboxpixeldungeon.Assets;
import com.alphadraxonis.sandboxpixeldungeon.Chrome;
import com.alphadraxonis.sandboxpixeldungeon.SPDAction;
import com.alphadraxonis.sandboxpixeldungeon.SandboxPixelDungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.WndEditorSettings;
import com.alphadraxonis.sandboxpixeldungeon.editor.util.EditorUtilies;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.ui.Button;
import com.alphadraxonis.sandboxpixeldungeon.ui.IconButton;
import com.alphadraxonis.sandboxpixeldungeon.ui.Icons;
import com.alphadraxonis.sandboxpixeldungeon.ui.ScrollPane;
import com.alphadraxonis.sandboxpixeldungeon.ui.StyledButton;
import com.alphadraxonis.sandboxpixeldungeon.windows.IconTitle;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndKeyBindings;
import com.watabou.input.GameAction;
import com.watabou.input.KeyBindings;
import com.watabou.input.KeyEvent;
import com.watabou.noosa.Game;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Signal;

public abstract class MultiWindowTabComp extends WndEditorSettings.TabComp {


    public static final int GAP = 2, BIG_GAP = GAP * 3, BUTTON_HEIGHT = 18;

    private boolean layoutOwnMenu = true;
    private Component otherTitle, otherBody, outsideSp;
    private ScrollPane spForOtherBody;
    private float alignmentOther, titleAlignmentOther;
    private ButtonBack buttonBack;

    protected IconTitle title;
    private ScrollPane sp;
    protected Component content;

    protected Component[] mainWindowComps;

    public MultiWindowTabComp() {

        super();

        sp.givePointerPriority();//Method invocation 'givePointerPriority' will NOT produce 'NullPointerException' -> see: Unable to find cause
        add(sp);
    }

    @Override
    protected void createChildren(Object... params) {
        super.createChildren(params);

        content = new Component();
        sp = new ScrollPane(content);
    }

    @Override
    public void layout() {

        float posY = y;

        if (layoutOwnMenu) {
            title.setRect(x, posY, width, title.height());

            EditorUtilies.layoutStyledCompsInRectangles(GAP, width, content, mainWindowComps);

            sp.setRect(x, title.bottom() + GAP, width, height - title.bottom() - GAP - 1);

        } else {
            posY += GAP * 2;
            float backW = buttonBack.width();
            float backH = buttonBack.height();
            otherTitle.setRect(x + Math.max(backW + GAP, (width - otherTitle.width()) * titleAlignmentOther), posY,
                    width - GAP - backW, Math.max(otherTitle.height(), backH));
            buttonBack.setPos(x, posY + (otherTitle.height() - backH) * 0.5f);
            posY = otherTitle.bottom() + GAP * 3;

            otherBody.setSize(width, -1);

            float normalSpHeight;
            if (outsideSp != null) {
                outsideSp.setSize(width, -1);
                float outsideSpH = outsideSp.height();
                outsideSp.setPos(x, y + height - outsideSpH);
                normalSpHeight = height - outsideSpH - posY - GAP;
                if (outsideSpH == 0) normalSpHeight++;
            } else {
                normalSpHeight = height - posY - 1;
            }
            float makeSpSmaller = Math.max(0, (normalSpHeight - otherBody.height()) * alignmentOther);
            spForOtherBody.setRect(x, posY + makeSpSmaller, width, normalSpHeight - makeSpSmaller);

            spForOtherBody.scrollToCurrentView();
            spForOtherBody.givePointerPriority();
        }
    }

    protected void changeContent(Component titleBar, Component body, Component outsideSp) {
        changeContent(titleBar, body, outsideSp, 0.5f, 0.5f);
    }

    protected void changeContent(Component titleBar, Component body, Component outsideSp, float alignment, float titleAlignmentX) {
        title.visible = title.active = false;
        content.visible = content.active = false;
        sp.visible = sp.active = false;

        if (alignment != -1f) alignmentOther = alignment;
        titleAlignmentOther = titleAlignmentX;

        buttonBack = new ButtonBack();
        add(buttonBack);
        otherTitle = titleBar;
        otherBody = body;
        spForOtherBody = new ScrollPane(otherBody);
        add(spForOtherBody);
        add(otherTitle);
        this.outsideSp = outsideSp;
        if (outsideSp != null) add(outsideSp);
        layoutOwnMenu = false;

        layout();
    }

    //Update layout manually!!!
    public void setAlignmentOther(float alignmentOther) {
        this.alignmentOther = alignmentOther;
    }

    public void closeCurrentSubMenu() {
        layoutOwnMenu = true;
        otherTitle.remove();
        otherTitle.destroy();
        spForOtherBody.remove();
        spForOtherBody.destroy();
        otherBody.remove();
        otherBody.destroy();
        buttonBack.remove();
        buttonBack.destroy();
        if (outsideSp != null) {
            outsideSp.remove();
            outsideSp.destroy();
        }

        title.visible = title.active = true;
        content.visible = content.active = true;
        sp.visible = sp.active = true;

        layout();
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
            if (!(otherBody instanceof BackPressImplemented) || !((BackPressImplemented) otherBody).onBackPressed()) closeCurrentSubMenu();
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
                super(Chrome.Type.GREY_BUTTON_TR, "");
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