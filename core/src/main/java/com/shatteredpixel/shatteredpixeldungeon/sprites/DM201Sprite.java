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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM201;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Group;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class DM201Sprite extends MobSprite {

	public DM201Sprite () {
		super();

		texture( Assets.Sprites.DM200 );

		initAnimations();

		play( idle );
	}

	@Override
	public void initAnimations() {
		TextureFilm frames = new TextureFilm( texture, 21, 18 );

		int c = 12;

		idle = new Animation( 2, true );
		idle.frames( frames, c+0, c+1 );

		run = idle.clone();

		attack = new Animation( 15, false );
		attack.frames( frames, c+4, c+5, c+6 );

		zap = new Animation( 15, false );
		zap.frames( frames, c+7, c+8, c+8, c+7 );

		die = new Animation( 8, false );
		die.frames( frames, c+9, c+10, c+11 );
	}

	@Override
	public void place(int cell) {
		if (parent != null) parent.bringToFront(this);
		super.place(cell);
	}

	@Override
	public void die() {
		emitter().burst( Speck.factory( Speck.WOOL ), 8 );
		super.die();
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
				MagicMissile.CORROSION,
				sprite,
				cell,
				new Callback() {
					@Override
					public void call() {
						Sample.INSTANCE.play( Assets.Sounds.GAS );
						ch.onZapComplete();
					}
				} );
		Sample.INSTANCE.play( Assets.Sounds.MISS, 1f, 1.5f );
		GLog.w(Messages.get(DM201.class, "vent"));
	}

	@Override
	public void onComplete( Animation anim ) {
		if (anim == zap) {
			idle();
		}
		super.onComplete( anim );
	}

	@Override
	public int blood() {
		return 0xFFFFFF88;
	}

}
