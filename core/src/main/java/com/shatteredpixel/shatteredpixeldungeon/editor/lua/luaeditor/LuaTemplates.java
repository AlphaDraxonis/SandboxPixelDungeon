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
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Consumer;

import java.util.ArrayList;
import java.util.List;

public class LuaTemplates {

	private static final LuaScript KILL_HERO_ON_DIE, SPAWN_MOB_ON_DIE, CRYSTAL_GUARDIAN_RECOVERY;

	private static final LuaScript REPLACES_WALLS_WITH_EMBERS;

	private static final LuaScript[] TEMPLATES;

	static {
		KILL_HERO_ON_DIE = new LuaScript(Mob.class, "When this mob dies, the hero also dies.", "");
		KILL_HERO_ON_DIE.code = "vars = {} static = {} function die(this, vars, super, cause) hero:die(this);\nsuper:call(cause); end" +
				"\n\nreturn {vars = vars; static = static; die = die}";

		SPAWN_MOB_ON_DIE = new LuaScript(Mob.class, "When this mob dies, a wraith (or another mob) is spawned in its place.", "");
		SPAWN_MOB_ON_DIE.code = "vars = {} static = {} function die(this, vars, super, cause) hero:die(this);\nsuper:call(cause);\n" +
				"\n" +
				"mob = new(\"Wraith\");\n" +
				"\n" +
				"--affectBuff(mob, new(\"Corruption\")); -- uncomment to add this buff\n" +
				"\n" +
				"placeMob(mob, this.pos);\n" +
				"\n" +
				"mob:spend(1); --so it doesn't act immediately end" +
				"\n\nreturn {vars = vars; static = static; die = die}";

		CRYSTAL_GUARDIAN_RECOVERY = new LuaScript(Mob.class, "Instead of dying of HP drops to 0, this mobs gains HP like a crystal guardian", "");
		CRYSTAL_GUARDIAN_RECOVERY.code = "vars = {\n" +
				"recovering = false, gainHpPerTurn = 1\n" +
				"}\n" +
				"\n" +
				"function defenseSkill(this, vars, super, enemy)\n" +
				"if vars.recovering then return 0; end\n" +
				"return super:call(enemy);\n" +
				"end\n" +
				"\n" +
				"function surprisedBy(this, vars, super, enemy, attacking)\n" +
				"if vars.recovering then return true; end\n" +
				"return super:call(enemy, attacking);\n" +
				"end\n" +
				"\n" +
				"function act(this, vars, super)\n" +
				"if vars.recovering then\n" +
				"\n" +
				"    this.HP = this.HP + vars.gainHpPerTurn;\n" +
				"\n" +
				"if Arrays.get(level.heroFOV, this.pos) then\n" +
				"\t\t\t\tthis.sprite:showStatusWithIcon(0x00FF00, vars.gainHpPerTurn, 18, {});\n" +
				"\t\t\tend\n" +
				"\n" +
				"    if this.HP >= this.HT then\n" +
				"		 this:throwItems();\n" +
				"        this.HP = this.HT;\n" +
				"\n" +
				"        vars.recovering = false;\n" +
				"\tthis:buff(class(\"Blessed\")):detach();\n" +
				"\n" +
				"    end\n" +
				"\n" +
				"this:spend(1);\n" +
				"return true;\n" +
				"\n" +
				"else return super:call();\n" +
				"\n" +
				"\n" +
				"end\n" +
				"end\n" +
				"\n" +
				"\n" +
				"function isAlive(this, vars, super) \n" +
				"\n" +
				"if this.HP <= 0 then\n" +
				"this.HP = 1;\n" +
				"vars.recovering = true;\n" +
				"affectBuff(this, class(\"Blessed\"));\n" +
				"end\n" +
				"return true;\n" +
				"end\n" +
				"return {\n" +
				"    vars = vars; static = static; defenseSkill = defenseSkill; surprisedBy = surprisedBy; act = act; isInvulnerable = isInvulnerable; isAlive = isAlive;\n" +
				"}";


		REPLACES_WALLS_WITH_EMBERS = new LuaScript(Level.class, "When first entering, 50% of all walls are replaced with embers.", "");
		REPLACES_WALLS_WITH_EMBERS.code = "vars = {} static = {} function initForPlay(this, vars, super) super:call();\n" +
				"\n" +
				"-- btw, it is very important that you don't try accessing this using 'level', instead use 'this'\n\n" +
				"Random.pushGenerator(Random.levelSeed());\n" +
				"local embers = 9; -- see constant in Terrain.java\n" +
				"local wall = 4; -- see constant in Terrain.java\n" +
				"local length = this:length();\n" +
				"    for pos = 0, length - 1 do\n" +
				"        if Random.int(2) == 0 and Arrays.get(this.map, pos) == wall then\n" +
				"            this:setTerrain(pos, embers);\n" +
				"        end\n" +
				"    end\n" +
				"\n" +
				"updateMap();\n" +
				"\n" +
				"Random.popGenerator();" +
				"\nend" +
				"\n\nreturn {vars = vars; static = static; initForPlay = initForPlay}";

		TEMPLATES = new LuaScript[]{KILL_HERO_ON_DIE, SPAWN_MOB_ON_DIE,
				REPLACES_WALLS_WITH_EMBERS};
	}

	private static String name(LuaScript script) {
		if (script == KILL_HERO_ON_DIE) return Messages.get(LuaTemplates.class, "kill_hero_death_name");
		if (script == REPLACES_WALLS_WITH_EMBERS) return Messages.get(LuaTemplates.class, "replaces_walls_with_embers_name");
		return Messages.NO_TEXT_FOUND;
	}

	private static String desc(LuaScript script) {
		if (script == KILL_HERO_ON_DIE) return Messages.get(LuaTemplates.class, "kill_hero_death_desc");
		if (script == REPLACES_WALLS_WITH_EMBERS) return Messages.get(LuaTemplates.class, "replaces_walls_with_embers_name");
		return Messages.NO_TEXT_FOUND;
	}

	public static void show(Consumer<LuaScript> onSelect, Class<?> useFor) {
		List<LuaScript> available = new ArrayList<>();
		for (int i = 0; i < TEMPLATES.length; i++) {
			if (TEMPLATES[i].type.isAssignableFrom(useFor)) available.add(TEMPLATES[i]);
		}
		LuaScript[][] twoDArray = new LuaScript[1][];
		twoDArray[0] = available.toArray(new LuaScript[0]);
		EditorScene.show(new WndChooseOneInCategories(
				Messages.get(LuaTemplates.class, "choose_template_title"), Messages.get(LuaTemplates.class, "choose_template_desc"),
				twoDArray, new String[]{Messages.get(LuaTemplates.class, "templates")}) {
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