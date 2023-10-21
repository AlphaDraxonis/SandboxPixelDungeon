package com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditHeapComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPartModify;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;

public /*sealed*/ abstract class HeapActionPart implements ActionPart {

    private Heap heap, copyForUndo;

    private HeapActionPart(Heap heap) {
        this.heap = heap;
        copyForUndo = (Heap) heap.getCopy();

        redo();
    }

    protected void place() {
        place(copyForUndo);
        heap = copyForUndo;
        copyForUndo = (Heap) heap.getCopy();
    }

    protected void remove() {
        remove(heap);
    }

    protected static void place(Heap heap) {
        Dungeon.level.heaps.put(heap.pos, heap);
        Item i = heap.peek();
        i.image = CustomDungeon.getDungeon().getItemSpriteOnSheet(i);
        EditorScene.add(heap);
    }

    protected static void remove(Heap heap) {
        heap = EditorScene.customLevel().heaps.get(heap.pos, heap);//This is because another place action could swap the actual heap with another copy
        heap.destroy();
    }

    @Override
    public boolean hasContent() {
        return true;
    }

    public static final class Place extends HeapActionPart {

        public Place(Heap heap) {
            super(heap);
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

    public static final class Remove extends HeapActionPart {
        public Remove(Heap heap) {
            super(heap);
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

    public static final class Modify implements ActionPartModify {

        private final Heap before;
        private Heap after;

        public Modify(Heap heap) {
            before = heap.getCopy();
            after = heap;
        }

        @Override
        public void undo() {

            Heap heapAtCell = EditorScene.customLevel().heaps.get(after.pos);

            remove(heapAtCell);

            place(before.getCopy());
        }

        @Override
        public void redo() {
            Heap heapAtCell = EditorScene.customLevel().heaps.get(after.pos);

            if (heapAtCell != null) remove(heapAtCell);

            place(after.getCopy());

        }

        @Override
        public boolean hasContent() {
            return !EditHeapComp.areEqual(before, after);
        }

        @Override
        public void finish() {
            after = after.getCopy();
        }
    }
}