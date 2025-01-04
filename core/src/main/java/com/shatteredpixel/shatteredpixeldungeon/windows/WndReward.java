package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.SimpleWindow;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.ui.Component;

public class WndReward extends SimpleWindow {

	protected static final int WIDTH	= 120;
	protected static final int BTN_SIZE	= 32;
	protected static final int BTN_GAP	= 5;
	protected static final int GAP		= 2;

	public WndReward() {
		super(WIDTH, 0);
	}

	@Override
	public void initComponents(Component title, Component body, Component outsideSp, float alignment, float titleAlignmentX, ScrollPane sp) {
		super.initComponents(title, body, outsideSp, alignment, titleAlignmentX, sp);
		resize(WIDTH, (int) Math.ceil(Math.min(preferredHeight(), WindowSize.HEIGHT_MEDIUM.get())));
	}

	public void layout() {

		if (body == null || sp == null) return;

		float posY = 0;

		if (title != null) {
			if (title instanceof RenderedTextBlock) ((RenderedTextBlock) title).maxWidth(width);
			title.setRect(Math.max(0, (width - title.width()) * titleAlignment), posY, width, title.height());
			posY = title.bottom() + GAP;
		}

		body.setSize(width, 0);

		float normalSpHeight;
		if (outsideSp != null) {
			outsideSp.setSize(width, 0);
			float outsideSpH = outsideSp.height();
			outsideSp.setPos(0, height - outsideSpH);
			normalSpHeight = height - posY - (outsideSpH == 0 ? 1 : outsideSpH + GAP);
		} else {
			normalSpHeight = height - posY - 1;
		}

		float makeSpSmaller = Math.max(0, (normalSpHeight - body.height()) * contentAlignment);
		sp.setRect(0, posY + makeSpSmaller, width, normalSpHeight - makeSpSmaller);

		sp.scrollToCurrentView();
		sp.givePointerPriority();
	}

	public float preferredHeight() {
		float result;

		if (title instanceof RenderedTextBlock) ((RenderedTextBlock) title).maxWidth(width);
		else title.setSize(width, title.height());

		body.setSize(width, -1);
		result = GAP + title.height() + body.height() + 1;

		if (outsideSp != null) {
			outsideSp.setSize(width, -1);
			float outsideSpH = outsideSp.height();
			if (outsideSpH != 0) {
				result += outsideSpH + GAP - 1;
			}
		}
		return result;
	}

	public abstract class SingleItemRewardsBody extends Component {

		protected static final int MAX_BUTTONS_PER_ROW = 3;

		protected final Item payItem;
		protected final Mob questInitiator;

		protected RenderedTextBlock message;

		protected final Item[] rewards;
		protected final ItemButton[] itemButtons;

		public SingleItemRewardsBody(String msg, Mob questInitiator, Item payItem, Item... rewards) {

			this.payItem = payItem;
			this.questInitiator = questInitiator;

			message = PixelScene.renderTextBlock(msg, 6);
			add(message);

			this.rewards = rewards;
			itemButtons = new ItemButton[rewards.length];
			for (int i = 0; i < itemButtons.length; i++) {
				itemButtons[i] = new ItemButton(){
					@Override
					protected void onClick() {
						if (item() != null) {
							GameScene.show(new WndConfirmReward(item(), SingleItemRewardsBody.this::selectReward));
						} else {
							hide();
						}
					}
				};
				itemButtons[i].item(rewards[i]);
				add( itemButtons[i] );
			}
		}

		@Override
		protected void layout() {
			float posY = y;
			float posX = x;

			message.maxWidth((int) width);

			message.setPos(posX, posY);
			posY = message.top() + message.height() + BTN_GAP;

			for (int i = 0; i < itemButtons.length; i++) {

				if (i % MAX_BUTTONS_PER_ROW == 0) {//in first column
					int buttonsInRow = Math.min(itemButtons.length - i, MAX_BUTTONS_PER_ROW);
					itemButtons[i].setRect( (width - BTN_GAP * (buttonsInRow - 1) - BTN_SIZE * buttonsInRow) * 0.5f, posY, BTN_SIZE, BTN_SIZE );
				} else {
					itemButtons[i].setRect( posX, posY, BTN_SIZE, BTN_SIZE );
				}
				if (i + 1 == itemButtons.length) posY = itemButtons[i].bottom();
				else if (i % MAX_BUTTONS_PER_ROW == MAX_BUTTONS_PER_ROW-1) posY += BTN_GAP + BTN_SIZE;
				posX = itemButtons[i].right() + BTN_GAP;
			}

			height = posY - y;
		}

		protected final void selectReward( Item reward ) {

			if (reward == null) return;

			hide();

			if (payItem != null && Dungeon.hero.belongings.contains(payItem)) {
				for (int i = 0; i < payItem.quantity(); i++) {
					payItem.detachAll(Dungeon.hero.belongings.backpack);
				}
			}

			onSelectReward(reward);

			if (reward.doPickUp( Dungeon.hero )) {
				GLog.i( Messages.capitalize(Messages.get(Dungeon.hero, "you_now_have", reward.name())) );
			} else {
				Dungeon.level.drop( reward, questInitiator == null ? Dungeon.hero.pos : questInitiator.pos ).sprite.drop();
			}

			if (questInitiator != null) {
				makeQuestInitiatorDisappear();
			}
		}

		protected abstract void onSelectReward( Item reward );

		protected void makeQuestInitiatorDisappear() {
			String yell;
			if (questInitiator instanceof Ghost)
				yell = Messages.get(WndSadGhost.class, "farewell", Messages.titleCase(Dungeon.hero.name()));
			else yell = Messages.get(WndWandmaker.class, "farewell", Messages.titleCase(Dungeon.hero.name()));
			questInitiator.yell(yell);
			questInitiator.die(null);
		}

	}
}