/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2023 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.alphadraxonis.sandboxpixeldungeon.actors.hero;

import com.alphadraxonis.sandboxpixeldungeon.Assets;
import com.alphadraxonis.sandboxpixeldungeon.Badges;
import com.alphadraxonis.sandboxpixeldungeon.Challenges;
import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.QuickSlot;
import com.alphadraxonis.sandboxpixeldungeon.SPDSettings;
import com.alphadraxonis.sandboxpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.alphadraxonis.sandboxpixeldungeon.actors.hero.abilities.duelist.Challenge;
import com.alphadraxonis.sandboxpixeldungeon.actors.hero.abilities.duelist.ElementalStrike;
import com.alphadraxonis.sandboxpixeldungeon.actors.hero.abilities.duelist.Feint;
import com.alphadraxonis.sandboxpixeldungeon.actors.hero.abilities.huntress.NaturesPower;
import com.alphadraxonis.sandboxpixeldungeon.actors.hero.abilities.huntress.SpectralBlades;
import com.alphadraxonis.sandboxpixeldungeon.actors.hero.abilities.huntress.SpiritHawk;
import com.alphadraxonis.sandboxpixeldungeon.actors.hero.abilities.mage.ElementalBlast;
import com.alphadraxonis.sandboxpixeldungeon.actors.hero.abilities.mage.WarpBeacon;
import com.alphadraxonis.sandboxpixeldungeon.actors.hero.abilities.mage.WildMagic;
import com.alphadraxonis.sandboxpixeldungeon.actors.hero.abilities.rogue.DeathMark;
import com.alphadraxonis.sandboxpixeldungeon.actors.hero.abilities.rogue.ShadowClone;
import com.alphadraxonis.sandboxpixeldungeon.actors.hero.abilities.rogue.SmokeBomb;
import com.alphadraxonis.sandboxpixeldungeon.actors.hero.abilities.warrior.Endure;
import com.alphadraxonis.sandboxpixeldungeon.actors.hero.abilities.warrior.HeroicLeap;
import com.alphadraxonis.sandboxpixeldungeon.actors.hero.abilities.warrior.Shockwave;
import com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.dungeon.HeroSettings;
import com.alphadraxonis.sandboxpixeldungeon.items.BrokenSeal;
import com.alphadraxonis.sandboxpixeldungeon.items.EquipableItem;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.items.Waterskin;
import com.alphadraxonis.sandboxpixeldungeon.items.armor.ClothArmor;
import com.alphadraxonis.sandboxpixeldungeon.items.artifacts.Artifact;
import com.alphadraxonis.sandboxpixeldungeon.items.artifacts.CloakOfShadows;
import com.alphadraxonis.sandboxpixeldungeon.items.bags.Bag;
import com.alphadraxonis.sandboxpixeldungeon.items.bags.MagicalHolster;
import com.alphadraxonis.sandboxpixeldungeon.items.bags.PotionBandolier;
import com.alphadraxonis.sandboxpixeldungeon.items.bags.ScrollHolder;
import com.alphadraxonis.sandboxpixeldungeon.items.bags.VelvetPouch;
import com.alphadraxonis.sandboxpixeldungeon.items.food.Food;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.PotionOfHealing;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.PotionOfInvisibility;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.PotionOfMindVision;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.PotionOfStrength;
import com.alphadraxonis.sandboxpixeldungeon.items.rings.Ring;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.ScrollOfLullaby;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.ScrollOfMirrorImage;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.ScrollOfRage;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.alphadraxonis.sandboxpixeldungeon.items.wands.WandOfMagicMissile;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.SpiritBow;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.melee.Dagger;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.melee.Gloves;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.melee.MagesStaff;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.melee.Rapier;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.melee.WornShortsword;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.missiles.ThrowingKnife;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.missiles.ThrowingSpike;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.missiles.ThrowingStone;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;

public enum HeroClass {

    WARRIOR(HeroSubClass.BERSERKER, HeroSubClass.GLADIATOR),
    MAGE(HeroSubClass.BATTLEMAGE, HeroSubClass.WARLOCK),
    ROGUE(HeroSubClass.ASSASSIN, HeroSubClass.FREERUNNER),
    HUNTRESS(HeroSubClass.SNIPER, HeroSubClass.WARDEN),
    DUELIST(HeroSubClass.CHAMPION, HeroSubClass.MONK);

