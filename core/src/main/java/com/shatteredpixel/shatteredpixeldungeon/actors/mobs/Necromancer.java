/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2023 Evan Debenham
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

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Adrenaline;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.NecromancerSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SkeletonSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

public class Necromancer extends SpawnerMob {
	
	{
		spriteClass = NecromancerSprite.class;
		
		HP = HT = 40;
		defenseSkill = 14;
		damageReductionMax = 5;
		
		EXP = 7;
		maxLvl = 14;
		
		loot = new PotionOfHealing();
		lootChance = 0.2f; //see lootChance()
		
		properties.add(Property.UNDEAD);
		
		HUNTING = new Hunting();
	}
	
	public boolean summoning = false;
	public int summoningPos = -1;
	
	protected boolean firstSummon = true;

	{
		defaultTemplateClass = Skeleton.class;
		Mob summon = Reflection.newInstance(defaultTemplateClass);
		summon.state = summon.WANDERING;
		summonTemplate.clear();
		summonTemplate.add(summon);
	}
	protected Mob mySummon;
	private int storedSkeletonID = -1;

	@Override
	protected boolean act() {
		if (summoning && state != HUNTING){
			summoning = false;
			if (sprite instanceof NecromancerSprite) ((NecromancerSprite) sprite).cancelSummoning();
		}
		return super.act();
	}
	
//	@Override
//	public int drRoll() {
//		return super.drRoll() + Random.NormalIntRange(0, 5);
//	}
	
	@Override
	public float lootChance() {
		return super.lootChance() * ((6f - Dungeon.LimitedDrops.NECRO_HP.count) / 6f);
	}

	@Override
	public void increaseLimitedDropCount(Item generatedLoot) {
		if (generatedLoot instanceof PotionOfHealing)
			Dungeon.LimitedDrops.NECRO_HP.count++;
		super.increaseLimitedDropCount(generatedLoot);
	}

	@Override
	public void die(Object cause) {
		if (storedSkeletonID != -1){
			Actor ch = Actor.findById(storedSkeletonID);
			storedSkeletonID = -1;
			if (ch instanceof Mob){
				mySummon = (Mob) ch;
			}
		}
		
		if (mySummon != null && mySummon.isAlive()){
			mySummon.die(null);
		}
		
		super.die(cause);
	}

	@Override
	protected boolean canAttack(Char enemy) {
		return false;
	}

