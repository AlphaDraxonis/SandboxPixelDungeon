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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Necromancer;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;

public class NecromancerSprite extends MobSprite {
	
	protected Animation charging;
	
	public NecromancerSprite(){
		super();
		
		texture( Assets.Sprites.NECRO );

		initAnimations();
		
		idle();
	}

	@Override
	public void initAnimations() {
		TextureFilm film = new TextureFilm( texture, 16, 16 );

		int c = texOffset();

		idle = new Animation( 1, true );
		idle.frames( film, c+0, c+0, c+0, c+1, c+0, c+0, c+0, c+0, c+1 );

		run = new Animation( 8, true );
		run.frames( film, c+0, c+0, c+0, c+2, c+3, c+4 );

		zap = new Animation( 10, false );
		zap.frames( film, c+5, c+6, c+7, c+8 );

		charging = new Animation( 5, true );
		charging.frames( film, c+7, c+8 );

		die = new Animation( 10, false );
		die.frames( film, c+9, c+10, c+11, c+12 );

		attack = zap.clone();
	}

	protected int texOffset() {
		return 0;
	}

	/**
	 * Only works if char is a Necromancer!
	 */
	public static class SummoningParticle implements CharSpriteExtraCode {

		protected Emitter summoningParticles;

		@Override
		public void onLink(CharSprite sprite, Char ch) {
			if (ch instanceof Necromancer && ((Necromancer) ch).summoning){
				sprite.zap(((Necromancer) ch).summoningPos);
			}
		}

		@Override
		public void onUpdate(CharSprite sprite) {
			if (summoningParticles != null && sprite.ch instanceof Necromancer && ((Necromancer) sprite.ch).summoningPos != -1){
				summoningParticles.visible = Dungeon.level.heroFOV[((Necromancer) sprite.ch).summoningPos];
			}
		}

		@Override
		public void onDie(CharSprite sprite) {
			shutDownSummoningParticles();
		}

		@Override
		public void onKill(CharSprite sprite) {
			shutDownSummoningParticles();
		}

		public void cancelSummoning() {
			shutDownSummoningParticles();
		}

		private void shutDownSummoningParticles() {
			if (summoningParticles != null){
				summoningParticles.on = false;
				summoningParticles = null;
			}
		}

		public void finishSummoning(CharSprite sprite){
			if (summoningParticles != null) {
				if (summoningParticles.visible) {
					playSummoningSound();
					summoningParticles.burst(Speck.factory(Speck.RATTLE), 5);
				} else {
					summoningParticles.on = false;
				}
				summoningParticles = null;
			}
			sprite.idle();
		}

		protected void playSummoningSound() {
			Sample.INSTANCE.play(Assets.Sounds.BONES);
		}

		protected void emitSummoningParticles() {
			summoningParticles.pour(Speck.factory(Speck.RATTLE), 0.2f);
		}

		@Override
		public boolean playZapAnimation(CharSprite sprite, int cell) {
			if (sprite.ch instanceof Necromancer && ((Necromancer) sprite.ch).summoning){
				shutDownSummoningParticles();
				summoningParticles = CellEmitter.get(((Necromancer) sprite.ch).summoningPos);
				emitSummoningParticles();
				summoningParticles.visible = Dungeon.level.heroFOV[((Necromancer) sprite.ch).summoningPos];
				if (sprite.visible || summoningParticles.visible ) Sample.INSTANCE.play( Assets.Sounds.CHARGEUP, 1f, 0.8f );
				return true;
			}
			return false;
		}
	}

	public void charge(){
		play(charging);
	}

	@Override
	public void onComplete(Animation anim) {
		super.onComplete(anim);
		if (anim == zap){
			if (ch instanceof Necromancer){
				if (((Necromancer) ch).summoning){
					charge();
				} else {
					ch.onZapComplete();
					idle();
				}
			} else {
				idle();
			}
		}
	}
}