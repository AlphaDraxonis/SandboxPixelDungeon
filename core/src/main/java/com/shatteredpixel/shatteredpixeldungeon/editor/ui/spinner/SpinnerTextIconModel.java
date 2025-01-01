package com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.ui.Component;

public abstract class SpinnerTextIconModel extends SpinnerTextModel {

    public SpinnerTextIconModel(Object... data) {
        super(false, data);
    }

    public SpinnerTextIconModel(boolean cycle, Object... data) {
        super(cycle, 0, data);
    }

    public SpinnerTextIconModel(boolean cycle, int initValueIndex, Object... data) {
        super(cycle, initValueIndex, data);
    }

    protected abstract Image displayIcon(Object value);

    protected Image getSubIcon(Object value) {
        return null;
    }

    @Override
    public Component createInputField(int fontSize) {
        valueDisplay = new ShowField(Chrome.get(getChromeType()), fontSize) {
            @Override
            public void showValue(Object value) {
                textBlock.text(displayString(value));
                setIcon(displayIcon(value));
                setSubIcon(getSubIcon(value));
                layout();
            }
        };
        return (Component) valueDisplay;
    }

    public static class ShowField extends Spinner.SpinnerTextBlock {

        private Image icon;
        private Image subIcon;
        private static final float GAP = 1;

        public ShowField(NinePatch bg, int fontSize) {
            super(bg, fontSize);
        }

        public void setIcon(Image icon) {
            if (this.icon != null) remove(this.icon);
            this.icon = icon;
            if (icon != null) add(icon);
        }

        public void setSubIcon(Image subIcon) {
            if (this.subIcon != null) remove(this.subIcon);
            this.subIcon = subIcon;
            if (subIcon != null) add(subIcon);
        }

        @Override
        protected void layout() {

            if (icon == null) {
                super.layout();
                return;
            }

            float contX = x;
            float contY = y;
            float contW = width;
            float contH = height;

            if (bg != null) {
                bg.x = x;
                bg.y = y;
                bg.size(width, height);

                contX += bg.marginLeft();
                contY += bg.marginTop();
                contW -= bg.marginHor();
                contH -= bg.marginVer();
            }

            float w = textBlock.width() + (icon == null ? 0 : icon.width() + GAP);
            if (icon != null) {
                icon.x = (contW - w) / 2 + contX + textOffsetX;
                icon.y = getCenterPos(icon.height, contH, contY, textOffsetY);
                PixelScene.align(icon);
                if (subIcon != null) {
                    subIcon.x = icon.x + 1.5f + icon.width / 2f;
                    subIcon.y = icon.y - 2 + (ItemSpriteSheet.Icons.SIZE - subIcon.height) / 2f;
                    PixelScene.align(subIcon);
                }
            }
            float x = icon == null ? (contW - w) / 2 + contX + textOffsetX : icon.x + icon.width + GAP;
            textBlock.setRect(x,
                    getCenterPos(textBlock.height(), contH, contY, textOffsetX),
                    textBlock.width(), textBlock.height());
        }
    }

    private static float getCenterPos(float size, float contentSize, float contentPos, float offset) {
        return (contentSize - size) / 2 + contentPos + offset;
    }

}