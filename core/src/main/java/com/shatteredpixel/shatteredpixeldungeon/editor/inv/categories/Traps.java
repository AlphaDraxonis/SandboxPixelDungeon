package com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories;

import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.RandomItem;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.*;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.shatteredpixel.shatteredpixeldungeon.usercontent.blueprints.CustomTrap;
import com.shatteredpixel.shatteredpixeldungeon.usercontent.ui.WndNewCustomObject;
import com.watabou.noosa.Image;

import java.util.Locale;

public final class Traps extends GameObjectCategory<Trap> {

    private static Traps instance = new Traps();

    private final Gas GAS = new Gas();
    private final Water WATER = new Water();
    private final Fire FIRE = new Fire();
    private final Dart DART = new Dart();
    private final Spawn SPAWN = new Spawn();
    private final Teleport TELEPORT = new Teleport();
    private final Equipment EQUIPMENT = new Equipment();

    {
        values = new TrapCategory[] {
                GAS,
                WATER,
                FIRE,
                DART,
                SPAWN,
                TELEPORT,
                EQUIPMENT
        };
    }

    private Traps() {
        super(new EditorItemBag(){});
        addItemsToBag();
    }

    public static Traps instance() {
        return instance;
    }

    public static EditorItemBag bag() {
        return instance().getBag();
    }

    @Override
    public void updateCustomObjects() {
        updateCustomObjects(Trap.class);
    }

    public static void updateCustomTrap(CustomTrap customTrap) {
        if (instance != null) {
            instance.updateCustomObject(customTrap);
        }
    }

    @Override
    public ScrollingListPane.ListButton createAddBtn() {
        return new ScrollingListPane.ListButton() {
            protected RedButton createButton() {
                return new RedButton(Messages.get(Traps.class, "add_custom_obj")) {
                    @Override
                    protected void onClick() {
                        DungeonScene.show(new WndNewCustomObject(CustomTrap.class));
                    }
                };
            }
        };
    }

    private static abstract class TrapCategory extends GameObjectCategory.SubCategory<Trap> {

        private final Trap sprite;

        protected TrapCategory(Trap sprite, Class<?>[] classes) {
            super(classes);
            this.sprite = sprite;
            sprite.visible = true;
        }

        @Override
        public Image getSprite() {
            return sprite.getSprite();
        }

        @Override
        public String messageKey() {
            return getClass().getSimpleName().toLowerCase(Locale.ENGLISH);
        }
    }

    private static final class Gas extends TrapCategory {

        private Gas() {
            super(new ToxicTrap(), new Class[] {
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
            });
        }
    }

    private static final class Water extends TrapCategory {

        private Water() {
            super(new ChillingTrap(), new Class[] {
                    ChillingTrap.class,
                    FrostTrap.class,
                    GeyserTrap.class,
                    ShockingTrap.class,
                    StormTrap.class
            });
        }
    }

    private static final class Fire extends TrapCategory {

        private Fire() {
            super(new BurningTrap(), new Class[] {
                    BlazingTrap.class,
                    BurningTrap.class,
                    ExplosiveTrap.class
            });
        }
    }

    private static final class Dart extends TrapCategory {

        private Dart() {
            super(new PoisonDartTrap(), new Class[] {
                    WornDartTrap.class,
                    PoisonDartTrap.class,
                    DisintegrationTrap.class,
                    GrimTrap.class
            });
        }
    }

    private static final class Spawn extends TrapCategory {

        private Spawn() {
            super(new SummoningTrap(), new Class[] {
                    FlockTrap.class,
                    AlarmTrap.class,
                    GuardianTrap.class,
                    RageTrap.class,
                    ActionTrap.class,
                    SummoningTrap.class,
                    DistortionTrap.class
            });
        }
    }

    private static final class Teleport extends TrapCategory {

        private Teleport() {
            super(new GatewayTrap(), new Class[] {
                    TeleportationTrap.class,
                    WarpingTrap.class,
                    GatewayTrap.class,
                    WarpwayTrap.class,
                    PitfallTrap.class
            });
        }
    }

    private static final class Equipment extends TrapCategory {

        private Equipment() {
            super(new DisarmingTrap(), new Class[] {
                    WeakeningTrap.class,
                    CursingTrap.class,
                    DisarmingTrap.class,
                    LooseItemsTrap.class,
                    GoldDiggerTrap.class
            });
        }
    }

}