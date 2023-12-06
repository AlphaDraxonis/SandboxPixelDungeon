package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.items;

import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.RandomItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.level.FeelingSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerTextIconModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.StyledSpinner;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAugmentation;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Random;

import java.util.Locale;

public class AugumentationSpinner extends StyledSpinner {


    public AugumentationSpinner(Item item) {
        super(item instanceof Weapon ?
                        new WeaponAugSpinnerModel((Weapon) item) :
                        new ArmorAugSpinnerModel((Armor) item),
                Messages.get(AugumentationSpinner.class, "label"), 9,
                new ItemSprite(ItemSpriteSheet.STONE_AUGMENTATION));

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
        public Component createInputField(int fontSize) {
            return super.createInputField(fontSize-2);
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
        public Component createInputField(int fontSize) {
            return super.createInputField(fontSize-2);
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

    public static Item assignRandomAugmentation(Item i) {
        return assignRandomAugmentation(i, false);
    }

    public static Item assignRandomAugmentation(Item i, boolean identifyOnlyEquipment) {
        if (i instanceof RandomItem) {
            i = ((RandomItem<?>) i).generateItem();
        }
        if (i instanceof Weapon) {
            Weapon w;
            if ((w = (Weapon) i).augment == Weapon.Augment.RANDOM) {
                w.augment = (Random.Int(2) == 0) ? Weapon.Augment.DAMAGE : Weapon.Augment.SPEED;
            }
        } else if (i instanceof Armor) {
            Armor a;
            if ((a = (Armor) i).augment == Armor.Augment.RANDOM) {
                a.augment = (Random.Int(2) == 0) ? Armor.Augment.DEFENSE : Armor.Augment.EVASION;
            }
        }
        if (i == null) return null;
        if (i.identifyOnStart && (!identifyOnlyEquipment || (i instanceof EquipableItem || i instanceof Wand))) i.identify();
        return i;
    }
}