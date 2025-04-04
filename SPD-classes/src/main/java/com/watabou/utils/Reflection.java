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

package com.watabou.utils;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.watabou.NotAllowedInLua;
import com.watabou.noosa.Game;

//wrapper for libGDX reflection
@NotAllowedInLua
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
//			throw new RuntimeException(">>>"+cls+"<<<");
			Game.reportException(e);
			return null;
		}
	}
	
	public static <T> T newInstanceUnhandled( Class<T> cls ) throws Exception {
		return ClassReflection.newInstance(cls);
	}

	public static Function<Class<?>, Class<?>> makeToUserContentClass;

	public static Class forName( String name ){

		name = name.replace("alphadraxonis.sandboxpixeldungeon", "shatteredpixel.shatteredpixeldungeon");
//		name = name.replace("shatteredpixel.shatteredpixeldungeon","alphadraxonis.sandboxpixeldungeon");

		name = name.replace("gases.PermaGas", "other.PermaGas");

		int indexByteBuddy = name.indexOf("$ByteBuddy$");
		if (indexByteBuddy != -1) {
			name = name.substring(0, indexByteBuddy);
			return makeToUserContentClass.apply(forName(name));
		}

		if (name.endsWith("_lua")) {
			name = name.substring(0, name.length() - 4);
			name = name.replace(".luamobs.", ".");
			if (name.endsWith("Elemental")) {
				name = name.replace(".actors.mobs.", ".actors.mobs.Elemental$");
			} else if (name.endsWith("Fist")) {
				name = name.replace(".actors.mobs.", ".actors.mobs.YogFist$");
			} else if (name.endsWith("Shaman")) {
				name = name.replace(".actors.mobs.", ".actors.mobs.Shaman$");
			}
			else if (name.endsWith("Sentry")) {
				name = "com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SentryRoom$Sentry";
			} else if (name.endsWith("Lotus")) {
					name = "com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth$Lotus";
			} else if (name.endsWith("Larva")) {
				name = "com.shatteredpixel.shatteredpixeldungeon.actors.mobs.YogDzewa$Larva";
			}
			Class<?> cl = forName(name);
			Class<?> result;
			if (cl != null) {
				result = makeToUserContentClass.apply(cl);
			} else {
				name = name.replace(".actors.mobs.", ".actors.mobs.npcs.");
				result = makeToUserContentClass.apply(forName(name));
			}
			return result;
		}

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
