package com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Foliage;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SacrificialFire;
import com.shatteredpixel.shatteredpixeldungeon.editor.ArrowCell;
import com.shatteredpixel.shatteredpixeldungeon.editor.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.editor.Checkpoint;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.TileSprite;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditCustomTileComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditParticleComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.FindInBag;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.WndEditorInv;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.ArrowCellItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.BarrierItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.BlobItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.CheckpointItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.CustomTileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.ParticleItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.CustomParticle;
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
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
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
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.sewerboss.GooBossRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.DemonSpawnerRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.MagicalFireRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.WeakFloorRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTerrainTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTileSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import com.watabou.noosa.Image;

import static com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor.WndNewFloor.BUTTON_HEIGHT;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.*;

public enum Tiles {


    EMPTY(Terrain.EMPTY, EMPTY_DECO, WATER, GRASS, HIGH_GRASS, FURROWED_GRASS, EMPTY_SP, PEDESTAL, EMBERS),
    WALL(Terrain.WALL, WALL_DECO, BOOKSHELF, BARRICADE, STATUE, STATUE_SP, CHASM),
    DOOR(Terrain.DOOR, OPEN_DOOR, LOCKED_DOOR, CRYSTAL_DOOR, SECRET_DOOR, SECRET_LOCKED_DOOR, SECRET_CRYSTAL_DOOR, COIN_DOOR, LOCKED_EXIT, UNLOCKED_EXIT, MIMIC_DOOR),
    SPECIAL(ENTRANCE, ENTRANCE_SP, EXIT, EMPTY_WELL, WELL, ALCHEMY, SIGN, MINE_CRYSTAL, MINE_BOULDER);

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
            case SECRET_LOCKED_DOOR:
                return DungeonTileSheet.FLAT_LOCKED_DOOR_SECRET;
            case SECRET_CRYSTAL_DOOR:
                return DungeonTileSheet.FLAT_CRYSTAL_DOOR_SECRET;
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


    public static final EditorItemBag bag = new EditorItemBag() {
        @Override
        public Item findItem(FindInBag src) {

            Item result = super.findItem(src);
            if (result != null) return result;
            
            FindInBag.Type type = src.getType();
            Object value = src.getValue();
            if (type == FindInBag.Type.CUSTOM_TILE || type == FindInBag.Type.SIMPLE_CUSTOM_TILE) {
                for (Item i : customTileBag.items) {
                    if (i instanceof CustomTileItem) {
                        CustomTilemap customTile = ((CustomTileItem) i).getObject();
                        if (customTile instanceof CustomTileLoader.UserCustomTile
                                && value.equals(((CustomTileLoader.UserCustomTile) customTile).getIdentifier())) return i;
                    }
                }
                return null;
            }
            if (type == FindInBag.Type.PARTICLE) {
                int id;
                if (value instanceof CustomParticle.ParticleProperty) id = ((CustomParticle.ParticleProperty) value).particleID();
                else id = (int) value;
                for (Item i : particleBag.items) {
                    if (i instanceof ParticleItem && ((ParticleItem) i).getObject().particleID() == id) return i;
                }
                return null;
            }
            if (type == FindInBag.Type.TILE) {
                int val = (int) value;
                if (val == Terrain.CUSTOM_DECO_EMPTY) {
                    val = EMPTY_DECO;
                }
                if (EditorScene.getCustomLevel() != null) {
                    if (val == Terrain.CUSTOM_DECO) {
                        if (EditorScene.getCustomLevel().bossGroundVisuals instanceof CityBossLevel.CustomGroundVisuals)
                            return findItem(new FindInBag(FindInBag.Type.CLASS, CityBossLevel.KingsThrone.class, null));
                        val = Terrain.WALL;
                    }
                    if (val == TRAP || val == INACTIVE_TRAP || val == SECRET_TRAP) {
                        if (EditorScene.getCustomLevel().bossGroundVisuals instanceof CavesBossLevel.ArenaVisuals)
                            return findItem(new FindInBag(FindInBag.Type.CLASS, CavesBossLevel.TrapTile.class, null));
                        val = Terrain.EMPTY;
                    }
                }
                for (Item bag : items) {
                    for (Item i : ((Bag) bag).items) {
                        if (i instanceof TileItem && ((TileItem) i).terrainType() == val) return i;
                    }
                }
                return null;
            }

            return null;
        }
    };

    public static class TileBag extends EditorItemBag {

        public TileBag(String name, int... terrainCategory) {
            super(name, terrainCategory[0]);
            for (int i : terrainCategory) {
                items.add(new TileItem(i, -1));
            }
        }

