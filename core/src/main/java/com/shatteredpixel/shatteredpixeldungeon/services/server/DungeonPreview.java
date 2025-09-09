package com.shatteredpixel.shatteredpixeldungeon.services.server;

import com.badlogic.gdx.files.FileHandle;
import com.shatteredpixel.shatteredpixeldungeon.editor.server.TempFilesHandler;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.NotAllowedInLua;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Consumer;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@NotAllowedInLua
public class DungeonPreview implements Bundlable {

    //entered by uploader
    public String title;
    public String description;
    public String version;
    public int intVersion;
    public String uploader;
    public int difficulty;
    
    public String dungeonID;

    //entered by server
    public long uploadTime;
    
    public String mostRecentDungeon;//if the newest version of that dungeon is to be downloaded, this is the direct ID to it
    
    public String versionFileID;
    public String[] previewImageFileIDs;
    
    public boolean isDebug;//only shown if debug mode is enabled

    //TODO more information like rating, download counter, image
    
    
    //*************************************************
    //Additional information that must be requested from the server separately
    
    private DungeonVersionsHistoryEntry[] dungeonVersionsHistory;
    private FileHandle[] previewImageFiles;
    
    //*************************************************
    

    private static final String DUNGEON_ID = "dungeon_id";
    private static final String MOST_RECENT_DUNGEON = "most_recent_dungeon";
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String VERSION = "version";
    private static final String INT_VERSION = "int_version";
    private static final String UPLOADER = "uploader";
    private static final String DIFFICULTY = "difficulty";
    private static final String UPLOAD_TIME = "upload_time";
    private static final String VERSION_FILE_ID = "version_file_id";
    private static final String PREVIEW_IMAGE_FILE_IDS = "preview_image_file_ids";
    private static final String IS_DEBUG = "is_debug";

    @Override
    public void restoreFromBundle(Bundle bundle) {
        dungeonID = bundle.getString(DUNGEON_ID);
        mostRecentDungeon = bundle.getString(MOST_RECENT_DUNGEON);
        title = bundle.getString(TITLE);
        description = bundle.getString(DESCRIPTION);
        version = bundle.getString(VERSION);
        intVersion = bundle.getInt(INT_VERSION);
        uploader = bundle.getString(UPLOADER);
        difficulty = bundle.getInt(DIFFICULTY);
        uploadTime = bundle.getLong(UPLOAD_TIME);
        versionFileID = bundle.getString(VERSION_FILE_ID);
        if (bundle.contains(PREVIEW_IMAGE_FILE_IDS)) previewImageFileIDs = bundle.getStringArray(PREVIEW_IMAGE_FILE_IDS);
        isDebug = bundle.getBoolean(IS_DEBUG);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(DUNGEON_ID, dungeonID);
        bundle.put(MOST_RECENT_DUNGEON, mostRecentDungeon);
        bundle.put(TITLE, title);
        bundle.put(DESCRIPTION, description);
        bundle.put(VERSION, version);
        bundle.put(INT_VERSION, intVersion);
        bundle.put(UPLOADER, uploader);
        bundle.put(DIFFICULTY, difficulty);
        bundle.put(UPLOAD_TIME, uploadTime);
        bundle.put(VERSION_FILE_ID, versionFileID);
        if (previewImageFileIDs != null) bundle.put(PREVIEW_IMAGE_FILE_IDS, previewImageFileIDs);
        bundle.put(IS_DEBUG, isDebug);
    }

