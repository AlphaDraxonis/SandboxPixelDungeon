package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.dungeon;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BlobImmunity;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FireImbue;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Foresight;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FrostImbue;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Haste;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Levitation;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MindVision;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Stamina;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ToxicImbue;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.stateditor.WndEditStats;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.StyledSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfCleansing.Cleanse;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.ui.Component;

public class DurationSettings extends Component {

    private final Component outsideSp;

    private final SettingsTab[] tabs;


    public DurationSettings() {

        tabs = new SettingsTab[1];

        for (int i = 0; i < tabs.length; i++) {
            tabs[i] = SettingsTab.createTab(i);
            tabs[i].visible = tabs[i].active = false;
            add(tabs[i]);
        }
        tabs[0].visible = tabs[0].active = true;

//        outsideSp = new MultiWindowTabComp.OutsideSpSwitchTabs() {
//
//            @Override
//            protected void createChildren(Object... params) {
//                tabs = new TabControlButton[DurationSettings.this.tabs.length];
//                for (int j = 0; j < tabs.length; j++) {
//                    tabs[j] = new MultiWindowTabComp.OutsideSpSwitchTabs.TabControlButton(j);
//                    tabs[j].icon(createTabIcon(j));
//                    add(tabs[j]);
//                }
//
//                super.createChildren(params);
//
//                select(currentIndex);
//            }
//
//            @Override
//            public void select(int index) {
//                selectTab(index);
//                super.select(index);
//            }
//
//            @Override
//            public String getTabName(int index) {
//                return HeroSettings.getTabName(index);
//            }
//        };

        outsideSp = new RedButton(Messages.get(WndEditStats.class, "restore_default")) {
            @Override
            protected void onClick() {
                tabs[0].restoreDefaults();
            }

            @Override
            protected void layout() {
                super.layout();
                height = 16;
            }
        };
    }

//    public void selectTab(int index) {
//        int currentIndex = outsideSp == null ? 0 : outsideSp.getCurrentIndex();
//        tabs[currentIndex].visible = tabs[currentIndex].active = false;
//        tabs[index].visible = tabs[index].active = true;
//    }

    @Override
    protected void layout() {
        for (SettingsTab tab : tabs) {
            if (tab.visible) {
                tab.setRect(x, y, width, -1);
                height = tab.height();
                break;
            }
        }
    }

    public Component createTitle() {
//        int index = outsideSp == null ? 0 : outsideSp.getCurrentIndex();
        RenderedTextBlock titleTextBlock = PixelScene.renderTextBlock(getTabName(0), 12);
        titleTextBlock.hardlight(Window.TITLE_COLOR);
        return titleTextBlock;
    }

    public Component getOutsideSp() {
        return outsideSp;
    }

//    public static Image createTabIcon(int index) {
//        if (index == 0) return Icons.ANY_HERO.get();
//        return BadgeBanner.image(index - 1);
//    }

    public static String getTabName(int index) {
        return Messages.get(DurationSettings.class, "title");
//        switch (index) {
//            case 0:
//                return "1";
//        }
//        return Messages.NO_TEXT_FOUND;
    }


    private static abstract class SettingsTab extends Component {

        protected RenderedTextBlock info;
        private Component wrapper;

        protected Component[] elements;

        public static SettingsTab createTab(int index) {
            return new TabOne();
        }

        public SettingsTab() {
            super();
            if (elements != null) {
                for (Component comp : elements) {
                    wrapper.add(comp);
                }
            }
        }

        @Override
        protected void createChildren(Object... params) {

            info = PixelScene.renderTextBlock(Messages.get(DurationSettings.class, "info"), 6);
            add(info);

            wrapper = new Component();
            add(wrapper);
        }

        @Override
        protected void layout() {
            info.maxWidth((int) width);
            info.setPos(x, y + 1);
            if (elements != null) {
                wrapper.setRect(x, info.bottom() + 4, width, EditorUtilies.layoutStyledCompsInRectangles(2, width, wrapper, elements));
                height = wrapper.bottom() + 1;
            } else height = info.bottom() + 1;
        }

