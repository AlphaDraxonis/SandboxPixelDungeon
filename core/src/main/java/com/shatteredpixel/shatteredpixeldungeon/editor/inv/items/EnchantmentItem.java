package com.shatteredpixel.shatteredpixeldungeon.editor.inv.items;

import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditEnchantmentComp;
import com.shatteredpixel.shatteredpixeldungeon.items.EnchantmentLike;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Image;

public class EnchantmentItem extends EditorItem<EnchantmentLike> {

    public EnchantmentItem(EnchantmentLike ench) {
        this.obj = ench;
    }

    @Override
    public DefaultEditComp<?> createEditComponent() {
        return new EditEnchantmentComp(this);
    }

    @Override
    public String name() {
        return getObject().name();
    }

    @Override
    public Image getSprite() {
        return getImage(getObject());
    }

    @Override
    public void place(int cell) {
        //can't be placed
    }

    @Override
    public Item getCopy() {
        return new EnchantmentItem(getObject());
    }

    public static Image getImage(EnchantmentLike ench) {

        return new ItemSprite(ItemSpriteSheet.SOMETHING);
    }
}