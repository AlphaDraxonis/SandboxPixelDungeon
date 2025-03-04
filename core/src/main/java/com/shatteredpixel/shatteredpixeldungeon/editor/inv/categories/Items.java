package com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories;

import com.shatteredpixel.shatteredpixeldungeon.GameObject;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SacrificialFire;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.blueprints.CustomItem;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.ui.WndNewCustomObject;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.ItemItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.RandomItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemsWithChanceDistrComp;
import com.shatteredpixel.shatteredpixeldungeon.items.Amulet;
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh;
import com.shatteredpixel.shatteredpixeldungeon.items.ArcaneResin;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop;
import com.shatteredpixel.shatteredpixeldungeon.items.EnergyCrystal;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KingsCrown;
import com.shatteredpixel.shatteredpixeldungeon.items.LiquidMetal;
import com.shatteredpixel.shatteredpixeldungeon.items.MerchantsBeacon;
import com.shatteredpixel.shatteredpixeldungeon.items.Stylus;
import com.shatteredpixel.shatteredpixeldungeon.items.TengusMask;
import com.shatteredpixel.shatteredpixeldungeon.items.Torch;
import com.shatteredpixel.shatteredpixeldungeon.items.Waterskin;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.AlchemistsToolkit;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CapeOfThorns;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ChaliceOfBlood;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.EtherealChains;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HornOfPlenty;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.LloydsBeacon;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.MasterThievesArmband;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.SandalsOfNature;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.UnstableSpellbook;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Backpack;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.MagicalHolster;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.PotionBandolier;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.ScrollHolder;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.VelvetPouch;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.ArcaneBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.FakeTenguBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.FakeTenguShocker;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Firebomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.FlashBangBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.FrostBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.HolyBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Noisemaker;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.RegrowthBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.ShrapnelBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.SmokeBomb;
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
import com.shatteredpixel.shatteredpixeldungeon.items.journal.CustomDocumentPage;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.CrystalKey;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.GoldenKey;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.SkeletonKey;
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
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.AquaBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.BlizzardBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.CausticBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.InfernalBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.ShockingBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.UnstableBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfAquaticRejuvenation;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfArcaneArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfDragonsBlood;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfFeatherFall;
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
import com.shatteredpixel.shatteredpixeldungeon.items.quest.RatSkullOld;
import com.shatteredpixel.shatteredpixeldungeon.items.remains.BowFragment;
import com.shatteredpixel.shatteredpixeldungeon.items.remains.BrokenHilt;
import com.shatteredpixel.shatteredpixeldungeon.items.remains.BrokenStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.remains.CloakScrap;
import com.shatteredpixel.shatteredpixeldungeon.items.remains.SealShard;
import com.shatteredpixel.shatteredpixeldungeon.items.remains.TornPage;
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
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfWipeOut;
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
import com.shatteredpixel.shatteredpixeldungeon.items.spells.BeaconOfReturning;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.CurseInfusion;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.MagicalInfusion;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.PhaseShift;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.ReclaimTrap;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.Recycle;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.SummonElemental;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.TelekineticGrab;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.UnstableSpell;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.WildEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAggression;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAugmentation;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfBlink;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfClairvoyance;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfDeepSleep;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfDetectMagic;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfDisarming;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfFear;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfFlock;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfIntuition;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfShock;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ChaoticCenser;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.DimensionalSundial;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ExoticCrystals;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.EyeOfNewt;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.MimicTooth;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.MossyClump;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ParchmentScrap;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.PetrifiedSeed;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.RatSkull;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.SaltCube;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ShardOfOblivion;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ThirteenLeafClover;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.TrapMechanism;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.TrinketCatalyst;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.VialOfBlood;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.WondrousResin;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.*;
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
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.AdrenalineDart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.BlindingDart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.ChillingDart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.CleansingDart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.Dart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.DisplacingDart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.HealingDart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.HolyDart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.IncendiaryDart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.ParalyticDart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.PoisonDart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.RotDart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.ShockingDart;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Blindweed;
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot;
import com.shatteredpixel.shatteredpixeldungeon.plants.Fadeleaf;
import com.shatteredpixel.shatteredpixeldungeon.plants.Firebloom;
import com.shatteredpixel.shatteredpixeldungeon.plants.Icecap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Mageroyal;
import com.shatteredpixel.shatteredpixeldungeon.plants.Rotberry;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sorrowmoss;
import com.shatteredpixel.shatteredpixeldungeon.plants.Starflower;
import com.shatteredpixel.shatteredpixeldungeon.plants.Stormvine;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sungrass;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.watabou.noosa.Image;

import java.util.Locale;

import static com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet.*;

public final class Items extends GameObjectCategory<Item> {

    private static Items instance = new Items();

