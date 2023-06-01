package com.shatteredpixel.shatteredpixeldungeon.editor.ui;

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.watabou.noosa.Image;

public class RedButtonWithSubIcon extends RedButton {


    private Image subIcon;

    public RedButtonWithSubIcon(String label, Image subIcon) {
        this(label, 9, subIcon);
    }

    public RedButtonWithSubIcon(String label, int size, Image subIcon) {
        super(label, size);
        this.subIcon = subIcon;
        if (subIcon != null) add(subIcon);
    }

    @Override
    public void icon(Image icon) {
        super.icon(icon);
        if (subIcon != null) bringToFront(subIcon);
    }

    public void setSubIcon(Image subIcon) {//not tested
        remove(this.subIcon);
        this.subIcon.destroy();
        this.subIcon = subIcon;
        if (subIcon != null) add(subIcon);
        layout();
    }

    @Override
    protected void layout() {
        super.layout();

        if (subIcon != null && icon != null) {
            subIcon.x = icon.x + icon.width() - subIcon.width() + 1.5f;
            subIcon.y = y + 1.5f;
            PixelScene.align(subIcon);
        }
    }
}