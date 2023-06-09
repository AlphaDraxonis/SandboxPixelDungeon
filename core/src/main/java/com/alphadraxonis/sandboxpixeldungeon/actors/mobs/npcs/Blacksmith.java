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

package com.alphadraxonis.sandboxpixeldungeon.actors.mobs.npcs;

import com.alphadraxonis.sandboxpixeldungeon.Assets;
import com.alphadraxonis.sandboxpixeldungeon.Badges;
import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.actors.Char;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.AscensionChallenge;
import com.alphadraxonis.sandboxpixeldungeon.editor.quests.BlacksmithQuest;
import com.alphadraxonis.sandboxpixeldungeon.editor.quests.Quest;
import com.alphadraxonis.sandboxpixeldungeon.editor.quests.QuestNPC;
import com.alphadraxonis.sandboxpixeldungeon.items.BrokenSeal;
import com.alphadraxonis.sandboxpixeldungeon.items.EquipableItem;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.items.armor.Armor;
import com.alphadraxonis.sandboxpixeldungeon.items.quest.DarkGold;
import com.alphadraxonis.sandboxpixeldungeon.items.quest.Pickaxe;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.Weapon;
import com.alphadraxonis.sandboxpixeldungeon.journal.Notes;
import com.alphadraxonis.sandboxpixeldungeon.levels.RegularLevel;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.Room;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.standard.BlacksmithRoom;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.GameScene;
import com.alphadraxonis.sandboxpixeldungeon.sprites.BlacksmithSprite;
import com.alphadraxonis.sandboxpixeldungeon.utils.GLog;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndBlacksmith;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndQuest;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

import java.util.List;

public class Blacksmith extends QuestNPC<BlacksmithQuest> {
	
	{
		spriteClass = BlacksmithSprite.class;

		properties.add(Property.IMMOVABLE);
	}

	public Blacksmith() {
	}

	public Blacksmith(BlacksmithQuest quest) {
		super(quest);
	}
	
	@Override
	protected boolean act() {
		if (Dungeon.hero.buff(AscensionChallenge.class) != null){
			die(null);
			return true;
		}
		if (quest != null && quest.type() != Quest.NONE && Dungeon.level.visited[pos] && !quest.reforged()){
			Notes.add( Notes.Landmark.TROLL );
		}
		return super.act();
	}
	
	@Override
	public boolean interact(Char c) {
		
		sprite.turnTo( pos, c.pos );

		if (c != Dungeon.hero){
			return true;
		}

		if (quest != null && quest.type() != Quest.NONE) {
			if (!quest.given()) {

				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						GameScene.show(new WndQuest(Blacksmith.this,
								Messages.get(Blacksmith.this, quest.getMessageString()+ "_1")) {

							@Override
							public void onBackPressed() {
								super.onBackPressed();

								quest.start();

								Pickaxe pick = new Pickaxe();
								pick.identify();
								if (pick.doPickUp(Dungeon.hero)) {
									GLog.i(Messages.capitalize(Messages.get(Dungeon.hero, "you_now_have", pick.name())));
								} else {
									Dungeon.level.drop(pick, Dungeon.hero.pos).sprite.drop();
								}
							}
						});
					}
				});

			} else if (!quest.completed()) {
				if (quest.type() == BlacksmithQuest.BLOOD) {

					Pickaxe bloodStainedPick = Dungeon.hero.belongings.doWithEachItem(Pickaxe.class, pickaxe -> pickaxe.bloodStained);
					Pickaxe normalPick = Dungeon.hero.belongings.getItem(Pickaxe.class);

					if (normalPick == null) {
						tell(Messages.get(this, "lost_pick"));
					} else if (bloodStainedPick == null) {
						tell(Messages.get(this, "blood_2"));
					} else {
						if (bloodStainedPick.isEquipped(Dungeon.hero)) {
							bloodStainedPick.cursed = false; //so that it can always be removed
							bloodStainedPick.doUnequip(Dungeon.hero, false);
						}
						bloodStainedPick.detach(Dungeon.hero.belongings.backpack);
						tell(Messages.get(this, "completed"));

						quest.complete();
					}

				} else if (quest.type() == BlacksmithQuest.GOLD) {

					Pickaxe pick = Dungeon.hero.belongings.doWithEachItem(Pickaxe.class, pickaxe -> !pickaxe.bloodStained);
					if (pick == null) pick = Dungeon.hero.belongings.getItem(Pickaxe.class);
					DarkGold gold = Dungeon.hero.belongings.getItem(DarkGold.class);

					if (pick == null) {
						tell(Messages.get(this, "lost_pick"));
					} else if (gold == null || gold.quantity() < 15) {
						tell(Messages.get(this, "gold_2"));
					} else {
						if (pick.isEquipped(Dungeon.hero)) {
							pick.doUnequip(Dungeon.hero, false);
						}
						pick.detach(Dungeon.hero.belongings.backpack);
						gold.detachAll(Dungeon.hero.belongings.backpack);
						tell(Messages.get(this, "completed"));

						quest.complete();
					}

				}
			} else if (!quest.reforged()) {

				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						GameScene.show(new WndBlacksmith(Blacksmith.this, Dungeon.hero));
					}
				});

			} else {
				tell(Messages.get(this, "get_lost"));
			}
		 }else tell(Messages.get(this, "get_lost"));

		return true;
	}
	
	private void tell( String text ) {
		Game.runOnRenderThread(new Callback() {
			@Override
			public void call() {
				GameScene.show( new WndQuest( Blacksmith.this, text ) );
			}
		});
	}
	
	public static String verify( Item item1, Item item2 ) {
		
		if (item1 == item2 && (item1.quantity() == 1 && item2.quantity() == 1)) {
			return Messages.get(Blacksmith.class, "same_item");
		}

		if (item1.getClass() != item2.getClass()) {
			return Messages.get(Blacksmith.class, "diff_type");
		}
		
		if (!item1.isIdentified() || !item2.isIdentified()) {
			return Messages.get(Blacksmith.class, "un_ided");
		}
		
		if (item1.cursed || item2.cursed ||
				(item1 instanceof Armor && ((Armor) item1).hasCurseGlyph()) ||
				(item2 instanceof Armor && ((Armor) item2).hasCurseGlyph()) ||
				(item1 instanceof Weapon && ((Weapon) item1).hasCurseEnchant()) ||
				(item2 instanceof Weapon && ((Weapon) item2).hasCurseEnchant())) {
			return Messages.get(Blacksmith.class, "cursed");
		}
		
		if (item1.level() < 0 || item2.level() < 0) {
			return Messages.get(Blacksmith.class, "degraded");
		}
		
		if (!item1.isUpgradable() || !item2.isUpgradable()) {
			return Messages.get(Blacksmith.class, "cant_reforge");
		}
		
		return null;
	}
	
	public static void upgrade( Item item1, Item item2 ) {
		
		Item first, second;
		if (item2.trueLevel() > item1.trueLevel()) {
			first = item2;
			second = item1;
		} else {
			first = item1;
			second = item2;
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
		Dungeon.hero.spendAndNext( 2f );
		Badges.validateItemLevelAquired( first );
		Item.updateQuickslot();
	}


	@Override
	public void place(RegularLevel level, List<Room> rooms) {
		for (Room room : rooms) {
			if (room instanceof BlacksmithRoom) {
				if (((BlacksmithRoom) room).placeBlacksmith(this, level)) break;
			}
		}
	}

	@Override
	public void createNewQuest() {
		quest = new BlacksmithQuest();
	}
}