package com.shatteredpixel.shatteredpixeldungeon.editor.scene.inv;

import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.Items;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.Mobs;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.Traps;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.Image;

import java.util.ArrayList;

public abstract class EditorItemBag extends Bag{




    @Override
    public int capacity() {
        return 100;
    }

    public Image getCategoryImage(){
        return null;
    }


    public static final EditorItemBag mainBag = new EditorItemBag() {
        @Override
        public int capacity() {
            int count = super.capacity();
            for (Item i : items) if (i instanceof Bag) count++;
            return count;
        }
    };

    static {
        EditorItemBag tiles = new EditorItemBag(){
            @Override
            public String name() {
                return Messages.get(EditorItemBag.class,"tiles");
            }
        };
        tiles.items.addAll(Tiles.bags);
        mainBag.items.add(tiles);

        EditorItemBag mobs = new EditorItemBag() {
            @Override
            public String name() {
                return Messages.get(EditorItemBag.class,"mobs");
            }
        };
        mobs.items.addAll(Mobs.bags);
        mainBag.items.add(mobs);

        mainBag.items.add(Items.bag);

        mainBag.items.add(Traps.bag);
    }


    public static EditorItemBag getLastBag() {
        EditorItemBag lastBag = WndEditorItemsBag.lastBag();
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