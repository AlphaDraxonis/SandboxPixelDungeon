package com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.Zone;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPart;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;

import java.util.Objects;

public /*sealed*/ abstract class ZoneActionPart implements ActionPart {

    protected final String zoneBefore, zoneAfter;
    protected final int cell;

    private ZoneActionPart(String zone, int cell) {
        Zone zoneBF = EditorScene.customLevel().zone[cell];
        this.zoneBefore = zoneBF == null ? null : zoneBF.getName();
        this.zoneAfter = zone;
        this.cell = cell;

        redo();
    }

    private static void place(String zone, int cell) {
        if(zone == null) {
            remove(cell);
            EditorScene.updateZoneCell(cell);
            return;
        }
        Level level = Dungeon.level;
        Zone z = level.zoneMap.get(zone);
        if (z == null) {
            z = new Zone();
            z.name = zone;
            EditorScene.add(z);
        }
        remove(cell);
        level.zone[cell] = z;
        z.addCell(cell, level);
        EditorScene.updateZoneCell(cell);
    }

    private static void remove(int cell) {
        if (Dungeon.level.zone[cell] != null) {
            Zone.removeCell(cell, Dungeon.level);
        }
    }

    @Override
    public boolean hasContent() {
        return !Objects.equals(zoneBefore, zoneAfter);
    }

    public static final class Place extends ZoneActionPart {

        public Place(String zone, int cell) {
            super(zone, cell);
        }

        @Override
        public void undo() {
            ZoneActionPart.place(zoneBefore, cell);
        }

        @Override
        public void redo() {
            ZoneActionPart.place(zoneAfter, cell);
        }
    }

//    public static final class Modify implements ActionPartModify {
//
//        private final Mob before;
//        private Mob after;
//
//        public Modify(Mob mob) {
//            before = (Mob) mob.getCopy();
//            after = mob;
//        }
//
//        @Override
//        public void undo() {
//
//            Mob mobAtCell = EditorScene.customLevel().findMob(after.pos);
//
//            remove(mobAtCell);
//
//            place((Mob) before.getCopy());
//        }
//
//        @Override
//        public void redo() {
//            Mob mobAtCell = EditorScene.customLevel().findMob(after.pos);
//
//            if (mobAtCell != null) remove(mobAtCell);
//
//            place((Mob) after.getCopy());
//
//        }
//
//        @Override
//        public boolean hasContent() {
//            return !EditMobComp.areEqual(before, after);
//        }
//
//        @Override
//        public void finish() {
//            after = (Mob) after.getCopy();
//        }
//    }
}