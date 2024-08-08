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
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.shatteredpixel.shatteredpixeldungeon.GameObject;
import com.shatteredpixel.shatteredpixeldungeon.android.AndroidLauncher;
import com.shatteredpixel.shatteredpixeldungeon.android.R;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.DungeonScript;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.LuaCodeHolder;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.luaeditor.IDEWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.luaeditor.LuaMethodManager;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.luaeditor.LuaTemplates;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.luaeditor.NewInstanceButton;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import com.watabou.idewindowactions.CodeInputPanelInterface;
import com.watabou.idewindowactions.LuaScript;

import java.util.List;

public class AndroidIDEWindow extends Activity {

	public static LuaCodeHolder luaCodeHolder;
	public static LuaScript script;

	private AndroidCodeInputPanel[] codeInputPanels;
	private AndroidCodeInputPanel inputDesc, inputLocalVars, inputScriptVars;
	private AndroidAdditionalCodePanel additionalCode;
	private EditText pathInput;

	private Class<?> clazz;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.android_ide_window);

		clazz = luaCodeHolder.clazz;

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
							goBackAfterInGameSelection("insertCode", NewInstanceButton.generateCodeForNewInstance(clName, obj));
							dialog.dismiss();
						}));
						break;
					case 1:
						doInGameSelection(() -> LuaTemplates.show(script -> {
							selectedScriptFromSelectionDialog = script;
							goBackAfterInGameSelection(script == null ? null : "force", "false");
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
		pathInput.setHint(".lua");
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
				selectedScriptFromSelectionDialog = script;
				goBackAfterInGameSelection(script == null ? null : "force", "true");
			}));
