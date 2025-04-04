package com.shatteredpixel.shatteredpixeldungeon.editor.util;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.ArrowCell;
import com.shatteredpixel.shatteredpixeldungeon.editor.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.editor.CoinDoor;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditTileComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.transitions.ChooseDestLevelComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.QuestLevels;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.Zone;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndMenuEditor;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.dungeon.WndNewDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor.WndSelectLevelType;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.services.server.ServerCommunication;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.ArrowCellTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndSupportPrompt;
import com.watabou.noosa.CombinedImage;
import com.watabou.noosa.Game;
import com.watabou.noosa.Gizmo;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Random;
import com.watabou.utils.RectF;

import java.io.IOException;
import java.util.Map;

public final class EditorUtilities {


    public static final Item[] EMPTY_ITEM_ARRAY = new Item[0];
    public static final String[] EMPTY_STRING_ARRAY = new String[0];
    public static final Class[] EMPTY_CLASS_ARRAY = new Class[0];
    public static final Component[] EMPTY_COMP_ARRAY = new Component[0];


    private EditorUtilities() {
    }


    public static String appendCellToString(int cell) {
        return appendCellToString(cell, Dungeon.level);
    }

    public static String appendCellToString(int cell, Level level) {
        if (cell < 0) return "";
        return " " + cellToString(cell, level);
    }

    public static String cellToString(int cell) {
        return cellToString(cell, Dungeon.level);
    }

    public static String cellToString(int cell, Level level) {
        return cellToString(cell, level.width());
    }

    public static String cellToString(int cell, int levelWidth) {
        int x = cell % levelWidth + 1;
        int y = cell / levelWidth + 1;
        return "( " + x + " | " + y + " )";
    }

    public static String cellToStringNoBrackets(int cell, int levelWidth) {
        int x = cell % levelWidth + 1;
        int y = cell / levelWidth + 1;
        return x + " | " + y;
    }

    public static String formatTitle(String name, int cell) {
        return Messages.titleCase(name) + ": " + cellToString(cell);
    }

    public static String formatTitle(CustomLevel.ItemWithPos item) {
        return formatTitle(item.item().title(), item.pos());
    }

    public static String appendBoss(Mob maybeBoss) {
        return (maybeBoss.isBossMob || Dungeon.level != null && CustomDungeon.isEditing() && Dungeon.level.bossmobAt == maybeBoss.pos
                ? " - " + Messages.get(WndSelectLevelType.class, "type_boss") : "");
    }

    public static String getDispayName(LevelTransition transition) {
        if (transition.destBranch == 0) return getDispayName(transition.destLevel);
        QuestLevels questLevel = QuestLevels.get(transition.destBranch);
        return questLevel == null ? "Branch=" + transition.destBranch : questLevel.getName();
    }

    public static String getDispayName(String specialLevelSchemeName) {
        if (Level.NONE.equals(specialLevelSchemeName))
            return Messages.get(ChooseDestLevelComp.class, "none_level");
        if (Level.SURFACE.equals(specialLevelSchemeName))
            return Messages.get(ChooseDestLevelComp.class, "surface_level");
        if (Level.ANY.equals(specialLevelSchemeName))
            return Messages.get(ChooseDestLevelComp.class, "any_level");
        return specialLevelSchemeName;
    }

    public static String getDispayNameForZone(String specialZoneName) {
        if (Zone.NONE.equals(specialZoneName))
            return Messages.get(Zone.class, "none_zone");
        return specialZoneName;
    }

    public static String getCodeName(LevelScheme specialLevelScheme) {
        if (specialLevelScheme == LevelScheme.NO_LEVEL_SCHEME) return null;
        if (specialLevelScheme == LevelScheme.SURFACE_LEVEL_SCHEME) return Level.SURFACE;
        if (specialLevelScheme == LevelScheme.ANY_LEVEL_SCHEME) return Level.ANY;
        return specialLevelScheme.getName();
    }

    public static LevelScheme getLevelScheme(String name) {
        if (Level.SURFACE.equals(name)) return LevelScheme.SURFACE_LEVEL_SCHEME;
        if (Level.ANY.equals(name)) return LevelScheme.ANY_LEVEL_SCHEME;
        if (Level.NONE.equals(name)) return LevelScheme.NO_LEVEL_SCHEME;
        return Dungeon.customDungeon.getFloor(name);
    }

    public static String replaceInvalidChars(String s) {
        return s.replace("ä", "Ã¤").replace("ö", "Ã¶").replace("ü", "Ã¼")
                .replace("Ä", "Ã\u0084").replace("Ö", "Ã\u0096").replace("Ü", "Ã\u009C")
                .replace("ß", "Ã\u009F");
    }

