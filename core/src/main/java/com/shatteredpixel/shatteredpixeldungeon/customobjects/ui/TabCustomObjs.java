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

package com.shatteredpixel.shatteredpixeldungeon.customobjects.ui;

import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObject;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObjectManager;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.blueprints.AnyLuaCustomObj;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.blueprints.CustomBuff;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.blueprints.CustomCharSprite;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.blueprints.CustomGameObject;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.blueprints.CustomItem;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.blueprints.CustomMob;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.blueprints.CustomPlant;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.blueprints.CustomRoom;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.blueprints.CustomTrap;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.EditorInventoryWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.CategoryScroller;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.CompactCategoryScroller;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.watabou.NotAllowedInLua;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@NotAllowedInLua
public class TabCustomObjs extends WndAllCustomObjects.TabCustomObjs {

	private static final class CategoryFilter {

		private final Class<? extends CustomObject> typeOfClasses;
		private final String nameKey;

		private CategoryFilter(Class<? extends CustomObject> typeOfClasses, String nameKey) {
			this.typeOfClasses = typeOfClasses;
			this.nameKey = nameKey;
		}

		private boolean belongsToCategory(CustomObject obj) {
			return typeOfClasses.isAssignableFrom(obj.getClass());
		}

		public String displayName() {
			String str = Messages.get("editor.inv.categories." + nameKey + "$1.name");
			return Messages.titleCase(
					Messages.NO_TEXT_FOUND.equals(str) ? Messages.get(TabCustomObjs.class, nameKey) : str
			);
		}


	}

	private static final CategoryFilter[] CATEGORY_FILTERS = new CategoryFilter[] {
			new CategoryFilter(CustomCharSprite.class, "char_sprites"),
			new CategoryFilter(CustomRoom.class, "rooms"),
			new CategoryFilter(CustomMob.class, "mobs"),
			new CategoryFilter(CustomItem.class, "items"),
			new CategoryFilter(CustomTrap.class, "traps"),
			new CategoryFilter(CustomPlant.class, "plants"),
			new CategoryFilter(CustomBuff.class, "buffs"),
			new CategoryFilter(AnyLuaCustomObj.class, "other"),
	};

	private final EditorInventoryWindow window;
	private final Set<Class<? extends CustomObject>> visibleCategories;

	public TabCustomObjs(EditorInventoryWindow window, Set<Class<? extends CustomObject>> visibleCategories) {
		super();
		this.visibleCategories = visibleCategories;
		this.window = window;
		addBtn.setVisible(false);
		add( categoryScroller = new CompactCategoryScroller(createCategories(), window) );
	}

	@Override
	protected CategoryScroller.Category[] createCategories() {
		CategoryScroller.Category[] cats = new CategoryScroller.Category[visibleCategories == null ? CATEGORY_FILTERS.length : visibleCategories.size()];
		int j = 0;
		for (int i = 0; i < CATEGORY_FILTERS.length; i++) {
			if (visibleCategories != null && !visibleCategories.contains(CATEGORY_FILTERS[i].typeOfClasses)) {
				continue;
			}
			final int index = i;
			cats[j++] = new CategoryScroller.Category() {

				@Override
				public List<?> createItems(boolean required) {

					List<Object> ret = new ArrayList<>();
					for (CustomObject obj : CustomObjectManager.allUserContents.values()) {
						if (CATEGORY_FILTERS[index].belongsToCategory(obj) && showObject(obj)) {
							ret.add(EditorItem.wrapObject(
									obj instanceof CustomGameObject
											? ((CustomGameObject<?>) obj).getUserContentClass()
											: obj

							));
						}
					}

					Component addBtn = new ScrollingListPane.ListButton() {
						protected RedButton createButton() {
							return new RedButton(Messages.get(TabCustomObjs.class, "add")) {
								@Override
								protected void onClick() {
									DungeonScene.show(new WndNewCustomObject(CATEGORY_FILTERS[index].typeOfClasses) {
										@Override
										public CustomObject onCreate() {
											CustomObject result = super.onCreate();
											categoryScroller.updateItemsInCategories(true);
											TabCustomObjs.this.layout();
											return result;
										}
										
										@Override
										protected boolean isValidSuperclass(Class<?> c) {
											return super.isValidSuperclass(c) && isClassSelectableAsTargetClassForNewObj(c);
										}
									});
								}
							};
						}
					};
					ret.add(addBtn);

					return ret;
				}

				@Override
				public Image getImage() {
					return new ItemSprite();
				}

				@Override
				public String getName() {
					return CATEGORY_FILTERS[index].displayName();
				}
			};
		}
		return cats;
	}

	@Override
	protected String addBtnLabel() {
		return Messages.get(this, "add_custom_object");
	}

	@Override
	protected void onAddBtnClick() {
		DungeonScene.show(new WndNewCustomObject() {
			@Override
			public CustomObject onCreate() {
				CustomObject result = super.onCreate();
				categoryScroller.updateItemsInCategories(true);
				TabCustomObjs.this.layout();
				return result;
			}
		});
	}

	@Override
	public Image createIcon() {
		return new ItemSprite();
	}

	@Override
	public String hoverText() {
		return Messages.get(this, "title");
	}

	protected boolean showObject(CustomObject obj) {
		return true;
	}
	
	protected boolean isClassSelectableAsTargetClassForNewObj(Class<?> cl) {
		return true;
	}

}
