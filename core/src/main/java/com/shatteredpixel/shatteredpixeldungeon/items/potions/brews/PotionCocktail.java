/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.items.potions.brews;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Bundle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PotionCocktail extends Potion {

	{
		image = ItemSpriteSheet.BREW_COCKTAIL;
	}
	
	public List<Potion> potions = new ArrayList<>();
	public boolean potionsKnown = false;

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		
		if (potionsKnown) {
			boolean mustThrowAll = true;
			boolean mustDrinkAll = true;
			for (Potion p : potions) {
				if (mustThrowPots.contains(p.getClass())) {
					mustDrinkAll = false;
				} else if (canThrowPots.contains(p.getClass())) {
					mustDrinkAll = false;
					mustThrowAll = false;
				} else {
					mustThrowAll = false;
				}
			}
			if (mustThrowAll) actions.remove( AC_DRINK );
		}
		
		return actions;
	}
	
	@Override
	public String defaultAction() {
		if (potionsKnown) {
			boolean mustThrowAll = true;
			boolean mustDrinkAll = true;
			for (Potion p : potions) {
				if (mustThrowPots.contains(p.getClass())) {
					mustDrinkAll = false;
				} else if (canThrowPots.contains(p.getClass())) {
					mustDrinkAll = false;
					mustThrowAll = false;
				} else {
					mustThrowAll = false;
				}
			}
			if (mustThrowAll) return AC_THROW;
			if (mustDrinkAll) return AC_DRINK;
		}
		return AC_CHOOSE;
	}
	
	@Override
	public void apply(Hero hero) {
		for (Potion p : potions) {
			p.anonymize();
			for (int i = 0; i < Math.min(10, p.quantity()); i++) {
				p.apply(hero);
			}
		}
	}
	
	@Override
	public void shatter(int cell) {
		for (Potion p : potions) {
			p.anonymize();
			for (int i = 0; i < Math.min(10, p.quantity()); i++) {
				p.anonymize();
				curItem = p;
				p.shatter(cell);
			}
		}
	}
	
	@Override
	public boolean isKnown() {
		return true;
	}

	@Override
	public int value() {
		int value = 0;
		if (potionsKnown) {
			for (Potion p : potions) {
				value += p.value();
			}
		} else {
			value = 60;
		}
		return value * quantity;
	}

	@Override
	public int energyVal() {
		int value = 0;
		if (potionsKnown) {
			for (Potion p : potions) {
				value += p.energyVal();
			}
		} else {
			value = 12;
		}
		return value * quantity;
	}
	
	@Override
	public String info() {
		String info = super.info();
		if (potionsKnown && !potions.isEmpty()) {
			info += "\n\n" + Messages.get(this, "contains");
			for (Potion p : potions) {
				info += "\n_" + p.name() + "_";
			}
		}
		return info;
	}
	
	private static final String POTIONS = "potions";
	private static final String POTIONS_KNOWN = "potions_known";
	
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(POTIONS, potions);
		bundle.put(POTIONS_KNOWN, potionsKnown);
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		potions.addAll((Collection<Potion>) ((Collection<?>) bundle.getCollection( POTIONS )));
		potionsKnown = bundle.getBoolean(POTIONS_KNOWN);
	}
	
	public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe {
		
		@Override
		public boolean testIngredients(ArrayList<Item> ingredients) {
			boolean potionCocktail = false;
			boolean potion = false;

			for (Item i : ingredients){
				if (i instanceof PotionCocktail) {
					if (potionCocktail) return false;
					potionCocktail = true;
				}
				else if (i instanceof Potion && i.isIdentified()) {
					potion = true;
				}
				else {
					return false;
				}
			}

			return potion && potionCocktail;
		}

		@Override
		public int cost(ArrayList<Item> ingredients) {
			int cost = 0;
			for (Item i : ingredients){
				if (!(i instanceof PotionCocktail) && i instanceof Potion && i.isIdentified()) {
					cost += 2;
				}
			}
			return cost;
		}

		@Override
		public Item brew(ArrayList<Item> ingredients) {

			for (Item i : ingredients){
				i.quantity(i.quantity()-1);
			}
			
			return sampleOutput(ingredients);
		}
		
		@Override
		public Item sampleOutput(ArrayList<Item> ingredients) {
			PotionCocktail cocktail = null;
			for (Item i : ingredients){
				if (i instanceof PotionCocktail) {
					cocktail = (PotionCocktail) i;
					break;
				}
			}
			cocktail = (PotionCocktail) cocktail.getCopy();
			cocktail.quantity(1);
			for (Item i : ingredients){
				if (!(i instanceof PotionCocktail)) {
					cocktail.potions.add((Potion) i);
				}
			}
			return cocktail;
		}
	}
	
}
