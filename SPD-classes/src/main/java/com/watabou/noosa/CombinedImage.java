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

package com.watabou.noosa;

import com.badlogic.gdx.graphics.Pixmap;
import com.watabou.gltextures.SmartTexture;
import com.watabou.utils.RectF;

/**
 * This class is so scuffed...
 */
public class CombinedImage extends Image {

	private Image[] images;
	private RectF[] defaultFrames;

	public CombinedImage(Image... images) {
		super();
		this.images = images;
		if (images.length > 0) {
			width = images[0].width;
			height = images[0].height;
		}
		texture = new SmartTexture(new Pixmap(1, 1, Pixmap.Format.RGB888));

		defaultFrames = new RectF[images.length];
		for (int i = 0; i < images.length; i++) {
			defaultFrames[i] = images[i].frame();
		}

		vertices = null;
		verticesBuffer = null;
	}

	@Override
	public void texture( Object tx ) {
	}

	@Override
	public void frame(RectF frame) {
		if (images != null) {
			for (int i = 0; i < images.length; i++) {
				int divisor = images[i].texture.height;
				RectF f = new RectF(defaultFrames[i]);
				f.left += frame.left / divisor;
				f.top += frame.top / divisor;
				f.right += (frame.width() - width) / divisor;
				f.bottom += (frame.bottom - height) / divisor;
				images[i].frame(f);
			}
		}
	}

	@Override
	public void frame( int left, int top, int width, int height ) {
		if (images != null) {
			for (int i = 0; i < images.length; i++) {
				RectF frame = new RectF(defaultFrames[i]);
				frame.left += left;
				frame.top += top;
				frame.right += this.width - width;
				frame.bottom += this.height - height;
				images[i].frame(frame);
			}
		}
	}

	@Override
	public RectF frame() {
		return new RectF(0, 0, width, height);
	}

	@Override
	public void copy( Image other ) {
		if (other instanceof CombinedImage) {
			if (images != null) {
				for (Image img : images) {
					img.destroy();
				}
			}

			width = other.width;
			height = other.height;

			scale = other.scale;

			images = ((CombinedImage) other).images;
			defaultFrames = new RectF[images.length];
			for (int i = 0; i < images.length; i++) {
				defaultFrames[i] = images[i].frame();
			}
		}
	}

	@Override
	protected void updateFrame() {
	}

	@Override
	protected void updateVertices() {
	}

	@Override
	protected void updateMatrix() {
	}
	
	@Override
	public void draw() {
		if (images != null) {
			for (Image img : images) {
				img.parent = parent;
				img.x = x;
				img.y = y;
				img.scale = scale;
				img.origin = origin;
				img.rm = rm;
				img.gm = gm;
				img.bm = bm;
				img.am = am;
				img.ra = ra;
				img.ga = ga;
				img.ba = ba;
				img.aa = aa;
				img.speed = speed;
				img.acc = acc;
				img.angle = angle;
				img.angularSpeed = angularSpeed;
				img.draw();
			}
			dirty = false;
		}
	}


	//---------------------------------------------


	@Override
	public void destroy() {
		super.destroy();
		if (images != null) {
			for (Image img : images) {
				img.destroy();
			}
		}
	}

	@Override
	public void update() {
		super.update();
		if (images != null) {
			for (Image img : images) {
				img.update();
			}
		}
	}

	@Override
	public void kill() {
		super.kill();
		if (images != null) {
			for (Image img : images) {
				img.kill();
			}
		}
	}
}