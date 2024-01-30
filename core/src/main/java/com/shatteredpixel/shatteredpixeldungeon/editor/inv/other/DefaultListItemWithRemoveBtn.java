package com.shatteredpixel.shatteredpixeldungeon.editor.inv.other;

import com.shatteredpixel.shatteredpixeldungeon.editor.inv.DefaultListItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.EditorInventoryWindow;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.watabou.noosa.Image;

public abstract class DefaultListItemWithRemoveBtn extends DefaultListItem {

    private IconButton remove;

    public DefaultListItemWithRemoveBtn(Item item, EditorInventoryWindow window, String title, Image image) {
        super(item, window, title, image);
    }

    @Override
    protected void createChildren(Object... params) {
        super.createChildren(params);
        remove = new IconButton(Icons.TRASH.get()) {
            @Override
            protected void onClick() {
                onRemove();
            }
        };
        add(remove);
    }

    @Override
    protected void layout() {
        super.layout();
        if (remove != null) {
            float posX;
            if (editButton != null) {
                editButton.setPos(editButton.left() - ICON_WIDTH - 2, editButton.top());
                hotArea.width = editButton.left() - 1;
                posX = editButton.right() + 2;
            } else {
                posX = x + width - ICON_WIDTH;
                hotArea.width = width - ICON_WIDTH - 1;
            }
            remove.setRect(posX + (ICON_WIDTH - remove.icon().width()) * 0.5f, y + (height - remove.icon().height()) * 0.5f,
                    remove.icon().width(), remove.icon().height());
        }
    }

    @Override
    protected int getLabelMaxWidth() {
        return super.getLabelMaxWidth() - ICON_WIDTH;
    }

    @Override
    public void onUpdate() {
        if (item == null) return;
        super.onUpdate();
        label.text(item.name());//no title case
    }

    protected abstract void onRemove();

}