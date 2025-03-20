package com.shatteredpixel.shatteredpixeldungeon.editor.ui;

import com.shatteredpixel.shatteredpixeldungeon.GameObject;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditCompWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditItemComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.ItemContainer;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.stateditor.LootTableComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.stateditor.WndEditStats;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.ItemItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.noosa.Group;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Consumer;
import com.watabou.utils.Function;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.shatteredpixel.shatteredpixeldungeon.editor.ui.MultiWindowTabComp.BUTTON_HEIGHT;
import static com.shatteredpixel.shatteredpixeldungeon.editor.ui.MultiWindowTabComp.GAP;

public abstract class ItemsWithChanceDistrComp extends Component {

    protected static final float ROW_HEIGHT = 20;

    protected RandomItemData randomItemData;
    private List<Slot> slots = new ArrayList<>();
    private Map<ItemWithCount, Slot> itemToSlot = new HashMap<>();//null means nothing

    protected RenderedTextBlock title;
    protected boolean hasNullInLoot;

    private int sum;
    private boolean isInInit;

    private final int minNumItemsPerSlot, maxNumItemsPerSlot;

    public ItemsWithChanceDistrComp(RandomItemData randomItemData, int minNumItemsPerSlot, int maxNumItemsPerSlot) {
        this.randomItemData = randomItemData;
        this.minNumItemsPerSlot = minNumItemsPerSlot;
        this.maxNumItemsPerSlot = maxNumItemsPerSlot;

        isInInit = true;
        initComps();
        isInInit = false;
    }

    protected final Component createTitle(String title) {
        this.title = PixelScene.renderTextBlock(Messages.titleCase(title), 10);
        this.title.hardlight(Window.TITLE_COLOR);
        return this.title;
    }

    public Component getOutsideSp() {
        String addItemLabel = Messages.get(this, "add_item");
        return new Component() {
            RedButton addItem;
            Component restore;

            @Override
            protected void createChildren() {
                super.createChildren();
                addItem = new RedButton(addItemLabel) {
                    @Override
                    protected void onClick() {
                        showAddItemWnd(null);
                    }
                };
                add(addItem);
                    restore = createRestoreButton();
                    if(restore!=null)add(restore);
            }

            @Override
            protected void layout() {
                if (restore == null) {
                    addItem.setRect(x, y, width, BUTTON_HEIGHT);
                } else {
                    addItem.setRect(x, y, (width - GAP) * 3 / 5, BUTTON_HEIGHT);
                    restore.setRect(addItem.right() + GAP, y, width - GAP - addItem.width(), BUTTON_HEIGHT);
                }
                height = BUTTON_HEIGHT;
            }
        };
    }

    protected void showAddItemWnd(Consumer<Item> onSelect) {
        EditorScene.selectItem(createSelector(onSelect));
    }

    protected abstract WndBag.ItemSelector createSelector(Consumer<Item> onSelect);

    protected final WndBag.ItemSelector createSelector(Class<? extends Item> itemClasses, boolean allowRandomItem, Class<? extends Bag> prefBag, Consumer<Item> onSelect) {
        return new ItemSelector.AnyItemSelectorWnd(itemClasses, allowRandomItem) {
            {
                preferredBag = prefBag;
            }
            @Override
            public void onSelect(Item item) {
                if (!acceptItem(item)) return;
                if (item != EditorItem.NULL_ITEM)
                    item = item instanceof ItemItem ? ((ItemItem) item).item().getCopy() : item.getCopy();

                if (onSelect == null) {
                    ItemWithCount i = new ItemWithCount();
                    i.items.add(item);
                    i.setCount(1);
                    sum++;
                    randomItemData.distrSlots.add(i);
                    addSlot(i);

                    updateList(true);
                } else {
                    onSelect.accept(item);
                }
            }

            @Override
            public boolean itemSelectable(Item item) {
                return super.itemSelectable(item) && acceptItem(item);
            }

            @Override
            public boolean acceptsNull() {
                return !hasNullInLoot && onSelect == null;
            }
        };
    }

    protected boolean acceptItem(Item item) {
        return item != null;
    }

    protected Component createRestoreButton() {
        return null;
    }

    private void initComps() {
        for (ItemWithCount item : randomItemData.distrSlots) {
            sum += item.getCount();
            addSlot(item);
        }
        if (randomItemData.noLootCount > 0) {
            sum += randomItemData.noLootCount;
            ItemWithCount item = new ItemWithCount();
            item.items.add(EditorItem.NULL_ITEM);
            item.count = randomItemData.noLootCount;
            randomItemData.distrSlots.add(item);
            addSlot(item);
        }
        updateList(false);
    }

    @Override
    protected void layout() {
        updateList(false);
    }

    public void updateList(boolean updateParent) {
        float posY = y;

        isInInit = true;

        for (Slot slot : slots) {
            int count = (int) slot.countSpinner.getValue();
            slot.setCount(count);
            
            slot.setRect(x, posY, width, ROW_HEIGHT);
            PixelScene.align(slot);
            posY = slot.bottom();
        }
        height = posY - y;
        isInInit = false;
        if (updateParent) {
            updateParent();
        }
    }

