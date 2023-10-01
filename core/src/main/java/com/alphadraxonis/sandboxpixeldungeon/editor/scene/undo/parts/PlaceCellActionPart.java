package com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.parts;

import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.EditCustomTileComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.items.CustomTileItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.ActionPart;
import com.alphadraxonis.sandboxpixeldungeon.levels.Level;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.Trap;
import com.alphadraxonis.sandboxpixeldungeon.plants.Plant;
import com.alphadraxonis.sandboxpixeldungeon.tiles.CustomTilemap;

public class PlaceCellActionPart implements ActionPart {

    private int oldTerrain, newTerrain, cell;
    private Trap oldTrap;
    private Plant oldPlant;
    private CustomTilemap oldCustomTile;
    private boolean wall;

    public PlaceCellActionPart(int oldTerrain, int newTerrain, int cell, Trap oldTrap, Plant oldPlant, CustomTilemap oldCustomTile) {
        init(oldTerrain, newTerrain, cell, oldTrap, oldPlant, oldCustomTile);
    }

    public PlaceCellActionPart() {
    }

    protected void init(int oldTerrain, int newTerrain, int cell, Trap oldTrap, Plant oldPlant, CustomTilemap oldCustomTile) {
        this.oldTerrain = oldTerrain;
        this.newTerrain = newTerrain;
        this.cell = cell;
        this.oldTrap = oldTrap;
        this.oldPlant = oldPlant;
        this.oldCustomTile = oldCustomTile;
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
            Dungeon.level.customTiles.add(oldCustomTile);
            EditorScene.add(oldCustomTile, wall);
        }
    }

    @Override
    public void redo() {
        Level.set(cell, newTerrain); //old traps are already removed here
        CustomTileItem.removeCustomTilesAt(cell, Dungeon.level);
    }

    @Override
    public boolean hasContent() {
        return oldTerrain != newTerrain || !EditCustomTileComp.areEqual(oldCustomTile(),CustomTileItem.findCustomTileAt(cell()));
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