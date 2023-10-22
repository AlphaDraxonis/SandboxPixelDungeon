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
import com.alphadraxonis.sandboxpixeldungeon.Badges;
import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.Statistics;
import com.alphadraxonis.sandboxpixeldungeon.actors.hero.Belongings;
import com.alphadraxonis.sandboxpixeldungeon.actors.hero.Hero;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.alphadraxonis.sandboxpixeldungeon.items.BrokenSeal;
import com.alphadraxonis.sandboxpixeldungeon.items.EquipableItem;
import com.alphadraxonis.sandboxpixeldungeon.items.Gold;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.items.armor.Armor;
import com.alphadraxonis.sandboxpixeldungeon.items.bags.Bag;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.Weapon;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.alphadraxonis.sandboxpixeldungeon.journal.Notes;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.GameScene;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.ui.ItemButton;
import com.alphadraxonis.sandboxpixeldungeon.ui.RedButton;
import com.alphadraxonis.sandboxpixeldungeon.ui.RenderedTextBlock;
import com.alphadraxonis.sandboxpixeldungeon.ui.Window;
import com.alphadraxonis.sandboxpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class WndBlacksmith extends Window {


	private static final int WIDTH_P = 120;
	private static final int WIDTH_L = 160;

	private static final int GAP  = 2;

	protected Blacksmith troll;

	public WndBlacksmith( Blacksmith troll, Hero hero ) {
		super();

		this.troll = troll;

		int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;

		IconTitle titlebar = new IconTitle();
		titlebar.icon( troll.sprite() );
		titlebar.label( Messages.titleCase( troll.name() ) );
		titlebar.setRect( 0, 0, width, 0 );
		add( titlebar );

		RenderedTextBlock message = PixelScene.renderTextBlock( Messages.get(this, "prompt", troll.quest.favor), 6 );
		message.maxWidth( width );
		message.setPos(0, titlebar.bottom() + GAP);
		add( message );

		ArrayList<RedButton> buttons = new ArrayList<>();

		int pickaxeCost = Statistics.questScores[2] >= 2500 ? 0 : 250;
		RedButton pickaxe = new RedButton(Messages.get(this, "pickaxe", pickaxeCost), 6){
			@Override
			protected void onClick() {
				GameScene.show(new WndOptions(
						troll.sprite(),
						Messages.titleCase( troll.name() ),
						Messages.get(WndBlacksmith.class, "pickaxe_verify") + (pickaxeCost == 0 ? "\n\n" + Messages.get(WndBlacksmith.class, "pickaxe_free") : ""),
						Messages.get(WndBlacksmith.class, "pickaxe_yes"),
						Messages.get(WndBlacksmith.class, "pickaxe_no")
				){
					@Override
					protected void onSelect(int index) {
						if (index == 0){
							if (troll.quest.pickaxe.doPickUp( Dungeon.hero )) {
								GLog.i( Messages.capitalize(Messages.get(Dungeon.hero, "you_now_have", troll.quest.pickaxe.name()) ));
							} else {
								Dungeon.level.drop( troll.quest.pickaxe, Dungeon.hero.pos ).sprite.drop();
							}
							troll.quest.favor -= pickaxeCost;
							troll.quest.pickaxe = null;
							WndBlacksmith.this.hide();
						}
					}
				});
			}
		};
		pickaxe.enable(troll.quest.pickaxe != null && troll.quest.favor >= pickaxeCost);
		buttons.add(pickaxe);

		int reforgecost = 500 * (int)Math.pow(2, troll.quest.reforges);
		RedButton reforge = new RedButton(Messages.get(this, "reforge", reforgecost), 6){
			@Override
			protected void onClick() {
				GameScene.show(new WndReforge(troll, WndBlacksmith.this));
			}
		};
		reforge.enable(troll.quest.favor >= reforgecost);
		buttons.add(reforge);

		int hardenCost = 500 * (int)Math.pow(2, troll.quest.hardens);
		RedButton harden = new RedButton(Messages.get(this, "harden", hardenCost), 6){
			@Override
			protected void onClick() {
				GameScene.selectItem(new HardenSelector());
			}
		};
		harden.enable(troll.quest.favor >= hardenCost);
		buttons.add(harden);

		int upgradeCost = 1000 * (int)Math.pow(2, troll.quest.upgrades);
		RedButton upgrade = new RedButton(Messages.get(this, "upgrade", upgradeCost), 6){
			@Override
			protected void onClick() {
				GameScene.selectItem(new UpgradeSelector());
			}
		};
		upgrade.enable(troll.quest.favor >= upgradeCost);
		buttons.add(upgrade);

		RedButton smith = new RedButton(Messages.get(this, "smith", 2000), 6){
			@Override
			protected void onClick() {
				GameScene.show(new WndOptions(
						troll.sprite(),
						Messages.titleCase( troll.name() ),
						Messages.get(WndBlacksmith.class, "smith_verify"),
						Messages.get(WndBlacksmith.class, "smith_yes"),
						Messages.get(WndBlacksmith.class, "smith_no")
				){
					@Override
					protected void onSelect(int index) {
						if (index == 0){
							troll.quest.favor -= 2000;
							troll.quest.smiths++;
							WndBlacksmith.this.hide();
							GameScene.show(new WndSmith(troll, hero));
						}
					}
				});
			}
		};
		smith.enable(troll.quest.favor >= 2000);
		buttons.add(smith);

		RedButton cashOut = new RedButton(Messages.get(this, "cashout"), 6){
			@Override
			protected void onClick() {
				GameScene.show(new WndOptions(
						troll.sprite(),
						Messages.titleCase( troll.name() ),
						Messages.get(WndBlacksmith.class, "cashout_verify", troll.quest.favor),
						Messages.get(WndBlacksmith.class, "cashout_yes"),
						Messages.get(WndBlacksmith.class, "cashout_no")
				){
					@Override
					protected void onSelect(int index) {
						if (index == 0){
							new Gold(troll.quest.favor).doPickUp(Dungeon.hero, Dungeon.hero.pos);
							troll.quest.favor = 0;
							WndBlacksmith.this.hide();
						}
					}
				});
			}
		};
		cashOut.enable(troll.quest.favor > 0);
		buttons.add(cashOut);

		float pos = message.bottom() + 3*GAP;
		for (RedButton b : buttons){
			b.leftJustify = true;
			b.multiline = true;
			b.setSize(width, b.reqHeight());
			b.setRect(0, pos, width, b.reqHeight());
			b.enable(b.active); //so that it's visually reflected
			add(b);
			pos = b.bottom() + GAP;
		}

		resize(width, (int)pos);

	}


	//public so that it can be directly called for pre-v2.2.0 quest completions
	public static class WndReforge extends Window {

		private static final int WIDTH		= 120;

		private static final int BTN_SIZE	= 32;
		private static final float GAP		= 2;
		private static final float BTN_GAP	= 5;

		private ItemButton btnPressed;

		private ItemButton btnItem1;
		private ItemButton btnItem2;
		private RedButton btnReforge;

		public WndReforge( Blacksmith troll, Window wndParent ) {
			super();

			IconTitle titlebar = new IconTitle();
			titlebar.icon( troll.sprite() );
			titlebar.label( Messages.titleCase( troll.name() ) );
			titlebar.setRect( 0, 0, WIDTH, 0 );
			add( titlebar );

			RenderedTextBlock message = PixelScene.renderTextBlock( Messages.get(this, "message"), 6 );
			message.maxWidth( WIDTH);
			message.setPos(0, titlebar.bottom() + GAP);
			add( message );

			btnItem1 = new ItemButton() {
				@Override
				protected void onClick() {
					btnPressed = btnItem1;
					GameScene.selectItem( itemSelector );
				}
			};
			btnItem1.setRect( (WIDTH - BTN_GAP) / 2 - BTN_SIZE, message.top() + message.height() + BTN_GAP, BTN_SIZE, BTN_SIZE );
			add( btnItem1 );

			btnItem2 = new ItemButton() {
				@Override
				protected void onClick() {
					btnPressed = btnItem2;
					GameScene.selectItem( itemSelector );
				}
			};
			btnItem2.setRect( btnItem1.right() + BTN_GAP, btnItem1.top(), BTN_SIZE, BTN_SIZE );
			add( btnItem2 );

			btnReforge = new RedButton( Messages.get(this, "reforge") ) {
				@Override
				protected void onClick() {

					Item first, second;
					if (btnItem1.item().trueLevel() > btnItem2.item().trueLevel()) {
						first = btnItem1.item();
						second = btnItem2.item();
					} else {
						first = btnItem2.item();
						second = btnItem1.item();
					}

					Sample.INSTANCE.play( Assets.Sounds.EVOKE );
					ScrollOfUpgrade.upgrade( Dungeon.hero );
					Item.evoke( Dungeon.hero );

					if (second.isEquipped( Dungeon.hero )) {
						((EquipableItem)second).doUnequip( Dungeon.hero, false );
					}
					second.detach( Dungeon.hero.belongings.backpack );

					if (second instanceof Armor){
						BrokenSeal seal = ((Armor) second).checkSeal();
						if (seal != null){
							Dungeon.level.drop( seal, Dungeon.hero.pos );
						}
					}

					//preserves enchant/glyphs if present
					if (first instanceof Weapon && ((Weapon) first).hasGoodEnchant()){
						((Weapon) first).upgrade(true);
					} else if (first instanceof Armor && ((Armor) first).hasGoodGlyph()){
						((Armor) first).upgrade(true);
					} else {
						first.upgrade();
					}
					Badges.validateItemLevelAquired( first );
					Item.updateQuickslot();


					troll.quest.favor -= 500 * (int)Math.pow(2, troll.quest.reforges);
					Notes.remove( Notes.Landmark.TROLL );
					troll.quest.reforges++;

					hide();
					if (wndParent != null){
						wndParent.hide();
					}
				}
			};
			btnReforge.enable( false );
			btnReforge.setRect( 0, btnItem1.bottom() + BTN_GAP, WIDTH, 20 );
			add( btnReforge );


			resize( WIDTH, (int)btnReforge.bottom() );
		}

		protected WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {

			@Override
			public String textPrompt() {
				return Messages.get(WndReforge.class, "prompt");
			}

			@Override
			public Class<?extends Bag> preferredBag(){
				return Belongings.Backpack.class;
			}

			@Override
			public boolean itemSelectable(Item item) {
				return item.isIdentified() && item.isUpgradable();
			}

			@Override
			public void onSelect( Item item ) {
				if (item != null && btnPressed.parent != null) {
					btnPressed.item(item);

					Item item1 = btnItem1.item();
					Item item2 = btnItem2.item();

					//need 2 items
					if (item1 == null || item2 == null) {
						btnReforge.enable(false);

						//both of the same type
					} else if (item1.getClass() != item2.getClass()) {
						btnReforge.enable(false);

						//and not the literal same item (unless quantity is >1)
					} else if (item1 == item2 && item1.quantity() == 1) {
						btnReforge.enable(false);

					} else {
						btnReforge.enable(true);
					}
				}
			}
		};

	}

	private class HardenSelector extends WndBag.ItemSelector {

		@Override
		public String textPrompt() {
			return Messages.get(this, "prompt");
		}

		@Override
		public Class<?extends Bag> preferredBag(){
			return Belongings.Backpack.class;
		}

		@Override
		public boolean itemSelectable(Item item) {
			return item.isUpgradable()
					&& item.isIdentified()
					&& ((item instanceof MeleeWeapon && !((Weapon) item).enchantHardened)
					|| (item instanceof Armor && !((Armor) item).glyphHardened));
		}

		@Override
		public void onSelect(Item item) {
			if (item != null) {
				if (item instanceof Weapon){
					((Weapon) item).enchantHardened = true;
				} else if (item instanceof Armor){
					((Armor) item).glyphHardened = true;
				}

				int hardenCost = troll.quest.hardens == 0 ? 500 : 1000;
				troll.quest.favor -= hardenCost;
				troll.quest.hardens++;

				WndBlacksmith.this.hide();

				Sample.INSTANCE.play(Assets.Sounds.EVOKE);
				Item.evoke( Dungeon.hero );
			}
		}
	}

	private class UpgradeSelector extends WndBag.ItemSelector {

		@Override
		public String textPrompt() {
			return Messages.get(this, "prompt");
		}

		@Override
		public Class<?extends Bag> preferredBag(){
			return Belongings.Backpack.class;
		}

		@Override
		public boolean itemSelectable(Item item) {
			return item.isUpgradable()
					&& item.isIdentified()
					&& item.level() < 3;
		}

		@Override
		public void onSelect(Item item) {
			if (item != null) {
				item.upgrade();
				int upgradeCost = 1000 * (int)Math.pow(2, troll.quest.upgrades);
				troll.quest.favor -= upgradeCost;
				troll.quest.upgrades++;

				WndBlacksmith.this.hide();

				Sample.INSTANCE.play(Assets.Sounds.EVOKE);
				ScrollOfUpgrade.upgrade( Dungeon.hero );
				Item.evoke( Dungeon.hero );

				Badges.validateItemLevelAquired( item );
			}
		}
	}

	public static class WndSmith extends Window {

		private static final int WIDTH      = 120;
		private static final int BTN_SIZE	= 32;
		private static final int BTN_GAP	= 5;
		private static final int GAP		= 2;

		public WndSmith( Blacksmith troll, Hero hero ){
			super();

			IconTitle titlebar = new IconTitle();
			titlebar.icon(troll.sprite());
			titlebar.label(Messages.titleCase(troll.name()));

			RenderedTextBlock message = PixelScene.renderTextBlock( Messages.get(this, "prompt"), 6 );

			titlebar.setRect( 0, 0, WIDTH, 0 );
			add( titlebar );

			message.maxWidth(WIDTH);
			message.setPos(0, titlebar.bottom() + GAP);
			add( message );

			int count = 0;
			for (Item i : troll.quest.smithRewards){
				count++;
				ItemButton btnReward = new ItemButton(){
					@Override
					protected void onClick() {
						GameScene.show(new RewardWindow(troll, hero, item()));
					}
				};
				btnReward.item( i );
				btnReward.setRect( count*(WIDTH - BTN_GAP) / 3 - BTN_SIZE, message.top() + message.height() + BTN_GAP, BTN_SIZE, BTN_SIZE );
				add( btnReward );

			}

			resize(WIDTH, (int)message.bottom() + 2*BTN_GAP + BTN_SIZE);

		}

		@Override
		public void onBackPressed() {
			//do nothing
		}

		private class RewardWindow extends WndInfoItem {

			public RewardWindow( Blacksmith troll, Hero hero, Item item ) {
				super(item);

				RedButton btnConfirm = new RedButton(Messages.get(WndSadGhost.class, "confirm")){
					@Override
					protected void onClick() {
						RewardWindow.this.hide();
						item.identify(false);
						Sample.INSTANCE.play(Assets.Sounds.EVOKE);
						Item.evoke( Dungeon.hero );
						if (item.doPickUp( Dungeon.hero )) {
							GLog.i( Messages.capitalize(Messages.get(Dungeon.hero, "you_now_have", item.name())) );
						} else {
							Dungeon.level.drop( item, Dungeon.hero.pos ).sprite.drop();
						}
						WndSmith.this.hide();
						troll.quest.smithRewards = null;
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

}