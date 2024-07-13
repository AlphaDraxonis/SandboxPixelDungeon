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

package com.watabou.idewindowactions;

import com.watabou.noosa.Game;

public final class IDEWindowActions {

	private IDEWindowActions() {}

	public static LuaScript selectedScriptFromSelectionDialog;

	public static void viewDocumentation() {
		Game.platform.openURI("https://docs.google.com/document/d/1uXWzyO0wXJ6jDfKB3wrzVcFY4WvaCsX7oLtc5EkxDi0/?usp=sharing");
	}

	public static String compileResult(CodeInputPanelInterface[] codeInputPanels) {//null means no errors
		StringBuilder b = new StringBuilder();
		for (CodeInputPanelInterface inputPanel : codeInputPanels) {
			String msg = inputPanel.compile();
			if (msg != null) {
				if (b.length() > 0) b.append("\n\n");
				b.append('_').append(inputPanel.getLabel()).append("_:\n");
				b.append(msg);
			}
		}
		String result = b.toString();
		return result.isEmpty() ? null : result;
	}
}