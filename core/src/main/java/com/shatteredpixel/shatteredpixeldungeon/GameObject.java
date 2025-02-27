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

package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.customobjects.interfaces.CustomGameObjectClass;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.RandomItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.BiPredicate;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.Consumer;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Function;
import com.watabou.utils.IntFunction;
import com.watabou.utils.SparseArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This superclass is responsible that certain method calls reach all variables and fields of subclasses
 */
public abstract class GameObject implements Bundlable {

	public int sparseArrayKey() {
		return 0;
	}

	public String name() {
		return null;
	}

	public String desc() {
		return null;
	}

	public void copyStats(GameObject template) {
		if (template == null) return;
		if (getClass() != template.getClass()) return;
		Bundle bundle = new Bundle();
		bundle.put("OBJ", template);
		bundle.getBundle("OBJ").put(CustomGameObjectClass.INHERIT_STATS, true);

		restoreFromBundle(bundle.getBundle("OBJ"));
	}
	
	public void initAsInventoryItem() {
	}

	/**
	 * <b>Returns <u>true</u> if a change was performed</b>.
	 * The purpose of this method is to simply redirect the whatToDo to all stored references.
	 * The actual result is only calculated in the specific methods that are called inside whatToDo
	 */
	public boolean doOnAllGameObjects(Function<GameObject, ModifyResult> whatToDo) {
		if (this instanceof RandomItem) {
			return RandomItem.doOnAllGameObjects((RandomItem) this, whatToDo);
		}
		return false;
	}

//	private GameObject example;
//	public boolean exampleDoOnAllGameObjectsImpl(Function<GameObject, ModifyResult> whatToDo) {
//		return doOnSingleObject(example, whatToDo, newValue -> example = newValue);
//	}

	public ModifyResult onDeleteLevelScheme(String name) {
		return ModifyResult.noChange();
	}

	public ModifyResult onRenameLevelScheme(String oldName, String newName) {
		return ModifyResult.noChange();
	}

	public void onMapSizeChange(IntFunction<Integer> newPosition, BiPredicate<Integer, Integer> isPositionValid) {
	}

	public ModifyResult initRandoms() {
		if (this instanceof RandomItem<?>) {
			GameObject[] newItems = ((RandomItem<?>) this).generateItems();
			if (newItems == null || newItems.length == 0) return GameObject.ModifyResult.removeFully();
			else {
				if (this instanceof Item) {
					for (GameObject i : newItems) {
						((Item) i).spreadIfLoot = ((Item) this).spreadIfLoot;
					}
				}
				return GameObject.ModifyResult.multipleReplacements(newItems);
			}
		}
		return ModifyResult.noChange();
	}

	public ModifyResult overrideResult(ModifyResult superResult, Function<GameObject, Boolean> strategy) {
		if (ModifyResult.isNoChange(superResult)) {
			Boolean b = strategy.apply(this);
			if (b == null) return ModifyResult.removeFully();
			return b
					? ModifyResult.singeReplacement(this)
					: superResult;
		}

		if (ModifyResult.isRemovingFully(superResult) || superResult.newValues.length == 0) {
			return superResult;
		}
		for (int i = 0; i < superResult.newValues.length; i++) {
			Boolean b = strategy.apply(superResult.newValues[i]);
			if (b == null) superResult.newValues[i] = null;
		}
		return ModifyResult.multipleReplacements(superResult.newValues);

	}

	public static <T extends GameObject> boolean doOnSingleObject(T obj, Function<GameObject, ModifyResult> whatToDo, Consumer<T> updater) {
		if (obj == null) {
			updater.accept(null);
			return false;
		}

		ModifyResult result = whatToDo.apply(obj);

		if (ModifyResult.isNoChange(result)) {
			return obj.doOnAllGameObjects(whatToDo);
		}

		else if (ModifyResult.isRemovingFully(result)) {
			updater.accept(null);
			return true;
		}

		else {
			GameObject v;
			int i = 0;
			do {
				v = result.newValues[i++];
			} while (v == null && i < result.newValues.length);
			if (v == null) {
				updater.accept(null);
				return true;
			} else {
				updater.accept((T) v);
				return v.doOnAllGameObjects(whatToDo) || v != obj;
			}
		}
	}

	private static final GameObject[] EMPTY_GAME_OBJECT_ARRAY = new GameObject[0];

	public static <T extends GameObject> boolean doOnAllGameObjectsList(List<T> list, Function<GameObject, ModifyResult> whatToDo) {
		if (list == null) return false;
		boolean changedSth = false;
		int index = 0;
		for (GameObject obj : list.toArray(EMPTY_GAME_OBJECT_ARRAY)) {

			ModifyResult result = obj == null ? ModifyResult.noChange() : whatToDo.apply(obj);

			if (ModifyResult.isNoChange(result)) {
				if (obj != null && obj.doOnAllGameObjects(whatToDo)) changedSth = true;
				index++;
			}

			else if (ModifyResult.isRemovingFully(result)) {
				list.remove(index);
				changedSth = true;
			}

			else {
				list.remove(index);
				for (GameObject v : result.newValues) {
					if (v != null) {
						list.add(index, (T) v);
						v.doOnAllGameObjects(whatToDo);
						index++;
					}
				}
				changedSth = true;
			}
		}
		return changedSth;
	}

