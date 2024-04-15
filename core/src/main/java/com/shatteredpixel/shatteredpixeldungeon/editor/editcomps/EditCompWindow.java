package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.Zone;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.AdvancedListPaneItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;

public class EditCompWindow extends Window {

    private ScrollPane sp;
    protected final DefaultEditComp<?> content;

    public EditCompWindow(Object object) {
        this(object, null);
    }

    public EditCompWindow(Item item, Heap heap, AdvancedListPaneItem advancedListPaneItem) {
        content = new EditItemComp(item, heap);
        content.advancedListPaneItem = advancedListPaneItem;
        init();
    }

    public EditCompWindow(Object object, AdvancedListPaneItem advancedListPaneItem) {
        content = createContent(object);

        if (content == null)
            throw new IllegalArgumentException("Invalid object: " + object + " (class " + object.getClass().getName() + ")");

        content.advancedListPaneItem = advancedListPaneItem;
        init();
    }

    public static DefaultEditComp<?> createContent(Object object) {
        if (object instanceof EditorItem) return ((EditorItem<?>) object).createEditComponent();
        if (object instanceof Item) return new EditItemComp((Item) object, null);
        if (object instanceof Mob) return new EditMobComp((Mob) object);
        if (object instanceof Trap) return new EditTrapComp((Trap) object);
        if (object instanceof Plant) return new EditPlantComp((Plant) object);
        if (object instanceof Heap) return new EditHeapComp((Heap) object);
        if (object instanceof Zone) return new EditZoneComp((Zone) object);
        if (object instanceof Barrier) return new EditBarrierComp((Barrier) object);
        return null;
    }

    private void init() {

        float newWidth = Math.min(WndTitledMessage.WIDTH_MAX - 5, (int) (PixelScene.uiCamera.width * 0.82f));

        content.setRect(0, 0, newWidth, -1);
        sp = new ScrollPane(content);
        add(sp);

        width = (int) Math.ceil(newWidth);

        Runnable r = this::onUpdate;
        content.setOnUpdate(r);
        r.run();
    }

    protected void onUpdate() {
        float ch = content.height();
        float maxHeightNoOffset = PixelScene.uiCamera.height * 0.8f - 10;
        int offset = EditorUtilies.getMaxWindowOffsetYForVisibleToolbar();
        if (ch > maxHeightNoOffset) {
            if (ch > maxHeightNoOffset + offset) ch = maxHeightNoOffset + offset;
            else offset = (int) Math.ceil(ch - maxHeightNoOffset);
        }
        offset = Math.max(offset, 10);


        offset(xOffset, -offset);
        resize(width, (int) Math.ceil(ch));
        sp.setSize(width, (int) Math.ceil(ch));
        sp.scrollToCurrentView();

//        float ch = content.height();
//        int maxHeight = (int) (PixelScene.uiCamera.height * 0.8);
//        int weite = (int) Math.ceil(ch > maxHeight ? maxHeight : ch);
//        resize(width, weite);
//        sp.setSize(width, weite);
//        sp.scrollToCurrentView();
    }

}