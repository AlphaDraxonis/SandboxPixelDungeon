package com.shatteredpixel.shatteredpixeldungeon.editor.inv;

import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;

public interface EditorInventoryWindow {

    void hide();
    WndBag.ItemSelectorInterface selector();

}