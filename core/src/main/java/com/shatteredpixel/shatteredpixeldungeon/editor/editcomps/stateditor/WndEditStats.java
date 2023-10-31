package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.stateditor;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Brute;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditMobComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndMenuEditor;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.MultiWindowTabComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledButtonWithIconAndText;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerFloatModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.StyledSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAugmentation;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SentryRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class WndEditStats extends MultiWindowTabComp {

    public static Window createWindow(int width, int offsetY, Object defaultStats, Object editStats, Runnable onHide) {
        Window w = new Window() {
            @Override
            public void hide() {
                if (onHide != null) onHide.run();
                super.hide();
            }
        };

        final int offset = offsetY;
        WndEditStats wndEditStats = new WndEditStats(width - 10, defaultStats, editStats) {
            @Override
            public void layout() {
                super.layout();
                w.offset(0, offsetY);
            }
        };

        float height = wndEditStats.preferredHeight();

        w.add(wndEditStats);
        w.resize(width - 10, (int) Math.ceil(height));
        w.offset(0, offsetY);
        wndEditStats.setSize(width - 10, height);

        return w;
    }


    private RedButton restoreDefaults;

    private IntegerSpinner hp, attackSkill, defenseSkill, armor, dmgMin, dmgMax, xp, maxLvl;
    private FloatSpinner speed, statsScale;
    private StyledButtonWithIconAndText loot;

    protected Object defaultStats;

    public WndEditStats(int myWidth, Object defaultStats, Object editStats) {

        super();

        this.width = myWidth;
        this.defaultStats = defaultStats;

        title = new Component() {
            RenderedTextBlock t;

            @Override
            protected void createChildren(Object... params) {
                super.createChildren(params);
                t = PixelScene.renderTextBlock(Messages.get(EditMobComp.class, "edit_stats"), 10);
                t.hardlight(Window.TITLE_COLOR);
                t.maxWidth(myWidth);
                add(t);
            }

            @Override
            public float height() {
                return t.height() + GAP * 2;
            }

            @Override
            public float width() {
                return t.width();
            }

            @Override
            protected void layout() {
                t.setPos(x + (width - title.width()) * 0.5f, y + GAP);
            }
        };
        add(title);

        if (defaultStats instanceof Mob) {
            Mob def = (Mob) defaultStats;
            Mob current = (Mob) editStats;


            if (DefaultStatsCache.useStatsScale(current)) {

                statsScale = new FloatSpinner(Messages.get(Mob.class, "stats_scale"),
                        0.1f, def.statsScale * 10, current.statsScale, false, 0.1f);
                statsScale.addChangeListener(() -> current.statsScale = statsScale.getAsFloat());
                content.add(statsScale);

                if (!(current instanceof SentryRoom.Sentry)) addSpeedSpinner(def, current);

                if (current instanceof Brute) {
                    addHPAccuracyEvasionArmorSpinner(def, current);
                }

            } else {

                addSpeedSpinner(def, current);

                addHPAccuracyEvasionArmorSpinner(def, current);

                dmgMin = new IntegerSpinner(Messages.get(Mob.class, "dmg_min"),
                        0, def.damageRollMin * 10, current.damageRollMin, false);
                dmgMin.addChangeListener(() -> current.damageRollMin = dmgMin.getAsInt());
                content.add(dmgMin);

                dmgMax = new IntegerSpinner(Messages.get(Mob.class, "dmg_max"),
                        0, def.damageRollMax * 10, current.damageRollMax, false);
                dmgMax.addChangeListener(() -> current.damageRollMax = dmgMax.getAsInt());
                content.add(dmgMax);
            }
            xp = new IntegerSpinner(Messages.get(Mob.class, "xp"),
                    0, def.EXP * 10, current.EXP, false);
            xp.addChangeListener(() -> current.EXP = xp.getAsInt());
            content.add(xp);

            maxLvl = new IntegerSpinner(Messages.get(Mob.class, "max_lvl"),
                    -2, 28, current.maxLvl, false);
            maxLvl.addChangeListener(() -> current.maxLvl = maxLvl.getAsInt());
            ((IntegerSpinnerModel) maxLvl.getModel()).setAbsoluteMinimum(-2f);
            content.add(maxLvl);

            if (!(current instanceof Mimic)) {

                loot = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(WndEditStats.class, "loot"), 9){
                    @Override
                    protected void onClick() {
                        LootTableComp lootTable = new LootTableComp(current);
                        changeContent(lootTable.createTitle(), lootTable, lootTable.getOutsideSp(), 0f, 0.5f);
                    }
                };
                content.add(loot);
            }

        }

        restoreDefaults = new RedButton(Messages.get(WndEditStats.class, "restore_default")) {
            @Override
            protected void onClick() {
                restoreDefaults();
            }
        };
        add(restoreDefaults);

        mainWindowComps = new Component[]{
                statsScale, speed, EditorUtilies.PARAGRAPH_INDICATOR_INSTANCE,
                hp, attackSkill, defenseSkill, EditorUtilies.PARAGRAPH_INDICATOR_INSTANCE,
                armor, dmgMin, dmgMax, EditorUtilies.PARAGRAPH_INDICATOR_INSTANCE,
                xp, maxLvl, loot
        };
    }

    @Override
    protected void changeContent(Component titleBar, Component body, Component outsideSp, float alignment, float titleAlignmentX) {
        restoreDefaults.visible = restoreDefaults.active = false;
        super.changeContent(titleBar, body, outsideSp, alignment, titleAlignmentX);
    }

    @Override
    public void closeCurrentSubMenu() {
        restoreDefaults.visible = restoreDefaults.active = true;
        super.closeCurrentSubMenu();
    }

    //    public WndEditStats(int width, int offsetY, Object defaultStats, Object editStats) {
