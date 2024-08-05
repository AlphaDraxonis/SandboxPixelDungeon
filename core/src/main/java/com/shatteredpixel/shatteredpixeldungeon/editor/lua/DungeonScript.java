/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
 *
 * Sandbox Pixel Dungeon
 * Copyright (C) 2023-2024 AlphaDraxonis
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

package com.shatteredpixel.shatteredpixeldungeon.editor.lua;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;

public class DungeonScript {

	//***************
	//**** ITEMS ****
	//***************

	@KeepProguard
	public boolean onExecuteItem(Item item, Hero hero) {
		if (item.defaultAction() != null) return onExecuteItem(item, hero, item.defaultAction());
		return true;//false = cancel execution
	}

	@KeepProguard
	public boolean onExecuteItem(Item item, Hero hero, String action) {
		return true;//false = cancel execution
	}

	//Only for Lua
	@KeepProguard
	public void onItemCollected(Item item) {
	}

	//blocked items can not appear in game by any means
	@KeepProguard
	public boolean isItemBlocked(Item item) {
		if (Dungeon.isChallenged(Challenges.NO_HERBALISM) && item instanceof Dewdrop){
			return true;
		}

		return false;
	}

}