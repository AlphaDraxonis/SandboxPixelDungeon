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

package com.shatteredpixel.shatteredpixeldungeon.editor.lua.luaeditor;

import com.shatteredpixel.shatteredpixeldungeon.editor.lua.LuaScript;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class MethodPanel extends CodeInputPanel {

	private final Method method;
	private final String[] paramNames;

	public MethodPanel(Method method, String[] paramNames) {

		title.text(createLabel(method, paramNames));

		this.method = method;
		this.paramNames = paramNames;
	}

	private static String createLabel(Method method, String[] paramNames) {

		StringBuilder b = new StringBuilder();

		b.append(Modifier.toString(method.getModifiers())).append(' ');

		b.append(method.getReturnType().getSimpleName()).append(" _");
		b.append(method.getName()).append("_ (");

		Class<?>[] paramTypes = method.getParameterTypes();
		for (int i = 0; i < paramTypes.length; i++) {
			b.append(paramTypes[i].getSimpleName()).append(' ');
			b.append(paramNames[i]);
			if (i < paramTypes.length - 1)
				b.append(", ");
		}

		b.append(')');

		return b.toString();
	}

	@Override
	protected String createDescription() {
		return Messages.get(this, "method_" + method.getName());
	}

	@Override
	protected String convertToLuaCode() {

		if (textInput == null) return null;

		StringBuilder b = new StringBuilder();

		b.append("function ");
		b.append(method.getName());
		b.append("(this, vars, super");
		for (String p : paramNames) {
			b.append(", ");
			b.append(p);
		}
		b.append(")\n");

		b.append(textInput.getText());

		b.append("\nend");

		return b.toString();
	}

	@Override
	String getLabel() {
		StringBuilder b = new StringBuilder(method.getName());
		b.append(" (");
		Class<?>[] paramTypes = method.getParameterTypes();
		for (int i = 0; i < paramTypes.length; i++) {
			b.append(paramTypes[i].getSimpleName()).append(' ');
			b.append(paramNames[i]);
			if (i < paramTypes.length - 1)
				b.append(", ");
		}
		b.append(')');
		return b.toString();
	}

//	@Override
//	protected String compile() {
//		String result = super.compile();
//		String code = convertToLuaCode();
//		if (code == null) return result;
//		String cleanedCode = LuaScript.cleanLuaCode(code);
//		if (LuaScript.extractTableFromScript(cleanedCode, code, "vars", false) != null
//				|| LuaScript.extractTableFromScript(cleanedCode, code, "static", false) != null) {
//			String tableNamesReserved = Messages.get(this, "compile_error_table_names_reserved", "vars", "static");
//			if (result == null) return tableNamesReserved;
//			return result + "\n" + tableNamesReserved;
//		}
//		return result;
//	}

	@Override
	public void applyScript(boolean forceChange, LuaScript fullScript, String cleanedCode) {
		setCode(forceChange, LuaScript.extractMethodFromScript(cleanedCode, fullScript.code, method.getName()));
	}

	public String getMethodName() {
		return method.getName();
	}
}