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

package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.effects.ShadowBox;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.input.KeyBindings;
import com.watabou.input.KeyEvent;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.PointerArea;
import com.watabou.utils.GameMath;
import com.watabou.utils.Point;
import com.watabou.utils.Signal;

public class Window extends Group implements Signal.Listener<KeyEvent> {

	protected int width;
	protected int height;

	protected int xOffset;
	protected int yOffset;
	
	protected PointerArea blocker;
	protected ShadowBox shadow;
	protected NinePatch chrome;

	public static final int WHITE = 0xFFFFFF;
	public static final int TITLE_COLOR = 0xFFFF44;
	public static final int SILVER = 0xCCCCCC;
	public static final int GOLD = 0xFFC61A;
	public static final int EMERALD = 0x00CC00;
	public static final int SHPX_COLOR = 0x33BB33;
	
	public Window() {
		this( 0, 0, Chrome.get( Chrome.Type.WINDOW ) );
	}
	
	public Window( int width, int height ) {
		this( width, height, Chrome.get( Chrome.Type.WINDOW ) );
	}

	public Window( int width, int height, NinePatch chrome ) {
		super();
		
		blocker = new PointerArea( 0, 0, PixelScene.uiCamera.width, PixelScene.uiCamera.height ) {
			@Override
			protected void onClick( PointerEvent event ) {
				if (Window.this.parent != null && !Window.this.chrome.overlapsScreenPoint(
					(int) event.current.x,
					(int) event.current.y )) {
					
					onBackPressed();
				}
			}
		};
		blocker.camera = PixelScene.uiCamera;
		add( blocker );
		
		this.chrome = chrome;

		this.width = width;
		this.height = height;

		shadow = new ShadowBox();
		shadow.am = 0.5f;
		shadow.camera = PixelScene.uiCamera.visible ?
				PixelScene.uiCamera : Camera.main;
		add( shadow );

		chrome.x = -chrome.marginLeft();
		chrome.y = -chrome.marginTop();
		chrome.size(
			width - chrome.x + chrome.marginRight(),
			height - chrome.y + chrome.marginBottom() );
		add( chrome );
		
		camera = new Camera( 0, 0,
			(int)chrome.width,
			(int)chrome.height,
			PixelScene.defaultZoom );
		camera.x = (int)(Game.width - camera.width * camera.zoom) / 2;
		camera.y = (int)(Game.height - camera.height * camera.zoom) / 2;
		camera.y -= yOffset * camera.zoom;
		camera.scroll.set( chrome.x, chrome.y );
		Camera.add( camera );

		shadow.boxRect(
				camera.x / camera.zoom,
				camera.y / camera.zoom,
				chrome.width(), chrome.height );

		KeyEvent.addKeyListener( this );
	}
	
	public void resize( int w, int h ) {
		this.width = w;
		this.height = h;
		
		chrome.size(
			width + chrome.marginHor(),
			height + chrome.marginVer() );
		
		camera.resize( (int)chrome.width(), (int)chrome.height() );

		camera.x = (int)(Game.width - camera.screenWidth()) / 2;
		camera.x += xOffset * camera.zoom;

		camera.y = (int)(Game.height - camera.screenHeight()) / 2;
		camera.y += yOffset * camera.zoom;

		shadow.boxRect( camera.x / camera.zoom, camera.y / camera.zoom, chrome.width(), chrome.height() );
	}

	public int width() {
		return width;
	}

	public int height() {
		return height;
	}

	public Point getOffset(){
		return new Point(xOffset, yOffset);
	}

	public final void offset( Point offset ){
		offset(offset.x, offset.y);
	}

	//windows with scroll panes will likely need to override this and refresh them when offset changes
	public void offset( int xOffset, int yOffset ){
		camera.x -= this.xOffset * camera.zoom;
		this.xOffset = xOffset;
		camera.x += xOffset * camera.zoom;

		camera.y -= this.yOffset * camera.zoom;
		this.yOffset = yOffset;
		camera.y += yOffset * camera.zoom;

		shadow.boxRect( camera.x / camera.zoom, camera.y / camera.zoom, chrome.width(), chrome.height );
	}

