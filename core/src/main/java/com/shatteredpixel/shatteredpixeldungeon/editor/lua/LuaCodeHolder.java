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

import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import com.watabou.NotAllowedInLua;
import com.watabou.idewindowactions.AbstractLuaCodeHolder;
import com.watabou.idewindowactions.LuaScript;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;

@NotAllowedInLua
public class LuaCodeHolder extends AbstractLuaCodeHolder implements Bundlable {


	LuaValue script;
	private LuaValue staticVarsTemp;

	public LuaCodeHolder() {}

	public LuaCodeHolder(LuaScript fromScript) {
		super(fromScript);
	}

	public void loadScript(LuaScript luaScript) {
		if (luaScript != null) {
			try {
				script = LuaManager.globals.load(luaScript.code).call();
				if (staticVarsTemp != null) {
					script.set("static", staticVarsTemp);
					staticVarsTemp = null;
				}
			} catch (LuaError e) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(e))); }
		}
		else script = null;
	}

	public void unloadScript() {
		script = null;
	}

	public final LuaValue getScript() {
		return script;
	}

	private static final String STATIC_VARS = "static_vars";

	@Override
	public void restoreFromBundle(Bundle bundle) {
		LuaValue loaded = LuaManager.restoreVarFromBundle(bundle, STATIC_VARS);
		if (loaded != null && loaded.istable()) {
			staticVarsTemp = loaded.checktable();
		}
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		if (script != null && script.get("static").istable() && !CustomDungeon.isEditing()) {
			LuaManager.storeVarInBundle(bundle, script.get("static"), STATIC_VARS);
		}
	}
}