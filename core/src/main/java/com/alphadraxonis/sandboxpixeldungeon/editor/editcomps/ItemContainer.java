package com.alphadraxonis.sandboxpixeldungeon.editor.editcomps;

import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.categories.Items;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.items.EditorItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.items.ItemItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.WndMenuEditor;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.items.bags.Bag;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSprite;
import com.alphadraxonis.sandboxpixeldungeon.ui.IconButton;
import com.alphadraxonis.sandboxpixeldungeon.ui.Icons;
import com.alphadraxonis.sandboxpixeldungeon.ui.InventorySlot;
import com.alphadraxonis.sandboxpixeldungeon.ui.ScrollPane;
import com.alphadraxonis.sandboxpixeldungeon.ui.Window;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndBag;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.ui.Component;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ItemContainer<T extends Item> extends Component implements WndBag.ItemSelectorInterface { // needs access to protected methods

    private final DefaultEditComp<?> editComp;
    protected final boolean reverseUiOrder;

    public final Class<T> typeParameterClass;
    protected List<T> itemList;
    protected LinkedList<Slot> slots;
    protected final IconButton addBtn;

    {
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            if (typeArguments.length > 0 && typeArguments[0] instanceof Class) {
                typeParameterClass = (Class<T>) typeArguments[0];
            } else typeParameterClass = null;
        } else typeParameterClass = null;
    }

    public ItemContainer(List<T> itemList) {
        this(itemList, null);
    }

    public ItemContainer(List<T> itemList, DefaultEditComp<?> editComp) {
        this(itemList, editComp, false);
    }

    public ItemContainer(List<T> itemList, DefaultEditComp<?> editComp, boolean reverseUiOrder) {
        this.itemList = itemList;
        this.editComp = editComp;
        this.reverseUiOrder = reverseUiOrder;
        slots = new LinkedList<>();

        addBtn = new IconButton(Icons.get(Icons.PLUS)) {
            @Override
            protected void onClick() {
                showSelectWindow();
            }
        };
        add(addBtn);

        for (Item i : itemList) {
            addItemToUI(i, true);
        }
    }

    public final void addNewItem(T item) {
        int sizePrev = itemList.size();
        doAddItem((T) item);
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

    protected void addItemToUI(Item item, boolean last) {
        if (reverseUiOrder) last = !last;
        item.image = Dungeon.customDungeon.getItemSpriteOnSheet(item);
        Slot slot = new Slot(item);
        if (last) slots.add(slot);
        else slots.add(0, slot);
        add(slot);
        if (editComp != null) {
            editComp.layout();
            editComp.updateObj();
        }
        onSlotNumChange();
        updatePointerPriorityForSp();
    }

    @Override
    public void onSelect(Item item) {
        if (item == null || typeParameterClass != null &&
                (!typeParameterClass.isAssignableFrom(item.getClass())
                        && (!(item instanceof ItemItem) ||!typeParameterClass.isAssignableFrom(((ItemItem) item).item().getClass())  )))
            return;
        if (item instanceof ItemItem) item = ((ItemItem) item).item();
        item = item.getCopy();
        addNewItem((T) item);
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
    public String textPrompt() {
        return null;
    }

    //IMPORTANT METHODS
    //IMPORTANT METHODS
    @Override
    public boolean itemSelectable(Item item) {
        return true;
    }

    protected void doAddItem(T item) {
        itemList.add(item);
    }

    protected void onUpdateItem() {//nur dafür gedacht, wenn sich bestehende Items ändern
    }

    protected void showSelectWindow() {
        EditorScene.selectItem(this);
    }


    @Override
    public Class<? extends Bag> preferredBag() {
        return Items.bag.getClass();
    }
    //IMPORTANT METHODS
    //IMPORTANT METHODS

    public int getNumSlots() {
        return slots.size();
    }

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
        if (addBtn.visible) {
            addBtn.setRect(posX, posY, WndMenuEditor.BTN_HEIGHT, WndMenuEditor.BTN_HEIGHT);
            posY = addBtn.bottom() + WndTitledMessage.GAP;
        } else {
            Slot lastSlot = slots.getLast();
            if (lastSlot != null) posY = lastSlot.bottom() + WndTitledMessage.GAP;
        }

        height = posY - y;
    }

    protected boolean removeSlot(Slot slot) {
        slots.remove(slot);
        itemList.remove(slot.item());
        remove(slot);
        slot.destroy();
        if (editComp != null) {
            editComp.layout();
            editComp.updateObj();
        }
        onSlotNumChange();
        return true;
    }

    protected void onSlotNumChange() {
    }

    private void updatePointerPriorityForSp() {
        Group sp = parent;
        while (!(sp instanceof ScrollPane) && sp != null) {
            sp = sp.parent;
        }
        if (sp != null) ((ScrollPane) sp).givePointerPriority();
    }

    protected class Slot extends InventorySlot {

        public Slot(Item item) {
            super(item);
        }

        @Override
        protected void onClick() {
            Window w = new EditCompWindow(item, editComp == null ? null : editComp.advancedListPaneItem) {
                @Override
                protected void onUpdate() {
                    super.onUpdate();
                    item(item);
                    onUpdateItem();
                }
            };
            if (Game.scene() instanceof EditorScene) EditorScene.show(w);
            else Game.scene().addToFront(w);
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

        @Override
        protected void viewSprite(Item item) {
            if (typeParameterClass == Item.class) {
                super.viewSprite(item);
                return;
            }
            if (sprite != null) {
                remove(sprite);
                sprite.destroy();
            }
            if (item instanceof EditorItem) sprite = ((EditorItem) item).getSprite();
            else sprite = new ItemSprite(item);
            if (sprite != null) addToBack(sprite);
            sendToBack(bg);
        }


    }

}