package com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.*;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditCustomObjectComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.MobItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.CustomObject;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.LuaMob;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.QuestNPC;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SentryRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.*;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.watabou.noosa.Image;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Locale;

public enum Mobs implements EditorInvCategory<Mob> {


    //Any changes in ordinal should also be made in MobSprites!!!
    SEWER,
    PRISON,
    CAVES,
    CITY,
    HALLS,
    SPECIAL,
    NPC;


    @Override
    public String getName() {
        switch (this) {
            case SEWER:  return Document.INTROS.pageTitle("Sewers");
            case PRISON: return Document.INTROS.pageTitle("Prison");
            case CAVES:  return Document.INTROS.pageTitle("Caves");
            case CITY:   return Document.INTROS.pageTitle("City");
            case HALLS:  return Document.INTROS.pageTitle("Halls");
            default:     return Messages.get(this, name().toLowerCase(Locale.ENGLISH));
        }
    }

    @Override
    public Image getSprite() {
        switch (this) {
            case SEWER:   return new GnollSprite();
            case PRISON:  return new SkeletonSprite();
            case CAVES:   return new BatSprite();
            case CITY:    return new MonkSprite();
            case HALLS:   return new SuccubusSprite();
            case NPC:     return new WandmakerSprite();
            case SPECIAL: return new WraithSprite();
            default:      return new ItemSprite(ItemSpriteSheet.SOMETHING);
        }
    }

    private Class<?>[] classes;

    @Override
    public Class<?>[] classes() {
        return classes;
    }

    static {

        NPC.classes = new Class[] {
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

        SPECIAL.classes = new Class[] {
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
                SentryRoom.Sentry.class,
                HeroMob.class
        };

        SEWER.classes = new Class[] {
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

        PRISON.classes = new Class[] {
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
        };

        CAVES.classes = new Class[] {
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
        };

        CITY.classes = new Class[] {
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
        };

        HALLS.classes = new Class[] {
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
        };

    }

    public static Mob initMob(Class<? extends Mob> mobClass) {
        Mob mob = Reflection.newInstance(mobClass);
        if (mob instanceof WandOfRegrowth.Lotus) {
            ((WandOfRegrowth.Lotus) mob).setLevel(7);
        }
        if (mob instanceof QuestNPC) {
            ((QuestNPC<?>) mob).createNewQuest();
        }
        if (mob instanceof HeroMob) ((HeroMob) mob).setInternalHero(new HeroMob.InternalHero());
        if (mob == null) throw new RuntimeException(mobClass.getName());
        mob.pos = -1;
        return mob;
    }

    public static final EditorItemBag bag = new EditorItemBag("name", 0){};

    static {
        for (Mobs m : values()) {
            bag.items.add(new MobBag(m));
        }
        bag.items.add(customMobsBag = new CustomMobsBag());
    }

    public static class MobBag extends EditorInvCategoryBag {
        public MobBag(Mobs mobs) {
            super(mobs);
            for (Class<?> m : mobs.classes) {
                items.add(new MobItem(initMob((Class<? extends Mob>) m)));
            }
        }
    }

    public static CustomMobsBag customMobsBag;

    public static class CustomMobsBag extends EditorItemBag {
        public CustomMobsBag() {
            super("nametzz", -1);
        }

        @Override
        public Image getCategoryImage() {
            return Icons.TALENT.get();
        }
    }

    public static void updateCustomMobsInInv() {
        //Need to keep the same reference for the Undo list
        if (Undo.canUndo() || Undo.canRedo()) {
            ArrayList<Item> newItems = new ArrayList<>();
            oneMob:
            for (Mob customMob : CustomObject.getAllCustomObjects(Mob.class)) {
                int ident = ((LuaMob) customMob).getIdentifier();
                for (Item i : customMobsBag.items) {
                    MobItem mobItem = (MobItem) i;
                    if (((LuaMob) mobItem.getObject()).getIdentifier() == ident) {
                        newItems.add(mobItem);
                        continue oneMob;
                    }
                }
                newItems.add(new MobItem(customMob));
            }
            customMobsBag.items = newItems;
        } else {
            customMobsBag.clear();
            for (Mob customMob : CustomObject.getAllCustomObjects(Mob.class)) {
                customMobsBag.items.add(new MobItem(customMob));
            }
        }
    }


    public static class AddCustomMobButton extends ScrollingListPane.ListButton {
        protected RedButton createButton() {
            return new RedButton(Messages.get(Tiles.WndCreateCustomTile.class, "titletzz")) {
                @Override
                protected void onClick() {
                    EditorScene.show(new EditCustomObjectComp.WndNewCustomObject());
                }
            };
        }
    }

}