package com.shatteredpixel.shatteredpixeldungeon.editor.inv.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GameObject;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObject;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObjectManager;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.interfaces.CustomObjectClass;
import com.shatteredpixel.shatteredpixeldungeon.editor.ArrowCell;
import com.shatteredpixel.shatteredpixeldungeon.editor.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.editor.Checkpoint;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditRemoverComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.DefaultListItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.EditorInventoryWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.GameObjectCategory;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Items;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Mobs;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Plants;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Tiles;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Traps;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.CustomParticle;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.CompactCategoryScroller;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.IconTitleWithSubIcon;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.items.EnchantmentLike;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SkeletonSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.watabou.noosa.Image;
import com.watabou.utils.Function;
import com.watabou.utils.Reflection;

public abstract class EditorItem<T> extends Item implements CompactCategoryScroller.CategoryAction {

    public static <T> EditorItem<T> wrapObject(T object) {
        if (object instanceof Item) return (EditorItem<T>) new ItemItem(((Item) object));
        if (object instanceof Mob) return (EditorItem<T>) new MobItem(((Mob) object));
        if (object instanceof Trap) return (EditorItem<T>) new TrapItem(((Trap) object));
        if (object instanceof Plant) return (EditorItem<T>) new PlantItem(((Plant) object));
        if (object instanceof Integer) return (EditorItem<T>) new TileItem((Integer) object, -1);
        if (object instanceof CustomObject) return (EditorItem<T>) new CustomObjectItem(((CustomObject) object));
        if (object instanceof Class<?> && Blob.class.isAssignableFrom((Class<?>) object)) return (EditorItem<T>) new BlobItem((Class<? extends Blob>) object);
        if (object instanceof Buff) return (EditorItem<T>) new BuffItem(((Buff) object));
        if (object instanceof Barrier) return (EditorItem<T>) new BarrierItem(((Barrier) object));
        if (object instanceof ArrowCell) return (EditorItem<T>) new ArrowCellItem(((ArrowCell) object));
        if (object instanceof Checkpoint) return (EditorItem<T>) new CheckpointItem(((Checkpoint) object));
        if (object instanceof CustomTilemap) return (EditorItem<T>) new CustomTileItem(((CustomTilemap) object), -1);
        if (object instanceof EnchantmentLike) return (EditorItem<T>) new EnchantmentItem((EnchantmentLike) object);
        if (object instanceof Class<?> && MobSprite.class.isAssignableFrom((Class<?>) object)) return (EditorItem<T>) new MobSpriteItem((Class<? extends MobSprite>) object);
        if (object instanceof CustomParticle.ParticleProperty) return (EditorItem<T>) new ParticleItem(((CustomParticle.ParticleProperty) object));
        if (object instanceof Char.Property) return (EditorItem<T>) new PropertyItem(((Char.Property) object));
        if (object instanceof Room) return (EditorItem<T>) new RoomItem(((Room) object));
        return null;
//      throw new IllegalArgumentException("EditorItemClass is missing for object" + (object == null ? null : object.getClass()));
    }

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

    public Image getSprite(Runnable reloadSprite) {
        return getSprite();
    }

    @Override
    public abstract Item getCopy();

    public abstract void place(int cell);

    public T getObject() {
        return obj;
    }

    public void setObject(T obj) {
        this.obj = obj;
    }

    @Override
    public boolean doOnAllGameObjects(Function<GameObject, ModifyResult> whatToDo) {
        if (obj instanceof GameObject) {
            return super.doOnAllGameObjects(whatToDo)
                    | doOnSingleObject(((GameObject) obj), whatToDo, newValue -> obj = (T) newValue);
        }
        return super.doOnAllGameObjects(whatToDo);
    }


    @Override
    public boolean supportsAction(Action action) {
        return obj instanceof CustomObjectClass && action == Action.REMOVE && CustomDungeon.isEditing();
    }

    @Override
    public void doAction(Action action) {
        if (action == Action.REMOVE) {
            if (obj instanceof CustomObjectClass) {
                CustomDungeonSaves.deleteCustomObject(
                        CustomObjectManager.getUserContent(((CustomObjectClass) obj).getIdentifier(), null)
                );
            }
        }
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
        CHECKPOINT,
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
                case CHECKPOINT: return CheckpointItem.remove(cell);
                case ARROW_CELL: return ArrowCellItem.remove(cell);
                case BARRIER: return BarrierItem.remove(cell);
                case CUSTOM_TILE: return CustomTileItem.remove(cell);
                case PARTICLE: return ParticleItem.remove(cell);
            }
            return null;
        }

        public String displayText() {
            switch (this) {
                case MOB: return Mobs.bag().name();
                case BLOB: return Messages.get(Tiles.BlobBag.class, "name");
                case ITEM: return Items.bag().name();
                case PLANT: return Plants.bag().name();
                case TRAP: return Traps.bag().name();
                case CHECKPOINT: return Messages.get(Checkpoint.class, "name");
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
                case ITEM: return new ItemSprite((Item) Reflection.newInstance(GameObjectCategory.getRandom(Items.instance().values())));
                case PLANT: return ((Plant) Reflection.newInstance(GameObjectCategory.getRandom(Plants.instance().values()))).getSprite();
                case TRAP:
                    Trap t = Reflection.newInstance(GameObjectCategory.getRandom(Traps.instance().values()));
                    t.visible = true;
                    return t.getSprite();
                case CHECKPOINT: return new Checkpoint.CheckpointSprite(new Checkpoint());
                case ARROW_CELL: return EditorUtilities.getArrowCellTexture(ArrowCell.ALL, true);
                case BARRIER: return EditorUtilities.getBarrierTexture(1);
                case CUSTOM_TILE: return Icons.TALENT.get();
                case PARTICLE: return Tiles.particleBag.getCategoryImage();
            }
            return null;
        }
    }

}