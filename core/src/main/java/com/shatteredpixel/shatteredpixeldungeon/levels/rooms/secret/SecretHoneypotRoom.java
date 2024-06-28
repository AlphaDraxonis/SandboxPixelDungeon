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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bee;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.watabou.utils.Point;

public class SecretHoneypotRoom extends SecretRoom {
	
	@Override
	public void paint(Level level) {
		Painter.fill( level, this, Terrain.WALL );
		Painter.fill(level, this, 1, Terrain.EMPTY );

		if (!itemsGenerated) generateItems(level);
		
		Honeypot.ShatteredPot pot = null;
		for (Item item : spawnItemsInRoom.toArray(EditorUtilies.EMPTY_ITEM_ARRAY)) {
			if (item instanceof Honeypot.ShatteredPot) {
				spawnItemsInRoom.remove(pot = (Honeypot.ShatteredPot) item);
				break;
			}
		}
		if (pot != null) {
			Point brokenPotPos = center();

			brokenPotPos.x = (brokenPotPos.x + entrance().x) / 2;
			brokenPotPos.y = (brokenPotPos.y + entrance().y) / 2;

			level.drop(pot, level.pointToCell(brokenPotPos));

			Bee bee = new Bee();
			bee.setLevel( Dungeon.depth );
			bee.HP = bee.HT;
			bee.pos = level.pointToCell(brokenPotPos);
			level.mobs.add( bee );

			bee.setPotInfo(level.pointToCell(brokenPotPos), null);
		}

		placeItemsAnywhere(level);

		entrance().set(Door.Type.HIDDEN);
	}

	@Override
	public void generateItems(Level level) {
		super.generateItems(level);

		spawnItemsInRoom.add(new Honeypot.ShatteredPot());
		spawnItemsInRoom.add(new Honeypot());
		spawnItemsInRoom.add(new Bomb().random());
	}
}