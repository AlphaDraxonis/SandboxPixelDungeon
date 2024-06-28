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

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.items.food.ChargrilledMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Pasty;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.plants.BlandfruitBush;
import com.watabou.utils.Point;

public class SecretLarderRoom extends SecretRoom {
	
	@Override
	public int minHeight() {
		return 6;
	}
	
	@Override
	public int minWidth() {
		return 6;
	}
	
	@Override
	public void paint(Level level) {
		Painter.fill(level, this, Terrain.WALL);
		Painter.fill(level, this, 1, Terrain.EMPTY_SP);
		
		Point c = center();
		
		Painter.fill(level, c.x-1, c.y-1, 3, 3, Terrain.WATER);
		Painter.set(level, c, Terrain.GRASS);
		
		level.plant(new BlandfruitBush.Seed(), level.pointToCell(c));

		if (!itemsGenerated) generateItems(level);
		placeItemsAnywhere(Terrain.EMPTY_SP, level);
		
		entrance().set(Door.Type.HIDDEN);
	}

	@Override
	public void generateItems(Level level) {
		super.generateItems(level);

		int extraFood = (int)(Hunger.STARVING - Hunger.HUNGRY) * (level.levelScheme.getRegion());

		while (extraFood > 0){
			if (extraFood >= Hunger.STARVING){
				spawnItemsInRoom.add(new Pasty());
				extraFood -= Hunger.STARVING;
			} else {
				spawnItemsInRoom.add(new ChargrilledMeat());
				extraFood -= (Hunger.STARVING - Hunger.HUNGRY);
			}
		}
	}
}