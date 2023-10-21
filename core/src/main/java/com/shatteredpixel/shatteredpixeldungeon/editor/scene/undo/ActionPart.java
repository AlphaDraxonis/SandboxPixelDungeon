package com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo;

public interface ActionPart {

    void undo();
    void redo();

    boolean hasContent();

}