    public static void showDuplicateNameWarning() {
        EditorScene.show(
                new WndOptions(Icons.get(Icons.WARNING),
                        Messages.get(WndNewDungeon.class, "dup_name_title"),
                        Messages.get(EditorUtilities.class, "dup_name_body"),
                        Messages.get(WndSupportPrompt.class, "close")
                )
        );
    }

//    public static int getWindowWidth(){
//
//    }
//    public static int getWindowHeight(){
//
//    }

    public static int getNumTiles(int terrain, Level level) {
        int numFound = 0;
        for (int i = 0; i < level.map.length; i++) {
            if (level.map[i] == terrain) numFound++;
        }
        return numFound;
    }

    public static int getNumContainer(Heap.Type type, Level level) {
        int numFound = 0;
        for (Heap h : level.heaps.values()) {
            if (h.type == type) numFound++;
        }
        return numFound;
    }

    public static int getNumKeys(Key.Type type, Level level) {
        int numFound = 0;
        for (Heap h : level.heaps.values()) {
            for (Item i : h.items) {
                if (i instanceof Key && ((Key) i).type() == type
                        && (Level.ANY.equals(((Key) i).levelName) || level.name.equals(((Key) i).levelName))) numFound += i.quantity();
            }
        }
        return numFound;
    }

    public static int getNumItem(Class<? extends Item> item, Level level) {
        int numFound = 0;
        for (Heap h : level.heaps.values()) {
            for (Item i : h.items) {
                if (i.getClass() == item) numFound += i.quantity();
            }
        }
        return numFound;
    }

    public static String addIronKeyDescription(String desc, Level level) {
        int numLockedDoors = EditorUtilities.getNumTiles(Terrain.LOCKED_DOOR, level)
                + EditorUtilities.getNumTiles(Terrain.SECRET_LOCKED_DOOR, level);
        int numIronKeys = EditorUtilities.getNumKeys(Key.Type.IRON, level);
        if (desc.length() > 0) desc += "\n";
        desc += "\n" + Messages.get(EditTileComp.class, "num_locked_doors") + ": " + numLockedDoors;
        desc += "\n" + Messages.get(EditTileComp.class, "num_iron_keys") + ": " + numIronKeys;
        return desc;
    }

    public static String addGoldKeyDescription(String desc, Level level) {
        int numLockedChests = EditorUtilities.getNumContainer(Heap.Type.LOCKED_CHEST, level);
        int numIronKeys = EditorUtilities.getNumKeys(Key.Type.GOLD, level);
        if (desc.length() > 0) desc += "\n";
        desc += "\n" + Messages.get(EditTileComp.class, "num_gold_containers") + ": " + numLockedChests;
        desc += "\n" + Messages.get(EditTileComp.class, "num_gold_keys") + ": " + numIronKeys;
        return desc;
    }

    public static String addCrystalKeyDescription(String desc, Level level) {
        int numLockedDoors = EditorUtilities.getNumTiles(Terrain.CRYSTAL_DOOR, level)
                + EditorUtilities.getNumTiles(Terrain.SECRET_CRYSTAL_DOOR, level);
        int numCrystalContainers = EditorUtilities.getNumContainer(Heap.Type.CRYSTAL_CHEST, level);
        int numCrystalKeys = EditorUtilities.getNumKeys(Key.Type.CRYSTAL, level);
        if (desc.length() > 0) desc += "\n";
        desc += "\n" + Messages.get(EditTileComp.class, "num_crystal_doors") + ": " + numLockedDoors;
        desc += "\n" + Messages.get(EditTileComp.class, "num_crystal_containers") + ": " + numCrystalContainers;
        desc += "\n" + Messages.get(EditTileComp.class, "num_crystal_keys") + ": " + numCrystalKeys;
        return desc;
    }

    public static String addSkeletonKeyDescription(String desc, Level level) {
        int numLockedDoors = EditorUtilities.getNumTiles(Terrain.LOCKED_EXIT, level);
        int numSkeleKeys = EditorUtilities.getNumKeys(Key.Type.SKELETON, level);
        if (desc.length() > 0) desc += "\n";
        desc += "\n" + Messages.get(EditTileComp.class, "num_locked_exits") + ": " + numLockedDoors;
        desc += "\n" + Messages.get(EditTileComp.class, "num_skeleton_keys") + ": " + numSkeleKeys;
        return desc;
    }

