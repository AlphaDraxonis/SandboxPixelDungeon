/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
 *
 * Sandbox Pixel Dungeon
 * Copyright (C) 2023-2024 AlphaDraxonis
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

package com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledButtonWithIconAndText;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;

public class SpinnerLikeButton extends StyledButtonWithIconAndText {

	private final AbstractSpinnerModel model;

	protected RenderedTextBlock value;

	public SpinnerLikeButton(AbstractSpinnerModel model, String label) {
		this(model, label, -1);
	}

	public SpinnerLikeButton(AbstractSpinnerModel model, String label, int size) {
		super(Chrome.Type.GREY_BUTTON_TR, label, size);

		this.model = model;

		value = PixelScene.renderTextBlock(model.displayString(model.getValue()), Math.max((size == -1 ? textSize() : size)-2, 6));
		add(value);

		model.setValue(model.getValue());

		model.valueDisplay = new AbstractSpinnerModel.ValueDisplay() {
			@Override
			public void showValue(Object value) {
				SpinnerLikeButton.this.value.text(value == null ? "<null>": value.toString());
				SpinnerLikeButton.this.layout();
			}

			@Override
			public void enableValueField(boolean flag) {
				//do nothing here
			}
		};

	}

	@Override
	protected void layout() {

		height = Math.max(getMinimumHeight(width()), height());

		super.layout();

		float contentHeight = height();

		boolean layoutLabel = text != null && !text.text().equals("");
		if (layoutLabel) {
			if (multiline) text.maxWidth((int) width() - bg.marginHor());
			text.setPos(
					x + (width() + text.width()) / 2f - text.width(),
					(icon == null ? y + (contentHeight - text.height() - value.height()) / 2f :
							y + (contentHeight - icon.height() - text.height() - value.height()) / 2f + 1 + icon.height())
			);
			PixelScene.align(text);

		}

		if (value != null && !value.text().equals("")) {
			if (multiline) value.maxWidth((int) width() - bg.marginHor());
			value.setPos(
					x + (width() + value.width()) / 2f - value.width(),
					(layoutLabel ? text.bottom() + 4
							: (icon == null ? y + (contentHeight - value.height()) / 2f :
							y + (contentHeight - icon.height() - value.height()) / 2f + 1 + icon.height()))
			);
			PixelScene.align(value);

		}

		if (icon != null) {
			icon.x = x + (width() - icon.width()) / 2f + 1;
			icon.y = text.top() - 2 - icon.height();
			PixelScene.align(icon);
		}

		if (leftJustify) throw new IllegalArgumentException("leftJustify not supported!");
	}

	@Override
	public float getMinimumHeight(float width) {
		if (multiline) {
			text.maxWidth((int) width - bg.marginHor());
			value.maxWidth((int) width - bg.marginHor());
		}
		if (icon == null) return text.height() + 4 + value.height() + bg.marginVer();
		return icon.height() + text.height() + 4 + value.height() + 3 + bg.marginVer();
	}

	public void setValue(Wand.RechargeRule rechargeRule) {
		model.setValue(rechargeRule);
	}

	@Override
	protected void onClick() {
		model.setValue(model.getNextValue());
		if (!isClickHolding()) afterClick();
	}

	@Override
	protected int getClicksPerSecondWhenHolding() {
		return model.getClicksPerSecondWhileHolding();
	}

	@Override
	protected void onPointerUp() {
		super.onPointerUp();
		afterClick();
	}

	protected void afterClick() {
	}
}