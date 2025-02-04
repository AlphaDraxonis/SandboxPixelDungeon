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

import com.watabou.NotAllowedInLua;
import com.watabou.idewindowactions.CodeInputPanelInterface;
import com.watabou.idewindowactions.LuaScript;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

@NotAllowedInLua
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
		return LuaMethodManager.descriptionForMethod(method);
	}

	@Override
	protected void onRemove() {
		String currentText = textInput != null ? textInput.getText() : textInputText;
		if (currentText != null && currentText.equals(CodeInputPanelInterface.defaultMethodCode(method, paramNames))) {
			textInputText = null;
			if (textInput != null) textInput.setText("");
		}
		super.onRemove();
	}

	@Override
	public String convertToLuaCode() {
		return CodeInputPanelInterface.methodPanelToLuaCode(method,
				textInput == null && (textInputText == null || textInputText.isEmpty()) ? null
						: textInput == null ? textInputText : textInput.getText(),
				paramNames);
	}

	@Override
	public String getLabel() {
		return CodeInputPanelInterface.methodPanelGetLabel(method, paramNames);
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

	@Override
	protected void onAddClick() {
		super.onAddClick();
		if (textInputText == null || textInputText.isEmpty()) {
			textInput.setText(textInputText = CodeInputPanelInterface.defaultMethodCode(method, paramNames));
		}
	}
}