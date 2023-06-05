package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.items;

import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ChooseOneInCategoriesBody;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ChooseOneInCategoriesBody.BtnRow;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.WndChooseOneInCategories;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.watabou.utils.Reflection;

public class WndChooseEnchant extends WndChooseOneInCategories {

    private final Item item;

    public WndChooseEnchant(Item item) {

        super(
                new IconTitle(new ItemSprite(item.image), Messages.get(WndChooseEnchant.class, "title", getEnchantType(item instanceof Weapon))),
                Messages.get(WndChooseEnchant.class, "body", getEnchantType(item instanceof Weapon)),
                createCategories(item),
                getRarityNames()
        );

        this.item = item;
    }


    private static Class<?>[][] createCategories(Item item) {
        Class<?>[][] cats = new Class[5][];
        if (item instanceof Weapon) {
            cats[1] = Weapon.Enchantment.common;
            cats[2] = Weapon.Enchantment.uncommon;
            cats[3] = Weapon.Enchantment.rare;
            cats[4] = Weapon.Enchantment.curses;
        } else {
            cats[1] = Armor.Glyph.common;
            cats[2] = Armor.Glyph.uncommon;
            cats[3] = Armor.Glyph.rare;
            cats[4] = Armor.Glyph.curses;
        }
        cats[0] = new Class[]{item.getClass()};
        return cats;
    }

    @Override
    protected ChooseOneInCategoriesBody.BtnRow[] createCategoryRows(Object[] enchantments) {

        boolean isWeapon;
        if (enchantments.length == 1 && ((isWeapon = Weapon.class.isAssignableFrom((Class<?>) enchantments[0])) || Armor.class.isAssignableFrom((Class<?>) enchantments[0]))) {
            ChooseOneInCategoriesBody.BtnRow[] ret = new ChooseOneInCategoriesBody.BtnRow[3];

            String titleRandom = Messages.get(WndChooseEnchant.class, "random", getEnchantType(isWeapon));
            String descRandom = Messages.get(WndChooseEnchant.class, "random_desc", getEnchantType(isWeapon)) + "\n" +
                    (isWeapon ? showChances(Weapon.Enchantment.typeChances) : showChances(Armor.Glyph.typeChances));
            ret[0] = new ChooseOneInCategoriesBody.BtnRow(titleRandom, descRandom) {
                @Override
                protected void onClick() {
                    randomEnchantment(item);
                    finish();
                }
            };
            ret[1] = new ChooseOneInCategoriesBody.BtnRow(
                    Messages.get(WndChooseEnchant.class, "random", Messages.get(WndChooseEnchant.class, "curse")),
                    Messages.get(WndChooseEnchant.class, "random_desc", Messages.get(WndChooseEnchant.class, "curse"))) {
                @Override
                protected void onClick() {
                    randomCurse(item);
                    finish();
                }
            };
            String descRemove = Messages.get(WndChooseEnchant.class, "remove_desc", getEnchantType(isWeapon));
            ret[2] = new ChooseOneInCategoriesBody.BtnRow(Messages.get(WndChooseEnchant.class, "remove"), descRemove) {
                @Override
                protected void onClick() {
                    removeEnchantment(item);
                    finish();
                }
            };
            return ret;
        }

        BtnRow[] ret = new BtnRow[enchantments.length];
        for (int i = 0; i < enchantments.length; i++) {

            Class<?> clazz = (Class<?>) enchantments[i];

            isWeapon = Weapon.Enchantment.class.isAssignableFrom(clazz);
            if (isWeapon) {
                Weapon.Enchantment enchantment = (Weapon.Enchantment) Reflection.newInstance(clazz);
                ret[i] = new BtnRow(enchantment.name(), enchantment.desc()) {
                    @Override
                    protected void onClick() {
                        ((Weapon) item).enchant(enchantment);
                        finish();
                    }
                };
            } else {
                Armor.Glyph glyph = (Armor.Glyph) Reflection.newInstance(clazz);
                ret[i] = new BtnRow(glyph.name(), glyph.desc()) {
                    @Override
                    protected void onClick() {
                        ((Armor) item).inscribe(glyph);
                        finish();
                    }
                };
            }
        }
        return ret;
    }

    private static String showChances(float[] typeChances) {
        String[] rarityNames = getRarityNames();
        StringBuilder b = new StringBuilder(Messages.get(WndChooseEnchant.class, "chance_desc"));
        for (int i = 0; i < typeChances.length; i++) {
            b.append("\n_-_ ").
                    append(Messages.decimalFormat("#.##", typeChances[i])).
                    append("% " + Messages.get(WndChooseEnchant.class, "chance_desc_for") + " ").append(rarityNames[i + 1]);
        }
        return b.toString();
    }

    private static String[] getRarityNames() {
        return new String[]{
                Messages.get(WndChooseEnchant.class, "rarity_other"),
                Messages.get(WndChooseEnchant.class, "rarity_common"),
                Messages.get(WndChooseEnchant.class, "rarity_uncommon"),
                Messages.get(WndChooseEnchant.class, "rarity_rare"),
                Messages.get(WndChooseEnchant.class, "rarity_curses")};
    }

    public static Item randomCurse(Item item) {
        if (item instanceof Weapon) {
            Weapon w = (Weapon) item;
            return w.enchant(Weapon.Enchantment.randomCurse(w.enchantment != null ? w.enchantment.getClass() : null));
        }
        if (item instanceof Armor) {
            Armor a = (Armor) item;
            return a.inscribe(Armor.Glyph.randomCurse(a.glyph != null ? a.glyph.getClass() : null));
        }
        return null;
    }

    public static Item randomEnchantment(Item item) {
        if (item instanceof Weapon) return ((Weapon) item).enchant();
        if (item instanceof Armor) return ((Armor) item).inscribe();
        return null;
    }

    public static void removeEnchantment(Item item) {
        if (item instanceof Weapon) ((Weapon) item).enchantment = null;
        else if (item instanceof Armor) ((Armor) item).glyph = null;
    }


    private static String getEnchantType(boolean isWeapon) {
        if(isWeapon)return Messages.get(Armor.Glyph.class,"glyph");
        else return Messages.get(Weapon.Enchantment.class,"enchant");
    }

}