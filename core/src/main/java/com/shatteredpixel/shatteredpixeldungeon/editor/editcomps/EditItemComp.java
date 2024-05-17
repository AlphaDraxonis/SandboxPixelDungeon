package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.ReorderHeapComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.customizables.ChangeItemCustomizable;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.items.*;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.transitions.ChooseDestLevelComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Items;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.ItemItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.MobItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TrapItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.RandomItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.RandomItemDistrComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelSchemeLike;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.*;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.StyledSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.*;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.FakeTenguShocker;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.*;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.TrinketCatalyst;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.*;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

import java.util.*;

public class EditItemComp extends DefaultEditComp<Item> {

    public static boolean showSpreadIfLoot;

    private final Heap heap;

    protected ReorderHeapComp reorderHeapComp;


    protected StyledSpinner quantity, quickslotPos;

    protected CurseButton curseBtn;
    protected LevelSpinner levelSpinner;
    protected ChargeSpinner chargeSpinner;
    protected DurabilitySpinner durabilitySpinner;
    protected StyledItemSelector magesStaffWand;
    protected StyledCheckBox hasSeal;

    protected StyledCheckBox autoIdentify;
    protected StyledCheckBox cursedKnown;
    protected StyledCheckBox spreadIfLoot;
    protected StyledCheckBox blessed;
    protected StyledCheckBox igniteBombOnDrop;
    protected StyledSpinner shockerDuration;
    protected AugmentationSpinner augmentationSpinner;
    protected StyledButton enchantBtn;
    protected StyledSpinner numChoosableTrinkets;
    protected ItemContainer<Trinket> rollTrinkets;
    protected ChooseDestLevelComp keylevel;
    protected StyledButton keyCell;
    protected StyledButton randomItem;

    protected ItemContainer<Item> bagItems;

    private Component[] rectComps;
    private Component[] linearComps;

    private final ItemItem itemItem;//used for linking the item with the sprite in the toolbar

    public EditItemComp(ItemItem itemItem) {
        super(itemItem.getObject());
        this.itemItem = itemItem;
        this.heap = null;
        initComps(getObj());
    }

    public EditItemComp(Item item, Heap heap) {
        super(item);
        this.itemItem = null;
        this.heap = heap;
        initComps(getObj());
    }

