package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum Buffs {


    BUFFS;

    private static final Class<?>[] EMPTY_BUFF_CLASS_ARRAY = new Class[0];

    public static Class<?>[][] getAllBuffs2(Set<? extends Buff> buffsToIgnore) {
        Set<Class<? extends Buff>> ignore = new HashSet<>();
        for (Buff b : buffsToIgnore) ignore.add(b.getClass());
        return getAllBuffs(ignore);
    }

    public static Class<?>[][] getAllBuffs(Set<Class<? extends Buff>> buffsToIgnore) {
        Buffs[] all = values();
        Class<?>[][] ret = new Class[all.length][];
        for (int i = 0; i < all.length; i++) {
            List<Class<?>> buffs = new ArrayList<>(Arrays.asList(all[i].classes()));
            if (buffsToIgnore != null) buffs.removeAll(buffsToIgnore);
            ret[i] = buffs.toArray(EMPTY_BUFF_CLASS_ARRAY);
        }
        return ret;
    }

    private Class<?>[] classes;

    public Class<?>[] classes() {
        return classes;
    }


    static {

        BUFFS.classes = new Class[]{

                ChampionEnemy.Blazing.class,
                ChampionEnemy.Projecting.class,
                ChampionEnemy.AntiMagic.class,
                ChampionEnemy.Giant.class,
                ChampionEnemy.Blessed.class,
                ChampionEnemy.Growing.class

        };

    }


}