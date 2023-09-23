package com.alphadraxonis.sandboxpixeldungeon.editor.ui;

import com.alphadraxonis.sandboxpixeldungeon.Chrome;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.ui.Icons;
import com.alphadraxonis.sandboxpixeldungeon.ui.RenderedTextBlock;
import com.alphadraxonis.sandboxpixeldungeon.ui.StyledButton;
import com.watabou.noosa.Image;

public class StyledCheckbox extends StyledButton {

    private boolean checked = false;

    protected Image checkboxIcon;

    public StyledCheckbox(String label, int size) {
        this(Chrome.Type.GREY_BUTTON_TR, label, size);
    }

    public StyledCheckbox(Chrome.Type type, String label, int size) {
        this(type, label, size, false);
    }

    public StyledCheckbox(Chrome.Type type, String label, int size, boolean checked) {
        super(type, label, size);
        text.align(RenderedTextBlock.CENTER_ALIGN);
        checkboxIcon(Icons.UNCHECKED.get());

        if (checked) checked(checked);
    }

    @Override
    protected void createChildren(Object... params) {
        multiline = true;
        super.createChildren(params);
    }

    @Override
    protected void layout() {

        height = Math.max(getMinimumHeight(width()), height());

        super.layout();

        if (text != null && !text.text().equals("")) {
            if (multiline) text.maxWidth((int) (width - bg.marginHor() - 2 - checkboxIcon.width()));
            text.setPos(
                    x + (width() - text.width() - 2 - checkboxIcon.width()) * 0.5f,
                    y + (icon == null ? (height() - text.height()) * 0.5f :
                            (height() - icon.height() - text.height()) * 0.5f + 1 + icon.height())
            );
            PixelScene.align(text);

            checkboxIcon.x = text.right() + 2;
        } else checkboxIcon.x = x + (width() - checkboxIcon.width()) * 0.5f;
        checkboxIcon.y = y + (height() - checkboxIcon.height()) * 0.5f;
        PixelScene.align(checkboxIcon);

        if (icon != null) {
            icon.x = x + (width() - 2 - checkboxIcon.width() - icon.width()) / 2f + 1;
            icon.y = text.top() - 2 - icon.height();
            PixelScene.align(icon);
        }

        if (leftJustify) throw new IllegalArgumentException("leftJustify not supported!");
    }

    public float getMinimumHeight(float width) {
        if (multiline) text.maxWidth((int) (width - bg.marginHor() - (checkboxIcon == null ? 0 : 2 + checkboxIcon.width())));
        return Math.max(checkboxIcon == null ? 0 : checkboxIcon.height(), text.height() + (icon == null ? 0 : icon.height() + 1)) + 2 + bg.marginVer();
    }

    public boolean checked() {
        return checked;
    }

    public void checked(boolean value) {
        if (checked != value) {
            checked = value;
            checkboxIcon.copy(Icons.get(checked ? Icons.CHECKED : Icons.UNCHECKED));
        }
    }

    @Override
    protected void onClick() {
        super.onClick();
        checked(!checked);
    }

    protected void checkboxIcon(Image icon) {
        if (checkboxIcon != null) {
            remove(checkboxIcon);
        }
        checkboxIcon = icon;
        if (checkboxIcon != null) {
            add(checkboxIcon);
            layout();
        }
    }
}