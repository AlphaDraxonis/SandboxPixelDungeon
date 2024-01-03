package com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditBarrierComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPartModify;

public /*sealed*/ abstract class BarrierActionPart implements ActionPart {

    private Barrier barrier, copyForUndo;
    protected final int cell;

    private BarrierActionPart(Barrier barrier) {
        this.barrier = barrier;
        this.cell = barrier.pos;
        copyForUndo = (Barrier) barrier.getCopy();

        redo();
    }

    protected void place() {
        place(copyForUndo);
        barrier = copyForUndo;
        copyForUndo = (Barrier) barrier.getCopy();
    }

    protected void remove() {
        Barrier barrierAtCell = EditorScene.customLevel().barriers.get(cell);
        if (barrierAtCell != null)
            barrier = barrierAtCell;//This is because another place action could swap the actual barrier with another copy
        remove(cell);
    }

    private static void place(Barrier barrier) {
        Dungeon.level.barriers.put(barrier.pos, barrier);
        EditorScene.updateMap(barrier.pos);
    }

    private static void remove(int pos) {
        Dungeon.level.barriers.remove(pos);
        EditorScene.updateMap(pos);
    }

    @Override
    public boolean hasContent() {
        return true;
    }

    public static final class Place extends BarrierActionPart {

        public Place(Barrier barrier) {
            super(barrier);
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

    public static final class Remove extends BarrierActionPart {
        public Remove(Barrier barrier) {
            super(barrier);
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

        private final Barrier before;
        private Barrier after;

        public Modify(Barrier barrier) {
            before = (Barrier) barrier.getCopy();
            after = barrier;
        }

        @Override
        public void undo() {
            remove(after.pos);
            place((Barrier) before.getCopy());
        }

        @Override
        public void redo() {
            remove(after.pos);
            place((Barrier) after.getCopy());
        }

        @Override
        public boolean hasContent() {
            return !EditBarrierComp.areEqual(before, after);
        }

        @Override
        public void finish() {
            after = (Barrier) after.getCopy();
        }
    }
}