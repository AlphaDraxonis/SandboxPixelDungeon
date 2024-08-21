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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GnollTrickster;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.ParalyticDart;
import com.watabou.noosa.Group;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class GnollTricksterSprite extends MobSprite {

	public GnollTricksterSprite() {
		super();

		texture( Assets.Sprites.GNOLL );

		initAnimations();

		play( idle );
	}

	@Override
	public void initAnimations() {
		TextureFilm frames = new TextureFilm( texture, 12, 15 );

		idle = new MovieClip.Animation( 2, true );
		idle.frames( frames, 21, 21, 21, 22, 21, 21, 22, 22 );

		run = new MovieClip.Animation( 12, true );
		run.frames( frames, 25, 26, 27, 28 );

		attack = new MovieClip.Animation( 12, false );
		attack.frames( frames, 23, 24, 21 );

		zap = attack.clone();

		die = new MovieClip.Animation( 12, false );
		die.frames( frames, 29, 30, 31 );
	}

	@Override
	public void attack( int cell ) {
		if (!doRealAttack(this, cell)) super.attack(cell);
	}

	public static boolean doRealAttack( CharSprite sprite, int cell ) {
		if (!Dungeon.level.adjacent( cell, sprite.ch.pos )) {
			sprite.zap(cell);
			sprite.turnTo( sprite.ch.pos , cell );
		} else {
			if (sprite instanceof GnollTricksterSprite) return false;
			sprite.attack( cell );
		}
		return true;
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
		((MissileSprite) parent.recycle(MissileSprite.class)).
				reset(sprite, cell, new ParalyticDart(), new Callback() {
					@Override
					public void call() {
						if (ch instanceof GnollTrickster) ch.onAttackComplete();
						else {
							ch.onZapComplete();
							Sample.INSTANCE.play(Assets.Sounds.HIT);
						}
					}
				});
	}
}