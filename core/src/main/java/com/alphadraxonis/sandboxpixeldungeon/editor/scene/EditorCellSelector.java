package com.alphadraxonis.sandboxpixeldungeon.editor.scene;

import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.SPDAction;
import com.alphadraxonis.sandboxpixeldungeon.actors.Char;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Mob;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.EToolbar;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.Undo;
import com.alphadraxonis.sandboxpixeldungeon.items.Heap;
import com.alphadraxonis.sandboxpixeldungeon.scenes.CellSelector;
import com.alphadraxonis.sandboxpixeldungeon.tiles.DungeonTilemap;
import com.watabou.input.GameAction;
import com.watabou.input.PointerEvent;
import com.watabou.input.ScrollEvent;
import com.watabou.noosa.Camera;
import com.watabou.utils.Point;
import com.watabou.utils.PointF;

public class EditorCellSelector extends CellSelector {

    public EditorCellSelector(DungeonTilemap map) {
        super(map);
    }

    @Override
    protected void handleClick(PointerEvent event) {

        if (event.button == PointerEvent.RIGHT || listener == EToolbar.informerEditCell) {

            PointF p = Camera.main.screenToCamera((int) event.current.x, (int) event.current.y);
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
            //then mobs
            for (Char mob : Dungeon.level.mobs.toArray(new Mob[0])){
                if (mob.sprite != null && mob.sprite.overlapsPoint( p.x, p.y )){
                    PointF c = DungeonTilemap.tileCenterToWorld(mob.pos);
                    if (Math.abs(p.x - c.x) <= 12 && Math.abs(p.y - c.y) <= 12) {
                        select(mob.pos, event.button, false);
                        return;
                    }
                }
            }

            //then heaps
            for (Heap heap : Dungeon.level.heaps.valueList()) {
                if (heap.sprite != null && heap.sprite.overlapsPoint(p.x, p.y)) {
                    PointF c = DungeonTilemap.tileCenterToWorld(heap.pos);
                    if (Math.abs(p.x - c.x) <= 12 && Math.abs(p.y - c.y) <= 12) {
                        select(heap.pos, event.button, false);
                        return;
                    }
                }
            }
        }

        select(((DungeonTilemap) target).screenToTile(
                (int) event.current.x,
                (int) event.current.y,
                true), event.button, false);
    }


    @Override
    public void select(int cell, int button, boolean dragClick) {

        if (enabled && !EditorScene.interfaceBlockingHero() && listener != null && cell != -1) {

            switch (button) {
                default:
                    if (dragClick) listener.onSelectDragging(cell);
                    else listener.onSelect(cell);
                    break;
                case PointerEvent.RIGHT:
                    listener.onRightClick(cell);
                    break;
                case PointerEvent.MIDDLE:
                    listener.onMiddleClick(cell);
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
    protected Point directionFromAction(GameAction action) {
        if (action == SPDAction.N) return new Point(0, -1);
        if (action == SPDAction.NE) return new Point(1, -1);
        if (action == SPDAction.E) return new Point(1,0);
        if (action == SPDAction.SE) return new Point(1,1);
        if (action == SPDAction.S) return new Point(0, 1);
        if (action == SPDAction.SW) return new Point(-1, 1);
        if (action == SPDAction.W) return new Point(-1, 0);
        if (action == SPDAction.NW) return new Point(-1, -1);
        else return new Point(0,0);
    }

    @Override
    protected void onScroll(ScrollEvent event) {
        if (!controlHolding) super.onScroll(event);
        else EToolbar.scroll(event.amount);
    }

    @Override
    protected boolean shiftKeyAction() {
        if (shiftHolding) EToolbar.selectRemoverTemporarily();
        else EToolbar.unselectTemporarilyRemover();
        return true;
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