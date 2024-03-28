package com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories;

import com.shatteredpixel.shatteredpixeldungeon.editor.inv.FindInBag;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.WndEditorInv;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.LuaClass;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.noosa.Image;

import java.util.ArrayList;

public class EditorItemBag extends Bag {

    public static void callStaticInitializers() {
    }//absolutely necessary to call this before using any of the subclasses

    private final String name;

    public EditorItemBag(String name, int img) {
        this.name = name;
        image = img;
    }

    @Override
    public int capacity() {
        return items.size() + 1;
    }

    public Image getCategoryImage() {
        return new ItemSprite(image);
    }

    @Override
    public String name() {
        return Messages.get(this, name);
    }

    public Item findItem(FindInBag src){
        return findItem(this, src);
    }

    private static Item findItem(Bag bag, FindInBag src){

        if (src.getType() == FindInBag.Type.CLASS) {
            for (Item item : bag.items) {
                if (item instanceof Bag) {
                    Item result = findItem((Bag) item, src);
                    if (result != null) return result;
                }
                Object realItem;
                if (item instanceof EditorItem) realItem = ((EditorItem<?>) item).getObject();
                else realItem = item;
                if ((realItem.getClass() == Class.class ? realItem : realItem.getClass()) == src.getValue()) return item;
            }
        }
        if (src.getType() == FindInBag.Type.CUSTOM_OBJECT) {
            for (Item item : bag.items) {
                if (item instanceof Bag) {
                    Item result = findItem((Bag) item, src);
                    if (result != null) return result;
                }
                Object realItem;
                if (item instanceof EditorItem) realItem = ((EditorItem<?>) item).getObject();
                else realItem = item;
                if (realItem.getClass() == src.getValue()) return item;
                if (realItem instanceof LuaClass && ((LuaClass) realItem).getIdentifier() == (int) src.getValue()) return item;
            }
        }

        return null;
    }

    public static final EditorItemBag mainBag = new EditorItemBag("main", 0) {
        @Override
        public Image getCategoryImage() {
            return null;
        }
    };

    static {
        mainBag.items.add(Tiles.bag);
        mainBag.items.add(Mobs.bag);
        do {
            mainBag.items.add(Items.bag);
        } while (Items.bag == null);//remove callStaticInitializers() and see if it is still 'always false' (obv don't enter EditorScene before)
        mainBag.items.add(Traps.bag);
        mainBag.items.add(Plants.bag);
    }

    public static EditorItemBag getLastBag() {
        EditorItemBag lastBag = WndEditorInv.lastBag();
        if (lastBag != null) return lastBag;
        return getBag(EditorItemBag.class);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Bag> T getBag(Class<T> bagClass) {
        for (Item item : mainBag.items) {
            if (bagClass.isInstance(item)) {
                return (T) item;
            }
        }
        Class<?> cl = bagClass.getEnclosingClass();
        if (Rooms.class == cl) return (T) Rooms.bag;
        if (Tiles.class == cl) return (T) Tiles.bag;
        if (Mobs.class  == cl) return (T) Mobs.bag;
        if (Items.class == cl) return (T) Items.bag;
        if (Traps.class == cl) return (T) Traps.bag;
        if (Plants.class == cl) return (T) Plants.bag;
        if (MobSprites.class == cl) return (T) MobSprites.bag;

        return null;
    }


    public static ArrayList<Bag> getBags() {
        return getBags(mainBag);
    }

    public static ArrayList<Bag> getBags(Bag bag) {
        ArrayList<Bag> list = new ArrayList<>();
        for (Item item : bag.items) {
            if (item instanceof Bag) list.add((Bag) item);
        }
        return list;
    }

    public static Item getFirstItem() {
        return getFirstItem(mainBag);
    }

    public static Item getFirstItem(Bag bag) {
        for (Item item : bag.items) {
            if (!(item instanceof Bag)) return item;
        }
        if (bag.items.size() > 0) return getFirstItem((Bag) bag.items.get(0));
        return null;
    }
}