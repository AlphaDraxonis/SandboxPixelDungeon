package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.dungeon;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndEditorSettings;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.MultiWindowTabComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledButtonWithIconAndText;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.effects.BadgeBanner;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class DungeonTab extends MultiWindowTabComp {

    @Override
    protected void createChildren(Object... params) {

        super.createChildren(params);

        title = new IconTitle(Icons.get(Icons.PREFS), Messages.get(DungeonTab.class, "title"));
        add(title);

        StyledButton potionColors, scrollRunes, ringGems;
        StyledButton heroes, durationSettings;

        potionColors = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(DungeonTab.class, "set_pot"), 7) {
            @Override
            protected void onClick() {
                SetPotionScrollRingType change = SetPotionScrollRingType.createPotionWnd(() -> closeCurrentSubMenu());
                changeContent(SetPotionScrollRingType.createTitle(Messages.get(SetPotionScrollRingType.class, "title_potion")),
                        change, change.getOutsideSp());
            }
        };
        potionColors.icon(new ItemSprite(ItemSpriteSheet.POTION_AMBER));
        content.add(potionColors);

        scrollRunes = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(DungeonTab.class, "set_scroll"), 7) {
            @Override
            protected void onClick() {
                SetPotionScrollRingType change = SetPotionScrollRingType.createScrollWnd(() -> closeCurrentSubMenu());
                changeContent(SetPotionScrollRingType.createTitle(Messages.get(SetPotionScrollRingType.class, "title_scroll")),
                        change, change.getOutsideSp());
            }
        };
        scrollRunes.icon(new ItemSprite(ItemSpriteSheet.SCROLL_BERKANAN));
        content.add(scrollRunes);

        ringGems = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(DungeonTab.class, "set_ring"), 7) {
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

        mainWindowComps = new Component[]{potionColors, scrollRunes, ringGems,EditorUtilies.PARAGRAPH_INDICATOR_INSTANCE,
                heroes, durationSettings
        };
    }


    public static void updateLayout() {
        WndEditorSettings.getInstance().getDungeonTab().layout();
    }

    @Override
    public Image createIcon() {
        return new ItemSprite(ItemSpriteSheet.KIT);
    }

}