package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.dungeon;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.items.ItemTab;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.level.ChangeRegion;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.level.LevelTab;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.IconTitleWithSubIcon;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerTextIconModel;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Reflection;

import java.util.LinkedHashMap;
import java.util.Map;

public class SetPotionScrollRingType extends Component {

    private static final int GAP = 4;
    private static final float BH = 22.5f;

    private final Component outsideSp;

    private final Spinner[] spinners;
    private final Item[] items;

    public SetPotionScrollRingType(Class<?>[] items, Runnable onClose) {
        super();

        spinners = new Spinner[items.length];
        this.items = new Item[items.length];

        for (int i = 0; i < items.length; i++) {
            this.items[i] = (Item) Reflection.newInstance(items[i]);
            spinners[i] = new Spinner(createModel(this.items[i]), "", 10);
            spinners[i].setAlignmentSpinnerX(0.5f);
            spinners[i].setButtonWidth(14.4f);
            add(spinners[i]);
        }

        outsideSp = new Component(){
           private RedButton cancel, save;
            @Override
            protected void createChildren(Object... params) {
                cancel = new RedButton(Messages.get(ChangeRegion.class,"cancel")) {
                    @Override
                    protected void onClick() {
                        onClose.run();
                    }
                };
                save = new RedButton(Messages.get(ChangeRegion.class,"close")) {
                    @Override
                    protected void onClick() {
                        save();
                        onClose.run();
                    }
                };
                add(cancel);
                add(save);
            }

            @Override
            protected void layout() {
                float w = width / 3f;
                cancel.setRect(x, y, w, LevelTab.BUTTON_HEIGHT);
                save.setRect(cancel.right() + LevelTab.GAP, y, width - w - LevelTab.GAP, LevelTab.BUTTON_HEIGHT);
                height = LevelTab.BUTTON_HEIGHT;
            }
        };
    }

    public static Component createTitle(String title){
        RenderedTextBlock titleTextBlock = PixelScene.renderTextBlock(title, 12);
        titleTextBlock.hardlight(Window.TITLE_COLOR);
        return titleTextBlock;
    }

    public Component getOutsideSp() {
        return outsideSp;
    }

    protected void layout() {

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
                    spY = spinners[i].bottom() + LevelTab.GAP;
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
                    spY = spinners[i].bottom() + LevelTab.GAP;
                }
            }
        }
        height = spY;
    }

    public static SetPotionScrollRingType createScrollWnd(Runnable onClose) {
        return new SetPotionScrollRingType(Generator.Category.SCROLL.classes, onClose);
    }

    public static SetPotionScrollRingType createPotionWnd(Runnable onClose) {
        return new SetPotionScrollRingType(Generator.Category.POTION.classes,onClose);
    }

    public static SetPotionScrollRingType createRingWnd(Runnable onClose) {
        return new SetPotionScrollRingType(Generator.Category.RING.classes, onClose);
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
        public float getInputFieldWidth(float height) {
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