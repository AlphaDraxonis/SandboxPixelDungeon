/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
 *
 * Sandbox Pixel Dungeon
 * Copyright (C) 2023-2024 AlphaDraxonis
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

package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.TileSprite;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerLikeButton;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTileSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Image;

public class TileVarianceSpinner extends SpinnerLikeButton {
	
	public TileVarianceSpinner(int terrain, int pos) {
		super(new SpinnerIntegerModel(0, numVariantsForTerrain(terrain), Math.min(numVariantsForTerrain(terrain), Dungeon.level.getTileVarianceAt(pos)), true) {
			@Override
			protected String displayString(Object value) {
				return "";
			}
			
			@Override
			protected Image displayIcon(Object value) {
				if (((int) value) == 0) {
					return new ItemSprite(ItemSpriteSheet.RANDOM_ITEM) {
						{
							height = width = DungeonTilemap.SIZE;
						}
					};
				}
				return TileSprite.createTilespriteWithImage(
						Level.tilesTex(Dungeon.level.visualRegions[pos], terrain == Terrain.WATER),
						DungeonTileSheet.getVisualForSpinner(terrain, (Integer) value)
				);
			}
		}, Messages.get(TileVarianceSpinner.class, "label"));
	}
	
	public static int numVariantsForTerrain(int terrain) {
		switch (terrain) {
			case Terrain.EMPTY_SP:
			case Terrain.EMPTY_DECO:
			case Terrain.GRASS:
			case Terrain.HIGH_GRASS:
			case Terrain.FURROWED_GRASS:
			case Terrain.EMBERS:
			case Terrain.WALL:
			case Terrain.WALL_DECO:
			case Terrain.BOOKSHELF:
				return 2;
			case Terrain.EMPTY:
			case Terrain.MINE_CRYSTAL:
			case Terrain.MINE_BOULDER:
				return 3;
			default:
				return 1;
			
		}
	}
	
	
}