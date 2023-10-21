package com.shatteredpixel.shatteredpixeldungeon.editor.ui;

import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.List;

public abstract class ChooseOneInCategoriesBody extends Component {

    public static final int BUTTON_HEIGHT = 18, GAP = 1;

    private final Component titleBar;
    private final RenderedTextBlock text;
    private final ScrollPane sp;
    private final Component wrapper;

    private final RedButton scrollUp, scrollDown, cancel;

    private Integer[] titlePos;

    private List<CategoryComp> categoryComps = new ArrayList<>();

    public ChooseOneInCategoriesBody(Component titleBar, String desc, Object[][] categories, String[] categoryNames) {

        super();

        this.titleBar = titleBar;
        if (titleBar instanceof RenderedTextBlock)
            ((RenderedTextBlock) titleBar).hardlight(Window.TITLE_COLOR);
        add(titleBar);

        this.text = PixelScene.renderTextBlock(desc, 6);
        add(this.text);

        scrollUp = new RedButton(Messages.get(ChooseOneInCategoriesBody.class,"up"), 7) {
            @Override
            protected void onClick() {
                scrollUp();
            }

            @Override
            protected int getClicksPerSecondWhenHolding() {
                return getScrollsPerSecondWhenHolding();
            }
        };
        add(scrollUp);
        scrollDown = new RedButton(Messages.get(ChooseOneInCategoriesBody.class,"down"), 7) {
            @Override
            protected void onClick() {
                scrollDown();
            }

            @Override
            protected int getClicksPerSecondWhenHolding() {
                return getScrollsPerSecondWhenHolding();
            }
        };
        add(scrollDown);
        cancel = new RedButton(Messages.get(ChooseOneInCategoriesBody.class,"cancel"), 7) {
            @Override
            protected void onClick() {
                onCancel();
            }
        };
        add(cancel);


        wrapper = new Component();

        for (int i = 0; i < categories.length; i++) {
            CategoryComp c = new CategoryComp(Messages.titleCase(categoryNames[i]),
                    createCategoryRows(categories[i]));
            wrapper.add(c);
            categoryComps.add(c);
        }
        sp = new ScrollPane(wrapper);
        add(sp);
    }

    @Override
    protected void layout() {

        WndTitledMessage.layoutTitleBar(titleBar, (int) width);
        text.maxWidth((int) (width - GAP * 2));
        text.setRect(0, titleBar.bottom() + (text.text().isEmpty() ? 0 : GAP * 4), width, text.height());

        float pos = 0;

        List<Integer> titlePosList = new ArrayList<>();
        for (CategoryComp cat : categoryComps) {
            if (cat.visible = cat.shouldBeVisible()) {
                titlePosList.add((int) PixelScene.align(pos));
                pos += GAP * 3;
                cat.setRect(x, pos, width, 0);
                PixelScene.align(cat);
                pos = cat.bottom() + GAP;
            }
        }
        titlePos = titlePosList.toArray(new Integer[0]);

        wrapper.setSize(width, pos);

        float bh = BUTTON_HEIGHT * getControllButtonHeightMultiplier();
        pos = height - bh;
        float bw = width / 3f;
        scrollUp.setRect(0, pos, bw, bh);
        scrollDown.setRect(scrollUp.right(), pos, bw, bh);
        cancel.setRect(scrollDown.right(), pos, bw, bh);

        float posY = text.bottom() + GAP * 2.5f;
        sp.setRect(0, posY, width, height - posY - 2 * GAP - bh);
    }


    public void scrollUp() {
        float y = getCurrentViewY() - 3 * GAP;
        for (int i = titlePos.length - 1; i >= 0; i--) {
            if (titlePos[i] < y) {
                y = titlePos[i];
                break;
            }
        }
        scrollTo(y);
    }

    public void scrollDown() {
        float y = getCurrentViewY() + 3 * GAP;
        boolean set = false;
        for (int i = 0; i < titlePos.length; i++) {
            if (titlePos[i] > y) {
                y = titlePos[i];
                set = true;
                break;
            }
        }
        if (!set) y = Float.MAX_VALUE;
        scrollTo(y);
    }

    public void scrollTo(float yPos) {
        sp.scrollTo(sp.content().camera.x, yPos);
    }

    public float getCurrentViewY() {
        return sp.content().camera.scroll.y;
    }

    public abstract void onCancel();

    protected abstract BtnRow[] createCategoryRows(Object[] category);


    //Settings
    protected int getScrollsPerSecondWhenHolding() {
        return 0;
    }

    protected float getControllButtonHeightMultiplier() {
        return 2f / 3f;
    }

    private static class CategoryComp extends Component {
        private final RenderedTextBlock tb;
        private BtnRow[] rows;

        public CategoryComp(String name, BtnRow... rows) {
            super();

            this.rows = rows;

            tb = PixelScene.renderTextBlock(name, 10);
            add(tb);

            for (BtnRow row : rows) {
                add(row);
            }
        }

        @Override
        protected void layout() {

            float pos = y;

            tb.maxWidth((int) (width - GAP * 2));
            tb.hardlight(Window.TITLE_COLOR);
            tb.setPos((int) ((width - tb.width()) / 2), pos);
            PixelScene.align(tb);

            pos = tb.bottom() + 3 * GAP;

            for (BtnRow row : rows) {
                row.setRect(0, pos, width, BUTTON_HEIGHT);
                PixelScene.align(row);
                pos = row.bottom() + GAP;
            }

            height = pos - y;
        }

        public boolean shouldBeVisible() {
            return rows.length > 0;
        }
    }

    public static class BtnRow extends Component {

        private RedButton btn;
        private IconButton infoBtn;

        private String name, info;
        private Image icon;

        public BtnRow(String name, String info) {
            this(name, info, null);
        }

        public BtnRow(String name, String info, Image icon) {
            this(name, info, icon, null, 8);
        }

        public BtnRow(String name, String info, Image icon, Image subIcon, int fontSize) {

            super();
            this.name = Messages.titleCase(name);
            this.info = info;
            this.icon = icon;

            btn = new RedButtonWithSubIcon(this.name, fontSize, subIcon) {
                @Override
                protected void onClick() {
                    BtnRow.this.onClick();
                }

                @Override
                protected void onRightClick() {
                    BtnRow.this.onRightClick();
                }

                @Override
                protected boolean onLongClick() {
                    return BtnRow.this.onLongClick();
                }
            };
            if (icon != null) btn.icon(icon);
            add(btn);
            infoBtn = new IconButton(Icons.get(Icons.INFO)) {
                @Override
                protected void onClick() {
                    onInfo();
                }
            };
            add(infoBtn);
        }

        protected void onClick() {
        }

        protected void onRightClick() {

        }

        protected boolean onLongClick() {
            return false;
        }

        protected void onInfo() {
            Image img;
            if (icon != null) {
                if (icon instanceof CharSprite) {
                    img = Reflection.newInstance(icon.getClass());
                } else {
                    img = new Image();
                    img.copy(icon);
                }
            } else img = Icons.get(Icons.INFO);
            if(Game.scene() instanceof EditorScene) EditorScene.show(new WndTitledMessage(img, name, info()));
            else Game.scene().addToFront(new WndTitledMessage(img, name, info()));
        }

        @Override
        protected void layout() {
            btn.setRect(x, y, width - BUTTON_HEIGHT, BUTTON_HEIGHT);
            infoBtn.setRect(width - BUTTON_HEIGHT, y, BUTTON_HEIGHT, BUTTON_HEIGHT);
        }

        protected String info() {
            return info;
        }
    }

}