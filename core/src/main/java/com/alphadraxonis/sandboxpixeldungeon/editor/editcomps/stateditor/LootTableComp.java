package com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.stateditor;

import static com.alphadraxonis.sandboxpixeldungeon.editor.ui.MultiWindowTabComp.BUTTON_HEIGHT;
import static com.alphadraxonis.sandboxpixeldungeon.editor.ui.MultiWindowTabComp.GAP;

import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Mob;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.EditCompWindow;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.EditItemComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.items.EditorItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.items.ItemItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.AdvancedListPaneItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.IconTitleWithSubIcon;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.ItemSelector;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.Spinner;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.ui.IconButton;
import com.alphadraxonis.sandboxpixeldungeon.ui.Icons;
import com.alphadraxonis.sandboxpixeldungeon.ui.RedButton;
import com.alphadraxonis.sandboxpixeldungeon.ui.RenderedTextBlock;
import com.alphadraxonis.sandboxpixeldungeon.ui.Window;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.List;

public class LootTableComp extends Component {

    public static final class ItemWithCount implements Bundlable {
        public Item item;
        private int count;

        private LootItem lootComp;

        public void setCount(int count) {
            this.count = count;
            if (lootComp != null) lootComp.setCount(count);
        }

        public int getCount() {
            return count;
        }

        private static final String ITEM = "item";
        private static final String COUNT = "count";

        @Override
        public void restoreFromBundle(Bundle bundle) {
            item = (Item) bundle.get(ITEM);
            count = bundle.getInt(COUNT);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            bundle.put(ITEM, item);
            bundle.put(COUNT, count);
        }
    }

    private RenderedTextBlock title;
    private CustomLootInfo customLootInfo;
    private boolean hasNullInLoot;

    private int sum;
    private boolean isInInit = true;//NOT redundant!

    protected final Mob mob;

    public LootTableComp(Mob mob) {
        this.mob = mob;

        initComps();
        isInInit = false;

    }

    public Component createTitle() {
        title = PixelScene.renderTextBlock(Messages.get(LootTableComp.class, "title"), 10);
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

                        EditorScene.selectItem(new ItemSelector.AnyItemSelectorWnd(Item.class) {
                            @Override
                            public void onSelect(Item item) {
                                if (item == null) return;
                                if (item != EditorItem.NULL_ITEM)
                                    item = item instanceof ItemItem ? ((ItemItem) item).item().getCopy() : item.getCopy();
                                ItemWithCount i = new ItemWithCount();
                                i.item = item;
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
                        });
                    }
                };
                add(addItem);
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

            @Override
            protected void layout() {
                addItem.setRect(x, y, (width - GAP) * 3 / 5, BUTTON_HEIGHT);
                restore.setRect(addItem.right() + GAP, y, width - GAP - addItem.width(), BUTTON_HEIGHT);
                height = BUTTON_HEIGHT;
            }
        };
    }

    private void initComps() {
        if (mob.loot instanceof CustomLootInfo) customLootInfo = (CustomLootInfo) mob.loot;
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
            item.item = EditorItem.NULL_ITEM;
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
                item.lootComp.setRect(x, posY, width, BUTTON_HEIGHT);
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
        if (item.item == EditorItem.NULL_ITEM) hasNullInLoot = true;
        item.lootComp = new LootItem(item);
        add(item.lootComp);
        item.lootComp.setSize(width, BUTTON_HEIGHT);
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


    private class LootItem extends AdvancedListPaneItem {

        private final Spinner countSpinner;
        private final IconButton removeBtn;
        private final ItemWithCount item;

        public LootItem(ItemWithCount item) {
            super(Dungeon.customDungeon.getItemImage(item.item),
                    IconTitleWithSubIcon.createSubIcon(item.item),
                    item.item.title());
            this.item = item;

            String oldText = label.text();
            remove(label);
            label = PixelScene.renderTextBlock(6);
            add(label);
            label.text(oldText);

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
            if (item.item == EditorItem.NULL_ITEM) {
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
            Window w = new EditCompWindow(item.item, this);
            if (Game.scene() instanceof EditorScene) EditorScene.show(w);
            else Game.scene().addToFront(w);
        }

        @Override
        protected void layout() {
            super.layout();

            if (countSpinner == null) return;

            float h = height() - 3;
            float ypsilon = y + 2f;
            float spinnW = countSpinner.width();
            float gap = -1.1f;
            countSpinner.setRect(width - spinnW + x - h - gap, ypsilon, spinnW, h);
            removeBtn.setRect(countSpinner.right() + gap, ypsilon, h, h);
            PixelScene.align(countSpinner);
            PixelScene.align(removeBtn);

            hotArea.width = countSpinner.left() - x - 2;
        }

        @Override
        protected int getLabelMaxWidth() {
            if (countSpinner == null) return super.getLabelMaxWidth();
            return (int) (super.getLabelMaxWidth() - countSpinner.width() + 4.1f - height());
        }

        @Override
        public void onUpdate() {
            if (item != null) onUpdateIfUsedForItem(item.item);
            super.onUpdate();
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
            if (item.item == EditorItem.NULL_ITEM) {
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
                if (!EditItemComp.areEqual(item.item, other.item)) return false;
                if (item.count != other.count) return false;
                index++;
            }
            return true;
        }

        public Item generateLoot() {
            if (lootList.isEmpty()) return null;//mob should use it's default create loot methods
            int sum = 0;
            for (ItemWithCount item : lootList)
                sum += item.getCount();
            int get = Random.Int(sum);
            sum = 0;
            for (ItemWithCount item : lootList) {
                sum += item.getCount();
                if (sum > get) return item.item;
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
            itemWithCount.item = item;
            itemWithCount.count = count;
            lootList.add(itemWithCount);
        }
    }

}