    private HeroSubClass[] subClasses;

    HeroClass(HeroSubClass... subClasses) {
        this.subClasses = subClasses;
    }

    public void initHero(Hero hero) {

        hero.heroClass = this;
        Talent.initClassTalents(hero);

        Item i = new ClothArmor().identify();
        if (!Challenges.isItemBlocked(i)) hero.belongings.armor = (ClothArmor) i;

        i = new Food();
        if (!Challenges.isItemBlocked(i)) i.collect();

        Waterskin waterskin = new Waterskin();
        waterskin.collect();

        new ScrollOfIdentify().identify();

        switch (this) {
            case WARRIOR:
                initWarrior(hero);
                break;

            case MAGE:
                initMage(hero);
                break;

            case ROGUE:
                initRogue(hero);
                break;

            case HUNTRESS:
                initHuntress(hero);
                break;

            case DUELIST:
                initDuelist(hero);
                break;
        }

        new VelvetPouch().collect();

        HeroSettings.HeroStartItemsData generalItems = Dungeon.customDungeon.startItems[0].getCopy();
        HeroSettings.HeroStartItemsData classItems = Dungeon.customDungeon.startItems[getIndex() + 1].getCopy();

        collectStartEq(hero, generalItems);
        collectStartEq(hero, classItems);

        collectStartItems(hero, classItems);
        collectStartItems(hero, generalItems);

        if (hero.belongings.weapon != null && hero.belongings.weapon.identifyOnStart)
            hero.belongings.weapon.identify();
        if (hero.belongings.armor != null && hero.belongings.armor.identifyOnStart)
            hero.belongings.armor.identify();
        if (hero.belongings.ring != null){
            hero.belongings.ring.reset();
            if( hero.belongings.ring.identifyOnStart) hero.belongings.ring.identify();
        }
        if (hero.belongings.artifact != null){
            hero.belongings.artifact.reset();
            if( hero.belongings.artifact.identifyOnStart) hero.belongings.artifact.identify();
        }
        if (hero.belongings.misc != null){
            hero.belongings.misc.reset();
            if( hero.belongings.misc.identifyOnStart) hero.belongings.misc.identify();
        }

//        validateItemLevelAquired(hero.belongings.weapon);
//        validateItemLevelAquired(hero.belongings.armor);
//        validateItemLevelAquired(hero.belongings.ring);
//        validateItemLevelAquired(hero.belongings.artifact);
//        validateItemLevelAquired(hero.belongings.misc);

        if (SPDSettings.quickslotWaterskin()) {
            for (int s = 0; s < QuickSlot.SIZE; s++) {
                if (Dungeon.quickslot.getItem(s) == null) {
                    Dungeon.quickslot.setSlot(s, waterskin);
                    break;
                }
            }
        }

//        if (Dungeon.isLevelTesting()) {
//            Item item = new ScrollOfIdentify();
//            item.quantity(100);
//            item.collect();
//
//            Weapon weapon = new Greataxe();
//            weapon.identify();
//            weapon.upgrade(300);
//            weapon.collect();
//
//            Ring ring = new RingOfHaste();
//            ring.identify();
//            ring.upgrade(50);
//            ring.collect();
//        }
    }

    private void validateItemLevelAquired(Item item) {
        if (item != null) Badges.validateItemLevelAquired(item);
    }

    public Badges.Badge masteryBadge() {
        switch (this) {
            case WARRIOR:
                return Badges.Badge.MASTERY_WARRIOR;
            case MAGE:
                return Badges.Badge.MASTERY_MAGE;
            case ROGUE:
                return Badges.Badge.MASTERY_ROGUE;
            case HUNTRESS:
                return Badges.Badge.MASTERY_HUNTRESS;
            case DUELIST:
                return Badges.Badge.MASTERY_DUELIST;
        }
        return null;
    }

    private static void initWarrior(Hero hero) {
        (hero.belongings.weapon = new WornShortsword()).identify();
        ThrowingStone stones = new ThrowingStone();
        stones.quantity(3).collect();
        Dungeon.quickslot.setSlot(0, stones);

        if (hero.belongings.armor != null) {
            hero.belongings.armor.affixSeal(new BrokenSeal());
        }

        new PotionOfHealing().identify();
        new ScrollOfRage().identify();
    }

