package com.alphadraxonis.sandboxpixeldungeon.editor.editcomps;

import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Mob;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.items.TileItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.ActionPartModify;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.Undo;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.parts.HeapActionPart;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.parts.MobActionPart;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.parts.TileModify;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.parts.TrapActionPart;
import com.alphadraxonis.sandboxpixeldungeon.editor.util.EditorUtilies;
import com.alphadraxonis.sandboxpixeldungeon.items.Heap;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.Trap;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.ui.Icons;
import com.alphadraxonis.sandboxpixeldungeon.ui.ScrollPane;
import com.alphadraxonis.sandboxpixeldungeon.ui.Window;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndTabbed;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditCompWindowTabbed extends WndTabbed {

    private static final int MAX_NUM_TABS = 6;

    private float[] otherHeights;
    private Map<Object, Float> scrollPos;
    private int numTabs;//no items[]

    private final TileItem tileItem;
    private final Heap heap;
    private final Mob mob;
    private final Trap trap;

    private TabBody curBody;

    private static final List<ActionPartModify> actionPartModifyList = new ArrayList<>(7);

    public static EditCompWindowTabbed createEditCompWindowTabbed(TileItem tileItem, Heap heap, Mob mob, Trap trap, int numTabs) {
        actionPartModifyList.clear();
        Item[] items = getItemsFromHeap(heap, numTabs);
        if (heap != null) actionPartModifyList.add(new HeapActionPart.Modify(heap));
        if (mob != null) actionPartModifyList.add(new MobActionPart.Modify(mob));
        if (trap != null) actionPartModifyList.add(new TrapActionPart.Modify(trap));
        if (tileItem != null) actionPartModifyList.add(new TileModify(tileItem.cell()));
        return new EditCompWindowTabbed(tileItem, heap, items, mob, trap, null, new HashMap<>(MAX_NUM_TABS), numTabs);
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

    private EditCompWindowTabbed(TileItem tileItem, Heap heap, Item[] items, Mob mob, Trap trap, Object selectObject, Map<Object, Float> scrollPos, int numTabs) {
        this.tileItem = tileItem;
        this.heap = heap;
        this.mob = mob;
        this.trap = trap;
        this.scrollPos = scrollPos;
        this.numTabs = numTabs;

        width = Math.min(WndTitledMessage.WIDTH_MAX, (int) (PixelScene.uiCamera.width * 0.9));

        otherHeights = new float[MAX_NUM_TABS];//one field will remain 0

        int index = 0;

        if (items != null) {
            for (Item item : items) {
                if (selectObject == null) selectObject = item;
                int idx = index;
                initComp(new EditItemComp(item, heap) {
                    @Override
                    protected void updateObj() {
                        super.updateObj();
                        if (tabs.size() > idx) ((TabBtn) tabs.get(idx)).setIcon(getIcon());
                    }
                }, index, item == selectObject);
                index++;
            }
        }
        if (heap != null) {
            if (selectObject == null) selectObject = heap;
            int idx = index;
            initComp(new EditHeapComp(heap) {
                @Override
                protected void updateObj() {
                    super.updateObj();
                    if (tabs.size() > idx) ((TabBtn) tabs.get(idx)).setIcon(getIcon());
                }
            }, index, heap == selectObject);
            index++;
        }
        if (mob != null) {
            if (selectObject == null) selectObject = mob;
            int idx = index;
            initComp(new EditMobComp(mob) {
                @Override
                public void updateObj() {
                    super.updateObj();
                    if (tabs.size() > idx) ((TabBtn) tabs.get(idx)).setIcon(getIcon());
                }
            }, index, mob == selectObject);
            index++;
        }
        if (trap != null) {
            if (selectObject == null) selectObject = trap;
            int idx = index;
            initComp(new EditTrapComp(trap) {
                @Override
                protected void updateObj() {
                    super.updateObj();
                    if (tabs.size() > idx) ((TabBtn) tabs.get(idx)).setIcon(getIcon());
                }
            }, index, trap == selectObject);
            index++;
        }
        if (tileItem != null) {
            if (selectObject == null) selectObject = tileItem;
            int idx = index;
            initComp(new EditTileComp(tileItem) {
                @Override
                protected void updateObj() {
                    super.updateObj();
                    if (tabs.size() > idx) ((TabBtn) tabs.get(idx)).setIcon(getIcon());
                }
            }, index, tileItem == selectObject);
            index++;
        }

        changeHeight();

        for (int i = 0; i < otherHeights.length; i++) {
            if (otherHeights[i] == 0) {
                select(i);//-> select(selectIndex);
                break;
            }
        }

        curBody.sp.scrollTo(curBody.sp.content().camera().scroll.x, EditorUtilies.getOrDefault(scrollPos, curBody.obj, 0f));

        layoutTabs();
    }

    private void initComp(DefaultEditComp<?> comp, int index, boolean selected) {
        add(new TabBtn(comp.getIcon(), comp.getObj(), index));
        if (!selected) {
            comp.setSize(width, -1);
            otherHeights[index] = comp.height();
            comp.destroy();
        } else {
            curBody = new TabBody(comp, width, comp.getObj());
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
        private final Object obj;
        private final int index;

        public TabBtn(Image icon, Object obj, int index) {
            super(icon);
            this.obj = obj;
            this.index = index;
        }

        private void setIcon(Image icon) {
            this.icon.copy(icon);
            this.defaultFrame = icon.frame();
            EditCompWindowTabbed.this.onClick(tabs.get(index));
        }
    }

    private class TabBody extends Component {

        private final Component content;
        private Runnable layouter;
        private ScrollPane sp;

        private final Object obj;

        public TabBody(DefaultEditComp<?> content, float width, Object obj) {
            super(Icons.get(Icons.CLOSE));
            this.obj = obj;

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

    private boolean goToOtherTab;

    @Override
    protected void onClick(Tab tab) {
        goToOtherTab = true;
        hide();
        goToOtherTab = false;
        TabBtn t = (TabBtn) tab;
        scrollPos.put(curBody.obj, curBody.sp.content().camera().scroll.y);
        Window w = new EditCompWindowTabbed(tileItem, heap, getItemsFromHeap(heap, numTabs), mob, trap, t.obj, scrollPos, numTabs);
        if (Game.scene() instanceof EditorScene) {
            EditorScene.show(w);
        } else {
            Game.scene().addToFront(w);
        }
    }

    @Override
    public void hide() {
        super.hide();
        if (!goToOtherTab) {
            for (ActionPartModify modify : actionPartModifyList) {
                Undo.startAction();
                modify.finish();
                Undo.addActionPart(modify);
                Undo.endAction();
            }
        }
    }
}