/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2023 Evan Debenham
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

package com.alphadraxonis.sandboxpixeldungeon.actors.mobs.npcs;

import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.SandboxPixelDungeon;
import com.alphadraxonis.sandboxpixeldungeon.Statistics;
import com.alphadraxonis.sandboxpixeldungeon.actors.Char;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.AscensionChallenge;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.Buff;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomDungeon;
import com.alphadraxonis.sandboxpixeldungeon.effects.CellEmitter;
import com.alphadraxonis.sandboxpixeldungeon.effects.particles.ElmoParticle;
import com.alphadraxonis.sandboxpixeldungeon.items.Heap;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.items.armor.Armor;
import com.alphadraxonis.sandboxpixeldungeon.journal.Notes;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.GameScene;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSprite;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ShopkeeperSprite;
import com.alphadraxonis.sandboxpixeldungeon.utils.GLog;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndBag;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndOptions;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndTitledMessage;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndTradeItem;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;

import java.util.ArrayList;

public class Shopkeeper extends NPC {

    {
        spriteClass = ShopkeeperSprite.class;

        properties.add(Property.IMMOVABLE);
    }

    public static int MAX_BUYBACK_HISTORY = 3;
	public ArrayList<Item> buybackItems = new ArrayList<>();
    @Override
    protected boolean act() {

        if (Dungeon.level.visited[pos]) {
            Notes.add(Notes.Landmark.SHOP);
        }



        sprite.turnTo(pos, Dungeon.hero.pos);
        spend(TICK);
        return super.act();
    }

    @Override
    public void damage(int dmg, Object src) {
        flee();
    }

    @Override
    public boolean add(Buff buff) {
        if (super.add(buff)) {
            flee();
            return true;
        }
        return false;
    }

    public void flee() {
        destroy();

        Notes.remove(Notes.Landmark.SHOP);

        if (sprite != null) {
            sprite.killAndErase();
            CellEmitter.get(pos).burst(ElmoParticle.FACTORY, 6);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        if (CustomDungeon.isEditing()) return;
        for (Heap heap : Dungeon.level.heaps.valueList()) {
            if (heap.type == Heap.Type.FOR_SALE) {
                if (SandboxPixelDungeon.scene() instanceof GameScene) {
                    CellEmitter.get(heap.pos).burst(ElmoParticle.FACTORY, 4);
                }
                if (heap.size() == 1) {
                    heap.destroy();
                } else {
                    heap.items.remove(heap.size() - 1);
                    heap.type = Heap.Type.HEAP;
                }
            }
        }
    }

    @Override
    public boolean reset() {
        return true;
    }

    //shopkeepers are greedy!
    public static int sellPrice(Item item) {
        return item.value() * 5 * Dungeon.customDungeon.getFloor(Dungeon.levelName).getPriceMultiplier();
    }

    public static WndBag sell() {
        return GameScene.selectItem(itemSelector);
    }

    public static boolean canSell(Item item) {
        if (item.value() <= 0) return false;
        if (item.unique && !item.stackable) return false;
        if (item instanceof Armor && ((Armor) item).checkSeal() != null) return false;
        if (item.isEquipped(Dungeon.hero) && item.cursed) return false;
        return true;
    }

    private static WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {
        @Override
        public String textPrompt() {
            return Messages.get(Shopkeeper.class, "sell");
        }

        @Override
        public boolean itemSelectable(Item item) {
            return Shopkeeper.canSell(item);
        }

        @Override
        public void onSelect(Item item) {
            if (item != null) {
                WndBag parentWnd = sell();
                GameScene.show(new WndTradeItem(item, parentWnd));
            }
        }
    };

    @Override
    public boolean interact(Char c) {
        if (c != Dungeon.hero) {
            return true;
        }
        Game.runOnRenderThread(new Callback() {
            @Override
            public void call() {
                String[] options = new String[2+ buybackItems.size()];
				int i = 0;
				options[i++] = Messages.get(Shopkeeper.this, "sell");
				options[i++] = Messages.get(Shopkeeper.this, "talk");
				for (Item item : buybackItems){
					options[i] = Messages.get(Heap.class, "for_sale", item.value(), Messages.titleCase(item.title()));
					if (options[i].length() > 26) options[i] = options[i].substring(0, 23) + "...";
					i++;
				}
				GameScene.show(new WndOptions(sprite(), Messages.titleCase(name()), description(), options){
					@Override
					protected void onSelect(int index) {
						super.onSelect(index);
						if (index == 0){sell();
            }else if (index == 1){
							GameScene.show(new WndTitledMessage(sprite(), Messages.titleCase(name()), chatText()));
						} else if (index > 1){
							GLog.i(Messages.get(Shopkeeper.this, "buyback"));
							Item returned = buybackItems.remove(index-2);
							Dungeon.gold -= returned.value();
							Statistics.goldCollected -= returned.value();
							if (!returned.doPickUp(Dungeon.hero)){
								Dungeon.level.drop(returned, Dungeon.hero.pos);
							}
						}
					}

					@Override
					protected boolean enabled(int index) {
						if (index > 1){
							return Dungeon.gold >= buybackItems.get(index-2).value();
						} else {
							return super.enabled(index);
						}
					}

					@Override
					protected boolean hasIcon(int index) {
						return index > 1;
					}

					@Override
					protected Image getIcon(int index) {
						if (index > 1){
							return new ItemSprite(buybackItems.get(index-2));
						}
						return null;
					}
				});
			}
        });
        return true;
    }public String chatText(){
		if (Dungeon.hero.buff(AscensionChallenge.class) != null){
			return Messages.get(this, "talk_ascent");
		}
		switch (Dungeon.depth){
			case 6: default:
				return Messages.get(this, "talk_prison_intro") + "\n\n" + Messages.get(this, "talk_prison_" + Dungeon.hero.heroClass.name());
			case 11:
				return Messages.get(this, "talk_caves");
			case 16:
				return Messages.get(this, "talk_city");
			case 20:
				return Messages.get(this, "talk_halls");
		}
	}

	public static String BUYBACK_ITEMS = "buyback_items";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(BUYBACK_ITEMS, buybackItems);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		buybackItems.clear();
		if (bundle.contains(BUYBACK_ITEMS)){
			for (Bundlable i : bundle.getCollection(BUYBACK_ITEMS)){
				buybackItems.add((Item) i);
			}
		}
	}
}