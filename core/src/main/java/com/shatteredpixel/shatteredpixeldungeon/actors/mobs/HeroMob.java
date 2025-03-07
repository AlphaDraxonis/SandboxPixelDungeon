package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;


import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GameObject;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorrosiveGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.RatKing;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.interfaces.CustomItemClass;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditCompWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditMobComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.ItemContainer;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs.ItemSelectables;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.dungeon.HeroSettings;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemContainerWithLabel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledItemSelector;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Surprise;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindofMisc;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.UnlimitedCapacityBag;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.FakeTenguBomb;
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
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.AquaBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.BlizzardBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.Brew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.CausticBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.InfernalBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.ShockingBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.UnstableBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.Elixir;
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
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfDisintegration;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLightning;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfSummoning;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfWarding;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.AdrenalineDart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.BlindingDart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.ChillingDart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.CleansingDart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.HealingDart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.HolyDart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.IncendiaryDart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.TippedDart;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.PitfallTrap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoMob;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Bundle;
import com.watabou.utils.Function;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

public class HeroMob extends Mob implements ItemSelectables.WeaponSelectable, ItemSelectables.ArmorSelectable, MobBasedOnDepth {

    private DirectableAlly directableAlly;

    {
        spriteClass = null;

        maxLvl = 29;
    }

    private InternalHero internalHero;

    public boolean bindEquipment;//if true, the player can't change the equipment of this hero when it is allied

    private float wandCD, potionCD, utilItemCD;

    public HeroMob() {
        setInternalHero(new InternalHero());
    }

    @Override
    public void setLevel(int depth) {

        if (alignment == Alignment.ALLY) directableAlly = new DriedRose.GhostHero.GhostHeroDirectableAlly(this);

        internalHero.baseSpeed = baseSpeed;
        if (internalHero.belongings.weapon != null) {
            internalHero.belongings.weapon.activate(internalHero);
            if (internalHero.belongings.weapon.identifyOnStart) internalHero.belongings.weapon.identify(false);
        }
        if (internalHero.belongings.armor != null) {
            internalHero.belongings.armor.activate(internalHero);
            if (internalHero.belongings.armor.identifyOnStart) internalHero.belongings.armor.identify(false);
        }
        if (internalHero.belongings.ring != null) {
            internalHero.belongings.ring.activate(internalHero);
            if (internalHero.belongings.ring.identifyOnStart) internalHero.belongings.ring.identify(false);
        }
        if (internalHero.belongings.artifact != null) {
            internalHero.belongings.artifact.activate(internalHero);
            if (internalHero.belongings.artifact.identifyOnStart) internalHero.belongings.artifact.identify(false);
        }
        if (internalHero.belongings.misc != null) {
            internalHero.belongings.misc.activate(internalHero);
            if (internalHero.belongings.misc.identifyOnStart) internalHero.belongings.misc.identify(false);
        }

        for (Item i : internalHero.wands()) {
            if (i.identifyOnStart) i.identify(false);
            ((Wand) i).charge(internalHero);
        }
        for (Item i : internalHero.potions()) {
            if (i.identifyOnStart) i.identify(false);
        }
        for (Item i : internalHero.utilItems()) {
            if (i.identifyOnStart) i.identify(false);
        }

        if (!hpSet) {
            if (!CustomDungeon.isEditing()) hero().updateHT(false);
            HP = HT = internalHero.HP = internalHero.HT = (int) (internalHero.HT * statsScale);
            hpSet = !CustomDungeon.isEditing();
        }

        if (buff(Regeneration.class) == null) {
            Buff.affect(this, Regeneration.class);
            Buff.affect(this, Hunger.class);
        }
        updateEXP();
    }
    
    @Override
    public void initAsInventoryItem() {
        super.initAsInventoryItem();
        setInternalHero(new HeroMob.InternalHero());
    }
    
    @Override
    public boolean doOnAllGameObjects(Function<GameObject, ModifyResult> whatToDo) {
        Belongings belongings = hero().belongings;
        return super.doOnAllGameObjects(whatToDo)

                | doOnSingleObject(belongings.weapon  , whatToDo, newValue -> belongings.weapon   = newValue)
                | doOnSingleObject(belongings.armor   , whatToDo, newValue -> belongings.armor    = newValue)
                | doOnSingleObject(belongings.ring    , whatToDo, newValue -> belongings.ring     = newValue)
                | doOnSingleObject(belongings.artifact, whatToDo, newValue -> belongings.artifact = newValue)
                | doOnSingleObject(belongings.misc    , whatToDo, newValue -> belongings.misc     = newValue)

                | doOnAllGameObjectsList(internalHero.wands(), whatToDo)
                | doOnAllGameObjectsList(internalHero.potions(), whatToDo)
                | doOnAllGameObjectsList(internalHero.utilItems(), whatToDo);
    }

    @Override
    public boolean interact(Char c) {
        if (directableAlly != null && state == SLEEPING) {
            sprite.turnTo(pos, c.pos);
            if (c == Dungeon.hero) {
                if (state == SLEEPING) {
                    notice();
                    yell(Messages.get(RatKing.class, "not_sleeping"));
                    state = WANDERING;
                }
            }
            return true;
        }
        return super.interact(c);
    }

    @Override
    public CharSprite createSprite() {
        HeroSprite.HeroMobSprite sprite = new HeroSprite.HeroMobSprite(internalHero) {
            @Override
            public void link(Char ch) {
                super.link(ch);
                internalHero.sprite = this;
            }
        };
        sprite.updateArmor(internalHero);
        return sprite;
    }

    @Override
    public String name() {
        return customName == null ? internalHero.name() : super.name();
    }

    @Override
    public String desc() {
        return customDesc == null ? internalHero.heroClass.shortDesc() : super.desc();
    }

    @Override
    protected boolean act() {
        updateInternalStats();
        boolean ret = super.act();
        updateStats();
        return ret;
    }

    @Override
    public void hitSound(float pitch) {
        updateInternalStats();
        internalHero.hitSound(pitch);
        updateStats();
    }

