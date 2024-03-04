package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.dungeon.WndSelectDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPartModify;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.BarrierActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.HeapActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.MobActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.PlantActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.TileModify;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.TrapActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.AdvancedListPaneItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndGameInProgress;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public abstract class DefaultEditComp<T> extends Component {


    protected final Component title;
    protected final RenderedTextBlock desc;

    protected final IconButton rename, delete;

    protected final T obj;

    private Runnable onUpdate;
    public AdvancedListPaneItem advancedListPaneItem;

    public DefaultEditComp(T obj) {

        this.obj = obj;

        title = createTitle();
        add(title);
        desc = PixelScene.renderTextBlock(createDescription(), 6);
        add(desc);

        rename = new IconButton(Icons.get(Icons.RENAME_ON)) {
            @Override
            protected void onClick() {
                onRenameClicked();
            }

            @Override
            protected String hoverText() {
                return Messages.get(WndSelectDungeon.class, "rename_yes");
            }
        };
        rename.visible = false;
        add(rename);

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
        delete.visible = false;
        add(delete);
    }

    @Override
    protected void layout() {
        //if you edit this, also check out EditItemComp#layout()

        desc.maxWidth((int) width);

        float renameDeleteWidth = (rename.visible ? rename.icon().width + 2 : 0)
                + (delete.visible ? delete.icon().width + 2 : 0);
        float posY = y;

        if (title.visible) {
            title.setRect(x, posY, width - renameDeleteWidth, title.height());
            posY = title.bottom();
        }
        if (desc.visible) {
            if (title.visible) posY += WndTitledMessage.GAP * 2;
            desc.setRect(x, posY, desc.width(), desc.height());
            posY = desc.bottom();
        }

        float posX = width - renameDeleteWidth;
        if (rename.visible) {
            rename.setRect(posX, title.top() + (title.height() - rename.icon().height) * 0.5f, rename.icon().width, rename.icon().height);
            posX += rename.width() + 2;
        }
        if (delete.visible) {
            delete.setRect(posX, title.top() + (title.height() - delete.icon().height) * 0.5f, delete.icon().width, delete.icon().height);
        }

        height = posY + 1 - y;
        if (height == 1) height = 0;
    }

    protected final void layoutCompsLinear(Component... comps) {
        height += WndTitledMessage.GAP;
        height = EditorUtilies.layoutCompsLinear(WndTitledMessage.GAP, this, comps);
    }

    protected final void layoutCompsInRectangles(Component... comps) {
        height += WndTitledMessage.GAP;
        height = EditorUtilies.layoutStyledCompsInRectangles(WndTitledMessage.GAP, width, this, comps);
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

    protected abstract String createTitleText();

    protected abstract String createDescription();

    public abstract Image getIcon();

    public T getObj() {
        return obj;
    }


    protected void updateObj() {
        if (title instanceof IconTitle) {
            ((IconTitle) title).label(createTitleText());
            ((IconTitle) title).icon(getIcon());
        }
        desc.text(createDescription());

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


    public static void showWindow(int terrainType, int terrainImage, Heap heap, Mob mob, Trap trap, Plant plant, Barrier barrier, int cell) {

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

        if (numTabs == 0) return;
        if (numTabs > 1 || (heap != null && !heap.items.isEmpty())) {
            EditorScene.show(new EditCompWindowTabbed(tileItem, heap, mob, trap, plant, barrier, numTabs));
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
        } else {
            content = new EditPlantComp(plant);
            actionPart = new BarrierActionPart.Modify(barrier);
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
        };
        content.setOnUpdate(r);
        r.run();

        sp.givePointerPriority();

        if (Game.scene() instanceof EditorScene) EditorScene.show(w);
        else Game.scene().addToFront(w);
    }

}