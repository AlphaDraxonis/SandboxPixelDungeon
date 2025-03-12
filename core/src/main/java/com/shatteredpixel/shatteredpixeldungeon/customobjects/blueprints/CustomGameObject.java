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

import com.shatteredpixel.shatteredpixeldungeon.GameObject;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.LuaCustomObject;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.interfaces.CustomGameObjectClass;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.interfaces.LuaClassGenerator;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.interfaces.LuaCustomObjectClass;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.GameObjectCategory;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

//basically CustomObject
public abstract class CustomGameObject<T extends CustomGameObjectClass> extends LuaCustomObject {

	//keep a reference to one UserContentClass to save additional information if required
	protected T userContentClass;

	public T getUserContentClass() {
		return userContentClass;
	}

	public void setUserContentClass(T userContentClass) {
		this.userContentClass = userContentClass;
		userContentClass.setIdentifier(getIdentifier());
		userContentClass.setInheritStats(true);
	}

	@Override
	public Class<?> getLuaTargetClass() {
		return getUserContentClass().getClass().getSuperclass();
	}

	public void setIdentifier(int identifier) {
		super.setIdentifier(identifier);

		if (userContentClass != null) {
			userContentClass.setIdentifier(identifier);
		}
	}

	public Image getSprite(Runnable spriteReloader) {
		Image result = EditorUtilities.imageOf(userContentClass, false);
		if (spriteReloader != null) spriteReloaders.put(result, spriteReloader);
		return result;
	}

	@Override
	public String desc() {
		return ((GameObject) userContentClass).desc();
	}

	public abstract GameObjectCategory<?> inventoryCategory();

	@Override
	public LuaCustomObjectClass newInstance() {
		return (LuaCustomObjectClass) userContentClass.newInstance();
	}

	private static final String USER_CONTENT_CLASS = "user_content_class";


	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (!LuaClassGenerator.skipConversion) {
			userContentClass = (T) bundle.get(USER_CONTENT_CLASS);
			if (userContentClass != null) userContentClass.setInheritStats(true);
		}
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(USER_CONTENT_CLASS, userContentClass);
	}
}
