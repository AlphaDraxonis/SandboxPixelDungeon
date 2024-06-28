package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.items;

import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.StyledSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class LevelSpinner extends StyledSpinner {


    public LevelSpinner(Item item) {
        super(new LevelSpinnerModel(item.level(), item instanceof Artifact ? ((Artifact) item).levelCap() : item instanceof Trinket ? 3 : 100),
                Messages.get(LevelSpinner.class, "label"), 9, EditorUtilies.createSubIcon(ItemSpriteSheet.Icons.SCROLL_UPGRADE));
        icon.scale.set(9f / icon.height());
        SpinnerIntegerModel model = (SpinnerIntegerModel) getModel();
        if (item instanceof Artifact) {
            model.setAbsoluteMinAndMax((float) model.getMinimum(), (float) model.getMaximum());
        } else if (item instanceof Trinket) {
            model.setMinimum(0);
            model.setAbsoluteMinimum(0f);
        } else {
            model.setAbsoluteMinimum(-100f);
        }
        addChangeListener(() -> {
            item.level((int) getValue());
            onChange();
        });
    }

    protected void onChange() {
    }

    public static class LevelSpinnerModel extends SpinnerIntegerModel {

        public LevelSpinnerModel(int level) {
            this(level, 100);
        }

        public LevelSpinnerModel(int level, int max) {
            super(-10, max, level);
        }

        @Override
        public float getInputFieldWidth(float height) {
            return Spinner.FILL;
        }
    }

    public static boolean availableForItem(Item item) {
        return item.isUpgradable() || item instanceof Artifact || item instanceof Trinket;
    }
}