        @Override
        public Image getCategoryImage() {
            return new TileSprite(image);
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

    public static class ParticleBag extends EditorItemBag {
        public ParticleBag() {
            super("name", -1);
        }

        @Override
        public Image getCategoryImage() {
            Speck icon = new Speck();
            icon.image(Speck.LIGHT);
            icon.origin.set(0, 0);
            icon.scale.set(1.8f);
            return icon;
        }
    }

    private static CustomTileBag customTileBag;
    public static ParticleBag particleBag;

    static {
        Bag wallBag;
        bag.items.add(new TileBag("empty", EMPTY.terrains) {{items.add(new ArrowCellItem(new ArrowCell(-1)));}});
        bag.items.add(wallBag = new TileBag("wall", WALL.terrains));
        bag.items.add(new TileBag("door", DOOR.terrains));
        bag.items.add(new TileBag("other", SPECIAL.terrains) {{items.add(new CheckpointItem(new Checkpoint()));}});
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
                PermaGas.PStormCloud.class,
                PermaGas.PElectricity.class,
                Foliage.class));

        bag.items.add(particleBag = new ParticleBag());
    }


    public static CustomTileLoader.OwnCustomTile getCustomTile(String fileName) {
        return CustomTileLoader.OwnCustomTile.ownCustomTiles.get(fileName);
    }
    
    public static CustomTileLoader.SimpleCustomTile getCustomTile(int id) {
        return CustomTileLoader.SimpleCustomTile.simpleCustomTiles.get(id);
    }

    public static void clearCustomTiles() {
        CustomTileLoader.OwnCustomTile.ownCustomTiles.clear();
        CustomTileLoader.SimpleCustomTile.simpleCustomTiles.clear();
        customTileBag.items.clear();
        customTileBag.items.add(new CustomTileItem(new MassGraveRoom.Bones(), -1));
        customTileBag.items.add(new CustomTileItem(new RitualSiteRoom.RitualMarker(), -1));
        customTileBag.items.add(new CustomTileItem(new HallsBossLevel.CandleTile(), -1));
        customTileBag.items.add(new CustomTileItem(new BlacksmithRoom.QuestEntrance(), -1));
//        customTileBag.items.add(new CustomTileItem(new DemonSpawnerRoom.CustomFloor(), -1));
//        customTileBag.items.add(new CustomTileItem(new LastLevel.CustomFloor(), -1));
        customTileBag.items.add(new CustomTileItem(new WeakFloorRoom.HiddenWell(), -1));
        customTileBag.items.add(new CustomTileItem(new GooBossRoom.GooNest44(), -1));
        customTileBag.items.add(new CustomTileItem(new GooBossRoom.GooNest45(), -1));
        customTileBag.items.add(new CustomTileItem(new GooBossRoom.GooNest54(), -1));
        customTileBag.items.add(new CustomTileItem(new GooBossRoom.GooNest55(), -1));
        customTileBag.items.add(new CustomTileItem(new CavesBossLevel.TrapTile(), -1));
        customTileBag.items.add(new CustomTileItem(new CavesBossLevel.MetalGate(), -1));
        customTileBag.items.add(new CustomTileItem(new CityBossLevel.KingsThrone(), -1));
//        customTileBag.items.add(new CustomTileItem(new SewerBossExitRoom.SewerExit(), -1));
//        customTileBag.items.add(new CustomTileItem(new SewerBossExitRoom.SewerExitOverhang(), -1));
        customTileBag.items.add(new CustomTileItem(new DemonSpawnerRoom.NoDemonSpawnerFloor(), -1));
        customTileBag.items.add(new CustomTileItem(new DemonSpawnerRoom.DemonSpawnerFloor(), -1));

        customTileBag.items.add(new CustomTileItem(new HallsBossLevel.CenterPieceVisuals(), -1));
        customTileBag.items.add(new CustomTileItem(new HallsBossLevel.BigPillarVisual(), -1));
    }

    public static void updateParticlesInInv() {
        particleBag.clear();
        for (CustomParticle.ParticleProperty particle : Dungeon.customDungeon.particles.values()) {
            particleBag.items.add(new ParticleItem(particle));
        }
    }

    public static void addCustomTile(CustomTileLoader.UserCustomTile customTile) {
        customTile.addIntoStaticMap();
        customTileBag.items.add(new CustomTileItem(customTile, -1));
    }

    public static void removeCustomTile(CustomTileLoader.UserCustomTile customTile) {
        customTile.removeFromStaticMap();
        Item toRemove = null;
        for (Item i : customTileBag.items) {
            if (i instanceof CustomTileItem && ((CustomTileItem) i).getObject() == customTile) {
                toRemove = i;
                break;
            }
        }
        if (toRemove != null) customTileBag.items.remove(toRemove);
    }

    public static class AddSimpleCustomTileButton extends ScrollingListPane.ListButton {
        protected RedButton createButton() {
            return new RedButton(Messages.get(WndCreateCustomTile.class, "title")) {
                @Override
                protected void onClick() {
                    EditorScene.show(new WndCreateCustomTile(null, Messages.get(WndCreateCustomTile.class, "title")));
                }
            };
        }
    }

    public static class AddParticleButton extends ScrollingListPane.ListButton {
        protected RedButton createButton() {
            return new RedButton(Messages.get(EditParticleComp.WndNewParticle.class, "title")) {
                @Override
                protected void onClick() {
                    EditorScene.show(new EditParticleComp.WndNewParticle());
                }
            };
        }
    }

    public static class WndCreateCustomTile extends Window {

        protected IconTitle title;

