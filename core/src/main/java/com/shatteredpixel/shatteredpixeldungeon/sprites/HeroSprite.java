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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;
import com.watabou.utils.RectF;
import com.watabou.utils.Reflection;

import java.util.LinkedHashMap;

public class HeroSprite extends CharSprite implements HeroSpriteLike {
	
	private static final int FRAME_WIDTH	= 12;
	private static final int FRAME_HEIGHT	= 15;
	
	private static final int RUN_FRAMERATE	= 20;
	
	private static TextureFilm tiers;
	
	private Animation fly;
	private Animation read;

	@Override
	public LinkedHashMap<String, Animation> getAnimations() {
		LinkedHashMap<String, Animation> result = super.getAnimations();
		result.put("fly", fly);
		result.put("read", read);
		return result;
	}

	public HeroSprite(Hero hero) {
		super();

		updateArmor(hero);
		
		if (hero == Dungeon.hero) link( hero );

		if (hero.isAlive())
			idle();
		else
			die();
	}
	
	public void updateArmor() {
		updateArmor(Dungeon.hero);
	}

	public void updateArmor(Hero hero) {

		if (hero.internalSpriteClass != null) {

			CharSprite anims = Reflection.newInstance(hero.internalSpriteClass);

			if (anims instanceof StatueSprite) StatueSprite.setArmor(anims, hero.tier());

			texture(anims.texture);

			idle = anims.idle.clone();
			run = anims.run.clone();
			die = anims.die.clone();
			attack = anims.attack.clone();

			if (anims.zap != null) zap = anims.zap.clone();
			else {
				zap = attack.clone();
			}
			if (anims.operate != null) operate = anims.operate.clone();
			else {
				operate = idle.clone();
				operate.frames(idle.frames[0]);
			}

			fly = read = null;

			anims.destroy();

		} else {

			texture( hero.heroClass.spritesheet() );

			TextureFilm film = new TextureFilm( tiers(), hero.tier(), FRAME_WIDTH, FRAME_HEIGHT );

			idle = new Animation( 1, true );
			idle.frames( film, 0, 0, 0, 1, 0, 0, 1, 1 );

			run = new Animation( RUN_FRAMERATE, true );
			run.frames( film, 2, 3, 4, 5, 6, 7 );

			die = new Animation( 20, false );
			die.frames( film, 8, 9, 10, 11, 12, 11 );

			attack = new Animation( 15, false );
			attack.frames( film, 13, 14, 15, 0 );

			zap = attack.clone();

			operate = new Animation( 8, false );
			operate.frames( film, 16, 17, 16, 17 );

			fly = new Animation( 1, true );
			fly.frames( film, 18 );

			read = new Animation( 20, false );
			read.frames( film, 19, 20, 20, 20, 20, 20, 20, 20, 20, 19 );

		}

		if (Dungeon.isLevelTesting()) operate.delay = 0.04f;
		
		if (hero.isAlive())
			idle();
		else
			die();
	}
	
	@Override
	public void place( int p ) {
		super.place( p );
		if (Game.scene() instanceof GameScene) Camera.main.panFollow(this, 5f);
	}

	@Override
	public void move( int from, int to ) {
		super.move( from, to );
		if (ch != null && ch.isFlying()) {
			if (fly != null) play( fly );
		}
		Camera.main.panFollow(this, 20f);
	}

	@Override
	public void idle() {
		super.idle();
		if (ch != null && ch.isFlying()) {
			if (fly != null) play( fly );
		}
	}

	@Override
	public void jump( int from, int to, float height, float duration,  Callback callback ) {
		super.jump( from, to, height, duration, callback );
		if (fly != null) play( fly );
		Camera.main.panFollow(this, 20f);
	}

	public synchronized void read() {
		if (read != null) {
			animCallback = new Callback() {
				@Override
				public void call() {
					idle();
					ch.onOperateComplete();
				}
			};
			play(read);
		} else {
			ch.onOperateComplete();
		}
	}

	@Override
	public void bloodBurstA(PointF from, int damage) {
		//Does nothing.

		/*
		 * This is both for visual clarity, and also for content ratings regarding violence
		 * towards human characters. The heroes are the only human or human-like characters which
		 * participate in combat, so removing all blood associated with them is a simple way to
		 * reduce the violence rating of the game.
		 */
	}

