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

package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.Tilemap;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;

public abstract class DungeonTilemap extends Tilemap {

	public static final int SIZE = 16;

	protected int[] map;

	public DungeonTilemap(String tex) {
		super(tex, new TextureFilm( tex, SIZE, SIZE ) );
		hardlight(Window.TITLE_COLOR);
	}

	@Override
	//we need to retain two arrays, map is the dungeon tilemap which we can reference.
	// Data is our own internal image representation of the tiles, which may differ.
	public void map(int[] data, int cols) {
		map = data;
		super.map(new int[data.length], cols);
	}

	@Override
	public synchronized void updateMap() {
		boolean view2d = CustomDungeon.isEditing() || Dungeon.customDungeon.view2d;
		for (int i = 0; i < data.length; i++)
			data[i] = getTileVisual(i ,map[i], view2d);
		super.updateMap();
	}

	@Override
	public synchronized void updateMapCell(int cell) {
		boolean view2d = CustomDungeon.isEditing() || Dungeon.customDungeon.view2d;
		//update in a 3x3 grid to account for neighbours which might also be affected
//		if (Dungeon.level.insideMap(cell)) {
			for (int i : PathFinder.NEIGHBOURS9) {
				int index = cell + i;
				if (index >= 0 && index < data.length)
					data[index] = getTileVisual(index, map[index], view2d);
			}
			if(Dungeon.level.insideMap(cell)){
				super.updateMapCell(cell - mapWidth - 1);
				super.updateMapCell(cell + mapWidth + 1);
			}else{
				for (int i : PathFinder.NEIGHBOURS9) {
					int index = cell + i;
					if (index >= 0 && index < data.length)
						super.updateMapCell(index);
				}
			}

//		//unless we're at the level's edge, then just do the one tile.
//		} else {
//			data[cell] = getTileVisual(cell, map[cell], mapEditing);
//			super.updateMapCell(cell);
//		}
	}

	protected abstract int getTileVisual(int pos, int tile, boolean flat);

	public int screenToTile(int x, int y ){
		return screenToTile(x, y, false);
	}

	private static final float BORDER_SNAPPING = 0.5f;
	//wall assist is used to make raised perspective tapping a bit easier.
	// If the pressed tile is a wall tile, the tap can be 'bumped' down into a none-wall tile.
	// currently this happens if the bottom 1/4 of the wall tile is pressed.
	public int screenToTile(int x, int y, boolean wallAssist ) {
		PointF p = camera().screenToCamera( x, y ).
			offset( this.point().negate() ).
			invScale( SIZE );

		//snap to the edges of the tilemap
//		p.x = GameMath.gate(0, p.x, Dungeon.level.width()-0.001f);
//		p.y = GameMath.gate(0, p.y, Dungeon.level.height()-0.001f);
		if (p.x < 0) {
			if (p.x >= -BORDER_SNAPPING) p.x = 0;
			else return -1;
		} else {
			float maxW = Dungeon.level.width() - 0.001f;
			if (p.x > maxW) {
				if (p.x < maxW + BORDER_SNAPPING) p.x = maxW;
				else return -1;
			}
		}
		if (p.y < 0) {
			if (p.y >= -BORDER_SNAPPING) p.y = 0;
			else return -1;
		} else {
			float maxH = Dungeon.level.height() - 0.001f;
			if (p.y > maxH) {
				if (p.y < maxH + BORDER_SNAPPING) p.y = maxH;
				else return -1;
			}
		}

		int cell = (int)p.x + (int)p.y * Dungeon.level.width();
		if (cell >= Dungeon.level.length() || cell < 0) return -1;

		if (wallAssist
				&& map != null
				&& DungeonTileSheet.wallStitcheable(map[cell])){

			if (cell + mapWidth < size
					&& p.y % 1 >= 0.75f
					&& !DungeonTileSheet.wallStitcheable(map[cell + mapWidth])){
				cell += mapWidth;
			}

		}

		return cell;
	}

	@Override
	public boolean overlapsPoint( float x, float y ) {
		return true;
	}

	public void discover( int pos, int oldValue ) {

		int visual = getTileVisual( pos, oldValue, Dungeon.customDungeon.view2d);
		if (visual < 0) return;

		final Image tile = new Image( texture );
		tile.frame( tileset.get( getTileVisual( pos, oldValue, Dungeon.customDungeon.view2d)));
		tile.point( tileToWorld( pos ) );

		parent.add( tile );

		parent.add( new AlphaTweener( tile, 0, 0.6f ) {
			protected void onComplete() {
				tile.killAndErase();
				killAndErase();
			}
		} );
	}

	public static PointF tileToWorld( int pos ) {
		return new PointF( pos % Dungeon.level.width(), pos / Dungeon.level.width()  ).scale( SIZE );
	}

	public static int pointToTile(float x, float y) {
		return (int) (y / SIZE) * Dungeon.level.width() + (int) (x / SIZE);
	}

	public static PointF tileCenterToWorld( int pos ) {
		return new PointF(
			(pos % Dungeon.level.width() + 0.5f) * SIZE,
			(pos / Dungeon.level.width() + 0.5f) * SIZE );
	}

	public static PointF raisedTileCenterToWorld( int pos ) {
		return new PointF(
				(pos % Dungeon.level.width() + 0.5f) * SIZE,
				(pos / Dungeon.level.width() + 0.1f) * SIZE );
	}

	@Override
	public boolean overlapsScreenPoint( int x, int y ) {
		return true;
	}

}
