package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.level;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ChooseOneInCategoriesBody;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.WndChooseOneInCategories;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;

import java.util.List;

public class WndSelectMusic extends WndChooseOneInCategories {

    public enum TypeOfFirstCategory {
        REGION_MUSIC("region_music", LevelScheme.REGION_NONE, LevelScheme.REGION_SEWERS, LevelScheme.REGION_PRISON,
                LevelScheme.REGION_CAVES, LevelScheme.REGION_CITY, LevelScheme.REGION_HALLS),
        FOR_ZONES("no_change", -1),
        FOR_BOSSES("no_change", -2, -3);

        private final String msgKey;
        private final Object[] data;

        TypeOfFirstCategory(String msgKey, Object... data) {
            this.msgKey = msgKey;
            this.data = data;
        }

        private String title() {
            return Messages.get(WndSelectMusic.class, msgKey);
        }
    }

    public WndSelectMusic(TypeOfFirstCategory typeOfFirstCategory) {

        super(
                Messages.get(WndSelectMusic.class, "title"), "",
                createCategories(typeOfFirstCategory),
                getCategoryNames(typeOfFirstCategory)
        );
    }

    private static Object[][] createCategories(TypeOfFirstCategory typeOfFirstCategory) {

        Object[][] cats = new Object[8][];

        //imitate region aka normal music
        cats[0] = typeOfFirstCategory.data;

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

    private static String[] getCategoryNames(TypeOfFirstCategory typeOfFirstCategory) {
        return new String[]{
                typeOfFirstCategory.title(),
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
            ret[0] = new ChooseOneInCategoriesBody.BtnRow(Messages.get(WndSelectMusic.class, "custom_music"),
                    Messages.get(WndSelectMusic.class, "custom_music_info", CustomDungeonSaves.getAdditionalFilesDir().file().getAbsolutePath())) {
                @Override
                protected void onInfo() {
                    DungeonScene.show(new WndTitledMessage(Icons.get(Icons.INFO), name, info()) {
                        {
                            setHighlightingEnabled(false);
                        }
                    });
                }

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
        List<String> audioFiles = CustomDungeonSaves.findAllFilePaths("ogg", "mp3", "wav");
        String[] options = new String[audioFiles.size()];
        int i = 0;
        for (String m : audioFiles) {
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

            if (i < 0) {
                if (i == -1) return Messages.get(WndSelectMusic.class, "no_change");
                if (i == -2) return Messages.get(WndSelectMusic.class, "default_music");
                if (i == -3) return Messages.get(WndSelectMusic.class, "music_change_disabled");
            }

            if (i > 0) return Document.INTROS.pageTitle(ChangeRegion.REGION_KEYS[i-1]);
            else return Messages.get(ChangeRegion.class, "same");
        }

        if (music instanceof String) {
            switch (((String) music)) {
                case "": return Messages.get(WndSelectMusic.class, "none");
                case "/": return Messages.get(WndSelectMusic.class, "music_change_disabled");
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