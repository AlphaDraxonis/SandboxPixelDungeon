package com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories;

import com.shatteredpixel.shatteredpixeldungeon.items.wands.CursedWand;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.watabou.noosa.Image;

import java.util.Locale;

public final class CursedWandEffects extends GameObjectCategory<CursedWand.CursedEffect> {

    private static CursedWandEffects instance = new CursedWandEffects();

    private final Common COMMON = new Common();
    private final Uncommon UNCOMMON = new Uncommon();
    private final Rare RARE = new Rare();
    private final VeryRare VERY_RARE = new VeryRare();

    {
        values = new CursedWandEffects.CursedWandEffectCategory[] {
                COMMON,
                UNCOMMON,
                RARE,
                VERY_RARE
        };
    }

    private CursedWandEffects() {
        super(new EditorItemBag(){});
        addItemsToBag();
    }

    public static CursedWandEffects instance() {
        return instance;
    }

    public static EditorItemBag bag() {
        return instance().getBag();
    }
    
    @Override
    public ScrollingListPane.ListButton createAddBtn() {
        return null;
    }
    
    @Override
    public void updateCustomObjects() {
    }
    
    private static abstract class CursedWandEffectCategory extends SubCategory<CursedWand.CursedEffect> {

        private CursedWandEffectCategory(Class<?>[] classes) {
            super(classes);
        }

        @Override
        public Image getSprite() {
            return new ItemSprite();
        }

        @Override
        public String messageKey() {
            return getClass().getSimpleName().toLowerCase(Locale.ENGLISH);
        }
    }

    private static final class Common extends CursedWandEffectCategory {

        private Common() {
            super(new Class[] {
                    CursedWand.BurnAndFreeze.class,
                    CursedWand.SpawnRegrowth.class,
                    CursedWand.RandomTeleport.class,
                    CursedWand.RandomGas.class,
                    CursedWand.RandomAreaEffect.class,
                    CursedWand.Bubbles.class,
                    CursedWand.RandomWand.class,
                    CursedWand.SelfOoze.class,
            });
        }
    }
    
    private static final class Uncommon extends CursedWandEffectCategory {
        
        private Uncommon() {
            super(new Class[] {
                    CursedWand.RandomPlant.class,
                    CursedWand.HealthTransfer.class,
                    CursedWand.Explosion.class,
                    CursedWand.LightningBolt.class,
                    CursedWand.Geyser.class,
                    CursedWand.SummonSheep.class,
                    CursedWand.Levitate.class,
                    CursedWand.Alarm.class,
            });
        }
    }
    
    private static final class Rare extends CursedWandEffectCategory {
        
        private Rare() {
            super(new Class[] {
                    CursedWand.SheepPolymorph.class,
                    CursedWand.CurseEquipment.class,
                    CursedWand.InterFloorTeleport.class,
                    CursedWand.SummonMonsters.class,
                    CursedWand.FireBall.class,
                    CursedWand.ConeOfColors.class,
                    CursedWand.MassInvuln.class,
                    CursedWand.Petrify.class,
            });
        }
    }
    
    private static final class VeryRare extends CursedWandEffectCategory {
        
        private VeryRare() {
            super(new Class[] {
                    CursedWand.ForestFire.class,
                    CursedWand.SpawnGoldenMimic.class,
                    CursedWand.AbortRetryFail.class,
                    CursedWand.RandomTransmogrify.class,
                    CursedWand.HeroShapeShift.class,
                    CursedWand.SuperNova.class,
                    CursedWand.SinkHole.class,
                    CursedWand.GravityChaos.class,
            });
        }
    }

}
