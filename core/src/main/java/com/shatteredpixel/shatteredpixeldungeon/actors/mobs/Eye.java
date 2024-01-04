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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.stateditor.LootTableComp;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PurpleParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAggression;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfBlink;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfClairvoyance;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfDeepSleep;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfDisarming;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfFear;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfFlock;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfIntuition;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfShock;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfDisintegration;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DisintegrationTrap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Blindweed;
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot;
import com.shatteredpixel.shatteredpixeldungeon.plants.Fadeleaf;
import com.shatteredpixel.shatteredpixeldungeon.plants.Firebloom;
import com.shatteredpixel.shatteredpixeldungeon.plants.Icecap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Mageroyal;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sorrowmoss;
import com.shatteredpixel.shatteredpixeldungeon.plants.Starflower;
import com.shatteredpixel.shatteredpixeldungeon.plants.Stormvine;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sungrass;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.EyeSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.List;

public class Eye extends Mob {
	
	{
		spriteClass = EyeSprite.class;
		
		HP = HT = 100;
		defenseSkill = 20;
		attackSkill = 30;
		damageRollMin = 20;
		damageRollMax = 30;
		specialDamageRollMin = 30;
		specialDamageRollMax = 50;
		damageReductionMax = 10;
		viewDistance = Light.DISTANCE;
		
		EXP = 13;
		maxLvl = 26;
		
		flying = true;

		HUNTING = new Hunting();

		lootChance = 1f;
		//special loot logic, see createActualLoot()

		properties.add(Property.DEMONIC);
	}

//	@Override
//	public int damageRoll() {
//		return Random.NormalIntRange(20, 30);
//	}
//
//	@Override
//	public int attackSkill( Char target ) {
//		return 30;
//	}
//
//	@Override
//	public int drRoll() {
//		return super.drRoll() + Random.NormalIntRange(0, 10);
//	}
	
	private Ballistica beam;
	private int beamTarget = -1;
	private int beamCooldown;
	public boolean beamCharged;

	@Override
	protected boolean canAttack( Char enemy ) {

		if (beamCooldown == 0) {
			Ballistica aim = new Ballistica(pos, enemy.pos, Ballistica.STOP_SOLID | Ballistica.STOP_BARRIER_PROJECTILES, null);

			if (enemy.invisible == 0 && !isCharmedBy(enemy) && fieldOfView[enemy.pos]
					&& (super.canAttack(enemy) || aim.subPath(1, aim.dist).contains(enemy.pos))){
				beam = aim;
				beamTarget = enemy.pos;
				return true;
			} else {
				//if the beam is charged, it has to attack, will aim at previous location of target.
				return beamCharged;
			}
		} else {
			return super.canAttack(enemy);
		}
	}

	@Override
	protected boolean act() {
		if (beamCharged && state != HUNTING){
			beamCharged = false;
			sprite.idle();
		}
		if (beam == null && beamTarget != -1) {
			beam = new Ballistica(pos, beamTarget, Ballistica.STOP_SOLID | Ballistica.STOP_BARRIER_PROJECTILES, null);
			sprite.turnTo(pos, beamTarget);
		}
		if (beamCooldown > 0)
			beamCooldown--;
		return super.act();
	}

	@Override
	protected boolean doAttack( Char enemy ) {

		beam = new Ballistica(pos, beamTarget, Ballistica.STOP_SOLID | Ballistica.STOP_BARRIER_PROJECTILES, null);
		if (beamCooldown > 0 || (!beamCharged && !beam.subPath(1, beam.dist).contains(enemy.pos))) {
			return super.doAttack(enemy);
		} else if (!beamCharged){
			((EyeSprite)sprite).charge( enemy.pos );
			spend( attackDelay()*2f );
			beamCharged = true;
			return true;
		} else {

			spend( attackDelay() );
			
			if (Dungeon.level.heroFOV[pos] || Dungeon.level.heroFOV[beam.collisionPos] ) {
				sprite.zap( beam.collisionPos );
				return false;
			} else {
				sprite.idle();
				deathGaze();
				return true;
			}
		}

	}