        protected StringInputComp name, desc;
        protected Spinner imageTerrain, realTerrain, region;
        protected RedButton create, cancel;

        protected CustomTileLoader.SimpleCustomTile customTile;

        private boolean nameSet = false, descSet = false;

        public WndCreateCustomTile(CustomTileLoader.SimpleCustomTile customTile, String title) {
            this.customTile = customTile;

            this.title = new IconTitle(new Image(), title);
            add(this.title);

            name = new StringInputComp(Messages.get(WndCreateCustomTile.class, "name_label"), null, 100, false, customTile == null ? "???" : customTile.name) {
                @Override
                protected void onChange() {
                    nameSet = true;
                    updateLayout();
                }
            };
            name.setHighlightingEnabled(false);
            add(name);
            nameSet = customTile != null && !Level.getFullMessageKey(customTile.region, customTile.imageTerrain, false).equals(customTile.name);

            desc = new StringInputComp(Messages.get(WndCreateCustomTile.class, "desc_label"), null, 500, true, customTile == null ? "???" : customTile.desc) {
                @Override
                protected void onChange() {
                    descSet = true;
                    updateLayout();
                }
            };
            desc.setHighlightingEnabled(false);
            add(desc);
            descSet = customTile != null && !Level.getFullMessageKey(customTile.region, customTile.imageTerrain, true).equals(customTile.desc);

            region = new Spinner(new SpinnerIntegerModel(LevelScheme.REGION_SEWERS, LevelScheme.REGION_HALLS,
                    customTile == null ? Dungeon.level.levelScheme.getVisualRegion() : customTile.region, true) {
                @Override
                protected String displayString(Object value) {
                    if (imageTerrain != null) imageTerrain.setValue(imageTerrain.getValue());
                    if (realTerrain != null) realTerrain.setValue(realTerrain.getValue());
                    return Document.INTROS.pageTitle(ChangeRegion.REGION_KEYS[(int) value - 1]);
                }

                @Override
                public float getInputFieldWidth(float height) {
                    return Spinner.FILL;
                }

                @Override
                public void displayInputAnyNumberDialog() {
                    //do nothing
                }
            }, Messages.get(WndCreateCustomTile.class, "image_region"), 8);
            add(region);

            imageTerrain = EditCustomTileComp.createTerrainSpinner(customTile == null ? Terrain.EMPTY : customTile.imageTerrain, Messages.get(WndCreateCustomTile.class, "image_terrain"), value -> {
                String texture = CustomLevel.tilesTex((int) region.getValue(), (Integer) value == WATER);
                Image img = new Image(texture);
                if ((Integer) value == WATER) img.frame(0, 0, DungeonTerrainTilemap.SIZE, DungeonTerrainTilemap.SIZE);
                else img.frame(CustomLevel.getTextureFilm(texture).get(DungeonTerrainTilemap.tileSlot(-1, (Integer) value, 0)));
                if (!nameSet) name.setText(Level.getFullMessageKey((int) region.getValue(), (int) value, false));
                if (!descSet) desc.setText(Level.getFullMessageKey((int) region.getValue(), (int) value, true));
                return img;
            });
            add(imageTerrain);

            realTerrain = EditCustomTileComp.createTerrainSpinner(customTile == null ? Terrain.EMPTY : customTile.terrain, Messages.get(WndCreateCustomTile.class, "real_terrain"), value -> {
                String texture = CustomLevel.tilesTex((int) region.getValue(), (Integer) value == WATER);
                Image img = new Image(texture);
                if ((Integer) value == WATER) img.frame(0, 0, DungeonTerrainTilemap.SIZE, DungeonTerrainTilemap.SIZE);
                else img.frame(CustomLevel.getTextureFilm(texture).get(DungeonTerrainTilemap.tileSlot(-1, (Integer) value, 0)));
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

            width = WindowSize.WIDTH_LARGE_M.get();
            updateLayout();
        }

        protected void updateLayout() {
            float posY;

            title.setRect(0, 0, width, -1);
            posY = title.bottom() + 4;

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

            create.setRect(1, posY, (width - 2) / 2f, BUTTON_HEIGHT + 1);
            cancel.setRect(create.right() + 2, posY, (width - 2) / 2f, BUTTON_HEIGHT + 1);
            posY += BUTTON_HEIGHT + 2;

            resize(width, (int) Math.ceil(posY));
        }

        protected void create(boolean positive) {
            if (positive) {
                
                int id = 1;
                while (CustomTileLoader.SimpleCustomTile.simpleCustomTiles.containsKey(id)) {
                    id++;
                }
                
                String n = name.getText();
                String d = desc.getText();
                if (   n == null || n.trim().isEmpty()
					|| d == null || d.trim().isEmpty()) {
                    DungeonScene.show( new WndError(Messages.get(WndCreateCustomTile.class, "invalid_args")) );
                    return;
                }

                boolean newCustomTile = customTile == null;

                if (newCustomTile) {
                    customTile = new CustomTileLoader.SimpleCustomTile((int) imageTerrain.getValue(), (int) region.getValue(), id);
                }
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
