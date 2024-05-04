package com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.ArrowCell;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditArrowCellComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPartModify;

public /*sealed*/ abstract class ArrowCellActionPart implements ActionPart {

    private ArrowCell arrowCell, copyForUndo;
    protected final int cell;

    private ArrowCellActionPart(ArrowCell arrowCell) {
        this.arrowCell = arrowCell;
        this.cell = arrowCell.pos;
        copyForUndo = arrowCell.getCopy();

        redo();
    }

    protected void place() {
        place(copyForUndo);
        arrowCell = copyForUndo;
        copyForUndo = arrowCell.getCopy();
    }

    protected void remove() {
        ArrowCell arrowCellAtCell = Dungeon.level.arrowCells.get(cell);
        if (arrowCellAtCell != null)
            arrowCell = arrowCellAtCell;//This is because another place action could swap the actual arrowCell with another copy
        remove(cell);
    }

    private static void place(ArrowCell arrowCell) {
        Dungeon.level.arrowCells.put(arrowCell.pos, arrowCell);
        EditorScene.updateMap(arrowCell.pos);
    }

    private static void remove(int pos) {
        Dungeon.level.arrowCells.remove(pos);
        EditorScene.updateMap(pos);
    }

    @Override
    public boolean hasContent() {
        return true;
    }

    public static final class Place extends ArrowCellActionPart {

        public Place(ArrowCell arrowCell) {
            super(arrowCell);
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

    public static final class Remove extends ArrowCellActionPart {
        public Remove(ArrowCell arrowCell) {
            super(arrowCell);
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

        private final ArrowCell before;
        private ArrowCell after;

        public Modify(ArrowCell arrowCell) {
            before = arrowCell.getCopy();
            after = arrowCell;
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
            return !EditArrowCellComp.areEqual(before, after);
        }

        @Override
        public void finish() {
            after = after.getCopy();
        }
    }
}