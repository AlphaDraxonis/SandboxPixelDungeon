package com.alphadraxonis.sandboxpixeldungeon.editor.ui;

import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.ui.RenderedTextBlock;
import com.alphadraxonis.sandboxpixeldungeon.ui.ScrollPane;
import com.alphadraxonis.sandboxpixeldungeon.ui.Window;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndTitledMessage;

public class WndReadSign extends Window {

    private RenderedTextBlock text;
    private ScrollPane scrollPane;

    public WndReadSign(String string) {

        resize(Math.min(WndTitledMessage.WIDTH_MAX, (int) (PixelScene.uiCamera.width * 0.9)), 200);

        text = PixelScene.renderTextBlock(string, 6);
        add(text);
        text.maxWidth(width);

        scrollPane = new ScrollPane(text);
        add(scrollPane);
        resize(width, Math.max(50, (int) Math.min((PixelScene.uiCamera.height * 0.8f), text.height()+2)));
        scrollPane.setRect(0, 0, width, height);
    }
}