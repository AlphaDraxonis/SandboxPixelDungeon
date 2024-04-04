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

import com.shatteredpixel.shatteredpixeldungeon.editor.lua.LuaManager;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.LuaScript;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VariablesPanel extends CodeInputPanel {

	private final String tableName;

	public VariablesPanel(String label, String tableName) {
		title.text(label);
		this.tableName = tableName;
	}


	@Override
	protected void onAddClick() {
		super.onAddClick();
		remover.visible = remover.active = false;
		layout();
	}

	@Override
	protected void onRemove() {
		if (textInput != null) textInput.setText("");
	}


	@Override
	protected String convertToLuaCode() {
		return tableName + " = {\n" +
				textInput.getText() +
				"\n}";
	}

	@Override
	protected String compile() {
		String result = super.compile();
		String code = convertToLuaCode();
		if (code == null) return result;
		String cleanedComments = LuaScript.cleanLuaCode(code.replace('\"', '_'));
		if (cleanedComments.contains("--")) {
			String comments = Messages.get(this, "compile_error_comments_not_allowed");
			if (result == null) return comments;
			return result + "\n" + comments;
		}
		return result;
	}

	@Override
	public void applyScript(boolean forceChange, LuaScript fullScript, String cleanedCode) {
		String newCode = LuaScript.extractTableFromScript(cleanedCode, fullScript.code, tableName, true);

		if (!forceChange && newCode != null && textInput != null && !textInput.getText().isEmpty()) {
			//smart insert code
			StringBuilder writeCode = new StringBuilder();

			LuaTable oldTable = LuaManager.load("return {" + textInput.getText().replaceAll("\\bnil\\b", "\"_\"") + "}").call().checktable();
			LuaTable newTable = LuaManager.load("return {" +       newCode      .replaceAll("\\bnil\\b", "\"_\"") + "}").call().checktable();
			String cleanedNewCode = LuaScript.cleanLuaCode(newCode);
			String oldCode = textInput.getText();
			String cleanedOldCode = LuaScript.cleanLuaCode(oldCode);
			LuaValue[] oldKeys = oldTable.keys();
			List<String> oldKeysString = new ArrayList<>(oldKeys.length + 1);
			for (int i = 0; i < oldKeys.length; i++) {
				oldKeysString.add(oldKeys[i].toString());
			}
			LuaValue[] newKeys = newTable.keys();
			for (int i = 0; i< newKeys.length; i++) {

				String key = newKeys[i].toString();
				String value = extractKeyWithValueAssignment(cleanedNewCode, newCode, key);

				if (i > 0) {
					writeCode.append(",\n");
				}

				if (oldKeysString.contains(key)) {
					oldKeysString.remove(key);

//					String oldValue = extractKeyWithValueAssignment(cleanedOldCode, oldCode, key);
//					//comments are not supported
//					if (oldValue != null && !oldValue.equals(value)) writeCode.append("--[[").append(oldValue).append("]]\n     ").append(value);
//					else
						writeCode.append(value);

				} else {
					writeCode.append(value);
				}
			}

			int insertIndex = 0;
			for (String key : oldKeysString) {
				String value = extractKeyWithValueAssignment(cleanedOldCode, oldCode, key);
				if (value != null) {
					writeCode.insert(insertIndex, value + ",\n");
					insertIndex += value.length() + 2;
				}
			}

			textInput.setText(writeCode.toString());

		} else {
			setCode(forceChange, newCode);
		}
	}

	private static String extractKeyWithValueAssignment(String cleanedCode, String originalCode, String key) {
		Matcher findKeyStart = Pattern.compile("\\b"+key+"\\b", Pattern.DOTALL).matcher(cleanedCode);
		if (!findKeyStart.find()) return null;
		int indexValueStart = findKeyStart.start();
		int indexSemikolon = cleanedCode.indexOf(';', indexValueStart);
		int indexNormalComma = cleanedCode.indexOf(',', indexValueStart);
		int indexEnd = Math.min(indexSemikolon == -1 ? Integer.MAX_VALUE : indexSemikolon, indexNormalComma == -1 ? Integer.MAX_VALUE : indexNormalComma);
		return indexEnd == Integer.MAX_VALUE ? originalCode.substring(indexValueStart) : originalCode.substring(indexValueStart, indexEnd);
	}
}