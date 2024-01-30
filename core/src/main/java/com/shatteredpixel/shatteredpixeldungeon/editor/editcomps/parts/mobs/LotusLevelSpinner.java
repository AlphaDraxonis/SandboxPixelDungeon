package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs;

import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.items.LevelSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.StyledSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class LotusLevelSpinner extends StyledSpinner {


    public LotusLevelSpinner(WandOfRegrowth.Lotus lotus) {
        super(new LevelSpinnerModel(lotus.getLvl()), Messages.get(LevelSpinner.class,"label"), 9,
                EditorUtilies.createSubIcon(ItemSpriteSheet.Icons.SCROLL_UPGRADE));
        icon.scale.set(9f / icon.height());
        addChangeListener(() -> {
            lotus.setLevel((int) getValue());
            updateDesc(false);
        });
    }

    @Override
    protected void afterClick() {
        updateDesc(true);
    }

    protected void updateDesc(boolean foreceUpdate) {
    }

    public static class LevelSpinnerModel extends SpinnerIntegerModel {

        public LevelSpinnerModel(int level) {
            super(0, 200, level, 1, false, null);

        }

        @Override
        public int getClicksPerSecondWhileHolding() {
            return 70;
        }
    }
}