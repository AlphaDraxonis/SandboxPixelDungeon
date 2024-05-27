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
import com.shatteredpixel.shatteredpixeldungeon.editor.Copyable;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

public class LuaManager {

	public static void callStaticInitializers() {
	}

	static final Globals globals = new LuaGlobals();

	public static boolean areScriptsRunning() {
		for (StackTraceElement[] stackTrace : Thread.getAllStackTraces().values()) {
			for (StackTraceElement element : stackTrace) {
				if (element.getClassName().contains("org.luaj.vm2")) {
					return true;
				}
			}
		}
		return false;
	}

	private static void showScriptRunningWarning(String methodName) {
		DungeonScene.show( new WndError(Messages.get(LuaManager.class, "inaccessible_method_called", methodName)) );
	}

	public static boolean checkAccess(String methodName) {
		if (areScriptsRunning()) {
			showScriptRunningWarning(methodName);
			return false;
		}
		return true;
	}

	public static void updateGlobalVars() {
		if (Dungeon.hero != null) {
			globals.set("hero", CoerceJavaToLua.coerce(Dungeon.hero));
		} else {
			globals.set("hero", ((LuaValue) null));
		}
		globals.set("customDungeon", CoerceJavaToLua.coerce(Dungeon.customDungeon));
		globals.set("level", CoerceJavaToLua.coerce(Dungeon.level));
		globals.set("depth", LuaValue.valueOf(Dungeon.depth));
		globals.set("branch", LuaValue.valueOf(Dungeon.branch));
		globals.set("version", LuaValue.valueOf(Game.version));
		globals.set("versionCode", LuaValue.valueOf(Game.versionCode));
		globals.set("seed", LuaValue.valueOf(Dungeon.seed));

		if (Dungeon.levelName != null) globals.set("levelSeed", LuaValue.valueOf(Dungeon.seedCurLevel()));

		updateStatistics();
	}

	public static void updateStatistics() {//tzz
		if (Dungeon.hero != null) {
//			globals.set("limitedDrops", CoerceJavaToLua.coerce(Dungeon.LimitedDrops.values()));
		} else {
//			globals.set("limitedDrops", ((LuaValue) null));
		}
	}


	public static String compile(String code) {
		try {
			globals.load(code).call();
			return null;
		} catch (LuaError e) {
//			"[string \"function die(this, vars, super, source)\n" +
//					"return source-m-.hashcode(...\"]:2: unexpected symbol 46 (.)";
			return e.getMessage();
		}
	}

	public static LuaValue load(String code) {
		return globals.load(code);
	}


	//*** FOR BUNDLING ***

	public static LuaValue deepCopyLuaValue(LuaValue value) {
		if (value.isuserdata()) {

			Object obj = value.touserdata();
			if (obj instanceof Copyable) obj = ((Copyable<?>) obj).getCopy();
			return new LuaUserdata(obj);

		} else if (value.istable()) {

			LuaTable originalTable = value.checktable();

			LuaTable copiedTable = new LuaTable();
			LuaValue[] keys = originalTable.keys();
			for (LuaValue key : keys) {
				LuaValue copiedValue = deepCopyLuaValue(originalTable.get(key));
				copiedTable.set(key, copiedValue);
			}
			return copiedTable;

		} else {
			return value;
		}
	}

	private static final String TYPE_OF_PREFIX = "__type_";
	private static final int TYPE_BUNDLABLE = 1, TYPE_INT = 2, TYPE_BOOLEAN = 3, TYPE_STRING = 4, TYPE_LONG = 5, TYPE_FLOAT = 6, TYPE_TABLE = 100;
	public static void storeVarInBundle(Bundle bundle, LuaValue value, String key) {

		String keyType = TYPE_OF_PREFIX + key;

		if (value.isuserdata()) {

			Object obj = value.touserdata();
			if (obj instanceof Bundlable) bundle.put(key, ((Bundlable) obj));
			else if (obj == null) bundle.put(key, ((Bundlable) null));
			else ;//cannot be stored! (this would also include classes ig)
			bundle.put(keyType, TYPE_BUNDLABLE);

		} else if (value.istable()) {

			bundle.put(key, new LuaTableBundlable(value.checktable()));
			bundle.put(keyType, TYPE_TABLE);

		} else {
			if (value.isint()) {
				bundle.put(key, value.toint());
				bundle.put(keyType, TYPE_INT);
			}
			else if (value.isboolean()) {
				bundle.put(key, value.toboolean());
				bundle.put(keyType, TYPE_BOOLEAN);
			}
			else if (value.isstring()) {
				bundle.put(key, value.toString());
				bundle.put(keyType, TYPE_STRING);
			}
			else if (value.islong()) {
				bundle.put(key, value.tolong());
				bundle.put(keyType, TYPE_LONG);
			}
			else if (value.isnumber()){
				bundle.put(key, value.tofloat());
				bundle.put(keyType, TYPE_FLOAT);
			}
			else if (value.isnil()) {
				bundle.put(key, ((Bundlable) null));
				bundle.put(keyType, TYPE_BUNDLABLE);
			}
		}
	}

	public static LuaValue restoreVarFromBundle(Bundle bundle, String key) {

		switch (bundle.getInt(TYPE_OF_PREFIX + key)) {
			default:
			case 0: return null;
			case TYPE_BUNDLABLE: return CoerceJavaToLua.coerce(bundle.get(key));
			case TYPE_INT: return LuaValue.valueOf(bundle.getInt(key));
			case TYPE_BOOLEAN: return LuaValue.valueOf(bundle.getBoolean(key));
			case TYPE_STRING: return LuaValue.valueOf(bundle.getString(key));
			case TYPE_LONG: return LuaValue.valueOf(bundle.getLong(key));
			case TYPE_FLOAT: return LuaValue.valueOf(bundle.getFloat(key));
			case TYPE_TABLE: return ((LuaTableBundlable) bundle.get(key)).luaTable;
		}
	}


	public static class LuaTableBundlable implements Bundlable {

		private LuaTable luaTable;

		public LuaTableBundlable() {
		}

		public LuaTableBundlable(LuaTable luaTable) {
			this.luaTable = luaTable;
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			luaTable = new LuaTable();
			for (String key : bundle.getKeys()) {
				if (!key.startsWith(TYPE_OF_PREFIX)) {
					LuaValue v = restoreVarFromBundle(bundle, key);
					if (v != null) luaTable.set(key, v);
				}

			}
		}

		@Override
		public void storeInBundle(Bundle bundle) {
			for (LuaValue k : luaTable.keys()) {
				storeVarInBundle(bundle, luaTable.get(k), k.toString());
			}
		}
	}

}