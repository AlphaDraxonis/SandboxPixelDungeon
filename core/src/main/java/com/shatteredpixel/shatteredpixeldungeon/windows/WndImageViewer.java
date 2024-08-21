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
		super(
				Math.min((int) (PixelScene.uiCamera.width * 0.9), image.texture.width / PixelScene.defaultZoom + 1),
				Math.min((int) (PixelScene.uiCamera.height * 0.9), image.texture.height / PixelScene.defaultZoom + 5)
		);
		initComponents(null, new Body(image), null, 0.5f, 0f);
	}

	private static class Body extends Component {
		private final Image image;

		private Body(Image image) {
			this.image = image;
			image.scale.set(1f / PixelScene.defaultZoom);
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