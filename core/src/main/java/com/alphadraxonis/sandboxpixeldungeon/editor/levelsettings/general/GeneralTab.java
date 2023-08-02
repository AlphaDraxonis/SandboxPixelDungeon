package com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.general;

import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.WndEditorSettings;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.Spinner;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.impls.DepthSpinner;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.ui.Icons;
import com.alphadraxonis.sandboxpixeldungeon.ui.RedButton;
import com.alphadraxonis.sandboxpixeldungeon.ui.ScrollPane;
import com.alphadraxonis.sandboxpixeldungeon.windows.IconTitle;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class GeneralTab extends WndEditorSettings.TabComp {

    public static final int GAP = 2, BIG_GAP = GAP * 3, BUTTON_HEIGHT = 18;

    //TODO:
    //boolean ignoreTerrainForScore (floor only)
    //DecorationPainter (floor only)

    private IconTitle title;
    private ScrollPane sp;
    private Component content;

    private RedButton potionColors, scrollRunes, ringGems;
    private RedButton region;
    private RedButton mobSpawn;
    private Spinner viewDistance, depth, shopPrice;
    private RedButton changeSize;


    @Override
    protected void createChildren(Object... params) {

        title = new IconTitle(Icons.get(Icons.PREFS), Messages.get(GeneralTab.class,"title"));
        add(title);

        content = new Component();
        sp = new ScrollPane(content);

        potionColors = new RedButton(Messages.get(GeneralTab.class,"set_pot")) {
            @Override
            protected void onClick() {
                EditorScene.show(WndSetPotionScrollRingType.createPotionWnd());
            }
        };
        content.add(potionColors);

        scrollRunes = new RedButton(Messages.get(GeneralTab.class,"set_scroll")) {
            @Override
            protected void onClick() {
                EditorScene.show(WndSetPotionScrollRingType.createScrollWnd());
            }
        };
        content.add(scrollRunes);

        ringGems = new RedButton(Messages.get(GeneralTab.class,"set_ring")) {
            @Override
            protected void onClick() {
                EditorScene.show(WndSetPotionScrollRingType.createRingWnd());
            }
        };
        content.add(ringGems);


        region = new RedButton(Messages.get(GeneralTab.class,"region")) {
            @Override
            protected void onClick() {
                EditorScene.show(new WndChangeRegion());
            }
        };
        content.add(region);

        mobSpawn = new RedButton(Messages.get(GeneralTab.class,"mob_spawn")) {
            @Override
            protected void onClick() {
                EditorScene.show(new WndMobSpawn());
            }
        };
        content.add(mobSpawn);

        viewDistance = new Spinner(new SpinnerIntegerModel(1, 30, EditorScene.customLevel().viewDistance, 1, false, null) {
            @Override
            public float getInputFieldWith(float height) {
                return height * 1.2f;
            }

            @Override
            public int getClicksPerSecondWhileHolding() {
                return 13;
            }
        }, " " + Messages.get(GeneralTab.class, "view_distance") + ":", 9);
        viewDistance.addChangeListener(() -> EditorScene.customLevel().viewDistance = (int) viewDistance.getValue());
        content.add(viewDistance);

        depth = new DepthSpinner(EditorScene.customLevel().levelScheme.getDepth(), 9) {
            @Override
            protected void onChange(int newDepth) {
                EditorScene.customLevel().levelScheme.setDepth(newDepth);
            }
        };
        content.add(depth);

        shopPrice = new Spinner(new SpinnerIntegerModel(1, 10, EditorScene.customLevel().levelScheme.getPriceMultiplier(), 1, false, null) {
            @Override
            public float getInputFieldWith(float height) {
                return height * 1.2f;
            }

            @Override
            public int getClicksPerSecondWhileHolding() {
                return 8;
            }
        }, " " + Messages.get(GeneralTab.class, "shop_price") + ":", 9);
        shopPrice.addChangeListener(() -> {
            EditorScene.customLevel().levelScheme.setShopPriceMultiplier((int) shopPrice.getValue());
        });
        content.add(shopPrice);

        changeSize = new RedButton(Messages.get(WndChangeMapSize.class, "title")){

            @Override
            protected void onClick() {
                EditorScene.show(new WndChangeMapSize());
            }
        };
        content.add(changeSize);

        sp.givePointerPriority();
        add(sp);
    }

    @Override
    public void layout() {

        float posY = y;

        title.setRect(x, posY, width, title.height());
        posY = title.bottom() + GAP * 2.5f*0;

        sp.setRect(x, posY, width, height - posY - GAP);

        posY = 0;

        potionColors.setRect(x, posY, width, BUTTON_HEIGHT);
        posY = potionColors.bottom() + GAP;

        scrollRunes.setRect(x, posY, width, BUTTON_HEIGHT);
        posY = scrollRunes.bottom() + GAP;

        ringGems.setRect(x, posY, width, BUTTON_HEIGHT);
        posY = ringGems.bottom() + BIG_GAP;


        region.setRect(x, posY, width, BUTTON_HEIGHT);
        posY = region.bottom() + BIG_GAP;

        mobSpawn.setRect(x, posY, width, BUTTON_HEIGHT);
        posY = mobSpawn.bottom() + BIG_GAP;

        viewDistance.setRect(x, posY, width, BUTTON_HEIGHT);
        posY = viewDistance.bottom() + GAP;

        depth.setRect(x, posY, width, BUTTON_HEIGHT);
        posY = depth.bottom() + GAP;

        shopPrice.setRect(x, posY, width, BUTTON_HEIGHT);
        posY = shopPrice.bottom() + BIG_GAP;

        changeSize.setRect(x, posY, width, BUTTON_HEIGHT);
        posY = changeSize.bottom() + BIG_GAP;

        content.setSize(width, posY - BIG_GAP);
    }

    @Override
    protected void updateList() {

    }

    @Override
    public Image createIcon() {
        return Icons.get(Icons.PREFS);
    }

}