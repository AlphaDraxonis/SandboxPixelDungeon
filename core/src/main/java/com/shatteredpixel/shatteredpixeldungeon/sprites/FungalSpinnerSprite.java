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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.watabou.noosa.Group;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class FungalSpinnerSprite extends MobSprite {

	public FungalSpinnerSprite() {
		super();

		perspectiveRaise = 0f;

		texture( Assets.Sprites.FUNGAL_SPINNER );

		initAnimations();

		play( idle );
	}

	@Override
	public void initAnimations() {
		TextureFilm frames = new TextureFilm( texture, 16, 16 );

		idle = new MovieClip.Animation( 10, true );
		idle.frames( frames, 0, 0, 0, 0, 0, 1, 0, 1 );

		run = new MovieClip.Animation( 15, true );
		run.frames( frames, 0, 2, 0, 3 );

		attack = new MovieClip.Animation( 12, false );
		attack.frames( frames, 0, 4, 5, 0 );

		zap = attack.clone();

		die = new MovieClip.Animation( 12, false );
		die.frames( frames, 6, 7, 8, 9 );
	}

	@Override
	public void link(Char ch) {
		super.link(ch);
		if (parent != null) {
			parent.sendToBack(this);
			if (aura != null){
				parent.sendToBack(aura);
			}
		}
		renderShadow = false;
	}

	@Override
	protected void playZapAnim(int cell) {
		playZap(parent, this, cell, ch);
	}

	@Override
	public boolean hasOwnZapAnimation() {
		return true;
	}

	public static void playZap(Group parent, Visual sprite, int cell, Char ch) {
		MagicMissile.boltFromChar( parent,
				MagicMissile.FOLIAGE,
				sprite,
				cell,
				new Callback() {
					@Override
					public void call() {
						ch.onZapComplete();
					}
				} );
		Sample.INSTANCE.play( Assets.Sounds.MISS );
	}

	@Override
	public void onComplete( MovieClip.Animation anim ) {
		if (anim == zap) {
			play( run );
		}
		super.onComplete( anim );
	}

	@Override
	public int blood() {
		return 0xFF88CC44;
	}
}