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

package com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories;

import com.shatteredpixel.shatteredpixeldungeon.editor.inv.WndEditorInv;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.Consumer;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.watabou.noosa.Image;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class EditorInventory {

	//absolutely necessary to call this before using any of the other classes in this package
	public static void callStaticInitializers() {
	}

	public EditorInventory() {}//tzz make private, and add clas statics to LuaGlobals!!!!!!!!!!!

	static Set<GameObjectCategory<?>> allCategories = new HashSet<>();

	static {
		allCategories.add(Items.instance());
		allCategories.add(Mobs.instance());
		allCategories.add(Traps.instance());
		allCategories.add(Plants.instance());

		allCategories.add(Buffs.instance());
		allCategories.add(Rooms.instance());
		allCategories.add(MobSprites.instance());
	}

	public static void doWidthAllCategories(Consumer<GameObjectCategory<?>> whatToDo) {
		for (GameObjectCategory<?> category : allCategories)
			whatToDo.accept(category);
	}

	public static Set<GameObjectCategory<?>> getAllCategories() {
		return allCategories;
	}

	public static final EditorItemBag mainBag = new EditorItemBag("main", 0) {
		@Override
		public Image getCategoryImage() {
			return null;
		}
	};

	static {
		if (Items.bag() == null) {
			throw new RuntimeException("Must call EditorInventory.callStaticInitializers() at any point before accessing the creative inventory!");
		}
		mainBag.items.add(Tiles.bag);
		mainBag.items.add(Mobs.bag());
		mainBag.items.add(Items.bag());
		mainBag.items.add(Traps.bag());
		mainBag.items.add(Plants.bag());
	}

	public static EditorItemBag getLastBag(List<Bag> availableBags) {
		EditorItemBag lastBag = WndEditorInv.lastBag();
		if (lastBag != null && availableBags.contains(lastBag)) return lastBag;
		return (EditorItemBag) availableBags.get(0);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Bag> T getBag(Class<T> bagClass) {
		for (Item item : mainBag.items) {
			if (bagClass.isInstance(item)) {
				return (T) item;
			}
		}
		Class<?> cl = bagClass.getEnclosingClass();
		if (Rooms.class == cl) return (T) Rooms.bag();
		if (Tiles.class == cl) return (T) Tiles.bag;
		if (Mobs.class  == cl) return (T) Mobs.bag();
		if (Items.class == cl) return (T) Items.bag();
		if (Traps.class == cl) return (T) Traps.bag();
		if (Plants.class == cl) return (T) Plants.bag();
		if (Buffs.class == cl) return (T) Buffs.bag();
		if (MobSprites.class == cl) return (T) MobSprites.bag();

		throw new IllegalArgumentException("Bag not found for " + bagClass);
	}


	public static ArrayList<Bag> getMainBags() {
		return getMainBags(mainBag);
	}

	public static ArrayList<Bag> getMainBags(Bag bag) {
		ArrayList<Bag> list = new ArrayList<>();
		for (Item item : bag.items) {
			if (item instanceof Bag) list.add((Bag) item);
		}
		return list;
	}

	public static Item getFirstItem() {
		return getFirstItem(mainBag);
	}

	public static Item getFirstItem(Bag bag) {
		for (Item item : bag.items) {
			if (!(item instanceof Bag)) return item;
		}
		if (bag.items.size() > 0) return getFirstItem((Bag) bag.items.get(0));
		return null;
	}

}