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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.stateditor.DefaultStatsCache;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.stateditor.LootTableComp;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SkeletonSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Skeleton extends Mob {
	
	{
		spriteClass = SkeletonSprite.class;
		
		HP = HT = 25;
		defenseSkill = 9;
		attackSkill = 12;
		damageRollMin = 2;
		damageRollMax = 10;
		explosionDamageRollMin = 6;
		explosionDamageRollMax = 12;
		damageReductionMax = 5;
		
		EXP = 5;
		maxLvl = 10;

		loot = Generator.Category.WEAPON;
		lootChance = 0.1667f; //by default, see lootChance()

		properties.add(Property.UNDEAD);
		properties.add(Property.INORGANIC);
	}
	
//	@Override
//	public int damageRoll() {
//		return Random.NormalIntRange( 2, 10 );
//	}

	public int explosionDamageRollMin, explosionDamageRollMax;
	
	@Override
	public void die( Object cause ) {
		
		super.die( cause );
		
		if (cause == Chasm.class) return;
		
		boolean heroKilled = false;
		for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
			Char ch = findChar( pos + PathFinder.NEIGHBOURS8[i] );
			if (ch != null && ch.isAlive()) {
				int damage = Math.round(Random.NormalIntRange(explosionDamageRollMin, explosionDamageRollMax));
				damage = Math.round( damage * AscensionChallenge.statModifier(this));
				damage = Math.max( 0,  damage - (ch.drRoll() + ch.drRoll()) );
				ch.damage( damage, this );
				if (ch == Dungeon.hero && !ch.isAlive()) {
					heroKilled = true;
				}
			}
		}
		
		if (Dungeon.level.heroFOV[pos]) {
			Sample.INSTANCE.play( Assets.Sounds.BONES );
		}
		
		if (heroKilled) {
			Dungeon.fail( this );
			GLog.n( Messages.get(this, "explo_kill") );
		}
	}

	@Override
	public float lootChance() {
		//each drop makes future drops 1/2 as likely
		// so loot chance looks like: 1/6, 1/12, 1/24, 1/48, etc.
		return super.lootChance() * (float)Math.pow(1/2f, Dungeon.LimitedDrops.SKELE_WEP.count);
	}


	@Override
	public LootTableComp.CustomLootInfo convertToCustomLootInfo() {
		LootTableComp.CustomLootInfo customLootInfo = super.convertToCustomLootInfo();
		int set;
		if (Dungeon.level == null || Dungeon.level.levelScheme == null) set = (int) (Math.random() * 5);
		else set = Dungeon.level.levelScheme.getRegion() - 1;
		Generator.convertRandomWeaponToCustomLootInfo(customLootInfo, set);
		customLootInfo.setLootChance(customLootInfo.calculateSum() * 5);
		return customLootInfo;
	}

	@Override
	public void increaseLimitedDropCount(Item generatedLoot) {
		if (generatedLoot instanceof MeleeWeapon)
			Dungeon.LimitedDrops.SKELE_WEP.count++;
		super.increaseLimitedDropCount(generatedLoot);
	}

	//	@Override
//	public int attackSkill( Char target ) {
//		return 12;
//	}
//
//	@Override
//	public int drRoll() {
//		return super.drRoll() + Random.NormalIntRange(0, 5);
//	}


	private static final String EXPLOSION_DAMAGE_ROLL_MIN = "explosion_damage_roll_min";
	private static final String EXPLOSION_DAMAGE_ROLL_MAX = "explosion_damage_roll_max";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		Skeleton defaultMob = DefaultStatsCache.getDefaultObject(getClass());
		if (defaultMob != null) {
			if (defaultMob.explosionDamageRollMin != explosionDamageRollMin) bundle.put(EXPLOSION_DAMAGE_ROLL_MIN, explosionDamageRollMin);
			if (defaultMob.explosionDamageRollMax != explosionDamageRollMax) bundle.put(EXPLOSION_DAMAGE_ROLL_MAX, explosionDamageRollMax);
		}
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (bundle.contains(EXPLOSION_DAMAGE_ROLL_MIN)) explosionDamageRollMin = bundle.getInt(EXPLOSION_DAMAGE_ROLL_MIN);
		if (bundle.contains(EXPLOSION_DAMAGE_ROLL_MAX)) explosionDamageRollMax = bundle.getInt(EXPLOSION_DAMAGE_ROLL_MAX);
	}
}