    @Override
    public boolean blockSound(float pitch) {
        updateInternalStats();
        boolean ret = internalHero.blockSound(pitch);
        updateStats();
        return ret;
    }

    @Override
    public String defenseVerb() {
        updateInternalStats();
        String ret = internalHero.defenseVerb();
        updateStats();
        return ret;
    }

    @Override
    public int defenseSkill(Char enemy) {
        int defenseSkill = super.defenseSkill(enemy);
        if (defenseSkill == 0 || defenseSkill == INFINITE_EVASION) return defenseSkill;//normal defense skill is always at least 5 (Hero.STARTING_DEF_SKILL)
        updateInternalStats();
        int ret = (int) (internalHero.defenseSkill(enemy) * statsScale);
        updateStats();
        return ret;
    }

    @Override
    public int drRoll() {
        updateInternalStats();
        int ret = (int) (internalHero.drRoll() * statsScale);
        updateStats();
        return ret;
    }

    @Override
    public float speed() {
        updateInternalStats();
        float ret = internalHero.speed();
        updateStats();
        return ret;
    }

    private Item nextUseMeleeAttackItem() {
        for (Item item : internalHero.potions()) {
            Potion potion = (Potion) item;

            potion.anonymize();

            Class<? extends Potion> c = potion.getClass();
            if (CustomItemClass.class.isAssignableFrom(c)) c = (Class<? extends Potion>) c.getSuperclass();

            if (usePotionAsMelee(c)) return potion;

            if (c == UnstableBrew.class) {
                for (Class<? extends Potion> clazz : UnstableBrew.potionEffects()) {
                    if (usePotionAsMelee(clazz)) return potion;
                }
            }
        }

        if (hero().belongings.weapon == null) {
            for (Item item : internalHero.wands()) {
                if (!(item instanceof WandOfLightning) && useWandForAttack((Wand) item) != -1) return item;
            }
        }

        return null;
    }

    private boolean usePotionAsMelee(Class<? extends Potion> c) {
        if (c == PotionOfHealing.class   && internalHero.HP < 4 + internalHero.lvl && buff(Healing.class) == null) return true;
        if (c == PotionOfShielding.class && internalHero.HP < 4 + internalHero.lvl && buff(Healing.class) == null && buff(Barrier.class) == null) return true;

        if (c == PotionOfPurity.class && internalHero.buff(BlobImmunity.class) == null) {
            HashSet<Class> immunities = new BlobImmunity().immunities();
            oneBlob:
            for (Blob b : Dungeon.level.blobs.values()) {
                if (b != null && b.volume > 0 && b.cur[pos] > 0) {
                    Class<?> cl = b.getClass();
                    while (cl != null) {
                        if (immunities.contains(cl)) {
                            if (b instanceof Fire && buff(Burning.class) != null) continue oneBlob;
                            return true;
                        }
                        cl = cl.getSuperclass();
                    }
                }
            }
            return false;
        }
        if (c == PotionOfCleansing.class && buff(BlobImmunity.class) == null) {
            //only use on self
            for (Buff b : internalHero.buffs()) {
                if (b.type == Buff.buffType.NEGATIVE
                        && !(b instanceof AllyBuff)
                        && !(b instanceof LostInventory)) return true;
            }
            return false;
        }

        if (Elixir.class.isAssignableFrom(c)) {
            if (c == ElixirOfAquaticRejuvenation.class && buff(ElixirOfAquaticRejuvenation.AquaHealing.class) == null && internalHero.HP * 1.5f < 4 + internalHero.lvl) return true;
            if (c == ElixirOfArcaneArmor.class && buff(ArcaneArmor.class) == null) return true;
            if (c == ElixirOfDragonsBlood.class && buff(FireImbue.class) == null) return true;
            if (c == ElixirOfIcyTouch.class && buff(FrostImbue.class) == null) return true;
            if (c == ElixirOfMight.class && buff(ElixirOfMight.HTBoost.class) == null) return true;
            if (c == ElixirOfToxicEssence.class && buff(ToxicImbue.class) == null) return true;
            if (c == ElixirOfIcyTouch.class && buff(FireImbue.class) == null) return true;

            if (c == ElixirOfFeatherFall.class && buff(ElixirOfFeatherFall.FeatherBuff.class) == null) {
                //check if we are in the radius of a pitfall trap
                PitfallTrap.DelayedPit delayedPit = Dungeon.hero.buff(PitfallTrap.DelayedPit.class);
                if (delayedPit != null && delayedPit.activatedOn.equals(Dungeon.levelName) && delayedPit.branch == Dungeon.branch) {
                    for (int cell : delayedPit.positions) {
                        if (cell == pos) return true;
                    }
                }
            }
        }
        return false;
    }

