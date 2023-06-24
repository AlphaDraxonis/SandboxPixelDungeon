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
import com.alphadraxonis.sandboxpixeldungeon.editor.quests.ImpQuest;
import com.alphadraxonis.sandboxpixeldungeon.editor.quests.QuestNPC;
import com.alphadraxonis.sandboxpixeldungeon.items.quest.DwarfToken;
import com.alphadraxonis.sandboxpixeldungeon.journal.Notes;
import com.alphadraxonis.sandboxpixeldungeon.levels.RegularLevel;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.Room;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.GameScene;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ImpSprite;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndImp;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndQuest;
import com.watabou.noosa.Game;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;

import java.util.List;

public class Imp extends QuestNPC<ImpQuest> {

	{
		spriteClass = ImpSprite.class;

		properties.add(Property.IMMOVABLE);
	}
	
	private boolean seenBefore = false;

	public Imp() {
	}

	public Imp(ImpQuest quest) {
		super(quest);
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
				tell(Messages.get(this, quest.getMessageString(), Messages.titleCase(Dungeon.hero.name())));
			}

		} else {
			tell(Messages.get(this, quest.getMessageString()+"_1"));
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

	@Override
	public void place(RegularLevel level, List<Room> rooms) {
		do {
			pos = level.randomRespawnCell(this);
		} while (pos == -1 ||
				level.heaps.get(pos) != null ||
				level.traps.get(pos) != null ||
				level.findMob(pos) != null ||
				//The imp doesn't move, so he cannot obstruct a passageway
				!(level.passable[pos + PathFinder.CIRCLE4[0]] && level.passable[pos + PathFinder.CIRCLE4[2]]) ||
				!(level.passable[pos + PathFinder.CIRCLE4[1]] && level.passable[pos + PathFinder.CIRCLE4[3]]));
		if (pos != -1) level.mobs.add(this);
	}


	@Override
	public void createNewQuest() {
		quest = new ImpQuest();
	}
}