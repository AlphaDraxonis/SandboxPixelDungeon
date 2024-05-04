package com.shatteredpixel.shatteredpixeldungeon.editor.inv.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.ArrowCell;
import com.shatteredpixel.shatteredpixeldungeon.editor.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditRemoverComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.DefaultListItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.EditorInventoryWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.*;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.IconTitleWithSubIcon;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SkeletonSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.watabou.noosa.Image;
import com.watabou.utils.Reflection;

public abstract class EditorItem<T> extends Item {

    protected T obj;

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    public Image getSubIcon() {
        return null;
    }

    public ScrollingListPane.ListItem createListItem(EditorInventoryWindow window) {
        return new DefaultListItem(this, window, Messages.titleCase(title()), getSprite());
    }

    public abstract DefaultEditComp<?> createEditComponent();

    public abstract Image getSprite();

    @Override
    public abstract Item getCopy();

    public abstract void place(int cell);

    public T getObject() {
        return obj;
    }

    public void setObject(T obj) {
        this.obj = obj;
    }


    //Constant items

    public static final NullItemClass NULL_ITEM = new NullItemClass(), RANDOM_ITEM = new NullItemClass() {
        @Override
        public String name() {
            return Messages.get(EditorItem.class, "random_name");
        }

        @Override
        public String desc() {
            return Messages.get(EditorItem.class, "random_desc");
        }
    };

    public static class NullItemClass extends EditorItem<Object> {
        private NullItemClass() {
        }

        @Override
        public String name() {
            return Messages.get(EditorItem.class, "nothing_name");
        }

        @Override
        public String desc() {
            return Messages.get(EditorItem.class, "nothing_desc");
        }

        @Override
        public Image getSprite() {
            return new ItemSprite(ItemSpriteSheet.SOMETHING);
        }

        @Override
        public DefaultEditComp<?> createEditComponent() {
            return new DefaultEditComp<Item>(this) {
                @Override
                protected IconTitleWithSubIcon createTitle() {
                    return new IconTitleWithSubIcon(getIcon(), getSubIcon(), createTitleText());
                }

                @Override
                protected String createDescription() {
                    return obj.desc();
                }

                @Override
                protected String createTitleText() {
                    return Messages.titleCase(obj.name());
                }

                @Override
                public Image getIcon() {
                    return Icons.get(Icons.CLOSE);
                }
            };
        }

        @Override
        public Item getCopy() {
            return this;
        }

        @Override
        public void place(int cell) {
            //Can't place this
        }

        @Override
        public Object getObject() {
            return this;
        }

    }

    public final static NullItemClass REMOVER_ITEM = new NullItemClass() {//WARNING! DO NOT CHANGE THE POSITION (NUMBER) OF THIS INNER CLASS!!

        @Override
        public String name() {
            return Messages.get(EditorItem.class, "remover_name");
        }

        @Override
        public String desc() {
            return Messages.get(EditorItem.class, "remover_desc");
        }

        @Override
        public Image getSprite() {
            return Icons.get(Icons.CLOSE);
        }

        @Override
        public DefaultEditComp<?> createEditComponent() {
            return new EditRemoverComp();
        }

        @Override
        public void place(int cell) {
            int i = 0;
            ActionPart part;
            do {
                part = REMOVER_PRIORITY[i++].doRemove(cell);
            } while (part == null && i < REMOVER_PRIORITY.length);
            if (part == null)
                part = TileItem.place(cell, Dungeon.level.feeling == Level.Feeling.CHASM ? Terrain.CHASM : Terrain.EMPTY);
            Undo.addActionPart(part);
        }

        @Override
        public Object getObject() {
            return this;
        }
    };

    public static final RemoverPriority[] REMOVER_PRIORITY = RemoverPriority.values().clone();

    public enum RemoverPriority {
        MOB,
        BLOB,
        ITEM,
        PLANT,
        TRAP,
        ARROW_CELL,
        BARRIER,
        CUSTOM_TILE,
        PARTICLE;

        public ActionPart doRemove(int cell) {
            Level level = Dungeon.level;
            switch (this) {
                case MOB: return MobItem.remove(level.findMob(cell));
                case BLOB: return BlobItem.remove(cell);
                case ITEM: return ItemItem.remove(cell);
                case PLANT: return PlantItem.remove(cell);
                case TRAP: return TrapItem.remove(level.traps.get(cell));
                case ARROW_CELL: return ArrowCellItem.remove(cell);
                case BARRIER: return BarrierItem.remove(cell);
                case CUSTOM_TILE: return CustomTileItem.remove(cell);
                case PARTICLE: return ParticleItem.remove(cell);
            }
            return null;
        }

        public String displayText() {
            switch (this) {
                case MOB: return Mobs.bag.name();
                case BLOB: return Messages.get(Tiles.BlobBag.class, "name");
                case ITEM: return Items.bag.name();
                case PLANT: return Plants.bag.name();
                case TRAP: return Traps.bag.name();
                case ARROW_CELL: return Messages.get(ArrowCell.class, "name");
                case BARRIER: return Messages.get(Barrier.class, "name");
                case CUSTOM_TILE: return Messages.get(Tiles.CustomTileBag.class, "name");
                case PARTICLE: return Tiles.particleBag.name();
            }
            return null;
        }

        public Image getSprite() {
            switch (this) {
                case MOB: return new SkeletonSprite();
                case BLOB:
                    Image icon = Icons.ETERNAL_FIRE.get();
                    icon.scale.set(2.28f);// 16/7 = 2.28
                    return icon;
                case ITEM: return new ItemSprite((Item) Reflection.newInstance(EditorInvCategory.getRandom(Items.values())));
                case PLANT: return ((Plant) Reflection.newInstance(EditorInvCategory.getRandom(Plants.values()))).getSprite();
                case TRAP:
                    Trap t = Reflection.newInstance(EditorInvCategory.getRandom(Traps.values()));
                    t.visible = true;
                    return t.getSprite();
                case ARROW_CELL: return EditorUtilies.getBarrierTexture(1);//tzz different sprite
                case BARRIER: return EditorUtilies.getBarrierTexture(1);
                case CUSTOM_TILE: return Icons.TALENT.get();
                case PARTICLE: return Tiles.particleBag.getCategoryImage();
            }
            return null;
        }
    }

}