package com.shatteredpixel.shatteredpixeldungeon.editor.scene.inv;

import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoCell;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
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