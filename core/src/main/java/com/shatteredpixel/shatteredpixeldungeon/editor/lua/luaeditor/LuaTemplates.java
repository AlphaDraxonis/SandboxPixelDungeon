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
import com.shatteredpixel.shatteredpixeldungeon.customobjects.LuaManager;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ChooseOneInCategoriesBody;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.WndChooseOneInCategories;
import com.shatteredpixel.shatteredpixeldungeon.items.Stylus;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.NotAllowedInLua;
import com.watabou.idewindowactions.LuaScript;
import com.watabou.utils.Consumer;

import java.util.ArrayList;
import java.util.List;

@NotAllowedInLua
public class LuaTemplates {

	private static final LuaScript KILL_HERO_ON_DIE, SPAWN_MOB_ON_DIE, CRYSTAL_GUARDIAN_RECOVERY, RANGED_ATTACK;

	private static final LuaScript REPLACES_WALLS_WITH_EMBERS;
	private static final LuaScript INSCRIBE_LOOT_TABLE;
	private static final LuaScript SET_CURSED_EFFECTS;

	private static final LuaScript[] TEMPLATES;

	static {
		KILL_HERO_ON_DIE = new LuaScript(Mob.class, "When this mob dies, the hero also dies.");
		KILL_HERO_ON_DIE.code = "vars = {} static = {} function die(this, vars, cause)hero:die(this);\nthis:super_die(cause); end\n" +
				"\n" +
				"\n" +
				"return {\n" +
				"    vars = vars; static = static; die = die;\n" +
				"}";

		SPAWN_MOB_ON_DIE = new LuaScript(Mob.class, "When this mob dies, a wraith (or another mob) is spawned in its place.");
		SPAWN_MOB_ON_DIE.code = "vars = {} static = {} function die(this, vars, cause)this:super_die(cause);\n" +
				"\n" +
				"local mob = new(\"Wraith\");\n" +
				"\n" +
				"--affectBuff(mob, new(\"Corruption\")); -- uncomment to add this buff\n" +
				"\n" +
				"placeMob(mob, this.pos);\n" +
				"\n" +
				"mob:spend(1); --so it doesn't act immediately\n" +
				"end\n" +
				"\n" +
				"\n" +
				"return {\n" +
				"    vars = vars; static = static; die = die;\n" +
				"}";

		CRYSTAL_GUARDIAN_RECOVERY = new LuaScript(Mob.class, "Instead of dying of HP drops to 0, this mobs gains HP like a crystal guardian");
		CRYSTAL_GUARDIAN_RECOVERY.code = "vars = {\n" +
				"recovering = false, gainHpPerTurn = 1\n" +
				"}\n" +
				"\n" +
				"function defenseSkill(this, vars, enemy)\n" +
				"if vars.recovering then return 0; end\n" +
				"return this:super_defenseSkill(enemy);\n" +
				"end\n" +
				"\n" +
				"function surprisedBy(this, vars, enemy, attacking)\n" +
				"if vars.recovering then return true; end\n" +
				"return this:super_surprisedBy(enemy, attacking);\n" +
				"end\n" +
				"\n" +
				"function act(this, vars)\n" +
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
				"else return this:super_act();\n" +
				"\n" +
				"\n" +
				"end\n" +
				"end\n" +
				"\n" +
				"\n" +
				"function isAlive(this, vars) \n" +
				"\n" +
				"if this.HP <= 0 then\n" +
				"this.HP = 1;\n" +
				"vars.recovering = true;\n" +
				"affectBuff(this, class(\"Blessed\"));\n" +
				"end\n" +
				"return true;\n" +
				"end" +
				"\n" +
				"\n" +
				"return {\n" +
				"    vars = vars; static = static; defenseSkill = defenseSkill; surprisedBy = surprisedBy; act = act; isInvulnerable = isInvulnerable; isAlive = isAlive;\n" +
				"}";

		RANGED_ATTACK = new LuaScript(Mob.class, "Adds a ranged attack to (melee) mobs.\nSee Additional code to change the animation.");
		RANGED_ATTACK.code = "function canAttack(this, vars, enemy)\n" +
				"return this:super_canAttack(enemy)\n" +
				"or ballistica(this.pos, enemy.pos, Ballistica.REAL_MAGIC_BOLT, nil).collisionPos == enemy.pos;\n" +
				"end\n" +
				"\n" +
				"function doAttack(this, vars, enemy)\n" +
				"if level:adjacent( this.pos, enemy.pos )\n" +
				"or ballistica(this.pos, enemy.pos, Ballistica.REAL_MAGIC_BOLT, nil).collisionPos ~= enemy.pos\n" +
				"then\n" +
				"    return this:super_doAttack(enemy);\n" +
				"end\n" +
				"\n" +
				"if this.sprite ~= null\n" +
				" and (this.sprite.visible or enemy.sprite.visible) then\n" +
				"    this.sprite:zap( enemy.pos );\n" +
				"    return false;\n" +
				"else\n" +
				"    this:zap();\n" +
				"\t\t\t\treturn true;\n" +
				"end;\n" +
				"end\n" +
				"\n" +
				"function zap(this, vars)\n" +
				"    --Zaps.attack(this, this.enemy, damage); -- specific amount of damage\n" +
				"    --Zaps.attack(this, this.enemy, damageMin, damageMax); -- parameters for damage roll\n" +
				"    --Zaps.attack(this, this.enemy, isMagicAttack); -- if it is a magic attack\n" +
				"    Zaps.attack(this, this.enemy); -- combine these up to 3 parameters in any order or just don't use any\n" +
				"end\n" +
				"\n" +
				"\n" +
				"function playZapAnim(this, vars, target)\n" +
				"     -- change attack animation here (mob class name in camelCase)\n" +
				"    Zaps.warlock(this.sprite.parent, this.sprite, target, this);\n" +
				"end\n" +
				"\n" +
				"\n" +
				"return {\n" +
				"    vars = vars; static = static; canAttack = canAttack; doAttack = doAttack; playZapAnim = playZapAnim; \n" +
				"}";


		REPLACES_WALLS_WITH_EMBERS = new LuaScript(Level.class, "When first entering, 50% of all walls are replaced with embers.");
		REPLACES_WALLS_WITH_EMBERS.code = "vars = {} static = {} function initForPlay(this, vars) this:super_initForPlay();\n" +
				"\n" +
				"-- btw, it is very important that you don't try accessing this using 'level', instead use 'this'\n" +
				"Random.pushGenerator(Random.levelSeed());\n" +
				"local length = this:length();\n" +
				"    for pos = 0, length - 1 do\n" +
				"        if Random.int(2) == 0 and Arrays.get(this.map, pos) == Terrain.WALL then\n" +
				"            this:setTerrain(pos, Terrain.EMBERS);\n" +
				"        end\n" +
				"    end\n" +
				"\n" +
				"updateMap();\n" +
				"\n" +
				"Random.popGenerator();" +
				"\nend\n" +
				"\n" +
				"\n" +
				"return {\n" +
				"    vars = vars; static = static; initForPlay = initForPlay;\n" +
				"}";
		
		INSCRIBE_LOOT_TABLE = new LuaScript(Stylus.class, "Select a custom loot table.");
		INSCRIBE_LOOT_TABLE.code = "vars = {} static = {} function createGlyphToInscribe(this, vars, armor)" +
				"local list = new(\"List\");\n" +
				
				"\n" +
				"--add an element multiple times to increase the odds!\n" +
				
				"\n" +
				"--enchantments\n" +
				"list:add(\"Affection\");\n" +
				"list:add(\"AntiMagic\");\n" +
				"list:add(\"Brimstone\");\n" +
				"list:add(\"Camouflage\");\n" +
				"list:add(\"Entanglement\");\n" +
				"list:add(\"Flow\");\n" +
				"list:add(\"Obfuscation\");\n" +
				"list:add(\"Potential\");\n" +
				"list:add(\"Repulsion\");\n" +
				"list:add(\"Stone\");\n" +
				"list:add(\"Swiftness\");\n" +
				"list:add(\"Thorns\");\n" +
				"list:add(\"Viscosity\");\n" +
				
				"\n" +
				"--curses\n" +
				"--list:add(\"AntiEntropy\");\n" +
				"--list:add(\"Corrosion\");\n" +
				"--list:add(\"Displacement\");\n" +
				"--list:add(\"Metabolism\");\n" +
				"--list:add(\"Multiplicity\");\n" +
				"--list:add(\"Overgrowth\");\n" +
				"--list:add(\"Stench\");\n" +
				
				"\n" +
				"--should roll a different glyph\n" +
				"if armor.glyph ~= nil then\n" +
				"    list:removeAll(armor.glyph.simpleClassName);\n" +
				"end\n" +
				"\n" +
				
				"local glyphClassName = Random.element(list);\n" +
				"return new(glyphClassName);\n" +
				"end\n" +
				"\n" +
				"\n" +
				"return {\n" +
				"    vars = vars; static = static; createGlyphToInscribe = createGlyphToInscribe;\n" +
				"}";
		
		SET_CURSED_EFFECTS = new LuaScript(Wand.class, "Select which curse effects can be used by the wand.");
		SET_CURSED_EFFECTS.code = "vars = {} static = {} function cursedEffect(this, vars, user, bolt, positiveOnly)" +
				
				"\n" +
				"--add an element multiple times to increase the odds!\n" +
				
				"\n" +
				"--common (60% chance)\n" +
				"local commons = new(\"List\");\n" +
				"commons:add(\"BurnAndFreeze\");\n" +
				"commons:add(\"SpawnRegrowth\");\n" +
				"commons:add(\"RandomTeleport\");\n" +
				"commons:add(\"RandomGas\");\n" +
				"commons:add(\"RandomAreaEffect\");\n" +
				"commons:add(\"Bubbles\");\n" +
				"commons:add(\"RandomWand\");\n" +
				"commons:add(\"SelfOoze\");\n" +
				
				"\n" +
				"--uncommon (30% chance)\n" +
				"local uncommons = new(\"List\");\n" +
				"uncommons:add(\"RandomPlant\");\n" +
				"uncommons:add(\"HealthTransfer\");\n" +
				"uncommons:add(\"Explosion\");\n" +
				"uncommons:add(\"LightningBolt\");\n" +
				"uncommons:add(\"Geyser\");\n" +
				"uncommons:add(\"SummonSheep\");\n" +
				"uncommons:add(\"Levitate\");\n" +
				"uncommons:add(\"Alarm\");\n" +
				
				"\n" +
				"--rare (9% chance)\n" +
				"local rares = new(\"List\");\n" +
				"rares:add(\"SheepPolymorph\");\n" +
				"rares:add(\"CurseEquipment\");\n" +
				"rares:add(\"InterFloorTeleport\");\n" +
				"rares:add(\"SummonMonsters\");\n" +
				"rares:add(\"FireBall\");\n" +
				"rares:add(\"ConeOfColors\");\n" +
				"rares:add(\"MassInvuln\");\n" +
				"rares:add(\"Petrify\");\n" +
				
				"\n" +
				"--very rare (1% chance)\n" +
				"local veryRares = new(\"List\");\n" +
				"veryRares:add(\"ForestFire\");\n" +
				"veryRares:add(\"SpawnGoldenMimic\");\n" +
				"veryRares:add(\"AbortRetryFail\");\n" +
				"veryRares:add(\"RandomTransmogrify\");\n" +
				"veryRares:add(\"HeroShapeShift\");\n" +
				"veryRares:add(\"SuperNova\");\n" +
				"veryRares:add(\"SinkHole\");\n" +
				"veryRares:add(\"GravityChaos\");\n" +
				
				"\n" +
				"local roll = Random.int(100)\n" +
				"local list;\n" +
				"if roll < 60 then\n" +
				"    list = commons;\n" +
				"elseif roll < 90 then\n" +
				"    list = uncommons;\n" +
				"elseif roll < 99 then\n" +
				"    list = rares;\n" +
				"else\n" +
				"    list = veryRares;\n" +
				"end\n" +
				"\n" +
				
				"local effect;\n" +
				"repeat\n" +
				"    effect = new( Random.element(list) )\n" +
				"until effect:valid(this, user, bolt, positiveOnly)\n" +
				"\n" +
				"return effect;\n" +
				"end\n" +
				"\n" +
				"return {\n" +
				"    vars = vars; static = static; createGlyphToInscribe = createGlyphToInscribe;\n" +
				"}";

		TEMPLATES = new LuaScript[]{KILL_HERO_ON_DIE, SPAWN_MOB_ON_DIE, CRYSTAL_GUARDIAN_RECOVERY, RANGED_ATTACK,
				REPLACES_WALLS_WITH_EMBERS, INSCRIBE_LOOT_TABLE, SET_CURSED_EFFECTS};
	}

