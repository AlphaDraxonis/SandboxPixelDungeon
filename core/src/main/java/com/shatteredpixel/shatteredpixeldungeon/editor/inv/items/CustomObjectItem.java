package com.shatteredpixel.shatteredpixeldungeon.editor.inv.items;

import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObject;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.DefaultListItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.EditorInventoryWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.DefaultListItemWithRemoveBtn;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.watabou.noosa.Image;

public class CustomObjectItem extends EditorItem<CustomObject> {

    public CustomObjectItem() {
    }

    public CustomObjectItem(CustomObject customObject) {
        this.obj = customObject;
    }

    @Override
    public DefaultEditComp<?> createEditComponent() {
        return getObject().createEditComp();
    }

    public ScrollingListPane.ListItem createListItem(EditorInventoryWindow window) {
        if (supportsAction(Action.REMOVE)) {
            return new DefaultListItemWithRemoveBtn(this, window, Messages.titleCase(title()), getSprite()) {
                @Override
                protected void onRemove() {
                    CustomObjectItem.this.doAction(Action.REMOVE);
                }
            };
        }
        return new DefaultListItem(this, window, Messages.titleCase(title()), getSprite());
    }

    @Override
    public String name() {
        return getObject().getName();
    }

    @Override
    public Image getSprite() {
        return getSprite(null);
    }

    @Override
    public Image getSprite(Runnable reloadSprite) {
        return getObject().getSprite(reloadSprite);
    }

    @Override
    public Item getCopy() {
        return new CustomObjectItem(getObject());
    }

    @Override
    public void place(int cell) {
        //can't be placed
    }

    @Override
    public boolean supportsAction(Action action) {
        return action == Action.REMOVE && CustomDungeon.isEditing();
    }

    @Override
    public void doAction(Action action) {
        if (action == Action.REMOVE)
            CustomDungeonSaves.deleteCustomObject(getObject());
    }
}