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

package com.shatteredpixel.shatteredpixeldungeon.editor.util;

import com.badlogic.gdx.files.FileHandle;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObject;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObjectManager;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.ResourcePath;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import com.watabou.NotAllowedInLua;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.FileUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

@NotAllowedInLua
public class LoadCustomObjects {
	
	private final boolean onlyLoadResourceFiles;

	private int openResponses;

	private List<Throwable> errors = new ArrayList<>(2);
	
	private boolean allExecutorsStarted = false;
	
	private final CountDownLatch latch = new CountDownLatch(1);

	public LoadCustomObjects(Bundle bundle, CustomDungeon curDungeon, boolean onlyLoadResourceFiles) {
		
		this.onlyLoadResourceFiles = onlyLoadResourceFiles;
		
		CustomObjectManager.allResourcePaths.clear();
		CustomObjectManager.allUserContents.clear();
		
		if (bundle != null) {
			CustomObjectManager.restorePre_v_1_3(bundle, curDungeon);
		}
		
		FileHandle localCustomObjectDir = FileUtils.getFileHandle(CustomDungeonSaves.getCurrentCustomObjectsPath());
		loadFiles(localCustomObjectDir, Pattern.quote(localCustomObjectDir.path() + "/"));
		
		if (openResponses <= 0) {
			//finish immediately
			maybeDisplayErrors();
			
		} else {
			//wait until everything is loaded
			allExecutorsStarted = true;
			
			try {
				latch.await();
				maybeDisplayErrors();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
	
	private void loadFiles(FileHandle file, String quotedRootPath) {
		if (file.isDirectory()) {
			for (FileHandle f : file.list()) {
				loadFiles(f, quotedRootPath);
			}
		}
		else {
			try {
				if (ResourcePath.isCustomObject(file.extension())) {
					if (!onlyLoadResourceFiles) {
						loadCustomObject(file, quotedRootPath);
					}
				} else {
					loadResourceFile(file, quotedRootPath);
				}
			} catch (Exception e) {
				Game.reportException(e);
			}
		}
	}
	
	private void loadResourceFile(FileHandle file, String quotedRootPath) {
		String path = file.path().replaceFirst(quotedRootPath, "");
		CustomObjectManager.allResourcePaths.put(path, file);
	}
	
	private final ExecutorService executorService = Executors.newFixedThreadPool(10);
	
	private void loadCustomObject(FileHandle file, String quotedRootPath) {
		openResponses++;
		executorService.submit(() -> {
			try {
				Bundle bundle = FileUtils.bundleFromStream(file.read());
				CustomObject customObject = (CustomObject) bundle.get(CustomObject.BUNDLE_KEY);
				customObject.saveDirPath = file.path().replaceFirst(quotedRootPath, "");
				decreaseOpenResponses(customObject);
			} catch (Exception e) {
				errors.add(e);
				decreaseOpenResponses(null);
			}
		});
	}
	
	protected synchronized void decreaseOpenResponses(CustomObject customObject) {
		openResponses--;
		
		if (customObject != null) {
			CustomObjectManager.allUserContents.put(customObject.getIdentifier(), customObject);
		}
		
		if (allExecutorsStarted && openResponses <= 0) {
			latch.countDown();
		}
	}
	
	private void maybeDisplayErrors() {
		if (!errors.isEmpty()) {
			StringBuilder b = new StringBuilder();
			for (Throwable e : errors) {
				b.append(e.getMessage()).append('\n');
			}
			b.deleteCharAt(b.length()-1);
			
			DungeonScene.show(new WndError(b.toString()){{setHighlightingEnabled(false);}});
		}
		
		for (CustomObject obj : new HashSet<>(CustomObjectManager.allUserContents.values())) {
			obj.restoreIDs();
		}
	}
}
