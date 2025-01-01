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

import com.shatteredpixel.shatteredpixeldungeon.customobjects.interfaces.CustomPlantClass;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.interfaces.LuaClassGenerator;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.ui.editcomps.CustomObjectEditor;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.ui.editcomps.CustomPlantEditor;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditPlantComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.GameObjectCategory;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Plants;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.watabou.utils.Reflection;

public class CustomPlant extends CustomGameObject<CustomPlantClass> {

	@Override
	public Class<? extends Bag> preferredBag() {
		return Plants.bag().getClass();
	}

	@Override
	public String defaultSaveDir() {
		return "plants/";
	}

	@Override
	public DefaultEditComp<?> createEditComp() {
		return new EditPlantComp((Plant) userContentClass);
	}

	@Override
	public CustomObjectEditor<?> createCustomObjectEditor(Runnable onUpdateObj) {
		return new CustomPlantEditor(onUpdateObj, this);
	}

	@Override
	public GameObjectCategory<?> inventoryCategory() {
		return Plants.instance();
	}

	@Override
	public boolean isSuperclassValid(Class<?> superClass) {
		return Plant.class.isAssignableFrom(superClass);
	}

	@Override
	public void setTargetClass(String superClass) {
		Class<?> clazz = LuaClassGenerator.luaUserContentClass(Reflection.forName(superClass));
		setUserContentClass(!CustomPlantClass.class.isAssignableFrom(clazz) ? null : (CustomPlantClass) Reflection.newInstance(clazz));
	}

}