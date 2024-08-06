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

//This class stores all methods and tells names of parameters etc
//it also tells which are all displayed

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.DungeonScript;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;

import java.lang.reflect.Method;
import java.util.*;

public final class LuaMethodManager implements Comparable<LuaMethodManager> {

	private LuaMethodManager(int sortingRank, Method method, String... paramNames) {
		this.sortingRank = sortingRank;
		this.method = method;
		this.paramNames = paramNames;
	}

	private final int sortingRank;
	public final Method method;
	public final String[] paramNames;

	@Override
	public int compareTo(LuaMethodManager o) {
		return sortingRank - o.sortingRank;
	}


	public static void init() {

	}

	private static final Map<String, LuaMethodManager> methods = new HashMap<>();

	static {

		//Mob.class
		try {
			addMethod(1, Mob.class.getDeclaredMethod("attackSkill", Char.class), "target");//accuracy
			addMethod(2, Mob.class.getMethod("defenseSkill", Char.class), "enemy");//evasion
			addMethod(3, Mob.class.getMethod("defenseVerb"));//what to say if it evades an attack
			addMethod(4, Mob.class.getMethod("speed"));//movement speed
			addMethod(5, Mob.class.getMethod("attackDelay"));//attack speed (result > 1 -> slower)
			addMethod(6, Mob.class.getMethod("drRoll"));//armor
			addMethod(7, Mob.class.getMethod("damageRoll"));//attack damage

//			addMethod(11, Mob.class.getDeclaredMethod("spawningWeight"));
			addMethod(12, Mob.class.getDeclaredMethod("lootChance"));
			addMethod(13, Mob.class.getMethod("hitSound", float.class), "pitch");

			addMethod(21, Mob.class.getDeclaredMethod("heroShouldInteract"));
			addMethod(22, Mob.class.getMethod("interact", Char.class), "ch");

			addMethod(26, Mob.class.getMethod("notice"));

			addMethod(31, Mob.class.getDeclaredMethod("canAttack", Char.class), "enemy");
			addMethod(32, Mob.class.getDeclaredMethod("doAttack", Char.class), "enemy");
			addMethod(33, Mob.class.getDeclaredMethod("damage", int.class, Object.class), "dmg", "source");//when taking damage
			addMethod(34, Mob.class.getMethod("attack", Char.class, float.class, float.class, float.class), "enemy", "dgmMulti", "dmgBonus", "accMulti");//complicated attack logic, only modify input parameters!
			addMethod(35, Char.class.getDeclaredMethod("zap"));
			addMethod(36, Mob.class.getMethod("attackProc", Char.class, int.class), "enemy", "damage");
			addMethod(37, Mob.class.getMethod("defenseProc", Char.class, int.class), "enemy", "damage");
			addMethod(38, Mob.class.getMethod("surprisedBy", Char.class, boolean.class), "enemy", "attacking");//attacking is always true unless the Masterthieves thing was used

			addMethod(51, Mob.class.getDeclaredMethod("onAdd"));
			addMethod(52, Mob.class.getDeclaredMethod("die", Object.class), "cause");
			addMethod(53, Mob.class.getDeclaredMethod("destroy"));

			addMethod(61, Mob.class.getMethod("move", int.class, boolean.class), "step", "traveling");//travelling may be false when a character is moving instantaneously, such as via teleportation
			addMethod(62, Mob.class.getDeclaredMethod("randomDestination"));//where to walk to
			addMethod(63, Mob.class.getDeclaredMethod("cellIsPathable", int.class), "cell");//whether it can walk onto a passable cell
			addMethod(64, Mob.class.getMethod("beckon", int.class), "cell");//makes the mob walk torwards to that cell, e.g. called by alarming trap
			addMethod(65, Mob.class.getMethod("aggro", Char.class), "ch");//will target ch
			addMethod(66, Mob.class.getDeclaredMethod("chooseEnemyImpl"));//use to set to a certain enemy, return null if there is no enemy
			addMethod(67, Mob.class.getMethod("clearEnemy"));//forgets current enemy
			addMethod(68, Mob.class.getMethod("restoreEnemy"));//restore any actors if only id was stored

			addMethod(81, Mob.class.getDeclaredMethod("act"));
			addMethod(82, Mob.class.getMethod("info"));
			addMethod(83, Mob.class.getMethod("add", Buff.class), "buff");
			addMethod(84, Mob.class.getMethod("remove", Buff.class), "buff");
			addMethod(85, Mob.class.getMethod("isImmune", Class.class), "effect");
			addMethod(86, Mob.class.getMethod("isInvulnerable", Class.class), "effect");
			addMethod(87, Char.class.getDeclaredMethod("spend", float.class), "time");



			//Level.class
//			addMethod(1401, Level.class.getMethod("create"));
			addMethod(1402, Level.class.getMethod("initForPlay"));

			addMethod(1411, Level.class.getMethod("randomRespawnCell", Char.class, boolean.class), "ch", "guarantee");
			addMethod(1412, Level.class.getDeclaredMethod("isValidSpawnCell", Char.class, int.class), "ch", "cell");
			addMethod(1413, Level.class.getMethod("fallCell", boolean.class, String.class), "fallIntoPit", "destZone");

			addMethod(1421, Level.class.getMethod("occupyCell", Char.class), "ch");

			addMethod(1431, Level.class.getMethod("plant", Plant.Seed.class, int.class), "seed", "pos");
			addMethod(1432, Level.class.getMethod("uproot", int.class), "pos");
			addMethod(1433, Level.class.getMethod("setTrap", Trap.class, int.class), "trap", "pos");
			addMethod(1434, Level.class.getMethod("disarmTrap", int.class, boolean.class), "pos", "reveal");



			//DungeonScript.class
			addMethod(10000, DungeonScript.class.getMethod("onItemCollected", Item.class), "item");
			addMethod(10001, DungeonScript.class.getMethod("executeItem", Item.class, Hero.class, String.class, DungeonScript.Executer.class), "item", "user", "action", "executer");

			addMethod(10010, DungeonScript.class.getMethod("isItemBlocked", Item.class), "item");

			addMethod(10020, DungeonScript.class.getMethod("onEarnXP", int.class, Class.class), "amount", "source");
			addMethod(10021, DungeonScript.class.getMethod("onLevelUp"));


		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}


	}


	private static void addMethod(int sortingRank, Method method, String... paramNames) {
		methods.put(method.getName(), new LuaMethodManager(sortingRank, method, paramNames));
	}


	public static List<LuaMethodManager> getAllMethodsInOrder(Class<?> clazz) {
		List<LuaMethodManager> result = new ArrayList<>();
		Set<String> alreadyAdded = new HashSet<>();
		List<LuaMethodManager> newlyAddTemp = new LinkedList<>();

		do {
			addMethodsForClass(clazz, result, alreadyAdded, newlyAddTemp);
			clazz = clazz.getSuperclass();
		} while (clazz != null);

		return result;
	}

	private static void addMethodsForClass(Class<?> clazz, List<LuaMethodManager> result, Set<String> alreadyAdded, List<LuaMethodManager> newlyAddTemp) {
		for (Method m : clazz.getDeclaredMethods()) {
			String name = m.getName();
			if (!alreadyAdded.contains(name)) {
				LuaMethodManager obj = methods.get(name);
				if (obj != null) {
					newlyAddTemp.add(obj);
					alreadyAdded.add(name);
				}
			}
		}
		Collections.sort(newlyAddTemp);
		result.addAll(newlyAddTemp);
		newlyAddTemp.clear();
	}

}