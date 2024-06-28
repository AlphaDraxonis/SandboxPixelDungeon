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

import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;

import java.util.Collections;

public class GrassyGraveRoom extends StandardRoom {

	@Override
	public void merge(Level l, Room other, Rect merge, int mergeTerrain) {
		if (mergeTerrain == Terrain.EMPTY &&
				(other instanceof GrassyGraveRoom || other instanceof PlantsRoom)){
			super.merge(l, other, merge, Terrain.GRASS);
		} else {
			super.merge(l, other, merge, mergeTerrain);
		}
	}
	
	@Override
	public void paint(Level level) {
		
		Painter.fill( level, this, Terrain.WALL );
		for (Door door : connected.values()) {
			door.set( Door.Type.REGULAR );
		}
		
		Painter.fill( level, this, 1 , Terrain.GRASS );
		
		int w = width() - 2;
		int h = height() - 2;
		int nGraves = Math.max( w, h ) / 2;

		if (!itemsGenerated) generateItems(level);

		int itemsPerGrave = spawnItemsInRoom.size() / nGraves;
		int gravesWithMoreItems = spawnItemsInRoom.size() % nGraves;

		int[] numItemsAt = new int[nGraves];
		for (int i = 0; i < numItemsAt.length; i++) {
			int plus = 0;
			if (Random.Int(nGraves - gravesWithMoreItems) < gravesWithMoreItems) {
				plus++;
				gravesWithMoreItems--;
			}
			numItemsAt[i] = itemsPerGrave + plus;
		}
		Collections.shuffle(spawnItemsInRoom);
		
		int shift = Random.Int( 2 );
		for (int i=0; i < nGraves; i++) {
			int pos = w > h ?
					left + 1 + shift + i * 2 + (top + 2 + Random.Int( h-2 )) * level.width() :
					(left + 2 + Random.Int( w-2 )) + (top + 1 + shift + i * 2) * level.width();
			for (int j = 0; j < numItemsAt[i]; j++) {
				level.drop( spawnItemsInRoom.remove(0), pos ).type = Heap.Type.TOMB;
			}
			if (numItemsAt[i] == 0) {
				level.drop( new Gold().random(), pos ).type = Heap.Type.TOMB;
			}
		}
	}

	@Override
	public void generateItems(Level level) {
		super.generateItems(level);
		spawnItemsInRoom.add(Generator.random());
	}
}