	private static String name(LuaScript script) {
		if (script == KILL_HERO_ON_DIE) return Messages.get(LuaTemplates.class, "kill_hero_death_name");
		if (script == SPAWN_MOB_ON_DIE) return Messages.get(LuaTemplates.class, "spawn_mob_on_die_name");
		if (script == CRYSTAL_GUARDIAN_RECOVERY) return Messages.get(LuaTemplates.class, "crystal_guardian_recovery_name");
		if (script == RANGED_ATTACK) return Messages.get(LuaTemplates.class, "ranged_attack_name");
		if (script == REPLACES_WALLS_WITH_EMBERS) return Messages.get(LuaTemplates.class, "replaces_walls_with_embers_name");
		if (script == INSCRIBE_LOOT_TABLE) return Messages.get(LuaTemplates.class, "inscribe_loot_table_name");
		if (script == SET_CURSED_EFFECTS) return Messages.get(LuaTemplates.class, "set_cursed_effects_name");
		return Messages.NO_TEXT_FOUND;
	}

	private static String desc(LuaScript script) {
		if (script == KILL_HERO_ON_DIE) return Messages.get(LuaTemplates.class, "kill_hero_death_desc");
		if (script == SPAWN_MOB_ON_DIE) return Messages.get(LuaTemplates.class, "spawn_mob_on_die_desc");
		if (script == CRYSTAL_GUARDIAN_RECOVERY) return Messages.get(LuaTemplates.class, "crystal_guardian_recovery_desc");
		if (script == RANGED_ATTACK) return Messages.get(LuaTemplates.class, "ranged_attack_desc");
		if (script == REPLACES_WALLS_WITH_EMBERS) return Messages.get(LuaTemplates.class, "replaces_walls_with_embers_desc");
		if (script == INSCRIBE_LOOT_TABLE) return Messages.get(LuaTemplates.class, "inscribe_loot_table_desc");
		if (script == SET_CURSED_EFFECTS) return Messages.get(LuaTemplates.class, "set_cursed_effects_desc");
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
				Messages.get(LuaTemplates.class, "choose_template_title"), Messages.get(LuaTemplates.class, "choose_template_body"),
				twoDArray, new String[]{Messages.get(LuaTemplates.class, "templates")}) {
			@Override
			protected ChooseOneInCategoriesBody.BtnRow[] createCategoryRows(Object[] category) {
				ChooseOneInCategoriesBody.BtnRow[] ret = new ChooseOneInCategoriesBody.BtnRow[category.length];
				for (int i = 0; i < ret.length; i++) {
					LuaScript script = (LuaScript) category[i];
					ret[i] = new ChooseOneInCategoriesBody.BtnRow(name(script), desc(script), LuaManager.scriptSprite(script)) {
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

			@Override
			public void hide() {
				super.hide();
				onSelect.accept(null);
			}
		});
	}
}
