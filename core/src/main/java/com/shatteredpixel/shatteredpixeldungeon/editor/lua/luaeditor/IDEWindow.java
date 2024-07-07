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

import com.badlogic.gdx.files.FileHandle;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.GameObject;
import com.shatteredpixel.shatteredpixeldungeon.SandboxPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.WndEditorInv;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.*;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.LuaClass;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.LuaCodeHolder;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.LuaLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.LuaScript;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.PopupMenu;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.SimpleWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.BiConsumer;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.*;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextInput;
import com.watabou.noosa.ui.Component;

import java.util.Arrays;
import java.util.List;

public class IDEWindow extends Component {

	private LuaScript script;

	private CodeInputPanel[] codeInputPanels;
	private CodeInputPanel inputDesc, inputLocalVars, inputScriptVars;
	private AdditionalCodePanel additionalCode;
	private RenderedTextBlock pathLabel;
	private TextInput pathInput;
	private IconButton changeScript;

	private Component outsideSp;

	private final Class<?> clazz;
	private final LuaCodeHolder luaCodeHolder;

	private boolean unsavedChanges = false;

	public IDEWindow(LuaCodeHolder luaCodeHolder, LuaScript script, Runnable layoutParent) {
		this.luaCodeHolder = luaCodeHolder;
		this.clazz = luaCodeHolder.clazz;

		pathLabel = PixelScene.renderTextBlock(Messages.get(IDEWindow.class, "path"),9);
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

			@Override
			public void setText(String text) {
				super.setText(text);
				unsavedChanges = true;
			}
		};
		pathInput.setTextFieldFilter((textField, c) -> TextInput.FILE_NAME_INPUT.acceptChar(textField, c) || c == '/' || c == '\\');
		add(pathInput);

