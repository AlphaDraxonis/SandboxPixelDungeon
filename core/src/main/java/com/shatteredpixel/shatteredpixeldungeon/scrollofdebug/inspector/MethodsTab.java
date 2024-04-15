package com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.inspector;

import com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.SearchBar;
import com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.WndScrollOfDebug;
import com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.references.Reference;
import com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.references.StaticReference;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

public class MethodsTab extends ObjInspectorTab {

	public <T> MethodsTab(Reference reference, int accessLevel) {

		Class<?> clazz = reference.getValue() == null ? reference.getType() : reference.getValue().getClass();
		List<Method> allMethods = new LinkedList<>();
		if (reference instanceof StaticReference) addStaticMethods(clazz, accessLevel, allMethods);
		else addAllFields(clazz, accessLevel, allMethods);

		Object object = reference.getValue();

		comps = new MethodComp[allMethods.size()];
		int i = 0;
		for (Method m : allMethods) {
			MethodComp c = new MethodComp(m, object);
			comps[i++] = c;
			add(c);
		}

	}

	@Override
	public SearchBar getSearchBar() {
		return searchBar;
	}

	private static <T> void addAllFields(Class<T> clazz, int accessLevel, List<Method> allMethods) {
		for (Method m : clazz.getDeclaredMethods()) {
			int mods = m.getModifiers();
			if (Modifier.isStatic(mods) || !WndScrollOfDebug.canAccess(mods, accessLevel)) continue;
			if (m.getExceptionTypes().length > 0) continue;//don't include methods with checked exceptions
			if (m.getName().contains("_")) continue;
			allMethods.add(m);
		}
		Class<? super T> superClass = clazz.getSuperclass();
		if (superClass != null) addAllFields(superClass, accessLevel, allMethods);
	}

	private static <T> void addStaticMethods(Class<T> clazz, int accessLevel, List<Method> allMethods) {
		for (Method m : clazz.getDeclaredMethods()) {
			int mods = m.getModifiers();
			if (!Modifier.isStatic(mods) || !WndScrollOfDebug.canAccess(mods, accessLevel)) continue;
			if (m.getExceptionTypes().length > 0) continue;//don't include methods with checked exceptions
			if (m.getName().contains("_")) continue;
			allMethods.add(m);
		}
	}
}