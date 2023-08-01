package com.alphadraxonis.sandboxpixeldungeon.editor.inv;

import com.alphadraxonis.sandboxpixeldungeon.windows.WndBag;

public interface EditorInventoryWindow {

    void hide();
    WndBag.ItemSelector selector();

}