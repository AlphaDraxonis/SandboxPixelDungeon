package com.shatteredpixel.shatteredpixeldungeon.editor.inv.items;

import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.DefaultListItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.EditorInventoryWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.IconTitleWithSubIcon;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.watabou.noosa.Image;

public abstract class EditorItem<T> extends Item {

    public static final String AC_PLACE = "PLACE";

    protected T obj;


    {
        defaultAction = AC_PLACE;
    }

    public void randomizeTexture() {
    }

    public abstract Image getSprite();

    public Image getSubIcon() {
        return null;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    public abstract ScrollingListPane.ListItem createListItem(EditorInventoryWindow window);

    public abstract DefaultEditComp<?> createEditComponent();

    public abstract void place(int cell);

    public T getObject() {
        return obj;
    }

    public void setObject(T obj) {
        this.obj = obj;
    }


    //Constant items

    public static final EditorItem NULL_ITEM = new NullItemClass(), RANDOM_ITEM = new NullItemClass() {
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
        public Image getSprite() {
            return new ItemSprite(ItemSpriteSheet.SOMETHING);
        }

        @Override
        public String title() {
            return Messages.titleCase(name());
        }

        @Override
        public ScrollingListPane.ListItem createListItem(EditorInventoryWindow window) {
            return new DefaultListItem(this, window, title(), getSprite());
        }

        @Override
        public DefaultEditComp<?> createEditComponent() {
            return new DefaultEditComp<Item>(this) {

                @Override
                protected IconTitleWithSubIcon createTitle() {
                    return new IconTitleWithSubIcon(getIcon(), null, Messages.titleCase(obj.title()));
                }

                @Override
                protected String createDescription() {
                    return obj.desc();
                }

                @Override
                public Image getIcon() {
                    return Icons.get(Icons.CLOSE);
                }
            };
        }

        @Override
        public void place(int cell) {
            //Cant place this
        }

        @Override
        public Object getObject() {
            return this;
        }

        @Override
        public String name() {
            return Messages.get(EditorItem.class, "nothing_name");
        }

        @Override
        public String desc() {
            return Messages.get(EditorItem.class, "nothing_desc");
        }
    }

    public final static EditorItem REMOVER_ITEM = new EditorItem() {//WARNING! DO NOT CHANGE THE POSITION (NUMBER) OF THIS INNER CLASS!!

        @Override
        public ScrollingListPane.ListItem createListItem(EditorInventoryWindow window) {
            return new DefaultListItem(this, window, title(), getSprite());
        }

        @Override
        public DefaultEditComp<?> createEditComponent() {
            return new DefaultEditComp<Item>(this) {

                @Override
                protected IconTitleWithSubIcon createTitle() {
                    return new IconTitleWithSubIcon(getIcon(), null, Messages.titleCase(obj.title()));
                }

                @Override
                protected String createDescription() {
                    return obj.desc();
                }

                @Override
                public Image getIcon() {
                    return getSprite();
                }
            };
        }

        @Override
        public void place(int cell) {
            CustomLevel level = EditorScene.customLevel();
            ActionPart part = MobItem.remove(level.findMob(cell));
            //would be better if the if-statements were nested...
            if (part == null) part = BlobItem.remove(cell);
            if (part == null) part = ItemItem.remove(cell, level);
            if (part == null) part = PlantItem.remove(cell, level);
            if (part == null) part = TrapItem.remove(level.traps.get(cell));
            if (part == null) part = BarrierItem.remove(level.barriers.get(cell));
            if (part == null) part = CustomTileItem.remove(cell);
            if (part == null)
                part = TileItem.place(cell, level.feeling == Level.Feeling.CHASM ? Terrain.CHASM : Terrain.EMPTY);
            Undo.addActionPart(part);
        }

        @Override
        public Object getObject() {
            return this;
        }

        @Override
        public Image getSprite() {
            return Icons.get(Icons.CLOSE);
        }

        @Override
        public String name() {
            return Messages.get(EditorItem.class, "remover_name");
        }

        @Override
        public String desc() {
            return Messages.get(EditorItem.class, "remover_desc");
        }
    };

}