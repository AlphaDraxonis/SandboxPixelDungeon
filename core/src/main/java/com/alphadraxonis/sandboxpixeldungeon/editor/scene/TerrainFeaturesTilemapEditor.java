package com.alphadraxonis.sandboxpixeldungeon.editor.scene;

import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomLevel;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.Trap;
import com.alphadraxonis.sandboxpixeldungeon.tiles.TerrainFeaturesTilemap;

public class TerrainFeaturesTilemapEditor extends TerrainFeaturesTilemap {


    public TerrainFeaturesTilemapEditor(CustomLevel customLevel) {
        super(customLevel.plants, customLevel.traps);
    }

    @Override
    protected int getTileVisual(int pos, int tile, boolean flat) {

        if (traps.get(pos) != null) {
            Trap trap = traps.get(pos);
            return (trap.active ? trap.color : Trap.BLACK) + (trap.shape * 16) +
                    (trap.visible ? 0 : 128);//Added a transparent set of trap images to the file...
        }

        if (plants.get(pos) != null) {
            return plants.get(pos).image + 7 * 16;
        }
        return -1;
    }

}