	//ensures the window, with offset, does not go beyond a given margin
	public void boundOffsetWithMargin( int margin ){
		float x = camera.x / camera.zoom;
		float y = camera.y / camera.zoom;

		Camera sceneCam = PixelScene.uiCamera.visible ? PixelScene.uiCamera : Camera.main;

		int newXOfs = xOffset;
		if (x < margin){
			newXOfs += margin - x;
		} else if (x + camera.width > sceneCam.width - margin){
			newXOfs += (sceneCam.width - margin) - (x + camera.width);
		}

		int newYOfs = yOffset;
		if (y < margin){
			newYOfs += margin - y;
		} else if (y + camera.height > sceneCam.height - margin){
			newYOfs += (sceneCam.height - margin) - (y + camera.height);
		}

		offset(newXOfs, newYOfs);
	}
	
	public void hide() {
		if (parent != null) {
			parent.erase(this);
		}
		destroy();
	}
	
	@Override
	public void destroy() {
		super.destroy();
		
		Camera.remove( camera );
		KeyEvent.removeKeyListener( this );
	}

	@Override
	public boolean onSignal( KeyEvent event ) {
		if (event.pressed) {
			if (KeyBindings.getActionForKey( event ) == SPDAction.BACK
				|| KeyBindings.getActionForKey( event ) == SPDAction.WAIT){
				onBackPressed();
			}
		}
		
		return true;
	}
	
	public void onBackPressed() {
		hide();
	}

	public enum WindowSize {
		
		WIDTH_VERY_SMALL,
		WIDTH_SMALL,
		WIDTH_MEDIUM,
		WIDTH_LARGE_S,
		WIDTH_LARGE_M,
		WIDTH_LARGE,
		
		HEIGHT_VERY_SMALL,
		HEIGHT_SMALL,
		HEIGHT_MEDIUM,
		HEIGHT_LARGE;
		
		public int get() {
			switch (this) {
				case WIDTH_VERY_SMALL: return gateWidth(160, (int) (PixelScene.uiCamera.width * 0.9f), Float.MAX_VALUE);
				case WIDTH_SMALL:  return gateWidth(140, 180, PixelScene.uiCamera.width * 0.7f);
				case WIDTH_MEDIUM: return gateWidth(150, 200, PixelScene.uiCamera.width * 0.8f);
				case WIDTH_LARGE:  return gateWidth(160, WndTitledMessage.WIDTH_MAX, PixelScene.uiCamera.width * 0.9f);
				
				case WIDTH_LARGE_S:  return WIDTH_LARGE.get() - (PixelScene.landscape() ? 10 : 5);
				case WIDTH_LARGE_M:  return WIDTH_LARGE.get() - (PixelScene.landscape() ? 5 : 0);
				
				case HEIGHT_VERY_SMALL:  return (int) (PixelScene.uiCamera.height * 0.65f);
				case HEIGHT_SMALL:  return (int) (PixelScene.uiCamera.height * 0.8f);
				case HEIGHT_MEDIUM: return (int) (PixelScene.uiCamera.height * 0.85f);
				case HEIGHT_LARGE:  return (int) (PixelScene.uiCamera.height * 0.9f);
			}
			return 0;
		}
		
		private int gateWidth(float min, float val, float max) {
			if (min > PixelScene.uiCamera.width) min = PixelScene.uiCamera.width * 0.8f;
			return (int) GameMath.gate(min, val, Math.min(PixelScene.uiCamera.width, max));
		}
		
		private int gateHeight(float min, float val, float max) {
			if (min > PixelScene.uiCamera.height) min = PixelScene.uiCamera.height * 0.8f;
			return (int) GameMath.gate(min, val, Math.min(PixelScene.uiCamera.height, max));
		}
	}
}