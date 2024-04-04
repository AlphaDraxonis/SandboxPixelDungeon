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

package com.shatteredpixel.shatteredpixeldungeon.scrollofdebug;

import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.MultiWindowTabComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTabbed;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextInput;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

public class WndInspectObject extends WndTabbed {

	public static final int ACCESS_LEVEL_PRIVATE = 0, ACCESS_LEVEL_PCKGE_PRIVATE = 1, ACCESS_LEVEL_PROTECTED = 2, ACCESS_LEVEL_PUBLIC = 3;

	private final Reference reference; //shows variable values for this object

	private final int accessLevel; //minimum access modifier required for stuff to appear


	protected TextInput searchIpnut;
	protected RedButton startSearch;

	protected StyledButton openOnGitHub;

	protected VariablesTab variablesTab;

	public WndInspectObject(Reference reference, int accessLevel) {

		super();

		resize(Math.min(WndTitledMessage.WIDTH_MAX, (int) (PixelScene.uiCamera.width * 0.9)), (int) (PixelScene.uiCamera.height * 0.8f));

		this.reference = reference;
		this.accessLevel = accessLevel;

		variablesTab = new VariablesTab(reference, accessLevel);

		MultiWindowTabComp[] tbs = {
				variablesTab
		};
		for (int i = 0; i < tbs.length; i++) {
			add(tbs[i]);
			tbs[i].setRect(0, 0, width, height);
			int index = i;
			add(new IconTab(tbs[i].createIcon()) {
				protected void select(boolean value) {
					super.select(value);
					tbs[index].active = tbs[index].visible = value;
				}

				@Override
				protected String hoverText() {
					return tbs[index].hoverText();
				}
			});
		}
		layoutTabs();
	}

	private static <T> void addAllFields(Class<T> clazz, int accessLevel, List<Field> fields) {
		for (Field f : clazz.getDeclaredFields()) {
			int mods = f.getModifiers();
			if (Modifier.isStatic(mods) || !canAccess(mods, accessLevel)) continue;
			fields.add(f);
		}
		Class<? super T> superClass = clazz.getSuperclass();
		if (superClass != null) addAllFields(superClass, accessLevel, fields);
	}

	public static boolean canAccess(int modifiers, int minAccessLevel) {
		int accessLevel;
		if (Modifier.isPublic(modifiers)) accessLevel = ACCESS_LEVEL_PUBLIC;
		else if (Modifier.isPrivate(modifiers)) accessLevel = ACCESS_LEVEL_PRIVATE;
		else if (Modifier.isProtected(modifiers)) accessLevel = ACCESS_LEVEL_PROTECTED;
		else accessLevel = ACCESS_LEVEL_PCKGE_PRIVATE;

		return minAccessLevel <= accessLevel;
	}

	public static <T> void show(Class<T> clazz, int accessLevel, T object) {
		EditorScene.show(new WndInspectObject(new Reference(clazz, object, clazz.getSimpleName()), accessLevel));
	}

	protected static class VariablesTab extends MultiWindowTabComp {

		protected FieldComp[] fields;

		public <T> VariablesTab(Reference reference, int accessLevel) {

			List<Field> allFields = new LinkedList<>();
			addAllFields(reference.getType(), accessLevel, allFields);

			Object object = reference.getValue();

			fields = new FieldComp[allFields.size()];
			int i = 0;
			for (Field f : allFields) {
				FieldComp c = new FieldComp(f, object);
				fields[i++] = c;
				content.add(c);
			}
		}

		@Override
		public void layoutOwnContent() {
			content.setSize(width, 0);
			content.setSize(width, EditorUtilies.layoutCompsLinear(2, 16, content, fields));
		}

		@Override
		public Image createIcon() {
			return new ItemSprite();
		}

		@Override
		public String hoverText() {
			return null;
		}
	}

}