//        this.defaultStats = defaultStats;
//        this.editStats = editStats;
//
//        resize(width - 10, 100);
//        offset(0, offsetY);
//
//
//        content = new Component();
//
//
//        scrollPane = new ScrollPane(content);
//        add(scrollPane);
//
//        float h = Math.min(content.height(), -10 - title.bottom() - 3 - WndMenuEditor.BTN_HEIGHT - WndTitledMessage.GAP - 2);
//
//        resize(this.width, (int) Math.ceil(h + title.bottom() + 3 + WndTitledMessage.GAP + WndMenuEditor.BTN_HEIGHT + 1));//Always call window.resize() before scrollPane.setRect()
//
//        scrollPane.setRect(0, title.bottom() + 3, this.width, h + 1);
//
//        restoreDefaults.setRect(0, scrollPane.bottom() + WndTitledMessage.GAP, this.width, WndMenuEditor.BTN_HEIGHT);
//
//    }


    @Override
    public float preferredHeight() {
        return Math.min(super.preferredHeight(), PixelScene.uiCamera.height * 0.73f)
                + (layoutOwnMenu ? WndMenuEditor.BTN_HEIGHT + GAP - 1 : 0);
    }

    @Override
    public void layout() {
        super.layout();
        if (layoutOwnMenu) {
            sp.setRect(sp.left(), sp.top(), width, height - title.bottom() - GAP * 2 - WndMenuEditor.BTN_HEIGHT);
            restoreDefaults.setRect(x, sp.bottom() + GAP, width, WndMenuEditor.BTN_HEIGHT);
        }
    }

    private void addSpeedSpinner(Mob def, Mob current) {
        speed = new FloatSpinner(Messages.get(StoneOfAugmentation.WndAugment.class, "speed"),
                0.1f, def.baseSpeed * 10, current.baseSpeed, false);
        speed.addChangeListener(() -> current.baseSpeed = speed.getAsFloat());
        content.add(speed);
    }

    private void addHPAccuracyEvasionArmorSpinner(Mob def, Mob current) {

        hp = new IntegerSpinner(Messages.get(Mob.class, "hp"),
                1, def.HT * 10, current.HT, true, 1);
        hp.addChangeListener(() -> {
            int val = hp.getAsInt();
            if (val == -1) val = Char.INFINITE_HP;
            current.HT = current.HP = val;
        });
        content.add(hp);

        attackSkill = new IntegerSpinner(Messages.get(Mob.class, "accuracy"),
                0, def.attackSkill * 10, current.attackSkill, true);
        attackSkill.addChangeListener(() -> {
            int val = attackSkill.getAsInt();
            if (val == -1) val = Char.INFINITE_ACCURACY;
            current.attackSkill = val;
        });
        content.add(attackSkill);

        defenseSkill = new IntegerSpinner(Messages.get(StoneOfAugmentation.WndAugment.class, "evasion"),
                0, def.defenseSkill * 10, current.defenseSkill, true);
        defenseSkill.addChangeListener(() -> {
            int val = defenseSkill.getAsInt();
            if (val == -1) val = Char.INFINITE_EVASION;
            current.defenseSkill = val;
        });
        content.add(defenseSkill);

        armor = new IntegerSpinner(Messages.get(Mob.class, "armor"),
                0, def.damageReductionMax * 10, current.damageReductionMax, false);
        armor.addChangeListener(() -> current.damageReductionMax = armor.getAsInt());
        content.add(armor);
    }

    protected void restoreDefaults() {
        if (defaultStats instanceof Mob) {
            Mob def = (Mob) defaultStats;

            if (speed != null) speed.setValue(SpinnerFloatModel.convertToInt(def.baseSpeed));
            if (statsScale != null) statsScale.setValue(SpinnerFloatModel.convertToInt(def.statsScale));
            if (hp != null) {
                hp.setValue(def.HT);
                attackSkill.setValue(def.attackSkill);
                defenseSkill.setValue(def.defenseSkill);
                armor.setValue(def.damageReductionMax);
            }
            if (dmgMin != null) {
                dmgMin.setValue(def.damageRollMin);
                dmgMax.setValue(def.damageRollMax);
            }
            if (xp != null) xp.setValue(def.EXP);
        }
    }

    private static class FloatSpinner extends StyledSpinner {

        public FloatSpinner(String name, float minimum, float maximum, float value, boolean includeInfinity) {
            super(new SpinnerFloatModel(minimum, maximum, value, false) {
                @Override
                public float getInputFieldWith(float height) {
                    return Spinner.FILL;
                }
            }, name, 9);
            setButtonWidth(12);
        }

        public FloatSpinner(String name, float minimum, float maximum, float value, boolean includeInfinity, float realMin) {
            super(new SpinnerFloatModel(minimum, maximum, value, false){@Override
            public float getInputFieldWith(float height) {
                return Spinner.FILL;
            }}, name, 9);
            ((SpinnerIntegerModel) getModel()).setAbsoluteMinimum(realMin);
            setButtonWidth(12);
        }

        protected float getAsFloat() {
            return ((SpinnerFloatModel) getModel()).getAsFloat();
        }
    }


    private static class IntegerSpinner extends StyledSpinner {

        public IntegerSpinner(String name, int minimum, int maximum, int value, boolean includeInfinity) {
            super(new IntegerSpinnerModel(minimum, maximum, value, false), name, 9);
            setButtonWidth(12);
        }

        public IntegerSpinner(String name, int minimum, int maximum, int value, boolean includeInfinity, int realMin) {
            super(new IntegerSpinnerModel(minimum, maximum, value, false){
                @Override
                public void displayInputAnyNumberDialog() {
                    displayInputAnyNumberDialog(realMin, Integer.MAX_VALUE);
                }
            }, name, 9);
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
            return Spinner.FILL;
        }
    }

    @Override
    public Image createIcon() {
        return null;
    }
}