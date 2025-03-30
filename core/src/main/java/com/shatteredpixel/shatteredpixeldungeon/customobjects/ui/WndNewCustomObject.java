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

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObject;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObjectManager;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.LuaCustomObject;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.LuaGlobals;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.LuaManager;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.blueprints.AnyLuaCustomObj;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.blueprints.CustomBuff;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.blueprints.CustomCharSprite;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.blueprints.CustomItem;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.blueprints.CustomMob;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.blueprints.CustomPlant;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.blueprints.CustomRoom;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.blueprints.CustomTrap;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.interfaces.CustomObjectClass;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.ItemContainer;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.WndEditorInv;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.CustomObjectItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.LuaRestrictionProxy;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemContainerWithLabel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.SimpleWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.BeaconOfReturning;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RatSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.CreateCancelComp;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.InventorySlot;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.NotAllowedInLua;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextInput;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Reflection;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NotAllowedInLua
public class WndNewCustomObject extends SimpleWindow {

	private static final Map<Class<? extends CustomObject>, String> enteredSuperclasses = new HashMap<>();
	private static Class<? extends CustomObject> selectedType;
	private static String enteredName, enteredSuperClass;

	private LuaCustomObject selectedObject;

	private TypeSelector availableTypes;
	private CreateCancelComp outsideSp;

	private RenderedTextBlock title;
	private RenderedTextBlock nameLabel, classLabel;
	private TextInput nameInput, classInput;
	private IconButton btnSelectClass;

	public WndNewCustomObject() {
		this(null);
	}

	public WndNewCustomObject(Class<? extends CustomObject> forceType) {

		title = PixelScene.renderTextBlock(Messages.get(this, "title"), 12);
		title.hardlight(Window.TITLE_COLOR);

		if (forceType == null) {
			selectedType = CustomMob.class;
			availableTypes = new TypeSelector();
		}
		else selectedType = forceType;

		nameLabel = PixelScene.renderTextBlock(Messages.get(this, "name"), 8);
		classLabel = PixelScene.renderTextBlock(Messages.get(this, "superclass"), 7);

		nameInput = new TextInput(Chrome.get(Chrome.Type.TOAST_WHITE), false, PixelScene.uiCamera.zoom) {
			@Override
			public void setText(String text) {
				super.setText(text);
				enteredName = text;
				updateEnableStateOfCreate();
			}
		};

		classInput = new TextInput(Chrome.get(Chrome.Type.TOAST_WHITE), false, 6, PixelScene.uiCamera.zoom) {
			@Override
			public void setText(String text) {
				super.setText(text);
				enteredSuperClass = text;
				Class<?> c = Reflection.forName(enteredSuperClass);
				if (c == null) {
					enteredSuperClass = LuaGlobals.searchFullyQualifiedName(enteredSuperClass);
				}
				updateEnableStateOfCreate();
			}
		};

		btnSelectClass = new IconButton(Icons.CHANGES.get()) {
			@Override
			protected void onClick() {
				EditorScene.selectItem(new WndBag.ItemSelectorInterface() {
					@Override
					public String textPrompt() {
						return null;
					}

					@Override
					public boolean includeAddButtons() {
						return false;
					}
					
					@Override
					public Class<? extends Bag> preferredBag() {
						return selectedObject.preferredBag();
					}

					@Override
					public List<Bag> getBags() {
						return selectedObject.getBags();
					}

					@Override
					public boolean itemSelectable(Item item) {
						Class<?> c;
						if (item instanceof EditorItem<?>) {
							c = ((EditorItem<?>) item).getObject().getClass();
						} else {
							c = item.getClass();
						}
						if (CustomObjectClass.class.isAssignableFrom(c)) {
							return false;
						}
						if (!isValidSuperclass(c)) {
							return false;
						}
						return true;
					}

					@Override
					public void onSelect(Item item) {
						if (item != null) {
							Class<?> clazz = ((EditorItem<?>) item).getObject().getClass();
							String txt = "";
							if (Plant.Seed.class.isAssignableFrom(clazz)) {
								Class<?> enclosingClass = clazz.getEnclosingClass();
								while (enclosingClass != null) {
									txt = enclosingClass.getSimpleName() + "$" + txt;
									enclosingClass = enclosingClass.getEnclosingClass();
								}
							}
							classInput.setText(txt + clazz.getSimpleName());
						}
					}

					@Override
					public boolean acceptsNull() {
						return false;
					}
				});
			}
		};

		Component content = new Component() {
			@Override
			protected void createChildren() {
				if (availableTypes != null) add(availableTypes);
				add(nameLabel);
				add(nameInput);
				add(classLabel);
				add(classInput);
				add(btnSelectClass);
			}

			@Override
			protected void layout() {
				float posY = y;

				if (availableTypes != null && availableTypes.visible) {
					availableTypes.setRect(x, posY, width, 0);
					posY = availableTypes.bottom() + 2;
				}

				if (nameLabel.isVisible()) nameLabel.maxWidth((int) (width * 0.2f));
				if (classLabel.isVisible()) classLabel.maxWidth((int) (width * 0.2f));

				float labelWidth = 0;
				if (nameLabel.isVisible()) {
					if (classLabel.isVisible()) labelWidth = Math.max(nameLabel.width(), classLabel.width());
					else labelWidth = nameLabel.width();
				} else if (classLabel.isVisible()) labelWidth = classLabel.width();
				labelWidth += 3;


				if (nameInput.isVisible()) {
					nameLabel.setPos(x, posY + (18 - nameLabel.height()) * 0.5f);
					nameInput.setRect(x + labelWidth, posY, width - labelWidth, 18);
					posY = nameInput.bottom() + 2;
				}

				if (classInput.isVisible()) {
					classLabel.setPos(x, posY + (18 - classLabel.height()) * 0.5f);
					classInput.setRect(x + labelWidth, posY, width - labelWidth - 20, 18);
					btnSelectClass.setRect(classInput.right() + 2, posY + 1, 16, 16);
					posY = classInput.bottom();
				}

				height = posY - y;
			}
		};

		outsideSp = new CreateCancelComp() {
			@Override
			protected void onPositive() {
				if (enteredName != null) {
					if (onCreate() == null) return;
				}
				hide();
			}

			@Override
			protected void onNegative() {
				hide();
			}
		};

		initComponents(title, content, outsideSp);

		if (availableTypes != null)
			availableTypes.setSelectedType(selectedType);
		else {
			for (CustomObjectItem objItem : TYPES) {
				CustomObject obj = objItem.getObject();
				if (obj.getClass().getSuperclass() == selectedType) {
					selectedObject = (LuaCustomObject) obj;
					break;
				}
			}
			setSelectedType(selectedObject);
		}
		nameInput.gainFocus();
	}

