package com.shatteredpixel.shatteredpixeldungeon.levels.rooms;

import com.shatteredpixel.shatteredpixeldungeon.GameObject;
import com.watabou.utils.Bundle;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class RoomRect extends GameObject {

		public int left;
		public int top;
		public int right;
		public int bottom;

		public RoomRect() {
			this( 0, 0, 0, 0 );
		}

		public RoomRect(RoomRect rect ) {
			this( rect.left, rect.top, rect.right, rect.bottom );
		}

		public RoomRect(int left, int top, int right, int bottom) {
			this.left	= left;
			this.top	= top;
			this.right	= right;
			this.bottom	= bottom;
		}

		public int width() {
			return right - left;
		}

		public int height() {
			return bottom - top;
		}

		public int square() {
			return width() * height();
		}

		public RoomRect set(int left, int top, int right, int bottom ) {
			this.left	= left;
			this.top	= top;
			this.right	= right;
			this.bottom	= bottom;
			return this;
		}

		public RoomRect set(RoomRect rect ) {
			return set( rect.left, rect.top, rect.right, rect.bottom );
		}

		public RoomRect setPos(int x, int y ) {
			return set( x, y, x + (right - left), y + (bottom - top));
		}

		public RoomRect shift(int x, int y ) {
			return set( left+x, top+y, right+x, bottom+y );
		}

		public RoomRect resize(int w, int h ){
			return set( left, top, left+w, top+h);
		}

		public boolean isEmpty() {
			return right <= left || bottom <= top;
		}

		public RoomRect setEmpty() {
			left = right = top = bottom = 0;
			return this;
		}

		public RoomRect intersect(RoomRect other ) {
			RoomRect result = new RoomRect();
			result.left		= Math.max( left, other.left );
			result.right	= Math.min( right, other.right );
			result.top		= Math.max( top, other.top );
			result.bottom	= Math.min( bottom, other.bottom );
			return result;
		}

		public RoomRect union(RoomRect other ){
			RoomRect result = new RoomRect();
			result.left		= Math.min( left, other.left );
			result.right	= Math.max( right, other.right );
			result.top		= Math.min( top, other.top );
			result.bottom	= Math.max( bottom, other.bottom );
			return result;
		}

		public RoomRect union(int x, int y ) {
			if (isEmpty()) {
				return set( x, y, x + 1, y + 1 );
			} else {
				if (x < left) {
					left = x;
				} else if (x >= right) {
					right = x + 1;
				}
				if (y < top) {
					top = y;
				} else if (y >= bottom) {
					bottom = y + 1;
				}
				return this;
			}
		}

		public RoomRect union(Point p ) {
			return union( p.x, p.y );
		}

		public boolean inside( Point p ) {
			return p.x >= left && p.x < right && p.y >= top && p.y < bottom;
		}

		public Point center() {
			return new Point(
					(left + right) / 2 + (((right - left) % 2) == 0 ? Random.Int( 2 ) : 0),
					(top + bottom) / 2 + (((bottom - top) % 2) == 0 ? Random.Int( 2 ) : 0) );
		}

		public RoomRect shrink(int d ) {
			return new RoomRect( left + d, top + d, right - d, bottom - d );
		}

		public RoomRect shrink() {
			return shrink( 1 );
		}

		public RoomRect scale(int d ){
			return new RoomRect( left * d, top * d, right * d, bottom * d );
		}

		public ArrayList<Point> getPoints() {
			ArrayList<Point> points = new ArrayList<>();
			for (int i = left; i <= right; i++)
				for (int j = top; j <= bottom; j++)
					points.add(new Point(i, j));
			return points;
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			throw new RuntimeException("RoomRect should be abstract, and this method is not callable!");
		}

		@Override
		public void storeInBundle(Bundle bundle) {
			throw new RuntimeException("RoomRect should be abstract, and this method is not callable!");
		}
	}