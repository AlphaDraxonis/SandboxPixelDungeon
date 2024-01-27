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
    private Boolean newCustomTileIsWall;

    public PlaceCellActionPart(int oldTerrain, int newTerrain, int cell, Trap oldTrap, Plant oldPlant, CustomTilemap oldCustomTile, Boolean newCustomTileIsWall) {
        init(oldTerrain, newTerrain, cell, oldTrap, oldPlant, oldCustomTile, newCustomTileIsWall);
    }

    public PlaceCellActionPart() {
    }

    protected void init(int oldTerrain, int newTerrain, int cell, Trap oldTrap, Plant oldPlant, CustomTilemap oldCustomTile, Boolean newCustomTileIsWall) {
        this.oldTerrain = oldTerrain;
        this.newTerrain = newTerrain;
        this.cell = cell;
        this.oldTrap = oldTrap;
        this.oldPlant = oldPlant;
        this.oldCustomTile = oldCustomTile;
        this.newCustomTileIsWall = newCustomTileIsWall;

        redo();
    }

    @Override
    public void undo() {
        Level.set(cell, oldTerrain);
        if (oldTrap != null) Dungeon.level.setTrap(oldTrap, cell);
        Dungeon.level.plants.put(cell, oldPlant);
        CustomTileItem.removeCustomTilesAt(cell, Dungeon.level, newCustomTileIsWall);
        if (oldCustomTile != null) {
            if (oldCustomTile.wallVisual) Dungeon.level.customWalls.add(oldCustomTile);
            else Dungeon.level.customTiles.add(oldCustomTile);
            EditorScene.add(oldCustomTile);
        }
    }

    @Override
    public void redo() {
        Level.set(cell, newTerrain); //old traps are already removed here
        CustomTileItem.removeCustomTilesAt(cell, Dungeon.level, newCustomTileIsWall);
    }

    @Override
    public boolean hasContent() {
        return oldTerrain != newTerrain
                || (oldCustomTile() == null && CustomTileItem.findAnyCustomTileAt(cell()) != null)
                || oldCustomTile() != null && !EditCustomTileComp.areEqual(oldCustomTile(), CustomTileItem.findCustomTileAt(cell(), oldCustomTile().wallVisual));
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