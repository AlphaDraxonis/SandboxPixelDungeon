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
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemsWithChanceDistrComp;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Spear;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GnollGuardSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class GnollGuard extends Mob {

	{
		spriteClass = GnollGuardSprite.class;

		HP = HT = 35;
		defenseSkill = 15;
		attackSkill = 20;
		damageRollMin = 6;
		damageRollMax = 12;
		specialDamageRollMin = 16;
		specialDamageRollMax = 22;
		damageReductionMax = 6;

		EXP = 7;
		maxLvl = -2;

		loot = Spear.class;
		lootChance = 0.1f;

		WANDERING = new Wandering();
	}

	private int sapperID = -1;

	public void linkSapper( GnollSapper sapper){
		this.sapperID = sapper.id();
		if (sprite != null && sprite.extraCode instanceof GnollGuardSprite.EarthArmor) {
			((GnollGuardSprite.EarthArmor) sprite.extraCode).setupArmor(sprite);
		}
	}

	public boolean hasSapper(){
		return sapperID != -1
				&& Actor.findById(sapperID) instanceof GnollSapper
				&& ((GnollSapper)Actor.findById(sapperID)).isAlive();
	}

	public void loseSapper(){
		if (sapperID != -1){
			sapperID = -1;
			if (sprite != null && sprite.extraCode instanceof GnollGuardSprite.EarthArmor){
				((GnollGuardSprite.EarthArmor) sprite.extraCode).loseArmor();
			}
		}
	}

	@Override
	public void damage(int dmg, Object src) {
		if (hasSapper()) dmg /= 4;
		super.damage(dmg, src);
	}

	@Override
	public int damageRoll() {
		if (enemy != null && !Dungeon.level.adjacent(pos, enemy.pos)){
			return Random.NormalIntRange( specialDamageRollMin, specialDamageRollMax );
		} else {
			return super.damageRoll();
		}
	}

//	@Override
//	public int attackSkill( Char target ) {
//		return 20;
//	}
//
//	@Override
//	public int drRoll() {
//		return super.drRoll() + Random.NormalIntRange(0, 6);
//	}

	@Override
	public int attackProc(Char enemy, int damage) {
		int dmg = super.attackProc(enemy, damage);
		if (enemy == Dungeon.hero && !Dungeon.level.adjacent(pos, enemy.pos) && dmg > 12){
			GLog.n(Messages.get(this, "spear_warn"));
		}
		return dmg;
	}

	@Override
	protected boolean canAttack( Char enemy ) {
		//cannot 'curve' spear hits like the hero, requires fairly open space to hit at a distance
		return Dungeon.level.distance(enemy.pos, pos) <= 2
				&& new Ballistica( pos, enemy.pos, Ballistica.REAL_PROJECTILE, null).collisionPos == enemy.pos
				&& new Ballistica( enemy.pos, pos, Ballistica.REAL_PROJECTILE, null).collisionPos == pos;
	}

	@Override
	public ItemsWithChanceDistrComp.RandomItemData convertLootToRandomItemData() {
		//Not a real representation since normally it would use Spear#random()
		ItemsWithChanceDistrComp.RandomItemData customLootInfo = super.convertLootToRandomItemData();
		Spear spear = new Spear();
		spear.level(0);
		spear.cursed = false;
		spear.enchant(null);
		customLootInfo.addItem(spear, 1);
		customLootInfo.setLootChance(9);
		return customLootInfo;
	}
	@Override
	public String desc() {
		if (hasSapper()){
			return super.desc() + "\n\n" + Messages.get(this, "desc_armor");
		} else {
			return super.desc();
		}
	}

	private static final String SAPPER_ID = "sapper_id";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(SAPPER_ID, sapperID);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		sapperID = bundle.getInt(SAPPER_ID);
	}

	public class Wandering extends Mob.Wandering {
		@Override
		protected int randomDestination() {
			if (hasSapper()){
				return ((GnollSapper)Actor.findById(sapperID)).pos;
			} else {
				return super.randomDestination();
			}
		}
	}

}
