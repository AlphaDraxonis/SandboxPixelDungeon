/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2023 Evan Debenham
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

package com.alphadraxonis.sandboxpixeldungeon.levels.rooms.standard;

import com.alphadraxonis.sandboxpixeldungeon.items.Generator;
import com.alphadraxonis.sandboxpixeldungeon.items.Gold;
import com.alphadraxonis.sandboxpixeldungeon.items.Heap;
import com.alphadraxonis.sandboxpixeldungeon.levels.Level;
import com.alphadraxonis.sandboxpixeldungeon.levels.Terrain;
import com.alphadraxonis.sandboxpixeldungeon.levels.painters.Painter;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.Room;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;

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
		
		int index = Random.Int( nGraves );
		
		int shift = Random.Int( 2 );
		for (int i=0; i < nGraves; i++) {
			int pos = w > h ?
					left + 1 + shift + i * 2 + (top + 2 + Random.Int( h-2 )) * level.width() :
					(left + 2 + Random.Int( w-2 )) + (top + 1 + shift + i * 2) * level.width();
			level.drop( i == index ? Generator.random() : new Gold().random(), pos ).type = Heap.Type.TOMB;
		}
	}
}