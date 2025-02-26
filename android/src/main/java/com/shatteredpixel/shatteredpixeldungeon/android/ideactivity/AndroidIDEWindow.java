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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.shatteredpixel.shatteredpixeldungeon.GameObject;
import com.shatteredpixel.shatteredpixeldungeon.android.AndroidLauncher;
import com.shatteredpixel.shatteredpixeldungeon.android.R;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObject;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObjectManager;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.LuaCustomObject;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.ResourcePath;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.DungeonScript;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.luaeditor.IDEWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.luaeditor.LuaMethodManager;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.luaeditor.LuaTemplates;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.luaeditor.NewInstanceButton;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import com.watabou.NotAllowedInLua;
import com.watabou.idewindowactions.CodeInputPanelInterface;
import com.watabou.idewindowactions.LuaScript;
import com.watabou.utils.Consumer;

import java.io.IOException;
import java.util.List;

@NotAllowedInLua
public class AndroidIDEWindow extends Activity {

	private String scriptPath;
	public static String originalScriptPath;
	public static Class<?> clazz;
	public static Consumer<String> onScriptChanged;

	private AndroidCodeInputPanel[] codeInputPanels;
	private AndroidCodeInputPanel inputDesc, inputLocalVars, inputScriptVars;
	private AndroidAdditionalCodePanel additionalCode;
	private EditText pathInput;
	
	private static final String CODE_PANEL = "code_panel_";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		scriptPath = originalScriptPath;

		setContentView(R.layout.android_ide_window);

		Button btnCompile = findViewById(R.id.btn_compile);
		Button btnMore = findViewById(R.id.btn_more);
		ImageButton btnExit = findViewById(R.id.btn_exit);

		ImageButtonTouchFeedback.attach(btnExit);

		btnCompile.setText(Messages.get(IDEWindow.class, "compile"));
		btnMore.setText(Messages.get(IDEWindow.class, "more"));

		btnCompile.setOnClickListener(v -> compile());

		btnMore.setOnClickListener(v -> {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setItems(new String[]{
					Messages.get(NewInstanceButton.class, "label"),
					Messages.get(IDEWindow.class, "insert_full"),
					Messages.get(IDEWindow.class, "view_documentation")
			}, (dialog, which) -> {
				switch (which) {
					case 0:
						doInGameSelection(() -> IDEWindow.chooseClassName((clName, obj) -> {
							goBackAfterInGameSelection(null, null, "insertCode", NewInstanceButton.generateCodeForNewInstance(clName, obj));
							dialog.dismiss();
						}));
						break;
					case 1:
						doInGameSelection(() -> LuaTemplates.show(script -> {
							goBackAfterInGameSelection(null, script, script == null ? null : "force", "false");
							dialog.dismiss();
						}, clazz));
						break;
					case 2:
						CodeInputPanelInterface.viewDocumentation();
						dialog.dismiss();
						break;
				}
			});

			builder.create().show();
		});

		btnExit.setOnClickListener(v -> onBackPressed());


		LinearLayout compGroup = findViewById(R.id.comp_group);

		pathInput = findViewById(R.id.path_input);
		pathInput.setHint("your_path.lua");
		TextView pathLabel = findViewById(R.id.path_label);
		pathLabel.setText(Messages.get(IDEWindow.class, "path"));
		ImageButton btnChange = findViewById(R.id.btn_change);
		ImageButtonTouchFeedback.attach(btnChange);

		ViewTreeObserver toolbarObserver = pathInput.getViewTreeObserver();
		toolbarObserver.addOnGlobalLayoutListener(() -> {
			pathInput.setWidth(((View) pathInput.getParent()).getWidth() - pathLabel.getWidth() - btnChange.getWidth() - dpToPx(this, 20));
		});

		pathInput.setOnFocusChangeListener((v, hasFocus) -> {
			if (!hasFocus && !pathInput.getText().toString().endsWith(".lua")) {
				pathInput.getText().append(".lua");
			}
		});

