package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.level;

import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ChooseOneInCategoriesBody;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Game;
import com.watabou.noosa.ui.Component;

public class ChangeRegion extends Component {

    private final RenderedTextBlock[] catTitles;
    private final Component outsideSp;

    private static final int CAT_REGION = 0, CAT_WATER = 1, CAT_MUSIC = 2;
    public static final String[] REGION_KEYS = {
            "Sewers",
            "Prison",
            "Caves",
            "City",
            "Halls"
    };
    private final AssetCheckbox[][] checkboxes = {
            {
                    new AssetCheckbox(null, LevelScheme.REGION_SEWERS, CAT_REGION),
                    new AssetCheckbox(null, LevelScheme.REGION_PRISON, CAT_REGION),
                    new AssetCheckbox(null, LevelScheme.REGION_CAVES, CAT_REGION),
                    new AssetCheckbox(null, LevelScheme.REGION_CITY, CAT_REGION),
                    new AssetCheckbox(null, LevelScheme.REGION_HALLS, CAT_REGION)
            }, {
            new AssetCheckbox(Messages.get(ChangeRegion.class, "same"), 0, CAT_WATER),
            new AssetCheckbox(null, LevelScheme.REGION_SEWERS, CAT_WATER),
            new AssetCheckbox(null, LevelScheme.REGION_PRISON, CAT_WATER),
            new AssetCheckbox(null, LevelScheme.REGION_CAVES, CAT_WATER),
            new AssetCheckbox(null, LevelScheme.REGION_CITY, CAT_WATER),
            new AssetCheckbox(null, LevelScheme.REGION_HALLS, CAT_WATER)
    }, {
            new AssetCheckbox(Messages.get(ChangeRegion.class, "same"), 0, CAT_MUSIC),
            new AssetCheckbox(null, LevelScheme.REGION_SEWERS, CAT_MUSIC),
            new AssetCheckbox(null, LevelScheme.REGION_PRISON, CAT_MUSIC),
            new AssetCheckbox(null, LevelScheme.REGION_CAVES, CAT_MUSIC),
            new AssetCheckbox(null, LevelScheme.REGION_CITY, CAT_MUSIC),
            new AssetCheckbox(null, LevelScheme.REGION_HALLS, CAT_MUSIC),
            new AssetCheckbox(Messages.get(ChangeRegion.class, "theme_final"), -1, CAT_MUSIC)
    }
    };

    private final MusicVariantSpinner musicVariantSpinner;

    public ChangeRegion(Runnable onClose) {
        super();

        CustomLevel f = EditorScene.customLevel();

        int[] oldValues = {
                f.getRegionValue(),
                f.getWaterTextureValue(),
                f.getMusicValue()
        };
        int oldMusicVariant = f.musicVariant;


        outsideSp = new Component() {
            RedButton save, cancel;
            @Override
            protected void createChildren(Object... params) {

                save = new RedButton(Messages.get(ChangeRegion.class, "close")) {
                    @Override
                    protected void onClick() {
                        int[] newValues = oldValues.clone();
                        int newMusicVariant = (int) musicVariantSpinner.getValue();

                        for (int i = 0; i < checkboxes.length; i++) {
                            for (AssetCheckbox cb : checkboxes[i]) {
                                if (cb.checked()) newValues[i] = cb.region;
                            }
                        }
                        onClose.run();
                        for (int i = 0; i < newValues.length; i++) {
                            if (newValues[i] != oldValues[i] || newMusicVariant != oldMusicVariant) {
                                f.setRegion(newValues[CAT_REGION]);
                                f.setWaterTexture(newValues[CAT_WATER]);
                                f.setMusic(newValues[CAT_MUSIC]);
                                f.musicVariant = newMusicVariant;
                                Game.switchScene(EditorScene.class);
                                return;
                            }
                        }
                    }
                };
                cancel = new RedButton(Messages.get(ChangeRegion.class, "cancel")) {
                    @Override
                    protected void onClick() {
                        onClose.run();
                    }
                };
                add(save);
                add(cancel);
            }

            @Override
            protected void layout() {
                float pos = y;
                float w = (width - LevelTab.GAP) / 3f;
                cancel.setRect(0, pos, w, ChooseOneInCategoriesBody.BUTTON_HEIGHT);
                PixelScene.align(cancel);
                save.setRect(cancel.right() + LevelTab.GAP, pos, w * 2, ChooseOneInCategoriesBody.BUTTON_HEIGHT);
                PixelScene.align(save);

                height =  ChooseOneInCategoriesBody.BUTTON_HEIGHT;
            }
        };


        catTitles = new RenderedTextBlock[]{
                PixelScene.renderTextBlock(Messages.get(ChangeRegion.class, "region"), 9),
                PixelScene.renderTextBlock(Messages.get(ChangeRegion.class, "water"), 9),
                PixelScene.renderTextBlock(Messages.get(ChangeRegion.class, "music"), 9)
        };
        for (RenderedTextBlock t : catTitles) {
            t.hardlight(Window.TITLE_COLOR);
            add(t);
        }

        for (int i = 0; i < checkboxes.length; i++) {
            for (AssetCheckbox cb : checkboxes[i]) {
                cb.checked(cb.region == oldValues[i]);
                add(cb);
            }
        }

        musicVariantSpinner = new MusicVariantSpinner(oldMusicVariant);
        add(musicVariantSpinner);
    }

