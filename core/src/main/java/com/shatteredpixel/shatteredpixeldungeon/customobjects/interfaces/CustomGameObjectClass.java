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
import com.watabou.utils.Function;

import java.io.IOException;

/**
 * UserContent that can be placed directly on the map
 */
public interface CustomGameObjectClass extends LuaCustomObjectClass {

	public static final String INHERIT_STATS = "inherit_stats";

	boolean getInheritStats();
	void setInheritStats(boolean inheritStats);

	static void updateInheritStats(CustomGameObjectClass self, Level level) {
		if (CustomObjectClass.isOriginal(self)) {

			final int ident = self.getIdentifier();

			Function<GameObject, GameObject.ModifyResult> whatToDo = obj -> {
				if (obj instanceof CustomGameObjectClass) {
					CustomGameObjectClass customClass = (CustomGameObjectClass) obj;
					if (customClass.getIdentifier() == ident) {
						ActionPartModify modify = doUpdateInheritStats(self, obj, customClass);
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
			if (self instanceof CustomItemClass) CustomItemClass.updateInheritStats((CustomItemClass) self, level);
		}
	}

	static ActionPartModify doUpdateInheritStats(CustomGameObjectClass self, GameObject obj, CustomGameObjectClass customClass) {
		if (self instanceof CustomMobClass) return CustomMobClass.doUpdateInheritStats(self, obj, customClass);
		if (self instanceof CustomItemClass) return CustomItemClass.doUpdateInheritStats(self, obj, customClass);
		if (self instanceof CustomBuffClass) return CustomBuffClass.doUpdateInheritStats(self, obj, customClass);
		if (self instanceof CustomPlantClass) return CustomPlantClass.doUpdateInheritStats(self, obj, customClass);
		if (self instanceof CustomTrapClass) return CustomTrapClass.doUpdateInheritStats(self, obj, customClass);
		if (self instanceof CustomRoomClass) return CustomRoomClass.doUpdateInheritStats(self, obj, customClass);
		throw new RuntimeException("Must implement method doUpdateInheritStats() for each subclass of CustomGameObjectClass!");
	}
}
