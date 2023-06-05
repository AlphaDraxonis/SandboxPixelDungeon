package com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.editcomps;

import com.alphadraxonis.sandboxpixeldungeon.Chrome;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.Koord;
import com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.WndMenuEditor;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.Spinner;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.SpinnerTextModel;
import com.alphadraxonis.sandboxpixeldungeon.items.Heap;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.ui.CheckBox;
import com.alphadraxonis.sandboxpixeldungeon.ui.IconButton;
import com.alphadraxonis.sandboxpixeldungeon.ui.Icons;
import com.alphadraxonis.sandboxpixeldungeon.windows.IconTitle;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class EditHeapComp extends DefaultEditComp<Heap> {

    //autoexplored, haunted, goToItems

    protected final CheckBox autoExplored;
    protected final IconButton autoExploredInfo;

    protected final CheckBox haunted;
    protected final IconButton hauntedInfo;

    protected final HeapTypeSpinner heapType;

    protected final ItemContainer itemContainer;

    public EditHeapComp(Heap heap) {
        super(heap);

        autoExplored = new CheckBox(Messages.get(EditHeapComp.class, "auto_explored")) {
            @Override
            public void checked(boolean value) {
                super.checked(value);
                heap.autoExplored = value;
            }
        };
        add(autoExplored);
        autoExplored.checked(heap.autoExplored);

        autoExploredInfo = new IconButton(Icons.get(Icons.INFO)) {
            @Override
            protected void onClick() {
                EditorScene.show(new WndTitledMessage(Icons.get(Icons.INFO), Messages.titleCase(Messages.get(EditHeapComp.class, "auto_explored")),
                        Messages.get(EditHeapComp.class, "auto_explored_info")));
            }
        };
        add(autoExploredInfo);


        haunted = new CheckBox(Messages.get(EditHeapComp.class, "haunted")) {
            @Override
            public void checked(boolean value) {
                super.checked(value);
                heap.haunted = value;
            }
        };
        add(haunted);
        haunted.checked(heap.haunted);

        hauntedInfo = new IconButton(Icons.get(Icons.INFO)) {
            @Override
            protected void onClick() {
                EditorScene.show(new WndTitledMessage(Icons.get(Icons.INFO), Messages.titleCase(Messages.get(EditHeapComp.class, "haunted")),
                        Messages.get(EditHeapComp.class, "haunted_info")));
            }
        };
        add(hauntedInfo);

        heapType = new HeapTypeSpinner(heap);
        add(heapType);

        itemContainer = new ItemContainer(heap.items, this) {
            @Override
            protected void addItem(Item item) {
                EditHeapComp.this.item.drop(item);
            }

            @Override
            protected void onUpdateItem() {
                EditHeapComp.this.item.updateSubicon();
                EditorScene.updateHeapImage(EditHeapComp.this.item);
                super.onUpdateItem();
            }

            @Override
            protected boolean removeSlot(ItemContainer.Slot slot) {
                if (itemList.size() > 1) return super.removeSlot(slot);
                return false;
            }
        };
        add(itemContainer);

        updateHauntedEnabledState();
    }


    @Override
    protected Component createTitle() {
        return new IconTitle(getIcon(), getTitle());
    }

    @Override
    protected String createDescription() {
        if (item.type == Heap.Type.HEAP) return Messages.get(EditHeapComp.class, "desc_heap_open");
        if (item.type == Heap.Type.FOR_SALE)
            return Messages.get(EditHeapComp.class, "desc_heap_for_sale");
        return item.info();
    }

    private String getTitle() {
        String title;
        if (item.type == Heap.Type.HEAP) title = Messages.get(EditHeapComp.class, "title_heap_open");
        else if (item.type == Heap.Type.FOR_SALE) title = Messages.get(EditHeapComp.class, "title_heap_open");
        else title = Messages.titleCase(item.title());
        return title + " " + new Koord(item.pos);
    }

    @Override
    public Image getIcon() {
        Image img = new Image();
        img.copy(item.sprite);
        return img;
    }

    @Override
    protected void updateItem() {
        item.sprite.view(item);
        if (title instanceof IconTitle) {
            ((IconTitle) title).label(getTitle());
            ((IconTitle) title).icon(getIcon());
        }
        desc.text(createDescription());

        updateHauntedEnabledState();
        item.updateSubicon();

        super.updateItem();
    }

    private void updateHauntedEnabledState() {
        if (item.type == Heap.Type.HEAP || item.type == Heap.Type.FOR_SALE) {
            haunted.checked(false);
            haunted.enable(false);
        } else haunted.enable(true);
    }

    @Override
    protected void layout() {
        super.layout();

        float posY = height + WndTitledMessage.GAP * 2 - 1;

        if (autoExplored != null) {
            autoExplored.setRect(x, posY, width - WndMenuEditor.BTN_HEIGHT - WndTitledMessage.GAP, WndMenuEditor.BTN_HEIGHT);
            autoExploredInfo.setRect(autoExplored.right() + WndTitledMessage.GAP, posY, WndMenuEditor.BTN_HEIGHT, WndMenuEditor.BTN_HEIGHT);
            posY = autoExplored.bottom() + WndTitledMessage.GAP;
        }

        if (haunted != null) {
            haunted.setRect(x, posY, width - WndMenuEditor.BTN_HEIGHT - WndTitledMessage.GAP, WndMenuEditor.BTN_HEIGHT);
            hauntedInfo.setRect(haunted.right() + WndTitledMessage.GAP, posY, WndMenuEditor.BTN_HEIGHT, WndMenuEditor.BTN_HEIGHT);
            posY = haunted.bottom() + WndTitledMessage.GAP;
        }

        if (heapType != null) {
            heapType.setRect(x, posY, width, WndMenuEditor.BTN_HEIGHT);
            posY = heapType.bottom() + WndTitledMessage.GAP;
        }

        if (itemContainer != null) {
            itemContainer.setRect(x, posY, width, -1);
            posY = itemContainer.bottom() + WndTitledMessage.GAP;
        }

        height = posY - y - WndTitledMessage.GAP + 1;
    }


    private class HeapTypeSpinner extends Spinner {

        public HeapTypeSpinner(Heap heap) {
            super(new HeapTypeSpinnerModel(heap), " "+Messages.get(EditHeapComp.class,"type"), 10);

            addChangeListener(EditHeapComp.this::updateItem);
        }
    }

    private static class HeapTypeSpinnerModel extends SpinnerTextModel {

        private Heap heap;

        public HeapTypeSpinnerModel(Heap heap) {
            super(true, getIndex(heap.type), (Object[]) Heap.Type.values());
            this.heap = heap;
        }

        @Override
        public Component createInputField(int fontSize) {
            inputField = new Spinner.SpinnerTextBlock(Chrome.get(getChromeType()), 8);
            return inputField;
        }

        @Override
        protected String getAsString(Object value) {
            Heap.Type type = (Heap.Type) value;
            heap.type = type;
            if (type == Heap.Type.HEAP) return Messages.get(EditHeapComp.class, "title_heap_open");
            if (type == Heap.Type.FOR_SALE) return Messages.get(EditHeapComp.class, "title_heap_for_sale");
            return heap.title();
        }


        private static int getIndex(Heap.Type type) {
            Heap.Type[] types = Heap.Type.values();
            for (int i = 0; i < types.length; i++) {
                if (types[i] == type) return i;
            }
            return -1;
        }

    }

}