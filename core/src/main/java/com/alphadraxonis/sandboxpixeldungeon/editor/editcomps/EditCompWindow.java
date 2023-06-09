package com.alphadraxonis.sandboxpixeldungeon.editor.editcomps;

import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Mob;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.items.EditorItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.AdvancedListPaneItem;
import com.alphadraxonis.sandboxpixeldungeon.items.Heap;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.Trap;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.ui.ScrollPane;
import com.alphadraxonis.sandboxpixeldungeon.ui.Window;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndTitledMessage;

public class EditCompWindow extends Window {

    private final ScrollPane sp;
    private final DefaultEditComp<?> content;

    public EditCompWindow(Object object) {
        this(object, null);
    }

    public EditCompWindow(Object object, AdvancedListPaneItem advancedListPaneItem) {
        if (object instanceof EditorItem) content = ((EditorItem) object).createEditComponent();
        else if (object instanceof Item) content = new EditItemComp((Item) object, null);
        else if (object instanceof Mob) content = new EditMobComp((Mob) object);
        else if (object instanceof Trap) content = new EditTrapComp((Trap) object);
        else if (object instanceof Heap)content = new EditHeapComp((Heap)object);
        else
            throw new IllegalArgumentException("Invalid object: " + object + " (class " + object.getClass().getName() + ")");
        content.advancedListPaneItem = advancedListPaneItem;

        float newWidth = Math.min(WndTitledMessage.WIDTH_MAX, (int) (PixelScene.uiCamera.width * 0.85));

        content.setRect(0, 0, newWidth, -1);
        sp = new ScrollPane(content);
        add(sp);

        width = (int) Math.ceil( newWidth);

        Runnable r = this::onUpdate;
        content.setOnUpdate(r);
        r.run();
    }

    protected void onUpdate(){
        float ch = content.height();
        int maxHeight = (int) (PixelScene.uiCamera.height * 0.8);
        int weite = (int) Math.ceil(ch > maxHeight ? maxHeight : ch);
        resize(width, weite);
        sp.setSize(width, weite);
        sp.scrollToCurrentView();
    }

}