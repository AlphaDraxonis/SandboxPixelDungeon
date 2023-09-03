package com.alphadraxonis.sandboxpixeldungeon.editor.util;

import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomLevel;
import com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.WndMenuEditor;
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

    public static int getMaxWindowOffsetYForVisibleToolbar(){
        return -11;
    }

    public static Window getParentWindow(Gizmo g){
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
        if (hasAtLeastOneComp) return  (posY - parent.top() - gap);
        return parent.height();
    }

    public static void layoutStyledCompsInRectangles(int gap, float width, Component parent, Component[] comps){

        float oneThirdWidth = (width - gap * 2) / 3f;

        //TODO take logic from heroselectscene!

        for (Component c : comps) {
            if (c != null) c.setSize(oneThirdWidth, -1);
        }
        float maxCompHeight = 0;
        for (Component c : comps) {
            if (c != null && c.height() > maxCompHeight) maxCompHeight = c.height();
        }
        for (Component c : comps) {
            if (c != null) c.setSize(oneThirdWidth, maxCompHeight);
        }

        float posY = parent.top();
        float posX = parent.left();
        for (int i = 0; i < comps.length; i++) {
            if (comps[i] != null) {
                comps[i].setPos(posX, posY);
                if ((i + 1) % 3 == 0) {
                    posY += gap + maxCompHeight;
                    posX = parent.left();
                } else posX = comps[i].right() + gap;
            }
        }

        parent.setSize(width, posY + (comps.length % 3 == 0 ? -gap : maxCompHeight) - parent.top());
    }
}