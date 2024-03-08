package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.level;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ChooseOneInCategoriesBody;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.WndChooseOneInCategories;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;

import java.util.List;

public class WndSelectMusic extends WndChooseOneInCategories {

    public WndSelectMusic() {

        super(
                Messages.get(WndSelectMusic.class, "title"), "",
                createCategories(),
                getCategoryNames()
        );
    }

    private static Object[][] createCategories() {

        Object[][] cats = new Object[8][];

        //imitate region aka normal music
        cats[0] = new Object[6];
        cats[0][0] = LevelScheme.REGION_NONE;
        cats[0][1] = LevelScheme.REGION_SEWERS;
        cats[0][2] = LevelScheme.REGION_PRISON;
        cats[0][3] = LevelScheme.REGION_CAVES;
        cats[0][4] = LevelScheme.REGION_CITY;
        cats[0][5] = LevelScheme.REGION_HALLS;

        //button for custom music
        cats[1] = new Object[]{null};

        cats[2] = new Object[3];
        cats[2][0] = "";//no music
        cats[2][1] = Assets.Music.THEME_FINALE;
        cats[2][2] = Assets.Music.VANILLA_GAME;

        //each music
        int region = 3;
        int i = 0;

        cats[region] = new Object[5];
        cats[region][i++] = Assets.Music.SEWERS_1;
        cats[region][i++] = Assets.Music.SEWERS_2;
        cats[region][i++] = Assets.Music.SEWERS_3;
        cats[region][i++] = Assets.Music.SEWERS_TENSE;
        cats[region][i++] = Assets.Music.SEWERS_BOSS;

        region++;
        i = 0;
        cats[region] = new Object[5];
        cats[region][i++] = Assets.Music.PRISON_1;
        cats[region][i++] = Assets.Music.PRISON_2;
        cats[region][i++] = Assets.Music.PRISON_3;
        cats[region][i++] = Assets.Music.PRISON_TENSE;
        cats[region][i++] = Assets.Music.PRISON_BOSS;

        region++;
        i = 0;
        cats[region] = new Object[6];
        cats[region][i++] = Assets.Music.CAVES_1;
        cats[region][i++] = Assets.Music.CAVES_2;
        cats[region][i++] = Assets.Music.CAVES_3;
        cats[region][i++] = Assets.Music.CAVES_TENSE;
        cats[region][i++] = Assets.Music.CAVES_BOSS;
        cats[region][i++] = Assets.Music.CAVES_BOSS_FINALE;

        region++;
        i = 0;
        cats[region] = new Object[6];
        cats[region][i++] = Assets.Music.CITY_1;
        cats[region][i++] = Assets.Music.CITY_2;
        cats[region][i++] = Assets.Music.CITY_3;
        cats[region][i++] = Assets.Music.CITY_TENSE;
        cats[region][i++] = Assets.Music.CITY_BOSS;
        cats[region][i++] = Assets.Music.CITY_BOSS_FINALE;

        region++;
        i = 0;
        cats[region] = new Object[6];
        cats[region][i++] = Assets.Music.HALLS_1;
        cats[region][i++] = Assets.Music.HALLS_2;
        cats[region][i++] = Assets.Music.HALLS_3;
        cats[region][i++] = Assets.Music.HALLS_TENSE;
        cats[region][i++] = Assets.Music.HALLS_BOSS;
        cats[region][i++] = Assets.Music.HALLS_BOSS_FINALE;

        return cats;
    }

    private static String[] getCategoryNames() {
        return new String[]{
                Messages.get(WndSelectMusic.class, "region_music"),
                Messages.get(WndSelectMusic.class, "custom_music"),
                Messages.get(WndSelectMusic.class, "special"),
                Document.INTROS.pageTitle(ChangeRegion.REGION_KEYS[0]),
                Document.INTROS.pageTitle(ChangeRegion.REGION_KEYS[1]),
                Document.INTROS.pageTitle(ChangeRegion.REGION_KEYS[2]),
                Document.INTROS.pageTitle(ChangeRegion.REGION_KEYS[3]),
                Document.INTROS.pageTitle(ChangeRegion.REGION_KEYS[4])};
    }

    @Override
    protected ChooseOneInCategoriesBody.BtnRow[] createCategoryRows(Object[] cat) {

        ChooseOneInCategoriesBody.BtnRow[] ret = new ChooseOneInCategoriesBody.BtnRow[cat.length];

        if (cat[0] instanceof Integer) {
            for (int i = 0; i < cat.length; i++) {
                Object value = cat[i];
                ret[i] = new ChooseOneInCategoriesBody.BtnRow(getDisplayName(value), null) {
                    @Override
                    protected void onClick() {
                        onSelect(value);
                        finish();
                    }
                };
            }
            return ret;
        }

        if (cat[0] == null) {
            ret[0] = new ChooseOneInCategoriesBody.BtnRow(Messages.get(WndSelectMusic.class, "custom_music"), Messages.get(WndSelectMusic.class, "custom_music_info")) {
                @Override
                protected void onClick() {
                    onSelect(null);
                }
            };
            return ret;
        }

        if (cat.length == 3) {
            for (int i = 0; i < cat.length; i++) {
                Object value = cat[i];
                ret[i] = new ChooseOneInCategoriesBody.BtnRow(getDisplayName(value), null) {
                    @Override
                    protected void onClick() {
                        onSelect(value);
                        finish();
                    }
                };
            }
            return ret;
        }

        for (int i = 0; i < cat.length; i++) {
            Object value = cat[i];
            ret[i] = new ChooseOneInCategoriesBody.BtnRow(getDisplayName(value), null) {
                {
                    btn.setHighlightingEnabled(false);
                }
                @Override
                protected void onClick() {
                    onSelect(value);
                    finish();
                }
            };

        }

        return ret;
    }

    protected void onSelect(Object music) {
        if (music == null) {
            chooseCustomMusic();
        }
    }

    private void chooseCustomMusic() {
        List<String> audioFiles = CustomDungeonSaves.findAllFiles("ogg", "mp3", "wav");
        String[] options = new String[audioFiles.size()];
        int i = 0;
        for (String m : audioFiles) {//tzz removed: no custom music
            options[i++] = m;
        }
        EditorScene.show(new WndOptions(
                Messages.get(WndSelectMusic.class, "custom_music"),
                Messages.get(WndSelectMusic.class, "custom_music_info", CustomDungeonSaves.getAdditionalFilesDir().file().getAbsolutePath()),
                options
        ) {
            {
                tfMessage.setHighlighting(false);
            }

            @Override
            protected void onSelect(int index) {
                super.onSelect(index);
                WndSelectMusic.this.onSelect(options[index]);
                finish();
            }
        });
    }

    public static String getDisplayName(Object music) {

        if (music == null) {
            return Messages.get(WndSelectMusic.class, "custom_music");
        }

        if (music instanceof Integer) {
            int i = (int) music;
            if (i == 0) return Messages.get(ChangeRegion.class, "same");
            else return Document.INTROS.pageTitle(ChangeRegion.REGION_KEYS[i-1]);
        }

        if (music instanceof String) {
            switch (((String) music)) {
                case "": return Messages.get(WndSelectMusic.class, "none");
                case Assets.Music.THEME_FINALE: return Messages.get(WndSelectMusic.class, "theme_final");
                case Assets.Music.VANILLA_GAME: return Messages.get(WndSelectMusic.class, "vanilla");
            }

            String s = (String) music;
            if (s.startsWith("music/")) s = s.substring(6);

            return s;
        }

        return Messages.NO_TEXT_FOUND;

    }

}