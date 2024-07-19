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

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SandboxPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.editor.ArrowCell;
import com.shatteredpixel.shatteredpixeldungeon.editor.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.editor.Checkpoint;
import com.shatteredpixel.shatteredpixeldungeon.editor.WndCreator;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.*;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.*;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.Zone;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.QuestNPC;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.*;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.*;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.journal.DocumentPage;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.Brew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.Elixir;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Pickaxe;
import com.shatteredpixel.shatteredpixeldungeon.items.remains.RemainsItem;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfForce;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.Spell;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.SummonElemental;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.Runestone;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.Dart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.TippedDart;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.*;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndResurrect;
import com.watabou.noosa.Game;
import com.watabou.noosa.Gizmo;
import com.watabou.noosa.Group;
import com.watabou.noosa.Visual;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import com.watabou.utils.SparseArray;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.*;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

public class LuaGlobals extends Globals {

	LuaGlobals() {

		Globals standardGlobals = JsePlatform.standardGlobals();
		compiler = standardGlobals.compiler;
		loader = standardGlobals.loader;

		final LuaFunction newInstance = standardGlobals.get("luajava").get("newInstance").checkfunction();
		set("new", new VarArgFunction() {

			@Override
			public Varargs invoke(Varargs varargs) {
				LuaValue arg = varargs.arg1();
				if (arg.isstring()) {
					if (arg.checkjstring().startsWith(Messages.MAIN_PACKAGE_NAME)) {
						LuaValue result = newInstance.invoke(arg, varargs.subargs(2)).arg1();
						if (result.isuserdata()) {
							Object obj = result.checkuserdata();
							if (obj instanceof Bundlable || obj instanceof CharSprite) return result;
							else {
								if (obj instanceof Gizmo) ((Gizmo) obj).destroy();
								throw new IllegalArgumentException("Instancing class " + arg.checkjstring() + " is not permitted for security reasons!");
							}
						}
					}
					String fullName = searchFullyQualifiedName(arg.checkjstring());
					if (fullName != null) {
						return CoerceJavaToLua.coerce(Reflection.newInstance(Reflection.forName(fullName)));
//						return newInstance.invoke(LuaValue.valueOf(fullName), varargs.subargs(2));
					}
				}
				return LuaValue.NIL;
			}
		});

		set("newCus", new VarArgFunction() {

			@Override
			public Varargs invoke(Varargs varargs) {
				LuaValue arg = varargs.arg1();
				if (arg.isstring()) {
					String s = arg.tojstring();
					for (CustomObject obj : CustomObject.customObjects.values()) {
						if (obj.name.equals(s)) return CoerceJavaToLua.coerce(obj.luaClass.newInstance());
					}
				}
				if (arg.isint()) {
					LuaClass original = CustomObject.getLuaClass(arg.checkint());
					return original == null
							? LuaValue.NIL
							: CoerceJavaToLua.coerce(original.newInstance());
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
						if (result != null && Bundlable.class.isAssignableFrom(result)) {
							return CoerceJavaToLua.coerce(result);
						}
					}
					String fullName = searchFullyQualifiedName(arg.checkjstring());
					if (fullName != null) return CoerceJavaToLua.coerce(Reflection.forName(fullName));
				}
				return LuaValue.NIL;
			}
		});

