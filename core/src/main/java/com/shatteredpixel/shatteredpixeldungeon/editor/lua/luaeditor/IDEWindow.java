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

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.customizables.ChangeItemCustomizable;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.CustomObject;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.LuaScript;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ChooseObjectComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.SimpleWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTextInput;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Reflection;

import java.util.List;

public class IDEWindow extends Component {

	//Main window to edit lua files

	//No title
	//Obv use one scrollpane

	//Top: select file (if file not exists: will be created when saved (always ask for saving when trying to close, and compile))

	//Prominent toolbar: insert template (script, method, line), toggle visibility of fully qualified names, COMPILE (+ view errors), paste cut copy

	//Have an area for declaring variables
	//Space for many methods
	//Space for additional code
	//Space for writing script name and description, so it can be used as template flawlessly

	private LuaScript script;

	private ScriptSelector scriptSelector;
	private CodeInputPanel[] codeInputPanels;
	private CodeInputPanel inputDesc, inputLocalVars, inputScriptVars, inputGlobalVars;
	private final Class<?> clazz;
	private final CustomObject customObject;

	public IDEWindow(CustomObject customObject, Runnable layoutParent) {
		this.customObject = customObject;
		this.clazz = customObject.luaClass.getClass();

		scriptSelector = new ScriptSelector();
		add(scriptSelector);

		List<LuaMethodManager> methods = LuaMethodManager.getAllMethodsInOrder(clazz);

		codeInputPanels = new CodeInputPanel[methods.size() + 4];

		codeInputPanels[0] = inputDesc = new CodeInputPanel() {
			{
				title.text("ScriptDESC tzz");
			}

			@Override
			protected String createDescription() {
				return Messages.get(this, "desc");
			}

			@Override
			protected String convertToLuaCode() {
				return "--" + textInput.getText();
			}

			@Override
			public void applyScript(boolean forceChange, LuaScript fullScript, String cleanedCode) {
				if (forceChange || textInput == null || textInput.getText().isEmpty()) {
					if (textInput == null) onAddClick();
					textInput.setText(fullScript.desc);
				}
			}
			@Override
			protected void layoutParent() {
				layoutParent.run();
			}

			@Override
			protected void onAddClick() {
				super.onAddClick();
				remover.visible = remover.active = false;
			}

			@Override
			protected void onRemove() {
				super.onRemove();
				adder.visible = adder.active = false;
			}
		};
		add(inputDesc);

		codeInputPanels[1] = inputLocalVars = new VariablesPanel("local vars tzz", "vars") {
			@Override
			protected void layoutParent() {
				layoutParent.run();
			}

			@Override
			protected String createDescription() {
				return "tzz Define instance attributes. Separate them using a comma (,). Type declarations are not required!\nExample: aNumber = 5, item = nil";
			}
		};
		add(inputLocalVars);

		codeInputPanels[2] = inputScriptVars = new VariablesPanel("static vars tzz", "static") {
			@Override
			protected void layoutParent() {
				layoutParent.run();
			}

			//tzz description
		};
		add(inputScriptVars);

		int i = 4;
		for (LuaMethodManager methodInfo : methods) {
			codeInputPanels[i] = new MethodPanel(methodInfo.method, methodInfo.paramNames) {
				@Override
				protected void layoutParent() {
					layoutParent.run();
				}
			};
			add(codeInputPanels[i]);
			i++;
		}

		scriptSelector.selectObject(CustomDungeonSaves.readLuaFile(customObject.pathToScript));
	}

	@Override
	protected void layout() {
		height = 0;
		height = EditorUtilies.layoutCompsLinear(2, this, scriptSelector) + 2;
		height = EditorUtilies.layoutCompsLinear(2, this, codeInputPanels);
	}

	public static void showWindow(CustomObject customObject) {

		SimpleWindow w = new SimpleWindow((int) (PixelScene.uiCamera.width * 0.8f),  (int) (PixelScene.uiCamera.height * 0.9f));

		IDEWindow ideWindow = new IDEWindow(customObject, w::layout);

		w.initComponents(null, ideWindow, null);

		EditorScene.show(w);

	}

	private class ScriptSelector extends ChooseObjectComp {

		public ScriptSelector() {
			super("SCRIPT tzz");
		}

		@Override
		protected void doChange() {
			//TODO tzzz warning that all local changes will be lost!
			List<LuaScript> scripts = CustomDungeonSaves.findScripts(script -> script.type.isAssignableFrom(clazz));

			String[] options = new String[scripts.size() + 1];
			options[0] = "<new>tzz";
			int i = 1;
			for (LuaScript s : scripts) {
				options[i++] = s.name;
			}
			EditorScene.show(new WndOptions(
					Messages.get(ChangeItemCustomizable.class, "custom_sprite"),//tzz
					Messages.get(ChangeItemCustomizable.class, "custom_sprite_info", CustomDungeonSaves.getAdditionalFilesDir().file().getAbsolutePath()),
					options
			) {
				{
					tfMessage.setHighlighting(false);
				}

				@Override
				protected Image getIcon(int index) {
					if (index == 0) return new ItemSprite();
					if (Mob.class.isAssignableFrom(scripts.get(index-1).type)) return ((Mob) Reflection.newInstance(scripts.get(index-1).type)).sprite();
					return new ItemSprite();
				}

				@Override
				protected void onSelect(int index) {
					if (index == 0) {
						EditorScene.show(new WndTextInput(
								"TITLE: enter name, relplace space with _, check if file name is already used",
								"body",
								"", 50, false, "ok", "nein"
						) {
							@Override
							public void onSelect(boolean positive, String text) {
								if (positive) {
									LuaScript luaScript = new LuaScript(clazz, text, "empty desc", null);
									ScriptSelector.this.selectObject(luaScript);
								}
							}
						});
					} else {
						ScriptSelector.this.selectObject(scripts.get(index-1));
					}
				}
			});
		}

		@Override
		public void selectObject(Object object) {
			super.selectObject(object);
			script = ((LuaScript) object);
			String cleanedCode;
			LuaScript currentScript;
			if (script == null) {
				currentScript = new LuaScript(Object.class, "", "", "");
				currentScript.code = "";
				cleanedCode = "";
			} else {
				currentScript = script;
				cleanedCode = LuaScript.cleanLuaCode(script.code);
			}
			customObject.pathToScript = currentScript.pathFromRoot;

			for (CodeInputPanel inputPanel : codeInputPanels) {
				if (inputPanel != null/*<- <- <- tzz remove*/) inputPanel.applyScript(true, currentScript, cleanedCode);
			}
			IDEWindow.this.layout();
		}
	}
}