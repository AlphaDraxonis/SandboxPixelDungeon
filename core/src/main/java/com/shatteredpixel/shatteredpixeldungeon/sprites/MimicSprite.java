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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.MimicTooth;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.RectF;

public class MimicSprite extends MobSprite {

	protected Animation advancedHiding;

	protected Animation hiding;
	public boolean superHidden;

	{
		//adjust shadow slightly to account for 1 empty bottom pixel (used for border while hiding)
		perspectiveRaise    = 5 / 16f; //5 pixels
		shadowWidth         = 1f;
		shadowOffset        = -0.4f;
	}

	protected int texOffset(){
		return 0;
	}

	public MimicSprite() {
		super();

		texture( Assets.Sprites.MIMIC );

		initAnimations();

		play( idle );
	}

	@Override
	public void initAnimations() {
		TextureFilm frames = new TextureFilm( texture, 16, 16 );

		int c = texOffset();

		advancedHiding = new Animation( 1, true );
		advancedHiding.frames( frames, 0+c);

		hiding = new Animation( 1, true );
		hiding.frames( frames, 1+c, 1+c, 1+c, 1+c, 1+c, 2+c);

		idle = new Animation( 5, true );
		idle.frames( frames, 3+c, 3+c, 3+c, 4+c, 4+c );

		run = new Animation( 10, true );
		run.frames( frames, 3+c, 4+c, 5+c, 6+c, 6+c, 5+c, 4+c );

		attack = new Animation( 10, false );
		attack.frames( frames, 3+c, 7+c, 8+c, 9+c );

		die = new Animation( 5, false );
		die.frames( frames, 10+c, 11+c, 12+c );
	}

	@Override
	public void linkVisuals(Char ch) {
		super.linkVisuals(ch);
		if (ch instanceof Mimic && ch.alignment == Char.Alignment.NEUTRAL) {
			hideMimicSprite();
		}
	}

	protected void hideMimicSprite() {
		if (superHidden || MimicTooth.stealthyMimics()){
			play(advancedHiding);
		} else {
			play(hiding);
		}
		hideSleep();
	}

	public static void hideMimic(CharSprite sprite) {
		if (sprite instanceof MimicSprite) ((MimicSprite) sprite).hideMimicSprite();
		else {
			Animation hide = new Animation( 5, true );
			hide.frames = new RectF[1];
			hide.frames[0] = sprite.idle.frames[0];
			sprite.play(hide);
			sprite.hideSleep();
		}
	}

	@Override
	public void showSleep() {
		if (curAnim == hiding || curAnim == advancedHiding){
			return;
		}
		super.showSleep();
	}

	public static class Golden extends MimicSprite{
		@Override
		protected int texOffset() {
			return 16;
		}
	}

	public static class Crystal extends MimicSprite{
		@Override
		protected int texOffset() {
			return 32;
		}
	}

	public static class Ebony extends MimicSprite{
		@Override
		protected int texOffset() {
			return 48;
		}

		@Override
		protected void hideMimicSprite() {
			super.hideMimicSprite();
			alpha(0.2f);
		}

		@Override
		public void play(Animation anim) {
			if (curAnim == advancedHiding && anim != advancedHiding){
				alpha(1f);
			}
			super.play(anim);
		}
	}

}