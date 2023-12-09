package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndMenuEditor;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.level.LevelTab;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerFloatModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerTextModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class EditHeapComp extends DefaultEditComp<Heap> {

    protected final CheckBox autoExplored;
    protected final IconButton autoExploredInfo;

    protected final CheckBox haunted;
    protected final IconButton hauntedInfo;
    protected final Spinner priceMultiplier;

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

        priceMultiplier = new Spinner(new SpinnerFloatModel(0.1f, 10f, heap.priceMultiplier, 2, 0.1f,false) {
            @Override
            public float getInputFieldWith(float height) {
                return Spinner.FILL;
            }

            @Override
            public int getClicksPerSecondWhileHolding() {
                return 20;
            }

            @Override
            public String getDisplayString() {
                Item copy = heap.items.getLast().getCopy();
                copy.identify(false);
                float price = copy.value() * 5 * EditorScene.customLevel().levelScheme.getPriceMultiplier();
                return super.getDisplayString() + " = " + ((int)(getAsFloat() * price) + " " + Messages.get(Gold.class, "name"));
            }
        }, Messages.get(LevelTab.class, "shop_price"), 8);
        ((SpinnerIntegerModel) priceMultiplier.getModel()).setAbsoluteMinAndMax(0f, 10000f);
        priceMultiplier.addChangeListener(() -> heap.priceMultiplier = ((SpinnerFloatModel) priceMultiplier.getModel()).getAsFloat());
        add(priceMultiplier);

        heapType = new HeapTypeSpinner(heap);
        add(heapType);

        itemContainer = new ItemContainer<Item>(heap.items, this) {
            @Override
            protected void doAddItem(Item item) {
                EditHeapComp.this.obj.drop(item);
                updateObj();
            }

            @Override
            protected void onUpdateItem() {
                EditorScene.updateHeapImage(obj);
                super.onUpdateItem();
                obj.updateSubicon();
                updateObj();
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
        if (obj.type == Heap.Type.HEAP) return Messages.get(EditHeapComp.class, "desc_heap_open");
        if (obj.type == Heap.Type.FOR_SALE)
            return Messages.get(EditHeapComp.class, "desc_heap_for_sale");
        if (obj.type == Heap.Type.LOCKED_CHEST) return EditorUtilies.addGoldKeyDescription(obj.info(), Dungeon.level);
        if (obj.type == Heap.Type.CRYSTAL_CHEST) return EditorUtilies.addCrystalKeyDescription(obj.info(), Dungeon.level);
        return obj.info();
    }

    private String getTitle() {
        return getTitle(obj);
    }

    public static String getTitle(Heap heap) {
        String title;
        if (heap.type == Heap.Type.HEAP) title = Messages.get(EditHeapComp.class, "title_heap_open");
        else if (heap.type == Heap.Type.FOR_SALE)
            title = Messages.get(EditHeapComp.class, "title_heap_open");
        else title = Messages.titleCase(heap.title());
        return title + " " + EditorUtilies.cellToString(heap.pos);
    }

    @Override
    public Image getIcon() {
        Image img = new Image();
        img.copy(obj.sprite);
        return img;
    }

    @Override
    protected void updateObj() {
        obj.sprite.view(obj).place(obj.pos);
        if (title instanceof IconTitle) {
            ((IconTitle) title).label(getTitle());
            ((IconTitle) title).icon(getIcon());
        }
        desc.text(createDescription());

        updateHauntedEnabledState();
        obj.updateSubicon();

        priceMultiplier.setValue(SpinnerFloatModel.convertToInt(obj.priceMultiplier, 2));

        super.updateObj();
    }

    @Override
    protected void onShow(boolean fullyInitialized) {
        super.onShow(fullyInitialized);
        if (fullyInitialized) {
            updateObj();
            itemContainer.updateItemListOrder();
        }
    }

    private void updateHauntedEnabledState() {
        if (obj.type == Heap.Type.HEAP || obj.type == Heap.Type.FOR_SALE) {
            haunted.checked(false);
            haunted.enable(false);
        } else haunted.enable(true);
        priceMultiplier.enable(obj.type == Heap.Type.FOR_SALE);
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

        if (priceMultiplier != null) {
            priceMultiplier.setRect(x, posY, width, WndMenuEditor.BTN_HEIGHT);
            posY = priceMultiplier.bottom() + WndTitledMessage.GAP;
        }

        if (heapType != null) {
            heapType.setRect(x, posY, width, WndMenuEditor.BTN_HEIGHT);
            posY = heapType.bottom() + WndTitledMessage.GAP;
        }

        if (itemContainer != null) {
            itemContainer.setRect(x, posY, width, -1);
            posY = itemContainer.bottom() + WndTitledMessage.GAP;
        }

        height = (int)(posY - y - WndTitledMessage.GAP);
    }

    public static boolean areEqual(Heap a, Heap b) {
        if (a == null || b == null) return false;
        if (a.getClass() != b.getClass()) return false;
        if (a.autoExplored != b.autoExplored) return false;
        if (a.haunted != b.haunted) return false;
        if (a.type != b.type) return false;
        if (a.priceMultiplier != b.priceMultiplier) return false;
        return DefaultEditComp.isItemListEqual(a.items, b.items);
    }


    private class HeapTypeSpinner extends Spinner {

        public HeapTypeSpinner(Heap heap) {
            super(new HeapTypeSpinnerModel(heap), " " + Messages.get(EditHeapComp.class, "type"), 10);

            addChangeListener(EditHeapComp.this::updateObj);
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
            if (type == Heap.Type.HEAP)
                return Messages.get(EditHeapComp.class, "title_heap_open");
            if (type == Heap.Type.FOR_SALE)
                return Messages.get(EditHeapComp.class, "title_heap_for_sale");
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