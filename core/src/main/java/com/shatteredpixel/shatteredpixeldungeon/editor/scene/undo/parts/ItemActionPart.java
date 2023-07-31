package com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPart;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;

public /*sealed*/ abstract class ItemActionPart implements ActionPart {

    private final Item item;
    private final int cell, quantity;
    private Heap.Type heapType;

    private ItemActionPart(Item item, int pos) {
        this.item = item;
        this.cell = pos;
        this.quantity = item.quantity();

        redo();
    }

    protected void place() {
        place(item, cell, quantity, heapType);
    }

    protected void remove() {
        heapType = remove(item, cell, quantity);
    }

    protected static void place(Item item, int cell, int quantity, Heap.Type heapType) {
        item.quantity(quantity);
        Heap heap = Dungeon.level.drop(item, cell);
        if (heapType != null) {
            heap.type = heapType;
            EditorScene.updateHeapImage(heap);
        }
    }

    protected static Heap.Type remove(Item item, int cell, int quantity) {
        Heap heap = Dungeon.level.heaps.get(cell);
        if (heap != null) {
            Heap.Type heapType = heap.type;
            heap.remove(item, quantity);
            return heapType;
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
        place(item, pos, item.quantity(), null);
        modify.finish();
        return modify;
    }

    //Same problem, so should instead just always use Heap modify, though that would be difficult...
//    public static final class Modify implements ActionPartModify {
//
//        private final Item before;
//        private Item after;
//        private final int cell;
//
//        public Modify(Item item, int cell) {
//            before = (Item) item.getCopy();
//            after = item;
//            this.cell = cell;
//        }
//
//        @Override
//        public void undo() {
//            remove(after, cell);
//            place((Item) before.getCopy(), cell);
//            System.err.println("B4c: "+before.quantity());
//            System.err.println("AFTERc: "+after.quantity());
//        }
//
//        @Override
//        public void redo() {
//            remove(before, cell);
//            place((Item) after.getCopy(), cell);
//            System.err.println("B4: "+before.quantity());
//            System.err.println("AFTER: "+after.quantity());
//
//        }
//
//        @Override
//        public boolean hasContent() {
//            return !EditItemComp.areEqual(before, after);
//        }
//
//        @Override
//        public void finish() {
//            after = (Item) after.getCopy();
//        }
//    }
}