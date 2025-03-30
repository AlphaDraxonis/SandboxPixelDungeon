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

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM300;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Eye;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GnollGuard;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Golem;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Goo;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Necromancer;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.SpectralNecromancer;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SentryRoom;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Game;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.noosa.tweeners.ScaleTweener;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.LinkedHashMap;

public class MobSprite extends CharSprite {

	private static final float FADE_TIME	= 3f;
	private static final float FALL_TIME	= 1f;

	//for LuaMethodManager order
	@Override
	public LinkedHashMap<String, Animation> getAnimations() {
		return super.getAnimations();
	}

	@Override
	public void update() {
		if (ch != null && ch.isAlive()) {
			sleeping = ((Mob) ch).state == ((Mob) ch).SLEEPING;
		} else {
			sleeping = false;
		}
		super.update();
	}
	
	@Override
	public void onComplete( Animation anim ) {
		
		super.onComplete( anim );
		
		if (anim == die && parent != null) {
			parent.add( new AlphaTweener( this, 0, FADE_TIME ) {
				@Override
				protected void onComplete() {
					MobSprite.this.killAndErase();
				}
			} );
		}
	}
	
	public void fall() {
		
		origin.set( width / 2, height - DungeonTilemap.SIZE / 2 );
		angularSpeed = Random.Int( 2 ) == 0 ? -720 : 720;
		am = 1;

		hideEmo();

		if (health != null){
			health.killAndErase();
		}
		
		parent.add( new ScaleTweener( this, new PointF( 0, 0 ), FALL_TIME ) {
			@Override
			protected void onComplete() {
				MobSprite.this.killAndErase();
				parent.erase( this );
			}
			@Override
			protected void updateValues( float progress ) {
				super.updateValues( progress );
				y += 12 * Game.elapsed;
				am = 1 - progress;
			}
		} );
	}

	//remember: this is sprite independent and fundamentally necessary for the mob to function normally!
	public static CharSpriteExtraCode createExtraCodeForMob(Char ch) {
		if (ch instanceof Necromancer) {
			if (ch instanceof SpectralNecromancer) return new SpectralNecromancerSprite.SummoningParticle();
			else return new NecromancerSprite.SummoningParticle();
		}
		if (ch instanceof DM300) return new DM300Sprite.SuperchargeSparks();
		if (ch instanceof Eye) return new EyeSprite.ChargeParticles();
		if (ch instanceof GnollGuard) return new GnollGuardSprite.EarthArmor();
		if (ch instanceof Golem) return new GolemSprite.TeleParticles();
		if (ch instanceof SentryRoom.Sentry) return new SentryRoom.SentrySprite.ChargeParticles();
		if (ch instanceof Goo) return new GooSprite.PumpUpEmitters();
		return null;
	}
}
