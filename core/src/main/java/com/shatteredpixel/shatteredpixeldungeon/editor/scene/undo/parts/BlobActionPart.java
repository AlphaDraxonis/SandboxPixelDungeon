package com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.*;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditItemComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.PermaGas;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPartModify;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.MagicalFireRoom;

public class BlobActionPart {


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
                PermaGas.PFire.class,
                PermaGas.PFreezing.class,
                MagicalFireRoom.EternalFire.class,
                SacrificialFire.class,
                PermaGas.PToxicGas.class,
                PermaGas.PCorrosiveGas.class,
                PermaGas.PConfusionGas.class,
                PermaGas.PParalyticGas.class,
                PermaGas.PStenchGas.class,
                PermaGas.PSmokeScreen.class,
                PermaGas.PElectricity.class,
                Foliage.class,
                PermaGas.PStormCloud.class,
                WaterOfHealth.class,
                WaterOfAwareness.class,
                WaterOfTransmutation.class};

        private final int[] blobs = new int[BLOB_CLASSES.length];

        private Item sacrificialFirePrize;

        public BlobData(int cell) {
            for (int i = 0; i < BLOB_CLASSES.length; i++) {
                Blob b = Dungeon.level.blobs.getOnly(BLOB_CLASSES[i]);
                if (b != null && b.cur != null) {
                    blobs[i] = b.cur[cell];
                    if (b instanceof SacrificialFire) {
                        sacrificialFirePrize = ((SacrificialFire) b).getPrize(cell);
                        if (sacrificialFirePrize != null) sacrificialFirePrize = sacrificialFirePrize.getCopy();
                    }
                }
            }
        }

        public static boolean areEqual(BlobData a, BlobData b) {
            if (a == b) return true;
            if (a == null || b == null) return false;
            for (int i = 0; i < a.blobs.length; i++) {
                if (a.blobs[i] != b.blobs[i]) return false;
            }
            return EditItemComp.areEqual(a.sacrificialFirePrize, b.sacrificialFirePrize);
        }

        protected void place(int cell) {
            clearAllAtCell(cell);
            for (int i = 0; i < BLOB_CLASSES.length; i++) {
                EditorScene.add(Blob.seed(cell, blobs[i], BLOB_CLASSES[i]));
            }
            SacrificialFire sacrificialFire = Dungeon.level.blobs.getOnly(SacrificialFire.class);
            if (sacrificialFire != null) sacrificialFire.setPrize(cell, sacrificialFirePrize);
        }

    }

    public static void place(int cell, Class<? extends Blob> blob, int amount) {
        EditorScene.add(Blob.seed(cell, amount, blob));
        if (blob == SacrificialFire.class) {
            SacrificialFire sacrificialFire = Dungeon.level.blobs.getOnly(SacrificialFire.class);
            if (sacrificialFire != null && sacrificialFire.getPrize(cell) == null)
                sacrificialFire.setPrize(cell, SacrificialFire.prizeInInventory);
        }
    }

    public static void clearAllAtCell(int cell) {
        for (int i = 0; i < BlobData.BLOB_CLASSES.length; i++) {
            Blob b = Dungeon.level.blobs.getOnly(BlobData.BLOB_CLASSES[i]);
            if (b != null && b.cur != null) {
                b.volume -= b.cur[cell];
                b.cur[cell] = 0;
            }
        }
    }

    public static void clearNormalAtCell(int cell) {
        for (int i = 0; i < BlobData.BLOB_CLASSES.length; i++) {
            Blob b = Dungeon.level.blobs.getOnly(BlobData.BLOB_CLASSES[i]);
            if (b != null && !(b instanceof WellWater) && b.cur != null) {
                b.volume -= b.cur[cell];
                b.cur[cell] = 0;
            }
        }
    }

    public static void clearWellWaterAtCell(int cell) {
        for (int i = 0; i < BlobData.BLOB_CLASSES.length; i++) {
            Blob b = Dungeon.level.blobs.getOnly(BlobData.BLOB_CLASSES[i]);
            if (b != null && b instanceof WellWater && b.cur != null) {
                b.volume -= b.cur[cell];
                b.cur[cell] = 0;
            }
        }
    }

}