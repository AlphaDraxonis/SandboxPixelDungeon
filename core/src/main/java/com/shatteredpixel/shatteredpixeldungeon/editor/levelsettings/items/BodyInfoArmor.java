package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.items;


import static com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage.GAP;

import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndMenuEditor;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;

public class BodyInfoArmor extends WndInfoEq.Body {

    //TODO agument!!!



    public BodyInfoArmor(Armor item) {
        super(item);
    }

    @Override
    protected String createText() {
        return item instanceof ClassArmor ? item.desc() : item.info();
    }

}
