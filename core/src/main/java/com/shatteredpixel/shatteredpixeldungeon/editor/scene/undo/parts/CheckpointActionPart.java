package com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.Checkpoint;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditCheckpointComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPartModify;

public /*sealed*/ abstract class CheckpointActionPart implements ActionPart {

    private Checkpoint checkpoint, copyForUndo;
    protected final int cell;

    private CheckpointActionPart(Checkpoint checkpoint) {
        this.checkpoint = checkpoint;
        this.cell = checkpoint.pos;
        copyForUndo = checkpoint.getCopy();

        redo();
    }

    protected void place() {
        place(copyForUndo);
        checkpoint = copyForUndo;
        copyForUndo = checkpoint.getCopy();
    }

    protected void remove() {
        Checkpoint checkpointAtCell = Dungeon.level.checkpoints.get(cell);
        if (checkpointAtCell != null)
            checkpoint = checkpointAtCell;//This is because another place action could swap the actual checkpoint with another copy
        remove(cell);
    }

    private static void place(Checkpoint checkpoint) {
        Dungeon.level.checkpoints.put(checkpoint.pos, checkpoint);
        EditorScene.add(checkpoint);
        EditorScene.updateMap(checkpoint.pos);
    }

    private static void remove(int pos) {
        Checkpoint cp = Dungeon.level.checkpoints.remove(pos);
        cp.sprite.killAndErase();
        EditorScene.updateMap(pos);
    }

    @Override
    public boolean hasContent() {
        return true;
    }

    public static final class Place extends CheckpointActionPart {

        public Place(Checkpoint checkpoint) {
            super(checkpoint);
        }

        @Override
        public void undo() {
            remove();
        }

        @Override
        public void redo() {
            place();
        }
    }

    public static final class Remove extends CheckpointActionPart {
        public Remove(Checkpoint checkpoint) {
            super(checkpoint);
        }

        @Override
        public void undo() {
            place();
        }

        @Override
        public void redo() {
            remove();
        }
    }

    public static final class Modify implements ActionPartModify {

        private final Checkpoint before;
        private Checkpoint after;

        public Modify(Checkpoint checkpoint) {
            before = checkpoint.getCopy();
            after = checkpoint;
        }

        @Override
        public void undo() {
            remove(after.pos);
            place(before.getCopy());
        }

        @Override
        public void redo() {
            remove(after.pos);
            place(after.getCopy());
        }

        @Override
        public boolean hasContent() {
            return !EditCheckpointComp.areEqual(before, after);
        }

        @Override
        public void finish() {
            after = after.getCopy();
        }
    }
}