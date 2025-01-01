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

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObjectManager;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.LuaManager;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.utils.Bundle;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

/**
 * UserContent that is capable of running own Lua code
 */
public interface LuaCustomObjectClass extends CustomObjectClass {

	public static Class<? extends LuaCustomObjectClass> luaInterfaceClass(Class<?> originalClass) {
		if (Mob.class.isAssignableFrom(originalClass)) return CustomMobClass.class;
		if (Item.class.isAssignableFrom(originalClass)) return CustomItemClass.class;
		if (Trap.class.isAssignableFrom(originalClass)) return CustomTrapClass.class;
		if (Plant.class.isAssignableFrom(originalClass)) return CustomPlantClass.class;
		if (Buff.class.isAssignableFrom(originalClass)) return CustomBuffClass.class;
		if (Room.class.isAssignableFrom(originalClass)) return CustomRoomClass.class;
		if (CharSprite.class.isAssignableFrom(originalClass)) return LuaCharSprite.class;
		if (Level.class.isAssignableFrom(originalClass)) return LuaLevel.class;
		return LuaCustomObjectClass.class;
	}

	public static final String VARS = "vars";

	@Override
	default boolean isOriginal() {
		return CustomObjectManager.getLuaClass(getIdentifier()) == this;
	}

	//These are only meant to be accessible by the class itself (=private)
	LuaTable getVars();
	void setVars(LuaTable vars);

	default void onStoreInBundle(Bundle bundle) {
		bundle.put(IDENTIFIER, getIdentifier());
		if (getVars() != null && !CustomDungeon.isEditing()) {
			LuaManager.storeVarInBundle(bundle, getVars(), LuaCustomObjectClass.VARS);
		}
	}

	default void onRestoreFromBundle(Bundle bundle) {
		setIdentifier(bundle.getInt(IDENTIFIER));

		LuaValue script;
		if (!CustomDungeon.isEditing() && (script = CustomObjectManager.getScript(getIdentifier())) != null && script.get("vars").istable()) {
			setVars( LuaManager.deepCopyLuaValue(script.get("vars")).checktable() );

			LuaValue loaded = LuaManager.restoreVarFromBundle(bundle, LuaCustomObjectClass.VARS);
			if (loaded != null && loaded.istable()) setVars( loaded.checktable() );
			if (script.get("static").istable()) getVars().set("static", script.get("static"));
		}
	}
}