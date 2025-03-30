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

package com.shatteredpixel.shatteredpixeldungeon.customobjects.ui.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.customobjects.blueprints.CustomRoom;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledButtonWithIconAndText;

public class CustomRoomEditor extends CustomObjectEditor<CustomRoom> {

	private StyledButtonWithIconAndText changeLayout;

	public CustomRoomEditor(Runnable onUpdateObj, CustomRoom obj) {
		super(onUpdateObj, obj);
	}

//	@Override
//	protected void createChildren(CustomRoom obj) {
//		super.createChildren(obj);
//
//		changeLayout = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, "Change layoutt") {
//			@Override
//			protected void onClick() {
//				openLayoutEditor();
//			}
//		};
//		add(changeLayout);
//
//		rectComps = new Component[] {
//				luaScriptPath, changeLayout
//		};
//	}

	@Override
	public void updateObj() {
		super.updateObj();
//		com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Rooms.updateCustomRoom(obj);
	}

//	private void openLayoutEditor() {
//
//		EditorScene.start();
//
//		if (obj.predefinedRoomLayout == null) {
//			RoomLayoutLevel layout = new RoomLayoutLevel();
//			new LevelScheme(layout);
//			layout.create();
//			EditorScene.open(layout);
//		}
//
//
//	}

}
