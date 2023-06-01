package com.shatteredpixel.shatteredpixeldungeon.editor.ui;

import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.Items;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.editcomps.EditCompWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.inv.EditorInventoryWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.inv.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.inv.ItemItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sword;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.InventorySlot;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemSlot;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.noosa.ui.Component;

public class ItemSelector extends Component {

    protected static final int MIN_GAP = 6;//Gap between text and title
    protected static final float GAP = 0.5f;

    private Class<? extends Item> itemClasses;
    private Item selectedItem;
    protected final RenderedTextBlock renderedTextBlock;
    protected ItemSlot itemSlot;
    protected IconButton changeBtn;
    private final boolean acceptsNull;

    public ItemSelector(String text, Class<? extends Item> itemClasses, Item startItem,boolean acceptsNull) {
        this.itemClasses = itemClasses;
        this.acceptsNull=acceptsNull;

        renderedTextBlock = PixelScene.renderTextBlock(text, 10);
        add(renderedTextBlock);

        itemSlot = new InventorySlot(startItem) {
            @Override
            protected void onClick() {
                super.onClick();
                EditorScene.show(new EditCompWindow(selectedItem) {
                    @Override
                    protected void onUpdate() {
                        super.onUpdate();
                        updateItem();
                    }
                });
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
        itemSlot.item(selectedItem);
    }

    public Item getSelectedItem() {
        return selectedItem;
    }

    public void updateItem() {
        itemSlot.item(selectedItem);
    }

    public void change() {

        final int WIDTH = Math.min(160, (int) (PixelScene.uiCamera.width * 0.9));
        final int HEIGHT = (int) (PixelScene.uiCamera.height * 0.8f);

        Win w = new Win();
        w.resize(WIDTH, HEIGHT);
        ScrollingListPane sp = new ScrollingListPane();
        w.add(sp);
        sp.setSize(WIDTH, HEIGHT);

       if(acceptsNull) sp.addItem(EditorItem.NULL_ITEM.createListItem(w));
        for (Item bagitem : Items.bag.items) {
            if (bagitem instanceof Bag) {
                for (Item i : (Bag) bagitem) {
                    addItem(sp, i, w);
                }
            } else addItem(sp, bagitem, w);
        }
        Component[] comps = sp.getItems();
        if (comps.length == 0) return;
        if (comps[comps.length - 1].bottom() < HEIGHT) {
            w.resize(WIDTH, (int) comps[comps.length - 1].bottom());
            sp.setSize(WIDTH, (int) comps[comps.length - 1].bottom());
        }

        EditorScene.show(w);
    }

    private void addItem(ScrollingListPane sp, Item i, EditorInventoryWindow w) {
        if (i instanceof ItemItem && itemClasses.isAssignableFrom(((ItemItem) i).item().getClass())) {
            sp.addItem(((ItemItem) i).createListItem(w));
        }
    }


    protected final ItemChange selector = new ItemChange();

    private class Win extends Window implements EditorInventoryWindow {

        @Override
        public WndBag.ItemSelector selector() {
            return selector;
        }
    }

    private class ItemChange extends WndBag.ItemSelector {

        @Override
        public String textPrompt() {
            return null;
        }

        @Override
        public boolean itemSelectable(Item item) {
            return itemClasses.isAssignableFrom(item.getClass());
        }

        @Override
        public void onSelect(Item item) {
            if (item == EditorItem.NULL_ITEM) setSelectedItem(null);
            else
                setSelectedItem(item instanceof ItemItem ? ((ItemItem) item).item().clone() : item.clone());
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