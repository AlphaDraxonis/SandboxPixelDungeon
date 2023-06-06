package com.alphadraxonis.sandboxpixeldungeon.editor.scene;

import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.scenes.CellSelector;
import com.alphadraxonis.sandboxpixeldungeon.tiles.DungeonTilemap;
import com.watabou.input.PointerEvent;

import java.util.HashSet;
import java.util.Set;

public class EditorCellSelector extends CellSelector {

    public EditorCellSelector(DungeonTilemap map) {
        super(map);
    }

    @Override
    protected void handleClick(PointerEvent event) {
//        PointF p = Camera.main.screenToCamera((int) event.current.x, (int) event.current.y);
//
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

    private final Set<Integer> lastSelectedCells = new HashSet<>();

    @Override
    protected void onPointerUp(PointerEvent event) {
        lastSelectedCells.clear();
        super.onPointerUp(event);
    }

    @Override
    protected void handleDragClick(PointerEvent event) {
        int cell = ((DungeonTilemap) target).screenToTile(
                (int) event.current.x,
                (int) event.current.y,
                true);
        if (lastSelectedCells.contains(cell) ||EditorScene.customLevel().isBorder(cell)) return; //dont trigger every frame, only when a new cell was entered
        lastSelectedCells.add(cell);
        select(cell, event.button);
    }

    @Override
    public void select(int cell, int button) {

        if (enabled && !EditorScene.interfaceBlockingHero() && listener != null && cell != -1 && !EditorScene.customLevel().isBorder(cell)) {

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

    @Override
    protected boolean dragClickEnabled() {
        return true;
    }

    @Override
    protected void updateGameControlls() {
    }
}