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

import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.LuaManager;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.LuaScript;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.FoldableCompWithAdd;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.noosa.TextInput;
import com.watabou.noosa.ui.Component;

public abstract class CodeInputPanel extends FoldableCompWithAdd {

	protected TextInput textInput;
	protected RenderedTextBlock info;

	@Override
	protected int titleFontSize() {
		return 7;
	}

	@Override
	protected void onAddClick() {
		onAdd(null, false);
	}

	@Override
	protected void onRemove() {
		super.onRemove();
		textInput = null;
	}

	@Override
	protected Component createBody(Object param) {
		return new BodyWrapper();
	}

//	private int lastNumLines;
//	private void resizeTextArea(TextArea textArea) {
//		int numLines = textArea.getLines();
//		if (lastNumLines != numLines) {
//			lastNumLines = numLines;
//			layoutParent();
//			textArea.invalidateHierarchy();
//		}
//	}
//
	protected float calculateRequiredHeight(TextArea textArea) {
		return 100;
//		float height = (textArea.getLines() + 4) * textArea.getStyle().font.getLineHeight() / Camera.main.zoom;
//		return Math.max(height, 100); // 100 = minimum height
	}

	protected String createDescription() {
		return null;
	}

	protected abstract String convertToLuaCode();

	public abstract void applyScript(boolean forceChange, LuaScript fullScript, String cleanedCode);

	protected final void setCode(boolean forceChange, String code) {
		if (code == null) {
			if (forceChange && textInput != null) onRemove();//TODO no warning!
			return;
		}

		if (textInput == null || textInput.getText().isEmpty()) {
			if (textInput == null) onAddClick();
			else if (!textInput.visible) expand();
			textInput.setText(code);
		} else {
			String oldCode = forceChange ? "" : "--[[\n" + textInput.getText() + "]]\n\n";
			textInput.setText(oldCode + code);
		}
	}

	//can only check for syntax errors, not for undeclared variables
	protected void compile() {
		String code = convertToLuaCode();
		LuaManager.compile(code);
		String cleanedCode = LuaScript.cleanLuaCode(code);
		if (cleanedCode.indexOf("function", 2) != -1) {
			throw new RuntimeException("Declaring functions inside functions is not possible in SandboxPD!");
		}
	}

	private class BodyWrapper extends Component {
		@Override
		protected void createChildren(Object... params) {
			textInput = new TextInput(Chrome.get(Chrome.Type.TOAST_WHITE), true, 7, PixelScene.uiCamera.zoom) {
				@Override
				protected void layout() {
					height = calculateRequiredHeight((TextArea) textField);
					super.layout();
				}

//			@Override
//			protected void onKeyTyped(char c) {
//				super.onKeyTyped(c);
//				resizeTextArea((TextArea) textField);
//			}
//
//			@Override
//			public void pasteFromClipboard() {
//				super.pasteFromClipboard();
//				resizeTextArea((TextArea) textField);
//			}
			};
			add(textInput);

			info = PixelScene.renderTextBlock(6);
			String desc = createDescription();
			if (desc == null) info.visible = info.active = false;
			else info.text(desc);
			add(info);
		}

		@Override
		protected void layout() {
			info.maxWidth((int) width);
			height = 0;
			height = EditorUtilies.layoutCompsLinear(2, this, info, textInput);
		}

		@Override
		public synchronized void destroy() {
			super.destroy();
			textInput = null;
			info = null;
		}

	}

}