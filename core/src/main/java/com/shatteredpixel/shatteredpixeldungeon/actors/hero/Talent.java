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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.SandboxPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArtifactRecharge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CounterBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.EnhancedRings;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Haste;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.PhysicalEmpower;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.RevealedArea;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ScrollEmpower;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.WandEmpower;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.Ratmogrify;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.GuidingLight;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.RecallGlyph;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.HeroMob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClothArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HornOfPlenty;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.Runestone;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ShardOfOblivion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gloves;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

public enum Talent {

	//Warrior T1
	HEARTY_MEAL(0), VETERANS_INTUITION(1), PROVOKED_ANGER(2), IRON_WILL(3),
	//Warrior T2
	IRON_STOMACH(4), LIQUID_WILLPOWER(5), RUNIC_TRANSFERENCE(6), LETHAL_MOMENTUM(7), IMPROVISED_PROJECTILES(8),
	//Warrior T3
	HOLD_FAST(9, 3), STRONGMAN(10, 3),
	//Berserker T3
	ENDLESS_RAGE(11, 3), DEATHLESS_FURY(12, 3), ENRAGED_CATALYST(13, 3),
	//Gladiator T3
	CLEAVE(14, 3), LETHAL_DEFENSE(15, 3), ENHANCED_COMBO(16, 3),
	//Heroic Leap T4
	BODY_SLAM(17, 4), IMPACT_WAVE(18, 4), DOUBLE_JUMP(19, 4),
	//Shockwave T4
	EXPANDING_WAVE(20, 4), STRIKING_WAVE(21, 4), SHOCK_FORCE(22, 4),
	//Endure T4
	SUSTAINED_RETRIBUTION(23, 4), SHRUG_IT_OFF(24, 4), EVEN_THE_ODDS(25, 4),

	//Mage T1
	EMPOWERING_MEAL(32), SCHOLARS_INTUITION(33), LINGERING_MAGIC(34), BACKUP_BARRIER(35),
	//Mage T2
	ENERGIZING_MEAL(36), INSCRIBED_POWER(37), WAND_PRESERVATION(38), ARCANE_VISION(39), SHIELD_BATTERY(40),
	//Mage T3
	DESPERATE_POWER(41, 3), ALLY_WARP(42, 3),
	//Battlemage T3
	EMPOWERED_STRIKE(43, 3), MYSTICAL_CHARGE(44, 3), EXCESS_CHARGE(45, 3),
	//Warlock T3
	SOUL_EATER(46, 3), SOUL_SIPHON(47, 3), NECROMANCERS_MINIONS(48, 3),
	//Elemental Blast T4
	BLAST_RADIUS(49, 4), ELEMENTAL_POWER(50, 4), REACTIVE_BARRIER(51, 4),
	//Wild Magic T4
	WILD_POWER(52, 4), FIRE_EVERYTHING(53, 4), CONSERVED_MAGIC(54, 4),
	//Warp Beacon T4
	TELEFRAG(55, 4), REMOTE_BEACON(56, 4), LONGRANGE_WARP(57, 4),

	//Rogue T1
	CACHED_RATIONS(64), THIEFS_INTUITION(65), SUCKER_PUNCH(66), PROTECTIVE_SHADOWS(67),
	//Rogue T2
	MYSTICAL_MEAL(68), INSCRIBED_STEALTH(69), WIDE_SEARCH(70), SILENT_STEPS(71), ROGUES_FORESIGHT(72),
	//Rogue T3
	ENHANCED_RINGS(73, 3), LIGHT_CLOAK(74, 3),
	//Assassin T3
	ENHANCED_LETHALITY(75, 3), ASSASSINS_REACH(76, 3), BOUNTY_HUNTER(77, 3),
	//Freerunner T3
	EVASIVE_ARMOR(78, 3), PROJECTILE_MOMENTUM(79, 3), SPEEDY_STEALTH(80, 3),
	//Smoke Bomb T4
	HASTY_RETREAT(81, 4), BODY_REPLACEMENT(82, 4), SHADOW_STEP(83, 4),
	//Death Mark T4
	FEAR_THE_REAPER(84, 4), DEATHLY_DURABILITY(85, 4), DOUBLE_MARK(86, 4),
	//Shadow Clone T4
	SHADOW_BLADE(87, 4), CLONED_ARMOR(88, 4), PERFECT_COPY(89, 4),

	//Huntress T1
	NATURES_BOUNTY(96), SURVIVALISTS_INTUITION(97), FOLLOWUP_STRIKE(98), NATURES_AID(99),
	//Huntress T2
	INVIGORATING_MEAL(100), LIQUID_NATURE(101), REJUVENATING_STEPS(102), HEIGHTENED_SENSES(103), DURABLE_PROJECTILES(104),
	//Huntress T3
	POINT_BLANK(105, 3), SEER_SHOT(106, 3),
	//Sniper T3
	FARSIGHT(107, 3), SHARED_ENCHANTMENT(108, 3), SHARED_UPGRADES(109, 3),
	//Warden T3
	DURABLE_TIPS(110, 3), BARKSKIN(111, 3), SHIELDING_DEW(112, 3),
	//Spectral Blades T4
	FAN_OF_BLADES(113, 4), PROJECTING_BLADES(114, 4), SPIRIT_BLADES(115, 4),
	//Natures Power T4
	GROWING_POWER(116, 4), NATURES_WRATH(117, 4), WILD_MOMENTUM(118, 4),
	//Spirit Hawk T4
	EAGLE_EYE(119, 4), GO_FOR_THE_EYES(120, 4), SWIFT_SPIRIT(121, 4),

