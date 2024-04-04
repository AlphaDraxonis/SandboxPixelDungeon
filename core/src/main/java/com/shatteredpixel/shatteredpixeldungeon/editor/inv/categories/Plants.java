package com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories;

import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.PlantItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.plants.*;
import com.watabou.noosa.Image;
import com.watabou.utils.Reflection;

public enum Plants implements EditorInvCategory<Plant> {

    ALL;

    private Class<?>[] classes;

    @Override
    public Class<?>[] classes() {
        return classes;
    }

    static {

        ALL.classes = new Class[]{
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

    @Override
    public Image getSprite() {
        return EditorUtilies.getTerrainFeatureTexture(116);//Ice cap
    }

    public static final EditorItemBag bag = new EditorItemBag("name", 0) {};

    static {
        for (Plants p : values()) {
            bag.items.add(new PlantBag(p));
        }
    }

    public static class PlantBag extends EditorInvCategoryBag {
        public PlantBag(Plants plants) {
            super(plants);
            for (Class<?> p : plants.classes) {
                items.add(new PlantItem((Plant) Reflection.newInstance(p)));
            }
        }
    }
}