	@Override
	public void damage(int dmg, Object src) {
		if (beamCharged) dmg /= 4;
		super.damage(dmg, src);
	}
	
	//used so resistances can differentiate between melee and magical attacks
	public static class DeathGaze{}

	public void deathGaze(){
		if (!beamCharged || beamCooldown > 0 || beam == null)
			return;

		beamCharged = false;
		beamCooldown = Random.IntRange(4, 6);

		boolean terrainAffected = false;

		Invisibility.dispel(this);
		for (int pos : beam.subPath(1, beam.dist)) {

			if (Dungeon.level.isFlamable(pos)) {

				Dungeon.level.destroy( pos );
				GameScene.updateMap( pos );
				terrainAffected = true;

			}

			Char ch = Actor.findChar( pos );
			if (ch == null) {
				continue;
			}

			if (hit( this, ch, true )) {
				int dmg = Random.NormalIntRange( specialDamageRollMin, specialDamageRollMax );
				dmg = Math.round(dmg * AscensionChallenge.statModifier(this));
				ch.damage( dmg, new DeathGaze() );

				if (Dungeon.level.heroFOV[pos]) {
					ch.sprite.flash();
					CellEmitter.center( pos ).burst( PurpleParticle.BURST, Random.IntRange( 1, 2 ) );
				}

				if (!ch.isAlive() && ch == Dungeon.hero) {
					Badges.validateDeathFromEnemyMagic();
					Dungeon.fail( this );
					GLog.n( Messages.get(this, "deathgaze_kill") );
				}
			} else {
				ch.sprite.showStatus( CharSprite.NEUTRAL,  ch.defenseVerb() );
			}
		}

		if (terrainAffected) {
			Dungeon.observe();
		}

		beam = null;
		beamTarget = -1;
	}

	//generates an average of 1 dew, 0.25 seeds, and 0.25 stones
//	@Override
//	public Item createLoot() {
//		Item loot;
//		switch(Random.Int(4)){
//			case 0: case 1: default:
//				loot = new Dewdrop();
//				int ofs;
//				do {
//					ofs = PathFinder.NEIGHBOURS8[Random.Int(8)];
//				} while (Dungeon.level.solid[pos + ofs] && !Dungeon.level.passable[pos + ofs]);
//				if (Dungeon.level.heaps.get(pos+ofs) == null) {
//					Dungeon.level.drop(new Dewdrop(), pos + ofs).sprite.drop(pos);
//				} else {
//					Dungeon.level.drop(new Dewdrop(), pos + ofs).sprite.drop(pos + ofs);
//				}
//				break;
//			case 2:
//				loot = Generator.randomUsingDefaults(Generator.Category.SEED);
//				break;
//			case 3:
//				loot = Generator.randomUsingDefaults(Generator.Category.STONE);
//				break;
//		}
//		return loot;
//	}

	@Override
	public List<Item> createActualLoot() {
		if (loot == null) return convertToCustomLootInfo().generateLoot();
		else return super.createActualLoot();
	}

