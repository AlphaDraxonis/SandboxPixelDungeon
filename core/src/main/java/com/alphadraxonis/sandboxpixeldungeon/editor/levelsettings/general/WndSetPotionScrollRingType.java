package com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.general;

import com.alphadraxonis.sandboxpixeldungeon.Chrome;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomDungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.items.ItemTab;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.IconTitleWithSubIcon;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.Spinner;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.SpinnerModel;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.SpinnerTextIconModel;
import com.alphadraxonis.sandboxpixeldungeon.items.Generator;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.Potion;
import com.alphadraxonis.sandboxpixeldungeon.items.rings.Ring;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.Scroll;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSprite;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSpriteSheet;
import com.alphadraxonis.sandboxpixeldungeon.ui.QuickSlotButton;
import com.alphadraxonis.sandboxpixeldungeon.ui.RedButton;
import com.alphadraxonis.sandboxpixeldungeon.ui.RenderedTextBlock;
import com.alphadraxonis.sandboxpixeldungeon.ui.ScrollPane;
import com.alphadraxonis.sandboxpixeldungeon.ui.Window;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Reflection;

import java.util.LinkedHashMap;
import java.util.Map;

public class WndSetPotionScrollRingType extends Window {

    private static final int GAP = 4;
    private static final float BH = 22.5f;

    protected final RenderedTextBlock titleTextBlock;
    private final Spinner[] spinners;
    private final Item[] items;
    protected final RedButton save, cancel;
    protected final ScrollPane sp;
    private final Component content;

    public WndSetPotionScrollRingType(Class<?>[] items, String title) {
        super();

        titleTextBlock = PixelScene.renderTextBlock(title, 12);
        titleTextBlock.hardlight(Window.TITLE_COLOR);
        add(titleTextBlock);

        content = new Component();

        spinners = new Spinner[items.length];
        this.items = new Item[items.length];

        for (int i = 0; i < items.length; i++) {
            this.items[i] = (Item) Reflection.newInstance(items[i]);
            spinners[i] = new Spinner(createModel(this.items[i]), "", 10);
            spinners[i].setAlignmentSpinnerX(0.5f);
            spinners[i].setButtonWidth(14.4f);
            content.add(spinners[i]);
        }

        cancel = new RedButton(Messages.get(WndChangeRegion.class,"cancel")) {
            @Override
            protected void onClick() {
                hide();
            }
        };
        add(cancel);
        save = new RedButton(Messages.get(WndChangeRegion.class,"close")) {
            @Override
            protected void onClick() {
                save();
                hide();
            }
        };
        add(save);

        sp = new ScrollPane(content);
        add(sp);

        layout();
    }

    protected void layout() {

        width = PixelScene.landscape() ? WndTitledMessage.WIDTH_MAX : WndTitledMessage.WIDTH_MIN;
        titleTextBlock.maxWidth(width);

        float maxHeight = PixelScene.uiCamera.height * 0.9f;
        float heightContent = (float) Math.ceil(spinners.length / (PixelScene.landscape() ? 3f : 2f)) * (BH + GeneralTab.GAP);
        resize(width, (int) (titleTextBlock.height() + GeneralTab.BUTTON_HEIGHT + Math.min(maxHeight - titleTextBlock.height() - GeneralTab.GAP * 4 - GeneralTab.BUTTON_HEIGHT, heightContent)));

        float spY = 0;
        float spX = 0;
        if (PixelScene.landscape()) {
            width = WndTitledMessage.WIDTH_MAX;
            float w = (float) (width - GAP * 2) / 3;
            for (int i = 0; i < spinners.length; i++) {
                spinners[i].setRect(spX, spY, w, BH);
                if (i % 3 != 2) {
                    spX = spinners[i].right() + GAP;
                } else {
                    spX = 0;
                    spY = spinners[i].bottom() + GeneralTab.GAP;
                }
            }
        } else {
            width = WndTitledMessage.WIDTH_MIN;
            float w = (float) (width - GAP) / 2;
            for (int i = 0; i < spinners.length; i++) {
                spinners[i].setRect(spX, spY, w, BH);
                if (i % 2 == 0) {
                    spX = spinners[i].right() + GAP;
                } else {
                    spX = 0;
                    spY = spinners[i].bottom() + GeneralTab.GAP;
                }
            }
        }
        content.setSize(width, spY);

        float posY = 0;
        titleTextBlock.maxWidth(width);
        titleTextBlock.setPos((width - titleTextBlock.width()) * 0.5f, posY);
        posY = titleTextBlock.bottom() + GeneralTab.GAP ;


        sp.setRect(0, posY, width, Math.min(maxHeight - posY - GeneralTab.GAP * 1.5f - GeneralTab.BUTTON_HEIGHT, spY));
        posY = sp.bottom() + GeneralTab.GAP * 2.5f;

        float w = width / 3f;
        cancel.setRect(0, posY, w, GeneralTab.BUTTON_HEIGHT);
        save.setRect(cancel.right() + GeneralTab.GAP, posY, w * 2f - GeneralTab.GAP, GeneralTab.BUTTON_HEIGHT);

        height = (int) Math.ceil((save.bottom() + WndTitledMessage.GAP * 0.5f));
        resize(width, height);
    }