    public static String addCoinDoorDescription(String desc, Level level) {
        int numGold = EditorUtilities.getNumItem(Gold.class, level);
        int numCoinDoors = 0;
        int goldNeeded = 0;
        for (CoinDoor door : level.coinDoors.values()) {
            if (level.map[door.pos] == Terrain.COIN_DOOR) {
                numCoinDoors++;
                goldNeeded += door.cost;
            }
        }
        if (desc.length() > 0) desc += "\n";
        desc += "\n" + Messages.get(EditTileComp.class, "num_coin_doors") + ": " + numCoinDoors;
        desc += "\n" + Messages.get(EditTileComp.class, "total_coin_door_unlock_cost") + ": " + goldNeeded;
        desc += "\n" + Messages.get(EditTileComp.class, "num_gold") + ": " + numGold;
        return desc;
    }

    public static String convertTimeDifferenceToString(long timeDifferenceMillis) {
        long seconds = Math.abs(timeDifferenceMillis) / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long weeks = days / 7;
        long years = seconds / 31556952;

        if (timeDifferenceMillis < 0) {
            if (timeDifferenceMillis > - 15_000) return Messages.get(EditorUtilities.class, "time_diff_seconds_true", 0);
            if (timeDifferenceMillis > - 300_000) return Messages.get(EditorUtilities.class, "now");
            return Messages.get(EditorUtilities.class, "time_diff_future", days, hours % 24, minutes % 60);
        } else if (years > 0) {
            return Messages.get(EditorUtilities.class, "time_diff_years_" + (years != 1), years);
        } else if (weeks > 0) {
            return Messages.get(EditorUtilities.class, "time_diff_weeks_" + (weeks != 1), weeks);
        } else if (days > 0) {
            return Messages.get(EditorUtilities.class, "time_diff_days_" + (days != 1), days);
        } else if (hours > 0) {
            return Messages.get(EditorUtilities.class, "time_diff_hours_" + (hours != 1), hours);
        } else if (minutes > 0) {
            return Messages.get(EditorUtilities.class, "time_diff_minutes_" + (minutes != 1), minutes);
        } else {
            return Messages.get(EditorUtilities.class, "time_diff_seconds_" + (seconds != 1), seconds);
        }
    }

    public static boolean shouldConnectToInternet(Runnable onManualConfirm) {
        if (SPDSettings.WiFi() && !Game.platform.connectedToUnmeteredNetwork()) {
            Game.scene().addToFront(new WndOptions(
                    Messages.get(ServerCommunication.class, "paid_wifi_title"),
                    Messages.get(ServerCommunication.class, "paid_wifi_body"),
                    Messages.get(ServerCommunication.class, "paid_wifi_yes"),
                    Messages.get(ServerCommunication.class, "paid_wifi_no")
            ) {
                @Override
                protected void onSelect(int index) {
                    if (index == 0) onManualConfirm.run();
                }
            });
            return false;
        }
        return true;
    }

    public static <K, V> V getOrDefault(Map<K, V> map, K key, V defaultValue) {
        V val = map.get(key);
        return val == null ? defaultValue : val;
    }

    public static int getMaxWindowOffsetYForVisibleToolbar() {
        return -11;//toolbar height
    }

    public static Window getParentWindow(Gizmo g) {
        Group w = g.parent;
        while (w != null && !(w instanceof Window)) {
            w = w.parent;
        }
        return (Window) w;
    }

    public static float layoutCompsLinear(int gap, Component parent, Component... comps) {
        return layoutCompsLinear(gap, WndMenuEditor.BTN_HEIGHT, parent, comps);
    }

    public static float layoutCompsLinear(int gap, int compHeight, Component parent, Component... comps) {
        if (comps == null) return parent.height();

        float posY = parent.top() + parent.height();

        boolean hasAtLeastOneComp = false;
        for (Component c : comps) {
            if (c != null && c.visible) {
                hasAtLeastOneComp = true;
                c.setRect(parent.left(), posY, parent.width(), compHeight);
                PixelScene.align(c);
                posY = c.bottom() + gap;
            }
        }

//        if (hasAtLeastOneComp) height = (int)(posY - y - WndTitledMessage.GAP);
        if (hasAtLeastOneComp) return (posY - parent.top() - gap);
        return parent.height();
    }

    public static float layoutStyledCompsInRectangles(int gap, float width, Component parent, Component... comps) {
        return layoutStyledCompsInRectangles(gap, width, PixelScene.landscape() ? 3 : 2, parent, comps);
    }

