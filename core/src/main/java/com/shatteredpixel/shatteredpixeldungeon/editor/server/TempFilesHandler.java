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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.utils.Base64Coder;
import com.shatteredpixel.shatteredpixeldungeon.services.server.ServerCommunication;
import com.watabou.NotAllowedInLua;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.Consumer;
import com.watabou.utils.FileUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.net.SocketException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.shatteredpixel.shatteredpixeldungeon.services.server.ServerConstants.ACTION_DOWNLOAD_FILE;
import static com.shatteredpixel.shatteredpixeldungeon.services.server.ServerConstants.ACTION_READ_FILE;

@NotAllowedInLua
public final class TempFilesHandler {

    private TempFilesHandler() {}
    
    private static final String TEMP_FILE_DIR = "temp_files";
    
    private static final Map<String, Consumer<FileHandle>> activeRequestsFile = Collections.synchronizedMap(new HashMap<>());
    private static final Map<String, Consumer<Bundle>> activeRequestsData = Collections.synchronizedMap(new HashMap<>());
    
    /**
     * Deletes all files in the temp_files directory.
     */
    public static void clearTempFiles() {
        getTempFileDir().deleteDirectory();
    }
    
    public static synchronized void retrieveOrRequest(String dungeonID, String fileID, Consumer<FileHandle> onFileAvailable) {
        retrieveOrRequest(dungeonID, fileID, "", onFileAvailable);
    }
    
    public static synchronized void retrieveOrRequest(String dungeonID, String fileID, String fileExtension, Consumer<FileHandle> onFileAvailable) {
        if (!fileExtension.isEmpty()) {
            fileExtension = "." + fileExtension;
        }
        FileHandle retrieved = retrieveTempFile(dungeonID, fileID, fileExtension);
        if (retrieved != null) {
            onFileAvailable.accept(retrieved);
        } else {
            requestFileFromServer(dungeonID, fileID, fileExtension, onFileAvailable);
        }
    }
    
    /**
     * @return the File, but only if it has already been fetched from the server
     */
    private static FileHandle retrieveTempFile(String dungeonID, String fileID, String fileExtension) {
        FileHandle tempFileDir = getTempFileDir();
        if (tempFileDir == null || !tempFileDir.isDirectory()) {
            return null;
        }
        FileHandle[] files = tempFileDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.equals(dungeonID) && dir.isDirectory();
            }
        });
        if (files.length < 1) {
            return null;
        }
        FileHandle dungeonDir = files[0];
        String fileName = fileID + fileExtension;
        files = dungeonDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.equals(fileName) && !dir.isDirectory();
            }
        });
        if (files.length < 1) {
            return null;
        }
        return files[0];
    }
    
    private static void requestFileFromServer(String dungeonID, String fileID, String fileExtension, Consumer<FileHandle> onFileAvailable) {
        //keep in mind that only compressed files that are somewhere in the child tree of the root folder can be accessed
        
        boolean alreadySent = activeRequestsFile.containsKey(fileID);
        activeRequestsFile.put(fileID, onFileAvailable);
        
        if (alreadySent) {
            return;
        }
        
        Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.GET);
        httpRequest.setUrl(ServerCommunication.getURL()
                + "?action=" + ACTION_DOWNLOAD_FILE
                + "&fileID=" + fileID);
        
        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                String result = httpResponse.getResultAsString();
                if (statusCode != HttpStatus.SC_OK) {
                    activeRequestsFile.remove(fileID).accept(null);
                    System.err.println(result);
                    return;
                }
                try {
                    FileHandle file = decodeAndSafeToFile(result, dungeonID, fileID, fileExtension);
                    activeRequestsFile.remove(fileID).accept(file);
                    
                } catch (Exception e) {
                    Game.reportException(e);
                    System.err.println(result);
                    activeRequestsFile.remove(fileID).accept(null);
                }
            }
            
            @Override
            public void failed(Throwable throwable) {
                Game.reportException(throwable);
                activeRequestsFile.remove(fileID).accept(null);
            }
            
            @Override
            public void cancelled() {
                activeRequestsFile.remove(fileID);
            }
        });
    }
    
    public static synchronized void requestDataFromServer(String fileID, Consumer<Bundle> onDataAvailable) {
        //keep in mind that only compressed files that are somewhere in the child tree of the root folder can be accessed
        
        boolean alreadySent = activeRequestsData.containsKey(fileID);
        activeRequestsData.put(fileID, onDataAvailable);
        
        if (alreadySent) {
            return;
        }
        
        Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.GET);
        httpRequest.setUrl(ServerCommunication.getURL()
                + "?action=" + ACTION_READ_FILE
                + "&fileID=" + fileID);
        
        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                if (statusCode != HttpStatus.SC_OK) {
                    Game.reportException(new SocketException(statusCode + httpResponse.getResultAsString()));
                    activeRequestsData.remove(fileID).accept(null);
                    return;
                }
                try {
                    Bundle data = Bundle.read(httpResponse.getResultAsStream());
                    activeRequestsData.remove(fileID).accept(data);
                    
                } catch (Exception e) {
                    Game.reportException(e);
                    activeRequestsData.remove(fileID).accept(null);
                }
            }
            
            @Override
            public void failed(Throwable throwable) {
                Game.reportException(throwable);
                activeRequestsData.remove(fileID).accept(null);
            }
            
            @Override
            public void cancelled() {
                activeRequestsData.remove(fileID);
            }
        });
    }
    
    private static FileHandle getTempFileDir() {
        return FileUtils.getFileHandleWithDefaultPath(FileUtils.getOriginalFileType(), TEMP_FILE_DIR);
    }
    
    
    private static FileHandle decodeAndSafeToFile(String content, String dungeonID, String fileID, String fileExtension) {
        byte[] bytes = Base64Coder.decode(content.replace(' ', '+'));
        
        FileHandle dest = getTempFileDir().child(dungeonID).child(fileID + fileExtension);
        dest.writeBytes(bytes, false);
        
        return dest;
    }
    
}
