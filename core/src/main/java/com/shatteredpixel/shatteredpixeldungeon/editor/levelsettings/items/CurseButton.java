package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.items;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.ChangeButton;

public class CurseButton extends CheckBox {

    private Item item;
    public  CurseButton(Item item){
        super("Cursed");
        this.item=item;
        checked(item.cursed);
    }

    @Override
    protected void onClick() {
        super.onClick();
        item.cursed = checked();
        onChange();
    }

    protected  void onChange(){
    }

}