    public static float layoutStyledCompsInRectangles(int gap, float width, int numColumns, Component parent, Component... comps) {
        return layoutStyledCompsInRectangles(gap, width, 0, numColumns, parent, comps);
    }

    public static float layoutStyledCompsInRectangles(int gap, float width, float minCompHeight, int numColumns, Component parent, Component... comps) {
        if (comps == null || comps.length == 0) return parent.height();

        final int compsPerRow = numColumns;

        float widthOnePart = (width - gap * (compsPerRow - 1)) / compsPerRow;

        //TODO take logic from heroselectscene to center remaining

        for (Component c : comps) {
            if (c != null && c.visible) c.setSize(widthOnePart, 0);
        }
        float[] rowHeights = new float[comps.length];
        int row = 0;
        int column = 0;
        rowHeights[0] = minCompHeight;
        for (Component c : comps) {
            if (c != null && c.visible) {
                rowHeights[row] = Math.max(c.height(), rowHeights[row]);
                column++;
                if (column == compsPerRow || c instanceof ParagraphIndicator) {
                    column = 0;
                    row++;
                    if (row < rowHeights.length) rowHeights[row] = minCompHeight;
                }
            }
        }
        row = column = 0;
        for (Component c : comps) {
            if (c != null && c.visible) {
                if (c instanceof ParagraphIndicator) {
                    column = compsPerRow;
                } else {
                    c.setSize(widthOnePart, rowHeights[row]);
                    column++;
                }
                if (column == compsPerRow) {
                    column = 0;
                    row++;
                }
            }
        }

        float posY = parent.top() + parent.height();
        float posX = parent.left();
        row = column = 0;
        for (Component c : comps) {
            if (c != null && c.visible) {

                if (c instanceof ParagraphIndicator) {
                    if (column == 0) {
                        posY -= rowHeights[row];
                        row++;
                        continue;
                    }
                    column = compsPerRow;
                } else {
                    c.setPos(posX, posY);
                    column++;
                }

                if (column == compsPerRow) {
                    posY += gap + rowHeights[row];
                    posX = parent.left();
                    column = 0;
                    row++;
                } else posX += widthOnePart + gap;
            }
        }

        return posY + (column == 0 ? -gap : rowHeights[row]) - parent.top();
    }

    public static class ParagraphIndicator extends Component {
    }

    public static final ParagraphIndicator PARAGRAPH_INDICATOR_INSTANCE = new ParagraphIndicator();


    public static int getRandomCellGuaranteed(Level level, Char ch) {
        int pos;
        int tries = level.length();
        do {
            pos = Random.Int(level.length());//Choose a random cell
            tries--;
        } while ((!Barrier.canEnterCell(pos, ch, false, false)) && tries >= 0);
        if (!Barrier.canEnterCell(pos, ch, false, false)) {
            int l = level.length();
            for (pos = 0; pos < l; pos++) {
                if (Barrier.canEnterCell(pos, ch, false, false))
                    break;//choose first valid cell
            }
            return pos == l
                    ? l / 2//if all positions are invalid, just take the center
                    : pos;
        }
        return pos;
    }

    public static final int TOP = 1, TOP_RIGHT = 2, RIGHT = 4, BOTTOM_RIGHT = 8, BOTTOM = 16, BOTTOM_LEFT = 32, LEFT = 64, TOP_LEFT = 128;

    public static int stitchNeighbours(int cell, int terrain, Level level) {
        int result = 0;
        int width = level.width();
        int length = level.length();
        boolean rightEdge = (cell + 1) % width == 0;
        boolean leftEdge = (cell - 1) % width == width - 1;
        if (cell >= width && terrain == level.map[cell - width]) result += TOP;
        if (!rightEdge) {
            if (cell - width + 1 > 0 && terrain == level.map[cell - width + 1]) result += TOP_RIGHT;
            if (cell + 1 < length && terrain == level.map[cell + 1]) result += RIGHT;
            if (cell + 1 + width < length && terrain == level.map[cell + 1 + width]) result += BOTTOM_RIGHT;
        }
        if (cell + width < length && terrain == level.map[cell + width]) result += BOTTOM;
        if (!leftEdge) {
            if (cell - 1 + width < length && terrain == level.map[cell - 1 + width]) result += BOTTOM_LEFT;
            if (cell - 1 > 0 && terrain == level.map[cell - 1]) result += LEFT;
            if (cell - 1 - width > 0 && terrain == level.map[cell - 1 - width]) result += TOP_LEFT;
        }
        return result;
    }

    public static Image createSubIcon(Item item) {
        return createSubIcon(item.icon);
    }