		btnChange.setOnClickListener(v -> {

			List<LuaScript> scripts = CustomDungeonSaves.findScripts(script -> {
				Class<?> luca = script.type;
				while (!luca.isAssignableFrom(clazz)) {
					luca = luca.getSuperclass();
				}
				return luca != GameObject.class && luca != Object.class;
			});
			if (scripts.isEmpty()) {
				showErrorWindow(Messages.get(IDEWindow.class, "no_scripts_available"));
				return;
			}

			doInGameSelection(() -> IDEWindow.showSelectScriptWindow(clazz, script -> {
				if (script == null) {
					goBackAfterInGameSelection(null, null, null, "true");
				} else {
					goBackAfterInGameSelection(script.getPath(), script, "force", "true");
				}
			}));
		});


		int i = 0;

		List<LuaMethodManager> methods = LuaMethodManager.getAllMethodsInOrder(clazz);
		codeInputPanels = new AndroidCodeInputPanel[methods.size() + 4];

		codeInputPanels[i++] = inputDesc = new AndroidCodeInputPanel(this) {
			{
				label.setText(createSpannableStringWithColorsFromText(getLabel()));
				desc.setVisibility(GONE);

				btnAdd.setVisibility(GONE);
				btnExpand.setVisibility(VISIBLE);
			}
			
			@Override
			protected EditText createEditText() {
				EditText result = super.createEditText();
				result.setHint(createSpannableStringWithColorsFromText((Messages.get(IDEWindow.class, "desc_info"))));
				return result;
			}
			
			@Override
			protected void onAdd() {
				super.onAdd();
				desc.setVisibility(GONE);
			}

			@Override
			protected void onExpand() {
				super.onExpand();
				desc.setVisibility(GONE);
			}

			@Override
			public String getLabel() {
				return Messages.get(IDEWindow.class, "desc_title");
			}

			@Override
			public String convertToLuaCode() {
				return "--" + text.replace('\n', (char) 29);
			}

			@Override
			public void applyScript(boolean forceChange, LuaScript fullScript, String cleanedCode) {
				if (forceChange || text.isEmpty()) {
					setText(fullScript.desc);
				}
			}
		};
		compGroup.addView(inputDesc);
		
		
		if (clazz != DungeonScript.class) {
			codeInputPanels[i++] = inputLocalVars = new AndroidVariablesPanel(this, "vars", Messages.get(IDEWindow.class, "vars_title")) {
				{
					desc.setText(createSpannableStringWithColorsFromText(Messages.get(IDEWindow.class, "vars_info")));
				}
				
				@Override
				protected EditText createEditText() {
					EditText result = super.createEditText();
					result.setHint("aNumber = 5,  item = new(\"PotionOfHealing\")");
					return result;
				}
			};
			compGroup.addView(inputLocalVars);
		} else {
			codeInputPanels[i++] = inputLocalVars = null;
		}
		
		codeInputPanels[i++] = inputScriptVars = new AndroidVariablesPanel(this, "static", Messages.get(IDEWindow.class, (clazz == DungeonScript.class ? "global_vars" : "static") + "_title")) {
			{
				desc.setText(createSpannableStringWithColorsFromText(Messages.get(IDEWindow.class, (clazz == DungeonScript.class ? "global_vars" : "static") + "_info")));
			}
			
			@Override
			protected EditText createEditText() {
				EditText result = super.createEditText();
				result.setHint("aNumber = 5,  item = new(\"PotionOfHealing\")");
				return result;
			}
		};
		compGroup.addView(inputScriptVars);
		
		
		for (LuaMethodManager methodInfo : methods) {
			codeInputPanels[i] = new AndroidMethodPanel(this, methodInfo.method, methodInfo.paramNames);
			compGroup.addView(codeInputPanels[i]);
			//Note: the following was when the EditTexts were always there, just with visibility View.GONE
			//It took me like 3 hours to figure out that only the following statement would also work, but not the second...
			//What is not working? When scrolling, it would be stuck at the focused EditText which couldn't leave the screen bounds
//			compGroup.addView(codeInputPanels[i], new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)); working
//			compGroup.addView(codeInputPanels[i], new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)); not working
			i++;
		}

