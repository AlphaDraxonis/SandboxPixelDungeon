package com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.BuffItem;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.noosa.Image;
import com.watabou.utils.Reflection;

public enum Buffs implements EditorInvCategory<Buff> {

    CHAMPIONS,
    MOVEMENT,
    FIGHT,
    SIGHT,
    OTHER;

    private Class<?>[] classes;

    @Override
    public Class<?>[] classes() {
        return classes;
    }

    static {

        CHAMPIONS.classes = ChampionEnemy.CLASSES;

        MOVEMENT.classes = new Class[]{
                Haste.class,
                Stamina.class,
                Adrenaline.class,
                Slow.class,
                Cripple.class,
                Frost.class,
                Paralysis.class,
                Roots.class,
                Vertigo.class,//Confusion
                Amok.class,
                Terror.class,
                Dread.class,
        };

        FIGHT.classes = new Class[]{
//                Fury.class,
                Bless.class,
                AnkhInvulnerability.class,
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
                Burning.class,
        };

        SIGHT.classes = new Class[]{
                Invisibility.class,
                Blindness.class,
                Light.class,
                MindVision.class,
                MagicalSight.class,
                Foresight.class,
        };

        OTHER.classes = new Class[]{
                FireImbue.class,
                FrostImbue.class,
                ToxicImbue.class,

                Levitation.class,
                BlobImmunity.class,
                MagicImmune.class,
                Drowsy.class,
                MagicalSleep.class,
                Recharging.class,
                ArtifactRecharge.class,
//                EnhancedRings.class,
        };
    }

    @Override
    public Image getSprite() {
        return new ItemSprite();//TODO add icons
    }

    public static final EditorItemBag bag = new EditorItemBag("name", 0) {};

    static {
        for (Buffs buffs : values()) {
            bag.items.add(new BuffBag(buffs));
        }
    }

    public static class BuffBag extends EditorInvCategoryBag {
        public BuffBag(Buffs buffs) {
            super(buffs);
            for (Class<?> b : buffs.classes) {
                Buff buff = (Buff) Reflection.newInstance(b);
                buff.permanent = !(buff instanceof ChampionEnemy); //for description
                items.add(new BuffItem(buff));
            }
        }
    }

}