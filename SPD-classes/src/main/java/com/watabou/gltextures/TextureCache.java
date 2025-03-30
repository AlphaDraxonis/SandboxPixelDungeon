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

package com.watabou.gltextures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.watabou.NotAllowedInLua;
import com.watabou.glwrap.Texture;
import com.watabou.noosa.Game;
import com.watabou.utils.FileUtils;

import java.util.HashMap;

@NotAllowedInLua
public class TextureCache {

	public static final String EXTERNAL_ASSET_PREFIX = "external/";
	private static final int EXTERNAL_ASSET_PREFIX_LENGTH = EXTERNAL_ASSET_PREFIX.length();

	private static HashMap<Object,SmartTexture> all = new HashMap<>();

	public synchronized static SmartTexture createSolid( int color ) {
		final String key = "1x1:" + color;
		
		if (all.containsKey( key )) {
			
			return all.get( key );
			
		} else {
			
			Pixmap pixmap =new Pixmap( 1, 1, Pixmap.Format.RGBA8888 );
			// convert from Noosa ARGB to libGdx RGBA
			pixmap.setColor( (color << 8) | (color >>> 24) );
			pixmap.fill();
			
			SmartTexture tx = new SmartTexture( pixmap );
			all.put( key, tx );
			
			return tx;
		}
	}
	
	public synchronized static SmartTexture createGradient( int... colors ) {
		
		final String key = "" + colors;
		
		if (all.containsKey( key )) {
			
			return all.get( key );
			
		} else {
			
			Pixmap pixmap = new Pixmap( colors.length, 1, Pixmap.Format.RGBA8888);
			for (int i=0; i < colors.length; i++) {
				// convert from Noosa ARGB to libGdx RGBA
				pixmap.drawPixel( i, 0, (colors[i] << 8) | (colors[i] >>> 24) );
			}
			SmartTexture tx = new SmartTexture( pixmap );

			tx.filter( Texture.LINEAR, Texture.LINEAR );
			tx.wrap( Texture.CLAMP, Texture.CLAMP );

			all.put( key, tx );
			return tx;
		}
		
	}

	//texture is created at given size, but size is not enforced if it already exists
	//texture contents are also not enforced, make sure you know the texture's state!
	public synchronized static SmartTexture create( Object key, int width, int height ) {

		if (all.containsKey( key )) {

			return all.get( key );

		} else {

			SmartTexture tx = new SmartTexture(new Pixmap( width, height, Pixmap.Format.RGBA8888 ));

			tx.filter( Texture.LINEAR, Texture.LINEAR );
			tx.wrap( Texture.CLAMP, Texture.CLAMP );

			all.put( key, tx );

			return tx;
		}
	}
	
	public synchronized static void remove( Object key ){
		SmartTexture tx = all.get( key );
		if (tx != null){
			all.remove(key);
			tx.delete();
		}
	}

	public synchronized static SmartTexture get( Object src ) {
		
		if (all.containsKey( src )) {
			
			return all.get( src );
			
		} else if (src instanceof SmartTexture) {
			
			return (SmartTexture)src;
			
		} else {

			SmartTexture tx = new SmartTexture( getBitmap( src ) );
			all.put( src, tx );
			return tx;
		}
		
	}

	public synchronized static SmartTexture getFromCurrentSavePath( String src ) {
		if (all.containsKey( src )) {
			return all.get( src );
		} else {
			try {
				SmartTexture tx = new SmartTexture(new Pixmap(FileUtils.getFileHandle(src)));
				all.put(src, tx);
				return tx;
			} catch (Exception ex) {
				return null;
			}
		}
	}

	public synchronized static void clear() {
		
		for (Texture txt : all.values()) {
			txt.delete();
		}
		all.clear();
		
	}
	
	public synchronized static void reload() {
		for (SmartTexture tx : all.values()) {
			tx.reload();
		}
	}
	
	public static Pixmap getBitmap( Object src ) {
		
		try {
			if (src instanceof Integer){
				
				//libGDX does not support android resource integer handles, and they were
				//never used by the game anyway, should probably remove this entirely
				return null;
				
			} else if (src instanceof String) {

				String s = (String) src;
				if (s.startsWith(EXTERNAL_ASSET_PREFIX)) {
					return new Pixmap(FileUtils.getFileHandle(s.substring(EXTERNAL_ASSET_PREFIX_LENGTH)));
				} else {
					return new Pixmap(Gdx.files.internal(s));
				}

			} else if (src instanceof FileHandle) {

				return new Pixmap((FileHandle) src);
				
			} else if (src instanceof Pixmap) {

				return (Pixmap) src;
				
			} else {
				
				return null;
				
			}
		} catch (Exception e) {
			
			Game.reportException(e);
			return null;
			
		}
	}
	
	public synchronized static boolean contains( Object key ) {
		return all.containsKey( key );
	}
	
}
