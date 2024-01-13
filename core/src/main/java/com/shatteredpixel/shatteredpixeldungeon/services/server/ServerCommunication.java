package com.shatteredpixel.shatteredpixeldungeon.services.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.files.FileHandle;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.ExportDungeonWrapper;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.FileUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public final class ServerCommunication {

    private static String URL = null;
    private static String userID;

    public static boolean loadURL(boolean force) {

        if (URL == null) {

            if (!force && SPDSettings.WiFi() && !Game.platform.connectedToUnmeteredNetwork()) {
                return false;
            }

            String scriptLoadURL = "https://script.google.com/macros/s/AKfycbzR6JDJgBdSn0U0m10R2VMvF2Ou9kGg8XnPna_XO-BbN5IE9H9jzwLZ5-9CTcplfq5pyQ/exec";
            Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.GET);
            httpRequest.setUrl(scriptLoadURL);

            Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
                @Override
                public void handleHttpResponse(Net.HttpResponse httpResponse) {
                    int statusCode = httpResponse.getStatus().getStatusCode();
                    if (statusCode == 200) {
                        String result = httpResponse.getResultAsString();
                        if (result.startsWith("http")) URL = result;
                    }
                }

                @Override
                public void failed(Throwable t) {
                }

                @Override
                public void cancelled() {
                }
            });
        }
        return true;
    }

    public static String getURL() {
        if (URL == null) {
            if (loadURL(true)) {
                new Thread(() -> {
                    try {
                        int tries = 50;
                        while (URL == null && tries > 0) {
                            Thread.sleep(100);
                            tries--;
                        }
                    } catch (InterruptedException ignored) {
                    }
                }).run();
            }
        }
        return URL == null ? "https://script.google.com/macros/s/AKfycbwdZ33YW_XXxiV8-rOzrmzxIr5-RJSvhxDjLE4PfpWHRfR-0fQkNUu504kyxjoUebAOqQ/exec" : URL;
    }

    private static String getUUID() {
        if (userID == null) {
            userID = SPDSettings.uuid();
            if (userID == null) {
                try {
                    userID = URLEncoder.encode(UUID.randomUUID().toString(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    userID = String.valueOf(System.currentTimeMillis());
                }
                SPDSettings.uuid(userID);
            }
        }
        return userID;
    }

    public static abstract class ConnectionCallback {

        protected Window waitWindow;

        public void showWindow(com.badlogic.gdx.Net.HttpRequest httpRequest) {
            waitWindow = new WndOptions(Messages.get(ServerCommunication.class, "wait_title"),
                    Messages.get(ServerCommunication.class, "wait_body"),
                    Messages.get(ServerCommunication.class, "wait_cancel")) {
                @Override
                public void onBackPressed() {
                }

                @Override
                protected void onSelect(int index) {
                    if (index == 0) {
                        Gdx.net.cancelHttpRequest(httpRequest);
                    }
                }
            };
            Game.scene().addToFront(waitWindow);
        }

        public void hideWindow() {
            if (waitWindow != null) waitWindow.hide();
        }

        public void failed(Throwable t) {
            hideWindow();
            if (t instanceof UnknownHostException) Game.scene().addToFront(new WndError(Messages.get(ServerCommunication.class, "no_internet")));
            else
                Game.scene().addToFront(new WndError(Messages.get(ServerCommunication.class, "error") + ":\n" + t.getClass().getSimpleName() + ": " + t.getMessage()));
        }
    }

    public static abstract class OnPreviewReceive extends ConnectionCallback {
        public final void accept(DungeonPreview[] previews) {
            Game.runOnRenderThread(() -> {
                hideWindow();
                onSuccessful(previews);
            });
        }

        protected abstract void onSuccessful(DungeonPreview[] previews);
    }

    public static abstract class OnDungeonReceive extends ConnectionCallback {
        public final void accept(CustomDungeonSaves.Info info) {
            Game.runOnRenderThread(() -> {
                hideWindow();
                onSuccessful(info);
            });
        }

        protected abstract void onSuccessful(CustomDungeonSaves.Info info);
    }

    public static abstract class UploadCallback extends ConnectionCallback {
        public final void successful(String dungeonFileID) {
            Game.runOnRenderThread(() -> {
                hideWindow();
                onSuccessful(dungeonFileID);
            });
        }

        protected abstract void onSuccessful(String dungeonFileID);
    }

    public static abstract class OwnershipCheckerCallback extends ConnectionCallback {
        public final void successful(Boolean value) {
            Game.runOnRenderThread(() -> {
                hideWindow();
                onSuccessful(value);
            });
        }

        protected abstract void onSuccessful(Boolean value);
    }


    public static void dungeonList(OnPreviewReceive callback) {

        Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.GET);
        httpRequest.setUrl(getURL() + "?action=getPreviewList");

        callback.showWindow(httpRequest);

        Gdx.net.sendHttpRequest(httpRequest, new com.badlogic.gdx.Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(com.badlogic.gdx.Net.HttpResponse httpResponse) {
                List<DungeonPreview> dungeons = new ArrayList<>();
                try {
                    for (Bundle b : Bundle.read(httpResponse.getResultAsStream()).getBundleArray()) {
                        if (!b.getString("content").startsWith("Error")) {
                            DungeonPreview preview;
                            try {
                                //unfortunately it seems like there is no better way for this...
                                Bundle bundle = Bundle.class.getConstructor(String.class).newInstance(b.getString("content"));
                                preview = new DungeonPreview();
                                preview.restoreFromBundle(bundle);
                                preview.dungeonFileID = b.getString("dungeonID");

                            } catch (Exception e) {
                                preview = new DungeonPreview();
                                preview.dungeonFileID = "ERROR: " + e.getMessage();
                            }
                            dungeons.add(preview);
                        }
                    }
                } catch (IOException e) {
                    Game.runOnRenderThread(() -> callback.failed(e.getMessage() == null ? new IOException(String.valueOf(httpResponse.getStatus().getStatusCode())) : e));
                    return;
                }
                Collections.sort(dungeons, (o1, o2) -> new Date(o1.uploadTime).compareTo(new Date(o2.uploadTime)));
                callback.accept(dungeons.toArray(new DungeonPreview[0]));
            }

            @Override
            public void failed(Throwable t) {
                Game.runOnRenderThread(() -> callback.failed(t));
            }

            @Override
            public void cancelled() {
            }
        });

    }

    public static void downloadDungeon(String fileId, OnDungeonReceive callback) {

        Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.GET);
        httpRequest.setUrl(getURL() + "?action=downloadDungeon&fileID=" + fileId + "&userID=" + getUUID());

        callback.showWindow(httpRequest);

        Gdx.net.sendHttpRequest(httpRequest, new com.badlogic.gdx.Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(com.badlogic.gdx.Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                if (statusCode == 200) {
                    try {
                        for (Bundle b : Bundle.read(httpResponse.getResultAsStream()).getBundleArray()) {
                            if (!b.getString("content").startsWith("Error")) {
                                try {
                                    //unfortunately it seems like there is no better way for this...
                                    Bundle bundle = Bundle.class.getConstructor(String.class).newInstance(b.getString("content"));
                                    ExportDungeonWrapper dungeon = (ExportDungeonWrapper) bundle.get(CustomDungeonSaves.EXPORT);
                                    if (dungeon == null) throw new Exception("Could not download the dungeon!");
                                    CustomDungeonSaves.Info info = dungeon.doImport();
                                    if (info == null) throw new Exception("Failed to import dungeon!");
                                    callback.accept(info);
                                    return;
                                } catch (Exception e) {
                                    Game.runOnRenderThread(() -> callback.failed(e));
                                }
                            } else Game.runOnRenderThread(() -> callback.failed(new Exception(b.getString("content"))));
                        }
                        Game.runOnRenderThread(() -> callback.failed(new Exception("Could not find the dungeon!")));
                    } catch (IOException e) {
                        Game.runOnRenderThread(() -> callback.failed(e.getMessage() == null ? new Exception(Messages.get(ServerCommunication.class, "download_error")) : e));
                    }
                } else Game.runOnRenderThread(() -> callback.failed(new IOException(String.valueOf(statusCode))));
            }

            @Override
            public void failed(Throwable t) {
                Game.runOnRenderThread(() -> callback.failed(t));
            }

            @Override
            public void cancelled() {
            }
        });

    }

    private static class UploadDataListener implements Net.HttpResponseListener {

        private final UploadCallback callback;

        private UploadDataListener(UploadCallback callback) {
            this.callback = callback;
        }

        @Override
        public void handleHttpResponse(com.badlogic.gdx.Net.HttpResponse httpResponse) {
            int statusCode = httpResponse.getStatus().getStatusCode();
            if (statusCode == 200) {
                String result = httpResponse.getResultAsString();
                if (result.startsWith("true")) Game.runOnRenderThread(() -> callback.successful(result.substring(4)));
                else if (result.startsWith("banned")) Game.runOnRenderThread(() -> callback.failed(new Banned()));
                else Game.runOnRenderThread(() -> callback.failed(new Exception(result)));
            } else {
                Game.runOnRenderThread(() -> callback.failed(new SocketException(String.valueOf(statusCode))));
            }
        }

        @Override
        public void failed(Throwable t) {
            Game.runOnRenderThread(() -> callback.failed(t));
        }

        @Override
        public void cancelled() {
        }
    }

    private static class Banned extends Exception {
        public Banned() {
            super(Messages.get(ServerCommunication.class, "banned", hashUserIDOne() + "&\n" + hashUserIDTwo()));
        }
    }

    public static void uploadDungeon(String dungeonName, String description, String userName, int difficulty, UploadCallback callback) {
        try {
            Bundle dungeonAsBundle = CustomDungeonSaves.getExportDungeonBundle(dungeonName);

            DungeonPreview uploadPreview = new DungeonPreview();
            uploadPreview.title = dungeonName;
            uploadPreview.description = description;
            uploadPreview.version = Game.version;
            uploadPreview.intVersion = Game.versionCode;
            uploadPreview.uploader = userName;
            uploadPreview.difficulty = difficulty;

            Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.POST);
            httpRequest.setUrl(getURL() + "?action=upload&fileName=" + URLEncoder.encode(dungeonName, "UTF-8") + "&userID=" + getUUID()
                    + uploadPreview.writeArgumentsForURL());
            httpRequest.setHeader("Content-Type", "application/x-www-form-urlencoded");
            httpRequest.setContent("dungeon=" + dungeonAsBundle);

            callback.showWindow(httpRequest);

            Gdx.net.sendHttpRequest(httpRequest, new UploadDataListener(callback));
        } catch (IOException e) {
            Game.runOnRenderThread(() -> callback.failed(e));
        } catch (CustomDungeonSaves.RenameRequiredException e) {
            Game.runOnRenderThread(() -> {
                callback.hideWindow();
                e.showExceptionWindow();
            });
        }
    }

    public static void updateDungeon(DungeonPreview oldDungeonPreview, String newDungeonName, String newDescription, int difficulty, UploadCallback callback) {
        try {
            Bundle dungeonAsBundle;
            if (newDungeonName == null) {
                dungeonAsBundle = null;
                newDungeonName = oldDungeonPreview.title;
            } else dungeonAsBundle = CustomDungeonSaves.getExportDungeonBundle(newDungeonName);

            DungeonPreview uploadPreview = new DungeonPreview();
            uploadPreview.title = newDungeonName;
            uploadPreview.description = newDescription;
            uploadPreview.version = Game.version;
            uploadPreview.intVersion = Game.versionCode;
            uploadPreview.uploader = oldDungeonPreview.uploader;
            uploadPreview.difficulty = difficulty;

            Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.POST);
            httpRequest.setUrl(getURL() + "?action=update&fileName=" + URLEncoder.encode(newDungeonName, "UTF-8") + "&dungeonID=" + oldDungeonPreview.dungeonFileID
                    + "&userID=" + getUUID() + "&oldSalt=" + oldDungeonPreview.title.hashCode() + "&newSalt=" + newDungeonName.hashCode()
                    + uploadPreview.writeArgumentsForURL()
            );
            httpRequest.setHeader("Content-Type", "application/x-www-form-urlencoded");
            httpRequest.setContent("dungeon=" + dungeonAsBundle);

            callback.showWindow(httpRequest);

            Gdx.net.sendHttpRequest(httpRequest, new UploadDataListener(callback));
        } catch (IOException e) {
            Game.runOnRenderThread(() -> callback.failed(e));
        } catch (CustomDungeonSaves.RenameRequiredException e) {
            Game.runOnRenderThread(() -> {
                callback.hideWindow();
                e.showExceptionWindow();
            });
        }
    }

    public static void reportBug(String dungeonName, String description, UploadCallback callback) {
        try {
            Bundle dungeonAsBundle = dungeonName == null ? null : CustomDungeonSaves.getExportDungeonBundle(dungeonName);
            String fileName = URLEncoder.encode(description.substring(0, Math.min(20, description.length())), "UTF-8");

            DungeonPreview uploadPreview = new DungeonPreview();
            uploadPreview.title = fileName;
            uploadPreview.description = description;
            uploadPreview.version = Game.version;
            uploadPreview.uploader = "null";

            Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.POST);
            httpRequest.setUrl(getURL() + "?action=bug_report&fileName=" + URLEncoder.encode(fileName, "UTF-8")
                    + uploadPreview.writeArgumentsForURL());
            httpRequest.setHeader("Content-Type", "application/x-www-form-urlencoded");
            httpRequest.setContent("dungeon=" + dungeonAsBundle);

            callback.showWindow(httpRequest);

            Gdx.net.sendHttpRequest(httpRequest, new UploadDataListener(callback));
        } catch (IOException e) {
            Game.runOnRenderThread(() -> callback.failed(e));
        } catch (CustomDungeonSaves.RenameRequiredException e) {
            Game.runOnRenderThread(() -> {
                callback.hideWindow();
                e.showExceptionWindow();
            });
        }
    }

    public static void deleteDungeon(String dungeonID, String dungeonName, UploadCallback callback) {
        Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.GET);
        httpRequest.setUrl(getURL() + "?action=deleteDungeon&dungeonID=" + dungeonID + "&userID=" + getUUID() + "&salt=" + dungeonName.hashCode());

        callback.showWindow(httpRequest);

        Gdx.net.sendHttpRequest(httpRequest, new UploadDataListener(callback));
    }

    public static void isCreator(String dungeonID, OwnershipCheckerCallback callback) {
//        //send encrypted password:
//
//        //client sends password request to server
//        //server generates key pair + id, sends public key and id to client
//        //client encrypts password
//        //client sends encrypted password + id
//        //server uses the id to find the corresponding key pair
//        //server decrypts pawword
//        //server deletes key pair
//        Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.GET);
//        httpRequest.setUrl(getURL() + "?action=getKeyToEncryptPassword");
//        Gdx.net.sendHttpRequest(httpRequest, new com.badlogic.gdx.Net.HttpResponseListener() {
//            @Override
//            public void handleHttpResponse(com.badlogic.gdx.Net.HttpResponse httpResponse) {
//                String result = httpResponse.getResultAsString();
//                if (httpResponse.getStatus().getStatusCode() == 200 && result.startsWith("true")) {
//
//                    int indexSeparator = result.indexOf(';');
//                    int id = Integer.parseInt(result.substring(4, indexSeparator));
//                    String key = result.substring(indexSeparator + 1, result.length());
//
//                    String encrypted = "";
//
//
//                } else {
//                    Game.runOnRenderThread(() -> callback.failed(new SocketException(String.valueOf(httpResponse.getStatus().getStatusCode()))));
//                }
//
//            }
//
//            @Override
//            public void failed(Throwable t) {
//                Game.runOnRenderThread(() -> callback.failed(t));
//            }
//
//            @Override
//            public void cancelled() {
//                callback.successful(null);
//            }
//        });


        Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.GET);
        httpRequest.setUrl(getURL() + "?action=isCreator&dungeonID=" + dungeonID + "&userID=" + getUUID());

        callback.showWindow(httpRequest);

        Gdx.net.sendHttpRequest(httpRequest, new com.badlogic.gdx.Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(com.badlogic.gdx.Net.HttpResponse httpResponse) {
                String result = httpResponse.getResultAsString();
                if (httpResponse.getStatus().getStatusCode() == 200) {
                    if (result.startsWith("banned")) Game.runOnRenderThread(() -> callback.failed(new Banned()));
                    else Game.runOnRenderThread(() -> callback.successful(Boolean.parseBoolean(result)));
                } else {
                    Game.runOnRenderThread(() -> callback.failed(new SocketException(String.valueOf(httpResponse.getStatus().getStatusCode()))));
                }

            }

            @Override
            public void failed(Throwable t) {
                Game.runOnRenderThread(() -> callback.failed(t));
            }

            @Override
            public void cancelled() {
                callback.successful(null);
            }
        });
    }

