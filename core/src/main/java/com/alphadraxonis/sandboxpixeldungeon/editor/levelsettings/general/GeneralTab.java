package com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.general;

import com.alphadraxonis.sandboxpixeldungeon.Chrome;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.WndEditorSettings;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.StyledButtonWithIconAndText;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.Spinner;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.StyledSpinner;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.impls.DepthSpinner;
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

public class GeneralTab extends WndEditorSettings.TabComp {

    public static final int GAP = 2, BIG_GAP = GAP * 3, BUTTON_HEIGHT = 18;

    //TODO:
    //boolean ignoreTerrainForScore (floor only)
    //DecorationPainter (floor only)

    private boolean layoutOwnMenu = true;
    private Component otherTitle, otherBody, spForOtherBody, outsideSp;
    private float alignmentOther;
    private ButtonBack buttonBack;

    private IconTitle title;
    private ScrollPane sp;
    private Component content;

    private StyledButton potionColors, scrollRunes, ringGems;
    private StyledButton region;
    private StyledButton mobSpawn;
    private Spinner viewDistance, depth, shopPrice;
    private StyledButton changeSize;


    @Override
    protected void createChildren(Object... params) {

        title = new IconTitle(Icons.get(Icons.PREFS), Messages.get(GeneralTab.class, "title"));
        add(title);

        content = new Component();
        sp = new ScrollPane(content);

        potionColors = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(GeneralTab.class, "set_pot"), 7) {
            @Override
            protected void onClick() {
                SetPotionScrollRingType change = SetPotionScrollRingType.createPotionWnd(() -> closeCurrentSubMenu());
                changeContent(SetPotionScrollRingType.createTitle(Messages.get(SetPotionScrollRingType.class, "title_potion")),
                        change, change.getOutsideSp(), 0.5f);
            }
        };
        potionColors.icon(new ItemSprite(ItemSpriteSheet.POTION_AMBER));
        content.add(potionColors);

