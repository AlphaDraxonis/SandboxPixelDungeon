package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.dungeon;

import com.badlogic.gdx.files.FileHandle;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.ResourcePath;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.ui.CustomObjSelector;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.ui.WndAllCustomObjects;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.ui.WndSelectResourceFile;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.TileSprite;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.LuaOverviewTab;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndEditorSettings;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.DungeonScript;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.luaeditor.IDEWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.recipes.CustomRecipeList;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.MultiWindowTabComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledButtonWithIconAndText;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledCheckBox;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.effects.BadgeBanner;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndChallenges;
import com.watabou.NotAllowedInLua;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

import java.util.Map;

@NotAllowedInLua
public class DungeonTab extends MultiWindowTabComp {
    
    private final CustomObjSelector<String> dungeonScript;
    
    public DungeonTab() {

        title = new IconTitle(Icons.get(Icons.PREFS), Messages.get(DungeonTab.class, "title"));
        add(title);

        StyledButton potionColors, scrollRunes, ringGems;
        StyledButton heroes, durationSettings, forceChallenges, customRecipes;
        StyledCheckBox view2d, seeLevelOnDeath, autoRevealSecrets;
        StyledButton viewCustomObjects;

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

        scrollRunes = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(DungeonTab.class, "set_scroll"), SPDSettings.language() == Languages.GERMAN ? 7 : 8) {
            @Override
            protected void onClick() {
                SetPotionScrollRingType change = SetPotionScrollRingType.createScrollWnd(() -> closeCurrentSubMenu());
                changeContent(SetPotionScrollRingType.createTitle(Messages.get(SetPotionScrollRingType.class, "title_scroll")),
                        change, change.getOutsideSp());
            }
        };
        scrollRunes.icon(new ItemSprite(ItemSpriteSheet.SCROLL_BERKANAN));
        content.add(scrollRunes);

        ringGems = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(DungeonTab.class, "set_ring"), SPDSettings.language() == Languages.GERMAN ? 7 : 8) {
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

        forceChallenges = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(WndForceChallenges.class, "title"), SPDSettings.language() == Languages.GERMAN ? 7 : 8){
            @Override
            protected void onClick() {
                int forceChallenges = Dungeon.customDungeon.forceChallenges;//prevent cbs from being disabled
                Dungeon.customDungeon.forceChallenges = 0;
                EditorScene.show(new WndForceChallenges(forceChallenges));
                Dungeon.customDungeon.forceChallenges = forceChallenges;
            }
        };
        forceChallenges.icon(Icons.CHALLENGE_COLOR.get());
        content.add(forceChallenges);

        customRecipes = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(DungeonTab.class, "custom_recipes"), PixelScene.landscape() ? 8 : 6){
            @Override
            protected void onClick() {
                CustomRecipeList crl = new CustomRecipeList();
                changeContent(crl.createTitle(), crl, crl.getOutsideSp(), 0f, 0.5f);
            }
        };
        customRecipes.icon(new TileSprite(Terrain.ALCHEMY));
        content.add(customRecipes);

        view2d = new StyledCheckBox(Messages.get(DungeonTab.class, "enable_2d")) {
            @Override
            protected int textSize() {
                return 8;
            }
        };
        view2d.checked(Dungeon.customDungeon.view2d);
        view2d.addChangeListener(v -> Dungeon.customDungeon.view2d = v);
        content.add(view2d);

        seeLevelOnDeath = new StyledCheckBox(Messages.get(DungeonTab.class, "see_level_on_death")) {
            @Override
            protected int textSize() {
                return super.textSize() - 1;
            }
        };
        seeLevelOnDeath.checked(Dungeon.customDungeon.seeLevelOnDeath);
        seeLevelOnDeath.addChangeListener(v -> Dungeon.customDungeon.seeLevelOnDeath = v);
        content.add(seeLevelOnDeath);

        autoRevealSecrets = new StyledCheckBox(Messages.get(DungeonTab.class, "reveal_secrets")) {
            @Override
            protected int textSize() {
                return SPDSettings.language() == Languages.GERMAN ? 7 : 8;
            }
        };
        autoRevealSecrets.checked(!Dungeon.customDungeon.notRevealSecrets);
        autoRevealSecrets.addChangeListener(v -> Dungeon.customDungeon.notRevealSecrets = !v);
        content.add(autoRevealSecrets);

        viewCustomObjects = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(DungeonTab.class, "custom_object_overview"), PixelScene.landscape() ? 8 : 6){
            @Override
            protected void onClick() {
                DungeonScene.show(new WndAllCustomObjects());
            }
        };
        content.add(viewCustomObjects);
        
        dungeonScript = new CustomObjSelector<String>(Messages.get(LuaOverviewTab.class, "dungeon_script_title"), new CustomObjSelector.Selector<String>() {
            
            @Override
            public String getCurrentValue() {
                return Dungeon.customDungeon.dungeonScriptPath;
            }
            
            @Override
            public void onSelect(String path) {
                Dungeon.customDungeon.dungeonScriptPath = path;
            }
            
            @Override
            public void onItemSlotClick() {
                IDEWindow.showWindow(Dungeon.customDungeon.dungeonScriptPath, dungeonScript::setValue, DungeonScript.class);
            }
            
            @Override
            public void onChangeClick() {
                DungeonScene.show(new WndSelectResourceFile(null, null, false) {
                    @Override
                    protected boolean acceptExtension(String extension) {
                        return ResourcePath.isLua(extension);
                    }
                    
                    @Override
                    protected void onSelect(Map.Entry<String, FileHandle> path) {
                        dungeonScript.setValue(path.getKey());
                    }
                });
            }
        }, PixelScene.landscape() ? 8 : 6) {
            @Override
            protected void onChangeClick() {
                IDEWindow.showSelectScriptWindow(DungeonScript.class, script -> {
                    if (script != null) {
                        dungeonScript.setValue(script.getPath());
                    }
                });
            }
        };
        dungeonScript.enableChanging(true);
        dungeonScript.enableDetaching(true);
        content.add(dungeonScript);
        

        mainWindowComps = new Component[]{potionColors, scrollRunes, ringGems, EditorUtilities.PARAGRAPH_INDICATOR_INSTANCE,
                heroes, durationSettings, forceChallenges, customRecipes, view2d, seeLevelOnDeath, autoRevealSecrets, viewCustomObjects, dungeonScript
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
