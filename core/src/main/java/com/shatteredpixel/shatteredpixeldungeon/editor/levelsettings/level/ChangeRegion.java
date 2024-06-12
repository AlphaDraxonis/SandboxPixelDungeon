package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.level;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.TileSprite;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ChooseOneInCategoriesBody;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledButtonWithIconAndText;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerTextIconModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.StyledSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

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
            LevelScheme.REGION_HALLS
    };

    protected StyledSpinner region, water;

    protected StyledButton music;

    private final Component outsideSp;

    private String newMusicFile;

    public ChangeRegion(Runnable onClose) {
        super();

        CustomLevel f = EditorScene.getCustomLevel();

        int[] oldValues = {
                f.getRegionValue(),
                f.getWaterTextureValue(),
                f.musicRegion,
        };
        String oldMusic = f.musicFile;
        int[] newValues = {
                oldValues[0],
                oldValues[1],
                oldValues[2]
        };
        newMusicFile = oldMusic;

        region = new StyledSpinner(new SpinnerTextIconModel(true, oldValues[0] - 1, REGION_DATA) {
            @Override
            protected Image getIcon(Object value) {
                return new TileSprite(CustomLevel.tilesTex((int) value, false), Terrain.EMPTY);
            }

            @Override
            protected String displayString(Object value) {
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
            protected String displayString(Object value) {
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

        String musicLabel = Messages.get(ChangeRegion.class, "music");
        music = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, musicLabel) {
            {
                text.setHighlighting(false);
            }
            @Override
            protected void onClick() {
                EditorScene.show(new WndSelectMusic(WndSelectMusic.TypeOfFirstCategory.REGION_MUSIC) {
                    @Override
                    protected void onSelect(Object music) {
                        super.onSelect(music);
                        if (music instanceof Integer) {
                            newValues[2] = (int) music;
                            newMusicFile = null;
                        }
                        if (music instanceof String) {
                            newMusicFile = (String) music;
                        }
                        text(musicLabel + "\n" + WndSelectMusic.getDisplayName(newMusicFile == null ? newValues[2] : newMusicFile));
                    }
                });
            }
        };
        add(music);
        music.text(musicLabel + "\n" + WndSelectMusic.getDisplayName(newMusicFile == null ? newValues[2] : newMusicFile));

        outsideSp = new Component() {
            RedButton save, cancel;

            @Override
            protected void createChildren(Object... params) {

                save = new RedButton(Messages.get(ChangeRegion.class, "close")) {
                    @Override
                    protected void onClick() {

                        onClose.run();
                        for (int i = 0; i < newValues.length; i++) {
                            if (newValues[i] != oldValues[i] || !Objects.equals(oldMusic, newMusicFile)) {
                                f.setRegion(newValues[0]);
                                f.setWaterTexture(newValues[1]);
                                f.musicRegion = newValues[2];
                                f.musicFile = newMusicFile;
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
    }

    @Override
    protected void layout() {
        height = 0;
        height = EditorUtilies.layoutStyledCompsInRectangles(2, width, PixelScene.landscape() ? 2 : 1, this, region, water) + 2;
        height = EditorUtilies.layoutStyledCompsInRectangles(2, width, 24, 1, this, music);
    }

    public static Component createTitle() {
        RenderedTextBlock textBlock = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(LevelTab.class, "region")), 12);
        textBlock.hardlight(Window.TITLE_COLOR);
        return textBlock;
    }

    public Component getOutsideSp() {
        return outsideSp;
    }
}