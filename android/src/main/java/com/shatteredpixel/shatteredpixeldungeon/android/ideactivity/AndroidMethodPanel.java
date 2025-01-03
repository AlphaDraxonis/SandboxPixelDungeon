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

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.luaeditor.MethodPanel;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.NotAllowedInLua;
import com.watabou.idewindowactions.CodeInputPanelInterface;
import com.watabou.idewindowactions.LuaScript;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

@NotAllowedInLua
public class AndroidMethodPanel extends AndroidCodeInputPanel {

	private Method method;
	private String[] paramNames;

	public AndroidMethodPanel(Context context) {
		super(context);
	}

	public AndroidMethodPanel(Context context, Method method, String[] paramNames) {
		super(context);
		setMethod(method, paramNames);
	}

	public AndroidMethodPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AndroidMethodPanel(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	void setMethod(Method method, String[] paramNames) {
		this.method = method;
		this.paramNames = paramNames;

		desc.setText(AndroidIDEWindow.createSpannableStringWithColorsFromText(Messages.get(MethodPanel.class, "method_" + method.getName())));

		String modifiers = Modifier.toString(method.getModifiers());
		if (!modifiers.isEmpty()) modifiers += " ";

		modifiers += method.getReturnType().getSimpleName() + " ";

		String methodName = method.getName();
		int startIndexMethodName = modifiers.length();
		int endIndexMethodName = startIndexMethodName + methodName.length();

		StringBuilder b = new StringBuilder();
		b.append(modifiers);
		b.append(methodName);
		b.append(" (");
		Class<?>[] paramTypes = method.getParameterTypes();
		for (int i = 0; i < paramTypes.length; i++) {
			b.append(paramTypes[i].getSimpleName()).append(' ');
			b.append(paramNames[i]);
			if (i < paramTypes.length - 1)
				b.append(", ");
		}
		b.append(')');
		SpannableString spannableString = new SpannableString(b.toString());
		ForegroundColorSpan yellow = new ForegroundColorSpan(0xFFFFFF44);
		spannableString.setSpan(yellow, startIndexMethodName, endIndexMethodName, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		label.setText(spannableString);
	}

	@Override
	protected void onAdd() {
		super.onAdd();
		textInput.setText(CodeInputPanelInterface.defaultMethodCode(method, paramNames));
		textInput.setSelection(textInput.getText().length());
		AndroidIDEWindow.showKeyboard(textInput);
	}

	@Override
	protected void onExpand() {
		super.onExpand();
		textInput.setSelection(textInput.getText().length());
	}

	@Override
	protected void onRemove() {

		if (textInput.getText().toString().equals(CodeInputPanelInterface.defaultMethodCode(method, paramNames)))
			textInput.setText("");

		super.onRemove();
	}

	@Override
	public String convertToLuaCode() {
		String code = textInput.getText().toString();
		return CodeInputPanelInterface.methodPanelToLuaCode(method, code.isEmpty() ? null : code, paramNames);
	}

	@Override
	public String getLabel() {
		return CodeInputPanelInterface.methodPanelGetLabel(method, paramNames);
	}

	@Override
	public void applyScript(boolean forceChange, LuaScript fullScript, String cleanedCode) {
		setCode(forceChange, LuaScript.extractMethodFromScript(cleanedCode, fullScript.code, method.getName()));
	}

	public String getMethodName() {
		return method.getName();
	}
}