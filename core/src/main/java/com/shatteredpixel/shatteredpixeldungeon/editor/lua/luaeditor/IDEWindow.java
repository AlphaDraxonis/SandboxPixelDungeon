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

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.WndEditorInv;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.*;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.CustomObject;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.LuaScript;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.SimpleWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.*;
import com.shatteredpixel.shatteredpixeldungeon.windows.*;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextInput;
import com.watabou.noosa.ui.Component;

import java.util.Arrays;
import java.util.List;

public class IDEWindow extends Component {

	//TODO tzz button: view class on github

	//Top: select file (if file not exists: will be created when saved (always ask for saving when trying to close, and compile))

	//Prominent toolbar: insert template (script, method, line), toggle visibility of fully qualified names, COMPILE (+ view errors), paste cut copy

	//Have an area for declaring variables
	//Space for many methods
	//Space for additional code
	//Space for writing script name and description, so it can be used as template flawlessly

	private LuaScript script;

	private CodeInputPanel[] codeInputPanels;
	private CodeInputPanel inputDesc, inputLocalVars, inputScriptVars;
	private AdditionalCodePanel additionalCode;
	private RenderedTextBlock pathLabel;
	private TextInput pathInput;
	private IconButton changeScript;

	private Component outsideSp;

	private final Class<?> clazz;
	private final CustomObject customObject;

	public IDEWindow(CustomObject customObject, LuaScript script, Runnable layoutParent) {
		this.customObject = customObject;
		this.clazz = customObject.luaClass.getClass();

		pathLabel = PixelScene.renderTextBlock("Path tzz",9);
		add(pathLabel);

		pathInput = new TextInput(Chrome.get(Chrome.Type.TOAST_WHITE), false, PixelScene.uiCamera.zoom) {
			@Override
			protected void looseFocus() {
				super.looseFocus();
				String text = getText();
				if (text != null && !text.isEmpty() && !text.endsWith(".lua")) {
					setText(text + ".lua");
				}
			}
		};
		pathInput.setTextFieldFilter((textField, c) -> TextInput.FILE_NAME_INPUT.acceptChar(textField, c) || c == '/' || c == '\\');
		add(pathInput);

		changeScript = new IconButton(Icons.CHANGES.get()) {
			@Override
			protected void onClick() {
				List<LuaScript> scripts = CustomDungeonSaves.findScripts(script -> script.type.isAssignableFrom(clazz));

				String[] options = new String[scripts.size()];
				int i = 0;
				for (LuaScript s : scripts) {
					options[i++] = s.pathFromRoot;
				}
				EditorScene.show(new WndOptions(
						Messages.get(IDEWindow.class, "choose_script_title"),
						Messages.get(IDEWindow.class, "choose_script_body", CustomDungeonSaves.getAdditionalFilesDir().file().getAbsolutePath()),
						options
				) {
					{
						tfMessage.setHighlighting(false);
					}

					@Override
					protected Image getIcon(int index) {
						return scripts.get(index).sprite();
					}

					@Override
					protected void onSelect(int index) {
						selectScript(scripts.get(index), true);
						LuaScript s = LuaScript.readFromFileContent(createFullScript(), scripts.get(index).pathFromRoot);
						EditorUtilies.getParentWindow(IDEWindow.this).hide();
						showWindow(customObject, s);
					}
				});
			}
		};
		add(changeScript);

		outsideSp = new OutsideSp();

		List<LuaMethodManager> methods = LuaMethodManager.getAllMethodsInOrder(clazz);

		codeInputPanels = new CodeInputPanel[methods.size() + 4];

		codeInputPanels[0] = inputDesc = new CodeInputPanel() {
			{
				title.text(Messages.get(IDEWindow.class, "desc_title"));
			}

			@Override
			protected String createDescription() {
				return Messages.get(IDEWindow.class, "desc_info");
			}

			@Override
			protected String convertToLuaCode() {
				return "--" + (textInput == null ? "" : textInput.getText().replace('\n', (char) 29));
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
				remover.setVisible(false);
			}

			@Override
			protected void onRemove() {
				super.onRemove();
				adder.setVisible(false);
			}
		};
		add(inputDesc);

		codeInputPanels[1] = inputLocalVars = new VariablesPanel(Messages.get(IDEWindow.class, "vars_title"), "vars") {
			@Override
			protected void layoutParent() {
				layoutParent.run();
			}

			@Override
			protected String createDescription() {
				return Messages.get(IDEWindow.class, "vars_info");
			}
		};
		add(inputLocalVars);

		codeInputPanels[2] = inputScriptVars = new VariablesPanel(Messages.get(IDEWindow.class, "static_title"), "static") {
			@Override
			protected void layoutParent() {
				layoutParent.run();
			}

			@Override
			protected String createDescription() {
				return Messages.get(IDEWindow.class, "static_info");
			}
		};
		add(inputScriptVars);

		codeInputPanels[codeInputPanels.length-1] = additionalCode = new AdditionalCodePanel() {
			@Override
			protected void layoutParent() {
				layoutParent.run();
			}
		};
		add(additionalCode);

		int i = 3;
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

		selectScript(script, true);
		inputDesc.textInput.gainFocus();
	}

