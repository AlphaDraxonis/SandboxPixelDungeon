package com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.parts;

import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.ActionPart;
import com.alphadraxonis.sandboxpixeldungeon.levels.Level;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.Trap;
import com.alphadraxonis.sandboxpixeldungeon.plants.Plant;

public class PlaceCellActionPart implements ActionPart {

    private final int oldTerrain, newTerrain, cell;
    private final Trap oldTrap;
    private final Plant oldPlant;

    public PlaceCellActionPart(int oldTerrain, int newTerrain, int cell, Trap oldTrap, Plant oldPlant) {
        this.oldTerrain = oldTerrain;
        this.newTerrain = newTerrain;
        this.cell = cell;
        this.oldTrap = oldTrap;
        this.oldPlant = oldPlant;

        redo();
    }

    @Override
    public void undo() {
        Level.set(cell, oldTerrain);
        if (oldTrap != null) Dungeon.level.setTrap(oldTrap, cell);
        Dungeon.level.plants.put(cell, oldPlant);
    }

    @Override
    public void redo() {
        Level.set(cell, newTerrain);
        //old traps are already removed here
    }

    @Override
    public boolean hasContent() {
        return oldTerrain != newTerrain;
    }
}