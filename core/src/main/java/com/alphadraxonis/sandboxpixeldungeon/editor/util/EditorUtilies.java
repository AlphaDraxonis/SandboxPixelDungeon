package com.alphadraxonis.sandboxpixeldungeon.editor.util;

import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Mob;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.parts.transitions.ChooseDestLevelComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomDungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomLevel;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.LevelScheme;
import com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.WndMenuEditor;
import com.alphadraxonis.sandboxpixeldungeon.editor.overview.floor.WndSelectLevelType;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.levels.Level;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.ui.Window;
import com.watabou.noosa.Gizmo;
import com.watabou.noosa.Group;
import com.watabou.noosa.ui.Component;

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

    public static String formatTitle(String name, int cell) {
        return Messages.titleCase(name) + ": " + cellToString(cell);
    }

    public static String formatTitle(CustomLevel.ItemWithPos item) {
        return formatTitle(item.item().title(), item.pos());
    }

    public static String appendBoss(Mob maybeBoss) {
        return (maybeBoss.isBossMob || CustomDungeon.isEditing() && Dungeon.level.bossmobAt == maybeBoss.pos
                ? " - " + Messages.get(WndSelectLevelType.class, "type_boss") : "");
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

    public static <K, V> V getOrDefault(Map<K, V> map, K key, V defaultValue) {
        V val = map.get(key);
        return val == null ? defaultValue : val;
    }

    public static int getMaxWindowOffsetYForVisibleToolbar() {
        return -11;
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
            if (c != null) {
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

    public static void layoutStyledCompsInRectangles(int gap, float width, Component parent, Component[] comps) {

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

        float posY = parent.top();
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

        parent.setSize(width, posY + (indexInRow == 0 ? -gap : maxCompHeight) - parent.top());
    }

    public static class ParagraphIndicator extends Component {
    }

    public static final ParagraphIndicator PARAGRAPH_INDICATOR_INSTANCE = new ParagraphIndicator();
}