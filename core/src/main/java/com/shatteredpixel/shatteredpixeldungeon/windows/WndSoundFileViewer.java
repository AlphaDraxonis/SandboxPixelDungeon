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

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.badlogic.gdx.files.FileHandle;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.FileUtils;

public class WndSoundFileViewer extends Window {

	public WndSoundFileViewer(FileHandle file) {
		super();
		IconTitle c = new IconTitle(Icons.AUDIO.get(), file.name());
		c.setSize(Float.MAX_VALUE, 0);
		c.setSize(c.reqWidth(), c.height());
		add(c);
		resize((int) Math.ceil(c.width()) + 4, (int) Math.ceil(c.height()));

		Music.INSTANCE.pause();
		String trackCutted = file.path().substring(FileUtils.getFileHandle(CustomDungeonSaves.getExternalFilePath("")).path().length());
		Music.SOUND_FILE_PREVIEW_PLAYER.play(trackCutted, true);
	}

	@Override
	public void hide() {
		Music.SOUND_FILE_PREVIEW_PLAYER.stop();
		Music.INSTANCE.resume();
		super.hide();
	}
}