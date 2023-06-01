package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.gerneral;

import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ChooseOneInCategoriesBody;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.levels.editor.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.editor.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Game;
import com.watabou.noosa.ui.Component;

public class WndChangeRegion extends Window {


    private final int HEIGHT = (int) (PixelScene.uiCamera.height * 0.9), WIDTH = Math.min(160, (int) (PixelScene.uiCamera.width * 0.9));

    private final ScrollPane sp;
    private final Component wrapper;
    private final RenderedTextBlock[] catTitles;
    private final RenderedTextBlock title;
    private final RedButton save, cancel;

    private static final int CAT_REGION = 0, CAT_WATER = 1, CAT_MUSIC = 2;
    private final AssetCheckbox[][] checkboxes = {
            {
                    new AssetCheckbox(Document.INTROS.pageTitle("Sewers"), LevelScheme.REGION_SEWERS, CAT_REGION),
                    new AssetCheckbox(Document.INTROS.pageTitle("Prison"), LevelScheme.REGION_PRISON, CAT_REGION),
                    new AssetCheckbox(Document.INTROS.pageTitle("Caves"), LevelScheme.REGION_CAVES, CAT_REGION),
                    new AssetCheckbox(Document.INTROS.pageTitle("City"), LevelScheme.REGION_CITY, CAT_REGION),
                    new AssetCheckbox(Document.INTROS.pageTitle("Halls"), LevelScheme.REGION_HALLS, CAT_REGION)
            }, {
            new AssetCheckbox("Same as region", 0, CAT_WATER),
            new AssetCheckbox(Document.INTROS.pageTitle("Sewers"), LevelScheme.REGION_SEWERS, CAT_WATER),
            new AssetCheckbox(Document.INTROS.pageTitle("Prison"), LevelScheme.REGION_PRISON, CAT_WATER),
            new AssetCheckbox(Document.INTROS.pageTitle("Caves"), LevelScheme.REGION_CAVES, CAT_WATER),
            new AssetCheckbox(Document.INTROS.pageTitle("City"), LevelScheme.REGION_CITY, CAT_WATER),
            new AssetCheckbox(Document.INTROS.pageTitle("Halls"), LevelScheme.REGION_HALLS, CAT_WATER)
    }, {
            new AssetCheckbox("Same as region", 0, CAT_MUSIC),
            new AssetCheckbox(Document.INTROS.pageTitle("Sewers"), LevelScheme.REGION_SEWERS, CAT_MUSIC),
            new AssetCheckbox(Document.INTROS.pageTitle("Prison"), LevelScheme.REGION_PRISON, CAT_MUSIC),
            new AssetCheckbox(Document.INTROS.pageTitle("Caves"), LevelScheme.REGION_CAVES, CAT_MUSIC),
            new AssetCheckbox(Document.INTROS.pageTitle("City"), LevelScheme.REGION_CITY, CAT_MUSIC),
            new AssetCheckbox(Document.INTROS.pageTitle("Halls"), LevelScheme.REGION_HALLS, CAT_MUSIC)
    }
    };

    public WndChangeRegion() {
        super();

        title = WndTitledMessage.createTitleNoIcon(Messages.titleCase("Change region"));
        add(title);


        CustomLevel f = EditorScene.customLevel();

        int[] oldValues = {
                f.getRegionValue(),
                f.getWaterTextureValue(),
                f.getMusicValue()
        };

        save = new RedButton("Save and close") {
            @Override
            protected void onClick() {
                int[] newValues = oldValues.clone();

                for (int i = 0; i < checkboxes.length; i++) {
                    for (AssetCheckbox cb : checkboxes[i]) {
                        if (cb.checked()) newValues[i] = cb.region;
                    }
                }
                for (int i = 0; i < newValues.length; i++) {
                    if (newValues[i] != oldValues[i]) {
                        f.setRegion(newValues[CAT_REGION]);
                        f.setWaterTexture(newValues[CAT_WATER]);
                        f.setMusic(newValues[CAT_MUSIC]);
                        Game.switchScene(EditorScene.class);
                        return;
                    }
                }
                hide();
            }
        };
        cancel = new RedButton("Cancel") {
            @Override
            protected void onClick() {
                hide();
            }
        };
        add(save);
        add(cancel);

        sp = new ScrollPane(wrapper = new Component());

        catTitles = new RenderedTextBlock[]{
                PixelScene.renderTextBlock("Region:", 9),
                PixelScene.renderTextBlock("Water texture:", 9),
                PixelScene.renderTextBlock("Music:", 9)
        };
        for (RenderedTextBlock t : catTitles) {
            t.hardlight(Window.TITLE_COLOR);
            wrapper.add(t);
        }

        for (int i = 0; i < checkboxes.length; i++) {
            for (AssetCheckbox cb : checkboxes[i]) {
                cb.checked(cb.region == oldValues[i]);
                wrapper.add(cb);
            }
        }

        add(sp);

        resize(WIDTH, HEIGHT);
        layout();
    }

    protected void layout() {
        final int GAP = ChooseOneInCategoriesBody.GAP;
        float posY = 0;

        WndTitledMessage.layoutTitleBar(title, width);
        posY = title.bottom() + (title.text().isEmpty() ? 0 : GAP * 4);


        float pos = GAP * 3;

        for (int i = 0; i < checkboxes.length; i++) {

            catTitles[i].maxWidth((int) width);
            catTitles[i].setPos(((width - catTitles[i].width()) * 0.5f), pos);
            pos = catTitles[i].bottom() + GAP * 3;

            for (AssetCheckbox cb : checkboxes[i]) {
                cb.setRect(0, pos, width, ChooseOneInCategoriesBody.BUTTON_HEIGHT);
                PixelScene.align(cb);
                pos = cb.bottom() + GAP;
            }
            if (i + 1 < checkboxes.length) pos += GAP * 4;
        }

        wrapper.setSize(width, pos);


        pos = height - ChooseOneInCategoriesBody.BUTTON_HEIGHT;
        float w = (width - GeneralTab.GAP) / 3f;
        cancel.setRect(0, pos, w, ChooseOneInCategoriesBody.BUTTON_HEIGHT);
        PixelScene.align(cancel);
        save.setRect(cancel.right() + GeneralTab.GAP, pos, w * 2, ChooseOneInCategoriesBody.BUTTON_HEIGHT);
        PixelScene.align(save);

        sp.setRect(0, posY, width, height - posY - 2 * GAP - ChooseOneInCategoriesBody.BUTTON_HEIGHT);
        PixelScene.align(sp);
    }

    private void uncheckAll(int category) {
        for (AssetCheckbox cb : checkboxes[category]) {
            cb.checked(false);
        }
    }

    private class AssetCheckbox extends CheckBox {

        private final int region;
        private final int category;

        public AssetCheckbox(String label, int region, int category) {
            super(Messages.titleCase(label));
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
}