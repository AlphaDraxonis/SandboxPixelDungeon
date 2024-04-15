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

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.references.Reference;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.noosa.Gizmo;
import com.watabou.noosa.ui.Component;

public abstract class ObjInspectorTabComp extends Component {

	protected final Object obj;

	protected RenderedTextBlock modifiersTxt;
	protected RenderedTextBlock typeTxt;
	protected RenderedTextBlock nameTxt;

	public ObjInspectorTabComp(Object obj) {
		this.obj = obj;
	}
	@Override
	protected void createChildren(Object... params) {
		super.createChildren(params);

		modifiersTxt = PixelScene.renderTextBlock(6);
		add(modifiersTxt);
		typeTxt = PixelScene.renderTextBlock(6);
		add(typeTxt);
		nameTxt = PixelScene.renderTextBlock(6);
		nameTxt.setHighlighting(false);
		add(nameTxt);
	}

	@Override
	protected void layout() {
		super.layout();

	}

	protected void openDifferentInspectWnd(Reference reference) {
		Gizmo inspectObj = parent;
		while (inspectObj != null && !(inspectObj instanceof ObjInspector)) {
			inspectObj = inspectObj.parent;
		}
		if (inspectObj != null) {
			((ObjInspector) inspectObj).showDifferentInspectObj(reference);
		}
	}

	protected abstract boolean matchesSearch(String searchTerm);
}