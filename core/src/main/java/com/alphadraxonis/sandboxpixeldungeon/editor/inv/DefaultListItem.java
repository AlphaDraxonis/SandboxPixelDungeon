package com.alphadraxonis.sandboxpixeldungeon.editor.inv;

import com.alphadraxonis.sandboxpixeldungeon.Assets;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.DefaultEditComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.items.EditorItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.AdvancedListPaneItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.IconTitleWithSubIcon;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.ui.Icons;
import com.alphadraxonis.sandboxpixeldungeon.ui.QuickSlotButton;
import com.alphadraxonis.sandboxpixeldungeon.ui.RedButton;
import com.alphadraxonis.sandboxpixeldungeon.ui.ScrollPane;
import com.alphadraxonis.sandboxpixeldungeon.ui.Window;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;

public class DefaultListItem extends AdvancedListPaneItem {

    protected final Item item;
    private EditorInventoryWindow window;

    protected RedButton editButton;

    private static final float ICON_WIDTH, ICON_HEIGHT;

    private static final Image editIcon = Icons.get(Icons.PREFS);

    static {
        ICON_WIDTH = editIcon.width();
        ICON_HEIGHT = editIcon.height();
    }

    public DefaultListItem(Item item, EditorInventoryWindow window, String title, Image image) {
        super(image,
                (item instanceof EditorItem) ? ((EditorItem) item).getSubIcon() : IconTitleWithSubIcon.createSubIcon(item),
                Messages.titleCase(title));
        this.item = item;
        this.window = window;

//            editButton = new RedButton("");
//            add(editButton);

        onUpdate();
    }

    @Override
    protected void layout() {
        super.layout();

        if (editButton != null)
            editButton.setRect(width - 1 - ICON_WIDTH, y + (height - ICON_HEIGHT) * 0.5f, ICON_WIDTH, ICON_HEIGHT);
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

        if (item instanceof EditorItem) {
            Window w = new Window();
            DefaultEditComp<?> content = ((EditorItem) item).createEditComponent();
            content.advancedListPaneItem = this;

            float newWidth = PixelScene.landscape() ?
                    Math.min(200, PixelScene.uiCamera.width * 0.8f) :
                    Math.min(160, PixelScene.uiCamera.width * 0.8f);

            content.setRect(0, 0, newWidth, -1);
            ScrollPane sp = new ScrollPane(content);
            w.add(sp);

            Runnable r = () -> {
                float ch = content.height();
                int maxHeight = (int) (PixelScene.uiCamera.height * 0.8);
                int h = (int) Math.ceil(ch > maxHeight ? maxHeight : ch);
                w.resize((int) Math.ceil(newWidth), h);
                sp.setSize((int) Math.ceil(newWidth), h);
                sp.scrollTo(sp.content().camera.scroll.x, sp.content().camera.scroll.y);
            };
            content.setOnUpdate(r);
            r.run();

            EditorScene.show(w);
            return true;
        }

        window.hide();
        QuickSlotButton.set(item);
        return true;
    }
}