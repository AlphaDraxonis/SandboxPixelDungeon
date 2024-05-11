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

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.GhostQuest;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.FetidRatSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GnollTricksterSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GreatCrabSprite;

public class WndSadGhost extends WndReward {

	public WndSadGhost( final Ghost ghost, final int type ) {
		
		super();

		GhostQuest quest = ghost.quest;

		String msg = Messages.get(this, quest.getMessageString()) + "\n\n" + Messages.get(this, "give_item");

		IconTitle titlebar = new IconTitle();
		switch (type){
			case GhostQuest.RAT:default:
				titlebar.icon( new FetidRatSprite() );
				break;
			case GhostQuest.GNOLL:
				titlebar.icon( new GnollTricksterSprite() );
				break;
			case GhostQuest.CRAB:
				titlebar.icon( new GreatCrabSprite());
				break;
		}
		titlebar.label( Messages.get(this, quest.getMessageString() + "_title") );

		initComponents(
				titlebar,
				new SingleItemRewardsBody(msg, ghost, null, quest.weapon, quest.armor) {
					@Override
					protected void onSelectReward(Item reward) {
						if (reward instanceof Weapon && quest.enchant != null){
							((Weapon) reward).enchant(quest.enchant);
						} else if (reward instanceof Armor && quest.glyph != null){
							((Armor) reward).inscribe(quest.glyph);
						}

						reward.identify(false);
						quest.complete();
					}
				}, null);
	}
}