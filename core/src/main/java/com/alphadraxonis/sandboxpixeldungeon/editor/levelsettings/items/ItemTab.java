package com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.items;

import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.EditCompWindow;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.categories.Items;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomDungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomLevel;
import com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.WndEditorSettings;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.AdvancedListPaneItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.CategoryScroller;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.IconTitleWithSubIcon;
import com.alphadraxonis.sandboxpixeldungeon.editor.util.EditorUtilies;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.items.armor.Armor;
import com.alphadraxonis.sandboxpixeldungeon.items.wands.Wand;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.SpiritBow;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.Weapon;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSprite;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSpriteSheet;
import com.alphadraxonis.sandboxpixeldungeon.ui.ItemSlot;
import com.alphadraxonis.sandboxpixeldungeon.ui.ScrollingListPane;
import com.watabou.noosa.Image;

import java.util.List;

public class ItemTab extends WndEditorSettings.TabComp {

    //FIXME Itemtab needs a full rework, catalog item class will be deleted

    private static ItemTab itemTab;
    private CategoryScroller categoryScroller;

    public ItemTab() {
        itemTab = this;
    }

    @Override
    protected void createChildren(Object... params) {

        CategoryScroller.Category[] cats = new CategoryScroller.Category[Items.numCategoriesForItemOverwiew()];
        for (int i = 0; i < cats.length; i++) {
            final Items c = Items.getCategory(i);
            cats[i] = new CategoryScroller.Category() {

                @Override
                protected List<?> getItems() {
                    return c.filterStackSortItems(getItemsOnFl());
                }

                @Override
                protected Image getImage() {
                    return new ItemSprite(c.getItemSprite());
                }

                @Override
                protected ScrollingListPane.ListItem createListItem(Object object) {
                    return new CatalogItem((CustomLevel.ItemWithPos) object);
                }
            };
        }

        categoryScroller = new CategoryScroller(cats);
        add(categoryScroller);
    }

    private List<CustomLevel.ItemWithPos> getItemsOnFl() {
        return EditorScene.customLevel().getItems();
    }

    @Override
    public void layout() {
        super.layout();
        categoryScroller.setSize(width, height);
    }

    @Override
    public synchronized void destroy() {
        super.destroy();
        if (itemTab == this) itemTab = null;
    }

    public static class CatalogItem extends AdvancedListPaneItem {

        private final CustomLevel.ItemWithPos item;

        public CatalogItem(CustomLevel.ItemWithPos itemWithPos) {
            super(CustomDungeon.getDungeon().getItemImage(itemWithPos.item()),
                    IconTitleWithSubIcon.createSubIcon(itemWithPos.item()),
                    createTitle(itemWithPos));
            item = itemWithPos;
            onUpdate();
        }

        @Override
        public void onUpdate() {//also used in ItemItem!!!
            if (item == null) return;
            Item i = item.item();
            bg.visible = i.cursed;
            label.text(createTitle(item));

            if (icon != null) {
                remove(icon);
                icon.destroy();
            }
            icon = CustomDungeon.getDungeon().getItemImage(i);
            addToBack(icon);
            remove(bg);
            addToBack(bg);

            int lvl = i.level();
            if (lvl != 0) {
                lvlLabel.text(Messages.format(ItemSlot.TXT_LEVEL, lvl));
                lvlLabel.measure();
                if ((i instanceof Weapon && ((Weapon) i).curseInfusionBonus)
                        || (i instanceof Armor && ((Armor) i).curseInfusionBonus)
                        || (i instanceof Wand && ((Wand) i).curseInfusionBonus)) {
                    lvlLabel.hardlight(ItemSlot.CURSE_INFUSED);
                } else {
                    lvlLabel.hardlight(ItemSlot.UPGRADED);
                }
            } else lvlLabel.text(null);

            super.onUpdate();
        }

        @Override
        public void onClick() {
            EditorScene.show(new EditCompWindow(item.item(), this));
        }

    }

    public static void updateItems() {
        if (itemTab != null) {
            itemTab.categoryScroller.updateItems();
        }
    }

    public static String createTitle(CustomLevel.ItemWithPos itemWP) {
        Item item = itemWP.item();
        if ((item.quantity() > 1 || !item.isUpgradable()) && !(item instanceof SpiritBow))
            return Messages.titleCase(item.title());
        return EditorUtilies.formatTitle(itemWP);
    }

    @Override
    protected Image createIcon() {
        return new ItemSprite(ItemSpriteSheet.WEAPON_HOLDER);
    }
}