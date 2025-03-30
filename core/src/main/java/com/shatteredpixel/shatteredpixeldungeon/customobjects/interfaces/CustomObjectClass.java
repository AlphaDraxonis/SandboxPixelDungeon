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

package com.shatteredpixel.shatteredpixeldungeon.customobjects.interfaces;

import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObjectManager;
import com.watabou.utils.Bundlable;

/**
 * All instances of user content that actually acts in-game must implement this interface
 * For the BluePrint, see class UserContent
 */
//basically LuaClass
public interface CustomObjectClass extends Bundlable {

	public static final String IDENTIFIER = "identifier";

	void setIdentifier(int identifier);
	int getIdentifier();

	/**
	 * Returns if this object is the one associated in CustomObject
	 * @return CustomObject.getLuaClass(getIdentifier()) == this
	 */
	static boolean isOriginal(Object self) {
		if (self instanceof LuaCustomObjectClass) {
			return CustomObjectManager.getLuaClass(((LuaCustomObjectClass) self).getIdentifier()) == self;
		}
		return true;
	}

	CustomObjectClass newInstance();

}
