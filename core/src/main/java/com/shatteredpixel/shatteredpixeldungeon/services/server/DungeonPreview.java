package com.shatteredpixel.shatteredpixeldungeon.services.server;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class DungeonPreview implements Bundlable {

    //entered by uploader
    public String title;
    public String description;
    public String version;
    public String uploader;
    public int difficulty;

    //entered by server
    public long uploadTime;
    public String dungeonFileID;

    //TODO more information like rating, download counter, image

    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String VERSION = "version";
    private static final String UPLOADER = "uploader";
    private static final String DIFFICULTY = "difficulty";
    private static final String UPLOAD_TIME = "upload_time";

    @Override
    public void restoreFromBundle(Bundle bundle) {
        title = bundle.getString(TITLE);
        description = bundle.getString(DESCRIPTION);
        version = bundle.getString(VERSION);
        uploader = bundle.getString(UPLOADER);
        difficulty = bundle.getInt(DIFFICULTY);
        uploadTime = bundle.getLong(UPLOAD_TIME);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(TITLE, title);
        bundle.put(DESCRIPTION, description);
        bundle.put(VERSION, version);
        bundle.put(UPLOADER, uploader);
        bundle.put(DIFFICULTY, difficulty);
        bundle.put(UPLOAD_TIME, uploadTime);
    }

    public String writeArgumentsForURL() {
        try {
            return "&title="+ URLEncoder.encode(title, "UTF-8")
                    +"&description="+URLEncoder.encode(description==null ? "" : description, "UTF-8")
                    +"&version="+URLEncoder.encode(version, "UTF-8")
                    +"&uploader="+URLEncoder.encode(uploader, "UTF-8")
                    +"&difficulty="+difficulty;
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
}