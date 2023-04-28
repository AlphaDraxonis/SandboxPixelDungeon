package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.items;

import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerTextModel;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

public class AugumentationSpinner extends Spinner {


    public AugumentationSpinner(Item item) {
        super(item instanceof Weapon ?
                        new WeaponAugSpinnerModel((Weapon) item) :
                        new ArmorAugSpinnerModel((Armor) item),
                " Aug.:", 10);

        addChangeListener(() -> {
            ((Applyable) getModel()).apply();
            onChange();
        });
    }

    protected void onChange() {
    }


    private interface Applyable {
        void apply();
    }

    private static class WeaponAugSpinnerModel extends SpinnerTextModel implements Applyable {

        private Weapon item;

        public WeaponAugSpinnerModel(Weapon item) {
            super(Weapon.Augment.NONE, Weapon.Augment.SPEED, Weapon.Augment.DAMAGE);
            this.item = item;
            setValue(item.augment);

        }

        public void apply() {
            item.augment = (Weapon.Augment) getValue();
        }

        @Override
        protected String getAsString(Object value) {
            switch ((Weapon.Augment) value) {
                case SPEED:
                    return "SPEED";
//                    return Messages.get(Weapon.class, "faster");
                case DAMAGE:
                    return "DMG";
//                    return Messages.get(Weapon.class, "stronger");
                case NONE:
                    return "NONE";
            }
            return Messages.NO_TEXT_FOUND;
        }
    }

    private static class ArmorAugSpinnerModel extends SpinnerTextModel implements Applyable {

        private Armor item;

        public ArmorAugSpinnerModel(Armor item) {
            super(Armor.Augment.NONE, Armor.Augment.EVASION, Armor.Augment.DEFENSE);
            this.item = item;
            setValue(item.augment);
        }

        public void apply() {
            item.augment = (Armor.Augment) getValue();
        }

        @Override
        protected String getAsString(Object value) {
            switch ((Armor.Augment) value) {
                case EVASION:
                    return "EVASION";
//                    return Messages.get(Armor.class, "evasion");
                case DEFENSE:
                    return "DEFENSE";
//                    return Messages.get(Armor.class, "defense");
                case NONE:
                    return "NONE";
            }
            return Messages.NO_TEXT_FOUND;
        }
    }
}
