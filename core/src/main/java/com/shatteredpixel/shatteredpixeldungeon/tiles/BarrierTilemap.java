package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.watabou.utils.SparseArray;

public class BarrierTilemap extends DungeonTilemap {
    private static BarrierTilemap instance;

    protected SparseArray<Barrier> barriers;

    public BarrierTilemap(SparseArray<Barrier> barriers) {
        super(Assets.Environment.BARRIER);

        this.barriers = barriers;

        map( Dungeon.level.map, Dungeon.level.width() );

        instance = this;
    }

    protected int getTileVisual(int pos, int tile, boolean flat){
        if (barriers.get(pos) != null) {
            Barrier barrier = barriers.get(pos);
            if (barrier.visible || Dungeon.customDungeon.seeSecrets || CustomDungeon.isEditing()){
                return barrier.visible ? 1 : 0;
            }
        }
        return -1;
    }

}