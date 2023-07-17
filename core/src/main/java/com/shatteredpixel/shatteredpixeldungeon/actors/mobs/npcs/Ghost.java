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
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.GhostQuest;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.QuestNPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.RegularLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GhostSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuest;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndSadGhost;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.List;

public class Ghost extends QuestNPC<GhostQuest> {

	{
		spriteClass = GhostSprite.class;
		
		flying = true;
		
		state = PASSIVE;
	}

    public Ghost() {
    }

    public Ghost(GhostQuest quest) {
        super(quest);
    }

    @Override
    protected boolean act() {
        if (Dungeon.hero.buff(AscensionChallenge.class) != null) {
            die(null);
            return true;
        }
        if (quest != null && quest.type() >= 0) {
            if (quest.completed()) {
                target = Dungeon.hero.pos;
            }
            if (Dungeon.level.heroFOV[pos] && !quest.completed()) {
                Notes.add(Notes.Landmark.GHOST);
            }
        }
        return super.act();
    }
	
	@Override
	public float speed() {
		return quest != null && quest.type() >= 0 && quest.completed() ? 2f : 0.5f;
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

        if (c != Dungeon.hero) {
            return super.interact(c);
        }

        if (quest != null && quest.type() >= 0) {

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

                        int newPos = -1;
                        for (int i = 0; i < 10; i++) {
                            newPos = Dungeon.level.randomRespawnCell(this, true);
                            if (newPos != -1) {
                                break;
                            }
                        }
                        if (newPos != -1) {

                            CellEmitter.get(pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3);
                            pos = newPos;
                            sprite.place(pos);
                            sprite.visible = Dungeon.level.heroFOV[pos];
                        }
                }
            } else {
                Mob questBoss;
                String txt_quest = Messages.get(this, quest.getMessageString()+"_1", Messages.titleCase(Dungeon.hero.name()));

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
						GameScene.show( new WndQuest( Ghost.this, txt_quest ) );
					}
				});
			} else GLog.n(Messages.get(this, "no_boss_warning"));

		}}

		return true;
	}

    @Override
    public void place(RegularLevel level, List<Room> rooms) {
		Room exit = findExittzz;
		if(exit != null) {
			boolean validPos;
			//spawn along the border, but not on the exit, a trap, or in front of a door
			do {
				validPos = true;
				Point point = new Point();
				if (Random.Int(2) == 0) {
					point.x = Random.Int(2) == 0 ? exit.left + 1 : exit.right - 1;
					point.y = Random.IntRange(exit.top + 1, exit.bottom - 1);
				} else {
					point.x = Random.IntRange(exit.left + 1, exit.right - 1);
					point.y = Random.Int(2) == 0 ? exit.top + 1 : exit.bottom - 1;
				}
				pos = level.pointToCell(point);
				if (pos == level.exit()) {
					validPos = false;
				}
				for (Point door : exit.connected.values()) {
					if (level.trueDistance(pos, level.pointToCell(door)) <= 1) {
						validPos = false;
					}
				}
				if (level.traps.get(pos) != null) {
					validPos = false;
				}
			} while (!validPos);
		}else{
			tzz
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