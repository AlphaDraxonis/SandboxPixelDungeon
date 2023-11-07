package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.editor.util.Consumer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BlobStoreMap {

    private final HashMap<Class<? extends Blob>, Blob> blobs = new HashMap<>();

    public void put(Class<? extends Blob> clazz, Blob blob) {
        blobs.put(clazz, blob);
    }

    private static final Blob[] EMPTY_BLOB_ARRAY = new Blob[0];

    public Blob[] get(Class<? extends Blob> clazz) {
        List<Blob> result = new ArrayList<>(3);
        for (Blob b : blobs.values()) {
            if (clazz.isAssignableFrom(b.getClass())) {
                result.add(b);
            }
        }
        return result.toArray(EMPTY_BLOB_ARRAY);
    }

    public Blob getOnly(Class<? extends Blob> clazz) {
        return blobs.get(clazz);
    }

    public void doOnEach(Class<? extends Blob> clazz, Consumer<Blob> whatToDo){
        for (Blob b : get(clazz)){
            whatToDo.accept(b);
        }
    }

    public int size() {
        return blobs.size();
    }

    public boolean isEmpty() {
        return blobs.isEmpty();
    }

    public boolean containsKey(Class<? extends Blob> key) {
        return blobs.containsKey(key);
    }

    public void clear() {
        blobs.clear();
    }

    public void putAll(Map<? extends Class<? extends Blob>, ? extends Blob> m) {
        blobs.putAll(m);
    }

    public Blob remove(Class<? extends Blob> key) {
        return blobs.remove(key);
    }

    public boolean containsValue(Blob value) {
        return blobs.containsValue(value);
    }

    public Set<Class<? extends Blob>> keySet() {
        return blobs.keySet();
    }

    public Collection<Blob> values() {
        return blobs.values();
    }

    public Set<Map.Entry<Class<? extends Blob>, Blob>> entrySet() {
        return blobs.entrySet();
    }

    //    public Blob getOrDefault(Class<? extends Blob> key, Blob defaultValue) {
//        return blobs.getOrDefault(key, defaultValue);
//    }
//
//    public Blob putIfAbsent(Class<? extends Blob> key, Blob value) {
//        return blobs.putIfAbsent(key, value);
//    }
//
//    public boolean remove(Class<? extends Blob> key, Object value) {
//        return blobs.remove(key, value);
//    }
//
//    public boolean replace(Class<? extends Blob> key, Blob oldValue, Blob newValue) {
//        return blobs.replace(key, oldValue, newValue);
//    }
//
//    public Blob replace(Class<? extends Blob> key, Blob value) {
//        return blobs.replace(key, value);
//    }
//
//    public Blob computeIfAbsent(Class<? extends Blob> key, Function<? super Class<? extends Blob>, ? extends Blob> mappingFunction) {
//        return blobs.computeIfAbsent(key, mappingFunction);
//    }
//
//    public Blob computeIfPresent(Class<? extends Blob> key, BiFunction<? super Class<? extends Blob>, ? super Blob, ? extends Blob> remappingFunction) {
//        return blobs.computeIfPresent(key, remappingFunction);
//    }
//
//    public Blob compute(Class<? extends Blob> key, BiFunction<? super Class<? extends Blob>, ? super Blob, ? extends Blob> remappingFunction) {
//        return blobs.compute(key, remappingFunction);
//    }
//
//    public Blob merge(Class<? extends Blob> key, Blob value, BiFunction<? super Blob, ? super Blob, ? extends Blob> remappingFunction) {
//        return blobs.merge(key, value, remappingFunction);
//    }
//
//    public void forEach(BiConsumer<? super Class<? extends Blob>, ? super Blob> action) {
//        blobs.forEach(action);
//    }
//
//    public void replaceAll(BiFunction<? super Class<? extends Blob>, ? super Blob, ? extends Blob> function) {
//        blobs.replaceAll(function);
//    }
}