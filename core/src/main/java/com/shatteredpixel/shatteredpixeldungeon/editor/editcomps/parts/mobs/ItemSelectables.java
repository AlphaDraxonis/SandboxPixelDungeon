package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs;

import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemSelector;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;

public final class ItemSelectables {

    private ItemSelectables() {
    }

    public interface WeaponSelectable {
        Weapon weapon();

        void weapon(Weapon weapon);

        default ItemSelector.NullTypeSelector useNullWeapon() {
            return ItemSelector.NullTypeSelector.NOTHING;
        }
    }

    public interface ArmorSelectable {
        Armor armor();

        void armor(Armor armor);

        default ItemSelector.NullTypeSelector useNullArmor() {
            return ItemSelector.NullTypeSelector.NOTHING;
        }
    }
}