	//Duelist T1
	STRENGTHENING_MEAL(128), ADVENTURERS_INTUITION(129), PATIENT_STRIKE(130), AGGRESSIVE_BARRIER(131),
	//Duelist T2
	FOCUSED_MEAL(132), LIQUID_AGILITY(133), WEAPON_RECHARGING(134), LETHAL_HASTE(135), SWIFT_EQUIP(136),
	//Duelist T3
	PRECISE_ASSAULT(137, 3), DEADLY_FOLLOWUP(138, 3),
	//Champion T3
	VARIED_CHARGE(139, 3), TWIN_UPGRADES(140, 3), COMBINED_LETHALITY(141, 3),
	//Monk T3
	UNENCUMBERED_SPIRIT(142, 3), MONASTIC_VIGOR(143, 3), COMBINED_ENERGY(144, 3),
	//Challenge T4
	CLOSE_THE_GAP(145, 4), INVIGORATING_VICTORY(146, 4), ELIMINATION_MATCH(147, 4),
	//Elemental Strike T4
	ELEMENTAL_REACH(148, 4), STRIKING_FORCE(149, 4), DIRECTED_POWER(150, 4),
	//Duelist A3 T4
	FEIGNED_RETREAT(151, 4), EXPOSE_WEAKNESS(152, 4), COUNTER_ABILITY(153, 4),

	//Cleric T1
	SATIATED_SPELLS(160), DETECT_CURSE(161), SEARING_LIGHT(162), SHIELD_OF_LIGHT(163),
	//Cleric T2
	ENLIGHTENING_MEAL(164), RECALL_GLYPH(165), SUNRAY(166), DIVINE_SENSE(167), CLERICT2E(168),
	//Cleric T3
	CLERICT3A(169, 3), CLERICT3B(170, 3),
	//Priest T3
	PRIESTT3A(171, 3), PRIESTT3B(172, 3), PRIESTT3C(173, 3),
	//Paladin T3
	PALADINT3A(174, 3), PALADINT3B(175, 3), PALADINT3C(176, 3),

	//universal T4
	HEROIC_ENERGY(26, 4), //See icon() and title() for special logic for this one
	//Ratmogrify T4
	RATSISTANCE(215, 4), RATLOMACY(216, 4), RATFORCEMENTS(217, 4);

