package com.shatteredpixel.shatteredpixeldungeon.editor.ui;

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;

public class WndReadSign extends Window {
    
    private static final int MINIMUM_HEIGHT = 50;

    private RenderedTextBlock text;
    private ScrollPane scrollPane;

    public WndReadSign(String string) {

        resize(WindowSize.WIDTH_LARGE.get(), 200);

        text = PixelScene.renderTextBlock(string, 6);
        add(text);
        text.maxWidth(width);

        scrollPane = new ScrollPane(text);
        add(scrollPane);
        resize(width, (int) Math.min(WindowSize.HEIGHT_SMALL.get(), Math.max(MINIMUM_HEIGHT, (text.height()+2))));
        scrollPane.setRect(0, 0, width, height);
    }
}