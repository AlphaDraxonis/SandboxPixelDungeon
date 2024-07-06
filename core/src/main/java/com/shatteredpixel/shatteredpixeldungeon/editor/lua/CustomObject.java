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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Mobs;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Reflection;
import org.luaj.vm2.LuaValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomObject extends LuaCodeHolder {

	public LuaClass luaClass;
	public String name;

	private static LuaValue globalVars;

	public Image getSprite() {
		if (luaClass instanceof Mob) return ((Mob) luaClass).sprite();
		return new ItemSprite();
	}

	public static void loadScripts() {
		LuaManager.callStaticInitializers();

		if (globalVars == null) {
			StringBuilder createGlobalsScript = new StringBuilder();
			for (String s : globalVarsDefaults) {
				if (!s.isEmpty()) createGlobalsScript.append(s).append(",");
			}
			globalVars = LuaManager.globals.load("return {"+ createGlobalsScript +"}").call();
		}
		LuaManager.globals.set("globals", globalVars);

		for (CustomObject obj : customObjects.values()) {
			obj.loadScript();
		}
	}


	public static LuaValue getScript(int id) {
		CustomObject clo = customObjects.get(id);
		return clo == null ? null : clo.script;
	}

	public static String getName(int id) {
		CustomObject clo = customObjects.get(id);
		return clo == null ? null : clo.name;
	}


	public static LuaClass getLuaClass(int identifier) {
		CustomObject obj = customObjects.get(identifier);
		return obj == null ? null : obj.luaClass;
	}


	private static final String LUA_CLASS = "lua_class";
	private static final String NAME = "name";

	@Override
	public void restoreFromBundle(Bundle bundle) {
		luaClass = (LuaClass) bundle.get(LUA_CLASS);
		name = bundle.getString(NAME);
		super.restoreFromBundle(bundle);
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		bundle.put(LUA_CLASS, luaClass);
		bundle.put(NAME, name);
		super.storeInBundle(bundle);
	}

	public static void assignNewID(CustomObject customObject) {
		while (customObjects.containsKey(nextCustomObjectID)) nextCustomObjectID++;
		
		customObject.clazz = customObject.luaClass.getClass().getSuperclass();


		customObjects.put(nextCustomObjectID, customObject);
		customObject.luaClass.setIdentifier(nextCustomObjectID++);

	}

	public static void deleteCustomObject(int identifier) {
		if (customObjects.containsKey(identifier)) {

			CustomObject toDelete = customObjects.get(identifier);

			customObjects.remove(identifier);
			Mobs.updateCustomMobsInInv();
			Mobs.deleteCustomMobFromOtherContainers(toDelete, identifier, false);
			Dungeon.customDungeon.deleteCustomObj(toDelete, identifier, true);
		}
	}

	public static <T> List<T> getAllCustomObjects(Class<? super T> luaClassSuperclass) {
		List<T> ret = new ArrayList<>();
		for (CustomObject obj : customObjects.values()) {
			if (obj.luaClass != null && luaClassSuperclass.isAssignableFrom(obj.luaClass.getClass()))
				ret.add((T) obj.luaClass);
		}
		return ret;
	}


	public static int nextCustomObjectID = 1;
	public static Map<Integer, CustomObject> customObjects = new HashMap<>();

	public static List<String> globalVarsDefaults = new ArrayList<>(7);//not saved once game has started!


	private static final String NODE = "lua_static";
	private static final String CUSTOM_OBJECTS = "custom_objects";
	private static final String GLOBAL_VARS = "global_vars";
	private static final String GLOBAL_VARS_DEFAULTS = "global_vars_defaults";

	public static void restore(Bundle bundle) {

		reset();

		if (!bundle.contains(NODE)) {
			return;
		}

		Bundle node = bundle.getBundle(NODE);

		for (Bundlable b : node.getCollection(CUSTOM_OBJECTS)) {
			CustomObject clo = (CustomObject) b;
			customObjects.put(clo.luaClass.getIdentifier(), clo);
		}

		globalVarsDefaults.clear();

//		LuaValue loaded = LuaManager.restoreVarFromBundle(bundle, GLOBAL_VARS);
//		if (loaded != null && loaded.istable()) {
//			globalVars = loaded.checktable();
//		} else {
//			String[] array = node.getStringArray(GLOBAL_VARS_DEFAULTS);
//			if (array != null) globalVarsDefaults.addAll(Arrays.asList(array));
//		}
	}

	public static void store(Bundle bundle) {
		Bundle node = new Bundle();
		node.put(CUSTOM_OBJECTS, customObjects.values());
		bundle.put(NODE, node);

		if (globalVars != null && globalVars.istable() && !CustomDungeon.isEditing()) {
			LuaManager.storeVarInBundle(bundle, globalVars, GLOBAL_VARS);
		} else {
			bundle.put(GLOBAL_VARS_DEFAULTS, globalVarsDefaults.toArray(EditorUtilies.EMPTY_STRING_ARRAY));
		}
	}

	public static void overrideOriginal(LuaClass luaClass) {
		customObjects.get(luaClass.getIdentifier()).luaClass = luaClass;
	}

	public static void reset() {
		customObjects.clear();
		nextCustomObjectID = 1;
	}


	public static <T extends Bundlable> T returnReplacementOnDeleteCustomObj(T obj, int identifier, boolean replace) {

		if (identifier == -1) return null;

		if (obj instanceof LuaClass && ((LuaClass) obj).getIdentifier() == identifier) {

			if (replace) {
				Bundle bundle = new Bundle();
				bundle.put("obj", obj);
				T replacement = (T) Reflection.newInstance(obj.getClass().getSuperclass());
				replacement.restoreFromBundle(bundle.getBundle("obj"));

				if (obj instanceof Mob) {
					if (((Mob) obj).sprite != null) {
						EditorScene.add(((Mob) replacement));
						((Mob) obj).sprite.remove();
						((Mob) obj).sprite.destroy();
					}
				}
				//TODO maybe add items etc here as well...

				obj = replacement;
			} else {
				return null;
			}
		}

		return obj;
	}

}