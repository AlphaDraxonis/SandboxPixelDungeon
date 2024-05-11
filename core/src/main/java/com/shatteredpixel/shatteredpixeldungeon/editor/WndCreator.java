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

package com.shatteredpixel.shatteredpixeldungeon.editor;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.references.Reference;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.*;
import com.watabou.noosa.Image;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.util.Locale;

public final class WndCreator {

	private WndCreator(){}

	private static String parseMessage(LuaValue message) {
		if (message.isnil() || !message.isstring()) throw new IllegalArgumentException();
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
		return icon.isuserdata() ? Reference.objectToImage(icon.touserdata()) :
				icon.isint() ? (intvalue = icon.checkint()) >= 0 && intvalue < 512 ? new ItemSprite(intvalue) : null :
						null;
	}

	private static Mob parseMob(LuaValue mob) {
		if (mob.isuserdata()) {
			Object obj = mob.touserdata();
			if (obj instanceof Mob) return (Mob) obj;
		}
		return null;
	}

	private static Item parseItem(LuaValue item) {
		if (item.isuserdata()) {
			Object obj = item.touserdata();
			if (obj instanceof Item) return (Item) obj;
		}
		return null;
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
		return Chrome.Type.SCROLL;
	}

	public static Window showMessageWindow(LuaValue text, LuaValue title, LuaValue icon, LuaValue chromeType, LuaFunction onHide) {
		String t = parseTitle(title);
		Window w;
		w = t == null ? new WndMessage(parseMessage(text), parseChromeType(chromeType)) {
			@Override
			public void hide() {
				super.hide();
				if (onHide != null) onHide.call();
			}
		}
		: new WndTitledMessage(parseIcon(icon), parseTitle(title), parseMessage(text), parseChromeType(chromeType)) {
			@Override
			public void hide() {
				super.hide();
				if (onHide != null) onHide.call();
			}
		};

		GameScene.show(w);
		return w;
	}

	public static Window showStoryWindow(LuaValue text, LuaValue title, LuaValue icon, LuaValue chromeType, LuaFunction onHide) {
		return showStoryWindow(parseMessage(text), parseTitle(title), parseIcon(icon), parseChromeType(chromeType), onHide);
	}

	public static Window showStoryWindow(String text, String title, Image icon, Chrome.Type type, LuaFunction onHide) {
		Window w = new WndStory(icon, title, text, type) {
			@Override
			public void hide() {
				super.hide();
				if (onHide != null) onHide.call();
			}
		};
		GameScene.show(w);
		return w;
	}

	//showItemRewardWindow("Ich will dir was geben...", this, hero.belongings:getItem(class("Food")), {new("Pasty"):quantity(random.int(2,7))}, nil, nil, function(i) hero:die() end)
	//showItemRewardWindow("Ich will dir was geben...", this, hero.belongings:getItem(class("Food")), {new("Sword")}, nil, nil, function(i)  i:identify(false);i:level(8); end)
	//just passing sth like hero.die as function directly WILL NOT WORK
	public static Window showItemRewardWindow(LuaValue msg, LuaValue questInitiator, LuaValue payItem, LuaValue rewards,  LuaValue title, LuaValue icon, LuaFunction onSelectReward) {
		if (rewards.isnil() || !rewards.istable()) throw new IllegalArgumentException();

		LuaTable luaList = rewards.checktable();
		Item[] rewardItems = new Item[luaList.length()];
		for (int i = 0; i < rewardItems.length; i++) {
			rewardItems[i] = (Item) luaList.get(i + 1).touserdata();
		}

		return showItemRewardWindow(parseMessage(msg), parseTitle(title), parseIcon(icon), parseMob(questInitiator), parseItem(payItem), rewardItems, onSelectReward);
	}

	public static Window showItemRewardWindow(String msg, String titleText, Image icon, Mob questInitiator, Item payItem, Item[] rewards, LuaValue onSelectReward) {
		Window w = new WndReward() {
			{
				IconTitle titlebar = new IconTitle();
				if (icon != null) titlebar.icon(icon);
				else if (payItem != null) titlebar.icon(new ItemSprite(payItem, null));
				else if (questInitiator != null) titlebar.icon(questInitiator.sprite());

				if (title != null) titlebar.label(titleText);
				else if (payItem != null) titlebar.label( Messages.titleCase(payItem.name()));
				else if (questInitiator != null) titlebar.label( Messages.titleCase(questInitiator.name()));

				initComponents(
						titlebar,
						new SingleItemRewardsBody(msg, questInitiator, payItem, rewards) {
							@Override
							protected void onSelectReward(Item reward) {
								if (onSelectReward != null) onSelectReward.call(CoerceJavaToLua.coerce(reward));
							}
						}, null);
			}
		};
		GameScene.show(w);
		return w;
	}



}