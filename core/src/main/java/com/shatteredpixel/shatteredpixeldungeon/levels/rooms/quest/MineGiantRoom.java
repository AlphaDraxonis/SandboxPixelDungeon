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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.CrystalSpire;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.BlacksmithQuest;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.MiningLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.CaveRoom;
import com.watabou.utils.GameMath;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class MineGiantRoom extends CaveRoom {

	@Override
	public float[] sizeCatProbs() {
		return new float[]{0, 0, 1};
	}

	@Override
	protected float fill() {
		return 0.70f;
	}

	@Override
	public void paint(Level level) {
		super.paint(level);

		int questType = level instanceof MiningLevel ? ((MiningLevel) level).quest.type() : BlacksmithQuest.NONE;
		if (questType == BlacksmithQuest.CRYSTAL){
			Painter.fillEllipse(level, this, 3, Terrain.EMPTY);

			for (int i = 0; i < width()*height()/2; i ++){
				Point r = random(1);
				if (level.map[level.pointToCell(r)] != Terrain.WALL) {
					Painter.set(level, r, Terrain.MINE_CRYSTAL);
				}
			}

			Point p = center();
			CrystalSpire m = new CrystalSpire();
			m.pos = level.pointToCell(p);
			level.mobs.add(m);
			Painter.set(level, p, Terrain.EMPTY);

		} else if (questType == BlacksmithQuest.GNOLL){
			Painter.fillEllipse(level, this, 3, Terrain.EMPTY);

			//connections to non-secret rooms have a 9/10 chance to become empty, otherwise wall
			for (Room n : connected.keySet()){
				if (!(n instanceof SecretRoom) && connected.get(n).type == Door.Type.REGULAR){
					if (Random.Int(10) == 0){
						connected.get(n).set(Door.Type.EMPTY);
					} else {
						connected.get(n).set(Door.Type.WALL);
					}
					connected.get(n).lockTypeChanges(true);
				}
			}

			ArrayList<Door> doors = new ArrayList<>();
			for (Door d : connected.values()){
				if (d.type == Door.Type.WALL){
					doors.add(d);
				}
			}

			for (Point p : getPoints()){
				int cell = level.pointToCell(p);
				if (level.map[cell] == Terrain.EMPTY){
					float dist = 1000;
					for (Door d : doors){
						dist = Math.min(dist, Point.distance(p, d));
					}
					dist = GameMath.gate(1f, dist-0.5f, 3f);
					float val = Random.Float((float) Math.pow(dist, 2));
					if (val <= 0.75f) {
						Painter.set(level, cell, Terrain.MINE_BOULDER);
					} else if (val <= 3f && dist <= 3){
						Painter.set(level, cell, Terrain.EMPTY_DECO);
					}
				}
			}

			for (int i = 0; i < 8; i ++){
				Point r = random(5);
				if (level.map[level.pointToCell(r)] != Terrain.WALL) {
					Painter.set(level, r, Terrain.BARRICADE);
				}
			}

		} else if (questType == BlacksmithQuest.FUNGI){
			Painter.fillEllipse(level, this, 3, Terrain.EMPTY);

			for (int i = 0; i < width()*height()/2; i ++){
				Point r = random(1);
				if (level.map[level.pointToCell(r)] != Terrain.WALL) {
					Painter.set(level, r, Terrain.HIGH_GRASS);
				}
			}

		} else {
			Painter.fillEllipse(level, this, 3, Terrain.EMPTY);
		}

	}

}