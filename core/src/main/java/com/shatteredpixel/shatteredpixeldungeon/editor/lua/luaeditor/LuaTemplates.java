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

package com.shatteredpixel.shatteredpixeldungeon.editor.lua.luaeditor;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.LuaScript;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ChooseOneInCategoriesBody;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.WndChooseOneInCategories;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Consumer;

public class LuaTemplates {

	private static final LuaScript KILL_HERO_ON_DIE;

	private static final LuaScript[][] TEMPLATES;

	static {
		KILL_HERO_ON_DIE = new LuaScript(Mob.class, "Kills the hero when it dies", "");
		KILL_HERO_ON_DIE.code = "vars = {} static = {} function die(this, vars, super, cause) hero:die(this); super:call(cause); end" +
				"\n\nreturn {vars = vars; static = static; die = die}";

		TEMPLATES = new LuaScript[][]{{KILL_HERO_ON_DIE}};
	}

	private static String name(LuaScript script) {
		if (script == KILL_HERO_ON_DIE) {
			return Messages.get(LuaTemplates.class, "kill_hero_death_name");
		}
		return Messages.NO_TEXT_FOUND;
	}

	private static String desc(LuaScript script) {
		if (script == KILL_HERO_ON_DIE) {
			return Messages.get(LuaTemplates.class, "kill_hero_death_desc");
		}
		return Messages.NO_TEXT_FOUND;
	}

	public static void show(Consumer<LuaScript> onSelect) {
		EditorScene.show(new WndChooseOneInCategories(
				Messages.get(LuaTemplates.class, "choose_template_title"), Messages.get(LuaTemplates.class, "choose_template_desc"),
				TEMPLATES, new String[]{Messages.get(LuaTemplates.class, "templates")}) {
			@Override
			protected ChooseOneInCategoriesBody.BtnRow[] createCategoryRows(Object[] category) {
				ChooseOneInCategoriesBody.BtnRow[] ret = new ChooseOneInCategoriesBody.BtnRow[category.length];
				for (int i = 0; i < ret.length; i++) {
					LuaScript script = (LuaScript) category[i];
					ret[i] = new ChooseOneInCategoriesBody.BtnRow(name(script), desc(script), script.sprite()) {
						@Override
						protected void onClick() {
							finish();
							onSelect.accept(script.getCopy());
						}
					};
					ret[i].setLeftJustify(true);
				}
				return ret;
			}
		});
	}
}