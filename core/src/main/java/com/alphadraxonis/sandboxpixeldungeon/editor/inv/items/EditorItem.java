package com.alphadraxonis.sandboxpixeldungeon.editor.inv.items;

import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.DefaultEditComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.DefaultListItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.EditorInventoryWindow;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomLevel;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.ActionPart;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.Undo;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.IconTitleWithSubIcon;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.levels.Level;
import com.alphadraxonis.sandboxpixeldungeon.levels.Terrain;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSprite;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSpriteSheet;
import com.alphadraxonis.sandboxpixeldungeon.ui.Icons;
import com.alphadraxonis.sandboxpixeldungeon.ui.ScrollingListPane;
import com.watabou.noosa.Image;

public abstract class EditorItem extends Item {

    public static final String AC_PLACE = "PLACE";


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

    public abstract Object getObject();


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

    public static class NullItemClass extends EditorItem {
        private NullItemClass() {
        }

        @Override
        public Image getSprite() {
            return new ItemSprite(ItemSpriteSheet.SOMETHING);
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
            ActionPart part = MobItem.remove(level.getMobAtCell(cell));
            //would be better if the if-statements were nested...
            if (part == null) part = BlobItem.remove(cell);
            if (part == null) part = ItemItem.remove(cell, level);
            if (part == null) part = PlantItem.remove(cell, level);
            if (part == null) part = TrapItem.remove(level.traps.get(cell));
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