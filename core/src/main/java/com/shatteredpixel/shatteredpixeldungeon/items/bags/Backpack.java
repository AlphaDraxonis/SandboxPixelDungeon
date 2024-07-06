package com.shatteredpixel.shatteredpixeldungeon.items.bags;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Backpack extends Bag {

    {
        image = ItemSpriteSheet.BACKPACK;
    }

    @Override
    public int capacity() {
        int cap = super.capacity();
        for (Item item : items){
            if (item instanceof Bag){
                cap++;
            }
        }
        return cap;
    }

    @Override
    public boolean canHold(Item item) {
        return super.canHold(item) && !(item instanceof Backpack);
    }

    @Override
    public int value() {
        return 50;
    }
}