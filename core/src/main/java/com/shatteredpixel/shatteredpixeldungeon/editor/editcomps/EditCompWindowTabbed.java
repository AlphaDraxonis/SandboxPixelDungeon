package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.ArrowCell;
import com.shatteredpixel.shatteredpixeldungeon.editor.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.editor.Checkpoint;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPartModify;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.*;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
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

    private boolean fullyInitialized = false;


    public EditCompWindowTabbed(TileItem tileItem, Heap heap, Mob mob, Trap trap, Plant plant, Barrier barrier, ArrowCell arrowCell, Checkpoint checkpoint, int numTabs) {
        actionPartModifyList.clear();
        items = getItemsFromHeap(heap, numTabs);
        if (heap != null) actionPartModifyList.add(new HeapActionPart.Modify(heap));
        if (mob != null) actionPartModifyList.add(new MobActionPart.Modify(mob));
        if (trap != null) actionPartModifyList.add(new TrapActionPart.Modify(trap));
        if (plant != null) actionPartModifyList.add(new PlantActionPart.Modify(plant));
        if (barrier != null) actionPartModifyList.add(new BarrierActionPart.Modify(barrier));
        if (arrowCell != null) actionPartModifyList.add(new ArrowCellActionPart.Modify(arrowCell));
        if (checkpoint != null) actionPartModifyList.add(new CheckpointActionPart.Modify(checkpoint));
        if (tileItem != null) actionPartModifyList.add(new TileModify(tileItem.cell()));


        width = Math.min(WndTitledMessage.WIDTH_MAX, (int) (PixelScene.uiCamera.width * 0.9));

        comps = new LinkedHashMap<>();

        Object toSelect = null;
        if (items != null) {
            for (Item item : items) {
                if (toSelect == null) toSelect = item;
                initComp(new EditItemComp(item, heap) {
                    @Override
                    public void updateObj() {
                        super.updateObj();
                        if (comps.containsKey(item)) comps.get(item).tabBtn.setIcon(getIcon());

                        //only required for custom item image
                        if (comps.containsKey(heap)) {
                            comps.get(heap).body.content.updateObj();
                        }
                    }
                });
            }
        }
        if (heap != null) {
            if (toSelect == null) toSelect = heap;
            initComp(new EditHeapComp(heap) {
                @Override
                public void updateObj() {
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
                                        public void updateObj() {
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
                public void updateObj() {
                    super.updateObj();
                    if (comps.containsKey(trap)) comps.get(trap).tabBtn.setIcon(getIcon());
                }
            });
        }
        if (plant != null) {
            if (toSelect == null) toSelect = plant;
            initComp(new EditPlantComp(plant) {
                @Override
                public void updateObj() {
                    super.updateObj();
                    if (comps.containsKey(plant)) comps.get(plant).tabBtn.setIcon(getIcon());
                }
            });
        }
        if (barrier != null) {
            if (toSelect == null) toSelect = barrier;
            initComp(new EditBarrierComp(barrier) {
                @Override
                public void updateObj() {
                    super.updateObj();
                    if (comps.containsKey(barrier)) comps.get(barrier).tabBtn.setIcon(getIcon());
                }
            });
        }
        if (arrowCell != null) {
            if (toSelect == null) toSelect = arrowCell;
            initComp(new EditArrowCellComp(arrowCell) {
                @Override
                public void updateObj() {
                    super.updateObj();
                    if (comps.containsKey(arrowCell)) comps.get(arrowCell).tabBtn.setIcon(getIcon());
                }
            });
        }
        if (checkpoint != null) {
            if (toSelect == null) toSelect = checkpoint;
            initComp(new EditCheckpointComp(checkpoint) {
                @Override
                public void updateObj() {
                    super.updateObj();
                    if (comps.containsKey(checkpoint)) comps.get(checkpoint).tabBtn.setIcon(getIcon());
                }
            });
        }
        if (tileItem != null) {
            if (toSelect == null) toSelect = tileItem;
            initComp(new EditTileComp(tileItem) {
                @Override
                public void updateObj() {
                    super.updateObj();
                    if (comps.containsKey(tileItem)) comps.get(tileItem).tabBtn.setIcon(getIcon());
                }
            });
        }

        changeHeight();

        select(toSelect);

        layoutTabs();

        fullyInitialized = true;
    }

    private void initComp(DefaultEditComp<?> comp) {

        TabBody body = new TabBody(comp);
        TabBtn tabBtn = new TabBtn(comp.getIcon(), comp.getObj());
        comps.put(comp.getObj(), new Wrapper(body, tabBtn));
        add(tabBtn);
        add(body);
        body.setVisible(false);
    }

    private void changeHeight() {
        float h = 0;
        for (Wrapper wrapper : comps.values()) {
            h = Math.max(h, wrapper.body.getPreferredHeight() + 1);
        }
        float maxHeightNoOffset = PixelScene.uiCamera.height * 0.9f - tabHeight()-10;
        int offset = EditorUtilities.getMaxWindowOffsetYForVisibleToolbar();
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

    public void swapItemTabs(int index1, Item item1, int index2, Item item2, Heap heap, boolean selectNewTab) {
        if (items == null) return;

        if (index1 < items.length) items[index1] = item2;
        if (index2 < items.length) items[index2] = item1;

        if (index1 < items.length || index2 < items.length) {

            if (index1 >= items.length) {
                initComp(new EditItemComp(item1, heap) {
                    @Override
                    public void updateObj() {
                        super.updateObj();
                        if (comps.containsKey(item1)) comps.get(item1).tabBtn.setIcon(getIcon());
                    }
                });
                Wrapper wrapper = comps.get(item2);
                if (wrapper != null) {
                    wrapper.body.remove();
                    wrapper.body.destroy();
                    wrapper.body.killAndErase();
                }
            }
            if (index2 >= items.length) {
                initComp(new EditItemComp(item2, heap) {
                    @Override
                    public void updateObj() {
                        super.updateObj();
                        if (comps.containsKey(item2)) comps.get(item2).tabBtn.setIcon(getIcon());
                    }
                });
                Wrapper wrapper = comps.get(item1);
                if (wrapper != null) {
                    wrapper.body.remove();
                    wrapper.body.destroy();
                    wrapper.body.killAndErase();
                }
            }

            TabBtn tab1 = comps.get(item1).tabBtn;
            TabBtn tab2 = comps.get(item2).tabBtn;

            int tabIndex1 = tabs.indexOf(tab1);
            int tabIndex2 = tabs.indexOf(tab2);
            tabs.remove(tab1);
            if (index2 < items.length) {
                tabs.add(tabIndex2, tab1);
            } else {
                tab1.destroy();
                tab1.killAndErase();
                comps.remove(item1);
            }
            tabs.remove(tab2);
            if (index1 < items.length) {
                tabs.add(tabIndex1, tab2);
                if (selectNewTab && !tab1.alive) select(tab2);
            } else {
                tab2.destroy();
                tab2.killAndErase();
                comps.remove(item2);
                if (selectNewTab) select(tab1);
            }

        }

        ((EditHeapComp) comps.get(heap).body.content).itemContainer.updateItemListOrder();

        if (index1 >= items.length || index2 >= items.length) {
            changeHeight();
            layoutCurrent();
        }
        layoutTabs();
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
            comps.get(selectedObject).body.content.onShow(fullyInitialized);
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

        @Override
        protected String hoverText() {
            if (obj instanceof Item) return Messages.titleCase(((Item) obj).title());
            if (obj instanceof Mob) return Messages.titleCase(((Mob) obj).name());
            if (obj instanceof Trap) return Messages.titleCase(((Trap) obj).title());
            if (obj instanceof Heap) return EditHeapComp.getTitle((Heap) obj);
            return null;
        }
    }

    private class TabBody extends Component {

        private final DefaultEditComp<?> content;
        private Runnable layouter;
        private ScrollPane sp;

        public TabBody(DefaultEditComp<?> content) {
            super();

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
        CustomDungeon.knowsEverything = false;
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