    public static WndSetPotionScrollRingType createScrollWnd() {
        return new WndSetPotionScrollRingType(Generator.Category.SCROLL.classes,
                Messages.get(WndSetPotionScrollRingType.class,"title_scroll"));
    }

    public static WndSetPotionScrollRingType createPotionWnd() {
        return new WndSetPotionScrollRingType(Generator.Category.POTION.classes,
                Messages.get(WndSetPotionScrollRingType.class,"title_potion"));
    }

    public static WndSetPotionScrollRingType createRingWnd() {
        return new WndSetPotionScrollRingType(Generator.Category.RING.classes,
                Messages.get(WndSetPotionScrollRingType.class,"title_ring"));
    }

    protected void save() {
        CustomDungeon d = CustomDungeon.getDungeon();
        for (int i = 0; i < spinners.length; i++) {
            String val = (String) spinners[i].getValue();
            if (val.equals(RANDOM_KEY)) val = null;
            if (items[i] instanceof Scroll)
                d.putScrollRuneLabel(((Scroll) items[i]).getClass(), val);
            else if (items[i] instanceof Potion)
                d.putPotionColorLabel(((Potion) items[i]).getClass(), val);
            else if (items[i] instanceof Ring)
                d.putRingGemLabel(((Ring) items[i]).getClass(), val);
        }
        ItemTab.updateItems();
        QuickSlotButton.refresh();
        EditorScene.updateHeapImages();
    }

    public static final String RANDOM_KEY = "RANDOM";

    protected SpinnerModel createModel(Item forItem) {
        Map<String, Integer> keys;
        int random;
        if (forItem instanceof Scroll) {
            keys = Scroll.runes;
            random = ItemSpriteSheet.SCROLL_HOLDER;
        } else if (forItem instanceof Potion) {
            keys = Potion.colors;
            random = ItemSpriteSheet.POTION_HOLDER;
        } else {
            keys = Ring.gems;
            random = ItemSpriteSheet.RING_HOLDER;
        }
        keys = new LinkedHashMap<>(keys);
        keys.put(RANDOM_KEY, random);
        int currentImg = CustomDungeon.getDungeon().getItemSpriteOnSheet(forItem);
        int curItemIndex = 0;
        for (int val : keys.values()) {
            if (val == currentImg) break;
            curItemIndex++;
        }
        return new SpModel(forItem, curItemIndex, keys);
    }

    private static class SpModel extends SpinnerTextIconModel {

        private final Map<String, Integer> keyAndItemSprite;

        private Image subIcon;


        public SpModel(Item forItem, int initImg, Map<String, Integer> keyAndItemSprite) {
            super(true, initImg, keyAndItemSprite.keySet().toArray());
            this.keyAndItemSprite = keyAndItemSprite;
            subIcon = IconTitleWithSubIcon.createSubIcon(forItem);
        }

        @Override
        protected Image getIcon(Object value) {
            //noinspection SuspiciousMethodCalls
            return new ItemSprite(keyAndItemSprite.get(value));
        }

        @Override
        protected Image getSubIcon(Object value) {
            return subIcon;
        }

        @Override
        protected String getAsString(Object value) {
            return "";
        }

        @Override
        public float getInputFieldWith(float height) {
            return height;//Should return ~23
        }

        @Override
        public int getClicksPerSecondWhileHolding() {
            return 9;
        }

        @Override
        protected Chrome.Type getChromeType() {
            return Chrome.Type.GREY_BUTTON_TR;
        }
    }

}