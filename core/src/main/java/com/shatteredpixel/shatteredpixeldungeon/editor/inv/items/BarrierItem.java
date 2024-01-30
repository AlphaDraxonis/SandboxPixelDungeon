package com.shatteredpixel.shatteredpixeldungeon.editor.inv.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditBarrierComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.BarrierActionPart;
import com.watabou.noosa.Image;

public class BarrierItem extends EditorItem<Barrier> {

    public BarrierItem() {
    }

    public BarrierItem(Barrier barrier) {
        this.obj = barrier;
    }

    @Override
    public DefaultEditComp<?> createEditComponent() {
        return new EditBarrierComp(this);
    }

    @Override
    public String name() {
        return getObject().name();
    }

    @Override
    public Image getSprite() {
        return getObject().getSprite();
    }

    @Override
    public void setObject(Barrier obj) {
        Barrier copy = obj.getCopy();
        copy.pos = -1;
        super.setObject(copy);
    }

    @Override
    public void place(int cell) {
        if (!invalidPlacement(cell)) {
            Undo.addActionPart(remove(cell));
            Undo.addActionPart(place(getObject().getCopy(), cell));
        }
    }

    public static boolean invalidPlacement(int cell) {
        return Dungeon.level.solid[cell] || !Dungeon.level.insideMap(cell);
    }

    public static ActionPart remove(int cell) {
        Barrier barrier = Dungeon.level.barriers.get(cell);
        if (barrier != null) {
            return new BarrierActionPart.Remove(barrier);
        }
        return null;
    }


    public ActionPart place(Barrier barrier, int cell) {
        if (barrier != null && !EditBarrierComp.areEqual(Dungeon.level.barriers.get(cell), barrier)) {
            barrier.pos = cell;
            return new BarrierActionPart.Place(barrier);
        }
        return null;
    }
}