    protected abstract void updateParent();

    protected WndEditStats findWndEditStats() {
        Group w = parent;
        while (w != null && !(w instanceof WndEditStats)) {
            w = w.parent;
        }
        return (WndEditStats) w;
    }

    private void addSlot(ItemWithCount item) {
        if (item.items.size() == 1 && item.items.contains(EditorItem.NULL_ITEM)) hasNullInLoot = true;
        Slot slot = new Slot(item, minNumItemsPerSlot, maxNumItemsPerSlot);
        slot.setSize(width, ROW_HEIGHT);
        add(slot);
    }

    private String calculatePercentage(float count) {
        float calc = count * 100 / sum;
        int asInt = Math.round(calc);
        char string = (asInt == calc) ? '=' : '≈';
        return " " + string + asInt + "%";
    }

    private void updateSpinners(ItemWithCount exclude) {
        for (ItemWithCount item : randomItemData.distrSlots) {
            Slot slot = itemToSlot.get(item);
            if (slot != null && item != exclude) {
                int count = (int) slot.countSpinner.getValue();
                item.setCount(count);
                slot.setCount(count);
            }
        }
    }

    private class Slot extends Button {

        private final Spinner countSpinner;
        private final IconButton removeBtn;
        private final ItemWithCount item;

        private final ItemContainer<Item> items;
        private final RenderedTextBlock text;

        public Slot(ItemWithCount item, int minSlots, int maxSlots) {

            this.item = item;
            if (item.items.contains(EditorItem.NULL_ITEM)) {
                slots.add(0, this);
                itemToSlot.put(null, this);
            } else {
                slots.add(this);
                itemToSlot.put(item, this);
            }

            if (item.items.contains(EditorItem.NULL_ITEM)) {
                items = null;
                text = PixelScene.renderTextBlock(Messages.titleCase(EditorItem.NULL_ITEM.title()), 7);
                add(text);
            } else {
                text = null;

                items = new ItemContainer<Item>(item.items, null, false, minSlots, maxSlots) {
                    @Override
                    protected void onSlotNumChange() {
                        ItemsWithChanceDistrComp.Slot.this.layout();
                    }

                    @Override
                    protected void showWndEditItemComp(ItemContainer<Item>.Slot slot, Item item) {
                        if (ItemsWithChanceDistrComp.this instanceof LootTableComp)
                            EditItemComp.showSpreadIfLoot = true;
                        super.showWndEditItemComp(slot, item);
                    }

                    @Override
                    protected void showSelectWindow() {
                        showAddItemWnd(this::onSelect);
                    }
                };
                add(items);

                remove(hotArea);
            }

            countSpinner = new Spinner(new SModel(item.count) {
                @Override
                public void changeValue(Object oldValue, Object newValue) {
                    super.changeValue(oldValue, newValue);
                    int diff = (int) newValue - (int) oldValue;
                    if (isInInit) return;
                    sum += diff;
                    item.count += diff;
                }
            }, "", 6);
            countSpinner.setButtonWidth(10);
            countSpinner.addChangeListener(() -> updateSpinners(item));
            add(countSpinner);

            removeBtn = new IconButton(Icons.get(Icons.CLOSE)) {
                @Override
                protected void onClick() {
                    removeItem();
                }
            };
            add(removeBtn);
        }

        public void removeItem() {
            int count = (int) countSpinner.getValue();
            sum -= count;
            item.count = 0;
            randomItemData.distrSlots.remove(item);
            itemToSlot.remove(item);
            slots.remove(this);
            if (item.items.contains(EditorItem.NULL_ITEM)) {
                hasNullInLoot = false;
                randomItemData.noLootCount = 0;
            }
            destroy();
            updateList(true);
        }

        public void setCount(int count) {
            countSpinner.setValue(count);
            item.setCount(count);
        }

        @Override
        public void onClick() {
            if (item.items.contains(EditorItem.NULL_ITEM)) {
                EditorScene.show(new EditCompWindow(EditorItem.NULL_ITEM));
            }
        }

        @Override
        protected void layout() {
            super.layout();

            if (countSpinner == null) return;

            float spinnW = countSpinner.width();
            float gap = -1.1f;

            float labelWidth = width - spinnW + 4.1f - ROW_HEIGHT;
            if (items != null) {
                items.setRect(x, y, labelWidth, -1);
                height = Math.max(items.height(), ROW_HEIGHT);
            } else {
                text.maxWidth((int) labelWidth);
                text.setPos(x + 1, y + (height() - text.height()) / 2f);
                PixelScene.align(text);
            }

            float h = ROW_HEIGHT - 3;
            countSpinner.setRect(width - spinnW + x - h - gap, y + 2f + (height - ROW_HEIGHT) * 0.5f, spinnW, h);
            removeBtn.setRect(countSpinner.right() + gap, y + 2f + (height - ROW_HEIGHT) * 0.5f, h, h);
            PixelScene.align(countSpinner);
            PixelScene.align(removeBtn);

            hotArea.width = countSpinner.left() - x - 2;
        }
    }

