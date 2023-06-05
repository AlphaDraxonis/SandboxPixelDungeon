package com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.items;

import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.ui.CheckBox;

public class CurseButton extends CheckBox {

    private Item item;

    public CurseButton(Item item) {
        super(Messages.get(CurseButton.class, "label"));
        this.item = item;
        checked(item.cursed);
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