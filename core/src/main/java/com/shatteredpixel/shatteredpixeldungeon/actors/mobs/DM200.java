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
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemsWithChanceDistrComp;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DM200Sprite;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class DM200 extends DMMob {

	{
		spriteClass = DM200Sprite.class;

		HP = HT = 80;
		defenseSkill = 12;
		damageRollMin = 10;
		damageRollMax = 25;
		attackSkill = 20;
		damageReductionMax = 8;

		EXP = 9;
		maxLvl = 17;

		loot = Random.oneOf(Generator.Category.WEAPON, Generator.Category.ARMOR);
		lootChance = 0.2f; //initially, see lootChance()

		properties.add(Property.INORGANIC);
		properties.add(Property.LARGE);

		HUNTING = new Hunting();
	}

//	@Override
//	public int damageRoll() {
//		return Random.NormalIntRange( 10, 25 );
//	}
//
//	@Override
//	public int attackSkill( Char target ) {
//		return 20;
//	}
//
//	@Override
//	public int drRoll() {
//		return super.drRoll() + Random.NormalIntRange(0, 8);
//	}

	@Override
	public float lootChance(){
		//each drop makes future drops 1/3 as likely
		// so loot chance looks like: 1/5, 1/15, 1/45, 1/135, etc.
		return super.lootChance() * (float)Math.pow(1/3f, Dungeon.LimitedDrops.DM200_EQUIP.count);
	}

	public Item createLoot() {
		//uses probability tables for dwarf city
		if (loot == Generator.Category.WEAPON){
			return Generator.randomWeapon(4, true);
		} else {
			return Generator.randomArmor(4);
		}
	}

	@Override
	public ItemsWithChanceDistrComp.RandomItemData convertLootToRandomItemData() {
		ItemsWithChanceDistrComp.RandomItemData customLootInfo = super.convertLootToRandomItemData();
		Generator.convertRandomArmorToCustomLootInfo(customLootInfo, 4);
		Generator.convertRandomWeaponToCustomLootInfo(customLootInfo, 4);
		customLootInfo.setLootChance(customLootInfo.calculateSum() * 7);
		return customLootInfo;
	}

	@Override
	public void increaseLimitedDropCount(Item generatedLoot) {
		if (generatedLoot instanceof MeleeWeapon || generatedLoot instanceof Armor)
			Dungeon.LimitedDrops.DM200_EQUIP.count++;
		super.increaseLimitedDropCount(generatedLoot);
	}

	private int ventCooldown = 0;
	public int maxVentCooldown = 30;

	private static final String VENT_COOLDOWN = "vent_cooldown";
	private static final String MAX_VENT_COOLDOWN = "max_vent_cooldown";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(VENT_COOLDOWN, ventCooldown);
		bundle.put(MAX_VENT_COOLDOWN, maxVentCooldown);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		ventCooldown = bundle.getInt( VENT_COOLDOWN );
		maxVentCooldown = bundle.getInt( MAX_VENT_COOLDOWN );
	}

	@Override
	protected boolean act() {
		ventCooldown--;
		return super.act();
	}

	@Override
	public void zap(){
		spend( TICK );
		ventCooldown = maxVentCooldown;

		Ballistica trajectory = new Ballistica(pos, enemy.pos, Ballistica.STOP_TARGET, null);

		for (int i : trajectory.subPath(0, trajectory.dist)){
			GameScene.add(Blob.seed(i, 20, ToxicGas.class));
		}
		GameScene.add(Blob.seed(trajectory.collisionPos, 100, ToxicGas.class));

	}

	@Override
	public void playZapAnim(int target) {
		DM200Sprite.playZap(sprite.parent, sprite, target, this);
	}

	protected boolean canVent(int target){
		if (ventCooldown > 0) return false;
		PathFinder.buildDistanceMapForEnvironmentals(target, BArray.not(Dungeon.level.solid, null), Dungeon.level.distance(pos, target)+1);
		//vent can go around blocking terrain, but not through it
		if (PathFinder.distance[pos] == Integer.MAX_VALUE){
			return false;
		}
		return true;
	}

	private class Hunting extends Mob.Hunting{

		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {
			if (!enemyInFOV || (canAttack(enemy) && enemy.invisible <= 0)) {
				return super.act(enemyInFOV, justAlerted);
			} else {
				enemySeen = true;
				target = enemy.pos;

				int oldPos = pos;

				if (distance(enemy) >= 1 && Random.Int(100/distance(enemy)) == 0 && canVent(target)){
					return doRangedAttack();

				} else if (getCloser( target )) {
					spend( 1 / speed() );
					return moveSprite( oldPos,  pos );

				} else if (canVent(target)) {
					return doRangedAttack();

				} else {
					spend( TICK );
					return true;
				}

			}
		}
	}

}
