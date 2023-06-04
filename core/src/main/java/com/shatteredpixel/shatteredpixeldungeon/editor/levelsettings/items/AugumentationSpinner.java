package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.items;

import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerTextModel;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

import java.util.Locale;

public class AugumentationSpinner extends Spinner {


    public AugumentationSpinner(Item item) {
        super(item instanceof Weapon ?
                        new WeaponAugSpinnerModel((Weapon) item) :
                        new ArmorAugSpinnerModel((Armor) item),
                " "+Messages.get(AugumentationSpinner.class,"label")+":", 10);

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
            return Messages.get(AugumentationSpinner.class,((Weapon.Augment) value).name().toLowerCase(Locale.ENGLISH));
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
            return Messages.get(AugumentationSpinner.class,((Armor.Augment) value).name().toLowerCase(Locale.ENGLISH));
        }
    }
}