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

import com.shatteredpixel.shatteredpixeldungeon.editor.lua.CustomObject;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.LuaClass;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.PopupMenu;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;

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

		if (obj instanceof LuaClass) {
			boolean useID = false;
			CustomObject selected = CustomObject.customObjects.get(((LuaClass) obj).getIdentifier());
			for (CustomObject cusObj : CustomObject.customObjects.values()) {
				if (cusObj != selected && cusObj.name.equals(selected.name)) {
					useID = true;
				}
			}
			return "newCus(" + (useID ? ((LuaClass) obj).getIdentifier() : "\"" + selected.name + "\"") +")";
		}
		return  "new(\"" + clName + "\")";
	}
}