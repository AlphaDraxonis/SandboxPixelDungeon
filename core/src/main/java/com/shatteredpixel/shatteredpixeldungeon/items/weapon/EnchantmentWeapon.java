package com.shatteredpixel.shatteredpixeldungeon.items.weapon;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.RandomCurse;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Projecting;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.RandomEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

import java.util.*;

public class EnchantmentWeapon extends MeleeWeapon {

    public List<Enchantment> enchantments = new ArrayList<>(5);

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        int addDamage = 0;
        for (Enchantment ench : enchantments) {
            addDamage += ench.proc(this, attacker, defender, damage) - damage;
        }
        return super.proc(attacker, defender, damage + addDamage);
    }

    @Override
    public String info() {
        StringBuilder b = new StringBuilder();
        for (Enchantment ench : enchantments) {
            b.append(ench.name()).append(", ");
        }
        if (enchantments.size() > 1) b.setLength(b.length() - 2);
        return b.toString();
    }

    public boolean hasEnchantments() {
        return !enchantments.isEmpty();
    }

    public boolean hasEnchantment(Class<? extends Enchantment> enchantment) {
        for (Enchantment ench : enchantments) {
            if (ench.getClass() == enchantment) return true;
        }
        return false;
    }

    private static final String ENCHANTMENTS = "enchantments";
    private static final String LEVEL = "level";

    @Override
    public void restoreFromBundle(Bundle bundle) {
        level( bundle.getInt( LEVEL) );
        Collection<Bundlable> collection = bundle.getCollection(ENCHANTMENTS);
        for (Bundlable ench : collection) {
            addEnchantment((Enchantment) ench);
        }
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put( LEVEL, trueLevel() );
        bundle.put( ENCHANTMENTS, enchantments );
    }

    public void addEnchantment(Enchantment ench) {
        if (!hasEnchantment(ench.getClass())/* || ench.getClass() == RandomEnchantment.class || ench.getClass() == RandomCurse.class*/) {
            enchantments.add(ench);
            if (ench instanceof Projecting) enchantment = ench;
        }
    }

    public void removeEnchantment(Enchantment ench) {
        enchantments.remove(ench);
        if (ench == enchantment) enchantment = null;
    }

    public static boolean areEqual(EnchantmentWeapon a, EnchantmentWeapon b) {
        int sizeA = a == null ? 0 : a.enchantments.size();
        int sizeB = b == null ? 0 : b.enchantments.size();
        if (sizeA != sizeB) return false;
        if (a == null) return true;
        int index = 0;
        for (Enchantment ench : a.enchantments) {
            if (b.enchantments.get(index).getClass() != ench.getClass()) return false;
            index++;
        }
        return true;
    }


    public void replaceRandom() {
        Set<Class<? extends Enchantment>> existingEnchs = new HashSet<>();
        for (Enchantment ench : enchantments) {
            existingEnchs.add(ench.getClass());
        }

        if (existingEnchs.contains(RandomEnchantment.class)) {
            int tries = 100;
            Enchantment newEnch;
            do {
                newEnch = Enchantment.random();
                if (tries-- < 0) {
                    newEnch = null;
                    break;
                }
            } while (existingEnchs.contains(newEnch.getClass()));
            if (newEnch != null) {
                addEnchantment(newEnch);
                existingEnchs.add(newEnch.getClass());
            }
        }

        if (existingEnchs.contains(RandomCurse.class)) {
            int tries = 100;
            Enchantment newEnch;
            do {
                newEnch = Enchantment.randomCurse();
                if (tries-- < 0) {
                    newEnch = null;
                    break;
                }
            } while (existingEnchs.contains(newEnch.getClass()));
            if (newEnch != null) {
                addEnchantment(newEnch);
                existingEnchs.add(newEnch.getClass());
            }
        }
    }
}