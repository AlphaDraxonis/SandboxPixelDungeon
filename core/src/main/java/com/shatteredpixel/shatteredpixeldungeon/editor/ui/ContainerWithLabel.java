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

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.ItemContainer;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Buffs;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Mobs;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.BuffItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.MobItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;

import java.util.ArrayList;
import java.util.List;

public abstract class ContainerWithLabel<T> extends ItemContainerWithLabel<EditorItem<T>> {

	private final List<T> specificTypeList;

	public ContainerWithLabel(List<T> specificTypeList, String label) {
		super(toSpecificTypeList(specificTypeList), label);
		this.specificTypeList = specificTypeList;
	}

	public ContainerWithLabel(List<T> specificTypeList, DefaultEditComp<?> editComp, String label) {
		super(toSpecificTypeList(specificTypeList), editComp, label);
		this.specificTypeList = specificTypeList;
	}

	public ContainerWithLabel(List<T> specificTypeList, DefaultEditComp<?> editComp, boolean reverseUiOrder, String label) {
		super(toSpecificTypeList(specificTypeList), editComp, reverseUiOrder, label);
		this.specificTypeList = specificTypeList;
	}

	public ContainerWithLabel(List<T> specificTypeList, DefaultEditComp<?> editComp, String label, boolean reverseUiOrder, int minSlots, int maxSlots) {
		super(toSpecificTypeList(specificTypeList), editComp, label, reverseUiOrder, minSlots, maxSlots);
		this.specificTypeList = specificTypeList;
	}

	public static <T> List<EditorItem<T>> toSpecificTypeList(List<? extends T> mobs) {
		List<EditorItem<T>> ret = new ArrayList<>(5);
		for (T m : mobs) ret.add(EditorItem.wrapObject(m));
		return ret;
	}


	@Override
	public abstract boolean itemSelectable(Item item);

	@Override
	public abstract Class<? extends Bag> preferredBag();

	@Override
	protected void doAddItem(EditorItem<T> item) {
		item = (EditorItem<T>) item.getCopy();
		super.doAddItem(item);
		specificTypeList.add(item.getObject());
	}

	@Override
	protected boolean removeSlot(ItemContainer<EditorItem<T>>.Slot slot) {
		if (super.removeSlot(slot)) {
			specificTypeList.remove(((EditorItem<T>) slot.item()).getObject());
			return true;
		}
		return false;
	}

	public void updateState(List<T> source) {
		setItemList(toSpecificTypeList(source));
	}

	public static class ForMobs extends ContainerWithLabel<Mob> {

		public ForMobs(List<Mob> mobList, String label) {
			super(mobList, label);
		}

		public ForMobs(List<Mob> mobList, DefaultEditComp<?> editComp, String label) {
			super(mobList, editComp, label);
		}

		public ForMobs(List<Mob> mobList, DefaultEditComp<?> editComp, boolean reverseUiOrder, String label) {
			super(mobList, editComp, reverseUiOrder, label);
		}

		public ForMobs(List<Mob> mobList, DefaultEditComp<?> editComp, String label, boolean reverseUiOrder, int minSlots, int maxSlots) {
			super(mobList, editComp, label, reverseUiOrder, minSlots, maxSlots);
		}

		@Override
		public boolean itemSelectable(Item item) {
			return item instanceof MobItem;
		}

		@Override
		public Class<? extends Bag> preferredBag() {
			return Mobs.bag().getClass();
		}

		public static List<MobItem> toMobTypeList(List<? extends Mob> mobs) {
			List<MobItem> ret = new ArrayList<>(5);
			for (Mob m : mobs) ret.add(new MobItem(m));
			return ret;
		}
	}

	public static class ForBuffs extends ContainerWithLabel<Buff> {

		public ForBuffs(List<Buff> buffList, String label) {
			super(buffList, label);
		}

		public ForBuffs(List<Buff> buffList, DefaultEditComp<?> editComp, String label) {
			super(buffList, editComp, label);
		}

		public ForBuffs(List<Buff> buffList, DefaultEditComp<?> editComp, boolean reverseUiOrder, String label) {
			super(buffList, editComp, reverseUiOrder, label);
		}

		public ForBuffs(List<Buff> buffList, DefaultEditComp<?> editComp, String label, boolean reverseUiOrder, int minSlots, int maxSlots) {
			super(buffList, editComp, label, reverseUiOrder, minSlots, maxSlots);
		}

		@Override
		public boolean itemSelectable(Item item) {
			return item instanceof BuffItem;
		}

		@Override
		public Class<? extends Bag> preferredBag() {
			return Buffs.bag().getClass();
		}

		public static List<BuffItem> toBuffTypeList(List<? extends Buff> buffs) {
			List<BuffItem> ret = new ArrayList<>(5);
			for (Buff b : buffs) ret.add(new BuffItem(b));
			return ret;
		}
	}
}