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

package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

public class RatSprite extends MobSprite {
	
	public RatSprite() {
		super();
		
		texture( Assets.Sprites.RAT );
		
		initAnimations();
		
		play( idle );
	}

	@Override
	public void initAnimations() {
		TextureFilm frames = new TextureFilm( texture, 16, 15 );

		int c = texOffset();

		idle = new Animation( 2, true );
		idle.frames( frames, c+0, c+0, c+0, c+1 );

		run = new Animation( 10, true );
		run.frames( frames, c+6, c+7, c+8, c+9, c+10 );

		attack = new Animation( 15, false );
		attack.frames( frames, c+2, c+3, c+4, c+5, c+0 );

		die = new Animation( 10, false );
		die.frames( frames, c+11, c+12, c+13, c+14 );
	}

	protected int texOffset() {
		return 0;
	}
}
