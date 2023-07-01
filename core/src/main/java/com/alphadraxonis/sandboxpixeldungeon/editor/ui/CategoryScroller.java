package com.alphadraxonis.sandboxpixeldungeon.editor.ui;

import static com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.WndEditorSettings.ITEM_HEIGHT;

import com.alphadraxonis.sandboxpixeldungeon.SPDAction;
import com.alphadraxonis.sandboxpixeldungeon.SandboxPixelDungeon;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSprite;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSpriteSheet;
import com.alphadraxonis.sandboxpixeldungeon.ui.Button;
import com.alphadraxonis.sandboxpixeldungeon.ui.RedButton;
import com.alphadraxonis.sandboxpixeldungeon.ui.ScrollingListPane;
import com.alphadraxonis.sandboxpixeldungeon.ui.Window;
import com.watabou.input.GameAction;
import com.watabou.input.KeyBindings;
import com.watabou.input.KeyEvent;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Signal;

import java.util.List;

public class CategoryScroller extends Component {

    public final ScrollingListPane list;
    private final Category[] categories;
    private final RedButton[] categoryButtons;

    protected Signal.Listener<KeyEvent> keyListener;
    private GameAction curAction;
    private float time;
    private static final float INTERVAL = 0.14f;// 7 cps
    private boolean isHolding;

    private int selectedIndex;

    public CategoryScroller(Category[] categories) {
        super();

        this.categories = categories;
        if (categories.length > 1) {
            categoryButtons = new RedButton[categories.length];

            for (int i = 0; i < categoryButtons.length; i++) {
                final int idx = i;
                categoryButtons[i] = new RedButton("") {
                    @Override
                    protected void onClick() {
                        if (selectedIndex != idx) {
                            selectCategory(idx);
                        }
                    }

                    @Override
                    protected String hoverText() {
                        return categories[idx].getName();
                    }
                };
                Image icon = categories[i].getImage();
                if (icon != null) categoryButtons[i].icon(icon);
                else categoryButtons[i].text("-");
                add(categoryButtons[i]);
            }

            KeyEvent.addKeyListener(keyListener = new Signal.Listener<KeyEvent>() {
                @Override
                public boolean onSignal(KeyEvent keyEvent) {
                    GameAction action = KeyBindings.getActionForKey(keyEvent);

                    if (keyEvent.pressed) {
                        curAction = action;
                        return processKey();
                    }
                    curAction = null;
                    time = 0;
                    isHolding = false;
                    return false;
                }
            });
        } else categoryButtons = null;

        list = new ScrollingListPane();
        add(list);
    }


    private boolean processKey() {
        if (categoryButtons.length > 7) {
            if (curAction == SPDAction.S) {
                int index = (int) (selectedIndex + Math.ceil(categoryButtons.length / 2f)) % categoryButtons.length;
                selectCategory(index);
                return true;
            }
            if (curAction == SPDAction.N) {
                int index = (int) (selectedIndex - Math.ceil(categoryButtons.length / 2f)) % categoryButtons.length;
                if (index < 0) index += Math.ceil(categoryButtons.length / 2f) * 2;
                selectCategory(index);
                return true;
            }
        }
        if (curAction == SPDAction.E) {
            selectCategory((selectedIndex + 1) % categoryButtons.length);
            return true;
        }
        if (curAction == SPDAction.W) {
            int index = selectedIndex - 1;
            if (index < 0) index = categoryButtons.length - 1;
            selectCategory(index);
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
    protected void layout() {

        if (categoryButtons != null) {
            if (categoryButtons.length <= 7) {
                float buttonWidth = width() / categoryButtons.length;
                for (int i = 0; i < categoryButtons.length; i++) {
                    categoryButtons[i].setRect(i * buttonWidth, 0, buttonWidth, ITEM_HEIGHT);
                    PixelScene.align(categoryButtons[i]);
                }
            } else {
                //for first row
                float buttonWidth = (float) (width() / Math.ceil(categoryButtons.length / 2f));
                float y = 0;
                float x = 0;
                for (int i = 0; i < categoryButtons.length; i++) {
                    categoryButtons[i].setRect(x, y, buttonWidth, ITEM_HEIGHT);
                    PixelScene.align(categoryButtons[i]);
                    x += buttonWidth;
                    if (i == Math.ceil(categoryButtons.length / 2f) - 1) {
                        y += ITEM_HEIGHT;
                        x = 0;
                        buttonWidth = (float) (width() / Math.ceil(categoryButtons.length / 2f));
                    }
                }
            }
        }

        selectCategory(selectedIndex);
    }

    @Override
    public synchronized void destroy() {
        super.destroy();
        KeyEvent.removeKeyListener(keyListener);
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void selectCategory(int index) {

        selectedIndex = index;

        list.clear();

        if (categoryButtons != null) {
            for (int i = 0; i < categoryButtons.length; i++) {
                if (i == selectedIndex) categoryButtons[i].icon().color(Window.TITLE_COLOR);
                else categoryButtons[i].icon().resetColor();
            }
        }

        list.scrollTo(0, 0);


        for (Object o : categories[selectedIndex].getItems()) {
            //TODO maybe some titles here aswell?
            list.addItem(categories[selectedIndex].createListItem(o));
        }

        float bottom = categoryButtons == null ? 0 : categoryButtons[categoryButtons.length - 1].bottom() + 1;
        list.setRect(x, bottom, width,
                height - bottom);
    }

    public Category[] getCategories() {
        return categories;
    }

    public Component[] getObjectComps() {
        return list.getItems();
    }

    public void updateItems() {
        for (Component i : getObjectComps()) {
            if (i instanceof AdvancedListPaneItem) ((AdvancedListPaneItem) i).onUpdate();
        }
    }

    public float getCurrentViewY() {
        return list.content().camera.scroll.y;
    }

    public abstract static class Category {

        public Category() {
        }

        protected abstract List<?> getItems();

        protected abstract ScrollingListPane.ListItem createListItem(Object object);

        protected Image getImage() {
            return new ItemSprite(ItemSpriteSheet.SOMETHING);
        }

        protected String getName() {
            return Messages.NO_TEXT_FOUND;
        }

    }

}