package com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo;

import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;

import java.util.ArrayDeque;
import java.util.Deque;

public final class Undo {


    private static final int MAX_SIZE = 50;

    private static final Deque<ActionPartList> undoStack = new ArrayDeque<>();
    private static final Deque<ActionPartList> redoStack = new ArrayDeque<>();

    private Undo() {
    }

    public static void startAction() {
        undoStack.push(new ActionPartList());
    }

    public static void endAction() {
        if (!undoStack.isEmpty()) {
            addAction(undoStack.pop());
        }
    }

    public static void addActionPart(ActionPart part) {
        if (part != null && !undoStack.isEmpty()) {
            ActionPartList currentAction = undoStack.peek();
            currentAction.addActionPart(part);
        }
    }

    public static void addActionPart(ActionPartList part) {
        if (part != null && !part.isEmpty()) {
            addActionPart((ActionPart) part);
        }
    }

    private static void addAction(ActionPartList action) {
        if (!action.isEmpty()) {
            undoStack.push(action);
            if (undoStack.size() > MAX_SIZE) {
                undoStack.removeLast();
            }
            redoStack.clear(); // Clear redo stack when a new action is added
            EditorScene.updateUndoButtons();
        }
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
    }

}