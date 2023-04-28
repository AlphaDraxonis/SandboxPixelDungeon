package com.shatteredpixel.shatteredpixeldungeon.levels.editor;

import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.ItemStatusHandler;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.AlchemicalCatalyst;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.Brew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.Elixir;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CustomDungeon {

    private static CustomDungeon customDungeon;

    //NOTE: for custom-item-support, you need to modify Category in Generator.java


    private List<Floor> floors = new ArrayList<>();//TODO auch templates für FloorGeneration speichern!, index ist id(für multibranches)


    private LinkedHashMap<Class<? extends Scroll>, String> scrollRuneLabels;
    private LinkedHashMap<Class<? extends Potion>, String> potionColorLabels;
    private LinkedHashMap<Class<? extends Ring>, String> ringGemLabels;

    public ItemStatusHandler<Scroll> getScrollRunes() {
        if (scrollRuneLabels == null)
            return new ItemStatusHandler<Scroll>((Class<? extends Scroll>[]) Generator.Category.SCROLL.classes, Scroll.runes);
        return new ItemStatusHandler<Scroll>((Class<? extends Scroll>[]) Generator.Category.SCROLL.classes, Scroll.runes) {
            @Override
            protected LinkedHashMap<Class<? extends Scroll>, String> assignLabels(Set<Class<? extends Scroll>> items, List<String> labelsLeft, LinkedHashMap<Class<? extends Scroll>, String> itemLabels) {
                items.removeAll(new LinkedHashSet<>(scrollRuneLabels.keySet()));
                labelsLeft.removeAll(new LinkedHashSet<>(scrollRuneLabels.values()));
                return super.assignLabels(items, labelsLeft, scrollRuneLabels);
            }
        };
    }

    public ItemStatusHandler<Potion> getPotionColors() {
        if (potionColorLabels == null)
            return new ItemStatusHandler<>((Class<? extends Potion>[]) Generator.Category.POTION.classes, Potion.colors);
        return new ItemStatusHandler<Potion>((Class<? extends Potion>[]) Generator.Category.POTION.classes, Potion.colors) {
            @Override
            protected LinkedHashMap<Class<? extends Potion>, String> assignLabels(Set<Class<? extends Potion>> items, List<String> labelsLeft, LinkedHashMap<Class<? extends Potion>, String> itemLabels) {
                items.removeAll(new LinkedHashSet<>(potionColorLabels.keySet()));
                labelsLeft.removeAll(new LinkedHashSet<>(potionColorLabels.values()));
                return super.assignLabels(items, labelsLeft, potionColorLabels);
            }
        };
    }

    public ItemStatusHandler<Ring> getRingGems() {
        if (ringGemLabels == null)
            return new ItemStatusHandler<>((Class<? extends Ring>[]) Generator.Category.RING.classes, Ring.gems);
        return new ItemStatusHandler<Ring>((Class<? extends Ring>[]) Generator.Category.RING.classes, Ring.gems) {
            @Override
            protected LinkedHashMap<Class<? extends Ring>, String> assignLabels(Set<Class<? extends Ring>> items, List<String> labelsLeft, LinkedHashMap<Class<? extends Ring>, String> itemLabels) {
                items.removeAll(new LinkedHashSet<>(ringGemLabels.keySet()));
                labelsLeft.removeAll(new LinkedHashSet<>(ringGemLabels.values()));
                return super.assignLabels(items, labelsLeft, ringGemLabels);
            }
        };
    }

    public Image getItemImage(Item item) {
        int code = -1;
        Class<? extends Item> c = item.getClass();
        if (item instanceof Scroll) {
            if (item instanceof ExoticScroll) c = ExoticScroll.exoToReg.get(c);
            code = (scrollRuneLabels == null || !scrollRuneLabels.containsKey(c)) ?
                    ItemSpriteSheet.SCROLL_HOLDER :
                    Scroll.runes.get(scrollRuneLabels.get(c)) + (item instanceof ExoticScroll ? 16 : 0);
        } else if (item instanceof Potion && !(item instanceof Elixir || item instanceof Brew||item instanceof AlchemicalCatalyst)) {
            if (item instanceof ExoticPotion) c = ExoticPotion.exoToReg.get(c);
            code = (potionColorLabels == null || !potionColorLabels.containsKey(c)) ?
                    ItemSpriteSheet.POTION_HOLDER :
                    Potion.colors.get(potionColorLabels.get(c)) + (item instanceof ExoticPotion ? 16 : 0);
        } else if (item instanceof Ring) {
            code = (ringGemLabels == null || !ringGemLabels.containsKey(c)) ?
                    ItemSpriteSheet.RING_HOLDER :
                    Ring.gems.get(ringGemLabels.get(c));
        }

        if (code == -1) code = item.image;
        ItemSprite is = new ItemSprite(code);
        if (item.glowing() != null) is.view(item);
        return is;
    }


    public static CustomDungeon getDungeon() {
        return customDungeon;
    }

    //if is in edit mode -> identify all items
    public static boolean isEditing() {
        return Game.scene() instanceof EditorScene;
    }


    static {
        //Debug
        customDungeon = new CustomDungeon();
        customDungeon.scrollRuneLabels = new LinkedHashMap<>();
        customDungeon.scrollRuneLabels.put(ScrollOfIdentify.class, "ISAZ");

    }
}