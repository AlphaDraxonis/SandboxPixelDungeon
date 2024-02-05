package com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.MobSpriteItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.noosa.Image;

public enum MobSprites {

    SEWER,
    PRISON,
    CAVES,
    CITY,
    HALLS,
    SPECIAL,
    NPC;

    public String getName() {
        return Mobs.values()[ordinal()].getName();
    }

    public Image getImage() {
        return Mobs.values()[ordinal()].getImage();
    }

    public Class<?>[] classes() {
        return Mobs.values()[ordinal()].classes();
    }


    public static class MobSpriteBag extends EditorItemBag {
        private final MobSprites sprites;

        public MobSpriteBag(MobSprites sprites) {
            super(null, 0);
            this.sprites = sprites;
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

        @Override
        public Image getCategoryImage() {
            return sprites.getImage();
        }

        @Override
        public String name() {
            return sprites.getName();
        }
    }

    public static final EditorItemBag bag = new EditorItemBag("name", 0){
        @Override
        public Item findItem(Object src) {
            if (src instanceof Char) {
                for (Item bag : items) {
                    for (Item i : ((Bag) bag).items) {
                        if (((MobSpriteItem) i).mob() == src) return i;
                    }
                }
            } else {
                for (Item bag : items) {
                    for (Item i : ((Bag) bag).items) {
                        if (((MobSpriteItem) i).getObject() == src) return i;
                    }
                }
            }
            return null;
        }
    };

    static {
        for (MobSprites m : values()) {
            bag.items.add(new MobSpriteBag(m));
        }
    }

}