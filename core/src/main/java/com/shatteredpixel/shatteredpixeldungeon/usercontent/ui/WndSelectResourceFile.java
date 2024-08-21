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

package com.shatteredpixel.shatteredpixeldungeon.usercontent.ui;

import com.badlogic.gdx.files.FileHandle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.NotAllowedInLua;

import java.util.Map;

@NotAllowedInLua
public class WndSelectResourceFile extends Window {

	protected TabResourceFiles body;

	public WndSelectResourceFile() {
		resize(Math.min(WndTitledMessage.WIDTH_MAX, (int) (PixelScene.uiCamera.width * 0.9)), (int) (PixelScene.uiCamera.height * 0.8f));
		body = new TabResourceFiles(null) {

			@Override
			protected boolean includeExtension(String extension) {
				return acceptExtension(extension);
			}

			@Override
			protected void onClick(Map.Entry<String, FileHandle> path) {
				onSelect(path);
				hide();
			}

			@Override
			protected boolean onLongClick(Map.Entry<String, FileHandle> path) {
				super.onClick(path);
				return true;
			}
		};
		add(body);

		body.setRect(0, 0, width, height);
	}

	protected boolean acceptExtension(String extension) {
		return true;
	}

	protected void onSelect(Map.Entry<String, FileHandle> path) {
	}

}