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

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.editor.ui.SimpleWindow;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class WndImageViewer extends SimpleWindow {

	public WndImageViewer(Image image) {
		float scaleX = 1f, scaleY = 1f;
		float prefWidth = image.texture.width / PixelScene.defaultZoom + 1;
		if (PixelScene.uiCamera.width * 0.9f < prefWidth) {
			scaleX = (PixelScene.uiCamera.width * 0.9f - 1) * PixelScene.defaultZoom / image.texture.width;
		}
		float prefHeight = image.texture.height / PixelScene.defaultZoom + 5;
		if (PixelScene.uiCamera.height * 0.9f < prefHeight) {
			scaleY = (PixelScene.uiCamera.height * 0.9f - 5) * PixelScene.defaultZoom / image.texture.height;
		}
		float neededScale = Math.min(scaleX, scaleY);
		if (neededScale == 1f) {
			image.scale.set(1f / PixelScene.defaultZoom);
			resize((int) prefWidth, (int) prefHeight);
		} else {
			image.scale.set(neededScale / PixelScene.defaultZoom);
			if (scaleX < scaleY) {
				resize(
						(int) (prefWidth * neededScale + 1),
						(int) Math.min(PixelScene.uiCamera.height * 0.9f, prefHeight * neededScale + 5)
				);
			} else {
				resize(
						(int) Math.min(PixelScene.uiCamera.width * 0.9f, prefWidth * neededScale + 1),
						(int) (prefHeight * neededScale + 5)
				);
			}
			
		}
		
//		super(
//				Math.min((int) (PixelScene.uiCamera.width * 0.9f), image.texture.width / PixelScene.defaultZoom + 1),
//				Math.min((int) (PixelScene.uiCamera.height * 0.9f), image.texture.height / PixelScene.defaultZoom + 5)
//		);
//		image.scale.set(1f / PixelScene.defaultZoom);
		
		initComponents(null, new Body(image), null, 0.5f, 0f);
	}

	private static final class Body extends Component {
		private final Image image;

		private Body(Image image) {
			this.image = image;
			add(image);
		}

		@Override
		protected void layout() {
			image.x = x + (width - image.width()) / 2 + 1;
			image.y = y + (height - image.height()) / 2;
			width = image.width();
			height = image.height();
		}

		@Override
		public float width() {
			return image.width();
		}

		@Override
		public float height() {
			return image.height();
		}
	}

}