	@Override
	protected void layout() {

		float h = 18;
		changeScript.setRect(x + width - h, y, h, h);
		pathLabel.maxWidth((int) ((changeScript.left() - 1)*2/3f));
		float w = (changeScript.left() - x - pathLabel.width() - 3 - 2);
		pathInput.setRect(x + changeScript.left() - 2 - w, y, w, h);

		pathLabel.maxWidth((int) (pathInput.left() - 2));
		pathLabel.setPos(x, y + (h - pathLabel.height()) * 0.5f);

		height = Math.max(h, pathLabel.height()) + 3;
		height = EditorUtilies.layoutCompsLinear(2, this, codeInputPanels);
	}

	public Component getOutsideSp() {
		return outsideSp;
	}

	private String createFullScript() {
		StringBuilder b = new StringBuilder();

		if (script == null) script = new LuaScript(customObject.luaClass.getClass().getSuperclass(), null, customObject.pathToScript);

		script.desc = inputDesc.convertToLuaCode().substring(2);//uncomment, removes --
		b.append('\n');

		StringBuilder functions = new StringBuilder();

		for (int i = 1; i < codeInputPanels.length; i++) {
			String code = codeInputPanels[i].convertToLuaCode();
			if (code != null) {
				functions.append(code);
				functions.append("\n\n");
			}
		}

		String fullFunctions = functions.toString();
		b.append(fullFunctions);

		String cleanedFunctions = LuaScript.cleanLuaCode(fullFunctions);

		b.append("return {\n    vars = vars; static = static; ");
		for (String functionName : LuaScript.allFunctionNames(cleanedFunctions)) {
			b.append(functionName).append(" = ").append(functionName).append("; ");
		}
		b.append("\n}");

		script.code  = b.toString();

		return script.getAsFileContent();
	}

	private void compile() {
		StringBuilder b = new StringBuilder();
		for (CodeInputPanel inputPanel : codeInputPanels) {
			String msg = inputPanel.compile();
			if (msg != null) {
				if (b.length() > 0) b.append("\n\n");
				b.append('_').append(inputPanel.getLabel()).append("_:\n");
				b.append(msg);
			}
		}
		String result = b.toString();
		if (!result.isEmpty()) {
			EditorScene.show(new WndError(result));
		} else {
			RenderedTextBlock title = PixelScene.renderTextBlock(Messages.get(IDEWindow.class, "compile_no_error_title"), 9);
			title.hardlight(Window.TITLE_COLOR);
			EditorScene.show(new WndTitledMessage(title, Messages.get(IDEWindow.class, "compile_no_error_body")));
		}
	}

	private void save() {
		customObject.pathToScript = pathInput.getText();
		if (!customObject.pathToScript.endsWith(".lua")) customObject.pathToScript += ".lua";
		String content = createFullScript();
		try {
			CustomDungeonSaves.writeClearText(CustomDungeonSaves.getExternalFilePath(customObject.pathToScript), content);
			EditorScene.show(new WndMessage(Messages.get(IDEWindow.class, "write_file_successful", customObject.pathToScript)));
		} catch (Exception e) {
			EditorScene.show(new WndError(Messages.get(IDEWindow.class, "write_file_exception", e.getClass().getSimpleName(), e.getMessage())));
		}
	}

	public static void showWindow(CustomObject customObject) {
		showWindow(customObject, CustomDungeonSaves.readLuaFile(customObject.pathToScript));
	}

	public static void showWindow(CustomObject customObject, LuaScript script) {

		SimpleWindow w = new SimpleWindow((int) (PixelScene.uiCamera.width * 0.8f),  (int) (PixelScene.uiCamera.height * 0.9f));

		IDEWindow ideWindow = new IDEWindow(customObject, script, w::layout);

		w.initComponents(null, ideWindow, ideWindow.getOutsideSp(), 0f, 0f, new ScrollPaneWithScrollbar(ideWindow));

		EditorScene.show(w);

	}

	public void selectScript(LuaScript script, boolean force) {//TODO tzz add option to delete scripts!
		this.script = script;
		String cleanedCode;
		LuaScript currentScript;
		if (force) {
			if (script == null) {
				currentScript = new LuaScript(Object.class, "", "");
				currentScript.code = "";
				cleanedCode = "";
			} else {
				currentScript = script;
				cleanedCode = LuaScript.cleanLuaCode(script.code);
				pathInput.setText(currentScript.pathFromRoot);
			}
		} else {
			currentScript = script;
			cleanedCode = LuaScript.cleanLuaCode(script.code);
		}

		List<String> functions = LuaScript.allFunctionNames(cleanedCode);
		for (CodeInputPanel inputPanel : codeInputPanels) {
			inputPanel.applyScript(force, currentScript, cleanedCode);
			if (inputPanel instanceof MethodPanel) functions.remove(((MethodPanel) inputPanel).getMethodName());
		}
		additionalCode.actualApplyScript(force, currentScript, cleanedCode, functions);
		IDEWindow.this.layout();
	}

