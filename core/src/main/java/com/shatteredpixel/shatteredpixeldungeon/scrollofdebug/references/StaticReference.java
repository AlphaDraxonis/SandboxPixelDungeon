package com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.references;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.watabou.noosa.Image;

public class StaticReference extends Reference {

    public StaticReference(Class<?> type, String name) {
        super(type, null, name);
    }

    @Override
    public Image createIcon() {
        if (getType() == Dungeon.class) return Icons.ENTER.get();
        return super.createIcon();
    }

    @Override
    public Object valueViaParent() throws ReferenceNotFoundException {
        return null;//tzz maybe not correct
    }
}