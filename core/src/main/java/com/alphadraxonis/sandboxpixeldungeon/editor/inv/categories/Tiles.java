package com.alphadraxonis.sandboxpixeldungeon.editor.inv.categories;

import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.ALCHEMY;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.BARRICADE;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.BOOKSHELF;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.CHASM;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.CRYSTAL_DOOR;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.EMBERS;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.EMPTY_DECO;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.EMPTY_SP;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.EMPTY_WELL;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.ENTRANCE;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.EXIT;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.FURROWED_GRASS;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.GRASS;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.HIGH_GRASS;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.INACTIVE_TRAP;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.LOCKED_DOOR;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.LOCKED_EXIT;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.PEDESTAL;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.SECRET_DOOR;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.SECRET_TRAP;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.SIGN;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.STATUE;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.STATUE_SP;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.TRAP;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.UNLOCKED_EXIT;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.WALL_DECO;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.WATER;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.WELL;

import com.alphadraxonis.sandboxpixeldungeon.editor.inv.items.TileItem;
import com.alphadraxonis.sandboxpixeldungeon.levels.Terrain;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSprite;
import com.alphadraxonis.sandboxpixeldungeon.tiles.DungeonTileSheet;
import com.watabou.noosa.Image;

public enum Tiles {


    EMPTY(Terrain.EMPTY, EMPTY_DECO, WATER, GRASS, HIGH_GRASS, FURROWED_GRASS, EMPTY_SP, PEDESTAL, EMBERS),
    WALL(Terrain.WALL, WALL_DECO, BOOKSHELF, BARRICADE, STATUE, STATUE_SP, CHASM),
    DOOR(Terrain.DOOR, SECRET_DOOR, LOCKED_DOOR, CRYSTAL_DOOR, LOCKED_EXIT, UNLOCKED_EXIT),
    SPECIAL(ENTRANCE, EXIT, EMPTY_WELL, WELL, ALCHEMY, SIGN);


    public final int[] terrains;

    Tiles(int... terrainCategory) {
        terrains = terrainCategory;
    }


    public static int getPlainImage(int terrainFeature) {
        switch (terrainFeature) {
            case CHASM:
                return DungeonTileSheet.CHASM_WALL;
            case SECRET_DOOR:
                return DungeonTileSheet.FLAT_DOOR_SECRET;
            case WATER:
                return WATER;
            case SECRET_TRAP:
            case TRAP:
            case INACTIVE_TRAP:
                return -1;
        }

        int value = DungeonTileSheet.directVisuals.get(terrainFeature, -1);
        if (value != -1) return value;
        return DungeonTileSheet.directFlatVisuals.get(terrainFeature, -1);
    }

    public static int getVisualWithAlts(int visual) {
        float random = (float) Math.random();
        if (random >= 0.95f && DungeonTileSheet.rareAltVisuals.containsKey(visual))
            return DungeonTileSheet.rareAltVisuals.get(visual);
        else if (random >= 0.5f && DungeonTileSheet.commonAltVisuals.containsKey(visual))
            return DungeonTileSheet.commonAltVisuals.get(visual);
        else
            return visual;
    }


    //TODO Icon zeige in inv an, ob brennbar, oder Schlüssel?? -> muss über ListItem gemacht werden!


    public static final EditorItemBag bag = new EditorItemBag(Messages.get(EditorItemBag.class,"tiles"),0){};

    public static class TileBag extends EditorItemBag {

        private final int[] terrains;
        public TileBag(String name,int... terrainCategory) {
            super(name,terrainCategory[0]);
            for (int i : terrainCategory) {
                items.add(new TileItem(i, -1));
            }
            this.terrains = terrainCategory;
        }
        @Override
        public Image getCategoryImage() {
            TileItem t = new TileItem(image, -1);
            t.randomizeTexture();
            return new ItemSprite(t);
        }
    }

    static {
        bag.items.add(new TileBag(Messages.get(Tiles.class,"empty"), EMPTY.terrains));
        bag.items.add(new TileBag(Messages.get(Tiles.class,"wall"), WALL.terrains));
        bag.items.add(new TileBag(Messages.get(Tiles.class,"door"), DOOR.terrains));
        bag.items.add(new TileBag(Messages.get(Tiles.class,"special"), SPECIAL.terrains));
    }

}