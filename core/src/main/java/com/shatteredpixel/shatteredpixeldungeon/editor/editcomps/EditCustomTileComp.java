package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.CustomTileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomTileLoader;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class EditCustomTileComp extends EditTileComp {

    private final CustomTilemap customTile;

    public EditCustomTileComp(CustomTilemap customTile, int cell) {
        super(new TileItem(customTile.terrain, cell));
        this.customTile = customTile;
        updateObj();
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