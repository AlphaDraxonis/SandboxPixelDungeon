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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CeremonialCandle;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.watabou.noosa.Tilemap;
import com.watabou.utils.Bundle;
import com.watabou.utils.Point;

public class RitualSiteRoom extends StandardRoom {

	{
		spawnItemsOnLevel.add(new CeremonialCandle());
		spawnItemsOnLevel.add(new CeremonialCandle());
		spawnItemsOnLevel.add(new CeremonialCandle());
		spawnItemsOnLevel.add(new CeremonialCandle());
	}

	private int ownRitualPos;
	
	@Override
	public int minWidth() {
		return Math.max(super.minWidth(), 9);
	}
	
	@Override
	public int minHeight() {
		return Math.max(super.minHeight(), 9);
	}

	public void paint( Level level ) {

		for (Door door : connected.values()) {
			door.set( Door.Type.REGULAR );
		}

		Painter.fill(level, this, Terrain.WALL);
		Painter.fill(level, this, 1, Terrain.EMPTY);

		RitualMarker vis = new RitualMarker();
		Point c = center();
		vis.pos(c.x, c.y);

		level.customTiles.add(vis);
		
		Painter.fill(level, c.x-1, c.y-1, 3, 3, Terrain.CUSTOM_DECO_EMPTY);

		ownRitualPos = c.x + (level.width() * c.y);
	}

	@Override
	public boolean canPlaceItem(Point p, Level l) {
		return super.canPlaceItem(p, l) && l.distance(ownRitualPos, l.pointToCell(p)) >= 2;
	}

	@Override
	public boolean canPlaceCharacter(Point p, Level l) {
		return super.canPlaceItem(p, l) && l.distance(ownRitualPos, l.pointToCell(p)) >= 2;
	}

	public static class RitualMarker extends CustomTilemap {
		
		{
			texture = Assets.Environment.PRISON_QUEST;
			
			tileW = tileH = 3;

			offsetCenterX = offsetCenterY = 1;
		}
		
		final int TEX_WIDTH = 64;

		public boolean used;//only one summon per ritual site

		@Override
		public Tilemap create() {
			Tilemap v = super.create();
			v.map(mapSimpleImage(0, 0, TEX_WIDTH), 3);
			return v;
		}

		@Override
		public String name(int tileX, int tileY) {
			return Messages.get(this, "name");
		}

		@Override
		public String desc(int tileX, int tileY) {
			return Messages.get(this, "desc");
		}

		private static final String USED = "used";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(USED, used);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			used = bundle.getBoolean(USED);
		}
	}

}