/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

import com.shatteredpixel.shatteredpixeldungeon.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.duelist.Challenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.duelist.ElementalStrike;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.duelist.Feint;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.NaturesPower;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.SpectralBlades;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.SpiritHawk;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.ElementalBlast;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.WarpBeacon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.WildMagic;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue.DeathMark;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue.ShadowClone;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue.SmokeBomb;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.Endure;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.HeroicLeap;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.Shockwave;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.dungeon.HeroSettings;
import com.shatteredpixel.shatteredpixeldungeon.items.*;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClothArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.*;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.*;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.*;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.*;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingKnife;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingSpike;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingStone;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

import java.util.Collection;

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

        new ScrollOfIdentify().identify();
        new ClothArmor().identify();

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

        HeroSettings.HeroStartItemsData generalItems = Dungeon.customDungeon.startItems[0].getCopy();
        HeroSettings.HeroStartItemsData classItems = Dungeon.customDungeon.startItems[getIndex() + 1].getCopy();

        hero.internalSpriteClass = classItems.spriteClass == null ? generalItems.spriteClass : classItems.spriteClass;

        addProperties(hero, generalItems.properties);
        addProperties(hero, classItems.properties);

        collectStartEq(hero, generalItems);
        collectStartEq(hero, classItems);

        collectStartItems(classItems);
        collectStartItems(generalItems);

        int plusLvl = generalItems.plusLvl + classItems.plusLvl;
        if (plusLvl != 0) {
            hero.lvl += plusLvl;
            hero.updateHT(true);
            hero.attackSkill += plusLvl;
            hero.defenseSkill += plusLvl;
        }
        hero.STR += generalItems.plusStr + classItems.plusStr;

        if (hero.belongings.weapon != null && hero.belongings.weapon.identifyOnStart) {
            hero.belongings.weapon.identify();
            maybePutIntoToolbar(hero.belongings.weapon);
        }
        if (hero.belongings.armor != null && hero.belongings.armor.identifyOnStart) {
            hero.belongings.armor.identify();
            maybePutIntoToolbar(hero.belongings.armor);
        }
        if (hero.belongings.ring != null) {
            hero.belongings.ring.reset();
            if (hero.belongings.ring.identifyOnStart) hero.belongings.ring.identify();
            maybePutIntoToolbar(hero.belongings.ring);
        }
        if (hero.belongings.artifact != null) {
            hero.belongings.artifact.reset();
            if (hero.belongings.artifact.identifyOnStart) hero.belongings.artifact.identify();
            maybePutIntoToolbar(hero.belongings.artifact);
        }
        if (hero.belongings.misc != null) {
            hero.belongings.misc.reset();
            if (hero.belongings.misc.identifyOnStart) hero.belongings.misc.identify();
            maybePutIntoToolbar(hero.belongings.misc);
        }
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

    public static void initGeneral(HeroSettings.HeroStartItemsData data) {
        if (data.armor == null) {
            ClothArmor i = new ClothArmor();
            i.identifyOnStart = true;
            data.armor = i;
        }

        data.items.add(new Food());
        data.items.add(new Waterskin());

        data.items.add(new VelvetPouch());
    }

    private static void initWarrior(Hero hero) {
        new WornShortsword().identify();
        new PotionOfHealing().identify();
        new ScrollOfRage().identify();
    }

    private static void initMage(Hero hero) {
        new MagesStaff().identify();
        new ScrollOfUpgrade().identify();
        new PotionOfLiquidFlame().identify();
    }

    private static void initRogue(Hero hero) {
        new Dagger().identify();
        new ScrollOfMagicMapping().identify();
        new PotionOfInvisibility().identify();
    }

    private static void initHuntress(Hero hero) {
        new Gloves().identify();
        new SpiritBow().identify();
        new PotionOfMindVision().identify();
        new ScrollOfLullaby().identify();
    }

    private static void initDuelist(Hero hero) {
        new Rapier().identify();
        new PotionOfStrength().identify();
        new ScrollOfMirrorImage().identify();
    }

    public static void initWarrior(HeroSettings.HeroStartItemsData data) {

        if (data.weapon == null) {
            WornShortsword i = new WornShortsword();
            i.identifyOnStart = true;
            data.weapon = i;
        }

        data.armor = new ClothArmor();
        data.armor.affixSeal(new BrokenSeal());
        data.armor.identifyOnStart = true;

        ThrowingStone stones = new ThrowingStone();
        stones.quantity(3);
        stones.reservedQuickslot = 1;
        data.items.add(stones);

    }

    public static void initMage(HeroSettings.HeroStartItemsData data) {
        if (data.weapon == null) {
            MagesStaff i = new MagesStaff(new WandOfMagicMissile());
            i.reservedQuickslot = 1;
            i.identifyOnStart = true;
            data.weapon = i;
        }
    }

    public static void initRouge(HeroSettings.HeroStartItemsData data) {
        if (data.weapon == null) {
            Dagger i = new Dagger();
            i.identifyOnStart = true;
            data.weapon = i;
        }

        int nextQuickslot = 0;
        if (data.artifact == null || data.misc == null) {
            CloakOfShadows cloak = new CloakOfShadows();
            cloak.identifyOnStart = true;
            cloak.reservedQuickslot = ++nextQuickslot;
            if (data.artifact == null) data.artifact = cloak;
            else data.misc = cloak;
        }

        ThrowingKnife knives = new ThrowingKnife();
        knives.quantity(3);
        knives.reservedQuickslot = ++nextQuickslot;
        data.items.add(knives);
    }

    public static void initHuntress(HeroSettings.HeroStartItemsData data) {
        if (data.weapon == null) {
            Gloves i = new Gloves();
            i.identifyOnStart = true;
            data.weapon = i;
        }
        SpiritBow bow = new SpiritBow();
        bow.identifyOnStart = true;
        bow.reservedQuickslot = 1;
        data.items.add(bow);
    }

    public static void initDuelist(HeroSettings.HeroStartItemsData data) {
        int nextQuickslot = 0;
        if (data.weapon == null) {
            Rapier i = new Rapier();
            i.identifyOnStart = true;
            i.reservedQuickslot = ++nextQuickslot;
            data.weapon = i;
        }

        ThrowingSpike spikes = new ThrowingSpike();
        spikes.quantity(2);
        spikes.reservedQuickslot = ++nextQuickslot;
        data.items.add(spikes);
    }

    private static void collectStartEq(Hero hero, HeroSettings.HeroStartItemsData startItems) {

        if (startItems.weapon != null && !Challenges.isItemBlocked(startItems.weapon)) {
            if (hero.belongings.weapon != null) overrideEq(hero, hero.belongings.weapon);
            hero.belongings.weapon = startItems.weapon;
            hero.belongings.weapon.activate(hero);
        }
        if (startItems.armor != null && !Challenges.isItemBlocked(startItems.armor)) {
            if (hero.belongings.armor != null) overrideEq(hero, hero.belongings.armor);
            hero.belongings.armor = startItems.armor;
            hero.belongings.armor.activate(hero);
        }
        if (startItems.ring != null && !Challenges.isItemBlocked(startItems.ring)) {
            if (hero.belongings.misc == null) {
                hero.belongings.misc = startItems.ring;
                hero.belongings.misc.activate(hero);
            } else {
                if (hero.belongings.ring != null) overrideEq(hero, hero.belongings.ring);
                hero.belongings.ring = startItems.ring;
                hero.belongings.ring.activate(hero);
            }
        }
        if (startItems.artifact != null && !Challenges.isItemBlocked(startItems.artifact)) {
            equipArtifact(startItems.artifact, hero);
        }
        if (startItems.misc != null && !Challenges.isItemBlocked(startItems.misc)) {
            if (startItems.misc instanceof Artifact) {
                equipArtifact((Artifact) startItems.misc, hero);
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

        for (Item item : startItems.items) {
            if (item instanceof Bag && !Challenges.isItemBlocked(item)) {
                Bag b = (Bag) item;
                b.collect();
                if (b instanceof VelvetPouch) Dungeon.LimitedDrops.VELVET_POUCH.drop();
                if (b instanceof ScrollHolder) Dungeon.LimitedDrops.SCROLL_HOLDER.drop();
                if (b instanceof PotionBandolier) Dungeon.LimitedDrops.POTION_BANDOLIER.drop();
                if (b instanceof MagicalHolster) Dungeon.LimitedDrops.MAGICAL_HOLSTER.drop();
                maybePutIntoToolbar(b);
            }
        }
    }

    private static void equipArtifact(Artifact artifact, Hero hero) {
        if (hero.belongings.misc == null) {
            if (hero.belongings.artifact == null || hero.belongings.artifact.getClass() == artifact.getClass()) {
                if (hero.belongings.artifact != null) overrideEq(hero, hero.belongings.artifact);
                hero.belongings.artifact = artifact;
                hero.belongings.artifact.activate(hero);
            } else {
                hero.belongings.misc = artifact;
                hero.belongings.misc.activate(hero);
            }
        } else if (hero.belongings.misc.getClass() != artifact.getClass()
                && hero.belongings.artifact != null && hero.belongings.artifact.getClass() != artifact.getClass()) {
            overrideEq(hero, hero.belongings.misc);
            hero.belongings.misc = artifact;
            hero.belongings.misc.activate(hero);
        } else if (hero.belongings.misc.getClass() == artifact.getClass()) {
            overrideEq(hero, hero.belongings.misc);
            hero.belongings.misc = artifact;
            hero.belongings.misc.activate(hero);
        } else {
            if (hero.belongings.artifact != null) overrideEq(hero, hero.belongings.artifact);
            hero.belongings.artifact = artifact;
            hero.belongings.artifact.activate(hero);
        }
    }

    private static void overrideEq(Hero hero, EquipableItem toRemove){
        boolean cursed = toRemove.cursed;
        toRemove.cursed = false;
        toRemove.doUnequip(hero, false);
        toRemove.cursed = cursed;
    }

    private static void collectStartItems(HeroSettings.HeroStartItemsData startItems) {
        for (Item i : startItems.items) {
            if (!Challenges.isItemBlocked(i)) {
                i.reset();
                if (i.identifyOnStart) i.identify();
                if (i instanceof Key) Notes.add((Key) i);
                else if (i instanceof Gold) Dungeon.gold += i.quantity();
                else if (i instanceof EnergyCrystal) Dungeon.energy += i.quantity();
                else {
                    i.collect();
                    maybePutIntoToolbar(i);
                }
            }
        }
    }

    private static void maybePutIntoToolbar(Item item){
        if (item.reservedQuickslot > 0 && item.defaultAction() != null && !(item instanceof Key)) Dungeon.quickslot.setSlot(item.reservedQuickslot - 1, item);
        else if (SPDSettings.quickslotWaterskin() && item instanceof Waterskin)
            for (int s = 0; s < QuickSlot.SIZE; s++) {
                if (Dungeon.quickslot.getItem(s) == null) {
                    Dungeon.quickslot.setSlot(s, item);
                    break;
                }
            }
    }

    private static void addProperties(Char ch, Collection<Char.Property> properties) {
        for (Char.Property prop : properties) {
            ch.getPropertiesVar_ACCESS_ONLY_FOR_EDITING_UI().add(prop);
        }
    }

    public String title() {
        return Messages.get(HeroClass.class, name());
    }

	public String desc(){
		return Messages.get(HeroClass.class, name()+"_desc");
	}

	public String shortDesc(){
		return Messages.get(HeroClass.class, name()+"_desc_short");
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

    public static HeroClass getFromIndex(int index) {
        switch (index){
            case 0: return WARRIOR;
            case 1: return MAGE;
            case 2: return ROGUE;
            case 3: return HUNTRESS;
            case 4: return DUELIST;
        }
        return null;
    }

}