	public static <T extends GameObject> boolean doOnAllGameObjectsSet(Set<T> set, Function<GameObject, ModifyResult> whatToDo) {
		if (set == null) return false;
		boolean changedSth = false;
		for (GameObject obj : set.toArray(EMPTY_GAME_OBJECT_ARRAY)) {

			ModifyResult result = obj == null ? ModifyResult.noChange() : whatToDo.apply(obj);

			if (ModifyResult.isNoChange(result)) {
				if (obj != null && obj.doOnAllGameObjects(whatToDo)) changedSth = true;
			}

			else if (ModifyResult.isRemovingFully(result)) {
				set.remove(obj);
				changedSth = true;
			}

			else {
				set.remove(obj);
				for (GameObject v : result.newValues) {
					if (v != null) {
						set.add((T) v);
						v.doOnAllGameObjects(whatToDo);
					}
				}
				changedSth = true;
			}
		}
		return changedSth;
	}

	public static <T extends GameObject> boolean doOnAllGameObjectsSparseArray(SparseArray<T> sparseArray, Function<GameObject, ModifyResult> whatToDo) {
		if (sparseArray == null) return false;
		boolean changedSth = false;
		int[] keys = sparseArray.keyArray();
		for (int key : keys) {
			GameObject obj = sparseArray.get(key);

			ModifyResult result = obj == null ? ModifyResult.noChange() : whatToDo.apply(obj);

			if (ModifyResult.isNoChange(result)) {
				if (obj != null && obj.doOnAllGameObjects(whatToDo)) changedSth = true;
			}

			else if (ModifyResult.isRemovingFully(result)) {
				sparseArray.remove(key);
				changedSth = true;
			}

			else {
				sparseArray.remove(key);
				for (GameObject v : result.newValues) {
					if (v != null) {
						sparseArray.put(v.sparseArrayKey(), (T) v);
						v.doOnAllGameObjects(whatToDo);
					}
				}
				changedSth = true;
			}
		}
		return changedSth;
	}

	public static <K, T extends GameObject> boolean doOnAllGameObjectsMap(Map<K, T> map, Function<GameObject, ModifyResult> whatToDo) {
		if (map == null) return false;
		boolean changedSth = false;
		for (K key : map.keySet()) {
			GameObject obj = map.get(key);

			ModifyResult result = obj == null ? ModifyResult.noChange() : whatToDo.apply(obj);

			if (ModifyResult.isNoChange(result)) {
				if (obj != null && obj.doOnAllGameObjects(whatToDo)) changedSth = true;
			}

			else if (ModifyResult.isRemovingFully(result)) {
				map.remove(key);
				changedSth = true;
			}

			else {
				map.remove(key);
				for (GameObject v : result.newValues) {
					if (v != null) {
						map.put(key, (T) v);
						v.doOnAllGameObjects(whatToDo);
					}
				}
				changedSth = true;
			}
		}
		return changedSth;
	}

	public static <T extends GameObject> T[] doOnAllGameObjectsArray(T[] array, Function<GameObject, ModifyResult> whatToDo) {
		if (array == null) return null;
		boolean changedSth = false;
		List<T> list = new ArrayList<>();
		for (T obj : array) {

			ModifyResult result = obj == null ? ModifyResult.noChange() : whatToDo.apply(obj);

			if (ModifyResult.isNoChange(result)) {
				if (obj != null && obj.doOnAllGameObjects(whatToDo)) changedSth = true;
				list.add(obj);
			}

			else if (ModifyResult.isRemovingFully(result)) {
				changedSth = true;
			}

			else {
				for (GameObject v : result.newValues) {
					if (v != null) {
						list.add((T) v);
						v.doOnAllGameObjects(whatToDo);
					}
				}
				changedSth = true;
			}
		}
		if (!changedSth) return array;
		return list.toArray(array);
	}

	public static final class ModifyResult {

		public GameObject[] newValues;

		private ModifyResult(GameObject[] newValues) {
			this.newValues = newValues;
		}

		public static ModifyResult noChange() {
			return null;
		}

		public static ModifyResult singeReplacement(GameObject object) {
			return object == null
					? removeFully()
					: new ModifyResult(new GameObject[]{object});
		}

		public static ModifyResult multipleReplacements(GameObject... objects) {
			return new ModifyResult(objects);
		}

		public static ModifyResult removeFully() {
			return new ModifyResult(null);
		}

		public static boolean isNoChange(ModifyResult result) {
			return result == null;
		}

		public static boolean isRemovingFully(ModifyResult result) {
			return result.newValues == null || result.newValues.length == 0;
		}

	}

}