package com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.parts;

import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.ActionPart;
import com.alphadraxonis.sandboxpixeldungeon.levels.Level;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.Trap;

public class PlaceCellActionPart implements ActionPart {

    private final int oldTerrain, newTerrain, cell;
    private final Trap oldTrap;

    public PlaceCellActionPart(int oldTerrain, int newTerrain, int cell, Trap oldTrap) {
        this.oldTerrain = oldTerrain;
        this.newTerrain = newTerrain;
        this.cell = cell;
        this.oldTrap = oldTrap;

        redo();
    }

    @Override
    public void undo() {
        Level.set(cell, oldTerrain);
        if (oldTrap != null) Dungeon.level.setTrap(oldTrap, cell);
    }

    @Override
    public void redo() {
        Level.set(cell, newTerrain);
        //old traps are already removed here
    }
}