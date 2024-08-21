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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM100;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;

public class DM100Sprite extends MobSprite {

	private int zapPos;
	
	public DM100Sprite () {
		super();
		
		texture( Assets.Sprites.DM100 );
		
		initAnimations();
		
		play( idle );
	}

	@Override
	public void initAnimations() {
		TextureFilm frames = new TextureFilm( texture, 16, 14 );

		idle = new Animation( 1, true );
		idle.frames( frames, 0, 1 );

		run = new Animation( 12, true );
		run.frames( frames, 6, 7, 8, 9 );

		attack = new Animation( 12, false );
		attack.frames( frames, 2, 3, 4, 0 );

		zap = new Animation( 8, false );
		zap.frames( frames, 5, 5, 1 );

		die = new Animation( 12, false );
		die.frames( frames, 10, 11, 12, 13, 14, 15 );
	}

	@Override
	public void zap(int cell) {
		zapPos = cell;
		//some logic was moved to superclass!
		super.zap(cell);
		flash();
	}

	@Override
	protected void playZapAnim(int cell) {
		DM100Sprite.playZap(parent, this, cell, ch);
		//do nothing
	}

	@Override
	public boolean hasOwnZapAnimation() {
		return true;
	}

	@Override
	public boolean instantZapDamage() {
		return true;
	}

	public static void playZap(Group parent, Visual sprite, int cell, Char ch) {
		Char enemy = Actor.findChar(cell);

		//shoot lightning from eye, not sprite center.
		PointF origin = sprite.center();
		if (((CharSprite) sprite).flipHorizontal){
			origin.y -= 6*sprite.scale.y;
			origin.x -= 1*sprite.scale.x;
		} else {
			origin.y -= 8*sprite.scale.y;
			origin.x += 1*sprite.scale.x;
		}

		Callback callback = new Callback() {
			@Override
			public void call() {
				ch.onZapComplete();
			}
		};
		new Thread(() -> {
			try {
				Thread.sleep(15);
			} catch (InterruptedException ex) {
				Game.reportException(ex);
			}
			Group lightning = enemy == null ?
					new Lightning(origin, cell, callback)
					: new Lightning(origin, enemy.sprite.destinationCenter(), callback);
			parent.add(lightning);
		}).start();

		Sample.INSTANCE.play( Assets.Sounds.LIGHTNING );
	}

	public static void playOnZapHitEffect(int zapPos) {
		Char enemy = Actor.findChar(zapPos);
		if (enemy != null) {
			if (enemy.sprite.visible) {
				enemy.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3);
				enemy.sprite.flash();
			}
			if (enemy == Dungeon.hero) {
				PixelScene.shake(2, 0.3f);
			}
		}
	}

	@Override
	public void die() {
		emitter().burst( Speck.factory( Speck.WOOL ), 5 );
		super.die();
	}

	@Override
	public synchronized void play(Animation anim, boolean force) {
		if (anim == zap) {
			if (DM100.usesDM100ZapAnimation(this)) {
				playOnZapHitEffect(zapPos);
			}
		}
		super.play(anim, force);
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