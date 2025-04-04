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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.Ratmogrify;
import com.shatteredpixel.shatteredpixeldungeon.utils.Holiday;
import com.watabou.noosa.TextureFilm;

public class RatKingSprite extends MobSprite {
	
	public RatKingSprite() {
		super();

		resetAnims();
	}

	public void resetAnims(){
		initAnimations();
		play( idle );
	}

	@Override
	public void initAnimations() {
		int c;
		switch (Holiday.getCurrentHoliday()){
			default:
				c = 0;
				break;
			case APRIL_FOOLS:
				c = 32;
				break;
			case WINTER_HOLIDAYS:
				c = 64;
				break;
		}

		if (Dungeon.hero != null && Dungeon.hero.armorAbility instanceof Ratmogrify){
			c += 16;
			if (parent != null) aura(0xFFFF00);
		}

		texture( Assets.Sprites.RATKING );

		TextureFilm frames = new TextureFilm( texture, 16, 17 );

		idle = new Animation( 2, true );
		idle.frames( frames, c+0, c+0, c+0, c+1 );

		run = new Animation( 10, true );
		run.frames( frames, c+2, c+3, c+4, c+5, c+6 );

		attack = new Animation( 15, false );
		attack.frames( frames, c+7, c+8, c+9, c+10, c+0 );

		die = new Animation( 10, false );
		die.frames( frames, c+11, c+12, c+13, c+14 );
	}

	@Override
	public void link(Char ch) {
		super.link(ch);
		if (Dungeon.hero != null && Dungeon.hero.armorAbility instanceof Ratmogrify){
			aura(0xFFFF00);
		}
	}
}
