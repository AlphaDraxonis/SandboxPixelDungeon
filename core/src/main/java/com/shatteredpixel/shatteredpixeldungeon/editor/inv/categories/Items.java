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
import com.shatteredpixel.shatteredpixeldungeon.plants.*;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.noosa.Image;
import com.watabou.utils.Reflection;

import static com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet.*;

public enum Items implements EditorInvCategory<Item> {

    MELEE_WEAPON(WEAPON_HOLDER),
    MISSILE_WEAPON(MISSILE_HOLDER),
    ARMOR(ARMOR_HOLDER),
    WAND(WAND_HOLDER),
    RING(RING_HOLDER),
    ARTIFACT(ARTIFACT_HOLDER),
    POTION(POTION_HOLDER),
    SCROLL(SCROLL_HOLDER),
    STONE(STONE_HOLDER),
    SEED(SEED_HOLDER),
    FOOD(FOOD_HOLDER),
    BOMB(BOMB_HOLDER),
    ALCHEMICAL(SPELL_HOLDER),
    TRINKET(TRINKET_HOLDER),
    OTHER();

    private final int sprite;

    Items() {
        this(SOMETHING);
    }

    Items(int sprite) {
        this.sprite = sprite;
    }

    private Class<?>[] classes;

    @Override
    public Class<?>[] classes() {
        return classes;
    }

    static {

        MELEE_WEAPON.classes = new Class[] {
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

        MISSILE_WEAPON.classes = new Class[] {
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

        ARTIFACT.classes = new Class<?>[] {
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
                WondrousResin.class,
        };

        WAND.classes = Generator.Category.WAND.classes;

        POTION.classes = new Class[] {
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

                Pasty.PastyPride.class,
        };

        SCROLL.classes = new Class[] {
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

        SEED.classes = new Class[] {
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

        FOOD.classes = new Class[] {
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
        };

        BOMB.classes = new Class[] {
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

        ALCHEMICAL.classes = new Class[] {
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
        };

        TRINKET.classes = Generator.Category.TRINKET.classes;

        OTHER.classes = new Class[] {

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

                CeremonialCandle.class,
                CorpseDust.class,
                Embers.class,
                DarkGold.class,
                DwarfToken.class
        };
    }

    @Override
    public Image getSprite() {
        return new ItemSprite(sprite);
    }

    public static final EditorItemBag bag = new EditorItemBag("name", 0) {};

    private static final ItemBag bagWithKeys;

    static {
        bag.items.add(new ItemBag(MELEE_WEAPON));
        bag.items.add(new ItemBag(WAND));
        bag.items.add(new ItemBag(RING));
        bag.items.add(new ItemBag(SEED));
        bag.items.add(new ItemBag(STONE));
        bag.items.add(new ItemBag(BOMB));
        bag.items.add(new ItemBag(FOOD));
        bag.items.add(new ItemBag(MISSILE_WEAPON));
        bag.items.add(new ItemBag(ARMOR));
        bag.items.add(new ItemBag(ARTIFACT));
        bag.items.add(new ItemBag(POTION));
        bag.items.add(new ItemBag(SCROLL));
        bag.items.add(new ItemBag(ALCHEMICAL));
        bag.items.add(bagWithKeys = new ItemBag(OTHER));
    }

    public static class ItemBag extends EditorInvCategoryBag {
        public ItemBag(Items items) {
            super(items);
            for (Class<?> clazz : items.classes) {
                this.items.add(new ItemItem((Item) Reflection.newInstance(clazz)));
            }
        }
    }

    public static void updateKeys(String oldLvlName, String newLvlName) {

        GameObject.doOnAllGameObjectsList(EditorItemBag.mainBag.items, item -> {
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