package com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.general;

import com.alphadraxonis.sandboxpixeldungeon.Assets;
import com.alphadraxonis.sandboxpixeldungeon.Chrome;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomLevel;
import com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.WndEditorSettings;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.StyledButtonWithIconAndText;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.StyledCheckbox;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.Spinner;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.StyledSpinner;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.impls.DepthSpinner;
import com.alphadraxonis.sandboxpixeldungeon.editor.util.EditorUtilies;
import com.alphadraxonis.sandboxpixeldungeon.effects.BadgeBanner;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.sprites.GnollSprite;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSprite;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSpriteSheet;
import com.alphadraxonis.sandboxpixeldungeon.ui.IconButton;
import com.alphadraxonis.sandboxpixeldungeon.ui.Icons;
import com.alphadraxonis.sandboxpixeldungeon.ui.ScrollPane;
import com.alphadraxonis.sandboxpixeldungeon.ui.StyledButton;
import com.alphadraxonis.sandboxpixeldungeon.windows.IconTitle;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndKeyBindings;
import com.watabou.input.GameAction;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.RectF;

public class GeneralTab extends WndEditorSettings.TabComp {

    public static final int GAP = 2, BIG_GAP = GAP * 3, BUTTON_HEIGHT = 18;

    //TODO:
    //boolean ignoreTerrainForScore (floor only)
    //DecorationPainter (floor only)

    private boolean layoutOwnMenu = true;
    private Component otherTitle, otherBody, outsideSp;
    private ScrollPane spForOtherBody;
    private float alignmentOther, titleAlignmentOther;
    private ButtonBack buttonBack;

    private IconTitle title;
    private ScrollPane sp;
    private Component content;

    private StyledButton potionColors, scrollRunes, ringGems;
    private StyledButton region;
    private StyledButton mobSpawn;
    private Spinner viewDistance, depth, shopPrice;
    private StyledButton changeSize;
    private StyledButton heroes;
    private StyledCheckbox hungerDepletion, naturalRegen;


    @Override
    protected void createChildren(Object... params) {

        title = new IconTitle(Icons.get(Icons.PREFS), Messages.get(GeneralTab.class, "title"));
        add(title);

        content = new Component();
        sp = new ScrollPane(content);

        final CustomLevel level = EditorScene.customLevel();

        potionColors = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(GeneralTab.class, "set_pot"), 7) {
            @Override
            protected void onClick() {
                SetPotionScrollRingType change = SetPotionScrollRingType.createPotionWnd(() -> closeCurrentSubMenu());
                changeContent(SetPotionScrollRingType.createTitle(Messages.get(SetPotionScrollRingType.class, "title_potion")),
                        change, change.getOutsideSp());
            }
        };
        potionColors.icon(new ItemSprite(ItemSpriteSheet.POTION_AMBER));
        content.add(potionColors);

