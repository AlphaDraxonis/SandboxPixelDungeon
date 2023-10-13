package com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.parts;

import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.actors.blobs.Blob;
import com.alphadraxonis.sandboxpixeldungeon.actors.blobs.SacrificialFire;
import com.alphadraxonis.sandboxpixeldungeon.actors.blobs.WaterOfAwareness;
import com.alphadraxonis.sandboxpixeldungeon.actors.blobs.WaterOfHealth;
import com.alphadraxonis.sandboxpixeldungeon.actors.blobs.WaterOfTransmutation;
import com.alphadraxonis.sandboxpixeldungeon.actors.blobs.WellWater;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.ActionPartModify;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.special.MagicalFireRoom;

public class BlobEditPart {


    public static final class Modify implements ActionPartModify {

        private final int cell;
        private final BlobData before;
        private BlobData after;

        public Modify(int cell) {
            this.cell = cell;
            before = new BlobData(cell);
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
            return !BlobData.areEqual(before, after);
        }

        @Override
        public void finish() {
            after = new BlobData(cell);
        }
    }

    public static class BlobData {

        public static final Class<? extends Blob>[] BLOB_CLASSES = new Class[]{
                MagicalFireRoom.EternalFire.class,
                SacrificialFire.class,
                WaterOfHealth.class,
                WaterOfAwareness.class,
                WaterOfTransmutation.class};

        private final int[] blobs = new int[5];

        public BlobData(int cell) {
            for (int i = 0; i < BLOB_CLASSES.length; i++) {
                Blob b = Dungeon.level.blobs.get(BLOB_CLASSES[i]);
                if (b != null && b.cur != null) blobs[i] = b.cur[cell];
            }
        }

        public static boolean areEqual(BlobData a, BlobData b) {
            if (a == null && b == null) return true;
            if (a == null || b == null) return false;
            for (int i = 0; i < a.blobs.length; i++) {
                if (a.blobs[i] != b.blobs[i]) return false;
            }
            return true;
        }

        protected void place(int cell) {
            clearAllAtCell(cell);
            for (int i = 0; i < BLOB_CLASSES.length; i++) {
                EditorScene.add(Blob.seed(cell, blobs[i], BLOB_CLASSES[i]));
            }
        }

    }

    public static void place(int cell, Class<? extends Blob> blob, int amount) {
        clearNormalAtCell(cell);
        EditorScene.add(Blob.seed(cell, amount, blob));
    }

    public static void clearAllAtCell(int cell) {
        for (int i = 0; i < BlobData.BLOB_CLASSES.length; i++) {
            Blob b = Dungeon.level.blobs.get(BlobData.BLOB_CLASSES[i]);
            if (b != null && b.cur != null) {
                b.volume -= b.cur[cell];
                b.cur[cell] = 0;
            }
        }
    }

    public static void clearNormalAtCell(int cell) {
        for (int i = 0; i < BlobData.BLOB_CLASSES.length; i++) {
            Blob b = Dungeon.level.blobs.get(BlobData.BLOB_CLASSES[i]);
            if (b != null && !(b instanceof WellWater) && b.cur != null) {
                b.volume -= b.cur[cell];
                b.cur[cell] = 0;
            }
        }
    }

    public static void clearWellWaterAtCell(int cell) {
        for (int i = 0; i < BlobData.BLOB_CLASSES.length; i++) {
            Blob b = Dungeon.level.blobs.get(BlobData.BLOB_CLASSES[i]);
            if (b != null && b instanceof WellWater && b.cur != null) {
                b.volume -= b.cur[cell];
                b.cur[cell] = 0;
            }
        }
    }

}