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
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import org.luaj.vm2.LuaValue;

public abstract class DungeonScript extends LuaCodeHolder {

	{
		clazz = DungeonScript.class;
	}

	//***************
	//**** ITEMS ****
	//***************

	@KeepProguard
	public final void executeItem(Item item, Hero hero, Executer doExecute) {
		executeItem(item, hero, item.defaultAction(), doExecute);
	}

	@KeepProguard
	public void executeItem(Item item, Hero hero, String action, Executer doExecute) {
		doExecute.execute(item, hero, action);
	}

	public static class Executer {
		protected void execute(Item item, Hero hero, String action) {
			item.execute(hero, action);
		}
	}

	@KeepProguard
	public boolean onItemCollected(Item item) {
		return true;//false = don't collect
	}

	//blocked items cannot appear in game by any means
	@KeepProguard
	public boolean isItemBlocked(Item item) {
		if (Dungeon.isChallenged(Challenges.NO_HERBALISM) && item instanceof Dewdrop){
			return true;
		}

		return false;
	}

	@KeepProguard
	public int onEarnXP(int amount, Class<?> source) {
		return amount;
	}

	@KeepProguard
	public void onLevelUp() {
	}

	final LuaValue currentScript() {
		return CustomDungeon.isEditing() ? null : script;
	}
}