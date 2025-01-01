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

import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.EditorInventory;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndEditorSettings;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.AbstractCategoryScroller;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.CategoryScroller;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObjectManager;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTabbed;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.NotAllowedInLua;

@NotAllowedInLua
public class WndAllCustomObjects extends WndTabbed {

	public WndAllCustomObjects() {
		EditorInventory.callStaticInitializers();

		CustomObjectManager.loadScripts(false);

		int w = Math.min(WndTitledMessage.WIDTH_MAX, (int) (PixelScene.uiCamera.width * 0.9));
		int h = (int) (PixelScene.uiCamera.height * 0.9);
		offset(0, EditorUtilities.getMaxWindowOffsetYForVisibleToolbar());
		resize(w, h + yOffset - 5 - tabHeight());

		TabCustomObjs[] tbs = {
				new com.shatteredpixel.shatteredpixeldungeon.customobjects.ui.TabCustomObjs(null, null),
				new TabResourceFiles(null)
		};

		for (int i = 0; i < tbs.length; i++) {
			add(tbs[i]);
			tbs[i].setRect(0, 0, width, height);
			int index = i;
			add(new WndTabbed.IconTab(tbs[i].createIcon()) {
				protected void select(boolean value) {
					super.select(value);
					tbs[index].active = tbs[index].visible = value;
				}

				@Override
				protected String hoverText() {
					return tbs[index].hoverText();
				}
			});
		}

		layoutTabs();
		select(0);
	}


	@NotAllowedInLua
	public static abstract class TabCustomObjs extends WndEditorSettings.TabComp {

		protected AbstractCategoryScroller<?> categoryScroller;
		protected RedButton addBtn;

		public TabCustomObjs() {
			addBtn = new RedButton(addBtnLabel()) {
				@Override
				protected void onClick() {
					onAddBtnClick();
				}
			};
			add(addBtn);
		}


		@Override
		public void layout() {
			super.layout();
			if (addBtn.visible) {
				addBtn.setRect(x, y + height - 18, width, 18);
				categoryScroller.setRect(x, y, width, height - addBtn.height() - 2);
			} else {
				categoryScroller.setRect(x, y, width, height);
			}
		}

		protected abstract CategoryScroller.Category[] createCategories();

		protected abstract String addBtnLabel();
		protected abstract void onAddBtnClick();
	}

}