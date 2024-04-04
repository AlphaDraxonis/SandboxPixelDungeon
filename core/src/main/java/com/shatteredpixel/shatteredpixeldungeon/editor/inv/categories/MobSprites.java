package com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.MobSpriteItem;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.Image;

public enum MobSprites implements EditorInvCategory<MobSprite> {

    //Any changes in ordinal should also be made in Mobs!!!
    SEWER,
    PRISON,
    CAVES,
    CITY,
    HALLS,
    SPECIAL,
    NPC;

    @Override
    public String getName() {
        return Mobs.values()[ordinal()].getName();
    }

    @Override
    public Image getSprite() {
        return Mobs.values()[ordinal()].getSprite();
    }

    @Override
    public Class<?>[] classes() {
        return Mobs.values()[ordinal()].classes();
    }


    public static class MobSpriteBag extends EditorInvCategoryBag {

        public MobSpriteBag(MobSprites sprites) {
            super(sprites);
            for (Class<?> m : sprites.classes()) {
                Mob mob = Mobs.initMob((Class<? extends Mob>) m);
                if (MobSpriteItem.canSpriteBeUsedForOthers(mob)) {
                    Class<? extends CharSprite> sprite = mob.spriteClass;
                    Class<?> enclosingClass = sprite.getEnclosingClass();
                    if (enclosingClass != null && mob.getClass().getEnclosingClass() == null) {
                        for (Class<?> c : enclosingClass.getDeclaredClasses()) {
                            if (CharSprite.class.isAssignableFrom(c)) {
                                mob = (Mob) mob.getCopy();
                                mob.spriteClass = (Class<? extends CharSprite>) c;
                                items.add(new MobSpriteItem(mob));
                            }
                        }
                    }
                    else items.add(new MobSpriteItem(mob));
                }

            }
        }
    }

    public static final EditorItemBag bag = new EditorItemBag("name", 0){};

    static {
        for (MobSprites m : values()) {
            bag.items.add(new MobSpriteBag(m));
        }
    }

}