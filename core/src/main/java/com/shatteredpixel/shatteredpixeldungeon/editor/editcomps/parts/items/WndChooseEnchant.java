package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.items;

import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ChooseOneInCategoriesBody;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ChooseOneInCategoriesBody.BtnRow;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.WndChooseOneInCategories;
import com.shatteredpixel.shatteredpixeldungeon.items.EnchantmentLike;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.RandomGlyph;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.RandomEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.watabou.utils.Reflection;

public class WndChooseEnchant extends WndChooseOneInCategories {

    private final Item item;

    public WndChooseEnchant(Item item) {

        super(
                new IconTitle(new ItemSprite(item.image), Messages.get(WndChooseEnchant.class, "title", EnchantmentLike.getTypeName(item))),
                Messages.get(WndChooseEnchant.class, "body", EnchantmentLike.getTypeName(item)),
                createCategories(item),
                getRarityNames()
        );

        this.item = item;
    }


    private static Class<?>[][] createCategories(Item item) {
        Class<?>[][] cats = new Class[5][];
        cats[0] = new Class[3];
        if (item instanceof Weapon) {
            cats[0][0] = RandomEnchantment.class;
            cats[0][1] = com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.RandomCurse.class;
            cats[1] = Weapon.Enchantment.common;
            cats[2] = Weapon.Enchantment.uncommon;
            cats[3] = Weapon.Enchantment.rare;
            cats[4] = Weapon.Enchantment.curses;
        } else {
            cats[0][0] = RandomGlyph.class;
            cats[0][1] = com.shatteredpixel.shatteredpixeldungeon.items.armor.curses.RandomCurse.class;
            cats[1] = Armor.Glyph.common;
            cats[2] = Armor.Glyph.uncommon;
            cats[3] = Armor.Glyph.rare;
            cats[4] = Armor.Glyph.curses;
        }
        return cats;
    }

    @Override
    protected ChooseOneInCategoriesBody.BtnRow[] createCategoryRows(Object[] enchantments) {

        Class<? extends EnchantmentLike> type;
        BtnRow[] ret = new BtnRow[enchantments.length];

        for (int i = 0; i < enchantments.length; i++) {

            Class<? extends EnchantmentLike> clazz = (Class<? extends EnchantmentLike>) enchantments[i];

            if (clazz == null) {
                type = (Class<? extends EnchantmentLike>) enchantments[i-1];
                String descRemove = Messages.get(WndChooseEnchant.class, "remove_desc", EnchantmentLike.getTypeName(type));
                ret[i] = new ChooseOneInCategoriesBody.BtnRow(Messages.get(WndChooseEnchant.class, "remove"), descRemove) {
                    @Override
                    protected void onClick() {
                        EnchantmentLike.removeEnchantment(item);
                        finish();
                    }
                };
                continue;
            }

            EnchantmentLike enchantmentLike = Reflection.newInstance(clazz);
            ret[i] = new BtnRow(enchantmentLike.name(), enchantmentLike.desc()) {
                @Override
                protected void onClick() {
                    enchantmentLike.doApply(item);
                    finish();
                }
            };
        }
        return ret;
    }

    private static String[] getRarityNames() {
        return new String[]{
                Messages.get(WndChooseEnchant.class, "rarity_other"),
                Messages.get(WndChooseEnchant.class, "rarity_common"),
                Messages.get(WndChooseEnchant.class, "rarity_uncommon"),
                Messages.get(WndChooseEnchant.class, "rarity_rare"),
                Messages.get(WndChooseEnchant.class, "rarity_curses")};
    }

}