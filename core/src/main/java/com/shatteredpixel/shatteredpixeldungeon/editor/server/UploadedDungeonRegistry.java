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

package com.shatteredpixel.shatteredpixeldungeon.editor.server;

import com.badlogic.gdx.files.FileHandle;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.services.server.DungeonPreview;
import com.watabou.NotAllowedInLua;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.FileUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@NotAllowedInLua
public final class UploadedDungeonRegistry implements Bundlable {
	
	private static final String SAVE_FILE_NAME = "uploaded_dungeon_registry";
	
	private UploadedDungeonRegistry() {
		load();
	}
	
	private static final UploadedDungeonRegistry INSTANCE = new UploadedDungeonRegistry();
	
	//jede coreID (d.h. ein Dungeon und alle seine Kopien) kann nur EIN EINZIGES MAL auf dem Server hochgeladen sein.
	//zu einer coreID ist also stets 0..1 ServerDungeonIDs zugeordnet
	
	private final Map<String, DungeonPreview> mapping = Collections.synchronizedMap(new HashMap<>());//coreID → DungeonPreview with ServerDungeonID, not mapped coreIDs have not yet been uploaded
	
	/**
	 * returns the ServerDungeonID that belongs to the coreID.
	 *
	 * @param coreID the coreID of the dungeon
	 * @return a ServerDungeonID, or null
	 */
	public static String getAssociatedServerID(String coreID) {
		DungeonPreview preview = getAssociatedPreview(coreID);
		return preview == null ? null : preview.dungeonID;
	}
	
	public static DungeonPreview getAssociatedPreview(String coreID) {
		return INSTANCE.mapping.get(coreID);
	}
	
	public static String getAssociatedCoreID(String serverDungeonID) {
		for (Map.Entry<String, DungeonPreview> entry : INSTANCE.mapping.entrySet()) {
			if (entry.getValue().dungeonID.equals(serverDungeonID)) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	//when a dungeon is deleted on the server, the local dungeon that belonged to it is now treated as if it had never been uploaded
	public static void dungeonDeletedFromServer(String serverDungeonID) {
		for (Map.Entry<String, DungeonPreview> entry : INSTANCE.mapping.entrySet()) {
			if (entry.getValue().dungeonID.equals(serverDungeonID)) {
				INSTANCE.mapping.remove(entry.getKey());
			}
		}
		INSTANCE.save();
	}
	
	public static void newDungeonUploaded(String dungeonName, String serverDungeonID, String title, String desc, String uploader, int difficulty) {
		try {
			DungeonPreview locallySavedPreview = new DungeonPreview();
			locallySavedPreview.dungeonID = serverDungeonID;
			locallySavedPreview.title = title;
			locallySavedPreview.description = desc;
			locallySavedPreview.uploader = uploader;
			locallySavedPreview.difficulty = difficulty;
			
			String coreID = CustomDungeonSaves.getDungeonInfo(dungeonName).coreID;
			if (!coreID.isEmpty()) {
				INSTANCE.mapping.put(coreID, locallySavedPreview);
				INSTANCE.save();
			}
			
		} catch (Exception e) {
			Game.reportException(e);
		}
	}
	
	public static boolean hasDungeonBeenUploaded(String coreID) {
		return getAssociatedServerID(coreID) != null;
	}
	
	public static boolean canDungeonBeNewlyUploaded(String coreID) {
		return getAssociatedServerID(coreID) == null;
	}
	
	public static boolean isServerIDAssociated(String serverDungeonID) {
		for (DungeonPreview preview : INSTANCE.mapping.values()) {
			if (preview.dungeonID.equals(serverDungeonID)) return true;
		}
		return false;
	}
	
	
	
	// IO
	
	@Override
	public void storeInBundle(Bundle bundle) {
		synchronized (mapping) {
			for (Map.Entry<String, DungeonPreview> entry : mapping.entrySet()) {
				bundle.put(entry.getKey(), entry.getValue());
			}
		}
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		mapping.clear();
		for (String key : bundle.getKeys()) {
			DungeonPreview value = (DungeonPreview) bundle.get(key);
			if (value != null) {
				mapping.put(key, value);
			}
		}
	}
	
	private static FileHandle getSaveFile() {
		return FileUtils.getFileHandleWithDefaultPath(FileUtils.getOriginalFileType(), SAVE_FILE_NAME);
	}
	
	private void save() {
		try {
			Bundle bundle = new Bundle();
			INSTANCE.storeInBundle(bundle);
			FileUtils.bundleToFile(getSaveFile(), bundle);
		} catch (Exception e) {
			Game.reportException(e);
		}
	}
	
	private void load() {
		try {
			Bundle bundle = FileUtils.bundleFromFile(getSaveFile());
			restoreFromBundle(bundle);
		} catch (Exception e) {
			Game.reportException(e);
		}
	}
	
}
