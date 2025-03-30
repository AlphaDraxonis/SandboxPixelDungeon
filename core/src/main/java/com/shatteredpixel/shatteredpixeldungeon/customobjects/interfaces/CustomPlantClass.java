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
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPartModify;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.PlantActionPart;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;

public interface CustomPlantClass extends CustomGameObjectClass {
	
	static ActionPartModify doUpdateInheritStats(CustomGameObjectClass self, GameObject obj, CustomGameObjectClass customClass) {
		Plant m = (Plant) obj;
		Plant template = (Plant) self;
		ActionPartModify modify = new PlantActionPart.Modify(m);
		if (customClass.getInheritStats()) {
			obj.copyStats(template);
		}
//		if (m.sprite != null) {
//			EditorScene.replacePlantSprite(m, m.spriteClass);
//		}
//		EditPlantComp.updatePlantTexture(m);

		return modify;
	}

//	default boolean usesCustomSprite() {
//		CustomPlant customPlant = (CustomPlant) UserContentManager.getUserContent(getIdentifier());
//		if (customPlant.sprite != null) {
//			return customPlant.sprite.getActualCustomCharSpriteOrNull() != null;
//		}
//		return false;
//	}

}