//    private static byte[] compressString(String input) {
//        try {
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
//                gzipOutputStream.write(input.getBytes("UTF-8"));
//            }
//            return byteArrayOutputStream.toByteArray();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    private static String decompressString(byte[] compressedData) {
//        try {
//            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressedData);
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            try (GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream)) {
//                byte[] buffer = new byte[4096];
//                int length;
//                while ((length = gzipInputStream.read(buffer)) != -1) {
//                    byteArrayOutputStream.write(buffer, 0, length);
//                }
//            }
//            return byteArrayOutputStream.toString("UTF-8");
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
    private static String convertToHexString(byte[] data) {
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            String hex = Integer.toHexString(0xFF & data[i]);
            if (hex.length() == 1) hexString.append('0');
            if (i % 12 == 11) hexString.append(' ');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private static byte[] convertFromHexString(String hexString) {
        int len = hexString.length();
        byte[] byteArray = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            byteArray[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return byteArray;
    }

    private static String hashUserIDOne() {
        return hashUserID(1);
    }

    private static String hashUserIDTwo() {
        return hashUserID(1638);
    }

    private static String hashUserID(int salt) {
        try {
            String combined = userID + salt;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(combined.getBytes("UTF-8"));
//            Base64.getEncoder().encodeToString(hash);
            return convertToHexString(hash);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public enum UploadType {
        UPLOAD,
        REPORT_BUG,
        CHANGE,//need to include dungeonFileId and correct password
        DELETE;//need to include dungeonFileId and correct password

        public String id() {
            return name().toLowerCase(Locale.ENGLISH);
        }
    }


    //publish helper
    private static final String DIR = "publish_helper/";

    //place the files from https://drive.google.com/drive/folders/158uV22qE6eBmS2jcEtV50nywDKAKNyGm in /publish_helper/
    //call this method
    //for each file, a sub-directory will be created, containing the password, the upload type, the preview as plain text, and the dungeon file
    //the dungeon is automatically imported
    //the .dun file is ready to upload so upload it and copy the file id
    //paste the file id inside preview
    //move the preview one directory up and call this method again, it will be saved as zip with .dat extension, this file can be uploaded
    public static void scanFiles() {
        FileHandle srcDir = FileUtils.getFileHandleWithDefaultPath(FileUtils.getFileTypeForCustomDungeons(), DIR);
        if (!srcDir.isDirectory()) return;
        for (FileHandle file : srcDir.list()) {

            try {
                if (!file.isDirectory()) {
                    String content = file.readString("UTF-8");
                    Bundle bundle = Bundle.class.getConstructor(String.class).newInstance(content);
                    FileUtils.bundleToFile(file, bundle);

                    if (bundle.contains(CustomDungeonSaves.EXPORT)) {
                        ExportDungeonWrapper.doImport(file);
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

}