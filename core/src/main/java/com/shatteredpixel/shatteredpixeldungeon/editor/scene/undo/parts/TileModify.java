package com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.transitions.TransitionEditPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPartModify;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;

public final class TileModify implements ActionPartModify {

    private final LevelTransition transitionBefore;
    private LevelTransition transitionAfter;

    private final BlobActionPart.BlobData blobsBefore;//This is basically only BlobEditPart-Modify...
    private BlobActionPart.BlobData blobsAfter;

    private final int cell;

    public TileModify(int cell) {
        this(EditorScene.customLevel().transitions.get(cell), cell);
    }

    public TileModify(LevelTransition transition, int cell) {
        if (transition != null) {
            transitionBefore = (LevelTransition) transition.getCopy();
        } else transitionBefore = null;
        blobsBefore = new BlobActionPart.BlobData(cell);
        this.cell = cell;
    }

    @Override
    public void undo() {
        CustomLevel level = EditorScene.customLevel();
        LevelTransition oldTrans = level.transitions.get(cell);

        if (oldTrans != null) {//remove
            level.transitions.remove(cell);
            EditorScene.remove(oldTrans);
        }

        if (transitionBefore != null) {//place
            EditorScene.customLevel().transitions.put(cell, transitionBefore);
            EditorScene.add(transitionBefore);
        }

        blobsBefore.place(cell);
    }

    @Override
    public void redo() {
        CustomLevel level = EditorScene.customLevel();
        LevelTransition oldTrans = level.transitions.get(cell);

        if (oldTrans != null) {//remove
            level.transitions.remove(cell);
            EditorScene.remove(oldTrans);
        }

        if (transitionAfter != null) {//place
            EditorScene.customLevel().transitions.put(cell, transitionAfter);
            EditorScene.add(transitionAfter);
        }

        blobsAfter.place(cell);
    }

    @Override
    public boolean hasContent() {
        return !TransitionEditPart.areEqual(transitionBefore, transitionAfter) || !BlobActionPart.BlobData.areEqual(blobsBefore, blobsAfter);
    }

    @Override
    public void finish() {
        if (cell < Dungeon.level.length()) {
            LevelTransition newTrans = EditorScene.customLevel().transitions.get(cell);
            if (newTrans != null) transitionAfter = newTrans.getCopy();
            blobsAfter = new BlobActionPart.BlobData(cell);
        }
    }
}