        protected static SpinnerModel createSpinnerModel(float defaultVal, float currentVal) {
            return new SpinnerIntegerModel(1, (int) (defaultVal * 10), (int) currentVal, 1, false, null) {
                {
                    setAbsoluteMinimum((float) getMinimum());
                }

                @Override
                public int getClicksPerSecondWhileHolding() {
                    return 25;
                }

                @Override
                public void setMinimum(Integer minimum) {
                    super.setMinimum(minimum);
                    setAbsoluteMinimum((float) minimum);
                }
            };
        }

        public abstract void restoreDefaults();
    }

    private static class TabOne extends SettingsTab {

        @Override
        protected void createChildren(Object... params) {
            super.createChildren(params);

            final EffectDuration ed = Dungeon.customDungeon.effectDuration;

            StyledSpinner haste = new StyledSpinner(createSpinnerModel(Haste.defaultDuration(), Haste.DURATION()),
                    Messages.titleCase(Messages.get(Haste.class, "name")), 8, new BuffIcon(new Haste(), true));
            haste.addChangeListener(() -> ed.put(Haste.class, (Integer) haste.getValue()));

            StyledSpinner invisibility = new StyledSpinner(createSpinnerModel(Invisibility.defaultDuration(), Invisibility.DURATION()),
                    Messages.titleCase(Messages.get(Invisibility.class, "name")), 8, new BuffIcon(new Invisibility(), true));
            invisibility.addChangeListener(() -> ed.put(Invisibility.class, (Integer) invisibility.getValue()));

            StyledSpinner levitation = new StyledSpinner(createSpinnerModel(Levitation.defaultDuration(), Levitation.DURATION()),
                    Messages.titleCase(Messages.get(Levitation.class, "name")), 8, new BuffIcon(new Levitation(), true));
            levitation.addChangeListener(() -> ed.put(Levitation.class, (Integer) levitation.getValue()));

            StyledSpinner mindVision = new StyledSpinner(createSpinnerModel(MindVision.defaultDuration(), MindVision.DURATION()),
                    Messages.titleCase(Messages.get(MindVision.class, "name")), 8, new BuffIcon(new MindVision(), true));
            mindVision.addChangeListener(() -> ed.put(MindVision.class, (Integer) mindVision.getValue()));

            StyledSpinner blobImmunity = new StyledSpinner(createSpinnerModel(BlobImmunity.defaultDuration(), BlobImmunity.DURATION()),
                    Messages.titleCase(Messages.get(BlobImmunity.class, "name")), 8, new BuffIcon(new BlobImmunity(), true));
            blobImmunity.addChangeListener(() -> ed.put(BlobImmunity.class, (Integer) blobImmunity.getValue()));


            StyledSpinner cleanse = new StyledSpinner(createSpinnerModel(Cleanse.defaultDuration(), Cleanse.DURATION()),
                    Messages.titleCase(Messages.get(Cleanse.class, "name")), 8, new BuffIcon(new Cleanse(), true));
            cleanse.addChangeListener(() -> ed.put(Cleanse.class, (Integer) cleanse.getValue()));

            StyledSpinner stamina = new StyledSpinner(createSpinnerModel(Stamina.defaultDuration(), Stamina.DURATION()),
                    Messages.titleCase(Messages.get(Stamina.class, "name")), 8, new BuffIcon(new Stamina(), true));
            stamina.addChangeListener(() -> ed.put(Stamina.class, (Integer) stamina.getValue()));

            StyledSpinner fireImbue = new StyledSpinner(createSpinnerModel(FireImbue.defaultDuration(), FireImbue.DURATION()),
                    Messages.titleCase(Messages.get(FireImbue.class, "name")), 8, new BuffIcon(new FireImbue(), true));
            fireImbue.addChangeListener(() -> ed.put(FireImbue.class, (Integer) fireImbue.getValue()));

            StyledSpinner frostImbue = new StyledSpinner(createSpinnerModel(FrostImbue.defaultDuration(), FrostImbue.DURATION()),
                    Messages.titleCase(Messages.get(FrostImbue.class, "name")), 8, new BuffIcon(new FrostImbue(), true));
            frostImbue.addChangeListener(() -> ed.put(FrostImbue.class, (Integer) frostImbue.getValue()));

            StyledSpinner toxicImbue = new StyledSpinner(createSpinnerModel(ToxicImbue.defaultDuration(), ToxicImbue.DURATION()),
                    Messages.titleCase(Messages.get(ToxicImbue.class, "name")), 8, new BuffIcon(new ToxicImbue(), true));
            toxicImbue.addChangeListener(() -> ed.put(ToxicImbue.class, (Integer) toxicImbue.getValue()));


            StyledSpinner magicImmune = new StyledSpinner(createSpinnerModel(MagicImmune.defaultDuration(), MagicImmune.DURATION()),
                    Messages.titleCase(Messages.get(MagicImmune.class, "name")), 8, new BuffIcon(new MagicImmune(), true));
            magicImmune.addChangeListener(() -> ed.put(MagicImmune.class, (Integer) magicImmune.getValue()));

            StyledSpinner foresight = new StyledSpinner(createSpinnerModel(Foresight.defaultDuration(), Foresight.DURATION()),
                    Messages.titleCase(Messages.get(Foresight.class, "name")), 8, new BuffIcon(new Foresight(), true));
            foresight.addChangeListener(() -> ed.put(Foresight.class, (Integer) foresight.getValue()));

            elements = new Component[]{haste, invisibility, levitation, mindVision, blobImmunity, EditorUtilies.PARAGRAPH_INDICATOR_INSTANCE,
                    cleanse, stamina, EditorUtilies.PARAGRAPH_INDICATOR_INSTANCE,
                    fireImbue, frostImbue, toxicImbue, EditorUtilies.PARAGRAPH_INDICATOR_INSTANCE,
                    magicImmune, foresight};//if layout changes: also change restoreDefaults()!!!!
        }

