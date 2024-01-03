package com.shatteredpixel.shatteredpixeldungeon.editor.inv.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditBarrierComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.DefaultListItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.EditorInventoryWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.BarrierActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class BarrierItem extends EditorItem<Barrier> {

    public BarrierItem() {
    }

    public BarrierItem(Barrier barrier) {
        this.obj = barrier;
    }

    //TODO maybe implement functionality to only block certain entities, add sub-icons then

    @Override
    public Image getSprite() {
        return getBarrierImage(getObject());
    }

    public static Image getBarrierImage(Barrier barrier) {
        return EditorUtilies.getBarrierTexture(barrier.visible ? 1 : 0);
    }

    public static String createTitle(Barrier barrier) {
        return Messages.titleCase(barrier.name());
    }

    @Override
    public ScrollingListPane.ListItem createListItem(EditorInventoryWindow window) {
        return new DefaultListItem(this, window, createTitle(getObject()), getSprite()) {
            @Override
            public void onUpdate() {
                if (item == null || ((BarrierItem) item).getObject() == null) return;
                Barrier b = ((BarrierItem) item).getObject();
                label.text(BarrierItem.createTitle(b));

                if (icon != null) remove(icon);
                icon = BarrierItem.getBarrierImage(b);
                addToBack(icon);
                remove(bg);
                addToBack(bg);

                super.onUpdate();
            }
        };
    }

    @Override
    public DefaultEditComp<?> createEditComponent() {
        return new EditBarrierComp(this);
    }

    @Override
    public void place(int cell) {
        if (!invalidPlacement(cell, EditorScene.customLevel())) {
            Undo.addActionPart(remove(cell, EditorScene.customLevel()));
            Undo.addActionPart(place(getObject().getCopy(), cell));
        }
    }

    @Override
    public String name() {
        return getObject().name();
    }

    @Override
    public void setObject(Barrier obj) {
        Barrier copy = obj.getCopy();
        copy.pos = -1;
        super.setObject(copy);
    }

    public static boolean invalidPlacement(int cell, CustomLevel level) {
        return level.solid[cell] || !level.insideMap(cell);
    }

    public static BarrierActionPart remove(int cell, CustomLevel level) {
        return remove(level.barriers.get(cell));
    }

    public static BarrierActionPart.Remove remove(Barrier barrier) {
        if (barrier != null) {
            return new BarrierActionPart.Remove(barrier);
        }
        return null;
    }

    public static BarrierActionPart.Place place(Barrier barrier) {
        if (barrier != null && !EditBarrierComp.areEqual(Dungeon.level.barriers.get(barrier.pos), barrier))
            return new BarrierActionPart.Place(barrier);
        return null;
    }

    public static BarrierActionPart.Place place(Barrier barrier, int cell) {
        if (barrier != null && !EditBarrierComp.areEqual(Dungeon.level.barriers.get(cell), barrier)) {
            barrier.pos = cell;
            return new BarrierActionPart.Place(barrier);
        }
        return null;
    }

    private static final String BARRIER = "barrier";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(BARRIER, obj);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        obj = (Barrier) bundle.get(BARRIER);
    }
}