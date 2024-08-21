package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.level;

import com.badlogic.gdx.files.FileHandle;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndEditorSettings;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.luaeditor.IDEWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.MultiWindowTabComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledButtonWithIconAndText;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledCheckBox;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerFloatModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.StyledSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.impls.DepthSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ShadowCaster;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GnollSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.usercontent.ResourcePath;
import com.shatteredpixel.shatteredpixeldungeon.usercontent.UserContentManager;
import com.shatteredpixel.shatteredpixeldungeon.usercontent.blueprints.LuaLevelScript;
import com.shatteredpixel.shatteredpixeldungeon.usercontent.ui.CustomObjSelector;
import com.shatteredpixel.shatteredpixeldungeon.usercontent.ui.WndSelectResourceFile;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import com.watabou.NotAllowedInLua;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.RectF;

import java.io.IOException;
import java.util.Map;

@NotAllowedInLua
public class LevelTab extends MultiWindowTabComp {

    //TODO:
    //boolean ignoreTerrainForScore
    //DecorationPainter

    private CustomObjSelector<String> luaScriptPath;

    public LevelTab(final CustomLevel level, final LevelScheme levelScheme) {

        title = new IconTitle(Icons.get(Icons.PREFS), Messages.get(this, "title"));
        add(title);

        StyledButton region;
        StyledButton mobSpawn;
        Spinner viewDistance, depth, shopPrice;
        StyledButton changeSize;
        StyledSpinner hungerSpeed;
        StyledCheckBox naturalRegen, allowPickaxeMining, rememberLayout, magicMappingDisabled;
        StyledButton bossLevelRetexture;
        StyledButton levelColoring;

        region = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(this, "region"), 8) {
            @Override
            protected void onClick() {
                ChangeRegion changeRegion = new ChangeRegion(levelScheme, () -> closeCurrentSubMenu());
                changeContent(ChangeRegion.createTitle(), changeRegion, changeRegion.getOutsideSp());
            }
        };
        region.icon(Icons.get(Icons.CHANGES));
        content.add(region);

        if (level != null) {
            mobSpawn = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(this, "mobs"), 8) {
                @Override
                protected void onClick() {
                    MobSettings ms = new MobSettings();
                    changeContent(ms.createTitle(), ms, ms.getOutsideSp(), 0f, 0f);
                }
            };
            mobSpawn.icon(new GnollSprite());
            content.add(mobSpawn);
        } else mobSpawn = null;

        hungerSpeed = new StyledSpinner(new SpinnerFloatModel(0f, 100f, levelScheme.hungerSpeed, 2, 0.1f) {
            @Override
            protected String displayString(Object value) {
                return "x " + super.displayString(value);
            }

            @Override
            public float getInputFieldWidth(float height) {
                return Spinner.FILL;
            }
        }, Messages.get(this, "hunger_speed"), 8, new ItemSprite(ItemSpriteSheet.RATION));
        hungerSpeed.addChangeListener(() -> levelScheme.hungerSpeed = ((SpinnerFloatModel) hungerSpeed.getModel()).getAsFloat());
        content.add(hungerSpeed);

        naturalRegen = new StyledCheckBox(Chrome.Type.GREY_BUTTON_TR, Messages.get(this, "regeneration")) {
            @Override
            public void checked(boolean value) {
                super.checked(value);
                levelScheme.naturalRegeneration = value;
            }

            @Override
            protected int textSize() {
                return super.textSize() - 1;
            }
        };
        naturalRegen.checked(levelScheme.naturalRegeneration);
        RectF r = ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.POTION_HEALING);
        if (r != null) {
            Image icon = new Image(Assets.Sprites.ITEM_ICONS);
            icon.frame(r);
            icon.scale.set(10 / Math.max(icon.width(), icon.height()));
            naturalRegen.icon(icon);
        }
        content.add(naturalRegen);

        allowPickaxeMining = new StyledCheckBox(Chrome.Type.GREY_BUTTON_TR, Messages.get(this, "mining")) {
            @Override
            public void checked(boolean value) {
                super.checked(value);
                levelScheme.allowPickaxeMining = value;
            }

            @Override
            protected int textSize() {
                return SPDSettings.language() == Languages.GERMAN ? 6 : super.textSize() - 1;
            }
        };
        allowPickaxeMining.checked(levelScheme.allowPickaxeMining);
        allowPickaxeMining.icon(new ItemSprite(ItemSpriteSheet.PICKAXE));
        content.add(allowPickaxeMining);

        rememberLayout = new StyledCheckBox(Messages.get(this, "remember_level_layout")) {
            @Override
            protected int textSize() {
                return super.textSize() - 1;
            }
        };
        Image icon = EditorUtilities.createSubIcon(ItemSpriteSheet.Icons.SCROLL_MAGICMAP);
        icon.scale.set(DungeonTilemap.SIZE / Math.max(icon.width(), icon.height()));
        rememberLayout.icon(icon);
        rememberLayout.checked(levelScheme.rememberLayout);
        rememberLayout.addChangeListener(v -> levelScheme.rememberLayout = v);
        content.add(rememberLayout);

        magicMappingDisabled = new StyledCheckBox(Messages.get(this, "magic_mapping_disabled")) {
            @Override
            protected int textSize() {
                return SPDSettings.language() == Languages.GERMAN ? 6 : super.textSize() - 1;
            }
        };
        icon = EditorUtilities.createSubIcon(ItemSpriteSheet.Icons.SCROLL_MAGICMAP);
        icon.scale.set(DungeonTilemap.SIZE / Math.max(icon.width(), icon.height()));
        magicMappingDisabled.icon(icon);
        magicMappingDisabled.checked(levelScheme.magicMappingDisabled);
        magicMappingDisabled.addChangeListener(v -> levelScheme.magicMappingDisabled = v);
        content.add(magicMappingDisabled);

        if (level != null) {
            changeSize = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(ChangeMapSize.class, "title"), 8) {

                @Override
                protected void onClick() {
                    ChangeMapSize changeMapSize = new ChangeMapSize(() -> closeCurrentSubMenu());
                    changeContent(ChangeMapSize.createTitle(), changeMapSize, changeMapSize.getOutsideSp());
                }
            };
            changeSize.icon(Icons.RULER.get());
            content.add(changeSize);
        } else changeSize = null;

        if (level != null) {
            bossLevelRetexture = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(BossLevelRetexture.class, "title"), 8) {

                @Override
                protected void onClick() {
                    BossLevelRetexture content = new BossLevelRetexture();
                    changeContent(BossLevelRetexture.createTitle(), content, null);
                }
            };
