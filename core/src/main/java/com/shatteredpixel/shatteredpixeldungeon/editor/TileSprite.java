package com.shatteredpixel.shatteredpixeldungeon.editor;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Tiles;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;

//Renders the terrain as flat image
public class TileSprite extends Image {

    private int terrain;
    private int explicitImage = -1;
    private String explicitTexture;
    private TileItem tileItem;

    public TileSprite(int terrain) {
        super(terrain == Terrain.WATER ? Dungeon.level.waterTex() : Dungeon.level.tilesTex());
        this.terrain = terrain;
        view(terrain);
    }

    public TileSprite(String explicitTexture, int terrain) {
        super(explicitTexture);
        this.explicitTexture = explicitTexture;
        this.terrain = terrain;
        view(terrain);
    }

    public static TileSprite createTilespriteWithImage(String explicitTexture, int image) {
        TileSprite sprite = new TileSprite(explicitTexture, 0);
        sprite.explicitImage = image;
        sprite.view(0);
        return sprite;
    }

    public TileSprite(TileItem tileItem) {
        super(tileItem.terrainType() == Terrain.WATER ? Dungeon.level.waterTex() : Dungeon.level.tilesTex());
        this.terrain = tileItem.terrainType();
        this.tileItem = tileItem;
        view(terrain);
    }

    public TileSprite view(int terrain) {

        if ((terrain == Terrain.WATER) != (this.terrain == Terrain.WATER)) {
            if (explicitTexture == null) {
                if (terrain == Terrain.WATER) texture(Dungeon.level.waterTex());
                else texture(Dungeon.level.tilesTex());
            }
        }
        this.terrain = terrain;

        int img = getImage();
        if (img == -1) frame(5);
        else frame(getImage());

        return this;
    }

    public void frame(int image) {

        if (terrain == Terrain.WATER) {
            frame(0, 0, DungeonTilemap.SIZE, DungeonTilemap.SIZE);
            return;
        }

        TextureFilm film = CustomLevel.getTextureFilm(explicitTexture == null ? Dungeon.level.tilesTex() : explicitTexture);

        frame(film.get(image));
    }

    private int getImage() {
        if (explicitImage != -1) return explicitImage;
        if (tileItem != null) return tileItem.image();
        return Tiles.getPlainImage(terrain);
    }
}