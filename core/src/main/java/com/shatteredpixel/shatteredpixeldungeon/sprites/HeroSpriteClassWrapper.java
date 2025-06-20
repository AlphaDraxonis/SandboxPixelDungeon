/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObjectManager;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.blueprints.CustomCharSprite;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.CustomObjectItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.MobSpriteItem;
import com.watabou.NotAllowedInLua;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Reflection;

//used to represent a user-set hero sprite
@NotAllowedInLua
public class HeroSpriteClassWrapper implements Bundlable {
	
	private Class<? extends CharSprite> spriteClass; // for vanilla mob sprites
	private CustomCharSprite customSprite; // for a user-created sprite
	
	public HeroSpriteClassWrapper() {
	}
	
	public void setSpriteClass(Class<? extends CharSprite> spriteClass) {
		this.spriteClass = spriteClass;
		this.customSprite = null;
		this.sprite_id = 0;
	}
	
	public void setCustomSprite(CustomCharSprite customSprite) {
		this.customSprite = customSprite;
		this.sprite_id = customSprite == null ? 0 : customSprite.getIdentifier();
		this.spriteClass = null;
	}
	
	public CharSprite instantiateCharSprite() {
		if (spriteClass != null) {
			return Reflection.newInstance(spriteClass);
		}
		maybeRestoreFromID();
		if (customSprite != null) {
			return customSprite.getCharSprite(null);
		}
		return null;
	}
	
	public boolean definesSprite() {
		return spriteClass != null || sprite_id != 0;
	}
	
	public EditorItem<?> asEditorItem() {
		if (spriteClass != null) {
			return new MobSpriteItem(spriteClass);
		}
		maybeRestoreFromID();
		if (customSprite != null) {
			return new CustomObjectItem(customSprite);
		}
		return null;
	}
	
	public void clearData() {
		spriteClass = null;
		customSprite = null;
		sprite_id = 0;
	}
	
	public CustomCharSprite getCustomSprite() {
		maybeRestoreFromID();
		return customSprite;
	}
	
	private static final String SPRITE_CLASS = "sprite_class";
	private static final String SPRITE_ID = "sprite_id";
	private int sprite_id;
	
	@Override
	public void storeInBundle(Bundle bundle) {
		bundle.put(SPRITE_CLASS, spriteClass);
		bundle.put(SPRITE_ID, customSprite == null ? sprite_id : customSprite.getIdentifier());
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		spriteClass = bundle.getClass(SPRITE_CLASS);
		sprite_id = bundle.getInt(SPRITE_ID);
	}
	
	public void maybeRestoreFromID() {
		if (sprite_id != 0) {
//			customSprite = CustomObjectManager.getUserContent(sprite_id, CustomCharSprite.class);
			customSprite = CustomObjectManager.getUserContent(sprite_id, null);
		} else {
			customSprite = null;
		}
	}
}
