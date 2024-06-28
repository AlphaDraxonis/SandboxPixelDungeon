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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret;

import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.GoldenKey;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLevitation;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class SecretChestChasmRoom extends SecretRoom {

	{
		spawnItemsOnLevel.add(new PotionOfLevitation());
	}
	
	//width and height are controlled here so that this room always requires 2 levitation potions
	
	@Override
	public int minWidth() {
		return 8;
	}
	
	@Override
	public int maxWidth() {
		return 9;
	}
	
	@Override
	public int minHeight() {
		return 8;
	}
	
	@Override
	public int maxHeight() {
		return 9;
	}

	@Override
	public void paint(Level level) {
		Painter.fill(level, this, Terrain.WALL);
		Painter.fill(level, this, 1, Terrain.CHASM);

		if (!itemsGenerated) generateItems(level);
		
		int chests = 0;

		Point[] chestPositions = chestPositions();
		for (int i = 0; i < chestPositions.length; i++) {
			fillChest(1, chestPositions[i], level);
			if (level.heaps.get(level.pointToCell(chestPositions[i])) != null) chests++;
			else chestPositions[i] = null;
		}

		int itemsPerChest = spawnItemsInRoom.size() / chests;
		int chestsWithMoreItems = spawnItemsInRoom.size() % chests;

		for (Point p : chestPositions) {
			if (p == null) continue;
			int plus = 0;
			if (Random.Int(chests - chestsWithMoreItems) < chestsWithMoreItems) {
				plus++;
				chestsWithMoreItems--;
			}
			fillChest(itemsPerChest + plus, p, level);
		}

		for (Point p : chestPositions()) {
			Painter.set(level, p, Terrain.EMPTY_SP);
			if (chests > 0) {
				level.drop(new GoldenKey(), level.pointToCell(p));
				chests--;
			}
		}
		
		entrance().set(Door.Type.HIDDEN);
	}

	private void fillChest(int numItems, Point p, Level level) {
		if (!spawnItemsInRoom.isEmpty()) {
			level.drop(spawnItemsInRoom.get(0), level.pointToCell(p)).type = Heap.Type.LOCKED_CHEST;
			spawnItemsInRoom.remove(0);
		}
	}

	private Point[] chestPositions() {
		return new Point[] {
				new Point(left+3, top+3),
				new Point(right-3, top+3),
				new Point(right-3, bottom-3),
				new Point(left+3, bottom-3)
		};
	}

	@Override
	public void generateItems(Level level) {
		super.generateItems(level);

		for (int i = 0; i < 4; i++) {
			spawnItemsInRoom.add(Generator.randomUsingDefaults());
		}
	}
}