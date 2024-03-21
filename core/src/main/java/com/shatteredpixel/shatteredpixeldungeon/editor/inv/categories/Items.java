package com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories;

import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SacrificialFire;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Thief;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.TormentedSpirit;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.ItemItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.MobItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.RandomItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemsWithChanceDistrComp;
import com.shatteredpixel.shatteredpixeldungeon.items.*;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.*;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.*;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.*;
import com.shatteredpixel.shatteredpixeldungeon.items.food.*;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.*;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.*;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.BlizzardBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.CausticBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.InfernalBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.ShockingBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.*;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.*;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.*;
import com.shatteredpixel.shatteredpixeldungeon.items.remains.*;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.*;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.*;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.*;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.Runestone;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.*;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.*;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.*;
import com.shatteredpixel.shatteredpixeldungeon.plants.*;
import com.watabou.noosa.Image;
import com.watabou.utils.Reflection;

import java.util.*;

import static com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet.*;

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
    REMAINS(15),
    QUEST(16),
    ALCHEMICAL(17, CATA_HOLDER),
    SPELL(18, SPELL_HOLDER),
    OTHER(19);

    private static final int NUM_CATS = 18;//dont include other

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
                return REMAINS;
            case 16:
                return QUEST;
            case 17:
                return ALCHEMICAL;
            case 18:
                return SPELL;
            case 19:
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
            case REMAINS:
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

    private static final Class<?>[] EMPTY_ITEM_CLASS_ARRAY = new Class[0];

    public static Class<?>[][] getAllItems(Set<Class<? extends Item>> itemsToIgnore) {
        Items[] all = values();
        Class<?>[][] ret = new Class[all.length][];
        for (int i = 0; i < all.length; i++) {
            List<Class<?>> items = new ArrayList<>(Arrays.asList(all[i].classes()));
            if (itemsToIgnore != null) items.removeAll(itemsToIgnore);
            ret[i] = items.toArray(EMPTY_ITEM_CLASS_ARRAY);
        }
        return ret;
    }

    public static Class<? extends Item> getRandomItem(Set<Class<? extends Item>> itemsToIgnore) {
        Class<? extends Item>[][] items = (Class<? extends Item>[][]) getAllItems(itemsToIgnore);
        List<Class<? extends Item>> itemList = new ArrayList<>();
        for (Class<? extends Item>[] item : items) {
            itemList.addAll(Arrays.asList(item));
        }
        int length = itemList.size();
        if (length == 0) return null;
        return itemList.get((int) (Math.random() * length));
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
        ARTIFACT.classes = new Class<?>[]{
                AlchemistsToolkit.class,
                ChaliceOfBlood.class,
                CloakOfShadows.class,
                DriedRose.class,
                DriedRose.Petal.class,
                EtherealChains.class,
                HornOfPlenty.class,
                MasterThievesArmband.class,
                SandalsOfNature.class,
                TalismanOfForesight.class,
                TimekeepersHourglass.class,
                TimekeepersHourglass.sandBag.class,
                UnstableSpellbook.class,
                CapeOfThorns.class,
                LloydsBeacon.class
        };
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
                ScrollOfTransmutation.class, ScrollOfMetamorphosis.class,
                ScrollOfWipeOut.class
        };

        STONE.classes = Generator.Category.STONE.classes;

        SEED.classes = new Class[]{
                Dewdrop.class,
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
                Blandfruit.class,
                Pasty.FishLeftover.class,
                Pasty.PastyLunar.class,
                Pasty.PastyAprilFools.class,
                Pasty.PastyEaster.class,
                Pasty.PastyHWeen.class,
                Pasty.PastyXMas.class,
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
                MagicalHolster.class,
                Backpack.class
        };

        BOMB.classes = new Class[]{
                Bomb.class,
                FrostBomb.class,
                Firebomb.class,
                Flashbang.class,
                RegrowthBomb.class,
                WoollyBomb.class,
                Noisemaker.class,
                ShockBomb.class,
                HolyBomb.class,
                ArcaneBomb.class,
                ShrapnelBomb.class,
                FakeTenguBomb.class,
                FakeTenguShocker.class
        };

        REMAINS.classes = new Class[]{
                SealShard.class,
                BrokenStaff.class,
                CloakScrap.class,
                BowFragment.class,
                BrokenHilt.class
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
                RandomItem.RandomItemAny.class,
                Gold.class,
                Torch.class,
                Stylus.class,
                Honeypot.class,
                Honeypot.ShatteredPot.class,
                MerchantsBeacon.class,
                Ankh.class,
                Waterskin.class,
                BrokenSeal.class,
                TengusMask.class,
                KingsCrown.class,
                Amulet.class
        };

        List<Class<?>> otherClasses = new LinkedList<>();
        Collections.addAll(otherClasses, KEY.classes);
        Collections.addAll(otherClasses, BAG.classes);
        Collections.addAll(otherClasses, BOMB.classes);
        Collections.addAll(otherClasses, REMAINS.classes);
        Collections.addAll(otherClasses, QUEST.classes);
        Collections.addAll(otherClasses, ALCHEMICAL.classes);
        Collections.addAll(otherClasses, SPELL.classes);
        Collections.addAll(otherClasses, OTHER.classes);
        CATEGORY_OTHER.classes = otherClasses.toArray(new Class[0]);
    }


    public static final EditorItemBag bag = new EditorItemBag("name", 0) {
        @Override
        public Image getCategoryImage() {
            return null;
        }
    };

    public static class ItemBag extends EditorItemBag {

        public ItemBag(String name, int image, Class<?>[]... classes) {
            super(name, image);
            for (Class<?>[] cs : classes) {
                for (Class<?> c : cs) {
                    items.add(new ItemItem((Item) Reflection.newInstance(c)));
                }
            }
        }
    }

    private static final ItemBag bagWithKeys;

    static {
        bag.items.add(new ItemBag("melee", WEAPON_HOLDER, MELEE_WEAPON.classes()));
        bag.items.add(new ItemBag("wand", WAND_HOLDER, WAND.classes()));
        bag.items.add(new ItemBag("ring", RING_HOLDER, RING.classes()));
        bag.items.add(new ItemBag("seed", SEED_HOLDER, SEED.classes()));
        bag.items.add(new ItemBag("stone", STONE_HOLDER, STONE.classes()));
        bag.items.add(new ItemBag("bomb", BOMB_HOLDER, BOMB.classes()));
        bag.items.add(new ItemBag("food", FOOD_HOLDER, FOOD.classes()));
        bag.items.add(new ItemBag("missile", MISSILE_HOLDER, MISSILE_WEAPON.classes()));
        bag.items.add(new ItemBag("armor", ARMOR_HOLDER, ARMOR.classes()));
        bag.items.add(new ItemBag("artifact", ARTIFACT_HOLDER, ARTIFACT.classes()));
        bag.items.add(new ItemBag("potion", POTION_HOLDER, POTION.classes()));
        bag.items.add(new ItemBag("scroll", SCROLL_HOLDER, SCROLL.classes()));
        bag.items.add(new ItemBag("alch", SPELL_HOLDER, ALCHEMICAL.classes(), SPELL.classes()));
        bag.items.add(bagWithKeys = new ItemBag("other", SOMETHING, KEY.classes(), OTHER.classes(), BAG.classes(), REMAINS.classes(), QUEST.classes()));
    }

    public static void updateKeys(String oldLvlName, String newLvlName) {
        for (Item item : bagWithKeys) {
            maybeUpdateKeyLevel(((ItemItem) item).item(), oldLvlName, newLvlName);
        }
        maybeUpdateKeyLevel(SacrificialFire.prizeInInventory, oldLvlName, newLvlName);

        for (Item bag : Mobs.bag.items){
            if (bag instanceof Bag) {
                for (Item mobItem : ((Bag) bag).items) {
                    if (mobItem instanceof MobItem) {
                        updateKeysInMobContrainer((MobItem) mobItem, oldLvlName, newLvlName);
                    }
                }
            } else if (bag instanceof MobItem) {
                updateKeysInMobContrainer((MobItem) bag, oldLvlName, newLvlName);
            }
        }
    }

    public static void maybeUpdateKeyLevel(Item i, String oldLvlName, String newLvlName){
        if (i instanceof Key && (oldLvlName == null || ((Key) i).levelName == null || ((Key) i).levelName.equals(oldLvlName))) {
            ((Key) i).levelName = newLvlName;
        } else if (i instanceof RandomItem<?>){
            ((RandomItem<?>) i).updateInvalidKeys(oldLvlName, newLvlName);
        }
    }

    private static void updateKeysInMobContrainer(MobItem mobItem, String oldLvlName, String newLvlName){
        Mob m = mobItem.mob();
        if (m instanceof Mimic && ((Mimic) m).items != null) {
            for (Item item : ((Mimic) m).items) {
                maybeUpdateKeyLevel(item, oldLvlName, newLvlName);
            }
        }
        else if (m instanceof Thief) {
            maybeUpdateKeyLevel(((Thief) m).item, oldLvlName, newLvlName);
        }
        else if (m instanceof TormentedSpirit) {
            maybeUpdateKeyLevel(((TormentedSpirit) m).prize, oldLvlName, newLvlName);
        }
        if (m.loot instanceof ItemsWithChanceDistrComp.RandomItemData) {
            for (ItemsWithChanceDistrComp.ItemWithCount itemsWithCount : ((ItemsWithChanceDistrComp.RandomItemData) m.loot).distrSlots) {
                for (Item item : itemsWithCount.items) {
                    maybeUpdateKeyLevel(item, oldLvlName, newLvlName);
                }
            }
        }
    }

}