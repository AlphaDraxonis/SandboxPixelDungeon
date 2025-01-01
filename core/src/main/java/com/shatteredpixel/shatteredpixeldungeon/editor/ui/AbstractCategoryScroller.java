package com.shatteredpixel.shatteredpixeldungeon.editor.ui;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.SandboxPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.DefaultListItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.EditorInventoryWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TileItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.watabou.input.GameAction;
import com.watabou.input.KeyBindings;
import com.watabou.input.KeyEvent;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Signal;

import java.util.List;

public abstract class AbstractCategoryScroller<T extends Component> extends Component {

    protected final EditorInventoryWindow window;

    public final ScrollPane sp;
    protected final Category[] categories;
    protected int[] indicesWithNonEmptyCats;
    protected boolean showEmptyCategories = false;

    protected int selectedVisIndex;
	private int selectedCatIndex;

    protected final T[] categoryComps;//each of these is responsible to somehow display one category

    protected Signal.Listener<KeyEvent> keyListener;
    protected GameAction curAction;
    private float time;
    private static final float INTERVAL = 0.14f;// 7 cps
    private boolean isHolding;

    public AbstractCategoryScroller(Category[] categories, T[] categoryComps, EditorInventoryWindow window) {
        super();
		this.window = window;

		this.categories = categories;

        sp = createSp();
        add(sp);

        if (categories.length > minimumNumOfActiveCategoriesRequiredForFirstCategoryComp()) {
            this.categoryComps = categoryComps;

            for (int i = 0; i < this.categoryComps.length; i++) {
                this.categoryComps[i] = createCategoryComp(i, this.categories[i]);
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
        } else {
            this.categoryComps = null;
        }

        updateItemsInCategories(false);

        sp.givePointerPriority();
    }

    protected int minimumNumOfActiveCategoriesRequiredForFirstCategoryComp() {
        return 1;
    }

    protected abstract ScrollPane createSp();

    protected abstract T createCategoryComp(int index, Category category);

    public void updateItemsInCategories(boolean forceItemUpdates) {
        int[] tempIndicesWithNonEmptyCats = new int[categories.length];
        int nextIndex = 0;

        if (categoryComps == null) {
            for (int i = 0; i < categories.length; i++) {
                List<?> items = categories[i].items(forceItemUpdates, false);
                if (items == null || !items.isEmpty()) {
                    tempIndicesWithNonEmptyCats[nextIndex++] = i;
                }
            }
        } else {
            for (int i = 0; i < categories.length; i++) {
                List<?> items = categories[i].items(forceItemUpdates, false);
                if (showEmptyCategories || items == null || !items.isEmpty()) {
                    tempIndicesWithNonEmptyCats[nextIndex++] = i;
                    categoryComps[i].setVisible(true);
                } else
                    categoryComps[i].setVisible(false);
            }
        }
        indicesWithNonEmptyCats = new int[nextIndex];
        System.arraycopy(tempIndicesWithNonEmptyCats, 0, indicesWithNonEmptyCats, 0, nextIndex);
    }

    protected boolean processKey() {
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
    public synchronized void destroy() {
        super.destroy();
        KeyEvent.removeKeyListener(keyListener);
    }

    public void setShowEmptyCategories(boolean value) {
        this.showEmptyCategories = value;
        updateItemsInCategories(false);
    }

    public int getSelectedCatIndex() {
        return selectedCatIndex;
    }

    @Override
    protected abstract void layout();

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

    public final void selectVisibleCategory(int index) {

        if (indicesWithNonEmptyCats.length == 0) return;

        selectedVisIndex = index;
        selectedCatIndex = indicesWithNonEmptyCats[index];

        doSelectCategory(selectedVisIndex, selectedCatIndex);
    }

    protected abstract void doSelectCategory(int selectedVisIndex, int selectedCatIndex);

    public Category[] getCategories() {
        return categories;
    }

    public abstract void updateItems();

    public float getCurrentViewY() {
        return sp.content().camera.scroll.y;
    }

    public abstract static class Category {

        private List<?> items;

        public Category() {
        }

        protected List<?> items(boolean forceItemUpdates, boolean required) {
            return items == null || forceItemUpdates ? items = createItems(required) : items;
        }

        public abstract List<?> createItems(boolean required);

        protected Component createListItem(Object object, EditorInventoryWindow window) {
            if (object instanceof EditorItem) {
                EditorItem<?> e = (EditorItem<?>) object;
                if (!Dungeon.quickslot.contains(e) && e instanceof TileItem) ((TileItem) e).randomizeTexture();
                return e.createListItem(window);
            }
            else if (object instanceof ScrollingListPane.ListItem) {
                return (ScrollingListPane.ListItem) object;
            }
            else {
                Item item = (Item) object;
                return new DefaultListItem(item, window, item.name(), new ItemSprite(item));
            }
        }

        public Image getImage() {
            return new ItemSprite(ItemSpriteSheet.SOMETHING);
        }

        public String getName() {
            return Messages.NO_TEXT_FOUND;
        }

    }

}