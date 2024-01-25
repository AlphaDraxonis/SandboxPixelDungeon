package com.shatteredpixel.shatteredpixeldungeon.editor.inv.items;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditBuffComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.DefaultListItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.EditorInventoryWindow;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class BuffItem extends EditorItem<Buff> {

    public BuffItem(){}
    public BuffItem(Buff buff) {
        this.obj = buff;
    }

    @Override
    public ScrollingListPane.ListItem createListItem(EditorInventoryWindow window) {
        return new DefaultListItem(this, window, Messages.titleCase(name()), getSprite()) {
            @Override
            public void onUpdate() {
                if (item == null) return;

                if (icon != null) remove(icon);
                icon = getSprite();
                addToBack(icon);
                remove(bg);
                addToBack(bg);

                super.onUpdate();
            }
        };
    }

    @Override
    public DefaultEditComp<?> createEditComponent() {
        return new EditBuffComp(getObject(), null);
    }

    @Override
    public Image getSprite() {
        return new BuffIcon(getObject(), true);
    }

    @Override
    public void place(int cell) {
        //can't be placed
    }

    @Override
    public String name() {
        return getObject().name();
    }

    @Override
    public Item getCopy() {
        return new BuffItem((Buff) getObject().getCopy());
    }

    private static final String BUFF = "buff";
    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(BUFF, obj);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        obj = (Buff) bundle.get(BUFF);
    }
}