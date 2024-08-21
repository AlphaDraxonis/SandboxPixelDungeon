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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.LinkedHashMap;

public class RipperSprite extends MobSprite {

	private Animation stab;
	private Animation prep;
	private Animation leap;

	@Override
	public LinkedHashMap<String, Animation> getAnimations() {
		LinkedHashMap<String, Animation> result = super.getAnimations();
		result.put("stab", stab);
		result.put("prep", prep);
		result.put("leap", leap);
		return result;
	}

	private boolean alt = Random.Int(2) == 0;

	public RipperSprite() {
		super();

		texture( Assets.Sprites.RIPPER );

		initAnimations();

		play( idle );
	}

	@Override
	public void initAnimations() {
		TextureFilm frames = new TextureFilm( texture, 15, 14 );

		idle = new Animation( 4, true );
		idle.frames( frames, 1, 0, 1, 2 );

		run = new Animation( 15, true );
		run.frames( frames, 3, 4, 5, 6, 7, 8 );

		attack = new Animation( 12, false );
		attack.frames( frames, 0, 9, 10, 9 );

		stab = new Animation( 12, false );
		stab.frames( frames, 0, 9, 11, 9 );

		prep = new Animation( 1, true );
		prep.frames( frames, 9 );

		leap = new Animation( 1, true );
		leap.frames( frames, 12 );

		die = new Animation( 15, false );
		die.frames( frames, 1, 13, 14, 15, 16 );
	}

	public static void leapPrep(CharSprite sprite, int cell ){
		sprite.turnTo( sprite.ch.pos, cell );

		if (sprite instanceof RipperSprite) {
			sprite.play(((RipperSprite) sprite).prep);
		} else {
			sprite.idle();
		}
	}

	@Override
	public void jump( int from, int to, float height, float duration,  Callback callback ) {
		super.jump( from, to, height, duration, callback );
		play( leap );
	}

	@Override
	public void attack( int cell ) {
		super.attack( cell );
		if (alt) {
			play( stab );
		}
		alt = !alt;
	}

	@Override
	public void onComplete( Animation anim ) {
		super.onComplete( anim == stab ? attack : anim );
	}

}