        scrollRunes = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(GeneralTab.class, "set_scroll"), 7) {
            @Override
            protected void onClick() {
                SetPotionScrollRingType change = SetPotionScrollRingType.createScrollWnd(() -> closeCurrentSubMenu());
                changeContent(SetPotionScrollRingType.createTitle(Messages.get(SetPotionScrollRingType.class, "title_scroll")),
                        change, change.getOutsideSp(), 0.5f);
            }
        };
        scrollRunes.icon(new ItemSprite(ItemSpriteSheet.SCROLL_BERKANAN));
        content.add(scrollRunes);

        ringGems = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(GeneralTab.class, "set_ring"), 7) {
            @Override
            protected void onClick() {
                SetPotionScrollRingType change = SetPotionScrollRingType.createRingWnd(() -> closeCurrentSubMenu());
                changeContent(SetPotionScrollRingType.createTitle(Messages.get(SetPotionScrollRingType.class, "title_ring")),
                        change, change.getOutsideSp(), 0.5f);
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

        mobSpawn = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(GeneralTab.class, "mob_spawn"), 8) {
            @Override
            protected void onClick() {
                EditorScene.show(new WndMobSpawn());
            }
        };
        mobSpawn.icon(new GnollSprite());
        content.add(mobSpawn);

        changeSize = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(ChangeMapSize.class, "title"), 8) {

            @Override
            protected void onClick() {
                ChangeMapSize changeMapSize = new ChangeMapSize(() -> closeCurrentSubMenu());
                changeContent(ChangeMapSize.createTitle(), changeMapSize, changeMapSize.getOutsideSp());
            }
        };
        changeSize.icon(Icons.RULER.get());
        content.add(changeSize);


        viewDistance = new StyledSpinner(new SpinnerIntegerModel(1, 30, EditorScene.customLevel().viewDistance, 1, false, null) {
            @Override
            public float getInputFieldWith(float height) {
                return height * 1.2f;
            }

            @Override
            public int getClicksPerSecondWhileHolding() {
                return 13;
            }
        }, Messages.get(GeneralTab.class, "view_distance"), 8);
        viewDistance.addChangeListener(() -> EditorScene.customLevel().viewDistance = (int) viewDistance.getValue());
        content.add(viewDistance);

        depth = new StyledSpinner(DepthSpinner.createModel(EditorScene.customLevel().levelScheme.getDepth()), DepthSpinner.createLabel(), 8);
        depth.addChangeListener(() -> EditorScene.customLevel().levelScheme.setDepth((Integer) depth.getValue()));
        content.add(depth);

        shopPrice = new StyledSpinner(new SpinnerIntegerModel(1, 10, EditorScene.customLevel().levelScheme.getPriceMultiplier(), 1, false, null) {
            @Override
            public float getInputFieldWith(float height) {
                return height * 1.2f;
            }

            @Override
            public int getClicksPerSecondWhileHolding() {
                return 8;
            }
        }, Messages.get(GeneralTab.class, "shop_price"), 8);
        shopPrice.addChangeListener(() -> EditorScene.customLevel().levelScheme.setShopPriceMultiplier((int) shopPrice.getValue()));
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
                    depth, viewDistance, shopPrice};

            float oneThirdWidth = (width() - GAP * 2) / 3f;

            for (Component c : components) {
                if (c != null) c.setSize(oneThirdWidth, -1);
            }
            float maxCompHeight = 0;
            for (Component c : components) {
                if (c != null && c.height() > maxCompHeight) maxCompHeight = c.height();
            }
            for (Component c : components) {
                if (c != null) c.setSize(oneThirdWidth, maxCompHeight);
            }

            posY = 0;
            float posX = x;
            for (int i = 0; i < components.length; i++) {
                if (components[i] != null) {
                    components[i].setPos(posX, posY);
                    if ((i + 1) % 3 == 0) {
                        posY += GAP + maxCompHeight;
                        posX = x;
                    } else posX = components[i].right() + GAP;
                }
            }

            content.setSize(width, posY - BIG_GAP);
            sp.setRect(x, title.bottom() + GAP, width, height - title.bottom() - GAP - 1);

        } else {
            posY += GAP * 2;
            float backW = buttonBack.width();
            float backH = buttonBack.height();
            otherTitle.setRect(x + Math.max(backW + GAP, (width - otherTitle.width()) * 0.5f), posY,
                    width - GAP - backW, Math.max(otherTitle.height(), backH));
            buttonBack.setPos(x, posY + (otherTitle.height() - backH) * 0.5f);
            posY = otherTitle.bottom() + GAP * 3;

            otherBody.setSize( width, -1);

            float normalSpHeight;
            if (outsideSp != null) {
                outsideSp.setSize(width, -1);
                float outsideSpH = outsideSp.height();
                outsideSp.setPos(x, y + height - outsideSpH);
                normalSpHeight = height - outsideSpH - posY - GAP;
            } else {
                normalSpHeight = height - posY - 1;
            }
            float makeSpSmaller = Math.max(0, (normalSpHeight - otherBody.height()) * alignmentOther);
            spForOtherBody.setRect(x, posY + makeSpSmaller, width, normalSpHeight - makeSpSmaller);

        }
    }

    @Override
    protected void updateList() {

    }

    @Override
    public Image createIcon() {
        return Icons.get(Icons.PREFS);
    }


    protected void changeContent(Component titleBar, Component body, Component outsideSp) {
        changeContent(titleBar, body, outsideSp, 0);
    }

    protected void changeContent(Component titleBar, Component body, Component outsideSp, float alignment) {
        title.visible = title.active = false;
        content.visible = content.active = false;
        sp.visible = sp.active = false;

        alignmentOther = alignment;

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

    private class ButtonBack extends IconButton {
        public ButtonBack() {
            super(Icons.BACK.get());

            width = 20;
            height = 15;
        }

        @Override
        protected void onClick() {
            closeCurrentSubMenu();
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