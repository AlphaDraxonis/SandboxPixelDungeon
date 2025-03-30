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

package com.shatteredpixel.shatteredpixeldungeon.editor.lua.luaeditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.LuaManager;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.FoldableCompWithAdd;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.HeroSelectScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptionsCondensed;
import com.watabou.NotAllowedInLua;
import com.watabou.idewindowactions.CodeInputPanelInterface;
import com.watabou.noosa.TextInput;
import com.watabou.noosa.ui.Component;

@NotAllowedInLua
public abstract class CodeInputPanel extends FoldableCompWithAdd implements CodeInputPanelInterface {

	protected String textInputText;

	protected TextInput textInput;
	protected RenderedTextBlock info;

	{
		remover.icon(Icons.TRASH.get());
	}

	@Override
	protected int titleFontSize() {
		return 7;
	}

	@Override
	public void expand() {
		onAdd(null, true);
	}

	@Override
	public void fold() {
		textInputText = textInput.getText();

		if (body != null) {
			body.destroy();
			remove(body);
			body = null;
		}
		textInput = null;

		adder.setVisible(false);
		remover.setVisible(true);

		expanded = false;

		layoutParent();
	}

	@Override
	protected void onAddClick() {
		expand();
	}

	@Override
	protected void onRemove() {
		Runnable superCall = super::onRemove;

		String currentText = textInput != null ? textInput.getText() : textInputText;
		if (currentText == null || currentText.isEmpty()) {
			superCall.run();
			textInput = null;
			textInputText = null;
			return;
		}

		EditorScene.show(new WndOptionsCondensed(Icons.WARNING.get(),
				Messages.get(CodeInputPanel.class, "remove_title"),
				Messages.get(CodeInputPanel.class, "remove_body"),
				Messages.get(HeroSelectScene.class, "daily_yes"), Messages.get(HeroSelectScene.class, "daily_no")){
			@Override
			protected void onSelect(int index) {
				if (index == 0) {
					superCall.run();
					textInput = null;
					textInputText = null;
				}
			}
		});
	}

	@Override
	protected Component createBody(Object param) {
		return new BodyWrapper();
	}

	private int lastNumLines;
	private void resizeTextArea(TextArea textArea) {
		int numLines = textArea.getLines();
		if (lastNumLines != numLines) {
			lastNumLines = numLines;
			layoutParent();
			//TODO everything not working!
			if (textInput.isVisibleOnScreen()) Gdx.app.postRunnable(() -> {
				textArea.invalidateHierarchy();
				if (numLines > 0) {
					int pos = textArea.getCursorPosition();
					textArea.moveCursorLine(0);
					Gdx.app.postRunnable(()->textArea.setCursorPosition(pos));
				}
			});
//			else textArea.invalidateHierarchy();

//			textArea.invalidate();
		}
	}

	protected float calculateRequiredHeight(TextArea textArea) {
//		float height = ((textArea.getLines()+2) * textArea.getStyle().font.getLineHeight()) / PixelScene.uiCamera.zoom;
//		return Math.max(height, 70); // 70 = minimum height
		return 100;
	}

	protected String createDescription() {
		return null;
	}

	@Override
	public void setCode(boolean forceChange, String code) {
		if (code == null) {
			if (forceChange && textInput != null) onRemove();
			return;
		}

		if (textInput == null || textInput.getText().isEmpty()) {
			textInputText = code;
			if (textInput != null) textInput.setText(code);
			else {
				expandAndFold.setVisible(true);
				adder.setVisible(false);
				remover.setVisible(true);
			}
		} else {
			String oldCode = forceChange ? "" : "--[[\n" + textInput.getText() + "]]\n\n";
			textInput.setText(oldCode + code);
			textInputText = oldCode + code;
		}
	}

	@Override
	public String getLabel() {
		return title.text();
	}

	//can only check for syntax errors, not for undeclared variables
	@Override
	public String compile() {
		String code = convertToLuaCode();
		return code == null ? null : LuaManager.compile(code);
	}

	protected void onTextChange() {}

	private class BodyWrapper extends Component {
		@Override
		protected void createChildren() {
			textInput = new TextInput(Chrome.get(Chrome.Type.TOAST_WHITE), true, 7, PixelScene.uiCamera.zoom) {
				@Override
				protected void layout() {
					height = calculateRequiredHeight((TextArea) textField);
					super.layout();
				}

				@Override
				public void setText(String text) {
					super.setText(text);
					onTextChange();
				}

//				@Override
//				public void setText(String text) {
//					super.setText(text);
//					if (info != null) {
//						Gdx.app.postRunnable(() -> resizeTextArea((TextArea) textField));
//					}
//				}
			};
			add(textInput);

			if (textInputText != null) textInput.setText(textInputText);
			else textInputText = "";

			info = PixelScene.renderTextBlock(6);
			String desc = createDescription();
			if (desc == null) info.setVisible(false);
			else info.text(desc);
			add(info);
		}

		@Override
		protected void layout() {
			info.maxWidth((int) width);
			height = 1;
			height = EditorUtilities.layoutCompsLinear(2, this, info, textInput);
		}

		@Override
		public synchronized void destroy() {
			super.destroy();
			textInput = null;
			info = null;
		}

	}

}
