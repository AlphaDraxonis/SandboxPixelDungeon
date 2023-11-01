package com.shatteredpixel.shatteredpixeldungeon.editor.util;

import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

public class ItemWithPos implements Bundlable {

    private Item item;
    private int pos;

    public ItemWithPos(){}

    public ItemWithPos(Item item, int pos) {
        this.item = item;
        this.pos = pos;
    }

    public ItemWithPos(Item item, Heap heap) {
        this.item = item;
        this.pos = heap.pos;
    }

    public int pos() {
        return pos;
    }

    public Item item() {
        return item;
    }

    private static final String ITEM = "item";
    private static final String POS = "pos";

    @Override
    public void restoreFromBundle(Bundle bundle) {
        item = (Item) bundle.get(ITEM);
        pos = bundle.getInt(POS);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(ITEM, item);
        bundle.put(POS, pos);
    }
}