package com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.parts;

import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.ActionPart;
import com.alphadraxonis.sandboxpixeldungeon.items.Heap;

public class RemoveHeapActionPart implements ActionPart {

    private Heap heap, copyForUndo;

    public RemoveHeapActionPart(Heap heap) {
        this.heap = heap;
        copyForUndo = (Heap) heap.getCopy();
    }

    @Override
    public void undo() {
        Dungeon.level.heaps.put(copyForUndo.pos, copyForUndo);
        EditorScene.add(copyForUndo);
        heap = copyForUndo;
        copyForUndo = (Heap) heap.getCopy();
    }

    @Override
    public void redo() {
        heap.destroy();
        Dungeon.level.heaps.remove(heap.pos);
    }
}