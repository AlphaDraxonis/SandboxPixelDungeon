package com.shatteredpixel.shatteredpixeldungeon.editor.ui;

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;

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