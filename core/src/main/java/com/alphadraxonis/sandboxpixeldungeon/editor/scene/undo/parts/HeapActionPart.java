package com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.parts;

import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.ActionPart;
import com.alphadraxonis.sandboxpixeldungeon.items.Heap;

public /*sealed*/ abstract class HeapActionPart implements ActionPart {

    private Heap heap, copyForUndo;
    private final int cell;

    private HeapActionPart(Heap heap) {
        this.heap = heap;
        this.cell = heap.pos;
        copyForUndo = (Heap) heap.getCopy();
        
        redo();
    }

    protected void place() {
        Dungeon.level.heaps.put(copyForUndo.pos, copyForUndo);
        EditorScene.add(copyForUndo);
        heap = copyForUndo;
        copyForUndo = (Heap) heap.getCopy();
    }

    protected void remove() {
        heap = EditorScene.customLevel().heaps.get(cell,heap);//This is because another place action could swap the actual heap with another copy
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
}