	protected CustomObject onCreate() {
		Class<? extends CustomObject> type = selectedType;
		if (selectedType == AnyLuaCustomObj.class) {
			Class<?> c = Reflection.forName(enteredSuperClass);
			for (CustomObjectItem customObjectItem : TYPES) {
				CustomObject obj = customObjectItem.getObject();
				if (obj instanceof LuaCustomObject) {
					if (((LuaCustomObject) obj).isSuperclassValid(c)) {
						type = obj.getClass();
						break;
					}
				}
			}
		}
		if (type.isAnonymousClass()) type = (Class<? extends CustomObject>) type.getSuperclass();
		CustomObject obj = CustomObjectManager.createNewCustomObject(type, enteredName, enteredSuperClass);
        if (!CustomCharSprite.class.isAssignableFrom(type)) WndEditorInv.updateCurrentTab();
		return obj;
	}

	private void updateEnableStateOfCreate() {
		outsideSp.getPositiveButton().enable(
				     enteredName != null
						&& !enteredName.isEmpty()
						&& isValidSuperclass(enteredSuperClass)
		);
	}

	protected final boolean isValidSuperclass(String className) {
		if (className == null) return false;
		className = LuaManager.maybeAddMainPackageName(className);
		return isValidSuperclass(Reflection.forName(className));
	}
	
	protected boolean isValidSuperclass(Class<?> c) {
		if (c == null) return false;
		return !LuaRestrictionProxy.isRestricted(c)
				&& !Modifier.isAbstract(c.getModifiers())
				&& selectedObject.isSuperclassValid(c);
	}