        @Override
        public void restoreDefaults() {

            final EffectDuration ed = Dungeon.customDungeon.effectDuration;

            ((Spinner) elements[0]).setValue((int) Haste.defaultDuration());
            ed.put(Haste.class, 0);

            ((Spinner) elements[1]).setValue((int) Invisibility.defaultDuration());
            ed.put(Invisibility.class, 0);

            ((Spinner) elements[2]).setValue((int) Levitation.defaultDuration());
            ed.put(Levitation.class, 0);

            ((Spinner) elements[3]).setValue((int) MindVision.defaultDuration());
            ed.put(MindVision.class, 0);

            ((Spinner) elements[4]).setValue((int) BlobImmunity.defaultDuration());
            ed.put(BlobImmunity.class, 0);


            ((Spinner) elements[6]).setValue((int) Cleanse.defaultDuration());
            ed.put(Cleanse.class, 0);

            ((Spinner) elements[7]).setValue((int) Stamina.defaultDuration());
            ed.put(Stamina.class, 0);


            ((Spinner) elements[9]).setValue((int) FireImbue.defaultDuration());
            ed.put(FireImbue.class, 0);

            ((Spinner) elements[10]).setValue((int) FrostImbue.defaultDuration());
            ed.put(FrostImbue.class, 0);

            ((Spinner) elements[11]).setValue((int) ToxicImbue.defaultDuration());
            ed.put(ToxicImbue.class, 0);


            ((Spinner) elements[13]).setValue((int) MagicImmune.defaultDuration());
            ed.put(MagicImmune.class, 0);

            ((Spinner) elements[14]).setValue((int) Foresight.defaultDuration());
            ed.put(Foresight.class, 0);

        }
    }

}