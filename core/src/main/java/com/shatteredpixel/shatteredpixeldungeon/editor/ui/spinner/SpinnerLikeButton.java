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
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.noosa.Image;

public class SpinnerLikeButton extends StyledButtonWithIconAndText {

	private final AbstractSpinnerModel model;

	protected RenderedTextBlock valueText;
	protected Image valueIcon;

	public SpinnerLikeButton(AbstractSpinnerModel model, String label) {
		this(model, label, -1);
	}

	public SpinnerLikeButton(AbstractSpinnerModel model, String label, int size) {
		super(Chrome.Type.GREY_BUTTON_TR, label, size);

		this.model = model;

		valueText = PixelScene.renderTextBlock(model.displayString(model.getValue()), Math.max((size == -1 ? textSize() : size)-2, 6));
		add(valueText);

		valueIcon = model.displayIcon(model.getValue());
		if (valueIcon != null) {
			add(valueIcon);
		}

		model.setValue(model.getValue());

		model.valueDisplay = new AbstractSpinnerModel.ValueDisplay() {
			@Override
			public void showValue(Object value) {
				SpinnerLikeButton.this.valueText.text(model.displayString(value));

				if (valueIcon != null) {
					valueIcon.remove();
					valueIcon.destroy();
				}
				valueIcon = model.displayIcon(value);
				if (valueIcon != null) {
					SpinnerLikeButton.this.add(valueIcon);
				}

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

		float spaceForValueIcon = valueIcon == null ? 0 : getValueIconHeight() + 3;

		boolean layoutLabel = text != null && !text.text().equals("");
		if (layoutLabel) {
			if (multiline) text.maxWidth((int) width() - bg.marginHor());
			text.setPos(
					x + (width() + text.width()) / 2f - text.width(),
					(icon == null ? y + (contentHeight - text.height() - valueText.height() - spaceForValueIcon) / 2f :
							y + (contentHeight - icon.height() - text.height() - valueText.height() - spaceForValueIcon) / 2f + 1 + icon.height())
			);
			PixelScene.align(text);

		}

		if (valueText != null && !valueText.text().equals("")) {
			if (multiline) valueText.maxWidth((int) width() - bg.marginHor());
			valueText.setPos(
					x + (width() + valueText.width()) / 2f - valueText.width(),
					spaceForValueIcon
							+ (layoutLabel ? text.bottom() + 4
							: (icon == null ? y + (contentHeight - valueText.height()) / 2f :
							y + (contentHeight - icon.height() - valueText.height()) / 2f + 1 + icon.height()))
			);
			PixelScene.align(valueText);

		}

		if (icon != null) {
			icon.x = x + (width() - icon.width()) / 2f + 1;
			icon.y = text.top() - 2 - icon.height();
			PixelScene.align(icon);
		}

		if (valueIcon != null) {
			valueIcon.x = x + (width() - valueIcon.width()) / 2f + 1;
			valueIcon.y = valueText.top() - 2 - getValueIconHeight();
			PixelScene.align(valueIcon);
		}

		if (leftJustify) throw new IllegalArgumentException("leftJustify not supported!");
	}

	@Override
	public float getMinimumHeight(float width) {
		if (multiline) {
			text.maxWidth((int) width - bg.marginHor());
			valueText.maxWidth((int) width - bg.marginHor());
		}
		return text.height() + 4 + valueText.height() + bg.marginVer() + 4
				+ (icon == null ? 0 : icon.height() + 3)
				+ (valueIcon == null ? 0 : getValueIconHeight() + 3);
	}

	protected float getValueIconHeight() {
		return valueIcon == null ? 0 : valueIcon.height();
	}

	public void setValue(Object value) {
		model.setValue(value);
	}

	@Override
	protected void onClick() {
		model.setValue(model.getNextValue());
		if (!isClickHolding()) afterClick();
	}

	@Override
	protected boolean onLongClick() {
		model.setValue(model.getPreviousValue());
		if (!isClickHolding()) afterClick();
		return true;
	}
	
	@Override
	protected int getClicksPerSecondWhenHolding() {
		return 0;
	}

	@Override
	protected void onPointerUp() {
		super.onPointerUp();
		afterClick();
	}

	protected void afterClick() {
	}
	
	public void addChangeListener(Runnable listener) {
		model.addChangeListener(listener);
	}
	
	public Object getValue() {
		return model.getValue();
	}
}