    private class SModel extends SpinnerIntegerModel {

        public SModel(int count) {
            super(1, 10000, count);
        }

        @Override
        public int getClicksPerSecondWhileHolding() {
            return super.getClicksPerSecondWhileHolding() / 100;
        }

        @Override
        public float getInputFieldWidth(float height) {
            return height * 2f;
        }

        @Override
        protected String displayString(Object value) {
            return super.displayString(value) + " " + calculatePercentage((int) value);
        }
    }

    @Override
    public synchronized void destroy() {
        maybeConvertNullItemToNothingElement();
        super.destroy();
    }
    
    private void maybeConvertNullItemToNothingElement() {
        ItemWithCount remove = null;
        for (ItemWithCount item : randomItemData.distrSlots) {
            if (item.items.size() == 1 && item.items.contains(EditorItem.NULL_ITEM)) {
                randomItemData.noLootCount = item.count;
                remove = item;
                break;
            }
        }
        if (remove != null) {
            Slot slot = itemToSlot.get(remove);
            slots.remove(slot);
            slots.add(0, slot);
            itemToSlot.put(null, slot);
            randomItemData.distrSlots.remove(remove);
        }
    }

    public static class ItemWithCount extends GameObject {

        public List<Item> items = new ArrayList<>(3);
        private int count;

        public void setCount(int count) {
            this.count = count;
        }

        public int getCount() {
            return count;
        }

        @Override
        public boolean doOnAllGameObjects(Function<GameObject, ModifyResult> whatToDo) {
            return super.doOnAllGameObjects(whatToDo)
                    | doOnAllGameObjectsList(items, whatToDo);
        }

        private static final String ITEMS = "items";
        private static final String COUNT = "count";

        @Override
        public void restoreFromBundle(Bundle bundle) {
            items.clear();
            count = bundle.getInt(COUNT);
            if (bundle.contains("item")) items.add((Item) bundle.get("item"));
            else {
                for (Bundlable b : bundle.getCollection(ITEMS))
                    items.add((Item) b);
            }
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            bundle.put(ITEMS, items);
            bundle.put(COUNT, count);
        }
    }

    public static class RandomItemData implements Bundlable {

        public List<ItemWithCount> distrSlots = new ArrayList<>();
        protected int noLootCount;

        private static final String LOOT = "loot";
        private static final String NO_LOOT = "no_loot";

        @Override
        public void restoreFromBundle(Bundle bundle) {
            for (Bundlable b : bundle.getCollection(LOOT))
                distrSlots.add((ItemWithCount) b);
            noLootCount = bundle.getInt(NO_LOOT);
        }

        @Override
        public void storeInBundle(Bundle bundle) {

            ItemWithCount remove = null;
            for (ItemWithCount item : distrSlots) {
                if (item.items.size() == 1 && item.items.contains(EditorItem.NULL_ITEM)) {
                    noLootCount = item.count;
                    remove = item;
                    break;
                }
            }
            if (remove != null) {
                distrSlots.remove(remove);
            }
            
//            maybeConvertNullItemToNothingElement();

            bundle.put(LOOT, distrSlots);
            bundle.put(NO_LOOT, noLootCount);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            RandomItemData a = (RandomItemData) obj;
            if (noLootCount != a.noLootCount) return false;
            if (distrSlots.size() != a.distrSlots.size()) return false;
            int index = 0;
            for (ItemWithCount item : distrSlots) {
                ItemWithCount other = a.distrSlots.get(index);
                if (!EditItemComp.isItemListEqual(item.items, other.items)) return false;
                if (item.count != other.count) return false;
                index++;
            }
            return true;
        }

        public List<Item> generateLoot() {
            if (distrSlots.isEmpty()) return null;//mob should use it's default create loot methods
            int sum = 0;
            for (ItemWithCount item : distrSlots)
                sum += item.getCount();
            int get = Random.Int(sum);
            sum = 0;
            for (ItemWithCount item : distrSlots) {
                sum += item.getCount();
                if (sum > get) {
                    GameObject.doOnAllGameObjectsList(item.items, GameObject::initRandoms);
                    return item.items;
                }
            }
            return null;
        }

        public int calculateSum() {
            int sum = noLootCount;
            for (ItemWithCount item : distrSlots)
                sum += item.getCount();
            return sum;
        }

        public float lootChance() {
            float sum = calculateSum();
            return 1f - noLootCount / sum;
        }

        public void setLootChance(int noLootCount) {
            this.noLootCount = noLootCount;
        }

        public void addItem(Item item, int count) {
            ItemWithCount itemWithCount = new ItemWithCount();
            itemWithCount.items.add(item);
            itemWithCount.count = count;
            distrSlots.add(itemWithCount);
        }
    }
}
