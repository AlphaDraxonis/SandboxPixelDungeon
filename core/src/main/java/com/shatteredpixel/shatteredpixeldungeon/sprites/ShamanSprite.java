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
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.Visual;

public abstract class ShamanSprite extends MobSprite {
	
	protected int boltType;
	
	protected abstract int texOffset();
	
	public ShamanSprite() {
		super();
		
		int c = texOffset();
		
		texture( Assets.Sprites.SHAMAN );
		
		TextureFilm frames = new TextureFilm( texture, 12, 15 );
		
		idle = new Animation( 2, true );
		idle.frames( frames, c+0, c+0, c+0, c+1, c+0, c+0, c+1, c+1 );
		
		run = new Animation( 12, true );
		run.frames( frames, c+4, c+5, c+6, c+7 );
		
		attack = new Animation( 12, false );
		attack.frames( frames, c+2, c+3, c+0 );
		
		zap = attack.clone();
		
		die = new Animation( 12, false );
		die.frames( frames, c+8, c+9, c+10 );
		
		play( idle );
	}

	@Override
	protected void playZapAnim(int cell) {
		playZap(parent, this, cell, ch, boltType);
	}

	@Override
	public void onComplete( Animation anim ) {
		if (anim == zap) {
			idle();
		}
		super.onComplete( anim );
	}
	
	public static class Red extends ShamanSprite {
		{
			boltType = MagicMissile.SHAMAN_RED;
		}
		
		@Override
		protected int texOffset() {
			return 0;
		}

		public static void playZap(Group parent, Visual sprite, int cell, Char ch) {
			playZap(parent, sprite, cell, ch, MagicMissile.SHAMAN_RED);
		}
	}
	
	public static class Blue extends ShamanSprite {
		{
			boltType = MagicMissile.SHAMAN_BLUE;
		}
		
		@Override
		protected int texOffset() {
			return 21;
		}

		public static void playZap(Group parent, Visual sprite, int cell, Char ch) {
			playZap(parent, sprite, cell, ch, MagicMissile.SHAMAN_BLUE);
		}
	}
	
	public static class Purple extends ShamanSprite {
		{
			boltType = MagicMissile.SHAMAN_PURPLE;
		}
		
		@Override
		protected int texOffset() {
			return 42;
		}

		public static void playZap(Group parent, Visual sprite, int cell, Char ch) {
			playZap(parent, sprite, cell, ch, MagicMissile.SHAMAN_PURPLE);
		}
	}
}