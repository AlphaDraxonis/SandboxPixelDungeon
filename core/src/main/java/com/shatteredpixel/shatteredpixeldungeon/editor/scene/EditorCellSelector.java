package com.shatteredpixel.shatteredpixeldungeon.editor.scene;

import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Camera;
import com.watabou.utils.PointF;

public class EditorCellSelector extends CellSelector {

    public EditorCellSelector(DungeonTilemap map) {
        super(map);
    }

    @Override
    protected void handleClick(PointerEvent event) {
        PointF p = Camera.main.screenToCamera((int) event.current.x, (int) event.current.y);

//            //Prioritizes a sprite if it and a tile overlap, so long as that sprite isn't more than 4 pixels into another tile.
//            //The extra check prevents large sprites from blocking the player from clicking adjacent tiles
//
//            //hero first
//            if (Dungeon.hero.sprite != null && Dungeon.hero.sprite.overlapsPoint( p.x, p.y )){
//                PointF c = DungeonTilemap.tileCenterToWorld(Dungeon.hero.pos);
//                if (Math.abs(p.x - c.x) <= 12 && Math.abs(p.y - c.y) <= 12) {
//                    select(Dungeon.hero.pos, event.button);
//                    return;
//                }
//            }
//
//            //then mobs
//            for (Char mob : Dungeon.level.mobs.toArray(new Mob[0])){
//                if (mob.sprite != null && mob.sprite.overlapsPoint( p.x, p.y )){
//                    PointF c = DungeonTilemap.tileCenterToWorld(mob.pos);
//                    if (Math.abs(p.x - c.x) <= 12 && Math.abs(p.y - c.y) <= 12) {
//                        select(mob.pos, event.button);
//                        return;
//                    }
//                }
//            }
//
//            //then heaps
//            for (Heap heap : Dungeon.level.heaps.valueList()){
//                if (heap.sprite != null && heap.sprite.overlapsPoint( p.x, p.y)){
//                    PointF c = DungeonTilemap.tileCenterToWorld(heap.pos);
//                    if (Math.abs(p.x - c.x) <= 12 && Math.abs(p.y - c.y) <= 12) {
//                        select(heap.pos, event.button);
//                        return;
//                    }
//                }
//            }

        select(((DungeonTilemap) target).screenToTile(
                (int) event.current.x,
                (int) event.current.y,
                true), event.button);
    }


    @Override
    public void select(int cell, int button) {

        if (enabled && !EditorScene.interfaceBlockingHero() && listener != null && cell != -1 && !EditorScene.floor().isBorder(cell)) {

            switch (button) {
                default:
                    listener.onSelect(cell);
                    break;
                case PointerEvent.RIGHT:
                    listener.onRightClick(cell);
                    break;
            }
            EditorScene.ready();
        } else {
            EditorScene.cancel();
        }
    }

}