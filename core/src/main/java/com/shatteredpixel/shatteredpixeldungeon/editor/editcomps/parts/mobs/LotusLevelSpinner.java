package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs;

import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.items.LevelSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.StyledSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class LotusLevelSpinner extends StyledSpinner {

    public LotusLevelSpinner(WandOfRegrowth.Lotus lotus) {
        super(new LotusLevelSpinnerModel(lotus), Messages.get(LevelSpinner.class,"label"), 9,
              EditorUtilities.createSubIcon(ItemSpriteSheet.Icons.SCROLL_UPGRADE));

        ((LotusLevelSpinnerModel) getModel()).onAfterClick = () -> updateDesc(true);

        icon.scale.set(9f / icon.height());
        addChangeListener(() -> {
            lotus.setLevel((int) getValue());
            updateDesc(false);
        });
    }

    protected void updateDesc(boolean forceUpdate) {
    }

    private static final class LotusLevelSpinnerModel extends SpinnerIntegerModel {
        private Runnable onAfterClick;

        public LotusLevelSpinnerModel(WandOfRegrowth.Lotus lotus) {
            super(0, 200, lotus.getLvl());
        }

        @Override
        public void afterClick() {
            onAfterClick.run();
        }
    }

}