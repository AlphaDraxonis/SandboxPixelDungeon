package com.shatteredpixel.shatteredpixeldungeon.editor.levels;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ItemDistribution<T extends Bundlable> implements Bundlable {

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
            objectsToDistribute.add((T) bundle.getBundle(key));
            i++;
        }
    }

    protected abstract void addToSpawn(LevelScheme levelScheme, T obj);


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

    public static class Mobs extends ItemDistribution<Mob> {
        @Override
        protected void addToSpawn(LevelScheme levelScheme, Mob obj) {
            levelScheme.mobsToSpawn.add(obj);
        }
    }

    public static class Rooms extends ItemDistribution<Room> {
        @Override
        protected void addToSpawn(LevelScheme levelScheme, Room obj) {
            levelScheme.roomsToSpawn.add(obj);
        }
    }
}