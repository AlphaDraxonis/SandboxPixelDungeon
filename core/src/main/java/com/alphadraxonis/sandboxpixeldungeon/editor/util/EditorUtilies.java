package com.alphadraxonis.sandboxpixeldungeon.editor.util;

import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomLevel;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.levels.Level;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;

import java.util.Map;

public final class EditorUtilies {


    public static final Item[] EMPTY_ITEM_ARRAY = new Item[0];


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
        int x = cell % level.width() + 1;
        int y = cell / level.width() + 1;
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

}