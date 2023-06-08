package com.alphadraxonis.sandboxpixeldungeon.editor.inv.items;

import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.EXIT;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.LOCKED_EXIT;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.UNLOCKED_EXIT;

import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.DefaultEditComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.EditTileComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.DefaultListItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.EditorInventoryWindow;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.categories.Tiles;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.Undo;
import com.alphadraxonis.sandboxpixeldungeon.editor.util.EditorUtilies;
import com.alphadraxonis.sandboxpixeldungeon.levels.Level;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
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


    @Override
    public String name() {
        return getName(terrainType(), cell());
    }

    public int terrainType() {
        return terrainType;
    }

    public int cell() {
        return cell;
    }

    @Override
    public ScrollingListPane.ListItem createListItem(EditorInventoryWindow window) {
        Level level = EditorScene.customLevel();
        return new DefaultListItem(this, window, level.tileName(terrainType()), getSprite());
    }


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
        Undo.addActionPart(EditorScene.customLevel().setCell(cell, terrainType()));
    }


    public static boolean isExitTerrainCell(int terrain) {//why is this not in class Tiles? because it crashes the game for no reason!
        return terrain == EXIT || terrain == LOCKED_EXIT || terrain == UNLOCKED_EXIT;
    }

    public static String getName(int terrainType, int cell) {
        return Messages.titleCase(EditorScene.customLevel().tileName(terrainType)) + EditorUtilies.appendCellToString(cell);
    }

}