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
import com.watabou.utils.Random;

import java.util.LinkedHashMap;

public class MonkSprite extends MobSprite {
	
	private Animation kick;
	protected float kickChance = 0.5f;

	@Override
	public LinkedHashMap<String, Animation> getAnimations() {
		LinkedHashMap<String, Animation> result = super.getAnimations();
		result.put("kick", kick);
		return result;
	}
	
	public MonkSprite() {
		super();
		
		texture( Assets.Sprites.MONK );
		
		initAnimations();
		
		play( idle );
	}

	@Override
	public void initAnimations() {
		TextureFilm frames = new TextureFilm( texture, 15, 14 );

		int c = texOffset();

		idle = new Animation( 6, true );
		idle.frames( frames, c+1, c+0, c+1, c+2 );

		run = new Animation( 15, true );
		run.frames( frames, c+11, c+12, c+13, c+14, c+15, c+16 );

		attack = new Animation( 12, false );
		attack.frames( frames, c+3, c+4, c+3, c+4 );

		kick = new Animation( 10, false );
		kick.frames( frames, c+5, c+6, c+5 );

		die = new Animation( 15, false );
		die.frames( frames, c+1, c+7, c+8, c+8, c+9, c+10 );
	}

	@Override
	public void attack( int cell ) {
		super.attack( cell );
		if (Random.Float() < kickChance) {
			play( kick );
		}
	}
	
	@Override
	public void onComplete( Animation anim ) {
		super.onComplete( anim == kick ? attack : anim );
	}

	protected int texOffset() {
		return 0;
	}
}
