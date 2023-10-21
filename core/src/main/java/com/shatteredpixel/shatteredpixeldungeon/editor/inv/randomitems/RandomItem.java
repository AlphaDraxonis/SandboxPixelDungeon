package com.shatteredpixel.shatteredpixeldungeon.editor.inv.randomitems;

import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Bundle;

public class RandomItem extends Item {

    {
        image = ItemSpriteSheet.SOMETHING;
    }

    public boolean useDeck;
    public Generator.Category category;


    public Item generate() {
        boolean catWasNull = category == null;
        if (catWasNull) category = Generator.randomCategory();
        float[] defaultProbs = null;
        if (!useDeck) {
            defaultProbs = category.defaultProbs;
            category.defaultProbs = null;
        }
        Item ret = Generator.random(category);
        if (!useDeck) category.defaultProbs = defaultProbs;
        if (catWasNull) category = null;
        return ret;
    }


    private static final String CATEGORY = "category";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        if (category != null) bundle.put(CATEGORY, category);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (bundle.contains(CATEGORY))
            category = bundle.getEnum(CATEGORY, Generator.Category.class);
    }
}