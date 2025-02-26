package com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories;

import com.shatteredpixel.shatteredpixeldungeon.items.EnchantmentLike;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.curses.AntiEntropy;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.curses.Corrosion;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.curses.Displacement;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.curses.Metabolism;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.curses.Multiplicity;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.curses.Overgrowth;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.curses.Stench;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Affection;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.AntiMagic;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Brimstone;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Camouflage;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Entanglement;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Flow;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Obfuscation;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Potential;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Repulsion;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Stone;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Swiftness;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Thorns;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Viscosity;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Annoying;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Dazzling;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Displacing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Explosive;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Friendly;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Polarized;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Sacrificial;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Wayward;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Blazing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Blocking;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Blooming;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Chilling;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Corrupting;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Elastic;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Kinetic;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Lucky;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Projecting;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Shocking;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Unstable;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Vampiric;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.watabou.noosa.Image;

import java.util.Locale;

public final class Enchantments extends GameObjectCategory<EnchantmentLike> {

    private static Enchantments instance = new Enchantments();

    private final WeaponEnchantments WEAPON_ENCHANTMENTS = new WeaponEnchantments();
    private final WeaponCurses WEAPON_CURSES = new WeaponCurses();
    private final ArmorEnchantments ARMOR_ENCHANTMENTS = new ArmorEnchantments();
    private final ArmorCurses ARMOR_CURSES = new ArmorCurses();

    {
        values = new Enchantments.EnchantmentCategory[] {
                WEAPON_ENCHANTMENTS,
                WEAPON_CURSES,
                ARMOR_ENCHANTMENTS,
                ARMOR_CURSES
        };
    }

    private Enchantments() {
        super(new EditorItemBag(){});
        addItemsToBag();
    }

    public static Enchantments instance() {
        return instance;
    }

    public static EditorItemBag bag() {
        return instance().getBag();
    }
    
    @Override
    public ScrollingListPane.ListButton createAddBtn() {
        return null;
    }
    
    @Override
    public void updateCustomObjects() {
    }
    
    private static abstract class EnchantmentCategory extends SubCategory<EnchantmentLike> {

        private EnchantmentCategory(Class<?>[] classes) {
            super(classes);
        }

        @Override
        public Image getSprite() {
            return new ItemSprite();
        }

        @Override
        public String messageKey() {
            return getClass().getSimpleName().toLowerCase(Locale.ENGLISH);
        }
    }

    private static final class WeaponEnchantments extends EnchantmentCategory {

        private WeaponEnchantments() {
            super(new Class[] {
                    Blazing.class,
                    Blocking.class,
                    Blooming.class,
                    Chilling.class,
                    Corrupting.class,
                    Elastic.class,
                    Grim.class,
                    Kinetic.class,
                    Lucky.class,
                    Projecting.class,
                    Shocking.class,
                    Unstable.class,
                    Vampiric.class
            });
        }
    }

    private static final class WeaponCurses extends EnchantmentCategory {

        private WeaponCurses() {
            super(new Class[] {
                    Annoying.class,
                    Dazzling.class,
                    Displacing.class,
                    Explosive.class,
                    Friendly.class,
                    Polarized.class,
                    Sacrificial.class,
                    Wayward.class
            });
        }
    }

    private static final class ArmorEnchantments extends EnchantmentCategory {

        private ArmorEnchantments() {
            super(new Class[] {
                    Affection.class,
                    AntiMagic.class,
                    Brimstone.class,
                    Camouflage.class,
                    Entanglement.class,
                    Flow.class,
                    Obfuscation.class,
                    Potential.class,
                    Repulsion.class,
                    Stone.class,
                    Swiftness.class,
                    Thorns.class,
                    Viscosity.class
            });
        }
    }

    private static final class ArmorCurses extends EnchantmentCategory {

        private ArmorCurses() {
            super(new Class[] {
                    AntiEntropy.class,
                    Corrosion.class,
                    Displacement.class,
                    Metabolism.class,
                    Multiplicity.class,
                    Overgrowth.class,
                    Stench.class
            });
        }
    }

}