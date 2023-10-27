package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPartModify;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.HeapActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.MobActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.TileModify;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.TrapActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTabbed;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class EditCompWindowTabbed extends WndTabbed {

    private static final int MAX_NUM_TABS = 6;

    private static final List<ActionPartModify> actionPartModifyList = new ArrayList<>(7);

    private LinkedHashMap<Object, Wrapper> comps;

    private Object selectedObject;

    private Item[] items;


    public EditCompWindowTabbed(TileItem tileItem, Heap heap, Mob mob, Trap trap, Plant plant, int numTabs) {
        actionPartModifyList.clear();
        items = getItemsFromHeap(heap, numTabs);
        if (heap != null) actionPartModifyList.add(new HeapActionPart.Modify(heap));
        if (mob != null) actionPartModifyList.add(new MobActionPart.Modify(mob));
        if (trap != null) actionPartModifyList.add(new TrapActionPart.Modify(trap));
        if (tileItem != null) actionPartModifyList.add(new TileModify(tileItem.cell()));


        width = Math.min(WndTitledMessage.WIDTH_MAX, (int) (PixelScene.uiCamera.width * 0.9));

        comps = new LinkedHashMap<>();

        Object toSelect = null;
        if (items != null) {
            for (Item item : items) {
                if (toSelect == null) toSelect = item;
                initComp(new EditItemComp(item, heap) {
                    @Override
                    protected void updateObj() {
                        super.updateObj();
                        if (comps.containsKey(item)) comps.get(item).tabBtn.setIcon(getIcon());
                    }
                });
            }
        }
        if (heap != null) {
            if (toSelect == null) toSelect = heap;
            initComp(new EditHeapComp(heap) {
                @Override
                protected void updateObj() {
                    super.updateObj();
                    if (comps.containsKey(heap)) {//ik this code is trash, but if it works it works
                        comps.get(heap).tabBtn.setIcon(getIcon());

                        //Update logic for heap item change
                        LinkedHashMap<Item, Wrapper> removed = new LinkedHashMap();
                        if (items != null) {
                            for (Item itemOld : items) {
                                tabs.remove(comps.get(itemOld).tabBtn);
                                removed.put(itemOld, comps.get(itemOld));
                                comps.remove(itemOld);
                            }
                        }
                        LinkedHashMap<Object, Wrapper> otherComps = new LinkedHashMap<>(comps);

                        Item[] itemsNew = getItemsFromHeap(heap, numTabs);
                        if (itemsNew != null) {
                            for (int i = itemsNew.length - 1; i >= 0; i--) {
                                Item item = itemsNew[i];
                                if (removed.containsKey(item)) {
                                    tabs.add(0, removed.get(item).tabBtn);
                                    comps.put(item, removed.get(item));
                                    removed.remove(item);
                                } else {
                                    initComp(new EditItemComp(item, heap) {
                                        @Override
                                        protected void updateObj() {
                                            super.updateObj();
                                            if (comps.containsKey(item))
                                                comps.get(item).tabBtn.setIcon(getIcon());
                                        }
                                    });
                                    TabBtn tab = comps.get(item).tabBtn;
                                    tabs.remove(tab);
                                    tabs.add(0, tab);
                                }
                            }
                        }
                        for (Wrapper wrapper : removed.values()) {
                            remove(wrapper.body);
                            remove(wrapper.tabBtn);
                            wrapper.body.destroy();
                            wrapper.tabBtn.destroy();
                            tabs.remove(wrapper.tabBtn);
                        }
                        comps.putAll(otherComps);
                        items = itemsNew;

                        changeHeight();
                        layoutCurrent();
                        layoutTabs();
                    }
                }
            });
        }
        if (mob != null) {
            if (toSelect == null) toSelect = mob;
            initComp(new EditMobComp(mob) {
                @Override
                public void updateObj() {
                    super.updateObj();
                    if (comps.containsKey(mob)) comps.get(mob).tabBtn.setIcon(getIcon());
                }
            });
        }
        if (trap != null) {
            if (toSelect == null) toSelect = trap;
            initComp(new EditTrapComp(trap) {
                @Override
                protected void updateObj() {
                    super.updateObj();
                    if (comps.containsKey(trap)) comps.get(trap).tabBtn.setIcon(getIcon());
                }
            });
        }
        if (plant != null) {
            if (toSelect == null) toSelect = plant;
            initComp(new EditPlantComp(plant) {
                @Override
                protected void updateObj() {
                    super.updateObj();
                    if (comps.containsKey(plant)) comps.get(plant).tabBtn.setIcon(getIcon());
                }
            });
        }
        if (tileItem != null) {
            if (toSelect == null) toSelect = tileItem;
            initComp(new EditTileComp(tileItem) {
                @Override
                protected void updateObj() {
                    super.updateObj();
                    if (comps.containsKey(tileItem)) comps.get(tileItem).tabBtn.setIcon(getIcon());
                }
            });
        }

        changeHeight();

        select(toSelect);

        layoutTabs();
    }

    private void initComp(DefaultEditComp<?> comp) {

        TabBody body = new TabBody(comp, comp.getObj());
        TabBtn tabBtn = new TabBtn(comp.getIcon(), comp.getObj());
        comps.put(comp.getObj(), new Wrapper(body, tabBtn));
        add(tabBtn);
        add(body);
        body.active = body.visible = false;
    }

    private void changeHeight() {
        float h = 0;
        for (Wrapper wrapper : comps.values()) {
            h = Math.max(h, wrapper.body.getPreferredHeight() + 1);
        }
        float maxHeightNoOffset = PixelScene.uiCamera.height * 0.9f - tabHeight()-10;
        int offset = EditorUtilies.getMaxWindowOffsetYForVisibleToolbar();
        if (h > maxHeightNoOffset) {
            if (h > maxHeightNoOffset + offset) h = maxHeightNoOffset + offset;
            else offset = (int) Math.ceil(h - maxHeightNoOffset);
        }
        offset = Math.max(offset, 10);
        offset(xOffset, -offset);
        resize(width, (int) Math.ceil(h));
    }

    private void layoutCurrent() {
        TabBody body = comps.get(selectedObject).body;
        body.setSize(width, height);
        body.sp.scrollTo(body.sp.content().camera().scroll.x, comps.get(selectedObject).scrollPos);
    }


    public void select(Object object) {
        select(comps.get(object).tabBtn);
    }

    @Override
    public void select(Tab tab) {
        Object obj = ((TabBtn) tab).obj;
        if (selectedObject != obj) {
            super.select(tab);
            selectedObject = obj;
            layoutCurrent();
        }
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

    private class TabBtn extends IconTab {
        private final Object obj;

        public TabBtn(Image icon, Object obj) {
            super(icon);
            this.obj = obj;
        }

        private void setIcon(Image icon) {
            this.icon.copy(icon);
            this.defaultFrame = icon.frame();
        }

        @Override
        protected void select(boolean value) {
            super.select(value);
            TabBody body = comps.get(obj).body;
            if (visible) comps.get(obj).scrollPos = body.sp.content().camera().scroll.y;
            body.active = body.visible = value;
        }
    }

    private class TabBody extends Component {

        private final Component content;
        private Runnable layouter;
        private ScrollPane sp;

        private final Object obj;

        public TabBody(DefaultEditComp<?> content, Object obj) {
            super(Icons.get(Icons.CLOSE));
            this.obj = obj;

            add(content);
            this.content = content;

            sp = new ScrollPane(content);
            add(sp);

            layouter = () -> {
                changeHeight();
                layoutCurrent();
                layoutTabs();
            };
            content.setOnUpdate(layouter);
        }

        @Override
        protected void layout() {
            super.layout();
            content.setPos(0, 0);
            content.setRect(0, 0, EditCompWindowTabbed.this.width, content.height());
            sp.setRect(0, 0, EditCompWindowTabbed.this.width, EditCompWindowTabbed.this.height);
            sp.scrollToCurrentView();
        }

        private float getPreferredHeight() {
            content.setSize(EditCompWindowTabbed.this.width, -1);
            return content.height();
        }
    }

    @Override
    public void hide() {
        super.hide();
        for (ActionPartModify modify : actionPartModifyList) {
            Undo.startAction();
            modify.finish();
            Undo.addActionPart(modify);
            Undo.endAction();
        }
    }

    private class Wrapper {
        private TabBody body;
        private TabBtn tabBtn;
        private float scrollPos;

        private Wrapper(TabBody body, TabBtn tabBtn) {
            this(body, tabBtn, 0);
        }

        private Wrapper(TabBody body, TabBtn tabBtn, float scrollPos) {
            this.body = body;
            this.tabBtn = tabBtn;
            this.scrollPos = scrollPos;
        }
    }

}