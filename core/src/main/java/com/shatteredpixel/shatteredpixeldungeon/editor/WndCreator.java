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

package com.shatteredpixel.shatteredpixeldungeon.editor;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.LuaManager;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.LuaRestrictionProxy;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptionsCondensed;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndReward;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndStory;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.NotAllowedInLua;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.Locale;

@NotAllowedInLua
public final class WndCreator {

	private WndCreator(){}

	private static String parseMessage(LuaValue message) {
		if (message.isnil() || !message.isstring()) throw new LuaError(new IllegalArgumentException());
		String key = message.checkjstring();
		String msg = Messages.get(key);
		return msg.equals(Messages.NO_TEXT_FOUND) ? key : msg;
	}

	private static String parseTitle(LuaValue title) {
		if (title.isnil() || !title.isstring()) return null;
		String key = title.checkjstring();
		String msg = Messages.get(key);
		return msg.equals(Messages.NO_TEXT_FOUND) ? key : msg;
	}

	private static Image parseIcon(LuaValue icon) {
		int intvalue;
		if (icon.isuserdata()) {
			return EditorUtilities.imageOf(icon.touserdata(), true);
		}
		if (icon.isint()) {
			return (intvalue = icon.checkint()) >= 0 && intvalue < 512 ? new ItemSprite(intvalue) : null;
		}
		if (icon.isstring()) {
			String asString = icon.checkjstring();
			for (Icons i : Icons.values()) {
				if (i.name().toLowerCase(Locale.ENGLISH).equals(asString)) return i.get();
			}
		}
		return null;
	}

	private static Mob parseMob(LuaValue mob) {
		return (Mob) LuaRestrictionProxy.coerceLuaToJava(mob, Mob.class);
	}

	private static Item parseItem(LuaValue item) {
		return (Item) LuaRestrictionProxy.coerceLuaToJava(item, Item.class);
	}

	private static Chrome.Type parseChromeType(LuaValue chromeType) {
		String asString = !chromeType.isnil() && chromeType.isstring() ? chromeType.checkjstring() : null;
		if (asString != null) {
			for (Chrome.Type chrome : Chrome.Type.values()) {
				if (chrome.name().toLowerCase(Locale.ENGLISH).equals(asString)) {
					return chrome;
				}
			}
		}
		return null;
	}

	public static Window showMessageWindow(LuaValue text, LuaValue title, LuaValue icon, LuaValue chromeType, LuaFunction onHide) {
		String t = parseTitle(title);
		Window w;
		Chrome.Type bg = parseChromeType(chromeType);
		w = t == null ? new WndMessage(parseMessage(text), bg == null ? Chrome.Type.WINDOW : bg) {
			@Override
			public void hide() {
				super.hide();
				if (onHide != null) {
					try {
						onHide.call();
					} catch (LuaError error) { Game.runOnRenderThread(() ->	DungeonScene.show(new WndError(error))); }
				}
			}
		}
		: new WndTitledMessage(parseIcon(icon), parseTitle(title), parseMessage(text), bg == null ? Chrome.Type.WINDOW : bg) {
			@Override
			public void hide() {
				super.hide();
				if (onHide != null) {
					try {
						onHide.call();
					} catch (LuaError error) { Game.runOnRenderThread(() ->	DungeonScene.show(new WndError(error))); }
				}
			}
		};

		GameScene.show(w);
		return w;
	}

	public static Window showStoryWindow(LuaValue text, LuaValue title, LuaValue icon, LuaValue chromeType, LuaFunction onHide) {
		return showStoryWindow(parseMessage(text), parseTitle(title), parseIcon(icon), parseChromeType(chromeType), onHide);
	}

	public static Window showStoryWindow(String text, String title, Image icon, Chrome.Type type, LuaFunction onHide) {
		Window w = new WndStory(icon, title, text, type == null ? Chrome.Type.SCROLL : type) {
			@Override
			public void hide() {
				super.hide();
				if (onHide != null) {
					try {
						onHide.call();
					} catch (LuaError error) { Game.runOnRenderThread(() ->	DungeonScene.show(new WndError(error))); }
				}
			}
		};
		GameScene.show(w);
		return w;
	}

	//showItemRewardWindow("Ich will dir was geben...", this, hero.belongings:getItem(class("Food")), {new("Pasty"):quantity(random.int(2,7))}, nil, nil, function(i) hero:die() end)
	//showItemRewardWindow("Ich will dir was geben...", this, hero.belongings:getItem(class("Food")), {new("Sword")}, nil, nil, function(i)  i:identify(false);i:level(8); end)
	//just passing sth like hero.die as function directly WILL NOT WORK
	public static Window showItemRewardWindow(LuaValue msg, LuaValue questInitiator, LuaValue payItem, LuaValue rewards,  LuaValue title, LuaValue icon, LuaFunction onSelectReward) {
		if (rewards.isnil() || !rewards.istable()) throw new LuaError(new IllegalArgumentException());

		LuaTable luaList = rewards.checktable();
		Item[] rewardItems = LuaManager.luaTableToJavaArray(luaList, new Item[luaList.length()]);

		return showItemRewardWindow(parseMessage(msg), parseTitle(title), parseIcon(icon), parseMob(questInitiator), parseItem(payItem), rewardItems, onSelectReward);
	}

