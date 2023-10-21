package com.shatteredpixel.shatteredpixeldungeon.editor.ui;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.NinePatch;

public class StyledItemSelector extends ItemSelector {

    protected NinePatch bg;

    protected int slotHeight = ItemSpriteSheet.SIZE;


    public StyledItemSelector(String text, Class<? extends Item> itemClasses, Item startItem, NullTypeSelector nullTypeSelector) {
        super(text, itemClasses, startItem, nullTypeSelector);
    }

    @Override
    protected void createChildren(Object... params) {
        bg = Chrome.get(Chrome.Type.GREY_BUTTON_TR);
        add(bg);
        super.createChildren(params);
//        sendToBack(bg);
    }

    @Override
    protected void layout() {
        if (getSlotHeight() == 0) return;

        height = Math.max(getMinimumHeight(width()), Math.max(Math.max(renderedTextBlock.height(), 10), height()));

        bg.x = x;
        bg.y = y;
        bg.size(width(), height());

        float conW = getSlotHeight() * 2 + GAP * 3;
        float startX = x + (width - conW) * 0.5f;
        float conY = y + (height() - getSlotHeight() - renderedTextBlock.height()) / 2f + 2 + renderedTextBlock.height();


        itemSlot.setRect(startX, conY, getSlotHeight(), getSlotHeight());
        PixelScene.align(itemSlot);
        changeBtn.setRect(itemSlot.right() + GAP * 3, conY, getSlotHeight(), getSlotHeight());
        PixelScene.align(changeBtn);

        if (!renderedTextBlock.text().equals("")) {
            renderedTextBlock.maxWidth((int) width);
            renderedTextBlock.setPos(
                    x + (width() - renderedTextBlock.width() * 2 + 1) * 0.5f +
                            renderedTextBlock.width() / 2f,// /2f is labels alignment!!!!
                    conY - 3 - renderedTextBlock.height()
            );
            PixelScene.align(renderedTextBlock);
        }
    }

    public float getMinimumHeight(float width) {
        renderedTextBlock.maxWidth((int) width);
        return getSlotHeight() + renderedTextBlock.height() + 4 + bg.marginVer();
    }

    public void setSlotHeight(int slotHeight) {
        this.slotHeight = slotHeight;
    }

    public int getSlotHeight() {
        return slotHeight;
    }
}