    private static void initMage(Hero hero) {
        MagesStaff staff;

        staff = new MagesStaff(new WandOfMagicMissile());

        (hero.belongings.weapon = staff).identify();
        hero.belongings.weapon.activate(hero);

        Dungeon.quickslot.setSlot(0, staff);

        new ScrollOfUpgrade().identify();
        new PotionOfLiquidFlame().identify();
    }

    private static void initRogue(Hero hero) {
        (hero.belongings.weapon = new Dagger()).identify();

        CloakOfShadows cloak = new CloakOfShadows();
        (hero.belongings.artifact = cloak).identify();
        hero.belongings.artifact.activate(hero);

        ThrowingKnife knives = new ThrowingKnife();
        knives.quantity(3).collect();

        Dungeon.quickslot.setSlot(0, cloak);
        Dungeon.quickslot.setSlot(1, knives);

        new ScrollOfMagicMapping().identify();
        new PotionOfInvisibility().identify();
    }

    private static void initHuntress(Hero hero) {

        (hero.belongings.weapon = new Gloves()).identify();
        SpiritBow bow = new SpiritBow();
        bow.identify().collect();

        Dungeon.quickslot.setSlot(0, bow);

        new PotionOfMindVision().identify();
        new ScrollOfLullaby().identify();
    }

    private static void initDuelist(Hero hero) {

        (hero.belongings.weapon = new Rapier()).identify();
        hero.belongings.weapon.activate(hero);

        ThrowingSpike spikes = new ThrowingSpike();
        spikes.quantity(2).collect();

        Dungeon.quickslot.setSlot(0, hero.belongings.weapon);
        Dungeon.quickslot.setSlot(1, spikes);

        new PotionOfStrength().identify();
        new ScrollOfMirrorImage().identify();
    }

    private static void collectStartEq(Hero hero, HeroSettings.HeroStartItemsData startItems) {

        if (startItems.weapon != null && !Challenges.isItemBlocked(startItems.weapon)) {
            if (hero.belongings.weapon != null) overrideEq(hero, hero.belongings.weapon);
            hero.belongings.weapon = startItems.weapon;
            hero.belongings.weapon.activate(hero);
        }
        if (startItems.armor != null && !Challenges.isItemBlocked(startItems.weapon)) {
            if (hero.belongings.armor != null) overrideEq(hero, hero.belongings.armor);
            hero.belongings.armor = startItems.armor;
            hero.belongings.armor.activate(hero);
        }
        if (startItems.ring != null && !Challenges.isItemBlocked(startItems.weapon)) {
            if (hero.belongings.misc == null) {
                hero.belongings.misc = startItems.ring;
                hero.belongings.misc.activate(hero);
            } else {
                if (hero.belongings.ring != null) overrideEq(hero, hero.belongings.ring);
                hero.belongings.ring = startItems.ring;
                hero.belongings.ring.activate(hero);
            }
        }
        if (startItems.artifact != null && !Challenges.isItemBlocked(startItems.weapon)) {
            if (hero.belongings.misc == null) {
                if (hero.belongings.artifact.getClass() != startItems.artifact.getClass()) {
                    hero.belongings.misc = startItems.ring;
                    hero.belongings.misc.activate(hero);
                }
            } else if (hero.belongings.misc.getClass() != startItems.artifact.getClass()) {
                if (hero.belongings.artifact != null) overrideEq(hero, hero.belongings.artifact);
                hero.belongings.artifact = startItems.artifact;
                hero.belongings.artifact.activate(hero);
            }
        }
        if (startItems.misc != null && !Challenges.isItemBlocked(startItems.weapon)) {
            if (startItems.misc instanceof Artifact) {
                if (hero.belongings.artifact == null) {
                    hero.belongings.artifact = (Artifact) startItems.misc;
                    hero.belongings.artifact.activate(hero);
                } else if (hero.belongings.artifact.getClass() != startItems.misc.getClass()) {
                    if (hero.belongings.misc != null) overrideEq(hero, hero.belongings.misc);
                    hero.belongings.misc = startItems.misc;
                    hero.belongings.misc.activate(hero);
                }
            } else {
                if (hero.belongings.ring == null) {
                    hero.belongings.ring = (Ring) startItems.misc;
                    hero.belongings.ring.activate(hero);
                } else {
                    if (hero.belongings.misc != null) overrideEq(hero, hero.belongings.misc);
                    hero.belongings.misc = startItems.misc;
                    hero.belongings.misc.activate(hero);
                }
            }
        }

        for (Bag b : startItems.bags) {
            if (!Challenges.isItemBlocked(b)) {
                b.collect();
                if (b instanceof VelvetPouch) Dungeon.LimitedDrops.VELVET_POUCH.drop();
                if (b instanceof ScrollHolder) Dungeon.LimitedDrops.SCROLL_HOLDER.drop();
                if (b instanceof PotionBandolier) Dungeon.LimitedDrops.POTION_BANDOLIER.drop();
                if (b instanceof MagicalHolster) Dungeon.LimitedDrops.MAGICAL_HOLSTER.drop();
            }
        }
    }

