package com.shatteredpixel.shatteredpixeldungeon.editor.inv;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.TileSprite;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.*;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.ItemItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.CategoryScroller;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SkeletonSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTabbed;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.input.GameAction;
import com.watabou.input.KeyBindings;
import com.watabou.input.KeyEvent;
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

        offset(0, EditorUtilies.getMaxWindowOffsetYForVisibleToolbar() + (addTabs ? 0 : tabHeight() / 5));
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
                    List<Object> ret = new ArrayList<>(b.items);
                    if (selector == null) {
                        ret.add(0, EditorItem.REMOVER_ITEM);
                    } else if (selector.acceptsNull()) {
                        ret.add(0, selector.getItemForNull());
                    }

                    if (selector != null) {
                        for (Item i : b.items) {
                            if (i instanceof ItemItem) {
                                if (!selector.itemSelectable(((ItemItem) i).item()))
                                    ret.remove(i);
                            } else if (!selector.itemSelectable(i))
                                ret.remove(i);
                        }
                    }

                    if (b instanceof Tiles.CustomTileBag) {
                        ret.add(new Tiles.AddSimpleCustomTileButton());
                    }
                    if (b instanceof Tiles.ParticleBag) {
                        ret.add(new Tiles.AddParticleButton());
                    }
                    if (b instanceof Mobs.CustomMobsBag) {
                        ret.add(new Mobs.AddCustomMobButton());
                    }

                    return ret;
                }

                @Override
                protected ScrollingListPane.ListItem createListItem(Object object) {
                    if (object instanceof EditorItem) {
                        EditorItem e = (EditorItem) object;
                        if (!Dungeon.quickslot.contains(e) && e instanceof TileItem) ((TileItem) e).randomizeTexture();
                        return e.createListItem(WndEditorInv.this);
                    } else if (object instanceof ScrollingListPane.ListItem) {
                        return (ScrollingListPane.ListItem) object;
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

    public static void updateCurrentTab() {
        if (INSTANCE != null) {
            WndEditorInv w = (WndEditorInv) WndEditorInv.INSTANCE;
            for (Tab t : w.tabs) {
                if (((BagTab) t).isSelected()) {
                    w.onClick(t);
                    return;
                }
            }
        }
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
        EditorScene.show(w);
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

        protected boolean isSelected() {
            return selected;
        }

    }

    private static Trap lastTrapForImage;
    private static Plant lastPlantForImage;
    private static Item lastItemForImage;

    private static Image createIcon(int indexTab) {
        switch (indexTab) {
            case 1:
                return new TileSprite(Terrain.EMPTY);
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
                return lastTrapForImage.getSprite();
            case 5:
                if (lastPlantForImage == null)
                    lastPlantForImage = Reflection.newInstance(Plants.getRandomPlant(null));
                return lastPlantForImage.getSprite();
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