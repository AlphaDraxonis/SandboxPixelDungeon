package com.shatteredpixel.shatteredpixeldungeon.editor.ui;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditCompWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.EditorInventoryWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.EditorItemBag;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Items;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.ItemItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.InventorySlot;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.noosa.Game;
import com.watabou.noosa.ui.Component;

import java.util.Collection;
import java.util.HashSet;

public class ItemSelector extends Component {

    public enum NullTypeSelector {
        NONE, NOTHING, RANDOM
    }

    public static final int MIN_GAP = 6;//Gap between text and title
    public static final float GAP = 0.5f;

    private Class<? extends Item> itemClasses;
    private Item selectedItem;
    protected final RenderedTextBlock renderedTextBlock;
    protected InventorySlot itemSlot;
    protected IconButton changeBtn;
    private final NullTypeSelector nullTypeSelector;

    protected final AnyItemSelectorWnd selector;

    private int showWhenNull = -1;

    public ItemSelector(String text, Class<? extends Item> itemClasses, Item startItem, NullTypeSelector nullTypeSelector) {
        this.itemClasses = itemClasses;
        this.nullTypeSelector = nullTypeSelector;

        selector = new AnyItemSelectorWnd(itemClasses) {
            @Override
            public void onSelect(Item item) {
                if (item == null) return;//if window is canceled
                if (item == EditorItem.NULL_ITEM) setSelectedItem(null);
                else
                    setSelectedItem(item instanceof ItemItem ? ((ItemItem) item).item().getCopy() : item.getCopy());
            }
        };

        renderedTextBlock = PixelScene.renderTextBlock(text, 10);
        add(renderedTextBlock);

        itemSlot = new InventorySlot(startItem) {
            @Override
            protected void onClick() {
                super.onClick();
                Window w = new EditCompWindow(selectedItem) {
                    @Override
                    protected void onUpdate() {
                        super.onUpdate();
                        updateItem();
                    }
                };
                if (Game.scene() instanceof EditorScene) EditorScene.show(w);
                else Game.scene().addToFront(w);
            }

            @Override
            protected boolean onLongClick() {
                change();
                return true;
            }

            @Override
            public void item(Item item) {
                super.item(item);
                bg.visible = true;//gold and bags should have bg
            }

            @Override
            protected void viewSprite(Item item) {
                if (!EditorItem.class.isAssignableFrom(item.getClass())) {
                    super.viewSprite(item);
                    return;
                }
                if (sprite != null) {
                    remove(sprite);
                    sprite.destroy();
                }
                sprite = ((EditorItem) item).getSprite();
                if (sprite != null) addToBack(sprite);
                sendToBack(bg);
            }
        };
        add(itemSlot);

        changeBtn = new IconButton(Icons.get(Icons.CHANGES)) {
            @Override
            protected void onClick() {
                change();
            }
        };
        add(changeBtn);

        setSelectedItem(startItem);
    }

    @Override
    protected void layout() {
        renderedTextBlock.maxWidth((int) width);
        renderedTextBlock.setPos(x, y + (height - renderedTextBlock.height()) * 0.5f);
        itemSlot.setRect(Math.max(width - height * 2 - GAP, renderedTextBlock.right() + MIN_GAP), y, height, height);
        changeBtn.setRect(Math.max(width - height, renderedTextBlock.right() + MIN_GAP + GAP + height), y, height, height);
    }

    public void setSelectedItem(Item selectedItem) {
        this.selectedItem = selectedItem;
        if (showWhenNull != -1 && selectedItem == null) {
            selectedItem = new Item();
            selectedItem.image = showWhenNull;
            itemSlot.item(selectedItem);
            itemSlot.active = false;
        } else {
            if (selectedItem != null)
                selectedItem.image = Dungeon.customDungeon.getItemSpriteOnSheet(selectedItem);
            itemSlot.item(selectedItem);
        }
    }

    public int getShowWhenNull() {
        return showWhenNull;
    }

    public void setShowWhenNull(int showWhenNull) {
        this.showWhenNull = showWhenNull;
        if (getSelectedItem() == null) setSelectedItem(getSelectedItem());
    }

    public Item getSelectedItem() {
        return selectedItem;
    }

    public void updateItem() {
        itemSlot.item(selectedItem);
    }

    public void change() {
        showSelectWindow(selector, nullTypeSelector, itemClasses, Items.bag, new HashSet<>(0));
    }

    private static void addItem(ScrollingListPane sp, Item i, EditorInventoryWindow w, Class<?> itemClasses, Collection<Class<?>> excludeItems) {
        if (i instanceof EditorItem && itemClasses.isAssignableFrom(((EditorItem) i).getObject().getClass()) && !excludeItems.contains(((EditorItem) i).getObject().getClass())) {
            sp.addItem(((EditorItem) i).createListItem(w));
        }
    }

    public static EditorInventoryWindow showSelectWindow(WndBag.ItemSelectorInterface selector, NullTypeSelector nullTypeSelector, Class<?> itemClasses, EditorItemBag bag, Collection<Class<?>> excludeItems) {
        final int WIDTH = Math.min(160, (int) (PixelScene.uiCamera.width * 0.9));
        final int HEIGHT = (int) (PixelScene.uiCamera.height * 0.8f);

        Win w = new Win(selector);
        w.resize(WIDTH, HEIGHT);
        ScrollingListPane sp = new ScrollingListPane();
        w.add(sp);
        sp.setSize(WIDTH, HEIGHT);

        if (nullTypeSelector == NullTypeSelector.NOTHING)
            sp.addItem(EditorItem.NULL_ITEM.createListItem(w));
        else if (nullTypeSelector == NullTypeSelector.RANDOM)
            sp.addItem(EditorItem.RANDOM_ITEM.createListItem(w));
        for (Item bagitem : bag.items) {
            if (bagitem instanceof Bag) {
                for (Item i : (Bag) bagitem) {
                    addItem(sp, i, w, itemClasses, excludeItems);
                }
            } else addItem(sp, bagitem, w, itemClasses, excludeItems);
        }
        Component[] comps = sp.getItems();
        if (comps.length == 0) {
            w.destroy();
            return null;
        }
        if (comps[comps.length - 1].bottom() < HEIGHT) {
            w.resize(WIDTH, (int) comps[comps.length - 1].bottom());
            sp.setSize(WIDTH, (int) comps[comps.length - 1].bottom());
        }

        if (Game.scene() instanceof EditorScene) EditorScene.show(w);
        else Game.scene().addToFront(w);
        return w;
    }


    public static class Win extends Window implements EditorInventoryWindow {

        private final WndBag.ItemSelectorInterface selector;

        public Win(WndBag.ItemSelectorInterface selector) {
            this.selector = selector;
        }

        @Override
        public WndBag.ItemSelectorInterface selector() {
            return selector;
        }
    }

    public static abstract class AnyItemSelectorWnd  extends WndBag.ItemSelector {
        protected final Class<? extends Item> itemClasses;

        public AnyItemSelectorWnd(Class<? extends Item> itemClasses) {
            this.itemClasses = itemClasses;
        }

        @Override
        public String textPrompt() {
            return null;
        }

        @Override
        public boolean itemSelectable(Item item) {
            return itemClasses.isAssignableFrom(item.getClass());
        }

        @Override
        public Class<? extends Bag> preferredBag() {
            return Items.bag.getClass();
        }

        @Override
        public boolean addOtherTabs() {
            return false;
        }
    }
}