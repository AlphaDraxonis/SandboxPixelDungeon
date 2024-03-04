package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.level;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndEditorSettings;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.MultiWindowTabComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledButtonWithIconAndText;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledCheckBox;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerFloatModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.StyledSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.impls.DepthSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ShadowCaster;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GnollSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.RectF;

public class LevelTab extends MultiWindowTabComp {

    //TODO:
    //boolean ignoreTerrainForScore
    //DecorationPainter

    @Override
    protected void createChildren(Object... params) {

        super.createChildren(params);

        title = new IconTitle(Icons.get(Icons.PREFS), Messages.get(this, "title"));
        add(title);

        StyledButton region;
        StyledButton mobSpawn;
        Spinner viewDistance, depth, shopPrice;
        StyledButton changeSize;
        StyledCheckBox hungerDepletion, naturalRegen, allowPickaxeMining, rememberLayout, magicMappingDisabled;
        StyledButton bossLevelRetexture;
        StyledButton levelColoring;

        final CustomLevel level = EditorScene.customLevel();

        region = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(this, "region"), 8) {
            @Override
            protected void onClick() {
                ChangeRegion changeRegion = new ChangeRegion(() -> closeCurrentSubMenu());
                changeContent(ChangeRegion.createTitle(), changeRegion, changeRegion.getOutsideSp());
            }
        };
        region.icon(Icons.get(Icons.CHANGES));
        content.add(region);

        mobSpawn = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(this, "mobs"), 8) {
            @Override
            protected void onClick() {
                MobSettings ms = new MobSettings();
                changeContent(ms.createTitle(), ms, ms.getOutsideSp(), -1f, 0f);
            }
        };
        mobSpawn.icon(new GnollSprite());
        content.add(mobSpawn);

        hungerDepletion = new StyledCheckBox(Chrome.Type.GREY_BUTTON_TR, Messages.get(this, "hunger")) {
            @Override
            public void checked(boolean value) {
                super.checked(value);
                level.levelScheme.hungerDepletion = value;
            }

            @Override
            protected int textSize() {
                return super.textSize() - 1;
            }
        };
        hungerDepletion.checked(level.levelScheme.hungerDepletion);
        hungerDepletion.icon(new ItemSprite(ItemSpriteSheet.RATION));
        content.add(hungerDepletion);

        naturalRegen = new StyledCheckBox(Chrome.Type.GREY_BUTTON_TR, Messages.get(this, "regeneration")) {
            @Override
            public void checked(boolean value) {
                super.checked(value);
                level.levelScheme.naturalRegeneration = value;
            }

            @Override
            protected int textSize() {
                return super.textSize() - 1;
            }
        };
        naturalRegen.checked(level.levelScheme.naturalRegeneration);
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
                level.levelScheme.allowPickaxeMining = value;
            }

            @Override
            protected int textSize() {
                return SPDSettings.language() == Languages.GERMAN ? 6 : super.textSize() - 1;
            }
        };
        allowPickaxeMining.checked(level.levelScheme.allowPickaxeMining);
        allowPickaxeMining.icon(new ItemSprite(ItemSpriteSheet.PICKAXE));
        content.add(allowPickaxeMining);

        rememberLayout = new StyledCheckBox(Messages.get(this, "remember_level_layout")) {
            @Override
            protected int textSize() {
                return super.textSize() - 1;
            }
        };
        Image icon = EditorUtilies.createSubIcon(ItemSpriteSheet.Icons.SCROLL_MAGICMAP);
        icon.scale.set(DungeonTilemap.SIZE / Math.max(icon.width(), icon.height()));
        rememberLayout.icon(icon);
        rememberLayout.checked(level.levelScheme.rememberLayout);
        rememberLayout.addChangeListener(v -> level.levelScheme.rememberLayout = v);
        content.add(rememberLayout);

        magicMappingDisabled = new StyledCheckBox(Messages.get(this, "magic_mapping_disabled")) {
            @Override
            protected int textSize() {
                return SPDSettings.language() == Languages.GERMAN ? 6 : super.textSize() - 1;
            }
        };
        icon = EditorUtilies.createSubIcon(ItemSpriteSheet.Icons.SCROLL_MAGICMAP);
        icon.scale.set(DungeonTilemap.SIZE / Math.max(icon.width(), icon.height()));
        magicMappingDisabled.icon(icon);
        magicMappingDisabled.checked(level.levelScheme.magicMappingDisabled);
        magicMappingDisabled.addChangeListener(v -> level.levelScheme.magicMappingDisabled = v);
        content.add(magicMappingDisabled);

        changeSize = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(ChangeMapSize.class, "title"), 8) {

            @Override
            protected void onClick() {
                ChangeMapSize changeMapSize = new ChangeMapSize(() -> closeCurrentSubMenu());
                changeContent(ChangeMapSize.createTitle(), changeMapSize, changeMapSize.getOutsideSp());
            }
        };
        changeSize.icon(Icons.RULER.get());
        content.add(changeSize);

        bossLevelRetexture = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(BossLevelRetexture.class, "title"), 8) {

            @Override
            protected void onClick() {
                BossLevelRetexture content = new BossLevelRetexture();
                changeContent(BossLevelRetexture.createTitle(), content, null);
            }
        };
//        bossLevelRetexture.icon(Icons.SKULL.get());
        content.add(bossLevelRetexture);


        viewDistance = new StyledSpinner(new SpinnerIntegerModel(1, ShadowCaster.MAX_DISTANCE, level.viewDistance, 1, false, null) {
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

        depth = new StyledSpinner(DepthSpinner.createModel(level.levelScheme.getDepth(), height -> (float) Spinner.FILL),
                DepthSpinner.createLabel(), 8);
        depth.addChangeListener(() -> level.levelScheme.setDepth((Integer) depth.getValue()));
        content.add(depth);

        shopPrice = new StyledSpinner(new SpinnerFloatModel(0.1f, 10f, level.levelScheme.getPriceMultiplier(),2,0.1f, false) {
            @Override
            public float getInputFieldWidth(float height) {
                return Spinner.FILL;
            }
        }, Messages.get(LevelTab.class, "shop_price"), 8);
        ((SpinnerIntegerModel) shopPrice.getModel()).setAbsoluteMinAndMax(0f, 10000f);
        shopPrice.addChangeListener(() -> level.levelScheme.setShopPriceMultiplier(((SpinnerFloatModel) shopPrice.getModel()).getAsFloat()));
        content.add(shopPrice);

        levelColoring = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(ChangeLevelColoring.class, "title"), 8) {
            @Override
            protected void onClick() {
                ChangeLevelColoring content = new ChangeLevelColoring(Dungeon.level.levelScheme);
                changeContent(ChangeLevelColoring.createTitle(), content, null);
            }
        };
        content.add(levelColoring);

        mainWindowComps = new Component[]{
                region, mobSpawn, changeSize,
                hungerDepletion, naturalRegen, allowPickaxeMining, EditorUtilies.PARAGRAPH_INDICATOR_INSTANCE,
                depth, viewDistance, shopPrice, rememberLayout, magicMappingDisabled, levelColoring, bossLevelRetexture
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