package com.shatteredpixel.shatteredpixeldungeon.editor.overview;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.editor.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.editor.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.FileUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CustomDungeonSaves {

    private static final HashMap<String, CustomDungeonSaves.Info> dungeons = new HashMap<>();

    private static final String FILE_EXTENSION = ".dat";
    private static final String DUNGEON_FOLDER = "custom_dungeons/";
    private static final String LEVEL_FOLDER = "levels/";
    private static final String DUNGEON_DATA = "data" + FILE_EXTENSION;
    private static final String DUNGEON_INFO = "info" + FILE_EXTENSION;

    private static final String LEVEL_FILE = "%s" + FILE_EXTENSION;

    private static final String DUNGEON = "dungeon";
    private static final String INFO = "info";
    private static final String FLOOR = "floor";

    private static String curDirectory;
    private static Files.FileType fileType = Files.FileType.External;//set to internal while playing

    public static void setCurDirectory(String curDirectory) {
        CustomDungeonSaves.curDirectory = curDirectory;
    }

    public static void setFileType(Files.FileType fileType) {
        CustomDungeonSaves.fileType = fileType;
    }

    //TODO maybe add possibility to add floors just by adding files?
    public static void saveDungeon(CustomDungeon dungeon) throws IOException {
        Bundle bundle = new Bundle();
        bundle.put(DUNGEON, dungeon);

        Bundle bundleInfo = new Bundle();
        bundleInfo.put(INFO, dungeon.createInfo());

        setCurDirectory(DUNGEON_FOLDER + dungeon.getName() + "/");
        saveBundle(curDirectory + DUNGEON_DATA, bundle);
        saveBundle(curDirectory + DUNGEON_INFO, bundleInfo);
    }

    public static CustomDungeon loadDungeon(String name) throws IOException {
        Files.FileType oldFileType = fileType;
        fileType = Files.FileType.External;
        setCurDirectory(DUNGEON_FOLDER + name + "/");
        FileHandle file = FileUtils.getFileHandle(fileType, curDirectory + DUNGEON_DATA);
        CustomDungeon dungeon = (CustomDungeon) FileUtils.bundleFromStream(file.read()).get(DUNGEON);
        fileType = oldFileType;
        return dungeon;
    }

    public static CustomLevel loadLevel(String name) throws IOException {
        FileHandle file = FileUtils.getFileHandle(fileType, curDirectory + LEVEL_FOLDER + Messages.format(LEVEL_FILE, name));
        CustomLevel customLevel = (CustomLevel) FileUtils.bundleFromStream(file.read()).get(FLOOR);
        return customLevel;
    }

    public static void saveLevel(Level level) throws IOException {
        Bundle bundle = new Bundle();
        bundle.put(FLOOR, level);

        saveBundle(curDirectory + LEVEL_FOLDER + Messages.format(LEVEL_FILE, level.name), bundle);
    }

    public static boolean deleteLevelFile(String levelSchemeName) {
        return deleteFile(curDirectory + LEVEL_FOLDER + Messages.format(LEVEL_FILE, levelSchemeName));
    }

    private static boolean deleteFile(String name) {
        return FileUtils.getFileHandle(fileType, name).delete();
    }

    public static boolean deleteDungeonFile(String dungeonName) {
        return FileUtils.getFileHandle(fileType, DUNGEON_FOLDER + dungeonName).deleteDirectory();
    }

    private static List<String> getFilesInDir(String name) {
        FileHandle dir = FileUtils.getFileHandle(fileType, name);
        List<String> result = new ArrayList<>();
        if (dir.isDirectory()) {
            for (FileHandle file : dir.list()) {
                result.add(file.name());
            }
        }
        return result;
    }

    public static List<Info> getAllInfos() {
        Files.FileType oldFileType = fileType;
        fileType = Files.FileType.External;
        try {
            List<String> dungeonDirs = getFilesInDir(DUNGEON_FOLDER);
            List<Info> result = new ArrayList<>();
            for (String path : dungeonDirs) {
                FileHandle file = FileUtils.getFileHandle(fileType, DUNGEON_FOLDER + path + "/" + DUNGEON_INFO);
                result.add((Info) FileUtils.bundleFromStream(file.read()).get(INFO));
            }
            Collections.sort(result);
            fileType = oldFileType;
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            ShatteredPixelDungeon.reportException(e);
            fileType = oldFileType;
            return null;
        }
    }

    public static void copyLevelsForNewGame(String dungeonName, String dirDestination) throws IOException {
        try {
            FileHandle src = FileUtils.getFileHandle(Files.FileType.External, DUNGEON_FOLDER + dungeonName + "/" + LEVEL_FOLDER);
            FileHandle dest = FileUtils.getFileHandle(Files.FileType.Local, dirDestination);
            src.copyTo(dest);
        } catch (GdxRuntimeException e) {
            throw new IOException(e);
        }
    }

    public static void writeClearText(String fileName, String text) throws IOException {
        try {
            FileHandle file = FileUtils.getFileHandle(Files.FileType.External, fileName);
            //write to a temp file, then move the files.
            // This helps prevent save corruption if writing is interrupted
            if (file.exists()) {
                FileHandle temp = FileUtils.getFileHandle(fileName + ".tmp");
                write(temp.write(false), text);
                file.delete();
                temp.moveTo(file);
            } else {
                write(file.write(false), text);
            }

        } catch (GdxRuntimeException e) {
            //game classes expect an IO exception, so wrap the GDX exception in that
            throw new IOException(e);
        }
    }

    private static boolean write(OutputStream stream, String text) {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));

            //JSONObject.write does not exist on Android/iOS
            writer.write(text);
            writer.close();
            return true;
        } catch (IOException e) {
            Game.reportException(e);
            return false;
        }
    }

    private static void saveBundle(String name, Bundle bundle) throws IOException {
        try {
            FileHandle file = FileUtils.getFileHandle(fileType, name);
            //write to a temp file, then move the files.
            // This helps prevent save corruption if writing is interrupted
            if (file.exists()) {
                FileHandle temp = FileUtils.getFileHandle(name + ".tmp");
                FileUtils.bundleToStream(temp.write(false), bundle);
                file.delete();
                temp.moveTo(file);
            } else {
                FileUtils.bundleToStream(file.write(false), bundle);
            }

        } catch (GdxRuntimeException e) {
            //game classes expect an IO exception, so wrap the GDX exception in that
            throw new IOException(e);
        }
    }

    public static class Info implements Comparable<Info>, Bundlable {

        public String name;
        public int version;

        public int numLevels;

        public Info() {
        }

        public Info(String name, int version) {
            this.name = name;
            this.version = version;
        }

        @Override
        public int compareTo(Info o) {
            return name.compareTo(o.name);
        }

        private static final String NAME = "name";
        private static final String VERSION = "version";

        @Override
        public void restoreFromBundle(Bundle bundle) {
            name = bundle.getString(NAME);
            version = bundle.getInt(VERSION);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            bundle.put(NAME, name);
            bundle.put(VERSION, version);
        }
    }

}