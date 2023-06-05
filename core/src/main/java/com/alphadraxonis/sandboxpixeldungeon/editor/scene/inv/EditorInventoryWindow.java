package com.alphadraxonis.sandboxpixeldungeon.editor.scene.inv;

import com.alphadraxonis.sandboxpixeldungeon.windows.WndBag;

public interface EditorInventoryWindow {

    void hide();
    WndBag.ItemSelector selector();

}