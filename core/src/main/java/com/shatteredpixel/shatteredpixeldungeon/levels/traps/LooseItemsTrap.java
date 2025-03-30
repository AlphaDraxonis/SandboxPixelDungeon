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

package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LostInventory;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class LooseItemsTrap extends Trap {

	{
		color = YELLOW;
		shape = DOTS;
	}

	@Override
	public void activate() {

		Char c = Actor.findChar( pos );

		if (c instanceof Hero) {
			Hero hero = (Hero) c;
			int stealItems = (int) Math.ceil(countStealableItems(hero) / 2f);
			int tries = 100;
			while (stealItems > 0 && tries-- > 0) {
				Item stolen = steal(hero);
				if (stolen != null) {
					stealItems--;
					if (!dropAround(stolen, c, disarmedByActivation ? PathFinder.NEIGHBOURS9 : PathFinder.NEIGHBOURS8)) {
						stolen.collect();
					}
				}
			}
		}

	}

	public static int countStealableItems(Hero hero) {
		if (hero.buff(LostInventory.class) != null) return 0;

		int count = 0;
		for (Item item : hero.belongings.backpack.items) {
			if (!item.unique) {
				count += item.quantity();
			}
		}
		return count;
	}

	public static Item steal(Hero hero) {
		Item toSteal = hero.belongings.randomUnequipped();
		if (toSteal != null && !toSteal.unique) {
			toSteal = toSteal.detach( hero.belongings.backpack );
			if (toSteal != null && !toSteal.stackable) {
				Dungeon.quickslot.convertToPlaceholder(toSteal);
			}
			return toSteal;
		}
		return null;
	}

	public static boolean dropAround(Item item, Char ch, int[] neighbours) {
		int tries = 60;
		int quantity = item.quantity();
		int[] dropQuantities = new int[neighbours.length];
		while (quantity > 0) {

			tries--;
			int dropIndex = Random.Int(neighbours.length);
			int cell = ch.pos + neighbours[dropIndex];
			if (Dungeon.level.isPassable(cell, ch)) {
				dropQuantities[dropIndex]++;
				quantity--;
				tries = 60;
			}
			else if (tries < 0) {
				break;
			}

		}

		for (int i = 0; i < dropQuantities.length; i++) {
			if (dropQuantities[i] > 0) {
				Item toDrop = item.getCopy();
				toDrop.quantity(dropQuantities[i]);
				Dungeon.level.drop(toDrop,ch.pos + neighbours[i]).sprite.drop(ch.pos);
			}
		}

		if (tries > 0) {
			return true;
		}

		item.quantity(quantity);
		return false;
	}

}
