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
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.watabou.noosa.Group;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.Visual;

public class GolemSprite extends MobSprite {
	
	public GolemSprite() {
		super();
		
		texture( Assets.Sprites.GOLEM );
		
		initAnimations();
		
		play( idle );
	}

	@Override
	public void initAnimations() {
		TextureFilm frames = new TextureFilm( texture, 17, 19 );

		idle = new Animation( 4, true );
		idle.frames( frames, 0, 1 );

		run = new Animation( 12, true );
		run.frames( frames, 2, 3, 4, 5 );

		attack = new Animation( 10, false );
		attack.frames( frames, 6, 7, 8 );

		zap = attack.clone();

		die = new Animation( 15, false );
		die.frames( frames, 9, 10, 11, 12, 13 );
	}

	public static class TeleParticles extends CharSpriteExtraEmitter {

		@Override
		public void onLink(CharSprite sprite, Char ch) {
			emitter = sprite.emitter();
			emitter.autoKill = false;
			emitter.pour(ElmoParticle.FACTORY, 0.05f);
			emitter.on = false;
		}

		@Override
		public void onUpdate(CharSprite sprite) {
			if (emitter != null){
				emitter.pos( sprite );
				emitter.visible = sprite.visible;
			}
		}

		@Override
		public void onPlayAnimation(CharSprite sprite, Animation anim) {
			showParticles(false);
		}

		public void showParticles(boolean flag) {
			if (emitter != null) emitter.on = flag;
		}
	}

	@Override
	public int blood() {
		return 0xFF80706c;
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
		playZap(parent, sprite, cell, ch, MagicMissile.ELMO);
	}

	private boolean died = false;

	@Override
	public void onComplete( Animation anim ) {
		if (anim == die && !died) {
			died = true;
			emitter().burst( ElmoParticle.FACTORY, 4 );
		}
		if (anim == zap) {
			idle();
		}
		super.onComplete( anim );
	}
}
