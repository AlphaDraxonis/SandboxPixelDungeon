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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Tengu;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.Group;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class TenguSprite extends MobSprite {
	
	public TenguSprite() {
		super();
		
		texture( Assets.Sprites.TENGU );
		
		initAnimations();

		isMoving = true;
		play( run );
		isMoving = true;
	}

	@Override
	public void initAnimations() {
		TextureFilm frames = new TextureFilm( texture, 14, 16 );
		
		idle = new Animation( 2, true );
		idle.frames( frames, 0, 0, 0, 1 );
		
		run = new Animation( 15, false );
		run.frames( frames, 2, 3, 4, 5, 0 );
		
		attack = new Animation( 15, false );
		attack.frames( frames, 6, 7, 7, 0 );
		
		zap = attack.clone();
		
		die = new Animation( 8, false );
		die.frames( frames, 8, 9, 10, 10, 10, 10, 10, 10 );
	}

	@Override
	public void play(Animation anim) {
		if (isMoving && anim != run){
			synchronized (this){
				isMoving = false;
				notifyAll();
			}
		}
		super.play(anim);
	}

	@Override
	public void move( int from, int to ) {
		
		place( to );
		
		play( run );
		turnTo( from , to );

		isMoving = true;

		if (Dungeon.level.water[to]) {
			GameScene.ripple( to );
		}

	}

	@Override
	public void update() {
		if (paused) isMoving = false;
		super.update();
	}

	@Override
	public void attack( int cell ) {
		if (!doRealAttack(this, cell)) super.attack(cell);
	}

	public static boolean doRealAttack( CharSprite sprite, int cell ) {
		if (!Dungeon.level.adjacent( cell, sprite.ch.pos )) {
			sprite.zap(cell);
		} else {
			if (sprite instanceof TenguSprite) return false;
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
		if (ch instanceof Tengu) {
			//abilities have their own zap animation in Tengu.java
			if (!((Tengu) ch).zapForAbility) {
				((MissileSprite) parent.recycle(MissileSprite.class)).
						reset(sprite, cell, new TenguShuriken(), new Callback() {
							@Override
							public void call() {
								ch.onAttackComplete();
							}
						});
			}
		}
		else {
			((MissileSprite) parent.recycle(MissileSprite.class)).
					reset(sprite, cell, new TenguShuriken(), new Callback() {
						@Override
						public void call() {
							ch.onZapComplete();
						}
					});
			Sample.INSTANCE.play(Assets.Sounds.HIT);
		}
	}
	
	@Override
	public void onComplete( Animation anim ) {
		if (anim == run) {
			synchronized (this){
				isMoving = false;
				idle();

				notifyAll();
			}
		} else {
			super.onComplete( anim );
		}
	}
	
	public static class TenguShuriken extends Item {
		{
			image = ItemSpriteSheet.SHURIKEN;
		}
	}
}
