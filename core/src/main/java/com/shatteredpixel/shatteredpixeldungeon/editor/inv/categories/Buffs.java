package com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.blueprints.CustomBuff;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.ui.WndNewCustomObject;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfFeatherFall;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfCleansing;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAggression;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.watabou.noosa.Image;

import java.util.Locale;

public final class Buffs extends GameObjectCategory<Buff> {

    private static Buffs instance = new Buffs();

    private final Champions CHAMPIONS = new Champions();
    private final Movement MOVEMENT = new Movement();
    private final Fight FIGHT = new Fight();
    private final Sight SIGHT = new Sight();
    private final Other OTHER = new Other();

    {
        values = new Buffs.BuffCategory[] {
                CHAMPIONS,
                MOVEMENT,
                FIGHT,
                SIGHT,
                OTHER
        };
    }

    private Buffs() {
        super(new EditorItemBag(){});
        addItemsToBag();
    }

    public static Buffs instance() {
        return instance;
    }

    public static EditorItemBag bag() {
        return instance().getBag();
    }

    @Override
    public void updateCustomObjects() {
        updateCustomObjects(Buff.class);
    }

    public static void updateCustomBuff(CustomBuff customBuff) {
        if (instance != null) {
            instance.updateCustomObject(customBuff);
        }
    }

    @Override
    public ScrollingListPane.ListButton createAddBtn() {
        return new ScrollingListPane.ListButton() {
            protected RedButton createButton() {
                return new RedButton(Messages.get(Buffs.class, "add_custom_obj")) {
                    @Override
                    protected void onClick() {
                        DungeonScene.show(new WndNewCustomObject(CustomBuff.class));
                    }
                };
            }
        };
    }

    private static abstract class BuffCategory extends GameObjectCategory.SubCategory<Buff> {

        private BuffCategory(Class<?>[] classes) {
            super(classes);
        }

        @Override
        public Image getSprite() {
            return new ItemSprite();//TODO add icons
        }

        @Override
        public String messageKey() {
            return getClass().getSimpleName().toLowerCase(Locale.ENGLISH);
        }
    }

    private static final class Champions extends BuffCategory {

        private Champions() {
            super(ChampionEnemy.CLASSES);
        }
    }

    private static final class Movement extends BuffCategory {

        private Movement() {
            super(new Class[] {
                    Haste.class,
                    Stamina.class,
                    Adrenaline.class,
                    Slow.class,
                    Cripple.class,
                    Frost.class,
                    Paralysis.class,
                    Roots.class,
                    Vertigo.class,//Confusion
                    StoneOfAggression.Aggression.class,
                    Amok.class,
                    Terror.class,
                    Dread.class
            });
        }
    }

    private static final class Fight extends BuffCategory {

        private Fight() {
            super(new Class[] {
//                  Fury.class,
                    Bless.class,
                    Invulnerability.class,
                    SoulMark.class,

                    Weakness.class,
                    Degrade.class,
                    Hex.class,
                    Daze.class,
                    Vulnerable.class,
                    Doom.class,
                    Corruption.class,
                    Poison.class,
                    Corrosion.class,
                    Ooze.class,
                    Bleeding.class,
                    Burning.class
            });
        }
    }

    private static final class Sight extends BuffCategory {

        private Sight() {
            super(new Class[] {
                    Invisibility.class,
                    Blindness.class,
                    Light.class,
                    MindVision.class,
                    MagicalSight.class,
                    Foresight.class
            });
        }
    }

    private static final class Other extends BuffCategory {

        private Other() {
            super(new Class[] {
                    FireImbue.class,
                    FrostImbue.class,
                    ToxicImbue.class,

                    Levitation.class,
                    ElixirOfFeatherFall.FeatherBuff.class,
                    BlobImmunity.class,
                    PotionOfCleansing.Cleanse.class,
                    MagicImmune.class,
                    Drowsy.class,
                    MagicalSleep.class,
                    Recharging.class,
                    ArtifactRecharge.class,
//                  EnhancedRings.class,
            });
        }
    }

}
