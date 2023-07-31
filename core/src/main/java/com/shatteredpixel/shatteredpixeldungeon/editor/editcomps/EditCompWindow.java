package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.AdvancedListPaneItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;

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