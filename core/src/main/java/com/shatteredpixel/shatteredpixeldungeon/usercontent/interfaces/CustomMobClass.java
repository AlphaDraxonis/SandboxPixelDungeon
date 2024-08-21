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

package com.shatteredpixel.shatteredpixeldungeon.usercontent.interfaces;

import com.shatteredpixel.shatteredpixeldungeon.GameObject;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditMobComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPartModify;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.MobActionPart;
import com.shatteredpixel.shatteredpixeldungeon.usercontent.UserContentManager;
import com.shatteredpixel.shatteredpixeldungeon.usercontent.blueprints.CustomMob;

public interface CustomMobClass extends CustomGameObjectClass {

	default ActionPartModify doUpdateInheritStats(GameObject obj, CustomGameObjectClass customClass) {
		Mob m = (Mob) obj;
		Mob template = (Mob) this;
		ActionPartModify modify = new MobActionPart.Modify(m);
		if (customClass.getInheritStats()) {
			m.spriteClass = template.spriteClass;
			obj.copyStats(template);
		}
		if (m.sprite != null) {
			EditorScene.replaceMobSprite(m, m.spriteClass);
		}
		EditMobComp.updateMobTexture(m);

		return modify;
	}

	default boolean usesCustomSprite() {
		CustomMob customMob = UserContentManager.getUserContent(getIdentifier(), CustomMob.class);
		if (customMob.sprite != null) {
			return customMob.sprite.getActualCustomCharSpriteOrNull() != null;
		}
		return false;
	}

}