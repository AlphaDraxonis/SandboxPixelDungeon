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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SandboxPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.editor.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.*;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.*;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.QuestNPC;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.*;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.journal.DocumentPage;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.Brew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.Elixir;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.remains.RemainsItem;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.Spell;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.Runestone;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.TippedDart;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

public class LuaGlobals extends Globals {

	LuaGlobals() {

		Globals standardGlobals = JsePlatform.standardGlobals();
		compiler = standardGlobals.compiler;
		loader = standardGlobals.loader;

		final LuaFunction newInstance = standardGlobals.get("luajava").get("newInstance").checkfunction();
		set("new", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue arg) {
				if (arg.isstring()) {
					if (arg.checkjstring().startsWith(Messages.MAIN_PACKAGE_NAME)) {
						LuaValue result = newInstance.call(arg);
						if (result.isuserdata()) {
							Object obj = result.checkuserdata();
							if (obj instanceof Bundlable || obj instanceof Ballistica) return result;
						}
					}
					String fullName = searchFullyQualifiedName(arg.checkjstring());
					if (fullName != null) return newInstance.call(fullName);
				}
				return LuaValue.NIL;
			}
		});

		set("class", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue arg) {
				if (arg.isstring()) {
					if (arg.checkjstring().startsWith(Messages.MAIN_PACKAGE_NAME)) {
						Class<?> result = Reflection.forName(arg.checkjstring());
						if (Bundlable.class.isAssignableFrom(result)) {
							return CoerceJavaToLua.coerce(result);
						}
					}
					String fullName = searchFullyQualifiedName(arg.checkjstring());
					if (fullName != null) return newInstance.call(fullName);
				}
				return LuaValue.NIL;
			}
		});

		set("getActor", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue ID) {
				return ID.isint() ? new LuaUserdata(Actor.findById(ID.checkint())) : LuaValue.NIL;
			}
		});

		LuaTable messages = new LuaTable();
		messages.set("get", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue key) {
				return key.isstring()
						? LuaValue.valueOf(Messages.get(key.checkjstring()))
						: LuaValue.NIL;
			}
		});
		messages.set("titleCase", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue key) {
				return key.isstring()
						? LuaValue.valueOf(Messages.titleCase(key.checkjstring()))
						: LuaValue.NIL;
			}
		});
		messages.set("capitalize", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue key) {
				return key.isstring()
						? LuaValue.valueOf(Messages.capitalize(key.checkjstring()))
						: LuaValue.NIL;
			}
		});
		messages.set("upperCase", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue key) {
				return key.isstring()
						? LuaValue.valueOf(Messages.upperCase(key.checkjstring()))
						: LuaValue.NIL;
			}
		});
		set("messages", messages);

		LuaTable randomUtils = new LuaTable();
		randomUtils.set("int", new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue min, LuaValue max) {
				return min.isint() && max.isint()
						? LuaValue.valueOf(Random.Int(min.checkint(), max.checkint()))
						: LuaValue.NIL;
			}
		});
		set("random", randomUtils);

		LuaTable gLog = new LuaTable();
		gLog.set("i", new OneArgFunction() {//info, white
			@Override
			public LuaValue call(LuaValue text) {
				if (text.isstring()) GLog.i(text.checkjstring());
				return LuaValue.NIL;
			}
		});
		gLog.set("p", new OneArgFunction() {//positive, green
			@Override
			public LuaValue call(LuaValue text) {
				if (text.isstring()) GLog.p(text.checkjstring());
				return LuaValue.NIL;
			}
		});
		gLog.set("n", new OneArgFunction() {//negative, red
			@Override
			public LuaValue call(LuaValue text) {
				if (text.isstring()) GLog.n(text.checkjstring());
				return LuaValue.NIL;
			}
		});
		gLog.set("h", new OneArgFunction() {//highlight, yellow
			@Override
			public LuaValue call(LuaValue text) {
				if (text.isstring()) GLog.h(text.checkjstring());
				return LuaValue.NIL;
			}
		});
		gLog.set("w", new OneArgFunction() {//warning, orange
			@Override
			public LuaValue call(LuaValue text) {
				if (text.isstring()) GLog.w(text.checkjstring());
				return LuaValue.NIL;
			}
		});
		set("log", gLog);

		set("print", gLog.get("i"));

		//TODO show windows tzz

		set("cellToString", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue cell) {
				return cell.isint() ? LuaValue.valueOf(EditorUtilies.cellToString(cell.checkint())) : LuaValue.NIL;
			}
		});


		set("spawnMob", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue mob) {//returns false to indicate that no spawn point was found by randomly selecting many cells of the level
				if (mob.isuserdata()) {
					Object obj = mob.checkuserdata();
					if (obj instanceof Mob) {
						return LuaValue.valueOf(Dungeon.level.spawnMob(((Mob) obj), 12, null));
					}
				}
				return LuaValue.NIL;
			}
		});
		set("placeMob", new TwoArgFunction() {//no valid placement check is made
			@Override
			public LuaValue call(LuaValue mob, LuaValue pos) {
				if (mob.isuserdata() && pos.isint()) {
					Object obj = mob.checkuserdata();
					if (obj instanceof Mob) {
						((Mob) obj).pos = pos.checkint();
						Level.placeMob((Mob) obj);
					}
				}
				return LuaValue.NIL;
			}
		});

		set("drop", new ThreeArgFunction() {//no valid placement check is made
			@Override
			public LuaValue call(LuaValue item, LuaValue pos, LuaValue from) {
				if (item.isuserdata() && pos.isint()) {
					Object obj = item.checkuserdata();
					if (obj instanceof Item) {
						if (from.isnil()) Dungeon.level.drop((Item) obj, pos.toint()).sprite.drop();
						else if (from.isint()) Dungeon.level.drop((Item) obj, pos.toint()).sprite.drop(from.checkint());
					}
				}
				return LuaValue.NIL;
			}
		});

		set("giveItem", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue item) {
				if (item.isuserdata()) {
					Object obj = item.checkuserdata();
					if (obj instanceof Item) {
						((Item) obj).collect();
					}
				}
				return LuaValue.NIL;
			}
		});

		set("reloadScene", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				SandboxPixelDungeon.seamlessResetScene();
				return LuaValue.NIL;
			}
		});

		set("instanceof", new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue obj, LuaValue className) {
				if (obj.isuserdata() && className.isstring()) {
					Class<?> clazz = Reflection.forName(className.checkjstring());
					if (clazz == null) {
						String className2 = searchFullyQualifiedNameForInstanceof(className.checkjstring());
						if (className2 == null || (clazz = Reflection.forName(className2)) == null)
							return LuaValue.valueOf("Class not found: " + className.checkstring());
					}
					return LuaValue.valueOf(clazz.isInstance(obj.checkuserdata()));
				}
				return LuaValue.FALSE;
			}
		});

		set("areEqual", new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue a, LuaValue b) {
				if (a.isuserdata() && b.isuserdata()) {
					Object objA = a.touserdata();
					Object objB = b.touserdata();
					if (objA.getClass() != objB.getClass()) return LuaValue.FALSE;

					if (objA instanceof Item)    return LuaValue.valueOf(EditItemComp.areEqual(((Item) objA), (Item) objB));
					if (objA instanceof Mob)     return LuaValue.valueOf(EditMobComp.areEqual(((Mob) objA), (Mob) objB));
					if (objA instanceof Trap)    return LuaValue.valueOf(EditTrapComp.areEqual(((Trap) objA), (Trap) objB));
					if (objA instanceof Plant)   return LuaValue.valueOf(EditPlantComp.areEqual(((Plant) objA), (Plant) objB));
					if (objA instanceof Heap)    return LuaValue.valueOf(EditHeapComp.areEqual(((Heap) objA), (Heap) objB));
					if (objA instanceof Barrier) return LuaValue.valueOf(EditBarrierComp.areEqual(((Barrier) objA), (Barrier) objB));
					if (objA instanceof Room)    return LuaValue.valueOf(EditRoomComp.areEqual(((Room) objA), (Room) objB));

				}
				return LuaValue.FALSE;
			}
		});

		set("getGold", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return LuaValue.valueOf(Dungeon.gold);
			}
		});
		set("setGold", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue gold) {
				if (!gold.isint()) return LuaValue.NIL;
				Dungeon.gold = gold.checkint();
				return LuaValue.valueOf(Dungeon.gold);
			}
		});
		set("getEnergy", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return LuaValue.valueOf(Dungeon.energy);
			}
		});
		set("setEnergy", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue energy) {
				if (!energy.isint()) return LuaValue.NIL;
				Dungeon.energy = energy.checkint();
				return LuaValue.valueOf(Dungeon.energy);
			}
		});
		set("collectKey", new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue itemKey, LuaValue pos) {
				if (!itemKey.isuserdata()) return LuaValue.NIL;
				Object obj = itemKey.checkuserdata();
				if (!(obj instanceof Key)) return LuaValue.NIL;
				int cell = pos.isint() ? pos.checkint() : Dungeon.hero.pos;
				((Key) obj).instantPickupKey(cell);
				return LuaValue.TRUE;
			}
		});
		//TODO tzz code to create a new custom mob
	}

	private static String searchFullyQualifiedName(String simpleName) {
		String result = null;
		switch (simpleName) {
			case "Barrier": return Barrier.class.getName();
			case "Ballistica": return Ballistica.class.getName();
		}
		if (result == null) result = searchFullyQualifiedNameInArrays(simpleName, EditorInvCategory.getAll(Items.values()));
		if (result == null) result = searchFullyQualifiedNameInArrays(simpleName, EditorInvCategory.getAll(Mobs.values()));
		if (result == null) result = searchFullyQualifiedNameInArrays(simpleName, EditorInvCategory.getAll(Plants.values()));
		if (result == null) result = searchFullyQualifiedNameInArrays(simpleName, EditorInvCategory.getAll(Traps.values()));
		if (result == null) result = searchFullyQualifiedNameInArrays(simpleName, EditorInvCategory.getAll(Buffs.values()));
		return result;
	}

	private static String searchFullyQualifiedNameForInstanceof(String simpleName) {
		String result = null;
		switch (simpleName) {
			case "Mob": return Mob.class.getName();
			case "QuestNPC": return QuestNPC.class.getName();
			case "NPC": return NPC.class.getName();
			case "Buff": return Buff.class.getName();
			case "Barrier": return Barrier.class.getName();
			case "Actor": return Actor.class.getName();
			case "Weapon": return Weapon.class.getName();
			case "Armor": return Armor.class.getName();
			case "MeleeWeapon": return MeleeWeapon.class.getName();
			case "MissileWeapon": return MissileWeapon.class.getName();
			case "Wand": return Wand.class.getName();
			case "Seed": return Plant.Seed.class.getName();
			case "Runestone": return Runestone.class.getName();
			case "EquipableItem": return EquipableItem.class.getName();
			case "KindofMisc": return KindofMisc.class.getName();
			case "KindOfWeapon": return KindOfWeapon.class.getName();
			case "TippedDart": return TippedDart.class.getName();
			case "Key": return Key.class.getName();
			case "Ring": return Ring.class.getName();
			case "Artifact": return Artifact.class.getName();
			case "Potion": return Potion.class.getName();
			case "Scroll": return Scroll.class.getName();
			case "ExoticScroll": return ExoticScroll.class.getName();
			case "ExoticPotion": return ExoticPotion.class.getName();
			case "Brew": return Brew.class.getName();
			case "Elixir": return Elixir.class.getName();
			case "Spell": return Spell.class.getName();
			case "Bag": return Bag.class.getName();
			case "RemainsItem": return RemainsItem.class.getName();
			case "DocumentPage": return DocumentPage.class.getName();
			case "Hero": return Hero.class.getName();
			case "YogFist": return YogFist.class.getName();
			case "Elemental": return Elemental.class.getName();
			case "DMMob": return DMMob.class.getName();
			case "SpawnerMob": return SpawnerMob.class.getName();
			case "ChampionEnemy": return ChampionEnemy.class.getName();
		}
		if (result == null) result = searchFullyQualifiedNameInArrays(simpleName, EditorInvCategory.getAll(Mobs.values()));
		if (result == null) result = searchFullyQualifiedNameInArrays(simpleName, EditorInvCategory.getAll(Items.values()));
		if (result == null) result = searchFullyQualifiedNameInArrays(simpleName, EditorInvCategory.getAll(Buffs.values()));
		if (result == null) result = searchFullyQualifiedNameInArrays(simpleName, EditorInvCategory.getAll(Traps.values()));
		if (result == null) result = searchFullyQualifiedNameInArrays(simpleName, EditorInvCategory.getAll(Plants.values()));
		return result;
	}

	private static String searchFullyQualifiedNameInArrays(String simpleName, Class<?>[][] classes) {
		for (int i = 0; i < classes.length; i++) {
			for (int j = 0; j < classes[i].length; j++) {
				if (classes[i][j].getSimpleName().equals(simpleName)) return classes[i][j].getName();
			}
		}
		return null;
	}
}