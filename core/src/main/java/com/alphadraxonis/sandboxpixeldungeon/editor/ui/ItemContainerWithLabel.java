package com.alphadraxonis.sandboxpixeldungeon.editor.ui;

import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.DefaultEditComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.ItemContainer;
import com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.WndMenuEditor;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.ui.RenderedTextBlock;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndTitledMessage;

import java.util.List;

public class ItemContainerWithLabel<T extends Item> extends ItemContainer<T> {

    protected RenderedTextBlock label;
    protected float startColumnPos;

    public ItemContainerWithLabel(List<T> itemList, String label) {
        this(itemList, null, label);
    }

    public ItemContainerWithLabel(List<T> itemList, DefaultEditComp<?> editComp, String label) {
        this(itemList, editComp, false, label);
    }

    public ItemContainerWithLabel(List<T> itemList, DefaultEditComp<?> editComp, boolean reverseUiOrder, String label) {
        super(itemList, editComp, reverseUiOrder);

        this.label = PixelScene.renderTextBlock(label, 9);
        add(this.label);
        startColumnPos = this.label.width() + GAP;
    }


    @Override
    protected void layout() {
        float posY = y;
        float posX = x + GAP;

        float tempStartColumnPos = startColumnPos;//Helps for very long labels

        label.setPos(posX, posY + (WndMenuEditor.BTN_HEIGHT - label.height()) * 0.5f);
        PixelScene.align(label);
        posX += startColumnPos;
        if (posX + WndMenuEditor.BTN_HEIGHT > width + x) {
            posY += WndMenuEditor.BTN_HEIGHT + GAP;
            posX = x + GAP;
            tempStartColumnPos = 0;
        }

        for (Slot slot : slots) {
            slot.setRect(posX, posY, WndMenuEditor.BTN_HEIGHT, WndMenuEditor.BTN_HEIGHT);
            PixelScene.align(slot);
            posX = slot.right() + GAP;
            if (posX + WndMenuEditor.BTN_HEIGHT > width + x) {
                posY = slot.bottom() + GAP;
                posX = x + GAP + tempStartColumnPos;
            }
        }
        if (addBtn.visible) {
            addBtn.setRect(posX, posY, WndMenuEditor.BTN_HEIGHT, WndMenuEditor.BTN_HEIGHT);
            PixelScene.align(addBtn);
            posY = addBtn.bottom() + WndTitledMessage.GAP;
        } else {
            Slot lastSlot = slots.getLast();
            if (lastSlot != null) posY = lastSlot.bottom() + WndTitledMessage.GAP;
        }

        height = posY - y;
    }

    public float getStartColumnPos(){
        return startColumnPos;
    }

    public void setStartColumnPos(float startColumnPos) {
        this.startColumnPos = startColumnPos;
    }
}