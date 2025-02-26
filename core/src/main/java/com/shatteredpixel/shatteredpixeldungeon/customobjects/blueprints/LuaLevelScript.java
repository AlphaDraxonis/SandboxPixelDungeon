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

package com.shatteredpixel.shatteredpixeldungeon.customobjects.blueprints;

import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObjectManager;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;

import java.io.IOException;

public class LuaLevelScript extends AbstractAnyLuaCustomObj {

	@Override
	public boolean isSuperclassValid(Class<?> superClass) {
		return Level.class.isAssignableFrom(superClass);
	}

	@Override
	public String defaultSaveDir() {
		return "level_scripts/script_";
	}
	
	public void validate(LevelScheme levelScheme) {
		if (getLuaScriptPath() == null) {
			CustomDungeonSaves.deleteCustomObject(
					CustomObjectManager.getUserContent(levelScheme.luaScriptID, null)
			);
			levelScheme.luaScriptID = 0;
		} else {
			try {
				CustomDungeonSaves.storeCustomObject(this);
			} catch (IOException e) {
				DungeonScene.show(new WndError(e));
			}
		}
	}
}