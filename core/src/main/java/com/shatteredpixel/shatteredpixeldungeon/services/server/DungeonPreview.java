package com.shatteredpixel.shatteredpixeldungeon.services.server;

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

    //entered by server
    public long uploadTime;
    public String dungeonFileID;

    //TODO more information like rating, download counter, image

    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String VERSION = "version";
    private static final String UPLOADER = "uploader";
    private static final String UPLOAD_TIME = "upload_time";

    @Override
    public void restoreFromBundle(Bundle bundle) {
        title = bundle.getString(TITLE);
        description = bundle.getString(DESCRIPTION);
        version = bundle.getString(VERSION);
        uploader = bundle.getString(UPLOADER);
        uploadTime = bundle.getLong(UPLOAD_TIME);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(TITLE, title);
        bundle.put(DESCRIPTION, description);
        bundle.put(VERSION, version);
        bundle.put(UPLOADER, uploader);
        bundle.put(UPLOAD_TIME, uploadTime);
    }

    public String writeArgumentsForURL() {
        try {
            return "&title="+ URLEncoder.encode(title, "UTF-8")
                    +"&description="+URLEncoder.encode(description==null ? "" : description, "UTF-8")
                    +"&version="+URLEncoder.encode(version, "UTF-8")
                    +"&uploader="+URLEncoder.encode(uploader, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }
}