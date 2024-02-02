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

package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class LooseItemsTrap extends Trap {

	{
		color = GREY;
		shape = DOTS;
	}

	@Override
	public void activate() {

		Char c = Actor.findChar( pos );

		if (c instanceof Hero) {
			Hero hero = (Hero) c;
			int stealItems = hero.belongings.backpack.items.size() / 2;
			int tries = 100;
			while (stealItems > 0 && tries-- > 0) {
				Item stolen = steal(hero);
				if (stolen != null) {
					stealItems--;
					dropAround(stolen, c);
				}
			}
		}

	}

	public static Item steal(Hero hero) {
		Item toSteal = hero.belongings.randomUnequipped();
		if (toSteal != null && !toSteal.unique) {
			toSteal.detach( hero.belongings.backpack );
			if (!toSteal.stackable) {
				Dungeon.quickslot.convertToPlaceholder(toSteal);
			}
			return toSteal;
		}
		return null;
	}

	public static boolean dropAround(Item item, Char ch) {
		int tries = 50;
		int quantity = item.quantity();
		while (quantity > 0) {

			tries--;
			int cell = ch.pos + PathFinder.NEIGHBOURS8[Random.Int(8)];
			if (Dungeon.level.isPassable(cell, ch)) {
				Item toDrop = item.getCopy();
				toDrop.quantity(1);
				quantity--;
				tries = 50;
				Dungeon.level.drop( toDrop, cell ).sprite.drop( Dungeon.level.heaps.get(cell) == null ? ch.pos : cell);
			}
			else if (tries < 0) {
				break;
			}

		}
		if (tries > 0) {
			return true;
		}

		item.quantity(quantity);
		return false;
	}

}