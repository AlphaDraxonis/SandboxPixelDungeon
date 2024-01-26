package com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories;

import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TrapItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.RandomItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.AlarmTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.BlazingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.BurningTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ChillingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ConfusionTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.CorrosionTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.CursingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DisarmingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DisintegrationTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DistortionTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ExplosiveTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FlashingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FlockTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FrostTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GatewayTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GeyserTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GnollRockfallTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GrimTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GrippingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GuardianTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.OozeTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ParalyticTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.PitfallTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.PoisonDartTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.RockfallTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ShockingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ShroudingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.StormTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.SummoningTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.TeleportationTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ToxicTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.UnstableTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WarpingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WarpwayTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WeakeningTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WornDartTrap;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Image;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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
                DisarmingTrap.class
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
        return TrapItem.getTrapImage(t);
    }


    public static final EditorItemBag bag = new EditorItemBag("name", 0) {
        @Override
        public Item findItem(Object src) {
            for (Item bag : items) {
                for (Item i : ((Bag) bag).items) {
                    if (((TrapItem) i).getObject().getClass() == src) return i;
                }
            }
            return null;
        }
    };

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