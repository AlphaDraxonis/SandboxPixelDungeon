package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings;

import static com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet.ARMOR_HOLDER;
import static com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet.ARTIFACT_HOLDER;
import static com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet.BOMB_HOLDER;
import static com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet.CATA_HOLDER;
import static com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet.FOOD_HOLDER;
import static com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet.IRON_KEY;
import static com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet.MISSILE_HOLDER;
import static com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet.POTION_HOLDER;
import static com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet.RING_HOLDER;
import static com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet.SCROLL_HOLDER;
import static com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet.SEED_HOLDER;
import static com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet.SOMETHING;
import static com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet.SPELL_HOLDER;
import static com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet.STONE_HOLDER;
import static com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet.WAND_HOLDER;
import static com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet.WEAPON_HOLDER;

import com.shatteredpixel.shatteredpixeldungeon.editor.scene.inv.EditorItemBag;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.inv.ItemItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Amulet;
import com.shatteredpixel.shatteredpixeldungeon.items.ArcaneResin;
import com.shatteredpixel.shatteredpixeldungeon.items.EnergyCrystal;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KingsCrown;
import com.shatteredpixel.shatteredpixeldungeon.items.LiquidMetal;
import com.shatteredpixel.shatteredpixeldungeon.items.Stylus;
import com.shatteredpixel.shatteredpixeldungeon.items.TengusMask;
import com.shatteredpixel.shatteredpixeldungeon.items.Torch;
import com.shatteredpixel.shatteredpixeldungeon.items.Waterskin;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.MagicalHolster;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.PotionBandolier;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.ScrollHolder;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.VelvetPouch;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.ArcaneBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Firebomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Flashbang;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.FrostBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.HolyBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Noisemaker;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.RegrowthBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.ShockBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.ShrapnelBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.WoollyBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Berry;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Blandfruit;
import com.shatteredpixel.shatteredpixeldungeon.items.food.ChargrilledMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.food.FrozenCarpaccio;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MeatPie;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Pasty;
import com.shatteredpixel.shatteredpixeldungeon.items.food.PhantomMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.food.SmallRation;
import com.shatteredpixel.shatteredpixeldungeon.items.food.StewedMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.CrystalKey;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.GoldenKey;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.SkeletonKey;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.AlchemicalCatalyst;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHaste;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfInvisibility;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLevitation;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMindVision;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfParalyticGas;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfPurity;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.BlizzardBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.CausticBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.InfernalBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.ShockingBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfAquaticRejuvenation;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfArcaneArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfDragonsBlood;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfHoneyedHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfIcyTouch;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfMight;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfToxicEssence;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfCleansing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfCorrosiveGas;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfDivineInspiration;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfDragonsBreath;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfEarthenArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfMagicalSight;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfMastery;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfShielding;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfShroudingFog;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfSnapFreeze;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfStamina;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfStormClouds;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CeremonialCandle;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CorpseDust;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DarkGold;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DwarfToken;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Embers;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.GooBlob;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.MetalShard;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Pickaxe;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.RatSkull;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfLullaby;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMirrorImage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRetribution;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTerror;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfAntiMagic;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfChallenge;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfDivination;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfDread;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfMetamorphosis;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfMysticalEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfPassage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfPrismaticImage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfPsionicBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfSirensSong;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.Alchemize;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.AquaBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.ArcaneCatalyst;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.BeaconOfReturning;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.CurseInfusion;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.FeatherFall;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.MagicalInfusion;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.PhaseShift;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.ReclaimTrap;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.Recycle;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.SummonElemental;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.TelekineticGrab;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.WildEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.Runestone;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.AssassinsBlade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.BattleAxe;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Crossbow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Dagger;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Dirk;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Flail;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gauntlet;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Glaive;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gloves;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Greataxe;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Greatshield;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Greatsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.HandAxe;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Katana;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Longsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Mace;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Quarterstaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Rapier;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.RoundShield;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.RunicBlade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sai;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Scimitar;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Shortsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sickle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Spear;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WarHammer;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WarScythe;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Whip;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WornShortsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Bolas;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.FishingSpear;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ForceCube;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.HeavyBoomerang;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Javelin;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Kunai;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Shuriken;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingClub;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingHammer;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingKnife;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingSpear;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingSpike;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingStone;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Tomahawk;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Trident;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.plants.Blindweed;
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot;
import com.shatteredpixel.shatteredpixeldungeon.plants.Fadeleaf;
import com.shatteredpixel.shatteredpixeldungeon.plants.Firebloom;
import com.shatteredpixel.shatteredpixeldungeon.plants.Icecap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Mageroyal;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.plants.Rotberry;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sorrowmoss;
import com.shatteredpixel.shatteredpixeldungeon.plants.Starflower;
import com.shatteredpixel.shatteredpixeldungeon.plants.Stormvine;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sungrass;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.noosa.Image;
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
    MISSLE_WEAPON(2, MISSILE_HOLDER),
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
    CATEGORY_OTHER(0),


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
                return MISSLE_WEAPON;
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
            case MISSLE_WEAPON:
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

        MISSLE_WEAPON.classes = new Class[]{
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
                ForceCube.class
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


    public static final EditorItemBag bag = new EditorItemBag() {
        @Override
        public String name() {
            return "Items";
        }
    };

    public static class ItemBag extends EditorItemBag {

        private final int img;
        private final String name;

        public ItemBag(String name, int image, Class<?>[]... classes) {
            for (Class<?>[] cs : classes) {
                for (Class<?> c : cs) {
                    items.add(new ItemItem((Item) Reflection.newInstance(c)));
                }
            }
            img = image;
            this.name = name;
        }

        @Override
        public Image getCategoryImage() {
            return new ItemSprite(img);
        }

        public String name() {
            return name;
        }
    }

    static {

        bag.items.add(new ItemBag("Weapons", WEAPON_HOLDER, MELEE_WEAPON.classes(), MISSLE_WEAPON.classes(), WAND.classes()));
        bag.items.add(new ItemBag("Equipment", ARMOR_HOLDER, ARMOR.classes(), RING.classes(), ARTIFACT.classes()));
        bag.items.add(new ItemBag("Potion alchemy", POTION_HOLDER, SEED.classes(), POTION.classes()));
        bag.items.add(new ItemBag("Scroll alchemy", SCROLL_HOLDER, STONE.classes(), SCROLL.classes()));
        bag.items.add(new ItemBag("Other alchemy", CATA_HOLDER, ALCHEMICAL.classes(), SPELL.classes(), BOMB.classes()));
        bag.items.add(new ItemBag("Misc", FOOD_HOLDER, FOOD.classes(), KEY.classes()));
        bag.items.add(new ItemBag("Other", SOMETHING, OTHER.classes(), BAG.classes(), QUEST.classes()));
    }

}