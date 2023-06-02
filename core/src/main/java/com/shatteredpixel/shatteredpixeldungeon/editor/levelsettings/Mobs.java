package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Acidic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Albino;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.ArmoredBrute;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.ArmoredStatue;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bandit;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bat;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bee;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Brute;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.CausticSlime;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Crab;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.CrystalMimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM100;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM200;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM201;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DemonSpawner;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Elemental;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Eye;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.FetidRat;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Ghoul;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Gnoll;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GnollTrickster;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GoldenMimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Golem;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Goo;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GreatCrab;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Guard;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Monk;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Necromancer;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Piranha;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Rat;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.RipperDemon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.RotHeart;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.RotLasher;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Scorpio;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Senior;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Shaman;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Skeleton;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Slime;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Snake;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.SpectralNecromancer;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Statue;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Succubus;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Swarm;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Thief;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Warlock;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Wraith;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.YogFist;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.RatKing;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Sheep;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Shopkeeper;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Wandmaker;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.inv.EditorItemBag;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.inv.MobItem;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BatSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GnollSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MonkSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SkeletonSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SuccubusSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WandmakerSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WraithSprite;
import com.watabou.noosa.Image;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public enum Mobs {


    SEWER,
    PRISON,
    CAVES,
    CITY,
    HALLS,
    SPECIAL,
    NPC;


    private static final Class<?>[] EMPTY_MOB_CLASS_ARRAY = new Class[0];

    public static Class<?>[][] getAllMobs(Set<Class<? extends Mob>> mobsToIgnore) {
        Mobs[] all = values();
        Class<?>[][] ret = new Class[all.length][];
        for (int i = 0; i < all.length; i++) {
            List<Class<?>> mobs = new ArrayList<>(Arrays.asList(all[i].classes()));
            if (mobsToIgnore != null) mobs.removeAll(mobsToIgnore);
            ret[i] = mobs.toArray(EMPTY_MOB_CLASS_ARRAY);
        }
        return ret;
    }

    public static String[] getAllNames() {
        Mobs[] all = values();
        String[] ret = new String[all.length];
        for (int i = 0; i < all.length; i++) {
            ret[i] = all[i].getName();
        }
        return ret;
    }

    public String getName() {
        switch (this) {
            case NPC:
                return "Npcs";
            case SPECIAL:
                return "General";
            case SEWER:
                return Document.INTROS.pageTitle("Sewers");
            case PRISON:
                return Document.INTROS.pageTitle("Prison");
            case CAVES:
                return Document.INTROS.pageTitle("Caves");
            case CITY:
                return Document.INTROS.pageTitle("City");
            case HALLS:
                return Document.INTROS.pageTitle("Halls");
        }
        return null;
    }


    public int numCharacter() {
        return classes().length;
    }

    public static int numMobsTotal() {
        int numMobs = -NPC.numCharacter();
        Mobs[] all = values();
        for (Mobs m : all) {
            numMobs += m.numCharacter();
        }
        return numMobs;
    }

    private Class<?>[] classes;

    public Class<?>[] classes() {
        return classes;
    }

    static {

        NPC.classes = new Class[]{
                Wandmaker.class,
                Blacksmith.class,
                Imp.class,
                Shopkeeper.class,
                RatKing.class,
//                Ghost.class,
                Sheep.class
        };

        SPECIAL.classes = new Class[]{
                Statue.class,
                ArmoredStatue.class,
                Piranha.class,
                Wraith.class,
                Bee.class,
                Mimic.class,
                GoldenMimic.class,
                CrystalMimic.class
        };

        SEWER.classes = new Class[]{
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
        };

        PRISON.classes = new Class[]{
                Skeleton.class,
                Thief.class,
                Bandit.class,
                DM100.class,
                Necromancer.class,
                SpectralNecromancer.class,
                Guard.class,
                RotLasher.class,
                RotHeart.class
        };

        CAVES.classes = new Class[]{
                Brute.class,
                ArmoredBrute.class,
                Shaman.RedShaman.class,
                Shaman.BlueShaman.class,
                Shaman.PurpleShaman.class,
                Bat.class,
                Spinner.class,
                DM200.class,
                DM201.class,
        };

        CITY.classes = new Class[]{
                Ghoul.class,
                Warlock.class,
                Elemental.FireElemental.class,
                Elemental.FrostElemental.class,
                Elemental.ShockElemental.class,
                Elemental.ChaosElemental.class,
                Monk.class,
                Senior.class,
                Golem.class,
        };

        HALLS.classes = new Class[]{
                Succubus.class,
                Eye.class,
                Scorpio.class,
                Acidic.class,
                RipperDemon.class,
                DemonSpawner.class,
//                YogDzewa.class,
                YogFist.SoiledFist.class,
                YogFist.BurningFist.class,
                YogFist.RustedFist.class,
                YogFist.RottingFist.class,
                YogFist.DarkFist.class,
                YogFist.BrightFist.class
        };


    }

    public MobBag createBag() {
        return new MobBag(classes) {
            {
                image = ItemSpriteSheet.BACKPACK;
            }

            @Override
            public String name() {
                return getName();
            }

            @Override
            public Image getCategoryImage() {
                switch (Mobs.this) {
                    case NPC:
                        return new WandmakerSprite();
                    case SPECIAL:
                        return new WraithSprite();
                    case SEWER:
                        return new GnollSprite();
                    case PRISON:
                        return new SkeletonSprite();
                    case CAVES:
                        return new BatSprite();
                    case CITY:
                        return new MonkSprite();
                    case HALLS:
                        return new SuccubusSprite();
                }
                return new ItemSprite(ItemSpriteSheet.SOMETHING);
            }
        };
    }

    public static final ArrayList<Bag> bags = new ArrayList<>();

    static {
        for (Mobs mobs : Mobs.values()) {
            bags.add(mobs.createBag());
        }
    }

    public static abstract class MobBag extends EditorItemBag {

        public MobBag(Class<?>[] mobs) {
            for (Class<?> m : mobs) {
                items.add(new MobItem((Mob) Reflection.newInstance(m)));
            }
        }
    }


}