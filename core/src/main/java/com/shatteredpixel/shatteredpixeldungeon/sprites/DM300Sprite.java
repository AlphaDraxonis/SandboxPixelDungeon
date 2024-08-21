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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM300;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.watabou.noosa.Group;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

import java.util.LinkedHashMap;

public class DM300Sprite extends MobSprite {

	private Animation charge;
	private Animation slam;

	@Override
	public LinkedHashMap<String, Animation> getAnimations() {
		LinkedHashMap<String, Animation> result = super.getAnimations();
		result.put("charge", charge);
		result.put("slam", slam);
		return result;
	}

	public DM300Sprite() {
		super();
		
		texture( Assets.Sprites.DM300 );

		setSuperchargedTexture(false);
	}

	public static class SuperchargeSparks extends CharSpriteExtraEmitter {

		@Override
		public void onLink(CharSprite sprite, Char ch) {
			emitter = sprite.emitter();
			emitter.autoKill = false;
			emitter.pour(SparkParticle.STATIC, 0.05f);
			emitter.on = false;

			if (ch instanceof DM300){
				updateChargeState(sprite, ((DM300) ch).isSupercharged());
			}
		}

		@Override
		public void onUpdate(CharSprite sprite) {
			if (emitter != null){
				emitter.visible = sprite.visible;
			}
		}

		public void updateChargeState( CharSprite sprite, boolean enraged ){
			if (emitter != null) emitter.on = enraged;

			//can only change texture if we are the actual
			if (sprite instanceof DM300Sprite) {
				((DM300Sprite) sprite).setSuperchargedTexture(enraged);
			}
		}

	}

	protected void setSuperchargedTexture( boolean enraged ) {
		this.enraged = enraged;

		initAnimations();

		if (curAnim != charge) play(idle);
	}

	private boolean enraged;

	public boolean enraged() {
		return enraged;
	}

	@Override
	public void initAnimations() {
		TextureFilm frames = new TextureFilm(texture, 25, 22);

		int c = enraged() ? 10 : 0;

		idle = new Animation(enraged() ? 15 : 10, true);
		idle.frames(frames, c + 0, c + 1);

		run = new Animation(enraged() ? 15 : 10, true);
		run.frames(frames, c + 0, c + 2);

		attack = new Animation(15, false);
		attack.frames(frames, c + 3, c + 4, c + 5);

		//unaffected by enrage state

		if (charge == null) {
			charge = new Animation(4, true);
			charge.frames(frames, 0, 10);

			slam = attack.clone();

			zap = new Animation(15, false);
			zap.frames(frames, 6, 7, 7, 6);

			die = new Animation(20, false);
			die.frames(frames, 0, 10, 0, 10, 0, 10, 0, 10, 0, 10, 0, 10, 0, 10, 0, 10, 0, 10, 0, 10);
		}
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
				MagicMissile.TOXIC_VENT,
				sprite,
				cell,
				new Callback() {
					@Override
					public void call() {
						ch.onZapComplete();
					}
				} );
		Sample.INSTANCE.play( Assets.Sounds.GAS );
	}

	public void charge(){
		play( charge );
	}

	public void slam(){
		play( slam );
	}

	public static boolean slam(CharSprite sprite, int cell) {
		sprite.turnTo( sprite.ch.pos , cell );

		if (sprite instanceof DM300Sprite) ((DM300Sprite) sprite).slam();

		Sample.INSTANCE.play( Assets.Sounds.ROCKS );
		PixelScene.shake( 3, 0.7f );

		return !(sprite instanceof DM300Sprite);
	}

	@Override
	public void onComplete( Animation anim ) {

		if (anim == zap || anim == slam){
			idle();
		}

		if (anim == slam){
			((DM300)ch).onSlamComplete();
		}

		super.onComplete( anim );
		
		if (anim == die) {
			Sample.INSTANCE.play(Assets.Sounds.BLAST);
			emitter().burst( BlastParticle.FACTORY, 100 );
			killAndErase();
		}
	}

	@Override
	public void place(int cell) {
		if (parent != null) parent.bringToFront(this);
		super.place(cell);
	}

	@Override
	public int blood() {
		return 0xFFFFFF88;
	}
}