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
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.watabou.noosa.audio.Sample;

public class SpectralNecromancerSprite extends NecromancerSprite {

	@Override
	protected int texOffset() {
		return 16;
	}

	public static class SummoningParticle extends NecromancerSprite.SummoningParticle {

		@Override
		protected void playSummoningSound() {
			Sample.INSTANCE.play(Assets.Sounds.CURSED);
		}

		@Override
		protected void emitSummoningParticles() {
			summoningParticles.pour(ShadowParticle.MISSILE, 0.1f);
		}
	}

}
