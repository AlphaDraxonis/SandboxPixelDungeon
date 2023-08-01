package com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.parts;

import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.parts.transitions.TransitionEditPart;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomLevel;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.ActionPartModify;
import com.alphadraxonis.sandboxpixeldungeon.levels.features.LevelTransition;

public final class TileModify implements ActionPartModify {

    private final LevelTransition transitionBefore;
    private LevelTransition transitionAfter;

    private final int cell;

    public TileModify(int cell) {
        this(EditorScene.customLevel().transitions.get(cell), cell);
    }

    public TileModify(LevelTransition transition, int cell) {
        if (transition != null) {
            transitionBefore = (LevelTransition) transition.getCopy();
        } else transitionBefore = null;
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
    }

    @Override
    public boolean hasContent() {
        return !TransitionEditPart.areEqual(transitionBefore, transitionAfter);
    }

    @Override
    public void finish() {
        LevelTransition newTrans = EditorScene.customLevel().transitions.get(cell);
        if (newTrans != null) transitionAfter = newTrans.getCopy();
    }
}