/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
 *
 * Sandbox Pixel Dungeon
 * Copyright (C) 2023-2025 AlphaDraxonis
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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Thief;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.DungeonScript;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.KingsCrown;
import com.shatteredpixel.shatteredpixeldungeon.items.Stylus;
import com.shatteredpixel.shatteredpixeldungeon.items.TengusMask;
import com.shatteredpixel.shatteredpixeldungeon.items.Waterskin;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ChaliceOfBlood;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.EtherealChains;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HornOfPlenty;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.SandalsOfNature;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.remains.RemainsItem;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.Spell;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.InventoryStone;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.Runestone;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.DamageWand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.PatchRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.PoisonDartTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.RageTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MimicSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.NecromancerSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.PylonSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.StatueSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WardSprite;
import com.watabou.NotAllowedInLua;
import com.watabou.noosa.Gizmo;
import com.watabou.noosa.Group;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.Visual;
import com.watabou.utils.Callback;
import com.watabou.utils.Point;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@NotAllowedInLua
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

	private static final Map<MethodKey, LuaMethodManager> methods = new HashMap<>();

	public static final class MethodKey {
		private final String name;
		private final Class<?> declaringClass;

		public MethodKey(String name, Class<?> declaringClass) {
			this.name = name;
			this.declaringClass = declaringClass;
		}

		public MethodKey(Method method) {
			this(method.getName(), method.getDeclaringClass());
		}

		@Override
		public boolean equals(Object object) {
			if (this == object) return true;
			if (object == null || getClass() != object.getClass()) return false;
			MethodKey methodKey = (MethodKey) object;
			return Objects.equals(name, methodKey.name)
					&& Objects.equals(declaringClass, methodKey.declaringClass);
		}

		@Override
		public int hashCode() {
			return Objects.hash(name, declaringClass);
		}
	}

	static {

		//Mob.class
		try {

			//thief
			addMethod(87, Thief.class.getMethod("steal", Hero.class), "hero");

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

			addMethod(61, Mob.class.getMethod("move", int.class, boolean.class), "step", "travelling");//travelling may be false when a character is moving instantaneously, such as via teleportation
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


			//Char Sprite
			addMethod(201, CharSprite.class.getMethod("initAnimations"));
			addMethod(202, CharSprite.class.getMethod("getAnimations"));
			addMethod(203, CharSprite.class.getMethod("play", MovieClip.Animation.class), "anim");
			addMethod(204, CharSprite.class.getMethod("link", Char.class), "ch");
			addMethod(205, CharSprite.class.getMethod("worldToCamera", int.class), "cell");
			addMethod(206, CharSprite.class.getMethod("showStatusWithIcon", boolean.class, int.class, String.class, int.class, Object[].class), "ignoreVisibility", "color", "text", "icon", "args");
			addMethod(207, CharSprite.class.getMethod("attack", int.class, Callback.class), "cell", "callback");
			addMethod(208, CharSprite.class.getMethod("operate", int.class, Callback.class), "cell", "callback");
			addMethod(209, CharSprite.class.getMethod("jump", int.class, int.class, Callback.class), "from", "to", "callback");
			addMethod(210, CharSprite.class.getMethod("zap", int.class, Callback.class), "cell", "callback");
			addMethod(211, CharSprite.class.getMethod("hasOwnZapAnimation"));
			addMethod(212, CharSprite.class.getMethod("instantZapDamage"));
			addMethod(213, CharSprite.class.getMethod("turnTo", int.class, int.class), "from", "to");
			addMethod(214, CharSprite.class.getMethod("die"));
			addMethod(215, CharSprite.class.getMethod("blood"));

			addMethod(231, MimicSprite.class.getDeclaredMethod("hideMimicSprite", Char.class), "ch");
			addMethod(232, NecromancerSprite.class.getMethod("charge"));

			addMethod(241, PylonSprite.class.getMethod("activate"));
			addMethod(242, PylonSprite.class.getMethod("deactivate"));

			addMethod(251, StatueSprite.class.getMethod("armorTier"));
			addMethod(252, WardSprite.class.getMethod("updateTier", int.class), "tier");



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


			//Item.class
			addMethod(2201, Item.class.getMethod("actions", Hero.class), "hero");
			addMethod(2202, Item.class.getMethod("actionName", String.class, Hero.class), "action", "hero");
			addMethod(2203, Item.class.getMethod("defaultAction"));
			addMethod(2204, Item.class.getMethod("execute", Hero.class, String.class), "hero", "action");
			addMethod(2205, Item.class.getMethod("collect"));
			addMethod(2206, Item.class.getMethod("doPickUp", Hero.class, int.class), "hero", "pos");
			addMethod(2207, Item.class.getMethod("identify", boolean.class), "byHero");
			addMethod(2208, Item.class.getMethod("onHeroGainExp", float.class, Hero.class), "levelPercent", "hero");
			addMethod(2209, Item.class.getMethod("name"));
			addMethod(2210, Item.class.getMethod("desc"));
			addMethod(2211, Item.class.getMethod("image"));
			addMethod(2212, Item.class.getMethod("glowing"));
			addMethod(2213, Item.class.getMethod("value"));
			addMethod(2214, Item.class.getMethod("energyVal"));
			addMethod(2215, Item.class.getMethod("status"));

			addMethod(2130, EquipableItem.class.getMethod("doEquip", Hero.class), "hero");
			addMethod(2131, EquipableItem.class.getMethod("doUnequip", Hero.class, boolean.class), "hero", "collect");
			addMethod(2132, EquipableItem.class.getDeclaredMethod("timeToEquip", Hero.class), "hero");
			addMethod(2133, EquipableItem.class.getMethod("activate", Char.class), "ch");

			addMethod(2140, KindOfWeapon.class.getMethod("min", int.class), "lvl");
			addMethod(2141, KindOfWeapon.class.getMethod("max", int.class), "lvl");
			addMethod(2142, KindOfWeapon.class.getMethod("damageRoll", Char.class), "owner");
			addMethod(2143, KindOfWeapon.class.getMethod("accuracyFactor", Char.class, Char.class), "owner", "target");
			addMethod(2144, KindOfWeapon.class.getMethod("defenseFactor", Char.class), "owner");
			addMethod(2145, KindOfWeapon.class.getMethod("delayFactor", Char.class), "owner");
			addMethod(2146, KindOfWeapon.class.getMethod("reachFactor", Char.class), "owner");
			addMethod(2147, KindOfWeapon.class.getMethod("canReach", Char.class, int.class), "owner", "target");
			addMethod(2148, KindOfWeapon.class.getMethod("hitSound", float.class), "pitch");
			addMethod(2149, KindOfWeapon.class.getMethod("proc", Char.class, Char.class, int.class), "attacker", "defender", "damage");

			addMethod(2060, Armor.class.getMethod("proc", Char.class, Char.class, int.class), "attacker", "defender", "damage");
			addMethod(2061, Armor.class.getMethod("STRReq", int.class), "lvl");
			addMethod(2062, Armor.class.getMethod("DRMin", int.class), "lvl");
			addMethod(2063, Armor.class.getMethod("DRMax", int.class), "lvl");
			addMethod(2064, Armor.class.getMethod("evasionFactor", Char.class, float.class), "owner", "evasion");
			addMethod(2065, Armor.class.getMethod("speedFactor", Char.class, float.class), "owner", "speed");
			addMethod(2067, Armor.class.getMethod("affixSeal", BrokenSeal.class), "seal");
			addMethod(2068, Armor.class.getMethod("upgrade", boolean.class), "inscribe");
			addMethod(2069, Armor.class.getMethod("inscribe", Armor.Glyph.class), "glyph");
			addMethod(2070, Armor.class.getMethod("detachSeal", Hero.class), "hero");

			addMethod(2050, Artifact.class.getMethod("charge", Hero.class, float.class), "target", "amount");
			addMethod(2051, ChaliceOfBlood.class.getDeclaredMethod("prick", Hero.class), "hero");
			addMethod(2052, DriedRose.class.getDeclaredMethod("ghostStrength"));
			addMethod(2053, EtherealChains.class.getDeclaredMethod("chainEnemy", Ballistica.class, Hero.class, Char.class), "chain", "hero", "enemy");
			addMethod(2054, HornOfPlenty.class.getMethod("gainFoodValue", Food.class), "food");
			addMethod(2055, SandalsOfNature.class.getMethod("canUseSeed", Item.class), "item");
			addMethod(2056, TalismanOfForesight.class.getDeclaredMethod("maxDist"));

			addMethod(2038, RemainsItem.class.getDeclaredMethod("doEffect", Hero.class), "hero");

			addMethod(2039, Bomb.class.getMethod("explode", int.class), "cell");
			addMethod(2040, Bomb.class.getMethod("explodesDestructively"));

			addMethod(2041, Food.class.getDeclaredMethod("satisfy", Hero.class), "hero");
			addMethod(2042, Food.class.getDeclaredMethod("eatingTime"));
			addMethod(2043, Food.class.getDeclaredMethod("eatSFX"));

			addMethod(2044, Potion.class.getDeclaredMethod("drink", Hero.class), "hero");
			addMethod(2045, Potion.class.getMethod("apply", Hero.class), "hero");
			addMethod(2046, Potion.class.getMethod("shatter", int.class), "cell");
			addMethod(2047, Potion.class.getDeclaredMethod("splash", int.class), "cell");
			addMethod(2048, Potion.class.getDeclaredMethod("splashColor"));

			addMethod(2001, Scroll.class.getDeclaredMethod("doRead"));
			addMethod(2002, Scroll.class.getDeclaredMethod("readAnimation"));

			addMethod(2003, Spell.class.getDeclaredMethod("onCast", Hero.class));

			addMethod(2004, Runestone.class.getDeclaredMethod("activate", int.class));
			addMethod(2005, StoneOfEnchantment.class.getDeclaredMethod("createEnchantmentToInscribe", Weapon.class), "weapon");
			addMethod(2006, StoneOfEnchantment.class.getDeclaredMethod("createGlyphToInscribe", Armor.class), "armor");
			addMethod(2007, InventoryStone.class.getDeclaredMethod("inventoryTitle"));
			addMethod(2008, InventoryStone.class.getDeclaredMethod("usableOnItem", Item.class), "item");
			addMethod(2009, InventoryStone.class.getDeclaredMethod("onItemSelected", Item.class), "item");

			addMethod(2008, Trinket.class.getDeclaredMethod("upgradeEnergyCost"));

			addMethod(2011, Wand.class.getMethod("cursedEffect", Char.class, Ballistica.class, boolean.class), "user", "bolt", "positiveOnly");
			addMethod(2012, Wand.class.getMethod("onZap", Ballistica.class), "attack");
			addMethod(2013, Wand.class.getMethod("onHit", MagesStaff.class, Char.class, Char.class, int.class), "staff", "attacker", "defender", "damage");
			addMethod(2014, Wand.class.getMethod("tryToZap", Hero.class, int.class), "owner", "target");
			addMethod(2015, Wand.class.getMethod("gainCharge", float.class, boolean.class), "amount", "overcharge");
			addMethod(2016, Wand.class.getDeclaredMethod("wandProc", Char.class, int.class), "target", "chargesUsed");
			addMethod(2017, Wand.class.getDeclaredMethod("initialCharges"));
			addMethod(2018, Wand.class.getDeclaredMethod("chargesPerCast"));
			addMethod(2019, Wand.class.getDeclaredMethod("wandUsed"));
			addMethod(2020, Wand.class.getMethod("collisionProperties", int.class), "target");
			addMethod(2021, Wand.class.getMethod("statsDesc"));

			addMethod(2009, DamageWand.class.getMethod("min", int.class), "lvl");
			addMethod(2010, DamageWand.class.getMethod("max", int.class), "lvl");
			addMethod(2011, DamageWand.class.getMethod("damageRoll", int.class), "lvl");

			addMethod(2022, Weapon.class.getDeclaredMethod("baseDelay", Char.class), "owner");
			addMethod(2023, Weapon.class.getDeclaredMethod("speedMultiplier", Char.class), "owner");
			addMethod(2024, Weapon.class.getMethod("STRReq", int.class), "lvl");
			addMethod(2025, Weapon.class.getMethod("enchant", Weapon.Enchantment.class), "ench");
			addMethod(2026, MissileWeapon.class.getDeclaredMethod("adjacentAccFactor", Char.class, Char.class), "owner", "target");
			addMethod(2027, MissileWeapon.class.getDeclaredMethod("rangedHit", Char.class, int.class), "enemy", "cell");
			addMethod(2028, MissileWeapon.class.getDeclaredMethod("rangedMiss", int.class), "cell");
			addMethod(2029, MissileWeapon.class.getMethod("repair", float.class), "amount");

			addMethod(2030, Honeypot.class.getMethod("shatter", Char.class, int.class), "owner", "pos");
			addMethod(2031, KingsCrown.class.getMethod("upgradeArmor", Hero.class, Armor.class, ArmorAbility.class), "hero", "armor", "ability");
			addMethod(2032, TengusMask.class.getMethod("choose", HeroSubClass.class), "way");
			addMethod(2033, Stylus.class.getMethod("inscribe", Armor.class), "armor");
			addMethod(2034, Stylus.class.getDeclaredMethod("createGlyphToInscribe", Armor.class), "armor");

			addMethod(2034, Waterskin.class.getMethod("collectDew", Dewdrop.class), "dew");
			addMethod(2035, Waterskin.class.getDeclaredMethod("maxVolume"));

			addMethod(2036, Bag.class.getMethod("canHold", Item.class), "item");
			addMethod(2037, Bag.class.getMethod("capacity"));


			//Trap.class
			addMethod(2501, Trap.class.getMethod("activate"));
			addMethod(2502, Trap.class.getMethod("reveal"));
			addMethod(2503, Trap.class.getMethod("hide"));
			addMethod(2504, Trap.class.getMethod("disarm"));
			addMethod(2505, Trap.class.getMethod("activate"));
			addMethod(2511, PoisonDartTrap.class.getDeclaredMethod("poisonAmount"));
			addMethod(2512, PoisonDartTrap.class.getDeclaredMethod("canTarget", Char.class), "ch");
			addMethod(2513, RageTrap.class.getDeclaredMethod("affectsChar", Char.class, int.class, int.class), "ch", "x", "y");


			//Plant.class
			addMethod(2701, Plant.class.getMethod("activate", Char.class), "ch");
			addMethod(2702, Plant.class.getMethod("wither"));


			//Room.class
			addMethod(4001, Room.class.getMethod("paint", Level.class), "level");//when overriding these YOU MUST store any randomly decided values.

			addMethod(4002, Room.class.getMethod("minWidth"));//when overriding these, YOU MUST store any randomly decided values.
			addMethod(4003, Room.class.getMethod("maxWidth"));
			addMethod(4004, Room.class.getMethod("minHeight"));
			addMethod(4005, Room.class.getMethod("maxHeight"));

			addMethod(4011, Room.class.getMethod("maxConnections", int.class), "direction");
			addMethod(4012, Room.class.getMethod("canConnect", Point.class), "p");
			addMethod(4013, Room.class.getMethod("canMerge", Level.class, Room.class, Point.class, int.class), "l", "other", "p", "mergeTerrain");
			addMethod(4014, Room.class.getMethod("connect", Room.class), "room");

			addMethod(4021, Room.class.getMethod("canPlaceWater", Point.class), "p");
			addMethod(4022, Room.class.getMethod("canPlaceGrass", Point.class), "p");
			addMethod(4023, Room.class.getMethod("canPlaceTrap", Point.class), "p");
			addMethod(4024, Room.class.getMethod("canPlaceItem", Point.class, Level.class), "p", "l");

			addMethod(4030, Room.class.getMethod("generateItems", Level.class), "level");

			addMethod(4031, StandardRoom.class.getMethod("sizeFactor"));
			addMethod(4032, StandardRoom.class.getMethod("mobSpawnWeight"));
			addMethod(4033, StandardRoom.class.getMethod("connectionWeight"));
			addMethod(4034, PatchRoom.class.getDeclaredMethod("clustering"));
			addMethod(4035, PatchRoom.class.getDeclaredMethod("cleanEdges"));
			addMethod(4036, PatchRoom.class.getDeclaredMethod("ensurePath"));
			addMethod(4037, PatchRoom.class.getDeclaredMethod("fill"));


			//Buff.class
			addMethod(4601, Buff.class.getMethod("act"));
			addMethod(4602, Buff.class.getMethod("attachTo", Char.class), "taget");
			addMethod(4603, Buff.class.getMethod("detach"));
			addMethod(4604, Buff.class.getMethod("icon"));
			addMethod(4605, Buff.class.getMethod("iconFadePercent"));
			addMethod(4606, Buff.class.getMethod("iconTextDisplay"));
			addMethod(4607, Buff.class.getMethod("fx", boolean.class), "on");
			addMethod(4608, Buff.class.getMethod("name"));
			addMethod(4609, Buff.class.getMethod("desc"));

			addMethod(4511, ChampionEnemy.class.getMethod("onAttackProc", Char.class), "enemy");
			addMethod(4512, ChampionEnemy.class.getMethod("canAttackWithExtraReach", Char.class), "enemy");
			addMethod(4513, ChampionEnemy.class.getMethod("meleeDamageFactor"));
			addMethod(4514, ChampionEnemy.class.getMethod("damageTakenFactor"));
			addMethod(4515, ChampionEnemy.class.getMethod("evasionAndAccuracyFactor"));

			addMethod(4521, Chill.class.getMethod("speedFactor"));



			//DungeonScript.class
			addMethod(10000, DungeonScript.class.getMethod("onItemCollected", Item.class), "item");
			addMethod(10001, DungeonScript.class.getMethod("executeItem", Item.class, Hero.class, String.class, DungeonScript.Executer.class), "item", "user", "action", "executer");

			addMethod(10010, DungeonScript.class.getMethod("isItemBlocked", Item.class), "item");

			addMethod(10020, DungeonScript.class.getMethod("onEarnXP", int.class, Class.class), "amount", "source");
			addMethod(10021, DungeonScript.class.getMethod("onLevelUp"));

			addMethod(10030, DungeonScript.class.getMethod("getMobRotation", int.class), "depth");


			//Visual.class
			addMethod(5001, Visual.class.getDeclaredMethod("resetColor"));

			//Group.class
			addMethod(6001, Group.class.getMethod("add", Gizmo.class), "g");
			addMethod(6002, Group.class.getMethod("addToBack", Gizmo.class), "g");
			addMethod(6003, Group.class.getMethod("addToFront", Gizmo.class), "g");
			addMethod(6004, Group.class.getMethod("erase", Gizmo.class), "g");
			addMethod(6005, Group.class.getMethod("remove", Gizmo.class), "g");
			addMethod(6006, Group.class.getMethod("bringToFront", Gizmo.class), "g");
			addMethod(6007, Group.class.getMethod("sendToBack", Gizmo.class), "g");

			//Gizmo.class
			addMethod(7001, Gizmo.class.getMethod("update"));
			addMethod(7002, Gizmo.class.getMethod("destroy"));
			addMethod(7003, Gizmo.class.getMethod("kill"));
			addMethod(7004, Gizmo.class.getMethod("killAndErase"));
			addMethod(7005, Gizmo.class.getMethod("isVisible"));
			addMethod(7006, Gizmo.class.getMethod("isActive"));
			addMethod(7007, Gizmo.class.getMethod("setVisible", boolean.class), "val");

		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}


	}


	private static void addMethod(int sortingRank, Method method, String... paramNames) {
		methods.put(new MethodKey(method), new LuaMethodManager(sortingRank, method, paramNames));
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
				LuaMethodManager obj = methods.get(new MethodKey(m));
				if (obj != null && obj.method.getDeclaringClass().isAssignableFrom(clazz)) {
					newlyAddTemp.add(obj);
					alreadyAdded.add(name);
				}
			}
		}
		Collections.sort(newlyAddTemp);
		result.addAll(newlyAddTemp);
		newlyAddTemp.clear();
	}
	
	public static String descriptionForMethod(Method method) {
		String result = Messages.get(LuaMethodManager.class, method.getDeclaringClass().getSimpleName() + "_" + method.getName());
		if (result.isEmpty()) {
			return Messages.get(LuaMethodManager.class, "placeholder");
		}
		return result;
	}

}
