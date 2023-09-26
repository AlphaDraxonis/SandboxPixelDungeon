package com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.general;

import static com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.WndEditorSettings.ITEM_HEIGHT;

import com.alphadraxonis.sandboxpixeldungeon.Assets;
import com.alphadraxonis.sandboxpixeldungeon.Chrome;
import com.alphadraxonis.sandboxpixeldungeon.SPDAction;
import com.alphadraxonis.sandboxpixeldungeon.SandboxPixelDungeon;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.ui.Button;
import com.alphadraxonis.sandboxpixeldungeon.ui.StyledButton;
import com.watabou.input.GameAction;
import com.watabou.input.KeyBindings;
import com.watabou.input.KeyEvent;
import com.watabou.noosa.Game;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Signal;

public abstract class OutsideSpSwitchTabs extends Component {

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

    public void select(int index){
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