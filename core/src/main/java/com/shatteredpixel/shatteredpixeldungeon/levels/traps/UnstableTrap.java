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

package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Traps;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Reflection;

import java.util.HashSet;
import java.util.Set;

public class UnstableTrap extends Trap {

	{
		color = GREY;
		shape = WAVES;
	}

	private static final Set<Class<? extends Trap>> IGNORE_TRAPS = new HashSet<>();
	{
		IGNORE_TRAPS.add(UnstableTrap.class);
	}

	@Override
	public void activate() {
		Class<? extends Trap> trapClass = Traps.getRandomTrap(IGNORE_TRAPS);
		Trap t = Reflection.newInstance(trapClass);
		t.pos = pos;

		GLog.newLine();
		GLog.i(Messages.get(this, "activates_as", t.name()));

		t.activate();

	}
}