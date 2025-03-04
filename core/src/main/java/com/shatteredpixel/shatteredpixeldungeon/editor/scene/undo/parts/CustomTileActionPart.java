package com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditCustomTileComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.CustomTileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.CustomTerrain;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPartList;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomTileLoader;
import com.shatteredpixel.shatteredpixeldungeon.levels.CavesBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;

public /*sealed*/ class CustomTileActionPart extends TileItem.PlaceTileActionPart {

    protected final CustomTilemap customTile;
    protected final int terrain;

    protected CustomTileActionPart(int cell, int terrain, CustomTilemap customTile) {
        super(cell, terrain, true, customTile.wallVisual);
        this.customTile = customTile;
        this.terrain = terrain;
    }

    public static void place(CustomTilemap customTile, int cell) {
        customTile.setRect(cell % Dungeon.level.width(), cell / Dungeon.level.width(),
                customTile.tileW, customTile.tileH);
        if (customTile instanceof CustomTileLoader.SimpleCustomTile) {
            ((CustomTileLoader.SimpleCustomTile) customTile).updateValues();
        }
        if (customTile.wallVisual) {
            Dungeon.level.customWalls.add(customTile);
        } else {
            Dungeon.level.customTiles.add(customTile);
        }
        EditorScene.add(customTile);
        EditorScene.updateMap(cell);
    }

    public static void remove(CustomTilemap customTile) {
        if (!Dungeon.level.customWalls.remove(customTile))
            Dungeon.level.customTiles.remove(customTile);
        EditorScene.remove(customTile);
    }

    public static class Place extends CustomTileActionPart {

        private ActionPartList otherTerrainChanges;//for customTiles larger than 1x1

        public Place(int cell, int terrain, CustomTilemap customTile) {
            super(cell, getFirstTerrain(terrain, customTile), customTile);
            if (customTile.tileW > 1 || customTile.tileH > 1) {
                otherTerrainChanges = new ActionPartList();
                int startPos = cell - customTile.offsetCenterX - customTile.offsetCenterY * Dungeon.level.width();
                customTile.create();//Need to render image first so we know the blank spots
                int[] terrains = customTile instanceof CustomTerrain ? ((CustomTerrain) customTile).getTerrain() : null;
                for (int i = 0; i < customTile.tileH; i++) {
                    for (int j = 0; j < customTile.tileW; j++) {
                        if (customTile.image(j, i) != null) {
                            int pos = startPos + j + i * Dungeon.level.width();
                            if (pos != cell) otherTerrainChanges.addActionPart(TileItem.place(pos, terrains == null ? newTerrain() : terrains[j + i * customTile.tileW],
                                    CustomTileItem.findCustomTileAt(pos, customTile.wallVisual) != null, customTile.wallVisual));
                        }
                    }
                }
                otherTerrainChanges.redo();
            }
            place(customTile, cell());
            
            if (customTile instanceof CavesBossLevel.MetalGate) {
                addToMoreActions(((CavesBossLevel.MetalGate) customTile).placeBarriers());
            }
        }

        private static int getFirstTerrain(int terrain, CustomTilemap customTile) {
            if (customTile instanceof CustomTerrain) {
                int[] terrains = ((CustomTerrain) customTile).getTerrain();
                return terrains[customTile.offsetCenterX + customTile.offsetCenterY * customTile.tileW];
            }
            return terrain;
        }

        @Override
        public void undo() {
            if (otherTerrainChanges != null) otherTerrainChanges.undo();//need to do this first because this would remove custom tiles
            super.undo();
        }

        @Override
        public void redo() {
            super.redo();
            if (otherTerrainChanges != null) otherTerrainChanges.redo();
            if (customTile != null) place(customTile, cell());
        }

        @Override
        public boolean hasContent() {
            return !EditCustomTileComp.areEqual(customTile, oldCustomTile()) || super.hasContent();
        }
    }

    public static class Remove extends CustomTileActionPart {

        protected final int offset;//can be removed from anywhere on the customTile, but placed only at one specific position

        public Remove(int cell, int terrain, CustomTilemap customTile) {
            super(cell, terrain, customTile);
            offset = cell - customTile.tileX - customTile.tileY * Dungeon.level.width()
                    - customTile.offsetCenterX - customTile.offsetCenterY * Dungeon.level.width();
            remove(customTile);
            
            if (customTile instanceof CavesBossLevel.MetalGate) {
                addToMoreActions(((CavesBossLevel.MetalGate) customTile).removeBarriers());
            }
        }

        @Override
        public void undo() {
            super.undo();

            place(customTile, cell() - offset);
            EditorScene.updateMap(cell());
        }

        @Override
        public void redo() {
            super.redo();
            if (customTile != null) {
                remove(customTile);
//                EditorScene.updateMap(cell());
            }
        }

        @Override
        public boolean hasContent() {
            return customTile != null || super.hasContent();
        }
    }

}
