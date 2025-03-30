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

package com.watabou.utils;

import java.util.ArrayList;

public class WatabouRect {

	//Auto-merging should not be possible here!

/*Also change RoomRect.class!*/	public int left;
/*Also change RoomRect.class!*/	public int top;
/*Also change RoomRect.class!*/	public int right;
/*Also change RoomRect.class!*/	public int bottom;
/*Also change RoomRect.class!*/
/*Also change RoomRect.class!*/	public WatabouRect() {
/*Also change RoomRect.class!*/		this( 0, 0, 0, 0 );
/*Also change RoomRect.class!*/	}
/*Also change RoomRect.class!*/
/*Also change RoomRect.class!*/	public WatabouRect(WatabouRect rect ) {
/*Also change RoomRect.class!*/		this( rect.left, rect.top, rect.right, rect.bottom );
/*Also change RoomRect.class!*/	}
/*Also change RoomRect.class!*/
/*Also change RoomRect.class!*/	public WatabouRect(int left, int top, int right, int bottom ) {
/*Also change RoomRect.class!*/		this.left	= left;
/*Also change RoomRect.class!*/		this.top	= top;
/*Also change RoomRect.class!*/		this.right	= right;
/*Also change RoomRect.class!*/		this.bottom	= bottom;
/*Also change RoomRect.class!*/	}
/*Also change RoomRect.class!*/
/*Also change RoomRect.class!*/	public int width() {
/*Also change RoomRect.class!*/		return right - left;
/*Also change RoomRect.class!*/	}
/*Also change RoomRect.class!*/
/*Also change RoomRect.class!*/	public int height() {
/*Also change RoomRect.class!*/		return bottom - top;
/*Also change RoomRect.class!*/	}
/*Also change RoomRect.class!*/
/*Also change RoomRect.class!*/	public int square() {
/*Also change RoomRect.class!*/		return width() * height();
/*Also change RoomRect.class!*/	}
/*Also change RoomRect.class!*/
/*Also change RoomRect.class!*/	public WatabouRect set(int left, int top, int right, int bottom ) {
/*Also change RoomRect.class!*/		this.left	= left;
/*Also change RoomRect.class!*/		this.top	= top;
/*Also change RoomRect.class!*/		this.right	= right;
/*Also change RoomRect.class!*/		this.bottom	= bottom;
/*Also change RoomRect.class!*/		return this;
/*Also change RoomRect.class!*/	}
/*Also change RoomRect.class!*/
/*Also change RoomRect.class!*/	public WatabouRect set(WatabouRect rect ) {
/*Also change RoomRect.class!*/		return set( rect.left, rect.top, rect.right, rect.bottom );
/*Also change RoomRect.class!*/	}
/*Also change RoomRect.class!*/
/*Also change RoomRect.class!*/	public WatabouRect setPos(int x, int y ) {
/*Also change RoomRect.class!*/		return set( x, y, x + (right - left), y + (bottom - top));
/*Also change RoomRect.class!*/	}
/*Also change RoomRect.class!*/
/*Also change RoomRect.class!*/	public WatabouRect shift(int x, int y ) {
/*Also change RoomRect.class!*/		return set( left+x, top+y, right+x, bottom+y );
/*Also change RoomRect.class!*/	}
/*Also change RoomRect.class!*/
/*Also change RoomRect.class!*/	public WatabouRect resize(int w, int h ){
/*Also change RoomRect.class!*/		return set( left, top, left+w, top+h);
/*Also change RoomRect.class!*/	}
/*Also change RoomRect.class!*/
/*Also change RoomRect.class!*/	public boolean isEmpty() {
/*Also change RoomRect.class!*/		return right <= left || bottom <= top;
/*Also change RoomRect.class!*/	}
/*Also change RoomRect.class!*/
/*Also change RoomRect.class!*/	public WatabouRect setEmpty() {
/*Also change RoomRect.class!*/		left = right = top = bottom = 0;
/*Also change RoomRect.class!*/		return this;
/*Also change RoomRect.class!*/	}
/*Also change RoomRect.class!*/
/*Also change RoomRect.class!*/	public WatabouRect intersect(WatabouRect other ) {
/*Also change RoomRect.class!*/		WatabouRect result = new WatabouRect();
/*Also change RoomRect.class!*/		result.left		= Math.max( left, other.left );
/*Also change RoomRect.class!*/		result.right	= Math.min( right, other.right );
/*Also change RoomRect.class!*/		result.top		= Math.max( top, other.top );
/*Also change RoomRect.class!*/		result.bottom	= Math.min( bottom, other.bottom );
/*Also change RoomRect.class!*/		return result;
/*Also change RoomRect.class!*/	}
/*Also change RoomRect.class!*/
/*Also change RoomRect.class!*/	public WatabouRect union(WatabouRect other ){
/*Also change RoomRect.class!*/		WatabouRect result = new WatabouRect();
/*Also change RoomRect.class!*/		result.left		= Math.min( left, other.left );
/*Also change RoomRect.class!*/		result.right	= Math.max( right, other.right );
/*Also change RoomRect.class!*/		result.top		= Math.min( top, other.top );
/*Also change RoomRect.class!*/		result.bottom	= Math.max( bottom, other.bottom );
/*Also change RoomRect.class!*/		return result;
/*Also change RoomRect.class!*/	}
/*Also change RoomRect.class!*/
/*Also change RoomRect.class!*/	public WatabouRect union(int x, int y ) {
/*Also change RoomRect.class!*/		if (isEmpty()) {
/*Also change RoomRect.class!*/			return set( x, y, x + 1, y + 1 );
/*Also change RoomRect.class!*/		} else {
/*Also change RoomRect.class!*/			if (x < left) {
/*Also change RoomRect.class!*/				left = x;
/*Also change RoomRect.class!*/			} else if (x >= right) {
/*Also change RoomRect.class!*/				right = x + 1;
/*Also change RoomRect.class!*/			}
/*Also change RoomRect.class!*/			if (y < top) {
/*Also change RoomRect.class!*/				top = y;
/*Also change RoomRect.class!*/			} else if (y >= bottom) {
/*Also change RoomRect.class!*/				bottom = y + 1;
/*Also change RoomRect.class!*/			}
/*Also change RoomRect.class!*/			return this;
/*Also change RoomRect.class!*/		}
/*Also change RoomRect.class!*/	}
/*Also change RoomRect.class!*/
/*Also change RoomRect.class!*/	public WatabouRect union(Point p ) {
/*Also change RoomRect.class!*/		return union( p.x, p.y );
/*Also change RoomRect.class!*/	}
/*Also change RoomRect.class!*/
/*Also change RoomRect.class!*/	public boolean inside( Point p ) {
/*Also change RoomRect.class!*/		return p.x >= left && p.x < right && p.y >= top && p.y < bottom;
/*Also change RoomRect.class!*/	}
/*Also change RoomRect.class!*/
/*Also change RoomRect.class!*/	public Point center() {
/*Also change RoomRect.class!*/		return new Point(
/*Also change RoomRect.class!*/				(left + right) / 2 + (((right - left) % 2) == 0 ? Random.Int( 2 ) : 0),
/*Also change RoomRect.class!*/				(top + bottom) / 2 + (((bottom - top) % 2) == 0 ? Random.Int( 2 ) : 0) );
/*Also change RoomRect.class!*/	}
/*Also change RoomRect.class!*/
/*Also change RoomRect.class!*/	public WatabouRect shrink(int d ) {
/*Also change RoomRect.class!*/		return new WatabouRect( left + d, top + d, right - d, bottom - d );
/*Also change RoomRect.class!*/	}
/*Also change RoomRect.class!*/
/*Also change RoomRect.class!*/	public WatabouRect shrink() {
/*Also change RoomRect.class!*/		return shrink( 1 );
/*Also change RoomRect.class!*/	}
/*Also change RoomRect.class!*/
/*Also change RoomRect.class!*/	public WatabouRect scale(int d ){
/*Also change RoomRect.class!*/		return new WatabouRect( left * d, top * d, right * d, bottom * d );
/*Also change RoomRect.class!*/	}
/*Also change RoomRect.class!*/
/*Also change RoomRect.class!*/	public ArrayList<Point> getPoints() {
/*Also change RoomRect.class!*/		ArrayList<Point> points = new ArrayList<>();
/*Also change RoomRect.class!*/		for (int i = left; i <= right; i++)
/*Also change RoomRect.class!*/			for (int j = top; j <= bottom; j++)
/*Also change RoomRect.class!*/				points.add(new Point(i, j));
/*Also change RoomRect.class!*/		return points;
/*Also change RoomRect.class!*/	}
/*Also change RoomRect.class!*/
/*Also change RoomRect.class!*/}
