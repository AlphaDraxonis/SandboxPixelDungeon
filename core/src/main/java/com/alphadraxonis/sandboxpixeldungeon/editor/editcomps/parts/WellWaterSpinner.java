package com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.parts;

import com.alphadraxonis.sandboxpixeldungeon.Assets;
import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.actors.blobs.Blob;
import com.alphadraxonis.sandboxpixeldungeon.actors.blobs.WaterOfAwareness;
import com.alphadraxonis.sandboxpixeldungeon.actors.blobs.WaterOfHealth;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.parts.BlobEditPart;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.Spinner;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.SpinnerTextIconModel;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Image;
import com.watabou.utils.RectF;

public class WellWaterSpinner extends Spinner {

    private final int cell;

    public WellWaterSpinner(int cell) {
        super(new WellWaterSpinnerModel(cell), " " + Messages.get(WellWaterSpinner.class, "label"), 9);
        this.cell = cell;
        addChangeListener(this::apply);
    }

    private void apply() {
        BlobEditPart.clearAtCell(cell);
        switch ((WellWaters) getValue()) {
            case HEALTH:
                EditorScene.add(Blob.seed(cell, 1, WaterOfHealth.class));
                break;
            case AWARENESS:
                EditorScene.add(Blob.seed(cell, 1, WaterOfAwareness.class));
                break;
        }
    }

    public enum WellWaters {
        NONE,
        HEALTH,
        AWARENESS;

        public int getIndex() {
            switch (this) {

                case NONE:
                    return 0;
                case HEALTH:
                    return 1;
                case AWARENESS:
                    return 2;
            }
            return -1;
        }

    }

    public static WellWaters convertToValue(int cell) {
        if (cell < 0) return WellWaters.NONE;

        Blob b = Dungeon.level.blobs.get(WaterOfHealth.class);
        if (b != null && b.cur != null && b.cur[cell] > 0) return WellWaters.HEALTH;
        b = Dungeon.level.blobs.get(WaterOfAwareness.class);
        if (b != null && b.cur != null && b.cur[cell] > 0) return WellWaters.AWARENESS;
        return WellWaters.NONE;
    }

    private static class WellWaterSpinnerModel extends SpinnerTextIconModel {

        public WellWaterSpinnerModel(int cell) {
            super(true, convertToValue(cell).getIndex(), (Object[]) WellWaters.values());
        }

        @Override
        protected Image getIcon(Object value) {
            switch ((WellWaters) value) {
                default:
                case NONE:
                    return new Image();
                case HEALTH:
                    RectF r = ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.POTION_HEALING);
                    if (r == null) return new Image();
                    Image icon = new Image(Assets.Sprites.ITEM_ICONS);
                    icon.frame(r);
                    return icon;
                case AWARENESS:
                    r = ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.SCROLL_IDENTIFY);
                    if (r == null) return new Image();
                    icon = new Image(Assets.Sprites.ITEM_ICONS);
                    icon.frame(r);
                    return icon;
            }
        }

        @Override
        protected String getAsString(Object value) {
            switch ((WellWaters) value) {
                default:
                case NONE:
                    return " " + Messages.get(WellWaterSpinner.class, "none");
                case HEALTH:
                    return " " + Messages.get(WellWaterSpinner.class, "health");
                case AWARENESS:
                    return " " + Messages.get(WellWaterSpinner.class, "awareness");
            }
        }
    }
}