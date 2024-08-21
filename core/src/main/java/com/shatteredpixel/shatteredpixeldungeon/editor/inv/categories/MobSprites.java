package com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.MobSpriteItem;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.shatteredpixel.shatteredpixeldungeon.usercontent.blueprints.CustomCharSprite;
import com.shatteredpixel.shatteredpixeldungeon.usercontent.ui.WndNewCustomObject;
import com.watabou.noosa.Image;

public final class MobSprites extends GameObjectCategory<MobSprite> {

    private static MobSprites instance = new MobSprites();

    private final MobSpriteCategory SEWER   = new MobSpriteCategory(Mobs.instance().SEWER);
    private final MobSpriteCategory PRISON  = new MobSpriteCategory(Mobs.instance().PRISON);
    private final MobSpriteCategory CAVES   = new MobSpriteCategory(Mobs.instance().CAVES);
    private final MobSpriteCategory CITY    = new MobSpriteCategory(Mobs.instance().CITY);
    private final MobSpriteCategory HALLS   = new MobSpriteCategory(Mobs.instance().HALLS);
    private final MobSpriteCategory SPECIAL = new MobSpriteCategory(Mobs.instance().SPECIAL);
    private final MobSpriteCategory NPC     = new MobSpriteCategory(Mobs.instance().NPC);

    {
        values = new MobSpriteCategory[] {
                SEWER,
                PRISON,
                CAVES,
                CITY,
                HALLS,
                SPECIAL,
                NPC
        };
    }

    private MobSprites() {
        super(new EditorItemBag(){});
        addItemsToBag();
    }

    public static MobSprites instance() {
        return instance;
    }

    public static EditorItemBag bag() {
        return instance().getBag();
    }

    @Override
    public void updateCustomObjects() {
    }

    @Override
    public ScrollingListPane.ListButton createAddBtn() {
        return new ScrollingListPane.ListButton() {
            protected RedButton createButton() {
                return new RedButton(Messages.get(MobSprites.class, "add_custom_obj")) {
                    @Override
                    protected void onClick() {
                        DungeonScene.show(new WndNewCustomObject(CustomCharSprite.class));
                    }
                };
            }
        };
    }

    private static final class MobSpriteCategory extends GameObjectCategory.SubCategory<MobSprite> {

        private final Mobs.MobCategory mobCategory;

        private MobSpriteCategory(Mobs.MobCategory mobCategory) {
            super(mobCategory.getClasses());
			this.mobCategory = mobCategory;
		}

        @Override
        public String getName() {
            return mobCategory.getName();
        }

        @Override
        public Image getSprite() {
            return mobCategory.getSprite();
        }

        @Override
        public String messageKey() {
            return mobCategory.messageKey();
        }
    }

    @Override
    GameObjectCategory.Bag<MobSprite> createBag(SubCategory<MobSprite> category) {
        return new Bag(category);
    }

    private static final class Bag extends GameObjectCategory.Bag<MobSprite> {

        public Bag(SubCategory<MobSprite> category) {
            super(category);
        }

        @Override
        public void updateBag() {
            items.clear();

            for (Class<?> m : category.getClasses()) {
                Mob mob = GameObjectCategory.Bag.initMob((Class<? extends Mob>) m);
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

}