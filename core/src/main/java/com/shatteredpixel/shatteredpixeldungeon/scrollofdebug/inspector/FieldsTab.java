package com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.inspector;

import com.badlogic.gdx.utils.IntMap;
import com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.SearchBar;
import com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.WndScrollOfDebug;
import com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.references.Reference;
import com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.references.StaticReference;
import com.watabou.utils.SparseArray;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FieldsTab extends ObjInspectorTab {

	public <T> FieldsTab(Reference reference, int accessLevel) {

		Class<?> clazz = reference.getValue() == null ? reference.getType() : reference.getValue().getClass();
		List<FieldLike> allFields = new LinkedList<>();
		if (reference instanceof StaticReference) addStaticFields(clazz, accessLevel, allFields);
		else if (clazz.isArray()) addArrayFields(clazz, reference.getValue(), accessLevel, allFields);
		else if (Collection.class.isAssignableFrom(clazz))
			addCollectionsFields(clazz, reference.getActualTypeArguments(), reference.getValue(), accessLevel, allFields);
		else if (Map.class.isAssignableFrom(clazz))
			addMapFields(clazz, reference.getActualTypeArguments(), reference.getValue(), accessLevel, allFields);
		else if (SparseArray.class.isAssignableFrom(clazz))
			addSparseArrayFields(clazz, reference.getActualTypeArguments(), ((SparseArray<?>) reference.getValue()), accessLevel, allFields);
		else {
			addAllFields(clazz, accessLevel, allFields);

			if (allFields.size() == 1 && allFields.get(0).getName().equals("value") || clazz == String.class) {
				allFields.clear();
				allFields.add(new FieldLike.FakeField(clazz,"value", Modifier.PUBLIC + Modifier.FINAL) {
					@Override
					public Object get(Object obj) throws IllegalAccessException {
						return obj;
					}

					@Override
					public void set(Object obj, Object value) throws IllegalAccessException {
						//can't set final values...
					}
				});
			}
		}

		Object object = reference.getValue();

		comps = new FieldComp[allFields.size()];
		int i = 0;
		for (FieldLike f : allFields) {
			FieldComp c = new FieldComp(f, object);
			comps[i++] = c;
			add(c);
		}

	}

	@Override
	public SearchBar getSearchBar() {
		return searchBar;
	}

	@Override
	protected void updateValues() {
		for (FieldComp c : ((FieldComp[]) comps)) {
			c.updateValueText();
		}
	}

	private static <T> void addAllFields(Class<T> clazz, int accessLevel, List<FieldLike> fields) {
		for (Field f : clazz.getDeclaredFields()) {
			int mods = f.getModifiers();
			if (Modifier.isStatic(mods) || !WndScrollOfDebug.canAccess(mods, accessLevel)) continue;
			fields.add(new FieldLike.RealField(f));
		}
		Class<? super T> superClass = clazz.getSuperclass();
		if (superClass != null) addAllFields(superClass, accessLevel, fields);
	}

	private void addCollectionsFields(Class<?> clazz, Type[] genericParameters, Object collection, int accessLevel, List<FieldLike> fields) {

		fields.add(new FieldLike.FakeField(int.class, "length", Modifier.PUBLIC + Modifier.FINAL) {
			@Override
			public Object get(Object obj) throws IllegalAccessException {
				return !(obj instanceof Collection) ? 0 : ((Collection<?>) obj).size();
			}

			@Override
			public void set(Object obj, Object value) throws IllegalAccessException {
				//cannot change...
			}
		});

		if (collection == null) return;

		Class<?> componentType = genericParameters == null
				|| genericParameters.length == 0
				|| !(genericParameters[0] instanceof Class<?>)
				? Object.class : (Class<?>) genericParameters[0];

		if (List.class.isAssignableFrom(clazz)) {
			int length = ((Collection<?>) collection).size();
			for (int i = 0; i < length; i++) {
				final int index = i;
				fields.add(new FieldLike.FakeField(componentType, "[" + index + "]", Modifier.PUBLIC) {
					@Override
					public Object get(Object obj) throws IllegalAccessException {
						return !(obj instanceof List) ? null : ((List<?>) obj).get(index);
					}

					@Override
					public void set(Object obj, Object value) throws IllegalAccessException {
						if (obj instanceof List) {
							((List) obj).set(index, value);
						} else throw new IllegalAccessException();
					}
				});
			}
		} else {//for Set and Queue
			int length = ((Collection<?>) collection).size();
			for (int i = 0; i < length; i++) {
				final int index = i;
				fields.add(new FieldLike.FakeField(componentType, "[@" + index + "]", Modifier.PUBLIC) {
					@Override
					public Object get(Object obj) throws IllegalAccessException {
						return !(obj instanceof Collection) ? null : ((Collection<?>) obj).toArray()[index];
					}

					@Override
					public void set(Object obj, Object value) throws IllegalAccessException {
						if (obj instanceof Collection) {
							Object o = ((Collection<?>) obj).toArray()[index];
							((Collection<?>) obj).remove(o);
							((Collection) obj).add(value);
						} else throw new IllegalAccessException();
					}
				});
			}
		}

	}

	private void addMapFields(Class<?> clazz, Type[] genericParameters, Object collection, int accessLevel, List<FieldLike> fields) {

		fields.add(new FieldLike.FakeField(int.class, "length", Modifier.PUBLIC + Modifier.FINAL) {
			@Override
			public Object get(Object obj) throws IllegalAccessException {
				return !(obj instanceof Map) ? 0 : ((Map<?, ?>) obj).size();
			}

			@Override
			public void set(Object obj, Object value) throws IllegalAccessException {
				//cannot change...
			}
		});

		if (collection == null) return;

		if (genericParameters == null) genericParameters = new Type[]{null, Object.class};

		for (Map.Entry<?, ?> entry : ((Map<?, ?>) collection).entrySet()) {
			fields.add(new FieldLike.FakeField((Class<?>) genericParameters[1], "[" + FieldComp.valueAsString(entry.getKey()) + "]", Modifier.PUBLIC) {
				@Override
				public Object get(Object obj) throws IllegalAccessException {
					if (obj instanceof Map) return ((Map<?, ?>) obj).get(entry.getKey());
					else throw new IllegalAccessException();
				}

				@Override
				public void set(Object obj, Object value) throws IllegalAccessException {
					if (obj instanceof Map) ((Map) obj).put(entry.getKey(), value);
					else throw new IllegalAccessException();
				}
			});
		}

	}
	
	private <T> void addSparseArrayFields(Class<?> clazz, Type[] genericParameters, SparseArray<T> sparseArray, int accessLevel, List<FieldLike> fields) {

		if (sparseArray == null) return;

		if (genericParameters == null) genericParameters = new Type[]{Object.class};

		for (IntMap.Entry<T> entry : sparseArray.entries()) {
			fields.add(new FieldLike.FakeField((Class<?>) genericParameters[0], "[" + FieldComp.valueAsString(entry.key) + "]", Modifier.PUBLIC) {
				@Override
				public Object get(Object obj) throws IllegalAccessException {
					if (obj instanceof SparseArray) return ((SparseArray<?>) obj).get(entry.key);
					else throw new IllegalAccessException();
				}

				@Override
				public void set(Object obj, Object value) throws IllegalAccessException {
					if (obj instanceof SparseArray) ((SparseArray<T>) obj).put(entry.key, ((T) value));
					else throw new IllegalAccessException();
				}
			});
		}

	}

	private static void addArrayFields(Class<?> clazz, Object obj, int accessLevel, List<FieldLike> fields) {

		fields.add(new FieldLike.FakeField(int.class, "length", Modifier.PUBLIC + Modifier.FINAL) {
			@Override
			public Object get(Object obj) throws IllegalAccessException {
				return Array.getLength(obj);
			}

			@Override
			public void set(Object obj, Object value) throws IllegalAccessException {
				//cannot change...
			}
		});

		if (obj != null) {
			Class<?> componentType = clazz.getComponentType();
			int length = Array.getLength(obj);
			for (int i = 0; i < length; i++) {
				final int index = i;
				fields.add(new FieldLike.FakeField(componentType, "[" + index + "]", Modifier.PUBLIC) {
					@Override
					public Object get(Object obj) throws IllegalAccessException {
						return Array.get(obj, index);
					}

					@Override
					public void set(Object obj, Object value) throws IllegalAccessException {
						Array.set(obj, index, value);
					}
				});
			}
		}
	}

	private static void addStaticFields(Class<?> clazz, int accessLevel, List<FieldLike> fields) {
		for (Field f : clazz.getDeclaredFields()) {
			int mods = f.getModifiers();
			if (!Modifier.isStatic(mods) || !WndScrollOfDebug.canAccess(mods, accessLevel)) continue;
			if (Modifier.isFinal(mods) && f.getType().isPrimitive() || f.getType() == String.class) continue; //don't include immutable constants
			fields.add(new FieldLike.RealField(f));
		}
	}
}