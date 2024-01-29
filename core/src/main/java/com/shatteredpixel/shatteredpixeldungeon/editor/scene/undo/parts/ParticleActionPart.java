package com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.CustomParticle;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPartModify;

import java.util.HashSet;
import java.util.Set;

public class ParticleActionPart {

    public static final class Modify implements ActionPartModify {

        private final int cell;
        private final ParticleData before;
        private ParticleData after;

        public Modify(int cell) {
            this.cell = cell;
            before = new ParticleData(cell);
        }

        @Override
        public void undo() {
            before.place(cell);
        }

        @Override
        public void redo() {
            after.place(cell);
        }

        @Override
        public boolean hasContent() {
            return !ParticleData.areEqual(before, after);
        }

        @Override
        public void finish() {
            after = new ParticleData(cell);
        }
    }

    public static class ParticleData {

        private final Set<Integer> particleIDs;

        public ParticleData(int cell) {
            particleIDs = new HashSet<>(4);
            for (CustomParticle particle : Dungeon.level.particles.values()) {
                if (particle != null && particle.cur != null && particle.cur[cell] > 0) {
                    particleIDs.add(particle.particleID);
                }
            }
        }

        public static boolean areEqual(ParticleData a, ParticleData b) {
            if (a == null && b == null) return true;
            if (a == null || b == null) return false;
            return a.particleIDs.equals(b.particleIDs);
        }

        protected void place(int cell) {
            clearCell(cell);
            for (int id : particleIDs) {
                ParticleActionPart.place(cell, id);
            }
        }

    }

    public static void place(int cell, int id) {
        EditorScene.add(Blob.addParticle(cell, id));
    }

    public static void clearCell(int cell) {
        for (CustomParticle particle : Dungeon.level.particles.values()) {
            if (particle != null && particle.cur != null && particle.cur[cell] > 0) {
                particle.volume -= particle.cur[cell];
                particle.cur[cell] = 0;
            }
        }
    }

}