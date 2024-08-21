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

package com.shatteredpixel.shatteredpixeldungeon.usercontent.ui;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.EditorInventoryWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.CategoryScroller;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.usercontent.CustomObject;
import com.shatteredpixel.shatteredpixeldungeon.usercontent.ResourcePath;
import com.shatteredpixel.shatteredpixeldungeon.usercontent.UserContentManager;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndImageViewer;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.NotAllowedInLua;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

import java.util.*;

@NotAllowedInLua
public class TabResourceFiles extends WndUserContent.TabCustomObjs {

	private /*final*/ CategoryScroller.Category[] categories;

	protected List<Map.Entry<String, FileHandle>> imageFiles, soundFiles, luaFiles, textFiles;

	public TabResourceFiles(EditorInventoryWindow window) {
		super();
		add( categoryScroller = new CategoryScroller(createCategories(), window) );
	}

	protected CategoryScroller.Category[] createCategories() {

		if (categories == null) {
			categories = new CategoryScroller.Category[4];
		}

		Map<String, FileHandle> pathsInFileSystem = new HashMap<>(UserContentManager.allResourcePaths);
		for (Map.Entry<String, FileHandle> entry : UserContentManager.allResourcePaths.entrySet()) {
			if (!includeExtension(entry.getValue().extension())) {
				pathsInFileSystem.remove(entry.getKey());
			}
		}

		imageFiles = new ArrayList<>();
		soundFiles = new ArrayList<>();
		luaFiles = new ArrayList<>();
		textFiles = new ArrayList<>();

		for (Map.Entry<String, FileHandle> entry : pathsInFileSystem.entrySet()) {
			if (ResourcePath.isImage(entry.getValue().extension())) {
				imageFiles.add(entry);
			} else if (ResourcePath.isSound(entry.getValue().extension())) {
				textFiles.add(entry);
			} else if (ResourcePath.isLua(entry.getValue().extension())) {
				luaFiles.add(entry);
			} else if (ResourcePath.isText(entry.getValue().extension())) {
				textFiles.add(entry);
			}
		}

		Collections.sort(imageFiles, (o1, o2) -> o1.getKey().compareTo(o2.getKey()));
		Collections.sort(textFiles, (o1, o2) -> o1.getKey().compareTo(o2.getKey()));
		Collections.sort(luaFiles, (o1, o2) -> o1.getKey().compareTo(o2.getKey()));
		Collections.sort(textFiles, (o1, o2) -> o1.getKey().compareTo(o2.getKey()));

		for (int i = 0; i < categories.length; i++) {
			final int index = i;
			categories[i] = new CategoryScroller.Category() {

				@Override
				protected List<?> createItems(boolean required) {

					switch (index) {
						case 0: return imageFiles;
						case 1: return soundFiles;
						case 2: return luaFiles;
						case 3: return textFiles;
					}

					return new ArrayList<>();
				}

				@Override
				protected Component createListItem(Object object, EditorInventoryWindow window) {
					return new FilePathListItem(((Map.Entry<String, FileHandle>) object)) {
						@Override
						protected void onClick() {
							TabResourceFiles.this.onClick(data);
						}

						@Override
						protected boolean onLongClick() {
							return TabResourceFiles.this.onLongClick(data);
						}
					};
				}

				@Override
				protected Image getImage() {
					return new ItemSprite();
				}

				@Override
				protected String getName() {
					return "test";
				}
			};
		}

		return categories;
	}

	protected boolean includeExtension(String extension) {
		return true;
	}

	protected void onClick(Map.Entry<String, FileHandle> path) {
		viewResource(path);
	}

	protected boolean onLongClick(Map.Entry<String, FileHandle> path) {
		return false;
	}

	public static void viewResource(Map.Entry<String, FileHandle> path) {
		viewResource(path.getKey(), path.getValue());
	}

