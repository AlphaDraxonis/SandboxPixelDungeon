package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.ArrowCell;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.watabou.utils.SparseArray;

public class ArrowCellTilemap extends DungeonTilemap {
    private static ArrowCellTilemap instance;

    protected SparseArray<ArrowCell> arrowCells;

    public ArrowCellTilemap(SparseArray<ArrowCell> arrowCells) {
        super(Assets.Environment.BARRIER);//tzz

        this.arrowCells = arrowCells;

        map( CustomDungeon.isEditing() ? Dungeon.level.map : Dungeon.level.visualMap, Dungeon.level.width() );

        instance = this;
    }

    @Override
    protected int getTileVisual(int pos, int tile, boolean flat){
        if (arrowCells.get(pos) != null) {
            ArrowCell arrowCell = arrowCells.get(pos);
            if (arrowCell.visible || Dungeon.customDungeon.seeSecrets || CustomDungeon.isEditing()){
                return arrowCell.visible ? 1 : 0;
            }
        }
        return -1;
    }

}