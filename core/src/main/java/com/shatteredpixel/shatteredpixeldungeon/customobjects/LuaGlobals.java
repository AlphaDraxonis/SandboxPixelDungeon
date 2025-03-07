/*
 *
 *  * Pixel Dungeon
 *  * Copyright (C) 2012-2015 Oleg Dolya
 *  *
 *  * Shattered Pixel Dungeon
 *  * Copyright (C) 2014-2024 Evan Debenham
 *  *
 *  * Sandbox Pixel Dungeon
 *  * Copyright (C) 2023-2024 AlphaDraxonis
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */

package com.shatteredpixel.shatteredpixeldungeon.customobjects;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SandboxPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DMMob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Elemental;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Goo;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.SpawnerMob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.YogFist;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.editor.ArrowCell;
import com.shatteredpixel.shatteredpixeldungeon.editor.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.editor.Checkpoint;
import com.shatteredpixel.shatteredpixeldungeon.editor.WndCreator;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditArrowCellComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditBarrierComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditBuffComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditCheckpointComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditHeapComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditItemComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditMobComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditPlantComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditRoomComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditTrapComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Buffs;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.CursedWandEffects;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Enchantments;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.GameObjectCategory;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Items;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.MobSprites;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Mobs;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Plants;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Traps;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.Zone;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.LuaRestrictionProxy;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.QuestNPC;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.effects.Effects;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.Amulet;
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh;
import com.shatteredpixel.shatteredpixeldungeon.items.ArcaneResin;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.KindofMisc;
import com.shatteredpixel.shatteredpixeldungeon.items.KingsCrown;
import com.shatteredpixel.shatteredpixeldungeon.items.Stylus;
import com.shatteredpixel.shatteredpixeldungeon.items.Torch;
import com.shatteredpixel.shatteredpixeldungeon.items.Waterskin;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.AlchemistsToolkit;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ChaliceOfBlood;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.EtherealChains;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HornOfPlenty;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.LloydsBeacon;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.MasterThievesArmband;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.SandalsOfNature;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.UnstableSpellbook;
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
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.Spell;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.SummonElemental;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.Runestone;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.CursedWand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.Dart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.TippedDart;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.CavesBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.BranchesBuilder;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.Builder;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.FigureEightBuilder;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.LineBuilder;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.LoopBuilder;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.RegularBuilder;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.CavesPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.CityPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.HallsPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.MiningLevelPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.PrisonPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.RegularPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.SewerPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ShadowCaster;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.*;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.DungeonSeed;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.utils.Holiday;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndResurrect;
import com.watabou.NotAllowedInLua;
import com.watabou.noosa.Game;
import com.watabou.noosa.Gizmo;
import com.watabou.noosa.Group;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import com.watabou.utils.SparseArray;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@NotAllowedInLua
public class LuaGlobals extends Globals {

