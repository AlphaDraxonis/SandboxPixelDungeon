package com.alphadraxonis.sandboxpixeldungeon.editor.util;

import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.SandboxPixelDungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomDungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomLevel;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.LevelScheme;
import com.alphadraxonis.sandboxpixeldungeon.levels.Level;
import com.alphadraxonis.sandboxpixeldungeon.levels.features.LevelTransition;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.DeviceCompat;
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

    private static final String ROOT_DIR = DeviceCompat.isDesktop() ? "Sandbox-Pixel-Dungeon/" : "";
    private static final String FILE_EXTENSION = ".dat";
    public static final String DUNGEON_FOLDER = ROOT_DIR + "custom_dungeons/";
    private static final String LEVEL_FOLDER = "levels/";
    private static final String DUNGEON_DATA = "data" + FILE_EXTENSION;
    private static final String DUNGEON_INFO = "info" + FILE_EXTENSION;

    private static final String LEVEL_FILE = "%s" + FILE_EXTENSION;

    private static final String DUNGEON = "dungeon";
    private static final String INFO = "info";
    private static final String FLOOR = "floor";

    private static String curDirectory;
    private static Files.FileType fileType = Files.FileType.External;//set to local while playing
    public static final Files.FileType defaultFileType = Files.FileType.External;

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

        setCurDirectory(DUNGEON_FOLDER + dungeon.getName().replace(' ', '_') + "/");
        saveBundle(curDirectory + DUNGEON_DATA, bundle);
        saveBundle(curDirectory + DUNGEON_INFO, bundleInfo);
    }

    public static CustomDungeon loadDungeon(String name) throws IOException {
        Files.FileType oldFileType = fileType;
        fileType = defaultFileType;
        setCurDirectory(DUNGEON_FOLDER + name.replace(' ', '_') + "/");
        FileHandle file = FileUtils.getFileHandle(fileType, curDirectory + DUNGEON_DATA);
        CustomDungeon dungeon = (CustomDungeon) FileUtils.bundleFromStream(file.read()).get(DUNGEON);
        fileType = oldFileType;
        return dungeon;
    }

    public static CustomLevel loadLevel(String name) throws IOException {
        return loadLevel(name.replace(' ', '_'), true);
    }

    public static CustomLevel loadLevel(String name, boolean removeInvalidTransitions) throws IOException {
        FileHandle file = FileUtils.getFileHandle(fileType, curDirectory + LEVEL_FOLDER + Messages.format(LEVEL_FILE, name.replace(' ', '_')));
        CustomLevel customLevel = (CustomLevel) FileUtils.bundleFromStream(file.read()).get(FLOOR);

        //checks if all transitions are still valid, can even remove transitions AFTER the game was started if necessary
        if (removeInvalidTransitions) {
            for (LevelTransition t : new ArrayList<>(customLevel.transitions.values())) {
                if (!t.destLevel.equals(Level.SURFACE)) {
                    LevelScheme destLevel = Dungeon.customDungeon.getFloor(t.destLevel);
                    if (destLevel == null
                            || !destLevel.entranceCells.contains(t.destCell)
                            && !destLevel.exitCells.contains(t.destCell))
                        customLevel.transitions.remove(t.cell());
                }
            }
        }

        return customLevel;
    }

    public static void saveLevel(Level level) throws IOException {
        Bundle bundle = new Bundle();
        bundle.put(FLOOR, level);

        saveBundle(curDirectory + LEVEL_FOLDER + Messages.format(LEVEL_FILE, level.name.replace(' ', '_')), bundle);
    }

    public static boolean deleteLevelFile(String levelSchemeName) {
        return deleteFile(curDirectory + LEVEL_FOLDER + Messages.format(LEVEL_FILE, levelSchemeName.replace(' ', '_')));
    }

    private static boolean deleteFile(String name) {
        return FileUtils.getFileHandle(fileType, name.replace(' ', '_')).delete();
    }

    public static boolean deleteDungeonFile(String dungeonName) {
        return FileUtils.getFileHandle(fileType, DUNGEON_FOLDER + dungeonName.replace(' ', '_')).deleteDirectory();
    }

    public static CustomDungeon renameDungeon(String oldName, String newName) throws IOException {

        FileHandle dungeonOld = FileUtils.getFileHandle(defaultFileType, DUNGEON_FOLDER + oldName.replace(' ', '_'));
        FileHandle dungeonNew = FileUtils.getFileHandle(defaultFileType, DUNGEON_FOLDER + newName.replace(' ', '_'));
        dungeonOld.moveTo(dungeonNew);

        return (CustomDungeon) FileUtils.bundleFromStream(
                FileUtils.getFileHandle(defaultFileType, DUNGEON_FOLDER + newName.replace(' ', '_')+"/"+DUNGEON_DATA).read()).get(DUNGEON);
    }

    public static void renameLevel(String oldName, String newName) throws IOException {
        FileHandle fileOld = FileUtils.getFileHandle(defaultFileType, curDirectory + LEVEL_FOLDER + Messages.format(LEVEL_FILE, oldName.replace(' ', '_')));
        FileHandle fileNew = FileUtils.getFileHandle(defaultFileType, curDirectory + LEVEL_FOLDER + Messages.format(LEVEL_FILE, newName.replace(' ', '_')));
        fileOld.moveTo(fileNew);
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
        fileType = defaultFileType;
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
            SandboxPixelDungeon.reportException(e);
            fileType = oldFileType;
            return null;
        }
    }

    public static void copyLevelsForNewGame(String dungeonName, String dirDestination) throws IOException {
        try {
            FileHandle src = FileUtils.getFileHandle(defaultFileType, DUNGEON_FOLDER + dungeonName.replace(' ', '_') + "/" + LEVEL_FOLDER);
            if (!src.exists()) return;
            FileHandle dest = FileUtils.getFileHandle(Files.FileType.Local, dirDestination.replace(' ', '_'));
            src.copyTo(dest);
        } catch (GdxRuntimeException e) {
            throw new IOException(e);
        }
    }

    public static void writeClearText(String fileName, String text) throws IOException {
        try {
            FileHandle file = FileUtils.getFileHandle(defaultFileType, ROOT_DIR + fileName.replace(' ', '_'));
            //write to a temp file, then move the files.
            // This helps prevent save corruption if writing is interrupted
            if (file.exists()) {
                FileHandle temp = FileUtils.getFileHandle(fileName.replace(' ', '_') + ".tmp");
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

    public static String getAbsolutePath(String fileName) {
        String path = FileUtils.getFileHandle(defaultFileType, ROOT_DIR + fileName.replace(' ', '_')).file().getAbsolutePath();
        if (DeviceCompat.isAndroid()) {
            path = path.substring(20);// /storage/emulated/0/ ->20chars
            path = path.replaceFirst("sandboxpd/", "sandboxpd/" + "\n");//does not work for .indev
        } else if (DeviceCompat.isDesktop()) {
            path = path.replace('\\', '/');//Backslashes don't look nice
            path = path.replaceFirst(ROOT_DIR, ROOT_DIR + "\n");
        }
        return path;
    }

    public static class Info implements Comparable<Info>, Bundlable {

        public String name;
        public int version;

        public int numLevels;

        public Info() {
        }

        public Info(String name, int version, int numLevels) {
            this.name = name;
            this.version = version;
            this.numLevels = numLevels;
        }

        @Override
        public int compareTo(Info o) {
            return name.compareTo(o.name);
        }

        private static final String NAME = "name";
        private static final String VERSION = "version";
        private static final String NUM_LEVLES = "num_levels";

        @Override
        public void restoreFromBundle(Bundle bundle) {
            name = bundle.getString(NAME);
            version = bundle.getInt(VERSION);
            numLevels = bundle.getInt(NUM_LEVLES);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            bundle.put(NAME, name);
            bundle.put(VERSION, version);
            bundle.put(NUM_LEVLES, numLevels);
        }
    }

}