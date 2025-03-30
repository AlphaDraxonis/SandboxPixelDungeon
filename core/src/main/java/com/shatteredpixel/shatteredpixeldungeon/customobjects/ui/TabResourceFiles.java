/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
 *
 * Sandbox Pixel Dungeon
 * Copyright (C) 2023-2025 AlphaDraxonis
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

package com.shatteredpixel.shatteredpixeldungeon.customobjects.ui;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObject;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObjectManager;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.ResourcePath;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.EditorInventoryWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.CategoryScroller;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.LoadCustomObjects;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndImageViewer;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndSoundFileViewer;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.NotAllowedInLua;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NotAllowedInLua
public class TabResourceFiles extends WndAllCustomObjects.TabCustomObjs {

	private /*final*/ CategoryScroller.Category[] categories;

	protected List<Map.Entry<String, FileHandle>> imageFiles, soundFiles, luaFiles, textFiles;
	private final boolean addNullOption;

	public TabResourceFiles(EditorInventoryWindow window, boolean addNullOption, boolean useMoreThanOneCategory) {
		super();
		this.addNullOption = addNullOption;
		add( categoryScroller = new CategoryScroller(createCategories(), window, useMoreThanOneCategory && categories.length > 1) );
	}

	protected CategoryScroller.Category[] createCategories() {

		if (categories == null) {
			categories = new CategoryScroller.Category[4];
		}

		Map<String, FileHandle> pathsInFileSystem = new HashMap<>(CustomObjectManager.allResourcePaths);
		for (Map.Entry<String, FileHandle> entry : CustomObjectManager.allResourcePaths.entrySet()) {
			if (!includeFile(entry.getValue(), entry.getKey())) {
				pathsInFileSystem.remove(entry.getKey());
			}
		}
		
		List<Map.Entry<String, FileHandle>>[] lists = new List[]{
				imageFiles = new ArrayList<>(),
				soundFiles = new ArrayList<>(),
				luaFiles = new ArrayList<>(),
				textFiles = new ArrayList<>()
		};

		for (Map.Entry<String, FileHandle> entry : pathsInFileSystem.entrySet()) {
			if (ResourcePath.isImage(entry.getValue().extension())) {
				imageFiles.add(entry);
			} else if (ResourcePath.isSound(entry.getValue().extension())) {
				soundFiles.add(entry);
			} else if (ResourcePath.isLua(entry.getValue().extension())) {
				luaFiles.add(entry);
			} else if (ResourcePath.isText(entry.getValue().extension())) {
				textFiles.add(entry);
			}
		}

		for (List<Map.Entry<String, FileHandle>> list : lists) {
			Collections.sort(list, (o1, o2) -> o1.getKey().compareTo(o2.getKey()));
			
			if (addNullOption && !list.isEmpty()) {
				list.add(0, null);
			}
		}

		for (int i = 0; i < categories.length; i++) {
			final int index = i;
			categories[i] = new CategoryScroller.Category() {

				@Override
				public List<?> createItems(boolean required) {

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
					if (object == null) {
						return new ScrollingListPane.ListItem(new ItemSprite(ItemSpriteSheet.NO_ITEM), createNullOptionLabel()) {
							@Override
							protected void onClick() {
								TabResourceFiles.this.onClick(null);
							}
						};
					}
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
				public Image getImage() {
					switch (index) {
						case 0: return new ItemSprite(ItemSpriteSheet.SCROLL_HOLDER);
						case 1: return Icons.AUDIO.get();
						case 2: return new ItemSprite(ItemSpriteSheet.DOCUMENT_HOLDER);
						case 3: return new ItemSprite(ItemSpriteSheet.DOCUMENT_HOLDER);
					}
					return new ItemSprite();
				}

				@Override
				public String getName() {
					switch (index) {
						case 0: return Messages.get(TabResourceFiles.class, "title_images");
						case 1: return Messages.get(TabResourceFiles.class, "title_sounds");
						case 2: return Messages.get(TabResourceFiles.class, "title_scripts");
						case 3: return Messages.get(TabResourceFiles.class, "title_texts");
					}
					return Messages.NO_TEXT_FOUND;
				}
			};
		}

		return categories;
	}

	protected boolean includeExtension(String extension) {
		return true;
	}
	
	protected boolean includeFile(FileHandle file, String path) {
		return includeExtension(file.extension());
	}
	
	protected String createNullOptionLabel() {
		return Messages.NO_TEXT_FOUND;
	}

	protected void onClick(Map.Entry<String, FileHandle> path) {
		if (path != null) {
			viewResource(path);
		}
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
		else if (ResourcePath.isSound(extension)) {
			DungeonScene.show(new WndSoundFileViewer(file));
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
		return Messages.get(this, "reload");
	}

	@Override
	protected void onAddBtnClick() {
		//reload files, instead of adding something
		new LoadCustomObjects(null, null, true);
		createCategories();
		categoryScroller.updateItemsInCategories(false);
		categoryScroller.selectCategory(categoryScroller.getSelectedCatIndex());
	}

	@Override
	public Image createIcon() {
		return new ItemSprite(ItemSpriteSheet.SEWER_PAGE);
	}

	@Override
	public String hoverText() {
		return Messages.get(this, "title");
	}

	public static Image getSpriteForPath(String path) {
		return getSpriteForPath(path, CustomObject.getResourceFile(path, true));
	}

	public static Image getSpriteForPath(String path, FileHandle file) {

		if (path == null) return new ItemSprite(ItemSpriteSheet.NO_IMAGE);

		String extension = ResourcePath.pathToExtension(path);

		if (ResourcePath.isLua(extension)) return new ItemSprite(ItemSpriteSheet.SEWER_PAGE);
		if (ResourcePath.isSound(extension)) return Icons.AUDIO.get();
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
