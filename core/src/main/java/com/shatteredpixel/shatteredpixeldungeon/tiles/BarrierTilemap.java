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

        map( CustomDungeon.isEditing() ? Dungeon.level.map : Dungeon.level.visualMap, Dungeon.level.width() );

        instance = this;
    }

    @Override
    protected int getTileVisual(int pos, int tile, boolean flat) {
        Barrier barrier = barriers.get(pos);
        if (barrier != null
                && (barrier.visible || Dungeon.customDungeon.seeSecrets || CustomDungeon.isEditing())) {
            return barrier.visible ? 1 : 0;
        }
        return -1;
    }

}