	//generates an average of 1 dew, 0.25 seeds, and 0.25 stones
	@Override
	public LootTableComp.CustomLootInfo convertToCustomLootInfo() {
		LootTableComp.CustomLootInfo customLootInfo = new LootTableComp.CustomLootInfo();

		int seedWeight = 50;
		//11 items, 25% chance, weight=52
		customLootInfo.addItem(new Sungrass.Seed(), (int)Generator.Category.SEED.defaultProbs[1] * seedWeight);
		customLootInfo.addItem(new Fadeleaf.Seed(), (int)Generator.Category.SEED.defaultProbs[2] * seedWeight);
		customLootInfo.addItem(new Icecap.Seed(), (int)Generator.Category.SEED.defaultProbs[3] * seedWeight);
		customLootInfo.addItem(new Firebloom.Seed(), (int)Generator.Category.SEED.defaultProbs[4] * seedWeight);
		customLootInfo.addItem(new Sorrowmoss.Seed(), (int)Generator.Category.SEED.defaultProbs[5] * seedWeight);
		customLootInfo.addItem(new Swiftthistle.Seed(), (int)Generator.Category.SEED.defaultProbs[6] * seedWeight);
		customLootInfo.addItem(new Blindweed.Seed(), (int)Generator.Category.SEED.defaultProbs[7] * seedWeight);
		customLootInfo.addItem(new Stormvine.Seed(), (int)Generator.Category.SEED.defaultProbs[8] * seedWeight);
		customLootInfo.addItem(new Earthroot.Seed(), (int)Generator.Category.SEED.defaultProbs[9] * seedWeight);
		customLootInfo.addItem(new Mageroyal.Seed(), (int)Generator.Category.SEED.defaultProbs[10] * seedWeight);
		customLootInfo.addItem(new Starflower.Seed(), (int)Generator.Category.SEED.defaultProbs[11] * seedWeight);

		int stoneWeight = 52;
		//10 items, 25% chance, weight=50
		customLootInfo.addItem(new StoneOfIntuition(), (int)Generator.Category.STONE.defaultProbs[1] * stoneWeight);
		customLootInfo.addItem(new StoneOfDisarming(), (int)Generator.Category.STONE.defaultProbs[2] * stoneWeight);
		customLootInfo.addItem(new StoneOfFlock(), (int)Generator.Category.STONE.defaultProbs[3] * stoneWeight);
		customLootInfo.addItem(new StoneOfShock(), (int)Generator.Category.STONE.defaultProbs[4] * stoneWeight);
		customLootInfo.addItem(new StoneOfBlink(), (int)Generator.Category.STONE.defaultProbs[5] * stoneWeight);
		customLootInfo.addItem(new StoneOfDeepSleep(), (int)Generator.Category.STONE.defaultProbs[6] * stoneWeight);
		customLootInfo.addItem(new StoneOfClairvoyance(), (int)Generator.Category.STONE.defaultProbs[7] * stoneWeight);
		customLootInfo.addItem(new StoneOfAggression(), (int)Generator.Category.STONE.defaultProbs[8] * stoneWeight);
		customLootInfo.addItem(new StoneOfBlast(), (int)Generator.Category.STONE.defaultProbs[9] * stoneWeight);
		customLootInfo.addItem(new StoneOfFear(), (int)Generator.Category.STONE.defaultProbs[10] * stoneWeight);

		//50% dew
		Dewdrop dew = new Dewdrop();
		dew.spreadIfLoot = true;
		customLootInfo.addItem(dew, customLootInfo.calculateSum());

		return customLootInfo;
	}

	private static final String BEAM_TARGET     = "beamTarget";
	private static final String BEAM_COOLDOWN   = "beamCooldown";
	private static final String BEAM_CHARGED    = "beamCharged";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( BEAM_TARGET, beamTarget);
		bundle.put( BEAM_COOLDOWN, beamCooldown );
		bundle.put( BEAM_CHARGED, beamCharged );
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (bundle.contains(BEAM_TARGET))
			beamTarget = bundle.getInt(BEAM_TARGET);
		beamCooldown = bundle.getInt(BEAM_COOLDOWN);
		beamCharged = bundle.getBoolean(BEAM_CHARGED);
	}

	{
		resistances.add( WandOfDisintegration.class );
		resistances.add( DeathGaze.class );
		resistances.add( DisintegrationTrap.class );
	}

	private class Hunting extends Mob.Hunting{
		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {
			//even if enemy isn't seen, attack them if the beam is charged
			if (beamCharged && enemy != null && canAttack(enemy)) {
				enemySeen = enemyInFOV;
				return doAttack(enemy);
			}
			return super.act(enemyInFOV, justAlerted);
		}
	}
}