	private static final List<CustomObjectItem> TYPES = new ArrayList<>();

	static {
		//must be anonymous class! (getSuperclass() should return the original class)
		TYPES.add(new CustomObjectItem(new CustomMob() {
			public Image getSprite() {
				return new RatSprite();
			}
			public String getName() {
				return getClass().getSuperclass().getSimpleName();
			}
		}));
		TYPES.add(new CustomObjectItem(new CustomItem() {
			public Image getSprite() {
				return new ItemSprite(new BeaconOfReturning());
			}
			public String getName() {
				return getClass().getSuperclass().getSimpleName();
			}
		}));
		TYPES.add(new CustomObjectItem(new CustomTrap() {
			public Image getSprite() {
				return EditorUtilities.getTerrainFeatureTexture(22);
			}
			public String getName() {
				return getClass().getSuperclass().getSimpleName();
			}
		}));
		TYPES.add(new CustomObjectItem(new CustomPlant() {
			public Image getSprite() {
				return EditorUtilities.getTerrainFeatureTexture(116);//icecap
			}
			public String getName() {
				return getClass().getSuperclass().getSimpleName();
			}
		}));
		TYPES.add(new CustomObjectItem(new CustomBuff() {
			public Image getSprite() {
				return new ItemSprite(ItemSpriteSheet.SOMETHING);
			}
			public String getName() {
				return getClass().getSuperclass().getSimpleName();
			}
		}));
		TYPES.add(new CustomObjectItem(new CustomCharSprite() {
			public Image getSprite() {
				return new ItemSprite();
			}
			public String getName() {
				return getClass().getSuperclass().getSimpleName();
			}
		}));
		TYPES.add(new CustomObjectItem(new CustomRoom() {
			public Image getSprite() {
				return new ItemSprite();
			}
			public String getName() {
				return getClass().getSuperclass().getSimpleName();
			}
		}));
		TYPES.add(new CustomObjectItem(new AnyLuaCustomObj() {
			public Image getSprite() {
				return new ItemSprite();
			}
			public String getName() {
				return getClass().getSuperclass().getSimpleName();
			}
		}));
	}

	private void setSelectedType(CustomObject obj) {

		selectedType = obj.getClass();
		while (selectedType.isAnonymousClass()) {
			selectedType = (Class<? extends CustomObject>) selectedType.getSuperclass();
		}

		if (selectedType != null) {
			enteredSuperclasses.put(selectedType, classInput.getText());
		}

		if (obj instanceof LuaCustomObject) {
			selectedObject = (LuaCustomObject) obj;

			String text = enteredSuperclasses.get(selectedType);
			if (text != null && !text.isEmpty()) classInput.setText(text);
			else if (!isValidSuperclass(classInput.getText())) classInput.setText("");

			classInput.setVisible(true);
			btnSelectClass.setVisible(true);
		} else {
			outsideSp.getPositiveButton().enable(true);
			classInput.setVisible(false);
			btnSelectClass.setVisible(false);
		}

		title.text(Messages.get(this, "title", selectedType.getSimpleName()));

		resize(WndNewCustomObject.this.width, (int) Math.ceil(preferredHeight()));
	}

	private class TypeSelector extends ItemContainerWithLabel<CustomObjectItem> {

		public TypeSelector() {
			super(TYPES, null, Messages.get(WndNewCustomObject.this, "type"), false, TYPES.size(), TYPES.size());
		}

		public void setSelectedType(Class<? extends CustomObject> clazz) {
			for (Slot s : slots) {
				CustomObject obj = ((CustomObjectItem) s.item()).getObject();
				if (obj.getClass().getSuperclass() == clazz) {
					setSelectedType(obj, s);
					return;
				}
			}

		}

		private void setSelectedType(CustomObject obj, Slot slot) {

			for (Slot s : slots) {
				if (s == slot) s.setBackgroundColor(InventorySlot.ENHANCED);
				else s.setBackgroundColor(InventorySlot.NORMAL);
			}

			WndNewCustomObject.this.setSelectedType(obj);
		}

		@Override
		protected void onItemSlotClick(ItemContainer<CustomObjectItem>.Slot slot, Item item) {
			setSelectedType(((CustomObjectItem) item).getObject(), slot);
		}
	}
}