    private void initComps(Item item) {

        rename.visible = rename.active = true;

        if (heap != null) {
            reorderHeapComp = new ReorderHeapComp(item, heap);
            title.add(reorderHeapComp);
        }

        if (item.stackable) {
            quantity = new StyledSpinner(new SpinnerIntegerModel(1, item instanceof Gold ? 1000 : 100, item.quantity()), label("quantity"));
            ((SpinnerIntegerModel) quantity.getModel()).setAbsoluteMaximum(2_000_000_000f);
            quantity.addChangeListener(() -> {
                item.quantity((int) quantity.getValue());
                updateObj();
            });
            add(quantity);
        }

        //only for start items
        if (item.reservedQuickslot != 0 && item.defaultAction() != null && !(item instanceof Key)) {//use -1 to indicate 0 while still enabling this
            quickslotPos = new StyledSpinner(new SpinnerIntegerModel(0, 6, item.reservedQuickslot == -1 ? 0 : item.reservedQuickslot) {
                @Override
                public void displayInputAnyNumberDialog() {
                    //do nothing
                }

                @Override
                public String getDisplayString() {
                    if ((int) getValue() == 0) return label("no_quickslot");
                    return super.getDisplayString();
                }

                @Override
                public int getClicksPerSecondWhileHolding() {
                    return 14;
                }
            }, label("quickslot"));
            quickslotPos.addChangeListener(() -> {
                item.reservedQuickslot = (int) quickslotPos.getValue();
                if (item.reservedQuickslot == 0) item.reservedQuickslot = -1;
            });
            add(quickslotPos);
        }

        if (heap == null && showSpreadIfLoot) {
            spreadIfLoot = new StyledCheckBox(label("spread_if_loot"));
            spreadIfLoot.checked(item.spreadIfLoot);
            spreadIfLoot.addChangeListener(v -> item.spreadIfLoot = v);
            add(spreadIfLoot);
        }
        showSpreadIfLoot = false;

        if (!(item instanceof RandomItem)) {
            if (!(item instanceof MissileWeapon) && (item instanceof Weapon || item instanceof Armor || item instanceof Ring || item instanceof Artifact || item instanceof Wand)) {
                curseBtn = new CurseButton(item) {
                    @Override
                    protected void onChange() {
                        updateObj();
                    }
                };
                add(curseBtn);
                cursedKnown = new StyledCheckBox(label("cursed_known"));
                cursedKnown.checked(item.getCursedKnownVar());
                cursedKnown.addChangeListener(item::setCursedKnown);
                add(cursedKnown);
            }

            if (item instanceof TrinketCatalyst) {

                TrinketCatalyst cata = (TrinketCatalyst) item;

                numChoosableTrinkets = new StyledSpinner(new SpinnerIntegerModel(1, Generator.Category.TRINKET.classes.length, cata.numChoosableTrinkets), label("num_choosable_trinkets"));
                ((SpinnerIntegerModel) numChoosableTrinkets.getModel()).setAbsoluteMinAndMax(1, 100);
                numChoosableTrinkets.addChangeListener(() -> {
                    cata.numChoosableTrinkets = (int) numChoosableTrinkets.getValue();
                });
                add(numChoosableTrinkets);

                rollTrinkets = new ItemContainerWithLabel<Trinket>(cata.rolledTrinkets, this, label("roll_trinkets")) {
                    @Override
                    protected void showSelectWindow() {
                        ItemSelector.showSelectWindow(this, ItemSelector.NullTypeSelector.DISABLED, Trinket.class, Items.bag, new HashSet<>(0));
                    }
                };
                add(rollTrinkets);
            }

            if (item instanceof Wand) {//Check ItemItem#status() if you change sth
                chargeSpinner = new ChargeSpinner((Wand) item) {
                    @Override
                    protected void onChange() {
                        updateObj();
                    }
                };
            } else if (item instanceof Artifact && ((Artifact) item).chargeCap() > 0) {//Check ItemItem#status() if you change sth
                chargeSpinner = new ChargeSpinner((Artifact) item) {
                    @Override
                    protected void onChange() {
                        updateObj();
                    }
                };
            }
            if (chargeSpinner != null) add(chargeSpinner);

            if (item.isUpgradable() || item instanceof Artifact || item instanceof Trinket) {
                levelSpinner = new LevelSpinner(item) {
                    @Override
                    protected void onChange() {
                        updateObj();
                        if (chargeSpinner != null) chargeSpinner.adjustMaximum(item);
                    }
                };
                add(levelSpinner);
            }

            if (item instanceof Potion || item instanceof Scroll || item instanceof Ring || item instanceof Wand || item instanceof Artifact
                    || (item instanceof Weapon && !(item instanceof MissileWeapon))
                    || (item instanceof Armor && !(item instanceof ClassArmor))) {
//      if (!DefaultStatsCache.getDefaultObject(item.getClass()).isIdentified()) { // always returns true while editing
                autoIdentify = new StyledCheckBox(label("auto_identify"));
                autoIdentify.icon(new ItemSprite(ItemSpriteSheet.SCROLL_ISAZ));
                autoIdentify.checked(item.identifyOnStart);
                autoIdentify.addChangeListener(v -> item.identifyOnStart = v);
                add(autoIdentify);
            }

            if (item instanceof Weapon || item instanceof Armor) {//Missiles support enchantments too
                enchantBtn = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, label("enchant"), PixelScene.landscape() ? 9 : 8) {
                    {
                        text.align(RenderedTextBlock.CENTER_ALIGN);
                    }
                    @Override
                    protected void onClick() {
                        EditorScene.show(new WndChooseEnchant(item) {
                            @Override
                            protected void finish() {
                                super.finish();
                                updateObj();
                            }
                        });
                    }
                };
                enchantBtn.icon(new ItemSprite(ItemSpriteSheet.STONE_ENCHANT));
                add(enchantBtn);
            }

            if (ScrollOfEnchantment.enchantable(item)) {
                augmentationSpinner = new AugmentationSpinner(item) {
                    @Override
                    protected void onChange() {
                        updateObj();
                    }
                };
                add(augmentationSpinner);
            }

            if (item instanceof MissileWeapon) {
                durabilitySpinner = new DurabilitySpinner((MissileWeapon) item) {
                    @Override
                    protected void onChange() {
                        updateObj();
                    }
                };
                add(durabilitySpinner);
            }

            if (item instanceof MagesStaff) {
                magesStaffWand = new StyledItemSelector(label("imbued_wand"), Wand.class, ((MagesStaff) item).wand, ItemSelector.NullTypeSelector.NOTHING) {
                    @Override
                    public void setSelectedItem(Item selectedItem) {
                        super.setSelectedItem(selectedItem);
                        ((MagesStaff) item).wand = (Wand) selectedItem;
                        updateObj();
                    }
                };
                magesStaffWand.setShowWhenNull(ItemSpriteSheet.WAND_HOLDER);
                add(magesStaffWand);
            }

            if (item instanceof Armor) {
                //cannot change properties of the seal! (not compared in areEqual!)
                hasSeal = new StyledCheckBox(label("has_seal"));
                hasSeal.icon(new ItemSprite(ItemSpriteSheet.SEAL));
                hasSeal.checked(((Armor) item).checkSeal() != null);
                hasSeal.addChangeListener(v -> {
                    if (v) ((Armor) item).affixSeal(new BrokenSeal());
                    else ((Armor) item).detachSeal(null);
                    updateObj();
                });
                add(hasSeal);
            }

            if (item instanceof Ankh) {
                blessed = new StyledCheckBox(label("blessed"));
                blessed.icon(Icons.TALENT.get());
                blessed.checked(((Ankh) item).blessed);
                blessed.addChangeListener(v -> {
                    ((Ankh) item).blessed = v;
                    updateObj();
                });
                add(blessed);
            }

            if (item instanceof Bomb) {
                igniteBombOnDrop = new StyledCheckBox(label("ignite_bomb_on_drop"));
                igniteBombOnDrop.icon(new ItemSprite(ItemSpriteSheet.BOMB, new ItemSprite.Glowing(0xFF0000, 0.6f)));
                igniteBombOnDrop.checked(((Bomb) item).igniteOnDrop);
                igniteBombOnDrop.addChangeListener(v -> {
                    ((Bomb) item).igniteOnDrop = v;
                    updateObj();
                });
                add(igniteBombOnDrop);
            }

            if (item instanceof FakeTenguShocker) {
                shockerDuration = new StyledSpinner(new SpinnerIntegerModel(1, 100, ((FakeTenguShocker) item).duration),
                        label("duration"));
                shockerDuration.addChangeListener(() -> ((FakeTenguShocker) item).duration = (int) shockerDuration.getValue());
                add(shockerDuration);
            }

            if (item instanceof Key) {
                keylevel = new ChooseDestLevelComp(label("floor")) {
                    @Override
                    protected List<LevelSchemeLike> filterLevels(Collection<? extends LevelSchemeLike> levels) {
                        List<LevelSchemeLike> ret = new ArrayList<>(levels);
                        ret.add(0, LevelScheme.ANY_LEVEL_SCHEME);
                        return ret;
                    }

                    @Override
                    public void selectObject(Object object) {
                        super.selectObject(object);
                        if (object instanceof LevelScheme) {
                            ((Key) item).levelName = EditorUtilies.getCodeName((LevelScheme) object);
                        }
                        if (keyCell != null) {
                            boolean canChangeKeyCell = Dungeon.level.name.equals(((Key) item).levelName);
                            if (!canChangeKeyCell && ((Key) item).cell != -1) {
                                ((Key) item).cell = -1;
                                keyCell.text(label("key_cell_any"));
                            }
                            keyCell.enable(canChangeKeyCell);
                        }
                        updateObj();
                    }
                };
                keylevel.selectObject(((Key) item).levelName);
                add(keylevel);

                if (heap != null && heap.pos != -1) {
                    int cell = ((Key) item).cell;
                    if (Dungeon.level == null || cell >= Dungeon.level.length()) cell = ((Key) item).cell = -1;
                    keyCell = new RedButton("") {
                        @Override
                        protected void onClick() {
                            EditorScene.hideWindowsTemporarily();
                            EditorScene.selectCell(keyCellPositionListener);
                        }
                    };
                    if (cell == -1) keyCell.text(Messages.get(EditItemComp.class, "key_cell_any"));
                    else keyCell.text(Messages.get(EditItemComp.class, "key_cell_fixed", EditorUtilies.cellToString(cell)));
                    add(keyCell);
                } else keyCell = null;

            }

            if (item instanceof Bag) {
                Bag bag = (Bag) item;
                bagItems = new ItemContainer<Item>(bag.items, this, false, 0, bag.capacity()) {
                    @Override
                    protected void doAddItem(Item item) {
                        if (bag.canHold( item )) {
                            boolean wasIdentifyOnStart = item.identifyOnStart;
                            item.identifyOnStart = false;
                            item.collect(bag);
                            item.identifyOnStart = wasIdentifyOnStart;
                        }
                        updateObj();
                    }

                    @Override
                    public boolean itemSelectable(Item item) {
//                        if (item instanceof Bag) return false;
                        if (!bag.canHold(item)) return false;

                        if (item.stackable) {
                            for (Item i : bag.items) {
                                if (item.isSimilar( i )) {
                                    return false;
                                }
                            }
                        }
                        return true;
                    }

                    @Override
                    protected void showWndEditItemComp(ItemContainer<Item>.Slot slot, Item item) {
                        EditorScene.show(new EditCompWindow(item, null, advancedListPaneItem) {
                            @Override
                            protected void onUpdate() {
                                super.onUpdate();
                                slot.item(item);
//                                updateObj();
                            }
                        });
                    }
                };
                add(bagItems);
            }

        } else {
            randomItem = new RedButton(label("edit_random")) {
                @Override
                protected void onClick() {
                    RandomItemDistrComp randomItemDistrComp = new RandomItemDistrComp((RandomItem<?>) item);
                    SimpleWindow w = new SimpleWindow((int) Math.ceil(width), (int) (PixelScene.uiCamera.height * 0.75));
                    w.initComponents(randomItemDistrComp.createTitle(), randomItemDistrComp, randomItemDistrComp.getOutsideSp(), 0f, 0.5f);
                    w.offset(EditorUtilies.getParentWindow(EditItemComp.this).getOffset());
                    EditorScene.show(w);
                }
            };
            add(randomItem);
        }

