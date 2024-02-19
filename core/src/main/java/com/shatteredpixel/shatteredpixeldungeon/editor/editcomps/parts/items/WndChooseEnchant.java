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

    public WndChooseEnchant() {

        super(
                Messages.get(WndChooseEnchant.class, "title_both",
                        Messages.get(Weapon.Enchantment.class, EnchantmentLike.getMessageKey(Weapon.Enchantment.class)),
                        Messages.get(Armor.Glyph.class, EnchantmentLike.getMessageKey(Armor.Glyph.class))),
                "",
                createCategories(),
                getAllRarityNames()
        );

        this.item = null;
    }

    public WndChooseEnchant(Item item) {

        super(
                new IconTitle(new ItemSprite(item.image), Messages.get(WndChooseEnchant.class, "title", EnchantmentLike.getTypeName(item))),
                Messages.get(WndChooseEnchant.class, "body", EnchantmentLike.getTypeName(item)),
                createCategories(item),
                getRarityNames()
        );

        this.item = item;
    }


    private static Class<?>[][] createCategories() {
        Class<?>[][] cats = new Class[9][];
        cats[0] = new Class[4];

        cats[0][0] = RandomEnchantment.class;
        cats[0][1] = com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.RandomCurse.class;
        cats[0][2] = RandomGlyph.class;
        cats[0][3] = com.shatteredpixel.shatteredpixeldungeon.items.armor.curses.RandomCurse.class;

        cats[1] = Weapon.Enchantment.common;
        cats[2] = Weapon.Enchantment.uncommon;
        cats[3] = Weapon.Enchantment.rare;
        cats[4] = Weapon.Enchantment.curses;

        cats[5] = Armor.Glyph.common;
        cats[6] = Armor.Glyph.uncommon;
        cats[7]= Armor.Glyph.rare;
        cats[8] = Armor.Glyph.curses;

        return cats;
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
                    onSelect(enchantmentLike);
                    finish();
                }
            };
        }
        return ret;
    }

    protected void onSelect(EnchantmentLike enchantment) {
        enchantment.doApply(item);
    }

    private static String[] getRarityNames() {
        return new String[]{
                Messages.get(WndChooseEnchant.class, "rarity_other"),
                Messages.get(WndChooseEnchant.class, "rarity_common"),
                Messages.get(WndChooseEnchant.class, "rarity_uncommon"),
                Messages.get(WndChooseEnchant.class, "rarity_rare"),
                Messages.get(WndChooseEnchant.class, "rarity_curses")};
    }

    private static String[] getAllRarityNames() {
        String msgWeapon = Messages.get(Weapon.Enchantment.class, EnchantmentLike.getMessageKey(Weapon.Enchantment.class));
        String msgArmor = Messages.get(Armor.Glyph.class, EnchantmentLike.getMessageKey(Armor.Glyph.class));
        String[] ret = new String[9];
        String[] rarities = getRarityNames();
        int index = 0;
        ret[index++] = rarities[0];
        for (; index < rarities.length; index++) {
            ret[index] = rarities[index] + " " + msgWeapon;
        }
        int offset = ret.length - rarities.length;
        for (; index < ret.length; index++) {
            ret[index] = rarities[index - offset] + " " + msgArmor;
        }
        return ret;
    }

}