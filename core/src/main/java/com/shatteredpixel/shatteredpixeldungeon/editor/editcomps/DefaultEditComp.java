package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObject;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObjectManager;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.LuaCustomObject;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.interfaces.CustomGameObjectClass;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.interfaces.CustomObjectClass;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.interfaces.LuaCustomObjectClass;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.ui.editcomps.CustomObjectEditor;
import com.shatteredpixel.shatteredpixeldungeon.editor.ArrowCell;
import com.shatteredpixel.shatteredpixeldungeon.editor.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.editor.Checkpoint;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.EToolbar;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.dungeon.WndSelectDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPartModify;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.ArrowCellActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.BarrierActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.CheckpointActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.HeapActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.MobActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.PlantActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.TileModify;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.TrapActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.AdvancedListPaneItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndGameInProgress;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.idewindowactions.LuaScript;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public abstract class DefaultEditComp<T> extends Component {


    protected final Component title;
    protected final Component mainTitleComp;
    protected final RenderedTextBlock desc;

    protected final IconButton rename, delete;

    protected CheckBox inheritStats;
    protected CustomObjectEditor<?> customObjectEditor;
    protected RedButton viewScript; // for test-games only

    protected final T obj;

    private Runnable onUpdate;
    public AdvancedListPaneItem advancedListPaneItem;
    protected boolean doLayoutTitle = true;

    public DefaultEditComp(T obj) {

        this.obj = obj;

        rename = new IconButton(Icons.SCROLL_COLOR.get()) {
            @Override
            protected void onClick() {
                onRenameClicked();
            }

            @Override
            protected String hoverText() {
                return Messages.get(WndSelectDungeon.class, "rename_yes");
            }

            @Override
            public void setVisible(boolean flag) {
                super.setVisible(flag);
            }
        };
        rename.setVisible(false);

        delete = new IconButton(Icons.get(Icons.TRASH)) {
            @Override
            protected void onClick() {
                onDeleteClicked();
            }

            @Override
            protected String hoverText() {
                return Messages.get(WndGameInProgress.class, "erase");
            }
        };
        delete.setVisible(false);

        mainTitleComp = createTitle();

        title = new Component() {
            @Override
            protected void createChildren() {
                add(mainTitleComp);
                add(rename);
                add(delete);
            }

            @Override
            protected void layout() {
                layoutTitle();
            }

            @Override
            public float height() {
                return mainTitleComp.height();
            }
        };
        add(title);

        desc = PixelScene.renderTextBlock(createDescription(), 6);
        add(desc);

        bringToFront(title);
    }

    protected void initializeCompsForCustomObjectClass() {
        if (!CustomDungeon.isEditing()) {
            if (obj instanceof LuaCustomObjectClass) {
                
                int identifier = ((LuaCustomObjectClass) obj).getIdentifier();
                LuaCustomObject customObject = CustomObjectManager.getUserContent(identifier, LuaCustomObject.class);
                
                if (customObject.getLuaScriptPath() != null) {
                    viewScript = new RedButton(Messages.get(this, "view_code")) {
                        @Override
                        protected void onClick() {
                            int identifier = ((LuaCustomObjectClass) obj).getIdentifier();
                            LuaCustomObject customObject = CustomObjectManager.getUserContent(identifier, LuaCustomObject.class);
                            LuaScript script = CustomDungeonSaves.readLuaFile(customObject.getLuaScriptPath());
                            DungeonScene.show(
                                    script == null
                                            ? new WndError("Error loading script")
                                            : new WndTitledMessage(Icons.INFO.get(), customObject.getLuaScriptPath(), script.code) {{
                                        setHighlightingEnabled(false);
                                    }}
                            );
                        }
                    };
                    add(viewScript);
                }
            }
            return;
        }

        if (obj instanceof CustomGameObjectClass && !((CustomGameObjectClass) obj).isOriginal()) {
            inheritStats = new CheckBox(Messages.get(this, "inherit_stats")) {
                boolean initializing;
                {
                    initializing = true;
                    checked(((CustomGameObjectClass) obj).getInheritStats());
                    initializing = false;
                }
                @Override
                public void checked(boolean value) {
                    if (value != checked()) {
                        onInheritStatsClicked(value, initializing);
                        updateStates();
                        updateObj();
                    }
                    super.checked(value);
                }
            };
            add(inheritStats);
        }

        if (obj instanceof CustomObjectClass) {
            CustomObject customObject = CustomObjectManager.getUserContent(((CustomObjectClass) obj).getIdentifier(), null);
            customObjectEditor = customObject == null ? null : customObject.createCustomObjectEditor(this::updateObj);
        } else if (obj instanceof CustomObject) {
            customObjectEditor = ((CustomObject) obj).createCustomObjectEditor(this::updateObj);
        }
        if (customObjectEditor != null)
            add(customObjectEditor);

        bringToFront(title);
    }

    public void setDoLayoutTitle(boolean doLayoutTitle) {
        this.doLayoutTitle = doLayoutTitle;
        if (doLayoutTitle) add(title);
        else remove(title);
    }

    @Override
    protected void layout() {
        desc.maxWidth((int) width);

        float posY = y;

        if (title.visible && doLayoutTitle) {
            title.setRect(x, posY, width, mainTitleComp.height());
            posY = mainTitleComp.bottom();
        }
        if (desc.visible) {
            if (title.visible && doLayoutTitle) posY += WndTitledMessage.GAP * 2;
            desc.setRect(x, posY, desc.width(), desc.height());
            posY = desc.bottom();
        }

        height = posY + 2 - y;
        if (height == 2) height = 0;

        layoutCompsLinear(inheritStats);
    }

    protected void layoutCustomObjectEditor() {
        if (customObjectEditor != null && customObjectEditor.visible) {
            customObjectEditor.setRect(x, height + WndTitledMessage.GAP, width, 0);
            height += customObjectEditor.height() + WndTitledMessage.GAP;
        }
        layoutCompsLinear(viewScript);
    }

    protected void layoutTitle() {
        //if you edit this, also check out EditItemComp#layout()

        float renameDeleteWidth = (rename.visible ? rename.icon().width() + 2 : 0)
                + (delete.visible ? delete.icon().width() + 2 : 0);

        float posX = title.left();

        mainTitleComp.setRect(posX, title.top(), title.width() - renameDeleteWidth, -1);
        posX = mainTitleComp.right();

        float h = title.height();

        if (rename.visible) {
            rename.setRect(posX, mainTitleComp.top() + (h - rename.icon().height()) * 0.5f, rename.icon().width(), rename.icon().height());
            posX += rename.width() + 2;
        }
        if (delete.visible) {
            delete.setRect(posX, mainTitleComp.top() + (h - delete.icon().height()) * 0.5f, delete.icon().width(), delete.icon().height());
            posX += delete.width() + 2;
        }
    }

    protected final void layoutCompsLinear(Component... comps) {
        if (height > 0) height += WndTitledMessage.GAP;
        float newHeight = EditorUtilities.layoutCompsLinear(WndTitledMessage.GAP, this, comps);
        if (newHeight == height && height > WndTitledMessage.GAP) height -= WndTitledMessage.GAP;
        else height = newHeight;
    }

    protected final void layoutCompsInRectangles(Component... comps) {
        layoutCompsInRectangles(WndTitledMessage.GAP, comps);
    }

    protected final void layoutCompsInRectangles(int gap, Component... comps) {
        if (height > 0) height += gap;
        float newHeight = EditorUtilities.layoutStyledCompsInRectangles(WndTitledMessage.GAP, width, this, comps);
        if (newHeight == height) height -= gap;
        else height = newHeight;
    }
    
    protected final void layoutOneRectCompInRow(Component... comps) {
        int gap = WndTitledMessage.GAP;
        if (height > 0) height += gap;
        float newHeight = EditorUtilities.layoutStyledCompsInRectangles(WndTitledMessage.GAP,width, 1,this, comps);
        if (newHeight == height) height -= gap;
        else height = newHeight;
    }

    protected void onInheritStatsClicked(boolean flag, boolean initializing) {
        if (viewScript != null) viewScript.visible = viewScript.active = !flag;
    }

    protected void onRenameClicked() {
    }

    protected void onDeleteClicked() {
    }

    protected void onShow(boolean fullyInitialized) {
    }

    protected Component createTitle() {
        return new IconTitle(getIcon(), createTitleText());
    }

    public Component getTitleComponent() {
        return title;
    }

    protected abstract String createTitleText();

    protected abstract String createDescription();

    public abstract Image getIcon();

    public T getObj() {
        return obj;
    }


    public void updateObj() {
        if (mainTitleComp instanceof IconTitle) {
            ((IconTitle) mainTitleComp).label(createTitleText());
            ((IconTitle) mainTitleComp).icon(getIcon());
        }
        desc.text(createDescription());

        layout();
        if (advancedListPaneItem != null) advancedListPaneItem.onUpdate();
        if (onUpdate != null) onUpdate.run();

        EToolbar.updateSlot(obj);
    }

    protected void updateStates() {
    }

    public void setOnUpdate(Runnable onUpdate) {
        this.onUpdate = onUpdate;
    }

    public Runnable getOnUpdate() {
        return onUpdate;
    }


    public static void showWindow(int terrainType, int terrainImage, Heap heap, Mob mob, Trap trap, Plant plant, Barrier barrier, ArrowCell arrowCell, Checkpoint checkpoint, int cell) {

        CustomDungeon.knowsEverything = true;

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
        if (barrier != null) numTabs++;
        if (arrowCell != null) numTabs++;
        if (checkpoint != null) numTabs++;

        if (numTabs == 0) return;
        if (numTabs > 1 || (heap != null && !heap.items.isEmpty())) {
            EditorScene.show(new EditCompWindowTabbed(tileItem, heap, mob, trap, plant, barrier, arrowCell, checkpoint, numTabs));
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
        } else if (plant != null) {
            content = new EditPlantComp(plant);
            actionPart = new PlantActionPart.Modify(plant);
        } else if (barrier != null) {
            content = new EditBarrierComp(barrier);
            actionPart = new BarrierActionPart.Modify(barrier);
        } else if (arrowCell != null) {
            content = new EditArrowCellComp(arrowCell);
            actionPart = new ArrowCellActionPart.Modify(arrowCell);
        } else {
            content = new EditCheckpointComp(checkpoint);
            actionPart = new CheckpointActionPart.Modify(checkpoint);
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
                CustomDungeon.knowsEverything = false;
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
            float maxHeightNoOffset = Window.WindowSize.HEIGHT_LARGE.get() - 10;
            int offset = EditorUtilities.getMaxWindowOffsetYForVisibleToolbar();
            if (ch > maxHeightNoOffset) {
                if (ch > maxHeightNoOffset + offset) ch = maxHeightNoOffset + offset;
                else offset = (int) Math.ceil(ch - maxHeightNoOffset);
            }
            offset = Math.max(offset, 10);


            w.offset(w.getOffset().x, -offset);
            w.resize((int) Math.ceil(newWidth), (int) Math.ceil(ch));
            sp.setSize((int) Math.ceil(newWidth), (int) Math.ceil(ch));
            sp.scrollToCurrentView();
        };
        content.setOnUpdate(r);
        r.run();

        sp.givePointerPriority();

        DungeonScene.show(w);
    }

}