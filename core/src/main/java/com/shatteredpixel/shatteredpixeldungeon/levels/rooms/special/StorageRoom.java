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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.watabou.utils.Random;

public class StorageRoom extends SpecialRoom {

	{
		spawnItemsOnLevel.add(new PotionOfLiquidFlame());
	}

	public void paint( Level level ) {
		
		Painter.fill( level, this, Terrain.WALL );
		Painter.fill( level, this, 1, Terrain.EMPTY_SP );

		if (!itemsGenerated) generateItems(level);
		placeItemsAnywhere(Terrain.EMPTY_SP, level);
		
		entrance().set( Door.Type.BARRICADE );
	}

	@Override
	public void generateItems(Level level) {
		super.generateItems(level);

		boolean honeyPot = Random.Int( 2 ) == 0;

		int n = Random.IntRange( 3, 4 );
		for (int i=0; i < n; i++) {
			if (honeyPot) {
				spawnItemsInRoom.add(new Honeypot());
				honeyPot = false;
			} else {
				spawnItemsInRoom.add(prize(level));
			}
		}
	}

	private static Item prize(Level level ) {

		if (Random.Int(3) != 0){
			Item prize = level.findPrizeItem();
			if (prize != null)
				return prize;
		}
		
		return Generator.random( Random.oneOf(
			Generator.Category.POTION,
			Generator.Category.SCROLL,
			Generator.Category.FOOD,
			Generator.Category.GOLD
		) );
	}
}