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

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObject;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObjectManager;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.interfaces.CustomMobClass;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.interfaces.LuaClassGenerator;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.ui.editcomps.CustomMobEditor;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.ui.editcomps.CustomObjectEditor;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditMobComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.GameObjectCategory;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Mobs;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.Reflection;

import java.io.IOException;

public class CustomMob extends CustomGameObject<CustomMobClass> {

	//identifier
	//name
	//luaMob
	//luaScript;
	public CustomCharSprite sprite;//wenn selected, dann sofort spriteClass von luaMob anpassen!

	@Override
	public Image getSprite(Runnable spriteReloader) {
		return sprite != null ? sprite.getSprite(spriteReloader) : super.getSprite(spriteReloader);
	}

	@Override
	public Class<? extends Bag> preferredBag() {
		return Mobs.bag().getClass();
	}

	@Override
	public int[] allUsedUserContent() {
		return userContentIDs(sprite);
	}
	
	@Override
	public void onDelete(CustomObject deleted) {
		super.onDelete(deleted);
		if (deleted == sprite) {
			sprite = null;
			Mobs.updateCustomMob(this);
			try {
				CustomDungeonSaves.storeCustomObject(this);
			} catch (IOException e) {
				Game.reportException(e);
				//save failed
			}
		}
	}
	
	@Override
	public String defaultSaveDir() {
		return "mobs/";
	}

	@Override
	public DefaultEditComp<?> createEditComp() {
		return new EditMobComp((Mob) userContentClass);
	}

	@Override
	public CustomObjectEditor<?> createCustomObjectEditor(Runnable onUpdateObj) {
		return new CustomMobEditor(onUpdateObj, this);
	}

	@Override
	public GameObjectCategory<?> inventoryCategory() {
		return Mobs.instance();
	}

	@Override
	public boolean isSuperclassValid(Class<?> superClass) {
		return Mob.class.isAssignableFrom(superClass);
	}

	@Override
	public void setTargetClass(String superClass) {
		Class<?> clazz = LuaClassGenerator.luaUserContentClass(Reflection.forName(superClass));
		setUserContentClass(!CustomMobClass.class.isAssignableFrom(clazz) ? null : (CustomMobClass) Reflection.newInstance(clazz));
	}

	private static final String SPRITE_ID = "sprite_id";
	private int sprite_id;//only during bundling, may be outdated later

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(SPRITE_ID, sprite == null ? 0 : sprite.getIdentifier());
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		sprite_id = bundle.getInt(SPRITE_ID);
	}

	@Override
	public void restoreIDs() {
		super.restoreIDs();

		CustomCharSprite spriteObj = CustomObjectManager.getUserContent(sprite_id, CustomCharSprite.class);
		if (spriteObj != null) sprite = spriteObj;
	}
}
