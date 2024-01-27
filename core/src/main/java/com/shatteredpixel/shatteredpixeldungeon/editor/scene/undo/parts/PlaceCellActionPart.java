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

        redo();
    }

    @Override
    public void undo() {
        Level.set(cell, oldTerrain);
        if (oldTrap != null) Dungeon.level.setTrap(oldTrap, cell);
        Dungeon.level.plants.put(cell, oldPlant);
        CustomTileItem.removeCustomTilesAt(cell, Dungeon.level);
        if (oldCustomTile != null) {
            if (oldCustomTile.wallVisual) Dungeon.level.customWalls.add(oldCustomTile);
            else Dungeon.level.customTiles.add(oldCustomTile);
            EditorScene.add(oldCustomTile);
        }
    }

    @Override
    public void redo() {
        Level.set(cell, newTerrain); //old traps are already removed here
        CustomTileItem.removeCustomTilesAt(cell, Dungeon.level);
    }

    @Override
    public boolean hasContent() {
        return oldTerrain != newTerrain || !EditCustomTileComp.areEqual(oldCustomTile(), CustomTileItem.findCustomTileAt(cell()));
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