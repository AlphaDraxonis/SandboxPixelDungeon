package com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Daze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Degrade;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Doom;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dread;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Drowsy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Foresight;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Haste;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hex;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Levitation;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicalSight;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicalSleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MindVision;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Stamina;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum Buffs {


    CHAMPIONS,
    MOVEMENT,
    FIGHT,
    SIGHT,
    OTHER;

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
                Messages.titleCase(Messages.get(Buffs.class, "movement")),
                Messages.titleCase(Messages.get(Buffs.class, "fight")),
                Messages.titleCase(Messages.get(Buffs.class, "sight")),
                Messages.titleCase(Messages.get(Buffs.class, "other"))
        };
    }

    private Class<?>[] classes;

    public Class<?>[] classes() {
        return classes;
    }


    static {

        CHAMPIONS.classes = ChampionEnemy.CLASSES;

        MOVEMENT.classes = new Class[]{
                Haste.class,
                Stamina.class,
                Cripple.class,
                Frost.class,
                Paralysis.class,
                Vertigo.class,//Confusion
                Amok.class,
                Terror.class,
                Dread.class,
        };

        FIGHT.classes = new Class[]{
//                Fury.class,
                Weakness.class,
                Degrade.class,
                Hex.class,
                Daze.class,
                Doom.class,
                Corruption.class,
//                Ooze.class,
        };

        SIGHT.classes = new Class[]{
                Invisibility.class,
                Blindness.class,
                Light.class,
                MindVision.class,
                MagicalSight.class,
                Foresight.class,
        };

        OTHER.classes = new Class[]{
                Levitation.class,
                MagicImmune.class,
                Drowsy.class,
                MagicalSleep.class,
                Recharging.class,
//                EnhancedRings.class,
        };
    }


}