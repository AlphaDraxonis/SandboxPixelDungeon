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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GnollGuard;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EarthParticle;
import com.watabou.noosa.TextureFilm;

public class GnollGuardSprite extends MobSprite {

	public GnollGuardSprite() {
		super();

		texture(Assets.Sprites.GNOLL_GUARD );

		initAnimations();

		play( idle );
	}

	@Override
	public void initAnimations() {
		TextureFilm frames = new TextureFilm( texture, 12, 16 );

		idle = new Animation( 2, true );
		idle.frames( frames, 0, 0, 0, 1, 0, 0, 1, 1 );

		run = new Animation( 12, true );
		run.frames( frames, 4, 5, 6, 7 );

		attack = new Animation( 12, false );
		attack.frames( frames, 2, 3, 0 );

		zap = attack.clone();

		die = new Animation( 12, false );
		die.frames( frames, 8, 9, 10 );
	}

	@Override
	public void link( Char ch ) {
		super.link( ch );

		if (extraCode instanceof GnollGuardSprite.EarthArmor){
			((EarthArmor) extraCode).setupArmor(this);
		}
	}

	public static class EarthArmor extends CharSpriteExtraEmitter {

		@Override
		public void onLink(CharSprite sprite, Char ch) {
		}

		@Override
		public void onUpdate(CharSprite sprite) {
			if (emitter != null){
				emitter.visible = sprite.visible;
			}
		}

		@Override
		public void onDie(CharSprite sprite) {
			loseArmor();
		}

		@Override
		public void onKill(CharSprite sprite) {
			loseArmor();
		}

		public void setupArmor(CharSprite sprite){
			if (sprite.ch instanceof GnollGuard && ((GnollGuard) sprite.ch).hasSapper()) {
				if (emitter == null) {
					emitter = sprite.emitter();
					emitter.fillTarget = false;
					emitter.y = sprite.height() / 2f;
					emitter.x = (2 * sprite.scale.x);
					emitter.width = sprite.width() - (4 * sprite.scale.x);
					emitter.height = sprite.height() - (10 * sprite.scale.y);
					emitter.pour(EarthParticle.SMALL, 0.15f);
				}
			}
		}

		public void loseArmor(){
			if (emitter != null){
				emitter.on = false;
				emitter = null;
			}
		}
	}


}