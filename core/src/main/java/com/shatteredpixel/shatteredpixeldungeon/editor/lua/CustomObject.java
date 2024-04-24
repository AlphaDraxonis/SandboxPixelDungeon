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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.luamobs.Mob_lua;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Mobs;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.MobItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPartList;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import org.luaj.vm2.LuaValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomObject implements Bundlable {

	//TODO find/restore in toolbar!

	public LuaClass luaClass;
	public String name;

	//TODO tzz we still need to configure synchronization! like all mobs should use the same description if not overriding
	// (maybe just add a checkbox in EdiMobComp 'inherit stats' and if checked, it just copies all changable stats to the LuaClass?)

	public String pathToScript;

	private LuaValue script;
	private LuaValue staticVarsTemp;

	private static LuaValue globalVars;

	public Image getSprite() {
		if (luaClass instanceof Mob) return ((Mob) luaClass).sprite();
		return new ItemSprite();
	}

	public void loadScript() {

		LuaScript ls = CustomDungeonSaves.readLuaFile(pathToScript);
		if (ls != null) {
			script = LuaManager.globals.load(ls.code).call();
			if (staticVarsTemp != null) {
				script.set("static", staticVarsTemp);
				staticVarsTemp = null;
			}
		}
		else script = null;

//		script = LuaManager.globals.load(
//				vars +
////                         "function attackSkill(this, vars) " +
////                         "if vars.item == nil then" +
////                         "   vars.item = luajava.newInstance(\"com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfFrost\")" +
////                         " else  level:drop(luajava.newInstance(\"com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing\"), this.pos + level:width()).sprite:drop()" +
////                         " end  vars.static.aNumber = vars.static.aNumber + 1 return vars.static.aNumber" +
////                         " end " +
//
//						"function die(this, vars, super, cause) " +
//						"local item = luajava.newInstance(\"com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfFrost\")" +
//						"level:drop(item, this.pos + level:width()).sprite:drop()" +
//						"super:call({cause})" +
//						" end  " +
//
//						"return {" +
////                         "attackProc = attackProc; " +
//						"die = die;" +
//						"vars = vars " +
//						"}").call();
	}


	public static void loadScripts() {
		LuaManager.callStaticInitializers();
		if (globalVars == null) globalVars = LuaManager.globals.load("return {globus = 6}").call();//TODO initialize globals from a file tzz
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
	private static final String PATH_TO_SCRIPT = "path_to_script";
	private static final String STATIC_VARS = "static_vars";

	@Override
	public void restoreFromBundle(Bundle bundle) {
		luaClass = (LuaClass) bundle.get(LUA_CLASS);
		name = bundle.getString(NAME);
		pathToScript = bundle.getString(PATH_TO_SCRIPT);

		LuaValue loaded = LuaManager.restoreVarFromBundle(bundle, STATIC_VARS);
		if (loaded != null && loaded.istable()) {
			staticVarsTemp = loaded.checktable();
		}
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		bundle.put(LUA_CLASS, luaClass);
		bundle.put(NAME, name);
		bundle.put(PATH_TO_SCRIPT, pathToScript);

		if (script != null && script.get("static").istable() && !CustomDungeon.isEditing()) {
			LuaManager.storeVarInBundle(bundle, script.get("static"), STATIC_VARS);
		}
	}

	public static void assignNewID(CustomObject customObject) {
		while (customObjects.containsKey(nextCustomObjectID++)) ;

		customObject.luaClass = new Mob_lua();//tzz this is just temporarily!
		((Mob_lua) customObject.luaClass).pos = -1;


		customObjects.put(nextCustomObjectID, customObject);
		customObject.luaClass.setIdentifier(nextCustomObjectID++);

	}

	public static void deleteCustomObject(int identifier) {//TODO tzz what about toolbar?
		if (customObjects.containsKey(identifier)) {

			CustomObject toDelete = customObjects.get(identifier);

			Undo.startAction();

			ActionPartList actionPart = new ActionPartList() {
				@Override
				public void undo() {
					customObjects.put(identifier, toDelete);
					Mobs.updateCustomMobsInInv();
					super.undo();
				}

				@Override
				public void redo() {
					customObjects.remove(identifier);
					Mobs.updateCustomMobsInInv();
					super.redo();
				}

				@Override
				public boolean hasContent() {
					return true;
				}
			};

			//TODO might want to be more flexible!
			if (Dungeon.level != null) {
				for (Mob m : Dungeon.level.mobs.toArray(new Mob[0])) {
					if (m instanceof LuaClass && ((LuaClass) m).getIdentifier() == identifier) {
						actionPart.addActionPart(MobItem.remove(m));
					}
				}
			}

			Undo.addActionPart(actionPart);

			Undo.endAction();

			actionPart.redo();
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


	private static final String NODE = "lua_static";
	private static final String NEXT_CUSTOM_OBJECT_ID = "next_custom_object_id";
	private static final String CUSTOM_OBJECTS = "custom_objects";
	private static final String GLOBAL_VARS = "global_vars";

	public static void restore(Bundle bundle) {

		reset();

		if (!bundle.contains(NODE)) {
			return;
		}

		Bundle node = bundle.getBundle(NODE);

		nextCustomObjectID = node.getInt(NEXT_CUSTOM_OBJECT_ID);

		for (Bundlable b : node.getCollection(CUSTOM_OBJECTS)) {
			CustomObject clo = (CustomObject) b;
			customObjects.put(clo.luaClass.getIdentifier(), clo);
		}

		LuaValue loaded = LuaManager.restoreVarFromBundle(bundle, GLOBAL_VARS);
		if (loaded != null && loaded.istable()) {
			globalVars = loaded.checktable();
		}
	}

	public static void store(Bundle bundle) {
		Bundle node = new Bundle();
		node.put(NEXT_CUSTOM_OBJECT_ID, nextCustomObjectID);
		node.put(CUSTOM_OBJECTS, customObjects.values());
		bundle.put(NODE, node);

		if (globalVars != null && globalVars.istable() && !CustomDungeon.isEditing()) {
			LuaManager.storeVarInBundle(bundle, globalVars, GLOBAL_VARS);
		}
	}

	public static void overrideOriginal(LuaClass luaClass) {
		customObjects.get(luaClass.getIdentifier()).luaClass = luaClass;
	}

	public static void reset() {
		customObjects.clear();
		nextCustomObjectID = 1;
	}

}