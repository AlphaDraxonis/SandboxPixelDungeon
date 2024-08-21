package com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditItemComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPartModify;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;

public /*sealed*/ abstract class ItemActionPart implements ActionPart {

    private final Item item;
    private final int cell, quantity;
    private Heap.Type heapType;
    private boolean heapAutoExplored, heapHaunted;
    private float heapShopPrice;

    private ItemActionPart(Item item, int pos) {
        this.item = item;
        this.cell = pos;
        this.quantity = item.quantity();
        heapShopPrice = 1f;

        redo();
    }

    protected void place() {
        place(item, cell, quantity, heapType, heapAutoExplored, heapHaunted, heapShopPrice);
    }

    protected void remove() {
        Heap heap = remove(item, cell, quantity);
        if (heap != null) {
            heapType = heap.type;
            heapAutoExplored = heap.autoExplored;
            heapHaunted = heap.haunted;
            heapShopPrice = heap.priceMultiplier;
        }
    }

    protected static void place(Item item, int cell, int quantity, Heap.Type heapType, boolean heapAutoExplored, boolean heapHaunted, float heapShopPrice) {
        item.quantity(quantity);
        Heap heap = Dungeon.level.drop(item, cell);
        heap.autoExplored = heapAutoExplored;
        heap.haunted = heapHaunted;
        heap.priceMultiplier = heapShopPrice;
        if (heapType != null) {
            heap.type = heapType;
            EditorScene.updateHeapImage(heap);
        }
        heap.updateSubicon();
    }

    protected static Heap remove(Item item, int cell, int quantity) {
        Heap heap = Dungeon.level.heaps.get(cell);
        if (heap != null) {
            heap.remove(item, quantity);
            return heap;
        }
        return null;
    }

    @Override
    public boolean hasContent() {
        return true;
    }

    //Not properly working bc placement affects heap item order which is out of control for this class
    private static final class PlaceClass extends ItemActionPart {//ONLY works if there is no heap, otherwise use the heap modify action

        public PlaceClass(Item item, int pos) {
            super(item, pos);
        }

        @Override
        public void undo() {
            remove();
        }

        @Override
        public void redo() {
            place();
        }
    }

    //
    public static final class Remove extends ItemActionPart {
        public Remove(Item item, int pos) {
            super(item, pos);
        }

        @Override
        public void undo() {
            place();
        }

        @Override
        public void redo() {
            remove();
        }
    }

    public static ActionPart Place(Item item, int pos) {
        Heap heap = Dungeon.level.heaps.get(pos);
        if (heap == null) return new PlaceClass(item, pos);
        HeapActionPart.Modify modify = new HeapActionPart.Modify(heap);
        place(item, pos, item.quantity(), null, false, false, 1f);
        modify.finish();
        return modify;
    }

    //Same problem, so should instead just always use Heap modify, though that would be difficult...

    /**
     * WARNING!! This is only for the Inventory!!!
     */
    public static final class Modify implements ActionPartModify {

        private final Item realItem;
        private final Item before;
        private Item after;

        public Modify(Item item) {
            before = item.getCopy();
            realItem = item;
        }

        @Override
        public void undo() {
            if (realItem != null) realItem.copyStats(before);
        }

        @Override
        public void redo() {
            if (realItem != null) realItem.copyStats(after);
        }

        @Override
        public boolean hasContent() {
            return !EditItemComp.areEqual(before, after);
        }

        @Override
        public void finish() {
            after = realItem.getCopy();
        }
    }
}