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

package com.alphadraxonis.sandboxpixeldungeon.actors.mobs;

import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.actors.Char;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.Buff;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.Cripple;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.Light;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.stateditor.LootTableComp;
import com.alphadraxonis.sandboxpixeldungeon.items.Generator;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.Potion;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.PotionOfHealing;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.PotionOfStrength;
import com.alphadraxonis.sandboxpixeldungeon.mechanics.Ballistica;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ScorpioSprite;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

public class Scorpio extends Mob {
	
	{
		spriteClass = ScorpioSprite.class;
		
		HP = HT = 110;
		defenseSkill = 24;
		attackSkill = 36;
		damageRollMin = 30;
		damageRollMax = 40;
		damageReductionMax = 16;
		viewDistance = Light.DISTANCE;
		
		EXP = 14;
		maxLvl = 27;
		
		loot = Generator.Category.POTION;
		lootChance = 0.5f;

		properties.add(Property.DEMONIC);
	}
	
//	@Override
//	public int damageRoll() {
//		return Random.NormalIntRange( 30, 40 );
//	}
//
//	@Override
//	public int attackSkill( Char target ) {
//		return 36;
//	}
//
//	@Override
//	public int drRoll() {
//		return super.drRoll() + Random.NormalIntRange(0, 16);
//	}
	
	@Override
	protected boolean canAttack( Char enemy ) {
		return !Dungeon.level.adjacent( pos, enemy.pos )
				&& (super.canAttack(enemy) || new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE).collisionPos == enemy.pos);
	}
	
	@Override
	public int attackProc( Char enemy, int damage ) {
		damage = super.attackProc( enemy, damage );
		if (Random.Int( 2 ) == 0) {
			Buff.prolong( enemy, Cripple.class, Cripple.DURATION );
		}
		
		return damage;
	}
	
	@Override
	protected boolean getCloser( int target ) {
		if (state == HUNTING) {
			return enemySeen && getFurther( target );
		} else {
			return super.getCloser( target );
		}
	}
	
	@Override
	public void aggro(Char ch) {
		//cannot be aggroed to something it can't see
		if (ch == null || fieldOfView == null || fieldOfView[ch.pos]) {
			super.aggro(ch);
		}
	}

	@Override
	public Item createLoot() {
		Class<?extends Potion> loot;
		do{
			loot = (Class<? extends Potion>) Random.oneOf(Generator.Category.POTION.classes);
		} while (loot == PotionOfHealing.class || loot == PotionOfStrength.class);

		return Reflection.newInstance(loot);
	}

	@Override
	public LootTableComp.CustomLootInfo convertToCustomLootInfo() {
		LootTableComp.CustomLootInfo customLootInfo = super.convertToCustomLootInfo();

		for (Class<?> cl : Generator.Category.POTION.classes) {
			if (cl != PotionOfHealing.class && cl != PotionOfStrength.class) {
				Potion p = (Potion) Reflection.newInstance(cl);
				customLootInfo.addItem(p, 1);
			}
		}
		customLootInfo.setLootChance(customLootInfo.calculateSum());
		return customLootInfo;
	}
	
}