	@Override
	public void update() {
		sleeping = ch.isAlive() && ((Hero)ch).resting;
		
		super.update();
	}

	@Override
	public void sprint( float speed ) {
		run.delay = 1f / speed / RUN_FRAMERATE;
	}
	
	public static TextureFilm tiers() {
		if (tiers == null) {
			SmartTexture texture = TextureCache.get( Assets.Sprites.ROGUE );
			tiers = new TextureFilm( texture, texture.width, FRAME_HEIGHT );
		}
		
		return tiers;
	}
	
	public static Image avatar( HeroClass cl, int armorTier ) {
		
		RectF patch = tiers().get( armorTier );
		Image avatar = new Image( cl.spritesheet() );
		RectF frame = avatar.texture.uvRect( 1, 0, FRAME_WIDTH, FRAME_HEIGHT );
		frame.shift( patch.left, patch.top );
		avatar.frame( frame );
		
		return avatar;
	}

	public static class HeroMobSprite extends MobSprite implements HeroSpriteLike {

		private Animation fly, read;

		@Override
		public LinkedHashMap<String, Animation> getAnimations() {
			LinkedHashMap<String, Animation> result = super.getAnimations();
			result.put("fly", fly);
			result.put("read", read);
			return result;
		}

		public HeroMobSprite() {
			//for reflection
			texture(HeroClass.WARRIOR.spritesheet());
			updateArmor(new Hero());
			idle();
		}

		public HeroMobSprite(Hero hero) {
			super();

			texture( hero.heroClass.spritesheet() );
			updateArmor(hero);

			if (hero.isAlive()) idle();
			else die();
		}

		public void updateHeroClass(Hero hero) {

			int play;
			if (curAnim == idle) play = 0;
			else if (curAnim == run) play = 1;
			else if (curAnim == die) play = 2;
			else if (curAnim == attack) play = 3;
			else if (curAnim == zap) play = 4;
			else if (curAnim == operate) play = 5;
			else if (curAnim == fly) play = 6;
			else if (curAnim == read) play = 7;
			else play = 0;

			texture( hero.heroClass.spritesheet() );
			updateArmor(hero);

			if (hero.isAlive()) {
				switch (play) {
					default:
					case 0: idle(); break;
					case 1: play(run); break;
					case 2: play(die); break;
					case 3: play(attack); break;
					case 4: play(zap); break;
					case 5: play(operate); break;
					case 6: play(fly); break;
					case 7: play(read); break;
				}
			}
			else die();
		}

		public void updateArmor(Hero hero) {
			HeroSprite anims = new HeroSprite(hero);

			idle = anims.idle.clone();
			run = anims.run.clone();
			die = anims.die.clone();
			attack = anims.attack.clone();
			zap = anims.zap.clone();
			operate = anims.operate.clone();
			fly = anims.fly.clone();
			read = anims.read.clone();

			anims.destroy();
		}

		@Override
		public void move( int from, int to ) {
			super.move( from, to );
			if (ch != null && ch.isFlying()) {
				play( fly );
			}
		}

		@Override
		public void idle() {
			super.idle();
			if (ch != null && ch.isFlying()) {
				play( fly );
			}
		}

		@Override
		public void jump( int from, int to, float height, float duration,  Callback callback ) {
			super.jump( from, to, height, duration, callback );
			play( fly );
		}
		@Override
		public void bloodBurstA(PointF from, int damage) {
			//Does nothing.

			/*
			 * This is both for visual clarity, and also for content ratings regarding violence
			 * towards human characters. The heroes are the only human or human-like characters which
			 * participate in combat, so removing all blood associated with them is a simple way to
			 * reduce the violence rating of the game.
			 */
		}

		@Override
		public void sprint( float speed ) {
			run.delay = 1f / speed / RUN_FRAMERATE;
		}

		@Override
		protected void playZapAnim(int cell) {
			//do nothing
		}

		@Override
		public boolean hasOwnZapAnimation() {
			return true;
		}

	}


}