/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
 *
 * Sandbox Pixel Dungeon
 * Copyright (C) 2023-2025 AlphaDraxonis
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

package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.ui.Component;

public class CreateCancelComp extends Component {

	private final RedButton positiveButton, negativeButton;
	public float divider = 0.5f;
	public int MARGIN = 2;
	public float BUTTON_HEIGHT = 18;

	public CreateCancelComp(String positive, String negative) {
		this();
		setLabels(positive, negative);
	}

	public CreateCancelComp() {

		positiveButton = new RedButton(Messages.get(this, "positive")) {
			@Override
			protected void onClick() {
				onPositive();
			}
		};
		add(positiveButton);

		negativeButton = new RedButton(Messages.get(this, "negative")) {
			@Override
			protected void onClick() {
				onNegative();
			}
		};
		add(negativeButton);

	}

	public RedButton getPositiveButton() {
		return positiveButton;
	}

	public RedButton getNegativeButton() {
		return negativeButton;
	}

	protected void onPositive() {
	}

	protected void onNegative() {
	}

	public void setLabels(String positive, String negative) {
		positiveButton.text(positive);
		negativeButton.text(negative);
	}

	@Override
	protected void layout() {
		float btnWidth = width - MARGIN * 3;
		positiveButton.setRect(x + MARGIN, y, btnWidth * divider, BUTTON_HEIGHT);
		negativeButton.setRect(positiveButton.right() + MARGIN, y, btnWidth * (1-divider), BUTTON_HEIGHT);
		height = BUTTON_HEIGHT;
	}
}
