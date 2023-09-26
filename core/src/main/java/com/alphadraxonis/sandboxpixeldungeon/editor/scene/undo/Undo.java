package com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo;

import com.alphadraxonis.sandboxpixeldungeon.SPDSettings;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

public final class Undo {


    private static final int MAX_SIZE = 50;

    private static final Deque<ActionPartList> undoStack = new ArrayDeque<>();
    private static final Deque<ActionPartList> redoStack = new ArrayDeque<>();
    private static final Deque<ActionPartList> actionsInProgress = new ArrayDeque<>(3);

    private Undo() {
    }

    public static void startAction() {
        actionsInProgress.push(new ActionPartList());
    }

    public static void endAction() {
        if (!actionsInProgress.isEmpty()) {
            addAction(actionsInProgress.pop());
        }
    }

    public static void addActionPart(ActionPart part) {
        if (part != null && part.hasContent() && !actionsInProgress.isEmpty()) {
            ActionPartList currentAction = actionsInProgress.peek();
            currentAction.addActionPart(part);
        }
    }

    private static int autoSaveCounter = SPDSettings.autoSave();

    private static void addAction(ActionPartList action) {
        if (action.hasContent()) {
            undoStack.push(action);
            if (undoStack.size() > MAX_SIZE) {
                undoStack.removeLast();
            }
            redoStack.clear(); // Clear redo stack when a new action is added
            EditorScene.updateUndoButtons();
        }
        if (autoSaveCounter == 25) {
            autoSaveCounter = SPDSettings.autoSave() * 5;
            if (SPDSettings.powerSaver()) autoSaveCounter /= 2;
            try {
                EditorScene.customLevel().levelScheme.saveLevel();
            } catch (IOException e) {
            }
        } else if (SPDSettings.autoSave() > 0) autoSaveCounter++;
    }

    public static void undo() {
        if (!undoStack.isEmpty()) {
            ActionPartList action = undoStack.pop();
            action.undo();
            redoStack.push(action);
            EditorScene.updateUndoButtons();
        }
    }

    public static void redo() {
        if (!redoStack.isEmpty()) {
            ActionPartList action = redoStack.pop();
            action.redo();
            undoStack.push(action);
            EditorScene.updateUndoButtons();
        }
    }

    public static boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public static boolean canRedo() {
        return !redoStack.isEmpty();
    }


    public static void reset() {
        undoStack.clear();
        redoStack.clear();
        EditorScene.updateUndoButtons();
        autoSaveCounter = SPDSettings.autoSave() * 5;
        if (SPDSettings.powerSaver()) autoSaveCounter /= 2;
    }

}