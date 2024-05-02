package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.items;

import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.StyledSpinner;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

public class DurabilitySpinner extends StyledSpinner {

	public DurabilitySpinner(MissileWeapon weapon) {
		super(new DurabilitySpinnerModel((int) weapon.baseUses), Messages.get(DurabilitySpinner.class, "label"), 9);
		addChangeListener(() -> {
			Integer val = (Integer) getValue();
			if (val == null) weapon.baseUses = MissileWeapon.MAX_DURABILITY;
			else weapon.baseUses = val;
			onChange();
		});
	}

	public void updateValue(Item item) {
		adjustMaximum(item);
	}

	protected void onChange() {
	}

	public void adjustMaximum(Item item) {
	}

	public static class DurabilitySpinnerModel extends SpinnerIntegerModel {

		public DurabilitySpinnerModel(int durability) {
			super(1, (int) MissileWeapon.MAX_DURABILITY, durability);
			setAbsoluteMaximum(MissileWeapon.MAX_DURABILITY);
		}

		@Override
		public String getDisplayString() {
			if ((int) (getValue()) == MissileWeapon.MAX_DURABILITY) return INFINITY;
			return super.getDisplayString();
		}

		@Override
		public float getInputFieldWidth(float height) {
			return Spinner.FILL;
		}
	}
}