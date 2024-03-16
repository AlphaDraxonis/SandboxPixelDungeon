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

package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.Zone;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;

public class RaisedTerrainTilemap extends DungeonTilemap {

	private final int region;

	public RaisedTerrainTilemap(int region) {
		super(CustomLevel.tilesTex(region == LevelScheme.REGION_NONE ? Dungeon.region() : region, false));

		this.region = region;

		map( CustomDungeon.isEditing() ? Dungeon.level.map : Dungeon.level.visualMap, Dungeon.level.width() );
	}
	
	@Override
	protected int getTileVisual(int pos, int tile, boolean flat) {
		
		if (flat) return -1;

		int region = Dungeon.level.visualRegions[pos];
		if (region != this.region && !(this.region == 0 && region == Dungeon.region()))
			return -1;

		if (DungeonWallsTilemap.skipCells.contains(pos)){
			return -1;
		}

		Zone.GrassType grassType = Zone.getGrassType(Dungeon.level, pos);
		if (grassType != Zone.GrassType.NONE)
			tile = grassType.terrain;

		if (tile == Terrain.HIGH_GRASS){
			return DungeonTileSheet.getVisualWithAlts(
					DungeonTileSheet.HIGH_GRASS_UNDERHANG,
					pos);
		} else if (tile == Terrain.FURROWED_GRASS){
			return DungeonTileSheet.getVisualWithAlts(
					DungeonTileSheet.FURROWED_UNDERHANG,
					pos);
		}
		
		return -1;
	}
}