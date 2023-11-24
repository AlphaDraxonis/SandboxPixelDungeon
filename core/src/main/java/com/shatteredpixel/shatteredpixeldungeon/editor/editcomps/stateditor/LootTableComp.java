package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.stateditor;

import static com.shatteredpixel.shatteredpixeldungeon.editor.ui.MultiWindowTabComp.BUTTON_HEIGHT;
import static com.shatteredpixel.shatteredpixeldungeon.editor.ui.MultiWindowTabComp.GAP;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditCompWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditItemComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.ItemContainer;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Items;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.ItemItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.RandomItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemSelector;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class LootTableComp extends Component {

    private static final float ROW_HEIGHT = 20;

    public static final class ItemWithCount implements Bundlable {

        public List<Item> items = new ArrayList<>(3);
        private int count;

        private LootItem lootComp;

        public void setCount(int count) {
            this.count = count;
            if (lootComp != null) lootComp.setCount(count);
        }

        public int getCount() {
            return count;
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

    private RenderedTextBlock title;
    private CustomLootInfo customLootInfo;
    private boolean hasNullInLoot;

    private int sum;
    private boolean isInInit = true;//NOT redundant!

    protected final Mob mob;
    protected final RandomItem<?> randomItem;

    public LootTableComp(Mob mob, RandomItem<?> randomItem) {
        this.mob = mob;
        this.randomItem = randomItem;

        initComps();
        isInInit = false;

    }

    public Component createTitle() {
        title = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(LootTableComp.class, "title_" + (randomItem == null ? "loot" : "random"))), 10);
        title.hardlight(Window.TITLE_COLOR);
        return title;
    }

    public Component getOutsideSp() {
        return new Component() {
            RedButton addItem, restore;

            @Override
            protected void createChildren(Object... params) {
                super.createChildren(params);
                addItem = new RedButton(Messages.get(LootTableComp.class, "add_item")) {
                    @Override
                    protected void onClick() {
                        WndBag.ItemSelector selector = new ItemSelector.AnyItemSelectorWnd(randomItem == null ? Item.class : randomItem.getType(), randomItem == null) {
                            @Override
                            public void onSelect(Item item) {
                                if (item == null) return;
                                if (item != EditorItem.NULL_ITEM)
                                    item = item instanceof ItemItem ? ((ItemItem) item).item().getCopy() : item.getCopy();
                                ItemWithCount i = new ItemWithCount();
                                i.items.add(item);
                                i.setCount(1);
                                sum++;
                                customLootInfo.lootList.add(i);
                                addLootItem(i);

                                updateList(true);
                            }

                            @Override
                            public boolean acceptsNull() {
                                return !hasNullInLoot;
                            }
                        };
                        if (randomItem != null && randomItem.getType() != Item.class)
                            ItemSelector.showSelectWindow(selector, ItemSelector.NullTypeSelector.NOTHING, randomItem.getType(),
                                    Items.bag, new HashSet<>(0), false);
                        else EditorScene.selectItem(selector);
                    }
                };
                add(addItem);
                if (mob != null) {
                    restore = new RedButton(Messages.get(LootTableComp.class, "reset_loot")) {
                        @Override
                        protected void onClick() {
                            mob.loot = Reflection.newInstance(mob.getClass()).loot;
                            WndEditStats wndEditStats = findWndEditStats();
                            wndEditStats.closeCurrentSubMenu();
                        }
                    };
                    add(restore);
                }
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

    private void initComps() {
        if (randomItem != null) customLootInfo = randomItem.getInternalRandomItem_ACCESS_ONLY_FOR_EDITING_UI();
        else if (mob.loot instanceof CustomLootInfo) customLootInfo = (CustomLootInfo) mob.loot;
        else {
            customLootInfo = mob.convertToCustomLootInfo();
            mob.loot = customLootInfo;
        }
        for (ItemWithCount item : customLootInfo.lootList) {
            sum += item.getCount();
            addLootItem(item);
        }
        if (customLootInfo.noLootCount > 0) {
            sum += customLootInfo.noLootCount;
            ItemWithCount item = new ItemWithCount();
            item.items.add(EditorItem.NULL_ITEM);
            item.count = customLootInfo.noLootCount;
            customLootInfo.lootList.add(item);
            addLootItem(item);
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

        for (ItemWithCount item : customLootInfo.lootList) {
            if (item.lootComp != null) {
                item.lootComp.setRect(x, posY, width, ROW_HEIGHT);
                item.setCount((int) item.lootComp.countSpinner.getValue());
                PixelScene.align(item.lootComp);
                posY = item.lootComp.bottom();
            }
        }
        height = posY;
        isInInit = false;
        if (updateParent) {
            WndEditStats wndEditStats = findWndEditStats();
            if (wndEditStats != null) wndEditStats.layout();
        }
    }

    private WndEditStats findWndEditStats() {
        Group w = parent;
        while (w != null && !(w instanceof WndEditStats)) {
            w = w.parent;
        }
        return (WndEditStats) w;
    }

    private void addLootItem(ItemWithCount item) {
        if (item.items.contains(EditorItem.NULL_ITEM)) hasNullInLoot = true;
        item.lootComp = new LootItem(item);
        add(item.lootComp);
        item.lootComp.setSize(width, ROW_HEIGHT);
    }

    private String calculatePercentage(float count) {
        float calc = count * 100 / sum;
        int asInt = Math.round(calc);
        char string = (asInt == calc) ? '=' : '≈';
        return " " + string + asInt + "%";
    }

    private void updateSpinners(ItemWithCount exclude) {
        for (ItemWithCount item : customLootInfo.lootList) {
            if (item.lootComp != null && item != exclude) item.setCount((int) item.lootComp.countSpinner.getValue());
        }
    }


    private class LootItem extends Button {

        private final Spinner countSpinner;
        private final IconButton removeBtn;
        private final ItemWithCount item;

        private final ItemContainer<Item> items;
        //        private final Image img;
        private final RenderedTextBlock text;

        public LootItem(ItemWithCount item) {

            this.item = item;

            if (item.items.contains(EditorItem.NULL_ITEM)) {
                items = null;
                text = PixelScene.renderTextBlock(EditorItem.NULL_ITEM.title(), 7);
//                img = Dungeon.customDungeon.getItemImage(EditorItem.NULL_ITEM);
                add(text);
//                add(img);
            } else {
//                img = null;
                text = null;

                items = new ItemContainer<Item>(item.items, null, true, 1) {
                    @Override
                    protected void onSlotNumChange() {
                        LootItem.this.layout();
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
            customLootInfo.lootList.remove(item);
            if (item.items.contains(EditorItem.NULL_ITEM)) {
                hasNullInLoot = false;
                customLootInfo.noLootCount = 0;
            }
            destroy();
            updateList(true);
        }

        public void setCount(int count) {
            countSpinner.setValue(count);
        }

        @Override
        public void onClick() {
            if (item.items.contains(EditorItem.NULL_ITEM)) {
                Window w = new EditCompWindow(EditorItem.NULL_ITEM);
                if (Game.scene() instanceof EditorScene) EditorScene.show(w);
                else Game.scene().addToFront(w);
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
//                img.y = y + 1 + (height() - 1 - img.height()) / 2f;
//                img.x = x + (16 - img.width()) / 2f;
//                PixelScene.align(img);

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
            super(1, 10000, count, 1, false, null);
        }

        @Override
        public int getClicksPerSecondWhileHolding() {
            return 40;
        }

        @Override
        public float getInputFieldWith(float height) {
            return height * 2f;
        }

        @Override
        public String getDisplayString() {
            return super.getDisplayString() + " " + calculatePercentage((int) getValue());
        }
    }

    @Override
    public synchronized void destroy() {
        ItemWithCount remove = null;
        for (ItemWithCount item : customLootInfo.lootList) {
            if (item.items.contains(EditorItem.NULL_ITEM)) {
                customLootInfo.noLootCount = item.count;
                remove = item;
                break;
            }
        }
        if (remove != null) customLootInfo.lootList.remove(remove);
        super.destroy();
    }

    public static class CustomLootInfo implements Bundlable {

        public List<ItemWithCount> lootList = new ArrayList<>();
        protected int noLootCount;

        private static final String LOOT = "loot";
        private static final String NO_LOOT = "no_loot";

        @Override
        public void restoreFromBundle(Bundle bundle) {
            for (Bundlable b : bundle.getCollection(LOOT))
                lootList.add((ItemWithCount) b);
            noLootCount = bundle.getInt(NO_LOOT);
        }

        @Override
        public void storeInBundle(Bundle bundle) {

            ItemWithCount remove = null;
            for (ItemWithCount item : lootList) {
                if (item.items.contains(EditorItem.NULL_ITEM)) {
                    noLootCount = item.count;
                    remove = item;
                    break;
                }
            }
            if (remove != null) lootList.remove(remove);

            bundle.put(LOOT, lootList);
            bundle.put(NO_LOOT, noLootCount);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            CustomLootInfo a = (CustomLootInfo) obj;
            if (noLootCount != a.noLootCount) return false;
            if (lootList.size() != a.lootList.size()) return false;
            int index = 0;
            for (ItemWithCount item : lootList) {
                ItemWithCount other = a.lootList.get(index);
                if (!EditItemComp.isItemListEqual(item.items, other.items)) return false;
                if (item.count != other.count) return false;
                index++;
            }
            return true;
        }

        public List<Item> generateLoot() {
            if (lootList.isEmpty()) return null;//mob should use it's default create loot methods
            int sum = 0;
            for (ItemWithCount item : lootList)
                sum += item.getCount();
            int get = Random.Int(sum);
            sum = 0;
            for (ItemWithCount item : lootList) {
                sum += item.getCount();
                if (sum > get) return item.items;
            }
            return null;
        }

        public int calculateSum() {
            int sum = noLootCount;
            for (ItemWithCount item : lootList)
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
            lootList.add(itemWithCount);
        }
    }

}