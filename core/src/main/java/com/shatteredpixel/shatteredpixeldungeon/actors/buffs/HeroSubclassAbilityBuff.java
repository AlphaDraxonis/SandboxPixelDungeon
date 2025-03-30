/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
 *
 * Sandbox Pixel Dungeon
 * Copyright (C) 2023-2025 AlphaDraxonis
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

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;

public abstract class HeroSubclassAbilityBuff extends Buff implements ActionIndicator.Action {

	public final boolean isPlayer() {
		return target.getClass() == Hero.class;
	}

	protected Hero targetHero() {
		return targetHero(target);
	}

	@Override
	public boolean attachTo(Char target) {
		if (super.attachTo(target)) {
			if (isPlayer()) {
				if (actionAvailable()){
					ActionIndicator.setAction(this);
				}
			}
			return true;
		}
		return false;
	}

	protected abstract boolean actionAvailable();
}
