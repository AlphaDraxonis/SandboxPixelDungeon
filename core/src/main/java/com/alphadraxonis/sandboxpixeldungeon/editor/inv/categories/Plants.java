package com.alphadraxonis.sandboxpixeldungeon.editor.inv.categories;

import com.alphadraxonis.sandboxpixeldungeon.editor.inv.items.PlantItem;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.items.bags.Bag;
import com.alphadraxonis.sandboxpixeldungeon.plants.BlandfruitBush;
import com.alphadraxonis.sandboxpixeldungeon.plants.Blindweed;
import com.alphadraxonis.sandboxpixeldungeon.plants.Earthroot;
import com.alphadraxonis.sandboxpixeldungeon.plants.Fadeleaf;
import com.alphadraxonis.sandboxpixeldungeon.plants.Firebloom;
import com.alphadraxonis.sandboxpixeldungeon.plants.Icecap;
import com.alphadraxonis.sandboxpixeldungeon.plants.Mageroyal;
import com.alphadraxonis.sandboxpixeldungeon.plants.Plant;
import com.alphadraxonis.sandboxpixeldungeon.plants.Rotberry;
import com.alphadraxonis.sandboxpixeldungeon.plants.Sorrowmoss;
import com.alphadraxonis.sandboxpixeldungeon.plants.Starflower;
import com.alphadraxonis.sandboxpixeldungeon.plants.Stormvine;
import com.alphadraxonis.sandboxpixeldungeon.plants.Sungrass;
import com.alphadraxonis.sandboxpixeldungeon.plants.Swiftthistle;
import com.watabou.noosa.Image;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public enum Plants {

    PLANTS;

    private static final Class<?>[] EMPTY_PLANT_CLASS_ARRAY = new Class[0];

    public static Class<?>[][] getAllPlants(Set<Class<? extends Plant>> plantsToIgnore) {
        Plants[] all = values();
        Class<?>[][] ret = new Class[all.length][];
        for (int i = 0; i < all.length; i++) {
            List<Class<?>> plants = new ArrayList<>(Arrays.asList(all[i].classes()));
            if (plantsToIgnore != null) plants.removeAll(plantsToIgnore);
            ret[i] = plants.toArray(EMPTY_PLANT_CLASS_ARRAY);
        }
        return ret;
    }

    public static Class<? extends Plant> getRandomPlant(Set<Class<? extends Plant>> plantsToIgnore) {
        Class<? extends Plant>[][] plants = (Class<? extends Plant>[][]) getAllPlants(plantsToIgnore);
        List<Class<? extends Plant>> plantList = new ArrayList<>();
        for (Class<? extends Plant>[] plant : plants) {
            plantList.addAll(Arrays.asList(plant));
        }
        int length = plantList.size();
        if (length == 0) return null;
        return plantList.get((int) (Math.random() * length));
    }

    private Class<?>[] classes;

    public Class<?>[] classes() {
        return classes;
    }

    static {
        PLANTS.classes = new Class[]{
                Firebloom.class,
                Icecap.class,
                Sungrass.class,
                Earthroot.class,
                Sorrowmoss.class,
                Swiftthistle.class,
                Blindweed.class,
                Stormvine.class,
                Fadeleaf.class,
                Mageroyal.class,
                Starflower.class,
                BlandfruitBush.class,
                Rotberry.class
        };
    }

    public static class PlantBag extends EditorItemBag {
        private final Plants plants;

        public PlantBag(Plants plants) {
            super("plants", 0);
            this.plants = plants;
            for (Class<?> p : plants.classes) {
                items.add(new PlantItem((Plant) Reflection.newInstance(p)));
            }
        }

        @Override
        public Image getCategoryImage() {
            return PlantItem.getPlantImage(116);//Ice cap
        }
    }

    public static final EditorItemBag bag = new EditorItemBag("name", 0) {
        @Override
        public Item findItem(Object src) {
            for (Item bag : items) {
                for (Item i : ((Bag) bag).items) {
                    if (((PlantItem) i).plant().getClass() == src) return i;
                }
            }
            return null;
        }
    };

    static {
        for (Plants p : values()) {
            bag.items.add(new PlantBag(p));
        }
    }
}