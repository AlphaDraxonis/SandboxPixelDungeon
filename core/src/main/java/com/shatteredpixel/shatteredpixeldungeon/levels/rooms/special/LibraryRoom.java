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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.TrinketCatalyst;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.watabou.utils.Random;

public class LibraryRoom extends SpecialRoom {

	{
		spawnItemsOnLevel.add(new IronKey());
	}

	public void paint( Level level ) {
		
		Painter.fill( level, this, Terrain.WALL );
		Painter.fill( level, this, 1, Terrain.EMPTY_SP );
		
		Door entrance = entrance();
		
		Painter.fill( level, left + 1, top+1, width() - 2, 1 , Terrain.BOOKSHELF );
		Painter.drawInside(level, this, entrance, 1, Terrain.EMPTY_SP );

		if (!itemsGenerated) generateItems(level);
		placeItemsAnywhere(Terrain.EMPTY_SP, level);
		
		entrance.set( Door.Type.LOCKED );
	}

	@Override
	public void generateItems(Level level) {
		super.generateItems(level);

		int n = Random.NormalIntRange( 1, 3 );

		spawnItemsInRoom.add(Random.Int(2) == 0 ? new ScrollOfIdentify() : new ScrollOfRemoveCurse());
		for (int i=1; i < n; i++) {
			spawnItemsInRoom.add(prize( level ));
		}
	}

	private static Item prize(Level level ) {
		
		Item prize = level.findPrizeItem( TrinketCatalyst.class );
		if (prize == null){
			prize = level.findPrizeItem( Scroll.class );
			if (prize == null) {
				prize = Generator.random( Generator.Category.SCROLL );
			}
		}
		
		return prize;
	}
}
