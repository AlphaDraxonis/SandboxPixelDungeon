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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.watabou.utils.Point;

public class CryptRoom extends SingleRewardSpecialRoom {

	{
		spawnItemsOnLevel.add(new IronKey());
	}

	public void paint( Level level ) {
		
		Painter.fill( level, this, Terrain.WALL );
		Painter.fill( level, this, 1, Terrain.EMPTY );

		Point c = center();
		int cx = c.x;
		int cy = c.y;
		
		Door entrance = entrance();

		if (entrance.x == left) {
			Painter.set( level, new Point( right-1, top+1 ), Terrain.STATUE );
			Painter.set( level, new Point( right-1, bottom-1 ), Terrain.STATUE );
			cx = right - 2;
		} else if (entrance.x == right) {
			Painter.set( level, new Point( left+1, top+1 ), Terrain.STATUE );
			Painter.set( level, new Point( left+1, bottom-1 ), Terrain.STATUE );
			cx = left + 2;
		} else if (entrance.y == top) {
			Painter.set( level, new Point( left+1, bottom-1 ), Terrain.STATUE );
			Painter.set( level, new Point( right-1, bottom-1 ), Terrain.STATUE );
			cy = bottom - 2;
		} else if (entrance.y == bottom) {
			Painter.set( level, new Point( left+1, top+1 ), Terrain.STATUE );
			Painter.set( level, new Point( right-1, top+1 ), Terrain.STATUE );
			cy = top + 2;
		}

		if (!itemsGenerated) generateItems(level);

		level.drop( prize, cx + cy * level.width() ).type = Heap.Type.TOMB;

		placeItemsAnywhere(level);

		entrance.set( Door.Type.LOCKED );
	}

	@Override
	public void generateItems(Level level) {
		super.generateItems(level);

		if (prize == null) prize = prize(level);
	}

	private static Item prize(Level level ) {
		
		//1 floor set higher than normal
		Armor prize = Generator.randomArmor( level.levelScheme.getRegion());

		if (Challenges.isItemBlocked(prize)){
			return new Gold().random();
		}

		//always generate the curse to prevent parchment scrap from altering levelgen
		Armor.Glyph curse = Armor.Glyph.randomCurse();

		//if it isn't already cursed, give it a free upgrade
		if (!prize.cursed){
			prize.upgrade();
			//curse the armor, unless it has a glyph
			if (!prize.hasGoodGlyph()){
				prize.inscribe(curse);
			}
		}
		prize.setCursedKnown(true);
		prize.cursed = true;
		
		return prize;
	}
}
