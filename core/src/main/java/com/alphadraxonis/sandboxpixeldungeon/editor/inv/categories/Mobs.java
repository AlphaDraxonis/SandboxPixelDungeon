package com.alphadraxonis.sandboxpixeldungeon.editor.inv.categories;

import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Acidic;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Albino;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.ArmoredBrute;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.ArmoredStatue;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Bandit;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Bat;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Bee;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Brute;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.CausticSlime;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Crab;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.CrystalMimic;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.DM100;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.DM200;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.DM201;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.DemonSpawner;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Elemental;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Eye;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.FetidRat;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Ghoul;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Gnoll;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.GnollTrickster;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.GoldenMimic;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Golem;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Goo;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.GreatCrab;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Guard;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Mimic;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Mob;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Monk;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Necromancer;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.PhantomPiranha;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Piranha;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Rat;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.RipperDemon;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.RotHeart;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.RotLasher;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Scorpio;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Senior;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Shaman;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Skeleton;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Slime;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Snake;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.SpectralNecromancer;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Spinner;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Statue;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Succubus;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Swarm;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Thief;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.TormentedSpirit;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Warlock;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Wraith;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.YogFist;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.npcs.Ghost;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.npcs.Imp;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.npcs.ImpShopkeeper;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.npcs.RatKing;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.npcs.Sheep;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.npcs.Shopkeeper;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.npcs.Wandmaker;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.items.MobItem;
import com.alphadraxonis.sandboxpixeldungeon.items.wands.WandOfRegrowth;
import com.alphadraxonis.sandboxpixeldungeon.journal.Document;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.sprites.BatSprite;
import com.alphadraxonis.sandboxpixeldungeon.sprites.GnollSprite;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSprite;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSpriteSheet;
import com.alphadraxonis.sandboxpixeldungeon.sprites.MonkSprite;
import com.alphadraxonis.sandboxpixeldungeon.sprites.SkeletonSprite;
import com.alphadraxonis.sandboxpixeldungeon.sprites.SuccubusSprite;
import com.alphadraxonis.sandboxpixeldungeon.sprites.WandmakerSprite;
import com.alphadraxonis.sandboxpixeldungeon.sprites.WraithSprite;
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
                return Messages.get(Mobs.class, "npc");
            case SPECIAL:
                return Messages.get(Mobs.class, "general");
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

    public Image getImage() {
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
                Ghost.class,
                Wandmaker.class,
                Blacksmith.class,
                Imp.class,
                Shopkeeper.class,
                ImpShopkeeper.class,
                RatKing.class,
                Sheep.class,
                WandOfRegrowth.Lotus.class
        };

        SPECIAL.classes = new Class[]{
                Statue.class,
                ArmoredStatue.class,
                Piranha.class,
                PhantomPiranha.class,
                Wraith.class,
                TormentedSpirit.class,
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

    public static class MobBag extends EditorItemBag {
        private final Mobs mobs;

        public MobBag(Mobs mobs) {
            super(mobs.getName(), 0);
            this.mobs = mobs;
            for (Class<?> m : mobs.classes) {
                Mob mob = (Mob) Reflection.newInstance(m);
                if (mob instanceof WandOfRegrowth.Lotus) {
                    ((WandOfRegrowth.Lotus) mob).setLevel(7);
                }
                mob.pos = -1;
                items.add(new MobItem(mob));
            }
        }

        @Override
        public Image getCategoryImage() {
            return mobs.getImage();
        }
    }

    public static final EditorItemBag bag = new EditorItemBag(Messages.get(EditorItemBag.class, "mobs"), 0);

    static {
        for (Mobs m : values()) {
            bag.items.add(new MobBag(m));
        }
    }

}