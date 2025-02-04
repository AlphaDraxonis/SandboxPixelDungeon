package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.level.LevelTab;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledCheckBox;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerEnumModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerFloatModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerLikeButton;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WraithSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Image;

import java.util.Collections;
import java.util.Locale;

public class EditHeapComp extends DefaultEditComp<Heap> {
    
    protected StyledCheckBox haunted;
    protected Spinner priceMultiplier;

    protected SpinnerLikeButton heapType;

    protected ItemContainer<Item> itemContainer;

    public EditHeapComp(Heap heap) {
        super(heap);

        haunted = new StyledCheckBox(Messages.get(this, "haunted")) {
            @Override
            public void checked(boolean value) {
                super.checked(value);
                heap.haunted = value;
            }
        };
        haunted.icon(new WraithSprite());
        haunted.checked(heap.haunted);
        add(haunted);

        priceMultiplier = new Spinner(new SpinnerFloatModel(0.1f, 10f, heap.priceMultiplier, 2, 0.1f) {
            @Override
            public float getInputFieldWidth(float height) {
                return Spinner.FILL;
            }

            @Override
            protected String displayString(Object value) {
                float price = Item.trueValue(heap.items.getLast()) * 5 * Dungeon.level.levelScheme.shopPriceMultiplier;
                return super.displayString(value) + " = " + ((int)(getAsFloat() * price) + " " + Messages.get(Gold.class, "name"));
            }
        }, Messages.get(LevelTab.class, "shop_price"), 8);
        ((SpinnerIntegerModel) priceMultiplier.getModel()).setAbsoluteMinAndMax(0f, 10000f);
        priceMultiplier.addChangeListener(() -> heap.priceMultiplier = ((SpinnerFloatModel) priceMultiplier.getModel()).getAsFloat());
        add(priceMultiplier);

        heapType = new SpinnerLikeButton(new HeapTypeSpinnerModel(heap), Messages.get(EditHeapComp.class, "type"), 9);
        add(heapType);

        itemContainer = new ItemContainer<Item>(heap.items, this, true, 1, Integer.MAX_VALUE) {
            @Override
            protected void doAddItem(Item item) {
                EditHeapComp.this.obj.drop(item);
                updateObj();
            }

            @Override
            protected void updateItemListOrder() {
                super.updateItemListOrder();
                Collections.reverse(slots);
                layout();
            }

            @Override
            protected void showWndEditItemComp(ItemContainer<Item>.Slot slot, Item item) {
                EditorScene.show(new EditCompWindow(item, heap, advancedListPaneItem) {
                    {
                        Window w = EditorUtilities.getParentWindow(EditHeapComp.this);
                        if (w instanceof EditCompWindowTabbed)
                            ((EditItemComp) content).reorderHeapComp.editCompWindowTabbed = (EditCompWindowTabbed) w;
                    }
                    @Override
                    protected void onUpdate() {
                        super.onUpdate();
                        slot.item(item);
                        EditorScene.updateHeapImage(obj);
                        obj.updateSubicon();
                        updateObj();
                    }
                });
            }
        };
        add(itemContainer);

        updateHauntedEnabledState();
    }

    @Override
    protected void updateStates() {
        super.updateStates();
        haunted.checked(obj.haunted);
        priceMultiplier.setValue(obj.priceMultiplier);
        heapType.setValue(obj.type);
        itemContainer.setItemList(obj.items);
    }

    @Override
    protected String createDescription() {
        if (obj.type == Heap.Type.HEAP) return Messages.get(EditHeapComp.class, "desc_heap_open");
        if (obj.type == Heap.Type.FOR_SALE)
            return Messages.get(EditHeapComp.class, "desc_heap_for_sale");
        if (obj.type == Heap.Type.LOCKED_CHEST) return EditorUtilities.addGoldKeyDescription(obj.info(), Dungeon.level);
        if (obj.type == Heap.Type.CRYSTAL_CHEST) return EditorUtilities.addCrystalKeyDescription(obj.info(), Dungeon.level);
        return obj.info();
    }

    @Override
    protected String createTitleText() {
        return getTitle(obj);
    }

    public static String getTitle(Heap heap) {
        String title;
        if (heap.type == Heap.Type.HEAP) title = Messages.get(EditHeapComp.class, "title_heap_open");
        else if (heap.type == Heap.Type.FOR_SALE)
            title = Messages.get(EditHeapComp.class, "title_heap_open");
        else title = Messages.titleCase(heap.title());
        return title + " " + EditorUtilities.cellToString(heap.pos);
    }

    @Override
    public Image getIcon() {
        Image img = new Image();
        img.copy(obj.sprite);
        return img;
    }

    @Override
	public void updateObj() {
        obj.sprite.view(obj).place(obj.pos);

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
        } else {
            haunted.enable(true);
        }
        priceMultiplier.enable(obj.type == Heap.Type.FOR_SALE);
    }

    @Override
    protected void layout() {
        super.layout();
        layoutCompsLinear(priceMultiplier);
        layoutCompsInRectangles(heapType, haunted);
        layoutCompsLinear(itemContainer);
    }

    public static boolean areEqual(Heap a, Heap b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        if (a.getClass() != b.getClass()) return false;
        if (a.autoExplored != b.autoExplored) return false;
        if (a.haunted != b.haunted) return false;
        if (a.type != b.type) return false;
        if (a.priceMultiplier != b.priceMultiplier) return false;
        return EditItemComp.isItemListEqual(a.items, b.items);
    }

    private class HeapTypeSpinnerModel extends SpinnerEnumModel<Heap.Type> {

        public HeapTypeSpinnerModel(Heap heap) {
            super(Heap.Type.class, heap.type, v -> {
                heap.type = v;
                EditHeapComp.this.updateObj();
            });
        }
        
        @Override
        protected String displayString(Object value) {
            switch((Heap.Type) value){
                case HEAP:
                    return Messages.get(EditHeapComp.class, "title_heap_open");
                case FOR_SALE:
                    return Messages.get(EditHeapComp.class, "title_heap_for_sale");
                default:
                   return Messages.get(Heap.class, ((Heap.Type) value).name().toLowerCase(Locale.ENGLISH));
            }
        }
    }

}