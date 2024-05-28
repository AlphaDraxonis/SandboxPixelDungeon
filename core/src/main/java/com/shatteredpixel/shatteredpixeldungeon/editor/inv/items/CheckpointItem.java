package com.shatteredpixel.shatteredpixeldungeon.editor.inv.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.Checkpoint;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditCheckpointComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.CheckpointActionPart;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.watabou.noosa.Image;

public class CheckpointItem extends EditorItem<Checkpoint> {

    public CheckpointItem() {
    }

    public CheckpointItem(Checkpoint checkpoint) {
        this.obj = checkpoint;
    }

    @Override
    public DefaultEditComp<?> createEditComponent() {
        return new EditCheckpointComp(this);
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
    public Item getCopy() {
        return new CheckpointItem(getObject().getCopy());
    }

    @Override
    public void setObject(Checkpoint obj) {
        Checkpoint copy = obj.getCopy();
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
        return !Dungeon.level.insideMap(cell)//can be placed on walls because these might be destroyed by the player
                || Dungeon.level.solid[cell];
    }

    public static ActionPart remove(int cell) {
        Checkpoint checkpoint = Dungeon.level.checkpoints.get(cell);
        if (checkpoint != null) {
            return new CheckpointActionPart.Remove(checkpoint);
        }
        return null;
    }


    public ActionPart place(Checkpoint checkpoint, int cell) {
        if (checkpoint != null && !EditCheckpointComp.areEqual(Dungeon.level.checkpoints.get(cell), checkpoint)) {
            checkpoint.pos = cell;
            return new CheckpointActionPart.Place(checkpoint);
        }
        return null;
    }
}