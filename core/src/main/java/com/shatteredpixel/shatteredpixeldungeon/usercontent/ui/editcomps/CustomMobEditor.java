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

package com.shatteredpixel.shatteredpixeldungeon.usercontent.ui.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditCompWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Mobs;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.usercontent.CustomObject;
import com.shatteredpixel.shatteredpixeldungeon.usercontent.blueprints.CustomCharSprite;
import com.shatteredpixel.shatteredpixeldungeon.usercontent.blueprints.CustomMob;
import com.shatteredpixel.shatteredpixeldungeon.usercontent.ui.CustomObjSelector;
import com.shatteredpixel.shatteredpixeldungeon.usercontent.ui.WndSelectCustomObject;
import com.watabou.noosa.ui.Component;

import java.util.Collections;

public class CustomMobEditor extends CustomObjectEditor<CustomMob> {

	protected CustomObjSelector<CustomCharSprite> charSprite;

	public CustomMobEditor(Runnable onUpdateObj, CustomMob obj) {
		super(onUpdateObj, obj);
	}

	@Override
	protected void createChildren(CustomMob obj) {
		super.createChildren(obj);

		charSprite = new CustomObjSelector<>("Char sprite", new CustomObjSelector.Selector<CustomCharSprite>() {

			@Override
			public CustomCharSprite getCurrentValue() {
				return obj.sprite;
			}

			@Override
			public void onSelect(CustomCharSprite sprite) {
				obj.sprite = sprite;
				updateObj();
			}

			@Override
			public void onItemSlotClick() {
				if (getCurrentValue() != null) {
					DungeonScene.show(new EditCompWindow(getCurrentValue()));
				}
			}

			@Override
			public void onChangeClick() {
				DungeonScene.show(new WndSelectCustomObject(Collections.singleton(CustomCharSprite.class)) {
					@Override
					protected boolean isSelectable(CustomObject sprite) {
						return sprite instanceof CustomCharSprite && ((CustomCharSprite) sprite).isAvailableAsSprite(obj.getLuaTargetClass());
					}

					@Override
					protected void onSelect(CustomObject sprite) {
						charSprite.setValue((CustomCharSprite) sprite);
					}
				});
			}
		});
		charSprite.enableChanging(true);
		charSprite.enableDetaching(true);
		add(charSprite);

		rectComps = new Component[] {
				luaScriptPath, charSprite
		};
	}

	@Override
	public void updateObj() {
		super.updateObj();
		Mobs.updateCustomMob(obj);
	}

}