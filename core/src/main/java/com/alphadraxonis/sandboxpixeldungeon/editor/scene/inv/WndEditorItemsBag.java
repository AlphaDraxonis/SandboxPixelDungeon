package com.alphadraxonis.sandboxpixeldungeon.editor.scene.inv;

import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.SPDAction;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.Traps;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.CategoryScroller;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.items.bags.Bag;
import com.alphadraxonis.sandboxpixeldungeon.levels.Terrain;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.Trap;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSprite;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSpriteSheet;
import com.alphadraxonis.sandboxpixeldungeon.sprites.SkeletonSprite;
import com.alphadraxonis.sandboxpixeldungeon.ui.ScrollingListPane;
import com.alphadraxonis.sandboxpixeldungeon.ui.Window;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndBag;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndTabbed;
import com.watabou.input.GameAction;
import com.watabou.input.KeyBindings;
import com.watabou.input.KeyEvent;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Subclass with all necessary Dungeon.hero nullchecks implemented
public class WndEditorItemsBag extends WndTabbed implements EditorInventoryWindow {

    private final int WIDTH = Math.min(160, (int) (PixelScene.uiCamera.width * 0.9));
    private final int HEIGHT = (int) (PixelScene.uiCamera.height * 0.63);
    private static final Item[] EMPTY_ITEM_ARRAY = new Item[0];


    //only one bag window can appear at a time
    public static Window INSTANCE;
    private final static Map<Bag, Float> lastScrollPos = new HashMap<>(5);
    private final static Map<Bag, Integer> lastSelected = new HashMap<>(5);
    private static EditorItemBag lastBag;

    protected WndBag.ItemSelector selector;

    private CategoryScroller body;

    private final boolean addTabs;


    public WndEditorItemsBag(EditorItemBag bag) {
        this(bag, null, true);
    }

    public WndEditorItemsBag(EditorItemBag bag, WndBag.ItemSelector selector, boolean addTabs) {

        super();

        this.addTabs = addTabs;

        if (addTabs) {
            if (INSTANCE != null) {
                INSTANCE.hide();
            }
            INSTANCE = this;
        }

        this.selector = selector;

        if (addTabs) lastBag = bag;

        resize(WIDTH, HEIGHT + (addTabs ? 0 : 50));

        body = createBody(bag);
        add(body);
        body.setSize(width, height);
        Integer select = lastSelected.get(bag);
        body.selectCategory(select == null ? 0 : select);
        Float scrollPos = lastScrollPos.get(bag);
        body.list.scrollTo(0, scrollPos == null ? 0 : scrollPos);

        if (addTabs) {
            int i = 1;
            for (Bag b : EditorItemBag.getBags()) {//need to be EditorItemBags
                if (b != null) {
                    BagTab tab = new BagTab(b, i++);
                    add(tab);
                    tab.select(b == bag);
                }
            }
        }

        layoutTabs();
    }

    private CategoryScroller createBody(EditorItemBag bag) {
        List<Bag> bags = EditorItemBag.getBags(bag);

        CategoryScroller.Category[] cats = new CategoryScroller.Category[bags.size()];

        int i = 0;
        for (Bag baAag : bags) {
            EditorItemBag b = (EditorItemBag) baAag;
            cats[i] = new CategoryScroller.Category() {
                @Override
                protected List<?> getItems() {
                    List<Item> ret = new ArrayList<>(b.items);
                    if (selector == null || selector().acceptsNull())
                        ret.add(0, addTabs ? EditorItem.REMOVER_ITEM : EditorItem.NULL_ITEM);
                    return ret;
                }

                @Override
                protected ScrollingListPane.ListItem createListItem(Object object) {
                    if (object instanceof EditorItem) {
                        EditorItem e = (EditorItem) object;
                        if (!Dungeon.quickslot.contains(e)) e.randomizeTexture();
                        return e.createListItem(WndEditorItemsBag.this);
                    } else {
                        Item item = (Item) object;
                        return new EditorItem.DefaultListItem(item, WndEditorItemsBag.this, item.name(), new ItemSprite(item));
                    }
                }

                @Override
                protected Image getImage() {
                    return b.getCategoryImage();
                }

                @Override
                protected String getName() {
                    return super.getName();
                }
            };
            i++;
        }
        return new CategoryScroller(cats);

    }

    public static EditorItemBag lastBag() {
        return lastBag;
    }


    @Override
    protected void onClick(Tab tab) {
        Trap tempTrapSave = lastTrapForImage;
        hide();
        lastTrapForImage = tempTrapSave;
        Window w = new WndEditorItemsBag((EditorItemBag) ((BagTab) tab).bag, selector, true);
        if (Game.scene() instanceof EditorScene) {
            EditorScene.show(w);
        } else {
            Game.scene().addToFront(w);
        }
    }

    private class BagTab extends IconTab {

        public final Bag bag;
        private int index;

        public BagTab(Bag bag, int index) {
            super(createIcon(index));

            this.bag = bag;
            this.index = index;
        }

        @Override
        public GameAction keyAction() {
            switch (index) {
                case 1:
                default:
                    return SPDAction.BAG_1;
                case 2:
                    return SPDAction.BAG_2;
                case 3:
                    return SPDAction.BAG_3;
                case 4:
                    return SPDAction.BAG_4;
                case 5:
                    return SPDAction.BAG_5;
            }
        }

        @Override
        protected String hoverText() {
            return Messages.titleCase(bag.name());
        }

        @Override
        protected void select(boolean value) {
            super.select(value);
        }
    }

    private static Trap lastTrapForImage;

    private static Image createIcon(int indexTab) {
        switch (indexTab) {
            case 1:
                return new ItemSprite(new TileItem(Terrain.EMPTY, -1));
            case 2:
                return new SkeletonSprite();
            case 4:
                if (lastTrapForImage == null) {
                    lastTrapForImage = Reflection.newInstance(Traps.getRandomTrap(null));
                    lastTrapForImage.visible = true;
                }
                return TrapItem.getTrapImage(lastTrapForImage);
        }
        Image img = new ItemSprite(ItemSpriteSheet.SOMETHING);
        img.visible = false;
        return img;
    }

    @Override
    public void hide() {
        if (addTabs) lastSelected.put(lastBag(), body.getSelectedIndex());
        lastScrollPos.put(lastBag, body.getCurrentViewY());
        lastTrapForImage = null;
        super.hide();
        if (INSTANCE == this) {
            INSTANCE = null;
        }
    }

    @Override
    public WndBag.ItemSelector selector() {
        return selector;
    }

    public void resetLastScrollPos() {
        lastScrollPos.clear();
    }

    @Override
    public boolean onSignal(KeyEvent event) {
        if (event.pressed && KeyBindings.getActionForKey(event) == SPDAction.INVENTORY) {
            onBackPressed();
            return true;
        } else {
            return super.onSignal(event);
        }
    }

    public static WndEditorItemsBag getBag(WndBag.ItemSelector selector) {
        if (selector.preferredBag() != null) {
            EditorItemBag bag = (EditorItemBag) EditorItemBag.getBag(selector.preferredBag());
            if (bag != null) return new WndEditorItemsBag(bag, selector, selector.addOtherTabs());
            else
                return new WndEditorItemsBag(EditorItemBag.mainBag, selector, selector.addOtherTabs());
        }
        return new WndEditorItemsBag(EditorItemBag.getLastBag(), selector, selector.addOtherTabs());
    }

}