    public static Image createSubIcon(int icon) {
        RectF r = ItemSpriteSheet.Icons.film.get(icon);
        if (r == null) return null;
        Image itemIcon = new Image(Assets.Sprites.ITEM_ICONS);
        itemIcon.frame(r);
        return itemIcon;
    }

    public static Image imageOf(Object obj, boolean nullIfNotFound) {
        if (obj instanceof Item) return Dungeon.customDungeon.getItemImage((Item) obj);
        if (obj instanceof Mob) return ((Mob) obj).createSprite();
        if (obj instanceof Trap) return ((Trap) obj).getSprite();
        if (obj instanceof Plant) return ((Plant) obj).getSprite();
        if (obj instanceof Heap) return new ItemSprite((Heap) obj);
        if (obj instanceof Barrier) return ((Barrier) obj).getSprite();
        if (obj instanceof ArrowCell) return ((ArrowCell) obj).getSprite();
        if (obj instanceof Image) return (Image) obj;

        return nullIfNotFound ? null : new ItemSprite();
    }

    private static final TextureFilm TERRAIN_FEATURE_FILM = new TextureFilm(Assets.Environment.TERRAIN_FEATURES, DungeonTilemap.SIZE, DungeonTilemap.SIZE);
    private static final TextureFilm BARRIER_TEXTURE_FILM = new TextureFilm(Assets.Environment.BARRIER, DungeonTilemap.SIZE, DungeonTilemap.SIZE);
    private static final TextureFilm ARROW_CELL_TEXTURE_FILM = new TextureFilm(Assets.Environment.ARROW_CELL, DungeonTilemap.SIZE, DungeonTilemap.SIZE);
    private static Image getImageFromFilm(int imgCode, TextureFilm film, String asset) {
        RectF frame = film.get(imgCode);
        if (frame != null) {
            Image img = new Image(asset);
            img.frame(frame);
            return img;
        }
        return new Image();
    }

    public static Image getTerrainFeatureTexture(int imgCode) {
        return getImageFromFilm(imgCode, TERRAIN_FEATURE_FILM, Assets.Environment.TERRAIN_FEATURES);
    }

    public static Image getBarrierTexture(int imgCode) {
        return getImageFromFilm(imgCode, BARRIER_TEXTURE_FILM, Assets.Environment.BARRIER);
    }

    public static Image getArrowCellTexture(ArrowCell arrowCell) {
        return getArrowCellTexture(arrowCell.directionsLeaving, arrowCell.visible);
    }

    public static Image getArrowCellTexture(int directions, boolean visible) {
        int diagonalArrows = ArrowCellTilemap.imgCodeDiagonalOuterArrows(directions, visible);
        int straightArrows = ArrowCellTilemap.imgCodeStraightOuterArrows(directions, visible);
        int center = ArrowCellTilemap.imgCodeCenter(directions, visible);
        Image a = getImageFromFilm(diagonalArrows, ARROW_CELL_TEXTURE_FILM, Assets.Environment.ARROW_CELL);
        Image b = getImageFromFilm(straightArrows, ARROW_CELL_TEXTURE_FILM, Assets.Environment.ARROW_CELL);
        Image c = getImageFromFilm(center, ARROW_CELL_TEXTURE_FILM, Assets.Environment.ARROW_CELL);
        return new CombinedImage(a, b, c);
    }
    
    
    private static boolean remindedCriticalBattery = false;
    
    public static void checkBatteryStateAndMaybeShowWarning() {
        if (Game.platform.batteryRemaining() <= 3) {
            
            if (!remindedCriticalBattery) {
                DungeonScene.show(new WndOptions(Icons.WARNING.get(),
                        Messages.get(EditorUtilities.class, "low_battery_title"),
                        Messages.get(EditorUtilities.class, "low_battery_body"),
                        Messages.get(EditorUtilities.class, "low_battery_continue"),
                        Messages.get(EditorUtilities.class, "low_battery_close_app")) {
                    @Override
                    protected void onSelect(int index) {
                        if (index == 0) {
                            //continue
                            remindedCriticalBattery = true;
                        } else {
                            //close now
							try {
								CustomDungeonSaves.saveLevel(EditorScene.getCustomLevel());
								CustomDungeonSaves.saveDungeon(Dungeon.customDungeon);
                                Game.instance.finish();
							} catch (IOException e) {
                                //don't kill the game if sth went wrong
								Game.reportException(e);
                                DungeonScene.show(new WndError(e));
							}
						}
                    }
                });
            }
        } else {
            remindedCriticalBattery = false;
        }
    }
    
}