		changeScript = new IconButton(Icons.CHANGES.get()) {
			@Override
			protected void onClick() {
				List<LuaScript> scripts = CustomDungeonSaves.findScripts(script -> {
					Class<?> luca = script.type;
					while (!luca.isAssignableFrom(clazz)) {
						luca = luca.getSuperclass();
					}
					return luca != GameObject.class && luca != Object.class;
				});

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
						unsavedChanges = true;
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
				String comment = textInput == null ? textInputText == null ? "" : textInputText : textInput.getText();
				return "--" + comment.replace('\n', (char) 29);
			}

			@Override
			public void applyScript(boolean forceChange, LuaScript fullScript, String cleanedCode) {
				if (forceChange || textInput == null || textInput.getText().isEmpty()) {
					if (textInput == null) onAddClick();
					textInput.setText(fullScript.desc);
				}
			}

			@Override
			protected void onTextChange() {
				unsavedChanges = true;
			}

			@Override
			protected void layoutParent() {
				layoutParent.run();
			}

			@Override
			public void expand() {
				onAdd(null, true);
				remover.setVisible(false);

				layoutParent();
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
				remover.setVisible(false);

				fold.setVisible(false);
				expand.setVisible(true);

				layoutParent();
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

			@Override
			protected void onTextChange() {
				unsavedChanges = true;
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

			@Override
			protected void onTextChange() {
				unsavedChanges = true;
			}
		};
		add(inputScriptVars);

		codeInputPanels[codeInputPanels.length-1] = additionalCode = new AdditionalCodePanel() {
			@Override
			protected void layoutParent() {
				layoutParent.run();
			}

			@Override
			protected void onTextChange() {
				unsavedChanges = true;
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

				@Override
				protected void onTextChange() {
					unsavedChanges = true;
				}
			};
			add(codeInputPanels[i]);
			i++;
		}

		selectScript(script, true);
		inputDesc.textInput.gainFocus();

		unsavedChanges = false;
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

		if (script == null) script = new LuaScript(luaCodeHolder.clazz, null, luaCodeHolder.pathToScript);

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

	private boolean save() {
		luaCodeHolder.pathToScript = pathInput.getText();
		if (!luaCodeHolder.pathToScript.endsWith(".lua")) luaCodeHolder.pathToScript += ".lua";

		FileHandle saveTo = CustomDungeonSaves.getAdditionalFilesDir().child(luaCodeHolder.pathToScript.replace(' ', '_'));
		if (saveTo.exists()) {
			if (script == null || !script.pathFromRoot.equals(luaCodeHolder.pathToScript)){
				EditorScene.show(new WndError(Messages.get(IDEWindow.class, "script_in_use_body")));
				return false;
			}
		}

		if (script != null) {
			for (LuaScript ls : CustomDungeonSaves.findScripts(null)) {
				if (luaCodeHolder.pathToScript.equals(ls.pathFromRoot)) {
					Class<?> luca = script.type;
					while (!luca.isAssignableFrom(clazz)) {
						luca = script.type.getSuperclass();
					}
					if (luca == GameObject.class || luca == Object.class) {
						EditorScene.show(new WndError(Messages.get(IDEWindow.class, "save_duplicate_name_error", luaCodeHolder.pathToScript)) {{
							setHighligtingEnabled(false);
						}});
						return false;
					}
				}
			}
		}

		String content = createFullScript();
		try {
			CustomDungeonSaves.writeClearText(CustomDungeonSaves.getExternalFilePath(luaCodeHolder.pathToScript), content);
			unsavedChanges = false;
			return true;
		} catch (Exception e) {
			EditorScene.show(new WndError(Messages.get(IDEWindow.class, "write_file_exception", e.getClass().getSimpleName(), e.getMessage())) {{setHighligtingEnabled(false);}});
			return false;
		}
	}

	public static SimpleWindow showWindow(LuaCodeHolder luaCodeHolder) {
		return showWindow(luaCodeHolder, CustomDungeonSaves.readLuaFile(luaCodeHolder.pathToScript));
	}

	public static SimpleWindow showWindow(LuaCodeHolder luaCodeHolder, LuaScript script) {

		SimpleWindow w = new SimpleWindow((int) (PixelScene.uiCamera.width * 0.8f),  (int) (PixelScene.uiCamera.height * 0.9f)) {
			IDEWindow ideWindow = new IDEWindow(luaCodeHolder, script, this::layout);
			{
				initComponents(null, ideWindow, ideWindow.getOutsideSp(), 0f, 0f, new ScrollPaneWithScrollbar(ideWindow));
			}
			@Override
			public void hide() {
				if (ideWindow.unsavedChanges || true) {
					if (!ideWindow.save()) {
						Runnable superHide = super::hide;
						GameScene.show(new WndOptions(Icons.WARNING.get(),
								Messages.get(IDEWindow.class, "close_unsaved_title"),
								Messages.get(IDEWindow.class, "close_unsaved_body"),
								Messages.get(IDEWindow.class, "close_unsaved_close_and_lose"),
								Messages.get(IDEWindow.class, "close_unsaved_cancel")) {
							@Override
							protected void onSelect(int index) {
								if (index == 0) {
									superHide.run();
								}
							}
						});
						return;
					}
				}
				super.hide();
			}
		};

		EditorScene.show(w);

		return w;
	}

	public void selectScript(LuaScript script, boolean force) {
		if (script != null) script.type = clazz;
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

	private class OutsideSp extends Component {
		private RedButton btnCompile, btnSave, btnOpenMenu;
		private StyledButton btnCopy, btnPaste;

		@Override
		protected void createChildren(Object... params) {
			btnCompile = new RedButton(Messages.get(IDEWindow.class, "compile")) {
				@Override
				protected void onClick() {
					compile();
				}

				@Override
				protected String hoverText() {
					return Messages.get(IDEWindow.class, "compile");
				}
			};
			add(btnCompile);

//			btnSave = new RedButton(Messages.get(IDEWindow.class, "save")){
//				@Override
//				protected void onClick() {
//					save();
//				}
//
//				@Override
//				protected String hoverText() {
//					return Messages.get(IDEWindow.class, "save");
//				}
//			};
//			add(btnSave);

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

			btnOpenMenu = new RedButton("") {

				@Override
				protected void onClick() {
					DungeonScene.show(new OutsideSpMenuPopup(
							(int) ((x + btnOpenMenu.width() + 2 - camera().width / 2f)),
					(int) (y - camera().height / 2f) - 3) {

					});
				}
			};
			btnOpenMenu.icon(Icons.MENU.get());
			add(btnOpenMenu);
		}

		@Override
		protected void layout() {
			float h = 18;
			float gap = 1;
			float posX = x + width;

			btnOpenMenu.setRect(posX - gap - h, y, h, h);
			posX = btnOpenMenu.left();

			btnPaste.setRect(posX - gap - h, y, h, h);
			posX = btnPaste.left();

			btnCopy.setRect(posX - gap - h, y, h, h);
			posX = btnCopy.left();

			float w = (posX - x - gap*4) / 1f;
//			btnSave.setRect(x + gap*2, y, w, h);
			btnCompile.setRect(x + gap*2, y, w, h);

			PixelScene.align(btnOpenMenu);
			PixelScene.align(btnPaste);
			PixelScene.align(btnCopy);
//			PixelScene.align(btnSave);
			PixelScene.align(btnCompile);

			height = h;
		}
	}

	private class OutsideSpMenuPopup extends PopupMenu {

		public OutsideSpMenuPopup(int posX, int posY) {

			finishInstantiation(new RedButton[] {
					new NewInstanceButton(this) {
						@Override
						protected void onSelect(String insertText) {
							TextInput textInput = TextInput.getWithFocus();
							if (textInput != null && textInput != pathInput) textInput.insert(insertText);
						}
					},
					new RedButton(Messages.get(IDEWindow.class, "insert_full")) {
						@Override
						protected void onClick() {
							LuaTemplates.show(script -> {
								selectScript(script, false);
								OutsideSpMenuPopup.this.hideImmediately();
							}, clazz);
						}
					},
					new RedButton(Messages.get(IDEWindow.class, "view_documentation")) {
						@Override
						protected void onClick() {
							SandboxPixelDungeon.platform.openURI("https://docs.google.com/document/d/1uXWzyO0wXJ6jDfKB3wrzVcFY4WvaCsX7oLtc5EkxDi0/?usp=sharing");
						}
					},
//					new RedButton(Messages.get(IDEWindow.class, "insert_line")) {
//						@Override
//						protected void onClick() {
//							//TODO way to add class("xxx");
//							//and some other examples...
//						}
//					},

			}, posX, posY, 200);
		}
	}

	public static void chooseClassName(BiConsumer<String, Object> whatToDo) {
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
				Class<?> clazz = obj.getClass();
				while (LuaClass.class.isAssignableFrom(clazz) || LuaLevel.class.isAssignableFrom(clazz)) clazz = clazz.getSuperclass();
				String clName = clazz.getSimpleName();
				if (clName.equals("Barrier")) clName = clazz.getName();
				whatToDo.accept(clName, obj);
			}

			@Override
			public boolean acceptsNull() {
				return false;
			}
		});
	}


}