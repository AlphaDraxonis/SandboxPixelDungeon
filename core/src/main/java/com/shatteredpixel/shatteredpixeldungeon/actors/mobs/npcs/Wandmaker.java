/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.QuestNPC;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.WandmakerQuest;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CorpseDust;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Embers;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.RegularLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Rotberry;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WandmakerSprite;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuest;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndWandmaker;
import com.watabou.noosa.Game;
import com.watabou.utils.Callback;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.List;
import java.util.Locale;

public class Wandmaker extends QuestNPC<WandmakerQuest> {

	{
		spriteClass = WandmakerSprite.class;

		properties.add(Property.IMMOVABLE);
	}

	public Wandmaker() {
	}

	public Wandmaker(WandmakerQuest quest) {
		super(quest);
	}

	@Override
	public Notes.Landmark landmark() {
		return Notes.Landmark.WANDMAKER;
	}

	@Override
	protected boolean act() {
		if (Dungeon.hero.buff(AscensionChallenge.class) != null){
			die(null);
			return true;
		}
		return super.act();
	}

	@Override
	public boolean interact(Char c) {
		sprite.turnTo( pos, Dungeon.hero.pos );

		if (c != Dungeon.hero){
			return true;
		}

		if(quest == null || quest.type() < 0) return true;

		if (quest.given()) {
			
			Item item;
			switch (quest.type()) {
				case WandmakerQuest.DUST:
				default:
					item = Dungeon.hero.belongings.getItem(CorpseDust.class);
					break;
				case WandmakerQuest.CANDLE:
					item = Dungeon.hero.belongings.getItem(Embers.class);
					break;
				case WandmakerQuest.SEED:
					item = Dungeon.hero.belongings.getItem(Rotberry.Seed.class);
					break;
			}

			if (item != null) {
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						GameScene.show( new WndWandmaker( Wandmaker.this, item ) );
					}
				});
			} else {
				String msg = Messages.get(this, "reminder_"+quest.getMessageString(), Messages.titleCase(Dungeon.hero.name()));
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						GameScene.show(new WndQuest(Wandmaker.this, msg));
					}
				});
			}
			
		} else {

			String msg1 = Messages.get(Wandmaker.this, "intro_" + Dungeon.hero.heroClass.name().toLowerCase(Locale.ENGLISH), Messages.titleCase(Dungeon.hero.name()));
			msg1 += Messages.get(this, "intro_1");

			String msg2 = Messages.get(this, "intro_"+quest.getMessageString());
			msg2 += Messages.get(this, "intro_2");

			final String msg1Final = msg1;
			final String msg2Final = msg2;
			
			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					GameScene.show(new WndQuest(Wandmaker.this, msg1Final){
						@Override
						public void hide() {
							super.hide();
							GameScene.show(new WndQuest(Wandmaker.this, msg2Final));
						}
					});
				}
			});

			quest.start();
		}

		return true;
	}

	@Override
	public void place(RegularLevel level, List<Room> rooms) {
//        Set<Room> entrances = new HashSet<>(4);// TODO maybe use just entranceRoom instead ?
//        for (Room room : rooms) {
//            if (room.isEntrance()) {
//                entrances.add(room);
//            }
//        }
		Room roomEntrance = null;
		for (Room room : rooms) {
			if (room.isEntrance()) {
				roomEntrance = room;
				break;
			}
		}
		if (roomEntrance == null) {
			roomEntrance = rooms.get(Random.Int(rooms.size()));
		}

		boolean validPos;
		int tries = level.length();
		//Do not spawn wandmaker on the entrance, a trap, or on bad terrain.
		do {
			validPos = true;
			pos = level.pointToCell(roomEntrance.random((roomEntrance.width() > 6 && roomEntrance.height() > 6) ? 2 : 1));
			if (pos == level.entrance()) {
				validPos = false;
			}
			for (Point door : roomEntrance.connected.values()) {
				if (level.trueDistance(pos, level.pointToCell(door)) <= 1) {
					validPos = false;
				}
			}
			if (level.traps.get(pos) != null
					|| !level.isPassable(pos, this)
					|| level.map[pos] == Terrain.EMPTY_SP) {
				validPos = false;
			}
			tries--;
			if (!validPos) pos = -1;
		} while (pos == -1 && tries > 0);
		if (pos != -1) level.mobs.add(this);
	}


	@Override
	public void createNewQuest() {
		quest = new WandmakerQuest();
	}
}
