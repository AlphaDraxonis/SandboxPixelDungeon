package com.alphadraxonis.sandboxpixeldungeon.editor.levels;

import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.items.MobItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.items.RoomItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.util.EditorUtilies;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.items.bags.Bag;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ItemDistribution<T extends Item> implements Bundlable {

    private ItemDistribution() {
    }

    private List<String> levels = new ArrayList<>(6);
    private List<T> objectsToDistribute = new ArrayList<>(6);

//    private Map<String, DistributedObjects<T>> distributedObjects;
//
//    public static class DistributedObjects<T extends Bundlable> implements Bundlable {
//        private String level;
//        private Collection<T> objects;
//
//        private DistributedObjects(String level, Collection<T> objects) {
//            this.level = level;
//            this.objects = objects;
//        }
//
//        private static final String LEVEL = "level";
//        private static final String VALUES = "values";
//
//        @Override
//        public void storeInBundle(Bundle bundle) {
//            bundle.put(LEVEL, level);
//            bundle.put(VALUES, objects);
//        }
//
//        @Override
//        public void restoreFromBundle(Bundle bundle) {
//            level = bundle.getString(LEVEL);
//            objects = (Collection<T>) bundle.getCollection(VALUES);
//        }
//
//        public String getLevel() {
//            return level;
//        }
//    }


    public List<String> getLevels() {
        return levels;
    }

    public List<T> getObjectsToDistribute() {
        return objectsToDistribute;
    }

    public void initForPlaying() {
        final int numLevels = levels.size();
        if (numLevels == 0) return;
        int leftObjects = objectsToDistribute.size();
        while (leftObjects > 0) {
            List<String> levelsToDistribute = new ArrayList<>(levels);
            int levelsLeft = numLevels;
            while (levelsLeft > 0 && leftObjects > 0) {
                String spawnAt = levelsToDistribute.remove(Random.Int(levelsLeft));
                T objToSpawn = objectsToDistribute.remove(Random.Int(leftObjects));

                addToSpawn(Dungeon.customDungeon.getFloor(spawnAt), objToSpawn);
                leftObjects--;
                levelsLeft--;
            }
        }

    }

    private static final String LEVELS = "levels";
    private static final String OBJECTS_TO_DISTRIBUTE = "objects_to_distribute_";
//    private static final String DISTRIBUTED_OBJECTS = "distributed_objects";

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(LEVELS, levels.toArray(EditorUtilies.EMPTY_STRING_ARRAY));
//        if (distributedObjects != null)
//            bundle.put(DISTRIBUTED_OBJECTS, distributedObjects.values());

        int i = 0;
        for (T t : objectsToDistribute) {
            bundle.put(OBJECTS_TO_DISTRIBUTE + i, t);
            i++;
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        levels = new ArrayList<>(Arrays.asList(bundle.getStringArray(LEVELS)));

//        if (bundle.contains(DISTRIBUTED_OBJECTS)) {
//            distributedObjects = new HashMap<>();
//            Collection<Bundlable> distr = bundle.getCollection(DISTRIBUTED_OBJECTS);
//            for (Bundlable dd : distr) {
//                DistributedObjects<T> cast = (DistributedObjects<T>) dd;
//                distributedObjects.put(cast.getLevel(), cast);
//            }
//        }

        objectsToDistribute = new ArrayList<>();
        int i = 0;
        String key;
        while (bundle.contains(key = OBJECTS_TO_DISTRIBUTE + i)) {
            objectsToDistribute.add((T) bundle.get(key));
            i++;
        }
    }

    protected abstract void addToSpawn(LevelScheme levelScheme, T obj);

    public abstract Class<? extends Bag> getPreferredBag();

    public String getDistributionLabel(){
        return Messages.get(this,"label");
    }


    public static class Items extends ItemDistribution<Item> {
        private boolean prizeItems;

        public Items() {
        }

        public Items(boolean prizeItems) {
            this.prizeItems = prizeItems;
        }

        @Override
        protected void addToSpawn(LevelScheme levelScheme, Item obj) {
            if (prizeItems) levelScheme.prizeItemsToSpawn.add(obj);
            else levelScheme.itemsToSpawn.add(obj);
        }

        @Override
        public Class<? extends Bag> getPreferredBag() {
            return com.alphadraxonis.sandboxpixeldungeon.editor.inv.categories.Items.bag.getClass();
        }

        private static final String PRIZE_ITEM = "prize_item";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(PRIZE_ITEM, prizeItems);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            prizeItems = bundle.getBoolean(PRIZE_ITEM);
        }
    }

    public static class Mobs extends ItemDistribution<MobItem> {
        @Override
        protected void addToSpawn(LevelScheme levelScheme, MobItem obj) {
            levelScheme.mobsToSpawn.add(obj.mob());
        }

        @Override
        public Class<? extends Bag> getPreferredBag() {
            return com.alphadraxonis.sandboxpixeldungeon.editor.inv.categories.Mobs.bag.getClass();
        }
    }

    public static class Rooms extends ItemDistribution<RoomItem> {
        @Override
        protected void addToSpawn(LevelScheme levelScheme, RoomItem obj) {
            levelScheme.roomsToSpawn.add(obj.room());
        }

        @Override
        public Class<? extends Bag> getPreferredBag() {
            return com.alphadraxonis.sandboxpixeldungeon.editor.inv.categories.Rooms.bag.getClass();
        }
    }
}