package com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.parts;

import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.ActionPart;
import com.alphadraxonis.sandboxpixeldungeon.items.Heap;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;

public /*sealed*/ abstract class ItemActionPart implements ActionPart {

    private final Item item;
    private final int cell, quatity;

    private ItemActionPart(Item item, int pos) {
        this.item = item;
        this.cell = pos;
        this.quatity = item.quantity();

        redo();
    }

    protected void place() {
        item.quantity(quatity);//when merging, the quantity is always set to 0
        Dungeon.level.drop(item, cell);
    }

    protected void remove() {
        Heap heap = Dungeon.level.heaps.get(cell);
        heap.remove(item, quatity);
    }

    @Override
    public boolean hasContent() {
        return true;
    }

    public static final class Place extends ItemActionPart {

        public Place(Item item, int pos) {
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
}