    private final MeleeWeaponCat MELEE_WEAPON = new MeleeWeaponCat();
    private final MissileWeaponCat MISSILE_WEAPON = new MissileWeaponCat();
    private final ArmorCat ARMOR = new ArmorCat();
    private final WandCat WAND = new WandCat();
    private final RingCat RING = new RingCat();
    private final ArtifactCat ARTIFACT = new ArtifactCat();
    private final PotionCat POTION = new PotionCat();
    private final ScrollCat SCROLL = new ScrollCat();
    private final StoneCat STONE = new StoneCat();
    private final SeedCat SEED = new SeedCat();
    private final FoodCat FOOD = new FoodCat();
    private final BombCat BOMB = new BombCat();
    private final AlchemicalCat ALCHEMICAL = new AlchemicalCat();
    private final TrinketCat TRINKET = new TrinketCat();
    private final MiscCat MISC = new MiscCat();

    {
        values = new ItemCategory[] {
                SEED,
                STONE,
                POTION,
                SCROLL,
                ALCHEMICAL,
                BOMB,
                FOOD,
                MISC,
                ARMOR,
                MELEE_WEAPON,
                MISSILE_WEAPON,
                WAND,
                ARTIFACT,
                RING
        };
    }

    private Items() {
        super(new EditorItemBag(){});
        addItemsToBag();
    }

    public static Items instance() {
        return instance;
    }

    public static EditorItemBag bag() {
        return instance().getBag();
    }

    @Override
    public void updateCustomObjects() {
        updateCustomObjects(Item.class);
    }

    public static void updateCustomItem(CustomItem customItem) {
        if (instance != null) {
            instance.updateCustomObject(customItem);
        }
    }

    @Override
    public ScrollingListPane.ListButton createAddBtn() {
        return new ScrollingListPane.ListButton() {
            protected RedButton createButton() {
                return new RedButton(Messages.get(Items.class, "add_custom_obj")) {
                    @Override
                    protected void onClick() {
                        DungeonScene.show(new WndNewCustomObject(CustomItem.class));
                    }
                };
            }
        };
    }


    private static abstract class ItemCategory extends GameObjectCategory.SubCategory<Item> {

        private final int sprite;

		private ItemCategory(int sprite, Class<?>[] classes) {
            super(classes);
            this.sprite = sprite;
		}

        @Override
        public Image getSprite() {
            return new ItemSprite(sprite);
        }

        @Override
        public String messageKey() {
            String name = getClass().getSimpleName().toLowerCase(Locale.ENGLISH);
            return name.substring(0, name.length() - 3);
        }
    }

    private static final class MeleeWeaponCat extends ItemCategory {

        private MeleeWeaponCat() {
            super(WEAPON_HOLDER, new Class[] {
                    WornShortsword.class,
                    MagesStaff.class,
                    Dagger.class,
                    Gloves.class,
                    Rapier.class,
                    SpiritBow.class,
                    Cudgel.class,
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
            });
        }
    }

    private static final class MissileWeaponCat extends ItemCategory {

        private MissileWeaponCat() {
            super(MISSILE_HOLDER, new Class[] {
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
            });
        }
    }

    private static final class ArmorCat extends ItemCategory {

        private ArmorCat() {
            super(ARMOR_HOLDER, Generator.Category.ARMOR.classes);
        }
    }

    private static final class WandCat extends ItemCategory {

        private WandCat() {
            super(WAND_HOLDER, Generator.Category.WAND.classes);
        }
    }

    private static final class RingCat extends ItemCategory {

        private RingCat() {
            super(RING_HOLDER, Generator.Category.RING.classes);
        }
    }

    private static final class ArtifactCat extends ItemCategory {

        private ArtifactCat() {
            super(ARTIFACT_HOLDER, new Class[] {
                    AlchemistsToolkit.class,
                    ChaliceOfBlood.class,
                    CloakOfShadows.class,
                    DriedRose.class,
                    DriedRose.Petal.class,
                    EtherealChains.class,
                    HolyTome.class,
                    HornOfPlenty.class,
                    MasterThievesArmband.class,
                    SandalsOfNature.class,
                    TalismanOfForesight.class,
                    TimekeepersHourglass.class,
                    TimekeepersHourglass.sandBag.class,
                    UnstableSpellbook.class,
                    CapeOfThorns.class,
                    LloydsBeacon.class,

                    TrinketCatalyst.class,

                    ChaoticCenser.class,
                    DimensionalSundial.class,
                    ExoticCrystals.class,
                    EyeOfNewt.class,
                    MimicTooth.class,
                    MossyClump.class,
                    ParchmentScrap.class,
                    PetrifiedSeed.class,
                    RatSkull.class,
                    SaltCube.class,
                    ShardOfOblivion.class,
                    ThirteenLeafClover.class,
                    TrapMechanism.class,
                    VialOfBlood.class,
                    WondrousResin.class
            });
        }
    }

    private static final class PotionCat extends ItemCategory {

        private PotionCat() {
            super(POTION_HOLDER, new Class[] {
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

                    ElixirOfFeatherFall.class,

                    BlizzardBrew.class,
                    CausticBrew.class,
                    InfernalBrew.class,
                    ShockingBrew.class,

                    UnstableBrew.class,
                    AquaBrew.class,

                    Pasty.PastyPride.class
            });
        }
    }

    private static final class ScrollCat extends ItemCategory {

        private ScrollCat() {
            super(SCROLL_HOLDER, new Class[] {
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
            });
        }
    }

    private static final class StoneCat extends ItemCategory {

