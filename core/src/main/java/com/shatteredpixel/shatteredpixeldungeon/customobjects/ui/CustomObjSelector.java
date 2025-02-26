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

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObject;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.InventorySlot;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemSlot;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.NotAllowedInLua;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.ui.Component;

@NotAllowedInLua
public class CustomObjSelector<T> extends Component {

	protected NinePatch bg;
	protected RenderedTextBlock label;
	public int slotHeight = ItemSpriteSheet.SIZE;
	protected ItemSlot itemSlot;
	protected IconButton change, detach;

	private final Item itemForImage;

	private final Selector<T> selector;

	public CustomObjSelector(String label, Selector<T> selector) {
		this(label, selector, 9);
	}
	
	public CustomObjSelector(String label, Selector<T> selector, int textSize) {

		this.selector = selector;

		bg = Chrome.get(Chrome.Type.GREY_BUTTON_TR);
		add(bg);

		this.label = PixelScene.renderTextBlock(label, textSize);
		add(this.label);

		itemForImage = new EditorItem<Object>() {
			@Override
			public DefaultEditComp<?> createEditComponent() {
				return null;
			}
			@Override
			public Image getSprite() {
				return selector.getSprite(selector.getCurrentValue(), () -> itemSlot.item(itemForImage));
			}
			@Override
			public String name() {
				Object obj = selector.getCurrentValue();
				if (obj instanceof String) return (String) obj;
				if (obj instanceof CustomObject) return ((CustomObject) obj).getName();
				return null;
			}
			@Override
			public Item getCopy() {
				return null;
			}
			@Override
			public void place(int cell) {
			}
		};

		itemSlot = new InventorySlot(itemForImage) {
			@Override
			protected void onClick() {
				super.onClick();
				selector.onItemSlotClick();
			}

			@Override
			protected boolean onLongClick() {
				return selector.onItemSlotLongClick();
			}

			@Override
			public void item(Item item) {
				super.item(item);
				bg.visible = true;//gold and bags should have bg
			}

			@Override
			protected void viewSprite(Item item) {
				if (!(item instanceof EditorItem)) {
					super.viewSprite(item);
					return;
				}
				if (sprite != null) {
					remove(sprite);
					sprite.destroy();
				}
				sprite = ((EditorItem<?>) item).getSprite();
				if (sprite != null) addToBack(sprite);
				sendToBack(bg);
			}
		};
		add(itemSlot);

		change = new IconButton(Icons.CHANGES.get()) {
			@Override
			protected void onClick() {
				onChangeClick();
			}
		};
		add(change);

		detach = new IconButton(Icons.CLOSE.get()) {
			@Override
			protected void onClick() {
				onDetachClick();
			}
		};
		detach.setSize(detach.icon().width(), detach.icon().height());
		add(detach);

		enableDetaching(false);
	}

	@Override
	protected void layout() {

		if (slotHeight == 0) return;

		height = Math.max(getMinimumHeight(width()), Math.max(Math.max(label.height(), 10), height()));

		bg.x = x;
		bg.y = y;
		bg.size(width(), height());

		float conW = slotHeight;
		if (change.visible) conW += 1.5f + slotHeight;
		if (detach.visible) conW += 1.5f + detach.width();
		float startX = x + (width - conW) * 0.5f;
		float conY = y + (height() - slotHeight - label.height()) / 2f + 2 + label.height();


		itemSlot.setRect(startX, conY, slotHeight, slotHeight);
		PixelScene.align(itemSlot);

		if (change.visible) {
			change.setRect(itemSlot.right() + 1.5f, conY, slotHeight, slotHeight);
			PixelScene.align(change);
		}

		if (detach.visible) {
			detach.setPos((change.visible ? change : itemSlot).right() + 1.5f, y + (height() - detach.height() - label.height()) / 2f + 2 + label.height());
			PixelScene.align(detach);
		}

		if (!label.text().equals("")) {
			label.maxWidth((int) width);
			label.setPos(
					x + (width() - label.width() * 2 + 1) * 0.5f +
							label.width() / 2f,// /2f is labels alignment!!!!
					conY - 3 - label.height()
			);
			PixelScene.align(label);
		}
	}

	public float getMinimumHeight(float width) {
		label.maxWidth((int) width);
		return slotHeight + label.height() + 4 + bg.marginVer();
	}

	protected void onChangeClick() {
		selector.onChangeClick();
	}

	private boolean detachEnabled = true;

	public void enableChanging(boolean flag) {
		change.setVisible(flag);
	}

	public void enableDetaching(boolean flag) {
		detach.setVisible(flag && selector.getCurrentValue() != null);
		detachEnabled = flag;
	}

	protected void onDetachClick() {
		selector.onSelect(null);
		itemSlot.item(itemForImage);
		detach.setVisible(false);
		layout();
	}

	public void setValue(T obj) {
		selector.onSelect(obj);
		itemSlot.item(itemForImage);
		detach.setVisible(obj != null);
		layout();
	}

	public interface Selector<T> {

		T getCurrentValue();

		void onSelect(T obj);

		void onItemSlotClick();

		default boolean onItemSlotLongClick() {
			return false;
		}

		void onChangeClick();

		default Image getSprite(T obj, Runnable spriteReloader) {
			if (obj instanceof String) return TabResourceFiles.getSpriteForPath((String) obj);
			if (obj instanceof Item) return new ItemSprite(((Item) obj));
			if (obj instanceof CustomObject) return ((CustomObject) obj).getSprite(spriteReloader);
			return new ItemSprite(ItemSpriteSheet.NO_IMAGE);
		}
	}

}