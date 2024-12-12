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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.MirrorImage;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.PointF;
import com.watabou.utils.Reflection;

public class MirrorSprite extends MobSprite {
	
	private static final int FRAME_WIDTH	= 12;
	private static final int FRAME_HEIGHT	= 15;

	private static final int RUN_FRAMERATE	= 20;

	public MirrorSprite() {
		super();

		updateArmor(Dungeon.hero);
		idle();
	}
	
	@Override
	public void link( Char ch ) {
		super.link( ch );
		updateArmor();
	}

	@Override
	public void bloodBurstA(PointF from, int damage) {
		//do nothing
	}

	public void updateArmor(){
		updateArmor( ((MirrorImage)ch).hero );
	}
	
	public void updateArmor( Hero hero ) {

		if (hero != null && hero.internalSpriteClass != null) {

			CharSprite anims = Reflection.newInstance(hero.internalSpriteClass);

			if (anims instanceof StatueSprite) StatueSprite.setArmor(anims, hero.tier());

			texture(anims.texture);

			idle = anims.idle.clone();
			run = anims.run.clone();
			die = anims.die.clone();
			attack = anims.attack.clone();

			if (anims.zap != null) zap = anims.zap.clone();
			else zap = attack.clone();

			anims.destroy();

		} else {

			texture( hero != null ? hero.heroClass.spritesheet() : HeroClass.WARRIOR.spritesheet());

			TextureFilm film = new TextureFilm( HeroSprite.tiers(), hero != null ? hero.tier() : 0, FRAME_WIDTH, FRAME_HEIGHT );

			idle = new Animation( 1, true );
			idle.frames( film, 0, 0, 0, 1, 0, 0, 1, 1 );

			run = new Animation( RUN_FRAMERATE, true );
			run.frames( film, 2, 3, 4, 5, 6, 7 );

			die = new Animation( 20, false );
			die.frames( film, 0 );

			attack = new Animation( 15, false );
			attack.frames( film, 13, 14, 15, 0 );

			zap = attack.clone();

		}
		
		idle();
	}
}