        private StoneCat() {
            super(STONE_HOLDER, new Class[] {
                    StoneOfEnchantment.class,
                    StoneOfIntuition.class,
                    StoneOfDetectMagic.class,
                    StoneOfFlock.class,
                    StoneOfShock.class,
                    StoneOfBlink.class,
                    StoneOfDeepSleep.class,
                    StoneOfClairvoyance.class,
                    StoneOfAggression.class,
                    StoneOfBlast.class,
                    StoneOfFear.class,
                    StoneOfAugmentation.class,
                    StoneOfDisarming.class,
            });
        }
    }

    private static final class SeedCat extends ItemCategory {

        private SeedCat() {
            super(SEED_HOLDER, new Class[] {
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
            });
        }
    }

    private static final class FoodCat extends ItemCategory {

        private FoodCat() {
            super(FOOD_HOLDER, new Class[] {
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
                    Pasty.PastyPride.class,
                    Pasty.PastyShPDBirthday.class,
                    Pasty.PastyPDBirthday.class
            });
        }
    }

    private static final class BombCat extends ItemCategory {

        private BombCat() {
            super(BOMB_HOLDER, new Class[] {
                    Bomb.class,
                    FrostBomb.class,
                    Firebomb.class,
                    FlashBangBomb.class,
                    RegrowthBomb.class,
                    WoollyBomb.class,
                    Noisemaker.class,
                    SmokeBomb.class,
                    HolyBomb.class,
                    ArcaneBomb.class,
                    ShrapnelBomb.class,
                    FakeTenguBomb.class,
                    FakeTenguShocker.class
            });
        }
    }

    private static final class AlchemicalCat extends ItemCategory {

        private AlchemicalCat() {
            super(SPELL_HOLDER, new Class[] {
                    EnergyCrystal.class,
                    ArcaneResin.class,
                    LiquidMetal.class,

                    Alchemize.class,
                    BeaconOfReturning.class,
                    CurseInfusion.class,
                    MagicalInfusion.class,
                    PhaseShift.class,
                    ReclaimTrap.class,
                    Recycle.class,
                    SummonElemental.class,
                    TelekineticGrab.class,
                    UnstableSpell.class,
                    WildEnergy.class
            });
        }
    }

    private static final class TrinketCat extends ItemCategory {

        private TrinketCat() {
            super(TRINKET_HOLDER, Generator.Category.TRINKET.classes);
        }
    }

    private static final class MiscCat extends ItemCategory {

        private MiscCat() {
            super(SOMETHING, new Class[] {
                    IronKey.class,
                    GoldenKey.class,
                    CrystalKey.class,
                    SkeletonKey.class,

                    RandomItem.RandomItemAny.class,
                    Gold.class,
                    Torch.class,
                    Stylus.class,
                    Honeypot.class,
                    Honeypot.ShatteredPot.class,

                    CustomDocumentPage.class,

                    MerchantsBeacon.class,
                    Ankh.class,
                    Waterskin.class,
                    BrokenSeal.class,
                    GooBlob.class,
                    MetalShard.class,
                    TengusMask.class,
                    KingsCrown.class,
                    Amulet.class,

                    VelvetPouch.class,
                    ScrollHolder.class,
                    PotionBandolier.class,
                    MagicalHolster.class,
                    Backpack.class,

                    SealShard.class,
                    BrokenStaff.class,
                    CloakScrap.class,
                    BowFragment.class,
                    BrokenHilt.class,
                    TornPage.class,

                    RatSkullOld.class,
                    CeremonialCandle.class,
                    CorpseDust.class,
                    Embers.class,
                    DarkGold.class,
                    DwarfToken.class
            });
        }
    }

    public static void updateKeys(String oldLvlName, String newLvlName) {

        GameObject.doOnAllGameObjectsList(EditorInventory.mainBag.items, item -> {
            if (item instanceof ItemItem) maybeUpdateKeyLevel(((ItemItem) item).item(), oldLvlName, newLvlName);
            return GameObject.ModifyResult.noChange();
        });

        maybeUpdateKeyLevel(SacrificialFire.prizeInInventory, oldLvlName, newLvlName);
    }

    private static void maybeUpdateKeyLevel(Item i, String oldLvlName, String newLvlName) {
        if (i instanceof Key && (oldLvlName == null || ((Key) i).levelName == null || ((Key) i).levelName.equals(oldLvlName))) {
            ((Key) i).levelName = newLvlName;
        } else if (i instanceof RandomItem<?>) {
            GameObject.doOnAllGameObjectsList(((RandomItem<?>) i).getInternalRandomItem_ACCESS_ONLY_FOR_EDITING_UI().distrSlots, item -> {
                if (item instanceof ItemsWithChanceDistrComp.ItemWithCount) {
                    GameObject.doOnAllGameObjectsList(((ItemsWithChanceDistrComp.ItemWithCount) item).items, item2 -> {
                        maybeUpdateKeyLevel((Item) item2, oldLvlName, newLvlName);
                        return GameObject.ModifyResult.noChange();
                    });
                }
                return GameObject.ModifyResult.noChange();
            });
        }
    }
}
