package com.shatteredpixel.shatteredpixeldungeon.editor.ui;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditCompWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndMenuEditor;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.InventorySlot;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Game;
import com.watabou.noosa.ui.Component;

import java.util.List;

//
//List items can only be replaced, not appended or removed!
//
public class ItemSelectorList<T extends Item> extends Component {

    protected RenderedTextBlock label;

    protected InventorySlot[] itemSlots;

    protected final List<T> list;

    public ItemSelectorList(List<T> list, String label, int fontSize) {

        this.list = list;

        this.label = PixelScene.renderTextBlock(label, fontSize);
        add(this.label);

        itemSlots = new InventorySlot[list.size()];

        int index = 0;
        for (T t : list) {
            itemSlots[index] = new OwnItemSlot(t, index);
            add(itemSlots[index]);
            index++;
        }
        if (index < itemSlots.length) {
            for (; index < itemSlots.length; index++) {
                itemSlots[index] = new OwnItemSlot(new Item(), index);
                add(itemSlots[index]);
            }
        }

    }

    private static final int GAP = 2;

    @Override
    protected void layout() {

        label.maxWidth((int) width);
        label.setPos(x, y);

        //layout in 2 rows
        float slotSize = Math.min(height, WndMenuEditor.BTN_HEIGHT);
        float minX = width + GAP;
        float minXRequired = Math.min(label.width(), width * 0.4f);
        int index = 0;
        while (minX > minXRequired && index < itemSlots.length) {
            minX -= slotSize + GAP;
            index++;
        }
        float posY;
        if (minX <= label.width() + ItemSelector.MIN_GAP) posY = label.bottom() + GAP * 2;
        else {
            posY = y;
            label.setPos(x, y + (height - label.height()) * 0.5f);
        }
        minX += x;
        float posX = minX;
        posX -= slotSize + GAP;//for first slot
        for (InventorySlot slot : itemSlots) {
            if (posX + slotSize > width) {
                posX = minX;
                posY += slotSize + GAP;
            } else {
                posX += slotSize + GAP;
            }
            slot.setRect(posX, posY, slotSize, slotSize);
            PixelScene.align(slot);
        }
        height = posY + slotSize - y;
    }

    public void setSelectedItem(T selectedItem, int index) {
        list.set(index, selectedItem);
        if (selectedItem != null)
            selectedItem.image = Dungeon.customDungeon.getItemSpriteOnSheet(selectedItem);
        itemSlots[index].item(selectedItem);
    }

    public List<T> getList() {
        return list;
    }

    public void updateItem(int index) {
        if (index < list.size()) itemSlots[index].item(list.get(index));
    }

    public void change(int index) {
    }

    private class OwnItemSlot extends InventorySlot {

        protected int index;

        public OwnItemSlot(Item item, int index) {
            super(item);
            this.index = index;
        }

        @Override
        protected void onClick() {
            if (item.getClass() == Item.class) {
                change(index);
                return;
            }

            super.onClick();
            Window w = new EditCompWindow(item) {
                @Override
                protected void onUpdate() {
                    super.onUpdate();
                    updateItem(index);
                }
            };
            if (Game.scene() instanceof EditorScene) EditorScene.show(w);
            else Game.scene().addToFront(w);
        }

        @Override
        protected boolean onLongClick() {
            change(index);
            return true;
        }

        @Override
        public void item(Item item) {
            super.item(item);
            bg.visible = true;//gold and bags should have bg
        }

        @Override
        protected void viewSprite(Item item) {
            if (!EditorItem.class.isAssignableFrom(item.getClass())) {
                super.viewSprite(item);
                return;
            }
            if (sprite != null) {
                remove(sprite);
                sprite.destroy();
            }
            sprite = ((EditorItem) item).getSprite();
            if (sprite != null) addToBack(sprite);
            sendToBack(bg);
        }
    }
}