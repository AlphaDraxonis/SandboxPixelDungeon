package com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.ImpShopkeeper;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.RatKing;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Sheep;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Shopkeeper;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Wandmaker;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.blueprints.CustomMob;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.ui.WndNewCustomObject;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SentryRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BatSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GnollSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MonkSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SkeletonSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SuccubusSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WandmakerSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WraithSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.watabou.noosa.Image;

import java.util.Locale;

public final class Mobs extends GameObjectCategory<Mob> {

    private static Mobs instance = new Mobs();

           final Sewer   SEWER   = new Sewer();
           final Prison  PRISON  = new Prison();
           final Caves   CAVES   = new Caves();
           final City    CITY    = new City();
           final Halls   HALLS   = new Halls();
           final Special SPECIAL = new Special();
    public final NPC     NPC     = new NPC();

    {
        values = new MobCategory[] {
                SEWER,
                PRISON,
                CAVES,
                CITY,
                HALLS,
                SPECIAL,
                NPC
        };
    }

    private Mobs() {
        super(new EditorItemBag(){});
        addItemsToBag();
    }

    public static Mobs instance() {
        return instance;
    }

    public static EditorItemBag bag() {
        return instance().getBag();
    }

    @Override
    public void updateCustomObjects() {
        updateCustomObjects(Mob.class);
    }

    public static void updateCustomMob(CustomMob customMob) {
        if (instance != null) {
            instance.updateCustomObject(customMob);
        }
    }

    @Override
    public ScrollingListPane.ListButton createAddBtn() {
        return new ScrollingListPane.ListButton() {
            protected RedButton createButton() {
                return new RedButton(Messages.get(Mobs.class, "add_custom_obj")) {
                    @Override
                    protected void onClick() {
                        DungeonScene.show(new WndNewCustomObject(CustomMob.class));
                    }
                };
            }
        };
    }

    static abstract class MobCategory extends GameObjectCategory.SubCategory<Mob> {

        private MobCategory(Class<?>[] classes) {
            super(classes);
        }

        @Override
        public String getName() {
                 if (getClass().equals(Sewer.class))  return Document.INTROS.pageTitle("Sewers");
            else if (getClass().equals(Prison.class)) return Document.INTROS.pageTitle("Prison");
            else if (getClass().equals(Caves.class))  return Document.INTROS.pageTitle("Caves");
            else if (getClass().equals(City.class))   return Document.INTROS.pageTitle("City");
            else if (getClass().equals(Halls.class))  return Document.INTROS.pageTitle("Halls");
            return Messages.get(Mobs.class, messageKey());
        }

        @Override
        public Image getSprite() {
                 if (getClass().equals(Sewer.class))    return new GnollSprite();
            else if (getClass().equals(Prison.class))   return new SkeletonSprite();
            else if (getClass().equals(Caves.class))    return new BatSprite();
            else if (getClass().equals(City.class))     return new MonkSprite();
            else if (getClass().equals(Halls.class))    return new SuccubusSprite();
            else if (getClass().equals(Special.class))  return new WraithSprite();
            else if (getClass().equals(NPC.class))      return new WandmakerSprite();
            return new ItemSprite(ItemSpriteSheet.SOMETHING);
        }

        @Override
        public String messageKey() {
            return getClass().getSimpleName().toLowerCase(Locale.ENGLISH);
        }
    }

    private static final class Sewer extends MobCategory {

        private Sewer() {
            super(new Class[] {
                    Rat.class,
                    Albino.class,
                    FetidRat.class,
                    Snake.class,
                    Gnoll.class,
                    GnollTrickster.class,
                    Crab.class,
                    GreatCrab.class,
                    Swarm.class,
                    Slime.class,
                    CausticSlime.class,
                    Goo.class
            });
        }
    }

    private static final class Prison extends MobCategory {

        private Prison() {
            super(new Class[] {
                    Skeleton.class,
                    Thief.class,
                    Bandit.class,
                    DM100.class,
                    Necromancer.class,
                    SpectralNecromancer.class,
                    Guard.class,
                    RotLasher.class,
                    RotHeart.class,
                    Elemental.NewbornFireElemental.class,
                    Tengu.class
            });
        }
    }

    private static final class Caves extends MobCategory {

        private Caves() {
            super(new Class[] {
                    Brute.class,
                    ArmoredBrute.class,
                    Shaman.RedShaman.class,
                    Shaman.BlueShaman.class,
                    Shaman.PurpleShaman.class,
                    Bat.class,
                    Spinner.class,
                    DM200.class,
                    DM201.class,
                    DM300.class,
                    Pylon.class,
                    GnollGuard.class,
                    GnollSapper.class,
                    GnollGeomancer.class,
                    FungalSpinner.class,
                    FungalSentry.class,
                    FungalCore.class,
                    CrystalGuardian.class,
                    CrystalWisp.class,
                    CrystalSpire.class
            });
        }
    }

    private static final class City extends MobCategory {

        private City() {
            super(new Class[] {
                    Ghoul.class,
                    Warlock.class,
                    Elemental.FireElemental.class,
                    Elemental.FrostElemental.class,
                    Elemental.ShockElemental.class,
                    Elemental.ChaosElemental.class,
                    Monk.class,
                    Senior.class,
                    Golem.class,
                    DwarfKing.class
            });
        }
    }

    private static final class Halls extends MobCategory {

        private Halls() {
            super(new Class[] {
                    Succubus.class,
                    Eye.class,
                    Scorpio.class,
                    Acidic.class,
                    RipperDemon.class,
                    DemonSpawner.class,
                    YogDzewa.class,
                    YogDzewa.Larva.class,
                    YogFist.SoiledFist.class,
                    YogFist.BurningFist.class,
                    YogFist.RustedFist.class,
                    YogFist.RottingFist.class,
                    YogFist.DarkFist.class,
                    YogFist.BrightFist.class
            });
        }
    }

    private static final class Special extends MobCategory {

        private Special() {
            super(new Class[] {
                    Statue.class,
                    ArmoredStatue.class,
                    Piranha.class,
                    PhantomPiranha.class,
                    Wraith.class,
                    TormentedSpirit.class,
                    Bee.class,
                    Mimic.class,
                    GoldenMimic.class,
                    CrystalMimic.class,
                    EbonyMimic.class,
                    SentryRoom.Sentry.class,
                    HeroMob.class
            });
        }
    }

    public static final class NPC extends MobCategory {

        private NPC() {
            super(new Class[] {
                    Ghost.class,
                    Wandmaker.class,
                    Blacksmith.class,
                    Imp.class,
                    Shopkeeper.class,
                    ImpShopkeeper.class,
                    RatKing.class,
                    Sheep.class,
                    WandOfRegrowth.Lotus.class
            });
        }
    }

}