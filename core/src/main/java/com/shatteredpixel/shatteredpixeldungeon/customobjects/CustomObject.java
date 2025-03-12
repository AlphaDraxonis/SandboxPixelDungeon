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

package com.shatteredpixel.shatteredpixeldungeon.customobjects;

import com.badlogic.gdx.files.FileHandle;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.interfaces.CustomObjectClass;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.ui.editcomps.CustomObjectEditor;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.ui.editcomps.EditCustomObjectComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.watabou.gltextures.TextureCache;
import com.watabou.idewindowactions.LuaScript;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.FileUtils;

import java.util.WeakHashMap;

public class CustomObject implements Bundlable {

	private int identifier;
	private String name;//for visual difference, but only identifier actually matters

	private boolean downloaded;

	public int getIdentifier() {
		return identifier;
	}

	public void setIdentifier(int identifier) {
		this.identifier = identifier;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public boolean isDownloaded() {
		return downloaded;
	}

	public void setDownloaded(boolean downloaded) {
		this.downloaded = downloaded;
	}

	//when spriteReloader.run(), getSprite() must be called again!
	public Image getSprite(Runnable spriteReloader) {
		return new Image();
	}

	protected final WeakHashMap<Image, Runnable> spriteReloaders = new WeakHashMap<>();
	private final WeakHashMap<Image, Runnable> tempSpriteReloaders = new WeakHashMap<>();
	
	public static void maybeAddSpriteReloaderToCustomImageLater(Runnable reloadSprite, Image customObjectImageInstance) {
		if (reloadSprite != null && customObjectImageInstance instanceof CustomObjectClass) {
			int id = ((CustomObjectClass) customObjectImageInstance).getIdentifier();
			CustomObject customObject = CustomObjectManager.getUserContent(id, CustomObject.class);
			if (customObject != null) {
				customObject.spriteReloaders.put(customObjectImageInstance, reloadSprite);
			}
		}
	}

	private boolean reloadingSprites;
	public void reloadSprite() {
		//should be called every time getSprite() would return a visually different sprite!
		if (!reloadingSprites) {
			reloadingSprites = true;
			synchronized (spriteReloaders) {
				tempSpriteReloaders.putAll(spriteReloaders);
				for (Image img : spriteReloaders.keySet()) {
					if (img.parent == null || img.parent.isDestroyed())
						tempSpriteReloaders.remove(img);
				}
				spriteReloaders.clear();
				for (Runnable r : tempSpriteReloaders.values())
					r.run();
				spriteReloaders.putAll(tempSpriteReloaders);
				tempSpriteReloaders.clear();
			}
			reloadingSprites = false;
		}
	}

	public DefaultEditComp<?> createEditComp() {
		return new EditCustomObjectComp<>(this);
	}

	public CustomObjectEditor<?> createCustomObjectEditor(Runnable onUpdateObj) {
		return new CustomObjectEditor<>(onUpdateObj, this);
	}

	public String desc() {
		return null;
	}


	public String saveDirPath = null;//not bundled! we find out when loading where the source file is located

	/**
	 * Not used when saveDirPath is not null!
	 * @return In which subfolder the file is stored
	 */
	public String defaultSaveDir() {
		return "";
	}


	protected void onLoad(boolean runActive) {
		//do nothing by default
	}

	/**
	 * @return An int array with all used user content
	 */
	public int[] allUsedUserContent() {
		return null; // null value means empty, can be ignored
	}

	public String[] allResourceFiles() {
		return null; // null value means empty, can be ignored
	}

	public static int[] userContentIDs(CustomObject... customObjects) {
		if (customObjects.length == 0) return null;
		int[] result = new int[customObjects.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = customObjects[i] == null ? -1 : customObjects[i].getIdentifier();
		}
		return result;
	}
	
	public void onDelete(CustomObject deleted) {
		//do nothing, because we don't reference this obj here
	}

	public static final String BUNDLE_KEY = "user_content";

	private static final String IDENTIFIER = "identifier";
	private static final String NAME = "name";
	private static final String DOWNLOADED = "downloaded";
	private static final String DEPENDENCIES = "dependencies";


	@Override
	public void restoreFromBundle(Bundle bundle) {
		identifier = bundle.getInt(IDENTIFIER);
		name = bundle.getString(NAME);

		downloaded = bundle.getBoolean(DOWNLOADED);
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		bundle.put(IDENTIFIER, identifier);
		bundle.put(NAME, name);

		bundle.put(DOWNLOADED, downloaded);
	}

	public void restoreIDs() {
	}

	public static FileHandle getResourceFile(String path, boolean mustExist) {
		FileHandle file = FileUtils.getFileHandle(CustomDungeonSaves.getCurrentCustomObjectsPath() + path);
		if (!mustExist || file.exists()) {
			return file;
		}
		else return file;
	}

	public static LuaScript loadScriptFromFile(String path) {
		FileHandle file = getResourceFile(path, true);
		return file.exists() ? LuaScript.readFromFileContent(file.readString(), path) : null;
	}

	public static String filePathToCreateImage(String path) {
		return TextureCache.EXTERNAL_ASSET_PREFIX + CustomDungeonSaves.getCurrentCustomObjectsPath() + path;
	}
}