	private static final String SUMMONING = "summoning";
	private static final String FIRST_SUMMON = "first_summon";
	private static final String SUMMONING_POS = "summoning_pos";
	private static final String MY_SKELETON = "my_skeleton";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( SUMMONING, summoning );
		bundle.put( FIRST_SUMMON, firstSummon );
		if (summoning){
			bundle.put( SUMMONING_POS, summoningPos);
		}
		if (mySummon != null){
			bundle.put( MY_SKELETON, mySummon.id() );
		} else if (storedSkeletonID != -1){
			bundle.put( MY_SKELETON, storedSkeletonID );
		}
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		summoning = bundle.getBoolean( SUMMONING );
		if (bundle.contains(FIRST_SUMMON)) firstSummon = bundle.getBoolean(FIRST_SUMMON);
		if (summoning){
			summoningPos = bundle.getInt( SUMMONING_POS );
		}
		if (bundle.contains( MY_SKELETON )){
			storedSkeletonID = bundle.getInt( MY_SKELETON );
		}
	}
	
	public void onZapComplete(){
		if (mySummon == null || mySummon.sprite == null || !mySummon.isAlive()){
			return;
		}
		
		//heal skeleton first
		if (mySummon.HP < mySummon.HT){

			if (sprite.visible || mySummon.sprite.visible) {
				sprite.parent.add(new Beam.HealthRay(sprite.center(), mySummon.sprite.center()));
			}
			
			mySummon.HP = Math.min(mySummon.HP + mySummon.HT/5, mySummon.HT);
			if (mySummon.sprite.visible) mySummon.sprite.emitter().burst( Speck.factory( Speck.HEALING ), 1 );
			
		//otherwise give it adrenaline
		} else if (mySummon.buff(Adrenaline.class) == null) {

			if (sprite.visible || mySummon.sprite.visible) {
				sprite.parent.add(new Beam.HealthRay(sprite.center(), mySummon.sprite.center()));
			}
			
			Buff.affect(mySummon, Adrenaline.class, 3f);
		}
		
		next();
	}

	public Mob summonMinion(){
		if (Actor.findChar(summoningPos) != null) {

			//cancel if character cannot be moved
			if (Char.hasProp(Actor.findChar(summoningPos), Property.IMMOVABLE)){
				summoning = false;
				((NecromancerSprite)sprite).finishSummoning();
				spend(TICK);
				return null;
			}

			int pushPos = pos;
			for (int c : PathFinder.NEIGHBOURS8) {
				if (Actor.findChar(summoningPos + c) == null
						&& Dungeon.level.isPassable(summoningPos + c, this)
						&& (Dungeon.level.openSpace[summoningPos + c] || !hasProp(Actor.findChar(summoningPos), Property.LARGE))
						&& Dungeon.level.trueDistance(pos, summoningPos + c) > Dungeon.level.trueDistance(pos, pushPos)) {
					pushPos = summoningPos + c;
				}
			}

			//push enemy, or wait a turn if there is no valid pushing position
			if (pushPos != pos) {
				Char ch = Actor.findChar(summoningPos);
				Actor.add( new Pushing( ch, ch.pos, pushPos ) );

				ch.pos = pushPos;
				Dungeon.level.occupyCell(ch );

			} else {

				Char blocker = Actor.findChar(summoningPos);
				if (blocker.alignment != alignment){
					blocker.damage( Random.NormalIntRange(2, 10), this );
					if (blocker == Dungeon.hero && !blocker.isAlive()){
						Badges.validateDeathFromEnemyMagic();
						Dungeon.fail(this);
						GLog.n( Messages.capitalize(Messages.get(Char.class, "kill", name())) );
					}
				}

				spend(TICK);
				return null;
			}
		}

		summoning = firstSummon = false;

		mySummon = convertToSummonedMob(createSummonedMob());
		mySummon.pos = summoningPos;
		GameScene.add(mySummon);
		Dungeon.level.occupyCell(mySummon);
		finishSummoning();

		if (mySummon instanceof Wraith) {
			Wraith.showSpawnParticle((Wraith) mySummon);
		}

		for (Buff b : buffs(AllyBuff.class)){
			Buff.affect(mySummon, b.getClass());
		}
		for (Buff b : buffs(ChampionEnemy.class)){
			Buff.affect(mySummon, b.getClass());
		}
		return mySummon;
	}

	protected void finishSummoning(){
		((NecromancerSprite)sprite).finishSummoning();
	}
	
	private class Hunting extends Mob.Hunting{
		
		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {
			enemySeen = enemyInFOV;

			if (enemySeen){
				target = enemy.pos;
			}
			
			if (storedSkeletonID != -1){
				Actor ch = Actor.findById(storedSkeletonID);
				storedSkeletonID = -1;
				if (ch instanceof Mob){
					mySummon = (Mob) ch;
					mySummon.spawningWeight_NOT_SAVED_IN_BUNDLE = 0;//spawningWeight is not stored is bundle, but mobs act before level respawner
				}
			}
			
			if (summoning){
				summonMinion();
				return true;
			}
			
			if (mySummon != null &&
					(!mySummon.isAlive()
					|| !Dungeon.level.mobs.contains(mySummon)
					|| (mySummon.alignment != alignment && mySummon.playerAlignment == NORMAL_ALIGNMENT))){
				mySummon = null;
			}
			
			//if enemy is seen, and enemy is within range, and we have no skeleton, summon a skeleton!
			if (enemySeen && Dungeon.level.distance(pos, enemy.pos) <= 4 && mySummon == null){
				
				summoningPos = -1;

				//we can summon around blocking terrain, but not through it
				PathFinder.buildDistanceMap(pos, BArray.not(Dungeon.level.solid, null), Dungeon.level.distance(pos, enemy.pos)+3);

				for (int c : PathFinder.NEIGHBOURS8){
					if (Actor.findChar(enemy.pos+c) == null
							&& PathFinder.distance[enemy.pos+c] != Integer.MAX_VALUE
							&& Dungeon.level.isPassable(enemy.pos+c, Necromancer.this)
							&& (!hasProp(Necromancer.this, Property.LARGE) || Dungeon.level.openSpace[enemy.pos+c])
							&& fieldOfView[enemy.pos+c]
							&& Dungeon.level.trueDistance(pos, enemy.pos+c) < Dungeon.level.trueDistance(pos, summoningPos)){
						summoningPos = enemy.pos+c;
					}
				}
				
				if (summoningPos != -1 && summonTemplate != null && !summonTemplate.isEmpty()){
					
					summoning = true;
					sprite.zap( summoningPos );
					
					spend( firstSummon ? TICK : 2*TICK );
				} else {
					//wait for a turn
					spend(TICK);
				}
				
				return true;
			//otherwise, if enemy is seen, and we have a skeleton...
			} else if (enemySeen && mySummon != null){
				
				spend(TICK);
				
				if (!fieldOfView[mySummon.pos]){
					
					//if the skeleton is not next to the enemy
					//teleport them to the closest spot next to the enemy that can be seen
					if (!Dungeon.level.adjacent(mySummon.pos, enemy.pos)){
						int telePos = -1;
						for (int c : PathFinder.NEIGHBOURS8){
							if (Actor.findChar(enemy.pos+c) == null
									&& Dungeon.level.isPassable(enemy.pos+c, mySummon)
									&& fieldOfView[enemy.pos+c]
									&& (Dungeon.level.openSpace[enemy.pos+c] || !Char.hasProp(mySummon, Property.LARGE))
									&& Dungeon.level.trueDistance(pos, enemy.pos+c) < Dungeon.level.trueDistance(pos, telePos)){
								telePos = enemy.pos+c;
							}
						}
						
						if (telePos != -1){
							
							ScrollOfTeleportation.appear(mySummon, telePos);
							mySummon.spend_DO_NOT_CALL_UNLESS_ABSOLUTELY_NECESSARY(TICK);
							
							if (sprite != null && sprite.visible){
								sprite.zap(telePos);
								return false;
							} else {
								onZapComplete();
							}
						}
					}
					
					return true;
					
				} else {
					
					//zap skeleton
					if (mySummon.HP < mySummon.HT || mySummon.buff(Adrenaline.class) == null) {
						if (sprite != null && sprite.visible){
							sprite.zap(mySummon.pos);
							return false;
						} else {
							onZapComplete();
						}
					}
					
				}
				
				return true;
				
			//otherwise, default to regular hunting behaviour
			} else {
				return super.act(enemyInFOV, justAlerted);
			}
		}
	}

	protected Mob convertToSummonedMob(Mob mob) {
		//TODO name/desc
		if (mob instanceof Skeleton)
			mob.spriteClass = NecroSkeletonSprite.class;

		if (mob instanceof MobBasedOnDepth) ((MobBasedOnDepth) mob).setLevel(Dungeon.depth);

		mob.spawningWeight_NOT_SAVED_IN_BUNDLE = 0;

		//no loot or exp
		mob.maxLvl = -5;

		//20/25 health to start  -> 20% less hp
		mob.HP = mob.HT - mob.HT/5;

		return mob;
	}

	public static class NecroSkeletonSprite extends SkeletonSprite {

		public NecroSkeletonSprite() {
			super();
			brightness(0.75f);
		}

		@Override
		public void resetColor() {
			super.resetColor();
			brightness(0.75f);
		}
	}

}