    protected void layout() {
        final int GAP = ChooseOneInCategoriesBody.GAP;

        float pos = GAP * 3;

        for (int i = 0; i < checkboxes.length; i++) {

            catTitles[i].maxWidth((int) width);
            catTitles[i].setPos(((width - catTitles[i].width()) * 0.5f), pos);
            pos = catTitles[i].bottom() + GAP * 3;

            if( i == CAT_MUSIC){
                musicVariantSpinner.setRect(0, pos, width, ChooseOneInCategoriesBody.BUTTON_HEIGHT);
                PixelScene.align(musicVariantSpinner);
                pos = musicVariantSpinner.bottom() + GAP;
            }

            for (AssetCheckbox cb : checkboxes[i]) {
                cb.setRect(0, pos, width, ChooseOneInCategoriesBody.BUTTON_HEIGHT);
                PixelScene.align(cb);
                pos = cb.bottom() + GAP;
            }
            if (i + 1 < checkboxes.length) pos += GAP * 4;
        }

        height = pos;
    }

    private void uncheckAll(int category) {
        for (AssetCheckbox cb : checkboxes[category]) {
            cb.checked(false);
        }
    }

    public static Component createTitle() {
        RenderedTextBlock textBlock = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(LevelTab.class, "region")),12);
        textBlock.hardlight(Window.TITLE_COLOR);
        return textBlock;
    }

    public Component getOutsideSp() {
        return outsideSp;
    }

    private class AssetCheckbox extends CheckBox {

        private final int region;
        private final int category;

        public AssetCheckbox(String label, int region, int category) {
            super(Messages.titleCase(label == null ? Document.INTROS.pageTitle(REGION_KEYS[region]) : label));
            this.region = region;
            this.category = category;
        }

        @Override
        protected void onClick() {
            if (!checked()) {
                uncheckAll(category);
                super.onClick();
            }
        }
    }

    private static class MusicVariantSpinner extends Spinner {


        public MusicVariantSpinner(int val) {
            super(new SpinnerIntegerModel(0,3, val, 1, true, null){
                @Override
                public String getDisplayString() {
                    switch ((int) getValue()) {
                        case 0: return Messages.get(ChangeRegion.class, "normal");
                        case 1: return Messages.get(ChangeRegion.class, "tense");
                        case 2: return Messages.get(ChangeRegion.class, "boss");
                        case 3: return Messages.get(ChangeRegion.class, "boss_final");
                    }
                    return super.getDisplayString();
                }

                @Override
                public float getInputFieldWith(float height) {
                    return Spinner.FILL;
                }
            }, Messages.get(MusicVariantSpinner.class, "label"), 9);
        }
    }
}