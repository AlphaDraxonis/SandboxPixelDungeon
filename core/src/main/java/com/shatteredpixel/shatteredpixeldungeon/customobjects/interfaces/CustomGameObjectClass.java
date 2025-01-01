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

package com.shatteredpixel.shatteredpixeldungeon.customobjects.interfaces;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GameObject;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPartModify;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.watabou.utils.Bundle;
import com.watabou.utils.Function;

import java.io.IOException;

/**
 * UserContent that can be placed directly on the map
 */
public interface CustomGameObjectClass extends LuaCustomObjectClass {

	public static final String INHERIT_STATS = "inherit_stats";

	boolean getInheritStats();
	void setInheritStats(boolean inheritStats);

	@Override
	default void onStoreInBundle(Bundle bundle) {
		bundle.put(INHERIT_STATS, getInheritStats());
		LuaCustomObjectClass.super.onStoreInBundle(bundle);
	}

	@Override
	default void onRestoreFromBundle(Bundle bundle) {
		setInheritStats(bundle.getBoolean(INHERIT_STATS));
		LuaCustomObjectClass.super.onRestoreFromBundle(bundle);
	}

	default void updateInheritStats(Level level) {
		if (isOriginal()) {

			final int ident = getIdentifier();

			Function<GameObject, GameObject.ModifyResult> whatToDo = obj -> {
				if (obj instanceof CustomGameObjectClass) {
					CustomGameObjectClass customClass = (CustomGameObjectClass) obj;
					if (customClass.getIdentifier() == ident) {
						ActionPartModify modify = doUpdateInheritStats(obj, customClass);
						modify.finish();
						Undo.addActionPart(modify);
					}
				}
				return GameObject.ModifyResult.noChange();
			};

			try {
				CustomDungeon.doOnEverythingInLevelScheme(level.levelScheme, true, whatToDo, false, null, null);

				for (LevelScheme ls : Dungeon.customDungeon.levelSchemes()) {
					CustomDungeon.doOnEverythingInLevelScheme(ls, false, whatToDo, false, null, null);
				}
			} catch (IOException e) {
				//level should already be loaded
			}
		}
	}

	ActionPartModify doUpdateInheritStats(GameObject obj, CustomGameObjectClass customClass);
}