	public static class ImprovisedProjectileCooldown extends FlavourBuff {
		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0.15f, 0.2f, 0.5f); }
		public float iconFadePercent() { return Math.max(0, visualcooldown() / 50); }
	}
	public static class LethalMomentumTracker extends FlavourBuff{}
	public static class StrikingWaveTracker extends FlavourBuff{}
	public static class WandPreservationCounter extends CounterBuff {{revivePersists = true;}}
	public static class EmpoweredStrikeTracker extends FlavourBuff{
		//blast wave on-hit doesn't resolve instantly, so we delay detaching for it
		public boolean delayedDetach = false;
	}
	public static class ProtectiveShadowsTracker extends Buff {
		float barrierInc = 0.5f;

		@Override
		public boolean act() {
			Hero hero = target instanceof Hero ? (Hero) target : ((HeroMob) target).hero();
			//barrier every 2/1 turns, to a max of 3/5
			if (hero.hasTalent(Talent.PROTECTIVE_SHADOWS) && target.invisible > 0){
				Barrier barrier = Buff.affect(target, Barrier.class);
				if (barrier.shielding() < 1 + 2*hero.pointsInTalent(Talent.PROTECTIVE_SHADOWS)) {
					barrierInc += 0.5f * hero.pointsInTalent(Talent.PROTECTIVE_SHADOWS);
				}
				if (barrierInc >= 1){
					barrierInc = 0;
					barrier.incShield(1);
				} else {
					barrier.incShield(0); //resets barrier decay
				}
			} else {
				detach();
			}
			spend( TICK );
			return true;
		}

		private static final String BARRIER_INC = "barrier_inc";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put( BARRIER_INC, barrierInc);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			barrierInc = bundle.getFloat( BARRIER_INC );
		}
	}
	public static class BountyHunterTracker extends FlavourBuff{};
	public static class RejuvenatingStepsCooldown extends FlavourBuff{
		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0f, 0.35f, 0.15f); }
		public float iconFadePercent() { return GameMath.gate(0, visualcooldown() / (15 - 5*Dungeon.hero.pointsInTalent(REJUVENATING_STEPS)), 1); }
	};
	public static class RejuvenatingStepsFurrow extends CounterBuff{{revivePersists = true;}};
	public static class SeerShotCooldown extends FlavourBuff{
		public int icon() { return target.buff(RevealedArea.class) != null ? BuffIndicator.NONE : BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0.7f, 0.4f, 0.7f); }
		public float iconFadePercent() { return Math.max(0, visualcooldown() / 20); }
	};
	public static class SpiritBladesTracker extends FlavourBuff{};
	public static class PatientStrikeTracker extends Buff {
		public int pos;
		{ type = Buff.buffType.POSITIVE; }
		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0.5f, 0f, 1f); }
		@Override
		public boolean act() {
			if (pos != target.pos) {
				detach();
			} else {
				spend(TICK);
			}
			return true;
		}
		private static final String POS = "pos";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(POS, pos);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			pos = bundle.getInt(POS);
		}
	};
	public static class AggressiveBarrierCooldown extends FlavourBuff{
		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0.35f, 0f, 0.7f); }
		public float iconFadePercent() { return Math.max(0, visualcooldown() / 50); }
	};
	public static class LiquidAgilEVATracker extends FlavourBuff{};
	public static class LiquidAgilACCTracker extends FlavourBuff{
		public int uses;

		{ type = buffType.POSITIVE; }
		public int icon() { return BuffIndicator.INVERT_MARK; }
		public void tintIcon(Image icon) { icon.hardlight(0.5f, 0f, 1f); }
		public float iconFadePercent() { return Math.max(0, 1f - (visualcooldown() / 5)); }

		private static final String USES = "uses";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(USES, uses);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			uses = bundle.getInt(USES);
		}
	};
	public static class LethalHasteCooldown extends FlavourBuff{
		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0.35f, 0f, 0.7f); }
		public float iconFadePercent() { return Math.max(0, visualcooldown() / 100); }
	};
	public static class SwiftEquipCooldown extends FlavourBuff{
		public boolean secondUse;
		public boolean hasSecondUse(){
			return secondUse;
		}

		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) {
			if (hasSecondUse()) icon.hardlight(0.85f, 0f, 1.0f);
			else                icon.hardlight(0.35f, 0f, 0.7f);
		}
		public float iconFadePercent() { return GameMath.gate(0, visualcooldown() / 20f, 1); }

		private static final String SECOND_USE = "second_use";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(SECOND_USE, secondUse);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			secondUse = bundle.getBoolean(SECOND_USE);
		}
	};
	public static class DeadlyFollowupTracker extends FlavourBuff{
		public int object;
		{ type = Buff.buffType.POSITIVE; }
		public int icon() { return BuffIndicator.INVERT_MARK; }
		public void tintIcon(Image icon) { icon.hardlight(0.5f, 0f, 1f); }
		public float iconFadePercent() { return Math.max(0, 1f - (visualcooldown() / 5)); }
		private static final String OBJECT    = "object";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(OBJECT, object);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			object = bundle.getInt(OBJECT);
		}
	}
	public static class PreciseAssaultTracker extends FlavourBuff{
		{ type = buffType.POSITIVE; }
		public int icon() { return BuffIndicator.INVERT_MARK; }
		public void tintIcon(Image icon) { icon.hardlight(1f, 1f, 0.0f); }
		public float iconFadePercent() { return Math.max(0, 1f - (visualcooldown() / 5)); }
	};
	public static class VariedChargeTracker extends Buff{
		public Class weapon;

		private static final String WEAPON    = "weapon";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(WEAPON, weapon);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			weapon = bundle.getClass(WEAPON);
		}
	}
	public static class CombinedLethalityAbilityTracker extends FlavourBuff{
		public MeleeWeapon weapon;
	};
	public static class CombinedEnergyAbilityTracker extends FlavourBuff{
		public boolean monkAbilused = false;
		public boolean wepAbilUsed = false;

		private static final String MONK_ABIL_USED  = "monk_abil_used";
		private static final String WEP_ABIL_USED   = "wep_abil_used";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(MONK_ABIL_USED, monkAbilused);
			bundle.put(WEP_ABIL_USED, wepAbilUsed);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			monkAbilused = bundle.getBoolean(MONK_ABIL_USED);
			wepAbilUsed = bundle.getBoolean(WEP_ABIL_USED);
		}
	}
	public static class CounterAbilityTacker extends FlavourBuff{};
	public static class SatiatedSpellsTracker extends Buff{
		@Override
		public int icon() {
			return BuffIndicator.SPELL_FOOD;
		}
	};

	int icon;
	int maxPoints;

	// tiers 1/2/3/4 start at levels 2/7/13/21
	public static int[] tierLevelThresholds = new int[]{0, 2, 7, 13, 21, 31};

	Talent( int icon ){
		this(icon, 2);
	}

	Talent( int icon, int maxPoints ){
		this.icon = icon;
		this.maxPoints = maxPoints;
	}

	public int icon(){
		if (this == HEROIC_ENERGY){
			if (Ratmogrify.useRatroicEnergy){
				return 218;
			}
			HeroClass cls = Dungeon.hero != null ? Dungeon.hero.heroClass : GamesInProgress.selectedClass;
			switch (cls){
				case WARRIOR: default:
					return 26;
				case MAGE:
					return 58;
				case ROGUE:
					return 90;
				case HUNTRESS:
					return 122;
				case DUELIST:
					return 154;
				case CLERIC:
					return 186;
			}
		} else {
			return icon;
		}
	}

	public int maxPoints(){
		return maxPoints;
	}

	public String title(){
		if (this == HEROIC_ENERGY && Ratmogrify.useRatroicEnergy){
			return Messages.get(this, name() + ".rat_title");
		}
		return Messages.get(this, name() + ".title");
	}

	public final String desc(){
		return desc(false);
	}

	public String desc(boolean metamorphed){
		if (metamorphed){
			String metaDesc = Messages.get(this, name() + ".meta_desc");
			if (!metaDesc.equals(Messages.NO_TEXT_FOUND)){
				return Messages.get(this, name() + ".desc") + "\n\n" + metaDesc;
			}
		}
		return Messages.get(this, name() + ".desc");
	}

	public static void onTalentUpgraded( Hero hero, Talent talent ){
		//for metamorphosis
		if (talent == IRON_WILL && hero.heroClass != HeroClass.WARRIOR){
			Buff.affect(hero, BrokenSeal.WarriorShield.class);
		}

		if (talent == VETERANS_INTUITION && hero.pointsInTalent(VETERANS_INTUITION) >= 2){
			if (hero.belongings.armor() != null && !ShardOfOblivion.passiveIDDisabled())  {
				hero.belongings.armor.identify();
			}
		}
		if (talent == THIEFS_INTUITION && hero.pointsInTalent(THIEFS_INTUITION) >= 2){
			if (hero.belongings.ring instanceof Ring && !ShardOfOblivion.passiveIDDisabled()) {
				hero.belongings.ring.identify();
			}
			if (hero.belongings.misc instanceof Ring && !ShardOfOblivion.passiveIDDisabled()) {
				hero.belongings.misc.identify();
			}
			for (Item item : Dungeon.hero.belongings){
				if (item instanceof Ring){
					((Ring) item).setKnown();
				}
			}
		}
		if (talent == THIEFS_INTUITION && hero.pointsInTalent(THIEFS_INTUITION) == 1){
			if (hero.belongings.ring instanceof Ring) hero.belongings.ring.setKnown();
			if (hero.belongings.misc instanceof Ring) ((Ring) hero.belongings.misc).setKnown();
		}
		if (talent == ADVENTURERS_INTUITION && hero.pointsInTalent(ADVENTURERS_INTUITION) >= 2){
			if (hero.belongings.weapon() != null && !ShardOfOblivion.passiveIDDisabled()){
				hero.belongings.weapon().identify();
			}
		}

		if (talent == PROTECTIVE_SHADOWS && hero.invisible > 0){
			Buff.affect(hero, Talent.ProtectiveShadowsTracker.class);
		}

		if (talent == LIGHT_CLOAK && hero.heroClass == HeroClass.ROGUE){
			for (Item item : Dungeon.hero.belongings.backpack){
				if (item instanceof CloakOfShadows){
					if (!hero.belongings.lostInventory() || item.keptThroughLostInventory()) {
						((CloakOfShadows) item).activate(Dungeon.hero);
					}
				}
			}
		}

		if (talent == HEIGHTENED_SENSES || talent == FARSIGHT || talent == DIVINE_SENSE){
			Dungeon.observe();
		}

		if (talent == TWIN_UPGRADES || talent == DESPERATE_POWER || talent == STRONGMAN){
			Item.updateQuickslot();
		}

		if (talent == UNENCUMBERED_SPIRIT && hero.pointsInTalent(talent) == 3){
			Item toGive = new ClothArmor().identify();
			if (!toGive.collect()){
				Dungeon.level.drop(toGive, hero.pos).sprite.drop();
			}
			toGive = new Gloves().identify();
			if (!toGive.collect()){
				Dungeon.level.drop(toGive, hero.pos).sprite.drop();
			}
		}
	}

	public static class CachedRationsDropped extends CounterBuff{{revivePersists = true;}};
	public static class NatureBerriesDropped extends CounterBuff{{revivePersists = true;}};

	public static void onFoodEaten( Hero hero, float foodVal, Item foodSource ){
		if (hero.hasTalent(HEARTY_MEAL)){
			//3/5 HP healed, when hero is below 30% health
			if (hero.HP/(float)hero.HT <= 0.3f) {
				int healing = 1 + 2 * hero.pointsInTalent(HEARTY_MEAL);
				hero.HP = Math.min(hero.HP + healing, hero.HT);
				hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(healing), FloatingText.HEALING);

			}
		}
		if (hero.hasTalent(IRON_STOMACH)){
			if (hero.cooldown() > 0) {
				Buff.affect(hero, WarriorFoodImmunity.class, hero.cooldown());
			}
		}
		if (hero.hasTalent(EMPOWERING_MEAL)){
			//2/3 bonus wand damage for next 3 zaps
			Buff.affect( hero, WandEmpower.class).set(1 + hero.pointsInTalent(EMPOWERING_MEAL), 3);
			ScrollOfRecharging.charge( hero );
		}
		if (hero.hasTalent(ENERGIZING_MEAL)){
			//5/8 turns of recharging
			Buff.prolong( hero, Recharging.class, 2 + 3*(hero.pointsInTalent(ENERGIZING_MEAL)) );
			ScrollOfRecharging.charge( hero );
			SpellSprite.show(hero, SpellSprite.CHARGE);
		}
		if (hero.hasTalent(MYSTICAL_MEAL)){
			//3/5 turns of recharging
			ArtifactRecharge buff = Buff.affect( hero, ArtifactRecharge.class);
			if (buff.left() < 1 + 2*(hero.pointsInTalent(MYSTICAL_MEAL))){
				Buff.affect( hero, ArtifactRecharge.class).set(1 + 2*(hero.pointsInTalent(MYSTICAL_MEAL))).ignoreHornOfPlenty = foodSource instanceof HornOfPlenty;
			}
			ScrollOfRecharging.charge( hero );
			SpellSprite.show(hero, SpellSprite.CHARGE, 0, 1, 1);
		}
		if (hero.hasTalent(INVIGORATING_MEAL)){
			//effectively 1/2 turns of haste
			Buff.prolong( hero, Haste.class, 0.67f+hero.pointsInTalent(INVIGORATING_MEAL));
		}
		if (hero.hasTalent(STRENGTHENING_MEAL)){
			//3 bonus physical damage for next 2/3 attacks
			Buff.affect( hero, PhysicalEmpower.class).set(3, 1 + hero.pointsInTalent(STRENGTHENING_MEAL));
		}
		if (hero.hasTalent(FOCUSED_MEAL)){
			if (hero.heroClass == HeroClass.DUELIST){
				//0.67/1 charge for the duelist
				Buff.affect( hero, MeleeWeapon.Charger.class ).gainCharge((hero.pointsInTalent(FOCUSED_MEAL)+1)/3f);
				ScrollOfRecharging.charge( hero );
			} else {
				// lvl/3 / lvl/2 bonus dmg on next hit for other classes
				Buff.affect( hero, PhysicalEmpower.class).set(Math.round(hero.lvl / (4f - hero.pointsInTalent(FOCUSED_MEAL))), 1);
			}
		}
		if (hero.hasTalent(SATIATED_SPELLS)){
			Buff.affect( hero, SatiatedSpellsTracker.class );
		}
		if (hero.hasTalent(ENLIGHTENING_MEAL)){
			HolyTome tome = hero.belongings.getItem(HolyTome.class);
			if (tome != null) {
				tome.directCharge( 0.5f * (1+hero.pointsInTalent(ENLIGHTENING_MEAL)));
				ScrollOfRecharging.charge(hero);
			}
		}
	}

	public static class WarriorFoodImmunity extends FlavourBuff{
		{ actPriority = HERO_PRIO+1; }
	}

	public static float itemIDSpeedFactor( Hero hero, Item item ){
		// 1.75x/2.5x speed with Huntress talent
		float factor = 1f + 0.75f*hero.pointsInTalent(SURVIVALISTS_INTUITION);

		// Affected by both Warrior(1.75x/2.5x) and Duelist(2.5x/inst.) talents
		if (item instanceof MeleeWeapon){
			factor *= 1f + 1.5f*hero.pointsInTalent(ADVENTURERS_INTUITION); //instant at +2 (see onItemEquipped)
			factor *= 1f + 0.75f*hero.pointsInTalent(VETERANS_INTUITION);
		}
		// Affected by both Warrior(2.5x/inst.) and Duelist(1.75x/2.5x) talents
		if (item instanceof Armor){
			factor *= 1f + 0.75f*hero.pointsInTalent(ADVENTURERS_INTUITION);
			factor *= 1f + hero.pointsInTalent(VETERANS_INTUITION); //instant at +2 (see onItemEquipped)
		}
		// 3x/instant for Mage (see Wand.wandUsed())
		if (item instanceof Wand){
			factor *= 1f + 2.0f*hero.pointsInTalent(SCHOLARS_INTUITION);
		}
		// 2x/instant for Rogue (see onItemEqupped), also id's type on equip/on pickup
		if (item instanceof Ring){
			factor *= 1f + hero.pointsInTalent(THIEFS_INTUITION);
		}
		return factor;
	}

	public static void onPotionUsed( Hero hero, int cell, float factor ){
		if (hero.hasTalent(LIQUID_WILLPOWER)){
			if (hero.heroClass == HeroClass.WARRIOR) {
				BrokenSeal.WarriorShield shield = hero.buff(BrokenSeal.WarriorShield.class);
				if (shield != null) {
					// 50/75% of total shield
					int shieldToGive = Math.round(factor * shield.maxShield() * 0.25f * (1 + hero.pointsInTalent(LIQUID_WILLPOWER)));
					hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(shieldToGive), FloatingText.SHIELDING);
					shield.supercharge(shieldToGive);
				}
			} else {
				// 5/7.5% of max HP
				int shieldToGive = Math.round( factor * hero.HT * (0.025f * (1+hero.pointsInTalent(LIQUID_WILLPOWER))));
				hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(shieldToGive), FloatingText.SHIELDING);
				Buff.affect(hero, Barrier.class).setShield(shieldToGive);
			}
		}
		if (hero.hasTalent(LIQUID_NATURE)){
			ArrayList<Integer> grassCells = new ArrayList<>();
			for (int i : PathFinder.NEIGHBOURS9){
				grassCells.add(cell+i);
			}
			Random.shuffle(grassCells);
			for (int grassCell : grassCells){
				Char ch = Actor.findChar(grassCell);
				if (ch != null && ch.alignment == Char.Alignment.ENEMY){
					//1/2 turns of roots
					Buff.affect(ch, Roots.class, factor * hero.pointsInTalent(LIQUID_NATURE));
				}
				if (Dungeon.level.map[grassCell] == Terrain.EMPTY ||
						Dungeon.level.map[grassCell] == Terrain.EMBERS ||
						Dungeon.level.map[grassCell] == Terrain.EMPTY_DECO){
					Level.set(grassCell, Terrain.GRASS);
					GameScene.updateMap(grassCell);
				}
				CellEmitter.get(grassCell).burst(LeafParticle.LEVEL_SPECIFIC, 4);
			}
			// 4/6 cells total
			int totalGrassCells = (int) (factor * (2 + 2 * hero.pointsInTalent(LIQUID_NATURE)));
			while (grassCells.size() > totalGrassCells){
				grassCells.remove(0);
			}
			for (int grassCell : grassCells){
				int t = Dungeon.level.map[grassCell];
				if ((t == Terrain.EMPTY || t == Terrain.EMPTY_DECO || t == Terrain.EMBERS
						|| t == Terrain.GRASS || t == Terrain.FURROWED_GRASS)
						&& Dungeon.level.plants.get(grassCell) == null){
					Level.set(grassCell, Terrain.HIGH_GRASS);
					GameScene.updateMap(grassCell);
				}
			}
			Dungeon.observe();
		}
		if (hero.hasTalent(LIQUID_AGILITY)){
			Buff.prolong(hero, LiquidAgilEVATracker.class, hero.cooldown() + Math.max(0, factor-1));
			if (factor >= 0.5f){
				Buff.prolong(hero, LiquidAgilACCTracker.class, 5f).uses = Math.round(factor);
			}
		}
	}

	public static void onScrollUsed( Hero hero, int pos, float factor, Class<?extends Item> cls ){
		if (hero.hasTalent(INSCRIBED_POWER)){
			// 2/3 empowered wand zaps
			Buff.affect(hero, ScrollEmpower.class).reset((int) (factor * (1 + hero.pointsInTalent(INSCRIBED_POWER))));
		}
		if (hero.hasTalent(INSCRIBED_STEALTH)){
			// 3/5 turns of stealth
			Buff.affect(hero, Invisibility.class, factor * (1 + 2*hero.pointsInTalent(INSCRIBED_STEALTH)));
			Sample.INSTANCE.play( Assets.Sounds.MELD );
		}
		if (hero.heroClass == HeroClass.CLERIC
				&& hero.hasTalent(RECALL_GLYPH)
				&& Scroll.class.isAssignableFrom(cls)){
			Buff.affect(hero, RecallGlyph.UsedGlyphTracker.class, hero.pointsInTalent(RECALL_GLYPH) == 2 ? 300 : 10).item = cls;
		}
	}

	public static void onRunestoneUsed( Hero hero, int pos, Class<?extends Item> cls ){
		if (hero.heroClass == HeroClass.CLERIC
				&& hero.hasTalent(RECALL_GLYPH)
				&& Runestone.class.isAssignableFrom(cls)){
			Buff.affect(hero, RecallGlyph.UsedGlyphTracker.class, hero.pointsInTalent(RECALL_GLYPH) == 2 ? 300 : 10).item = cls;
		}
	}

	public static void onArtifactUsed( Hero hero ){
		if (hero.hasTalent(ENHANCED_RINGS)){
			Buff.prolong(hero, EnhancedRings.class, 3f*hero.pointsInTalent(ENHANCED_RINGS));
		}
	}

	public static void onItemEquipped( Hero hero, Item item ){
		boolean identify = false;
		if (hero.pointsInTalent(VETERANS_INTUITION) >= 2 && item instanceof Armor){
			identify = true;
		}
		if (hero.hasTalent(THIEFS_INTUITION) && item instanceof Ring){
			if (hero.pointsInTalent(THIEFS_INTUITION) >= 2){
				identify = true;
			}
			((Ring) item).setKnown();
		}
		if (hero.pointsInTalent(ADVENTURERS_INTUITION) >= 2 && item instanceof Weapon){
			identify = true;
		}

		if (identify && !ShardOfOblivion.passiveIDDisabled()){
			item.identify();
		}
	}

	public static void onItemCollected( Hero hero, Item item ){
		if (hero.pointsInTalent(THIEFS_INTUITION) >= 2){
			if (item instanceof Ring) ((Ring) item).setKnown();
		}
	}

	//note that IDing can happen in alchemy scene, so be careful with VFX here
	public static void onItemIdentified( Hero hero, Item item ){
		//currently no talents that trigger here, it wasn't a very popular trigger =(
	}

	public static int onAttackProc( Hero hero, Char enemy, int dmg ){

		if (hero.hasTalent(Talent.PROVOKED_ANGER)
			&& hero.buff(ProvokedAngerTracker.class) != null){
			dmg += 1 + hero.pointsInTalent(Talent.PROVOKED_ANGER);
			hero.buff(ProvokedAngerTracker.class).detach();
		}

		if (hero.hasTalent(Talent.LINGERING_MAGIC)
				&& hero.buff(LingeringMagicTracker.class) != null){
			dmg += Random.IntRange(hero.pointsInTalent(Talent.LINGERING_MAGIC) , 2);
			hero.buff(LingeringMagicTracker.class).detach();
		}

		if (hero.hasTalent(Talent.SUCKER_PUNCH)
				&& enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero)
				&& enemy.buff(SuckerPunchTracker.class) == null){
			dmg += Random.IntRange(hero.pointsInTalent(Talent.SUCKER_PUNCH) , 2);
			Buff.affect(enemy, SuckerPunchTracker.class);
		}

		if (hero.hasTalent(Talent.FOLLOWUP_STRIKE) && enemy.isAlive() && enemy.alignment == Char.Alignment.ENEMY) {
			if (hero.belongings.attackingWeapon() instanceof MissileWeapon) {
				Buff.prolong(hero, FollowupStrikeTracker.class, 5f).object = enemy.id();
			} else if (hero.buff(FollowupStrikeTracker.class) != null
					&& hero.buff(FollowupStrikeTracker.class).object == enemy.id()){
				dmg += 1 + hero.pointsInTalent(FOLLOWUP_STRIKE);
				hero.buff(FollowupStrikeTracker.class).detach();
			}
		}

		if (hero.buff(Talent.SpiritBladesTracker.class) != null
				&& Random.Int(10) < 3*hero.pointsInTalent(Talent.SPIRIT_BLADES)){
			SpiritBow bow = hero.belongings.getItem(SpiritBow.class);
			if (bow != null) dmg = bow.proc( hero, enemy, dmg );
			hero.buff(Talent.SpiritBladesTracker.class).detach();
		}

		if (hero.hasTalent(PATIENT_STRIKE)){
			if (hero.buff(PatientStrikeTracker.class) != null
					&& !(hero.belongings.attackingWeapon() instanceof MissileWeapon)){
				hero.buff(PatientStrikeTracker.class).detach();
				dmg += Random.IntRange(hero.pointsInTalent(Talent.PATIENT_STRIKE), 2);
			}
		}

		if (hero.hasTalent(DEADLY_FOLLOWUP) && enemy.alignment == Char.Alignment.ENEMY) {
			if (hero.belongings.attackingWeapon() instanceof MissileWeapon) {
				if (!(hero.belongings.attackingWeapon() instanceof SpiritBow.SpiritArrow)) {
					Buff.prolong(hero, DeadlyFollowupTracker.class, 5f).object = enemy.id();
				}
			} else if (hero.buff(DeadlyFollowupTracker.class) != null
					&& hero.buff(DeadlyFollowupTracker.class).object == enemy.id()){
				dmg = Math.round(dmg * (1.0f + .1f*hero.pointsInTalent(DEADLY_FOLLOWUP)));
			}
		}

		if (enemy.buff(GuidingLight.GuidingLightDebuff.class) != null){
			enemy.buff(GuidingLight.GuidingLightDebuff.class).detach();
			if (hero.hasTalent(Talent.SEARING_LIGHT)){
				dmg += 1 + 2*hero.pointsInTalent(Talent.SEARING_LIGHT);
			}
		}

		return dmg;
	}

	public static class ProvokedAngerTracker extends FlavourBuff{
		{ type = Buff.buffType.POSITIVE; }
		public int icon() { return BuffIndicator.WEAPON; }
		public void tintIcon(Image icon) { icon.hardlight(1.43f, 1.43f, 1.43f); }
		public float iconFadePercent() { return Math.max(0, 1f - (visualcooldown() / 5)); }
	}
	public static class LingeringMagicTracker extends FlavourBuff{
		{ type = Buff.buffType.POSITIVE; }
		public int icon() { return BuffIndicator.WEAPON; }
		public void tintIcon(Image icon) { icon.hardlight(1.43f, 1.43f, 0f); }
		public float iconFadePercent() { return Math.max(0, 1f - (visualcooldown() / 5)); }
	}
	public static class SuckerPunchTracker extends Buff{};
	public static class FollowupStrikeTracker extends FlavourBuff{
		public int object;
		{ type = Buff.buffType.POSITIVE; }
		public int icon() { return BuffIndicator.INVERT_MARK; }
		public void tintIcon(Image icon) { icon.hardlight(0f, 0.75f, 1f); }
		public float iconFadePercent() { return Math.max(0, 1f - (visualcooldown() / 5)); }
		private static final String OBJECT    = "object";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(OBJECT, object);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			object = bundle.getInt(OBJECT);
		}
	};

	public static final int MAX_TALENT_TIERS = 4;

	public static void initClassTalents( Hero hero ){
		initClassTalents( hero.heroClass, hero.talents, hero.metamorphedTalents );
	}

	public static void initClassTalents( HeroClass cls, ArrayList<LinkedHashMap<Talent, Integer>> talents){
		initClassTalents( cls, talents, new LinkedHashMap<>());
	}

	public static void initClassTalents( HeroClass cls, ArrayList<LinkedHashMap<Talent, Integer>> talents, LinkedHashMap<Talent, Talent> replacements ){
		while (talents.size() < MAX_TALENT_TIERS){
			talents.add(new LinkedHashMap<>());
		}

		//tier 1
		for (Talent talent : availableTalents(cls, null, 1)){
			if (replacements.containsKey(talent)){
				talent = replacements.get(talent);
			}
			talents.get(0).put(talent, 0);
		}

		//tier 2
		for (Talent talent : availableTalents(cls, null, 2)){
			if (replacements.containsKey(talent)){
				talent = replacements.get(talent);
			}
			talents.get(1).put(talent, 0);
		}

		//tier 3
		for (Talent talent : availableTalents(cls, null, 3)){
			if (replacements.containsKey(talent)){
				talent = replacements.get(talent);
			}
			talents.get(2).put(talent, 0);
		}

		//tier4
		//TBD
	}

	public static void initSubclassTalents( Hero hero ){
		initSubclassTalents( hero.subClass, hero.talents );
	}

	public static void initSubclassTalents( HeroSubClass cls, ArrayList<LinkedHashMap<Talent, Integer>> talents ){
		if (cls == HeroSubClass.NONE) return;

		while (talents.size() < MAX_TALENT_TIERS){
			talents.add(new LinkedHashMap<>());
		}

		//tier 3
		for (Talent talent : availableTalents(null, cls, 3)){
			talents.get(2).put(talent, 0);
		}

	}

	public static void initArmorTalents( Hero hero ){
		initArmorTalents( hero.armorAbility, hero.talents);
	}

	public static void initArmorTalents(ArmorAbility abil, ArrayList<LinkedHashMap<Talent, Integer>> talents ){
		if (abil == null) return;

		while (talents.size() < MAX_TALENT_TIERS){
			talents.add(new LinkedHashMap<>());
		}

		for (Talent t : abil.talents()){
			talents.get(3).put(t, 0);
		}
	}

	private static final String TALENT_TIER = "talents_tier_";

	public static void storeTalentsInBundle( Bundle bundle, ArrayList<LinkedHashMap<Talent, Integer>> talents, LinkedHashMap<Talent, Talent> replacements ){
		if (!talents.isEmpty()) {
			for (int i = 0; i < MAX_TALENT_TIERS; i++) {
				LinkedHashMap<Talent, Integer> tier = talents.get(i);
				Bundle tierBundle = new Bundle();

				for (Talent talent : tier.keySet()) {
					if (tier.get(talent) > 0) {
						tierBundle.put(talent.name(), tier.get(talent));
					}
					if (tierBundle.contains(talent.name())) {
						tier.put(talent, Math.min(tierBundle.getInt(talent.name()), talent.maxPoints()));
					}
				}
				bundle.put(TALENT_TIER + (i + 1), tierBundle);
			}
		}

		if (replacements == null) return;


		Bundle replacementsBundle = new Bundle();
		for (Talent t : replacements.keySet()){
			replacementsBundle.put(t.name(), replacements.get(t));
		}
		bundle.put("replacements", replacementsBundle);
	}

	private static final HashSet<String> removedTalents = new HashSet<>();
	static{
		//v2.4.0
		removedTalents.add("TEST_SUBJECT");
		removedTalents.add("TESTED_HYPOTHESIS");
		//v2.2.0
		removedTalents.add("EMPOWERING_SCROLLS");
	}

	private static final HashMap<String, String> renamedTalents = new HashMap<>();
	static{
		//v2.4.0
		renamedTalents.put("SECONDARY_CHARGE",          "VARIED_CHARGE");

		//v2.2.0
		renamedTalents.put("RESTORED_WILLPOWER",        "LIQUID_WILLPOWER");
		renamedTalents.put("ENERGIZING_UPGRADE",        "INSCRIBED_POWER");
		renamedTalents.put("MYSTICAL_UPGRADE",          "INSCRIBED_STEALTH");
		renamedTalents.put("RESTORED_NATURE",           "LIQUID_NATURE");
		renamedTalents.put("RESTORED_AGILITY",          "LIQUID_AGILITY");
		//v2.1.0
		renamedTalents.put("LIGHTWEIGHT_CHARGE",        "PRECISE_ASSAULT");
		//v2.0.0 BETA
		renamedTalents.put("LIGHTLY_ARMED",             "UNENCUMBERED_SPIRIT");
		//v2.0.0
		renamedTalents.put("ARMSMASTERS_INTUITION",     "VETERANS_INTUITION");
	}

	public static void restoreTalentsFromBundle( Bundle bundle, Hero hero ){
		if (bundle.contains("replacements")){
			Bundle replacements = bundle.getBundle("replacements");
			for (String key : replacements.getKeys()){
				String value = replacements.getString(key);
				if (renamedTalents.containsKey(key)) key = renamedTalents.get(key);
				if (renamedTalents.containsKey(value)) value = renamedTalents.get(value);
				if (!removedTalents.contains(key) && !removedTalents.contains(value)){
					try {
						hero.metamorphedTalents.put(Talent.valueOf(key), Talent.valueOf(value));
					} catch (Exception e) {
						SandboxPixelDungeon.reportException(e);
					}
				}
			}
		}

		if (hero.heroClass != null)     initClassTalents(hero);
		if (hero.subClass != null)      initSubclassTalents(hero);
		if (hero.armorAbility != null)  initArmorTalents(hero);

		for (int i = 0; i < MAX_TALENT_TIERS; i++){
			LinkedHashMap<Talent, Integer> tier = hero.talents.get(i);
			Bundle tierBundle = bundle.contains(TALENT_TIER+(i+1)) ? bundle.getBundle(TALENT_TIER+(i+1)) : null;

			if (tierBundle != null){
				for (String tName : tierBundle.getKeys()){
					int points = tierBundle.getInt(tName);
					if (renamedTalents.containsKey(tName)) tName = renamedTalents.get(tName);
					if (!removedTalents.contains(tName)) {
						try {
							Talent talent = Talent.valueOf(tName);
							if (tier.containsKey(talent)) {
								tier.put(talent, Math.min(points, talent.maxPoints()));
							}
						} catch (Exception e) {
							SandboxPixelDungeon.reportException(e);
						}
					}
				}
			}
		}
	}

	public static Talent[] availableTalents(HeroClass heroClass, HeroSubClass subClass, int tier) {
		if (tier == 1) {
			switch (heroClass) {
				case WARRIOR:   return new Talent[] {HEARTY_MEAL, VETERANS_INTUITION, PROVOKED_ANGER, IRON_WILL};
				case MAGE:      return new Talent[] {EMPOWERING_MEAL, SCHOLARS_INTUITION, LINGERING_MAGIC, BACKUP_BARRIER};
				case ROGUE:     return new Talent[] {CACHED_RATIONS, THIEFS_INTUITION, SUCKER_PUNCH, PROTECTIVE_SHADOWS};
				case HUNTRESS:  return new Talent[] {NATURES_BOUNTY, SURVIVALISTS_INTUITION, FOLLOWUP_STRIKE, NATURES_AID};
				case DUELIST:   return new Talent[] {STRENGTHENING_MEAL, ADVENTURERS_INTUITION, PATIENT_STRIKE, AGGRESSIVE_BARRIER};
				case CLERIC:    return new Talent[] {SATIATED_SPELLS, DETECT_CURSE, SEARING_LIGHT, SHIELD_OF_LIGHT};
				default: 	    return new Talent[0];
			}
		}
		if (tier == 2) {
			switch (heroClass) {
				case WARRIOR:   return new Talent[] {IRON_STOMACH, LIQUID_WILLPOWER, RUNIC_TRANSFERENCE, LETHAL_MOMENTUM, IMPROVISED_PROJECTILES};
				case MAGE:      return new Talent[] {ENERGIZING_MEAL, INSCRIBED_POWER, WAND_PRESERVATION, ARCANE_VISION, SHIELD_BATTERY};
				case ROGUE:     return new Talent[] {MYSTICAL_MEAL, INSCRIBED_STEALTH, WIDE_SEARCH, SILENT_STEPS, ROGUES_FORESIGHT};
				case HUNTRESS:  return new Talent[] {INVIGORATING_MEAL, LIQUID_NATURE, REJUVENATING_STEPS, HEIGHTENED_SENSES, DURABLE_PROJECTILES};
				case DUELIST:   return new Talent[] {FOCUSED_MEAL, LIQUID_AGILITY, WEAPON_RECHARGING, LETHAL_HASTE, SWIFT_EQUIP};
				default: 	    return new Talent[0];
			}
		}
		if (tier == 3) {
			if (heroClass != null) {
				switch (heroClass) {
					case WARRIOR:   return new Talent[] {HOLD_FAST, STRONGMAN};
					case MAGE:      return new Talent[] {DESPERATE_POWER, ALLY_WARP};
					case ROGUE:     return new Talent[] {ENHANCED_RINGS, LIGHT_CLOAK};
					case HUNTRESS:  return new Talent[] {POINT_BLANK, SEER_SHOT};
					case DUELIST:   return new Talent[] {PRECISE_ASSAULT, DEADLY_FOLLOWUP};
					default: 	    return new Talent[0];
				}
			}
			if (subClass != null) {
				switch (subClass) {
					case BERSERKER:		return new Talent[] {ENDLESS_RAGE, DEATHLESS_FURY, ENRAGED_CATALYST};
					case GLADIATOR:		return new Talent[] {CLEAVE, LETHAL_DEFENSE, ENHANCED_COMBO};
					case BATTLEMAGE:	return new Talent[] {EMPOWERED_STRIKE, MYSTICAL_CHARGE, EXCESS_CHARGE};
					case WARLOCK:		return new Talent[] {SOUL_EATER, SOUL_SIPHON, NECROMANCERS_MINIONS};
					case ASSASSIN:		return new Talent[] {ENHANCED_LETHALITY, ASSASSINS_REACH, BOUNTY_HUNTER};
					case FREERUNNER:	return new Talent[] {EVASIVE_ARMOR, PROJECTILE_MOMENTUM, SPEEDY_STEALTH};
					case SNIPER:		return new Talent[] {FARSIGHT, SHARED_ENCHANTMENT, SHARED_UPGRADES};
					case WARDEN:		return new Talent[] {DURABLE_TIPS, BARKSKIN, SHIELDING_DEW};
					case CHAMPION:		return new Talent[] {VARIED_CHARGE, TWIN_UPGRADES, COMBINED_LETHALITY};
					case MONK:			return new Talent[] {UNENCUMBERED_SPIRIT, MONASTIC_VIGOR, COMBINED_ENERGY};
					default:			return new Talent[0];
				}
			}
		}

		//tier 4: use heroClass.armorAbilities()[n].talents(); and add don't forget  new Ratmogrify().talents()

		return new Talent[0];
	}

}