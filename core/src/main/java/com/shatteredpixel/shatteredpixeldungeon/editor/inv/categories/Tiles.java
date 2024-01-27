package com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories;

import static com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor.WndNewFloor.BUTTON_HEIGHT;
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
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.MINE_BOULDER;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.MINE_CRYSTAL;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.OPEN_DOOR;
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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Foliage;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SacrificialFire;
import com.shatteredpixel.shatteredpixeldungeon.editor.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditCustomTileComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.WndEditorInv;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.BarrierItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.BlobItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.CustomTileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.PermaGas;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.level.ChangeRegion;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.WndItemDistribution;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor.WndNewFloor;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StringInputComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomTileLoader;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.levels.CavesBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.CityBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.HallsBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.BlacksmithRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.MassGraveRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.RitualSiteRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.MagicalFireRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.WeakFloorRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTerrainTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTileSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import com.watabou.noosa.Image;

import java.util.HashMap;
import java.util.Map;

public enum Tiles {


    EMPTY(Terrain.EMPTY, EMPTY_DECO, WATER, GRASS, HIGH_GRASS, FURROWED_GRASS, EMPTY_SP, PEDESTAL, EMBERS),
    WALL(Terrain.WALL, WALL_DECO, BOOKSHELF, BARRICADE, STATUE, STATUE_SP, CHASM),
    DOOR(Terrain.DOOR, OPEN_DOOR, SECRET_DOOR, LOCKED_DOOR, CRYSTAL_DOOR, LOCKED_EXIT, UNLOCKED_EXIT),
    SPECIAL(ENTRANCE, EXIT, EMPTY_WELL, WELL, ALCHEMY, SIGN, MINE_CRYSTAL, MINE_BOULDER);

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


    //TODO Icon zeige in inv an, ob brennbar, oder Schlüssel?? -> muss über ListItem gemacht werden!


