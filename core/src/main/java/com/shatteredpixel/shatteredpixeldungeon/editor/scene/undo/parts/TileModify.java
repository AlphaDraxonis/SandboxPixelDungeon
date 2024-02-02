package com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.CoinDoor;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.transitions.TransitionEditPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPartModify;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;

public final class TileModify implements ActionPartModify {

    private final LevelTransition transitionBefore;
    private LevelTransition transitionAfter;

    private final BlobActionPart.BlobData blobsBefore;//This is basically only BlobEditPart-Modify...
    private BlobActionPart.BlobData blobsAfter;

    private int coinDoorPriceBefore, coinDoorPriceAfter;

    private final int cell;

    public TileModify(int cell) {
        this(Dungeon.level.transitions.get(cell), cell);
    }

    public TileModify(LevelTransition transition, int cell) {
        if (transition != null) {
            transitionBefore = (LevelTransition) transition.getCopy();
        } else transitionBefore = null;
        blobsBefore = new BlobActionPart.BlobData(cell);
        coinDoorPriceBefore = Dungeon.level.getCoinDoorCost(cell);
        this.cell = cell;
    }

    @Override
    public void undo() {

        LevelTransition oldTrans = Dungeon.level.transitions.get(cell);
        if (oldTrans != null) {//remove
            Dungeon.level.transitions.remove(cell);
            EditorScene.remove(oldTrans);
        }

        if (transitionBefore != null) {//place
            Dungeon.level.transitions.put(cell, transitionBefore);
            EditorScene.add(transitionBefore);
        }

        if (coinDoorPriceBefore != CoinDoor.DEFAULT_COST) {
            Dungeon.level.setCoinDoorCost(cell, coinDoorPriceBefore);
        }

        blobsBefore.place(cell);
    }

    @Override
    public void redo() {

        LevelTransition oldTrans = Dungeon.level.transitions.get(cell);
        if (oldTrans != null) {//remove
            Dungeon.level.transitions.remove(cell);
            EditorScene.remove(oldTrans);
        }

        if (transitionAfter != null) {//place
            Dungeon.level.transitions.put(cell, transitionAfter);
            EditorScene.add(transitionAfter);
        }

        if (coinDoorPriceAfter != CoinDoor.DEFAULT_COST) {
            Dungeon.level.setCoinDoorCost(cell, coinDoorPriceAfter);
        }

        blobsAfter.place(cell);
    }

    @Override
    public boolean hasContent() {
        return coinDoorPriceBefore != coinDoorPriceAfter
                || !TransitionEditPart.areEqual(transitionBefore, transitionAfter)
                || !BlobActionPart.BlobData.areEqual(blobsBefore, blobsAfter);
    }

    @Override
    public void finish() {
        if (cell < Dungeon.level.length()) {
            LevelTransition newTrans = Dungeon.level.transitions.get(cell);
            if (newTrans != null) transitionAfter = newTrans.getCopy();
            blobsAfter = new BlobActionPart.BlobData(cell);
            coinDoorPriceAfter = Dungeon.level.getCoinDoorCost(cell);
        }
    }
}