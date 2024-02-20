package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.IconTitleWithSubIcon;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.InventorySlot;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class EditRemoverComp extends DefaultEditComp<EditorItem.NullItemClass> {

    private PriorityChanger priorityChanger;
    private RenderedTextBlock priorityLabel;

    public EditRemoverComp() {
        super(EditorItem.REMOVER_ITEM);
        initComps();
    }

    private void initComps() {

        priorityLabel = PixelScene.renderTextBlock(Messages.get(this, "priority"), 8);
        add(priorityLabel);

        priorityChanger = new PriorityChanger(EditorItem.REMOVER_PRIORITY);
        add(priorityChanger);
    }

    @Override
    protected void layout() {
        super.layout();
        height = EditorUtilies.layoutCompsLinear(WndTitledMessage.GAP * 2, this, priorityLabel, priorityChanger);
    }

    @Override
    protected IconTitleWithSubIcon createTitle() {
        return new IconTitleWithSubIcon(getIcon(), null, createTitleText());
    }

    @Override
    protected String createDescription() {
        return EditorItem.REMOVER_ITEM.desc();
    }

    @Override
    protected String createTitleText() {
        return Messages.titleCase(EditorItem.REMOVER_ITEM.name());
    }

    @Override
    public Image getIcon() {
        return EditorItem.REMOVER_ITEM.getSprite();
    }


    private static class PriorityChanger extends Component {

        protected static final int SLOT_SIZE = ItemSpriteSheet.SIZE;

        protected final EditorItem.RemoverPriority[] priorityArray;
        protected IconButton[] swappers;
        protected Slot[] slots;

        public PriorityChanger(EditorItem.RemoverPriority[] priorityArray) {
            this.priorityArray = priorityArray;

            slots = new Slot[priorityArray.length];
            swappers = new IconButton[priorityArray.length - 1];

            int i = 0;
            for (; i < swappers.length; i++) {
                slots[i] = new Slot(priorityArray[i].displayText(), priorityArray[i].getSprite());
                add(slots[i]);
                final int index = i;
                swappers[i] = new IconButton(Icons.SWAP.get()){
                    @Override
                    protected void onClick() {
                        swapSlots(index, index + 1);
                    }
                };
                add(swappers[i]);
            }

            slots[i] = new Slot(priorityArray[i].displayText(), priorityArray[i].getSprite());
            add(slots[i]);
        }

        protected void swapSlots(int index1, int index2) {
            EditorItem.RemoverPriority temp = priorityArray[index1];
            priorityArray[index1] = priorityArray[index2];
            priorityArray[index2] = temp;
            Slot slot = slots[index1];
            slots[index1] = slots[index2];
            slots[index2] = slot;
            layout();
        }

        @Override
        protected void layout() {
            super.layout();

            float posY = y;
            float nextY = y;
            float posX = x;
            float maxX = x + width;

            for (int i = 0; i < swappers.length; i++) {
                IconButton swapper = swappers[i];
                posX += SLOT_SIZE + 3;
                swapper.setRect(posX, posY + (SLOT_SIZE - swapper.icon().height()) * 0.5f, swapper.icon().width(), swapper.icon().height());

                Slot slot = slots[i];
                slot.setRect(posX - SLOT_SIZE - 3, posY, SLOT_SIZE, SLOT_SIZE);
                if (swapper.right() > maxX) {
                    posX = x;
                    posY = nextY;
                    swapper.setRect(posX, posY + (SLOT_SIZE - swapper.icon().height()) * 0.5f, swapper.icon().width(), swapper.icon().height());
                }
                nextY = Math.max(nextY, swapper.bottom() + 3);
                posX += swapper.width() + 3;

                if (posX + SLOT_SIZE > maxX) {
                    posX = x;
                    posY = nextY;
                }
            }

            Slot slot = slots[slots.length - 1];
            slot.setRect(posX, posY, SLOT_SIZE, SLOT_SIZE);

            height = nextY - y;

        }

        private static class Slot extends Button {

            protected ColorBlock bg;
            protected Image image;

            protected final String name;

            public Slot(String name, Image image) {
                this.name = name;

                if (image != null) {
                    add(this.image = image);
                }
            }

            @Override
            protected void createChildren(Object... params) {
                bg = new ColorBlock(1, 1, InventorySlot.NORMAL);
                add(bg);

                super.createChildren(params);
            }

            @Override
            protected void layout() {
                bg.size(width, height);
                bg.x = x;
                bg.y = y;

                if (image != null) {
                    image.x = x + (width - image.width()) * 0.5f;
                    image.y = y + (height - image.height()) * 0.5f;
                }


                super.layout();
            }

            @Override
            protected String hoverText() {
                return name;
            }

            @Override
            protected void onPointerDown() {
                bg.brightness(1.5f);
                super.onPointerDown();
            }

            @Override
            protected void onPointerUp() {
                bg.resetColor();
                super.onPointerUp();
            }
        }

    }

}