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

package com.shatteredpixel.shatteredpixeldungeon.usercontent;

import com.watabou.utils.Bundle;

public class ResourcePath {

	private String path;

	public String getPath() {
		return path;
	}

	public void setPath(/*@Nullable*/ String path) {
		this.path = removeSpacesInPath(path);
	}

	public static String removeSpacesInPath(String pathWithSpaces) {
		return pathWithSpaces == null || pathWithSpaces.isEmpty() ? null : pathWithSpaces.replace(' ', '_');
	}

	public void restoreFromBundle(Bundle bundle, String key) {
		setPath(bundle.getString(key));
	}

	public void storeInBundle(Bundle bundle, String key) {
		if (getPath() != null) bundle.put(key, getPath());
	}

//	default FileHandle getFileHandle() {
//		return FileUtils.getFileHandle(CustomDungeonSaves.getCurrentCustomObjectsPath(isGloballyAvailable()) + getPath());
//	}
//
//	default String filePathToCreateImage() {
//		//TODO tzz always first look if dungeon locally offers this path! and only then look at global paths
//		//so that one dungeon can override the files
//		return TextureCache.EXTERNAL_ASSET_PREFIX + CustomDungeonSaves.getCurrentCustomObjectsPath(isGloballyAvailable()) + getPath();
//	}

	public static boolean isImage(String extension) {
		return extension.equals("png");
	}

	public static boolean isSound(String extension) {
		return extension.equals("mp3") || extension.equals("ogg") || extension.equals("wav");
	}

	public static boolean isText(String extension) {
		return extension.equals("txt");
	}

	public static boolean isLua(String extension) {
		return extension.equals("lua");
	}

	public static boolean isCustomObject(String extension) {
		return extension.equals("dat");
	}

	public static String pathToExtension(String path) {
		//all extensions are exactly 3 characters long, + plus the dot
		return path == null ? null : path.length() < 3 ? path : path.substring(path.length()-3);
	}
}