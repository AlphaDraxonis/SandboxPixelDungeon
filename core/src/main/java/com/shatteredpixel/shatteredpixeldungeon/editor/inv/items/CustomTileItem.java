package com.shatteredpixel.shatteredpixeldungeon.editor.inv.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditCustomTileComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.EditorInventoryWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Tiles;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.DefaultListItemWithRemoveBtn;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPartList;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.CustomTileActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomTileLoader;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.sewerboss.GooBossRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.watabou.noosa.Image;
import com.watabou.utils.Point;

import java.util.Set;

public class CustomTileItem extends EditorItem<CustomTilemap> {

    private final int cell;

    public CustomTileItem(CustomTilemap customTile, int cell) {
        this.cell = cell;
        this.obj = customTile;
    }

    @Override
    public DefaultEditComp<?> createEditComponent() {
        return new EditCustomTileComp(getObject(), cell());
    }

    @Override
    public String name() {
        return getName(getObject(), cell());
    }

    @Override
    public Image getSprite() {
        Image img = createImage(getObject());
        img.originToCenter();
        return img;
    }

    public static String getName(CustomTilemap customTile, int cell) {
        String defaultName = customTile.name(0, 0);

        if (customTile instanceof GooBossRoom.GooNest) {
            return customTile.tileW + "x" + customTile.tileH + " GooNest " + (defaultName == null ? TileItem.getName(customTile.terrain, cell) : Messages.titleCase(defaultName));
        }

        if (defaultName != null) return Messages.titleCase(defaultName) + EditorUtilities.appendCellToString(cell);
        return TileItem.getName(customTile.terrain, cell);
    }

    public static Image createImage(CustomTilemap cust) {
        Image img = cust instanceof CustomTileLoader.OwnCustomTile ? new Image(cust.getTexture()) : cust.fullImage();
        img.scale.set(Math.min(1f / img.width(), 1f / img.height()) * ItemSpriteSheet.SIZE);
        return img;
    }

    @Override
    public Item getCopy() {
        return new CustomTileItem(getObject().getCopy(), cell);
    }

    public int cell() {
        return cell;
    }

    @Override
    public boolean supportsAction(Action action) {
        return action == Action.REMOVE && getObject() instanceof CustomTileLoader.SimpleCustomTile;
    }

    @Override
    public void doAction(Action action) {
        if (action == Action.REMOVE) {
            CustomTileLoader.SimpleCustomTile del = (CustomTileLoader.SimpleCustomTile) getObject();

            Undo.startAction();

            ActionPartList actionPart = new ActionPartList() {
                @Override
                public void undo() {
                    Dungeon.customDungeon.customTiles.add(del);
                    Tiles.addCustomTile(del);
                    super.undo();
                    Dungeon.customDungeon.restoreToolbar();
                }

                @Override
                public void redo() {
                    Dungeon.customDungeon.customTiles.remove(del);
                    Tiles.removeCustomTile(del);
                    super.redo();
                    EditorScene.revalidateCustomTiles();
                    Dungeon.customDungeon.restoreToolbar();
                }

                @Override
                public boolean hasContent() {
                    return true;
                }
            };

            for (CustomTilemap ct : Dungeon.level.customTiles.toArray(new CustomTilemap[0])) {
                if (ct instanceof CustomTileLoader.SimpleCustomTile && ((CustomTileLoader.SimpleCustomTile) ct).identifier.equals(del.identifier)) {
                    int cell = ct.tileX + ct.tileY * Dungeon.level.width();
                    actionPart.addActionPart(new CustomTileActionPart.Remove(cell, Dungeon.level.map[cell], ct));
                }
            }

            Undo.addActionPart(actionPart);

            Undo.endAction();

            actionPart.redo();
        }
    }

    @Override
    public ScrollingListPane.ListItem createListItem(EditorInventoryWindow window) {
        if (supportsAction(Action.REMOVE)) {
            return new DefaultListItemWithRemoveBtn(this, window, name(), getSprite()) {
                @Override
                protected void onRemove() {
                    CustomTileItem.this.doAction(Action.REMOVE);
                }
            };
        }
        return super.createListItem(window);
    }

    @Override
    public void setObject(CustomTilemap obj) {
    }

    @Override
    public void place(int cell) {
        CustomTilemap place = getObject().getCopy();
        if (!invalidPlacement(place, cell)) {
            Undo.addActionPart(place(place, cell));
        }
    }

    public static boolean invalidPlacement(CustomTilemap obj, int cell) {
        int x = cell % Dungeon.level.width();
        int y = cell / Dungeon.level.width();
        return x < obj.offsetCenterX || x + obj.tileW - obj.offsetCenterX > Dungeon.level.width()
                || y < obj.offsetCenterY || y + obj.tileH - obj.offsetCenterY > Dungeon.level.height();
    }

    public static ActionPart place(CustomTilemap customTile, int cell) {
        return new CustomTileActionPart.Place(cell, customTile.terrain, customTile);
    }

    public static ActionPart remove(int cell) {
        Point p = Dungeon.level.cellToPoint(cell);
        CustomTilemap cust = findCustomTileAt(p, Dungeon.level.customWalls);
        if (cust == null) cust = findCustomTileAt(p, Dungeon.level.customTiles);
        if (cust != null) return new CustomTileActionPart.Remove(cell, Dungeon.level.map[cell], cust);
        return null;
    }

    public static CustomTilemap findCustomTileAt(int cell, Boolean wall) {
        if (wall == null) return findAnyCustomTileAt(cell);
        Point p = Dungeon.level.cellToPoint(cell);
        return wall ? findCustomTileAt(p, Dungeon.level.customWalls) : findCustomTileAt(p, Dungeon.level.customTiles);
    }

    public static CustomTilemap findAnyCustomTileAt(int cell) {
        Point p = Dungeon.level.cellToPoint(cell);
        CustomTilemap cust = findCustomTileAt(p, Dungeon.level.customTiles);
        return cust == null ? findCustomTileAt(p, Dungeon.level.customWalls) : cust;
    }

    private static CustomTilemap findCustomTileAt(Point p, Set<CustomTilemap> customTiles) {
        for (CustomTilemap cust : customTiles) {
            if (cust instanceof CustomTilemap.BossLevelVisuals) continue;
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

    public static void removeCustomTilesAt(int cell, Level level, Boolean wall) {
        Point p = level.cellToPoint(cell);
        if (wall == null || wall) {
            CustomTilemap cust = findCustomTileAt(p, level.customWalls);
            if (cust != null) {
                cust.wallVisual = true;
                level.customWalls.remove(cust);
                EditorScene.remove(cust);
            }
        }
        if (wall == null || !wall) {
            CustomTilemap cust = findCustomTileAt(p, level.customTiles);
            if (cust != null) {
                level.customTiles.remove(cust);
                EditorScene.remove(cust);
            }
        }

    }

}