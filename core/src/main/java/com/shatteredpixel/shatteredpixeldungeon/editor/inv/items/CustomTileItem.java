package com.shatteredpixel.shatteredpixeldungeon.editor.inv.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditCustomTileComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.DefaultListItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.EditorInventoryWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.WndEditorInv;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Tiles;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.CustomTileActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomTileLoader;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
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
        return new DefaultListItem(this, window, name(), getSprite()) {
            private IconButton remove;

            @Override
            protected void createChildren(Object... params) {
                super.createChildren(params);
                if (customTile() instanceof CustomTileLoader.SimpleCustomTile) {
                    remove = new IconButton(Icons.CLOSE.get()) {
                        @Override
                        protected void onClick() {
                            Dungeon.customDungeon.customTiles.remove(customTile());
                            Tiles.removeCustomTile(CustomTileItem.this);
                            WndEditorInv.updateCurrentTab();
                            EditorScene.revalidateCustomTiles();
                        }
                    };
                    add(remove);
                }
            }

            @Override
            protected void layout() {
                super.layout();
                if (remove != null) {
                    float posX;
                    if (editButton != null) {
                        editButton.setPos(editButton.left() - ICON_WIDTH - 2, editButton.top());
                        hotArea.width = editButton.left() - 1;
                        posX = editButton.right() + 2;
                    } else {
                        posX = x + width - ICON_WIDTH;
                        hotArea.width = width - ICON_WIDTH - 1;
                    }
                    remove.setRect(posX + (ICON_WIDTH - remove.icon().width()) * 0.5f, y + (height - remove.icon().height()) * 0.5f,
                            remove.icon().width(), remove.icon().height());
                }
            }

            @Override
            protected int getLabelMaxWidth() {
                return super.getLabelMaxWidth() - ICON_WIDTH;
            }

            @Override
            public void onUpdate() {

                label.text(name());

                if (icon != null) remove(icon);
                icon = getSprite();
                addToBack(icon);
                remove(bg);
                addToBack(bg);

                super.onUpdate();
            }
        };
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
        return new CustomTileActionPart.Place(cell, customTile.terrain, customTile);
    }

    public static ActionPart remove(int cell) {
        Point p = Dungeon.level.cellToPoint(cell);
        CustomTilemap cust = findCustomTileAt(p, Dungeon.level.customTiles);
        boolean wall = cust == null;
        if (wall) cust = findCustomTileAt(p, Dungeon.level.customWalls);
        if (cust != null) return new CustomTileActionPart.Remove(cell, Dungeon.level.map[cell], cust);
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
        if (defaultName != null) return Messages.titleCase(defaultName) + EditorUtilies.appendCellToString(cell);
        return TileItem.getName(customTile.terrain, cell);
    }

    public static CustomTilemap findCustomTileAt(int cell) {
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

}