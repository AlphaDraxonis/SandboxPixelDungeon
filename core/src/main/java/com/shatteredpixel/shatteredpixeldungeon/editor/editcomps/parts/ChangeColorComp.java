/*
 *
 *  * Pixel Dungeon
 *  * Copyright (C) 2012-2015 Oleg Dolya
 *  *
 *  * Shattered Pixel Dungeon
 *  * Copyright (C) 2014-2024 Evan Debenham
 *  *
 *  * Sandbox Pixel Dungeon
 *  * Copyright (C) 2023-2024 AlphaDraxonis
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */

package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledButtonWithIconAndText;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.WndColorPicker;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.Consumer;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;

import java.util.ArrayList;
import java.util.List;

public class ChangeColorComp extends StyledButtonWithIconAndText {
	
	private int color;
	
	public ChangeColorComp(String label) {
		super(Chrome.Type.GREY_BUTTON_TR, label);
		icon(Icons.ZONE.get());
	}
	
	public void setColor(int color) {
		this.color = color;
		icon.hardlight(color);
		
		for (Consumer<Integer> listener : checkedListeners) {
			listener.accept(color);
		}
	}
	
	@Override
	protected void onClick() {
		DungeonScene.show(new WndColorPicker(color) {
			@Override
			public void setSelectedColor(int color) {
				super.setSelectedColor(color);
				setColor(color);
			}
		});
	}
	
	private final List<Consumer<Integer>> checkedListeners = new ArrayList<>();
	public void addChangeListener(Consumer<Integer> listener) {
		checkedListeners.add(listener);
	}
	
	public void removeChangeListener(Consumer<Integer> listener) {
		checkedListeners.remove(listener);
	}
}