/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
 *
 * Sandbox Pixel Dungeon
 * Copyright (C) 2023-2025 AlphaDraxonis
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

package com.watabou.idewindowactions;

import com.watabou.noosa.Game;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface CodeInputPanelInterface {

	String convertToLuaCode();
	String compile();

	String getLabel();

	void applyScript(boolean forceChange, LuaScript fullScript, String cleanedCode);

	void setCode(boolean forceChange, String code);


	static String methodPanelGetLabel(Method method, String[] paramNames) {
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

	static String methodPanelToLuaCode(Method method, String customCode, String[] paramNames) {

		if (customCode == null) return null;

		StringBuilder b = new StringBuilder();

		b.append("function ");
		b.append(method.getName());
		b.append("(this, vars");
		for (String p : paramNames) {
			b.append(", ");
			b.append(p);
		}
		b.append(")\n");

		b.append(customCode);

		b.append("\nend");

		return b.toString();
	}

	static String defaultMethodCode(Method method, String[] paramNames) {
		StringBuilder b = new StringBuilder();
		if (method.getReturnType() != void.class)
			b.append("return ");
		b.append("this:super_").append(method.getName()).append("(");
		for (int i = 0; i < paramNames.length; i++) {
			b.append(paramNames[i]);
			if (i < paramNames.length - 1)
				b.append(", ");
		}
		b.append(");\n");
		return b.toString();
	}


	static String extractKeyWithValueAssignment(String cleanedCode, String originalCode, String key) {
		Matcher findKeyStart = Pattern.compile("\\b"+key+"\\b", Pattern.DOTALL).matcher(cleanedCode);
		if (!findKeyStart.find()) return null;
		int indexValueStart = findKeyStart.start();
		int indexSemikolon = cleanedCode.indexOf(';', indexValueStart);
		int indexNormalComma = cleanedCode.indexOf(',', indexValueStart);
		int indexEnd = Math.min(indexSemikolon == -1 ? Integer.MAX_VALUE : indexSemikolon, indexNormalComma == -1 ? Integer.MAX_VALUE : indexNormalComma);
		return indexEnd == Integer.MAX_VALUE ? originalCode.substring(indexValueStart) : originalCode.substring(indexValueStart, indexEnd);
	}



	static void viewDocumentation() {
		Game.platform.openURI("https://docs.google.com/document/d/1uXWzyO0wXJ6jDfKB3wrzVcFY4WvaCsX7oLtc5EkxDi0/?usp=sharing");
	}

	static String compileResult(CodeInputPanelInterface[] codeInputPanels) {//null as return means no errors
		StringBuilder b = new StringBuilder();
		for (CodeInputPanelInterface inputPanel : codeInputPanels) {
			if (inputPanel == null) continue;
			String msg = inputPanel.compile();
			if (msg != null) {
				if (b.length() > 0) b.append("\n\n");
				b.append(' ').append(inputPanel.getLabel()).append(" :\n");
				b.append(msg);
			}
		}
		String result = b.toString();
		return result.isEmpty() ? null : result;
	}
}
