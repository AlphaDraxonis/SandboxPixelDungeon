package com.shatteredpixel.shatteredpixeldungeon.editor.scene;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.EToolbar;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.input.GameAction;
import com.watabou.input.PointerEvent;
import com.watabou.input.ScrollEvent;
import com.watabou.noosa.Camera;
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