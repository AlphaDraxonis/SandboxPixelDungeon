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

package com.shatteredpixel.shatteredpixeldungeon.editor.lua.luaeditor;

import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObject;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObjectManager;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.interfaces.CustomObjectClass;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.PopupMenu;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.watabou.NotAllowedInLua;

@NotAllowedInLua
public abstract class NewInstanceButton extends RedButton {

	private final PopupMenu popupMenu;

	public NewInstanceButton(PopupMenu popupMenu) {
		super(Messages.get(NewInstanceButton.class, "label"));
		this.popupMenu = popupMenu;
	}

	@Override
	protected void onClick() {
		IDEWindow.chooseClassName((clName, obj) -> {
			popupMenu.hideImmediately();

			if (obj == null) return;

			onSelect(generateCodeForNewInstance(clName, obj));
		});
	}

	protected abstract void onSelect(String insertText);

	public static String generateCodeForNewInstance(String clName, Object obj) {

		if (obj == null || clName == null) return null;

		if (obj instanceof CustomObjectClass) {
			boolean useID = false;
			CustomObjectClass selected = (CustomObjectClass) obj;
			int id = selected.getIdentifier();
			CustomObject cus = CustomObjectManager.allUserContents.get(id);
			for (CustomObject cusObj : CustomObjectManager.allUserContents.values()) {
				if (cusObj != selected && cusObj.getName().equals(cus.getName())) {
					useID = true;
				}
			}
			return "newCus(" + (useID ? id : "\"" + cus.getName() + "\"") +")";
		}
		return  "new(\"" + clName + "\")";
	}
}