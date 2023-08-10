/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2023 Evan Debenham
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

package com.alphadraxonis.sandboxpixeldungeon.windows;

import com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.WndMenuEditor;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.IconTitleWithSubIcon;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.ui.RenderedTextBlock;
import com.alphadraxonis.sandboxpixeldungeon.ui.ScrollPane;
import com.alphadraxonis.sandboxpixeldungeon.ui.Window;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class WndTitledMessage extends Window {

    public static final int WIDTH_MIN = 120;
    public static final int WIDTH_MAX = 220;
    public static final int GAP = 2;

    protected final Component titlebar;
    private final Body body;
    private final ScrollPane sp;
    private boolean needsScrollPane;

    public WndTitledMessage(Image icon, String title, String message) {
        this(new IconTitle(icon, title), message);
    }

    public WndTitledMessage(String title, BodyFactory createBody) {
        this(createTitleNoIcon(title), createBody);
    }

    public static RenderedTextBlock createTitleNoIcon(String title) {
        RenderedTextBlock c = new RenderedTextBlock(title, 12 * PixelScene.defaultZoom) {//From PixelScene.renderTextBlock()
            @Override
            public float bottom() {
                return super.bottom() + GAP;
            }
        };
        c.zoom(1 / (float) PixelScene.defaultZoom);
        c.hardlight(Window.TITLE_COLOR);
        return c;
    }

    public WndTitledMessage(Image icon, Image subIcon, String title, String message) {
        this(new IconTitleWithSubIcon(icon, subIcon, title), message);
    }

    public WndTitledMessage(Image icon, String title, Component body) {
        this(new IconTitle(icon, title), body);
    }

    public WndTitledMessage(Image icon, Image subIcon, String title, Component body) {
        this(new IconTitleWithSubIcon(icon, subIcon, title), body);
    }

    public WndTitledMessage(Component titlebar, String message) {
        this(titlebar, PixelScene.renderTextBlock(message, 6));
    }

    public WndTitledMessage(Component titlebar, Component body) {
        this(titlebar, () -> new Body(body));
    }

    public WndTitledMessage(Component titlebar, BodyFactory createBody) {
        super();

        this.titlebar = titlebar;
        this.body = createBody.create();

        body.setMaxWith(WIDTH_MIN);

        if (titlebar != null) {
            layoutTitleBar(titlebar, WIDTH_MIN);
            add(titlebar);
        }

        sp = new ScrollPane(body) {
            @Override
            protected void layout() {
                super.layout();
                thumb.visible = false;
            }
        };
        add(sp);

        layout(-1);
    }

    public void layout(int newWidth) {

        boolean widthSet = newWidth >= 0;
        if (!widthSet) newWidth = WIDTH_MIN;

        body.setSize(newWidth, -1);

        if (!widthSet) {
            while (PixelScene.landscape()
                    && body.bottom() > (PixelScene.MIN_HEIGHT_L - 10)
                    && newWidth < WIDTH_MAX) {
                newWidth += 20;
                layoutTitleBar(titlebar, newWidth);
                body.setMaxWith(newWidth);
                body.layout();
            }
        }
        layoutTitleBar(titlebar, newWidth);

        body.setSize(newWidth, -1);

        int height = (int) (body.bottom() + (titlebar == null ? 0 : titlebar.bottom()));
        int maxHeight = (int) (PixelScene.uiCamera.height * 0.9);

        needsScrollPane = height > maxHeight;
        if (needsScrollPane) {
            height = maxHeight;
            resize(newWidth, height + GAP);
        } else resize(newWidth, height + 3 * GAP);
        body.setPos(0, 0);
        setPosAfterTitleBar(sp);
        if (needsScrollPane)
            sp.setSize(newWidth, height - (titlebar == null ? 0 : titlebar.bottom()) - 2.5f * GAP);
        else sp.setSize(newWidth, body.height() + GAP);
        sp.scrollToCurrentView();

        bringToFront(titlebar);
    }

    public static void layoutTitleBar(Component titlebar, int width) {
        if (titlebar == null) return;
        if (titlebar instanceof RenderedTextBlock) {
            ((RenderedTextBlock) titlebar).maxWidth(width);
            titlebar.setRect((width - titlebar.width()) / 2f, GAP, titlebar.width(), titlebar.height());
        } else titlebar.setRect(0, 0, width, 0);
        PixelScene.align(titlebar);
    }

    private void setPosAfterTitleBar(Component comp) {
        comp.setPos(0, (titlebar == null ? GAP : titlebar.bottom() + 2 * GAP));
    }

    //Body factory seems messy, but is the only way for custom bodies to receive input events //lol maybe givePointerPriority() would work too??
    public interface BodyFactory {
        Body create();
    }

    protected Component body() {
        return body;
    }

    public boolean needsScrollPane() {
        return needsScrollPane;
    }


    public static class Body extends Component {

        private final Component c;

        public Body() {
            this(null);
        }

        public Body(Component txt) {
            c = txt;
            if (c != null) add(c);
        }

        public void setMaxWith(int width) {
            if (c != null && c instanceof RenderedTextBlock)
                ((RenderedTextBlock) c).maxWidth(width);
        }

        protected void layout() {
            if (c != null) {
                c.setRect(0, y + 1, width, c.height() > 0 ? c.height() : WndMenuEditor.BTN_HEIGHT);
                height = c.bottom() - y - 1;
            }
        }


    }

}