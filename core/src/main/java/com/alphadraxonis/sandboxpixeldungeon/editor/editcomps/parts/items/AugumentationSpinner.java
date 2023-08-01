package com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.parts.items;

import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.Spinner;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.SpinnerTextModel;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.items.armor.Armor;
import com.alphadraxonis.sandboxpixeldungeon.items.stones.StoneOfAugmentation;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.Weapon;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;

import java.util.Locale;

public class AugumentationSpinner extends Spinner {


    public AugumentationSpinner(Item item) {
        super(item instanceof Weapon ?
                        new WeaponAugSpinnerModel((Weapon) item) :
                        new ArmorAugSpinnerModel((Armor) item),
                " " + Messages.get(AugumentationSpinner.class, "label") + ":", 10);

        addChangeListener(() -> {
            ((Runnable) getModel()).run();
            onChange();
        });
    }

    protected void onChange() {
    }


    private static class WeaponAugSpinnerModel extends SpinnerTextModel implements Runnable {

        private Weapon item;

        public WeaponAugSpinnerModel(Weapon item) {
            super(Weapon.Augment.NONE, Weapon.Augment.SPEED, Weapon.Augment.DAMAGE);
            this.item = item;
            setValue(item.augment);

        }

        @Override
        public void run() {
            item.augment = (Weapon.Augment) getValue();
        }

        @Override
        protected String getAsString(Object value) {
            if(value == Weapon.Augment.NONE) return Messages.get(AugumentationSpinner.class,"none");
            return Messages.get(StoneOfAugmentation.WndAugment.class,((Weapon.Augment) value).name().toLowerCase(Locale.ENGLISH));
        }
    }

    private static class ArmorAugSpinnerModel extends SpinnerTextModel implements Runnable {

        private Armor item;

        public ArmorAugSpinnerModel(Armor item) {
            super(Armor.Augment.NONE, Armor.Augment.EVASION, Armor.Augment.DEFENSE);
            this.item = item;
            setValue(item.augment);
        }

        @Override
        public void run() {
            item.augment = (Armor.Augment) getValue();
        }

        @Override
        protected String getAsString(Object value) {
            if(value == Armor.Augment.NONE) return Messages.get(AugumentationSpinner.class,"none");
           return Messages.get(StoneOfAugmentation.WndAugment.class,((Armor.Augment) value).name().toLowerCase(Locale.ENGLISH));
        }
    }
}