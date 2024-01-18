package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.stateditor;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Brute;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.CrystalWisp;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Eye;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GnollGuard;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Goo;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.HeroMob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Skeleton;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Warlock;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditMobComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs.GlyphSpinner;
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

    private IntegerSpinner hp, viewDistance, attackSkill, defenseSkill, armor, dmgMin, dmgMax, specialDmgMin, specialDmgMax, tilesBeforeWakingUp, xp, maxLvl;
    private FloatSpinner speed, statsScale;
    private StyledButtonWithIconAndText loot;

    private GlyphSpinner glyphSpinner;

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
                        0.1f, Math.max(10, def.statsScale * 10), current.statsScale, false);
                statsScale.addChangeListener(() -> current.statsScale = statsScale.getAsFloat());
                content.add(statsScale);

                if (!(current instanceof SentryRoom.Sentry)) addSpeedViewDistanceSpinner(def, current);

            } else {

                addSpeedViewDistanceSpinner(def, current);

                addHPAccuracyEvasionArmorSpinner(def, current);

                dmgMin = new IntegerSpinner(Messages.get(Mob.class, "dmg_min"),
                        0, Math.max(10, def.damageRollMin * 10), current.damageRollMin, false);
                dmgMin.addChangeListener(() -> current.damageRollMin = dmgMin.getAsInt());
                content.add(dmgMin);

                dmgMax = new IntegerSpinner(Messages.get(Mob.class, "dmg_max"),
                        0, Math.max(10, def.damageRollMax * 10), current.damageRollMax, false);
                dmgMax.addChangeListener(() -> current.damageRollMax = dmgMax.getAsInt());
                content.add(dmgMax);

                if (current instanceof Skeleton || current instanceof Warlock || current instanceof Brute
                        || current instanceof Goo || current instanceof CrystalWisp || current instanceof Eye || current instanceof GnollGuard) {
                    specialDmgMin = new IntegerSpinner(Messages.get(Mob.class, "special_dmg_min"),
                            0, Math.max(10, def.specialDamageRollMin * 10), current.specialDamageRollMin, false);
                    specialDmgMin.addChangeListener(() -> current.specialDamageRollMin = specialDmgMin.getAsInt());
                    content.add(specialDmgMin);

                    specialDmgMax = new IntegerSpinner(Messages.get(Mob.class, "special_dmg_max"),
                            0, Math.max(10, def.specialDamageRollMax * 10), current.specialDamageRollMax, false);
                    specialDmgMax.addChangeListener(() -> current.specialDamageRollMax = specialDmgMax.getAsInt());
                    content.add(specialDmgMax);
                }

            }
            tilesBeforeWakingUp = new IntegerSpinner(Messages.get(Mob.class, "tiles_before_waking_up"),
                    0, Math.max(10, def.tilesBeforeWakingUp * 10), current.tilesBeforeWakingUp, false);
            tilesBeforeWakingUp.addChangeListener(() -> current.tilesBeforeWakingUp = tilesBeforeWakingUp.getAsInt());
            content.add(tilesBeforeWakingUp);

            if (!(current instanceof HeroMob)) {
                xp = new IntegerSpinner(Messages.get(Mob.class, "xp"),
                        0, Math.max(10, def.EXP * 10), current.EXP, false);
                xp.addChangeListener(() -> current.EXP = xp.getAsInt());
                content.add(xp);
            }

            maxLvl = new IntegerSpinner(Messages.get(Mob.class, "max_lvl"),
                    0, 30, current.maxLvl + Mob.DROP_LOOT_IF_ABOVE_MAX_LVL, false);
            maxLvl.addChangeListener(() -> current.maxLvl = maxLvl.getAsInt() - Mob.DROP_LOOT_IF_ABOVE_MAX_LVL);
            content.add(maxLvl);

            if (!(current instanceof Mimic)) {

                loot = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(WndEditStats.class, "loot"), 9) {
                    @Override
                    protected void onClick() {
                        LootTableComp lootTable = new LootTableComp(current);
                        changeContent(lootTable.createTitle(), lootTable, lootTable.getOutsideSp(), 0f, 0.5f);
                    }
                };
                content.add(loot);
            }

            glyphSpinner = new GlyphSpinner(current);
            content.add(glyphSpinner);

            glyphSpinner.glyphLevelSpinner = new GlyphSpinner.GlyphLevelSpinner(current);
            content.add(glyphSpinner.glyphLevelSpinner);

        }

        restoreDefaults = new RedButton(Messages.get(WndEditStats.class, "restore_default")) {
            @Override
            protected void onClick() {
                restoreDefaults();
            }
        };
        add(restoreDefaults);

        mainWindowComps = new Component[]{
                statsScale, speed, viewDistance, EditorUtilies.PARAGRAPH_INDICATOR_INSTANCE,
                hp, attackSkill, defenseSkill,
                armor, dmgMin, dmgMax, specialDmgMin, specialDmgMax, EditorUtilies.PARAGRAPH_INDICATOR_INSTANCE,
                tilesBeforeWakingUp, xp, maxLvl, loot, glyphSpinner, glyphSpinner.glyphLevelSpinner
        };
    }

    @Override
    public void changeContent(Component titleBar, Component body, Component outsideSp, float alignment, float titleAlignmentX) {
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

    private void addSpeedViewDistanceSpinner(Mob def, Mob current) {
        speed = new FloatSpinner(Messages.get(StoneOfAugmentation.WndAugment.class, "speed"),
                0.1f, Math.max(10, def.baseSpeed * 10), current.baseSpeed, false);
        speed.addChangeListener(() -> current.baseSpeed = speed.getAsFloat());
        content.add(speed);

        viewDistance = new IntegerSpinner(Messages.get(Mob.class, "view_distance"),
                1, Math.max(10, def.viewDistance * 10), current.viewDistance, false);
        viewDistance.addChangeListener(() -> current.viewDistance = viewDistance.getAsInt());
        content.add(viewDistance);
    }

    private void addHPAccuracyEvasionArmorSpinner(Mob def, Mob current) {

        hp = new IntegerSpinner(Messages.get(Mob.class, "hp"),
                1, Math.max(10, def.HT * 10), current.HT, true);
        hp.addChangeListener(() -> {
            int val = hp.getAsInt();
            if (val == -1) val = Char.INFINITE_HP;
            current.HT = current.HP = val;
        });
        content.add(hp);

        attackSkill = new IntegerSpinner(Messages.get(Mob.class, "accuracy"),
                0, Math.max(10, def.attackSkill * 10), current.attackSkill, true);
        attackSkill.addChangeListener(() -> {
            int val = attackSkill.getAsInt();
            if (val == -1) val = Char.INFINITE_ACCURACY;
            current.attackSkill = val;
        });
        content.add(attackSkill);

        defenseSkill = new IntegerSpinner(Messages.get(StoneOfAugmentation.WndAugment.class, "evasion"),
                0, Math.max(10, def.defenseSkill * 10), current.defenseSkill, true);
        defenseSkill.addChangeListener(() -> {
            int val = defenseSkill.getAsInt();
            if (val == -1) val = Char.INFINITE_EVASION;
            current.defenseSkill = val;
        });
        content.add(defenseSkill);

        armor = new IntegerSpinner(Messages.get(Mob.class, "armor"),
                0, Math.max(10, def.damageReductionMax * 10), current.damageReductionMax, false);
        armor.addChangeListener(() -> current.damageReductionMax = armor.getAsInt());
        content.add(armor);
    }

    protected void restoreDefaults() {
        if (defaultStats instanceof Mob) {
            Mob def = (Mob) defaultStats;

            if (speed != null) speed.setValue(SpinnerFloatModel.convertToInt(def.baseSpeed, 1));
            if (viewDistance != null) viewDistance.setValue(def.viewDistance);
            if (statsScale != null) statsScale.setValue(SpinnerFloatModel.convertToInt(def.statsScale, 1));
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
            if (specialDmgMin != null) {
                specialDmgMin.setValue(def.specialDamageRollMin);
                specialDmgMin.setValue(def.specialDamageRollMax);
            }
            if (tilesBeforeWakingUp != null) tilesBeforeWakingUp.setValue(def.tilesBeforeWakingUp);
            if (xp != null) xp.setValue(def.EXP);
            if (maxLvl != null) maxLvl.setValue(def.maxLvl + Mob.DROP_LOOT_IF_ABOVE_MAX_LVL);
        }
    }

    private static class FloatSpinner extends StyledSpinner {

        public FloatSpinner(String name, float minimum, float maximum, float value, boolean includeInfinity) {
            super(new SpinnerFloatModel(minimum, maximum, value, false) {
                @Override
                public float getInputFieldWidth(float height) {
                    return Spinner.FILL;
                }
            }, name, 9);
        }

        protected float getAsFloat() {
            return ((SpinnerFloatModel) getModel()).getAsFloat();
        }
    }


    private static class IntegerSpinner extends StyledSpinner {

        public IntegerSpinner(String name, int minimum, int maximum, int value, boolean includeInfinity) {
            super(new IntegerSpinnerModel(minimum, maximum, value, false), name, 9);
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
        public float getInputFieldWidth(float height) {
            return Spinner.FILL;
        }
    }

    @Override
    public Image createIcon() {
        return null;
    }

    @Override
    public String hoverText() {
        return null;
    }
}