	public static Window showItemRewardWindow(String msg, String titleText, Image icon, Mob questInitiator, Item payItem, Item[] rewards, LuaValue onSelectReward) {
		Window w = new WndReward() {
			{
				IconTitle titlebar = new IconTitle();
				if (icon != null) titlebar.icon(icon);
				else if (payItem != null) titlebar.icon(new ItemSprite(payItem, null));
				else if (questInitiator != null) titlebar.icon(questInitiator.createSprite());

				if (title != null) titlebar.label(titleText);
				else if (payItem != null) titlebar.label( Messages.titleCase(payItem.name()));
				else if (questInitiator != null) titlebar.label( Messages.titleCase(questInitiator.name()));

				initComponents(
						titlebar,
						new SingleItemRewardsBody(msg, questInitiator, payItem, rewards) {
							@Override
							protected void onSelectReward(Item reward) {
								if (onSelectReward != null) {
									try {
										onSelectReward.call(LuaRestrictionProxy.wrapObject(reward));
									} catch (LuaError error) { Game.runOnRenderThread(() ->	DungeonScene.show(new WndError(error))); }
								}
							}
						}, null);
			}
		};
		GameScene.show(w);
		return w;
	}

	public static Window showOptionsWindow(LuaValue msg, LuaValue title, LuaValue icon, LuaValue options, LuaValue infos, LuaFunction onSelect) {
		if (options.isnil() || !options.istable()) throw new LuaError(new IllegalArgumentException());

		LuaTable luaList = options.checktable();
		String[] opt = LuaManager.luaTableToJavaArray(luaList, new String[luaList.length()]);
		String[] inf;
		if (infos.istable()) {
			luaList = infos.checktable();
			inf = LuaManager.luaTableToJavaArray(luaList, new String[luaList.length()]);
		} else inf = null;

		return showOptionsWindow(parseMessage(msg), parseTitle(title), parseIcon(icon), opt, inf, onSelect);
	}

	public static Window showCondensedOptionsWindow(LuaValue msg, LuaValue title, LuaValue icon, LuaValue options, LuaValue infos, LuaFunction onSelect) {
		if (options.isnil() || !options.istable()) throw new LuaError(new IllegalArgumentException());

		String[] opt = LuaManager.luaTableToJavaArray(options.checktable());
		String[] inf = infos.istable() ? LuaManager.luaTableToJavaArray(infos.checktable()) : null;

		return showCondensedOptionsWindow(parseMessage(msg), parseTitle(title), parseIcon(icon), opt, inf, onSelect);
	}

	public static Window showOptionsWindow(String msg, String titleText, Image icon, String[] options, String[] infoTexts, LuaValue onSelectReward) {
		Window w = new WndOptions(icon, titleText, msg, options) {
			@Override
			protected void onSelect(int index) {
				if (onSelectReward != null) {
					try {
						onSelectReward.call(LuaInteger.valueOf(index));
					} catch (LuaError error) { Game.runOnRenderThread(() ->	DungeonScene.show(new WndError(error))); }
				}
			}

			@Override
			protected void onInfo(int index) {
				GameScene.show(new WndTitledMessage(
						Icons.get(Icons.INFO),
						Messages.titleCase(options[index]),
						infoTexts[index]));
			}

			@Override
			protected boolean hasInfo(int index) {
				return infoTexts != null && infoTexts.length > index && infoTexts[index] != null;
			}
		};
		GameScene.show(w);
		return w;
	}

	public static Window showCondensedOptionsWindow(String msg, String titleText, Image icon, String[] options, String[] infoTexts, LuaValue onSelectReward) {
		Window w = new WndOptionsCondensed(icon, titleText, msg, options) {
			@Override
			protected void onSelect(int index) {
				if (onSelectReward != null) {
					try {
						onSelectReward.call(LuaInteger.valueOf(index));
					} catch (LuaError error) { Game.runOnRenderThread(() ->	DungeonScene.show(new WndError(error))); }
				}
			}

			@Override
			protected void onInfo(int index) {
				GameScene.show(new WndTitledMessage(
						Icons.get(Icons.INFO),
						Messages.titleCase(options[index]),
						infoTexts[index]));
			}

			@Override
			protected boolean hasInfo(int index) {
				return infoTexts != null && infoTexts.length > index && infoTexts[index] != null;
			}
		};
		GameScene.show(w);
		return w;
	}



}
