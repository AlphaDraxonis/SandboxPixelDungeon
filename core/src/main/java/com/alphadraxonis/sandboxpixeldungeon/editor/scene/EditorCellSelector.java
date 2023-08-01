package com.alphadraxonis.sandboxpixeldungeon.editor.scene;

import com.alphadraxonis.sandboxpixeldungeon.SPDAction;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.Undo;
import com.alphadraxonis.sandboxpixeldungeon.scenes.CellSelector;
import com.alphadraxonis.sandboxpixeldungeon.tiles.DungeonTilemap;
import com.watabou.input.GameAction;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Camera;

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
                true), event.button, false);
    }


    @Override
    public void select(int cell, int button, boolean dragClick) {

        if (enabled && !EditorScene.interfaceBlockingHero() && listener != null && cell != -1 && !EditorScene.customLevel().isBorder(cell)) {

            switch (button) {
                default:
                    if (dragClick) listener.onSelectDragging(cell);
                    else listener.onSelect(cell);
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

    private static final int UNITS_MOVE = 9;
    @Override
    protected boolean moveFromActions(GameAction... actions) {

        if (EditorScene.cancelCellSelector()) {
            return false;
        }

        int x = 0;
        int y = 0;
        for (GameAction action : actions) {
            if (action == SPDAction.E || action == SPDAction.NE || action == SPDAction.SE) x += UNITS_MOVE;
            if (action == SPDAction.W || action == SPDAction.NW || action == SPDAction.SW) x -= UNITS_MOVE;
            if (action == SPDAction.S || action == SPDAction.SE || action == SPDAction.SW) y += UNITS_MOVE;
            if (action == SPDAction.N || action == SPDAction.NE || action == SPDAction.NW) y -= UNITS_MOVE;
        }
        if (x == 0 && y == 0) return false;

        Camera.main.scroll.x += x;
        Camera.main.scroll.y += y;

        return true;
    }

    @Override
    protected int directionFromAction(GameAction action) {
        if (action == SPDAction.N) return -10;
        if (action == SPDAction.NE) return -9;
        if (action == SPDAction.E) return +1;
        if (action == SPDAction.SE) return +11;
        if (action == SPDAction.S) return +10;
        if (action == SPDAction.SW) return +9;
        if (action == SPDAction.W) return -1;
        if (action == SPDAction.NW) return -11;
        else return 0;
    }

    @Override
    protected void onPointerDown(PointerEvent event) {
        if (!EditorScene.interfaceBlockingHero()) Undo.startAction();
        super.onPointerDown(event);
    }

    @Override
    protected void onPointerUp(PointerEvent event) {
        super.onPointerUp(event);
        if (!EditorScene.interfaceBlockingHero()) Undo.endAction();
    }

    @Override
    protected void onClick(PointerEvent event) {
        //onPointerUp() is called before so we just start a new action bc empty actions are filtered anyway
        Undo.startAction();
        super.onClick(event);
        Undo.endAction();
    }
}