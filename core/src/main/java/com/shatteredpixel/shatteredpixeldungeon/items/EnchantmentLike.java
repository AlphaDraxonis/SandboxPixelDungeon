package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

public interface EnchantmentLike extends Bundlable {

    default String name() {
        if (curse())
            return name( Messages.get(Item.class, "curse"));
        else
            return name( Messages.get(this, getMessageKey(getClass())));
    }

    default String name( String itemName ) {
        return Messages.get(this, "name", itemName);
    }

    default String desc() {
        return Messages.get(this, "desc");
    }

    default boolean curse() {
        return false;
    }

    @Override
    default void storeInBundle(Bundle bundle) {}

    @Override
    default void restoreFromBundle(Bundle bundle) {}

    ItemSprite.Glowing glowing();

    void doApply(Item item);

    default EnchantmentLike getCopy() {
        Bundle bundle = new Bundle();
        bundle.put("ENCHANTMENT_LIKE",this);
        return  (EnchantmentLike) bundle.get("ENCHANTMENT_LIKE");
    }


    static Class<? extends EnchantmentLike> getEnchantmentType(Item item) {
        return getEnchantmentType(item.getClass());
    }

    static Class<? extends EnchantmentLike> getEnchantmentType(Class<? extends Item> itemClass) {
        if (Weapon.class.isAssignableFrom(itemClass)) return Weapon.Enchantment.class;
        if (Armor.class.isAssignableFrom(itemClass)) return Armor.Glyph.class;
        return null;
    }

    static void removeEnchantment(Item item) {
        if (item instanceof Weapon) ((Weapon) item).enchantment = null;
        else if (item instanceof Armor) ((Armor) item).glyph = null;
    }

    static String getMessageKey(Class<? extends EnchantmentLike> type) {
        if (Weapon.Enchantment.class.isAssignableFrom(type)) return "enchant";
        if (Armor.Glyph.class.isAssignableFrom(type)) return "glyph";
        return Messages.NO_TEXT_FOUND;
    }

    static String getTypeName(Item item) {
        return getTypeName(getEnchantmentType(item));
    }

    static String getTypeName(Class<? extends EnchantmentLike> type) {
        if (Weapon.Enchantment.class.isAssignableFrom(type)) return Messages.get(Weapon.Enchantment.class, "enchant");
        if (Armor.Glyph.class.isAssignableFrom(type)) return Messages.get(Armor.Glyph.class, "glyph");
        return Messages.NO_TEXT_FOUND;
    }
}