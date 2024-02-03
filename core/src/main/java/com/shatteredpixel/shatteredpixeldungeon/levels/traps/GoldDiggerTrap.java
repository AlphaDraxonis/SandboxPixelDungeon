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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class GoldDiggerTrap extends Trap {

	{
		color = YELLOW;
		shape = DIAMOND;
	}

	@Override
	public void activate() {

		Char c = Actor.findChar( pos );

		Gold gold = null;
		if (c instanceof Hero) {
			int dropGold = new Gold().random().quantity() * Random.Int(4, 10);
			if (Dungeon.gold >= dropGold) {
				Dungeon.gold -= dropGold;
			} else {
				dropGold = Dungeon.gold;
				Dungeon.gold = 0;
			}
			gold = new Gold();
			gold.quantity(dropGold);

		} else if (c instanceof Mob && ((Mob) c).loot == Gold.class ) {
			gold = (Gold) Generator.random(Gold.class);
			((Mob) c).loot = null;
		}

		if (gold != null) {
			if (!LooseItemsTrap.dropAround(gold, c, disarmedByActivation ? PathFinder.NEIGHBOURS9 : PathFinder.NEIGHBOURS8))
				Dungeon.gold += gold.quantity();
		}

	}

}