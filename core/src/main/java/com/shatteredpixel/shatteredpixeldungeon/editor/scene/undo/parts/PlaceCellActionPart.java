package com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPart;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;

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