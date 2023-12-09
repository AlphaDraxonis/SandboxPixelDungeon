package com.shatteredpixel.shatteredpixeldungeon.editor.inv;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditCompWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.AdvancedListPaneItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.IconTitleWithSubIcon;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;

public class DefaultListItem extends AdvancedListPaneItem {

    protected final Item item;
    private EditorInventoryWindow window;

    protected IconButton editButton;

    public DefaultListItem(Item item, EditorInventoryWindow window, String title, Image image) {
        super(image,
                (item instanceof EditorItem) ? ((EditorItem) item).getSubIcon() : IconTitleWithSubIcon.createSubIcon(item),
                Messages.titleCase(title));
        this.item = item;
        this.window = window;
        label.setHighlighting(false);

        if (item instanceof EditorItem) {
            editButton = new IconButton(Icons.get(Icons.RENAME_ON)) {
                @Override
                protected void onClick() {
                    openEditWindow();
                }
            };
            add(editButton);
        }

        onUpdate();
    }

    @Override
    protected void layout() {
        super.layout();

        if (editButton != null) {
            editButton.setRect(width - 3 - editButton.icon().width(), y + (height - editButton.icon().height()) * 0.5f, editButton.icon().width(), editButton.icon().height());
            hotArea.width = editButton.left() - 1;
        }

    }

    @Override
    public void onUpdate() {
        QuickSlotButton.refresh();
        super.onUpdate();
    }

    @Override
    protected int getLabelMaxWidth() {
        return (int) (width - ICON_WIDTH - 1 - 4 - ICON_WIDTH);
    }

    @Override
    protected void onClick() {
        Sample.INSTANCE.play(Assets.Sounds.CLICK);
        if (window.selector() != null) {
            window.hide();
            window.selector().onSelect(item);
        } else {
            window.hide();
            QuickSlotButton.set(item);
        }
    }

    @Override
    protected void onRightClick() {
        Sample.INSTANCE.play(Assets.Sounds.CLICK);
        onLongClick();
    }

    @Override
    protected boolean onLongClick() {

        if (openEditWindow()) {
            return true;
        }

        window.hide();
        QuickSlotButton.set(item);
        return true;
    }

    protected boolean openEditWindow() {
        if (item instanceof EditorItem) {
            Window w = new EditCompWindow(item, this);
//            Window w = new Window();
//            DefaultEditComp<?> content = ((EditorItem) item).createEditComponent();
//            content.advancedListPaneItem = this;
//
//            float newWidth = PixelScene.landscape() ?
//                    Math.min(200, PixelScene.uiCamera.width * 0.8f) :
//                    Math.min(160, PixelScene.uiCamera.width * 0.8f);
//
//            content.setRect(0, 0, newWidth, -1);
//            ScrollPane sp = new ScrollPane(content);
//            w.add(sp);
//
//            Runnable r = () -> {
//                float ch = content.height();
//                int maxHeight = (int) (PixelScene.uiCamera.height * 0.8);
//                int h = (int) Math.ceil(ch > maxHeight ? maxHeight : ch);
//                w.resize((int) Math.ceil(newWidth), h);
//                sp.setSize((int) Math.ceil(newWidth), h);
//                sp.scrollToCurrentView();
//            };
//            content.setOnUpdate(r);
//            r.run();

            if (Game.scene() instanceof EditorScene) EditorScene.show(w);
            else Game.scene().addToFront(w);
            return true;
        }
        return false;
    }
}