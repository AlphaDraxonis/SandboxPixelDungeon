package com.alphadraxonis.sandboxpixeldungeon.editor.inv.categories;

import com.alphadraxonis.sandboxpixeldungeon.editor.inv.items.TrapItem;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.AlarmTrap;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.BlazingTrap;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.BurningTrap;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.ChillingTrap;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.ConfusionTrap;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.CorrosionTrap;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.CursingTrap;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.DisarmingTrap;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.DisintegrationTrap;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.DistortionTrap;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.ExplosiveTrap;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.FlashingTrap;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.FlockTrap;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.FrostTrap;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.GatewayTrap;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.GeyserTrap;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.GrimTrap;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.GrippingTrap;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.GuardianTrap;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.OozeTrap;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.PitfallTrap;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.PoisonDartTrap;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.RockfallTrap;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.ShockingTrap;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.StormTrap;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.SummoningTrap;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.TeleportationTrap;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.ToxicTrap;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.Trap;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.WarpingTrap;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.WeakeningTrap;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.WornDartTrap;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSprite;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Image;
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
                GrippingTrap.class,
                FlashingTrap.class,
                RockfallTrap.class,
                OozeTrap.class
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

    public static String[] getAllNames() {
        Traps[] all = values();
        String[] ret = new String[all.length];
        for (int i = 0; i < all.length; i++) {
            ret[i] = all[i].getName();
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
        return trapList.get((int) (Math.random() * length));
    }

    public String getName() {
        return Messages.get(Traps.class, name().toLowerCase(Locale.ENGLISH));
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


    public static final EditorItemBag bag = new EditorItemBag(Messages.get(EditorItemBag.class, "traps"), 0);

    static {
        for (Traps traps : Traps.values()) {
            bag.items.add(new TrapBag(traps));
        }
    }

    public static class TrapBag extends EditorItemBag {
        private final Traps traps;

        public TrapBag(Traps traps) {
            super(traps.getName(), 0);
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