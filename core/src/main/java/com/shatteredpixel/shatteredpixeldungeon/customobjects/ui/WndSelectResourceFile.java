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

package com.shatteredpixel.shatteredpixeldungeon.customobjects.ui;

import com.badlogic.gdx.files.FileHandle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.NotAllowedInLua;

import java.util.Map;

@NotAllowedInLua
public class WndSelectResourceFile extends Window {

	protected RenderedTextBlock info;
	protected TabResourceFiles body;

	public WndSelectResourceFile(String prompt, String nullOptionLabel, boolean useMoreThanOneCategory) {
		resize(WindowSize.WIDTH_LARGE.get(), WindowSize.HEIGHT_SMALL.get());
		
		if (prompt != null) {
			info = PixelScene.renderTextBlock(prompt, 6);
			info.setHighlighting(false);
			add(info);
		}
		
		body = new TabResourceFiles(null, nullOptionLabel != null, useMoreThanOneCategory) {

			@Override
			protected boolean includeExtension(String extension) {
				return super.includeExtension(extension) && acceptExtension(extension);
			}
			
			@Override
			protected boolean includeFile(FileHandle file, String path) {
				return super.includeFile(file, path) && acceptFile(file, path);
			}
			
			@Override
			protected String createNullOptionLabel() {
				return nullOptionLabel;
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

		if (info == null) {
			body.setRect(0, 0, width, height);
		} else {
			info.maxWidth(width);
			info.setPos(0, 1);
			body.setRect(0, info.bottom() + 2, width, height - info.height() - 3);
		}
	}

	protected boolean acceptExtension(String extension) {
		return true;
	}
	
	protected boolean acceptFile(FileHandle file, String path) {
		return true;
	}

	protected void onSelect(Map.Entry<String, FileHandle> path) {
	}

}
