package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.DefaultListItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Tiles;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.CustomTileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.CustomTerrain;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerTextIconModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomTileLoader;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.Function;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class EditCustomTileComp extends EditTileComp {

    private final CustomTilemap customTile;

    private Spinner terrain;
    private RedButton editSimpleCustomTile;

    private final Component[] comps;

    public EditCustomTileComp(CustomTilemap customTile, int cell) {
        super(new TileItem(customTile.terrain, cell));
        this.customTile = customTile;

        if (customTile instanceof CustomTileLoader.SimpleCustomTile && cell == -1) {
            terrain = null;
            editSimpleCustomTile = new RedButton(Messages.get(DefaultListItem.class, "edit")){
                @Override
                protected void onClick() {
                    EditorScene.show(new Tiles.WndCreateCustomTile((CustomTileLoader.SimpleCustomTile) customTile,
                            Messages.get(DefaultListItem.class, "edit")){
                        @Override
                        public void hide() {
                            super.hide();
                            updateObj();
                            EditorScene.revalidateCustomTiles();
                        }
                    });
                }
            };
            add(editSimpleCustomTile);
        } else {
            if (!(customTile instanceof CustomTerrain)) {
                terrain = createTerrainSpinner(customTile.terrain, " " + Messages.get(this, "terrain") + ":", value -> {
                    getObj().setTerrainType((Integer) value);
                    return getObj().getSprite();
                });
                terrain.addChangeListener(() -> getObj().setTerrainType(customTile.terrain = (int) terrain.getValue()));
                add(terrain);
            }
        }

        updateObj();

        comps = new Component[]{terrain, editSimpleCustomTile};
    }

    public static Spinner createTerrainSpinner(int currentTerrain, String label, Function<Object, Image> getIcon){
        Object[] data = createTerrainDataForSpinner();
        int curIndex = 1;
        for (int i = 0; i < data.length; i++) {
            if (currentTerrain == (int) data[i]) {
                curIndex = i;
                break;
            }
        }
        return new Spinner(new SpinnerTextIconModel(true, curIndex, createTerrainDataForSpinner()) {
            @Override
            protected Image getIcon(Object value) {
                return getIcon.apply(value);
            }

            @Override
            protected String getAsString(Object value) {
                return TileItem.getName((int) value, -1);
            }

            @Override
            public int getClicksPerSecondWhileHolding() {
                return 14;
            }
        }, label, 8);
    }

    private static Object[] createTerrainDataForSpinner(){
        return new Object[]{
                Terrain.CHASM,
                Terrain.EMPTY,
                Terrain.EMPTY_DECO,
                Terrain.WATER,
                Terrain.GRASS,
                Terrain.HIGH_GRASS,
                Terrain.FURROWED_GRASS,
                Terrain.EMPTY_SP,
                Terrain.PEDESTAL,
                Terrain.EMBERS,

//                Terrain.TRAP,
//                Terrain.SECRET_TRAP,
                Terrain.INACTIVE_TRAP,

                Terrain.ENTRANCE,
                Terrain.ENTRANCE_SP,
                Terrain.EXIT,
                Terrain.LOCKED_EXIT,
                Terrain.UNLOCKED_EXIT,
                Terrain.EMPTY_WELL,
                Terrain.WELL,
                Terrain.ALCHEMY,
                Terrain.SIGN,
//                Terrain.SIGN_SP,
                Terrain.MINE_CRYSTAL,
                Terrain.MINE_BOULDER,

                Terrain.WALL,
                Terrain.WALL_DECO,
                Terrain.BOOKSHELF,
                Terrain.BARRICADE,

                Terrain.DOOR,
                Terrain.OPEN_DOOR,
                Terrain.SECRET_DOOR,
                Terrain.LOCKED_DOOR,
                Terrain.SECRET_CRYSTAL_DOOR,
                Terrain.COIN_DOOR,
                Terrain.MIMIC_DOOR,

                Terrain.STATUE,
                Terrain.STATUE_SP,
        };
    }

    @Override
    protected void layout() {
        super.layout();
        layoutCompsLinear(comps);
    }

    @Override
    protected CustomTilemapAndPosWrapper findCustomTile() {
        return customTile == null ? null : new CustomTilemapAndPosWrapper(0, 0, customTile);
    }

    @Override
    public Image getIcon() {
        return CustomTileItem.createImage(customTile);
    }

    @Override
    protected String createTitleText() {
        return CustomTileItem.getName(customTile, obj.cell());
    }

    @Override
    protected Component createTitle() {
        if (customTile == null) return new IconTitle();
        return super.createTitle();
    }

    @Override
    protected void updateObj() {
        if (customTile instanceof CustomTileLoader.SimpleCustomTile) {
            ((CustomTileLoader.SimpleCustomTile) customTile).updateTexture();
            customTile.create();
            obj.setTerrainType(customTile.terrain);
        }
        super.updateObj();
    }

    public static boolean areEqual(CustomTilemap a, CustomTilemap b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        if (a.getClass() != b.getClass()) return false;
        if (a.tileX != b.tileX) return false;
        if (a.tileY != b.tileY) return false;
//        if (a.terrain != b.terrain) return false;
//        if (a instanceof CustomTileLoader.SimpleCustomTile) {
//            if (((CustomTileLoader.SimpleCustomTile) a).region != ((CustomTileLoader.SimpleCustomTile) b).region) return false;
//            if (((CustomTileLoader.SimpleCustomTile) a).imageTerrain != ((CustomTileLoader.SimpleCustomTile) b).imageTerrain) return false;
//        }
        return !(a instanceof CustomTileLoader.UserCustomTile)
                || ((CustomTileLoader.UserCustomTile) a).identifier.equals(((CustomTileLoader.UserCustomTile) b).identifier);
    }
}