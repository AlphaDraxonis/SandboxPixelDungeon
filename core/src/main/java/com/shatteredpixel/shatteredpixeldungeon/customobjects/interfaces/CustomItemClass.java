/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
 *
 * Sandbox Pixel Dungeon
 * Copyright (C) 2023-2025 AlphaDraxonis
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

import com.shatteredpixel.shatteredpixeldungeon.GameObject;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPartModify;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.ItemActionPart;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;

public interface CustomItemClass extends CustomGameObjectClass {

	static void updateInheritStats(CustomItemClass self, Level level) {
		if (Undo.alreadyHasContent()) {
			Undo.addActionPartToBeginning(new ActionPart() {
				@Override
				public void undo() {
					EditorScene.updateHeapImagesAndSubIcons();
				}
				@Override
				public void redo() {
				}
				@Override
				public boolean hasContent() {
					return true;
				}
			});
			Undo.addActionPart(new ActionPart() {
				@Override
				public void undo() {
				}
				@Override
				public void redo() {
					EditorScene.updateHeapImagesAndSubIcons();
				}
				@Override
				public boolean hasContent() {
					return true;
				}
			});
		}
	}


	static ActionPartModify doUpdateInheritStats(CustomGameObjectClass self, GameObject obj, CustomGameObjectClass customClass) {
		Item item = (Item) obj;
		Item template = (Item) self;
		ActionPartModify modify = new ItemActionPart.Modify(item);
		if (customClass.getInheritStats()) {
			obj.copyStats(template);
		}

		return modify;
	}

//	default boolean usesCustomSprite() {
//		CustomItem customItem = (CustomItem) UserContentManager.getUserContent(getIdentifier());
//		if (customItem.sprite != null) {
//			return customItem.sprite.getActualCustomCharSpriteOrNull() != null;
//		}
//		return false;
//	}

}
