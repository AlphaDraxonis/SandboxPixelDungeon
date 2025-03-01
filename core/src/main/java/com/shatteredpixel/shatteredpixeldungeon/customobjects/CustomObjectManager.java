/*
 *
 *  * Pixel Dungeon
 *  * Copyright (C) 2012-2015 Oleg Dolya
 *  *
 *  * Shattered Pixel Dungeon
 *  * Copyright (C) 2014-2024 Evan Debenham
 *  *
 *  * Sandbox Pixel Dungeon
 *  * Copyright (C) 2023-2024 AlphaDraxonis
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */

package com.shatteredpixel.shatteredpixeldungeon.customobjects;

import com.badlogic.gdx.files.FileHandle;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GameObject;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.blueprints.CustomGameObject;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.blueprints.CustomMob;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.interfaces.CustomGameObjectClass;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.interfaces.CustomObjectClass;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.LoadCustomObjects;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import com.watabou.NotAllowedInLua;
import com.watabou.idewindowactions.LuaScript;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import org.luaj.vm2.LuaValue;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Utility class to keep track of all our UserContentClasses and their BluePrints
 */
@NotAllowedInLua
public final class CustomObjectManager {

	public static final Map<Integer, CustomObject> allUserContents = new HashMap<>();
	public static final Map<String, FileHandle> allResourcePaths = new HashMap<>();//extension shows type
	
	private static LuaValue globalVarsTemp;

	private CustomObjectManager() {
	}

	//Called when "synchronized manually", "game opened", "run has ended"
	//"run ended" = press exit in SideControlPane; go to TitleScene via MenuPane or death screen; win run
	public static void loadUserContentFromFiles() {
		loadUserContentFromFiles(null);
	}
	
	public static void loadUserContentFromFiles(Bundle bundle) {
		new LoadCustomObjects(bundle, false);
		loadScripts(false);
	}

	public static void loadScripts(boolean runActive) {
		LuaManager.callStaticInitializers();

		if (runActive) loadDungeonScript();

		for (CustomObject customObject : allUserContents.values()) {
			customObject.onLoad(runActive);
		}
	}

	public static void loadScript(CustomObject customObject) {
		LuaManager.callStaticInitializers();
		customObject.onLoad(false);
	}

	private static void loadDungeonScript() {
		if (Dungeon.dungeonScript != null) {
		
		}
		Dungeon.dungeonScript.unloadScript();
		if (Dungeon.customDungeon.dungeonScriptPath != null) {
			LuaScript ls = CustomDungeonSaves.readLuaFile(Dungeon.customDungeon.dungeonScriptPath);
			if (ls != null) {
				Dungeon.dungeonScript.loadScript(ls);

				if (globalVarsTemp == null && Dungeon.dungeonScript.getScript().istable()) {
					globalVarsTemp = Dungeon.dungeonScript.getScript().get("static");
				}
			}
		}
		LuaManager.globals.set("globals", globalVarsTemp == null ? LuaValue.NIL : globalVarsTemp);
		globalVarsTemp = null;
	}

	public static <T extends CustomObject> T getUserContent(int id, Class<T> clazz) {
		T result = (T) allUserContents.get(id);

		if (result == null && clazz != null && id != 0) {
			//not found for some reason, maybe file was deleted
			//just create it on the fly
			result = Reflection.newInstance(clazz);
			result.setIdentifier(id);
			result.setName("Unidentified Custom Object (" + id + ")");

			try {
				CustomDungeonSaves.storeCustomObject(result);
				allUserContents.put(id, result);
			} catch (IOException e) {
				Game.reportException(e);
				Game.runOnRenderThread(() -> DungeonScene.show(new WndError(e)));
				return result;
			}

		}
		return result;
	}

	public static LuaValue getScript(int id) {
		CustomObject customObject = getUserContent(id, null);
		if (customObject instanceof LuaCustomObject) {
			return ((LuaCustomObject) customObject).getLuaCodeHolder().getScript();
		}
		if (id == 0) {
			//some classes call native methods in their constructor before we even have a chance to set the id
//			Game.reportException(new Throwable("Attempted to retrieve a script with ID 0. Maybe it was forgotten to set the ID correctly when initialiting?"));
		}
		return null;
	}

	public static String getName(int id) {
		CustomObject customObject = getUserContent(id, null);
		return customObject == null ? null : customObject.getName();
	}

	public static CustomObjectClass getLuaClass(int id) {
		CustomObject customObject = getUserContent(id, null);
		return customObject instanceof CustomGameObject ? ((CustomGameObject<?>) customObject).getUserContentClass() : null;
	}

	public static <T extends CustomObject> T createNewCustomObject(Class<T> type, String name, String superClassName) {
		T obj = Reflection.newInstance(type);

		if (obj instanceof LuaCustomObject)
			((LuaCustomObject) obj).setTargetClass(LuaManager.maybeAddMainPackageName(superClassName));

		obj.setName(name);
		assignNewID(obj);
		try {
			CustomDungeonSaves.storeCustomObject(obj);
		} catch (IOException e) {
			Game.reportException(e);
			Game.runOnRenderThread(() -> DungeonScene.show(new WndError(e)));
			return null;
		}

		if (obj instanceof CustomGameObject) {
			((CustomGameObject<?>) obj).inventoryCategory().addCustomObject(((CustomGameObject<?>) obj).getUserContentClass());
		}

		return obj;
	}

