package com.shatteredpixel.shatteredpixeldungeon.editor.ui;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;

public class StyledButtonWithIconAndText extends StyledButton {


    public StyledButtonWithIconAndText(Chrome.Type type, String label) {
        this(type, label, 8);
    }

    public StyledButtonWithIconAndText(Chrome.Type type, String label, int size) {
        super(type, label, size);
        text.align(RenderedTextBlock.CENTER_ALIGN);
    }

    @Override
    protected void createChildren() {
        multiline = true;
        super.createChildren();
    }

    @Override
    protected void layout() {

        height = Math.max(getMinimumHeight(width()), height());

        super.layout();

        float contentHeight = height();

        if (text != null && !text.text().equals("")) {
            if (multiline) text.maxWidth((int) width() - bg.marginHor());
            text.setPos(
                    x + (width() + text.width()) / 2f - text.width(),
                    (icon == null ? y + (contentHeight - text.height()) / 2f :
                            y + (contentHeight - icon.height() - text.height()) / 2f + 1 + icon.height())
            );
            PixelScene.align(text);

        }

        if (icon != null) {
            icon.x = x + (width() - icon.width()) / 2f + 1;
            icon.y = text.top() - 2 - icon.height();
            PixelScene.align(icon);
        }

        if (leftJustify) throw new IllegalArgumentException("leftJustify not supported!");
    }

    public float getMinimumHeight(float width) {
        if (multiline) text.maxWidth((int) width - bg.marginHor());
        if (icon == null) return text.height() + 2 + bg.marginVer();
        return icon.height() + text.height() + 3 + bg.marginVer();
    }
}