package com.alphadraxonis.sandboxpixeldungeon.editor.scene.inv;

import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.EXIT;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.LOCKED_EXIT;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.UNLOCKED_EXIT;

import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.editcomps.DefaultEditComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.editcomps.EditTileComp;
import com.alphadraxonis.sandboxpixeldungeon.levels.Level;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSprite;
import com.alphadraxonis.sandboxpixeldungeon.ui.ScrollingListPane;
import com.watabou.noosa.Image;

public class TileItem extends EditorItem {


    private final int terrainType, cell;


    public TileItem(int terrainFeature, int cell) {
        this.terrainType = terrainFeature;
        this.cell = cell;
        randomizeTexture();
    }

    @Override
    public void randomizeTexture() {
        image = Tiles.getVisualWithAlts(Tiles.getPlainImage(terrainType));
    }

    public int cell() {
        return cell;
    }

    @Override
    public ScrollingListPane.ListItem createListItem(EditorInventoryWindow window) {
        Level level = EditorScene.customLevel();
        return new DefaultListItem(this, window, level.tileName(terrainType()), getSprite());
    }
//    @Override
//    public ChooseOneInCategoriesBody.BtnRow createBtnRow(WndEditorItemsBag window) {
//        Level level = EditorScene.floor();
//        return new DefaultBtnRow(this, window, level.tileName(terrainType()),
//                WndInfoTile.createDescription(terrainType(), level),
//                image() < 0 ? null : new ItemSprite(this));
//    }


    @Override
    public DefaultEditComp<?> createEditComponent() {
        return new EditTileComp(this);
    }

    @Override
    public Image getSprite() {
        return image() < 0 ? new Image() : new ItemSprite(this);
    }

    @Override
    public void place(int cell) {
        EditorScene.customLevel().setCell(cell,terrainType());
    }

    public int terrainType() {
        return terrainType;
    }




    public static boolean isExitTerrainCell(int terrain) {//why is this not in class Tiles? because it crashes the game for no reason!
        return terrain == EXIT || terrain == LOCKED_EXIT || terrain == UNLOCKED_EXIT;
    }

}