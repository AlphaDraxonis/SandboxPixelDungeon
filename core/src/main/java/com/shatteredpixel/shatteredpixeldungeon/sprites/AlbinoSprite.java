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

package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.watabou.utils.RectF;

public class AlbinoSprite extends RatSprite {

	@Override
	public void initAnimations() {
		super.initAnimations();
		RectF[] frames = new RectF[attack.frames.length + 1];
		int i = 0;
		for (; i < attack.frames.length; i++) {
			frames[i] = attack.frames[i];
		}
		frames[i] = idle.frames[0];//first frame
		attack.frames = frames;
	}

	@Override
	protected int texOffset() {
		return 16;
	}
}