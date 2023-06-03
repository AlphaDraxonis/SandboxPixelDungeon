package com.shatteredpixel.shatteredpixeldungeon.editor.scene.inv;

import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.editcomps.EditItemComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.IconTitleWithSubIcon;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemSlot;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.watabou.noosa.Image;

public class ItemItem extends EditorItem {


    private final Item item;


    public ItemItem(Item item) {
        this.item = item;
        icon = item.icon;
    }

    @Override
    public ScrollingListPane.ListItem createListItem(EditorInventoryWindow window) {
        return new DefaultListItem(this, window, item().name(), getSprite()) {
            @Override
            public void onUpdate() {//from in ItemTab.CatalogItem!!!
                if (item == null) return;
                Item i = item();
                bg.visible = i.cursed;
                label.text(Messages.titleCase(i.title()));

                if (icon != null) remove(icon);
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
        };
    }

    @Override
    public DefaultEditComp<?> createEditComponent() {
        return new EditItemComp(item(), null);
    }

    @Override
    public Image getSprite() {
        return CustomDungeon.getDungeon().getItemImage(item());
//        return new ItemSprite(item());
    }

    @Override
    public Image getSubIcon() {
        return IconTitleWithSubIcon.createSubIcon(item());
    }

    @Override
    public void place(int cell) {

        CustomLevel level = EditorScene.customLevel();

        if (!level.passable[cell]) return;

        Item i = item().clone();

        i.image = CustomDungeon.getDungeon().getItemSpriteOnSheet(i);

        level.drop(i, cell);
//
//        Mob m = level.getMobAtCell(cell);
//        if (m != null) {
//            MobItem.removeMob(m);
//        }

        //TODO remove highGrass?
    }

    public Item item() {
        return item;
    }

    @Override
    public int level() {
        return item().level();
    }

    @Override
    public int quantity() {
        return item().quantity();
    }

    public static boolean removeHeap(int cell, CustomLevel level) {
        Heap heap = level.heaps.get(cell);
        if (heap != null) {
            heap.destroy();
            return true;
        }
        return false;
    }

    public static boolean removeItem(int cell, CustomLevel level) {
        Heap heap = level.heaps.get(cell);
        if (heap != null) {
            heap.items.removeFirst();
            if (heap.items.isEmpty()) heap.destroy();
            else{
                heap.sprite.link();
                heap.updateSubicon();
                EditorScene.updateHeapImage(heap);
            }
            return true;
        }
        return false;
    }


    //Note: does not clone all variables, only selected ones (like level, ench. or quantity)
//    public static Item cloneItem(Item original) {
//        Item i = Reflection.newInstance(original.getClass());
//
//        i.image = original.image();
//        i.quantity(original.quantity());
//        i.dropsDownHeap = original.dropsDownHeap;
//        i.level(original.level());
//        i.cursed = original.cursed;
//        i.unique = original.unique;
//        i.keptThoughLostInvent = original.keptThoughLostInvent;
//        i.bones = original.bones;
//
//        if (original instanceof Armor) {
//            Armor a = (Armor) i;
//            Armor src = (Armor) original;
//            a.augment = src.augment;
//            a.glyph = src.glyph;
//            a.curseInfusionBonus = src.curseInfusionBonus;
//            a.masteryPotionBonus = src.masteryPotionBonus;
////          a.affixSeal(src.checkSeal());
//            a.tier = src.tier;
//        } else if (original instanceof Wand) {
//            Wand a = (Wand) i;
//            Wand src = (Wand) original;
//            a.curCharges = src.curCharges;
//            a.curChargeKnown = src.curChargeKnown;
//            a.curseInfusionBonus = src.curseInfusionBonus;
//            a.resinBonus = src.resinBonus;
//        } else if (original instanceof Bomb) {
//            ((Bomb) i).fuse = ((Bomb) original).fuse;
//        } else if (original instanceof Weapon) {
//            Weapon a = (Weapon) i;
//            Weapon src = (Weapon) original;
//            a.augment = src.augment;
//            a.enchantment = src.enchantment;
//            a.curseInfusionBonus = src.curseInfusionBonus;
//            a.masteryPotionBonus = src.masteryPotionBonus;
//        }
//
//
//        return i;
//    }
}