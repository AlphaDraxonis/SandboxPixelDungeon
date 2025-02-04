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

package com.shatteredpixel.shatteredpixeldungeon.editor.ui;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.ItemContainer;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.EditorInventoryWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.Supplier;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptionsCondensed;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CompactCategoryScroller extends AbstractCategoryScroller<CompactCategoryScroller.CategoryComp> {

	protected Component content;
	private EditorInventoryWindow selectorWindow;

	public CompactCategoryScroller(Category[] categories) {
		this(categories, null);
	}

	public CompactCategoryScroller(Category[] categories, EditorInventoryWindow selectorWindow) {
		super(categories, new CategoryComp[categories.length], selectorWindow);
		this.selectorWindow = selectorWindow;
	}

	@Override
	protected int minimumNumOfActiveCategoriesRequiredForFirstCategoryComp() {
		return 0;
	}

	@Override
	protected ScrollPane createSp() {
		return new ScrollPane(createSpContent());
	}

	protected Component createSpContent() {
		return content = new Component() {
			@Override
			protected void layout() {
				height = 0;
				if (categoryComps != null) {
					height = EditorUtilities.layoutCompsLinear(2, this, categoryComps);
				}
			}
		};
	}

	@Override
	protected boolean processKey() {
		return false;
	}

	@Override
	protected CategoryComp createCategoryComp(int index, Category category) {
		CategoryComp result = new CategoryComp(index, category);
		result.expandFoldInTitle.setVisible(false);
//		result.expanded = false;
		result.expandAndFold.setVisible(false);
		content.add(result);
		return result;
	}

	@Override
	protected void layout() {

		content.setRect(0, 0, width, 0);
		if (sp.camera() != null) {
			sp.setRect(x, y, width, height);
		}

		sp.scrollToCurrentView();
	}

	@Override
	public void updateItemsInCategories(boolean forceItemUpdates) {
		super.updateItemsInCategories(forceItemUpdates);
		if (forceItemUpdates) {
			for (int i = 0; i < categoryComps.length; i++) {
				categoryComps[i].set(categories[i].items(false, true));
			}
		}
	}

	@Override
	protected void doSelectCategory(int selectedVisIndex, int selectedCatIndex) {
		if (categoryComps != null) {
			FoldableComp parent = categoryComps[selectedCatIndex];
			parent.expand();
		} else {

		}
	}

	@Override
	public void updateItems() {
	}

	public interface CategoryAction {

		enum Action {
			REMOVE(Icons.TRASH::get),
			EDIT(Icons.EDIT::get),
			IMPORT(Icons.DOWNLOAD::get);

			public final Supplier<Image> createIcon;

			Action(Supplier<Image> createIcon) {
				this.createIcon = createIcon;
			}
		}

		boolean supportsAction(Action action);
		void doAction(Action action);

	}

	protected class CategoryComp extends FoldableComp {

		private final int index;

		private final Button expandFoldInTitle;

		protected final IconButton[] btnExtraActions = new IconButton[CategoryAction.Action.values().length];

		private List<Item> allItems;
		private Set<Item>[] itemsForAction;
		private Runnable onAdd;

		private CategoryAction.Action mode = null;

		private final ItemComp itemComp;


		protected void set(List<?> list) {
			allItems = new ArrayList<>();
			onAdd = null;
			itemsForAction = new Set[btnExtraActions.length];
			for (int i = 0; i < btnExtraActions.length; i++) {
				if (btnExtraActions[i] != null) {
					btnExtraActions[i].setVisible(false);
				}
			}
			for (Object o : list) {
				if (o instanceof Item) {
					Item item = (Item) o;
					allItems.add(item);
					addItemToAction(CategoryAction.Action.EDIT, item);
					if (o instanceof CategoryAction || o instanceof EditorItem && ((EditorItem<?>) o).getObject() instanceof CategoryAction) {
						CategoryAction obj = (CategoryAction) o;
						for (CategoryAction.Action action : CategoryAction.Action.values()) {
							if (obj.supportsAction(action)) {
								addItemToAction(action, item);
							}
						}
					}
				}
				else if (onAdd == null && o instanceof Runnable) onAdd = (Runnable) o;
			}

			if (itemComp != null) {
				itemComp.onAddClicking = onAdd;
				itemComp.setItems(new ArrayList<>(allItems));
			}

			CategoryAction.Action[] actions = CategoryAction.Action.values();
			for (int i = 0; i < actions.length; i++) {

				if (itemsForAction[i] == null) {
					continue;
				}

				final int index = i;
				if (btnExtraActions[i] == null) {
					btnExtraActions[i] = new IconButton(actions[index].createIcon.get()) {
						private boolean selected;

						@Override
						protected void onClick() {
							if (mode == actions[index]) {
								selected = false;
								mode = null;
								icon.resetColor();
								itemComp.setItems(new ArrayList<>(allItems));
								layoutParent();
							} else {
								selected = true;
								mode = actions[index];
								for (IconButton btn : btnExtraActions) {
									if (btn != null) btn.icon().resetColor();
								}
								icon.brightness(1.5f);
								itemComp.filterItems(itemsForAction[index]);
								layoutParent();
							}
						}

						@Override
						protected void onPointerUp() {
							super.onPointerUp();
							if (selected) icon.brightness(1.5f);
						}
					};
					add(btnExtraActions[i]);
				} else {
					btnExtraActions[i].setVisible(true);
//					if (mode == actions[i])
//						btnExtraActions[i].icon().brightness(1.5f);
				}
			}
		}

		private void addItemToAction(CategoryAction.Action action, Item item) {
			Set<Item> l = itemsForAction[action.ordinal()];
			if (l == null) {
				l = new HashSet<>(5);
				itemsForAction[action.ordinal()] = l;
			}
			l.add(item);
		}

		public CategoryComp(int index, Category category) {
			this.index = index;
			title.text(category.getName());

			expandFoldInTitle = new Button() {
				@Override
				protected void onClick() {
					if (body.visible) fold();
					else expand();
				}
			};
			add(expandFoldInTitle);

			set(category.items(false, true));

			setBody(itemComp = new ItemComp(new ArrayList<>(allItems), onAdd) {
				@Override
				protected void onItemSlotClick(ItemContainer<Item>.Slot slot, Item item) {

					if (mode == null) {
						if (selectorWindow != null) {
							if (selectorWindow.selector() != null) {
								selectorWindow.hide();
								selectorWindow.selector().onSelect(item);
							} else {
								selectorWindow.hide();
								QuickSlotButton.set(item);
							}
						} else {
							showWndEditItemComp(slot, item);
						}
					}
					else if (mode == CategoryAction.Action.REMOVE) {
						DungeonScene.show(new WndOptionsCondensed(new ItemSprite(item), "Really remove tzz", "msg", "yes", "no") {
							@Override
							protected void onSelect(int index) {
								if (index == 0) {
									doAction(CategoryAction.Action.REMOVE, item);
								}
							}
						});
					}
					else if (mode == CategoryAction.Action.EDIT) {
						showWndEditItemComp(slot, item);
					}
					else {
						doAction(mode, item);
					}
				}
			});
		}

		private void doAction(CategoryAction.Action action, Item item) {
			if (item instanceof CategoryAction) {
				((CategoryAction) item).doAction(action);
				updateItemsInCategories(true);

				itemComp.showAddBtn();
				for (int i = 0; i < btnExtraActions.length; i++) {
					if (itemsForAction[i] == null) {
						continue;
					}
					if (mode.ordinal() == i) {
						btnExtraActions[i].icon().brightness(1.5f);
						itemComp.filterItems(itemsForAction[i]);
					} else if (btnExtraActions[i] != null) btnExtraActions[i].icon().resetColor();
				}
				layoutParent();
			}
		}

		@Override
		public void expand() {
			if (getSelectedCatIndex() == index) super.expand();
			else selectCategory(index);
		}

		@Override
		protected void showBody(boolean flag) {
			super.showBody(flag);
			expandAndFold.setVisible(false);
		}

		@Override
		protected void layoutParent() {
			CompactCategoryScroller.this.layout();
		}

		@Override
		protected float requiredWidthForControlButtons() {
			float w = super.requiredWidthForControlButtons();
			for (int i = 0; i < btnExtraActions.length; i++) {
				if (btnExtraActions[i] != null && btnExtraActions[i].visible) {
					w += BUTTON_HEIGHT + BUTTON_GAP;
				}
			}
			return w;
		}

		@Override
		protected void layout() {
			super.layout();
			if (expandFoldInTitle.visible)
				expandFoldInTitle.setRect(title.left(), title.top() - 2, title.maxWidth(), title.height() + 4);
		}

		@Override
		protected float layoutControlButtons(float posX, float posY, float titleHeight) {
			posX = super.layoutControlButtons(posX, posY, titleHeight);
			for (IconButton btn : btnExtraActions) {
				if (btn != null && btn.visible) {
					btn.setRect(posX -= BUTTON_HEIGHT + BUTTON_GAP, posY + (titleHeight - btn.icon().height()) / 2f, BUTTON_HEIGHT, BUTTON_HEIGHT);
				}
			}
			return posX;
		}
	}

	protected static class ItemComp extends ItemContainer<Item> {

		private Runnable onAddClicking;

		public ItemComp(List<Item> items, Runnable onAddClicking) {
			super(items);
			this.onAddClicking = onAddClicking;
			showAddBtn();
		}

		protected void showAddBtn() {
			addBtn.setVisible(onAddClicking != null);
		}

		private void filterItems(Collection<Item> items) {
			for (Slot slot : new HashSet<>(slots)) {
				if (!items.contains(slot.item()))
					removeSlot(slot);
			}
			addBtn.setVisible(false);
		}

		private void setItems(List<Item> items) {
			for (Slot slot : new HashSet<>(slots)) {
				removeSlot(slot);
			}
			itemList = items;
			for (Item i : items) {
				addItemToUI(i, !reverseUiOrder);
			}
		}

		@Override
		protected void addItemToUI(Item item, boolean last) {
			super.addItemToUI(item, last);
			showAddBtn();
		}

		@Override
		protected void showSelectWindow() {
			if (onAddClicking != null) onAddClicking.run();
		}

		@Override
		protected boolean onItemSlotLongClick(ItemContainer<Item>.Slot slot, Item item) {
			showWndEditItemComp(slot, item);
			return true;
		}

		@Override
		protected void onItemSlotClick(ItemContainer<Item>.Slot slot, Item item) {
		}

		@Override
		protected void onItemSlotRightClick(ItemContainer<Item>.Slot slot, Item item) {
			Sample.INSTANCE.play(Assets.Sounds.CLICK);
			onItemSlotLongClick(slot, item);
		}
	}
}