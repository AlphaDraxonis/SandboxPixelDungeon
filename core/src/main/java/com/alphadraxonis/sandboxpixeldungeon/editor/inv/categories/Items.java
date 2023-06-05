package com.alphadraxonis.sandboxpixeldungeon.editor.inv.categories;

import static com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSpriteSheet.ARMOR_HOLDER;
import static com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSpriteSheet.ARTIFACT_HOLDER;
import static com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSpriteSheet.BOMB_HOLDER;
import static com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSpriteSheet.CATA_HOLDER;
import static com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSpriteSheet.FOOD_HOLDER;
import static com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSpriteSheet.IRON_KEY;
import static com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSpriteSheet.MISSILE_HOLDER;
import static com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSpriteSheet.POTION_HOLDER;
import static com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSpriteSheet.RING_HOLDER;
import static com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSpriteSheet.SCROLL_HOLDER;
import static com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSpriteSheet.SEED_HOLDER;
import static com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSpriteSheet.SOMETHING;
import static com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSpriteSheet.SPELL_HOLDER;
import static com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSpriteSheet.STONE_HOLDER;
import static com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSpriteSheet.WAND_HOLDER;
import static com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSpriteSheet.WEAPON_HOLDER;

import com.alphadraxonis.sandboxpixeldungeon.editor.inv.items.ItemItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomLevel;
import com.alphadraxonis.sandboxpixeldungeon.items.Amulet;
import com.alphadraxonis.sandboxpixeldungeon.items.ArcaneResin;
import com.alphadraxonis.sandboxpixeldungeon.items.EnergyCrystal;
import com.alphadraxonis.sandboxpixeldungeon.items.Generator;
import com.alphadraxonis.sandboxpixeldungeon.items.Gold;
import com.alphadraxonis.sandboxpixeldungeon.items.Honeypot;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.items.KingsCrown;
import com.alphadraxonis.sandboxpixeldungeon.items.LiquidMetal;
import com.alphadraxonis.sandboxpixeldungeon.items.Stylus;
import com.alphadraxonis.sandboxpixeldungeon.items.TengusMask;
import com.alphadraxonis.sandboxpixeldungeon.items.Torch;
import com.alphadraxonis.sandboxpixeldungeon.items.Waterskin;
import com.alphadraxonis.sandboxpixeldungeon.items.armor.Armor;
import com.alphadraxonis.sandboxpixeldungeon.items.artifacts.Artifact;
import com.alphadraxonis.sandboxpixeldungeon.items.bags.MagicalHolster;
import com.alphadraxonis.sandboxpixeldungeon.items.bags.PotionBandolier;
import com.alphadraxonis.sandboxpixeldungeon.items.bags.ScrollHolder;
import com.alphadraxonis.sandboxpixeldungeon.items.bags.VelvetPouch;
import com.alphadraxonis.sandboxpixeldungeon.items.bombs.ArcaneBomb;
import com.alphadraxonis.sandboxpixeldungeon.items.bombs.Firebomb;
import com.alphadraxonis.sandboxpixeldungeon.items.bombs.Flashbang;
import com.alphadraxonis.sandboxpixeldungeon.items.bombs.FrostBomb;
import com.alphadraxonis.sandboxpixeldungeon.items.bombs.HolyBomb;
import com.alphadraxonis.sandboxpixeldungeon.items.bombs.Noisemaker;
import com.alphadraxonis.sandboxpixeldungeon.items.bombs.RegrowthBomb;
import com.alphadraxonis.sandboxpixeldungeon.items.bombs.ShockBomb;
import com.alphadraxonis.sandboxpixeldungeon.items.bombs.ShrapnelBomb;
import com.alphadraxonis.sandboxpixeldungeon.items.bombs.WoollyBomb;
import com.alphadraxonis.sandboxpixeldungeon.items.food.Berry;
import com.alphadraxonis.sandboxpixeldungeon.items.food.Blandfruit;
import com.alphadraxonis.sandboxpixeldungeon.items.food.ChargrilledMeat;
import com.alphadraxonis.sandboxpixeldungeon.items.food.Food;
import com.alphadraxonis.sandboxpixeldungeon.items.food.FrozenCarpaccio;
import com.alphadraxonis.sandboxpixeldungeon.items.food.MeatPie;
import com.alphadraxonis.sandboxpixeldungeon.items.food.MysteryMeat;
import com.alphadraxonis.sandboxpixeldungeon.items.food.Pasty;
import com.alphadraxonis.sandboxpixeldungeon.items.food.PhantomMeat;
import com.alphadraxonis.sandboxpixeldungeon.items.food.SmallRation;
import com.alphadraxonis.sandboxpixeldungeon.items.food.StewedMeat;
import com.alphadraxonis.sandboxpixeldungeon.items.keys.CrystalKey;
import com.alphadraxonis.sandboxpixeldungeon.items.keys.GoldenKey;
import com.alphadraxonis.sandboxpixeldungeon.items.keys.IronKey;
import com.alphadraxonis.sandboxpixeldungeon.items.keys.SkeletonKey;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.AlchemicalCatalyst;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.Potion;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.PotionOfExperience;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.PotionOfFrost;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.PotionOfHaste;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.PotionOfHealing;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.PotionOfInvisibility;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.PotionOfLevitation;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.PotionOfMindVision;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.PotionOfParalyticGas;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.PotionOfPurity;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.PotionOfStrength;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.PotionOfToxicGas;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.brews.BlizzardBrew;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.brews.CausticBrew;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.brews.InfernalBrew;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.brews.ShockingBrew;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.elixirs.ElixirOfAquaticRejuvenation;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.elixirs.ElixirOfArcaneArmor;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.elixirs.ElixirOfDragonsBlood;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.elixirs.ElixirOfHoneyedHealing;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.elixirs.ElixirOfIcyTouch;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.elixirs.ElixirOfMight;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.elixirs.ElixirOfToxicEssence;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.exotic.PotionOfCleansing;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.exotic.PotionOfCorrosiveGas;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.exotic.PotionOfDivineInspiration;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.exotic.PotionOfDragonsBreath;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.exotic.PotionOfEarthenArmor;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.exotic.PotionOfMagicalSight;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.exotic.PotionOfMastery;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.exotic.PotionOfShielding;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.exotic.PotionOfShroudingFog;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.exotic.PotionOfSnapFreeze;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.exotic.PotionOfStamina;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.exotic.PotionOfStormClouds;
import com.alphadraxonis.sandboxpixeldungeon.items.quest.CeremonialCandle;
import com.alphadraxonis.sandboxpixeldungeon.items.quest.CorpseDust;
import com.alphadraxonis.sandboxpixeldungeon.items.quest.DarkGold;
import com.alphadraxonis.sandboxpixeldungeon.items.quest.DwarfToken;
import com.alphadraxonis.sandboxpixeldungeon.items.quest.Embers;
import com.alphadraxonis.sandboxpixeldungeon.items.quest.GooBlob;
import com.alphadraxonis.sandboxpixeldungeon.items.quest.MetalShard;
import com.alphadraxonis.sandboxpixeldungeon.items.quest.Pickaxe;
import com.alphadraxonis.sandboxpixeldungeon.items.quest.RatSkull;
import com.alphadraxonis.sandboxpixeldungeon.items.rings.Ring;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.Scroll;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.ScrollOfLullaby;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.ScrollOfMirrorImage;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.ScrollOfRage;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.ScrollOfRetribution;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.ScrollOfTerror;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.exotic.ScrollOfAntiMagic;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.exotic.ScrollOfChallenge;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.exotic.ScrollOfDivination;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.exotic.ScrollOfDread;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.exotic.ScrollOfEnchantment;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.exotic.ScrollOfForesight;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.exotic.ScrollOfMetamorphosis;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.exotic.ScrollOfMysticalEnergy;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.exotic.ScrollOfPassage;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.exotic.ScrollOfPrismaticImage;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.exotic.ScrollOfPsionicBlast;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.exotic.ScrollOfSirensSong;
import com.alphadraxonis.sandboxpixeldungeon.items.spells.Alchemize;
import com.alphadraxonis.sandboxpixeldungeon.items.spells.AquaBlast;
import com.alphadraxonis.sandboxpixeldungeon.items.spells.ArcaneCatalyst;
import com.alphadraxonis.sandboxpixeldungeon.items.spells.BeaconOfReturning;
import com.alphadraxonis.sandboxpixeldungeon.items.spells.CurseInfusion;
import com.alphadraxonis.sandboxpixeldungeon.items.spells.FeatherFall;
import com.alphadraxonis.sandboxpixeldungeon.items.spells.MagicalInfusion;
import com.alphadraxonis.sandboxpixeldungeon.items.spells.PhaseShift;
import com.alphadraxonis.sandboxpixeldungeon.items.spells.ReclaimTrap;
import com.alphadraxonis.sandboxpixeldungeon.items.spells.Recycle;
import com.alphadraxonis.sandboxpixeldungeon.items.spells.SummonElemental;
import com.alphadraxonis.sandboxpixeldungeon.items.spells.TelekineticGrab;
import com.alphadraxonis.sandboxpixeldungeon.items.spells.WildEnergy;
import com.alphadraxonis.sandboxpixeldungeon.items.stones.Runestone;
import com.alphadraxonis.sandboxpixeldungeon.items.wands.Wand;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.SpiritBow;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.Weapon;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.melee.AssassinsBlade;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.melee.BattleAxe;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.melee.Crossbow;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.melee.Dagger;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.melee.Dirk;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.melee.Flail;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.melee.Gauntlet;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.melee.Glaive;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.melee.Gloves;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.melee.Greataxe;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.melee.Greatshield;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.melee.Greatsword;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.melee.HandAxe;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.melee.Katana;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.melee.Longsword;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.melee.Mace;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.melee.MagesStaff;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.melee.Quarterstaff;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.melee.Rapier;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.melee.RoundShield;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.melee.RunicBlade;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.melee.Sai;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.melee.Scimitar;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.melee.Shortsword;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.melee.Sickle;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.melee.Spear;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.melee.Sword;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.melee.WarHammer;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.melee.WarScythe;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.melee.Whip;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.melee.WornShortsword;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.missiles.Bolas;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.missiles.FishingSpear;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.missiles.ForceCube;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.missiles.HeavyBoomerang;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.missiles.Javelin;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.missiles.Kunai;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.missiles.Shuriken;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.missiles.ThrowingClub;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.missiles.ThrowingHammer;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.missiles.ThrowingKnife;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.missiles.ThrowingSpear;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.missiles.ThrowingSpike;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.missiles.ThrowingStone;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.missiles.Tomahawk;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.missiles.Trident;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.missiles.darts.AdrenalineDart;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.missiles.darts.BlindingDart;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.missiles.darts.ChillingDart;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.missiles.darts.CleansingDart;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.missiles.darts.Dart;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.missiles.darts.DisplacingDart;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.missiles.darts.HealingDart;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.missiles.darts.HolyDart;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.missiles.darts.IncendiaryDart;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.missiles.darts.ParalyticDart;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.missiles.darts.PoisonDart;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.missiles.darts.RotDart;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.missiles.darts.ShockingDart;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.plants.Blindweed;
import com.alphadraxonis.sandboxpixeldungeon.plants.Earthroot;
import com.alphadraxonis.sandboxpixeldungeon.plants.Fadeleaf;
import com.alphadraxonis.sandboxpixeldungeon.plants.Firebloom;
import com.alphadraxonis.sandboxpixeldungeon.plants.Icecap;
import com.alphadraxonis.sandboxpixeldungeon.plants.Mageroyal;
import com.alphadraxonis.sandboxpixeldungeon.plants.Plant;
import com.alphadraxonis.sandboxpixeldungeon.plants.Rotberry;
import com.alphadraxonis.sandboxpixeldungeon.plants.Sorrowmoss;
import com.alphadraxonis.sandboxpixeldungeon.plants.Starflower;
import com.alphadraxonis.sandboxpixeldungeon.plants.Stormvine;
import com.alphadraxonis.sandboxpixeldungeon.plants.Sungrass;
import com.alphadraxonis.sandboxpixeldungeon.plants.Swiftthistle;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public enum Items {

    //These have own category in item overwiew
    MELEE_WEAPON(1, WEAPON_HOLDER),
    MISSILE_WEAPON(2, MISSILE_HOLDER),
    ARMOR(3, ARMOR_HOLDER),
    WAND(4, WAND_HOLDER),
    RING(5, RING_HOLDER),
    ARTIFACT(6, ARTIFACT_HOLDER),
    POTION(7, POTION_HOLDER),
    SCROLL(8, SCROLL_HOLDER),
    STONE(9, STONE_HOLDER),
    SEED(10, SEED_HOLDER),
    FOOD(11, FOOD_HOLDER),

    //Contains all missing items
    CATEGORY_OTHER(0),//FIXME delete WICHTIG


    //These dont
    KEY(12, IRON_KEY),
    BAG(13),
    BOMB(14, BOMB_HOLDER),
    QUEST(15),
    ALCHEMICAL(16, CATA_HOLDER),
    SPELL(17, SPELL_HOLDER),
    OTHER(18);

    private static final int NUM_CATS = 17;//dont include other

    private final int index, sprite;

    Items(int index) {
        this(index, SOMETHING);
    }

    Items(int index, int sprite) {
        this.index = index;
        this.sprite = sprite;
    }

    public static Items getCategory(int index) {
        switch (index) {
            case 1:
                return MELEE_WEAPON;
            case 2:
                return MISSILE_WEAPON;
            case 3:
                return ARMOR;
            case 4:
                return WAND;
            case 5:
                return RING;
            case 6:
                return ARTIFACT;
            case 7:
                return POTION;
            case 8:
                return SCROLL;
            case 9:
                return STONE;
            case 10:
                return SEED;
            case 11:
                return FOOD;
            case 0:
                return CATEGORY_OTHER;

            case 12:
                return KEY;
            case 13:
                return BAG;
            case 14:
                return BOMB;
            case 15:
                return QUEST;
            case 16:
                return ALCHEMICAL;
            case 17:
                return SPELL;
            case 18:
                return OTHER;
        }
        return null;
    }


    private Class<?>[] classes;

    public Class<?>[] classes() {
        return classes;
    }

    public static boolean hasOwnCategoryInItemOverview(Item item) {
        return item instanceof Weapon ||
                item instanceof Wand ||
                item instanceof Armor ||
                item instanceof Ring ||
                item instanceof Artifact ||
                item instanceof Potion ||
                item instanceof Scroll ||
                item instanceof Runestone ||
                item instanceof Plant.Seed ||
                item instanceof Food;
    }

    public static int numCategoriesForItemOverwiew() {
        return 12;
    }

    public int getItemSprite() {
        return sprite;
    }

    public int index() {
        return index;
    }

    public boolean isInCategory(Item item) {
        switch (this) {
            case MELEE_WEAPON:
            case MISSILE_WEAPON:
            case ARMOR:
            case WAND:
            case RING:
            case ARTIFACT:
            case POTION:
            case SCROLL:
            case STONE:
            case SEED:
            case FOOD:

            case KEY:
            case BAG:
            case BOMB:
            case SPELL:
            case QUEST:
            case ALCHEMICAL:
                for (Class<?> cl : classes()) {
                    if (cl == item.getClass()) return true;
                }
                return false;
            case OTHER:
                for (int i = 1; i <= NUM_CATS; i++) {
                    if (getCategory(i).isInCategory(item)) return false;
                }
                return true;
            case CATEGORY_OTHER:
                for (int i = 1; i < numCategoriesForItemOverwiew(); i++) {
                    if (getCategory(i).isInCategory(item)) return false;
                }
                return true;
        }
        return false;
    }


    public List<CustomLevel.ItemWithPos> filterStackSortItems(List<CustomLevel.ItemWithPos> items) {
        items = filterItems(items);
        items = stackItems(items);
        items = sortItems(items);
        return items;
    }

    public List<CustomLevel.ItemWithPos> filterItems(List<CustomLevel.ItemWithPos> items) {
        List<CustomLevel.ItemWithPos> ret = new ArrayList<>();
        for (CustomLevel.ItemWithPos i : items) {
            if (isInCategory(i.item())) ret.add(i);
        }
        return ret;
    }

    public List<CustomLevel.ItemWithPos> stackItems(List<CustomLevel.ItemWithPos> items) {
        List<CustomLevel.ItemWithPos> ret = new LinkedList<>();
        Map<Class<?>, Set<CustomLevel.ItemWithPos>> stackableItems = new HashMap<>();
        for (CustomLevel.ItemWithPos item : items) {
            Item i = item.item();
            if (!i.isUpgradable() && !(i instanceof SpiritBow)) {
                Class<? extends Item> cl = i.getClass();
                boolean added = false;
                if (stackableItems.containsKey(cl)) {
                    for (CustomLevel.ItemWithPos st : stackableItems.get(cl)) {
                        if (st.item().isSimilar(i)) {
                            st.item().merge(i);
                            added = true;
                            break;
                        }
                    }
                }
                if (!added) {
                    item = new CustomLevel.ItemWithPos(Reflection.newInstance(cl), item.pos());
                    Set<CustomLevel.ItemWithPos> set = stackableItems.get(cl);
                    if (set == null) set = new HashSet<>();
                    set.add(item);
                    ret.add(item);
                    stackableItems.put(cl, set);
                }
            } else {
                ret.add(item);
            }
        }
        return ret;
    }

    public List<CustomLevel.ItemWithPos> sortItems(List<CustomLevel.ItemWithPos> unsortedItems) {
        List<CustomLevel.ItemWithPos> sortedItems = new ArrayList<>();
        List<CustomLevel.ItemWithPos> toSort = new ArrayList<>(unsortedItems);

        for (Class<?> clazz : classes()) {
            for (CustomLevel.ItemWithPos item : unsortedItems) {
                if (clazz == item.item().getClass()) {
                    sortedItems.add(item);
                    toSort.remove(item);
                }
            }
        }
        sortedItems.addAll(toSort);//items that didnt have a class in the classesArray
        return sortedItems;
    }


    static {

        MELEE_WEAPON.classes = new Class[]{
                WornShortsword.class,
                MagesStaff.class,
                Dagger.class,
                Gloves.class,
                Rapier.class,
                SpiritBow.class,
                Shortsword.class,
                HandAxe.class,
                Spear.class,
                Quarterstaff.class,
                Dirk.class,
                Sickle.class,
                Pickaxe.class,
                Sword.class,
                Mace.class,
                Scimitar.class,
                RoundShield.class,
                Sai.class,
                Whip.class,
                Longsword.class,
                BattleAxe.class,
                Flail.class,
                RunicBlade.class,
                AssassinsBlade.class,
                Crossbow.class,
                Katana.class,
                Greatsword.class,
                WarHammer.class,
                Glaive.class,
                Greataxe.class,
                Greatshield.class,
                Gauntlet.class,
                WarScythe.class
        };

        MISSILE_WEAPON.classes = new Class[]{
                ThrowingStone.class,
                ThrowingKnife.class,
                ThrowingSpike.class,
                FishingSpear.class,
                ThrowingClub.class,
                Shuriken.class,
                ThrowingSpear.class,
                Kunai.class,
                Bolas.class,
                Javelin.class,
                Tomahawk.class,
                HeavyBoomerang.class,
                Trident.class,
                ThrowingHammer.class,
                ForceCube.class,
                Dart.class,
                AdrenalineDart.class,
                BlindingDart.class,
                ChillingDart.class,
                CleansingDart.class,
                DisplacingDart.class,
                HealingDart.class,
                HolyDart.class,
                IncendiaryDart.class,
                ParalyticDart.class,
                PoisonDart.class,
                RotDart.class,
                ShockingDart.class
        };

        ARMOR.classes = Generator.Category.ARMOR.classes;
        RING.classes = Generator.Category.RING.classes;
        ARTIFACT.classes = Generator.Category.ARTIFACT.classes;
        WAND.classes = Generator.Category.WAND.classes;

        POTION.classes = new Class[]{
                PotionOfStrength.class, PotionOfMastery.class,
                PotionOfHealing.class, PotionOfShielding.class,
                PotionOfMindVision.class, PotionOfMagicalSight.class,
                PotionOfFrost.class, PotionOfSnapFreeze.class,
                PotionOfLiquidFlame.class, PotionOfDragonsBreath.class,
                PotionOfToxicGas.class, PotionOfCorrosiveGas.class,
                PotionOfHaste.class, PotionOfStamina.class,
                PotionOfInvisibility.class, PotionOfShroudingFog.class,
                PotionOfLevitation.class, PotionOfStormClouds.class,
                PotionOfParalyticGas.class, PotionOfEarthenArmor.class,
                PotionOfPurity.class, PotionOfCleansing.class,
                PotionOfExperience.class, PotionOfDivineInspiration.class,

                ElixirOfAquaticRejuvenation.class,
                ElixirOfArcaneArmor.class,
                ElixirOfDragonsBlood.class,
                ElixirOfHoneyedHealing.class,
                ElixirOfIcyTouch.class,
                ElixirOfMight.class,
                ElixirOfToxicEssence.class,

                BlizzardBrew.class,
                CausticBrew.class,
                InfernalBrew.class,
                ShockingBrew.class
        };

        SCROLL.classes = new Class[]{
                ScrollOfUpgrade.class, ScrollOfEnchantment.class,
                ScrollOfIdentify.class, ScrollOfDivination.class,
                ScrollOfRemoveCurse.class, ScrollOfAntiMagic.class,
                ScrollOfMirrorImage.class, ScrollOfPrismaticImage.class,
                ScrollOfRecharging.class, ScrollOfMysticalEnergy.class,
                ScrollOfTeleportation.class, ScrollOfPassage.class,
                ScrollOfLullaby.class, ScrollOfSirensSong.class,
                ScrollOfMagicMapping.class, ScrollOfForesight.class,
                ScrollOfRage.class, ScrollOfChallenge.class,
                ScrollOfRetribution.class, ScrollOfPsionicBlast.class,
                ScrollOfTerror.class, ScrollOfDread.class,
                ScrollOfTransmutation.class, ScrollOfMetamorphosis.class
        };

        STONE.classes = Generator.Category.STONE.classes;

        SEED.classes = new Class[]{
                Sungrass.Seed.class,
                Fadeleaf.Seed.class,
                Icecap.Seed.class,
                Firebloom.Seed.class,
                Sorrowmoss.Seed.class,
                Swiftthistle.Seed.class,
                Blindweed.Seed.class,
                Stormvine.Seed.class,
                Earthroot.Seed.class,
                Mageroyal.Seed.class,
                Starflower.Seed.class,
                Rotberry.Seed.class
        };

        FOOD.classes = new Class[]{
                Food.class,
                Pasty.class,
                MysteryMeat.class,
                ChargrilledMeat.class,
                StewedMeat.class,
                FrozenCarpaccio.class,
                PhantomMeat.class,
                SmallRation.class,
                MeatPie.class,
                Berry.class,
                Blandfruit.class
        };

        KEY.classes = new Class[]{
                IronKey.class,
                GoldenKey.class,
                CrystalKey.class,
                SkeletonKey.class
        };


        BAG.classes = new Class[]{
                VelvetPouch.class,
                ScrollHolder.class,
                PotionBandolier.class,
                MagicalHolster.class
        };

        BOMB.classes = new Class[]{
                FrostBomb.class,
                Firebomb.class,
                Flashbang.class,
                RegrowthBomb.class,
                WoollyBomb.class,
                Noisemaker.class,
                ShockBomb.class,
                HolyBomb.class,
                ArcaneBomb.class,
                ShrapnelBomb.class
        };

        QUEST.classes = new Class[]{
                RatSkull.class,
                CeremonialCandle.class,
                CorpseDust.class,
                Embers.class,
                DarkGold.class,
                DwarfToken.class
        };

        ALCHEMICAL.classes = new Class[]{
                EnergyCrystal.class,
                AlchemicalCatalyst.class,
                ArcaneCatalyst.class,
                ArcaneResin.class,
                LiquidMetal.class,
                GooBlob.class,
                MetalShard.class
        };

        SPELL.classes = new Class[]{
                Alchemize.class,
                AquaBlast.class,
                BeaconOfReturning.class,
                CurseInfusion.class,
                FeatherFall.class,
                MagicalInfusion.class,
                PhaseShift.class,
                ReclaimTrap.class,
                Recycle.class,
                SummonElemental.class,
                TelekineticGrab.class,
                WildEnergy.class
        };

        OTHER.classes = new Class[]{
                Gold.class,
                Torch.class,
                Stylus.class,
                Honeypot.class,
                Honeypot.ShatteredPot.class,
                Waterskin.class,
                TengusMask.class,
                KingsCrown.class,
                Amulet.class
        };

        List<Class<?>> otherClasses = new LinkedList<>();
        Collections.addAll(otherClasses, KEY.classes);
        Collections.addAll(otherClasses, BAG.classes);
        Collections.addAll(otherClasses, BOMB.classes);
        Collections.addAll(otherClasses, QUEST.classes);
        Collections.addAll(otherClasses, ALCHEMICAL.classes);
        Collections.addAll(otherClasses, SPELL.classes);
        Collections.addAll(otherClasses, OTHER.classes);
        CATEGORY_OTHER.classes = otherClasses.toArray(new Class[0]);
    }


    public static final EditorItemBag bag = new EditorItemBag(Messages.get(EditorItemBag.class,"items"),null);

    public static class ItemBag extends EditorItemBag {

        public ItemBag(String name, int image, Class<?>[]... classes) {
            super(name,new ItemSprite(image));
            for (Class<?>[] cs : classes) {
                for (Class<?> c : cs) {
                    items.add(new ItemItem((Item) Reflection.newInstance(c)));
                }
            }
        }
    }

    static {
        bag.items.add(new ItemBag(Messages.get(Items.class,"weapons"), WEAPON_HOLDER, MELEE_WEAPON.classes(), MISSILE_WEAPON.classes(), WAND.classes()));
        bag.items.add(new ItemBag(Messages.get(Items.class,"eq"), ARMOR_HOLDER, ARMOR.classes(), RING.classes(), ARTIFACT.classes()));
        bag.items.add(new ItemBag(Messages.get(Items.class,"pot"), POTION_HOLDER, SEED.classes(), POTION.classes()));
        bag.items.add(new ItemBag(Messages.get(Items.class,"scroll"), SCROLL_HOLDER, STONE.classes(), SCROLL.classes()));
        bag.items.add(new ItemBag(Messages.get(Items.class,"alch"), CATA_HOLDER, ALCHEMICAL.classes(), SPELL.classes(), BOMB.classes()));
        bag.items.add(new ItemBag(Messages.get(Items.class,"misc"), FOOD_HOLDER, FOOD.classes(), KEY.classes()));
        bag.items.add(new ItemBag(Messages.get(Items.class,"other"), SOMETHING, OTHER.classes(), BAG.classes(), QUEST.classes()));
    }

}