	private class OutsideSp extends Component {//TODO tzz hover texts
		private RedButton btnCompile, btnSave, insertClassName, insertFullTemplate;
		private StyledButton btnCopy, btnPaste;

		@Override
		protected void createChildren(Object... params) {
			btnCompile = new RedButton("") {
				@Override
				protected void onClick() {
					compile();
				}

				@Override
				protected String hoverText() {
					return Messages.get(IDEWindow.class, "compile");
				}
			};
			btnCompile.icon(Icons.COLORS.get());//tzz
			add(btnCompile);

			btnSave = new RedButton(""){
				@Override
				protected void onClick() {
					save();
				}

				@Override
				protected String hoverText() {
					return Messages.get(IDEWindow.class, "save");
				}
			};
			btnSave.icon(Icons.COLORS.get());//tzz
			add(btnSave);

			insertClassName = new RedButton("InClName") {
				@Override
				protected void onClick() {
					WndEditorInv.chooseClass = true;
					EditorScene.selectItem(new WndBag.ItemSelectorInterface() {
						@Override
						public String textPrompt() {
							return null;
						}

						@Override
						public Class<? extends Bag> preferredBag() {
							return null;
						}

						@Override
						public List<Bag> getBags() {
							return Arrays.asList(Mobs.bag, Items.bag, Traps.bag, Plants.bag, Buffs.bag);
						}

						@Override
						public boolean itemSelectable(Item item) {
							return true;
						}

						@Override
						public void onSelect(Item item) {
							WndEditorInv.chooseClass = false;
							Object obj;
							if (item instanceof EditorItem) obj = ((EditorItem<?>) item).getObject();
							else obj = item;
							if (obj == null) return;
							String clName = obj.getClass().getSimpleName();
							if (clName.equals("Barrier")) clName = obj.getClass().getName();
							TextInput textInput = TextInput.getWithFocus();
							if (textInput != null && textInput != pathInput) textInput.insert("new(\"" + clName + "\")");
						}

						@Override
						public boolean acceptsNull() {
							return false;
						}
					});
				}
			};
			add(insertClassName);

			insertFullTemplate = new RedButton("InsFull tzz") {
				@Override
				protected void onClick() {
					LuaTemplates.show(script -> selectScript(script, false));
				}
			};
			add(insertFullTemplate);

			btnCopy = new RedButton(""){
				@Override
				protected void onPointerDown() {
					super.onPointerDown();
					PointerEvent.clearKeyboardThisPress = false;
				}

				@Override
				protected void onPointerUp() {
					super.onPointerUp();
					PointerEvent.clearKeyboardThisPress = false;
				}

				@Override
				protected void onClick() {
					super.onClick();
					TextInput textInput = TextInput.getWithFocus();
					if (textInput != null) textInput.copyToClipboard();
				}
			};
			btnCopy.icon(Icons.COPY.get());
			add(btnCopy);

			btnPaste = new RedButton(""){
				@Override
				protected void onPointerDown() {
					super.onPointerDown();
					PointerEvent.clearKeyboardThisPress = false;
				}

				@Override
				protected void onPointerUp() {
					super.onPointerUp();
					PointerEvent.clearKeyboardThisPress = false;
				}

				@Override
				protected void onClick() {
					super.onClick();
					TextInput textInput = TextInput.getWithFocus();
					if (textInput != null) textInput.pasteFromClipboard();
				}

			};
			btnPaste.icon(Icons.PASTE.get());
			add(btnPaste);
		}

		@Override
		protected void layout() {
			float h = 18;
			float gap = 1;
			float posX = x + width;
			btnPaste.setRect(posX - gap - btnPaste.icon().width, y + (h - btnPaste.height()) * 0.5f, btnPaste.icon().width, btnPaste.icon().height);
			posX = btnPaste.left();
			btnCopy.setRect(posX - gap - btnCopy.icon().width, y + (h - btnCopy.height()) * 0.5f, btnCopy.icon().width, btnCopy.icon().height);
			posX = btnCopy.left();
			btnCompile.setRect(posX - gap - btnCompile.icon().width, y + (h - btnCompile.height()) * 0.5f, btnCompile.icon().width, btnCompile.icon().height);
			posX = btnCompile.left();
			btnSave.setRect(posX - gap - btnSave.icon().width, y + (h - btnSave.height()) * 0.5f, btnSave.icon().width, btnSave.icon().height);
			posX = btnSave.left();
			float w = (posX - x - gap*6) / 2f;
			insertClassName.setRect(x + gap*2, y, w, h);
			insertFullTemplate.setRect(insertClassName.right() + gap*2, y, w, h);

			PixelScene.align(btnPaste);
			PixelScene.align(btnCopy);
			PixelScene.align(btnSave);
			PixelScene.align(insertClassName);
			PixelScene.align(insertFullTemplate);

			height = h;
		}
	}


}