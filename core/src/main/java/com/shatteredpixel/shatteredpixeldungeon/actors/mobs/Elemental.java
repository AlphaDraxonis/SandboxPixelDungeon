/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.WandmakerQuest;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Embers;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.RatSkull;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.CursedWand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Shocking;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ElementalSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public abstract class Elemental extends Mob {

	{
		HP = HT = 60;
		defenseSkill = 20;
		damageReductionMax = 5;
		damageRollMin = 20;
		damageRollMax = 25;
		attackSkill = 25;

		EXP = 10;
		maxLvl = 20;

		setFlying(true);
	}

	protected boolean summonedALly;

	public void setSummonedALly(){
		summonedALly = true;
		//sewers and prison are equivalent, otherwise scales as normal (2/2/3/4/5)
		int regionScale = Math.max(2, Dungeon.region());
		defenseSkill = 5*regionScale;
		attackSkill = 5 + 5*regionScale;
		damageRollMin = 5*regionScale;
		damageRollMax = 5 + 5*regionScale;
		HT = 15*regionScale;

		hpSet = true;
	}
	
	@Override
	public boolean areStatsEqual(Mob other) {

		if (!( summonedALly || other instanceof Elemental && ((Elemental) other).summonedALly) )
			return super.areStatsEqual(other);

		int otherAttackSkill = other.attackSkill;
		int otherDefenseSkill = other.defenseSkill;
		int otherDamageRollMin = other.damageRollMin;
		int otherDamageRollMax = other.specialDamageRollMin;
		int otherSpecialDamageRollMin = other.specialDamageRollMax;
		int otherSpecialDamageRollMax = other.damageRollMax;
		float otherAttackSpeed = other.attackSpeed;
		int otherDamageReductionMax = other.damageReductionMax;
		int otherHT = other.HT;

		other.attackSkill = attackSkill;
		other.defenseSkill = defenseSkill;
		other.damageRollMin = damageRollMin;
		other.damageRollMax = damageRollMax;
		other.specialDamageRollMin = specialDamageRollMin;
		other.specialDamageRollMax = specialDamageRollMax;
		other.attackSpeed = attackSpeed;
		other.damageReductionMax = damageReductionMax;
		other.HT = HT;

		boolean equal = super.areStatsEqual(other);

		other.attackSkill = otherAttackSkill;
		other.defenseSkill = otherDefenseSkill;
		other.damageRollMin = otherDamageRollMin;
		other.damageRollMax = otherDamageRollMax;
		other.specialDamageRollMin = otherSpecialDamageRollMin;
		other.specialDamageRollMax = otherSpecialDamageRollMax;
		other.attackSpeed = otherAttackSpeed;
		other.damageReductionMax = otherDamageReductionMax;
		other.HT = otherHT;

		return equal;
	}

//	@Override
//	public int drRoll() {
//		return super.drRoll() + Random.NormalIntRange(0, 5);
//	}

	protected int rangedCooldown = Random.NormalIntRange( 3, 5 );
	
	@Override
	protected boolean act() {
		if (state == HUNTING){
			rangedCooldown--;
		}
		
		return super.act();
	}

	@Override
	public void die(Object cause) {
		setFlying(false);
		super.die(cause);
	}

	@Override
	protected boolean canAttack( Char enemy ) {
		if (super.canAttack(enemy)){
			return true;
		} else {
			return rangedCooldown < 0 && new Ballistica( pos, enemy.pos, Ballistica.REAL_MAGIC_BOLT, null ).collisionPos == enemy.pos;
		}
	}
	
	protected boolean doAttack( Char enemy ) {
		
		if (Dungeon.level.adjacent( pos, enemy.pos )
				|| rangedCooldown > 0
				|| new Ballistica( pos, enemy.pos, Ballistica.REAL_MAGIC_BOLT, null ).collisionPos != enemy.pos) {
			
			return super.doAttack( enemy );
			
		} else {
			
			return doRangedAttack();
		}
	}
	
	@Override
	public int attackProc( Char enemy, int damage ) {
		damage = super.attackProc( enemy, damage );
		meleeProc( enemy, damage );
		
		return damage;
	}

	@Override
	public void zap() {
		spend( 1f );

		Invisibility.dispel(this);
		Char enemy = this.enemy;
		if (hit( this, enemy, true )) {
			
			rangedProc( enemy );
			
		} else {
			enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
		}

		rangedCooldown = Random.NormalIntRange( 3, 5 );
	}

	@Override
	public boolean add( Buff buff ) {
		if (harmfulBuffs.contains( buff.getClass() )) {
			damage( Random.NormalIntRange( HT/2, HT * 3/5 ), buff );
			return false;
		} else {
			return super.add( buff );
		}
	}
	
	protected abstract void meleeProc( Char enemy, int damage );
	protected abstract void rangedProc( Char enemy );
	
	protected ArrayList<Class<? extends Buff>> harmfulBuffs = new ArrayList<>();
	
	private static final String COOLDOWN = "cooldown";
	private static final String SUMMONED_ALLY = "summoned_ally";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( COOLDOWN, rangedCooldown );
		bundle.put( SUMMONED_ALLY, summonedALly);
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		if (bundle.contains( COOLDOWN )){
			rangedCooldown = bundle.getInt( COOLDOWN );
		}
		summonedALly = bundle.getBoolean( SUMMONED_ALLY );
		if (summonedALly){
			setSummonedALly();
		}
	}
	
	public static class FireElemental extends Elemental {
		
		{
			spriteClass = ElementalSprite.Fire.class;
			
			loot = PotionOfLiquidFlame.class;
			lootChance = 1/8f;
			
			properties.add( Property.FIERY );
			
			harmfulBuffs.add( Frost.class );
			harmfulBuffs.add( Chill.class );
		}
		
		@Override
		protected void meleeProc( Char enemy, int damage ) {
			if (Random.Int( 2 ) == 0 && !Dungeon.level.water[enemy.pos]) {
				Buff.affect( enemy, Burning.class ).reignite( enemy );
				if (enemy.sprite.visible) Splash.at( enemy.sprite.center(), sprite.blood(), 5);
			}
		}
		
		@Override
		protected void rangedProc( Char enemy ) {
			if (!Dungeon.level.water[enemy.pos]) {
				Buff.affect( enemy, Burning.class ).reignite( enemy, 4f );
			}
			if (enemy.sprite.visible) Splash.at( enemy.sprite.center(), sprite.blood(), 5);
		}

		@Override
		public void playZapAnim(int target) {
			ElementalSprite.Fire.playZap(sprite.parent, sprite, target, this);
		}
	}
	
	//used in wandmaker quest, a fire elemental with lower ACC/EVA/DMG, no on-hit fire
	// and a unique 'fireball' style ranged attack, which can be dodged
	public static class NewbornFireElemental extends FireElemental implements MobBasedOnDepth {
		
		{
			spriteClass = ElementalSprite.NewbornFire.class;

			defenseSkill = 12;

			attackSkill = 15;
			damageRollMin = 10;
			damageRollMax = 12;

			properties.add(Property.MINIBOSS);
		}

		private int targetingPos = -1;
		public boolean spawnedByQuest;

		@Override
		protected boolean act() {
			//fire a charged attack instead of any other action, as long as it is possible to do so
			if (targetingPos != -1 && state == HUNTING){
				//account for bolt hitting walls, in case position suddenly changed
				targetingPos = new Ballistica( pos, targetingPos, Ballistica.STOP_SOLID | Ballistica.STOP_TARGET | Ballistica.STOP_BARRIER_PROJECTILES, null ).collisionPos;
				return doRangedAttack(targetingPos, sprite != null && (sprite.visible || Dungeon.level.heroFOV[targetingPos]));
			} else {

				if (state != HUNTING){
					targetingPos = -1;
				}

				return super.act();
			}
		}

		@Override
		protected boolean canAttack( Char enemy ) {
			if (super.canAttack(enemy)){
				return true;
			} else {
				return rangedCooldown < 0 && new Ballistica( pos, enemy.pos,
						Ballistica.STOP_SOLID | Ballistica.STOP_TARGET | Ballistica.STOP_BARRIER_PROJECTILES, null ).collisionPos == enemy.pos;
			}
		}

		protected boolean doAttack( Char enemy ) {

			if (rangedCooldown > 0) {

				return super.doAttack( enemy );

			} else if (new Ballistica( pos, enemy.pos,
					Ballistica.STOP_SOLID | Ballistica.STOP_TARGET | Ballistica.STOP_BARRIER_PROJECTILES, null ).collisionPos == enemy.pos) {

				//set up an attack for next turn
				ArrayList<Integer> candidates = new ArrayList<>();
				for (int i : PathFinder.NEIGHBOURS8){
					int target = enemy.pos + i;
					if (target != pos && new Ballistica(pos, target,
							Ballistica.STOP_SOLID | Ballistica.STOP_TARGET | Ballistica.STOP_BARRIER_PROJECTILES, null ).collisionPos == target){
						candidates.add(target);
					}
				}

				if (!candidates.isEmpty()){
					targetingPos = Random.element(candidates);

					for (int i : PathFinder.NEIGHBOURS9){
						if (!Dungeon.level.solid[targetingPos + i]) {
							sprite.parent.addToBack(new TargetedCell(targetingPos + i, 0xFF0000));
						}
					}

					GLog.n(Messages.get(this, "charging"));
					spend(GameMath.gate(attackDelay(), (int)Math.ceil(Dungeon.hero.cooldown()), 3*attackDelay()));
					Dungeon.hero.interrupt();
					return true;
				} else {
					rangedCooldown = 1;
					return super.doAttack(enemy);
				}


			} else {

				return doRangedAttack(targetingPos, sprite != null && (sprite.visible || Dungeon.level.heroFOV[targetingPos]));

			}
		}

		@Override
		public void playZapAnim(int target) {
			ElementalSprite.NewbornFire.playZap(sprite.parent, sprite, target, this);
		}

		@Override
		public void zap() {
			if (targetingPos != -1) {
				spend(1f);

				Invisibility.dispel(this);

				for (int i : PathFinder.NEIGHBOURS9) {
					if (!Dungeon.level.solid[targetingPos + i]) {
						CellEmitter.get(targetingPos + i).burst(ElmoParticle.FACTORY, 5);
						if (Dungeon.level.water[targetingPos + i]) {
							GameScene.add(Blob.seed(targetingPos + i, 2, Fire.class));
						} else {
							GameScene.add(Blob.seed(targetingPos + i, 8, Fire.class));
						}

						Char target = Actor.findChar(targetingPos + i);
						if (target != null && target != this) {
							Buff.affect(target, Burning.class).reignite(target);
						}
					}
				}
				Sample.INSTANCE.play(Assets.Sounds.BURNING);
			}

			targetingPos = -1;
			rangedCooldown = Random.NormalIntRange( 3, 5 );
		}

		@Override
		protected void meleeProc(Char enemy, int damage) {
			//no fiery on-hit unless it is an ally summon
			if (summonedALly) {
				super.meleeProc(enemy, damage);
			}
		}

		@Override
		public void die(Object cause) {
			super.die(cause);
			if (alignment == Alignment.ENEMY) {
				Dungeon.level.drop( new Embers(), pos ).sprite.drop();
				WandmakerQuest.questsActive[WandmakerQuest.CANDLE]--;
				WandmakerQuest.updateMusic();
			}
		}

		@Override
		public boolean reset() {
			return !summonedALly;
		}

		@Override
		public void setLevel(int depth) {//not based on depth, but this method is called when game is inited
			if (!hpSet) {
				HP = HT / 2;
				hpSet = !CustomDungeon.isEditing();
			}
		}

		@Override
		public String desc() {
			String desc = super.desc();

			if (summonedALly){
				desc += " " + Messages.get(this, "desc_ally");
			} else {
				desc += " " + Messages.get(this, "desc_boss");
			}

			return desc;
		}

		private static final String TARGETING_POS = "targeting_pos";
		private static final String SPAWNED_BY_QUEST = "spawned_by_quest";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(TARGETING_POS, targetingPos);
			bundle.put(SPAWNED_BY_QUEST, spawnedByQuest);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			targetingPos = bundle.getInt(TARGETING_POS);
			spawnedByQuest = bundle.getBoolean(SPAWNED_BY_QUEST);
		}
	}

	//not a miniboss, no ranged attack, otherwise a newborn elemental
	public static class AllyNewBornElemental extends NewbornFireElemental {

		{
			rangedCooldown = Integer.MAX_VALUE;

			properties.remove(Property.MINIBOSS);
		}

	}
	
	public static class FrostElemental extends Elemental {
		
		{
			spriteClass = ElementalSprite.Frost.class;
			
			loot = PotionOfFrost.class;
			lootChance = 1/8f;
			
			properties.add( Property.ICY );
			
			harmfulBuffs.add( Burning.class );
		}
		
		@Override
		protected void meleeProc( Char enemy, int damage ) {
			if (Random.Int( 3 ) == 0 || Dungeon.level.water[enemy.pos]) {
				Freezing.freeze( enemy.pos );
				if (enemy.sprite.visible) Splash.at( enemy.sprite.center(), sprite.blood(), 5);
			}
		}
		
		@Override
		protected void rangedProc( Char enemy ) {
			Freezing.freeze( enemy.pos );
			if (enemy.sprite.visible) Splash.at( enemy.sprite.center(), sprite.blood(), 5);
		}

		@Override
		public void playZapAnim(int target) {
			ElementalSprite.Frost.playZap(sprite.parent, sprite, target, this);
		}
	}
	
	public static class ShockElemental extends Elemental {
		
		{
			spriteClass = ElementalSprite.Shock.class;
			
			loot = ScrollOfRecharging.class;
			lootChance = 1/4f;
			
			properties.add( Property.ELECTRIC );
		}
		
		@Override
		protected void meleeProc( Char enemy, int damage ) {
			ArrayList<Char> affected = new ArrayList<>();
			ArrayList<Lightning.Arc> arcs = new ArrayList<>();
			Shocking.arc( this, enemy, 2, affected, arcs );
			
			if (!Dungeon.level.water[enemy.pos]) {
				affected.remove( enemy );
			}
			
			for (Char ch : affected) {
				ch.damage( Math.round( damage * 0.4f ), new Shocking() );
				if (ch == Dungeon.hero && !ch.isAlive()){
					Dungeon.fail(this);
					GLog.n( Messages.capitalize(Messages.get(Char.class, "kill", name())) );
				}
			}

			boolean visible = sprite.visible || enemy.sprite.visible;
			for (Char ch : affected){
				if (ch.sprite.visible) visible = true;
			}

			if (visible) {
				sprite.parent.addToFront(new Lightning(arcs, null));
				Sample.INSTANCE.play(Assets.Sounds.LIGHTNING);
			}
		}
		
		@Override
		protected void rangedProc( Char enemy ) {
			Buff.affect( enemy, Blindness.class, Blindness.DURATION/2f );
			if (enemy == Dungeon.hero) {
				GameScene.flash(0x80FFFFFF);
			}
		}
	}
	
	public static class ChaosElemental extends Elemental {
		
		{
			spriteClass = ElementalSprite.Chaos.class;
			
			loot = ScrollOfTransmutation.class;
			lootChance = 1f;
		}
		
		@Override
		protected void meleeProc( Char enemy, int damage ) {
			Ballistica aim = new Ballistica(pos, enemy.pos, Ballistica.STOP_TARGET, null);
			//TODO shortcutting the fx seems fine for now but may cause problems with new cursed effects
			//of course, not shortcutting it means actor ordering issues =S
			CursedWand.randomValidEffect(null, this, aim, false).effect(null, this, aim, false);
		}

		@Override
		public void zap() {
			spend( 1f );

			Invisibility.dispel(this);
			Char enemy = this.enemy;
			//skips accuracy check, always hits
			rangedProc( enemy );

			rangedCooldown = Random.NormalIntRange( 3, 5 );
		}

		@Override
		public void onZapComplete() {
			zap();
			//next(); triggers after wand effect
		}

		@Override
		protected void rangedProc( Char enemy ) {
			CursedWand.cursedZap(null, this, new Ballistica(pos, enemy.pos, Ballistica.STOP_TARGET, null), new Callback() {
				@Override
				public void call() {
					next();
				}
			});
		}

		@Override
		public void playZapAnim(int target) {
			ElementalSprite.Chaos.playZap(sprite.parent, sprite, target, this);
		}
	}
	
	public static Class<? extends Elemental> random(){
		float altChance = 1/50f * RatSkull.exoticChanceMultiplier();
		if (Random.Float() < altChance){
			return ChaosElemental.class;
		}
		
		float roll = Random.Float();
		if (roll < 0.4f){
			return FireElemental.class;
		} else if (roll < 0.8f){
			return FrostElemental.class;
		} else {
			return ShockElemental.class;
		}
	}
}
