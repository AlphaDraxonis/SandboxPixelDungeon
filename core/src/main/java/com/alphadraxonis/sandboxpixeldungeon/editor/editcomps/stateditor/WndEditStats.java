package com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.stateditor;

import com.alphadraxonis.sandboxpixeldungeon.actors.Char;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Mob;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.EditMobComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.WndMenuEditor;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.Spinner;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.alphadraxonis.sandboxpixeldungeon.items.stones.StoneOfAugmentation;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.ui.RedButton;
import com.alphadraxonis.sandboxpixeldungeon.ui.RenderedTextBlock;
import com.alphadraxonis.sandboxpixeldungeon.ui.ScrollPane;
import com.alphadraxonis.sandboxpixeldungeon.ui.Window;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Gizmo;
import com.watabou.noosa.ui.Component;

public class WndEditStats extends Window {

    private RenderedTextBlock title;
    private Component content;
    private RedButton restoreDefaults;
    private ScrollPane scrollPane;

    private Object defaultStats, editStats;


    private IntegerSpinner hp, attackSkill, defenseSkill, armor, dmgMin, dmgMax;
    private FloatSpinner speed;

    public WndEditStats(int width, int offsetY, Object defaultStats, Object editStats) {
        this.defaultStats = defaultStats;
        this.editStats = editStats;

        resize(width - 10, 100);
        offset(0, offsetY);

        title = PixelScene.renderTextBlock(Messages.get(EditMobComp.class, "edit_stats"), 10);
        title.hardlight(Window.TITLE_COLOR);
        add(title);
        title.maxWidth(this.width);
        title.setPos((width - title.width()) * 0.5f, WndTitledMessage.GAP);

        content = new Component() {
            @Override
            protected void layout() {

                float posY = y + WndTitledMessage.GAP * 2 - 1;

                boolean hasAtLeastOneComp = false;
                for (Gizmo g : members) {
                    if (g instanceof Component) {
                        Component c = (Component) g;
                        hasAtLeastOneComp = true;
                        c.setRect(left(), posY, width, WndMenuEditor.BTN_HEIGHT);
                        posY = c.bottom() + WndTitledMessage.GAP;
                        PixelScene.align(c);
                    }
                }

                if (hasAtLeastOneComp) height = (int)(posY - y - WndTitledMessage.GAP);
            }
        };

        restoreDefaults = new RedButton(Messages.get(WndEditStats.class, "restore_default")) {
            @Override
            protected void onClick() {
                restoreDefaults();
            }
        };
        add(restoreDefaults);

        if (defaultStats instanceof Mob) {
            Mob def = (Mob) defaultStats;
            Mob current = (Mob) editStats;

            hp = new IntegerSpinner(Messages.get(Mob.class, "hp") + ":",
                    1, def.HT * 10, current.HT, true);
            hp.addChangeListener(() -> {
                int val = hp.getAsInt();
                if (val == -1) val = Char.INFINITE_HP;
                current.HT = current.HP = val;
            });
            content.add(hp);

            speed = new FloatSpinner(Messages.get(StoneOfAugmentation.WndAugment.class, "speed") + ":",
                    0.1f, def.baseSpeed * 10, current.baseSpeed, false);
            speed.addChangeListener(() -> current.baseSpeed = speed.getAsFloat());
            content.add(speed);

            attackSkill = new IntegerSpinner(Messages.get(Mob.class, "accuracy") + ":",
                    0, def.attackSkill * 10, current.attackSkill, true);
            attackSkill.addChangeListener(() -> {
                int val = attackSkill.getAsInt();
                if (val == -1) val = Char.INFINITE_ACCURACY;
                current.attackSkill = val;
            });
            content.add(attackSkill);

            defenseSkill = new IntegerSpinner(Messages.get(StoneOfAugmentation.WndAugment.class, "evasion") + ":",
                    0, def.defenseSkill * 10, current.defenseSkill, true);
            defenseSkill.addChangeListener(() -> {
                int val = defenseSkill.getAsInt();
                if (val == -1) val = Char.INFINITE_EVASION;
                current.defenseSkill = val;
            });
            content.add(defenseSkill);

            armor = new IntegerSpinner(Messages.get(Mob.class, "armor") + ":",
                    0, def.damageReductionMax * 10, current.damageReductionMax, false);
            armor.addChangeListener(() -> current.damageReductionMax = armor.getAsInt());
            content.add(armor);

            dmgMin = new IntegerSpinner(Messages.get(Mob.class, "dmg_min") + ":",
                    0, def.damageRollMin * 10, current.damageRollMin, false);
            dmgMin.addChangeListener(() -> current.damageRollMin = dmgMin.getAsInt());
            content.add(dmgMin);

            dmgMax = new IntegerSpinner(Messages.get(Mob.class, "dmg_max") + ":",
                    0, def.damageRollMax * 10, current.damageRollMax, false);
            dmgMax.addChangeListener(() -> current.damageRollMax = dmgMax.getAsInt());
            content.add(dmgMax);
        }

        content.setSize(this.width, -1);

        scrollPane = new ScrollPane(content);
        add(scrollPane);

        float h = Math.min(content.height(), PixelScene.uiCamera.height * 0.9f - 10 - title.bottom() - 3 - WndMenuEditor.BTN_HEIGHT - WndTitledMessage.GAP - 2);

        resize(this.width, (int) Math.ceil(h + title.bottom() + 3 + WndTitledMessage.GAP + WndMenuEditor.BTN_HEIGHT + 1));//Always call window.resize() before scrollPane.setRect()

        scrollPane.setRect(0, title.bottom() + 3, this.width, h + 1);

        restoreDefaults.setRect(0, scrollPane.bottom() + WndTitledMessage.GAP, this.width, WndMenuEditor.BTN_HEIGHT);

    }

