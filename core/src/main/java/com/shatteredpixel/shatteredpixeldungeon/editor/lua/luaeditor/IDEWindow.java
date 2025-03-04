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
import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObject;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObjectManager;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.LuaCustomObject;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.LuaManager;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.ResourcePath;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.interfaces.CustomObjectClass;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.WndEditorInv;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Buffs;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Items;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Mobs;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Plants;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Traps;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.DungeonScript;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.PopupMenu;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.SimpleWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPaneWithScrollbar;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptionsCondensed;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.NotAllowedInLua;
import com.watabou.idewindowactions.CodeInputPanelInterface;
import com.watabou.idewindowactions.LuaScript;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextInput;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.BiConsumer;
import com.watabou.utils.Consumer;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@NotAllowedInLua
public class IDEWindow extends Component {

	private String scriptPath;//initial value

	private CodeInputPanel[] codeInputPanels;
	private CodeInputPanel inputDesc, inputLocalVars, inputScriptVars;
	private AdditionalCodePanel additionalCode;
	private RenderedTextBlock pathLabel;
	private TextInput pathInput;
	private IconButton changeScript;

	private Component outsideSp;

	private final Class<?> clazz;

	private boolean unsavedChanges = false;

	public IDEWindow(String scriptPath, Class<?> clazz, Runnable layoutParent) {
		this.scriptPath = scriptPath;
		this.clazz = clazz;

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
				showSelectScriptWindow(clazz, script -> {
					if (script != null) {
						selectScript(script.getPath(), script, true);
						unsavedChanges = true;
					}
				});
			}
		};
		add(changeScript);

		outsideSp = new OutsideSp();

		int i = 0;

		List<LuaMethodManager> methods = LuaMethodManager.getAllMethodsInOrder(clazz);
		codeInputPanels = new CodeInputPanel[methods.size() + 4];

		codeInputPanels[i++] = inputDesc = new CodeInputPanel() {
			{
				title.text(Messages.get(IDEWindow.class, "desc_title"));
			}

			@Override
			protected String createDescription() {
				return Messages.get(IDEWindow.class, "desc_info");
			}

			@Override
			public String convertToLuaCode() {
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

				expanded = false;

				layoutParent();
			}
		};
		add(inputDesc);
		
		if (clazz != DungeonScript.class) {
			codeInputPanels[i++] = inputLocalVars = new VariablesPanel(Messages.get(IDEWindow.class, "vars_title"), "vars") {
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
		} else {
			codeInputPanels[i++] = inputLocalVars = null;
		}
		
		codeInputPanels[i++] = inputScriptVars = new VariablesPanel(Messages.get(IDEWindow.class, (clazz == DungeonScript.class ? "global_vars" : "static") + "_title"), "static") {
			@Override
			protected void layoutParent() {
				layoutParent.run();
			}

			@Override
			protected String createDescription() {
				return Messages.get(IDEWindow.class, (clazz == DungeonScript.class ? "global_vars" : "static") + "_info");
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

		selectScript(scriptPath, scriptPath == null ? null : CustomObject.loadScriptFromFile(scriptPath), true);
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
		height = EditorUtilities.layoutCompsLinear(2, this, codeInputPanels);
	}

	public Component getOutsideSp() {
		return outsideSp;
	}

	private String createFullScript() {
		StringBuilder b = new StringBuilder();

		LuaScript script = new LuaScript(clazz, null);

		script.desc = inputDesc.convertToLuaCode().substring(2);//uncomment, removes --
		b.append('\n');

		StringBuilder functions = new StringBuilder();

		for (int i = 1; i < codeInputPanels.length; i++) {
			if (codeInputPanels[i] == null) continue;
			String code = codeInputPanels[i].convertToLuaCode();
			if (code != null) {
				functions.append(code);
				functions.append("\n\n");
			}
		}

		String fullFunctions = functions.toString();
		b.append(fullFunctions);

		String cleanedFunctions = LuaScript.cleanLuaCode(fullFunctions);

		b.append(LuaScript.SCRIPT_RETURN_START);
		for (String functionName : LuaScript.allFunctionNames(cleanedFunctions)) {
			b.append(functionName).append(" = ").append(functionName).append("; ");
		}
		b.append("\n}");

		script.code  = b.toString();

		return script.getAsFileContent();
	}

	private void compile() {
		String result = CodeInputPanelInterface.compileResult(codeInputPanels);
		if (result != null) {
			EditorScene.show(new WndTitledMessage(Icons.WARNING.get(), Messages.get(IDEWindow.class, "compiling_error"), result) {{setHighlightingEnabled(false);}});
		} else {
			RenderedTextBlock title = PixelScene.renderTextBlock(Messages.get(IDEWindow.class, "compile_no_error_title"), 9);
			title.hardlight(Window.TITLE_COLOR);
			EditorScene.show(new WndTitledMessage(title, Messages.get(IDEWindow.class, "compile_no_error_body")) {{setHighlightingEnabled(false);}});
		}
	}

	private void save(Consumer<String> onSuccessfulSave, Consumer<String> onError) {
		
		String compileResult = CodeInputPanelInterface.compileResult(codeInputPanels);
		if (compileResult != null) {
			EditorScene.show(new WndTitledMessage(Icons.WARNING.get(), Messages.get(IDEWindow.class, "compiling_error"), compileResult) {{setHighlightingEnabled(false);}});
			return;
		}
		
		String newPath;
		if (pathInput.getText().isEmpty() || pathInput.getText().equals(".lua")) {
			onError.accept(Messages.get(IDEWindow.class, "save_invalid_name_error"));
			return;
		}
		
		//script could be stored where the original customObject is saved
		String pathPrefix;
//			pathPrefix = customObject.saveDirPath.substring(0, customObject.saveDirPath.length() - CustomDungeonSaves.fileName(customObject).length())
//					+ "scripts/";
		pathPrefix = "";
		
		
		newPath = pathPrefix + pathInput.getText();
		
		//make sure the path has the correct extension
		if (!newPath.endsWith(".lua")) newPath += ".lua";
		
		newPath = ResourcePath.removeSpacesInPath(newPath);
		
		if (scriptPath == null && newPath == null) {
			onError.accept(Messages.get(IDEWindow.class, "save_invalid_name_error"));
			return;
		}
		
		FileHandle saveTo = CustomObject.getResourceFile(newPath, false);
		if (scriptPath == null) {
			if (saveTo.exists()) {
				onError.accept(Messages.get(IDEWindow.class, "save_duplicate_name_error", newPath));
				return;
			}
		}
		
		final String saveLocation = newPath;
		
		Runnable doSave = () -> {
			try {
				CustomDungeonSaves.writeClearText(saveTo, createFullScript());
				unsavedChanges = false;
				
				onSuccessfulSave.accept(saveLocation);
				
			} catch (IOException e) {
				EditorScene.show(new WndError(Messages.get(IDEWindow.class, "write_file_exception", e.getClass().getSimpleName(), e.getMessage())) {{
					setHighlightingEnabled(false);}});
			}
		};

		if (newPath != null && scriptPath != null && !newPath.equals(scriptPath)) {
			DungeonScene.show(new WndOptionsCondensed(Messages.get(IDEWindow.class, "save_move_file_title"), Messages.get(IDEWindow.class, "save_move_file_body"),
					Messages.get(IDEWindow.class, "save_move_move"), Messages.get(IDEWindow.class, "save_move_new")) {
				{
					tfMessage.setHighlighting(false);
				}
				
				@Override
				protected void onSelect(int index) {
					if (index == 0) {
						for (CustomObject obj : CustomObjectManager.allUserContents.values()) {
							if (obj instanceof LuaCustomObject && scriptPath.equals(((LuaCustomObject) obj).getLuaScriptPath())) {
								((LuaCustomObject) obj).setLuaScriptPath(saveLocation);
							}
						}
						FileHandle oldFile = CustomObject.getResourceFile(scriptPath, false);
						if (oldFile != null) {
							oldFile.delete();
						}
					}
					doSave.run();
				}
			});
		} else {
			doSave.run();
		}
	}

	public static SimpleWindow showWindow(String luaScriptPath, Consumer<String> newPathAcceptor, Class<?> clazz) {
		
		if (luaScriptPath != null && luaScriptPath.isEmpty()) {
			luaScriptPath = null;
		}
		
		Consumer<String> onScriptChanged = newScriptPath -> {
			for (CustomObject obj : CustomObjectManager.allUserContents.values()) {
				if (obj instanceof LuaCustomObject && newScriptPath.equals(((LuaCustomObject) obj).getLuaScriptPath())) {
					((LuaCustomObject) obj).setLuaScriptPath(newScriptPath);
					CustomObjectManager.loadScript(obj);
					obj.reloadSprite();
					
					try {
						CustomDungeonSaves.storeCustomObject(obj);
					} catch (IOException e) {
						Game.reportException(e);
					}
				}
			}
			if (newPathAcceptor != null) {
				newPathAcceptor.accept(newScriptPath);
			}
		};
		
		if (Game.platform.openNativeIDEWindow(luaScriptPath, clazz, onScriptChanged)) {
			return null;
		}
		
		String finalLuaScriptPath = luaScriptPath;
		SimpleWindow w = new SimpleWindow(Window.WindowSize.WIDTH_LARGE.get(),  Window.WindowSize.HEIGHT_LARGE.get()) {
			IDEWindow ideWindow = new IDEWindow(finalLuaScriptPath, clazz, this::layout);
			{
				initComponents(null, ideWindow, ideWindow.getOutsideSp(), 0f, 0f, new ScrollPaneWithScrollbar(ideWindow));
			}
			@Override
			public void hide() {
				if (ideWindow.unsavedChanges || true) {
					Runnable superHide = super::hide;
					ideWindow.save(
							newScriptPath -> { //on successful
								super.hide();
								onScriptChanged.accept(newScriptPath);
							},
							errorText -> { //on error
								GameScene.show(new WndOptions(Icons.WARNING.get(),
										Messages.get(IDEWindow.class, "close_unsaved_title"),
										Messages.get(IDEWindow.class, "close_unsaved_body", errorText),
										Messages.get(IDEWindow.class, "close_unsaved_close_and_lose"),
										Messages.get(IDEWindow.class, "close_unsaved_cancel")) {
									@Override
									protected void onSelect(int index) {
										if (index == 0) {
											superHide.run();
										}
									}
								});
							});
				}
			}
		};

		DungeonScene.show(w);

		return w;
	}

	public void selectScript(String scriptPath, LuaScript script, boolean force) {
		if (force) this.scriptPath = scriptPath;
		String cleanedCode;
		LuaScript currentScript;
		if (force) {
			if (script == null) {
				currentScript = new LuaScript(Object.class, "");
				currentScript.code = "";
				cleanedCode = "";
			} else {
				currentScript = script;
				cleanedCode = LuaScript.cleanLuaCode(script.code);
				pathInput.setText(scriptPath);
			}
		} else {
			currentScript = script;
			cleanedCode = LuaScript.cleanLuaCode(script == null ? "" : script.code);
		}

		List<String> functions = LuaScript.allFunctionNames(cleanedCode);
		for (CodeInputPanel inputPanel : codeInputPanels) {
			if (inputPanel == null) continue;
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
		protected void createChildren() {
			btnCompile = new RedButton(Messages.get(IDEWindow.class, "compile"), PixelScene.landscape() ? 8 : 6) {
				@Override
				protected void onClick() {
					compile();
				}

				@Override
				protected String hoverText() {
					return Messages.get(IDEWindow.class, "compile");
				}
			};
			btnCompile.multiline = true;
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

				@Override
				protected String hoverText() {
					return Messages.get(IDEWindow.class, "more");
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
								if (script != null) {
									selectScript(null, script, false);
									OutsideSpMenuPopup.this.hideImmediately();
								}
							}, clazz);
						}
					},
					new RedButton(Messages.get(IDEWindow.class, "view_documentation")) {
						@Override
						protected void onClick() {
							CodeInputPanelInterface.viewDocumentation();
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
				return Arrays.asList(Mobs.bag(), Items.bag(), Traps.bag(), Plants.bag(), Buffs.bag());
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
				if (obj == null) {
					whatToDo.accept(null, null);
					return;
				}
				Class<?> clazz = obj.getClass();
				while (CustomObjectClass.class.isAssignableFrom(clazz)) clazz = clazz.getSuperclass();
				String clName = clazz.getSimpleName();
				if (clName.equals("Barrier")) clName = clazz.getName();
				if (obj instanceof Plant.Seed) clName = obj.getClass().getEnclosingClass().getSimpleName() + "$" + clName;
				whatToDo.accept(clName, obj);
			}

			@Override
			public boolean acceptsNull() {
				return false;
			}
		});
	}

	public static void showSelectScriptWindow(Class<?> clazz, Consumer<LuaScript> onSelect) {
		List<LuaScript> scripts = CustomDungeonSaves.findScripts(script -> {
			Class<?> lca = script.type;
			while (!lca.isAssignableFrom(clazz)) {
				lca = lca.getSuperclass();
			}
			return lca != GameObject.class && lca != Object.class;
		});

		String[] options = new String[scripts.size()];
		String[] descs = new String[options.length];
		int i = 0;
		for (LuaScript s : scripts) {
			options[i] = s.toString();
			descs[i++] = s.desc;
		}

		if (options.length == 0) {
			EditorScene.show(new WndError(Messages.get(IDEWindow.class, "no_scripts_available")));
			return;
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
				return LuaManager.scriptSprite(scripts.get(index));
			}

			@Override
			protected boolean hasInfo(int index) {
				return descs[index] != null && !descs[index].isEmpty();
			}

			@Override
			protected void onInfo(int index) {
				EditorScene.show(new WndTitledMessage(
						Icons.get(Icons.INFO),
						Messages.titleCase(options[index]),
						descs[index]));
			}

			@Override
			protected void onSelect(int index) {
				onSelect.accept(scripts.get(index));
			}
			
			@Override
			public void onBackPressed() {
				super.onBackPressed();
				onSelect.accept(null);
			}
		});
	}


}