	LuaGlobals() {

		Globals standardGlobals = JsePlatform.standardGlobals();
		compiler = standardGlobals.compiler;
		loader = standardGlobals.loader;

		set("_VERSION", standardGlobals.get("_VERSION"));
		set("tostring", standardGlobals.get("tostring"));
		set("tonumber", standardGlobals.get("tonumber"));
		set("type", standardGlobals.get("type"));
		set("pairs", standardGlobals.get("pairs"));
		set("next", standardGlobals.get("next"));
		set("table", standardGlobals.get("table"));
		set("rawlen", standardGlobals.get("rawlen"));
		set("rawequal", standardGlobals.get("rawequal"));
		set("assert", standardGlobals.get("assert"));
		set("error", standardGlobals.get("error"));
		set("math", standardGlobals.get("math"));

		final LuaFunction newInstance = standardGlobals.get("luajava").get("newInstance").checkfunction();
		set("new", new VarArgFunction() {

			@Override
			public Varargs invoke(Varargs varargs) {
				LuaValue arg = varargs.arg1();
				Object javaResult;
				if (arg.isstring()) {
					
					String s = arg.checkjstring();
					
					if (s.endsWith("[]")) {
						if (s.startsWith("int")) javaResult = new int[varargs.arg(2).checkint()];
						else if (s.startsWith("byte")) javaResult = new byte[varargs.arg(2).checkint()];
						else if (s.startsWith("boolean")) javaResult = new boolean[varargs.arg(2).checkint()];
						else if (s.startsWith("String")) javaResult = new String[varargs.arg(2).checkint()];
						else
							throw new LuaError("Class not found: " + arg.checkstring());
					} else {
						
						String fullName = s.startsWith(Messages.MAIN_PACKAGE_NAME) || s.startsWith(Messages.WATABOU_PACKAGE_NAME)
								? s
								: searchFullyQualifiedName(s);
						
						Class<?> c;
						if (fullName == null || (c = Reflection.forName(fullName)) == null) {
							fullName = Messages.MAIN_PACKAGE_NAME + s;
							
							if (Reflection.forName(fullName) == null)
								throw new LuaError("Class not found: " + arg.checkstring());
							
							javaResult = newInstance.invoke(LuaValue.valueOf(fullName), varargs.subargs(2)).arg1().touserdata();
						} else {
							
							Object[] params = LuaRestrictionProxy.unwrapRestrictionProxiesAsJavaArray(varargs.subargs(2));
							
							Constructor<?> constructor = null;
							
							oneConstructor:
							for (Constructor<?> constr : c.getConstructors()) {
								Class<?>[] paramTypes = constr.getParameterTypes();
								
								if (paramTypes.length == params.length) {
									
									for (int i = 0; i < paramTypes.length; i++) {
										if (!paramTypes[i].isAssignableFrom(params[i].getClass())) {
											continue oneConstructor;
										}
									}
									constructor = constr;
									break;
								}
								
							}
							if (constructor == null) {
								throw new LuaError("No matching constructor found: " + arg.checkstring());
							} else {
								try {
									javaResult = constructor.newInstance(params);
								} catch (Exception e) {
									throw new LuaError(e);
								}
							}
						}
						
					}

					if (javaResult != null) {

						if (!LuaRestrictionProxy.isRestricted(javaResult)) {
							return LuaRestrictionProxy.wrapObject(javaResult);
						}
						else {
							if (javaResult instanceof Gizmo) ((Gizmo) javaResult).destroy();
							throw new IllegalArgumentException(
									"Instancing class " + arg.checkjstring() + " is not permitted for security reasons!");
						}
					}

				}
				throw new LuaError("Illegal arguments: use new(String className)");
			}
		});

		set("newCus", new VarArgFunction() {

			@Override
			public Varargs invoke(Varargs varargs) {
				LuaValue arg = varargs.arg1();
				if (arg.isstring()) {
					String s = arg.tojstring();
					for (CustomObject obj : CustomObjectManager.allUserContents.values()) {
						if (obj.getName().equals(s) && obj instanceof LuaCustomObject)
							return CoerceJavaToLua.coerce(((LuaCustomObject) obj).newInstance());
					}
					throw new LuaError("No custom object with name \"" + s + "\" was found!");
				}
				else if (arg.isint()) {
					CustomObject original = CustomObjectManager.allUserContents.get(arg.checkint());
					if (original instanceof LuaCustomObject)
						return CoerceJavaToLua.coerce(((LuaCustomObject) original).newInstance());
					throw new LuaError("No custom object with id \"" + arg.checkint() + "\" was found!");

				}
				throw new LuaError("Illegal arguments: use newCus(String name) or newCus(int id)");
			}
		});

		set("class", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue arg) {
				Object javaResult = null;
				if (arg.isstring()) {
					if (arg.checkjstring().startsWith(Messages.MAIN_PACKAGE_NAME)
							|| arg.checkjstring().startsWith(Messages.WATABOU_PACKAGE_NAME)) {
						javaResult = Reflection.forName(arg.checkjstring());
					} else {
						String fullName = searchFullyQualifiedName(arg.checkjstring());
						if (fullName != null) javaResult = Reflection.forName(fullName);
						else {
							fullName = Messages.MAIN_PACKAGE_NAME + arg.checkjstring();
							Class<?> c = Reflection.forName(fullName);

							if (c == null)
								throw new LuaError("Class not found: " + arg.checkstring());

							javaResult = c;
						}
					}

					if (javaResult != null) {

						if (!LuaRestrictionProxy.isRestricted(javaResult)) {
							return LuaRestrictionProxy.wrapObject(javaResult);
						}
						else {
							throw new IllegalArgumentException(
									"Instancing class " + arg.checkjstring() + " is not permitted for security reasons!");
						}
					}

				}
				throw new LuaError("Illegal arguments: use class(String className)");
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
					return LuaRestrictionProxy.wrapObject(ballistica);
				}
				throw new LuaError("Illegal arguments: use ballistica(int from, int to, int params) or ballistica(int from, int to, int params, Char usePassableFromChar)");
			}
		});

		set("tostring", standardGlobals.get("tostring"));

		set("toPos", new ThreeArgFunction() {
			@Override
			public LuaValue call(LuaValue x, LuaValue y, LuaValue level) {
				if (!x.isint() || !y.isint()) throw new LuaError("Illegal arguments: use toPos(int x, int y)");
				Level l = level.isuserdata() ? (Level) level.touserdata(Level.class) : null;
				if (l == null) l = Dungeon.level;
				return LuaValue.valueOf(x.toint() - 1 + y.toint() * l.width());
			}
		});

		LuaTable arrayUtils = new LuaTable();
		arrayUtils.set("length", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue array) {
				return LuaValue.valueOf(Array.getLength(LuaRestrictionProxy.coerceLuaToJava(array)));
			}
		});
		arrayUtils.set("set", new ThreeArgFunction() {
			@Override
			public LuaValue call(LuaValue array, LuaValue index, LuaValue value) {
				Object javaArray = LuaRestrictionProxy.coerceLuaToJava(array);
				if (javaArray.getClass().getComponentType() == byte.class || javaArray.getClass().getComponentType() == Byte.class) {
					Array.set(javaArray, index.toint(), Byte.parseByte(Integer.toString((Integer) LuaRestrictionProxy.coerceLuaToJava(value))));
				} else {
					Array.set(javaArray, index.toint(), LuaRestrictionProxy.coerceLuaToJava(value));
				}
				return LuaValue.NIL;
			}
		});
		arrayUtils.set("get", new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue array, LuaValue index) {
				return LuaRestrictionProxy.wrapObject(Array.get(LuaRestrictionProxy.coerceLuaToJava(array), index.toint()));
			}
		});
		arrayUtils.set("iterate", new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue array, LuaValue consumer) {

				if (consumer.isnil()) throw new LuaError("Illegal arguments: consumer must not be null: use something like   function(obj) print(obj) end   as second parameter ");

				Object java = LuaRestrictionProxy.coerceLuaToJava(array);
				LuaFunction function = consumer.checkfunction();
				if (java instanceof SparseArray) {
					for (Object obj : ((SparseArray<?>) java).values()) {
						function.call(LuaRestrictionProxy.wrapObject(obj));
					}
					return LuaValue.TRUE;
				}
				else if (java instanceof Map<?, ?>) {
					for (Object obj : ((Map<?, ?>) java).values()) {
						function.call(LuaRestrictionProxy.wrapObject(obj));
					}
					return LuaValue.TRUE;
				}
				else if (java instanceof Iterable) {
					for (Object obj : ((Iterable<?>) java)) {
						function.call(LuaRestrictionProxy.wrapObject(obj));
					}
					return LuaValue.TRUE;
				}
				else if (java.getClass().isArray()) {
					int length = Array.getLength(java);
					for (int i = 0; i < length; i++) {
						function.call(LuaRestrictionProxy.wrapObject(Array.get(java, i)));
					}
				} else {
					throw new LuaError("Illegal arguments: collection must be a java array or a SparseArray or implement Iterable or Map<?,?>");
				}
				return LuaValue.NIL;
			}
		});
		set("Arrays", arrayUtils);

		set("getActor", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue ID) {
				if (!ID.isint()) throw new LuaError("Illegal arguments: use getActor(int id)");
				return new LuaUserdata(Actor.findById(ID.checkint()));
			}
		});

		LuaTable messages = new LuaTable();
		messages.set("get", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue key) {
				if (!key.isstring()) throw new LuaError("Illegal arguments: use Messages.get(String key)");
				return LuaValue.valueOf(Messages.get(key.checkjstring()));
			}
		});
		set("Messages", messages);

		LuaTable randomUtils = new LuaTable();
		randomUtils.set("int", new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue min, LuaValue max) {
				if (max.isnil()) {
					if (!min.isint()) throw new LuaError("Illegal arguments: use Random.int(int max)");
					return LuaValue.valueOf(Random.Int(min.checkint()));
				}
				if (!min.isint() || !max.isint()) throw new LuaError("Illegal arguments: use Random.int(int min, int max)");
				return LuaValue.valueOf(Random.Int(min.checkint(), max.checkint()));
			}
		});
		randomUtils.set("float", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return LuaValue.valueOf(Random.Float());
			}
		});
		randomUtils.set("combatRoll", new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue min, LuaValue max) {
				if (max.isnil()) {
					if (!min.isint()) throw new LuaError("Illegal arguments: use Random.combatRoll(int max)");
					return LuaValue.valueOf(Random.NormalIntRange(0, min.checkint()));
				}
				if (!min.isint() || !max.isint()) throw new LuaError("Illegal arguments: use Random.combatRoll(int min, int max)");
				return LuaValue.valueOf(Random.NormalIntRange(min.checkint(), max.checkint()));
			}
		});
		randomUtils.set("element", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue collection) {
				Object java = LuaRestrictionProxy.coerceLuaToJava(collection);
				if (java instanceof SparseArray<?>) {
					return LuaRestrictionProxy.wrapObject(Random.element((((SparseArray<?>) java).valueList())));
				}
				else if (java instanceof Collection<?>) {
					return LuaRestrictionProxy.wrapObject(Random.element(((Collection<?>) java)));
				}
				else if (java instanceof Map<?, ?>) {
					return LuaRestrictionProxy.wrapObject(Random.element(((Map<?, ?>) java).values()));
				}
				else if (java.getClass().isArray()) {
					return LuaRestrictionProxy.wrapObject(Array.get(java, Random.Int(Array.getLength(java))));
				}
				throw new LuaError("Illegal arguments: collection must be a java array or a SparseArray or implement Collection<?> or Map<?,?>");
			}
		});
		randomUtils.set("pushGenerator", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue seed) {
				if (seed.islong()) {
					Random.pushGenerator(seed.checklong());
					return LuaValue.NIL;
				}
				throw new LuaError("Illegal arguments: use Random.pushGenerator(long seed)");
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
		set("showWindow", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue window) {
				Game.runOnRenderThread(() -> {
					try {
						DungeonScene.show((Window) LuaRestrictionProxy.coerceLuaToJava(window, Window.class));
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
								LuaValue result = itemSelectable.checkfunction().call(LuaRestrictionProxy.wrapObject(item));
								return result.isboolean() && result == LuaValue.TRUE;
							} catch (LuaError error) { Game.runOnRenderThread(() ->	DungeonScene.show(new WndError(error))); }
							return false;
						}

						@Override
						public void onSelect(Item item) {
							try {
								onSelect.call(LuaRestrictionProxy.wrapObject(item));
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
				if (!cell.isint()) throw new LuaError("Illegal arguments: use cellToString(int cell)");
				return LuaValue.valueOf(EditorUtilities.cellToString(cell.checkint()));
			}
		});
		
		set("playSound", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue soundFile) {
				if (!soundFile.isstring()) throw new LuaError("Illegal arguments: use playSound(String soundFile)");
				if (Sample.INSTANCE.play("sounds/" + soundFile.checkjstring() + ".mp3") == -1) throw new LuaError("Invalid sound file: you can only use existing sounds, values found are in Assets.Sounds (without sounds/ and .mp3)");
				return LuaValue.TRUE;
			}
		});


		set("spawnMob", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue mob) {//returns false to indicate that no spawn point was found by randomly selecting many cells of the level
				if (mob.isuserdata()) {
					Mob m = (Mob) LuaRestrictionProxy.coerceLuaToJava(mob, Mob.class);
					if (m != null) {
						return LuaValue.valueOf(Dungeon.level.spawnMob(m, 12, null));
					}
				}
				throw new LuaError("Illegal arguments: use spawnMob(Mob mob)");
			}
		});
		set("placeMob", new TwoArgFunction() {//no valid placement check is made
			@Override
			public LuaValue call(LuaValue mob, LuaValue pos) {
				if (mob.isuserdata() && pos.isint()) {
					Mob m = (Mob) LuaRestrictionProxy.coerceLuaToJava(mob, Mob.class);
					if (m != null) {
						if (Dungeon.level.mobs.contains(m)) {
							ScrollOfTeleportation.appear(m, pos.checkint(), true);
						} else {
							m.pos = pos.checkint();
							Level.placeMob(m);
						}
						return LuaValue.NIL;
					}
				}
				throw new LuaError("Illegal arguments: use placeMob(Mob mob, int pos)");
			}
		});

		set("affectBuff", new ThreeArgFunction() {
			@Override
			public LuaValue call(LuaValue target, LuaValue buff, LuaValue duration) {
				if (target.isuserdata() && buff.isuserdata()) {
					Char ch = (Char) LuaRestrictionProxy.coerceLuaToJava(target, Char.class);
					if (ch != null) {
						Object b = LuaRestrictionProxy.coerceLuaToJava(buff);
						Class<?> buffClass = b instanceof Class ? (Class<?>) b : b.getClass();

						if (duration.isnil() || !duration.isnumber() || !FlavourBuff.class.isAssignableFrom(buffClass)) {

							if (Buff.class.isAssignableFrom(buffClass)) {
								return LuaRestrictionProxy.wrapObject(Buff.affect(ch, ((Class<? extends Buff>) buffClass)));
							}

						} else {
							return LuaRestrictionProxy.wrapObject(Buff.affect(ch, ((Class<? extends FlavourBuff>) buffClass), duration.tofloat()));
						}

					}
				}
				throw new LuaError("Illegal arguments: use affectBuff(Char target, Buff buff) or affectBuff(Char target, Class<? extends Buff> buff) or affectBuff(Char target, Buff buff, float duration) or affectBuff(Char target, Class<? extends Buff> buff, float duration)");
			}
		});

		set("drop", new ThreeArgFunction() {//no valid placement check is made
			@Override
			public LuaValue call(LuaValue item, LuaValue pos, LuaValue from) {
				if (item.isuserdata() && pos.isint()) {
					Item i = (Item) LuaRestrictionProxy.coerceLuaToJava(item, Item.class);
					if (i != null) {
						if (!from.isint()) Dungeon.level.drop(i, pos.toint()).sprite.drop();
						else Dungeon.level.drop(i, pos.toint()).sprite.drop(from.checkint());
						return LuaValue.NIL;
					}
				}
				throw new LuaError("Illegal arguments: use drop(Item item, int pos) or drop(Item item, int pos, int from)");
			}
		});

		set("giveItem", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue item) {
				if (item.isuserdata()) {
					Item i = (Item) LuaRestrictionProxy.coerceLuaToJava(item, Item.class);
					if (i != null) {
						if (!i.collect()) {
							Dungeon.level.drop(i, Dungeon.hero.pos);
						}
						return LuaValue.NIL;
					}
				}
				throw new LuaError("Illegal arguments: use giveItem(Item item)");
			}
		});
		set("collectKey", new TwoArgFunction() {
			@Override
			public LuaValue call(LuaValue itemKey, LuaValue pos) {
				if (!itemKey.isuserdata()) throw new LuaError("Illegal arguments: use collectKey(Key key) or collectKey(Key key, int fromCell)");
				Object obj = itemKey.checkuserdata();
				if (!(obj instanceof Key)) throw new LuaError("Illegal arguments: use collectKey(Key key) or collectKey(Key key, int fromCell)");
				int cell = pos.isint() ? pos.checkint() : Dungeon.hero.pos;
				((Key) obj).instantPickupKey(cell);
				return LuaValue.TRUE;
			}
		});

		set("runOnRenderThread", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue function) {
				try {
					LuaFunction run = function.checkfunction();
					Game.runOnRenderThread(() -> {
						try {
							run.invoke();
						} catch (LuaError e) {
							DungeonScene.show(new WndError(e));
						}
					});
					return LuaValue.NIL;
				} catch (Exception e) {
					throw new LuaError("Illegal arguments: must provide a function, use runOnRenderThread( function() print(\"example\") end )");
				}
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
					return LuaValue.NIL;
				}
				throw new LuaError("Illegal arguments: use updateCell(int cell)");
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
						if (className2 == null || (clazz = Reflection.forName(className2)) == null) {
							className2 = Messages.MAIN_PACKAGE_NAME + className.checkjstring();
							if ((clazz = Reflection.forName(className2)) == null) {
								return LuaValue.valueOf("Class not found: " + className.checkstring());
							}
						}
					}
					return LuaValue.valueOf(clazz.isInstance(obj.checkuserdata()));
				}
				throw new LuaError("Illegal arguments: use instanceof(Object obj, String className)");
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

					return LuaValue.FALSE;
				}
				throw new LuaError("Illegal arguments: use areEqual(Object a, Object b)");
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
				if (!gold.isint()) throw new LuaError("Illegal arguments: use setGold(int gold)");
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
				if (!energy.isint()) throw new LuaError("Illegal arguments: use setEnergy(int energy)");
				Dungeon.energy = energy.checkint();
				return LuaValue.valueOf(Dungeon.energy);
			}
		});

		set("newCallback", new OneArgFunction() {
			@Override
			public LuaValue call(LuaValue function) {
				if (function.isfunction()) {
					return LuaRestrictionProxy.wrapObject(new Callback() {
						@Override
						public void call() {
							function.call();
						}
					});
				}
				throw new LuaError("Illegal arguments: use newCallback( luaFunction )");
			}
		});

		set("isEditing", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				return LuaValue.valueOf(CustomDungeon.isEditing());
			}
		});

		


		addStaticFinals(Terrain.class);
		addStaticFinals(Ballistica.class);
		addStaticFinals(ConeAOE.class);
		addStaticFinals(ShadowCaster.class);
		addStaticFinals(Bestiary.class);
		addEnum(Bestiary.class);
		addEnum(Catalog.class);
		addStaticFinals(Notes.class);
		addEnum(Notes.Landmark.class);
		addStaticFinals(FloatingText.class);
		addStaticFinals(CharSprite.class);
		addStaticFinals(ItemSpriteSheet.class);
		addStaticFinals(Effects.class);
		addEnum(Effects.Type.class);
		addStaticFinals(Room.class);
		addStaticFinals(Painter.class);

		addStaticFinals(Messages.class);
		addStaticFinals(Languages.class);
		addEnum(Languages.class);

		addStaticFinals(Assets.class);
		addStaticFinals(Assets.Sounds.class);
		addStaticFinals(Badges.class);
		addStaticFinals(Challenges.class);
		addStaticFinals(Chrome.class);
		addStaticFinals(Dungeon.class);
		addStaticFinals(Statistics.class);
		addStaticFinals(EditorUtilities.class);

		addStaticFinals(CrystalGuardianSprite.class);
		addStaticFinals(CrystalWispSprite.class);
		addStaticFinals(DM100Sprite.class);
		addStaticFinals(DM200Sprite.class);
		addStaticFinals(DM201Sprite.class);
		addStaticFinals(DM300Sprite.class);
		addStaticFinals(ElementalSprite.Fire.class);
		addStaticFinals(ElementalSprite.Frost.class);
		addStaticFinals(ElementalSprite.Shock.class);
		addStaticFinals(ElementalSprite.Chaos.class);
		addStaticFinals(ElementalSprite.NewbornFire.class);
		addStaticFinals(EyeSprite.class);
		addStaticFinals(FistSprite.Bright.class);
		addStaticFinals(FistSprite.Burning.class);
		addStaticFinals(FistSprite.Soiled.class);
		addStaticFinals(FistSprite.Rotting.class);
		addStaticFinals(FistSprite.Rusted.class);
		addStaticFinals(FistSprite.Dark.class);
		addStaticFinals(FungalSentrySprite.class);
		addStaticFinals(FungalSpinnerSprite.class);
		addStaticFinals(GhoulSprite.class);
		addStaticFinals(GolemSprite.class);
		addStaticFinals(Goo.class);
		addStaticFinals(MimicSprite.class);
		addStaticFinals(RipperSprite.class);
		addStaticFinals(ScorpioSprite.class);
		addStaticFinals(ShamanSprite.Red.class);
		addStaticFinals(ShamanSprite.Blue.class);
		addStaticFinals(ShamanSprite.Purple.class);
		addStaticFinals(SpinnerSprite.class);
		addStaticFinals(StatueSprite.class);
		addStaticFinals(TenguSprite.class);
		addStaticFinals(WarlockSprite.class);

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
		addEnum(Chrome.Type.class);
		addEnum(Dungeon.LimitedDrops.class);


		addStaticFinals(Math.class);
		addStaticFinals(Collections.class);
		addStaticFinals(Arrays.class);
		addStaticFinals(DungeonSeed.class);
		addStaticFinals(GLog.class);
		addStaticFinals(Holiday.class);


		LuaTable zaps = new LuaTable();

		zaps.set("attack", new VarArgFunction() {
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
					if (arg4.isint()) damage = Random.NormalIntRange( arg3.checkint(), arg4.checkint() );
					else {
						if (arg5.isint()) damage = Random.NormalIntRange( arg3.checkint(), arg5.checkint() );
						else damage = arg3.checkint();
					}
				}
				else if (arg4.isint()) {
					if (arg5.isint()) damage = Random.NormalIntRange( arg4.checkint(), arg5.checkint() );
					else damage = arg4.checkint();
				}
				else if (arg5.isint()) damage = arg5.checkint();
				else {
					if (attacker instanceof Mob) damage = Random.NormalIntRange( ((Mob) attacker).damageRollMin, ((Mob) attacker).damageRollMin );
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
						Dungeon.fail( attacker );
						GLog.n( Messages.get(attacker, "kill", attacker.name()) );
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
					new Thread(() -> callMethod((Group) parent.touserdata(), (Visual) sprite.touserdata(), target.toint(), (Char) ch.touserdata())).start();
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

	public static String searchFullyQualifiedName(String simpleName) {
		switch (simpleName) {
			case "Checkpoint": return Checkpoint.class.getName();
			case "ArrowCell": return ArrowCell.class.getName();
			case "Barrier": return Barrier.class.getName();
			case "Ballistica": return Ballistica.class.getName();
			case "LevelTransition": return LevelTransition.class.getName();
			case "Set": return HashSet.class.getName();
			case "List": return ArrayList.class.getName();
			case "Map": return HashMap.class.getName();
			case "LandmarkRecord": return Notes.LandmarkRecord.class.getName();

			case "Date": return Date.class.getName();
		}
		if (simpleName.endsWith("$Seed")) {
			return Messages.MAIN_PACKAGE_NAME + "plants." + simpleName;
		}
		String result = null;
		if (result == null) result = otherAccessibleClasses.get(simpleName);
		if (result == null) result = searchFullyQualifiedNameInArrays(simpleName, GameObjectCategory.getAll(Items.instance().values()));
		if (result == null) result = searchFullyQualifiedNameInArrays(simpleName, GameObjectCategory.getAll(Mobs.instance().values()));
		if (result == null) result = searchFullyQualifiedNameInArrays(simpleName, GameObjectCategory.getAll(Plants.instance().values()));
		if (result == null) result = searchFullyQualifiedNameInArrays(simpleName, GameObjectCategory.getAll(Traps.instance().values()));
		if (result == null) result = searchFullyQualifiedNameInArrays(simpleName, GameObjectCategory.getAll(Buffs.instance().values()));
		if (result == null) result = searchFullyQualifiedNameInArrays(simpleName, GameObjectCategory.getAll(MobSprites.instance().values()));
		if (result == null) result = searchFullyQualifiedNameInArrays(simpleName, GameObjectCategory.getAll(Enchantments.instance().values()));
		if (result == null) result = searchFullyQualifiedNameInArrays(simpleName, GameObjectCategory.getAll(CursedWandEffects.instance().values()));
		return result;
	}

	private static String searchFullyQualifiedNameForInstanceof(String simpleName) {
		switch (simpleName) {
			case "Set": return Set.class.getName();
			case "List": return List.class.getName();
			case "Map": return Map.class.getName();
		}
		if (simpleName.endsWith("$Seed")) {
			return Messages.MAIN_PACKAGE_NAME + "plants." + simpleName;
		}
		String result = null;
		if (result == null) result = otherAccessibleClasses.get(simpleName);
		if (result == null) result = searchFullyQualifiedNameInArrays(simpleName, GameObjectCategory.getAll(Mobs.instance().values()));
		if (result == null) result = searchFullyQualifiedNameInArrays(simpleName, GameObjectCategory.getAll(Items.instance().values()));
		if (result == null) result = searchFullyQualifiedNameInArrays(simpleName, GameObjectCategory.getAll(Buffs.instance().values()));
		if (result == null) result = searchFullyQualifiedNameInArrays(simpleName, GameObjectCategory.getAll(Traps.instance().values()));
		if (result == null) result = searchFullyQualifiedNameInArrays(simpleName, GameObjectCategory.getAll(Plants.instance().values()));
		if (result == null) result = searchFullyQualifiedNameInArrays(simpleName, GameObjectCategory.getAll(MobSprites.instance().values()));
		if (result == null) result = searchFullyQualifiedNameInArrays(simpleName, GameObjectCategory.getAll(Enchantments.instance().values()));
		if (result == null) result = searchFullyQualifiedNameInArrays(simpleName, GameObjectCategory.getAll(CursedWandEffects.instance().values()));
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

	private static String searchFullyQualifiedNameInArrays(String simpleName, Class<?>[] classes) {
		for (int i = 0; i < classes.length; i++) {
			if (classes[i].getSimpleName().equals(simpleName)) return classes[i].getName();
		}
		return null;
	}

	private static String luaToString(LuaValue luaValue) {
		if (luaValue.isstring()) return luaValue.checkjstring();
		if (luaValue.isint()) return String.valueOf(luaValue.checkint());
		if (luaValue.islong()) return String.valueOf(luaValue.checklong());
		if (luaValue.isboolean()) return String.valueOf(luaValue.checkboolean());
		if (luaValue.isnil()) return "nil";
		return String.valueOf(LuaRestrictionProxy.coerceLuaToJava(luaValue));
	}

	private void addEnum(Class<? extends Enum<?>> enumClass) {

		LuaTable values = new LuaTable();
		for (Enum<?> e : enumClass.getEnumConstants()) {
			values.set(e.name(), LuaRestrictionProxy.wrapObject(e));
		}

		addConstantsTable(enumClass, values);
	}

	private static final int PUBLIC_STATIC_FINAL = Modifier.STATIC | Modifier.PUBLIC | Modifier.FINAL;
	private static final int PROTECTED_STATIC_FINAL = Modifier.STATIC | Modifier.PROTECTED | Modifier.FINAL;
	private static final int PUBLIC_STATIC = Modifier.STATIC | Modifier.PUBLIC;
	private static final int PROTECTED_STATIC = Modifier.STATIC | Modifier.PROTECTED;
	//adds methods and variables
	private void addStaticFinals(Class<?> clazz) {
		LuaTable values = new LuaTable();
		try {
			for (Field f : clazz.getDeclaredFields()) {
				int mods = f.getModifiers();
				if ((0x00004000 & f.getModifiers()) != 0x00004000 //Modifier.ENUM
						&& (mods & PUBLIC_STATIC_FINAL) == PUBLIC_STATIC_FINAL || (mods & PROTECTED_STATIC_FINAL) == PROTECTED_STATIC_FINAL) {
					values.set(f.getName(), LuaRestrictionProxy.wrapObject(f.get(null)));
				}
			}
			for (Method m : clazz.getDeclaredMethods()) {
				if (m.isAnnotationPresent(NotAllowedInLua.class)) continue;
				int mods = m.getModifiers();
				if ((mods & PUBLIC_STATIC) == PUBLIC_STATIC
					|| (mods & PROTECTED_STATIC) == PROTECTED_STATIC) {
					values.set(m.getName(), new VarArgFunction() {
						@Override
						public Varargs invoke(Varargs varargs) {
							try {
								return LuaRestrictionProxy.wrapObject(m.invoke(null, convertLuaToArgs(varargs)));
							} catch (Exception e) {
								throw new LuaError(e);
							}
						}
					});
				}
			}
		} catch (IllegalAccessException e) {
			Game.reportException(e);
		}

		addConstantsTable(clazz, values);

	}

	private void addConstantsTable(Class<?> sourceClass, LuaTable values) {
		Class<?> mostEnclosingClass = sourceClass;

		do {
			Class<?> enclosingClass = mostEnclosingClass.getEnclosingClass();

			if (enclosingClass == null) break;

			LuaTable enclosingTable;
			LuaValue existingTable = get(enclosingClass.getSimpleName());
			if (!existingTable.isnil() && existingTable.istable()) {
				enclosingTable = existingTable.checktable();
			} else {
				enclosingTable = new LuaTable();
			}
			enclosingTable.set(mostEnclosingClass.getSimpleName(), values);

			values = enclosingTable;
			mostEnclosingClass = enclosingClass;
		} while (true);

		insertConstants(this, values, mostEnclosingClass.getSimpleName());

	}

	private static void insertConstants(LuaTable current, LuaValue toInsert, String key) {
		insertConstants(current, toInsert, LuaValue.valueOf(key));
	}

	private static void insertConstants(LuaTable current, LuaValue toInsert, LuaValue key) {
		LuaValue existingTable = current.get(key);
		if (!existingTable.isnil()) {
			if (existingTable.istable()) {
				if (toInsert.istable()) {
					for (LuaValue k : toInsert.checktable().keys()) {
						insertConstants(existingTable.checktable(), toInsert.get(k), k);
					}
				} else {
//					Game.reportException(new Exception("Could not insert key " + key + "!"));
				}
			} else {
				if (toInsert != existingTable && !toInsert.equals(existingTable));
//					Game.reportException(new Exception("Could not insert table " + key + "!"));
			}
		} else {
			current.set(key, toInsert);
		}
	}

	private static Object[] convertLuaToArgs(Varargs args) {
		Object[] result = new Object[args.narg()];
		for (int i = 0; i < result.length; i++) {
			result[i] = CoerceLuaToJava.coerce(args.arg(i+1), Object.class);
		}
		return result;
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

	private static final Map<String, String> otherAccessibleClasses = new HashMap<>();
	static {
		addOtherAccessibleClass(Actor.class);
		addOtherAccessibleClass(Mob.class);
		addOtherAccessibleClass(NPC.class);
		addOtherAccessibleClass(QuestNPC.class);
		addOtherAccessibleClass(Buff.class);

		addOtherAccessibleClass(Hero.class);
		addOtherAccessibleClass(YogFist.class);
		addOtherAccessibleClass(Elemental.class);
		addOtherAccessibleClass(DMMob.class);
		addOtherAccessibleClass(SpawnerMob.class);
		addOtherAccessibleClass(ChampionEnemy.class);

		addOtherAccessibleClass(Checkpoint.class);
		addOtherAccessibleClass(ArrowCell.class);
		addOtherAccessibleClass(Barrier.class);
		
		addOtherAccessibleClass(CavesBossLevel.MetalGate.class);

		addOtherAccessibleClass(EquipableItem.class);
		addOtherAccessibleClass(Armor.class);
		addOtherAccessibleClass(Artifact.class);
		addOtherAccessibleClass(Ring.class);
		
		

		addOtherAccessibleClass(KindofMisc.class);
		addOtherAccessibleClass(KindOfWeapon.class);
		addOtherAccessibleClass(Weapon.class);
		addOtherAccessibleClass(MeleeWeapon.class);
		addOtherAccessibleClass(MissileWeapon.class);
		addOtherAccessibleClass(TippedDart.class);
		addOtherAccessibleClass(Wand.class);
		addOtherAccessibleClass(CursedWand.class);
		addOtherAccessibleClass(CursedWand.CursedEffect.class);

		addOtherAccessibleClass(Runestone.class);
		addOtherAccessibleClass(Scroll.class);
		addOtherAccessibleClass(ExoticScroll.class);
		addOtherAccessibleClass(Plant.Seed.class);
		addOtherAccessibleClass(Potion.class);
		addOtherAccessibleClass(ExoticPotion.class);
		addOtherAccessibleClass(Brew.class);
		addOtherAccessibleClass(Elixir.class);
		addOtherAccessibleClass(Spell.class);

		addOtherAccessibleClass(Key.class);
		addOtherAccessibleClass(Bag.class);
		addOtherAccessibleClass(RemainsItem.class);
		addOtherAccessibleClass(DocumentPage.class);


		addOtherAccessibleClass(Painter.class);
		addOtherAccessibleClass(RegularPainter.class);
		addOtherAccessibleClass(SewerPainter.class);
		addOtherAccessibleClass(PrisonPainter.class);
		addOtherAccessibleClass(CavesPainter.class);
		addOtherAccessibleClass(CityPainter.class);
		addOtherAccessibleClass(HallsPainter.class);
		addOtherAccessibleClass(MiningLevelPainter.class);

		addOtherAccessibleClass(Builder.class);
		addOtherAccessibleClass(RegularBuilder.class);
		addOtherAccessibleClass(BranchesBuilder.class);
		addOtherAccessibleClass(LineBuilder.class);
		addOtherAccessibleClass(LoopBuilder.class);
		addOtherAccessibleClass(FigureEightBuilder.class);


		addOtherAccessibleClass(Ballistica.class);
		addOtherAccessibleClass(LevelTransition.class);
		addOtherAccessibleClass(Notes.LandmarkRecord.class);

		addOtherAccessibleClass(Date.class);

		addOtherAccessibleClass(Set.class);
		addOtherAccessibleClass(List.class);
		addOtherAccessibleClass(Map.class);

	}

	private static void addOtherAccessibleClass(Class<?> clazz) {
		otherAccessibleClasses.put(clazz.getSimpleName(), clazz.getName());
	}


	public void exposeGlobalObject(String name, Object object) {
		set(name, LuaRestrictionProxy.wrapObject(object));
	}




}
