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
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.luaeditor.IDEWindow;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.NotAllowedInLua;
import com.watabou.idewindowactions.LuaScript;

import java.util.List;

@NotAllowedInLua
public class AndroidAdditionalCodePanel extends AndroidCodeInputPanel {

	{
		desc.setText(Messages.get(IDEWindow.class, "additional_code_info"));
	}

	public AndroidAdditionalCodePanel(Context context) {
		super(context);
	}

	public AndroidAdditionalCodePanel(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AndroidAdditionalCodePanel(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	public String convertToLuaCode() {
		return textInput.getText().toString();
	}

	@Override
	public String getLabel() {
		return Messages.get(IDEWindow.class, "additional_code_title");
	}

	@Override
	public void applyScript(boolean forceChange, LuaScript fullScript, String cleanedCode) {
		//do nothing, wait for actualApplyScript() instead
	}

	public void actualApplyScript(boolean forceChange, LuaScript fullScript, String cleanedCode, List<String> functions) {
		if (functions.isEmpty()) {
			setCode(forceChange, null);
			return;
		}

		//take all methods that are not used by others
		StringBuilder b = new StringBuilder();
		for (String functionName : functions) {
			b.append(LuaScript.extractRawMethodFromScript(cleanedCode, fullScript.code, functionName));
			b.append("\n\n");
		}
		setCode(forceChange, b.toString());
	}
}