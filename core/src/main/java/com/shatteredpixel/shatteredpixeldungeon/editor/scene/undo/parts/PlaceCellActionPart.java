package com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditCustomTileComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.CustomTileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPart;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;

public class PlaceCellActionPart implements ActionPart {

    private int oldTerrain, newTerrain, cell;
    private Trap oldTrap;
    private Plant oldPlant;
    private CustomTilemap oldCustomTile;
    private boolean wall;
    private boolean oldFlamableDisabled, newFlamableDisabled;

    public PlaceCellActionPart(int oldTerrain, int newTerrain, int cell, boolean flamableDisabled, Trap oldTrap, Plant oldPlant, CustomTilemap oldCustomTile) {
        init(oldTerrain, newTerrain, cell, flamableDisabled, oldTrap, oldPlant, oldCustomTile);
    }

    public PlaceCellActionPart() {
    }

    protected void init(int oldTerrain, int newTerrain, int cell, boolean flamableDisabled, Trap oldTrap, Plant oldPlant, CustomTilemap oldCustomTile) {
        this.oldTerrain = oldTerrain;
        this.newTerrain = newTerrain;
        this.cell = cell;
        this.oldTrap = oldTrap;
        this.oldPlant = oldPlant;
        this.oldCustomTile = oldCustomTile;
        this.oldFlamableDisabled = Dungeon.level.flamableDisabled.contains(cell);
        newFlamableDisabled = flamableDisabled;
        wall = Dungeon.level.customWalls.contains(oldCustomTile);

        redo();
    }

    @Override
    public void undo() {
        Level.set(cell, oldTerrain);
        if (oldTrap != null) Dungeon.level.setTrap(oldTrap, cell);
        Dungeon.level.plants.put(cell, oldPlant);
        CustomTileItem.removeCustomTilesAt(cell, Dungeon.level);
        if (oldCustomTile != null) {
            if (wall) Dungeon.level.customWalls.add(oldCustomTile);
            else Dungeon.level.customTiles.add(oldCustomTile);
            EditorScene.add(oldCustomTile, wall);
        }
        if (oldFlamableDisabled != newFlamableDisabled) {
            if (oldFlamableDisabled) Dungeon.level.flamableDisabled.add(cell);
            else Dungeon.level.flamableDisabled.remove(cell);
        }
    }

    @Override
    public void redo() {
        Level.set(cell, newTerrain); //old traps are already removed here
        CustomTileItem.removeCustomTilesAt(cell, Dungeon.level);
        if (oldFlamableDisabled != newFlamableDisabled) {
            if (newFlamableDisabled) Dungeon.level.flamableDisabled.add(cell);
            else Dungeon.level.flamableDisabled.remove(cell);
        }
    }

    @Override
    public boolean hasContent() {
        return oldTerrain != newTerrain || oldFlamableDisabled != newFlamableDisabled || !EditCustomTileComp.areEqual(oldCustomTile(), CustomTileItem.findCustomTileAt(cell()));
    }

    protected CustomTilemap oldCustomTile() {
        return oldCustomTile;
    }

    public int cell() {
        return cell;
    }

    public int newTerrain() {
        return newTerrain;
    }
}