package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.level;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.TileSprite;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ChooseOneInCategoriesBody;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledButtonWithIconAndText;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerTextIconModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerTextModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.StyledSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

import java.util.List;
import java.util.Objects;

public class ChangeRegion extends Component {

    public static final String[] REGION_KEYS = {
            "Sewers",
            "Prison",
            "Caves",
            "City",
            "Halls"
    };

    private static final Object[] REGION_DATA = {
            LevelScheme.REGION_SEWERS,
            LevelScheme.REGION_PRISON,
            LevelScheme.REGION_CAVES,
            LevelScheme.REGION_CITY,
            LevelScheme.REGION_HALLS
    };

    private static final Object[] WATER_DATA = {
            LevelScheme.REGION_NONE,//same as region
            LevelScheme.REGION_SEWERS,
            LevelScheme.REGION_PRISON,
            LevelScheme.REGION_CAVES,
            LevelScheme.REGION_CITY,
            LevelScheme.REGION_HALLS
    };

    private static final Object[] MUSIC_DATA = {
            LevelScheme.REGION_NONE,//same as region
            LevelScheme.REGION_SEWERS,
            LevelScheme.REGION_PRISON,
            LevelScheme.REGION_CAVES,
            LevelScheme.REGION_CITY,
            LevelScheme.REGION_HALLS,
            -1,//theme final
            -2,//none
            -3//vanilla game
    };

    protected StyledSpinner region, water, music;
    protected MusicVariantSpinner musicVariantSpinner;
    protected StyledButton customMusic;

    private final Component outsideSp;

    private String newCustomMusic;

    public ChangeRegion(Runnable onClose) {
        super();

        CustomLevel f = EditorScene.customLevel();

        int[] oldValues = {
                f.getRegionValue(),
                f.getWaterTextureValue(),
                f.getMusicValue()
        };
        int oldMusicVariant = f.musicVariant;
        String oldCustomMusic = f.customMusic;
        int[] newValues = {
                oldValues[0],
                oldValues[1],
                oldValues[2]
        };
        newCustomMusic = oldCustomMusic;

        region = new StyledSpinner(new SpinnerTextIconModel(true, oldValues[0] - 1, REGION_DATA) {
            @Override
            protected Image getIcon(Object value) {
                return new TileSprite(CustomLevel.tilesTex((int) value, false), Terrain.EMPTY);
            }

            @Override
            protected String getAsString(Object value) {
                return Document.INTROS.pageTitle(ChangeRegion.REGION_KEYS[(int) value - 1]);
            }
        }, Messages.get(ChangeRegion.class, "region"), 9);
        region.setSpinnerHeight(21);
        add(region);

        water = new StyledSpinner(new SpinnerTextIconModel(true, oldValues[1], WATER_DATA) {
            @Override
            protected Image getIcon(Object value) {
                int waterRegion = (int) ((int) value == LevelScheme.REGION_NONE ? region == null ? oldValues[0] : region.getValue() : value);
                return new TileSprite(CustomLevel.tilesTex(waterRegion, true), Terrain.WATER);
            }

            @Override
            protected String getAsString(Object value) {
                if ((int) value == LevelScheme.REGION_NONE) return Messages.get(ChangeRegion.class, "same");
                return Document.INTROS.pageTitle(ChangeRegion.REGION_KEYS[(int) value - 1]);
            }
        }, Messages.get(ChangeRegion.class, "water"), 9);
        water.addChangeListener(() -> newValues[1] = (int) water.getValue());
        water.setSpinnerHeight(21);
        add(water);

        region.addChangeListener(() -> {
            newValues[0] = (int) region.getValue();
            water.setValue(water.getValue());
        });

        music = new StyledSpinner(new SpinnerTextModel(true, oldValues[2] < 0 ? 5 - oldValues[2] : oldValues[2], MUSIC_DATA) {
            @Override
            protected String getAsString(Object value) {
                switch ((int)value) {
                    case LevelScheme.REGION_NONE:
                        return Messages.get(ChangeRegion.class, "same");
                    case -1:
                        return Messages.get(ChangeRegion.class, "theme_final");
                    case -2:
                        return Messages.get(ChangeRegion.class, "none");
                    case -3:
                        return Messages.get(ChangeRegion.class, "vanilla");
                    default:
                        return Document.INTROS.pageTitle(ChangeRegion.REGION_KEYS[(int) value - 1]);
                }
            }
        }, Messages.get(ChangeRegion.class, "music"), 9);
        music.addChangeListener(() -> newValues[2] = (int) music.getValue());
        music.setSpinnerHeight(21);
        add(music);

        String customMusicLabel = Messages.get(ChangeRegion.class, "custom_music");
        customMusic = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, customMusicLabel) {
            {
                text.setHighlighting(false);
            }
            @Override
            protected void onClick() {
                List<String> audioFiles = CustomDungeonSaves.findAllFiles("ogg", "mp3", "wav");
                String[] options = new String[audioFiles.size() + 1];
                options[0] = Messages.get(ChangeRegion.class, "no_custom_music");
                int i = 1;
                for (String m : audioFiles) {
                    options[i++] = m;
                }
                EditorScene.show(new WndOptions(
                        Messages.get(ChangeRegion.class, "custom_music"),
                        Messages.get(ChangeRegion.class, "custom_music_info", CustomDungeonSaves.getAdditionalFilesDir().file().getAbsolutePath()),
                        options
                ) {
                    {
                        tfMessage.setHighlighting(false);
                    }

                    @Override
                    protected void onSelect(int index) {
                        super.onSelect(index);
                        if (index == 0) {
                            newCustomMusic = null;
                        } else {
                            newCustomMusic = options[index];
                        }
                        customMusic.text(customMusicLabel + "\n" + options[index]);
                    }
                });
            }
        };
        add(customMusic);
        if (oldCustomMusic != null) customMusic.text(customMusicLabel + "\n" + oldCustomMusic);
        else customMusic.text(customMusicLabel + "\n" + Messages.get(ChangeRegion.class, "no_custom_music"));

