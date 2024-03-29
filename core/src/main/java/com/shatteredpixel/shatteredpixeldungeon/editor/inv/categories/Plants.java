package com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories;

import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.PlantItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.plants.BlandfruitBush;
import com.shatteredpixel.shatteredpixeldungeon.plants.Blindweed;
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot;
import com.shatteredpixel.shatteredpixeldungeon.plants.Fadeleaf;
import com.shatteredpixel.shatteredpixeldungeon.plants.Firebloom;
import com.shatteredpixel.shatteredpixeldungeon.plants.Icecap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Mageroyal;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.plants.Rotberry;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sorrowmoss;
import com.shatteredpixel.shatteredpixeldungeon.plants.Starflower;
import com.shatteredpixel.shatteredpixeldungeon.plants.Stormvine;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sungrass;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
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
                Rotberry.class,
                WandOfRegrowth.Dewcatcher.class,
                WandOfRegrowth.Seedpod.class
        };
    }

    public static class PlantBag extends EditorItemBag {
        public PlantBag(Plants plants) {
            super("plants", 0);
            for (Class<?> p : plants.classes) {
                items.add(new PlantItem((Plant) Reflection.newInstance(p)));
            }
        }

        @Override
        public Image getCategoryImage() {
            return EditorUtilies.getTerrainFeatureTexture(116);//Ice cap
        }
    }

    public static final EditorItemBag bag = new EditorItemBag("name", 0) {
        @Override
        public Item findItem(Object src) {
            for (Item bag : items) {
                for (Item i : ((Bag) bag).items) {
                    if (((PlantItem) i).getObject().getClass() == src) return i;
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