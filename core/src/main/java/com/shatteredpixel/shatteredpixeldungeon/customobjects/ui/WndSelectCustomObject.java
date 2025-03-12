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

package com.shatteredpixel.shatteredpixeldungeon.customobjects.ui;

import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObject;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.EditorInventoryWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.CustomObjectItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.NotAllowedInLua;

import java.util.List;
import java.util.Set;

@NotAllowedInLua
public abstract class WndSelectCustomObject extends Window {

	protected TabCustomObjs body;

	private WndBag.ItemSelectorInterface selector;

	public WndSelectCustomObject(Set<Class<? extends CustomObject>> visibleCategories) {
		resize(WindowSize.WIDTH_LARGE.get(), WindowSize.HEIGHT_SMALL.get());

		selector = new WndBag.ItemSelectorInterface() {
			@Override
			public String textPrompt() {
				return "";
			}

			@Override
			public Class<? extends Bag> preferredBag() {
				return null;
			}

			@Override
			public List<Bag> getBags() {
				return null;
			}

			@Override
			public boolean itemSelectable(Item item) {
				return true;//we have already filtered out any unwanted items before
			}

			@Override
			public void onSelect(Item item) {
				WndSelectCustomObject.this.onSelect(((CustomObjectItem) item).getObject());
			}

			@Override
			public boolean acceptsNull() {
				return false;
			}
		};

		body = new TabCustomObjs(new EditorInventoryWindow() {
			@Override
			public void hide() {
				WndSelectCustomObject.this.hide();
			}

			@Override
			public WndBag.ItemSelectorInterface selector() {
				return selector;
			}
		}, visibleCategories) {
			{
				addBtn.setVisible(false);
			}

			@Override
			protected boolean showObject(CustomObject obj) {
				return isSelectable(obj);
			}
			
			@Override
			protected boolean isClassSelectableAsTargetClassForNewObj(Class<?> cl) {
				return WndSelectCustomObject.this.isClassSelectableAsTargetClassForNewObj(cl);
			}
		};
		add(body);

		body.setRect(0, 0, width, height);
	}

	protected boolean isSelectable(CustomObject obj) {
		return true;
	}
	
	protected boolean isClassSelectableAsTargetClassForNewObj(Class<?> cl) {
		return true;
	}

	protected abstract void onSelect(CustomObject obj);

}
