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

import com.shatteredpixel.shatteredpixeldungeon.actors.DefaultStatsCache;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObjectManager;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.LuaCustomObject;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.ResourcePath;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.interfaces.CustomMobClass;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.interfaces.LuaCharSprite;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.interfaces.LuaClassGenerator;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.interfaces.LuaCustomObjectClass;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.ui.editcomps.CustomCharSpriteEditor;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.ui.editcomps.CustomObjectEditor;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Mobs;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.Reflection;

public class CustomCharSprite extends LuaCustomObject {

	private Class<? extends Mob> targetClass;

	private final ResourcePath spritePath = new ResourcePath();

	@Override
	protected void loadScript(boolean loadOnlyIfRunIsActive) {
		super.loadScript(true);
	}

	private Class<? extends CharSprite> getTargetSpriteClass() {
		return DefaultStatsCache.getDefaultObject(targetClass).spriteClass;
	}

	private Class<? extends CharSprite> createCharSpriteClass() {
		return LuaClassGenerator.luaUserContentClass(getTargetSpriteClass());
	}

	@Override
	public Image getSprite(Runnable spriteReloader) {
		if (targetClass == null || getIdentifier() == 0) return new ItemSprite();
		return getCharSprite(spriteReloader);
	}

	public CharSprite getCharSprite(Runnable spriteReloader) {
		if (targetClass == null || getIdentifier() == 0) return new CharSprite();

		CharSprite charSprite = Reflection.newInstance(createCharSpriteClass());
		((LuaCharSprite) charSprite).setIdentifier(getIdentifier());

		//sets the custom texture and animations
		charSprite.texture(null);//is overridden to always use this one's image path
		charSprite.initAnimations();
		charSprite.play(null);
		charSprite.play(charSprite.idle);

		if (spriteReloader != null)
			spriteReloaders.put(charSprite, spriteReloader);

		return charSprite;
	}

	public CharSprite getActualCustomCharSpriteOrNull() {
		if (targetClass == null || getIdentifier() == 0) return null;
		return getCharSprite(null);
	}

	@Override
	public void reloadSprite() {
		super.reloadSprite();
		for (Mob mob : CustomObjectManager.getAllCustomObjects(Mob.class)) {
			int ident = ((CustomMobClass) mob).getIdentifier();
			CustomMob customMob = CustomObjectManager.getUserContent(ident, CustomMob.class);
			if (customMob.sprite == this) Mobs.updateCustomMob(customMob);
		}
	}

	@Override
	public LuaCustomObjectClass newInstance() {
		Class<?> clazz = LuaClassGenerator.luaUserContentClass(targetClass);
		LuaCustomObjectClass instance = (LuaCustomObjectClass) Reflection.newInstance(clazz);
		instance.setIdentifier(getIdentifier());
		return instance;
	}

	public void setResourcePath(String path) {
		spritePath.setPath(path);
	}

	public String getResourcePath() {
		return spritePath.getPath();
	}

	@Override
	public void setTargetClass(String superClass) {
		targetClass = Reflection.forName(superClass);
	}

	@Override
	public boolean isSuperclassValid(Class<?> superClass) {
		return Mob.class.isAssignableFrom(superClass);
	}

	@Override
	public Class<?> getLuaTargetClass() {
		return getTargetSpriteClass();
	}

	@Override
	public Class<? extends Bag> preferredBag() {
		return Mobs.bag().getClass();
	}
	
	@Override
	public String defaultSaveDir() {
		return "char_sprites/";
	}
	
	@Override
	public CustomObjectEditor<?> createCustomObjectEditor(Runnable onUpdateObj) {
		return new CustomCharSpriteEditor(onUpdateObj, this);
	}

	@Override
	public String desc() {
		if (targetClass == null) return super.desc();
		return "Sprite for " + targetClass.getSimpleName();
	}

	@Override
	public String[] allResourceFiles() {
		String[] old = super.allResourceFiles();
		String[] result = new String[old == null ? 1 : old.length+1];
		if (old != null) System.arraycopy(old, 0, result, 0, old.length);
		result[result.length-1] = spritePath.getPath();
		return result;
	}

	private static final String SPRITE_PATH = "sprite_path";
	private static final String TARGET_CLASS = "target_class";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		spritePath.storeInBundle(bundle, SPRITE_PATH);
		bundle.put(TARGET_CLASS, targetClass);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		spritePath.restoreFromBundle(bundle, SPRITE_PATH);
		targetClass = bundle.getClass(TARGET_CLASS);
	}

	public boolean isAvailableAsSprite(Class<? /*extends Char*/> ch) {
		return isAvailableAsSprite(ch, targetClass);
	}
	
	public static boolean isAvailableAsSprite(Class<? /*extends Char*/> ch, Class<?> spriteTargetClass) {
		return spriteTargetClass == null || ch.isAssignableFrom(spriteTargetClass);
	}
}