        rectComps = new Component[]{quantity, quickslotPos, numChoosableTrinkets, shockerDuration, chargeSpinner, levelSpinner, durabilitySpinner,
                augmentationSpinner, curseBtn, cursedKnown, autoIdentify, enchantBtn, magesStaffWand, hasSeal, blessed, igniteBombOnDrop, spreadIfLoot};
        linearComps = new Component[]{rollTrinkets, bagItems, randomItem, keylevel, keyCell};
    }

    @Override
    protected void layout() {
        super.layout();
        layoutCompsInRectangles(rectComps);
        layoutCompsLinear(linearComps);
    }

    @Override
    protected void layoutTitle() {

        float renameDeleteWidth =
                (reorderHeapComp != null && reorderHeapComp.visible ? reorderHeapComp.width() + 2 : 0)
                + (rename.visible ? rename.icon().width() + 2 : 0)
                + (delete.visible ? delete.icon().width() + 2 : 0);

        float posX = title.left();

        mainTitleComp.setRect(posX, title.top(), title.width() - renameDeleteWidth, -1);
        posX = mainTitleComp.right();

        float h = title.height();

        if (reorderHeapComp != null && reorderHeapComp.visible) {
            reorderHeapComp.setRect(posX, title.top(), -1, h);
            posX += reorderHeapComp.width() + 2;
        }
        if (rename.visible) {
            rename.setRect(posX, mainTitleComp.top() + (h - rename.icon().height()) * 0.5f, rename.icon().width(), rename.icon().height());
            posX += rename.width() + 2;
        }
        if (delete.visible) {
            delete.setRect(posX, mainTitleComp.top() + (h - delete.icon().height()) * 0.5f, delete.icon().width(), delete.icon().height());
            posX += delete.width() + 2;
        }
    }

    @Override
    protected void onShow(boolean fullyInitialized) {
        super.onShow(fullyInitialized);
        if (reorderHeapComp != null) reorderHeapComp.updateEnableState();
        if (fullyInitialized) {
            updateObj();
            updateStates();
        }
    }

    @Override
    protected void onRenameClicked() {
        Window parent = EditorUtilies.getParentWindow(this);
        SimpleWindow w = new SimpleWindow(parent.camera().width - 10, parent.camera().height - 10) {
            @Override
            public void hide() {
                super.hide();
                updateObj();
            }
        };
        ChangeItemCustomizable cc = new ChangeItemCustomizable(EditItemComp.this);
        w.initComponents(cc.createTitle(), cc, null, 0f, 0.5f);
        EditorScene.show(w);
    }

    static String label(String key) {
        return Messages.get(EditItemComp.class, key);
    }

    @Override
    protected Component createTitle() {
        return new IconTitleWithSubIcon(obj);
    }

    @Override
    protected String createTitleText() {
        return Messages.titleCase(obj.title());
    }

    @Override
    protected String createDescription() {
        return obj.info();
    }

    @Override
    public Image getIcon() {
        return Dungeon.customDungeon.getItemImage(obj);
    }

    @Override
    public void updateObj() {
        if (heap != null) {
            EditorScene.updateHeapImage(heap);
            heap.updateSubicon();
        }

        if (itemItem != null) {
            ItemSlot slot = QuickSlotButton.containsItem(itemItem);
            if (slot != null) slot.item(itemItem);
        }

        super.updateObj();
    }

    private void updateStates() {
        if (quantity != null)               quantity.setValue(obj.quantity());
        if (quickslotPos != null)           quickslotPos.setValue(obj.reservedQuickslot);
        if (curseBtn != null)               curseBtn.checked(obj.cursed);
        if (levelSpinner != null)           levelSpinner.setValue(obj.level());
        if (chargeSpinner != null)          chargeSpinner.updateValue(obj);
        if (durabilitySpinner != null)      durabilitySpinner.updateValue(obj);
        if (augmentationSpinner != null)    augmentationSpinner.updateValue(obj);
        if (autoIdentify != null)           autoIdentify.checked(obj.identifyOnStart);
        if (cursedKnown != null)            cursedKnown.checked(obj.getCursedKnownVar());
        if (spreadIfLoot != null)           spreadIfLoot.checked(obj.spreadIfLoot);
        if (magesStaffWand != null)         magesStaffWand.setSelectedItem(((MagesStaff) obj).wand);
        if (hasSeal != null)                hasSeal.checked(((Armor) obj).checkSeal() != null);
        if (blessed != null)                blessed.checked(((Ankh) obj).blessed);
        if (igniteBombOnDrop != null)       igniteBombOnDrop.checked(((Bomb) obj).igniteOnDrop);
        if (shockerDuration != null)        shockerDuration.setValue(((FakeTenguShocker) obj).duration);
        if (keylevel != null)               keylevel.selectObject(((Key) obj).levelName);
        if (keyCell != null) {
            int cell = ((Key) obj).cell;
            if (cell == -1) keyCell.text(label("key_cell_any"));
            else keyCell.text(Messages.get(EditItemComp.class, "key_cell_fixed", EditorUtilies.cellToString(cell)));
        }
    }

    private final CellSelector.Listener keyCellPositionListener = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer cell) {
            if (cell != null) {
                Key key = (Key) obj;

                boolean validPos;
                Heap h = Dungeon.level.heaps.get(cell);
                if (key instanceof GoldenKey) validPos = h != null && h.type == Heap.Type.LOCKED_CHEST;
                else if (key instanceof CrystalKey)
                    validPos = Dungeon.level.map[cell] == Terrain.CRYSTAL_DOOR || h != null && h.type == Heap.Type.CRYSTAL_CHEST;
                else if (key instanceof IronKey) validPos = Dungeon.level.map[cell] == Terrain.LOCKED_DOOR;
                else if (key instanceof SkeletonKey) validPos = Dungeon.level.map[cell] == Terrain.LOCKED_EXIT;
                else validPos = false;

                if (!validPos) key.cell = -1;
                else key.cell = cell;
                if (key.cell == -1)
                    keyCell.text(label("key_cell_any"));
                else
                    keyCell.text(Messages.get(EditItemComp.class, "key_cell_fixed", EditorUtilies.cellToString(key.cell)));

                EditorScene.reshowWindows();
            }
        }

        @Override
        public String prompt() {
            return label("key_cell_prompt");
        }
    };


    public static boolean areEqual(Item a, Item b) {
        return areEqual(a, b, false);
    }

    public static boolean areEqual(Item a, Item b, boolean ignoreQuantity) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        if (a.getClass() != b.getClass()) return false;

        if (!ignoreQuantity && a.quantity() != b.quantity()) return false;
        if (a.cursed != b.cursed) return false;
        if (a.level() != b.level()) return false;
        if ((a.getCursedKnownVar() || a.identifyOnStart) != (b.getCursedKnownVar() || b.identifyOnStart)) return false;
        if ((a.levelKnown || a.identifyOnStart) != (b.levelKnown || b.identifyOnStart)) return false;
        if (a.spreadIfLoot != b.spreadIfLoot) return false;

        if (!Objects.equals(a.getCustomName(), b.getCustomName())) return false;
        if (!Objects.equals(a.getCustomDesc(), b.getCustomDesc())) return false;

        if (a instanceof Weapon) {
            Weapon aa = (Weapon) a, bb = (Weapon) b;
            if (aa.augment != bb.augment) return false;

            if (aa.enchantment != null && bb.enchantment != null) {
                if (aa.enchantment.getClass() != bb.enchantment.getClass()) return false;
            } else {
                if (aa.enchantment != bb.enchantment) return false;
            }
        }
        if (a instanceof Armor) {
            Armor aa = (Armor) a, bb = (Armor) b;
            if (aa.augment != bb.augment) return false;
            if ((aa.checkSeal() == null) != (bb.checkSeal() == null)) return false;//do not compare seal properties

            if (aa.glyph != null && bb.glyph != null) {
                if (aa.glyph.getClass() != bb.glyph.getClass()) return false;
            } else {
                if (aa.glyph != bb.glyph) return false;
            }
        }
        if (a instanceof MissileWeapon) {
            if (((MissileWeapon) a).baseUses != ((MissileWeapon) b).baseUses) return false;
        }
        if (a instanceof MagesStaff) {
            if (areEqual(((MagesStaff) a).wand, ((MagesStaff) b).wand)) return false;
        }
        if (a instanceof Key) {
            if (!((Key) a).levelName.equals(((Key) b).levelName)) return false;
            if (((Key) a).cell != ((Key) b).cell) return false;
        }
        if (a instanceof Ankh) {
            if (((Ankh) a).blessed != ((Ankh) b).blessed) return false;
        }
        if (a instanceof Bomb) {
            if (((Bomb) a).igniteOnDrop != ((Bomb) b).igniteOnDrop) return false;
        }
        if (a instanceof Bag) {
            if (!isItemListEqual(((Bag) a).items, ((Bag) b).items)) return false;
        }
        if (a instanceof TrinketCatalyst) {
            if (((TrinketCatalyst) a).numChoosableTrinkets != ((TrinketCatalyst) b).numChoosableTrinkets) return false;
            if (!isItemListEqual(((TrinketCatalyst) a).rolledTrinkets, ((TrinketCatalyst) b).rolledTrinkets)) return false;
        }
        if (a instanceof RandomItem) {
            if (!a.equals(b)) return false;
        }

        if (a instanceof MobItem) return EditMobComp.areEqual(((MobItem) a).getObject(), ((MobItem) b).getObject());
        if (a instanceof TrapItem) return EditTrapComp.areEqual(((TrapItem) a).getObject(), ((TrapItem) b).getObject());

        return true;
    }


    public static boolean isItemListEqual(List<? extends Item> a, List<? extends Item> b) {
        int sizeA = a == null ? 0 : a.size();
        int sizeB = b == null ? 0 : b.size();
        if (sizeA != sizeB) return false;
        if (a == null || b == null) return true;
        int index = 0;
        for (Item i : a) {
            if (!EditItemComp.areEqual(i, b.get(index))) return false;
            index++;
        }
        return true;
    }

}