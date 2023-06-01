package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.inv.EditorItemBag;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.inv.TileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.inv.WndEditorItemsBag;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sword;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTabbed;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.input.KeyEvent;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

import java.util.HashMap;
import java.util.Map;

import sun.tools.jconsole.Tab;

public class EditCompWindowTabbed extends WndTabbed {

    private static final int MAX_NUM_TABS = 6;

    private float[] otherHeights, scrollPos;
    private final TileItem tileItem;
    private final Heap heap;
    private final Item[] items;
    private final Mob mob;
    private final Trap trap;

    private TabBody curBody;

    public static EditCompWindowTabbed createEditCompWindowTabbed(TileItem tileItem, Heap heap, Mob mob, Trap trap, int selectIndex, int numTabs) {
        Item[] items = getItemsFromHeap(heap, numTabs);
        if (items != null) numTabs += items.length;
        return new EditCompWindowTabbed(tileItem, heap, items, mob, trap, selectIndex, new float[numTabs]);
    }

    private static Item[] getItemsFromHeap(Heap heap, int numTabs) {
        if (heap == null || heap.size() == 0) return null;

        int length = Math.min(heap.size(), MAX_NUM_TABS - numTabs);
        if (length <= 0) return null;
        Item[] ret = new Item[length];
        int i = 0;
        for (Item item : heap.items) {
            ret[i] = item;
            i++;
            if (i >= ret.length) break;
        }
        return ret;
    }

    public EditCompWindowTabbed(TileItem tileItem, Heap heap, Item[] items, Mob mob, Trap trap, int selectIndex, float[] scrollPos) {
        this.tileItem = tileItem;
        this.heap = heap;
        this.items = items;
        this.mob = mob;
        this.trap = trap;
        this.scrollPos = scrollPos;

        width = PixelScene.landscape() ? WndTitledMessage.WIDTH_MAX : WndTitledMessage.WIDTH_MIN;

        otherHeights = new float[scrollPos.length];//one field will remain 0

        int index = 0;

        if (items != null) {

            for (Item item : items) {
                int idx = index;
                initComp(new EditItemComp(item, heap) {
                    @Override
                    protected void updateItem() {
                        super.updateItem();
                        if (tabs.size() > idx) ((TabBtn) tabs.get(idx)).setIcon(getIcon());
                    }
                }, index, selectIndex);
                index++;
            }
        }
        if (heap != null) {
            int idx = index;
            initComp(new EditHeapComp(heap) {
                @Override
                protected void updateItem() {
                    super.updateItem();
                    if (tabs.size() > idx) ((TabBtn) tabs.get(idx)).setIcon(getIcon());
                }
            }, index, selectIndex);
            index++;
        }
        if (mob != null) {
            int idx = index;
            initComp(new EditMobComp(mob) {
                @Override
                public void updateItem() {
                    super.updateItem();
                    if (tabs.size() > idx) ((TabBtn) tabs.get(idx)).setIcon(getIcon());
                }
            }, index, selectIndex);
            index++;
        }
        if (trap != null) {
            int idx = index;
            initComp(new EditTrapComp(trap) {
                @Override
                protected void updateItem() {
                    super.updateItem();
                    if (tabs.size() > idx) ((TabBtn) tabs.get(idx)).setIcon(getIcon());
                }
            }, index, selectIndex);
            index++;
        }
        if (tileItem != null) {
            int idx = index;
            initComp(new EditTileComp(tileItem) {
                @Override
                protected void updateItem() {
                    super.updateItem();
                    if (tabs.size() > idx) ((TabBtn) tabs.get(idx)).setIcon(getIcon());
                }
            }, index, selectIndex);
            index++;
        }

        changeHeight();

        select(selectIndex);

        curBody.sp.scrollTo(curBody.sp.content().camera().scroll.x, scrollPos[curBody.index]);

        layoutTabs();
    }

    private void initComp(DefaultEditComp<?> comp, int index, int selectedIndex) {
        add(new TabBtn(comp.getIcon(), index));
        if (selectedIndex != index) {
            comp.setSize(width, -1);
            otherHeights[index] = comp.height();
            comp.destroy();
        } else {
            curBody = new TabBody(comp, width, index);
            add(curBody);
        }
    }

    private void changeHeight() {
        float h = 0;
        for (float otherHeight : otherHeights) {
            h = Math.max(h, otherHeight + 1);
        }
        h = Math.max(h, curBody.content.height() + 1);
        h = Math.min(h, PixelScene.uiCamera.height * 0.8f - tabHeight());

        resize(width, (int) Math.ceil(h));
        curBody.setSize(width, h);

        layoutTabs();
    }

    private class TabBtn extends IconTab {
        private final int index;

        public TabBtn(Image icon, int index) {
            super(icon);
            this.index = index;
        }

        private void setIcon(Image icon) {
            this.icon.copy(icon);
            this.defaultFrame = icon.frame();
        }
    }

    private class TabBody extends Component {

        private Component content;
        private Runnable layouter;
        private ScrollPane sp;

        private final int index;

        public TabBody(DefaultEditComp<?> content, float width, int index) {
            super(Icons.get(Icons.CLOSE));
            this.index = index;

            add(content);
            this.content = content;

            content.setRect(0, 0, width, -1);
            sp = new ScrollPane(content);
            add(sp);

            layouter = EditCompWindowTabbed.this::changeHeight;
            content.setOnUpdate(layouter);
        }

        @Override
        protected void layout() {
            super.layout();
            content.setPos(0, 0);
            content.setRect(0, 0, EditCompWindowTabbed.this.width, content.height());
            sp.setRect(0, 0, EditCompWindowTabbed.this.width, EditCompWindowTabbed.this.height);
            sp.scrollTo(sp.content().camera().scroll.x, sp.content().camera().scroll.y);
        }
    }

    @Override
    protected void onClick(Tab tab) {
        hide();
        TabBtn t = (TabBtn) tab;
        scrollPos[curBody.index] = curBody.sp.content().camera().scroll.y;
        Window w = new EditCompWindowTabbed(tileItem, heap, items, mob, trap, t.index, scrollPos);
        if (Game.scene() instanceof EditorScene) {
            EditorScene.show(w);
        } else {
            Game.scene().addToFront(w);
        }
    }

}