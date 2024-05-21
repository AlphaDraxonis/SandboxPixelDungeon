package com.shatteredpixel.shatteredpixeldungeon.editor.recipes;

import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditItemComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.RandomItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomRecipe extends Recipe implements Bundlable {

    protected final Item[] itemInputs = new Item[3];//quantity should default to 1, but when playing, same items are simplified
    protected Item itemOutput;
    protected int cost;

    public boolean isRecipeValid() {
        return itemOutput != null && numIngredients() > 0;
    }

    private static final String INPUT = "input_";
    private static final String OUTPUT = "output";
    private static final String COST = "cost";
    private static final String NUM_INGREDIENTS = "num_ingredients";//only relevant after game has started

    @Override
    public void restoreFromBundle(Bundle bundle) {

        cost = bundle.getInt(COST);
        itemOutput = (Item) bundle.get(OUTPUT);
        numIngredients = bundle.getInt(NUM_INGREDIENTS);

        for (int i = 0; i < 3; i++) {
            itemInputs[i] = (Item) bundle.get(INPUT + i);
        }
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(COST, cost);
        bundle.put(OUTPUT, itemOutput);
        if (numIngredients != 0) bundle.put(NUM_INGREDIENTS, numIngredients);

        for (int i = 0; i < 3; i++) {
            bundle.put(INPUT + i, itemInputs[i]);
        }
    }

    public final void setCost(int cost) {
        this.cost = cost;
    }

    public ArrayList<Item> getIngredients() {
        ArrayList<Item> ret = new ArrayList<>(Arrays.asList(itemInputs));
        for (int i = itemInputs.length - 1; i >= 0; i--) {
            if (itemInputs[i] == null) ret.remove(i);
        }
        return ret;
    }

    @Override
    public boolean testIngredients(ArrayList<Item> ingredients) {

        int[] neededQuantity = getNeededQuantities();

        for (Item ingredient : ingredients) {
            if (!ingredient.isIdentified()) return false;
            for (int i = 0; i < itemInputs.length; i++) {
                if (EditItemComp.areEqual(ingredient, itemInputs[i], true)) {
                    neededQuantity[i] -= ingredient.quantity();
                    break;
                }
            }
        }

        for (int i : neededQuantity) {
            if (i > 0) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int cost(ArrayList<Item> ingredients) {
        return cost;
    }

    @Override
    public Item brew(ArrayList<Item> ingredients) {
        if (!testIngredients(ingredients)) return null;

        List<Recipe> otherRecipes = Recipe.findRecipes(ingredients);
        otherRecipes.remove(this);

        int[] neededQuantity = getNeededQuantities();

        for (Item ingredient : ingredients) {
            for (int i = 0; i < itemInputs.length; i++) {
                if (EditItemComp.areEqual(ingredient, itemInputs[i], true) && neededQuantity[i] > 0) {
                    if (neededQuantity[i] <= ingredient.quantity()) {
                        ingredient.quantity(ingredient.quantity() - neededQuantity[i]);
                        neededQuantity[i] = 0;
                    } else {
                        neededQuantity[i] -= ingredient.quantity();
                        ingredient.quantity(0);
                    }
                }
            }
        }

        Item result = itemOutput.getCopy();
        boolean identifyResult = true;
        if (result instanceof Ring || result instanceof Wand || result instanceof Artifact
                || (result instanceof Weapon && !(result instanceof MissileWeapon))
                || (result instanceof Armor && !(result instanceof ClassArmor))) {
            identifyResult = false;
        } else {
            for (Recipe recipe : otherRecipes) {
                Class<? extends Item> outputClass;
                if (recipe instanceof CustomRecipe) outputClass = ((CustomRecipe) recipe).itemOutput.getClass();
                else outputClass = recipe.sampleOutput(ingredients).getClass();
                if (outputClass == result.getClass()) {
                    identifyResult = false;
                    break;
                }
            }
        }
        if (identifyResult || result.identifyOnStart)
            result.identify();

        return result;
    }

    @Override
    public Item sampleOutput(ArrayList<Item> ingredients) {
        Item sample = itemOutput.getCopy();
        if (!sample.isIdentified()) {
            if (sample instanceof Potion) ((Potion) sample).anonymize();
            else if (sample instanceof Scroll) ((Scroll) sample).anonymize();
            else if (sample instanceof Ring) ((Ring) sample).anonymize();
        }
        return sample;
    }

    private int[] getNeededQuantities() {
        int[] neededQuantity = new int[itemInputs.length];
        for (int i = 0; i < neededQuantity.length; i++) {
            if (itemInputs[i] != null) neededQuantity[i] = itemInputs[i].quantity();
        }
        return neededQuantity;
    }

    private int numIngredients;

    public int numIngredients() {
        //once called, this cannot change
        if (numIngredients != 0) return numIngredients;
        initRecipe();
        return numIngredients;
    }

    //should only be called after all changes are done, no changes should ever be made then
    private void initRecipe() {
        //sets numIngredients
        //combines same items in input and changes quantity accordingly
        for (int i = 0; i < itemInputs.length; i++) {
            if (itemInputs[i] != null) {
                for (int j = 0; j < i; j++) {
                    if (EditItemComp.areEqual(itemInputs[j], itemInputs[i], true)) {
                        itemInputs[j].merge(itemInputs[i]);
                        itemInputs[i] = null;
                        break;
                    }
                }
            }
        }
        numIngredients = 0;
        for (int i = 0; i < itemInputs.length; i++) {
            if (itemInputs[i] != null)
                numIngredients += itemInputs[i].quantity();
        }
    }

    public void setInput(int slot, Item item) {
        if (item != null) {
            item.quantity(1);
            if (!item.isIdentified()) {//identify
                item.setCursedKnown(true);
                item.levelKnown = true;
            }
        }
        itemInputs[slot] = item;
    }

	public void initRandom() {
        RandomItem.fillArrayAsGoodAsPossibleUsingRandomItems(itemInputs);
        itemOutput = RandomItem.initRandomStatsForItemSubclasses(itemOutput);
	}
}