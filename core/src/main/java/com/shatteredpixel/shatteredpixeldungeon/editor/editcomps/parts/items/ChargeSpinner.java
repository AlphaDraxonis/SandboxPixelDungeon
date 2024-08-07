package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.items;

import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.StyledSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class ChargeSpinner extends StyledSpinner {


    public ChargeSpinner(Wand wand) {
        super(new LevelSpinner.LevelSpinnerModel(wand.curCharges, wand.maxCharges),
                Messages.get(ChargeSpinner.class, "label"), 9, EditorUtilies.createSubIcon(ItemSpriteSheet.Icons.SCROLL_RECHARGE));
        icon.scale.set(9f / icon.height());
        ((SpinnerIntegerModel) getModel()).setMinimum(0);
        ((SpinnerIntegerModel) getModel()).setAbsoluteMinimum(0);
        addChangeListener(() -> {
            wand.curCharges = (int) getValue();
            onChange();
        });
    }

    public ChargeSpinner(Artifact artifact) {
        super(new LevelSpinner.LevelSpinnerModel(artifact.charge(), artifact.chargeCap()),
                Messages.get(ChargeSpinner.class, "label"), 9, EditorUtilies.createSubIcon(ItemSpriteSheet.Icons.SCROLL_RECHARGE));
        icon.scale.set(9f / icon.height());
        ((SpinnerIntegerModel) getModel()).setMinimum(0);
        ((SpinnerIntegerModel) getModel()).setAbsoluteMinimum(0);
        addChangeListener(() -> {
            artifact.charge((int) getValue());
            onChange();
        });
    }

    public void updateValue(Item item) {
        if (item instanceof Wand) setValue(((Wand) item).curCharges);
        else if (item instanceof Artifact) setValue(((Artifact) item).charge());
        adjustMaximum(item);
    }

    protected void onChange() {
    }

    public void adjustMaximum(Item item) {
        SpinnerIntegerModel model = (SpinnerIntegerModel) getModel();
        int maxCharges;
        if (item instanceof Wand) {
            maxCharges = Math.max(1, ((Wand) item).maxCharges);
        } else if (!(item instanceof Artifact)) throw new IllegalArgumentException("Error in line 37 hehehehe!");
        else maxCharges = Math.max(1, ((Artifact) item).chargeCap());

        if (model.getValue() == model.getMaximum()) model.setValue(maxCharges);
        model.setMaximum(maxCharges);
    }
}