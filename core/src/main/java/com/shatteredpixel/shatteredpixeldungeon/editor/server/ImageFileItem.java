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

package com.shatteredpixel.shatteredpixeldungeon.editor.server;

import com.badlogic.gdx.files.FileHandle;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.ui.TabResourceFiles;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.watabou.noosa.Image;
import com.watabou.utils.FileUtils;

public class ImageFileItem extends EditorItem<FileHandle> {
	
	public ImageFileItem(String dungeonName, String path) {
		this.obj = FileUtils.getFileHandle(FileUtils.getFileTypeForCustomDungeons(), CustomDungeonSaves.DUNGEON_FOLDER + dungeonName.replace(' ', '_') + "/" + path);
	}
	
	public ImageFileItem(FileHandle file) {
		this.obj = file;
	}
	
	@Override
	public DefaultEditComp<?> createEditComponent() {
		return null;
	}
	
	@Override
	public Image getSprite() {
		return TabResourceFiles.getSpriteForPath(obj.path(), obj);
	}
	
	@Override
	public Item getCopy() {
		return new ImageFileItem(obj);
	}
	
	@Override
	public void place(int cell) {
	}
	
	@Override
	public String name() {
		return obj.name();
	}
}