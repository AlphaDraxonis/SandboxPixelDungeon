package com.shatteredpixel.shatteredpixeldungeon.editor.recipes;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CustomRecipe extends Recipe.SimpleRecipe implements Bundlable {

    protected final Item[] itemInputs = new Item[3];//each class should be unique

    public Item itemOutput;

    {
        inQuantity = new int[3];
        outQuantity = 1;//Use output quantity directly
    }

    public boolean isRecipeValid() {
        return itemOutput != null && numIngredients() > 0;
    }

    private static final String INPUT = "input_";
    private static final String OUTPUT = "output";
    private static final String COST = "cost";

    @Override
    public void restoreFromBundle(Bundle bundle) {

        cost = bundle.getInt(COST);
        itemOutput = (Item) bundle.get(OUTPUT);

        for (int i = 0; i < 3; i++) {
            itemInputs[i] = (Item) bundle.get(INPUT + i);
        }

        updateQuantities();
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(COST, cost);
        bundle.put(OUTPUT, itemOutput);

        for (int i = 0; i < 3; i++) {
            bundle.put(INPUT + i, itemInputs[i]);
        }
    }

    public void updateQuantities(){
        inQuantity = new int[3];
        Map<Class<? extends Item>, Integer> alreadyIncludedItems = new HashMap<>(3);
        for (int i = 0; i < 3; i++) {
            Item item = itemInputs[i];
            if (item != null) {
                if (!alreadyIncludedItems.containsKey(item.getClass())) {
                    alreadyIncludedItems.put(itemInputs[i].getClass(), i);
                    inQuantity[i] = 1;
                } else {
                    int newQuantity = inQuantity[alreadyIncludedItems.get(item.getClass())] + 1;
                    inQuantity[alreadyIncludedItems.get(item.getClass())] = newQuantity;
                    alreadyIncludedItems.put(item.getClass(), newQuantity);
                    itemInputs[i] = null;
                }
            }
        }

        inputs = new Class[itemInputs.length];
        for (int i = 0; i < itemInputs.length; i++) {
            if (itemInputs[i] != null) inputs[i] = itemInputs[i].getClass();
        }
    }

    public final void setCost(int cost) {
        this.cost = cost;
    }

    public final int getCost() {
        return cost;
    }

    //gets a simple list of items based on inputs
    @Override
    public ArrayList<Item> getIngredients() {
        ArrayList<Item> result = new ArrayList<>();
        for (int i = 0; i < itemInputs.length; i++) {
            if (itemInputs[i] == null) continue;
            Item ingredient = itemInputs[i].getCopy();
            ingredient.quantity(inQuantity[i]);
            result.add(ingredient);
        }
        return result;
    }

    @Override
    public Item sampleOutput(ArrayList<Item> ingredients) {
        return itemOutput.getCopy();
    }

    private int numIngredients;

    public int numIngredients() {
        //once called, this cannot change
        if (numIngredients != 0) return numIngredients;
        for (Item i : itemInputs) {
            if (i != null) numIngredients++;
        }
        return numIngredients;
    }
}