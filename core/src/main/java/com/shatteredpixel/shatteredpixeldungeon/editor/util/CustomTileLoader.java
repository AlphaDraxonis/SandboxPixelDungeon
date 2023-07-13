package com.shatteredpixel.shatteredpixeldungeon.editor.util;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.EditorItemBag;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Tiles;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.watabou.noosa.Tilemap;
import com.watabou.utils.Bundle;
import com.watabou.utils.FileUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CustomTileLoader {

    static final String CUSTOM_TILES = "custom_tiles/";
    private static final String DESC_FILE_EXTENSION = ".dat";

    private CustomTileLoader() {
    }

    public static void loadTiles() {

        EditorItemBag.callStaticInitializers();
        Tiles.clearCustomTiles();

        FileHandle dir = FileUtils.getFileHandle(CustomDungeonSaves.curDirectory + CUSTOM_TILES);
        if (!dir.exists()) {
            dir.mkdirs();
        } else if (!dir.isDirectory()) {
            if (!dir.delete()) return;
            dir.mkdirs();
        }


        Map<String, FileHandle> fileMap = new HashMap<>();
        fillWithFilesInDirectory(dir, fileMap);

        List<FileHandle> sorted = new ArrayList<>(fileMap.values());
        Collections.sort(sorted, (o1, o2) -> o1.name().compareTo(o2.name()));
        for (FileHandle file : sorted) {
            if (file.name().endsWith(DESC_FILE_EXTENSION)) {
                readDescFile(file, fileMap);
            }
        }
    }

    private static void fillWithFilesInDirectory(FileHandle dir, Map<String, FileHandle> fileMap){
        FileHandle[] files = dir.list("");
        for (FileHandle file : files) {
            if (file.isDirectory()) fillWithFilesInDirectory(file, fileMap);
            else {
                String n;
                if (fileMap.containsKey(n = file.name()))
                    GameScene.errorMsg.add("Duplicate custom tile name detected: >>>" + n + "<<< (in dungeon " + Dungeon.customDungeon.getName() + ")");
                else fileMap.put(n, file);
            }
        }
    }

    // image="myImage.png"
    // name="Mein name\" oh"
    // desc="Desc"
    // terrain="Wall"
    private static void readDescFile(FileHandle file, Map<String, FileHandle> fileMap) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file.file()))) {

            OwnCustomTile ownCustomTile = new OwnCustomTile();
            boolean centerDisabled = false;

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("center")) {
                    centerDisabled = true;
                    continue;
                }
                String[] propertySplit = line.split("=");
                if (propertySplit.length <= 1) continue;
                String property = propertySplit[0];

                String[] valueSplit = line.split("\"");
                if (valueSplit.length <= 1) continue;
                String temp = String.valueOf((char) 76545678);
                int i;
                for (i = 1; i < valueSplit.length; i++) {
                    valueSplit[i] = valueSplit[i].replace("\\\\", temp);
                    if (!valueSplit[i].endsWith("\\")) break;
                }
                i++;
                String value = valueSplit[1];
                for (int j = 2; j < i; j++) {
                    value = value.substring(0, value.length() - 1);// cut \
                    value += "\"" + valueSplit[j];
                }
                value = value.replace("\\n", "\n");
                value = value.replace(temp, "\\");

                if (property.startsWith("i")) {
                    // FileUtils.getFileHandle(CustomDungeonSaves.curDirectory + CUSTOM_TILES + value)
                    Pixmap texture = new Pixmap(fileMap.get(value));
                    ownCustomTile.tileW = texture.getWidth() / 16;
                    ownCustomTile.tileH = texture.getHeight() / 16;
                    ownCustomTile.offsetCenterX = (ownCustomTile.tileW - 1) / 2;
                    ownCustomTile.offsetCenterY = (ownCustomTile.tileH - 1) / 2;
                    ownCustomTile.setTexture(texture);
                } else if (property.startsWith("n")) ownCustomTile.name = value;
                else if (property.startsWith("d")) ownCustomTile.desc = value;
                else if (property.startsWith("t")) ownCustomTile.terrain = Integer.parseInt(value);//TODO also accept strings
            }
            if (centerDisabled) {
                ownCustomTile.offsetCenterX = ownCustomTile.offsetCenterY = 0;
            }
            ownCustomTile.fileName = file.name();
            Tiles.addCustomTile(ownCustomTile);
        } catch (Exception ignored) {
            throw new RuntimeException(ignored);
        }
    }

    public static class OwnCustomTile extends CustomTilemap {

        private String desc, name;
        public String fileName;

        private void setTexture(Pixmap texture) {
            this.texture = texture;
        }

        @Override
        public String desc(int tileX, int tileY) {
            String ret;
            return desc == null ? super.desc(tileX, tileY) : ((ret = Messages.get(desc)).equals(Messages.NO_TEXT_FOUND) ? desc : ret);
        }

        @Override
        public String name(int tileX, int tileY) {
            String ret;
            return name == null ? super.name(tileX, tileY) : ((ret = Messages.get(name)).equals(Messages.NO_TEXT_FOUND) ? name : ret);
        }

        @Override
        public Tilemap create() {
            Tilemap v = super.create();
            int[] data = new int[tileW * tileH];
            for (int i = 0; i < data.length; i++) {
                data[i] = i;
            }
            v.map(data, tileW);
            return v;
        }

        private static final String FILE_NAME = "file_name";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(FILE_NAME, fileName);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            fileName = bundle.getString(FILE_NAME);
            OwnCustomTile template = Tiles.getCustomTile(fileName);
            if (template == null) fileName = null;
            else setValuesTo(template);
        }

        private void setValuesTo(OwnCustomTile other) {
            name = other.name;
            desc = other.desc;
            tileW = other.tileW;
            tileH = other.tileH;
            offsetCenterX = other.offsetCenterX;
            offsetCenterY = other.offsetCenterY;
            terrain = other.terrain;
            texture = other.getTexture();
        }

        @Override
        public CustomTilemap getCopy() {
            OwnCustomTile ret = (OwnCustomTile) super.getCopy();
            ret.setValuesTo(this);
            return ret;
        }
    }

}