		codeInputPanels[codeInputPanels.length-1] = additionalCode = new AndroidAdditionalCodePanel(this){{
			label.setText(getLabel());
		}
			@Override
			public String getLabel() {
				return Messages.get(IDEWindow.class, "additional_code_title");
			}};
		compGroup.addView(additionalCode);
		
		View rootView = findViewById(android.R.id.content);
		rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			private boolean isKeyboardVisible;
			@Override
			public void onGlobalLayout() {
				Rect rect = new Rect();
				rootView.getWindowVisibleDisplayFrame(rect);
				int screenHeight = rootView.getRootView().getHeight();
				int keypadHeight = screenHeight - rect.bottom;
				
				boolean isVisible = keypadHeight > screenHeight * 0.15; // Threshold for keyboard visibility
				
				if (isVisible && !isKeyboardVisible) {
					isKeyboardVisible = true;
				} else if (!isVisible && isKeyboardVisible) {
					isKeyboardVisible = false;
					
					View focused = findViewById(R.id.comp_group).findFocus();
					if (focused instanceof EditText) {
						focused.clearFocus();
					}
				}
			}
		});
		
		if (savedInstanceState == null) {
			inputDesc.onExpand();
//			if (inputDesc.textInput != null) {
//				inputDesc.textInput.requestFocus();
//			}
			
			selectScript(scriptPath, scriptPath == null ? null : CustomDungeonSaves.readLuaFile(scriptPath), true);
		}
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		for (int j = 0; j < codeInputPanels.length; j++) {
			if (codeInputPanels[j] != null) {
				codeInputPanels[j].restoreState(savedInstanceState.getBundle(CODE_PANEL + j));
			}
		}
		scriptPath = originalScriptPath;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		for (int j = 0; j < codeInputPanels.length; j++) {
			if (codeInputPanels[j] != null) {
				outState.putBundle(CODE_PANEL + j, codeInputPanels[j].storeState());
			}
		}
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
			showErrorWindow(result);
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(Messages.get(IDEWindow.class, "compile_no_error_title"));
			builder.setMessage(Messages.get(IDEWindow.class, "compile_no_error_body"));

			AlertDialog dialog = builder.create();
			dialog.setCanceledOnTouchOutside(true);
			dialog.setCancelable(true);
			dialog.show();
		}
	}

	@Override
	public void finish() {
		if (isFinishing()) {
			return;
		};
		save(
				scriptPath -> { //on successful
					super.finish();
					onScriptChanged.accept(scriptPath);
					
					originalScriptPath = null;
					clazz = null;
					onScriptChanged = null;
				},
				errorText -> { //on error
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setTitle(Messages.get(IDEWindow.class, "close_unsaved_title"));
					builder.setMessage(Messages.get(IDEWindow.class, "close_unsaved_body", errorText));

					builder.setPositiveButton(Messages.get(IDEWindow.class, "close_unsaved_close_and_lose"), (dialog, option) -> {
						super.finish();
					});

					builder.setNegativeButton(Messages.get(IDEWindow.class, "close_unsaved_cancel"), (dialog, option) -> dialog.dismiss());

					builder.create().show();
				});
		
	}
	
	private void save(Consumer<String> onSuccessfulSave, Consumer<String> onError) {
		String newPath;
		if (pathInput.getText().toString().isEmpty() || pathInput.getText().toString().equals(".lua")) {
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
				
				onSuccessfulSave.accept(saveLocation);
				
			} catch (IOException e) {
				showErrorWindow(Messages.get(IDEWindow.class, "write_file_exception", e.getClass().getSimpleName(), e.getMessage()));
			}
		};
		
		if (newPath != null && scriptPath != null && !newPath.equals(scriptPath)) {
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(Messages.get(IDEWindow.class, "save_move_file_title"));
			builder.setMessage(Messages.get(IDEWindow.class, "save_move_file_body"));
			
			builder.setPositiveButton(Messages.get(IDEWindow.class, "save_move_move"), (dialog, option) -> {
				for (CustomObject obj : CustomObjectManager.allUserContents.values()) {
					if (obj instanceof LuaCustomObject && scriptPath.equals(((LuaCustomObject) obj).getLuaScriptPath())) {
						((LuaCustomObject) obj).setLuaScriptPath(saveLocation);
					}
				}
				FileHandle oldFile = CustomObject.getResourceFile(scriptPath, false);
				if (oldFile != null) {
					oldFile.delete();
				}
				doSave.run();
			});
			
			builder.setNegativeButton(Messages.get(IDEWindow.class, "save_move_new"), (dialog, option) -> {
				doSave.run();
				dialog.dismiss();
			});
			
			builder.create().show();
			
		} else {
			doSave.run();
		}
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
		for (CodeInputPanelInterface inputPanel : codeInputPanels) {
			if (inputPanel == null) continue;
			inputPanel.applyScript(force, currentScript, cleanedCode);
			if (inputPanel instanceof AndroidMethodPanel) functions.remove(((AndroidMethodPanel) inputPanel).getMethodName());
		}
		additionalCode.actualApplyScript(force, currentScript, cleanedCode, functions);
	}

	private void showErrorWindow(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(Messages.get(WndError.class, "title"));
		builder.setMessage(message);

		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);
		dialog.show();
	}

	private static LuaScript selectedScriptFromSelectionDialog;
	private static String selectedScriptPathFromSelectionDialog;

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);

		if (intent == null) return;

		if (intent.hasExtra("insertCode")) {
			String insertCode = intent.getStringExtra("insertCode");
			if (insertCode == null) return;

			View focused = findViewById(R.id.comp_group).findFocus();
			if (focused instanceof EditText) {
				((EditText) focused).getText().insert(((EditText) focused).getSelectionEnd(), insertCode);
			}
		}
		else if (intent.hasExtra("force")) {
			selectScript(selectedScriptPathFromSelectionDialog, selectedScriptFromSelectionDialog, Boolean.parseBoolean(intent.getStringExtra("force")));
			selectedScriptFromSelectionDialog = null;
			selectedScriptPathFromSelectionDialog = null;
		}
	}

	private void doInGameSelection(Runnable whatToDo) {
		Intent backToLibGDX = new Intent(AndroidIDEWindow.this, AndroidLauncher.class);
		backToLibGDX.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(backToLibGDX);

		Gdx.app.postRunnable(whatToDo);
	}

	private void goBackAfterInGameSelection(String scriptPath, LuaScript luaScript, String resultKey, String resultValue) {
		selectedScriptPathFromSelectionDialog = scriptPath;
		selectedScriptFromSelectionDialog = luaScript;
		Intent backToIDEWindow = new Intent(AndroidLauncher.instance, AndroidIDEWindow.class);
		backToIDEWindow.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		backToIDEWindow.putExtra(resultKey, resultValue);
		startActivity(backToIDEWindow);
		//see onNewIntent for text insertion
	}


	static void showKeyboard(View view) {
		InputMethodManager imm = (InputMethodManager) AndroidLauncher.instance.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			view.requestFocus();
			imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
		}
	}

	static void hideKeyboard(EditText view) {
		if (view.hasFocus()) {
			InputMethodManager imm = (InputMethodManager) AndroidLauncher.instance.getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm != null) {
				imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
			}
		}
	}


	static SpannableString createSpannableStringWithColorsFromText(String input) {
		SpannableString spannableString = new SpannableString(input.replace("_", ""));

		int start = -1;
		int offset = 0;

		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i) == '_') {
				if (start == -1) {
					start = i - offset;
				} else {
					spannableString.setSpan( new ForegroundColorSpan(0xFFFFFF44), start, i - offset, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					start = -1;
				}
				offset++;
			}
		}

		return spannableString;

	}

	public static int dpToPx(Context context, float dp) {
		return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics()));
	}

}