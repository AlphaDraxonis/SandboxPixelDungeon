package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.dungeon;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndEditorSettings;
import com.shatteredpixel.shatteredpixeldungeon.editor.recipes.CustomRecipeList;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.MultiWindowTabComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledButtonWithIconAndText;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledCheckBox;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.effects.BadgeBanner;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndChallenges;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class DungeonTab extends MultiWindowTabComp {

    @Override
    protected void createChildren(Object... params) {

        super.createChildren(params);

        title = new IconTitle(Icons.get(Icons.PREFS), Messages.get(DungeonTab.class, "title"));
        add(title);

        StyledButton potionColors, scrollRunes, ringGems;
        StyledButton heroes, durationSettings, forceChallenges, customRecipes;
        StyledCheckBox view2d, seeLevelOnDeath, autoRevealSecrets;

        potionColors = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(DungeonTab.class, "set_pot"), 8) {
            @Override
            protected void onClick() {
                SetPotionScrollRingType change = SetPotionScrollRingType.createPotionWnd(() -> closeCurrentSubMenu());
                changeContent(SetPotionScrollRingType.createTitle(Messages.get(SetPotionScrollRingType.class, "title_potion")),
                        change, change.getOutsideSp());
            }
        };
        potionColors.icon(new ItemSprite(ItemSpriteSheet.POTION_AMBER));
        content.add(potionColors);

        scrollRunes = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(DungeonTab.class, "set_scroll"), 8) {
            @Override
            protected void onClick() {
                SetPotionScrollRingType change = SetPotionScrollRingType.createScrollWnd(() -> closeCurrentSubMenu());
                changeContent(SetPotionScrollRingType.createTitle(Messages.get(SetPotionScrollRingType.class, "title_scroll")),
                        change, change.getOutsideSp());
            }
        };
        scrollRunes.icon(new ItemSprite(ItemSpriteSheet.SCROLL_BERKANAN));
        content.add(scrollRunes);

        ringGems = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(DungeonTab.class, "set_ring"), 8) {
            @Override
            protected void onClick() {
                SetPotionScrollRingType change = SetPotionScrollRingType.createRingWnd(() -> closeCurrentSubMenu());
                changeContent(SetPotionScrollRingType.createTitle(Messages.get(SetPotionScrollRingType.class, "title_ring")),
                        change, change.getOutsideSp());
            }
        };
        ringGems.icon(new ItemSprite(ItemSpriteSheet.RING_AMETHYST));
        content.add(ringGems);

        heroes = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(DungeonTab.class, "heroes"), 8) {
            @Override
            protected void onClick() {
                HeroSettings heroSettings = new HeroSettings();
                changeContent(heroSettings.createTitle(), heroSettings, heroSettings.getOutsideSp(), 0.5f, 0f);
            }
        };
        heroes.icon(BadgeBanner.image(0));
        content.add(heroes);

        durationSettings = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(DungeonTab.class, "effects"), 8){
            @Override
            protected void onClick() {
                DurationSettings ds = new DurationSettings();
                changeContent(ds.createTitle(), ds, ds.getOutsideSp(), 0f, 0.5f);
            }
        };
        durationSettings.icon(new ItemSprite(ItemSpriteSheet.POTION_JADE));
        content.add(durationSettings);

        forceChallenges = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(WndForceChallenges.class, "title"), 8){
            @Override
            protected void onClick() {
                int forceChallenges = Dungeon.customDungeon.forceChallenges;//prevent cbs from being disabled
                Dungeon.customDungeon.forceChallenges = 0;
                EditorScene.show(new WndForceChallenges(forceChallenges));
                Dungeon.customDungeon.forceChallenges = forceChallenges;
            }
        };
        forceChallenges.icon(Icons.CHALLENGE_ON.get());
        content.add(forceChallenges);

        customRecipes = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(DungeonTab.class, "custom_recipes"), 8){
            @Override
            protected void onClick() {
                CustomRecipeList crl = new CustomRecipeList();
                changeContent(crl.createTitle(), crl, crl.getOutsideSp(), 0f, 0.5f);
            }
        };
        customRecipes.icon(new ItemSprite(EditorScene.customLevel().tilesTex(), new TileItem(Terrain.ALCHEMY, -1)));
        content.add(customRecipes);

        view2d = new StyledCheckBox(Messages.get(DungeonTab.class, "enable_2d")){
            @Override
            public void checked(boolean value) {
                super.checked(value);
                Dungeon.customDungeon.view2d = value;
            }

            @Override
            protected int textSize() {
                return 8;
            }
        };
        view2d.checked(Dungeon.customDungeon.view2d);
        content.add(view2d);

        seeLevelOnDeath = new StyledCheckBox(Messages.get(DungeonTab.class, "see_level_on_death")){
            @Override
            public void checked(boolean value) {
                super.checked(value);
                Dungeon.customDungeon.seeLevelOnDeath = value;
            }

            @Override
            protected int textSize() {
                return 8;
            }
        };
        seeLevelOnDeath.checked(Dungeon.customDungeon.seeLevelOnDeath);
        content.add(seeLevelOnDeath);

        autoRevealSecrets = new StyledCheckBox(Messages.get(DungeonTab.class, "reveal_secrets")){
            @Override
            public void checked(boolean value) {
                super.checked(value);
                Dungeon.customDungeon.notRevealSecrets = !value;
            }

            @Override
            protected int textSize() {
                return 8;
            }
        };
        autoRevealSecrets.checked(!Dungeon.customDungeon.notRevealSecrets);
        content.add(autoRevealSecrets);

        mainWindowComps = new Component[]{potionColors, scrollRunes, ringGems, EditorUtilies.PARAGRAPH_INDICATOR_INSTANCE,
                heroes, durationSettings, forceChallenges, customRecipes, view2d, seeLevelOnDeath, autoRevealSecrets
        };
    }


    public static void updateLayout() {
        WndEditorSettings.getInstance().getDungeonTab().layout();
    }

    @Override
    public Image createIcon() {
        return new ItemSprite(ItemSpriteSheet.KIT);
    }

    @Override
    public String hoverText() {
        return Messages.get(DungeonTab.class, "title");
    }

    private static class WndForceChallenges extends WndChallenges {

        public WndForceChallenges(int checked) {
            super(checked, true);
        }

        public void onBackPressed() {
            hide();
            Dungeon.customDungeon.forceChallenges = getSelectedValues();
        }
    }

}