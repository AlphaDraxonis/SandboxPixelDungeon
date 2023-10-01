package com.alphadraxonis.sandboxpixeldungeon.editor.inv.items;

import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.DefaultEditComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.EditCustomTileComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.DefaultListItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.EditorInventoryWindow;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.ActionPart;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.ActionPartList;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.Undo;
import com.alphadraxonis.sandboxpixeldungeon.editor.util.CustomTileLoader;
import com.alphadraxonis.sandboxpixeldungeon.editor.util.EditorUtilies;
import com.alphadraxonis.sandboxpixeldungeon.levels.Level;
import com.alphadraxonis.sandboxpixeldungeon.levels.Terrain;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.tiles.CustomTilemap;
import com.alphadraxonis.sandboxpixeldungeon.ui.ScrollingListPane;
import com.watabou.noosa.Image;
import com.watabou.utils.Point;

import java.util.Set;

public class CustomTileItem extends EditorItem {


    private final int cell;
    private final CustomTilemap customTile;


    public CustomTileItem(CustomTilemap customTile, int cell) {
        this.cell = cell;
        this.customTile = customTile;
    }

    @Override
    public String name() {
        return getName(customTile(), cell());
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

    @Override
    public ScrollingListPane.ListItem createListItem(EditorInventoryWindow window) {
        return new DefaultListItem(this, window, name(), getSprite());
    }


    @Override
    public DefaultEditComp<?> createEditComponent() {
        return new EditCustomTileComp(customTile(), cell());
    }

    @Override
    public Image getSprite() {
        return createImage(customTile());
    }

    public static Image createImage(CustomTilemap cust) {
        Image img = cust instanceof CustomTileLoader.OwnCustomTile ? new Image(cust.getTexture()) : cust.fullImage();
        img.scale.set(Math.min(1f / cust.tileW, 1f / cust.tileH));
        return img;
    }

    @Override
    public void place(int cell) {
        if (isPositionValid(cell, customTile()))
            Undo.addActionPart(place(cell, customTile().getCopy()));
    }

    public static ActionPart place(int cell, CustomTilemap customTile) {
        return new CustomTileItem.Place(cell, customTile.terrain, customTile);
    }

    public static ActionPart remove(int cell) {
        Point p = Dungeon.level.cellToPoint(cell);
        CustomTilemap cust = findCustomTileAt(p, Dungeon.level.customTiles);
        boolean wall = cust == null;
        if (wall) cust = findCustomTileAt(p, Dungeon.level.customWalls);
        if (cust != null) return new CustomTileItem.Remove(cell, wall ? Terrain.WALL : cust.terrain, cust);
        return null;
    }

    public static boolean isPositionValid(int cell, CustomTilemap customTile) {
        int x = cell % Dungeon.level.width();
        int y = cell / Dungeon.level.width();
        return x >= customTile.offsetCenterX && x + customTile.tileW - customTile.offsetCenterX <= Dungeon.level.width()
                && y >= customTile.offsetCenterY && y + customTile.tileH - customTile.offsetCenterY <= Dungeon.level.height();

    }

    public static String getName(CustomTilemap customTile, int cell) {
        String defaultName = customTile.name(0, 0);
        if (defaultName != null) return Messages.titleCase(defaultName)+ EditorUtilies.appendCellToString(cell);
        return TileItem.getName(customTile.terrain, cell);
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
//            if (custPoint.x == 0 && custPoint.y == 0 && cust.image(0, 0) != null) return cust;//only top left
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
        protected final int terrain;

        protected PlaceCustomTileActionPart(int cell, int terrain, CustomTilemap customTile) {
            super(cell, terrain, true);
            this.customTile = customTile;
            this.terrain = terrain;
        }

        public static void place(CustomTilemap customTile, int cell, int terrain) {
            customTile.setRect(cell % Dungeon.level.width(), cell / Dungeon.level.width(),
                    customTile.tileW, customTile.tileH);
            Dungeon.level.customTiles.add(customTile);
            EditorScene.add(customTile, terrain == Terrain.WALL);
        }

        public static void remove(CustomTilemap customTile, int terrain) {
            if (terrain == Terrain.WALL) Dungeon.level.customWalls.remove(customTile);
            else Dungeon.level.customTiles.remove(customTile);
            EditorScene.remove(customTile, terrain == Terrain.WALL);
        }
    }

    public static class Place extends PlaceCustomTileActionPart {

        private ActionPartList otherTerrainChanges;//for customTiles larger than 1x1

        public Place(int cell, int terrain, CustomTilemap customTile) {
            super(cell, terrain, customTile);
            if (customTile.tileW > 1 || customTile.tileH > 1) {
                otherTerrainChanges = new ActionPartList();
                int startPos = cell - customTile.offsetCenterX - customTile.offsetCenterY * Dungeon.level.width();
                customTile.create();//Need to render image first so we know the blank spots
                for (int i = 0; i < customTile.tileH; i++) {
                    for (int j = 0; j < customTile.tileW; j++) {
                        if (customTile.image(j, i) != null) {
                            int pos = startPos + j + i * Dungeon.level.width();
                            if (pos != cell) otherTerrainChanges.addActionPart(TileItem.place(pos, newTerrain(),
                                    CustomTileItem.findCustomTileAt(pos) != null));
                        }
                    }
                }
                otherTerrainChanges.redo();
            }
            place(customTile, cell(), terrain);
        }

        @Override
        public void undo() {
            super.undo();
            if (otherTerrainChanges != null) otherTerrainChanges.undo();
        }

        @Override
        public void redo() {
            super.redo();
            if (otherTerrainChanges != null) otherTerrainChanges.redo();
            if (customTile != null) place(customTile, cell(), terrain);
        }

        @Override
        public boolean hasContent() {
            return !EditCustomTileComp.areEqual(customTile, oldCustomTile()) || super.hasContent();
        }
    }

    public static class Remove extends PlaceCustomTileActionPart {

        protected final int offset;//can be removed from anywhere on the customTile, but placed only at one specific position

        public Remove(int cell, int terrain, CustomTilemap customTile) {
            super(cell, terrain, customTile);
            offset = cell - customTile.tileX - customTile.tileY * Dungeon.level.width()
                    - customTile.offsetCenterX - customTile.offsetCenterY * Dungeon.level.width();
            remove(customTile, terrain);
        }

        @Override
        public void undo() {
            super.undo();

            place(customTile, cell() - offset, terrain);
        }

        @Override
        public void redo() {
            super.redo();
            if (customTile != null) remove(customTile, terrain);
        }

        @Override
        public boolean hasContent() {
            return customTile != null || super.hasContent();
        }
    }

}