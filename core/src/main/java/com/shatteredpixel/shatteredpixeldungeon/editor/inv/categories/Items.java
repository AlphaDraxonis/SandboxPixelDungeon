package com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories;

import com.shatteredpixel.shatteredpixeldungeon.GameObject;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SacrificialFire;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.ItemItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.RandomItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemsWithChanceDistrComp;
import com.shatteredpixel.shatteredpixeldungeon.items.*;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.*;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.*;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.*;
import com.shatteredpixel.shatteredpixeldungeon.items.food.*;
import com.shatteredpixel.shatteredpixeldungeon.items.journal.CustomDocumentPage;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.*;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.*;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.*;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.*;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.*;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.*;
import com.shatteredpixel.shatteredpixeldungeon.items.remains.*;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.*;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.*;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.*;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.*;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.*;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.*;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.*;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.*;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.shatteredpixel.shatteredpixeldungeon.usercontent.blueprints.CustomItem;
import com.shatteredpixel.shatteredpixeldungeon.usercontent.ui.WndNewCustomObject;
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
    private final OtherCat OTHER = new OtherCat();

    {
        values = new ItemCategory[] {
                MELEE_WEAPON,
                WAND,
                RING,
                SEED,
                STONE,
                BOMB,
                FOOD,
                MISSILE_WEAPON,
                ARMOR,
                ARTIFACT,
                POTION,
                SCROLL,
                ALCHEMICAL,
                OTHER
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

                    DimensionalSundial.class,
                    ExoticCrystals.class,
                    EyeOfNewt.class,
                    MimicTooth.class,
                    MossyClump.class,
                    ParchmentScrap.class,
                    PetrifiedSeed.class,
                    RatSkull.class,
                    ThirteenLeafClover.class,
                    TrapMechanism.class,
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
            super(STONE_HOLDER, Generator.Category.STONE.classes);
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

    private static final class OtherCat extends ItemCategory {

        private OtherCat() {
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