    protected void restoreDefaults() {
        if (defaultStats instanceof Mob) {
            Mob def = (Mob) defaultStats;

            hp.setValue(def.HT);
            speed.setValue(FloatSpinnerModel.convertToInt(def.baseSpeed));
            attackSkill.setValue(def.attackSkill);
            defenseSkill.setValue(def.defenseSkill);
            armor.setValue(def.damageReductionMax);
            dmgMin.setValue(def.damageRollMin);
            dmgMax.setValue(def.damageRollMax);
        }
    }

    private static class FloatSpinner extends Spinner {

        public FloatSpinner(String name, float minimum, float maximum, float value, boolean includeInfinity) {
            super(new FloatSpinnerModel(minimum, maximum, value, false), name, 9);
            setButtonWidth(12);
        }

        protected float getAsFloat() {
            return ((FloatSpinnerModel) getModel()).getAsFloat();
        }
    }


    private static class FloatSpinnerModel extends SpinnerIntegerModel {

        public FloatSpinnerModel(float minimum, float maximum, float value, boolean includeInfinity) {
            super(convertToInt(minimum), convertToInt(maximum), convertToInt(value), 1, includeInfinity, includeInfinity ? INFINITY : null);
        }

        public static int convertToInt(float val) {
            return (int) (val * 10 + 0.01f);
        }

        protected float getAsFloat() {
            if (getValue() == null) return -1;
            return ((int) getValue()) / 10f;
        }

        @Override
        public String getDisplayString() {
            return getValue() == null ? super.getDisplayString() : Float.toString(getAsFloat());
        }

        @Override
        public float getInputFieldWith(float height) {
            return height * 1.4f;
        }
    }

    private static class IntegerSpinner extends Spinner {

        public IntegerSpinner(String name, int minimum, int maximum, int value, boolean includeInfinity) {
            super(new IntegerSpinnerModel(minimum, maximum, value, false), name, 9);
            setButtonWidth(12);
        }

        protected int getAsInt() {
            return ((IntegerSpinnerModel) getModel()).getAsInt();
        }
    }


    private static class IntegerSpinnerModel extends SpinnerIntegerModel {

        public IntegerSpinnerModel(int minimum, int maximum, int value, boolean includeInfinity) {
            super(minimum, maximum, value, 1, includeInfinity, includeInfinity ? INFINITY : null);
        }

        protected int getAsInt() {
            if (getValue() == null) return -1;
            return (int) getValue();
        }

        @Override
        public String getDisplayString() {
            return getValue() == null ? super.getDisplayString() : Integer.toString(getAsInt());
        }

        @Override
        public float getInputFieldWith(float height) {
            return height * 1.4f;
        }
    }
}