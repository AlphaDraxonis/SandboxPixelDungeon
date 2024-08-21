package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.level;

import com.badlogic.gdx.files.FileHandle;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.TileSprite;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ChooseOneInCategoriesBody;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledButtonWithIconAndText;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerTextIconModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.StyledSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.NotAllowedInLua;
import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@NotAllowedInLua
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

    protected StyledButton customRegion, customWater;

    private final Component outsideSp;

    public ChangeRegion(LevelScheme levelScheme, Runnable onClose) {
        super();

        Object[] oldValues = {
                levelScheme.getRegion(),
                levelScheme.waterTexture,
                levelScheme.musicRegion,
                levelScheme.musicFile,

                levelScheme.customTilesTex,
                levelScheme.customWaterTex
        };

        Object[] newValues = new Object[oldValues.length];
        for (int i = 0; i < oldValues.length; i++) {
            newValues[i] = oldValues[i];
        }

        region = new StyledSpinner(new SpinnerTextIconModel(true, ((int) oldValues[0]) - 1, REGION_DATA) {
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

        water = new StyledSpinner(new SpinnerTextIconModel(true, ((int) oldValues[1]), WATER_DATA) {
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
        water.addChangeListener(() -> newValues[1] = water.getValue());
        water.setSpinnerHeight(21);
        add(water);

        region.addChangeListener(() -> {
            newValues[0] = region.getValue();
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
                            newValues[2] = music;
                            newValues[3] = null;
                        }
                        if (music instanceof String) {
                            newValues[3] = music;
                        }
                        text(musicLabel + "\n" + WndSelectMusic.getDisplayName(newValues[3] == null ? newValues[2] : newValues[3]));
                    }
                });
            }
        };
        add(music);
        music.text(musicLabel + "\n" + WndSelectMusic.getDisplayName(newValues[3] == null ? newValues[2] : newValues[3]));


        String customRegionLabel = Messages.get(ChangeRegion.class, "custom_tilesheet");
        customRegion = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, customRegionLabel) {
            {
                text.setHighlighting(false);
            }
            @Override
            protected void onClick() {
                List<String> imgFiles = new ArrayList<>(8);

                Set<FileHandle> files = CustomDungeonSaves.findAllFiles("png");
                if (files != null) {
                    String rootDir = CustomDungeonSaves.getAdditionalFilesDir().path() + "/";
                    for (FileHandle f : files) {
                        String rawPath = f.path().replaceFirst(Pattern.quote(rootDir), "");
                        SmartTexture tx = TextureCache.get(TextureCache.EXTERNAL_ASSET_PREFIX + CustomDungeonSaves.getExternalFilePath(rawPath));
                        if (tx != null && tx.width == 256 && tx.height == 256) {
                            imgFiles.add(rawPath);
                        }
                    }
                    Collections.sort(imgFiles);
                }

                String[] options = new String[imgFiles.size() + 2];
                int i = 0;
                options[i++] = Messages.get(ChangeRegion.class, "view_game_assets");
                options[i++] = Messages.get(ChangeRegion.class, "no_custom_spritesheet");
                for (String m : imgFiles) {
                    options[i++] = m;
                }
                EditorScene.show(new WndOptions(
                        Messages.titleCase(Messages.get(ChangeRegion.class, "custom_tilesheet")),
                        Messages.get(ChangeRegion.class, "custom_tilesheet_info", CustomDungeonSaves.getAdditionalFilesDir().file().getAbsolutePath()),
                        options
                ) {
                    {
                        tfMessage.setHighlighting(false);
                    }

                    @Override
                    protected Image getIcon(int index) {
                        if (index < 2) {
                            return new Image();
                        }
                        Image img = new Image(TextureCache.getFromCurrentSavePath(CustomDungeonSaves.getExternalFilePath(options[index])));
                        img.scale.set(ItemSpriteSheet.SIZE / Math.max(img.width, img.height));
                        return img;
                    }

                    @Override
                    protected void onSelect(int index) {
                        super.onSelect(index);
                        if (index == 0) {
                            Game.platform.openURI("https://github.com/AlphaDraxonis/SandboxPixelDungeon/tree/master/core/src/main/assets/environment");
                            return;
                        }
                        if (index == 1) {
                            newValues[4] = null;
                        } else {
                            newValues[4] = options[index];
                        }
                        customRegion.text(customRegionLabel + "\n" + (newValues[4] == null ? Messages.get(ChangeRegion.class, "no_custom_spritesheet") : newValues[4]));
                    }
                });
            }
        };
        add(customRegion);
        customRegion.text(customRegionLabel + "\n" + (newValues[4] == null ? Messages.get(ChangeRegion.class, "no_custom_spritesheet") : newValues[4]));

        String customWaterLabel = Messages.get(ChangeRegion.class, "custom_watersheet");
        customWater = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, customWaterLabel) {
            {
                text.setHighlighting(false);
            }
            @Override
            protected void onClick() {
                List<String> imgFiles = new ArrayList<>(8);

                Set<FileHandle> files = CustomDungeonSaves.findAllFiles("png");
                if (files != null) {
                    String rootDir = CustomDungeonSaves.getAdditionalFilesDir().path() + "/";
                    for (FileHandle f : files) {
                        String rawPath = f.path().replaceFirst(Pattern.quote(rootDir), "");
                        SmartTexture tx = TextureCache.get(TextureCache.EXTERNAL_ASSET_PREFIX + CustomDungeonSaves.getExternalFilePath(rawPath));
                        if (tx != null && tx.width == 32 && tx.height == 32) {
                            imgFiles.add(rawPath);
                        }
                    }
                    Collections.sort(imgFiles);
                }

                String[] options = new String[imgFiles.size() + 2];
                int i = 0;
                options[i++] = Messages.get(ChangeRegion.class, "view_game_assets");
                options[i++] = Messages.get(ChangeRegion.class, "no_custom_spritesheet");
                for (String m : imgFiles) {
                    options[i++] = m;
                }
                EditorScene.show(new WndOptions(
                        Messages.titleCase(Messages.get(ChangeRegion.class, "custom_watersheet")),
                        Messages.get(ChangeRegion.class, "custom_watersheet_info", CustomDungeonSaves.getAdditionalFilesDir().file().getAbsolutePath()),
                        options
                ) {
                    {
                        tfMessage.setHighlighting(false);
                    }

                    @Override
                    protected Image getIcon(int index) {
                        if (index < 2) {
                            return new Image();
                        }
                        Image img = new Image(TextureCache.getFromCurrentSavePath(CustomDungeonSaves.getExternalFilePath(options[index])));
                        img.scale.set(ItemSpriteSheet.SIZE / Math.max(img.width, img.height));
                        return img;
                    }

                    @Override
                    protected void onSelect(int index) {
                        super.onSelect(index);
                        if (index == 0) {
                            Game.platform.openURI("https://github.com/AlphaDraxonis/SandboxPixelDungeon/tree/master/core/src/main/assets/environment");
                            return;
                        }
                        if (index == 1) {
                            newValues[5] = null;
                        } else {
                            newValues[5] = options[index];
                        }
                        customWater.text(customWaterLabel + "\n" + (newValues[5] == null ? Messages.get(ChangeRegion.class, "no_custom_spritesheet") : newValues[5]));
                    }
                });
            }
        };
        add(customWater);
        customWater.text(customWaterLabel + "\n" + (newValues[5] == null ? Messages.get(ChangeRegion.class, "no_custom_spritesheet") : newValues[5]));

        outsideSp = new Component() {
            RedButton save, cancel;

            @Override
            protected void createChildren() {

                save = new RedButton(Messages.get(ChangeRegion.class, "close")) {
                    @Override
                    protected void onClick() {

                        onClose.run();
                        for (int i = 0; i < newValues.length; i++) {
                            if (newValues[i] != oldValues[i]) {
                                levelScheme.setRegion((int) newValues[0]);
                                levelScheme.waterTexture = (int) newValues[1];
                                levelScheme.musicRegion = (int) newValues[2];
                                levelScheme.musicFile = (String) newValues[3];
                                levelScheme.customTilesTex = (String) newValues[4];
                                levelScheme.customWaterTex = (String) newValues[5];
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
        height = EditorUtilities.layoutStyledCompsInRectangles(2, width, PixelScene.landscape() ? 2 : 1, this, region, water) + 2;
        height = EditorUtilities.layoutStyledCompsInRectangles(2, width, 24, 1, this, music);

        height += 10;
        height = EditorUtilities.layoutStyledCompsInRectangles(2, width, PixelScene.landscape() ? 2 : 1, this, customRegion, customWater);
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