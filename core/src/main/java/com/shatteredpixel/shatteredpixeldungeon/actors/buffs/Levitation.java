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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SandboxPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.dungeon.EffectDuration;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;

public class Levitation extends FlavourBuff {
	
	{
		type = buffType.POSITIVE;
	}

	private static final float DURATION	= 20f;
	
	@Override
	public boolean attachTo( Char target ) {
		if (super.attachTo( target )) {
			target.setFlying(true);
			detach( target, Roots.class );
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void detach() {
		target.setFlying(false);
		super.detach();
		//only press tiles if we're current in the game screen
		if (SandboxPixelDungeon.scene() instanceof GameScene) {
			Dungeon.level.occupyCell(target );
		}
	}

	//used to determine if levitation is about to end
	public boolean detachesWithinDelay(float delay, int cell){
		if (target.buff(Swiftthistle.TimeBubble.class) != null){
			return false;
		}

		if (target.buff(TimekeepersHourglass.timeFreeze.class) != null){
			return false;
		}

		if (Dungeon.level.zone[cell] != null && Dungeon.level.zone[cell].appliesBuff(Levitation.class, target)) {
			return false;
		}


		return cooldown() < delay;
	}
	
	@Override
	public int icon() {
		return BuffIndicator.LEVITATION;
	}

	@Override
	public void tintIcon(Image icon) {
		icon.hardlight(1f, 2.1f, 2.5f);
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (DURATION() - visualcooldown()) / DURATION());
	}
	
	@Override
	public void fx(boolean on) {
		if (on && !alwaysHidesFx) target.sprite.add(CharSprite.State.LEVITATING);
		else target.sprite.remove(CharSprite.State.LEVITATING);
	}

	public static float DURATION(){
		return EffectDuration.get(Levitation.class, defaultDuration());
	}

	public static float defaultDuration() {
		return DURATION;
	}
}
