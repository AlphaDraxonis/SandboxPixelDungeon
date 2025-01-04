package com.shatteredpixel.shatteredpixeldungeon.editor.inv;

import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.TileSprite;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Buffs;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.EditorInventory;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.EditorItemBag;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.GameObjectCategory;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Items;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Mobs;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Plants;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Tiles;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Traps;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.ItemItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.AbstractCategoryScroller;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.CategoryScroller;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.CompactCategoryScroller;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
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
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SkeletonSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPaneWithScrollbar;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTabbed;
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

    private static final boolean USE_COMPACT_UI = true;

    public static boolean chooseClass = false;


    //only one bag window can appear at a time
    public static Window INSTANCE;
    private final static Map<Bag, Float> lastScrollPos = new HashMap<>(5);
    private final static Map<Bag, Integer> lastSelected = new HashMap<>(5);
    private static EditorItemBag lastBag, curBag;

    protected WndBag.ItemSelectorInterface selector;

    private AbstractCategoryScroller<?> body;


    public WndEditorInv(EditorItemBag bag) {
        this(bag, null);
    }

    public WndEditorInv(EditorItemBag bag, WndBag.ItemSelectorInterface selector) {

        super();

        this.selector = selector;

        boolean addTabs = getBags().size() > 1;

        if (addTabs) {
            if (INSTANCE != null) {
                INSTANCE.hide();
            }
            INSTANCE = this;
            lastBag = bag;
        }
        curBag = bag;

        offset(0, EditorUtilities.getMaxWindowOffsetYForVisibleToolbar() + (addTabs ? 0 : tabHeight() / 5));
        resize(WindowSize.WIDTH_LARGE.get(), WindowSize.HEIGHT_LARGE.get() - (addTabs ? tabHeight() : 0) - yOffset - tabHeight());

        body = createBody(bag);
        add(body);
        body.setSize(width, height);
        Integer select = lastSelected.get(bag);
        body.selectCategory(select == null ? 0 : select);
        Float scrollPos = lastScrollPos.get(bag);
        body.sp.scrollTo(0, scrollPos == null ? 0 : scrollPos);

        if (addTabs) {
            int i = 1;
            for (Bag b : getBags()) {
                if (b != null) {
                    BagTab tab = new BagTab(b, i++);
                    add(tab);
                    tab.select(b == bag);
                }
            }
        }

        layoutTabs();
    }

    protected List<Bag> getBags() {
        List<Bag> result =
                selector == null
                        ? null
                        : selector.getBags();
        return result == null ? EditorInventory.getMainBags() : result;
    }

    private AbstractCategoryScroller<?> createBody(EditorItemBag bag) {
        List<Bag> bags = EditorInventory.getMainBags(bag);

        CategoryScroller.Category[] cats = new CategoryScroller.Category[bags.size()];

        int i = 0;
        for (Bag baAag : bags) {
            EditorItemBag b = (EditorItemBag) baAag;
            cats[i] = new CategoryScroller.Category() {

                @Override
                public List<?> createItems(boolean required) {
                    if (selector == null && !required) return null;

                    List<Object> ret = new ArrayList<>(b.items);

                    if (selector != null) {
                        for (Item i : b.items) {
                            if (i instanceof ItemItem) {
                                if (!selector.itemSelectable(((ItemItem) i).item()))
                                    ret.remove(i);
                            } else if (!selector.itemSelectable(i))
                                ret.remove(i);
                        }
                    }

                    if (!ret.isEmpty()) {
                        if (selector == null) {
                            ret.add(0, EditorItem.REMOVER_ITEM);
                        } else if (selector.acceptsNull()) {
                            ret.add(0, selector.getItemForNull());
                        }
                    }

                    if (b instanceof Tiles.CustomTileBag) {
                        ret.add(new Tiles.AddSimpleCustomTileButton());
                    }
                    if (b instanceof Tiles.ParticleBag) {
                        ret.add(new Tiles.AddParticleButton());
                    }
                    if (b instanceof GameObjectCategory.CustomObjectBag && WndEditorInv.INSTANCE != null) {
                        ret.add( ((GameObjectCategory.CustomObjectBag) b).createAddBtn() );
                    }

                    return ret;
                }

                @Override
                public Image getImage() {
                    return b.getCategoryImage();
                }

                @Override
                public String getName() {
                    return Messages.titleCase(b.name());
                }
            };
            i++;
        }
        return USE_COMPACT_UI ?
                new CompactCategoryScroller(cats, this) {
                    @Override
                    protected ScrollPane createSp() {
                        return new ScrollPaneWithScrollbar(createSpContent());
                    }
                }
                : new CategoryScroller(cats, this);
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
        Buff tempBuffSave = lastBuffForImage;
        Item tempItemSave = lastItemForImage;
        hide();
        lastTrapForImage = tempTrapSave;
        lastPlantForImage = tempPlantSave;
        lastBuffForImage = tempBuffSave;
        lastItemForImage = tempItemSave;
        EditorScene.show( new WndEditorInv((EditorItemBag) ((BagTab) tab).bag, selector) );
    }

    private class BagTab extends IconTab {

        public final Bag bag;
        private int index;

        public BagTab(Bag bag, int index) {
            super(createIcon(bag));

            this.bag = bag;
            this.index = index;
        }

        @Override
        public GameAction keyAction() {
            switch (index) {
                case 1:
                default: return SPDAction.BAG_1;
                case 2:  return SPDAction.BAG_2;
                case 3:  return SPDAction.BAG_3;
                case 4:  return SPDAction.BAG_4;
                case 5:  return SPDAction.BAG_5;
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
    private static Buff lastBuffForImage;

    private static Image createIcon(Bag bag) {
        if (bag == Tiles.bag) return new TileSprite(Terrain.EMPTY);
        if (bag == Mobs.bag()) return new SkeletonSprite();
        if (bag == Items.bag()) {
            if (lastItemForImage == null) {
                lastItemForImage = Reflection.newInstance(GameObjectCategory.getRandom(Items.instance().values(), null));
                lastItemForImage.image = CustomDungeon.getItemSpriteOnSheet(lastItemForImage);
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
        }
        if (bag == Traps.bag()) {
            if (lastTrapForImage == null) {
                lastTrapForImage = Reflection.newInstance(GameObjectCategory.getRandom(Traps.instance().values()));
                lastTrapForImage.visible = true;
            }
            return lastTrapForImage.getSprite();
        }
        if (bag == Plants.bag()) {
            if (lastPlantForImage == null)
                lastPlantForImage = Reflection.newInstance(GameObjectCategory.getRandom(Plants.instance().values()));
            return lastPlantForImage.getSprite();
        }
        if (bag == Buffs.bag()) {
            if (lastBuffForImage == null)
                lastBuffForImage = Reflection.newInstance(GameObjectCategory.getRandom(Buffs.instance().values()));
            return new BuffIcon(lastBuffForImage, true);
        }
        Image img = new ItemSprite(ItemSpriteSheet.SOMETHING);
        img.visible = false;
        return img;
    }

    @Override
    public void hide() {
        lastSelected.put(curBag, body.getSelectedCatIndex());
        lastScrollPos.put(curBag, body.getCurrentViewY());
        lastTrapForImage = null;
        lastPlantForImage = null;
        lastBuffForImage = null;
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
            EditorItemBag bag = (EditorItemBag) EditorInventory.getBag(selector.preferredBag());
            if (bag != null) return new WndEditorInv(bag, selector);
        }
        return new WndEditorInv(EditorInventory.getLastBag(selector.getBags()), selector);
    }

}