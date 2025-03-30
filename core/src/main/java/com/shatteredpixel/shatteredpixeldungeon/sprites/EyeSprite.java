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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Eye;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;

import java.util.LinkedHashMap;

public class EyeSprite extends MobSprite {

	private Animation charging;

	@Override
	public LinkedHashMap<String, Animation> getAnimations() {
		LinkedHashMap<String, Animation> result = super.getAnimations();
		result.put("charging", charging);
		return result;
	}
	
	public EyeSprite() {
		super();
		
		texture( Assets.Sprites.EYE );
		
		initAnimations();
		
		play( idle );
	}

	@Override
	public void initAnimations() {
		TextureFilm frames = new TextureFilm( texture, 16, 18 );

		idle = new Animation( 8, true );
		idle.frames( frames, 0, 1, 2 );

		charging = new Animation( 12, true);
		charging.frames( frames, 3, 4 );

		run = new Animation( 12, true );
		run.frames( frames, 5, 6 );

		attack = new Animation( 8, false );
		attack.frames( frames, 4, 3 );
		zap = attack.clone();

		die = new Animation( 8, false );
		die.frames( frames, 7, 8, 9 );
	}

	public static class ChargeParticles extends CharSpriteExtraEmitter {

		private int zapPos;

		@Override
		public void onLink(CharSprite sprite, Char ch) {
			emitter = sprite.centerEmitter();
			emitter.autoKill = false;
			emitter.pour(MagicMissile.MagicParticle.ATTRACTING, 0.05f);
			emitter.on = false;
		}

		@Override
		public void onUpdate(CharSprite sprite) {
			if (emitter != null){
				emitter.pos( sprite.center() );
				emitter.visible = sprite.visible;
			}
		}

		@Override
		public boolean playZapAnimation(CharSprite sprite, int cell) {
			this.zapPos = cell;
			return true;//animation is via in onComplete()
		}

		@Override
		public void onComplete(CharSprite sprite, Animation anim) {
			 if (anim == sprite.die){
				emitter.killAndErase();
			 }
			 else if (anim == sprite.zap) {
				sprite.idle();
				if (sprite instanceof EyeSprite) {
					playZap(sprite.parent, sprite, zapPos, sprite.ch);
				}
				else sprite.playZapAnim(zapPos);
			}
		}

		public void startCharging() {
			if (emitter != null) {
				emitter.on = true;
			}
		}

		public void cancelCharging() {
			if (emitter != null) {
				emitter.on = false;
			}
		}
	}

	@Override
	public void link(Char ch) {
		super.link(ch);
		if (ch instanceof Eye && ((Eye)ch).beamCharged) play(charging);
	}

	public static void charge( CharSprite sprite, int pos ){
		charge(sprite, pos, sprite instanceof EyeSprite ? ((EyeSprite) sprite).charging : null);
	}

	public static void charge( CharSprite sprite, int pos, Animation charging ){
		sprite.turnTo(sprite.ch.pos, pos);

		if (sprite.extraCode instanceof EyeSprite.ChargeParticles)
			((EyeSprite.ChargeParticles) sprite.extraCode).startCharging();

		if (charging != null) sprite.play(charging);
		else sprite.idle();

		if (sprite.visible) Sample.INSTANCE.play( Assets.Sounds.CHARGEUP );
	}

	@Override
	protected void playZapAnim(int cell) {
		if (!(ch instanceof Eye)) {
			playZap(parent, this, cell, ch);
		}
	}

	@Override
	public boolean hasOwnZapAnimation() {
		return true;
	}

	public static void playZap(Group parent, Visual sprite, int cell, Char ch) {
		Char enemy = Actor.findChar(cell);
		new Thread(() -> {
			try {
				Thread.sleep(100);
			} catch (InterruptedException ex) {
				Game.reportException(ex);
			}
			if (enemy != null){
				parent.add(new Beam.DeathRay(sprite.center(), enemy.sprite.center()));
			} else {
				parent.add(new Beam.DeathRay(sprite.center(), DungeonTilemap.raisedTileCenterToWorld(cell)));
			}
		}).start();
		ch.onZapComplete();
	}
}
