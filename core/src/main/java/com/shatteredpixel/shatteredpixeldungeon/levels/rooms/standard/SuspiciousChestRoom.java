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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.MimicTooth;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.watabou.utils.Random;

import java.util.Collections;

public class SuspiciousChestRoom extends StandardRoom {

	@Override
	public int minWidth() {
		return Math.max(5, super.minWidth());
	}

	@Override
	public int minHeight() {
		return Math.max(5, super.minHeight());
	}

	@Override
	public void paint(Level level) {
		Painter.fill( level, this, Terrain.WALL );
		Painter.fill( level, this, 1 , Terrain.EMPTY );

		for (Door door : connected.values()) {
			door.set( Door.Type.REGULAR );
		}

		int center = level.pointToCell(center());

		Painter.set(level, center, Terrain.PEDESTAL);

		if (!itemsGenerated) generateItems(level);

		//most valuable item is first
		Collections.sort(spawnItemsInRoom, (i1, i2) -> Integer.compare(Item.trueValue(i2), Item.trueValue(i1)));

		if (!spawnItemsInRoom.isEmpty()) {
			float mimicChance = 1 / 3f * MimicTooth.mimicChanceMultiplier();
			if (Random.Float() < mimicChance) {
				level.mobs.add(Mimic.spawnAt(center, spawnItemsInRoom.remove(0)));
			} else {
				level.drop(spawnItemsInRoom.remove(0), center).type = Heap.Type.CHEST;
			}
		}
		placeItemsAnywhere(level);
	}

	@Override
	public void generateItems(Level level) {
		super.generateItems(level);

		Item i = level.findPrizeItem();
		if ( i == null ) i = new Gold().random();

		spawnItemsInRoom.add(i);
	}
}