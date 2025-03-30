/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
 *
 * Sandbox Pixel Dungeon
 * Copyright (C) 2023-2025 AlphaDraxonis
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

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.watabou.noosa.MovieClip;

/**
 * Uses composition to enable additional features for CharSprite that actually belong to the char, not the sprite, and are supposed to be independent of the exact sprite implementation.
 */
public interface CharSpriteExtraCode {

	void onLink(CharSprite sprite, Char ch);
	void onUpdate(CharSprite sprite);
	void onDie(CharSprite sprite);
	void onKill(CharSprite sprite);

	// return true = don't call super
	default boolean playZapAnimation(CharSprite sprite, int cell) {
		return false;
	}

	default void onComplete( CharSprite sprite, MovieClip.Animation anim ) {
	}

	default void onPlayAnimation( CharSprite sprite, MovieClip.Animation anim ) {
	}

}
