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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class Dread extends BuffWithDuration {

	public int object = 0;

	public static final float DURATION = 20f;

	{
		type = buffType.NEGATIVE;
		announced = true;

		left = DURATION;
	}

	//dread overrides terror
	@Override
	public boolean attachTo(Char target) {
		if (super.attachTo(target)){
			Buff.detach( target, Terror.class );
			return true;
		} else {
			return false;
		}
	}

	{
		immunities.add(Terror.class);
	}

	@Override
	public boolean act() {

		if (!Dungeon.level.heroFOV[target.pos]
				&& Dungeon.level.distance(target.pos, Dungeon.hero.pos) >= 6) {
			if (target instanceof Mob){
				((Mob) target).EXP /= 2;
			}
			target.destroy();
			target.sprite.killAndErase();
			Dungeon.level.mobs.remove(target);
		} else {;
			if (!permanent && (left -= TICK) <= 0){
				detach();
			}
		}

		spend(TICK);
		return true;
	}

	private static final String OBJECT    = "object";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);
		bundle.put(OBJECT, object);
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		object = bundle.getInt( OBJECT );
	}

	@Override
	public int icon() {
		return BuffIndicator.TERROR;
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (DURATION - left) / DURATION);
	}

	@Override
	public void tintIcon(Image icon) {
		icon.hardlight(1, 0, 0);
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", left) + appendDescForPermanent();
	}

	public void recover() {
		if (!permanent && (left -= 5) <= 0){
			detach();
		}
	}

}
