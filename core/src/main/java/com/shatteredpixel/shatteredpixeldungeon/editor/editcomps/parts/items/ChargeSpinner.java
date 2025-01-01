package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.items;

import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.StyledSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class ChargeSpinner extends StyledSpinner {


    public ChargeSpinner(Wand wand) {
        super(new LevelSpinner.LevelSpinnerModel(wand.curCharges, wand.maxCharges),
                Messages.get(ChargeSpinner.class, "label"), 9, EditorUtilities.createSubIcon(ItemSpriteSheet.Icons.SCROLL_RECHARGE));
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
                Messages.get(ChargeSpinner.class, "label"), 9, EditorUtilities.createSubIcon(ItemSpriteSheet.Icons.SCROLL_RECHARGE));
        icon.scale.set(9f / icon.height());
        ((SpinnerIntegerModel) getModel()).setMinimum(0);
        ((SpinnerIntegerModel) getModel()).setAbsoluteMinimum(0);
        addChangeListener(() -> {
            artifact.charge((int) getValue());
            onChange();
        });
    }

    public ChargeSpinner(ClassArmor classArmor) {
        super(new SpinnerIntegerModel(0, 100, (int) classArmor.charge),
                Messages.get(ChargeSpinner.class, "label"), 9, EditorUtilities.createSubIcon(ItemSpriteSheet.Icons.SCROLL_RECHARGE));
        icon.scale.set(9f / icon.height());
        ((SpinnerIntegerModel) getModel()).setMinimum(0);
        ((SpinnerIntegerModel) getModel()).setAbsoluteMinimum(0);
        addChangeListener(() -> {
            classArmor.charge = (int) getValue();
            onChange();
        });
    }

    public void updateValue(Item item) {
        if (item instanceof Wand) setValue(((Wand) item).curCharges);
        else if (item instanceof Artifact) setValue(((Artifact) item).charge());
        else if (item instanceof ClassArmor) setValue(((ClassArmor) item).charge);
        adjustMaximum(item);
    }

    protected void onChange() {
    }

    public void adjustMaximum(Item item) {
        SpinnerIntegerModel model = (SpinnerIntegerModel) getModel();
        int maxCharges;
        if (item instanceof Wand) maxCharges = Math.max(1, ((Wand) item).maxCharges);
        else if (item instanceof Artifact) maxCharges = Math.max(1, ((Artifact) item).chargeCap());
        else if (item instanceof ClassArmor) maxCharges = 100;
        else throw new IllegalArgumentException("Error in line 69 hehehehe!");

        if (model.getValue() == model.getMaximum()) model.setValue(maxCharges);
        model.setMaximum(maxCharges);
    }
}