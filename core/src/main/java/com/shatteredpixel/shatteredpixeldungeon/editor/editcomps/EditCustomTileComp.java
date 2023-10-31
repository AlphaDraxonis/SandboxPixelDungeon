package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.CustomTileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerTextIconModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomTileLoader;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class EditCustomTileComp extends EditTileComp {

    private final CustomTilemap customTile;

    private final Spinner terrain;

    private final Component[] comps;

    public EditCustomTileComp(CustomTilemap customTile, int cell) {
        super(new TileItem(customTile.terrain, cell));
        this.customTile = customTile;

        Object[] data = createTerrainDataForSpinner();
        int curIndex = 1;
        for (int i = 0; i < data.length; i++) {
            if (customTile.terrain == (int) data[i]) {
                curIndex = i;
                break;
            }
        }
        terrain = new Spinner(new SpinnerTextIconModel(true, curIndex, createTerrainDataForSpinner()) {
            @Override
            protected Image getIcon(Object value) {
                getObj().setTerrainType((Integer) value);
                return getObj().getSprite();
            }

            @Override
            protected String getAsString(Object value) {
                return TileItem.getName((int) value, -1) + " (" + value + ")";
            }

            @Override
            public int getClicksPerSecondWhileHolding() {
                return 14;
            }
        }, " " + Messages.get(EditCustomTileComp.class, "terrain") + ":", 8);
        terrain.addChangeListener(() -> getObj().setTerrainType(customTile.terrain = (int) terrain.getValue()));
        add(terrain);

        updateObj();

        comps = new Component[]{terrain};
    }

    private static Object[] createTerrainDataForSpinner(){
        return new Object[]{
                Terrain.CHASM,
                Terrain.EMPTY,
//                Terrain.EMPTY_DECO,
                Terrain.WATER,
                Terrain.GRASS,
                Terrain.HIGH_GRASS,
                Terrain.FURROWED_GRASS,
                Terrain.EMPTY_SP,
//                Terrain.PEDESTAL,
//                Terrain.EMBERS,

//                Terrain.TRAP,
//                Terrain.SECRET_TRAP,
                Terrain.INACTIVE_TRAP,

                Terrain.ENTRANCE,
                Terrain.EXIT,
                Terrain.EMPTY_WELL,
                Terrain.WELL,
                Terrain.ALCHEMY,
                Terrain.SIGN,
//                Terrain.SIGN_SP,

                Terrain.WALL,
                Terrain.WALL_DECO,
                Terrain.BOOKSHELF,
                Terrain.BARRICADE,
                Terrain.STATUE,
//                Terrain.STATUE_SP,
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

    public static boolean areEqual(CustomTilemap a, CustomTilemap b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        if (a.getClass() != b.getClass()) return false;
        if (a.tileX != b.tileX) return false;
        if (a.tileY != b.tileY) return false;
        return !(a instanceof CustomTileLoader.OwnCustomTile)
                || ((CustomTileLoader.OwnCustomTile) a).fileName.equals(((CustomTileLoader.OwnCustomTile) b).fileName);
    }
}