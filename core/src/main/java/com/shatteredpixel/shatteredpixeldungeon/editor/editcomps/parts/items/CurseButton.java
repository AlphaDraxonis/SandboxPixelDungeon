package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.items;

import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledCheckBox;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.TormentedSpiritSprite;

public class CurseButton extends StyledCheckBox {

    private Item item;

    public CurseButton(Item item) {
        super(Messages.get(CurseButton.class, "label"));
        this.item = item;
        checked(item.cursed);
        icon(new TormentedSpiritSprite());
    }

    @Override
    protected void onClick() {
        super.onClick();
        item.cursed = checked();
        onChange();
    }

    protected void onChange() {
    }

}