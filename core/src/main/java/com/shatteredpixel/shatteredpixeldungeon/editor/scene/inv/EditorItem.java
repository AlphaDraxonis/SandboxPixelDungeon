package com.shatteredpixel.shatteredpixeldungeon.editor.scene.inv;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.AdvancedListPaneItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.IconTitleWithSubIcon;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;

public abstract class EditorItem extends Item {

    public static final String AC_PLACE = "PLACE";


    {
        defaultAction = AC_PLACE;
    }

    public static final EditorItem NULL_ITEM = new EditorItem(){

        @Override
        public Image getSprite() {
            return new ItemSprite(ItemSpriteSheet.SOMETHING);
        }

        @Override
        public ScrollingListPane.ListItem createListItem(EditorInventoryWindow window) {
            return new DefaultListItem(NULL_ITEM, window, title(), getSprite());
        }

        @Override
        public DefaultEditComp<?> createEditComponent() {
            return new DefaultEditComp<Item>(NULL_ITEM) {

                @Override
                protected IconTitleWithSubIcon createTitle() {
                    return new IconTitleWithSubIcon(getIcon(), null, Messages.titleCase(item.title()));
                }

                @Override
                protected String createDescription() {
                    return item.desc();
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
        public String name() {
            return "Nothing";
        }

        @Override
        public String desc() {
            return "This item represents no item.";
        }
    };
    public final static EditorItem REMOVER_ITEM = new EditorItem() {

        @Override
        public ScrollingListPane.ListItem createListItem(EditorInventoryWindow window) {
            return new DefaultListItem(REMOVER_ITEM, window, title(), getSprite());
        }

        @Override
        public DefaultEditComp<?> createEditComponent() {
            return new DefaultEditComp<Item>(REMOVER_ITEM) {

                @Override
                protected IconTitleWithSubIcon createTitle() {
                    return new IconTitleWithSubIcon(getIcon(), null, Messages.titleCase(item.title()));
                }

                @Override
                protected String createDescription() {
                    return item.desc();
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
            Mob mob = level.getMobAtCell(cell);
            if (mob != null) MobItem.removeMob(mob);
            else {
                if (!ItemItem.removeItem(cell, level) && !TrapItem.removeTrap(cell, level))
                    level.setCell(cell, Terrain.EMPTY);
            }
        }

        @Override
        public Image getSprite() {
            return Icons.get(Icons.CLOSE);
        }

        @Override
        public String name() {
            return "remover";
        }

        @Override
        public String desc() {
            return "This item can remove objects from the world.";
        }
    };


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

    public static class DefaultListItem extends AdvancedListPaneItem {

        protected final Item item;
        private EditorInventoryWindow window;

        protected RedButton editButton;

        private static final float ICON_WIDTH, ICON_HEIGHT;

        private static final Image editIcon = Icons.get(Icons.PREFS);

        static {
            ICON_WIDTH = editIcon.width();
            ICON_HEIGHT = editIcon.height();
        }

        public DefaultListItem(Item item, EditorInventoryWindow window, String title, Image image) {
            super(image,
                    (item instanceof EditorItem) ? ((EditorItem) item).getSubIcon() : IconTitleWithSubIcon.createSubIcon(item),
                    Messages.titleCase(title));
            this.item = item;
            this.window = window;

//            editButton = new RedButton("");
//            add(editButton);

            onUpdate();
        }

        @Override
        protected void layout() {
            super.layout();

            if (editButton != null)
                editButton.setRect(width - 1 - ICON_WIDTH, y + (height - ICON_HEIGHT) * 0.5f, ICON_WIDTH, ICON_HEIGHT);
        }

        @Override
        public void onUpdate() {
            QuickSlotButton.refresh();
            super.onUpdate();
        }

        @Override
        protected int getLabelMaxWidth() {
            return (int) (width - ICON_WIDTH - 1 - 4 - ICON_WIDTH);
        }

        @Override
        protected void onClick() {
            Sample.INSTANCE.play(Assets.Sounds.CLICK);
            if (window.selector() != null) {
                window.hide();
               window.selector().onSelect(item);
            } else {
                window.hide();
                QuickSlotButton.set(item);
            }
        }

        @Override
        protected void onRightClick() {
            Sample.INSTANCE.play(Assets.Sounds.CLICK);
            if (window.selector() != null) {
                window.hide();
                window.selector().onSelect(item);
            } else {
                onClick();
            }
        }

        @Override
        protected boolean onLongClick() {

            if (item instanceof EditorItem) {
                Window w = new Window();
                DefaultEditComp<?> content = ((EditorItem) item).createEditComponent();
                content.advancedListPaneItem = this;

                float newWidth = WndTitledMessage.WIDTH_MIN;
                while (PixelScene.landscape()
                        && content.bottom() > (PixelScene.MIN_HEIGHT_L - 10)
                        && newWidth < WndTitledMessage.WIDTH_MAX) {
                    newWidth += 20;
                    content.setSize(newWidth, content.height());
                }

                content.setRect(0, 0, newWidth, -1);
                ScrollPane sp = new ScrollPane(content);
                w.add(sp);

                float nW = newWidth;

                Runnable r = () -> {
                    float ch = content.height();
                    int maxHeight = (int) (PixelScene.uiCamera.height * 0.9);
                    int weite = (int) Math.ceil(ch > maxHeight ? maxHeight : ch);
                    w.resize((int) Math.ceil(nW), weite);
                    sp.setSize((int) Math.ceil(nW), weite);
                    sp.scrollTo(sp.content().camera.scroll.x, sp.content().camera.scroll.y);
                };
                content.setOnUpdate(r);
                r.run();

                EditorScene.show(w);
                return true;
            }

            window.hide();
            QuickSlotButton.set(item);
            return true;
        }
    }
}