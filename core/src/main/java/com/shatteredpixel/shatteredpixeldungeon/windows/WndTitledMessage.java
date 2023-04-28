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

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndMenuEditor;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.IconTitleWithSubIcon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class WndTitledMessage extends Window {

    protected static final int WIDTH_MIN = 120;
    protected static final int WIDTH_MAX = 220;
    public static final int GAP = 2;

    protected final Component titlebar;
    private final Body body;
    private final ScrollPane sp;
    private boolean needsScrollPane;

    public WndTitledMessage(Image icon, String title, String message) {
        this(new IconTitle(icon, title), message);
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

        int width = WIDTH_MIN;
        body.setMaxWith(width);

        titlebar.setRect(0, 0, width, 0);
        add(titlebar);

        sp = new ScrollPane(body) {
            @Override
            protected void layout() {
                super.layout();
                thumb.visible = false;
            }
        };
        add(sp);

        layout();
    }

    public void layout() {

        int width = WIDTH_MIN;
        body.setSize(width, -1);

        while (PixelScene.landscape()
                && body.bottom() > (PixelScene.MIN_HEIGHT_L - 10)
                && width < WIDTH_MAX) {
            width += 20;
            titlebar.setRect(0, 0, width, 0);
            body.setMaxWith(width);
            body.layout();
        }
        titlebar.setRect(0, 0, width, 0);

        body.setSize(width, -1);

        int height = (int) (body.bottom() + titlebar.bottom());
        int maxHeight = (int) (PixelScene.uiCamera.height * 0.9);

        needsScrollPane = height > maxHeight;
        if (needsScrollPane) {
            height = maxHeight;
            resize(width, height);
        } else resize(width, height + 3 * GAP);
        body.setPos(0, 0);
        setPosAfterTitleBar(sp);
        if (needsScrollPane) sp.setSize(width, height - titlebar.bottom() - 2.5f * GAP);
        else sp.setSize(width, body.height() + GAP);
        sp.scrollTo(sp.content().camera.scroll.x, sp.content().camera.scroll.y);

        bringToFront(titlebar);
    }

    private void setPosAfterTitleBar(Component comp) {
        comp.setPos(titlebar.left(), titlebar.bottom() + 2 * GAP);
    }

    //Body factory seems messy, but is the only way for custom bodies to receive input events
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
                c.setRect(0, y, width, c.height() > 0 ? c.height() : WndMenuEditor.BTN_HEIGHT);
                height = c.bottom() - y;
            }
        }


    }

}


