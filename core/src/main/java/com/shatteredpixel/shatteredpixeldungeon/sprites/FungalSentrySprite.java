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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.watabou.noosa.Group;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.Visual;
import com.watabou.utils.Callback;

public class FungalSentrySprite extends MobSprite {

	public FungalSentrySprite(){
		super();

		texture( Assets.Sprites.FUNGAL_SENTRY );

		initAnimations();

		play( idle );

	}


	@Override
	public void initAnimations() {
		TextureFilm frames = new TextureFilm( texture, 18, 18 );

		idle = new Animation( 0, true );
		idle.frames( frames, 0);

		run = new Animation( 0, true );
		run.frames( frames, 0);

		attack = new Animation( 24, false );
		attack.frames( frames, 0 );

		zap = attack.clone();

		die = new Animation( 12, false );
		die.frames( frames, 0 );
	}

	@Override
	public void attack( int cell ) {
		if (!doRealAttack(this, cell)) super.attack(cell);
	}

	public static boolean doRealAttack( CharSprite sprite, int cell ) {
		if (!Dungeon.level.adjacent( cell, sprite.ch.pos )) {
			sprite.zap(cell);
		} else {
			if (sprite instanceof FungalSentrySprite) return false;
			sprite.attack( cell );
		}
		return true;
	}

	@Override
	public void onComplete( Animation anim ) {
		if (anim == zap) {
			idle();
		}
		super.onComplete( anim );
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
		MagicMissile.boltFromChar(parent, MagicMissile.POISON, sprite, cell, new Callback() {
			@Override
			public void call() {
				ch.onZapComplete();
			}
		} );
	}

	@Override
	public void turnTo(int from, int to) {
		//do nothing
	}

	@Override
	public int blood() {
		return 0xFF88CC44;
	}

}