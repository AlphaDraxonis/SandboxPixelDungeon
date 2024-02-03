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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.FetidRat;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GnollTrickster;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GreatCrab;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.GhostQuest;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.QuestNPC;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.RegularLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.SewerLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.ExitRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GhostSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuest;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndSadGhost;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

import java.util.List;

public class Ghost extends QuestNPC<GhostQuest> {

	{
		spriteClass = GhostSprite.class;

		setFlying(true);

		WANDERING = new Wandering();
		state = WANDERING;
	}

	public Ghost() {
	}

	public Ghost(GhostQuest quest) {
		super(quest);
	}

	@Override
	protected boolean cellIsPathable(int cell) {
		if (!super.cellIsPathable(cell)) {
			return false;
		}
		if (Dungeon.level.map[cell] == Terrain.DOOR || Dungeon.level.map[cell] == Terrain.OPEN_DOOR) {
			return false;
		}
		return true;
	}

	@Override
	public boolean[] modPassable(boolean[] passable) {
		passable = super.modPassable(passable);
		for (int i = 0; i < passable.length; i++){
			passable[i] = passable[i] && Dungeon.level.map[i] != Terrain.DOOR && Dungeon.level.map[i] != Terrain.OPEN_DOOR;
		}
		return passable;
	}

	protected class Wandering extends Mob.Wandering{
		@Override
		protected int randomDestination() {
			int pos = super.randomDestination();
			//cannot wander onto heaps or the level exit
			if (Dungeon.level.heaps.get(pos) != null || pos == Dungeon.level.exit()){
				return -1;
			}
			if (Dungeon.level.map[pos] == Terrain.DOOR || Dungeon.level.map[pos] == Terrain.OPEN_DOOR) {
				return -1;
			}
			return pos;
		}
	}

	@Override
	protected boolean act() {
		if (Dungeon.hero.buff(AscensionChallenge.class) != null){
			die(null);
			return true;
		}
		if (quest != null && quest.type() >= 0) {
			if (Dungeon.level.heroFOV[pos] && !quest.completed()){
				Notes.add( Notes.Landmark.GHOST );
			}
		}
		return super.act();
	}
	
	@Override
	public float speed() {
		return 0.5f;
	}
	
	@Override
	protected Char chooseEnemy() {
		return null;
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
		sprite.turnTo( pos, c.pos );
		
		Sample.INSTANCE.play( Assets.Sounds.GHOST );

		if (c != Dungeon.hero){
			return super.interact(c);
		}

		if (quest == null || quest.type() < 0) return true;

		if (quest.given()) {
			if (quest.weapon != null && quest.completed()) {
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						GameScene.show(new WndSadGhost(Ghost.this, quest.type()));
					}
				});
			} else {
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						GameScene.show(new WndQuest(Ghost.this, Messages.get(Ghost.this, quest.getMessageString()+"_2")));
					}
				});
			}
		} else {
			Mob questBoss;
			String txt_quest;

			switch (quest.type()){
				case GhostQuest.RAT: default:
					questBoss = new FetidRat(this);
					txt_quest = Messages.get(this, "rat_1", Messages.titleCase(Dungeon.hero.name())); break;
				case GhostQuest.GNOLL:
					questBoss = new GnollTrickster(this);
					txt_quest = Messages.get(this, "gnoll_1", Messages.titleCase(Dungeon.hero.name())); break;
				case GhostQuest.CRAB:
					questBoss = new GreatCrab(this);
					txt_quest = Messages.get(this, "crab_1", Messages.titleCase(Dungeon.hero.name())); break;
			}

			questBoss.pos = Dungeon.level.randomRespawnCell( this, true );

			if (questBoss.pos != -1) {
				GameScene.add(questBoss);
				quest.start();
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						GameScene.show( new WndQuest( Ghost.this, txt_quest ){
							@Override
							public void hide() {
								super.hide();
								if (!SewerLevel.playingQuestMusic && Dungeon.level.playsMusicFromRegion() == LevelScheme.REGION_SEWERS)
									Dungeon.level.playLevelMusic();
							}
						} );
					}
				});
			} else GLog.n(Messages.get(this, "no_boss_warning"));

		}

		return true;
	}

    @Override
    public void place(RegularLevel level, List<Room> rooms) {
		Room exit = null;
		for (Room room : rooms) {
			if (room instanceof ExitRoom) {
				exit = room;
				break;
			}
		}
		if (exit != null) {
			do {
				pos = level.pointToCell(exit.random());
			} while (pos == -1 || level.transitions.containsKey(pos));
		} else {
			int tries = level.length();
			do {
				pos = level.randomRespawnCell(this);
				tries--;
			} while (pos == -1 && tries > 0);
		}
        if (pos != -1) level.mobs.add(this);
    }

    @Override
    public void createNewQuest() {
        quest = new GhostQuest();
    }
}