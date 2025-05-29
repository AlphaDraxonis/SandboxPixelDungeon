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

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Locale;

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
	
	public static <T> T newInstance( Class<T> cls, Object[] params ){
		Constructor constructor = findBestMatchingExecutableC(params, cls.getConstructors());
		if (constructor == null) {
			Game.reportException(new NoSuchMethodException("No matching constructor found: " + cls.getSimpleName()));
			return null;
		} else {
			try {
				return (T) constructor.newInstance(makeParamsFitVarArgsMethods(params, constructor.getParameterTypes(), constructor.isVarArgs()));
			} catch (Exception e) {
				Game.reportException(e);
				throw new RuntimeException(e);
			}
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
	
	
	
	public static Method findBestMatchingExecutableM(Object[] params, Iterable<? extends Method> executables) {
		oneExe:
		for (Method exe : executables) {
			Class<?>[] paramTypes = exe.getParameterTypes();
			
			boolean matchesFirst;
			if (paramTypes.length == params.length) {
				
				matchesFirst = true;
				int i = 0;
				for (; i < paramTypes.length - 1; i++) {
					if (!isArgumentApplicable(paramTypes[i], params[i])) {
						matchesFirst = false;
						break;
					}
				}
				if (paramTypes.length == 0 || isArgumentApplicable(paramTypes[i], params[i])) return exe;
			} else {
				matchesFirst = false;
			}
			if (exe.isVarArgs()) {
				if (!matchesFirst) {
					for (int i = 0; i < paramTypes.length - 1; i++) {
						if (!isArgumentApplicable(paramTypes[i], params[i])) {
							continue oneExe;
						}
					}
				}
				Class<?> lastParamType = paramTypes[paramTypes.length-1].getComponentType();
				for (int i = paramTypes.length-1; i < params.length - 1; i++) {
					if (!isArgumentApplicable(lastParamType, params[i])) {
						continue oneExe;
					}
				}
				return exe;
			}
			
		}
		return null;
	}
	
	public static Constructor findBestMatchingExecutableC(Object[] params, Constructor[] executables) {
		oneExe:
		for (Constructor exe : executables) {
			Class<?>[] paramTypes = exe.getParameterTypes();
			
			boolean matchesFirst;
			if (paramTypes.length == params.length) {
				
				matchesFirst = true;
				int i = 0;
				for (; i < paramTypes.length - 1; i++) {
					if (!isArgumentApplicable(paramTypes[i], params[i])) {
						matchesFirst = false;
						break;
					}
				}
				if (paramTypes.length == 0 || isArgumentApplicable(paramTypes[i], params[i])) return exe;
			} else {
				matchesFirst = false;
			}
			if (exe.isVarArgs()) {
				if (!matchesFirst) {
					for (int i = 0; i < paramTypes.length - 1; i++) {
						if (!isArgumentApplicable(paramTypes[i], params[i])) {
							continue oneExe;
						}
					}
				}
				Class<?> lastParamType = paramTypes[paramTypes.length-1].getComponentType();
				for (int i = paramTypes.length-1; i < params.length - 1; i++) {
					if (!isArgumentApplicable(lastParamType, params[i])) {
						continue oneExe;
					}
				}
				return exe;
			}
			
		}
		return null;
	}
	
	public static Object[] makeParamsFitVarArgsMethods(Object[] params, Class<?>[] paramTypes, boolean isVarArgs) {
		Object[] result = new Object[paramTypes.length];
		int i = 0;
		for (; i < result.length-1; i++) {
			result[i] = castArgumentAccordingly(params[i], paramTypes[i]);
		}
		if (!isVarArgs) {
			if (i < result.length) result[i] = castArgumentAccordingly(params[i], paramTypes[i]);
			return result;
		}
		int numVarArgs = params.length - result.length;
		Class<?> componentType = paramTypes[i].getComponentType();
		result[i] = Array.newInstance(componentType, numVarArgs);
		for (int j = 0; j < numVarArgs; j++) {
			Array.set(result[i], j, params[i + j]);
		}
		return result;
	}
	
	private static boolean isArgumentApplicable(Class<?> methodParam, Object argumentClass) {
		if (argumentClass == null || isArgumentApplicable(methodParam, argumentClass.getClass())) return true;
		if (methodParam.isArray() && argumentClass.getClass().isArray()) {
			if (Array.getLength(argumentClass) == 0) return true;
		}
		return false;
	}
	
	private static boolean isArgumentApplicable(Class<?> methodParam, Class<?> argumentClass) {
		if (methodParam.isAssignableFrom(argumentClass)) {
			return true;
		}
		if (methodParam.isPrimitive() || Number.class.isAssignableFrom(methodParam)) {
			switch (methodParam.getSimpleName().toLowerCase(Locale.ENGLISH)) {
				case "float":
				case "double":
					if (argumentClass == Float.class || argumentClass == float.class  ||  argumentClass == Double.class || argumentClass == double.class)
						return true;
				
				case "byte":
				case "short":
				case "long":
				case "integer":
				case "int": return argumentClass == Integer.class || argumentClass == int.class
						|| argumentClass == Long.class || argumentClass == long.class
						|| argumentClass == Short.class || argumentClass == short.class
						|| argumentClass == Byte.class || argumentClass == byte.class
						
						|| argumentClass == Float.class || argumentClass == float.class  ||  argumentClass == Double.class || argumentClass == double.class;
				
				
				case "boolean": return argumentClass == Boolean.class || argumentClass == boolean.class;
				
				case "char": return argumentClass == Character.class || argumentClass == char.class;
				
			}
		}
		if (argumentClass.isPrimitive() || Number.class.isAssignableFrom(argumentClass)) {
			switch (argumentClass.getSimpleName().toLowerCase(Locale.ENGLISH)) {
				case "byte":
				case "short":
				case "long":
				case "integer":
				case "int":
					if (methodParam == Integer.class || methodParam == int.class
							|| methodParam == Long.class || methodParam == long.class
							|| methodParam == Short.class || methodParam == short.class
							|| methodParam == Byte.class || methodParam == byte.class
							
							|| methodParam == Float.class || methodParam == float.class  ||  methodParam == Double.class || methodParam == double.class)
						return true;
				
				case "float":
				case "double":
					return methodParam == Float.class || methodParam == float.class  ||  methodParam == Double.class || methodParam == double.class;
				
				case "boolean": return methodParam == Boolean.class || methodParam == boolean.class;
				
				case "char": return methodParam == Character.class || methodParam == char.class;
			}
		}
		
		return false;
	}
	
	private static Object castArgumentAccordingly(Object param, Class<?> paramType) {
		if (param instanceof Number) {
			switch (paramType.getSimpleName().toLowerCase(Locale.ENGLISH)) {
				case "byte": 	return ((Number) param).byteValue();
				case "short": 	return ((Number) param).shortValue();
				case "long": 	return ((Number) param).longValue();
				case "int":
				case "integer": return ((Number) param).intValue();
				case "float": 	return ((Number) param).floatValue();
				case "double": 	return ((Number) param).doubleValue();
				default: 		return param;
			}
		}
		return param;
	}
	
}
