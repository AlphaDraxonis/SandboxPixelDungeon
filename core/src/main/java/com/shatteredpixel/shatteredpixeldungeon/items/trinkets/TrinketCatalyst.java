/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.items.trinkets;

import com.shatteredpixel.shatteredpixeldungeon.GameObject;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.journal.Guidebook;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.AlchemyScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndReward;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.Function;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Collection;

public class TrinketCatalyst extends Item {

	{
		image = ItemSpriteSheet.TRINKET_CATA;

		unique = true;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean doPickUp(Hero hero, int pos) {
		if (super.doPickUp(hero, pos)){
			if (!Document.ADVENTURERS_GUIDE.isPageRead(Document.GUIDE_ALCHEMY)){
				GLog.p(Messages.get(Guidebook.class, "hint"));
				GameScene.flashForDocument(Document.ADVENTURERS_GUIDE, Document.GUIDE_ALCHEMY);
			}
			return true;
		} else {
			return false;
		}
	}

	public ArrayList<Trinket> rolledTrinkets = new ArrayList<>();
	public int numChoosableTrinkets = 3;// must always be  0 < this < Generator.Category.Trinket.classes.length
	private boolean paidEnergy;

	@Override
	public boolean doOnAllGameObjects(Function<GameObject, ModifyResult> whatToDo) {
		return super.doOnAllGameObjects(whatToDo)
				| doOnAllGameObjectsList(rolledTrinkets, whatToDo);
	}

	private static final String ROLLED_TRINKETS = "rolled_trinkets";
	private static final String NUM_CHOOSABLE_TRINKETS = "num_choosable_trinkets";
	private static final String PAID_ENERGY = "paid_energy";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(PAID_ENERGY, paidEnergy);
		bundle.put(NUM_CHOOSABLE_TRINKETS, numChoosableTrinkets);
		if (!rolledTrinkets.isEmpty()){
			bundle.put(ROLLED_TRINKETS, rolledTrinkets);
		}
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		numChoosableTrinkets = bundle.getInt(NUM_CHOOSABLE_TRINKETS);
		paidEnergy = bundle.getBoolean(PAID_ENERGY);
		rolledTrinkets.clear();
		if (bundle.contains(ROLLED_TRINKETS)){
			rolledTrinkets.addAll((Collection<Trinket>) ((Collection<?>)bundle.getCollection( ROLLED_TRINKETS )));
		}
	}

	public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe {

		@Override
		public boolean testIngredients(ArrayList<Item> ingredients) {
			return ingredients.size() == 1 && ingredients.get(0) instanceof TrinketCatalyst;
		}

		@Override
		public int cost(ArrayList<Item> ingredients) {
			if (ingredients.get(0) instanceof TrinketCatalyst && ((TrinketCatalyst) ingredients.get(0)).paidEnergy){
				return 0; //costs 0 if rolledTrinkets has items as the player already paid 6 energy
			}
			return 6;
		}

		@Override
		public Item brew(ArrayList<Item> ingredients) {
			//we silently re-add the catalyst so that we can clear it when a trinket is selected
			//this way player isn't totally screwed if they quit the game while selecting
			TrinketCatalyst newCata = (TrinketCatalyst) ingredients.get(0).duplicate();
			newCata.collect();

			ingredients.get(0).quantity(0);

			newCata.paidEnergy = true;
			newCata.initRandom();

			Game.scene().addToFront(new WndTrinket(newCata));
			return null;
		}

		@Override
		public Item sampleOutput(ArrayList<Item> ingredients) {
			return new Trinket.PlaceHolder();
		}
	}

	private void initRandom() {
		//roll new trinkets if trinkets were not already rolled
		int curSize = rolledTrinkets.size();
		rollTrinket:
		while (curSize < numChoosableTrinkets){
			Trinket trinket = (Trinket) Generator.random(Generator.Category.TRINKET);
			if (curSize < Generator.Category.TRINKET.classes.length) {
				for (Trinket t : rolledTrinkets) {
					if (t.getClass() == trinket.getClass()) continue rollTrinket;
				}
			}
			rolledTrinkets.add(trinket);
			curSize++;
		}
		while (curSize > numChoosableTrinkets) {
			rolledTrinkets.remove(Random.Int(rolledTrinkets.size()));
			curSize--;
		}
		GameObject.doOnAllGameObjectsList(rolledTrinkets, GameObject::initRandoms);
	}

	public static class WndTrinket extends WndReward {

		public WndTrinket( TrinketCatalyst cata ){
			super();

			initComponents(
					new IconTitle(new ItemSprite(cata), Messages.titleCase(cata.name())),
					new SingleItemRewardsBody(Messages.get(TrinketCatalyst.class, "window_text"), null, cata, cata.rolledTrinkets.toArray(EditorUtilies.EMPTY_ITEM_ARRAY)) {
						@Override
						protected void onSelectReward(Item reward) {
							((AlchemyScene)Game.scene()).craftItem(null, reward);
						}
					}, null);

		}

		@Override
		public void onBackPressed() {
			//do nothing
		}

	}
}