    private static void overrideEq(Hero hero, EquipableItem toRemove){
        boolean cursed = toRemove.cursed;
        toRemove.cursed = false;
        toRemove.doUnequip(hero, false);
        toRemove.cursed = cursed;
    }

    private static void collectStartItems(Hero hero, HeroSettings.HeroStartItemsData startItems) {
        for (Item i : startItems.items) {
            if (!Challenges.isItemBlocked(i)) {
                i.reset();
                if (i.identifyOnStart) i.identify();
                i.doPickUp(hero);
            }
        }
    }

    public String title() {
        return Messages.get(HeroClass.class, name());
    }

    public String desc() {
        return Messages.get(HeroClass.class, name() + "_desc");
    }

    public String shortDesc() {
        return Messages.get(HeroClass.class, name() + "_desc_short");
    }

    public HeroSubClass[] subClasses() {
        return subClasses;
    }

    public ArmorAbility[] armorAbilities() {
        switch (this) {
            case WARRIOR:
            default:
                return new ArmorAbility[]{new HeroicLeap(), new Shockwave(), new Endure()};
            case MAGE:
                return new ArmorAbility[]{new ElementalBlast(), new WildMagic(), new WarpBeacon()};
            case ROGUE:
                return new ArmorAbility[]{new SmokeBomb(), new DeathMark(), new ShadowClone()};
            case HUNTRESS:
                return new ArmorAbility[]{new SpectralBlades(), new NaturesPower(), new SpiritHawk()};
            case DUELIST:
                return new ArmorAbility[]{new Challenge(), new ElementalStrike(), new Feint()};
        }
    }

    public String spritesheet() {
        switch (this) {
            case WARRIOR:
            default:
                return Assets.Sprites.WARRIOR;
            case MAGE:
                return Assets.Sprites.MAGE;
            case ROGUE:
                return Assets.Sprites.ROGUE;
            case HUNTRESS:
                return Assets.Sprites.HUNTRESS;
            case DUELIST:
                return Assets.Sprites.DUELIST;
        }
    }

    public String splashArt() {
        switch (this) {
            case WARRIOR:
            default:
                return Assets.Splashes.WARRIOR;
            case MAGE:
                return Assets.Splashes.MAGE;
            case ROGUE:
                return Assets.Splashes.ROGUE;
            case HUNTRESS:
                return Assets.Splashes.HUNTRESS;
            case DUELIST:
                return Assets.Splashes.DUELIST;
        }
    }

    public boolean isUnlocked() {

       return Dungeon.customDungeon.heroesEnabled[getIndex()];

//        //always unlock on debug builds
//        if (DeviceCompat.isDebug()) return true;
//
//        switch (this) {
//            case WARRIOR:
//            default:
//                return true;
//            case MAGE:
//                return Badges.isUnlocked(Badges.Badge.UNLOCK_MAGE);
//            case ROGUE:
//                return Badges.isUnlocked(Badges.Badge.UNLOCK_ROGUE);
//            case HUNTRESS:
//                return Badges.isUnlocked(Badges.Badge.UNLOCK_HUNTRESS);
//            case DUELIST:
//                return Badges.isUnlocked(Badges.Badge.UNLOCK_DUELIST);
//        }
    }

    public String unlockMsg() {
        return shortDesc()/* + "\n\n" + Messages.get(HeroClass.class, name() + "_unlock")*/;
    }

    public int getIndex(){
        switch (this) {
            case WARRIOR:  return 0;
            case MAGE:     return 1;
            case ROGUE:    return 2;
            case HUNTRESS: return 3;
            case DUELIST:  return 4;
        }
        return -1;
    }

}