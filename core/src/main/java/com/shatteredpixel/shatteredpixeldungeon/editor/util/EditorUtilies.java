package com.shatteredpixel.shatteredpixeldungeon.editor.util;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditTileComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.transitions.ChooseDestLevelComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.QuestLevels;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.Zone;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndMenuEditor;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor.WndSelectLevelType;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.CrystalKey;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.GoldenKey;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.SkeletonKey;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.services.server.ServerCommunication;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;
import com.watabou.noosa.Gizmo;
import com.watabou.noosa.Group;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Random;

import java.util.Map;

public final class EditorUtilies {


    public static final Item[] EMPTY_ITEM_ARRAY = new Item[0];
    public static final String[] EMPTY_STRING_ARRAY = new String[0];


    private EditorUtilies() {
    }


    public static String appendCellToString(int cell) {
        return appendCellToString(cell, EditorScene.customLevel());
    }

    public static String appendCellToString(int cell, Level level) {
        if (cell < 0) return "";
        return " " + cellToString(cell, level);
    }

    public static String cellToString(int cell) {
        return cellToString(cell, EditorScene.customLevel());
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

    public static int getNumKeys(Class<? extends Key> type, Level level) {
        int numFound = 0;
        for (Heap h : level.heaps.values()) {
            for (Item i : h.items) {
                if (i.getClass() == type
                        && (Level.ANY.equals(((Key) i).levelName) || level.name.equals(((Key) i).levelName))) numFound += i.quantity();
            }
        }
        return numFound;
    }

    public static String addIronKeyDescription(String desc, Level level) {
        int numLockedDoors = EditorUtilies.getNumTiles(Terrain.LOCKED_DOOR, level);
        int numIronKeys = EditorUtilies.getNumKeys(IronKey.class, level);
        if (desc.length() > 0) desc += "\n";
        desc += "\n" + Messages.get(EditTileComp.class, "num_locked_doors") + ": " + numLockedDoors;
        desc += "\n" + Messages.get(EditTileComp.class, "num_iron_keys") + ": " + numIronKeys;
        return desc;
    }

    public static String addGoldKeyDescription(String desc, Level level) {
        int numLockedChests = EditorUtilies.getNumContainer(Heap.Type.LOCKED_CHEST, level);
        int numIronKeys = EditorUtilies.getNumKeys(GoldenKey.class, level);
        if (desc.length() > 0) desc += "\n";
        desc += "\n" + Messages.get(EditTileComp.class, "num_gold_containers") + ": " + numLockedChests;
        desc += "\n" + Messages.get(EditTileComp.class, "num_gold_keys") + ": " + numIronKeys;
        return desc;
    }

    public static String addCrystalKeyDescription(String desc, Level level) {
        int numLockedDoors = EditorUtilies.getNumTiles(Terrain.CRYSTAL_DOOR, level);
        int numCrystalContainers = EditorUtilies.getNumContainer(Heap.Type.CRYSTAL_CHEST, level);
        int numCrystalKeys = EditorUtilies.getNumKeys(CrystalKey.class, level);
        if (desc.length() > 0) desc += "\n";
        desc += "\n" + Messages.get(EditTileComp.class, "num_crystal_doors") + ": " + numLockedDoors;
        desc += "\n" + Messages.get(EditTileComp.class, "num_crystal_containers") + ": " + numCrystalContainers;
        desc += "\n" + Messages.get(EditTileComp.class, "num_crystal_keys") + ": " + numCrystalKeys;
        return desc;
    }

    public static String addSkeletonKeyDescription(String desc, Level level) {
        int numLockedDoors = EditorUtilies.getNumTiles(Terrain.LOCKED_EXIT, level);
        int numSkeleKeys = EditorUtilies.getNumKeys(SkeletonKey.class, level);
        if (desc.length() > 0) desc += "\n";
        desc += "\n" + Messages.get(EditTileComp.class, "num_locked_exits") + ": " + numLockedDoors;
        desc += "\n" + Messages.get(EditTileComp.class, "num_skeleton_keys") + ": " + numSkeleKeys;
        return desc;
    }

    public static String convertTimeDifferenceToString(long timeDifferenceMillis) {
        long seconds = Math.abs(timeDifferenceMillis) / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (timeDifferenceMillis < 0) {
            if (timeDifferenceMillis > -20000) return Messages.get(EditorUtilies.class, "time_diff_seconds_true", 0);
            return Messages.get(EditorUtilies.class, "time_diff_future", days, hours % 24, minutes % 60);
        } else if (days > 0) {
            return Messages.get(EditorUtilies.class, "time_diff_days_" + (days != 1), days);
        } else if (hours > 0) {
            return Messages.get(EditorUtilies.class, "time_diff_hours_" + (hours != 1), hours);
        } else if (minutes > 0) {
            return Messages.get(EditorUtilies.class, "time_diff_minutes_" + (minutes != 1), minutes);
        } else {
            return Messages.get(EditorUtilies.class, "time_diff_seconds_" + (seconds != 1), seconds);
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
        if (comps == null) return parent.height();

        float posY = parent.top() + parent.height() + gap * 2 - 1;

        boolean hasAtLeastOneComp = false;
        for (Component c : comps) {
            if (c != null && c.visible) {
                hasAtLeastOneComp = true;
                c.setRect(parent.left(), posY, parent.width(), WndMenuEditor.BTN_HEIGHT);
                PixelScene.align(c);
                posY = c.bottom() + gap;
            }
        }

//        if (hasAtLeastOneComp) height = (int)(posY - y - WndTitledMessage.GAP);
        if (hasAtLeastOneComp) return (posY - parent.top() - gap);
        return parent.height();
    }

    public static float layoutStyledCompsInRectangles(int gap, float width, Component parent, Component[] comps) {
        if (comps == null) return parent.height();

        final int compsPerRow = PixelScene.landscape() ? 3 : 2;

        float widthOnePart = (width - gap * (compsPerRow - 1)) / compsPerRow;

        //TODO take logic from heroselectscene to center remaining

        for (Component c : comps) {
            if (c != null) c.setSize(widthOnePart, -1);
        }
        float maxCompHeight = 0;
        for (Component c : comps) {
            if (c != null && c.height() > maxCompHeight) maxCompHeight = c.height();
        }
        for (Component c : comps) {
            if (c != null) c.setSize(widthOnePart, maxCompHeight);
        }

        float posY = parent.top() + parent.height() + gap * 2 - 1;
        float posX = parent.left();
        int indexInRow = 0;
        for (Component c : comps) {
            if (c != null) {

                if (c instanceof ParagraphIndicator) {
                    if (indexInRow == 0) continue;
                    indexInRow = compsPerRow;
                } else {
                    c.setPos(posX, posY);
                    indexInRow++;
                }

                if (indexInRow == compsPerRow) {
                    posY += gap + maxCompHeight;
                    posX = parent.left();
                    indexInRow = 0;
                } else posX += widthOnePart + gap;
            }
        }

        return posY + (indexInRow == 0 ? -gap : maxCompHeight) - parent.top();
    }

    public static class ParagraphIndicator extends Component {
    }

    public static final ParagraphIndicator PARAGRAPH_INDICATOR_INSTANCE = new ParagraphIndicator();


    public static int getRandomCellGuranteed(Level level) {
        int pos;
        int tries = level.length();
        do {
            pos = Random.Int(level.length());//Choose a random cell
            tries--;
        } while ((!level.passable[pos] || level.avoid[pos]) && tries >= 0);
        if (!level.passable[pos] || level.avoid[pos]) {
            int l = level.length();
            for (pos = 0; pos < l; pos++) {
                if (level.passable[pos] && !level.avoid[pos])
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
}