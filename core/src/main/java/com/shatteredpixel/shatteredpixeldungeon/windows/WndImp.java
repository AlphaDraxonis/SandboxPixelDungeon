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

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DwarfToken;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;

public class WndImp extends WndReward {

	public WndImp( final Imp imp, final DwarfToken tokens ) {
		
		super();

		initComponents(
				new IconTitle( new ItemSprite( tokens, null ), Messages.titleCase( tokens.name() ) ),
				new SingleItemRewardsBody( Messages.get(this, "message"), imp, tokens, imp.quest.reward ) {
					@Override
					protected void onSelectReward(Item reward) {
						reward.identify(false);
					}

					@Override
					protected void makeQuestInitiatorDisappear() {
						imp.flee();
						imp.quest.complete();
					}
				}, null);
	}
}