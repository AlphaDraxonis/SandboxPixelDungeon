package com.shatteredpixel.shatteredpixeldungeon.windows;
/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class WndTitledMessage extends Window {

    public static final int WIDTH_MIN = 120;
    public static final int WIDTH_MAX = 220;
    public static final int GAP = 2;

    protected WindowContent content;
    protected RenderedTextBlock text;


    public WndTitledMessage(Image icon, String title, String message) {
        this(icon, title, message, Chrome.Type.WINDOW);
    }

    public WndTitledMessage(Image icon, String title, String message, Chrome.Type type) {

        this(new IconTitle(icon, title), message, type);

    }

    public WndTitledMessage(Component titlebar, String message) {
        this(titlebar, message, Chrome.Type.WINDOW);
    }

    public WndTitledMessage(Component titlebar, String message, Chrome.Type type) {

        super(0, 0, Chrome.get(type));

        content = new WindowContent(titlebar, message);
        text = content.text;
        add(content);
        content.setSize(0, 0);

        resize((int) Math.ceil(content.width()), (int) Math.ceil(content.height()));

        content.setPos(0, 0);
    }

    public static void layoutTitleBar(Component titlebar, int width) {
        if (titlebar == null) return;
        if (titlebar instanceof RenderedTextBlock) {
            ((RenderedTextBlock) titlebar).maxWidth(width);
            titlebar.setRect((width - titlebar.width()) / 2f, GAP, titlebar.width(), titlebar.height());
        } else titlebar.setRect(0, 0, width, 0);
        PixelScene.align(titlebar);
    }

    public void setHighlightingEnabled(boolean enableHighligthing) {
        content.setHighlightingEnabled(enableHighligthing);
        resize((int) Math.ceil(content.width()), (int) Math.ceil(content.height()));
        content.setPos(0, 0);
    }

    @Override
    public void offset(int xOffset, int yOffset) {
        super.offset(xOffset, yOffset);
        content.layout();
        resize((int) Math.ceil(content.width()), (int) Math.ceil(content.height()));
    }

    protected boolean useHighlighting() {
        return true;
    }

    protected float targetHeight() {
        return PixelScene.MIN_HEIGHT_L - 10;
    }

    protected class WindowContent extends Component {

        protected Component titlebar;
        protected RenderedTextBlock text;
        protected ScrollPane sp;

        public WindowContent(Component titlebar, String message) {
            this.titlebar = titlebar;
            add(titlebar);

            sp = new ScrollPane(new Component() {
                @Override
                protected void createChildren() {
                    text = PixelScene.renderTextBlock(6);
                    if (!useHighlighting()) text.setHighlighting(false);
                    text.text(message);
                    add(text);
                }

                @Override
                protected void layout() {
                    text.setPos(x, y);
                    width = text.width();
                    height = text.height();
                    if (height > sp.height()) height += 2;
                }
            });
            add(sp);

            bringToFront(titlebar);
        }

        @Override
        protected void layout() {
            width = Math.max(width, WIDTH_MIN);
            titlebar.setRect(0, 0, width, 0);

            int maxWidth = WIDTH_MAX;
            text.maxWidth(maxWidth);
            maxWidth = (int) Math.ceil(text.width());

            text.maxWidth((int) width);

            while (PixelScene.landscape()
                    && text.bottom() > targetHeight()
                    && width < WIDTH_MAX) {
                width += 20;
                titlebar.setRect(0, 0, width, 0);
                text.setPos(titlebar.left(), titlebar.bottom() + 2 * GAP);
                text.maxWidth((int) width);
            }

            bringToFront(titlebar);

            height = titlebar.height() + 2 * GAP + text.height() + 2 - 1;
            if (height > WindowSize.HEIGHT_MEDIUM.get()) {
                height = WindowSize.HEIGHT_MEDIUM.get();
                sp.setRect(titlebar.left(), titlebar.bottom() + 2 * GAP, width, height - titlebar.height() - 2 * GAP);
            } else {
                sp.setRect(titlebar.left(), titlebar.bottom() + 2 * GAP, width, height - titlebar.height() - 2 * GAP + 1);
            }
        }

        public void setHighlightingEnabled(boolean enableHighligthing) {
            text.setHighlighting(enableHighligthing);
            layout();
        }
    }
}
