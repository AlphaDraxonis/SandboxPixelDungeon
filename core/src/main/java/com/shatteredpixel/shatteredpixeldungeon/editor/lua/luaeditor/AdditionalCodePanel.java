package com.shatteredpixel.shatteredpixeldungeon.editor.lua.luaeditor;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.idewindowactions.LuaScript;

import java.util.List;

public class AdditionalCodePanel extends CodeInputPanel {

	{
		title.text(Messages.get(IDEWindow.class, "additional_code_title"));
	}

	@Override
	protected String createDescription() {
		return Messages.get(IDEWindow.class, "additional_code_info");
	}

	@Override
	public String convertToLuaCode() {
		return textInput == null ? textInputText == null ? "" : textInputText : textInput.getText();
	}

	@Override
	public void applyScript(boolean forceChange, LuaScript fullScript, String cleanedCode) {
		//do nothing, wait for actualApplyScript() instead
	}

	public void actualApplyScript(boolean forceChange, LuaScript fullScript, String cleanedCode, List<String> functions) {
		if (functions.isEmpty()) {
			setCode(forceChange, null);
			return;
		}

		//take all methods that are not used by others
		StringBuilder b = new StringBuilder();
		for (String functionName : functions) {
			b.append(LuaScript.extractRawMethodFromScript(cleanedCode, fullScript.code, functionName));
			b.append("\n\n");
		}
		setCode(forceChange, b.toString());
	}
}