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

import com.shatteredpixel.shatteredpixeldungeon.customobjects.LuaCustomObject;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.interfaces.LuaClassGenerator;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.interfaces.LuaCustomObjectClass;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Buffs;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Items;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Mobs;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Plants;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Traps;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.LuaRestrictionProxy;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.Reflection;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractAnyLuaCustomObj extends LuaCustomObject {

	protected Class<?> target;

	@Override
	public void setTargetClass(String fullClassName) {
		if (!fullClassName.startsWith(Messages.MAIN_PACKAGE_NAME) && !fullClassName.startsWith(Messages.WATABOU_PACKAGE_NAME)) {
			fullClassName = Messages.MAIN_PACKAGE_NAME + fullClassName;
		}
		Class<?> c = Reflection.forName(fullClassName);
		if (c == null || LuaRestrictionProxy.isRestricted(c)) this.target = null;
		else this.target = c;
	}

	@Override
	public LuaCustomObjectClass newInstance() {
		Class<?> clazz = LuaClassGenerator.luaUserContentClass(target);
		LuaCustomObjectClass instance = (LuaCustomObjectClass) Reflection.newInstance(clazz);
		instance.setIdentifier(getIdentifier());
		return instance;
	}

	@Override
	public Class<?> getLuaTargetClass() {
		return target;
	}

	@Override
	public Class<? extends Bag> preferredBag() {
		return null;
	}

	@Override
	public List<Bag> getBags() {
		return Arrays.asList(Mobs.bag(), Items.bag(), Traps.bag(), Plants.bag(), Buffs.bag());
	}

	private static final String TARGET_CLASS = "target_class";

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		target = bundle.getClass(TARGET_CLASS);
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(TARGET_CLASS, target);
	}

	@Override
	public Image getSprite(Runnable spriteReloader) {
		return new ItemSprite(ItemSpriteSheet.SOMETHING);
	}
}