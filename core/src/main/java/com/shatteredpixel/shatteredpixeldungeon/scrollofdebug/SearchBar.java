package com.shatteredpixel.shatteredpixeldungeon.scrollofdebug;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.noosa.TextInput;
import com.watabou.noosa.ui.Component;

public class SearchBar extends Component {
	protected RenderedTextBlock label;
	protected TextInput input;

	@Override
	protected void createChildren(Object... params) {

		label = PixelScene.renderTextBlock(Messages.get(this, "label"), 8);
		add(label);

		input = new TextInput(Chrome.get(Chrome.Type.TOAST_WHITE), false, PixelScene.uiCamera.zoom) {
			private String lastText = "";
			@Override
			public void setText(String text) {
				super.setText(text);
				onTextChanged(lastText, text);
				if (!lastText.equals(text)) lastText = text;
			}

			@Override
			protected void layout() {
				super.layout();

				TextInput withFocus = getWithFocus();
				if (withFocus == null || !withFocus.isVisible()) {
					focusToFirstVisible();
				}
			}
		};
		add(input);
	}

	public void gainFocus() {
		input.gainFocus();
	}


	@Override
	protected void layout() {
		height = 18;
		label.setPos(x, y + (height - label.height()) * 0.5f);

		input.setRect(label.right() + 5, y, width - 5 - label.width(), 18);
	}

	protected void onTextChanged(String textBefore, String textAfter) {

	}

}