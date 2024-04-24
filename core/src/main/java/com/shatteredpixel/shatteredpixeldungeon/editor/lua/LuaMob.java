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

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditMobComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.MobActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.Function;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;

public interface LuaMob extends LuaClass {

	String INHERITS_STATS = "inherits_stats";

	boolean getInheritsStats();
	void setInheritsStats(boolean value);

	default void updateInheritStats(Level level) {
		if (isOriginal()) {

			final int ident = getIdentifier();
			final Mob template = (Mob) this;

			Function<Mob, Boolean> whatToDo = mob -> {
				if (mob instanceof LuaMob && ((LuaMob) mob).getIdentifier() == ident && ((LuaMob) mob).getInheritsStats()) {
					MobActionPart.Modify modify = new MobActionPart.Modify(mob);
					EditMobComp.setToMakeEqual(mob, template);
					EditMobComp.updateMobTexture(mob);
					modify.finish();
					Undo.addActionPart(modify);
				}
				return false;
			};

			for (Mob m : level.levelScheme.mobsToSpawn) whatToDo.apply(m);

			CustomDungeon.doOnAllMobs(level, whatToDo);
		}
	}
}