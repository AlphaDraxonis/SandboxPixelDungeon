package com.shatteredpixel.shatteredpixeldungeon.editor.ui;

import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.SandboxPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.input.GameAction;
import com.watabou.input.KeyBindings;
import com.watabou.input.KeyEvent;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Signal;

import java.util.List;

import static com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndEditorSettings.ITEM_HEIGHT;

public class CategoryScroller extends Component {

    private static final int MAX_BUTTONS_PER_ROW = 7;

    public final ScrollingListPane list;
    private final Category[] categories;
    private final RedButton[] categoryButtons;
    private int[] indicesWithNonEmptyCats;

    protected Signal.Listener<KeyEvent> keyListener;
    private GameAction curAction;
    private float time;
    private static final float INTERVAL = 0.14f;// 7 cps
    private boolean isHolding;

    private int selectedVisIndex, selectedCatIndex;

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
                        if (selectedVisIndex != idx) {
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

        updateItemsInCategories();

        list = new ScrollingListPane();
        add(list);
    }

    public void updateItemsInCategories() {
        int[] tempIndicesWithNonEmptyCats = new int[categories.length];
        int nextIndex = 0;

        if (categoryButtons == null) {
            for (int i = 0; i < categories.length; i++) {
                List<?> items = categories[i].items(false);
                if (items == null || !items.isEmpty()) {
                    tempIndicesWithNonEmptyCats[nextIndex++] = i;
                }
            }
        } else {
            for (int i = 0; i < categories.length; i++) {
                List<?> items = categories[i].items(false);
                if (items == null || !items.isEmpty()) {
                    tempIndicesWithNonEmptyCats[nextIndex++] = i;
                    categoryButtons[i].visible = categoryButtons[i].active = true;
                } else
                    categoryButtons[i].visible = categoryButtons[i].active = false;
            }
        }
        indicesWithNonEmptyCats = new int[nextIndex];
        System.arraycopy(tempIndicesWithNonEmptyCats, 0, indicesWithNonEmptyCats, 0, nextIndex);
    }


    private boolean processKey() {
        if (indicesWithNonEmptyCats.length > MAX_BUTTONS_PER_ROW) {
            if (curAction == SPDAction.S) {
                int index = (int) (selectedVisIndex + Math.ceil(indicesWithNonEmptyCats.length / 2f)) % indicesWithNonEmptyCats.length;
                selectVisibleCategory(index);
                return true;
            }
            if (curAction == SPDAction.N) {
                int index = (int) (selectedVisIndex - Math.ceil(indicesWithNonEmptyCats.length / 2f)) % indicesWithNonEmptyCats.length;
                if (index < 0) index += Math.ceil(indicesWithNonEmptyCats.length / 2f) * 2;
                selectVisibleCategory(index);
                return true;
            }
        }
        if (curAction == SPDAction.E) {
            selectVisibleCategory((selectedVisIndex + 1) % indicesWithNonEmptyCats.length);
            return true;
        }
        if (curAction == SPDAction.W) {
            int index = selectedVisIndex - 1;
            if (index < 0) index = indicesWithNonEmptyCats.length - 1;
            selectVisibleCategory(index);
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
            if (indicesWithNonEmptyCats.length <= MAX_BUTTONS_PER_ROW) {
                float buttonWidth = width() / indicesWithNonEmptyCats.length;
                for (int i = 0; i < indicesWithNonEmptyCats.length; i++) {
                    int catIndex = indicesWithNonEmptyCats[i];
                    categoryButtons[catIndex].setRect(i * buttonWidth, 0, buttonWidth, ITEM_HEIGHT);
                    PixelScene.align(categoryButtons[catIndex]);
                }
            } else {
                //for first row
                float buttonWidth = (float) (width() / Math.ceil(indicesWithNonEmptyCats.length / 2f));
                float y = 0;
                float x = 0;
                for (int i = 0; i < indicesWithNonEmptyCats.length; i++) {
                    int catIndex = indicesWithNonEmptyCats[i];
                    categoryButtons[catIndex].setRect(x, y, buttonWidth, ITEM_HEIGHT);
                    PixelScene.align(categoryButtons[catIndex]);
                    x += buttonWidth;
                    if (i == Math.ceil(indicesWithNonEmptyCats.length / 2f) - 1) {
                        y += ITEM_HEIGHT;
                        x = 0;
                    }
                }
            }
        }

        selectVisibleCategory(selectedVisIndex);
    }

    @Override
    public synchronized void destroy() {
        super.destroy();
        KeyEvent.removeKeyListener(keyListener);
    }

    public int getSelectedCatIndex() {
        return selectedCatIndex;
    }

    public void selectCategory(int index) {
        int visibleIndex = 0;
        for (int i = 0; i < indicesWithNonEmptyCats.length; i++) {
            if (indicesWithNonEmptyCats[i] == index) {
                visibleIndex = i;
                break;
            }
        }
        selectVisibleCategory(visibleIndex);
    }

    public void selectVisibleCategory(int index) {

        if (indicesWithNonEmptyCats.length == 0) return;

        selectedVisIndex = index;
        selectedCatIndex = indicesWithNonEmptyCats[index];

        list.clear();

        if (categoryButtons != null) {
            for (int i = 0; i < indicesWithNonEmptyCats.length; i++) {
                int catIndex = indicesWithNonEmptyCats[i];
                if (catIndex == selectedCatIndex) categoryButtons[catIndex].icon().color(Window.TITLE_COLOR);
                else categoryButtons[catIndex].icon().resetColor();
            }
        }

        list.scrollTo(0, 0);


        for (Object o : categories[selectedCatIndex].items(true)) {
            //TODO maybe some titles here aswell?
            list.addItem(categories[selectedCatIndex].createListItem(o));
        }

        float bottom = categoryButtons == null ? 0 : categoryButtons[indicesWithNonEmptyCats[indicesWithNonEmptyCats.length - 1]].bottom() + 1;
        list.setRect(x, bottom, width,height - bottom);
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

        private List<?> items;

        public Category() {
        }

        private List<?> items(boolean required) {
            return items == null ? items = createItems(required) : items;
        }

        protected abstract List<?> createItems(boolean required);

        protected abstract ScrollingListPane.ListItem createListItem(Object object);

        protected Image getImage() {
            return new ItemSprite(ItemSpriteSheet.SOMETHING);
        }

        protected String getName() {
            return Messages.NO_TEXT_FOUND;
        }

    }

}