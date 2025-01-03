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

import android.app.AlertDialog;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.shatteredpixel.shatteredpixeldungeon.android.R;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.LuaManager;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.luaeditor.CodeInputPanel;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.NotAllowedInLua;
import com.watabou.idewindowactions.CodeInputPanelInterface;
import com.watabou.idewindowactions.LuaScript;

@NotAllowedInLua
public abstract class AndroidCodeInputPanel extends ConstraintLayout implements CodeInputPanelInterface {

	protected TextView label, desc;
	protected EditText textInput;
	protected ImageButton btnAdd, btnRemove, btnFold, btnExpand;
	protected View line;

	public AndroidCodeInputPanel(Context context) {
		super(context);
		init(context);
	}

	public AndroidCodeInputPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public AndroidCodeInputPanel(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.code_input_panel, this, true);

		label = findViewById(R.id.label);
		desc = findViewById(R.id.desc);
		textInput = findViewById(R.id.text_input);
		line =findViewById(R.id.line);

		btnAdd = findViewById(R.id.btn_add);
		btnRemove = findViewById(R.id.btn_remove);
		btnFold = findViewById(R.id.btn_fold);
		btnExpand = findViewById(R.id.btn_expand);
		ImageButtonTouchFeedback.attach(btnAdd);
		ImageButtonTouchFeedback.attach(btnRemove);
		ImageButtonTouchFeedback.attach(btnFold);
		ImageButtonTouchFeedback.attach(btnExpand);

		btnAdd.setOnClickListener(v -> onAdd());
		btnRemove.setOnClickListener(v -> onRemove());
		btnFold.setOnClickListener(v -> onFold());
		btnExpand.setOnClickListener(v -> onExpand());

		btnRemove.setVisibility(GONE);
		btnFold.setVisibility(GONE);
		btnExpand.setVisibility(GONE);

		textInput.setVisibility(GONE);
		desc.setVisibility(GONE);

		ViewTreeObserver toolbarObserver = getViewTreeObserver();
		toolbarObserver.addOnGlobalLayoutListener(() -> {
			int reservedWidth = (btnAdd.getVisibility() != GONE ? btnAdd.getWidth() : 0)
					+ (btnRemove.getVisibility() != GONE ? btnRemove.getWidth() : 0)
					+ (btnFold.getVisibility() != GONE ? btnFold.getWidth() : 0)
					+ (btnExpand.getVisibility() != GONE ? btnExpand.getWidth() : 0);
			label.setWidth(line.getWidth() - reservedWidth);
		});
	}

	protected void onAdd() {
		onExpand();
		btnAdd.setVisibility(GONE);
		btnRemove.setVisibility(VISIBLE);

		desc.setVisibility(VISIBLE);
		textInput.setVisibility(VISIBLE);
		textInput.requestFocus();
	}

	protected void onRemove() {

		if (textInput.getText().toString().isEmpty()) {
			btnAdd.   setVisibility(VISIBLE);
			btnRemove.setVisibility(GONE);
			btnFold.  setVisibility(GONE);
			btnExpand.setVisibility(GONE);
			desc.	  setVisibility(GONE);
			textInput.setVisibility(GONE);
			return;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle(Messages.get(CodeInputPanel.class, "remove_title"));
		builder.setMessage(Messages.get(CodeInputPanel.class, "remove_body"));

		builder.setPositiveButton(android.R.string.ok, (dialog, option) -> {
					btnAdd.   setVisibility(VISIBLE);
					btnRemove.setVisibility(GONE);
					btnFold.  setVisibility(GONE);
					btnExpand.setVisibility(GONE);
					desc.	  setVisibility(GONE);
					textInput.setVisibility(GONE);
					textInput.setText("");
				});

		builder.setNegativeButton(android.R.string.cancel, (dialog, option) -> dialog.dismiss());

		builder.create().show();
	}

	protected void onFold() {
		btnFold.setVisibility(GONE);
		btnExpand.setVisibility(VISIBLE);
		desc.setVisibility(GONE);
		textInput.setVisibility(GONE);
		AndroidIDEWindow.hideKeyboard(textInput);
	}

	protected void onExpand() {
		btnFold.setVisibility(VISIBLE);
		btnExpand.setVisibility(GONE);
		desc.setVisibility(VISIBLE);
		textInput.setVisibility(VISIBLE);
		AndroidIDEWindow.showKeyboard(textInput);
	}

	@Override
	public String compile() {
		String code = convertToLuaCode();
		return code == null ? null : LuaManager.compile(code);
	}

	@Override
	public void applyScript(boolean forceChange, LuaScript fullScript, String cleanedCode) {

	}

	@Override
	public void setCode(boolean forceChange, String code) {
		if (code == null) {
			if (forceChange) onRemove();
			return;
		}

		if (btnAdd.getVisibility() == VISIBLE) {
			onAdd();
			textInput.setText(code);
		} else {
			textInput.setText((forceChange || textInput.getText().toString().isEmpty() ? "" : "--[[\n" + textInput.getText() + "]]\n\n") + code);
		}
	}
}