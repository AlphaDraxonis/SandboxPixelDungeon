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

package com.shatteredpixel.shatteredpixeldungeon.android.ideactivity;

import android.content.Context;
import android.util.AttributeSet;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.LuaManager;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.NotAllowedInLua;
import com.watabou.idewindowactions.CodeInputPanelInterface;
import com.watabou.idewindowactions.LuaScript;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.ArrayList;
import java.util.List;

@NotAllowedInLua
public class AndroidVariablesPanel extends AndroidCodeInputPanel {

	{
		btnAdd.setVisibility(GONE);
		btnExpand.setVisibility(VISIBLE);
	}

	private final String tableName;
	private final String label;

	public AndroidVariablesPanel(Context context) {
		super(context);
		this.tableName = "unnamedTable";
		this.label = "no-label";
		super.label.setText(AndroidIDEWindow.createSpannableStringWithColorsFromText((getLabel())));
	}

	public AndroidVariablesPanel(Context context, String tableName, String label) {
		super(context);
		this.tableName = tableName;
		this.label = label;
		super.label.setText(getLabel());
	}

	public AndroidVariablesPanel(Context context, AttributeSet attrs, String tableName, String label) {
		super(context, attrs);
		this.tableName = tableName;
		this.label = label;
		super.label.setText(AndroidIDEWindow.createSpannableStringWithColorsFromText((getLabel())));
	}

	public AndroidVariablesPanel(Context context, AttributeSet attrs, int defStyleAttr, String tableName, String label) {
		super(context, attrs, defStyleAttr);
		this.tableName = tableName;
		this.label = label;
		super.label.setText(AndroidIDEWindow.createSpannableStringWithColorsFromText((getLabel())));
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public String convertToLuaCode() {
		return tableName + " = {\n" + text + "\n}";
	}

	@Override
	public String compile() {
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

		if (!forceChange && newCode != null && !text.isEmpty()) {
			//smart insert code
			StringBuilder writeCode = new StringBuilder();

			LuaTable oldTable = LuaManager.load("return {" + text.replaceAll("\\bnil\\b", "\"_\"") + "}").call().checktable();
			LuaTable newTable = LuaManager.load("return {" +       newCode      .replaceAll("\\bnil\\b", "\"_\"") + "}").call().checktable();
			String cleanedNewCode = LuaScript.cleanLuaCode(newCode);
			String oldCode = text;
			String cleanedOldCode = LuaScript.cleanLuaCode(oldCode);
			LuaValue[] oldKeys = oldTable.keys();
			List<String> oldKeysString = new ArrayList<>(oldKeys.length + 1);
			for (int i = 0; i < oldKeys.length; i++) {
				oldKeysString.add(oldKeys[i].toString());
			}
			LuaValue[] newKeys = newTable.keys();
			for (int i = 0; i< newKeys.length; i++) {

				String key = newKeys[i].toString();
				String value = CodeInputPanelInterface.extractKeyWithValueAssignment(cleanedNewCode, newCode, key);

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
				String value = CodeInputPanelInterface.extractKeyWithValueAssignment(cleanedOldCode, oldCode, key);
				if (value != null) {
					writeCode.insert(insertIndex, value + ",\n");
					insertIndex += value.length() + 2;
				}
			}

			setText(writeCode.toString());

		} else {
			setCode(forceChange, newCode);
		}
	}
}