package com.alphadraxonis.sandboxpixeldungeon.editor.inv;

import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.SPDAction;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.categories.EditorItemBag;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.categories.Items;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.categories.Plants;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.categories.Traps;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.items.EditorItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.items.ItemItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.items.PlantItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.items.TileItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.items.TrapItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.CategoryScroller;
import com.alphadraxonis.sandboxpixeldungeon.editor.util.EditorUtilies;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.items.bags.Bag;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.Potion;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.exotic.ExoticPotion;
import com.alphadraxonis.sandboxpixeldungeon.items.rings.Ring;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.Scroll;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.alphadraxonis.sandboxpixeldungeon.levels.Terrain;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.Trap;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.plants.Plant;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSprite;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSpriteSheet;
import com.alphadraxonis.sandboxpixeldungeon.sprites.SkeletonSprite;
import com.alphadraxonis.sandboxpixeldungeon.ui.ScrollingListPane;
import com.alphadraxonis.sandboxpixeldungeon.ui.Window;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndBag;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndTabbed;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndTitledMessage;
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
public class WndEditorInv extends WndTabbed implements EditorInventoryWindow {

    private final int WIDTH = Math.min(WndTitledMessage.WIDTH_MAX, (int) (PixelScene.uiCamera.width * 0.9));
    private final int HEIGHT = (int) (PixelScene.uiCamera.height * 0.9);


    //only one bag window can appear at a time
    public static Window INSTANCE;
    private final static Map<Bag, Float> lastScrollPos = new HashMap<>(5);
    private final static Map<Bag, Integer> lastSelected = new HashMap<>(5);
    private static EditorItemBag lastBag, curBag;

    protected WndBag.ItemSelectorInterface selector;

    private CategoryScroller body;

    private final boolean addTabs;


    public WndEditorInv(EditorItemBag bag) {
        this(bag, null, true);
    }

    public WndEditorInv(EditorItemBag bag, WndBag.ItemSelectorInterface selector, boolean addTabs) {

        super();

        this.addTabs = addTabs;

        if (addTabs) {
            if (INSTANCE != null) {
                INSTANCE.hide();
            }
            INSTANCE = this;
            lastBag = bag;
        }
        curBag = bag;

        this.selector = selector;

        offset(0, EditorUtilies.getMaxWindowOffsetYForVisibleToolbar() + (addTabs ? 0 : tabHeight() /5));
        resize(WIDTH, HEIGHT - (addTabs ? tabHeight() : 0) - yOffset - tabHeight());

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

                    if (selector != null) {
                        for (Item i : b.items) {
                            if (i instanceof ItemItem && !selector.itemSelectable(((ItemItem) i).item()))
                                ret.remove(i);
                        }
                    }

                    return ret;
                }

                @Override
                protected ScrollingListPane.ListItem createListItem(Object object) {
                    if (object instanceof EditorItem) {
                        EditorItem e = (EditorItem) object;
                        if (!Dungeon.quickslot.contains(e)) e.randomizeTexture();
                        return e.createListItem(WndEditorInv.this);
                    } else {
                        Item item = (Item) object;
                        return new DefaultListItem(item, WndEditorInv.this, item.name(), new ItemSprite(item));
                    }
                }

                @Override
                protected Image getImage() {
                    return b.getCategoryImage();
                }

                @Override
                protected String getName() {
                    return b.name();
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
    public void onBackPressed() {
        if (selector != null) {
            selector.onSelect(null);
        }
        super.onBackPressed();
    }

    @Override
    public void select(int index) {
        if (!tabs.isEmpty()) super.select(index);
    }

    @Override
    protected void onClick(Tab tab) {
        Trap tempTrapSave = lastTrapForImage;
        Plant tempPlantSave = lastPlantForImage;
        Item tempItemSave = lastItemForImage;
        hide();
        lastTrapForImage = tempTrapSave;
        lastPlantForImage = tempPlantSave;
        lastItemForImage = tempItemSave;
        Window w = new WndEditorInv((EditorItemBag) ((BagTab) tab).bag, selector, true);
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
    private static Plant lastPlantForImage;
    private static Item lastItemForImage;

    private static Image createIcon(int indexTab) {
        switch (indexTab) {
            case 1:
                return new ItemSprite(new TileItem(Terrain.EMPTY, -1));
            case 2:
                return new SkeletonSprite();
            case 3:
                if (lastItemForImage == null) {
                    lastItemForImage = Reflection.newInstance(Items.getRandomItem(null));
                    lastItemForImage.image = Dungeon.customDungeon.getItemSpriteOnSheet(lastItemForImage);
                    if (lastItemForImage.image == ItemSpriteSheet.SCROLL_HOLDER) {
                        lastItemForImage.image = ItemSpriteSheet.SCROLLS + (int) (Math.random() * Scroll.runes.size())
                                + (lastItemForImage instanceof ExoticScroll ? 16 : 0);
                    } else if (lastItemForImage.image == ItemSpriteSheet.POTION_HOLDER) {
                        lastItemForImage.image = ItemSpriteSheet.POTIONS + (int) (Math.random() * Potion.colors.size())
                                + (lastItemForImage instanceof ExoticPotion ? 16 : 0);
                    } else if (lastItemForImage.image == ItemSpriteSheet.RING_HOLDER) {
                        lastItemForImage.image = ItemSpriteSheet.RINGS + (int) (Math.random() * Ring.gems.size());
                    }
                }
                return new ItemSprite(lastItemForImage.image());
            case 4:
                if (lastTrapForImage == null) {
                    lastTrapForImage = Reflection.newInstance(Traps.getRandomTrap(null));
                    lastTrapForImage.visible = true;
                }
                return TrapItem.getTrapImage(lastTrapForImage);
            case 5:
                if (lastPlantForImage == null)
                    lastPlantForImage = Reflection.newInstance(Plants.getRandomPlant(null));
                return PlantItem.getPlantImage(lastPlantForImage);
        }
        Image img = new ItemSprite(ItemSpriteSheet.SOMETHING);
        img.visible = false;
        return img;
    }

    @Override
    public void hide() {
        lastSelected.put(curBag, body.getSelectedIndex());
        lastScrollPos.put(curBag, body.getCurrentViewY());
        lastTrapForImage = null;
        lastPlantForImage = null;
        lastItemForImage = null;
        super.hide();
        if (INSTANCE == this) {
            INSTANCE = null;
        }
    }

    @Override
    public WndBag.ItemSelectorInterface selector() {
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

    public static WndEditorInv getBag(WndBag.ItemSelectorInterface selector) {
        if (selector.preferredBag() != null) {
            EditorItemBag bag = (EditorItemBag) EditorItemBag.getBag(selector.preferredBag());
            if (bag != null) return new WndEditorInv(bag, selector, selector.addOtherTabs());
        }
        return new WndEditorInv(EditorItemBag.getLastBag(), selector, selector.addOtherTabs());
    }

}