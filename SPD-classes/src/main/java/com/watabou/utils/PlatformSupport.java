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

package com.watabou.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.watabou.input.ControllerHandler;
import com.watabou.noosa.Game;

import java.util.HashMap;

public abstract class PlatformSupport {
	
	public abstract void updateDisplaySize();
	
	public abstract void updateSystemUI();

	public abstract boolean connectedToUnmeteredNetwork();

	public abstract boolean supportsVibration();

	public void vibrate( int millis ){
		if (ControllerHandler.isControllerConnected()) {
			ControllerHandler.vibrate(millis);
		} else {
			Gdx.input.vibrate( millis );
		}
	}

	public void setHonorSilentSwitch( boolean value ){
		//does nothing by default
	}

	public boolean openURI( String uri ){
		return Gdx.net.openURI( uri );
	}

	public void openFileExplorer(FileHandle selectedDirectory){
	}
	public boolean supportsOpenFileExplorer(){
		return false;
	}

	public void selectFile(Consumer<FileHandle> callback) {
//		JFileChooser dialog = new JFileChooser();
//		dialog.setDialogTitle("TestTitle");
//		dialog.setFileFilter(new FileNameExtensionFilter("Images", "png"));
//		dialog.setCurrentDirectory(new File(System.getProperty("user.home"),"Pictures"));
//		dialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
//		if (dialog.showDialog(null, UIManager.getString("FileChooser.openButtonText")) == JFileChooser.APPROVE_OPTION){
//			File selectedFile = dialog.getSelectedFile();
//
//			FileHandle dest = FileUtils.getFileHandle("temp_img.png");
//			new FileHandle(selectedFile).copyTo(dest);
//		}
	}

	public boolean canReadExternalFilesIfUserGrantsPermission() {
		return false;
	}

	public boolean canReadExternalFiles() {
		return false;
	}

	public FileHandle getDownloadDirectory(String fileName) {
		return null;
	}

	public boolean openNativeIDEWindow(Object luaCodeHolder, Object luaScript) {
		return false;
	}

	public void setOnscreenKeyboardVisible(boolean value){
		Gdx.input.setOnscreenKeyboardVisible(value);
	}

	//TODO should consider spinning this into its own class, rather than platform support getting ever bigger
	protected static HashMap<FreeTypeFontGenerator, HashMap<Integer, BitmapFont>> fonts;

	protected int pageSize;
	protected PixmapPacker packer;
	protected boolean systemfont;
	
	public abstract void setupFontGenerators(int pageSize, boolean systemFont );

	protected abstract FreeTypeFontGenerator getGeneratorForString( String input );

	public abstract String[] splitforTextBlock( String text, boolean multiline );

	public void resetGenerators(){
		resetGenerators( true );
	}

	public void resetGenerators( boolean setupAfter ){
		if (fonts != null) {
			for (FreeTypeFontGenerator generator : fonts.keySet()) {
				for (BitmapFont f : fonts.get(generator).values()) {
					f.dispose();
				}
				fonts.get(generator).clear();
				generator.dispose();
			}
			fonts.clear();
			if (packer != null) {
				for (PixmapPacker.Page p : packer.getPages()) {
					p.getTexture().dispose();
				}
				packer.dispose();
			}
			fonts = null;
		}
		if (setupAfter) setupFontGenerators(pageSize, systemfont);
	}

	public void reloadGenerators(){
		if (packer != null) {
			for (FreeTypeFontGenerator generator : fonts.keySet()) {
				for (BitmapFont f : fonts.get(generator).values()) {
					f.dispose();
				}
				fonts.get(generator).clear();
			}
			if (packer != null) {
				for (PixmapPacker.Page p : packer.getPages()) {
					p.getTexture().dispose();
				}
				packer.dispose();
			}
			packer = new PixmapPacker(pageSize, pageSize, Pixmap.Format.RGBA8888, 1, false);
		}
	}

	//flipped is needed because Shattered's graphics are y-down, while GDX graphics are y-up.
	//this is very confusing, I know.
	public BitmapFont getFont(int size, String text, boolean flipped, boolean border) {
		FreeTypeFontGenerator generator = getGeneratorForString(text);

		if (generator == null){
			return null;
		}

		int key = size;
		if (border) key += Short.MAX_VALUE; //surely we'll never have a size above 32k
		if (flipped) key = -key;
		if (!fonts.get(generator).containsKey(key)) {
			FreeTypeFontGenerator.FreeTypeFontParameter parameters = new FreeTypeFontGenerator.FreeTypeFontParameter();
			parameters.size = size;
			parameters.flip = flipped;
			if (border) {
				parameters.borderWidth = parameters.size / 10f;
			}
			if (size >= 20){
				parameters.renderCount = 2;
			} else {
				parameters.renderCount = 3;
			}
			parameters.hinting = FreeTypeFontGenerator.Hinting.None;
			parameters.spaceX = -(int) parameters.borderWidth;
			parameters.incremental = true;
			parameters.characters = "�";
			parameters.packer = packer;

			try {
				BitmapFont font = generator.generateFont(parameters);
				font.getData().missingGlyph = font.getData().getGlyph('�');
				fonts.get(generator).put(key, font);
			} catch ( Exception e ){
				Game.reportException(e);
				return null;
			}
		}

		return fonts.get(generator).get(key);
	}

}