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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class ServerCommunication {

    private static String URL = null;

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
        return URL == null ? "https://script.google.com/macros/s/AKfycbwT0aQYl-MyyYTz2vtzqE8M_R8060PfA8vq5SmN7c6oF5g5UvYeL0SvJt3JZ78NfdftBw/exec" : URL;
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

    public static abstract class PasswordCheckerCallback extends ConnectionCallback {
        public final void successful(Boolean value) {
            Game.runOnRenderThread(() -> {
                hideWindow();
                onSuccessful(value);
            });
        }

        protected abstract void onSuccessful(Boolean value);
    }


    private static final String PREVIEW = "preview";

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
        httpRequest.setUrl(getURL() + "?action=downloadDungeon&fileID=" + fileId);

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
                                    CustomDungeonSaves.Info info = dungeon.doImport(true);
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

    public static void uploadDungeon(String dungeonName, String description, String userName, String password, UploadCallback callback) {
        try {
            Bundle dungeonAsBundle = CustomDungeonSaves.getExportDungeonBundle(dungeonName);

            DungeonPreview uploadPreview = new DungeonPreview();
            uploadPreview.title = dungeonName;
            uploadPreview.description = description;
            uploadPreview.version = Game.version;
            uploadPreview.uploader = userName;

            Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.POST);
            httpRequest.setUrl(getURL() + "?action=upload&fileName=" + URLEncoder.encode(dungeonName, "UTF-8") + "&password=" + password + "&salt=" + dungeonName.hashCode()
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

    public static void updateDungeon(DungeonPreview oldDungeonPreview, String newDungeonName, String newDescription, String password, UploadCallback callback) {
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
            uploadPreview.uploader = oldDungeonPreview.uploader;

            Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.POST);
            httpRequest.setUrl(getURL() + "?action=update&fileName=" + URLEncoder.encode(newDungeonName, "UTF-8") + "&dungeonID=" + oldDungeonPreview.dungeonFileID
                    + "&pw=" + URLEncoder.encode(password, "UTF-8") + "&oldSalt=" + oldDungeonPreview.title.hashCode() + "&newSalt=" + newDungeonName.hashCode()
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

    public static void deleteDungeon(String dungeonID, String password, String dungeonName, UploadCallback callback) {
        Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.GET);
        try {
            httpRequest.setUrl(getURL() + "?action=deleteDungeon&dungeonID=" + dungeonID + "&pw=" + URLEncoder.encode(password, "UTF-8") + "&salt=" + dungeonName.hashCode());
        } catch (UnsupportedEncodingException e) {
            Game.scene().addToFront(new WndError(e.getClass().getSimpleName() + ":\n" + e.getMessage()));
            return;
        }

        callback.showWindow(httpRequest);

        Gdx.net.sendHttpRequest(httpRequest, new UploadDataListener(callback));
    }

    public static void isPasswordCorrect(String dungeonID, String password, String dungeonName, PasswordCheckerCallback callback) {
//        //send encryoted password:
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
        try {
            httpRequest.setUrl(getURL() + "?action=isPasswordCorrect&dungeonID=" + dungeonID + "&pw=" + URLEncoder.encode(password, "UTF-8") + "&salt=" + dungeonName.hashCode());
        } catch (UnsupportedEncodingException e) {
            Game.scene().addToFront(new WndError(e.getClass().getSimpleName() + ":\n" + e.getMessage()));
            return;
        }

        callback.showWindow(httpRequest);

        Gdx.net.sendHttpRequest(httpRequest, new com.badlogic.gdx.Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(com.badlogic.gdx.Net.HttpResponse httpResponse) {
                String result = httpResponse.getResultAsString();
                if (httpResponse.getStatus().getStatusCode() == 200) {
                    Game.runOnRenderThread(() -> callback.successful(Boolean.parseBoolean(result)));
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
//    private static String convertToHexString(byte[] data) {
//        StringBuilder hexString = new StringBuilder();
//        for (byte b : data) {
//            String hex = Integer.toHexString(0xFF & b);
//            if (hex.length() == 1) hexString.append('0');
//            hexString.append(hex);
//        }
//        return hexString.toString();
//    }
//
//    private static byte[] convertFromHexString(String hexString) {
//        int len = hexString.length();
//        byte[] byteArray = new byte[len / 2];
//        for (int i = 0; i < len; i += 2) {
//            byteArray[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
//                    + Character.digit(hexString.charAt(i + 1), 16));
//        }
//        return byteArray;
//    }

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