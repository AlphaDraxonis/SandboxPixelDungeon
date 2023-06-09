/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2023 Evan Debenham
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

package com.watabou.utils;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.watabou.noosa.Game;

//wrapper for libGDX reflection
public class Reflection {
	
	public static boolean isMemberClass( Class cls ){
		return ClassReflection.isMemberClass(cls);
	}
	
	public static boolean isStatic( Class cls ){
		return ClassReflection.isStaticClass(cls);
	}
	
	public static <T> T newInstance( Class<T> cls ){
		try {
			return ClassReflection.newInstance(cls);
		} catch (Exception e) {
			Game.reportException(e);
			return null;
		}
	}
	
	public static <T> T newInstanceUnhandled( Class<T> cls ) throws Exception {
		return ClassReflection.newInstance(cls);
	}
	
	public static Class forName( String name ){
		if (name.equals("com.shatteredpixel.shatteredpixeldungeon.levels.editor.CustomDungeon"))//TODO remove in future!
			name = "com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon";
		else if (name.equals("com.shatteredpixel.shatteredpixeldungeon.levels.editor.LevelScheme"))//TODO remove in future!
			name = "com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme";
		else if (name.equals("com.shatteredpixel.shatteredpixeldungeon.levels.editor.CustomLevel"))//TODO remove in future!
			name = "com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel";
		else if (name.equals("com.alphadraxonis.sandboxpixeldungeon.editor.overview.CustomDungeonSaves$Info"))//TODO remove in future!
			name = "com.alphadraxonis.sandboxpixeldungeon.editor.util.CustomDungeonSaves$Info";

		name = name.replace("shatteredpixel.shatteredpixeldungeon","alphadraxonis.sandboxpixeldungeon");

		try {
			return ClassReflection.forName( name );
		} catch (Exception e) {
//			throw new RuntimeException(">>>"+name+"<<<");
			Game.reportException(e);
			return null;
		}
	}
	
	public static Class forNameUnhandled( String name ) throws Exception {
		return ClassReflection.forName( name );
	}
	
}