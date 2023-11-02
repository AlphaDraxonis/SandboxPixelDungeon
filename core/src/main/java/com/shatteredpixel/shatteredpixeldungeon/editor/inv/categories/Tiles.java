package com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories;

import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.ALCHEMY;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.BARRICADE;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.BOOKSHELF;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.CHASM;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.CRYSTAL_DOOR;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.EMBERS;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.EMPTY_DECO;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.EMPTY_SP;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.EMPTY_WELL;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.ENTRANCE;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.EXIT;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.FURROWED_GRASS;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.GRASS;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.HIGH_GRASS;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.INACTIVE_TRAP;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.LOCKED_DOOR;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.LOCKED_EXIT;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.PEDESTAL;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.SECRET_DOOR;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.SECRET_TRAP;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.SIGN;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.STATUE;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.STATUE_SP;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.TRAP;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.UNLOCKED_EXIT;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.WALL_DECO;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.WATER;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.WELL;

import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SacrificialFire;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.gases.PermaGas;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.BlobItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.CustomTileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomTileLoader;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.BlacksmithRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.MassGraveRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.RitualSiteRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.MagicalFireRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.WeakFloorRoom;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTileSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.watabou.noosa.Image;

import java.util.HashMap;
import java.util.Map;

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


    public static final EditorItemBag bag = new EditorItemBag("name", 0) {
        @Override
        public Item findItem(Object src) {
            if (src instanceof Class<?>) {//for blobs and customTiles
                for (Item bag : items) {
                    for (Item i : ((Bag) bag).items) {
                        if (i instanceof BlobItem && ((BlobItem) i).blob() == src) return i;
                        if (i instanceof CustomTileItem && ((CustomTileItem) i).customTile().getClass() == src) return i;
                    }
                }
                return null;
            }
            if (src instanceof String) {
                for (Item i : customTileBag.items) {
                    if (i instanceof CustomTileItem) {
                        CustomTilemap customTile = ((CustomTileItem) i).customTile();
                        if (customTile instanceof CustomTileLoader.OwnCustomTile
                                && src.equals(((CustomTileLoader.OwnCustomTile) customTile).fileName)) return i;
                    }
                }
                return null;
            }
            int val = (int) src;
            for (Item bag : items) {
                for (Item i : ((Bag) bag).items) {
                    if (((TileItem) i).terrainType() == val) return i;
                }
            }
            return null;
        }
    };

    public static class TileBag extends EditorItemBag {

        private final int[] terrains;

        public TileBag(String name, int... terrainCategory) {
            super(name, terrainCategory[0]);
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

    public static class BlobBag extends EditorItemBag {

        public BlobBag(Class<? extends Blob>... blobs) {
            super("name", -1);
            for (Class<? extends Blob> b : blobs) {
                items.add(new BlobItem(b));
            }
        }

        @Override
        public Image getCategoryImage() {
            Image icon = Icons.ETERNAL_FIRE.get();
            icon.scale.set(2.28f);// 16/7 = 2.28
            return icon;
        }
    }

    public static class CustomTileBag extends EditorItemBag {
        public CustomTileBag() {
            super("name", -1);
        }

        @Override
        public Image getCategoryImage() {
            return Icons.TALENT.get();
        }
    }

    private static CustomTileBag customTileBag;

    static {
        bag.items.add(new TileBag("empty", EMPTY.terrains));
        bag.items.add(new TileBag("wall", WALL.terrains));
        bag.items.add(new TileBag("door", DOOR.terrains));
        bag.items.add(new TileBag("other", SPECIAL.terrains));
        bag.items.add(customTileBag = new CustomTileBag());

        bag.items.add(new BlobBag(
                MagicalFireRoom.EternalFire.class,
                SacrificialFire.class,
                PermaGas.PToxicGas.class,
                PermaGas.PCorrosiveGas.class,
                PermaGas.PConfusionGas.class,
                PermaGas.PParalyticGas.class,
                PermaGas.PStenchGas.class));
    }

    private static final Map<String, CustomTileLoader.OwnCustomTile> ownCustomTiles = new HashMap<>();

    public static CustomTileLoader.OwnCustomTile getCustomTile(String fileName) {
        return ownCustomTiles.get(fileName);
    }

    public static void clearCustomTiles() {
        ownCustomTiles.clear();
        customTileBag.items.clear();
        customTileBag.items.add(new CustomTileItem(new MassGraveRoom.Bones(), -1));
        customTileBag.items.add(new CustomTileItem(new RitualSiteRoom.RitualMarker(), -1));
        customTileBag.items.add(new CustomTileItem(new BlacksmithRoom.QuestEntrance(), -1));
//        customTileBag.items.add(new CustomTileItem(new DemonSpawnerRoom.CustomFloor(), -1));
//        customTileBag.items.add(new CustomTileItem(new LastLevel.CustomFloor(), -1));
        customTileBag.items.add(new CustomTileItem(new WeakFloorRoom.HiddenWell(), -1));
//        customTileBag.items.add(new CustomTileItem(new SewerBossExitRoom.SewerExit(), -1));
//        customTileBag.items.add(new CustomTileItem(new SewerBossExitRoom.SewerExitOverhang(), -1));
    }

    public static void addCustomTile(CustomTileLoader.OwnCustomTile customTile) {
        ownCustomTiles.put(customTile.fileName, customTile);
        customTileBag.items.add(new CustomTileItem(customTile, -1));
    }
}