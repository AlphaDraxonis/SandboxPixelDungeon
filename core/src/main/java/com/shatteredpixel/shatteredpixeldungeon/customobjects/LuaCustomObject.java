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

package com.shatteredpixel.shatteredpixeldungeon.customobjects;

import com.shatteredpixel.shatteredpixeldungeon.customobjects.interfaces.LuaCustomObjectClass;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.EditorInventory;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.watabou.utils.Bundle;

import java.util.Collections;
import java.util.List;

public abstract class LuaCustomObject extends CustomObject {

	//uses composition pattern
	private final LuaCodeHolder luaCodeHolder;

	private final ResourcePath scriptPath = new ResourcePath();

	public LuaCustomObject() {
		luaCodeHolder = new LuaCodeHolder();
	}

	public LuaCodeHolder getLuaCodeHolder() {
		return luaCodeHolder;
	}

	@Override
	public String[] allResourceFiles() {
		return new String[]{scriptPath.getPath()};
	}

	@Override
	protected void onLoad(boolean runActive) {
		super.onLoad(runActive);
		loadScript(runActive);
	}

	protected void loadScript(boolean loadOnlyIfRunIsActive) {
		if (loadOnlyIfRunIsActive) {
			getLuaCodeHolder().loadScript(CustomObject.loadScriptFromFile(scriptPath.getPath()));
		}
	}

	public abstract LuaCustomObjectClass newInstance();

	public abstract boolean isSuperclassValid(Class<?> superClass);
	public abstract void setTargetClass(String superClass);
	public abstract Class<?> getLuaTargetClass();//so the IDEWindow knows which methods are available

	public abstract Class<? extends Bag> preferredBag();

	public List<Bag> getBags() {
		return Collections.singletonList(EditorInventory.getBag(preferredBag()));
	}

	public void setLuaScriptPath(String path) {
		scriptPath.setPath(path);
	}

	public String getLuaScriptPath() {
		return scriptPath.getPath();
	}

	private static final String SCRIPT_PATH = "script_path";
	private static final String LUA_CODE_HOLDER = "lua_code_holder";

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		scriptPath.restoreFromBundle(bundle, SCRIPT_PATH);
		luaCodeHolder.restoreFromBundle(bundle.getBundle(LUA_CODE_HOLDER));
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		scriptPath.storeInBundle(bundle, SCRIPT_PATH);

		Bundle node = new Bundle();
		luaCodeHolder.storeInBundle(node);
		bundle.put(LUA_CODE_HOLDER, node);
	}
}