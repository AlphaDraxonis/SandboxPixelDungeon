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
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.watabou.noosa.Group;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.Visual;
import com.watabou.utils.Callback;

public class ScorpioSprite extends MobSprite {
	
	public ScorpioSprite() {
		super();
		
		texture( Assets.Sprites.SCORPIO );
		
		TextureFilm frames = new TextureFilm( texture, 17, 17 );
		
		idle = new Animation( 12, true );
		idle.frames( frames, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 1, 2, 1, 2 );
		
		run = new Animation( 8, true );
		run.frames( frames, 5, 5, 6, 6 );
		
		attack = new Animation( 15, false );
		attack.frames( frames, 0, 3, 4 );
		
		zap = attack.clone();
		
		die = new Animation( 12, false );
		die.frames( frames, 0, 7, 8, 9, 10 );
		
		play( idle );
	}
	
	@Override
	public int blood() {
		return 0xFF44FF22;
	}
	
	@Override
	public void attack( int cell ) {
		if (!doRealAttack(this, cell)) super.attack(cell);
	}

	public static boolean doRealAttack( CharSprite sprite, int cell ) {
		if (!Dungeon.level.adjacent( cell, sprite.ch.pos )) {
			sprite.zap(cell);
		} else {
			if (sprite instanceof ScorpioSprite) return false;
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

	public static void playZap(Group parent, Visual sprite, int cell, Char ch) {
		((MissileSprite) parent.recycle(MissileSprite.class)).
				reset(sprite, cell, new ScorpioShot(), new Callback() {
					@Override
					public void call() {
						ch.onZapComplete();
					}
				});
	}
	
	public static class ScorpioShot extends Item {
		{
			image = ItemSpriteSheet.FISHING_SPEAR;
		}
	}
}