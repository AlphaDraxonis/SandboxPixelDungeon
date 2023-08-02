package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.dungeon;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

import java.util.HashMap;
import java.util.Map;

public class EffectDuration implements Bundlable {

    public static float get(Class<?> effect) {
        return get(effect, 0);
    }

    public static float get(Class<?> effect, float defaultValue) {//from Map#getOrDefault
        Float v;
        Map<Class<?>, Float> durationMap = Dungeon.customDungeon.effectDuration.durationMap;
        if (durationMap == null) Dungeon.customDungeon.effectDuration.durationMap = new HashMap<>();
        return (((v = durationMap.get(effect)) != null) || durationMap.containsKey(effect))
                ? v
                : defaultValue;
    }

    private Map<Class<?>, Float> durationMap;


    public void put(Class<?> value, float duration) {
        if (duration == 0) durationMap.remove(value);
        else durationMap.put(value, duration);
    }

    public void load(EffectDuration template) {
        durationMap = template == null ? new HashMap<>() :
                (template.durationMap == null ? new HashMap<>() : template.durationMap);
    }

    private static final String KEYS = "keys";
    private static final String VALUES = "values";

    @Override
    public void storeInBundle(Bundle bundle) {
        if (durationMap == null) return;
        float[] values = new float[durationMap.size()];
        if (values.length != 0) {
            Class<?>[] keys = new Class[values.length];
            int i = 0;
            for (Class<?> clazz : durationMap.keySet()) {
                keys[i] = clazz;
                values[i] = durationMap.get(clazz);
                i++;
            }
            bundle.put(KEYS, values);
            bundle.put(VALUES, keys);
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        durationMap = new HashMap<>();
        if (bundle.contains(KEYS)) {
            float[] values = bundle.getFloatArray(KEYS);
            if (values == null) return;
            Class<?>[] classes = bundle.getClassArray(VALUES);
            for (int i = 0; i < values.length; i++) {
                durationMap.put(classes[i], values[i]);
            }
        }
    }
}