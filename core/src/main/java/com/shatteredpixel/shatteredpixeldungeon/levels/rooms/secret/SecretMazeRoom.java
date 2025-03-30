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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret;

import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Maze;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

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
		
		PathFinder.buildDistanceMap( entrancePos, passable, null );

		if (!itemsGenerated) generateItems(level);

		//furthest away is first
//		Map<Integer, Integer> deadEnds = new TreeMap<>((k1, k2) -> {
//			int compare = Integer.compare(PathFinder.distance[k2], PathFinder.distance[k1]);
//			return compare == 0
//					? k1.compareTo(k2)
//					: compare;
//		});
//
//		//most valuable item is first
//		Collections.sort(spawnItemsInRoom, (i1, i2) -> Integer.compare(Item.trueValue(i2), Item.trueValue(i1)));
//		spawnItemsInRoom.add(new Gold());
//
//		PathFinder.buildDistanceMap(entrance.x - left, passable);
//
//		checkOneCell:
//		for (int i = 0; i < PathFinder.distance.length; i++){
//			if (PathFinder.distance[i] != Integer.MAX_VALUE) {
//
//				for (int j : PathFinder.NEIGHBOURS4) {
//					int n = i + j;//no check for bounds required as maze has walls
//					if (PathFinder.distance[n] != Integer.MAX_VALUE
//							&& PathFinder.distance[n] > PathFinder.distance[i]) continue checkOneCell;
//				}
//
//				deadEnds.put(i, PathFinder.distance[i]);
//
//			}
//		}
//
//		PathFinder.setMapSize(level.width(), level.height());
//
//		int itemsRemaining = spawnItemsInRoom.size();
//		dropItems:
//		while (true) {
//			for (int pos : deadEnds.keySet()) {
//				level.drop(spawnItemsInRoom.remove(0), level.pointToCell(new Point(left + pos%width(),top +  pos/width()))).type = Heap.Type.CHEST;
//				itemsRemaining--;
//				if (itemsRemaining <= 0) break dropItems;
//			}
//		}

		int bestDist = 0;
		Point bestDistP = new Point();
		for (int i = 0; i < PathFinder.distance.length; i++){
			if (PathFinder.distance[i] != Integer.MAX_VALUE
					&& PathFinder.distance[i] > bestDist){
				bestDist = PathFinder.distance[i];
				bestDistP.x = (i % width()) + left;
				bestDistP.y = (i / width()) + top;
			}
		}

		level.drop(spawnItemsInRoom.remove(0), level.pointToCell(bestDistP)).type = Heap.Type.CHEST;

		PathFinder.setMapSize(level.width(), level.height());


		entrance().set(Door.Type.HIDDEN);
	}

	@Override
	public void generateItems(Level level) {
		super.generateItems(level);

		spawnItemsInRoom.add(prize(level));
	}

	private static Item prize(Level level) {
		Item prize;
		//1 floor set higher in probability, never cursed
		if (Random.Int(2) == 0) {
			prize = Generator.randomWeapon(level.levelScheme.getRegion(),true);
			if (((Weapon)prize).hasCurseEnchant()){
				((Weapon) prize).enchant(null);
			}
		} else {
			prize = Generator.randomArmor(level.levelScheme.getRegion());
			if (((Armor)prize).hasCurseGlyph()){
				((Armor) prize).inscribe(null);
			}
		}
		prize.cursed = false;
		prize.setCursedKnown(true);
		//33% chance for an extra update.
		if (Random.Int(3) == 0){
			prize.upgrade();
		}

		return prize;
	}

}
