package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.gerneral;

import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndEditorSettings;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.impls.DepthSpinner;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
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

    private FeelingSpinner feeling;
    private RedButton potionColors, scrollRunes, ringGems;
    private RedButton region;
    private RedButton mobSpawn;
    private Spinner viewDistance, depth, shopPrice;


    @Override
    protected void createChildren(Object... params) {

        title = new IconTitle(Icons.get(Icons.PREFS), "General Settings");
        add(title);

        content = new Component();
        sp = new ScrollPane(content);

        feeling = new FeelingSpinner(EditorScene.customLevel().feeling,false);
        feeling.addChangeListener(() -> {
            EditorScene.customLevel().feeling = (Level.Feeling) feeling.getValue();
            EditorScene.updateDepthIcon();
        });
        content.add(feeling);

        potionColors = new RedButton("Set potion colors") {
            @Override
            protected void onClick() {
                EditorScene.show(WndSetPotionScrollRingType.createPotionWnd());
            }
        };
        content.add(potionColors);

        scrollRunes = new RedButton("Set scroll runes") {
            @Override
            protected void onClick() {
                EditorScene.show(WndSetPotionScrollRingType.createScrollWnd());
            }
        };
        content.add(scrollRunes);

        ringGems = new RedButton("Set ring gems") {
            @Override
            protected void onClick() {
                EditorScene.show(WndSetPotionScrollRingType.createRingWnd());
            }
        };
        content.add(ringGems);


        region = new RedButton("Change region") {
            @Override
            protected void onClick() {
                EditorScene.show(new WndChangeRegion());
            }
        };
        content.add(region);

        mobSpawn = new RedButton("Mob spawn settings") {
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
        }, " View distance:", 9);
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
        }, " ShopPrice:", 9);
        shopPrice.addChangeListener(() -> {
            EditorScene.customLevel().levelScheme.setShopPriceMultiplier((int) shopPrice.getValue());
        });
        content.add(shopPrice);

        sp.givePointerPriority();
        add(sp);
    }

    @Override
    public void layout() {

        float posY = y;

        title.setRect(x, posY, width, title.height());
        posY = title.bottom() + GAP * 2.5f;

        sp.setRect(x, posY, width, height - posY - GAP);


        posY = 0;
        feeling.setRect(x, posY, width, BUTTON_HEIGHT);
        posY = feeling.bottom() + BIG_GAP;


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

        content.setSize(width, posY - BIG_GAP);
    }

    @Override
    protected void updateList() {

    }

    @Override
    protected Image createIcon() {
        return Icons.get(Icons.PREFS);
    }

}