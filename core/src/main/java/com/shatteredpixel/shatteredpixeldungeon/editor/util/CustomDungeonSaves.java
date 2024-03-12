package com.shatteredpixel.shatteredpixeldungeon.editor.util;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SandboxPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.transitions.TransitionEditPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomDungeonSaves {

    private static final String ROOT_DIR = "";
    private static final String FILE_EXTENSION = ".dat";
    public static final String EXPORT_FILE_EXTENSION = ".dun";//also used in AndroidManifest.xml!!!
    public static final String DUNGEON_FOLDER = ROOT_DIR + "custom_dungeons/";
    private static final String LEVEL_FOLDER = "levels/";
    private static final String DUNGEON_DATA = "data" + FILE_EXTENSION;
    private static final String DUNGEON_INFO = "info" + FILE_EXTENSION;

    private static final String LEVEL_FILE = "%s" + FILE_EXTENSION;

    private static final String DUNGEON = "dungeon";
    private static final String INFO = "info";
    private static final String FLOOR = "floor";
    public static final String EXPORT = "export";
    public static final String BUGGED = "bugged";

    static String curDirectory;

    public static void setCurDirectory(String curDirectory) {
        CustomDungeonSaves.curDirectory = curDirectory;
    }

    //TODO maybe add possibility to add floors just by adding files?
    public static void saveDungeon(CustomDungeon dungeon) throws IOException {
        Bundle bundle = new Bundle();
        bundle.put(DUNGEON, dungeon);

        Bundle bundleInfo = new Bundle();
        bundleInfo.put(INFO, dungeon.createInfo());

        setCurDirectory(DUNGEON_FOLDER + dungeon.getName().replace(' ', '_') + "/");
        FileUtils.bundleToFile(curDirectory + DUNGEON_DATA, bundle);
        FileUtils.bundleToFile(curDirectory + DUNGEON_INFO, bundleInfo);
    }

    public static void exportDungeon(String dungeonName, String pathFromDefaultDir) throws IOException {
        FileUtils.setDefaultFileType(FileUtils.getFileTypeForCustomDungeons());
        exportDungeon(dungeonName, FileUtils.getFileHandle(pathFromDefaultDir + dungeonName.replace(' ', '_') + EXPORT_FILE_EXTENSION));
    }

    public static void exportDungeon(String dungeonName, FileHandle file) throws IOException {
        try {
            FileUtils.bundleToFile(file, getExportDungeonBundle(dungeonName));
        } catch (RenameRequiredException e) {
            e.showExceptionWindow();
        }
    }

    public static Bundle getExportDungeonBundle(String dungeonName) throws IOException, RenameRequiredException {
        Bundle export = new Bundle();
        CustomDungeon dun = CustomDungeonSaves.loadDungeon(dungeonName);
        dun.downloaded = true;
        export.put(EXPORT, new ExportDungeonWrapper(dun));
        return export;
    }

    public static class RenameRequiredException extends Exception {
        public RenameRequiredException(FileHandle file, String name) {
            super(Messages.get(CustomDungeonSaves.class, "file_rename", file.file().getAbsolutePath(), name.replace(' ', '_') + FILE_EXTENSION));
        }

        public RenameRequiredException(FileHandle file, String name, Object ignoredMakeNotAmbiguous) {
            super(Messages.get(CustomDungeonSaves.class, "dir_rename", file.file().getAbsolutePath(), name.replace(' ', '_')));
        }

        public void showExceptionWindow() {
            Game.runOnRenderThread(() -> {
                WndTitledMessage w = new WndError(getMessage());
                w.setHighligtingEnabled(false);
                if (Game.scene() instanceof EditorScene) EditorScene.show(w);
                else Game.scene().addToFront(w);
            });
        }
    }

    public static CustomDungeon loadDungeon(String name) throws IOException, RenameRequiredException {
        FileUtils.setDefaultFileType(FileUtils.getFileTypeForCustomDungeons());
        setCurDirectory(DUNGEON_FOLDER + name.replace(' ', '_') + "/");
        FileHandle file = FileUtils.getFileHandle(curDirectory + DUNGEON_DATA);
        if (!file.exists()) {
            throw new RenameRequiredException(FileUtils.getFileHandle(DUNGEON_FOLDER + findActualDungeonFolderName(name)), name, null);
        }
        return (CustomDungeon) FileUtils.bundleFromStream(file.read()).get(DUNGEON);
    }

    public static CustomLevel loadLevel(String name) throws IOException, RenameRequiredException {
        return loadLevel(name, true);
    }

    public static CustomLevel loadLevel(String name, boolean removeInvalidTransitions) throws IOException, RenameRequiredException {
        FileHandle file = FileUtils.getFileHandle(curDirectory + LEVEL_FOLDER + Messages.format(LEVEL_FILE, name));
        if (!file.exists())//Still: it is important to rename old ones properly before or they might override other files
            file = FileUtils.getFileHandle(curDirectory + LEVEL_FOLDER + Messages.format(LEVEL_FILE, name.replace(' ', '_')));
        else if (name.contains(" ") && file.exists() && curDirectory.contains("custom_dungeons"))
            throw new RenameRequiredException(file, name);
        CustomLevel customLevel = (CustomLevel) FileUtils.bundleFromStream(file.read()).get(FLOOR);

        //checks if all transitions are still valid, can even remove transitions AFTER the game was started if necessary
        if (removeInvalidTransitions) {
            for (LevelTransition t : new ArrayList<>(customLevel.transitions.values())) {
                if (t.destBranch == 0 && !t.destLevel.equals(Level.SURFACE) && t.departCell != TransitionEditPart.NONE) {
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

    public static FileHandle getLevelFile(String levelName) {
        FileHandle file = FileUtils.getFileHandle(curDirectory + LEVEL_FOLDER + Messages.format(LEVEL_FILE, levelName));
        if (!file.exists())//Still: it is important to rename old ones properly before or they might override other files
            file = FileUtils.getFileHandle(curDirectory + LEVEL_FOLDER + Messages.format(LEVEL_FILE, levelName.replace(' ', '_')));
        return file;
    }

    public static CustomLevel loadLevelWithOgName(String name) throws IOException {
        FileHandle file = FileUtils.getFileHandle(curDirectory + LEVEL_FOLDER + Messages.format(LEVEL_FILE, name));
        if (!file.exists())//Still: it is important to rename old ones properly before or they might override other files
            file = FileUtils.getFileHandle(curDirectory + LEVEL_FOLDER + Messages.format(LEVEL_FILE, name.replace(' ', '_')));
        return (CustomLevel) FileUtils.bundleFromStream(file.read()).get(FLOOR);
    }

    public static void saveLevelWithOgName(Level level) throws IOException {
        Bundle bundle = new Bundle();
        bundle.put(FLOOR, level);
        FileUtils.bundleToFile(curDirectory + LEVEL_FOLDER + Messages.format(LEVEL_FILE, level.name), bundle);
    }

    public static void saveLevel(Level level) throws IOException {
        Bundle bundle = new Bundle();
        Dungeon.level = level;
        bundle.put(FLOOR, level);
        Dungeon.level = EditorScene.customLevel();
        FileUtils.bundleToFile(curDirectory + LEVEL_FOLDER + Messages.format(LEVEL_FILE, level.name.replace(' ', '_')), bundle);
    }

    public static boolean deleteLevelFile(String levelSchemeName) {
        return FileUtils.getFileHandle(curDirectory + LEVEL_FOLDER + Messages.format(LEVEL_FILE, levelSchemeName.replace(' ', '_'))).delete();
    }

    public static boolean deleteDungeonFile(String dungeonName) {
        try {
            return FileUtils.getFileHandleWithDefaultPath(FileUtils.getFileTypeForCustomDungeons(), DUNGEON_FOLDER + findActualDungeonFolderName(dungeonName)).deleteDirectory();
        } catch (IOException e) {
            return FileUtils.getFileHandleWithDefaultPath(FileUtils.getFileTypeForCustomDungeons(), DUNGEON_FOLDER + dungeonName.replace(' ', '_')).deleteDirectory();
        }
    }

    public static CustomDungeon renameDungeon(String oldName, String newName) throws IOException {

        FileHandle dungeonOld = FileUtils.getFileHandleWithDefaultPath(FileUtils.getFileTypeForCustomDungeons(), DUNGEON_FOLDER + oldName.replace(' ', '_'));
        FileHandle dungeonNew = FileUtils.getFileHandleWithDefaultPath(FileUtils.getFileTypeForCustomDungeons(), DUNGEON_FOLDER + newName.replace(' ', '_'));
        dungeonOld.moveTo(dungeonNew);

        return (CustomDungeon) FileUtils.bundleFromStream(
                FileUtils.getFileHandle(DUNGEON_FOLDER + newName.replace(' ', '_') + "/" + DUNGEON_DATA).read()).get(DUNGEON);
    }

    public static void moveLevel(String oldName, String newName) {
        FileHandle fileOld = FileUtils.getFileHandle(curDirectory + LEVEL_FOLDER + Messages.format(LEVEL_FILE, oldName.replace(' ', '_')));
        FileHandle fileNew = FileUtils.getFileHandle(curDirectory + LEVEL_FOLDER + Messages.format(LEVEL_FILE, newName.replace(' ', '_')));
        fileOld.moveTo(fileNew);
    }

    private static String findActualDungeonFolderName(String dungeonName) throws IOException {
        for (String path : getFilesInDir(DUNGEON_FOLDER)) {
            FileHandle datFile = FileUtils.getFileHandleWithDefaultPath(FileUtils.getFileTypeForCustomDungeons(), DUNGEON_FOLDER + path + "/" + DUNGEON_INFO);
            if (datFile.exists() && ((Info) FileUtils.bundleFromStream(datFile.read()).get(INFO)).name.equals(dungeonName))
                return path;
        }
        return dungeonName;
    }

    private static List<String> getFilesInDir(String name) {
        return getFilesInDir(FileUtils.getFileHandleWithDefaultPath(FileUtils.getFileTypeForCustomDungeons(), name));
    }

    public static List<String> getFilesInDir(FileHandle dir) {
        List<String> result = new ArrayList<>();
        if (dir.isDirectory()) {
            for (FileHandle file : dir.list()) {
                result.add(file.name());
            }
        }
        return result;
    }

    public static List<Info> getAllInfos() {

        try {
            List<String> dungeonDirs = getFilesInDir(DUNGEON_FOLDER);
            List<Info> result = new ArrayList<>();
            for (String path : dungeonDirs) {
                if (path.endsWith(EXPORT_FILE_EXTENSION)) {
                    FileHandle file = FileUtils.getFileHandleWithDefaultPath(FileUtils.getFileTypeForCustomDungeons(), DUNGEON_FOLDER + path);
                    if (file.exists() && !file.isDirectory()) {
                        Info info = ExportDungeonWrapper.doImport(file);
                        if (info != null) {
                            result.add(info);
                            file.delete();
                        }
                        continue;
                    }
                }
                FileHandle file = FileUtils.getFileHandleWithDefaultPath(FileUtils.getFileTypeForCustomDungeons(), DUNGEON_FOLDER + path + "/" + DUNGEON_INFO);
                if (file.exists())
                    result.add((Info) FileUtils.bundleFromStream(file.read()).get(INFO));
            }
            Collections.sort(result);
            return result;

        } catch (IOException e) {
            SandboxPixelDungeon.scene().add(new WndError(
                    "Could not retrieve the available dungeons:\n"
                            + e.getClass().getSimpleName() + ": " + e.getMessage()));
            return null;
        }
    }

    public static FileHandle getAdditionalFilesDir() {
        return FileUtils.getFileHandle(CustomDungeonSaves.curDirectory + CustomTileLoader.EXTRA_FILES);
    }

    public static List<String> findAllFiles(String... extensions) {
        FileHandle dir = getAdditionalFilesDir();
        if (!dir.exists()) {
            dir.mkdirs();
        } else if (!dir.isDirectory()) {
            return null;
        }

        Set<FileHandle> files = new HashSet<>();
        addAddSubdirFiles(files, dir);
        List<String> fullFilePaths = new ArrayList<>(8);

        String rootDir = dir.path() + "/";

        StringBuilder b = new StringBuilder();
        for (int i = 0; i < extensions.length; i++) {
            b.append(extensions[i]).append('/');
        }
        String extensionsCSV = b.toString();

        for (FileHandle f : files) {
            if (extensionsCSV.contains(f.extension())) {
                fullFilePaths.add(f.path().replaceFirst(rootDir, ""));
            }
        }

        Collections.sort(fullFilePaths);

        return fullFilePaths;
    }

    private static void addAddSubdirFiles(Set<FileHandle> files, FileHandle parentDir) {
        for (FileHandle f : parentDir.list()) {
            if (f.isDirectory()) {
                addAddSubdirFiles(files, f);
            } else {
                files.add(f);
            }
        }
    }

    public static String getExternalFilePath(String pathFromRoot) {
        return CustomDungeonSaves.curDirectory + CustomTileLoader.EXTRA_FILES + pathFromRoot;
    }

    public static FileHandle getExternalFile(String pathFromRoot) {
        return FileUtils.getFileHandle(getExternalFilePath(pathFromRoot));
    }

    public static void copyLevelsForNewGame(String dungeonName, String dirDestination) throws IOException {
        try {
            FileHandle src = FileUtils.getFileHandleWithDefaultPath(FileUtils.getFileTypeForCustomDungeons(),
                    DUNGEON_FOLDER + dungeonName.replace(' ', '_') + "/" + LEVEL_FOLDER);
            if (src.exists()) {
                FileHandle dest = FileUtils.getFileHandle(dirDestination.replace(' ', '_'));
                src.copyTo(dest);
            }

            src = FileUtils.getFileHandleWithDefaultPath(FileUtils.getFileTypeForCustomDungeons(),
                    DUNGEON_FOLDER + dungeonName.replace(' ', '_') + "/" + CustomTileLoader.EXTRA_FILES);
            if (src.exists()) {
                FileHandle dest = FileUtils.getFileHandle(dirDestination.replace(' ', '_'));
                src.copyTo(dest);
            }

        } catch (GdxRuntimeException e) {
            throw new IOException(e);
        }
    }

    public static void writeClearText(String fileName, String text) throws IOException {
        try {
            FileHandle file = FileUtils.getFileHandleWithDefaultPath(FileUtils.getFileTypeForCustomDungeons(), ROOT_DIR + fileName.replace(' ', '_'));
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

    public static void writeBytes(String fileName, byte[] bytes) throws IOException {
        try {
            FileHandle file = FileUtils.getFileHandleWithDefaultPath(FileUtils.getFileTypeForCustomDungeons(), ROOT_DIR + fileName.replace(' ', '_'));
            //write to a temp file, then move the files.
            // This helps prevent save corruption if writing is interrupted
            if (file.exists()) {
                FileHandle temp = FileUtils.getFileHandle(fileName.replace(' ', '_') + ".tmp");
                temp.writeBytes(bytes, false);
                file.delete();
                temp.moveTo(file);
            } else {
                file.writeBytes(bytes, false);
            }

        } catch (GdxRuntimeException e) {
            //game classes expect an IO exception, so wrap the GDX exception in that
            throw new IOException(e);
        }
    }

    public static String getAbsolutePath(String fileName) {
        String path = FileUtils.getFileHandleWithDefaultPath(FileUtils.getFileTypeForCustomDungeons(), ROOT_DIR + fileName.replace(' ', '_')).file().getAbsolutePath();
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
        public boolean downloaded;

        public int hashcode;

        public Info() {
        }

        public Info(String name, int version, int numLevels, int hashcode, boolean downloaded) {
            this.name = name;
            this.version = version;
            this.numLevels = numLevels;
            this.hashcode = hashcode;
            this.downloaded = downloaded;
        }

        @Override
        public int compareTo(Info o) {
            return name.compareTo(o.name);
        }

        private static final String NAME = "name";
        private static final String VERSION = "version";
        private static final String NUM_LEVLES = "num_levels";
        private static final String HASHCODE = "hashcode";
        private static final String DOWNLOADED = "downloaded";

        @Override
        public void restoreFromBundle(Bundle bundle) {
            name = bundle.getString(NAME);
            version = bundle.getInt(VERSION);
            numLevels = bundle.getInt(NUM_LEVLES);
            hashcode = bundle.getInt(HASHCODE);
            downloaded = bundle.getBoolean(DOWNLOADED);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            bundle.put(NAME, name);
            bundle.put(VERSION, version);
            bundle.put(NUM_LEVLES, numLevels);
            bundle.put(HASHCODE, hashcode);
            bundle.put(DOWNLOADED, downloaded);
        }
    }

}