	public static void assignNewID(CustomObject customObject) {
		int start = 100_000_000;
		int end = Integer.MAX_VALUE;
		int nextID;
		do {
			nextID = Random.Int(start, end);
		} while (allUserContents.containsKey(nextID) || nextID == -1);

		customObject.setIdentifier(nextID);

		allUserContents.put(nextID, customObject);
	}

	public static <T extends GameObject> Set<T> getAllCustomObjects(Class<? super T> luaClassSuperclass) {
		Set<T> ret = new HashSet<>();
		for (CustomObject customObject : allUserContents.values()) {

			if (customObject instanceof CustomGameObject) {
				CustomObjectClass clazz = ((CustomGameObject<?>) customObject).getUserContentClass();
				if (clazz != null && luaClassSuperclass.isAssignableFrom(clazz.getClass()))
					ret.add((T) clazz);
			}

		}
		return ret;
	}


	private static final String PRE_v_1_3_NODE = "lua_static";
	private static final String PRE_v_1_3_CUSTOM_OBJECTS = "custom_objects";
	private static final String PRE_v_1_3_GLOBAL_VARS = "global_vars";
	private static final String PRE_v_1_3_DUNGEON_SCRIPT_PATH = "dungeon_script_path";

	public static void restorePre_v_1_3(Bundle bundle) {

		if (!bundle.contains(PRE_v_1_3_NODE)) {
			return;
		}

		Bundle node = bundle.getBundle(PRE_v_1_3_NODE);

		if (node.contains(PRE_v_1_3_DUNGEON_SCRIPT_PATH))
			Dungeon.customDungeon.dungeonScriptPath = node.getString(PRE_v_1_3_DUNGEON_SCRIPT_PATH);

		Bundle[] array = node.getBundleArray(PRE_v_1_3_CUSTOM_OBJECTS);
		for (Bundle b : array) {
			Bundle mobBundle = b.getBundle("lua_class");
			int id = mobBundle.getInt("identifier");
			String name = b.getString("name");
			Class<?> targetClass = b.getClass("clazz");
			if (Mob.class.isAssignableFrom(targetClass)) {
				CustomMob m = new CustomMob();
				m.setName(name);
				m.setIdentifier(id);
				allUserContents.put(id, m);

				m.setTargetClass(targetClass.getName());
				if (m.getUserContentClass() != null) {
					m.getUserContentClass().restoreFromBundle(mobBundle);
				}

				try {
					if (m.saveDirPath == null)  {
						m.saveDirPath = m.defaultSaveDir();
						m.saveDirPath += CustomDungeonSaves.fileName(m);
					}
					CustomDungeonSaves.storeCustomObject(m);
				} catch (IOException ignored) {
				}
			}
		}

		if (node.contains(PRE_v_1_3_GLOBAL_VARS)) {
			LuaValue loaded = LuaManager.restoreVarFromBundle(node, PRE_v_1_3_GLOBAL_VARS);
			if (loaded != null && loaded.istable()) {
				globalVarsTemp = loaded.checktable();
			} else {
				globalVarsTemp = null;
			}
		}
	}


//	//Dungeon-Specific
//
//	private static LuaValue globalVars;
//
//	private static final String NODE = "lua_static";
//	private static final String CUSTOM_OBJECTS = "custom_objects";
//	private static final String GLOBAL_VARS = "global_vars";
//	private static final String DUNGEON_SCRIPT_PATH = "dungeon_script_path";
//
//	public static void restore(Bundle bundle) {
//
//		reset();
//
//		if (!bundle.contains(NODE)) {
//			return;
//		}
//
//		Bundle node = bundle.getBundle(NODE);
//
//		if (node.contains(DUNGEON_SCRIPT_PATH))
//			Dungeon.dungeonScript.pathToScript = node.getString(DUNGEON_SCRIPT_PATH);
//
//		for (Bundlable b : node.getCollection(CUSTOM_OBJECTS)) {
//			CustomObject clo = (CustomObject) b;
//			userContentMap.put(clo.luaClass.getIdentifier(), clo);
//		}
//
//		LuaValue loaded = LuaManager.restoreVarFromBundle(bundle, GLOBAL_VARS);
//		if (loaded != null && loaded.istable()) {
//			globalVars = loaded.checktable();
//		} else {
//			globalVars = null;
//		}
//	}
//
//	public static void store(Bundle bundle) {
//		Bundle node = new Bundle();
//		node.put(CUSTOM_OBJECTS, userContentMap.values());
//		bundle.put(NODE, node);
//
//		if (Dungeon.dungeonScript.pathToScript != null)
//			node.put(DUNGEON_SCRIPT_PATH, Dungeon.dungeonScript.pathToScript);
//
//		if (globalVars != null && globalVars.istable() && !CustomDungeon.isEditing()) {
//			LuaManager.storeVarInBundle(bundle, globalVars, GLOBAL_VARS);
//		}
//	}
//
	public static <T extends CustomGameObjectClass> void overrideOriginal(CustomObjectClass clazz) {
		CustomObject obj = allUserContents.get(clazz.getIdentifier());
		if (obj instanceof CustomGameObject)
			((CustomGameObject<T>) obj).setUserContentClass(((T) clazz));
	}

}