    private Item nextUseDistanceAttackItem(int target, boolean justCheck) {

        Ballistica shot = new Ballistica( pos, target, Ballistica.REAL_PROJECTILE, null);

        if (justCheck) {
            if (internalHero.belongings.weapon() instanceof SpiritBow && shot.collisionPos == target)
                return internalHero.belongings.weapon();
        }

        if (potionCD <= 0 && shot.collisionPos == target) {
            for (Item item : internalHero.potions()) {
                Potion potion = (Potion) item;

                potion.anonymize();

                Class<? extends Potion> c = potion.getClass();
                if (CustomItemClass.class.isAssignableFrom(c)) c = (Class<? extends Potion>) c.getSuperclass();

                if (c == PotionOfStrength.class) return potion;
                if (c == PotionOfExperience.class) return potion;
                if (c == PotionOfHaste.class && buff(Haste.class) == null) return potion;
                if (c == PotionOfStamina.class && buff(Stamina.class) == null) return potion;
                if (c == PotionOfMindVision.class && buff(MindVision.class) == null) return potion;
                if (c == PotionOfMagicalSight.class && buff(MagicalSight.class) == null) return potion;
                if (c == PotionOfEarthenArmor.class && buff(Barkskin.class) == null) return potion;
                if ((c == PotionOfInvisibility.class || c == PotionOfShroudingFog.class) && invisible <= 0 && buff(Invisibility.class) == null && shot.dist >= 2) return potion;
                if (c == PotionOfLevitation.class && buff(Levitation.class) == null) return potion;//do not throw; maybe check for Chasms?

                if (c == PotionOfStormClouds.class && !Dungeon.level.water[target] && Dungeon.level.canSetCellToWater(true, target)) return potion;

                if (c == PotionOfFrost.class && enemy != null && !enemy.isImmune(Frost.class)) return potion;
                if (c == PotionOfLiquidFlame.class   && enemy != null && !enemy.isImmune(Fire.class) && !Dungeon.level.water[target] && enemy.buff(Burning.class) == null) return potion;
                if (c == PotionOfDragonsBreath.class && enemy != null && !enemy.isImmune(Fire.class) && !Dungeon.level.water[target] && enemy.buff(Burning.class) == null && shot.dist >= 4) return potion;

                if (enemy != null && (c == PotionOfToxicGas.class && !enemy.isImmune(ToxicGas.class) || c == PotionOfParalyticGas.class && !enemy.isImmune(Paralysis.class)
                        || c == PotionOfCorrosiveGas.class && !enemy.isImmune(CorrosiveGas.class) || c == PotionOfSnapFreeze.class && !enemy.isImmune(Frost.class) && enemy.buff(Frost.class) == null)
                        && shot.dist > 4
                        /*&& new Ballistica(pos, target, Ballistica.STOP_BARRIER_BLOBS, null).dist*/) return potion;

                if (potion instanceof Brew && shot.dist > 3) {
                    if (c == BlizzardBrew.class && enemy != null && !enemy.isImmune(Frost.class)) return potion;
                    if (c == CausticBrew.class && enemy != null && !enemy.isImmune(Ooze.class) && !Dungeon.level.water[target]) return potion;
                    if (c == InfernalBrew.class && enemy != null && !enemy.isImmune(Fire.class) && !Dungeon.level.water[target]) return potion;
                    if (c == ShockingBrew.class && enemy != null && !enemy.isImmune(Electricity.class)) return potion;
                }

                if (c == AquaBrew.class && enemy != null && (shot.dist > 2 || enemy.properties().contains(Property.FIERY))) return potion;

                if (c == ElixirOfHoneyedHealing.class && (internalHero.HP < 6 + internalHero.lvl/2 || enemy instanceof Bee && enemy.alignment != alignment)) return potion;

                //don't use: Mastery, DivineInspiration, Placeholder
            }
        }

        if (utilItemCD <= 0) {
            for (Item item : internalHero.utilItems()) {

                if (item instanceof MissileWeapon) {
                    if (item.throwPos(internalHero, target) != target) continue;

                    if (enemy != null && item instanceof TippedDart) {
                        if (item instanceof AdrenalineDart && enemy.isImmune(Cripple.class)) continue;
                        if (item instanceof BlindingDart && enemy.isImmune(Blindness.class)) continue;
                        if (item instanceof ChillingDart && enemy.isImmune(Chill.class)) continue;
                        if (item instanceof IncendiaryDart && (enemy.isImmune(Fire.class) || Dungeon.level.water[target] || enemy.buff(Burning.class) != null)) continue;

                        if (item instanceof HealingDart && enemy.isImmune(Healing.class) && enemy.alignment != alignment) continue;
                        if (item instanceof CleansingDart && enemy.alignment != alignment) continue;
                        if (item instanceof HolyDart && enemy.alignment != alignment) continue;
                    }

                    if (enemy == null || !enemy.isImmune(item.getClass())) return item;
                }

                if (item instanceof Bomb) {

                    if (shot.dist <= 2) continue;

                    if (item instanceof FakeTenguBomb && shot.dist < 4) continue;

                    return item;

                }

            }
        }

        if (wandCD <= 0) {
            for (Item item : internalHero.wands()) {
                if (useWandForAttack((Wand) item) != -1) return item;
            }
        }

        if (internalHero.belongings.weapon() instanceof SpiritBow
                && new Ballistica( pos, target, Ballistica.REAL_PROJECTILE, null).collisionPos == target)
            return internalHero.belongings.weapon();

        return null;
    }

    private int useWandForAttack(Wand wand) {
        if (wand.curCharges > 0) {

            List<Integer> candidates = new ArrayList<>();
            if (target == pos || !(wand instanceof WandOfSummoning || wand instanceof WandOfWarding)) candidates.add(target);
            else {
                for (int i : PathFinder.NEIGHBOURS8) {
                    candidates.add(i + target);
                }
            }

            int targetForSelfTarget = -1;
            List<Integer> targets = new ArrayList<>();

            while (!candidates.isEmpty()) {
                int aimingTarget = candidates.remove(Random.Int(candidates.size()));

                if (aimingTarget == pos) continue;

                final Ballistica wandShot = new Ballistica(pos, aimingTarget, wand.collisionProperties(aimingTarget), null);
                if (wandShot.collisionPos == pos) {
                    if (aimingTarget != pos || !hero().hasTalent(Talent.SHIELD_BATTERY)) {
                        continue;
                    }
                    if (buff(MagicImmune.class) != null) {
                        continue;
                    }
                    if (enemy != null && enemy.isImmune(wand.getClass())) {
                        continue;
                    }
                    targetForSelfTarget = aimingTarget;
                } else {
                    int distance = wand instanceof WandOfDisintegration ? ((WandOfDisintegration) wand).distance() : Integer.MAX_VALUE;
                    int indexTarget = wandShot.path.indexOf(aimingTarget);
                    if (indexTarget >= 0 && indexTarget <= distance) {
                        if (enemy != null && enemy.isImmune(wand.getClass())) {
                            continue;
                        }
                        if (wand.tryToZap(hero(), wandShot.collisionPos))
                            targets.add(aimingTarget);
                    }
                }
            }

            if (targets.isEmpty()) return targetForSelfTarget;
            return Random.element(targets);

        }
        return -1;
    }

    @Override
    public boolean canSurpriseAttack() {
        updateInternalStats();
        boolean ret = internalHero.canSurpriseAttack();
        updateStats();
        return ret;
    }

