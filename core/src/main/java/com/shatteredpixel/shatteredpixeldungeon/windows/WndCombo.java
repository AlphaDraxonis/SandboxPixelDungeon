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

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Combo;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;

public class WndCombo extends Window {

	private static final int WIDTH_P = 120;
	private static final int WIDTH_L = 180;

	private static final int MARGIN  = 2;

	public WndCombo( Combo combo ){
		super();

		int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;

		float pos = MARGIN;
		RenderedTextBlock title = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(this, "title")), 9);
		title.hardlight(TITLE_COLOR);
		title.setPos((width-title.width())/2, pos);
		title.maxWidth(width - MARGIN * 2);
		add(title);

		pos = title.bottom() + 3*MARGIN;

		for (Combo.ComboMove move : Combo.ComboMove.values()) {

			String text = "_" + Messages.titleCase(move.title()) + " " + Messages.get(this, "combo_req", move.comboReq) + ":_ " + move.desc(combo.getComboCount());
			RedButton moveBtn = new RedButton(text, 6){
				@Override
				protected void onClick() {
					super.onClick();
					hide();
					combo.useMove(move);
				}
			};
			moveBtn.leftJustify = true;
			moveBtn.multiline = true;
			moveBtn.setSize(width, moveBtn.reqHeight());
			moveBtn.setRect(0, pos, width, moveBtn.reqHeight());
			moveBtn.enable(combo.canUseMove(move));
			add(moveBtn);
			pos = moveBtn.bottom() + MARGIN;
		}

		resize(width, (int)pos);

	}


}
