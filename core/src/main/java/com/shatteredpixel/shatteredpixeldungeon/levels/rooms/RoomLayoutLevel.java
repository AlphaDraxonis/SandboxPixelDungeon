/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
 *
 * Sandbox Pixel Dungeon
 * Copyright (C) 2023-2025 AlphaDraxonis
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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;

public class RoomLayoutLevel extends CustomLevel {

	private Room roomToGenerate;

	private static final int SIZE = 5;

	@Override
	protected boolean build() {
//		setSize(roomToGenerate.width(), roomToGenerate.height());
//		roomToGenerate.connected.clear();
//
//		roomToGenerate.connected = new LinkedHashMap<Room, Room.Door>() {
//			@Override
//			public Collection<Room.Door> values() {
//				List<Room.Door> result = new ArrayList<>();
//				result.add(new Room.Door());
//				return result;
//			}
//		};
//
//		roomToGenerate.paint(this);
//
//
//
//		roomToGenerate.neigbours.clear();
//		roomToGenerate.connected.clear();
//
//		ArrayList<Room> rooms = new ArrayList<>();
//		rooms.add(roomToGenerate);
//
//		return painter().paint(this, rooms);

		setSize(7, 7);

		for (int i=2; i < SIZE; i++) {
			for (int j=2; j < SIZE; j++) {
				map[i * width() + j] = Terrain.EMPTY;
			}
		}

		for (int i=1; i <= SIZE; i++) {
			map[width() + i] =
					map[width() * SIZE + i] =
							map[width() * i + 1] =
									map[width() * i + SIZE] =
											Terrain.WATER;
		}

		int entrance = SIZE * width() + SIZE / 2 + 1;
		//TODO Fix branch!?
		//different exit behaviour depending on main branch or side one
//		if (Dungeon.branch == 0) {
		LevelTransition t = new LevelTransition(this, entrance, LevelTransition.Type.REGULAR_ENTRANCE);
		if (Dungeon.customDungeon.getFloor(t.destLevel) != null) transitions.put(entrance, t);
//		} else {
//			LevelTransition t = new LevelTransition(this, entrance, LevelTransition.Type.BRANCH_ENTRANCE, Dungeon.depth, 0, LevelTransition.Type.BRANCH_EXIT);
//			if (Dungeon.customDungeon.getFloor(t.destLevel) != null) transitions.put(entrance, t);
//		}
		map[entrance] = Terrain.ENTRANCE;

		return true;
	}

	@Override
	protected void createMobs() {
	}

	@Override
	protected void createItems() {
	}

	@Override
	public int getRegionValue() {
		return 1;
	}

	@Override
	public int getVisualRegionValue() {
		return 1;
	}
}
