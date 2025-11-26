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

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class WndOptions extends Window {
	
	protected static final int WIDTH_P = 120;
	protected static final int WIDTH_L = 144;
	
	protected static final int MARGIN 		= 2;
	protected static final int BUTTON_HEIGHT	= 18;
	
	private ScrollPane spForText, spForButtons;
	private Component messageComp, buttonsComp;
	
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
		
		initBody(options);
	}
	
	public WndOptions( String title, String message, String... options ) {
		this(title, message, true, options);
	}
	
	public WndOptions( String title, String message, boolean highlightEnabled, String... options ) {
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
		tfMessage.setHighlighting(highlightEnabled);
		
		initBody(options);
	}
	
	protected void initBody(String... options){
		
		buttonsComp = new Component();
		
		buttons = new RedButton[options.length];
		infos = new IconButton[options.length];
		
		for (int i=0; i < options.length; i++) {
			final int index = i;
			buttons[i] = new RedButton( options[i] ) {
				{
					setHighlightingEnabled(false);
				}
				@Override
				protected void onClick() {
					hide();
					onSelect( index );
				}
			};
			Image icon = getIcon(i);
			if (icon != null) buttons[i].icon(icon);
			buttons[i].multiline = true;
			buttonsComp.add( buttons[i] );
			
			if (hasInfo(i)) {
				infos[i] = new IconButton(Icons.get(Icons.INFO)){
					@Override
					protected void onClick() {
						onInfo( index );
					}
				};
				buttonsComp.add(infos[i]);
			}
			buttons[i].enable(enabled(i));
		}
		
		spForButtons = new ScrollPane(buttonsComp);
		add(spForButtons);
		
		messageComp = new Component() {
			{
				add(tfMessage);
			}
			@Override
			protected void layout() {
				tfMessage.setPos(x, y);
				width = tfMessage.width();
				height = tfMessage.height() + MARGIN;
			}
		};
		spForText = new ScrollPane(messageComp);
		add(spForText);
		
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
		
		
		float spaceForButtons = layoutButtons(width);
		buttonsComp.setSize(width, spaceForButtons);
		
		float textSpHeight = Math.min(
				tfMessage.height(),
				Math.max(PixelScene.uiCamera.height * 0.2f, PixelScene.uiCamera.height * 0.88f - pos - MARGIN - spaceForButtons - MARGIN)
		);
		boolean needsTextSp = tfMessage.height() > textSpHeight;
		
		float buttonsSpHeight = Math.min(
				spaceForButtons,
				PixelScene.uiCamera.height * 0.88f - pos - MARGIN - textSpHeight - MARGIN
		);
		
		textSpHeight += MARGIN;
		
		float totalMargins = buttonsSpHeight == 0f ? 0 : MARGIN;
		resize(width, (int)(pos + textSpHeight + /*MARGIN*/ + buttonsSpHeight + /*MARGIN*/ + totalMargins));
		
		spForText.setRect(0, pos, width, textSpHeight);
		spForButtons.setRect(0, spForText.bottom() + MARGIN, width, buttonsSpHeight);
		
	}
	
	protected float layoutButtons(int width) {
		float pos = 0;
		for (int i = 0; i < buttons.length; i++) {
			if (infos[i] == null) {
				buttons[i].setRect(0, pos, width, BUTTON_HEIGHT);
			} else {
				buttons[i].setRect(0, pos, width - BUTTON_HEIGHT, BUTTON_HEIGHT);
				infos[i].setRect(width-BUTTON_HEIGHT, pos, BUTTON_HEIGHT, BUTTON_HEIGHT);
			}

			pos += BUTTON_HEIGHT + MARGIN;
		}
		return pos - MARGIN;
	}
	
	@Override
	public void offset(int xOffset, int yOffset) {
		super.offset(xOffset, yOffset);
		layout(width);
	}
	
	protected boolean enabled( int index ){
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
	
	public String getMessage() {
		return tfMessage.text();
	}
}
