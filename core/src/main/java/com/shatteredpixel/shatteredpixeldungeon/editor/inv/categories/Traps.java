package com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories;

import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TrapItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.RandomItem;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.*;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Image;
import com.watabou.utils.Reflection;

public enum Traps implements EditorInvCategory<Trap> {

    GAS,
    WATER,
    FIRE,
    DART,
    SPAWN,
    TELEPORT,
    EQUIPMENT;

    private Class<?>[] classes;

    @Override
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

    @Override
    public Image getSprite() {
        Trap t;
        switch (Traps.this) {
            case GAS: t = new ToxicTrap(); break;
            case WATER: t = new ChillingTrap(); break;
            case FIRE: t = new BurningTrap(); break;
            case DART: t = new PoisonDartTrap(); break;
            case SPAWN: t = new SummoningTrap(); break;
            case TELEPORT: t = new GatewayTrap(); break;
            case EQUIPMENT: t = new DisarmingTrap(); break;
            default: return new ItemSprite(ItemSpriteSheet.SOMETHING);
        }
        t.visible = true;
        return t.getSprite();
    }


    public static final EditorItemBag bag = new EditorItemBag("name", 0) {};

    static {
        for (Traps traps : values()) {
            bag.items.add(new TrapBag(traps));
        }
    }

    public static class TrapBag extends EditorInvCategoryBag {
        public TrapBag(Traps traps) {
            super(traps);
            for (Class<?> t : traps.classes) {
                Trap trap = (Trap) Reflection.newInstance(t);
                trap.visible = true;
                items.add(new TrapItem(trap));
            }
        }
    }

}