        scrollRunes = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(GeneralTab.class, "set_scroll"), 7) {
            @Override
            protected void onClick() {
                SetPotionScrollRingType change = SetPotionScrollRingType.createScrollWnd(() -> closeCurrentSubMenu());
                changeContent(SetPotionScrollRingType.createTitle(Messages.get(SetPotionScrollRingType.class, "title_scroll")),
                        change, change.getOutsideSp());
            }
        };
        scrollRunes.icon(new ItemSprite(ItemSpriteSheet.SCROLL_BERKANAN));
        content.add(scrollRunes);

        ringGems = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(GeneralTab.class, "set_ring"), 7) {
            @Override
            protected void onClick() {
                SetPotionScrollRingType change = SetPotionScrollRingType.createRingWnd(() -> closeCurrentSubMenu());
                changeContent(SetPotionScrollRingType.createTitle(Messages.get(SetPotionScrollRingType.class, "title_ring")),
                        change, change.getOutsideSp());
            }
        };
        ringGems.icon(new ItemSprite(ItemSpriteSheet.RING_AMETHYST));
        content.add(ringGems);


        region = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(GeneralTab.class, "region"), 8) {
            @Override
            protected void onClick() {
                ChangeRegion changeRegion = new ChangeRegion(() -> closeCurrentSubMenu());
                changeContent(ChangeRegion.createTitle(), changeRegion, changeRegion.getOutsideSp());
            }
        };
        region.icon(Icons.get(Icons.CHANGES));
        content.add(region);

        mobSpawn = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(GeneralTab.class, "mobs"), 8) {
            @Override
            protected void onClick() {
                MobSettings ms = new MobSettings();
                changeContent(ms.createTitle(), ms, ms.getOutsideSp(), -1f, 0f);
            }
        };
        mobSpawn.icon(new GnollSprite());
        content.add(mobSpawn);

        heroes = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(GeneralTab.class, "heroes"), 8) {
            @Override
            protected void onClick() {
                HeroSettings heroSettings = new HeroSettings();
                changeContent(heroSettings.createTitle(), heroSettings, heroSettings.getOutsideSp(), 0.5f, 0f);
            }
        };
        heroes.icon(BadgeBanner.image(0));
        content.add(heroes);

        hungerDepletion = new StyledCheckbox(Chrome.Type.GREY_BUTTON_TR, Messages.get(GeneralTab.class, "hunger"), 8, level.levelScheme.hungerDepletion) {
            @Override
            public void checked(boolean value) {
                super.checked(value);
                level.levelScheme.hungerDepletion = value;
            }
        };
        hungerDepletion.icon(new ItemSprite(ItemSpriteSheet.RATION));
        content.add(hungerDepletion);

        naturalRegen = new StyledCheckbox(Chrome.Type.GREY_BUTTON_TR, Messages.get(GeneralTab.class, "regeneration"), 8, level.levelScheme.naturalRegeneration) {
            @Override
            public void checked(boolean value) {
                super.checked(value);
                level.levelScheme.naturalRegeneration = value;
            }
        };
        RectF r = ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.POTION_HEALING);
        if (r != null) {
            Image icon = new Image(Assets.Sprites.ITEM_ICONS);
            icon.frame(r);
            icon.scale.set(10 / Math.max(icon.width(), icon.height()));
            naturalRegen.icon(icon);
        }
        content.add(naturalRegen);

        changeSize = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(ChangeMapSize.class, "title"), 8) {

            @Override
            protected void onClick() {
                ChangeMapSize changeMapSize = new ChangeMapSize(() -> closeCurrentSubMenu());
                changeContent(ChangeMapSize.createTitle(), changeMapSize, changeMapSize.getOutsideSp());
            }
        };
        changeSize.icon(Icons.RULER.get());
        content.add(changeSize);


        viewDistance = new StyledSpinner(new SpinnerIntegerModel(1, 20, level.viewDistance, 1, false, null) {
            @Override
            public float getInputFieldWith(float height) {
                return Spinner.FILL;
            }

            @Override
            public int getClicksPerSecondWhileHolding() {
                return 13;
            }
        }, Messages.get(GeneralTab.class, "view_distance"), 8);
        viewDistance.addChangeListener(() -> level.viewDistance = (int) viewDistance.getValue());
        content.add(viewDistance);

        depth = new StyledSpinner(DepthSpinner.createModel(level.levelScheme.getDepth(), height -> (float) Spinner.FILL),
                DepthSpinner.createLabel(), 8);
        depth.addChangeListener(() -> level.levelScheme.setDepth((Integer) depth.getValue()));
        content.add(depth);

        shopPrice = new StyledSpinner(new SpinnerIntegerModel(1, 10, level.levelScheme.getPriceMultiplier(), 1, false, null) {
            @Override
            public float getInputFieldWith(float height) {
                return Spinner.FILL;
            }

            @Override
            public int getClicksPerSecondWhileHolding() {
                return 8;
            }

            @Override
            public void displayInputAnyNumberDialog() {
                super.displayInputAnyNumberDialog(0, 10000);
            }
        }, Messages.get(GeneralTab.class, "shop_price"), 8);
        shopPrice.addChangeListener(() -> level.levelScheme.setShopPriceMultiplier((int) shopPrice.getValue()));
        content.add(shopPrice);

        sp.givePointerPriority();
        add(sp);
    }

    @Override
    public void layout() {

        float posY = y;

        if (layoutOwnMenu) {
            title.setRect(x, posY, width, title.height());

            Component[] components = {potionColors, scrollRunes, ringGems,
                    region, mobSpawn, changeSize,
                    heroes, hungerDepletion, naturalRegen, EditorUtilies.PARAGRAPH_INDICATOR_INSTANCE,
                    depth, viewDistance, shopPrice
            };

            EditorUtilies.layoutStyledCompsInRectangles(GAP, width, content, components);

            sp.setRect(x, title.bottom() + GAP, width, height - title.bottom() - GAP - 1);

        } else {
            posY += GAP * 2;
            float backW = buttonBack.width();
            float backH = buttonBack.height();
            otherTitle.setRect(x + Math.max(backW + GAP, (width - otherTitle.width()) * titleAlignmentOther), posY,
                    width - GAP - backW, Math.max(otherTitle.height(), backH));
            buttonBack.setPos(x, posY + (otherTitle.height() - backH) * 0.5f);
            posY = otherTitle.bottom() + GAP * 3;

            otherBody.setSize(width, -1);

            float normalSpHeight;
            if (outsideSp != null) {
                outsideSp.setSize(width, -1);
                float outsideSpH = outsideSp.height();
                outsideSp.setPos(x, y + height - outsideSpH);
                normalSpHeight = height - outsideSpH - posY - GAP;
                if (outsideSpH == 0) normalSpHeight++;
            } else {
                normalSpHeight = height - posY - 1;
            }
            float makeSpSmaller = Math.max(0, (normalSpHeight - otherBody.height()) * alignmentOther);
            spForOtherBody.setRect(x, posY + makeSpSmaller, width, normalSpHeight - makeSpSmaller);

            spForOtherBody.scrollToCurrentView();
            spForOtherBody.givePointerPriority();
        }
    }

    public static void updateLayout() {
        WndEditorSettings.getInstance().getGeneralTab().layout();
    }

    @Override
    protected void updateList() {

    }

    @Override
    public Image createIcon() {
        return Icons.get(Icons.PREFS);
    }


    protected void changeContent(Component titleBar, Component body, Component outsideSp) {
        changeContent(titleBar, body, outsideSp, 0.5f, 0.5f);
    }

    protected void changeContent(Component titleBar, Component body, Component outsideSp, float alignment, float titleAlignmentX) {
        title.visible = title.active = false;
        content.visible = content.active = false;
        sp.visible = sp.active = false;

        if (alignment != -1f) alignmentOther = alignment;
        titleAlignmentOther = titleAlignmentX;

        buttonBack = new ButtonBack();
        add(buttonBack);
        otherTitle = titleBar;
        otherBody = body;
        spForOtherBody = new ScrollPane(otherBody);
        add(spForOtherBody);
        add(otherTitle);
        this.outsideSp = outsideSp;
        if (outsideSp != null) add(outsideSp);
        layoutOwnMenu = false;

        layout();
    }

    //Update layout manually!!!
    public void setAlignmentOther(float alignmentOther) {
        this.alignmentOther = alignmentOther;
    }

    public void closeCurrentSubMenu() {
        layoutOwnMenu = true;
        otherTitle.remove();
        otherTitle.destroy();
        spForOtherBody.remove();
        spForOtherBody.destroy();
        otherBody.remove();
        otherBody.destroy();
        buttonBack.remove();
        buttonBack.destroy();
        if (outsideSp != null) {
            outsideSp.remove();
            outsideSp.destroy();
        }

        title.visible = title.active = true;
        content.visible = content.active = true;
        sp.visible = sp.active = true;

        layout();
    }

    public interface BackPressImplemented {
        boolean onBackPressed();
    }

    private class ButtonBack extends IconButton {
        public ButtonBack() {
            super(Icons.BACK.get());

            width = 20;
            height = 15;
        }

        @Override
        protected void onClick() {
            if (!(otherBody instanceof BackPressImplemented) || !((BackPressImplemented) otherBody).onBackPressed()) closeCurrentSubMenu();
        }

        @Override
        public GameAction keyAction() {
            return GameAction.BACK;
        }

        @Override
        protected String hoverText() {
            return Messages.titleCase(Messages.get(WndKeyBindings.class, "back"));
        }
    }
}