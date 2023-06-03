package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.Items;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndMenuEditor;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.inv.ItemItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.InventorySlot;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemContainer extends Component {

    private final DefaultEditComp<?> editComp;
    protected final boolean reverseUiOrder;

    protected List<Item> itemList;
    protected List<Slot> slots;
    protected final IconButton addBtn;


    public ItemContainer(List<Item> itemList) {
        this(itemList, null);
    }

    public ItemContainer(List<Item> itemList, DefaultEditComp<?> editComp) {
        this(itemList, editComp, false);
    }

    public ItemContainer(List<Item> itemList, DefaultEditComp<?> editComp, boolean reverseUiOrder) {
        this.itemList = itemList;
        this.editComp = editComp;
        this.reverseUiOrder = reverseUiOrder;
        slots = new ArrayList<>();

        addBtn = new IconButton(Icons.get(Icons.PLUS)) {
            @Override
            protected void onClick() {
                EditorScene.selectItem(new WndBag.ItemSelector() {
                    @Override
                    public String textPrompt() {
                        return null;
                    }

                    @Override
                    public boolean itemSelectable(Item item) {
                        return ItemContainer.this.itemSelectable(item);
                    }

                    @Override
                    public boolean addOtherTabs() {
                        return false;
                    }

                    @Override
                    public boolean acceptsNull() {
                        return false;
                    }

                    @Override
                    public Class<? extends Bag> preferredBag() {
                        return Items.bag.getClass();
                    }

                    @Override
                    public void onSelect(Item item) {
                        if (item instanceof ItemItem) item = ((ItemItem) item).item();
                        item = item.getCopy();
                        int sizePrev = itemList.size();
                        addItem(item);
                        if (itemList.size() > sizePrev)
                            addItemToUI(item, false);//if it wasnt stacked
                        else {
                            //Rearrange slots
                            Map<Item, Slot> slotMap = new HashMap<>();
                            for (Slot slot : slots) {
                                slot.item(slot.item());
                                slotMap.put(slot.item(), slot);
                            }
                            slots.clear();
                            for (Item i : itemList) slots.add(slotMap.get(i));
                            if (reverseUiOrder) Collections.reverse(slots);
                            ItemContainer.this.layout();
                        }
                    }
                });
            }
        };
        add(addBtn);

        for (Item i : itemList) {
            addItemToUI(i, !reverseUiOrder);
        }
    }

    protected void addItemToUI(Item item, boolean last) {
        item.image = Dungeon.customDungeon.getItemSpriteOnSheet(item);
        Slot slot = new Slot(item);
        if (last) slots.add(slot);
        else slots.add(0, slot);
        add(slot);
        if (editComp != null) {
            editComp.layout();
            editComp.updateItem();
        }
    }


    //IMPORTANT METHODS
    //IMPORTANT METHODS
    protected boolean itemSelectable(Item item) {
        return true;
    }

    protected void addItem(Item item) {
        itemList.add(item);
    }

    protected void onUpdateItem() {
    }
    //IMPORTANT METHODS
    //IMPORTANT METHODS

    private static final int GAP = 2;

    @Override
    protected void layout() {
        float posY = y;
        float posX = x + GAP;
        for (Slot slot : slots) {
            slot.setRect(posX, posY, WndMenuEditor.BTN_HEIGHT, WndMenuEditor.BTN_HEIGHT);
            posX = slot.right() + GAP;
            if (posX + WndMenuEditor.BTN_HEIGHT > width + x) {
                posY = slot.bottom() + GAP;
                posX = x + GAP;
            }
        }
        addBtn.setRect(posX, posY, WndMenuEditor.BTN_HEIGHT, WndMenuEditor.BTN_HEIGHT);
        posY = addBtn.bottom() + WndTitledMessage.GAP;

        height = posY - y;
    }

    protected boolean removeSlot(Slot slot) {
        slots.remove(slot);
        itemList.remove(slot.item());
        remove(slot);
        slot.destroy();
        if (editComp != null) {
            editComp.layout();
            editComp.updateItem();
        }
        return true;
    }

    protected class Slot extends InventorySlot {

        public Slot(Item item) {
            super(item);
        }

        @Override
        protected void onClick() {
            EditorScene.show(new EditCompWindow(item, editComp == null ? null : editComp.advancedListPaneItem) {
                @Override
                protected void onUpdate() {
                    super.onUpdate();
                    item(item);
                    onUpdateItem();
                }
            });

        }

        @Override
        protected boolean onLongClick() {
            return removeSlot(this);
        }

        @Override
        public void item(Item item) {
            super.item(item);
            bg.visible = true;//gold and bags should have bg
        }
    }

}