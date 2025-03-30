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

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Wandmaker;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CorpseDust;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Embers;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Rotberry;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;

public class WndWandmaker extends WndReward {

	public WndWandmaker( final Wandmaker wandmaker, final Item item ) {
		
		super();

		String msg = item instanceof CorpseDust    ? Messages.get(this, "dust") :
					 item instanceof Embers 	   ? Messages.get(this, "ember") :
					 item instanceof Rotberry.Seed ? Messages.get(this, "berry") :
					 Messages.NO_TEXT_FOUND;

		initComponents(
				new IconTitle(new ItemSprite(item, null), Messages.titleCase(item.name())),
				new SingleItemRewardsBody(msg, wandmaker, item, wandmaker.quest.wand1, wandmaker.quest.wand2) {
					@Override
					protected void onSelectReward(Item reward) {
						reward.identify(false);
						wandmaker.quest.complete();
					}
				}, null);
	}

}
