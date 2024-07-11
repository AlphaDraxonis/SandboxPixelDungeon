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

package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.watabou.input.PointerEvent;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ScrollArea;
import com.watabou.noosa.Visual;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.GameMath;
import com.watabou.utils.Point;
import com.watabou.utils.PointF;

public class ScrollPaneWithScrollbar extends ScrollPane {

	private final ScrollbarThumb scrollbarVer, scrollbarHor;//TODO NEVER tested with horizontal scrolling!

	public ScrollPaneWithScrollbar(Component content) {

		super(content);

		scrollbarVer = new ScrollbarThumb(ScrollbarThumb.VERTICAL);
		add(scrollbarVer);

		scrollbarHor = new ScrollbarThumb(ScrollbarThumb.HORIZONTAL);
		add(scrollbarHor);
		scrollbarHor.symbol.angle = 90;
	}

	@Override
	protected void layout(boolean modifyContentCameraPosition) {

		boolean verVisible, horVisible;
		boolean newVerVisible = true, newHorVisible = false;

		float w, h;
		do {
			verVisible = newVerVisible;
			horVisible = newHorVisible;
			w = (width - (verVisible ? ScrollbarThumb.TOTAL_THICKNESS : 0));
			h = (height - (horVisible ? ScrollbarThumb.TOTAL_THICKNESS : 0));
			content.setRect(0, 0, w, h);
			newVerVisible = h < content.height();
			newHorVisible = w + 1 < content.width();
		} while (verVisible != newVerVisible || horVisible != newHorVisible);

		scrollbarVer.visible = scrollbarVer.active = newVerVisible;
		scrollbarHor.visible = scrollbarHor.active = newHorVisible;

		controller.x = x;
		controller.y = y;
		controller.width = w;
		controller.height = h;

		Camera cs = content.camera;
		if (modifyContentCameraPosition) {
			Point p = camera().cameraToScreen(x, y);
			cs.x = p.x;
			cs.y = p.y;
		}
		cs.resize((int) w, (int) h);

		layoutThumbs();

		thumbVer.setVisible(false);
	}

	@Override
	protected void layoutThumbs() {
		//TODO when the thumb reaches the minimum size of 13, it can be moved further than intended!
		if (scrollbarVer != null) {
			float availableHeight = height;
			float h = Math.max(13, availableHeight * Math.min(1, ScrollbarThumb.SCALE_FACTOR * availableHeight / content.height()));
			scrollbarVer.fullBarSize = availableHeight - h/2;
			scrollbarVer.setRect(x + width - ScrollbarThumb.TOTAL_THICKNESS + ScrollbarThumb.GAP, y + (availableHeight * content.camera.scroll.y / content.height()),
					ScrollbarThumb.THICKNESS, h);
		}
		if (scrollbarHor != null) {
			float availableWidth = (width - (scrollbarVer != null ? ScrollbarThumb.TOTAL_THICKNESS + ScrollbarThumb.GAP : 0));
			float w = Math.max(13, availableWidth * Math.min(1, ScrollbarThumb.SCALE_FACTOR * availableWidth / content.width()));
			scrollbarHor.fullBarSize = availableWidth - w/2;
			scrollbarHor.setRect(x + (availableWidth * content.camera.scroll.x / content.width()), y + height - ScrollbarThumb.TOTAL_THICKNESS + ScrollbarThumb.GAP,
					w, ScrollbarThumb.THICKNESS);
		}
	}

	private class ScrollbarThumb extends RedButton {

		public static final int GAP = 1, THICKNESS = 10, TOTAL_THICKNESS = THICKNESS + GAP;
		public static final float SCALE_FACTOR = 1.0f;//TODO doesn't really work atm
		public static final int VERTICAL = 0, HORIZONTAL = 1;

		private final int orientation;

		private Visual symbol;

		private float fullBarSize;

		public ScrollbarThumb(int orientation) {
			super("");
			this.orientation = orientation;
		}

		@Override
		protected void createChildren(Object... params) {

			//intentionally don't call super!

			hotArea = new ScrollArea(0, 0, 0, 0) {

				private boolean dragging;

				{
					doNotHover = true;
				}

				@Override
				protected void onPointerDown(PointerEvent event) {
					ScrollbarThumb.this.onPointerDown();
				}

				@Override
				protected void onPointerUp(PointerEvent event) {
					ScrollbarThumb.this.onPointerUp();
					dragging = false;
				}

				private PointF lastPos = new PointF();

				@Override
				protected void onDrag(PointerEvent event) {
					if (dragging) {
						scroll(event.current);
					} else if (!event.handled) {
						dragging = true;
						lastPos.set(event.current);
					}
				}

				private void scroll(PointF current) {
					Camera c = content.camera;
					float w = ScrollPaneWithScrollbar.this.width();
					float h = ScrollPaneWithScrollbar.this.height();

					PointF diff = PointF.diff(lastPos, current);
					diff.x = orientation == HORIZONTAL ? -diff.x * content.width() / fullBarSize : 0;
					diff.y = orientation == VERTICAL ? -diff.y * content.height() / fullBarSize : 0;
					c.shift(diff.invScale(c.zoom));

					if (orientation == HORIZONTAL) {
						c.scroll.x = GameMath.gate(0, c.scroll.x, content.width() - w);
					}
					if (orientation == VERTICAL) {
						c.scroll.y = GameMath.gate(0, c.scroll.y, content.height() - h);
					}

					layoutThumbs();

					lastPos.set(current);

					ScrollPaneWithScrollbar.this.onScroll();
				}
			};
			add(hotArea);

			symbol = Icons.SCROLL_BAR_THUMB_TOP.get();
			add(symbol);
		}

		@Override
		protected void layout() {
			super.layout();

			symbol.x = x + (width - symbol.width()) * 0.5f;
			symbol.y = y + (height - symbol.height()) * 0.5f;
		}
	}

}