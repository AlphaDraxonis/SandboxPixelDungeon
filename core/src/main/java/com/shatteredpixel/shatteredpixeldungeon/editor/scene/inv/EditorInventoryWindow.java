package com.shatteredpixel.shatteredpixeldungeon.editor.scene.inv;

import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;

public interface EditorInventoryWindow {

    void hide();
    WndBag.ItemSelector selector();

}