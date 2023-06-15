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
import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.actors.Char;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.AscensionChallenge;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.FetidRat;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.GnollTrickster;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.GreatCrab;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Mob;
import com.alphadraxonis.sandboxpixeldungeon.editor.quests.GhostQuest;
import com.alphadraxonis.sandboxpixeldungeon.editor.quests.QuestNPC;
import com.alphadraxonis.sandboxpixeldungeon.effects.CellEmitter;
import com.alphadraxonis.sandboxpixeldungeon.effects.Speck;
import com.alphadraxonis.sandboxpixeldungeon.journal.Notes;
import com.alphadraxonis.sandboxpixeldungeon.levels.RegularLevel;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.Room;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.GameScene;
import com.alphadraxonis.sandboxpixeldungeon.sprites.GhostSprite;
import com.alphadraxonis.sandboxpixeldungeon.utils.GLog;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndQuest;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndSadGhost;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;

import java.util.List;

public class Ghost extends QuestNPC<GhostQuest> {

    {
        spriteClass = GhostSprite.class;

        flying = true;

        state = WANDERING;
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
        if (quest != null) {
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
        return quest != null && quest.completed() ? 2f : 0.5f;
    }

    @Override
    protected Char chooseEnemy() {
        return null;
    }


    @Override
    public boolean interact(Char c) {
        sprite.turnTo(pos, c.pos);

        Sample.INSTANCE.play(Assets.Sounds.GHOST);

        if (c != Dungeon.hero) {
            return super.interact(c);
        }

        if (quest != null) {

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
                                switch (quest.type()) {
                                    case GhostQuest.RAT:
                                    default:
                                        GameScene.show(new WndQuest(Ghost.this, Messages.get(Ghost.this, "rat_2")));
                                        break;
                                    case GhostQuest.GNOLL:
                                        GameScene.show(new WndQuest(Ghost.this, Messages.get(Ghost.this, "gnoll_2")));
                                        break;
                                    case GhostQuest.CRAB:
                                        GameScene.show(new WndQuest(Ghost.this, Messages.get(Ghost.this, "crab_2")));
                                        break;
                                }
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
                String txt_quest;

                switch (quest.type()) {
                    case GhostQuest.RAT:
                    default:
                        questBoss = new FetidRat(this);
                        txt_quest = Messages.get(this, "rat_1", Messages.titleCase(Dungeon.hero.name()));
                        break;
                    case GhostQuest.GNOLL:
                        questBoss = new GnollTrickster(this);
                        txt_quest = Messages.get(this, "gnoll_1", Messages.titleCase(Dungeon.hero.name()));
                        break;
                    case GhostQuest.CRAB:
                        questBoss = new GreatCrab(this);
                        txt_quest = Messages.get(this, "crab_1", Messages.titleCase(Dungeon.hero.name()));
                        break;
                }

                questBoss.pos = Dungeon.level.randomRespawnCell(this, true);

                if (questBoss.pos != -1) {
                    GameScene.add(questBoss);
                    quest.start();
                    Game.runOnRenderThread(new Callback() {
                        @Override
                        public void call() {
                            GameScene.show(new WndQuest(Ghost.this, txt_quest));
                        }
                    });
                } else GLog.n(Messages.get(this, "no_boss_warning"));

            }
        }

        return true;
    }

    @Override
    public void place(RegularLevel level, List<Room> rooms) {
        int tries = level.length();
        do {
            pos = level.randomRespawnCell(this);
            tries--;
        } while (pos == -1 && tries > 0);
        if (pos != -1) level.mobs.add(this);
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
        if (bundle.contains(QUEST)) quest = (GhostQuest) bundle.get(QUEST);
    }
}