    @Override
    protected boolean canAttack( Char enemy ) {
        return waitNextTurn || super.canAttack(enemy) || internalHero.canAttack(enemy) || nextUseMeleeAttackItem() != null || nextUseDistanceAttackItem(enemy.pos, true) != null;
    }

    @Override
    protected boolean doAttack(Char enemy) {
        if (waitNextTurn) {
            waitNextTurn = false;
            spend(TICK);
            return true;
        }
        Item meleeAttackItem = nextUseMeleeAttackItem();
        if (meleeAttackItem != null && !(meleeAttackItem instanceof Wand)) {
            useMeleeAttackItem();
			return sprite == null || (!sprite.visible && !enemy.sprite.visible);
        }

        if (Dungeon.level.adjacent( pos, enemy.pos ) || nextUseDistanceAttackItem(enemy.pos, true) == null) {

            return super.doAttack( enemy );

        } else {

            useDistanceAttackItem();

            if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
                return false;
            } else {
                return true;
            }
        }
    }

    protected void useMeleeAttackItem() {
        Item item = nextUseMeleeAttackItem();

        if (item == null) return;

        Invisibility.dispel(this);

        if (item instanceof Potion) {
            Random.shuffleWithChances(internalHero.potions(), Item::quantity);
            Potion potion = (Potion) (item.quantity() > 1 ? item.split(1) : item);
            potion.doDrink(internalHero);
            if (item.quantity() <= 1) internalHero.potions().remove(item);
            return;
        }
    }

    private boolean waitNextTurn = false;

    protected void useDistanceAttackItem() {

        Item item = nextUseDistanceAttackItem(target, false);

        if (item == null) return;

        Invisibility.dispel(this);

        if (item instanceof Potion) {

            Potion potion = (Potion) (item.quantity() > 1 ? item.split(1) : item);

            //throw:
            if (potion instanceof Brew || potion instanceof PotionOfFrost || potion instanceof PotionOfLiquidFlame
                    || potion instanceof PotionOfStormClouds
                || potion instanceof ElixirOfHoneyedHealing && enemy instanceof Bee && enemy.alignment == Alignment.ENEMY) {
                potion.cast(internalHero, target);
            }

            else if (potion instanceof PotionOfToxicGas || potion instanceof PotionOfParalyticGas || potion instanceof PotionOfCorrosiveGas) {
                Ballistica shot = new Ballistica( pos, target, Ballistica.REAL_PROJECTILE, null);
                if (shot.dist <= 2) potion.cast(internalHero, target);
                else potion.cast(internalHero, shot.path.get(shot.dist-2));
            }

            else if (potion instanceof PotionOfSnapFreeze) {
                Ballistica shot = new Ballistica( pos, target, Ballistica.REAL_PROJECTILE, null);
                if (shot.dist <= 2) potion.cast(internalHero, target);
                else potion.cast(internalHero, shot.path.get(shot.dist-1));
            }

            else if (potion instanceof PotionOfDragonsBreath) {
                ((PotionOfDragonsBreath) potion).usedByHeroMob(this, enemy);
            }

            //drink:
            else potion.doDrink(internalHero);

            if (item.quantity() <= 1) internalHero.potions().remove(item);


            if (internalHero.belongings.weapon == null) potionCD = 0;
            else potionCD += Random.Float(5f, 8f);

            Random.shuffleWithChances(internalHero.potions(), Item::quantity);
            return;
        }

        if (item instanceof Bomb) {
            Ballistica shot = new Ballistica( pos, target, Ballistica.REAL_PROJECTILE, null);
            ((Bomb) item).shoot(internalHero, shot.path.get(shot.path.size()-2));

            if (internalHero.belongings.weapon == null) utilItemCD = 0;
            else utilItemCD += Random.Float(1.9f, 3.3f);

            Random.shuffleWithChances(internalHero.utilItems(), Item::quantity);
            return;
        }

        if (item instanceof MissileWeapon) {
            item.cast(internalHero, target);

            if (internalHero.belongings.weapon == null) utilItemCD = 0;
            else utilItemCD += Random.Float(1.4f, 1.9f);

            Random.shuffleWithChances(internalHero.utilItems(), Item::quantity);
            return;
        }

        if (item instanceof Wand) {
            int aimTarget = useWandForAttack((Wand) item);
            ((Wand) item).performZap(aimTarget, hero());

            if (internalHero.belongings.weapon == null) wandCD = 0;
            else wandCD += Random.Float(1.2f, 1.7f);

            Random.shuffleWithChances(internalHero.wands(), wand -> ((Wand) wand).curCharges);
            return;
        }

        if (item instanceof SpiritBow) {//TODO we ignore projecting enchantment
            ((SpiritBow) item).knockArrow().cast(internalHero, target);
            return;
        }
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        updateInternalStats();
        int ret = internalHero.attackProc(enemy, damage);
        updateStats();
        return ret;
    }

    @Override
    public int defenseProc(Char enemy, int damage) {
        if (surprisedBy(enemy)) Surprise.hit(this);
        updateInternalStats();
        int ret = internalHero.defenseProc(enemy, damage);
        updateStats();
        return ret;
    }

    @Override
    public void damage(int dmg, Object src) {

        //from super.damage()
        boolean bleedingCheck;
        if (isBossMob && !BossHealthBar.isAssigned(this)){
            BossHealthBar.addBoss( this );
            Dungeon.level.seal();
            bleedingCheck = (HP*2 <= HT);
        } else bleedingCheck = false;

        if (!isInvulnerable(src.getClass())) {
            if (state == SLEEPING) {
                state = WANDERING;
            }
            if (state != HUNTING && !(src instanceof Corruption)) {
                alerted = true;
            }
        }

        updateInternalStats();
        internalHero.superDamage(dmg, src);
        updateStats();

        if (isBossMob) {
            if ((HP * 2 <= HT) && !bleedingCheck) {
                bleeding = true;
                sprite.showStatus(CharSprite.NEGATIVE, Messages.get(this, "enraged"));
//                ((GooSprite) sprite).spray(true);
            }
            LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
            if (lock != null) {
                if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)) lock.addTime(dmg);
                else lock.addTime(dmg * 1.5f);
            }
        }
    }

    public void earnExp( int EXP, Class source ) {
        updateInternalStats();
        int exp = internalHero.lvl <= maxLvl ? EXP : 0;
        if (exp > 0 && sprite != null) {
            sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(exp), FloatingText.EXPERIENCE);
        }
        internalHero.earnExp(exp, source);
        if (internalHero.subClass == HeroSubClass.MONK){
            Buff.affect(internalHero, MonkEnergy.class).gainEnergy(this);
        }
        updateEXP();
        updateStats();
    }

    private void updateEXP() {
        EXP = (Hero.totalExp(internalHero.lvl) + internalHero.exp) / 3;
    }

    @Override
    public float stealth() {
        updateInternalStats();
        float ret = internalHero.stealth();
        updateStats();
        return ret;
    }

    @Override
    public boolean isActive() {
        updateInternalStats();
        boolean ret = internalHero.isActive();
        updateStats();
        return ret;
    }

    @Override
    public void move(int step, boolean travelling) {
        updateInternalStats();
        internalHero.moveNoSound(step, travelling);
        updateStats();
    }

    @Override
    public void onAttackComplete() {
        updateInternalStats();
        internalHero.onAttackComplete();
        updateStats();
    }

    @Override
    public boolean isImmune(Class effect) {
        if (internalHero == null) return false;
        updateInternalStats();
        boolean ret = internalHero.isImmune(effect);
        updateStats();
        return ret;
    }

    @Override
    public boolean isInvulnerable(Class effect) {
        if (internalHero == null) return false;
        updateInternalStats();
        boolean ret = internalHero.isInvulnerable(effect);
        updateStats();
        return ret;
    }

    public boolean isStarving() {
        return internalHero.isStarving();
    }

    @Override
    public void die(Object cause) {
        super.die(cause);
        if (directableAlly != null && !bindEquipment) {
            for (Item i : internalHero.belongings) {//also includes UnlimitedCapacityBags
                if (!(i instanceof Bag)) Dungeon.level.drop(i, pos).sprite.drop();
            }
        }
    }

    @Override
	public void spend(float time) {
        updateInternalStats();
        super.spend(time);
        internalHero.superSpend(time);
        updateStats();

        wandCD -= wandCD < 0 ? 0.1f : 1f;
        potionCD -= potionCD < 0 ? 0.1f : 1f;
        utilItemCD -= utilItemCD < 0 ? 0.1f : 1f;
    }

    public boolean killedMob(Object cause) {
        return cause == this || cause == internalHero || cause instanceof Item && internalHero.belongings.contains((Item) cause);
    }

    private void updateInternalStats() {
        updateStats(this, internalHero);
        internalHero.setEnemy(enemy);
        internalHero.resting = state == SLEEPING;
    }

    private void updateStats() {
        updateStats(internalHero, this);
        enemy = internalHero.enemy();
        if (internalHero.resting) state = SLEEPING;
    }

    private void updateStats(Char src, Char dest) {
        dest.HP = src.HP;
        dest.HT = src.HT;
        dest.paralysed = src.paralysed;
        dest.rooted = src.rooted;
        dest.setFlying(src.isFlying());
        dest.invisible = src.invisible;
        dest.viewDistance = src.viewDistance;
        dest.baseSpeed = src.baseSpeed;
        dest.attackSpeed = src.attackSpeed;
        dest.pos = src.pos;
        dest.alignment = src.alignment;
    }

    public void setInternalHero(InternalHero hero) {
        internalHero = hero;
        internalHero.alignment = alignment;
        internalHero.setOwner(this);
        updateStats();
        updateEXP();
    }

    public InternalHero hero() {
        return internalHero;
    }

    @Override
    public Weapon weapon() {
        return (Weapon) internalHero.belongings.weapon;
    }

    @Override
    public void weapon(Weapon weapon) {
        internalHero.belongings.weapon = weapon;
    }

    @Override
    public Armor armor() {
        return internalHero.belongings.armor;
    }

    @Override
    public void armor(Armor armor) {
        internalHero.belongings.armor = armor;
    }

    public void setHeroLvl(int lvl) {
        internalHero.setLvl(lvl);
        updateEXP();
    }

    private static final String INTERNAL_HERO = "internal_hero";
    private static final String BIND_EQUIPMENT = "bind_equipment";

    private static final String WANDS = "wands";
    private static final String POTIONS = "potions";
    private static final String UTIL_ITEMS = "util_items";
    private static final String WAIT_NEXT_TURN = "wait_next_turn";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(INTERNAL_HERO, internalHero);
        bundle.put(BIND_EQUIPMENT, bindEquipment);
        if (directableAlly != null) directableAlly.store(bundle);

        bundle.put(WANDS + "_cd", wandCD);
        bundle.put(POTIONS + "_cd", potionCD);
        bundle.put(UTIL_ITEMS + "_cd", utilItemCD);

        bundle.put(WAIT_NEXT_TURN, waitNextTurn);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        if (alignment == Alignment.ALLY) {
            directableAlly = new DirectableAlly(this);
            directableAlly.restore(bundle);
        }
        internalHero = null;
        super.restoreFromBundle(bundle);
        bindEquipment = bundle.getBoolean(BIND_EQUIPMENT);
        internalHero = (InternalHero) bundle.get(INTERNAL_HERO);
        internalHero.owner = this;

        wandCD = bundle.getFloat(WANDS + "_cd");
        potionCD = bundle.getFloat(POTIONS + "_cd");
        utilItemCD = bundle.getFloat(UTIL_ITEMS + "_cd");

        waitNextTurn = bundle.getBoolean(WAIT_NEXT_TURN);
    }

    @Override
    public void aggro(Char ch) {
        if (directableAlly != null) directableAlly.aggroOverride(ch);
        else super.aggro(ch);
    }

    @Override
    public void beckon(int cell) {
        if (directableAlly != null) directableAlly.beckonOverride(cell);
        else super.beckon(cell);
    }

    @Override
    public DirectableAlly getDirectableAlly() {
        return directableAlly;
    }

    public static class InternalHero extends Hero {

        public InternalHero() {
            super();
            belongings = new InternalHeroBelongings(this);
            alignment = Alignment.ENEMY;
        }

        private HeroMob owner;

        public void setOwner(HeroMob owner) {
            this.owner = owner;
            for (Buff b : super.buffs()) {
                moveBuffSilentlyToOtherChar_ACCESS_ONLY_FOR_HeroMob(b, owner);
            }
        }

        @Override
        public int attackSkill(Char target) {
            return (int) (super.attackSkill(target) * owner.statsScale);
        }

        @Override
        public int damageRoll() {
            return (int) (super.damageRoll() * owner.statsScale);
        }

        @Override
        public void damage(int dmg, Object src) {
            if (owner != null) owner.damage(dmg, src);
            else super.damage(dmg, src);
        }

        protected void superDamage(int dmg, Object src) {
            super.damage(dmg, src);
        }

        @Override
        public void die(Object cause) {
            owner.die(cause);
        }

        public void superDie(Object cause) {
            super.die(cause);
        }

        @Override
        public void spend(float time) {
            owner.spend(time);
        }

        public void superSpend(float time) {
            super.spend(time);
        }

        @Override
        public void next() {
            owner.next();
        }

        protected void setEnemy(Char enemy) {
            this.enemy = enemy;
        }

        @Override
        public synchronized <T extends Buff> T buff(Class<T> c) {
            if (owner == null) return super.buff(c);
            return owner.buff(c);
        }

        @Override
        public synchronized <T extends Buff> HashSet<T> buffs(Class<T> c) {
            if (owner == null) return super.buffs(c);
            return owner.buffs(c);
        }

        @Override
        public synchronized LinkedHashSet<Buff> buffs() {
            if (owner == null) return super.buffs();
            return owner.buffs();
        }

        @Override
        public boolean add(Buff buff) {
            if (super.add(buff)) {
                if (owner != null) moveBuffSilentlyToOtherChar_ACCESS_ONLY_FOR_HeroMob(buff, owner);
                return true;
            }
            return false;
        }

        @Override
        public HashSet<Property> properties() {
            if (owner == null) return super.properties();
            return owner.properties();
        }

        @Override
        public int id() {
            if (owner != null) return owner.id();
            return super.id();
        }

        @Override
        public void updateHT(boolean boostHP) {
            if (owner != null) owner.updateInternalStats();
            super.updateHT(boostHP);
            if (owner != null) owner.updateStats();
        }

        public List<Item> wands() {
            return ((InternalHeroBelongings) belongings).wands.items;
        }

        public List<Item> potions() {
            return ((InternalHeroBelongings) belongings).potions.items;
        }

        public List<Item> utilItems() {
            return ((InternalHeroBelongings) belongings).utilItems.items;
        }

        public static class InternalHeroBelongings extends Belongings {
            //Will occasionally use these items
            public Bag wands;//need to call stopCharging() and charge()!
            public Bag potions;
            public Bag utilItems;

            public InternalHeroBelongings(Hero owner) {
                super(owner);
                backpack.items.add(wands = new UnlimitedCapacityBag());
                backpack.items.add(potions = new UnlimitedCapacityBag());
                backpack.items.add(utilItems = new UnlimitedCapacityBag());
            }

            @Override
            public void storeInBundle(Bundle bundle) {
                super.storeInBundle(bundle);
            }

            @Override
            public void restoreFromBundle(Bundle bundle) {
                super.restoreFromBundle(bundle);
                if (!backpack.items.isEmpty()) {
                    wands = (Bag) backpack.items.get(0);
                    potions = (Bag) backpack.items.get(1);
                    utilItems = (Bag) backpack.items.get(2);
                } else {
                    backpack.items.add(wands = new UnlimitedCapacityBag());
                    backpack.items.add(potions = new UnlimitedCapacityBag());
                    backpack.items.add(utilItems = new UnlimitedCapacityBag());
                }
            }
        }
    }

    public Window mobInfoWindow() {
        if (directableAlly == null) return null;
        return new EditCompWindow(new EquipHeroMobComp(this));
    }

    private static class EquipHeroMobComp extends DefaultEditComp<HeroMob> {

        private StyledItemSelector mobWeapon, mobArmor;
        private StyledItemSelector mobRing, mobArti, mobMisc;
        private ItemContainer<Item> wands;
        private ItemContainer<Item> utilItems;
        private RedButton direct;

        private final Component[] rectComps, linearComps;

        public EquipHeroMobComp(HeroMob hero) {
            super(hero);

            if (!hero.bindEquipment) {

                mobWeapon = new HeroEqSelector(Messages.get(EditMobComp.class, "weapon"),
                        MeleeWeapon.class, hero.weapon(), new HeroEqSelector.Selector() {
                    @Override
                    public void actuallyOnSelectAfterConditions(Item item) {
                        mobWeapon.setSelectedItem(item);
                    }

                    @Override
                    public String textPrompt() {
                        return Messages.get(DriedRose.WndGhostHero.class, "weapon_prompt");
                    }

                    @Override
                    public boolean itemSelectable(Item item) {
                        return item instanceof MeleeWeapon;
                    }

                    @Override
                    public boolean passMoreConditions(Item item) {
                        if (((MeleeWeapon) item).STRReq() > hero.internalHero.STR) {
                            GLog.w(Messages.get(HeroMob.class, "cant_strength"));
                            return false;
                        }
                        return true;
                    }
                }) {
                    @Override
                    public void setSelectedItem(Item selectedItem) {

                        maybeDetachItem(selectedItem, hero.weapon());

                        super.setSelectedItem(selectedItem);
                        hero.weapon((Weapon) selectedItem);
                        EquipHeroMobComp.this.updateObj();
                    }
                };
                mobWeapon.setShowWhenNull(ItemSpriteSheet.WEAPON_HOLDER);
                add(mobWeapon);

                mobArmor = new HeroEqSelector(Messages.get(EditMobComp.class, "armor"),
                        Armor.class, hero.armor(), new HeroEqSelector.Selector() {
                    @Override
                    public void actuallyOnSelectAfterConditions(Item item) {
                        mobArmor.setSelectedItem(item);
                    }

                    @Override
                    public String textPrompt() {
                        return Messages.get(DriedRose.WndGhostHero.class, "armor_prompt");
                    }

                    @Override
                    public boolean itemSelectable(Item item) {
                        return item instanceof Armor;
                    }

                    @Override
                    public boolean passMoreConditions(Item item) {
                        if (((Armor) item).checkSeal() != null) {
                            GLog.w(Messages.get(HeroMob.class, "cant_unique"));
                            return false;
                        }
                        if (((Armor) item).STRReq() > hero.internalHero.STR) {
                            GLog.w(Messages.get(HeroMob.class, "cant_strength"));
                            return false;
                        }
                        return true;
                    }
                }) {
                    @Override
                    public void setSelectedItem(Item selectedItem) {

                        maybeDetachItem(selectedItem, hero.armor());

                        super.setSelectedItem(selectedItem);
                        hero.armor((Armor) selectedItem);
                        EquipHeroMobComp.this.updateObj();
                    }
                };
                mobArmor.setShowWhenNull(ItemSpriteSheet.ARMOR_HOLDER);
                add(mobArmor);

                Hero h = hero.internalHero;
                mobRing = new HeroEqSelector(Messages.get(HeroSettings.class, "ring"),
                        Ring.class, h.belongings.ring, new HeroEqSelector.Selector() {
                    @Override
                    public void actuallyOnSelectAfterConditions(Item item) {
                        mobRing.setSelectedItem(item);
                    }

                    @Override
                    public String textPrompt() {
                        return Messages.get(HeroMob.class, "ring_prompt");
                    }

                    @Override
                    public boolean itemSelectable(Item item) {
                        return item instanceof Ring;
                    }
                }) {
                    @Override
                    public void setSelectedItem(Item selectedItem) {

                        maybeDetachItem(selectedItem, h.belongings.ring);

                        super.setSelectedItem(selectedItem);
                        h.belongings.ring = (Ring) selectedItem;
                        EquipHeroMobComp.this.updateObj();
                    }
                };
                mobRing.setShowWhenNull(ItemSpriteSheet.RING_HOLDER);
                add(mobRing);

                mobArti = new HeroEqSelector(Messages.get(HeroSettings.class, "artifact"), Artifact.class, h.belongings.artifact, new HeroEqSelector.Selector() {
                    @Override
                    public void actuallyOnSelectAfterConditions(Item item) {
                        mobArti.setSelectedItem(item);
                    }

                    @Override
                    public String textPrompt() {
                        return Messages.get(HeroMob.class, "artifact_prompt");
                    }

                    @Override
                    public boolean itemSelectable(Item item) {
                        return item instanceof Artifact;
                    }
                }) {
                    @Override
                    public void setSelectedItem(Item selectedItem) {

                        maybeDetachItem(selectedItem, h.belongings.artifact);

                        super.setSelectedItem(selectedItem);
                        h.belongings.artifact = (Artifact) selectedItem;
                        EquipHeroMobComp.this.updateObj();
                    }
                };
                mobArti.setShowWhenNull(ItemSpriteSheet.ARTIFACT_HOLDER);
                add(mobArti);

                mobMisc = new HeroEqSelector(Messages.get(HeroSettings.class, "misc"), KindofMisc.class, h.belongings.misc, new HeroEqSelector.Selector() {
                    @Override
                    public void actuallyOnSelectAfterConditions(Item item) {
                        mobMisc.setSelectedItem(item);
                    }

                    @Override
                    public String textPrompt() {
                        return Messages.get(HeroMob.class, "misc_prompt");
                    }

                    @Override
                    public boolean itemSelectable(Item item) {
                        return item instanceof KindofMisc;
                    }
                }) {
                    @Override
                    public void setSelectedItem(Item selectedItem) {

                        maybeDetachItem(selectedItem, h.belongings.misc);

                        super.setSelectedItem(selectedItem);
                        h.belongings.misc = (KindofMisc) selectedItem;
                        EquipHeroMobComp.this.updateObj();
                    }
                };
                mobMisc.setShowWhenNull(ItemSpriteSheet.SOMETHING);
                add(mobMisc);

                wands = new ItemContainerWithLabel<Item>(hero.hero().wands(), this, Messages.get(HeroMob.class, "wands"), false, 0, 3) {
                    @Override
                    protected boolean removeSlot(ItemContainer<Item>.Slot slot) {
                        if (super.removeSlot(slot)) {
                            Wand wand = ((Wand) slot.item());
                            wand.stopCharging();
                            maybeDetachItem(null, wand);
                            return true;
                        }
                        return false;
                    }

                    protected void showSelectWindow() {
                        GameScene.selectItem(new WndBag.ItemSelector() {
                            @Override
                            public String textPrompt() {
                                return Messages.get(HeroMob.class, "wands_prompt");
                            }

                            @Override
                            public boolean itemSelectable(Item item) {
                                return item instanceof Wand;
                            }

                            @Override
                            public void onSelect(Item item) {
                                if (item instanceof Wand) {
                                    unequipItemFromDungeonHero(item);
                                    ((Wand) item).charge(hero.hero());
                                    addNewItem(item);
                                }
                            }
                        });
                    }

                    @Override
                    protected void onItemSlotClick(ItemContainer<Item>.Slot slot, Item item) {
                        GameScene.examineObject(item);
                    }
                };
                add(wands);

                List<Item> utilItemList = new ArrayList<>(hero.hero().potions());
                utilItemList.addAll(hero.hero().utilItems());
                utilItems = new ItemContainerWithLabel<Item>(utilItemList, this, Messages.get(HeroMob.class, "utils")) {
                    @Override
                    protected boolean removeSlot(ItemContainer<Item>.Slot slot) {
                        Item item = slot.item();
                        if (item.quantity() <= 1) {
                            if (!super.removeSlot(slot)) return false;
                        } else {
                            item = item.split(1);
                            slot.item(slot.item());
                        }
                        maybeDetachItem(null, item);
                        return true;
                    }

                    @Override
                    protected void doAddItem(Item item) {
                        if (item.stackable) {
                            for (Item i : itemList) {
                                if (item.isSimilar( i )) {
                                    i.merge( item );
                                    return;
                                }
                            }
                        }
                        super.doAddItem(item);
                    }

                    @Override
                    public synchronized void destroy() {
                        super.destroy();
                        hero.hero().potions().clear();
                        hero.hero().utilItems().clear();
                        for (Item i : utilItemList) {
                            if (i instanceof Potion) hero.hero().potions().add(((Potion) i));
                            else hero.hero().utilItems().add(i);
                        }
                    }

                    protected void showSelectWindow() {
                        GameScene.selectItem(new WndBag.ItemSelector() {
                            @Override
                            public String textPrompt() {
                                return Messages.get(HeroMob.class, "util_prompt");
                            }

                            @Override
                            public boolean itemSelectable(Item item) {
                                return item instanceof Potion && !(item instanceof PotionOfMastery) && !(item instanceof PotionOfDivineInspiration)
                                        || item instanceof MissileWeapon || item instanceof Bomb;
                            }

                            @Override
                            public void onSelect(Item item) {
                                if (itemSelectable(item)) {
                                    if (item.quantity() == 1) unequipItemFromDungeonHero(item);
                                    else {
                                        item = item.split(1);
                                        QuickSlotButton.refresh();
                                    }
                                    if (item instanceof MissileWeapon) ((MissileWeapon) item).resetParent();
                                    addNewItem(item);
                                }
                            }
                        });
                    }

                    @Override
                    protected void onItemSlotClick(ItemContainer<Item>.Slot slot, Item item) {
                        GameScene.examineObject(item);
                    }
                };
                add(utilItems);

            }

            direct = new RedButton(Messages.get(DriedRose.class, "ac_direct")) {
                @Override
                protected void onClick() {
                    EditorUtilities.getParentWindow(this).hide();
                    GameScene.selectCell(new CellSelector.Listener(){
                        @Override
                        public void onSelect(Integer cell) {
                            if (cell == null) return;
                            hero.directableAlly.directTocell(cell);
                        }

                        @Override
                        public String prompt() {
                            return  "\"" + Messages.get(DriedRose.GhostHero.class, "direct_prompt") + "\"";
                        }
                    });
                }
            };
            add(direct);

            rectComps = new Component[] {
                  mobWeapon, mobArmor, mobRing, mobArti, mobMisc
            };

            linearComps = new Component[] {
                    wands, utilItems, direct
            };
        }

        @Override
        protected void layout() {
            super.layout();
            layoutCompsInRectangles(rectComps);
            layoutCompsLinear(linearComps);
        }

        @Override
        public void updateObj() {
            if (mainTitleComp instanceof WndInfoMob.MobTitle) {

                ((HeroSprite.HeroMobSprite) ((WndInfoMob.MobTitle) mainTitleComp).image).updateHeroClass(obj.hero());

                ((WndInfoMob.MobTitle) mainTitleComp).setText(((WndInfoMob.MobTitle) mainTitleComp).createTitle(obj));
                mainTitleComp.setPos(mainTitleComp.left(), mainTitleComp.top());
            }

            if (mobWeapon != null) {
                mobWeapon.updateItem();
            }

            if (mobArmor != null) {
                mobArmor.updateItem();
            }

            ((HeroSprite.HeroMobSprite) obj.sprite).updateHeroClass(obj.hero());

            super.updateObj();
        }

        private void maybeDetachItem(Item newItem, Item item) {
            if (newItem != item && item != null) {
                if (!item.doPickUp(Dungeon.hero)) {
                    Dungeon.level.drop(item, Dungeon.hero.pos);
                } else {
                    QuickSlotButton.refresh();
                }
            }
        }

        @Override
        protected Component createTitle() {
            return new WndInfoMob.MobTitle(obj, Mimic.isLikeMob(obj));
        }

        @Override
        protected String createTitleText() {
            return null;
        }

        @Override
        protected String createDescription() {
            return obj.info();
        }

        @Override
        public Image getIcon() {
            return obj.createSprite();
        }

        private static class HeroEqSelector extends StyledItemSelector {

            private final WndBag.ItemSelector selector;

            public HeroEqSelector(String text, Class<? extends Item> itemClasses, Item startItem, WndBag.ItemSelector selector) {
                super(text, itemClasses, startItem, null);
                this.selector = selector;
            }

            private static abstract class Selector extends WndBag.ItemSelector {

                public abstract void actuallyOnSelectAfterConditions(Item item);

                @Override
                public Class<?extends Bag> preferredBag(){
                    return Belongings.Backpack.class;
                }

                public boolean passMoreConditions(Item item) {
                    return true;
                }

                @Override
                public void onSelect(Item item) {

                    if (item == null) {
                        actuallyOnSelectAfterConditions(item);
                        return;
                    }

                    if (!itemSelectable(item)) {
                        //do nothing, should only happen when window is cancelled
                        return;
                    }

                    if (item.unique) {
                        GLog.w( Messages.get(HeroMob.class, "cant_unique"));
                        return;
                    }

                    if (!item.isIdentified()) {
                        GLog.w( Messages.get(DriedRose.WndGhostHero.class, "cant_unidentified"));
                        return;
                    }

                    if (item.cursed) {
                        GLog.w( Messages.get(HeroMob.class, "cant_cursed"));
                        return;
                    }

                    if (!passMoreConditions(item)) {
                        return;
                    }

                    unequipItemFromDungeonHero(item);

                    actuallyOnSelectAfterConditions(item);

                }
            }

            @Override
            public void change() {
                GameScene.selectItem(selector);
            }

            @Override
            protected void onItemSlotClick() {
                GameScene.examineObject(getSelectedItem());
            }

            @Override
            protected boolean onItemSlotLongClick() {
                selector.onSelect(null);
                return true;
            }
        }

        static void unequipItemFromDungeonHero(Item item) {
            if (item instanceof EquipableItem && item.isEquipped(Dungeon.hero)) {
                ((EquipableItem) item).doUnequip(Dungeon.hero, false, false);
            } else {
                item.detach(Dungeon.hero.belongings.backpack);
            }
            QuickSlotButton.refresh();
        }
    }

}
