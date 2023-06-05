package com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.editcomps;

import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Mob;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.inv.TileItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.AdvancedListPaneItem;
import com.alphadraxonis.sandboxpixeldungeon.items.Heap;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.Trap;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.ui.RenderedTextBlock;
import com.alphadraxonis.sandboxpixeldungeon.ui.ScrollPane;
import com.alphadraxonis.sandboxpixeldungeon.ui.Window;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public abstract class DefaultEditComp<T> extends Component {


    protected final Component title;
    protected final RenderedTextBlock desc;

    protected final T item;

    private Runnable onUpdate;
    public AdvancedListPaneItem advancedListPaneItem;

    public DefaultEditComp(T item) {

        this.item = item;

        title = createTitle();
        add(title);
        desc = PixelScene.renderTextBlock(createDescription(), 6);
        add(desc);
    }

    @Override
    protected void layout() {

        desc.maxWidth((int) width);

        title.setRect(x, y, width, title.height());
        desc.setRect(x, title.bottom() + WndTitledMessage.GAP * 2, desc.width(), desc.height());

        height = desc.bottom() + 1;
    }

    protected abstract Component createTitle();

    protected abstract String createDescription();

    public abstract Image getIcon();

    public T getItem() {
        return item;
    }


    protected void updateItem() {
        layout();
        if (advancedListPaneItem != null) advancedListPaneItem.onUpdate();
        if (onUpdate != null) onUpdate.run();
    }

    public void setOnUpdate(Runnable onUpdate) {
        this.onUpdate = onUpdate;
    }

    public Runnable getOnUpdate() {
        return onUpdate;
    }


    public static void showWindow(int terrainType, int terrainImage, Heap heap, Mob mob, Trap trap,int cell) {

        EditTileComp editTileComp = null;
        EditItemComp editItemComp = null;
        EditMobComp editMobComp = null;
        EditTrapComp editTrapComp = null;

        int numTabs = 0;
        TileItem tileItem = null;

        if (terrainType != -1) {
            tileItem = new TileItem(terrainType,cell);
            if (terrainImage != -1) tileItem.image = terrainImage;
            numTabs++;
        }
        if (heap != null) numTabs++;
        if (mob != null) numTabs++;
        if (trap != null) numTabs++;

        if (numTabs == 0) return;
        if (numTabs > 1 || (heap != null && !heap.items.isEmpty())) {
            EditorScene.show(EditCompWindowTabbed.createEditCompWindowTabbed(tileItem, heap, mob, trap, 0, numTabs));
            return;
        }
        Window w = new Window();

        float newWidth = PixelScene.landscape() ? WndTitledMessage.WIDTH_MAX : WndTitledMessage.WIDTH_MIN;

        DefaultEditComp<?> content;
        if (tileItem != null) content = new EditTileComp(tileItem);
        else if (heap != null) content = new EditHeapComp(heap);
        else if (mob != null) content = new EditMobComp(mob);
        else content = new EditTrapComp(trap);

        content.setRect(0, 0, newWidth, -1);
        ScrollPane sp = new ScrollPane(content);
        w.add(sp);

        Runnable r = () -> {
            float ch = content.height();
            int maxHeight = (int) (PixelScene.uiCamera.height * 0.9);
            int weite = (int) Math.ceil(ch > maxHeight ? maxHeight : ch);
            w.resize((int) Math.ceil(newWidth), weite);
            sp.setSize((int) Math.ceil(newWidth), weite);
            sp.scrollTo(sp.content().camera.scroll.x, sp.content().camera.scroll.y);
        };
        content.setOnUpdate(r);
        r.run();

        EditorScene.show(w);

    }


}