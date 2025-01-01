package com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories;

import com.shatteredpixel.shatteredpixeldungeon.customobjects.blueprints.CustomPlant;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.ui.WndNewCustomObject;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
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
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.watabou.noosa.Image;

import java.util.Locale;

public final class Plants extends GameObjectCategory<Plant> {

    private static Plants instance = new Plants();

    private final PlantsCat ALL = new PlantsCat();

    {
        values = new PlantCategory[] {
                ALL
        };
    }

    private Plants() {
        super(new EditorItemBag(){});
        addItemsToBag();
    }

    public static Plants instance() {
        return instance;
    }

    public static EditorItemBag bag() {
        return instance().getBag();
    }

    @Override
    public void updateCustomObjects() {
        updateCustomObjects(Plant.class);
    }

    public static void updateCustomPlant(CustomPlant customPlant) {
        if (instance != null) {
            instance.updateCustomObject(customPlant);
        }
    }

    @Override
    public ScrollingListPane.ListButton createAddBtn() {
        return new ScrollingListPane.ListButton() {
            protected RedButton createButton() {
                return new RedButton(Messages.get(Plants.class, "add_custom_obj")) {
                    @Override
                    protected void onClick() {
                        DungeonScene.show(new WndNewCustomObject(CustomPlant.class));
                    }
                };
            }
        };
    }

    private static abstract class PlantCategory extends GameObjectCategory.SubCategory<Plant> {

        private PlantCategory(Class<?>[] classes) {
            super(classes);
        }

        @Override
        public Image getSprite() {
            return EditorUtilities.getTerrainFeatureTexture(116);//Ice cap
        }

        @Override
        public String messageKey() {
            String name = getClass().getSimpleName().toLowerCase(Locale.ENGLISH);
            return name.substring(0, name.length() - 3);
        }
    }

    private static final class PlantsCat extends PlantCategory {

        private PlantsCat() {
            super(new Class[] {
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
            });
        }
    }
}