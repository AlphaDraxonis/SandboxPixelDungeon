package com.alphadraxonis.sandboxpixeldungeon.editor.scene.inv;

import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.levels.Level;
import com.alphadraxonis.sandboxpixeldungeon.levels.Terrain;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.tiles.DungeonTilemap;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndInfoCell;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndTitledMessage;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.Image;

public class WndInfoTile extends WndTitledMessage {


    public WndInfoTile(int terrainFeature, Level level,int image) {
        super(createImage(terrainFeature, level,image), Messages.titleCase(level.tileName(terrainFeature)), createDescription(terrainFeature, level));
    }


    public static String createDescription(int terrainFeature, Level level) {
        String desc = level.tileDesc(terrainFeature);
        return desc.length() == 0 ? Messages.get(WndInfoCell.class, "nothing") : desc;
    }

    private static Image createImage(int terrainFeature, Level level, int image) {
        if (terrainFeature == Terrain.WATER) {
            Image water = new Image(level.waterTex());
            water.frame(0, 0, DungeonTilemap.SIZE, DungeonTilemap.SIZE);
            return water;
        } else {
            Image img = new Image(TextureCache.get(level.tilesTex()));
            img.frame( EditorScene.customLevel().getTextureFilm().get( image ) );
            return img;
        }
    }
}