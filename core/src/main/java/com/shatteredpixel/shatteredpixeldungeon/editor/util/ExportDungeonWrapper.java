package com.shatteredpixel.shatteredpixeldungeon.editor.util;

import com.badlogic.gdx.files.FileHandle;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SandboxPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.FileUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExportDungeonWrapper implements Bundlable {

    private CustomDungeon dungeon;
    public CustomDungeonSaves.Info dungeonInfo;
    private Set<Level> customLevels;
    private AdditionalFileInfo customTiles;

    public ExportDungeonWrapper() {
    }

    public ExportDungeonWrapper(CustomDungeon dungeon) {
        this.dungeon = dungeon;
    }

    public static boolean hasCustomTiles(String dungeonName){
        FileHandle file = FileUtils.getFileHandle(
                CustomDungeonSaves.DUNGEON_FOLDER + dungeonName.replace(' ', '_') + "/" + CustomTileLoader.CUSTOM_TILES);
        return file.exists() && file.isDirectory() && file.list().length > 0;
    }

    private static final String DUNGEON = "dungeon";
    private static final String LEVEL = "level";
    private static final String CUSTOM_TILES = "custom_tiles";

    @Override
    public void restoreFromBundle(Bundle bundle) {

        dungeon = (CustomDungeon) bundle.get(DUNGEON);
        dungeonInfo = dungeon.createInfo();

        Dungeon.customDungeon = dungeon;//for level loading
        customLevels = new HashSet<>();

        int i = 0;
        while (bundle.contains(LEVEL + "_" + i)) {
            customLevels.add((Level) bundle.get(LEVEL + "_" + i));
            i++;
        }

//        if (bundle.contains(CUSTOM_TILES)) {
//            customTiles = (AdditionalFileInfo) bundle.get(CUSTOM_TILES);
//        }
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(DUNGEON, dungeon);

        Dungeon.customDungeon = dungeon;//for level loading

        int i = 0;
        for (LevelScheme levelScheme : dungeon.levelSchemes()) {
            if (levelScheme.getType() == CustomLevel.class) {
                Level l = levelScheme.loadLevel();
                if (l == null) continue;
                Dungeon.level = l;
                bundle.put(LEVEL + "_" + i, l);
                i++;
            }
        }

//        FileHandle customTiles = FileUtils.getFileHandle(
//                CustomDungeonSaves.DUNGEON_FOLDER + dungeon.getName().replace(' ', '_') + "/" + CustomTileLoader.CUSTOM_TILES);
//        if (customTiles.exists() && customTiles.isDirectory() && customTiles.list().length > 0) {
//            bundle.put(CUSTOM_TILES, new AdditionalFileInfo(customTiles));
//        }
    }

    public static CustomDungeonSaves.Info doImport(FileHandle file) {
        try {
            return ((ExportDungeonWrapper) FileUtils.bundleFromStream(file.read()).get(CustomDungeonSaves.EXPORT)).doImport();
        } catch (IOException ex) {
            SandboxPixelDungeon.reportException(ex);
            return null;
        }
    }

    public CustomDungeonSaves.Info doImport() {
        try {
            FileUtils.setDefaultFileType(FileUtils.getFileTypeForCustomDungeons());

            if (FileUtils.getFileHandle(CustomDungeonSaves.DUNGEON_FOLDER + dungeon.getName().replace(' ', '_')).exists()) return null;

            CustomDungeonSaves.saveDungeon(dungeon);

            for (Level l : customLevels) {
                CustomDungeonSaves.saveLevel(l);
            }

//            if (export.customTiles != null) {
//                export.customTiles.doImport(
//                        CustomDungeonSaves.DUNGEON_FOLDER + export.dungeon.getName().replace(' ', '_') + "/");
//            }

            return dungeonInfo;

        } catch (Exception ex) {
            SandboxPixelDungeon.reportException(ex);
        }
        return null;
    }

    public static class AdditionalFileInfo implements Bundlable {

        private boolean skip = false;//does not create file for it, assumes it already exists

        private String name;
        private boolean isDirectory;
        private String content;
        private AdditionalFileInfo[] subFiles;

        public AdditionalFileInfo() {
        }

        public AdditionalFileInfo(FileHandle file) {
            name = file.name();
            isDirectory = file.isDirectory();

            if (isDirectory) {
                FileHandle[] files = file.list();
                subFiles = new AdditionalFileInfo[files.length];
                for (int i = 0; i < files.length; i++) {
                    if (files[i].exists()) subFiles[i] = new AdditionalFileInfo(files[i]);
                }
            } else {
                try {
                    content = file.readString();
                } catch (Exception ignored) {
                    content = null;
                    throw new RuntimeException(ignored);
                }

            }
        }

        private static final String NAME = "name";
        private static final String IS_DIRECTORY = "is_directory";
        private static final String CONTENT_AS_STRING = "content_as_string";
        private static final String SUB_FILE = "sub_file";

        @Override
        public void restoreFromBundle(Bundle bundle) {
            name = bundle.getString(NAME);
            isDirectory = bundle.getBoolean(IS_DIRECTORY);

            if (bundle.contains(CONTENT_AS_STRING)) content = bundle.getString(CONTENT_AS_STRING);

            int i = 0;
            List<AdditionalFileInfo> subFilesList = new ArrayList<>();
            while (bundle.contains(SUB_FILE + "_" + i)) {
                subFilesList.add((AdditionalFileInfo) bundle.get(SUB_FILE + "_" + i));
                i++;
            }
            subFiles = subFilesList.toArray(new AdditionalFileInfo[0]);
        }

        @Override
        public void storeInBundle(Bundle bundle) {

            bundle.put(NAME, name);
            bundle.put(IS_DIRECTORY, isDirectory);

            if (content != null) bundle.put(CONTENT_AS_STRING, content);

            if (isDirectory) {
                int i = 0;
                for (AdditionalFileInfo afi : subFiles) {
                    bundle.put(SUB_FILE + "_" + i, afi);
                }
            }
        }

        public void doImport(String path) {
            if (!skip) {
                if (isDirectory) {
                    FileUtils.getFileHandle(path + name).mkdirs();
                } else {
                    try {
                        CustomDungeonSaves.writeClearText(path + name, content);
                    } catch (IOException ignored) {
                    }
                }
            }
            if (isDirectory) {
                for (AdditionalFileInfo f : subFiles) {
                    f.doImport(path + name + "/");
                }
            }
        }

    }
}