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

package com.shatteredpixel.shatteredpixeldungeon.usercontent.blueprints;


import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditBuffComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Buffs;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.GameObjectCategory;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.usercontent.interfaces.CustomBuffClass;
import com.shatteredpixel.shatteredpixeldungeon.usercontent.interfaces.LuaClassGenerator;
import com.shatteredpixel.shatteredpixeldungeon.usercontent.ui.editcomps.CustomBuffEditor;
import com.shatteredpixel.shatteredpixeldungeon.usercontent.ui.editcomps.CustomObjectEditor;
import com.watabou.utils.Reflection;

public class CustomBuff extends CustomGameObject<CustomBuffClass> {

	@Override
	public Class<? extends Bag> preferredBag() {
		return Buffs.bag().getClass();
	}

	@Override
	public String defaultSaveDir() {
		return "buffs/";
	}

	@Override
	public DefaultEditComp<?> createEditComp() {
		return new EditBuffComp((Buff) userContentClass, null);
	}

	@Override
	public CustomObjectEditor<?> createCustomObjectEditor(Runnable onUpdateObj) {
		return new CustomBuffEditor(onUpdateObj, this);
	}

	@Override
	public GameObjectCategory<?> inventoryCategory() {
		return Buffs.instance();
	}

	@Override
	public boolean isSuperclassValid(Class<?> superClass) {
		return Buff.class.isAssignableFrom(superClass);
	}

	@Override
	public void setTargetClass(String superClass) {
		Class<?> clazz = LuaClassGenerator.luaUserContentClass(Reflection.forName(superClass));
		setUserContentClass(!CustomBuffClass.class.isAssignableFrom(clazz) ? null : (CustomBuffClass) Reflection.newInstance(clazz));
	}

}