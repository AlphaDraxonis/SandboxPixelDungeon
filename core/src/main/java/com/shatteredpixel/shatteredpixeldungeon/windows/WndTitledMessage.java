package com.shatteredpixel.shatteredpixeldungeon.windows;
/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
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

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class WndTitledMessage extends Window {

    public static final int WIDTH_MIN    = 120;
    public static final int WIDTH_MAX    = 220;
    public static final int GAP	= 2;

    protected RenderedTextBlock text;

    public WndTitledMessage(Image icon, String title, String message ) {

        this( new IconTitle( icon, title ), message );

    }

    public WndTitledMessage(Component titlebar, String message ) {

        super();

        int width = WIDTH_MIN;

        titlebar.setRect(0, 0, width, 0);
        add(titlebar);

        text = PixelScene.renderTextBlock( 6 );
        text.text( message, width );
        text.setPos( titlebar.left(), titlebar.bottom() + 2*GAP );
        add( text );

        while (PixelScene.landscape()
                && text.bottom() > (PixelScene.MIN_HEIGHT_L - 10)
                && width < WIDTH_MAX){
            width += 20;
            titlebar.setRect(0, 0, width, 0);
            text.setPos( titlebar.left(), titlebar.bottom() + 2*GAP );
            text.maxWidth(width);
        }

        bringToFront(titlebar);

        resize( width, (int)text.bottom() + 2 );
    }

    public static void layoutTitleBar(Component titlebar, int width) {
        if (titlebar == null) return;
        if (titlebar instanceof RenderedTextBlock) {
            ((RenderedTextBlock) titlebar).maxWidth(width);
            titlebar.setRect((width - titlebar.width()) / 2f, GAP, titlebar.width(), titlebar.height());
        } else titlebar.setRect(0, 0, width, 0);
        PixelScene.align(titlebar);
    }

    public void setHighligtingEnabled(boolean enableHighligthing){
        text.setHighlighting(enableHighligthing);
    }
}