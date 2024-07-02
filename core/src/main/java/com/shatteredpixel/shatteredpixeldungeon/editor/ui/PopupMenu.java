package com.shatteredpixel.shatteredpixeldungeon.editor.ui;

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.SlowExtendWindow;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.watabou.utils.Point;

public class PopupMenu extends SlowExtendWindow {

	public static final float TIME_TO_OPEN_WINDOW = 0.15f;//in seconds


	private StyledButton[] buttons;

	protected void finishInstantiation(StyledButton[] buttons, int posX, int posY, int maxWidth) {
		finishInstantiation(buttons, posX, posY, maxWidth, Orientation.BOTTOM_TO_TOP);
	}

	protected void finishInstantiation(StyledButton[] buttons, int posX, int posY, int maxWidth, Orientation orientation) {
		this.buttons = buttons;

		float pY = 0;

		for (StyledButton btn : buttons) {
			add(btn);
			btn.setSize(maxWidth, 15);
			endWidth = (int) Math.max(endWidth, Math.ceil(btn.reqWidth()));
		}
		if (endWidth > maxWidth) endWidth = maxWidth;
		for (StyledButton btn : buttons) {
			add(btn);
			btn.setRect(0, pY, endWidth, Math.max(15, btn.reqHeight()));
			PixelScene.align(btn);
			pY += 2 + btn.height();
		}
		pY -= 2;

		endHeight = (int) Math.ceil(pY);

		setAttributes(endWidth, endHeight, orientation, new Point(posX - endWidth / 2, posY), TIME_TO_OPEN_WINDOW);
	}
}