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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.ImpQuest;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemsWithChanceDistrComp;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GolemSprite;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Golem extends Mob {
	
	{
		spriteClass = GolemSprite.class;
		
		HP = HT = 120;
		defenseSkill = 15;
		attackSkill = 28;
		damageRollMin = 25;
		damageRollMax = 30;
		damageReductionMax = 12;

		EXP = 12;
		maxLvl = 22;

		loot = Random.oneOf(Generator.Category.WEAPON, Generator.Category.ARMOR);
		lootChance = 0.2f; //initially, see lootChance()

		properties.add(Property.INORGANIC);
		properties.add(Property.LARGE);

		WANDERING = new Wandering();
		HUNTING = new Hunting();
	}

//	@Override
//	public int damageRoll() {
//		return Random.NormalIntRange( 25, 30 );
//	}
//
//	@Override
//	public int attackSkill( Char target ) {
//		return 28;
//	}
//
//	@Override
//	public int drRoll() {
//		return super.drRoll() + Random.NormalIntRange(0, 12);
//	}

	@Override
	public float lootChance() {
		//each drop makes future drops 1/3 as likely
		// so loot chance looks like: 1/5, 1/15, 1/45, 1/135, etc.
		return super.lootChance() * (float)Math.pow(1/3f, Dungeon.LimitedDrops.GOLEM_EQUIP.count);
	}

	@Override
	public void rollToDropLoot() {
		ImpQuest.process( this );
		super.rollToDropLoot();
	}

	public Item createLoot() {
		//uses probability tables for demon halls
		if (loot == Generator.Category.WEAPON){
			return Generator.randomWeapon(5, true);
		} else {
			return Generator.randomArmor(5);
		}
	}

	@Override
	public ItemsWithChanceDistrComp.RandomItemData convertLootToRandomItemData() {
		ItemsWithChanceDistrComp.RandomItemData customLootInfo = super.convertLootToRandomItemData();
		Generator.convertRandomArmorToCustomLootInfo(customLootInfo, 5);
		Generator.convertRandomWeaponToCustomLootInfo(customLootInfo, 5);
		customLootInfo.setLootChance(customLootInfo.calculateSum() * 7);
		return customLootInfo;
	}


	@Override
	public void increaseLimitedDropCount(Item generatedLoot) {
		if (generatedLoot instanceof MeleeWeapon || generatedLoot instanceof Armor)
			Dungeon.LimitedDrops.GOLEM_EQUIP.count++;
		super.increaseLimitedDropCount(generatedLoot);
	}

	private boolean teleporting = false;
	private int selfTeleCooldown = 0;
	private int enemyTeleCooldown = 0;
	public int maxTeleCooldown = 20;//selfTele *1.5f

	private static final String TELEPORTING = "teleporting";
	private static final String SELF_COOLDOWN = "self_cooldown";
	private static final String ENEMY_COOLDOWN = "enemy_cooldown";
	private static final String MAX_TELE_COOLDOWN = "max_tele_cooldown";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(TELEPORTING, teleporting);
		bundle.put(SELF_COOLDOWN, selfTeleCooldown);
		bundle.put(ENEMY_COOLDOWN, enemyTeleCooldown);
		bundle.put(MAX_TELE_COOLDOWN, maxTeleCooldown);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		teleporting = bundle.getBoolean( TELEPORTING );
		selfTeleCooldown = bundle.getInt( SELF_COOLDOWN );
		enemyTeleCooldown = bundle.getInt( ENEMY_COOLDOWN );
		maxTeleCooldown = bundle.getInt( MAX_TELE_COOLDOWN );
	}

	@Override
	protected boolean act() {
		selfTeleCooldown--;
		enemyTeleCooldown--;
		if (teleporting){
			if (sprite.extraCode instanceof GolemSprite.TeleParticles)
				((GolemSprite.TeleParticles) sprite.extraCode).showParticles(false);

			if (Actor.findChar(target) == null && Dungeon.level.openSpace[target]) {
				ScrollOfTeleportation.appear(this, target);
				selfTeleCooldown = (int) (maxTeleCooldown * 1.5f);
			} else {
				target = Dungeon.level.randomDestination(this);
			}
			teleporting = false;
			spend(TICK);
			return true;
		}
		return super.act();
	}

	@Override
	public void playZapAnim(int target) {
		GolemSprite.playZap(sprite.parent, sprite, target, this);
	}

	@Override
	public void zap() {
		teleportEnemy();
	}

	public void teleportEnemy(){
		spend(TICK);

		int bestPos = enemy.pos;
		for (int i : PathFinder.NEIGHBOURS8){
			if (Dungeon.level.isPassable(pos + i, enemy)
				&& Actor.findChar(pos+i) == null
				&& Dungeon.level.trueDistance(pos+i, enemy.pos) > Dungeon.level.trueDistance(bestPos, enemy.pos)){
				bestPos = pos+i;
			}
		}

		if (enemy.buff(MagicImmune.class) != null){
			bestPos = enemy.pos;
		}

		if (bestPos != enemy.pos){
			ScrollOfTeleportation.appear(enemy, bestPos);
			if (enemy instanceof Hero){
				((Hero) enemy).interrupt();
				Dungeon.observe();
				GameScene.updateFog();
			}
		}

		enemyTeleCooldown = maxTeleCooldown;
	}

	public boolean canTele(int target){
		if (enemyTeleCooldown > 0) return false;
		PathFinder.buildDistanceMapForCharacters(target, BArray.not(Dungeon.level.solid, null), Dungeon.level.distance(pos, target)+1, target);
		//zaps can go around blocking terrain, but not through it
		if (PathFinder.distance[pos] == Integer.MAX_VALUE){
			return false;
		}
		return true;
	}

	private class Wandering extends Mob.Wandering{

		@Override
		protected boolean continueWandering() {
			enemySeen = false;

			int oldPos = pos;
			if (target != -1 && getCloser( target )) {
				spend( 1 / speed() );
				return moveSprite( oldPos, pos );
			} else if (!Dungeon.bossLevel() && target != -1 && target != pos && selfTeleCooldown <= 0) {
				if (sprite.extraCode instanceof GolemSprite.TeleParticles)
					((GolemSprite.TeleParticles) sprite.extraCode).showParticles(true);
				teleporting = true;
				spend( 2*TICK );
			} else {
				target = randomDestination();
				spend( TICK );
			}

			return true;
		}
	}

	private class Hunting extends Mob.Hunting{

		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {
			if (!enemyInFOV || canAttack(enemy)) {
				return super.act(enemyInFOV, justAlerted);
			} else {
				enemySeen = true;
				target = enemy.pos;

				int oldPos = pos;

				if (distance(enemy) >= 1 && Random.Int(100/distance(enemy)) == 0
						&& !Char.hasProp(enemy, Property.IMMOVABLE) && canTele(target)){
					return doRangedAttack();

				} else if (getCloser( target )) {
					spend( 1 / speed() );
					return moveSprite( oldPos,  pos );

				} else if (!Char.hasProp(enemy, Property.IMMOVABLE) && canTele(target)) {
					return doRangedAttack();

				} else {
					spend( TICK );
					return true;
				}

			}
		}
	}

}
