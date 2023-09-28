package com.alphadraxonis.sandboxpixeldungeon.editor.inv.items;

import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.DefaultEditComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.DefaultListItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.EditorInventoryWindow;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.ActionPart;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.ActionPartList;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.Undo;
import com.alphadraxonis.sandboxpixeldungeon.editor.util.EditorUtilies;
import com.alphadraxonis.sandboxpixeldungeon.levels.Level;
import com.alphadraxonis.sandboxpixeldungeon.levels.Terrain;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSprite;
import com.alphadraxonis.sandboxpixeldungeon.tiles.CustomTilemap;
import com.alphadraxonis.sandboxpixeldungeon.ui.ScrollingListPane;
import com.watabou.noosa.Image;
import com.watabou.utils.Point;

import java.util.Set;

public class CustomTileItem extends EditorItem {


    private final int cell;
    private final CustomTilemap customTile;
    private final boolean wall;


    public CustomTileItem(CustomTilemap customTile, boolean wall, int cell) {
        this.cell = cell;
        this.customTile = customTile;
        this.wall = wall;
    }

//    public CustomTileItem(int terrainFeature, int image, int cell) {
//        this.terrainType = terrainFeature;
//        this.cell = cell;
//        this.image = image;
//    }

    @Override
    public String name() {
        return "TESTNAME";
    }

    @Override
    public Object getObject() {
        return customTile();
    }

    public CustomTilemap customTile() {
        return customTile;
    }

    public int cell() {
        return cell;
    }

    public boolean wall() {
        return wall;
    }

    @Override
    public ScrollingListPane.ListItem createListItem(EditorInventoryWindow window) {
        Level level = EditorScene.customLevel();
        return new DefaultListItem(this, window, "TTN", getSprite());
    }


    @Override
    public DefaultEditComp<?> createEditComponent() {
        return null;
//        return new EditTileComp(this);
    }

    @Override
    public Image getSprite() {
        return new ItemSprite();
//        if (image() == Terrain.WATER)
//            return new ItemSprite(EditorScene.customLevel().waterTex(), this);
//        return image() < 0 ? new Image() : new ItemSprite(this);
    }

    @Override
    public void place(int cell) {
        if (isPositionValid(cell, customTile()))
            Undo.addActionPart(place(cell, wall(), customTile().getCopy()));
    }

    public static ActionPart place(int cell, boolean wall, CustomTilemap customTile) {
        return new CustomTileItem.Place(cell, wall, customTile);
    }

    public static ActionPart remove(int cell) {
        Point p = Dungeon.level.cellToPoint(cell);
        CustomTilemap cust = findCustomTileAt(p, Dungeon.level.customTiles);
        boolean wall = cust == null;
        if (wall) cust = findCustomTileAt(p, Dungeon.level.customWalls);
        if (cust != null) return new CustomTileItem.Remove(cell, wall, cust);
        return null;
    }

    public static boolean isPositionValid(int cell, CustomTilemap customTile) {
        int x = cell % Dungeon.level.width();
        int y = cell / Dungeon.level.width();
        return x >= 0 && x + customTile.tileW <= Dungeon.level.width()
                && y >= 0 && y + customTile.tileH <= Dungeon.level.height();

    }

    public static String getName(int terrainType, int cell) {
        return Messages.titleCase(EditorScene.customLevel().tileName(terrainType)) + EditorUtilies.appendCellToString(cell);
    }

    public static CustomTilemap findCustomTileAt(int cell) {
        Point p = Dungeon.level.cellToPoint(cell);
        CustomTilemap cust = findCustomTileAt(p, Dungeon.level.customTiles);
        return cust == null ? findCustomTileAt(p, Dungeon.level.customWalls) : cust;
    }

    private static CustomTilemap findCustomTileAt(Point p, Set<CustomTilemap> customTiles) {
        for (CustomTilemap cust : customTiles) {
            Point custPoint = new Point(p);
            custPoint.x -= cust.tileX;
            custPoint.y -= cust.tileY;
            if (custPoint.x >= 0 && custPoint.y >= 0
                    && custPoint.x < cust.tileW && custPoint.y < cust.tileH) {
                if (cust.image(custPoint.x, custPoint.y) != null) {
                    return cust;
                }
            }
        }
        return null;
    }

    public static void removeCustomTilesAt(int cell, Level level) {
        Point p = level.cellToPoint(cell);
        CustomTilemap cust = findCustomTileAt(p, level.customTiles);
        if (cust != null) {
            level.customTiles.remove(cust);
            EditorScene.remove(cust, false);
        }
        cust = findCustomTileAt(p, level.customWalls);
        if (cust != null) {
            level.customWalls.remove(cust);
            EditorScene.remove(cust, true);
        }
    }

    public static class PlaceCustomTileActionPart extends TileItem.PlaceTileActionPart {

        protected final CustomTilemap customTile;
        protected final boolean wall;

        protected PlaceCustomTileActionPart(int cell, boolean wall, CustomTilemap customTile) {
            super(cell, wall ? Terrain.WALL : Terrain.EMPTY, true);
            this.customTile = customTile;
            this.wall = wall;
        }

        public static void place(CustomTilemap customTile, int cell, boolean wall) {
            customTile.setRect(cell % Dungeon.level.width(), cell / Dungeon.level.width(),
                    customTile.tileW, customTile.tileH);
            Dungeon.level.customTiles.add(customTile);
            EditorScene.add(customTile, wall);
        }

        public static void remove(CustomTilemap customTile, boolean wall) {
            if (wall) Dungeon.level.customWalls.remove(customTile);
            else Dungeon.level.customTiles.remove(customTile);
            EditorScene.remove(customTile, wall);
        }
    }

    public static class Place extends PlaceCustomTileActionPart {

        private ActionPartList otherTerrainChanges;//for customTiles larger than 1x1

        public Place(int cell, boolean wall, CustomTilemap customTile) {
            super(cell, wall, customTile);
            if (customTile.tileW > 1 || customTile.tileH > 1) {
                otherTerrainChanges = new ActionPartList();
                int startPos = cell - customTile.offsetCenterX - customTile.offsetCenterY * Dungeon.level.width();
                for (int i = 0; i < customTile.tileH; i++) {
                    for (int j = 0; j < customTile.tileW; j++) {
                        int pos = startPos + j + i * Dungeon.level.width();
                        if (pos != cell) otherTerrainChanges.addActionPart(TileItem.place(pos, newTerrain()));
                    }
                }
                otherTerrainChanges.redo();
            }
            place(customTile, cell(), wall);
        }

        @Override
        public void undo() {
            super.undo();
            if (otherTerrainChanges != null) otherTerrainChanges.undo();
        }

        @Override
        public void redo() {
            super.redo();
            if (customTile != null) place(customTile, cell(), wall);
            if (otherTerrainChanges != null) otherTerrainChanges.redo();
        }

        @Override
        public boolean hasContent() {
            return customTile != oldCustomTile() || super.hasContent();
        }
    }

    public static class Remove extends PlaceCustomTileActionPart {

        public Remove(int cell, boolean wall, CustomTilemap customTile) {
            super(cell, wall, customTile);
            remove(customTile, wall);
        }

        @Override
        public void undo() {
            super.undo();

            place(customTile, cell(), wall);
        }

        @Override
        public void redo() {
            super.redo();
            if (customTile != null) remove(customTile, wall);
        }

        @Override
        public boolean hasContent() {
            return customTile != null || super.hasContent();
        }
    }

}