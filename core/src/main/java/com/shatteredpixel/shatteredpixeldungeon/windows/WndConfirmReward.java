package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.editor.util.Consumer;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;

public class WndConfirmReward extends WndInfoItem {

	public WndConfirmReward(Item item, Consumer<Item> onConfirm) {
		super(item);

		RedButton btnConfirm = new RedButton(Messages.get(this, "confirm")) {
			@Override
			protected void onClick() {
				hide();
				onConfirm.accept(item);
			}
		};
		btnConfirm.setRect(0, height + 2, width / 2 - 1, 16);
		add(btnConfirm);

		RedButton btnCancel = new RedButton(Messages.get(this, "cancel")) {
			@Override
			protected void onClick() {
				hide();
			}
		};
		btnCancel.setRect(btnConfirm.right() + 2, height + 2, btnConfirm.width(), 16);
		add(btnCancel);

		resize(width, (int) btnCancel.bottom());
	}
}