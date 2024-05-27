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

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.*;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class WndOptions extends Window {

	protected static final int WIDTH_P = 120;
	protected static final int WIDTH_L = 144;

	protected static final int MARGIN 		= 2;
	protected static final int BUTTON_HEIGHT	= 18;

	private ScrollPane sp;
	private Component content;

	protected RenderedTextBlock tfMessage;
	protected Component tfTitle;
	protected RedButton[] buttons;
	protected IconButton[] infos;

	public WndOptions(Image icon, String title, String message, String... options) {
		super();

		if (title != null) {
			tfTitle = new IconTitle(icon, title);
			add(tfTitle);
		}

		tfMessage = PixelScene.renderTextBlock( message, 6 );
		add(tfMessage);

		initBody(options);
	}
	
	public WndOptions( String title, String message, String... options ) {
		super();

		int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;

		if (title != null) {
			RenderedTextBlock tfTitle = PixelScene.renderTextBlock(title, 9);
			tfTitle.hardlight(TITLE_COLOR);
			tfTitle.maxWidth(width - MARGIN * 2);
			add(tfTitle);
			this.tfTitle = tfTitle;
		}

		tfMessage = PixelScene.renderTextBlock( message, 6 );
		add(tfMessage);
		
		initBody(options);
	}

	protected void initBody(String... options){

		content = new Component();

		buttons = new RedButton[options.length];
		infos = new IconButton[options.length];

		for (int i=0; i < options.length; i++) {
			final int index = i;
			buttons[i] = new RedButton( options[i] ) {
				@Override
				protected void onClick() {
					hide();
					onSelect( index );
				}
			};
			Image icon = getIcon(i);
			if (icon != null) buttons[i].icon(icon);
			buttons[i].enable(enabled(i));
			content.add( buttons[i] );

			if (hasInfo(i)) {
				infos[i] = new IconButton(Icons.get(Icons.INFO)){
					@Override
					protected void onClick() {
						onInfo( index );
					}
				};
				content.add(infos[i]);
			}
		}

		sp = new ScrollPane(content);
		add(sp);

		layout(PixelScene.landscape() ? WIDTH_L : WIDTH_P);
	}

	protected void layout(int width) {

		float pos = 0;

		if (tfTitle instanceof RenderedTextBlock) {
			pos += MARGIN;
			((RenderedTextBlock) tfTitle).maxWidth(width - MARGIN * 2);
			tfTitle.setPos(MARGIN, pos);
		} else {
			tfTitle.setRect(0, pos, width, 0);
		}
		pos = tfTitle.bottom() + 2*MARGIN;

		tfMessage.maxWidth(width);
		tfMessage.setPos(0, pos);
		pos = tfMessage.bottom() + 2*MARGIN;

		float posInSp = 0;

		for (int i = 0; i < buttons.length; i++) {
			if (infos[i] == null) {
				buttons[i].setRect(0, posInSp, width, BUTTON_HEIGHT);
			} else {
				buttons[i].setRect(0, posInSp, width - BUTTON_HEIGHT, BUTTON_HEIGHT);
				infos[i].setRect(width-BUTTON_HEIGHT, posInSp, BUTTON_HEIGHT, BUTTON_HEIGHT);
			}

			posInSp += BUTTON_HEIGHT + MARGIN;
		}

		content.setSize(width, posInSp - MARGIN);

		float spHeight = Math.min(content.height(), PixelScene.uiCamera.height * 0.88f - pos);

		resize(width, (int)(pos + spHeight));

		sp.setRect(0, pos, width, spHeight);

	}

	@Override
	public void offset(int xOffset, int yOffset) {
		super.offset(xOffset, yOffset);
		layout(width);
	}

	protected boolean enabled( int index ) {
		return true;
	}
	
	protected void onSelect( int index ) {}

	protected boolean hasInfo( int index ) {
		return false;
	}

	protected void onInfo( int index ) {}

	protected Image getIcon( int index ) {
		return null;
	}

	public void setHighlightingEnabled(boolean flag) {
//		float plusHeight = 0f;
//		for (Gizmo g : content.me) {
//			if (g instanceof RenderedTextBlock) {
//				float h = ((RenderedTextBlock) g).height();
//				((RenderedTextBlock) g).setHighlighting(flag);
//				plusHeight += ((RenderedTextBlock) g).height() - h;
//			}
//		}
//		if (plusHeight != 0) {
//			for (Gizmo g : members) {
//				if (g instanceof Button) {
//					((Button) g).setPos(((Button) g).left(), ((Button) g).top() + plusHeight);
//				}
//			}
//			resize(width, (int) (height + plusHeight));
//		}
	}

	public void appendMessage(String msg) {
		tfMessage.text(tfMessage.text() + " " + msg);
		layout(width);
	}
}