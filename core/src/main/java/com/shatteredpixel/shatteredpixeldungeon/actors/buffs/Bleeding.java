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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Sacrificial;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sickle;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class Bleeding extends BuffWithDuration {

	{
		type = buffType.NEGATIVE;
		announced = true;
	}

	//used in specific cases where the source of the bleed is important for death logic
	private Class source;

	private static final String SOURCE	= "source";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( SOURCE, source );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		source = bundle.getClass( SOURCE );
		if (bundle.contains("level")) left = bundle.getFloat("level");
	}
	
	public void set( float level ) {
		set( level, null );
	}

	public void set( float level, Class source ){
		if (this.left < level) {
			this.left = Math.max(this.left, level);
			this.source = source;
		}
	}
	
	@Override
	public void set(BuffWithDuration buff, Class source) {
		if (this.left < buff.left) {
			set(buff.left, source);
			this.source = source;
		}
	}

	@Override
	public int icon() {
		return BuffIndicator.BLEEDING;
	}

	@Override
	public String iconTextDisplay() {
		return Integer.toString(Math.round(left));
	}
	
	@Override
	public boolean act() {
		if (target.isAlive()) {
			
			if (!permanent) left = Random.NormalFloat(left / 2f, left);
			int dmg = Math.round(left);
			
			if (dmg > 0) {
				
				target.damage( dmg, this );
				if (target.sprite.visible) {
					Splash.at( target.sprite.center(), -PointF.PI / 2, PointF.PI / 6,
							target.sprite.blood(), Math.min( 10 * dmg / target.HT, 10 ) );
				}
				
				if (target == Dungeon.hero && !target.isAlive()) {
					if (source == Chasm.class){
						Badges.validateDeathFromFalling();
					} else if (source == Sacrificial.class){
						Badges.validateDeathFromFriendlyMagic();
					}
					Dungeon.fail( this );
					GLog.n( Messages.get(this, "ondeath") );
				}

				if (source == Sickle.HarvestBleedTracker.class && !target.isAlive()){
					MeleeWeapon.onAbilityKill(Dungeon.hero, target);
				}
				
				spend( TICK );
			} else {
				if (!permanent) detach();
			}
			
		} else {
			
			detach();
			
		}
		
		return true;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", Math.round(left)) + appendDescForPermanent();
	}
}