		set("ballistica", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs varargs) {
				LuaValue from = varargs.arg(1);
				LuaValue to = varargs.arg(2);
				LuaValue params = varargs.arg(3);
				LuaValue usePassable = varargs.arg(4);
				if (from.isint() && to.isint() && params.isint() && (usePassable.isnil() || usePassable.isuserdata(Char.class))) {
					Ballistica ballistica = new Ballistica(from.toint(), to.toint(), params.toint(), (Char) usePassable.touserdata());
					return CoerceJavaToLua.coerce(ballistica);
				}
				return LuaValue.NIL;
			}
		});

		set("tostring", standardGlobals.get("tostring"));

		LuaTable arrayUtils = new LuaTable();
		arrayUtils.set("length", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue array) {
				return LuaValue.valueOf(Array.getLength(CoerceLuaToJava.coerce(array, Object.class)));
			}
		});
		arrayUtils.set("set", new ThreeArgFunction() {
			@Override
			public LuaValue call(LuaValue array, LuaValue index, LuaValue value) {
				Array.set(CoerceLuaToJava.coerce(array, Object.class), index.toint(), CoerceLuaToJava.coerce(value, Object.class));
				return LuaValue.NIL;
			}
		});
		arrayUtils.set("get", new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue array, LuaValue index) {
				return CoerceJavaToLua.coerce(Array.get(CoerceLuaToJava.coerce(array, Object.class), index.toint()));
			}
		});
		arrayUtils.set("iterate", new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue array, LuaValue consumer) {
				Object java = CoerceLuaToJava.coerce(array, Object.class);
				LuaFunction function = consumer.checkfunction();
				if (java instanceof SparseArray) {
					for (Object obj : ((SparseArray<?>) java).values()) {
						function.call(CoerceJavaToLua.coerce(obj));
					}
					return LuaValue.TRUE;
				}
				else if (java instanceof Map<?, ?>) {
					for (Object obj : ((Map<?, ?>) java).values()) {
						function.call(CoerceJavaToLua.coerce(obj));
					}
					return LuaValue.TRUE;
				}
				else if (java instanceof Iterable) {
					for (Object obj : ((Iterable<?>) java)) {
						function.call(CoerceJavaToLua.coerce(obj));
					}
					return LuaValue.TRUE;
				}
				else if (java.getClass().isArray()) {
					int length = Array.getLength(java);
					for (int i = 0; i < length; i++) {
						function.call(CoerceJavaToLua.coerce(Array.get(java, i)));
					}
				}
				return LuaValue.NIL;
			}
		});
		set("Arrays", arrayUtils);

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
		set("Messages", messages);

		LuaTable randomUtils = new LuaTable();
		randomUtils.set("int", new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue min, LuaValue max) {
				if (max.isnil()) {
					return min.isint()
							? LuaValue.valueOf(Random.Int(min.checkint()))
							: LuaValue.NIL;
				}
				return min.isint() && max.isint()
						? LuaValue.valueOf(Random.Int(min.checkint(), max.checkint()))
						: LuaValue.NIL;
			}
		});
		randomUtils.set("combatRoll", new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue min, LuaValue max) {
				if (max.isnil()) {
					return min.isint()
							? LuaValue.valueOf(Char.combatRoll(0, min.checkint()))
							: LuaValue.NIL;
				}
				return min.isint() && max.isint()
						? LuaValue.valueOf(Char.combatRoll(min.checkint(), max.checkint()))
						: LuaValue.NIL;
			}
		});
		randomUtils.set("element", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue collection) {
				Object java = CoerceLuaToJava.coerce(collection, Object.class);
				if (java instanceof SparseArray<?>) {
					return CoerceJavaToLua.coerce(Random.element((((SparseArray<?>) java).valueList())));
				}
				else if (java instanceof Collection<?>) {
					return CoerceJavaToLua.coerce(Random.element(((Collection<?>) java)));
				}
				else if (java.getClass().isArray()) {
					return CoerceJavaToLua.coerce(Array.get(java, Random.Int(Array.getLength(java))));
				}
				return LuaValue.NIL;
			}
		});
		randomUtils.set("pushGenerator", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue seed) {
				if (seed.islong()) Random.pushGenerator(seed.checklong());
				return LuaValue.NIL;
			}
		});
		randomUtils.set("popGenerator", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				Random.popGenerator();
				return LuaValue.NIL;
			}
		});
		randomUtils.set("dungeonSeed", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return LuaValue.valueOf(Dungeon.seed);
			}
		});
		randomUtils.set("levelSeed", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return LuaValue.valueOf(Dungeon.seedCurLevel());
			}
		});
		set("Random", randomUtils);

		LuaTable gLog = new LuaTable();
		gLog.set("i", new OneArgFunction() {//info, white
			@Override
			public LuaValue call(LuaValue text) {
				String s = luaToString(text);
				if (s != null) GLog.i(s);
				return s == null ? LuaValue.NIL : LuaValue.valueOf(s);
			}
		});
		gLog.set("p", new OneArgFunction() {//positive, green
			@Override
			public LuaValue call(LuaValue text) {
				String s = luaToString(text);
				if (s != null) GLog.p(s);
				return s == null ? LuaValue.NIL : LuaValue.valueOf(s);
			}
		});
		gLog.set("n", new OneArgFunction() {//negative, red
			@Override
			public LuaValue call(LuaValue text) {
				String s = luaToString(text);
				if (s != null) GLog.n(s);
				return s == null ? LuaValue.NIL : LuaValue.valueOf(s);
			}
		});
		gLog.set("h", new OneArgFunction() {//highlight, yellow
			@Override
			public LuaValue call(LuaValue text) {
				String s = luaToString(text);
				if (s != null) GLog.h(s);
				return s == null ? LuaValue.NIL : LuaValue.valueOf(s);
			}
		});
		gLog.set("w", new OneArgFunction() {//warning, orange
			@Override
			public LuaValue call(LuaValue text) {
				String s = luaToString(text);
				if (s != null) GLog.w(s);
				return s == null ? LuaValue.NIL : LuaValue.valueOf(s);
			}
		});
		set("GLog", gLog);

		set("print", gLog.get("i"));

		set("showMessageWindow", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs varargs) {
				LuaValue fifthArg = varargs.arg(4);
				LuaFunction onHide = fifthArg.isfunction() ? fifthArg.checkfunction() : null;
				Game.runOnRenderThread(() -> {
					try {
						WndCreator.showMessageWindow(varargs.arg(1), varargs.arg(2), varargs.arg(3), LuaValue.NIL, onHide);
					} catch (LuaError e) {
						DungeonScene.show(new WndError(e));
					}
				});
				return LuaValue.NIL;
			}

		});
		set("showStoryWindow", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs varargs) {
				LuaValue fifthArg = varargs.arg(4);
				LuaFunction onHide = fifthArg.isfunction() ? fifthArg.checkfunction() : null;
				Game.runOnRenderThread(() -> {
					try {
						WndCreator.showStoryWindow(varargs.arg(1), varargs.arg(2), varargs.arg(3), LuaValue.NIL, onHide);
					} catch (LuaError e) {
						DungeonScene.show(new WndError(e));
					}
				});
				return LuaValue.NIL;
			}

		});
		set("showItemRewardWindow", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs varargs) {
				LuaValue seventhArg = varargs.arg(7);
				LuaFunction onSelectReward = seventhArg.isfunction() ? seventhArg.checkfunction() : null;
				Game.runOnRenderThread(() -> {
					try {
						WndCreator.showItemRewardWindow(varargs.arg(1), varargs.arg(2), varargs.arg(3), varargs.arg(4), varargs.arg(5), varargs.arg(6), onSelectReward);
					} catch (LuaError e) {
						DungeonScene.show(new WndError(e));
					}
				});
				return LuaValue.NIL;
			}
		});
		set("showOptionsWindow", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs varargs) {
				LuaValue sixthArg = varargs.arg(6);
				LuaFunction onSelect = sixthArg.isfunction() ? sixthArg.checkfunction() : null;
				Game.runOnRenderThread(() -> {
					try {
						WndCreator.showOptionsWindow(varargs.arg(1), varargs.arg(2), varargs.arg(3), varargs.arg(4), varargs.arg(5), onSelect);
					} catch (LuaError e) {
						DungeonScene.show(new WndError(e));
					}
				});
				return LuaValue.NIL;
			}
		});
		set("showCompactOptionsWindow", new VarArgFunction() {
			@Override
			public Varargs invoke(Varargs varargs) {
				LuaValue sixthArg = varargs.arg(6);
				LuaFunction onSelect = sixthArg.isfunction() ? sixthArg.checkfunction() : null;
				Game.runOnRenderThread(() -> {
					try {
						WndCreator.showCondensedOptionsWindow(varargs.arg(1), varargs.arg(2), varargs.arg(3), varargs.arg(4), varargs.arg(5), onSelect);
					} catch (LuaError e) {
						DungeonScene.show(new WndError(e));
					}
				});
				return LuaValue.NIL;
			}
		});

		set("showCellSelector", new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue prompt, LuaValue onSelect) {
				String promptString;
				if (prompt.isnil() || !prompt.isstring()) promptString = Messages.get(LuaGlobals.class, "select_cell_prompt");
				else {
					promptString = prompt.checkjstring();
					String msg = Messages.get(promptString);
					if (msg != Messages.NO_TEXT_FOUND) promptString = msg;
				}
				final String p = promptString;
				Game.runOnRenderThread(() -> {
					GameScene.selectCell(new CellSelector.Listener() {
						{
							minShowingTime = 50;
						}
						@Override
						public void onSelect(Integer cell) {
							try {
								if (cell == null || cell < 0 || cell >= Dungeon.level.length())
									cell = -1;
								onSelect.call(LuaValue.valueOf(cell));
							} catch (LuaError error) { Game.runOnRenderThread(() ->	DungeonScene.show(new WndError(error))); }
						}

						@Override
						public String prompt() {
							return p;
						}
					});
				});
				return LuaValue.NIL;
			}
		});
		set("showItemSelector", new ThreeArgFunction() {
			@Override
			public LuaValue call(LuaValue prompt, LuaValue itemSelectable, LuaValue onSelect) {
				String promptString;
				if (prompt.isnil() || !prompt.isstring()) promptString = Messages.get(WndResurrect.class, "prompt");
				else {
					promptString = prompt.checkjstring();
					String msg = Messages.get(promptString);
					if (msg != Messages.NO_TEXT_FOUND) promptString = msg;
				}
				final String p = promptString;
				Game.runOnRenderThread(() -> {
					GameScene.selectItem(new WndBag.ItemSelector() {
						@Override
						public String textPrompt() {
							return p;
						}

						@Override
						public boolean itemSelectable(Item item) {
							if ( !itemSelectable.isfunction()) return true;
							try {
								LuaValue result = itemSelectable.checkfunction().call(CoerceJavaToLua.coerce(item));
								return result.isboolean() && result == LuaValue.TRUE;
							} catch (LuaError error) { Game.runOnRenderThread(() ->	DungeonScene.show(new WndError(error))); }
							return false;
						}

						@Override
						public void onSelect(Item item) {
							try {
								onSelect.call(CoerceJavaToLua.coerce(item));
							} catch (LuaError error) { Game.runOnRenderThread(() ->	DungeonScene.show(new WndError(error))); }
						}
					});
				});
				return LuaValue.NIL;
			}
		});

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

		set("affectBuff", new ThreeArgFunction() {
			@Override
			public LuaValue call(LuaValue target, LuaValue buff, LuaValue duration) {
				if (target.isuserdata() && buff.isuserdata()) {
					Object obj = target.checkuserdata();
					if (obj instanceof Char) {
						Object b = buff.touserdata();
						Class<?> buffClass = b instanceof Class ? (Class<?>) b : b.getClass();

						if (duration.isnil() || !duration.isnumber() || !FlavourBuff.class.isAssignableFrom(buffClass)) {

							if (Buff.class.isAssignableFrom(buffClass)) {
								return CoerceJavaToLua.coerce(Buff.affect((Char) obj, ((Class<? extends Buff>) buffClass)));
							}

						} else {
							return CoerceJavaToLua.coerce(Buff.affect((Char) obj, ((Class<? extends FlavourBuff>) buffClass), duration.tofloat()));
						}

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
						if (!from.isint()) Dungeon.level.drop((Item) obj, pos.toint()).sprite.drop();
						else Dungeon.level.drop((Item) obj, pos.toint()).sprite.drop(from.checkint());
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
						if (!((Item) obj).collect()) {
							Dungeon.level.drop((Item) obj, Dungeon.hero.pos);
						}
					}
				}
				return LuaValue.NIL;
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

		set("reloadScene", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				SandboxPixelDungeon.seamlessResetScene();
				return LuaValue.NIL;
			}
		});

		set("updateCell", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue cell) {
				if (cell.isint()) {
					DungeonScene.updateMap(cell.checkint());
					Dungeon.level.cleanWallCell(cell.checkint());
				}
				return LuaValue.NIL;
			}
		});

		set("updateMap", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				DungeonScene.updateMap();
				Dungeon.level.cleanWalls();
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
					if (objA instanceof Buff)    return LuaValue.valueOf(EditBuffComp.areEqual(((Buff) objA), (Buff) objB));
					if (objA instanceof Barrier) return LuaValue.valueOf(EditBarrierComp.areEqual(((Barrier) objA), (Barrier) objB));
					if (objA instanceof ArrowCell) return LuaValue.valueOf(EditArrowCellComp.areEqual(((ArrowCell) objA), (ArrowCell) objB));
					if (objA instanceof Room)    return LuaValue.valueOf(EditRoomComp.areEqual(((Room) objA), (Room) objB));
					if (objA instanceof Checkpoint)    return LuaValue.valueOf(EditCheckpointComp.areEqual(((Checkpoint) objA), (Checkpoint) objB));

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

		LuaTable terrainConstants = new LuaTable();

		terrainConstants.set("CHASM", Terrain.CHASM);
		terrainConstants.set("EMPTY", Terrain.EMPTY);
		terrainConstants.set("GRASS", Terrain.GRASS);

		terrainConstants.set("EMPTY_WELL", Terrain.EMPTY_WELL);
		terrainConstants.set("WALL", Terrain.WALL);
		terrainConstants.set("DOOR", Terrain.DOOR);
		terrainConstants.set("OPEN_DOOR", Terrain.OPEN_DOOR);
		terrainConstants.set("ENTRANCE", Terrain.ENTRANCE);
		terrainConstants.set("ENTRANCE_SP", Terrain.ENTRANCE_SP);
		terrainConstants.set("EXIT", Terrain.EXIT);
		terrainConstants.set("EMBERS", Terrain.EMBERS);
		terrainConstants.set("LOCKED_DOOR", Terrain.LOCKED_DOOR);
		terrainConstants.set("CRYSTAL_DOOR", Terrain.CRYSTAL_DOOR);
		terrainConstants.set("PEDESTAL", Terrain.PEDESTAL);
		terrainConstants.set("WALL_DECO", Terrain.WALL_DECO);
		terrainConstants.set("BARRICADE", Terrain.BARRICADE);
		terrainConstants.set("EMPTY_SP", Terrain.EMPTY_SP);
		terrainConstants.set("HIGH_GRASS", Terrain.HIGH_GRASS);
		terrainConstants.set("FURROWED_GRASS", Terrain.FURROWED_GRASS);

		terrainConstants.set("SECRET_DOOR", Terrain.SECRET_DOOR);
		terrainConstants.set("SECRET_TRAP", Terrain.SECRET_TRAP);
		terrainConstants.set("TRAP", Terrain.TRAP);
		terrainConstants.set("INACTIVE_TRAP", Terrain.INACTIVE_TRAP);

		terrainConstants.set("EMPTY_DECO", Terrain.EMPTY_DECO);
		terrainConstants.set("LOCKED_EXIT", Terrain.LOCKED_EXIT);

		terrainConstants.set("UNLOCKED_EXIT", Terrain.UNLOCKED_EXIT);
		terrainConstants.set("WELL", Terrain.WELL);
		terrainConstants.set("BOOKSHELF", Terrain.BOOKSHELF);
		terrainConstants.set("ALCHEMY", Terrain.ALCHEMY);

		terrainConstants.set("SIGN", Terrain.SIGN);
		terrainConstants.set("SIGN_SP", Terrain.SIGN_SP);
		terrainConstants.set("CUSTOM_DECO", Terrain.CUSTOM_DECO);
		terrainConstants.set("CUSTOM_DECO_EMPTY", Terrain.CUSTOM_DECO_EMPTY);
		terrainConstants.set("STATUE", Terrain.STATUE);
		terrainConstants.set("STATUE_SP", Terrain.STATUE_SP);
		terrainConstants.set("MINE_CRYSTAL", Terrain.MINE_CRYSTAL);
		terrainConstants.set("MINE_BOULDER", Terrain.MINE_BOULDER);
		terrainConstants.set("WATER", Terrain.WATER);

		terrainConstants.set("SECRET_LOCKED_DOOR", Terrain.SECRET_LOCKED_DOOR);
		terrainConstants.set("SECRET_CRYSTAL_DOOR", Terrain.SECRET_CRYSTAL_DOOR);
		terrainConstants.set("COIN_DOOR", Terrain.COIN_DOOR);
		terrainConstants.set("MIMIC_DOOR", Terrain.MIMIC_DOOR);

		set("Terrain", terrainConstants);

		LuaTable ballisticaConstants = new LuaTable();

		ballisticaConstants.set("STOP_TARGET", Ballistica.STOP_TARGET);
		ballisticaConstants.set("STOP_CHARS", Ballistica.STOP_CHARS);
		ballisticaConstants.set("STOP_SOLID", Ballistica.STOP_SOLID);
		ballisticaConstants.set("IGNORE_SOFT_SOLID", Ballistica.IGNORE_SOFT_SOLID);
		ballisticaConstants.set("STOP_BARRIER_PROJECTILES", Ballistica.STOP_BARRIER_PROJECTILES);
		ballisticaConstants.set("PROJECTILE", Ballistica.PROJECTILE);
		ballisticaConstants.set("REAL_PROJECTILE", Ballistica.REAL_PROJECTILE);
		ballisticaConstants.set("MAGIC_BOLT", Ballistica.MAGIC_BOLT);
		ballisticaConstants.set("REAL_MAGIC_BOLT", Ballistica.REAL_MAGIC_BOLT);
		ballisticaConstants.set("WONT_STOP", Ballistica.WONT_STOP);

		set("Ballistica", ballisticaConstants);


		addEnum(Level.Feeling.class);
		addEnum(Char.Alignment.class);
		addEnum(Char.Property.class);
		addEnum(Buff.buffType.class);
		addEnum(HeroClass.class);
		addEnum(HeroSubClass.class);
		addEnum(ArrowCell.EnterMode.class);
		addEnum(Zone.GrassType.class);
		addEnum(Heap.Type.class);
		addEnum(Armor.Augment.class);
		addEnum(Weapon.Augment.class);
		addEnum(Wand.RechargeRule.class);
		addEnum(LevelTransition.Type.class);
		addEnum(StandardRoom.SizeCategory.class);
		addEnum(CharSprite.State.class);


		LuaTable zaps = new LuaTable();

		zaps.set("attack", new VarArgFunction() {//tzz add documentation
			@Override
			public Varargs invoke(Varargs varargs) {
				LuaValue attackerLua = varargs.arg(1);
				LuaValue defenderLua = varargs.arg(2);

				//up to 2 are int (-> damage) and one may be boolean (-> magicAttack)
				LuaValue arg3 = varargs.arg(3);
				LuaValue arg4 = varargs.arg(4);
				LuaValue arg5 = varargs.arg(5);

				Char attacker = (Char) attackerLua.touserdata();
				Char defender = (Char) defenderLua.touserdata();

				int damage;
				if (arg3.isint()) {
					if (arg4.isint()) damage = Char.combatRoll( arg3.checkint(), arg4.checkint() );
					else {
						if (arg5.isint()) damage = Char.combatRoll( arg3.checkint(), arg5.checkint() );
						else damage = arg3.checkint();
					}
				}
				else if (arg4.isint()) {
					if (arg5.isint()) damage = Char.combatRoll( arg4.checkint(), arg5.checkint() );
					else damage = arg4.checkint();
				}
				else if (arg5.isint()) damage = arg5.checkint();
				else {
					if (attacker instanceof Mob) damage = Char.combatRoll( ((Mob) attacker).damageRollMin, ((Mob) attacker).damageRollMin );
					else damage = 0;
				}

				damage = Math.round(damage * AscensionChallenge.statModifier(attacker));

				boolean magicAttack;
				if (arg3.isboolean()) magicAttack = arg3.checkboolean();
				else if (arg4.isboolean()) magicAttack = arg4.checkboolean();
				else if (arg5.isboolean()) magicAttack = arg5.checkboolean();
				else magicAttack = false;

				attacker.spend_DO_NOT_CALL_UNLESS_ABSOLUTELY_NECESSARY(1f);

				Invisibility.dispel(attacker);

				if (Char.hit( attacker, defender, magicAttack )) {

					defender.damage( damage, new Object() );

					if (defender == Dungeon.hero && !defender.isAlive()) {
						if (magicAttack) Badges.validateDeathFromEnemyMagic();
						Dungeon.fail( this );
						GLog.n( Messages.get(this, "bolt_kill") );
					}
					return LuaValue.TRUE;
				} else {
					defender.sprite.showStatus( CharSprite.NEUTRAL,  defender.defenseVerb() );
					return LuaValue.FALSE;
				}

			}
		});

		abstract class ZapHandler extends VarArgFunction {
			@Override
			public Varargs invoke(Varargs varargs) {
				LuaValue parent = varargs.arg(1);
				LuaValue sprite = varargs.arg(2);
				LuaValue target = varargs.arg(3);
				LuaValue ch = varargs.arg(4);
				if (parent.isuserdata(Group.class) && sprite.isuserdata(Visual.class) && target.isint() && ch.isuserdata(Char.class)) {
					callMethod((Group) parent.touserdata(), (Visual) sprite.touserdata(), target.toint(), (Char) ch.touserdata());
				}
				return LuaValue.NIL;
			}

			protected abstract void callMethod(Group parent, Visual sprite, int target, Char ch);
		}
		zaps.set("warlock", new ZapHandler() {
			@Override
			protected void callMethod(Group parent, Visual sprite, int target, Char ch) {
				WarlockSprite.playZap(parent, sprite, target, ch);
			}
		});
//		zaps.set("dm100", new ZapHandler() {
//			@Override
//			protected void callMethod(Group parent, Visual sprite, int target, Char ch) {
//				DM100Sprite.playZap(parent, sprite, target, ch);
//			}
//		});
		zaps.set("dm200", new ZapHandler() {
			@Override
			protected void callMethod(Group parent, Visual sprite, int target, Char ch) {
				DM200Sprite.playZap(parent, sprite, target, ch);
			}
		});
		zaps.set("dm201", new ZapHandler() {
			@Override
			protected void callMethod(Group parent, Visual sprite, int target, Char ch) {
				DM201Sprite.playZap(parent, sprite, target, ch);
			}
		});
		zaps.set("fungalSpinner", new ZapHandler() {
			@Override
			protected void callMethod(Group parent, Visual sprite, int target, Char ch) {
				FungalSpinnerSprite.playZap(parent, sprite, target, ch);
			}
		});
		zaps.set("spinner", new ZapHandler() {
			@Override
			protected void callMethod(Group parent, Visual sprite, int target, Char ch) {
				SpinnerSprite.playZap(parent, sprite, target, ch);
			}
		});
		zaps.set("golem", new ZapHandler() {
			@Override
			protected void callMethod(Group parent, Visual sprite, int target, Char ch) {
				GolemSprite.playZap(parent, sprite, target, ch);
			}
		});
//		zaps.set("eye", new ZapHandler() {
//			@Override
//			protected void callMethod(Group parent, Visual sprite, int target, Char ch) {
//				EyeSprite.playZap(parent, sprite, target, ch);
//			}
//		});
		zaps.set("scorpio", new ZapHandler() {
			@Override
			protected void callMethod(Group parent, Visual sprite, int target, Char ch) {
				ScorpioSprite.playZap(parent, sprite, target, ch);
			}
		});
		zaps.set("gnollTrickster", new ZapHandler() {
			@Override
			protected void callMethod(Group parent, Visual sprite, int target, Char ch) {
				GnollTricksterSprite.playZap(parent, sprite, target, ch);
			}
		});
		zaps.set("crystalWisp", new ZapHandler() {
			@Override
			protected void callMethod(Group parent, Visual sprite, int target, Char ch) {
				CrystalWispSprite.Red.playZap(parent, (CharSprite) sprite, target, ch);
			}
		});
		zaps.set("redShaman", new ZapHandler() {
			@Override
			protected void callMethod(Group parent, Visual sprite, int target, Char ch) {
				ShamanSprite.Red.playZap(parent, sprite, target, ch);
			}
		});
		zaps.set("blueShaman", new ZapHandler() {
			@Override
			protected void callMethod(Group parent, Visual sprite, int target, Char ch) {
				ShamanSprite.Blue.playZap(parent, sprite, target, ch);
			}
		});
		zaps.set("purpleShaman", new ZapHandler() {
			@Override
			protected void callMethod(Group parent, Visual sprite, int target, Char ch) {
				ShamanSprite.Purple.playZap(parent, sprite, target, ch);
			}
		});
		zaps.set("newbornFireElemental", new ZapHandler() {
			@Override
			protected void callMethod(Group parent, Visual sprite, int target, Char ch) {
				ElementalSprite.NewbornFire.playZap(parent, sprite, target, ch);
			}
		});
		zaps.set("fireElemental", new ZapHandler() {
			@Override
			protected void callMethod(Group parent, Visual sprite, int target, Char ch) {
				ElementalSprite.Fire.playZap(parent, sprite, target, ch);
			}
		});
		zaps.set("frostElemental", new ZapHandler() {
			@Override
			protected void callMethod(Group parent, Visual sprite, int target, Char ch) {
				ElementalSprite.Frost.playZap(parent, sprite, target, ch);
			}
		});
		zaps.set("chaosElemental", new ZapHandler() {
			@Override
			protected void callMethod(Group parent, Visual sprite, int target, Char ch) {
				ElementalSprite.Chaos.playZap(parent, sprite, target, ch);
			}
		});
		zaps.set("brightFist", new ZapHandler() {
			@Override
			protected void callMethod(Group parent, Visual sprite, int target, Char ch) {
				FistSprite.Bright.playZap(parent, sprite, target, ch);
			}
		});
		zaps.set("burningFist", new ZapHandler() {
			@Override
			protected void callMethod(Group parent, Visual sprite, int target, Char ch) {
				FistSprite.Burning.playZap(parent, sprite, target, ch);
			}
		});
		zaps.set("darkFist", new ZapHandler() {
			@Override
			protected void callMethod(Group parent, Visual sprite, int target, Char ch) {
				FistSprite.Dark.playZap(parent, sprite, target, ch);
			}
		});
		zaps.set("rottingFist", new ZapHandler() {
			@Override
			protected void callMethod(Group parent, Visual sprite, int target, Char ch) {
				FistSprite.Rotting.playZap(parent, sprite, target, ch);
			}
		});
		zaps.set("rustedFist", new ZapHandler() {
			@Override
			protected void callMethod(Group parent, Visual sprite, int target, Char ch) {
				FistSprite.Rusted.playZap(parent, sprite, target, ch);
			}
		});
		zaps.set("soiledFist", new ZapHandler() {
			@Override
			protected void callMethod(Group parent, Visual sprite, int target, Char ch) {
				FistSprite.Soiled.playZap(parent, sprite, target, ch);
			}
		});
		zaps.set("warlock", new ZapHandler() {
			@Override
			protected void callMethod(Group parent, Visual sprite, int target, Char ch) {
				WarlockSprite.playZap(parent, sprite, target, ch);
			}
		});
		zaps.set("dm300", new ZapHandler() {
			@Override
			protected void callMethod(Group parent, Visual sprite, int target, Char ch) {
				DM300Sprite.playZap(parent, sprite, target, ch);
			}
		});
		zaps.set("tengu", new ZapHandler() {
			@Override
			protected void callMethod(Group parent, Visual sprite, int target, Char ch) {
				TenguSprite.playZap(parent, sprite, target, ch);
			}
		});

		set("Zaps", zaps);


		set("AC_THROW", Item.AC_THROW);
		set("AC_DROP", Item.AC_DROP);
		set("AC_END", Amulet.AC_END);
		set("AC_BLESS", Ankh.AC_BLESS);
		set("AC_APPLY", ArcaneResin.AC_APPLY);//LiquidMetal.AC_APPLY
		set("AC_AFFIX", BrokenSeal.AC_AFFIX);
		set("AC_INFO", BrokenSeal.AC_INFO);
		set("AC_EQUIP", EquipableItem.AC_EQUIP);
		set("AC_UNEQUIP", EquipableItem.AC_UNEQUIP);
		set("AC_WEAR", KingsCrown.AC_WEAR);//TengusMask.AC_WEAR
		set("AC_USE", RemainsItem.AC_USE);//MerchantsBeacon.AC_USE, InventoryStone.AC_USE
		set("AC_INSCRIBE", Stylus.AC_INSCRIBE);
		set("AC_LIGHT", Torch.AC_LIGHT);
		set("AC_DRINK", Waterskin.AC_DRINK);
		set("AC_DETACH", Armor.AC_DETACH);
		set("AC_ABILITY", ClassArmor.AC_ABILITY);
		set("AC_TRANSFER", ClassArmor.AC_TRANSFER);
		set("AC_BREW", AlchemistsToolkit.AC_BREW);
		set("AC_ENERGIZE", AlchemistsToolkit.AC_ENERGIZE);
		set("AC_PRICK", ChaliceOfBlood.AC_PRICK);
		set("AC_STEALTH", CloakOfShadows.AC_STEALTH);
		set("AC_DIRECT", DriedRose.AC_DIRECT);
		set("AC_OUTFIT", DriedRose.AC_OUTFIT);
		set("AC_SUMMON", DriedRose.AC_SUMMON);
		set("AC_CAST", EtherealChains.AC_CAST);
		set("AC_EAT", HornOfPlenty.AC_EAT);
		set("AC_SNACK", HornOfPlenty.AC_SNACK);
		set("AC_STORE", HornOfPlenty.AC_STORE);
		set("AC_RETURN", LloydsBeacon.AC_RETURN);
		set("AC_SET", LloydsBeacon.AC_SET);
		set("AC_ZAP", LloydsBeacon.AC_ZAP);
		set("AC_STEAL", MasterThievesArmband.AC_STEAL);
		set("AC_FEED", SandalsOfNature.AC_FEED);
		set("AC_ROOT", SandalsOfNature.AC_ROOT);
		set("AC_SCRY", TalismanOfForesight.AC_SCRY);
		set("AC_ACTIVATE", TimekeepersHourglass.AC_ACTIVATE);
		set("AC_ADD", UnstableSpellbook.AC_ADD);
		set("AC_READ", UnstableSpellbook.AC_READ);
		set("AC_OPEN", Bag.AC_OPEN);
		set("AC_LIGHTTHROW", Bomb.AC_LIGHTTHROW);
		set("AC_EAT", Food.AC_EAT);
		set("AC_DRINK", Potion.AC_DRINK);
		set("AC_READ", Scroll.AC_READ);
		set("AC_CAST", Spell.AC_CAST);
		set("AC_IMBUE", SummonElemental.AC_IMBUE);//MagesStaff.AC_IMBUE
		set("AC_MINE", Pickaxe.AC_MINE);
		set("AC_ABILITY", RingOfForce.AC_ABILITY);
		set("AC_ZAP", Wand.AC_ZAP);//MagesStaff.AC_ZAP
		set("AC_SHOOT", SpiritBow.AC_SHOOT);
		set("AC_TIP", Dart.AC_TIP);
		set("AC_CLEAN", TippedDart.AC_CLEAN);
		set("AC_PLANT", Plant.Seed.AC_PLANT);

	}

	private static String searchFullyQualifiedName(String simpleName) {
		switch (simpleName) {
			case "Checkpoint": return Checkpoint.class.getName();
			case "ArrowCell": return ArrowCell.class.getName();
			case "Barrier": return Barrier.class.getName();
			case "Ballistica": return Ballistica.class.getName();
		}
		if (simpleName.endsWith("$Seed")) {
			return Messages.MAIN_PACKAGE_NAME + "plants." + simpleName;
		}
		String result = null;
		if (result == null) result = searchFullyQualifiedNameInArrays(simpleName, EditorInvCategory.getAll(Items.values()));
		if (result == null) result = searchFullyQualifiedNameInArrays(simpleName, EditorInvCategory.getAll(Mobs.values()));
		if (result == null) result = searchFullyQualifiedNameInArrays(simpleName, EditorInvCategory.getAll(Plants.values()));
		if (result == null) result = searchFullyQualifiedNameInArrays(simpleName, EditorInvCategory.getAll(Traps.values()));
		if (result == null) result = searchFullyQualifiedNameInArrays(simpleName, EditorInvCategory.getAll(Buffs.values()));
		if (result == null) result = searchFullyQualifiedNameInArrays(simpleName, EditorInvCategory.getAll(MobSprites.values()));
		return result;
	}

	private static String searchFullyQualifiedNameForInstanceof(String simpleName) {
		switch (simpleName) {
			case "Mob": return Mob.class.getName();
			case "QuestNPC": return QuestNPC.class.getName();
			case "NPC": return NPC.class.getName();
			case "Buff": return Buff.class.getName();
			case "Checkpoint": return Checkpoint.class.getName();
			case "ArrowCell": return ArrowCell.class.getName();
			case "Barrier": return Barrier.class.getName();
			case "Ballistica": return Ballistica.class.getName();
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
		if (simpleName.endsWith("$Seed")) {
			return Messages.MAIN_PACKAGE_NAME + "plants." + simpleName;
		}
		String result = null;
		if (result == null) result = searchFullyQualifiedNameInArrays(simpleName, EditorInvCategory.getAll(Mobs.values()));
		if (result == null) result = searchFullyQualifiedNameInArrays(simpleName, EditorInvCategory.getAll(Items.values()));
		if (result == null) result = searchFullyQualifiedNameInArrays(simpleName, EditorInvCategory.getAll(Buffs.values()));
		if (result == null) result = searchFullyQualifiedNameInArrays(simpleName, EditorInvCategory.getAll(Traps.values()));
		if (result == null) result = searchFullyQualifiedNameInArrays(simpleName, EditorInvCategory.getAll(Plants.values()));
		if (result == null) result = searchFullyQualifiedNameInArrays(simpleName, EditorInvCategory.getAll(MobSprites.values()));
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

	private static String luaToString(LuaValue luaValue) {
		if (luaValue.isstring()) return luaValue.checkjstring();
		if (luaValue.isint()) return String.valueOf(luaValue.checkint());
		if (luaValue.islong()) return String.valueOf(luaValue.checklong());
		if (luaValue.isboolean()) return String.valueOf(luaValue.checkboolean());
		if (luaValue.isnil()) return "nil";
		if (luaValue.isuserdata()) return luaValue.touserdata().toString();
		return null;
	}

	private void addEnum(Class<? extends Enum<?>> enumClass) {

		LuaTable values = new LuaTable();
		for (Enum<?> e : enumClass.getEnumConstants()) {
			values.set(e.name(), LuaValue.userdataOf(e));
		}

		Class<?> mostEnclosingClass = enumClass;

		do {
			Class<?> enclosingClass = mostEnclosingClass.getEnclosingClass();

			if (enclosingClass == null) break;

			LuaTable enclosingTable = new LuaTable();
			enclosingTable.set(mostEnclosingClass.getSimpleName(), values);

			values = enclosingTable;
			mostEnclosingClass = enclosingClass;
		} while (true);

		set(mostEnclosingClass.getSimpleName(), values);
	}

//	private static LuaTable arrayToTable(Object array) {
//		LuaTable result = new LuaTable();
//
//		int length = Array.getLength(array);
//		for (int i = 0; i < length; ++i) {
//
//			Object v = Array.get(array, i);
//			result.set(i, v != null && v.getClass().isArray()
//					? arrayToTable(v)
//					: CoerceJavaToLua.coerce(v));
//
//		}
//		return result;
//	}
}