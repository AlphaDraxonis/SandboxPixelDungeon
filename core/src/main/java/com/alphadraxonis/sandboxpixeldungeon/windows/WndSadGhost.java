/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2023 Evan Debenham
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

package com.alphadraxonis.sandboxpixeldungeon.windows;

import com.alphadraxonis.sandboxpixeldungeon.Assets;
import com.alphadraxonis.sandboxpixeldungeon.Chrome;
import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.npcs.Ghost;
import com.alphadraxonis.sandboxpixeldungeon.editor.other.GhostQuest;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.items.armor.Armor;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.Weapon;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.GameScene;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.sprites.FetidRatSprite;
import com.alphadraxonis.sandboxpixeldungeon.sprites.GnollTricksterSprite;
import com.alphadraxonis.sandboxpixeldungeon.sprites.GreatCrabSprite;
import com.alphadraxonis.sandboxpixeldungeon.ui.ItemSlot;
import com.alphadraxonis.sandboxpixeldungeon.ui.RedButton;
import com.alphadraxonis.sandboxpixeldungeon.ui.RenderedTextBlock;
import com.alphadraxonis.sandboxpixeldungeon.ui.Window;
import com.alphadraxonis.sandboxpixeldungeon.utils.GLog;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Component;

public class WndSadGhost extends Window {

	private static final int WIDTH		= 120;
	private static final int BTN_SIZE	= 32;
	private static final int BTN_GAP	= 5;
	private static final int GAP		= 2;

	Ghost ghost;
	private GhostQuest quest;
	
	public WndSadGhost( final Ghost ghost, final int type ) {
		
		super();

		this.ghost = ghost;
		quest = ghost.quest;
		
		IconTitle titlebar = new IconTitle();
		RenderedTextBlock message;
		switch (type){
			case 0:default:
				titlebar.icon( new FetidRatSprite() );
				titlebar.label( Messages.get(this, "rat_title") );
				message = PixelScene.renderTextBlock( Messages.get(this, "rat")+"\n\n"+Messages.get(this, "give_item"), 6 );
				break;
			case 1:
				titlebar.icon( new GnollTricksterSprite() );
				titlebar.label( Messages.get(this, "gnoll_title") );
				message = PixelScene.renderTextBlock( Messages.get(this, "gnoll")+"\n\n"+Messages.get(this, "give_item"), 6 );
				break;
			case 2:
				titlebar.icon( new GreatCrabSprite());
				titlebar.label( Messages.get(this, "crab_title") );
				message = PixelScene.renderTextBlock( Messages.get(this, "crab")+"\n\n"+Messages.get(this, "give_item"), 6 );
				break;

		}

		titlebar.setRect( 0, 0, WIDTH, 0 );
		add( titlebar );

		message.maxWidth(WIDTH);
		message.setPos(0, titlebar.bottom() + GAP);
		add( message );

		RewardButton btnWeapon = new RewardButton( quest.weapon );
		btnWeapon.setRect( (WIDTH - BTN_GAP) / 2 - BTN_SIZE, message.top() + message.height() + BTN_GAP, BTN_SIZE, BTN_SIZE );
		add( btnWeapon );

		RewardButton btnArmor = new RewardButton( quest.armor );
		btnArmor.setRect( btnWeapon.right() + BTN_GAP, btnWeapon.top(), BTN_SIZE, BTN_SIZE );
		add(btnArmor);

		resize(WIDTH, (int) btnArmor.bottom() + BTN_GAP);
	}
	
	private void selectReward( Item reward ) {
		
		hide();
		
		if (reward == null) return;

		if (reward instanceof Weapon && quest.enchant != null){
			((Weapon) reward).enchant(quest.enchant);
		} else if (reward instanceof Armor && quest.glyph != null){
			((Armor) reward).inscribe(quest.glyph);
		}
		
		reward.identify(false);
		if (reward.doPickUp( Dungeon.hero )) {
			GLog.i( Messages.capitalize(Messages.get(Dungeon.hero, "you_now_have", reward.name())) );
		} else {
			Dungeon.level.drop( reward, ghost.pos ).sprite.drop();
		}
		
		ghost.yell( Messages.get(this, "farewell") );
		ghost.die( null );
		
		ghost.quest.complete();
	}

	private class RewardButton extends Component {

		protected NinePatch bg;
		protected ItemSlot slot;

		public RewardButton( Item item ){
			bg = Chrome.get( Chrome.Type.RED_BUTTON);
			add( bg );

			slot = new ItemSlot( item ){
				@Override
				protected void onPointerDown() {
					bg.brightness( 1.2f );
					Sample.INSTANCE.play( Assets.Sounds.CLICK );
				}
				@Override
				protected void onPointerUp() {
					bg.resetColor();
				}
				@Override
				protected void onClick() {
					GameScene.show(new RewardWindow(item));
				}
			};
			add(slot);
		}

		@Override
		protected void layout() {
			super.layout();

			bg.x = x;
			bg.y = y;
			bg.size( width, height );

			slot.setRect( x + 2, y + 2, width - 4, height - 4 );
		}
	}

	private class RewardWindow extends WndInfoItem {

		public RewardWindow( Item item ) {
			super(item);

			RedButton btnConfirm = new RedButton(Messages.get(WndSadGhost.class, "confirm")){
				@Override
				protected void onClick() {
					RewardWindow.this.hide();

					WndSadGhost.this.selectReward( item );
				}
			};
			btnConfirm.setRect(0, height+2, width/2-1, 16);
			add(btnConfirm);

			RedButton btnCancel = new RedButton(Messages.get(WndSadGhost.class, "cancel")){
				@Override
				protected void onClick() {
					RewardWindow.this.hide();
				}
			};
			btnCancel.setRect(btnConfirm.right()+2, height+2, btnConfirm.width(), 16);
			add(btnCancel);

			resize(width, (int)btnCancel.bottom());
		}
	}
}