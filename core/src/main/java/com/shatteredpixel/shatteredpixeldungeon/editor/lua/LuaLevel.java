/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
 *
 * Sandbox Pixel Dungeon
 * Copyright (C) 2023-2024 AlphaDraxonis
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

package com.shatteredpixel.shatteredpixeldungeon.editor.lua;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Reflection;
import org.luaj.vm2.LuaTable;

public interface LuaLevel {

	void setVars(LuaTable vars);

	static String getLuaLevelClassName(Class<?> clazz) {
		return getLuaLevelClassName(clazz.getSimpleName());
	}

	static String getLuaLevelClassName(String simpleClassName) {
		return Messages.MAIN_PACKAGE_NAME + "levels.lualevels." + simpleClassName + "_lua";
	}

	static Class<?> getLuaLevelClass(Class<?> clazz) {
		return Reflection.forName(getLuaLevelClassName(clazz));
	}
}