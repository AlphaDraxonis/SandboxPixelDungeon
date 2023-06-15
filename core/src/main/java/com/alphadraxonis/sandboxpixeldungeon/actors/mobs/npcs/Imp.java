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

import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.actors.Char;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.AscensionChallenge;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.Buff;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.LevelScheme;
import com.alphadraxonis.sandboxpixeldungeon.editor.quests.ImpQuest;
import com.alphadraxonis.sandboxpixeldungeon.items.quest.DwarfToken;
import com.alphadraxonis.sandboxpixeldungeon.journal.Notes;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.GameScene;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ImpSprite;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndImp;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndQuest;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;

public class Imp extends NPC {

	{
		spriteClass = ImpSprite.class;

		properties.add(Property.IMMOVABLE);
	}
	
	private boolean seenBefore = false;
	public ImpQuest quest;

	public Imp() {
	}

	public Imp(LevelScheme levelScheme) {
		quest = ImpQuest.createRandom(levelScheme);
	}
	
	@Override
	protected boolean act() {
		if (Dungeon.hero.buff(AscensionChallenge.class) != null){
			die(null);
			return true;
		}
		if (!quest.given() && Dungeon.level.visited[pos]) {
			if (!seenBefore) {
				yell( Messages.get(this, "hey", Messages.titleCase(Dungeon.hero.name()) ) );
			}
			Notes.add( Notes.Landmark.IMP );
			seenBefore = true;
		} else {
			seenBefore = false;
		}
		
		return super.act();
	}
	
	@Override
	public int defenseSkill( Char enemy ) {
		return INFINITE_EVASION;
	}

	@Override
	public void damage( int dmg, Object src ) {
		//do nothing
	}

	@Override
	public boolean add( Buff buff ) {
		return false;
	}
	
	@Override
	public boolean reset() {
		return true;
	}
	
	@Override
	public boolean interact(Char c) {
		
		sprite.turnTo( pos, Dungeon.hero.pos );

		if (c != Dungeon.hero){
			return true;
		}

		if (quest.given()) {
			
			DwarfToken tokens = Dungeon.hero.belongings.getItem( DwarfToken.class );
			if (tokens != null && tokens.quantity() >= quest.getRequiredQuantity()) {
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						GameScene.show( new WndImp( Imp.this, tokens ) );
					}
				});
			} else {
				String key;
				if (quest.type() == ImpQuest.MONK_QUEST) key = "monks_2";
				else if (quest.type() == ImpQuest.GOLEM_QUEST) key = "golems_2";
				else key = "";
				tell(Messages.get(this, key, Messages.titleCase(Dungeon.hero.name())));
			}

		} else {
			if (quest.type() == ImpQuest.MONK_QUEST) tell(Messages.get(this, "monks_1"));
			else if (quest.type() == ImpQuest.GOLEM_QUEST) tell(Messages.get(this, "golems_1"));
			quest.start();
		}

		return true;
	}
	
	private void tell( String text ) {
		Game.runOnRenderThread(new Callback() {
			@Override
			public void call() {
				GameScene.show( new WndQuest( Imp.this, text ));
			}
		});
	}
	
	public void flee() {
		
		yell( Messages.get(this, "cya", Messages.titleCase(Dungeon.hero.name())) );
		
		destroy();
		sprite.die();
	}

	private static final String QUEST = "quest";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		if (quest != null) bundle.put(QUEST, quest);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (bundle.contains(QUEST)) quest = (ImpQuest) bundle.get(QUEST);
	}

//	public static class Quest {
//
//		private static boolean alternative;
//
//		private static boolean spawned;
//		private static boolean given;
//		private static boolean completed;
//
//		public static Ring reward;
//
//		public static void reset() {
//			spawned = false;
//			given = false;
//			completed = false;
//
//			reward = null;
//		}
//
//		private static final String NODE		= "demon";
//
//		private static final String ALTERNATIVE	= "alternative";
//		private static final String SPAWNED		= "spawned";
//		private static final String GIVEN		= "given";
//		private static final String COMPLETED	= "completed";
//		private static final String REWARD		= "reward";
//
//		public static void storeInBundle( Bundle bundle ) {
//
//			Bundle node = new Bundle();
//
//			node.put( SPAWNED, spawned );
//
//			if (spawned) {
//				node.put( ALTERNATIVE, alternative );
//
//				node.put( GIVEN, given );
//				node.put( COMPLETED, completed );
//				node.put( REWARD, reward );
//			}
//
//			bundle.put( NODE, node );
//		}
//
//		public static void restoreFromBundle( Bundle bundle ) {
//
//			Bundle node = bundle.getBundle( NODE );
//
//			if (!node.isNull() && (spawned = node.getBoolean( SPAWNED ))) {
//				alternative	= node.getBoolean( ALTERNATIVE );
//
//				given = node.getBoolean( GIVEN );
//				completed = node.getBoolean( COMPLETED );
//				reward = (Ring)node.get( REWARD );
//			}
//		}
//
//		public static void spawn( Level level ) {
//			Imp npc = new Imp();
//			do {
//				npc.pos = level.randomRespawnCell( npc );
//			} while (
//					npc.pos == -1 ||
//							level.heaps.get( npc.pos ) != null ||
//							level.traps.get( npc.pos) != null ||
//							level.findMob( npc.pos ) != null ||
//							//The imp doesn't move, so he cannot obstruct a passageway
//							!(level.passable[npc.pos + PathFinder.CIRCLE4[0]] && level.passable[npc.pos + PathFinder.CIRCLE4[2]]) ||
//							!(level.passable[npc.pos + PathFinder.CIRCLE4[1]] && level.passable[npc.pos + PathFinder.CIRCLE4[3]]));
//			level.mobs.add( npc );
//			spawned = true;
//
//			//always assigns monks on floor 17, golems on floor 19, and 50/50 between either on 18
//			alternative= CustomDungeon.getDungeon().getImpQuest();
//
//			given = false;
//
//			do {
//				reward = (Ring)Generator.randomUsingDefaults( Generator.Category.RING );
//			} while (reward.cursed);
//			reward.upgrade( 2 );
//			reward.cursed = true;
//		}
//
//		public static void process( Mob mob ) {
//			if (spawned && given && !completed && !Dungeon.bossLevel()) {
//				if ((alternative && mob instanceof Monk) ||
//					(!alternative && mob instanceof Golem)) {
//
//					Dungeon.level.drop( new DwarfToken(), mob.pos ).sprite.drop();
//				}
//			}
//		}
//
//		public static void complete() {
//			reward = null;
//			completed = true;
//
//			Statistics.questScores[3] = 4000;
//			Notes.remove( Notes.Landmark.IMP );
//		}
//
//		public static boolean isCompleted() {
//			return completed;
//		}
//	}
}