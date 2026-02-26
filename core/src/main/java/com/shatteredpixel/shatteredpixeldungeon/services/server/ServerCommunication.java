package com.shatteredpixel.shatteredpixeldungeon.services.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpStatus;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.server.ServerDungeonList;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.ExportDungeonWrapper;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.NotAllowedInLua;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.FileUtils;

import java.io.IOException;
import java.net.SocketException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static com.shatteredpixel.shatteredpixeldungeon.services.server.ServerConstants.*;

@NotAllowedInLua
public final class ServerCommunication {

    private static String URL = null;
    private static String userID;

    public static boolean loadURL(boolean force) {

        if (URL == null) {

            if (!force && SPDSettings.WiFi() && !Game.platform.connectedToUnmeteredNetwork()) {
                return false;
            }

            if (DeviceCompat.isDebug()) {
                return false;
            }

            String scriptLoadURL = "https://script.google.com/macros/s/AKfycbxpT2WJZvOtWIex-mprHvL5I4l9RyF1JbXzPh3VrIZlD_E-w1QAej0hoHVfa6QrEg_r/exec";
            Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.GET);
            httpRequest.setUrl(scriptLoadURL);

            Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
                @Override
                public void handleHttpResponse(Net.HttpResponse httpResponse) {
                    int statusCode = httpResponse.getStatus().getStatusCode();
                    if (statusCode == HttpStatus.SC_OK) {
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
        if (URL == null && !DeviceCompat.isDebug()) {
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
        return URL == null ? "https://script.google.com/macros/s/AKfycbzRY6eKCjzgkm3EbdtVZMOR0h_k8AAvc1w9t_Tkv_puTcBgFILmLm3h8KBxNhdEUDBi/exec" : URL;
    }

    static String getUUID() {
        if (userID == null) {
            userID = SPDSettings.uuid();
            if (userID == null) {
				userID = URLEncoder.encode(UUID.randomUUID().toString(), StandardCharsets.UTF_8);
				SPDSettings.uuid(userID);
            }
        }
        return userID;
    }

    public static abstract class ConnectionCallback {

        protected Window waitWindow;

        public void showWindow(com.badlogic.gdx.Net.HttpRequest httpRequest) {
            showWindow(httpRequest, null);
        }

        public void showWindow(com.badlogic.gdx.Net.HttpRequest httpRequest, Runnable onCancel) {
            waitWindow = new WndOptions(Messages.get(ServerCommunication.class, "wait_title"),
                    Messages.get(ServerCommunication.class, "wait_body"),
                    Messages.get(ServerCommunication.class, "wait_cancel")) {
                {
                    tfMessage.setHighlighting(false);
                }
                @Override
                public void onBackPressed() {
                }

                @Override
                protected void onSelect(int index) {
                    if (index == 0) {
                        if (onCancel != null) onCancel.run();
                        else Gdx.net.cancelHttpRequest(httpRequest);
                    }
                }
                
                @Override
                public void destroy() {
                    onSelect(0);//select cancel
                    super.destroy();
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
                if (DeviceCompat.isDebug()) {
                    Game.scene().addToFront(new WndError(Messages.get(ServerCommunication.class, "error") + ":\n" + t.getClass().getSimpleName() + ": " + t.getMessage()) {
                        {
                            setHighlightingEnabled(false);
                        }
                    });
                }
        }
        
        public void setMessage(String msg) {
            if (waitWindow instanceof WndOptions) {
                ((WndOptions) waitWindow).setMessage(msg);
            }
        }

        public void hideCancel() {
            Game.runOnRenderThread(() -> {
                if (waitWindow != null) waitWindow.hide();
                waitWindow = new WndOptions(Messages.get(ServerCommunication.class, "wait_title"),
                        !(waitWindow instanceof WndOptions)  ? Messages.get(ServerCommunication.class, "wait_body") : ((WndOptions) waitWindow).getMessage()) {
                    {
                        tfMessage.setHighlighting(false);
                    }
                    @Override
                    public void onBackPressed() {
                    }
                };
                Game.scene().addToFront(waitWindow);
            });
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


    public static void dungeonList(OnPreviewReceive callback, int page) {

        Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.GET);
        httpRequest.setUrl(getURL()
                + "?action=" + ACTION_GET_PREVIEW_LIST
                + "&page=" + page
                + "&perPage=" + ServerDungeonList.PREVIEWS_PER_PAGE);

//        callback.showWindow(httpRequest);

        Gdx.net.sendHttpRequest(httpRequest, new com.badlogic.gdx.Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(com.badlogic.gdx.Net.HttpResponse httpResponse) {
                List<DungeonPreview> dungeons = new ArrayList<>();
                try {
                    Bundle[] bundles = Bundle.read(httpResponse.getResultAsStream()).getBundleArray();
                    ServerDungeonList.setNumPreviews(bundles[0].getInt("numPreviews"));
                    for (int i = 1; i < bundles.length; i++) {
                        Bundle b = bundles[i];
                        if (!b.getString("content").startsWith("Error")) {
                            DungeonPreview preview;
                            try {
                                //unfortunately it seems like there is no better way for this...
                                Bundle bundle = Bundle.class.getConstructor(String.class).newInstance(b.getString("content"));
                                preview = new DungeonPreview();
                                preview.restoreFromBundle(bundle);
                                preview.dungeonID = b.getString("dungeonID");
                                
                                if (preview.isDebug && !DeviceCompat.isDebug()) {
                                    continue;
                                }

                            } catch (Exception e) {
                                preview = new DungeonPreview();
                                preview.dungeonID = "ERROR: " + e.getMessage();
                            }
                            dungeons.add(preview);
                        }
                    }
                } catch (Exception e) {
                    if (DeviceCompat.isDebug())
                        Game.runOnRenderThread(() -> callback.failed(e.getMessage() == null ? new IOException(String.valueOf(httpResponse.getStatus().getStatusCode())) : e));
                    return;
                }
                //sorting is already done on the backend
                //Collections.sort(dungeons, (o1, o2) -> new Date(o2.uploadTime).compareTo(new Date(o1.uploadTime)));
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

    public static void downloadDungeon(String dungeonName, String fileId, OnDungeonReceive callback) {
        new DownloadDungeonAction(CustomDungeon.maybeFixIncorrectNameEnding(dungeonName), fileId, callback);
    }

    static class UploadDataListener implements Net.HttpResponseListener {

        private final UploadCallback callback;

        UploadDataListener(UploadCallback callback) {
            this.callback = callback;
        }

        @Override
        public void handleHttpResponse(com.badlogic.gdx.Net.HttpResponse httpResponse) {
            int statusCode = httpResponse.getStatus().getStatusCode();
            String result = httpResponse.getResultAsString();
            if (statusCode != HttpStatus.SC_OK) {
                Game.runOnRenderThread(() -> callback.failed(new SocketException(statusCode + result)));
                return;
            }
            if (result.startsWith(KEYWORD_BANNED)) Game.runOnRenderThread(() -> callback.failed(new Banned()));
            else if (result.startsWith(KEYWORD_SUCCESS)) Game.runOnRenderThread(() -> callback.successful(result.substring(KEYWORD_SUCCESS.length())));
            else Game.runOnRenderThread(() -> callback.failed(new Exception(result)));
            
        }

        @Override
        public void failed(Throwable t) {
            Game.runOnRenderThread(() -> callback.failed(t));
        }

        @Override
        public void cancelled() {
        }
    }

    static class Banned extends Exception {
        public Banned() {
            super(Messages.get(ServerCommunication.class, "banned", hashUserIDOne() + "&\n" + hashUserIDTwo()));
        }
    }

    public static void uploadDungeon(String dungeonName, String title, String description, String userName, int difficulty, String versionName, UploadCallback callback) {
        new UploadDungeonAction(dungeonName, title, description, userName, difficulty, versionName, callback);
    }

    public static void updateDungeon(DungeonPreview oldDungeonPreview, String newDungeonName, String title, String newDescription, int difficulty, String versionName, UploadCallback callback) {
        new UpdateDungeonAction(oldDungeonPreview, CustomDungeon.maybeFixIncorrectNameEnding(newDungeonName), title, newDescription, difficulty, versionName, callback);
    }

    public static void reportBug(String dungeonName, String description, UploadCallback callback) {
		Bundle dungeonAsBundle;
		if (dungeonName == null) dungeonAsBundle = null;
		else {
			try {
				dungeonAsBundle = CustomDungeonSaves.getExportDungeonBundle(dungeonName);
			} catch (Exception ex) {
				ExportDungeonWrapper.AdditionalFileInfo dungeonFiles = new ExportDungeonWrapper.AdditionalFileInfo(
						FileUtils.getFileHandle(CustomDungeonSaves.DUNGEON_FOLDER + dungeonName.replace(' ', '_'))
				);
				dungeonAsBundle = new Bundle();
				dungeonAsBundle.put(CustomDungeonSaves.BUGGED, dungeonFiles);
			}
		}
		String fileName = URLEncoder.encode(description.substring(0, Math.min(20, description.length())), StandardCharsets.UTF_8);
		
		DungeonPreview uploadPreview = new DungeonPreview();
		uploadPreview.title = fileName;
		uploadPreview.description = description;
		uploadPreview.version = Game.version;
		uploadPreview.intVersion = Game.versionCode;
		uploadPreview.uploader = "null";
		
		Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.POST);
		httpRequest.setUrl(getURL()
				+ "?action=" + ACTION_BUG_REPORT
				+ "&fileName=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8)
				+ uploadPreview.writeArgumentsForURL());
		httpRequest.setHeader("Content-Type", "application/x-www-form-urlencoded");
		httpRequest.setContent("dungeon=" + dungeonAsBundle);
		
		callback.showWindow(httpRequest);
		
		Gdx.net.sendHttpRequest(httpRequest, new UploadDataListener(callback));
	}

    public static void deleteDungeon(String dungeonID, String dungeonName, UploadCallback callback) {
        Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.GET);
        httpRequest.setUrl(getURL()
                + "?action=" + ACTION_DELETE_DUNGEON
                + "&dungeonID=" + dungeonID
                + "&userID=" + getUUID());

        callback.hideCancel();

        Gdx.net.sendHttpRequest(httpRequest, new UploadDataListener(callback));
    }
    
    public static void deleteVersion(String dungeonID, String versionID, UploadCallback callback) {
        Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.GET);
        httpRequest.setUrl(getURL()
                + "?action=" + ACTION_DELETE_VERSION
                + "&dungeonID=" + dungeonID
                + "&versionID=" + versionID
                + "&userID=" + getUUID());
        
        callback.hideCancel();
        
        Gdx.net.sendHttpRequest(httpRequest, new UploadDataListener(callback));
    }

    public static void isCreator(String dungeonID, OwnershipCheckerCallback callback) {

        Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.GET);
        httpRequest.setUrl(getURL()
                + "?action=" + ACTION_IS_CREATOR
                + "&dungeonID=" + dungeonID
                + "&userID=" + getUUID());

        callback.showWindow(httpRequest);

        Gdx.net.sendHttpRequest(httpRequest, new com.badlogic.gdx.Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(com.badlogic.gdx.Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                String result = httpResponse.getResultAsString();
                if (statusCode != HttpStatus.SC_OK) {
                    Game.runOnRenderThread(() -> callback.failed(new SocketException(statusCode + result)));
                    return;
                }
				if (result.startsWith(KEYWORD_BANNED)) Game.runOnRenderThread(() -> callback.failed(new Banned()));
				else Game.runOnRenderThread(() -> callback.successful(Boolean.parseBoolean(result)));
				
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
            byte[] hash = digest.digest(combined.getBytes(StandardCharsets.UTF_8));
//            Base64.getEncoder().encodeToString(hash);
            return convertToHexString(hash);
        } catch (NoSuchAlgorithmException e) {
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

}
