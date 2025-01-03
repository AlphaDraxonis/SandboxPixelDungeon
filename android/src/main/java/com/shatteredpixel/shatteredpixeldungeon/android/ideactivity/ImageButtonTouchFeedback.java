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

package com.shatteredpixel.shatteredpixeldungeon.android.ideactivity;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import com.watabou.NotAllowedInLua;
import com.watabou.noosa.audio.Sample;

@NotAllowedInLua
public class ImageButtonTouchFeedback implements View.OnTouchListener {

	public static ImageButtonTouchFeedback attach(ImageButton button) {
		ImageButtonTouchFeedback result = new ImageButtonTouchFeedback(button);
		button.setOnTouchListener(result);
		button.setSoundEffectsEnabled(false);
		return result;
	}

	private final ImageButton button;
	private final Drawable originalImage, brighterImage;

	public ImageButtonTouchFeedback(ImageButton button) {
		this.button = button;
		originalImage = button.getDrawable().mutate();
		brighterImage = setBrightness(originalImage.getConstantState().newDrawable(), 1.2f);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				button.setImageDrawable(brighterImage);
				Sample.INSTANCE.play( "sounds/click.mp3" );
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				button.setImageDrawable(originalImage);
				break;
		}
		return false;
	}


	private static android.graphics.drawable.Drawable setBrightness(android.graphics.drawable.Drawable drawable, float factor) {
		ColorMatrix colorMatrix = new ColorMatrix();
		colorMatrix.set(new float[] {
				factor, 0, 0, 0, 0,
				0, factor, 0, 0, 0,
				0, 0, factor, 0, 0,
				0, 0, 0, 1, 0
		});

		ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
		drawable.setColorFilter(colorFilter);
		return drawable;
	}
}