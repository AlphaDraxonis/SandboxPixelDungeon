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

package com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.inspector;

import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.SearchBar;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ui.Component;

import java.util.HashMap;
import java.util.Map;

public abstract class ObjInspectorTab extends Component {

	protected final SearchBar searchBar;
	protected ObjInspectorTabComp[] comps;

	private final Map<String, Float> scrollPosY;

	public ObjInspectorTab() {

		scrollPosY = new HashMap<>(7);

		searchBar = new SearchBar() {
			@Override
			protected void onTextChanged(String textBefore, String textAfter) {
				Camera camera = ObjInspectorTab.this.camera();
				if (camera == null) return;

				scrollPosY.put(textBefore, camera.scroll.y);
				updateSearch(textAfter);
				Float scrollTo = scrollPosY.get(textAfter);
				if (scrollTo == null) scrollTo = 0f;
				if (ObjInspectorTab.this.parent instanceof ObjInspector)
					((ObjInspector) ObjInspectorTab.this.parent).scrollTo(camera.scroll.x, scrollTo);
			}
		};
	}

	@Override
	protected void layout() {
		height = 0;
		height = EditorUtilies.layoutCompsLinear(2, 16, this, comps);
	}

	private void updateSearch(String searchTerm) {
		for (ObjInspectorTabComp c : comps) {
			c.visible = c.active = c.matchesSearch(searchTerm);
		}
		if (parent instanceof ObjInspector) ((ObjInspector) parent).updateParentLayout();
		else layout();
	}

	protected SearchBar getSearchBar() {
		return searchBar;
	}

	protected void updateValues() {
	}
}