//			List<LuaScript> scripts = CustomDungeonSaves.findScripts(script -> {
//				Class<?> luca = script.type;
//				while (!luca.isAssignableFrom(clazz)) {
//					luca = luca.getSuperclass();
//				}
//				return luca != GameObject.class && luca != Object.class;
//			});
//
//			String[] options = new String[scripts.size()];
//			int i = 0;
//			for (LuaScript s : scripts) {
//				options[i++] = s.pathFromRoot;
//			}
//
//			if (options.length == 0) {
//				showErrorWindow(Messages.get(IDEWindow.class, "no_scripts_available"));
//				return;
//			}
//
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			builder.setTitle(Messages.get(IDEWindow.class, "choose_script_title"));//tzz bugged!!!
//			builder.setMessage(Messages.get(IDEWindow.class, "choose_script_body", CustomDungeonSaves.getAdditionalFilesDir().file().getAbsolutePath()));
//
//			builder.setItems(options, (dialog, which) -> selectScript(scripts.get(which), true));
//
//			AlertDialog dialog = builder.create();
//			dialog.setCancelable(true);
//			dialog.show();
		});


		int i = 0;

		List<LuaMethodManager> methods = LuaMethodManager.getAllMethodsInOrder(clazz);
		codeInputPanels = new AndroidCodeInputPanel[methods.size() + 4];

		codeInputPanels[i++] = inputDesc = new AndroidCodeInputPanel(this) {
			{
				label.setText(createSpannableStringWithColorsFromText(getLabel()));
				desc.setVisibility(GONE);
				textInput.setHint(createSpannableStringWithColorsFromText((Messages.get(IDEWindow.class, "desc_info"))));

				btnAdd.setVisibility(GONE);
				btnExpand.setVisibility(VISIBLE);
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
				String comment = textInput.getText().toString();
				return "--" + comment.replace('\n', (char) 29);
			}

			@Override
			public void applyScript(boolean forceChange, LuaScript fullScript, String cleanedCode) {
				if (forceChange || textInput.getText().toString().isEmpty()) {
					textInput.setText(fullScript.desc);
				}
			}
		};
		compGroup.addView(inputDesc);

		codeInputPanels[i++] = inputLocalVars = new AndroidVariablesPanel(this, "vars", Messages.get(IDEWindow.class, (clazz == DungeonScript.class ? "global_vars" : "vars") + "_title")) {
			{
				desc.setText(createSpannableStringWithColorsFromText(Messages.get(IDEWindow.class, (clazz == DungeonScript.class ? "global_vars" : "vars") + "_info")));
				textInput.setHint("aNumber = 5,  item = new(\"PotionOfHealing\")");
			}
		};
		compGroup.addView(inputLocalVars);

		if (clazz == DungeonScript.class) {
			codeInputPanels[i++] = inputScriptVars = new AndroidVariablesPanel(this, "static", Messages.get(IDEWindow.class, "static_title")) {
				{
					desc.setText(createSpannableStringWithColorsFromText(Messages.get(IDEWindow.class, "static_info")));
					textInput.setHint("aNumber = 5,  item = new(\"PotionOfHealing\")");
				}
			};
			compGroup.addView(inputScriptVars);
		} else {
			codeInputPanels[i++] = inputScriptVars = null;
		}


		for (LuaMethodManager methodInfo : methods) {
			codeInputPanels[i] = new AndroidMethodPanel(this, methodInfo.method, methodInfo.paramNames);
			compGroup.addView(codeInputPanels[i]);
			//it took me like 3 hours to figure out that only the following statement would also work, but not the second...
			//What is not working? When scrolling, it would be stuck at the focused EditText which couldn't leave the screen bounds
//			compGroup.addView(codeInputPanels[i], new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//			compGroup.addView(codeInputPanels[i], new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
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

		selectScript(script, true);

		inputDesc.onExpand();
		inputDesc.textInput.requestFocus();
	}

	private String createFullScript() {
		StringBuilder b = new StringBuilder();

		if (script == null) script = new LuaScript(luaCodeHolder.clazz, null, luaCodeHolder.pathToScript);

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

		b.append("return {\n    vars = vars; static = static; ");
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
		if (save()) super.finish();
		else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(Messages.get(IDEWindow.class, "close_unsaved_title"));
			builder.setMessage(Messages.get(IDEWindow.class, "close_unsaved_body"));

			builder.setPositiveButton(Messages.get(IDEWindow.class, "close_unsaved_close_and_lose"), (dialog, option) -> {
				finish();
			});

			builder.setNegativeButton(Messages.get(IDEWindow.class, "close_unsaved_cancel"), (dialog, option) -> dialog.dismiss());

			builder.create().show();
		}
	}

	private boolean save() {
		luaCodeHolder.pathToScript = pathInput.getText().toString();
		if (!luaCodeHolder.pathToScript.endsWith(".lua")) luaCodeHolder.pathToScript += ".lua";

		FileHandle saveTo = CustomDungeonSaves.getAdditionalFilesDir().child(luaCodeHolder.pathToScript.replace(' ', '_'));
		if (saveTo.exists()) {
			if (script == null || !script.pathFromRoot.equals(luaCodeHolder.pathToScript)){
				showErrorWindow(Messages.get(IDEWindow.class, "script_in_use_body"));
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
						showErrorWindow(Messages.get(IDEWindow.class, "save_duplicate_name_error", luaCodeHolder.pathToScript));
						return false;
					}
				}
			}
		}

		String content = createFullScript();
		try {
			CustomDungeonSaves.writeClearText(CustomDungeonSaves.getExternalFilePath(luaCodeHolder.pathToScript), content);
			return true;
		} catch (Exception e) {
			showErrorWindow(Messages.get(IDEWindow.class, "write_file_exception", e.getClass().getSimpleName(), e.getMessage()));
			return false;
		}
	}

	public void selectScript(LuaScript script, boolean force) {
		if (script != null) script.type = clazz;
		if (force) this.script = script;
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
			selectScript(selectedScriptFromSelectionDialog, Boolean.parseBoolean(intent.getStringExtra("force")));
			selectedScriptFromSelectionDialog = null;
		}
	}

	private void doInGameSelection(Runnable whatToDo) {
		Intent backToLibGDX = new Intent(AndroidIDEWindow.this, AndroidLauncher.class);
		backToLibGDX.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(backToLibGDX);

		Gdx.app.postRunnable(whatToDo);
	}

	private void goBackAfterInGameSelection(String resultKey, String resultValue) {
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