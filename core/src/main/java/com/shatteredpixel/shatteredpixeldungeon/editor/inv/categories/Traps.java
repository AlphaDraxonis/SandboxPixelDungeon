package com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories;

import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TrapItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.RandomItem;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.*;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Image;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.*;

public enum Traps {

    GAS,
    WATER,
    FIRE,
    DART,
    SPAWN,
    TELEPORT,
    EQUIPMENT;


    private Class<?>[] classes;

    public Class<?>[] classes() {
        return classes;
    }

    static {

        GAS.classes = new Class[]{
                ToxicTrap.class,
                CorrosionTrap.class,
                ConfusionTrap.class,
                ParalyticTrap.class,
                ShroudingTrap.class,
                GrippingTrap.class,
                FlashingTrap.class,
                RockfallTrap.class,
                GnollRockfallTrap.class,
                OozeTrap.class,
                UnstableTrap.class,
                RandomItem.RandomTrap.class
        };
        WATER.classes = new Class[]{
                ChillingTrap.class,
                FrostTrap.class,
                GeyserTrap.class,
                ShockingTrap.class,
                StormTrap.class
        };
        FIRE.classes = new Class[]{
                BlazingTrap.class,
                BurningTrap.class,
                ExplosiveTrap.class
        };
        DART.classes = new Class[]{
                WornDartTrap.class,
                PoisonDartTrap.class,
                DisintegrationTrap.class,
                GrimTrap.class
        };
        SPAWN.classes = new Class[]{
                FlockTrap.class,
                AlarmTrap.class,
                GuardianTrap.class,
                RageTrap.class,
                ActionTrap.class,
                SummoningTrap.class,
                DistortionTrap.class
        };
        TELEPORT.classes = new Class[]{
                TeleportationTrap.class,
                WarpingTrap.class,
                GatewayTrap.class,
                WarpwayTrap.class,
                PitfallTrap.class
        };
        EQUIPMENT.classes = new Class[]{
                WeakeningTrap.class,
                CursingTrap.class,
                DisarmingTrap.class,
                LooseItemsTrap.class,
                GoldDiggerTrap.class
        };
    }

    private static final Class<?>[] EMPTY_TRAP_CLASS_ARRAY = new Class[0];

    public static Class<?>[][] getAllTraps(Set<Class<? extends Trap>> trapsToIgnore) {
        Traps[] all = values();
        Class<?>[][] ret = new Class[all.length][];
        for (int i = 0; i < all.length; i++) {
            List<Class<?>> traps = new ArrayList<>(Arrays.asList(all[i].classes()));
            if (trapsToIgnore != null) traps.removeAll(trapsToIgnore);
            ret[i] = traps.toArray(EMPTY_TRAP_CLASS_ARRAY);
        }
        return ret;
    }

    public static Class<? extends Trap> getRandomTrap(Set<Class<? extends Trap>> trapsToIgnore) {
        Class<? extends Trap>[][] traps = (Class<? extends Trap>[][]) getAllTraps(trapsToIgnore);
        List<Class<? extends Trap>> trapList = new ArrayList<>();
        for (Class<? extends Trap>[] trap : traps) {
            trapList.addAll(Arrays.asList(trap));
        }
        int length = trapList.size();
        if (length == 0) return null;
        return trapList.get(Random.Int(length));
    }

    public Image getImage() {
        Trap t;
        switch (Traps.this) {
            case GAS:
                t = new ToxicTrap();
                break;
            case WATER:
                t = new ChillingTrap();
                break;
            case FIRE:
                t = new BurningTrap();
                break;
            case DART:
                t = new PoisonDartTrap();
                break;
            case SPAWN:
                t = new SummoningTrap();
                break;
            case TELEPORT:
                t = new GatewayTrap();
                break;
            case EQUIPMENT:
                t = new DisarmingTrap();
                break;
            default:
                return new ItemSprite(ItemSpriteSheet.SOMETHING);
        }
        t.visible = true;
        return t.getSprite();
    }


    public static final EditorItemBag bag = new EditorItemBag("name", 0) {};

    static {
        for (Traps traps : Traps.values()) {
            bag.items.add(new TrapBag(traps));
        }
    }

    public static class TrapBag extends EditorItemBag {
        private final Traps traps;

        public TrapBag(Traps traps) {
            super(traps.name().toLowerCase(Locale.ENGLISH), 0);
            this.traps = traps;
            for (Class<?> t : traps.classes) {
                Trap trap = (Trap) Reflection.newInstance(t);
                trap.visible = true;
                items.add(new TrapItem(trap));
            }
        }

        @Override
        public Image getCategoryImage() {
            return traps.getImage();
        }
    }

}