//          bossLevelRetexture.icon(Icons.SKULL.get());
            content.add(bossLevelRetexture);
        } else bossLevelRetexture = null;


        if (level != null) {
            viewDistance = new StyledSpinner(new SpinnerIntegerModel(1, ShadowCaster.MAX_DISTANCE, level.viewDistance) {
                {
                    setAbsoluteMaximum(ShadowCaster.MAX_DISTANCE);
                }

                @Override
                public float getInputFieldWidth(float height) {
                    return Spinner.FILL;
                }
            }, Messages.get(LevelTab.class, "view_distance"), 8);
            viewDistance.addChangeListener(() -> level.viewDistance = (int) viewDistance.getValue());
            content.add(viewDistance);
        } else viewDistance = null;

        depth = new StyledSpinner(DepthSpinner.createModel(levelScheme.getDepth(), height -> (float) Spinner.FILL),
                DepthSpinner.createLabel(), 8);
        depth.addChangeListener(() -> levelScheme.setDepth((Integer) depth.getValue()));
        content.add(depth);

        shopPrice = new StyledSpinner(new SpinnerFloatModel(0.1f, 10f, levelScheme.shopPriceMultiplier,2,0.1f) {
            @Override
            public float getInputFieldWidth(float height) {
                return Spinner.FILL;
            }
        }, Messages.get(LevelTab.class, "shop_price"), 8);
        ((SpinnerIntegerModel) shopPrice.getModel()).setAbsoluteMinAndMax(0f, 10000f);
        shopPrice.addChangeListener(() -> levelScheme.shopPriceMultiplier = ((SpinnerFloatModel) shopPrice.getModel()).getAsFloat());
        content.add(shopPrice);

        levelColoring = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(ChangeLevelColoring.class, "title"), 8) {
            @Override
            protected void onClick() {
                ChangeLevelColoring content = new ChangeLevelColoring(levelScheme);
                changeContent(ChangeLevelColoring.createTitle(), content, null);
            }
        };
        content.add(levelColoring);

        LuaLevelScript lco;
        if (levelScheme.luaScriptID == 0) {
            lco = UserContentManager.createNewCustomObject(LuaLevelScript.class, "", levelScheme.getType().getName());
            levelScheme.luaScriptID = lco.getIdentifier();
        } else {
            lco = UserContentManager.getUserContent(levelScheme.luaScriptID, LuaLevelScript.class);
        }

        luaScriptPath = new CustomObjSelector<String>("Lua Script Path, ", new CustomObjSelector.Selector<String>() {

            @Override
            public String getCurrentValue() {
                return lco.getLuaScriptPath();
            }

            @Override
            public void onSelect(String path) {
                lco.setLuaScriptPath(path);
            }

            @Override
            public void onItemSlotClick() {
                IDEWindow.showWindow(lco, luaScriptPath, lco.getLuaTargetClass());
            }

            @Override
            public void onChangeClick() {
                DungeonScene.show(new WndSelectResourceFile() {
                    @Override
                    protected boolean acceptExtension(String extension) {
                        return ResourcePath.isLua(extension);
                    }

                    @Override
                    protected void onSelect(Map.Entry<String, FileHandle> path) {
                        luaScriptPath.setValue(path.getKey());
                    }
                });
            }
        }) {
            @Override
            protected void onChangeClick() {
                IDEWindow.showSelectScriptWindow(levelScheme.getType(), script -> {
                    if (script != null) {
                        luaScriptPath.setValue(script.getPath());
                    }
                });
            }

            @Override
            public synchronized void destroy() {
                super.destroy();
                if (lco.getLuaScriptPath() == null) {
                    CustomDungeonSaves.deleteUserContent(
                            UserContentManager.getUserContent(levelScheme.luaScriptID, null)
                    );
                    levelScheme.luaScriptID = 0;
                } else {
					try {
						CustomDungeonSaves.storeUserContent(lco);
					} catch (IOException e) {
                        DungeonScene.show(new WndError(e));
					}
				}
            }
        };
        luaScriptPath.enableChanging(true);
        luaScriptPath.enableDetaching(true);
        content.add(luaScriptPath);

        mainWindowComps = new Component[]{
                region, mobSpawn, changeSize,
                hungerSpeed, naturalRegen, allowPickaxeMining, EditorUtilities.PARAGRAPH_INDICATOR_INSTANCE,
                depth, viewDistance, shopPrice, PixelScene.landscape() && viewDistance == null ? levelColoring : null, rememberLayout, magicMappingDisabled, PixelScene.landscape() && viewDistance == null ? null : levelColoring, bossLevelRetexture
                ,luaScriptPath
        };
    }


    public static void updateLayout() {
        WndEditorSettings.getInstance().getLevelTab().layout();
    }

    @Override
    public Image createIcon() {
        return Icons.get(Icons.PREFS);
    }

    @Override
    public String hoverText() {
        return Messages.get(LevelTab.class, "title");
    }

}