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

package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.noosa.Gizmo;

public class ColoringBlock extends Gizmo{

	private CharSprite target;
	
	private int color;

	public ColoringBlock(CharSprite target, int color ) {
		super();

		this.target = target;
		this.color = color;
	}

	@Override
	public void update() {
		super.update();

		target.tint(color, 0.5f);

	}
	
	public void undoEffect() {
		target.resetColor();
		killAndErase();
	}

	public static ColoringBlock apply(CharSprite sprite, int color) {

		ColoringBlock colorBlock = new ColoringBlock( sprite, color );
		if (sprite.parent != null)
			sprite.parent.add( colorBlock );

		return colorBlock;
	}

}