	public static void viewResource(String path, FileHandle file) {
		String extension = file.extension();
		if (ResourcePath.isImage(extension)) {
			DungeonScene.show(new WndImageViewer(new Image(file)));
		}
		else if (ResourcePath.isText(extension) || ResourcePath.isLua(extension)) {
			Image icon = getSpriteForPath(path, file);
			try {
				DungeonScene.show(new WndTitledMessage(icon, path, file.readString()) {
					{
						setHighlightingEnabled(ResourcePath.isText(extension));
					}
				});
			} catch (GdxRuntimeException ex) {
				DungeonScene.show(new WndError(ex));
			}
		}
	}

	@Override
	protected String addBtnLabel() {
		return "Reload files";
	}

	@Override
	protected void onAddBtnClick() {
		//reload files, instead of adding something
		CustomDungeonSaves.loadAllCustomResourceFiles();
		createCategories();
		categoryScroller.updateItemsInCategories(false);
		categoryScroller.selectCategory(categoryScroller.getSelectedCatIndex());
	}

	@Override
	public Image createIcon() {
		return new ItemSprite();
	}

	@Override
	public String hoverText() {
		return "null";
	}

	public static Image getSpriteForPath(String path) {
		return getSpriteForPath(path, CustomObject.getResourceFile(path, true));
	}

	public static Image getSpriteForPath(String path, FileHandle file) {
		if (path == null) return new ItemSprite(ItemSpriteSheet.NO_IMAGE);
		String extension = ResourcePath.pathToExtension(path);
		if (ResourcePath.isLua(extension)) return new ItemSprite(ItemSpriteSheet.SEWER_PAGE);

		if (ResourcePath.isImage(extension)) {
			Image result = new Image(file);
			if (result.texture.width > ItemSpriteSheet.SIZE) {
				result.scale.set(ItemSpriteSheet.SIZE / (float) (Math.max(result.texture.width, result.texture.height)));
			} else if (result.texture.height > ItemSpriteSheet.SIZE * 2) {
				result.scale.set(ItemSpriteSheet.SIZE * 2 / (float) result.texture.height);
			}
			return result;
		}

		return new ItemSprite(ItemSpriteSheet.SOMETHING);
	}

	private static class FilePathListItem extends Button {

		protected static final int ICON_SIZE = 16;

		protected Image icon;
		protected RenderedTextBlock text;
		protected ColorBlock line;

		protected final Map.Entry<String, FileHandle> data;

		public FilePathListItem(Map.Entry<String, FileHandle> data) {

			this.data = data;

			if (ResourcePath.isImage(data.getValue().extension())) {
				icon = new Image(data.getValue());
				if (icon.texture.width > ICON_SIZE) {
					icon.scale.set(ICON_SIZE / (float) (Math.max(icon.texture.width, icon.texture.height)));
				} else if (icon.texture.height > ICON_SIZE * 2) {
					icon.scale.set(ICON_SIZE*2 / (float) icon.texture.height);
				}
				add(icon);
			} else {
				icon = getSpriteForPath(data.getKey(), data.getValue());
				add(icon);
			}

			text = PixelScene.renderTextBlock(data.getKey(), 9);
			text.setHighlighting(false);
			add(text);

			line = new ColorBlock(1, 1, ColorBlock.SEPARATOR_COLOR);
			add(line);
		}

		@Override
		protected void layout() {

			height = ICON_SIZE + 4;

			if (icon != null) {
				icon.y = y + 1 + (height() - 2 - icon.height()) / 2f;
				icon.x = x + (ICON_SIZE - icon.width()) / 2f;
				PixelScene.align(icon);

				text.maxWidth((int) (width - ICON_SIZE - 2));
				text.setPos(x + ICON_SIZE + 2, y + (height() - text.height()) / 2f);
			} else {
				text.maxWidth((int) width);
				text.setPos(x, y + (height() - text.height()) / 2f);
			}
			PixelScene.align(text);

			line.size(width, 1);
			line.x = x;
			line.y = y;

			super.layout();
		}

	}
}