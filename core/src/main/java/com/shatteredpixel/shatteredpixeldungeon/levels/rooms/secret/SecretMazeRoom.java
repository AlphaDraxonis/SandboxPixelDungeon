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

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Maze;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SecretMazeRoom extends SecretRoom {
	
	@Override
	public int minWidth() {
		return 14;
	}
	
	@Override
	public int minHeight() {
		return 14;
	}
	
	@Override
	public int maxWidth() {
		return 18;
	}
	
	@Override
	public int maxHeight() {
		return 18;
	}
	
	@Override
	public void paint(Level level) {
		Painter.fill(level, this, Terrain.WALL);
		Painter.fill(level, this, 1, Terrain.EMPTY);
		
		//true = space, false = wall
		Maze.allowDiagonals = false;
		boolean[][] maze = Maze.generate(this);
		boolean[] passable = new boolean[width()*height()];
		
		Painter.fill(level, this, 1, Terrain.EMPTY);
		for (int x = 0; x < maze.length; x++) {
			for (int y = 0; y < maze[0].length; y++) {
				if (maze[x][y] == Maze.FILLED) {
					Painter.fill(level, x + left, y + top, 1, 1, Terrain.WALL);
				}
				passable[x + width()*y] = maze[x][y] == Maze.EMPTY;
			}
		}
		
		PathFinder.setMapSize(width(), height());
		Point entrance = entrance();
		int entrancePos = (entrance.x - left) + width()*(entrance.y - top);
		
		PathFinder.buildDistanceMap( entrancePos, passable );

		if (!itemsGenerated) generateItems(level);

		int posRequired = spawnItemsInRoom.size();
		List<Integer> deadEnds = new ArrayList<>();

		//most valuable item is first
		Collections.sort(spawnItemsInRoom, (i1, i2) -> Integer.compare(Item.trueValue(i2), Item.trueValue(i1)));

		idkHowToName:
		for (int i = 0; i < PathFinder.distance.length; i++){
			if (PathFinder.distance[i] != Integer.MAX_VALUE) {

				for (int j : PathFinder.NEIGHBOURS4) {
					int n = i + j;//no check for bounds required as maze has walls
					if (PathFinder.distance[n] != Integer.MAX_VALUE
							&& PathFinder.distance[n] > PathFinder.distance[i]) break idkHowToName;
				}

				deadEnds.add(i);

			}
		}
		//furthest away is first
		Collections.sort(deadEnds, (p1, p2) -> Integer.compare(p2, p1));

		int i = 0;
		int posAvailable = deadEnds.size();
		for (Item item : spawnItemsInRoom) {
			if (i >= posAvailable) i -= posAvailable;
			level.drop(item, deadEnds.get(i++)).type = Heap.Type.CHEST;
		}
		
		PathFinder.setMapSize(level.width(), level.height());
		
		entrance().set(Door.Type.HIDDEN);
	}

	private static Item prize(Level level) {
		Item prize;
		//1 floor set higher in probability, never cursed
		do {
			if (Random.Int(2) == 0) {
				prize = Generator.randomWeapon(level.levelScheme.getRegion(),true);
			} else {
				prize = Generator.randomArmor(level.levelScheme.getRegion());
			}
		} while (prize.cursed || Challenges.isItemBlocked(prize));
		prize.setCursedKnown(true);

		//33% chance for an extra update.
		if (Random.Int(3) == 0){
			prize.upgrade();
		}

		return prize;
	}

}