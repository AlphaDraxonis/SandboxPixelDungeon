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

package com.alphadraxonis.sandboxpixeldungeon.levels.rooms.quest;

import com.alphadraxonis.sandboxpixeldungeon.editor.quests.BlacksmithQuest;
import com.alphadraxonis.sandboxpixeldungeon.items.Heap;
import com.alphadraxonis.sandboxpixeldungeon.items.quest.DarkGold;
import com.alphadraxonis.sandboxpixeldungeon.levels.Level;
import com.alphadraxonis.sandboxpixeldungeon.levels.MiningLevel;
import com.alphadraxonis.sandboxpixeldungeon.levels.Terrain;
import com.alphadraxonis.sandboxpixeldungeon.levels.painters.Painter;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.secret.SecretRoom;
import com.watabou.utils.Random;

public class MineSecretRoom extends SecretRoom {

	@Override
	public int maxWidth() { return 7; }

	@Override
	public int maxHeight() { return 7; }

	@Override
	public void paint(Level level) {
		Painter.fill( level, this, Terrain.WALL );

		entrance().set( Door.Type.HIDDEN );

		int questType = level instanceof MiningLevel ? ((MiningLevel) level).quest.type() : BlacksmithQuest.NONE;
		if (questType == BlacksmithQuest.CRYSTAL) {
			Painter.fill(level, this, 1, Terrain.MINE_CRYSTAL);
		} else if (questType == BlacksmithQuest.FUNGI) {
			Painter.fill(level, this, 1, Terrain.HIGH_GRASS);

			//random plant?

		} else if (questType == BlacksmithQuest.GNOLL) {
			Painter.fill( level, this, 1, Terrain.EMPTY_SP );
			level.drop(new DarkGold().quantity(Random.NormalIntRange(3, 5)), level.pointToCell(center())).type = Heap.Type.CHEST;
			return;
		} else {
			Painter.fill(level, this, 1, Terrain.EMPTY);
		}

		int goldAmount = Random.NormalIntRange(3, 5);

		for (int i = 0; i < goldAmount; i++){
			int cell;
			do {
				cell = level.pointToCell(random(1));
			} while (level.map[cell] == Terrain.WALL_DECO);
			Painter.set(level, random(1), Terrain.WALL_DECO);
		}

	}
}