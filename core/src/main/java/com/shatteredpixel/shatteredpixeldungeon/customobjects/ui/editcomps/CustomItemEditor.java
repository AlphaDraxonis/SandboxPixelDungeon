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

package com.shatteredpixel.shatteredpixeldungeon.customobjects.ui.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.customobjects.blueprints.CustomItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Items;

public class CustomItemEditor extends CustomObjectEditor<CustomItem> {

	public CustomItemEditor(Runnable onUpdateObj, CustomItem obj) {
		super(onUpdateObj, obj);
	}

	@Override
	public void updateObj() {
		super.updateObj();
		Items.updateCustomItem(obj);
	}

}