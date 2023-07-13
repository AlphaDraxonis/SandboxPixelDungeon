package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.items;

import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.level.FeelingSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerTextIconModel;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAugmentation;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Image;
import com.watabou.utils.Random;

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


    private static class WeaponAugSpinnerModel extends SpinnerTextIconModel implements Runnable {

        private Weapon item;

        public WeaponAugSpinnerModel(Weapon item) {
            super(true, new Object[]{Weapon.Augment.NONE, Weapon.Augment.SPEED, Weapon.Augment.DAMAGE, Weapon.Augment.RANDOM});
            this.item = item;
            setValue(item.augment);

        }

        @Override
        public void run() {
            item.augment = (Weapon.Augment) getValue();
        }

        @Override
        protected String getAsString(Object value) {
            if (value == Weapon.Augment.NONE) return Messages.get(AugumentationSpinner.class, "none");
            if (value == Weapon.Augment.RANDOM) return Messages.get(FeelingSpinner.class, "random");
            return Messages.get(StoneOfAugmentation.WndAugment.class, ((Weapon.Augment) value).name().toLowerCase(Locale.ENGLISH));
        }

        @Override
        protected Image getIcon(Object value) {
            return value == Weapon.Augment.RANDOM ? new ItemSprite(ItemSpriteSheet.SOMETHING) : null;
        }
    }

    private static class ArmorAugSpinnerModel extends SpinnerTextIconModel implements Runnable {

        private Armor item;

        public ArmorAugSpinnerModel(Armor item) {
            super(true, new Object[]{Armor.Augment.NONE, Armor.Augment.EVASION, Armor.Augment.DEFENSE, Armor.Augment.RANDOM});
            this.item = item;
            setValue(item.augment);
        }

        @Override
        public void run() {
            item.augment = (Armor.Augment) getValue();
        }

        @Override
        protected String getAsString(Object value) {
            if (value == Armor.Augment.NONE) return Messages.get(AugumentationSpinner.class, "none");
            if (value == Armor.Augment.RANDOM) return Messages.get(FeelingSpinner.class, "random");
            return Messages.get(StoneOfAugmentation.WndAugment.class, ((Armor.Augment) value).name().toLowerCase(Locale.ENGLISH));
        }

        @Override
        protected Image getIcon(Object value) {
            return value == Armor.Augment.RANDOM ? new ItemSprite(ItemSpriteSheet.SOMETHING) : null;
        }
    }

    public static void assignRandomAugumentation(Item i){
        Weapon w;
        if (i instanceof Weapon && (w = (Weapon) i).augment == Weapon.Augment.RANDOM) {
            w.augment = (Random.Int(2) == 0) ? Weapon.Augment.DAMAGE : Weapon.Augment.SPEED;
        } else {
            Armor a;
            if (i instanceof Armor && (a = (Armor) i).augment == Armor.Augment.RANDOM) {
                a.augment = (Random.Int(2) == 0) ? Armor.Augment.DEFENSE : Armor.Augment.EVASION;
            }
        }
    }
}