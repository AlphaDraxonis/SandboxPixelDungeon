/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.BlacksmithQuest;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.QuestNPC;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DarkGold;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Pickaxe;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.RegularLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.BlacksmithRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BlacksmithSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBlacksmith;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuest;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;

import java.util.List;
import java.util.Locale;

public class Blacksmith extends QuestNPC<BlacksmithQuest> {

	{
		spriteClass = BlacksmithSprite.class;

		properties.add(Property.IMMOVABLE);
	}

	private int questId = -1;

	public Blacksmith() {
	}

	public Blacksmith(BlacksmithQuest quest) {
		super(quest);
	}

	@Override
	protected void onAdd() {
		super.onAdd();
		if (Dungeon.hero != null) {
			registerQuest();
		}
	}

	@Override
	protected boolean act() {
		if (Dungeon.hero.buff(AscensionChallenge.class) != null) {
			die(null);
			Notes.remove(Notes.Landmark.TROLL);
			return true;
		}
		if (quest != null && quest.type() >= 0 && !quest.started() && Dungeon.level.visited[pos]) {
			Notes.add(Notes.Landmark.TROLL);
		}
		return super.act();
	}
	
	@Override
	public boolean interact(Char c) {
		
		sprite.turnTo( pos, c.pos );

		if (c != Dungeon.hero){
			return true;
		}

		if (quest == null || quest.type() < 0) {
			tell(Messages.get(this, "get_lost"));
			return true;
		}

		if (!quest.given()) {
			String msg1 = "";
			String msg2 = "";

			if (quest.type() <= BlacksmithQuest.BLOOD) {
				//pre-v2.2.0 saves
				msg1 = Messages.get(Blacksmith.this, quest.getMessageString() + "_1");
			} else {
				msg1 += Messages.get(Blacksmith.this, "intro_quest_" + Dungeon.hero.heroClass.name().toLowerCase(Locale.ENGLISH));
				msg1 += "\n\n" + Messages.get(Blacksmith.this, "intro_quest_start");
				msg2 += Messages.get(Blacksmith.this, "intro_quest_" + quest.getMessageString());
			}

			final String msg1Final = msg1;
			final String msg2Final = msg2;
			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					GameScene.show(new WndQuest(Blacksmith.this, msg1Final) {
						@Override
						public void hide() {
							super.hide();

							quest.start();

							Item pick = quest.pickaxe != null ? quest.pickaxe : new Pickaxe();
							pick.identify();
							if (pick.doPickUp(Dungeon.hero)) {
								GLog.i(Messages.capitalize(Messages.get(Dungeon.hero, "you_now_have", pick.name())));
							} else {
								Dungeon.level.drop( pick, Dungeon.hero.pos ).sprite.drop();
							}
							quest.pickaxe = null;

							if (!"".equals(msg2Final)){
								GameScene.show(new WndQuest(Blacksmith.this, msg2Final));
							}

						}
					} );
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
					quest.pickaxe = bloodStainedPick;
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
					if (gold.quantity() <= 15) gold.detachAll(Dungeon.hero.belongings.backpack);
					else gold.quantity(gold.quantity() - 15);
					quest.pickaxe = pick;
					tell(Messages.get(this, "completed"));

					quest.complete();
				}

			} else {
					String msg = Messages.get(this, "reminder");
					if (quest.type() >= BlacksmithQuest.CRYSTAL) msg += "\n\n" + Messages.get(Blacksmith.this, "reminder_" + quest.getMessageString());
					tell(msg);
			}
		} else if (quest.reforges == 0 && quest.type() <= BlacksmithQuest.BLOOD) {

			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					GameScene.show(new WndBlacksmith.WndReforge(Blacksmith.this, null));
				}
			});

		} else if (quest.rewardsAvailable()) {

			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					//in case game was closed during smith reward selection
					if (quest.smithRewards != null && quest.smiths > 0){
						GameScene.show( new WndBlacksmith.WndSmith( Blacksmith.this, Dungeon.hero ) );
					} else {
						GameScene.show(new WndBlacksmith(Blacksmith.this, Dungeon.hero));
					}
				}
			});

		} else {
			
			tell( Messages.get(this, "get_lost") );
			
		}

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

	private static final String QUEST_ID = "quest_id";
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (bundle.contains(QUEST_ID)){
			questId = bundle.getInt(QUEST_ID);
			if (questId >= 0) {
				BlacksmithQuest q = BlacksmithQuest.findById(questId);
				if (q != null) quest = q;
			}
		} else registerQuest();
	}
	
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(QUEST_ID, questId);
	}

	public void registerQuest(){
		int id = quest.registerQuest();
		if (id != -1) questId = id;
	}

	@Override
	public Actor getCopy() {
		int prevQuestId = questId;
		questId = -2;//Do not take the quest from the storage
		Blacksmith blacksmith = (Blacksmith) super.getCopy();
		questId = prevQuestId;
		blacksmith.questId = -1;
		return blacksmith;
	}
}