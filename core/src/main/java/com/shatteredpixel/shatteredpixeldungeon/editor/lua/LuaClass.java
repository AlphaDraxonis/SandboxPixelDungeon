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

import com.watabou.utils.Bundlable;

public interface LuaClass extends Bundlable {

    String VARS = "vars";
    String IDENTIFIER = "identifier";

    void setIdentifier(int identifier);
    int getIdentifier();

    /**
     * Returns if this object is the one associated in CustomObject
     * @return CustomObject.getLuaClass(getIdentifier()) == this
     */
    default boolean isOriginal() {
        return CustomObject.getLuaClass(getIdentifier()) == this;
    }

    //TODO tzz we also need a way to override mob sprites...

}