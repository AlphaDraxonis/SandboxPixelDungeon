package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObject;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObjectManager;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.interfaces.CustomGameObjectClass;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.interfaces.CustomObjectClass;
import com.shatteredpixel.shatteredpixeldungeon.editor.ArrowCell;
import com.shatteredpixel.shatteredpixeldungeon.editor.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.editor.Checkpoint;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.Zone;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPartModify;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.AdvancedListPaneItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Game;

import java.io.IOException;

public class EditCompWindow extends Window {

    private ScrollPane sp;
    protected final DefaultEditComp<?> content;

    private ActionPartModify actionPartModify;

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

        if (object instanceof EditorItem && ((EditorItem<?>) object).getObject() instanceof CustomObjectClass && ((CustomObjectClass) ((EditorItem<?>) object).getObject()).isOriginal()) {
            actionPartModify = ActionPartModify.startNewModify(((EditorItem<?>) object).getObject());
        }

        content.advancedListPaneItem = advancedListPaneItem;

        init();
    }

    public EditCompWindow(DefaultEditComp<?> content) {
        this.content = content;
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
        if (object instanceof ArrowCell) return new EditArrowCellComp((ArrowCell) object);
        if (object instanceof Checkpoint) return new EditCheckpointComp((Checkpoint) object);
        if (object instanceof Room) return new EditRoomComp((Room) object);
        if (object instanceof CustomObject) return ((CustomObject) object).createEditComp();
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
        float maxHeightNoOffset = WindowSize.HEIGHT_SMALL.get() - 10;
        int offset = EditorUtilities.getMaxWindowOffsetYForVisibleToolbar();
        if (ch > maxHeightNoOffset) {
            if (ch > maxHeightNoOffset + offset) ch = maxHeightNoOffset + offset;
            else offset = (int) Math.ceil(ch - maxHeightNoOffset);
        }
        offset = Math.max(offset, 10);


        offset(xOffset, -offset);
        resize(width, (int) Math.ceil(ch));
        sp.setSize(width, (int) Math.ceil(ch));
        sp.scrollToCurrentView();
    }

    @Override
    public void hide() {
        super.hide();

        if (content.getObj() instanceof CustomGameObjectClass && ((CustomGameObjectClass) content.getObj()).isOriginal()) {

            Undo.startAction();

            actionPartModify.finish();
            Undo.addActionPart(actionPartModify);

            ((CustomGameObjectClass) content.getObj()).updateInheritStats(Dungeon.level);

            Undo.endAction();

			try {
				CustomDungeonSaves.storeCustomObject((CustomObject) CustomObjectManager.getUserContent(((CustomGameObjectClass) content.getObj()).getIdentifier(), null));
			} catch (IOException e) {
                Game.reportException(e);
			}
		}
    }
}