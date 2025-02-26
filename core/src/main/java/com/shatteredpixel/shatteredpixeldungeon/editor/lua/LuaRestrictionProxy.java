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

import com.badlogic.gdx.files.FileHandle;
import com.watabou.NotAllowedInLua;
import com.watabou.noosa.Game;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class LuaRestrictionProxy extends LuaValue {

	private final Object javaObject; // Original Java object

	private static final Map<Class<?>, Map<String, Map<Integer, Method>> > ACCESSIBLE_METHODS_MAP = new HashMap<>();

	public LuaRestrictionProxy(Object javaObject) {
		this.javaObject = javaObject;

		if (!ACCESSIBLE_METHODS_MAP.containsKey(javaObject.getClass())) {
			Map<String, Map<Integer, Method>> methods = new HashMap<>();
			Class<?> clazz = javaObject.getClass();
			do {
				for (Method m : clazz.getDeclaredMethods()) {
					if (m.isAnnotationPresent(NotAllowedInLua.class)) {
						continue;
					}
					int mods = m.getModifiers();
					
					Map<Integer, Method> methodsWithSameName = methods.get(m.getName());
					if (methodsWithSameName == null) {
						methodsWithSameName = new HashMap<>();
						methods.put(m.getName(), methodsWithSameName);
					}
					
					if (Modifier.isPublic(mods) || Modifier.isProtected(mods)) {
						methodsWithSameName.put(m.getParameterCount(), m);
					}
				}

				clazz = clazz.getSuperclass();
			} while (clazz != null);
			
			ACCESSIBLE_METHODS_MAP.put(javaObject.getClass(), methods);
		}

	}

	@Override
	public LuaValue get(LuaValue key) {
		try {
			String name = key.tojstring();
			
			if (!name.endsWith("_v")) {
				Map<Integer, Method> methods = ACCESSIBLE_METHODS_MAP.get(javaObject.getClass()).get(name);
				if (methods != null) {
					return new FunctionInterceptor(methods, javaObject);
				}
			} else {
				name = name.substring(0, name.length() - 2);
			}

			Field field = getField(name);

			if (isFieldRestricted(field)) {
				return NIL;
			}
			
			if (field == null) {
				if (name.equals("simpleClassName")) {
					return wrapObject(javaObject.getClass().getSimpleName());
				}
				if (name.equals("className")) {
					return wrapObject(javaObject.getClass().getName());
				}
				return NIL;
			}

			return wrapObject(field.get(javaObject));

		} catch (Exception e) {
			Game.reportException(e);
			return NIL;
		}
	}
	
	private static final class FunctionInterceptor extends VarArgFunction {
		
		private final Map<Integer, Method> methods;
		private final Object javaObject;
		
		private FunctionInterceptor(Map<Integer, Method> methods, Object javaObject) {
			this.methods = methods;
			this.javaObject = javaObject;
		}
		
		@Override
		public Varargs invoke(Varargs varargs) {
			Method method = methods.get( varargs.narg()-1 );
			try {
				Object[] javaArgs = new Object[method.getParameterCount()];
				for (int i = 0; i < javaArgs.length; i++) {
					javaArgs[i] = coerceLuaToJava(varargs.arg(i + 2), method.getParameterTypes()[i]);
				}
				return wrapObject(method.invoke(javaObject, javaArgs));
			} catch (Exception e) {
				return new LuaRestrictionProxy(e);
			}
		}
	}

	@Override
	public void set(LuaValue key, LuaValue value) {
		try {
			Field field = getField(key.tojstring());

			if (isFieldRestricted(field)) {
				return;
			}

			Object javaV = coerceLuaToJava(value, field.getType());
			field.set(javaObject, javaV);

		} catch (Exception e) {
			Game.reportException(e);
		}
	}

	private Field getField(String name) {
		Field field = null;
		try {
			//if the field is public
			field = javaObject.getClass().getField(name);
		} catch (NoSuchFieldException e) {
			//if the field is protected
			Class<?> clazz = javaObject.getClass();
			do {
				for (Field f : clazz.getDeclaredFields()) {
					if (f.getName().equals(name)) {
						if (Modifier.isProtected(f.getModifiers())) {
							f.setAccessible(true);
							field = f;
							break;
						}
					}
				}

				clazz = clazz.getSuperclass();
			} while (clazz != null);
		}
		return field;
	}


	public static LuaValue wrapObject(Object object) {
		if (object == null || isRestricted(object)) {
			return NIL;
		}
		if (object instanceof Number) {
			if (object instanceof Integer) return LuaValue.valueOf((int) object);
			if (object instanceof Float) return LuaValue.valueOf((float) object);
			if (object instanceof Long) return LuaValue.valueOf((long) object);
			if (object instanceof Double) return LuaValue.valueOf((double) object);
			if (object instanceof Byte) return LuaValue.valueOf((byte) object);
			if (object instanceof Short) return LuaValue.valueOf((short) object);
		}
		if (object instanceof String) return LuaValue.valueOf((String) object);
		if (object instanceof Boolean) return LuaValue.valueOf((Boolean) object);
		return new LuaRestrictionProxy(object);
	}

	public static Object coerceLuaToJava(LuaValue luaValue) {
		return coerceLuaToJava(luaValue, Object.class);
	}

	// WARNING: NEVER use CoerceLuaToJava.coerce anywhere else! use LuaRestrictionPolicy.coerceLuaToJava(obj) instead!
	public static Object coerceLuaToJava(LuaValue luaValue, Class<?> aClass) {
		if (luaValue instanceof LuaRestrictionProxy) return luaValue.touserdata();
		return CoerceLuaToJava.coerce(luaValue, aClass);
	}

	//WARNING! use carefully or else this is a security leak!
	public static Varargs unwrapRestrictionProxies(Varargs varargs) {
		LuaValue[] result = new LuaValue[varargs.narg()];
		for (int i = 0; i < result.length; i++) {
			LuaValue v = varargs.arg(i+1);
			if (v instanceof LuaRestrictionProxy) result[i] = LuaValue.userdataOf(coerceLuaToJava(v));
			else result[i] = v;
		}
		return LuaValue.varargsOf(result);
	}
	
	public static Object[] unwrapRestrictionProxiesAsJavaArray(Varargs varargs) {
		Object[] result = new Object[varargs.narg()];
		for (int i = 0; i < result.length; i++) {
			result[i] = coerceLuaToJava( varargs.arg(i+1) );
		}
		return result;
	}


	public static boolean isFieldRestricted(Field field) {
//		Class<?> fieldType = field.getType();
//		return isRestricted(fieldType);
		return false;
	}

	public static boolean isRestricted(Object obj) {
		if (obj == null) return false;
		Class<?> clazz = obj instanceof Class<?> ? (Class<?>) obj : obj.getClass();
		Class<?> superclass = clazz;
		do {
			if (superclass.isAnnotationPresent(NotAllowedInLua.class)) {
				return true;
			}
			superclass = superclass.getSuperclass();
		} while (superclass != null);

		return FileHandle.class.isAssignableFrom(clazz)
				|| File.class.isAssignableFrom(clazz)
				|| OutputStream.class.isAssignableFrom(clazz)
				|| InputStream.class.isAssignableFrom(clazz);
	}

	@Override
	public int type() {
		return LuaValue.TUSERDATA;
	}

	@Override
	public String typename() {
		return getClass().getSimpleName();
	}

	@Override
	public Object touserdata() {
		return javaObject;
	}

	@Override
	public Object touserdata(Class c) {
		return c.isAssignableFrom(javaObject.getClass()) ? javaObject : null;
	}
	
	@Override
	public Object checkuserdata() {
		return touserdata();
	}
	
	@Override
	public Object checkuserdata(Class aClass) {
		return touserdata(aClass);
	}
	
	@Override
	public boolean isuserdata() {
		return true;
	}
	
	@Override
	public boolean isuserdata(Class aClass) {
		return javaObject != null && aClass !=null && aClass.isAssignableFrom(javaObject.getClass());
	}
	
	@Override
	public LuaValue arg(int index) {
		if (index == 0) return this;
		return super.arg(index);
	}

	@Override
	public String toString() {
		if (javaObject instanceof Exception) return javaObject.getClass().getSimpleName() + ": " + ((Exception) javaObject).getMessage();
		else return javaObject.toString();
	}
}