    public static final EditorItemBag bag = new EditorItemBag("name", 0) {
        @Override
        public Item findItem(Object src) {
            if (src instanceof Class<?>) {//for blobs, customTiles and barriers
                for (Item bag : items) {
                    for (Item i : ((Bag) bag).items) {
                        if (i instanceof BlobItem && ((BlobItem) i).getObject() == src) return i;
                        if (i instanceof CustomTileItem && ((CustomTileItem) i).getObject().getClass() == src) return i;
                        if (i instanceof BarrierItem && ((BarrierItem) i).getObject().getClass() == src) return i;
                    }
                }
                return null;
            }
            if (src instanceof String) {
                for (Item i : customTileBag.items) {
                    if (i instanceof CustomTileItem) {
                        CustomTilemap customTile = ((CustomTileItem) i).getObject();
                        if (customTile instanceof CustomTileLoader.UserCustomTile
                                && src.equals(((CustomTileLoader.UserCustomTile) customTile).identifier)) return i;
                    }
                }
                return null;
            }
            int val = (int) src;
            if (val == Terrain.CUSTOM_DECO) {
                if (EditorScene.customLevel().bossGroundVisuals instanceof CityBossLevel.CustomGroundVisuals)
                    return findItem(CityBossLevel.KingsThrone.class);
                val = Terrain.WALL;
            }
            if (val == TRAP || val == INACTIVE_TRAP || val == SECRET_TRAP) {
                if (EditorScene.customLevel().bossGroundVisuals instanceof CavesBossLevel.ArenaVisuals)
                    return findItem(CavesBossLevel.TrapTile.class);
                val = Terrain.EMPTY;
            }
            for (Item bag : items) {
                for (Item i : ((Bag) bag).items) {
                    if (i instanceof TileItem && ((TileItem) i).terrainType() == val) return i;
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
        Bag wallBag;
        bag.items.add(new TileBag("empty", EMPTY.terrains));
        bag.items.add(wallBag = new TileBag("wall", WALL.terrains));
        bag.items.add(new TileBag("door", DOOR.terrains));
        bag.items.add(new TileBag("other", SPECIAL.terrains));
        bag.items.add(customTileBag = new CustomTileBag());

        wallBag.items.add(2, new BarrierItem(new Barrier(-1)));

        bag.items.add(new BlobBag(
                PermaGas.PFire.class,
                PermaGas.PFreezing.class,
                MagicalFireRoom.EternalFire.class,
                SacrificialFire.class,
                PermaGas.PToxicGas.class,
                PermaGas.PCorrosiveGas.class,
                PermaGas.PConfusionGas.class,
                PermaGas.PParalyticGas.class,
                PermaGas.PStenchGas.class,
                PermaGas.PSmokeScreen.class,
                PermaGas.PElectricity.class,
                Foliage.class,
                PermaGas.PStormCloud.class));
    }

    private static final Map<String, CustomTileLoader.UserCustomTile> ownCustomTiles = new HashMap<>();

    public static CustomTileLoader.UserCustomTile getCustomTile(String fileName) {
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
        customTileBag.items.add(new CustomTileItem(new CavesBossLevel.TrapTile(), -1));
        customTileBag.items.add(new CustomTileItem(new CityBossLevel.KingsThrone(), -1));
//        customTileBag.items.add(new CustomTileItem(new SewerBossExitRoom.SewerExit(), -1));
//        customTileBag.items.add(new CustomTileItem(new SewerBossExitRoom.SewerExitOverhang(), -1));

        customTileBag.items.add(new CustomTileItem(new HallsBossLevel.CenterPieceVisuals(), -1));
        customTileBag.items.add(new CustomTileItem(new HallsBossLevel.BigPillarVisual(), -1));
    }

    public static void addCustomTile(CustomTileLoader.UserCustomTile customTile) {
        ownCustomTiles.put(customTile.identifier, customTile);
        customTileBag.items.add(new CustomTileItem(customTile, -1));
    }

    public static void removeCustomTile(CustomTileItem customTileItem) {
        ownCustomTiles.remove(((CustomTileLoader.UserCustomTile) customTileItem.getObject()).identifier);
        customTileBag.items.remove(customTileItem);
    }

    public static class AddSimpleCustomTileButton extends ScrollingListPane.ListItem {

        protected RedButton button;

        public AddSimpleCustomTileButton() {
            super(new Image(), "");
        }

        @Override
        protected void createChildren(Object... params) {
            super.createChildren(params);
            remove(icon);
            remove(label);
            hotArea.destroy();
            hotArea.killAndErase();
            hotArea.remove();
            button = new RedButton(Messages.get(WndCreateCustomTile.class, "title")) {

                @Override
                protected void onClick() {
                    EditorScene.show(new WndCreateCustomTile(null, Messages.get(WndCreateCustomTile.class, "title")));
                }

                @Override
                protected void onPointerDown() {
                    super.onPointerDown();
                }
            };
            add(button);
        }

        @Override
        protected void layout() {
            super.layout();

            button.setRect(x + (width - Math.max(width * 0.8f, button.reqWidth())) * 0.5f, y + Math.max(0, (height - button.reqHeight() - 2) * 0.5f),
                    Math.max(width * 0.8f, button.reqWidth()), Math.min(height, button.reqHeight() + 2));
            PixelScene.align(button);
        }
    }

    public static class WndCreateCustomTile extends Window {

        protected IconTitle title;
        protected RenderedTextBlock info;

        protected StringInputComp identifier, name, desc;
        protected Spinner imageTerrain, realTerrain, region;
        protected RedButton create, cancel;

        protected CustomTileLoader.SimpleCustomTile customTile;

        private boolean nameSet = false, descSet = false;

        public WndCreateCustomTile(CustomTileLoader.SimpleCustomTile customTile, String title) {
            this.customTile = customTile;

            this.title = new IconTitle(new Image(), title);
            add(this.title);

            if (customTile == null) {
                identifier = new StringInputComp(Messages.get(WndCreateCustomTile.class, "identifier_label"), null, 100, false, "???") {
                    @Override
                    protected void onChange() {
                        updateLayout();
                    }
                };
                identifier.setHighlightingEnabled(false);
                add(identifier);
            } else {
//                info = PixelScene.renderTextBlock(Messages.get(WndCreateCustomTile.class, "edit_info"),6);
//                add(info);
            }

            name = new StringInputComp(Messages.get(WndCreateCustomTile.class, "name_label"), null, 100, false, customTile == null ? "???" : customTile.name) {
                @Override
                protected void onChange() {
                    nameSet = true;
                    updateLayout();
                }
            };
            name.setHighlightingEnabled(false);
            add(name);

            desc = new StringInputComp(Messages.get(WndCreateCustomTile.class, "desc_label"), null, 500, true, customTile == null ? "???" : customTile.desc) {
                @Override
                protected void onChange() {
                    descSet = true;
                    updateLayout();
                }
            };
            desc.setHighlightingEnabled(false);
            add(desc);

            region = new Spinner(new SpinnerIntegerModel(LevelScheme.REGION_SEWERS, LevelScheme.REGION_HALLS,
                    customTile == null ? EditorScene.customLevel().getRegionValue() : customTile.region, 1, true, null) {
                @Override
                public String getDisplayString() {
                    if (imageTerrain != null) imageTerrain.setValue(imageTerrain.getValue());
                    if (realTerrain != null) realTerrain.setValue(realTerrain.getValue());
                    return Document.INTROS.pageTitle(ChangeRegion.REGION_KEYS[(int) getValue() - 1]);
                }

                @Override
                public float getInputFieldWidth(float height) {
                    return Spinner.FILL;
                }
            }, Messages.get(WndCreateCustomTile.class, "image_region"), 8);
            add(region);

            imageTerrain = EditCustomTileComp.createTerrainSpinner(customTile == null ? Terrain.EMPTY : customTile.imageTerrain, Messages.get(WndCreateCustomTile.class, "image_terrain"), value -> {
                String texture = CustomLevel.tilesTex((int) region.getValue(), (Integer) value == WATER);
                Image img = new Image(texture);
                if ((Integer) value == WATER) img.frame(0, 0, DungeonTerrainTilemap.SIZE, DungeonTerrainTilemap.SIZE);
                else img.frame(CustomLevel.getTextureFilm(texture).get(DungeonTerrainTilemap.tileSlot(-1, (Integer) value)));
                if (!nameSet) name.setText(Level.getFullMessageKey((int) region.getValue(), (int) value, false));
                if (!descSet) desc.setText(Level.getFullMessageKey((int) region.getValue(), (int) value, true));
                return img;
            });
            add(imageTerrain);

            realTerrain = EditCustomTileComp.createTerrainSpinner(customTile == null ? Terrain.EMPTY : customTile.terrain, Messages.get(WndCreateCustomTile.class, "real_terrain"), value -> {
                String texture = CustomLevel.tilesTex((int) region.getValue(), (Integer) value == WATER);
                Image img = new Image(texture);
                if ((Integer) value == WATER) img.frame(0, 0, DungeonTerrainTilemap.SIZE, DungeonTerrainTilemap.SIZE);
                else img.frame(CustomLevel.getTextureFilm(texture).get(DungeonTerrainTilemap.tileSlot(-1, (Integer) value)));
                return img;
            });
            add(realTerrain);

            create = new RedButton(customTile == null ? Messages.get(WndNewFloor.class, "create_label") : Messages.get(WndItemDistribution.class, "save")) {
                @Override
                protected void onClick() {
                    create(true);
                }
            };
            add(create);

            cancel = new RedButton(Messages.get(WndNewFloor.class, "cancel_label")) {
                @Override
                protected void onClick() {
                    create(false);
                }
            };
            add(cancel);

            width = PixelScene.landscape() ? 215 : Math.min(160, (int) (PixelScene.uiCamera.width * 0.9));
            updateLayout();
        }

        protected void updateLayout() {
            float posY;

            title.setRect(0, 0, width, -1);
            posY = title.bottom() + 4;

            if (identifier != null) {
                identifier.setRect(0, posY, width, -1);
                posY = identifier.bottom() + 2;
            }

            if (info != null) {
                info.setPos(0, posY);
                posY = info.bottom() + 3;
            }

            name.setRect(0, posY, width, -1);
            posY = name.bottom() + 2;

            desc.setRect(0, posY, width, -1);
            posY = desc.bottom() + 2;


            imageTerrain.setRect(0, posY, width, DungeonTerrainTilemap.SIZE + 4);
            posY = imageTerrain.bottom() + 2;

            region.setRect(0, posY, width, DungeonTerrainTilemap.SIZE + 4);
            posY = region.bottom() + 2;

            realTerrain.setRect(0, posY, width, DungeonTerrainTilemap.SIZE + 4);
            posY = realTerrain.bottom() + 4;

            create.setRect(1, posY, (width - 2) / 2, BUTTON_HEIGHT + 1);
            cancel.setRect(create.right() + 2, posY, (width - 2) / 2, BUTTON_HEIGHT + 1);
            posY += BUTTON_HEIGHT + 2;

            resize(width, (int) Math.ceil(posY));
        }

        protected void create(boolean positive) {
            if (positive) {
                String id = identifier == null ? "" : identifier.getText();
                String n = name.getText();
                String d = desc.getText();
                if (customTile == null && (id == null || id.trim().isEmpty() || ownCustomTiles.containsKey(id))
                        || n == null || n.trim().isEmpty()
                        || desc == null || d.trim().isEmpty()) {
                    EditorScene.show(new WndError(Messages.get(WndCreateCustomTile.class, "invalid_args")));
                    return;
                }

                boolean newCustomTile = customTile == null;

                if (newCustomTile) customTile = new CustomTileLoader.SimpleCustomTile((int) imageTerrain.getValue(), (int) region.getValue(), id);
                else {
                    customTile.imageTerrain = (int) imageTerrain.getValue();
                    customTile.region = (int) region.getValue();
                }
                customTile.terrain = (int) realTerrain.getValue();
                customTile.name = n;
                customTile.desc = d;
                if (newCustomTile) {
                    Tiles.addCustomTile(customTile);
                    Dungeon.customDungeon.customTiles.add(customTile);
                    WndEditorInv.updateCurrentTab();
                }
            }
            hide();
        }

        @Override
        public void onBackPressed() {
            if (customTile != null) super.onBackPressed();
        }
    }

}