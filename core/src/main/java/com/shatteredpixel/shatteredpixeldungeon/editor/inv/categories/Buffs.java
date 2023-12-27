package com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Haste;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Levitation;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MindVision;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum Buffs {


    CHAMPIONS,
    POSITIVE,
    NEGATIVE;

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

    public static String[] getCatNames() {
        return new String[]{
                Messages.titleCase(Messages.get(Buffs.class, "champions")),
                Messages.titleCase(Messages.get(Buffs.class, "positive")),
                Messages.titleCase(Messages.get(Buffs.class, "negative"))
        };
    }

    private Class<?>[] classes;

    public Class<?>[] classes() {
        return classes;
    }


    static {

        CHAMPIONS.classes = new Class[]{

                ChampionEnemy.Blazing.class,
                ChampionEnemy.Projecting.class,
                ChampionEnemy.AntiMagic.class,
                ChampionEnemy.Giant.class,
                ChampionEnemy.Blessed.class,
                ChampionEnemy.Growing.class

        };

        POSITIVE.classes = new Class[]{
                Invisibility.class,
                MindVision.class,
                Haste.class,
                Levitation.class
        };

        NEGATIVE.classes = new Class[]{
                Vertigo.class,//Confusion
                Corruption.class,
                Amok.class,
                Terror.class
        };

    }


}