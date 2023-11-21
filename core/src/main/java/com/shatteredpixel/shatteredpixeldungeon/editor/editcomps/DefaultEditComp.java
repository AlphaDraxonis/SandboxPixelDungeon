package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPartModify;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.HeapActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.MobActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.PlantActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.TileModify;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.TrapActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.AdvancedListPaneItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

import java.util.List;

public abstract class DefaultEditComp<T> extends Component {


    protected final Component title;
    protected final RenderedTextBlock desc;

    protected final T obj;

    private Runnable onUpdate;
    public AdvancedListPaneItem advancedListPaneItem;

    public DefaultEditComp(T obj) {

        this.obj = obj;

        title = createTitle();
        add(title);
        desc = PixelScene.renderTextBlock(createDescription(), 6);
        add(desc);
    }

    @Override
    protected void layout() {
        //if you edit this, also check out EditItemComp#layout()

        desc.maxWidth((int) width);

        title.setRect(x, y, width, title.height());
        desc.setRect(x, title.bottom() + WndTitledMessage.GAP * 2, desc.width(), desc.height());

        height = desc.bottom() + 1;
    }

    protected final void layoutCompsLinear(Component... comps) {
        height = EditorUtilies.layoutCompsLinear(WndTitledMessage.GAP, this, comps);
    }

    protected void onShow() {
    }

    protected abstract Component createTitle();

    protected abstract String createDescription();

    public abstract Image getIcon();

    public T getObj() {
        return obj;
    }


    protected void updateObj() {
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


    public static void showWindow(int terrainType, int terrainImage, Heap heap, Mob mob, Trap trap, Plant plant, int cell) {

        int numTabs = 0;
        TileItem tileItem = null;

        if (terrainType != -1) {
            tileItem = new TileItem(terrainType, cell);
            if (terrainImage != -1) tileItem.image = terrainImage;
            numTabs++;
        }
        if (heap != null) numTabs++;
        if (mob != null) numTabs++;
        if (trap != null) numTabs++;
        if (plant != null) numTabs++;

        if (numTabs == 0) return;
        if (numTabs > 1 || (heap != null && !heap.items.isEmpty())) {
            Window w = new EditCompWindowTabbed(tileItem, heap, mob, trap, plant, numTabs);
            if (Game.scene() instanceof EditorScene) EditorScene.show(w);
            else Game.scene().addToFront(w);
            return;
        }

        DefaultEditComp<?> content;
        ActionPartModify actionPart;
        if (tileItem != null) {
            content = new EditTileComp(tileItem);
            actionPart = new TileModify(cell);
        } else if (heap != null) {
            content = new EditHeapComp(heap);
            actionPart = new HeapActionPart.Modify(heap);
        } else if (mob != null) {
            content = new EditMobComp(mob);
            actionPart = new MobActionPart.Modify(mob);
        } else if (trap != null) {
            content = new EditTrapComp(trap);
            actionPart = new TrapActionPart.Modify(trap);
        } else {
            content = new EditPlantComp(plant);
            actionPart = new PlantActionPart.Modify(plant);
        }

        showSingleWindow(content, actionPart);
    }

    public static void showSingleWindow(DefaultEditComp<?> content, ActionPartModify actionPart) {
        float newWidth = PixelScene.landscape() ? WndTitledMessage.WIDTH_MAX : WndTitledMessage.WIDTH_MIN;


        content.setRect(0, 0, newWidth, -1);
        ScrollPane sp = new ScrollPane(content);

        Window w = new Window() {
            @Override
            public void hide() {
                super.hide();
                if (actionPart != null) actionPart.finish();
                Undo.startAction();
                Undo.addActionPart(actionPart);
                Undo.endAction();
            }
        };
        w.add(sp);

        Runnable r = () -> {

            float ch = content.height();
            float maxHeightNoOffset = PixelScene.uiCamera.height * 0.9f - 10;
            int offset = EditorUtilies.getMaxWindowOffsetYForVisibleToolbar();
            if (ch > maxHeightNoOffset) {
                if (ch > maxHeightNoOffset + offset) ch = maxHeightNoOffset + offset;
                else offset = (int) Math.ceil(ch - maxHeightNoOffset);
            }
            offset = Math.max(offset, 10);


            w.offset(w.getOffset().x, -offset);
            w.resize((int) Math.ceil(newWidth), (int) Math.ceil(ch));
            sp.setSize((int) Math.ceil(newWidth), (int) Math.ceil(ch));
            sp.scrollToCurrentView();


//            float ch = content.height();
//            int maxHeight = (int) (PixelScene.uiCamera.height * 0.9);
//            int hei = (int) Math.ceil(ch > maxHeight ? maxHeight : ch);
//            w.resize((int) Math.ceil(newWidth), hei);
//            sp.setSize((int) Math.ceil(newWidth), hei);
//            sp.scrollToCurrentView();
        };
        content.setOnUpdate(r);
        r.run();

        sp.givePointerPriority();

        if (Game.scene() instanceof EditorScene) EditorScene.show(w);
        else Game.scene().addToFront(w);
    }


    public static boolean isItemListEqual(List<Item> a, List<Item> b) {
        if (a == null) return b == null || b.size() == 0;
        if (b == null) return a.size() == 0;
        if (a.size() != b.size()) return false;
        int index = 0;
        for (Item i : a) {
            if (!EditItemComp.areEqual(i, b.get(index))) return false;
            index++;
        }
        return true;
    }


}