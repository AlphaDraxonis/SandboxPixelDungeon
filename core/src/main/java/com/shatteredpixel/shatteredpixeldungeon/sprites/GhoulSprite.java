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
import com.watabou.utils.RectF;

import java.util.LinkedHashMap;

public class GhoulSprite extends MobSprite {

	private Animation crumple;

	@Override
	public LinkedHashMap<String, Animation> getAnimations() {
		LinkedHashMap<String, Animation> result = super.getAnimations();
		result.put("crumple", crumple);
		return result;
	}
	
	public GhoulSprite() {
		super();
		
		texture( Assets.Sprites.GHOUL );
		
		initAnimations();
		
		play( idle );
	}

	@Override
	public void initAnimations() {
		TextureFilm frames = new TextureFilm( texture, 12, 14 );

		idle = new Animation( 2, true );
		idle.frames( frames, 0, 0, 0, 1 );

		run = new Animation( 12, true );
		run.frames( frames, 2, 3, 4, 5, 6, 7 );

		attack = new Animation( 12, false );
		attack.frames( frames, 0, 8, 9 );

		zap = attack.clone();

		crumple = new Animation( 15, false);
		crumple.frames( frames, 0, 10, 11, 12 );

		die = new Animation( 15, false );
		die.frames( frames, 0, 10, 11, 12, 13 );
	}

	public static void crumple(CharSprite sprite){
		sprite.hideEmo();
		sprite.processStateRemoval(State.PARALYSED);

		if (sprite instanceof GhoulSprite) {
			sprite.play(((GhoulSprite) sprite).crumple);
		} else {
			Animation crumple = new Animation((int) (1f / sprite.die.delay), false);
			crumple.frames = new RectF[sprite.die.frames.length-1];
			for (int i = 0; i < crumple.frames.length; i++) {
				crumple.frames[i] = sprite.die.frames[i];
			}
			sprite.play(crumple);
		}
	}

	@Override
	public void die() {
		if (curAnim == crumple){
			//causes the sprite to not rise then fall again when dieing.
			die.frames[0] = die.frames[1] = die.frames[2] = die.frames[3];
		}
		super.die();
	}
}
