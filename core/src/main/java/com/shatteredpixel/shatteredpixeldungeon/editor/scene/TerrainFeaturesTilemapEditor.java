package com.shatteredpixel.shatteredpixeldungeon.editor.scene;

import com.shatteredpixel.shatteredpixeldungeon.levels.editor.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.TerrainFeaturesTilemap;

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