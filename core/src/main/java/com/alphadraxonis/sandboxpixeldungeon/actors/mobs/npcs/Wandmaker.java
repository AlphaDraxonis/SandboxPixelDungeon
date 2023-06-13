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
import com.alphadraxonis.sandboxpixeldungeon.editor.other.WandmakerQuest;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.items.quest.CorpseDust;
import com.alphadraxonis.sandboxpixeldungeon.items.quest.Embers;
import com.alphadraxonis.sandboxpixeldungeon.journal.Notes;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.plants.Rotberry;
import com.alphadraxonis.sandboxpixeldungeon.scenes.GameScene;
import com.alphadraxonis.sandboxpixeldungeon.sprites.WandmakerSprite;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndQuest;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndWandmaker;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;

public class Wandmaker extends NPC {

	{
		spriteClass = WandmakerSprite.class;

		properties.add(Property.IMMOVABLE);
	}

	public WandmakerQuest quest;//FIXME WARNING getCopy() will not work for quest type 2!!!! (bc candles)

	public Wandmaker() {
	}

	public Wandmaker(LevelScheme levelScheme) {
		quest = WandmakerQuest.createRandom(levelScheme);
	}

	@Override
	protected boolean act() {
		if (Dungeon.hero.buff(AscensionChallenge.class) != null){
			die(null);
			return true;
		}
		if (quest != null && Dungeon.level.visited[pos] && quest.wand1 != null) {
			Notes.add( Notes.Landmark.WANDMAKER );
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
		sprite.turnTo(pos, Dungeon.hero.pos);

		if (c != Dungeon.hero) {
			return true;
		}

		if (quest != null) {
			if (quest.given) {

				Item item;
				switch (quest.type()) {
					case 1:
					default:
						item = Dungeon.hero.belongings.getItem(CorpseDust.class);
						break;
					case 2:
						item = Dungeon.hero.belongings.getItem(Embers.class);
						break;
					case 3:
						item = Dungeon.hero.belongings.getItem(Rotberry.Seed.class);
						break;
				}

				if (item != null) {
					Game.runOnRenderThread(new Callback() {
						@Override
						public void call() {
							GameScene.show(new WndWandmaker(Wandmaker.this, item));
						}
					});
				} else {
					String msg;
					switch (quest.type()) {
						case 1:
						default:
							msg = Messages.get(this, "reminder_dust", Messages.titleCase(Dungeon.hero.name()));
							break;
						case 2:
							msg = Messages.get(this, "reminder_ember", Messages.titleCase(Dungeon.hero.name()));
							break;
						case 3:
							msg = Messages.get(this, "reminder_berry", Messages.titleCase(Dungeon.hero.name()));
							break;
					}
					Game.runOnRenderThread(new Callback() {
						@Override
						public void call() {
							GameScene.show(new WndQuest(Wandmaker.this, msg));
						}
					});
				}

			} else {

				String msg1 = "";
				String msg2 = "";
				switch (Dungeon.hero.heroClass) {
					case WARRIOR:
						msg1 += Messages.get(this, "intro_warrior");
						break;
					case ROGUE:
						msg1 += Messages.get(this, "intro_rogue");
						break;
					case MAGE:
						msg1 += Messages.get(this, "intro_mage", Messages.titleCase(Dungeon.hero.name()));
						break;
					case HUNTRESS:
						msg1 += Messages.get(this, "intro_huntress");
						break;
					case DUELIST:
						msg1 += Messages.get(this, "intro_duelist");
						break;
				}

				msg1 += Messages.get(this, "intro_1");

				switch (quest.type()) {
					case 1:
						msg2 += Messages.get(this, "intro_dust");
						break;
					case 2:
						msg2 += Messages.get(this, "intro_ember");
						break;
					case 3:
						msg2 += Messages.get(this, "intro_berry");
						break;
				}

				msg2 += Messages.get(this, "intro_2");
				final String msg1Final = msg1;
				final String msg2Final = msg2;

				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						GameScene.show(new WndQuest(Wandmaker.this, msg1Final) {
							@Override
							public void hide() {
								super.hide();
								GameScene.show(new WndQuest(Wandmaker.this, msg2Final));
							}
						});
					}
				});

				quest.given = true;
				Notes.add(Notes.Landmark.WANDMAKER);
			}
		}

		return true;
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
		if (bundle.contains(QUEST)) quest = (WandmakerQuest) bundle.get(QUEST);
	}
}