    //this does not include things that are only entered by the server!
    public String writeArgumentsForURL() {
        try {
            return    "&" + DUNGEON_ID + "=" + dungeonID
                    + "&" + TITLE + "=" + URLEncoder.encode(title, "UTF-8")
                    + "&" + DESCRIPTION + "=" + URLEncoder.encode(description == null ? "" : description, "UTF-8")
                    + "&" + VERSION + "=" + URLEncoder.encode(version, "UTF-8")
                    + "&" + INT_VERSION + "=" + intVersion
                    + "&" + UPLOADER + "=" + URLEncoder.encode(uploader, "UTF-8")
                    + "&" + DIFFICULTY + "=" + difficulty;
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    public static final int EASY = 0, MEDIUM = 1, HARD = 2, EXPERT = 3, INSANE = 4;
    public static String displayDifficulty(int difficulty) {
        switch (difficulty) {
            case EASY: return Messages.get(DungeonPreview.class, "easy");
            case MEDIUM: return Messages.get(DungeonPreview.class, "medium");
            case HARD: return Messages.get(DungeonPreview.class, "hard");
            case EXPERT: return Messages.get(DungeonPreview.class, "expert");
            case INSANE: return Messages.get(DungeonPreview.class, "insane");
        }
        return Messages.NO_TEXT_FOUND;
    }
    
    
    public void requestAdditionalFiles() {
        //download version history
        getVersionHistory(versionHistory -> {});
        
        //download preview images
        if (previewImageFileIDs != null) {
            synchronized (DungeonPreview.this) {
                if (previewImageFiles == null) {
                    previewImageFiles = new FileHandle[previewImageFileIDs.length];
                    for (int i = 0; i < previewImageFileIDs.length; i++) {
                        final int index = i;
                        TempFilesHandler.retrieveOrRequest(dungeonID, previewImageFileIDs[index], "png", file -> {
                            previewImageFiles[index] = file;
                        });
                    }
                }
            }
        }
    }
    
    public void getVersionHistory(Consumer<DungeonVersionsHistoryEntry[]> onAvailable) {
        if (dungeonVersionsHistory != null) {
            onAvailable.accept(dungeonVersionsHistory);
        } else {
            TempFilesHandler.requestDataFromServer(versionFileID, data -> {
                if (data == null) {
                    onAvailable.accept(null);
                    return;
                }
                Bundle[] bundles = data.getBundleArray();
                synchronized (DungeonPreview.this) {
                    if (bundles.length > 0) {
                        dungeonVersionsHistory = new DungeonVersionsHistoryEntry[bundles.length];
                        for (int i = 0; i < bundles.length; i++) {
                            DungeonVersionsHistoryEntry entry = new DungeonVersionsHistoryEntry();
                            entry.restoreFromBundle(bundles[i]);
                            dungeonVersionsHistory[i] = entry;
                        }
                    } else {
                        dungeonVersionsHistory = null;
                    }
                }
                
                onAvailable.accept(dungeonVersionsHistory);
            });
        }
    }
    
    public boolean isVersionHistoryAvailable() {
        return dungeonVersionsHistory != null;
    }
    
    public FileHandle retrievePreviewImage(int index) {
        return previewImageFiles == null ? null : previewImageFiles[index];
    }
    
    public void getPreviewImage(int index, Consumer<FileHandle> onAvailable) {
        FileHandle previewImage = retrievePreviewImage(index);
        if (previewImage != null) {
            onAvailable.accept(previewImage);
            return;
        }
        if (previewImageFileIDs == null) {
            onAvailable.accept(null);
            return;
        }
        if (previewImageFiles == null) {
            previewImageFiles = new FileHandle[previewImageFileIDs.length];
        }
        TempFilesHandler.retrieveOrRequest(dungeonID, previewImageFileIDs[index], "png", file -> {
            previewImageFiles[index] = file;
            onAvailable.accept(file);
        });
    }
    
    //keep in mind that this class cannot be loaded by Bundle.class normally because it’s private!
    public static final class DungeonVersionsHistoryEntry implements Bundlable {
        
        private DungeonVersionsHistoryEntry() {
        }
        
        private String name;
        private long uploadTime;
        private String versionID;
        
        private static final String NAME = "version_name";
        private static final String UPLOAD_TIME = "upload_time";
        private static final String VERSION_ID = "version_id";
        
        @Override
        public void restoreFromBundle(Bundle bundle) {
            name = bundle.getString(NAME);
            uploadTime = bundle.getLong(UPLOAD_TIME);
            versionID = bundle.getString(VERSION_ID);
        }
        
        @Override
        public void storeInBundle(Bundle bundle) {
            //this method shouldn’t be used client-side anyway…
            //bundle.put(NAME, name);
            //bundle.put(UPLOAD_TIME, uploadTime);
            //bundle.put(VERSION_ID, versionID);
            throw new RuntimeException("DungeonVersionsHistoryEntry cannot be stored in Bundle!");
        }
		
		public String getName() {
			return name;
		}
		
		public long getUploadTime() {
			return uploadTime;
		}
        
        public String getVersionID() {
            return versionID;
        }
    }
    
}