        outsideSp = new Component() {
            RedButton save, cancel;

            @Override
            protected void createChildren(Object... params) {

                save = new RedButton(Messages.get(ChangeRegion.class, "close")) {
                    @Override
                    protected void onClick() {
                        int newMusicVariant = (int) musicVariantSpinner.getValue();

                        onClose.run();
                        for (int i = 0; i < newValues.length; i++) {
                            if (newValues[i] != oldValues[i] || newMusicVariant != oldMusicVariant || !Objects.equals(oldCustomMusic, newCustomMusic)) {
                                f.setRegion(newValues[0]);
                                f.setWaterTexture(newValues[1]);
                                f.setMusic(newValues[2]);
                                f.musicVariant = newMusicVariant;
                                f.customMusic = newCustomMusic;
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

                height = ChooseOneInCategoriesBody.BUTTON_HEIGHT;
            }
        };

        musicVariantSpinner = new MusicVariantSpinner(oldMusicVariant);
        musicVariantSpinner.setSpinnerHeight(21);
        add(musicVariantSpinner);
    }

    @Override
    protected void layout() {
        height = 0;
        height = EditorUtilies.layoutStyledCompsInRectangles(2, width, PixelScene.landscape() ? 2 : 1, this, region, water, music, musicVariantSpinner) + 2;
        height = EditorUtilies.layoutStyledCompsInRectangles(2, width, 24, 1, this, customMusic);
    }

    public static Component createTitle() {
        RenderedTextBlock textBlock = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(LevelTab.class, "region")), 12);
        textBlock.hardlight(Window.TITLE_COLOR);
        return textBlock;
    }

    public Component getOutsideSp() {
        return outsideSp;
    }

    public static class MusicVariantSpinner extends StyledSpinner {


        public MusicVariantSpinner(int val) {
            super(new SpinnerIntegerModel(0, 3, val, 1, true, null) {
                {
                    setAbsoluteMaximum(3);
                }
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
                public float getInputFieldWidth(float height) {
                    return Spinner.FILL;
                }
            }, Messages.get(MusicVariantSpinner.class, "label"), 9);
        }
    }
}