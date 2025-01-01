package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObjectManager;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.interfaces.CustomGameObjectClass;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.ReorderHeapComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.customizables.ChangeCustomizable;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.customizables.ChangeItemCustomizable;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.items.AugmentationSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.items.ChargeSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.items.CurseButton;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.items.DurabilitySpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.items.LevelSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.items.WndChooseEnchant;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.transitions.ChooseDestLevelComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Items;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Mobs;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.ItemItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.MobItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TrapItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.RandomItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.RandomItemDistrComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelSchemeLike;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.IconTitleWithSubIcon;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemContainerWithLabel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemSelector;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StringInputComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledButtonWithIconAndText;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledCheckBox;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledItemSelector;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.AbstractSpinnerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerEnumModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerLikeButton;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerTextIconModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.StyledSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.EnergyCrystal;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.FakeTenguShocker;
import com.shatteredpixel.shatteredpixeldungeon.items.journal.CustomDocumentPage;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.CrystalKey;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.GoldenKey;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.SkeletonKey;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.TrinketCatalyst;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfSummoning;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Corrupting;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemSlot;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class EditItemComp extends DefaultEditComp<Item> {

    public static boolean showSpreadIfLoot, showOnlyCheckType, hideLevelSpinner;

    private final Heap heap;

    protected ReorderHeapComp reorderHeapComp;


    protected StyledSpinner quantity, quickslotPos;

    protected CurseButton curseBtn;
    protected StyledCheckBox permaCursed;
    protected LevelSpinner levelSpinner;
    protected ChargeSpinner chargeSpinner;
    protected SpinnerLikeButton wandRecharging;
    protected DurabilitySpinner durabilitySpinner;
    protected StyledItemSelector magesStaffWand;
    protected StyledCheckBox hasSeal;
    protected SpinnerLikeButton classArmorTier;

    protected StyledCheckBox autoIdentify;
    protected StyledCheckBox cursedKnown;
    protected StyledCheckBox spreadIfLoot;
    protected StyledCheckBox exactItemInRecipe;
    protected StyledCheckBox blessed;
    protected StyledCheckBox igniteBombOnDrop;
    protected StyledSpinner shockerDuration;
    protected AugmentationSpinner augmentationSpinner;
    protected StyledButton enchantBtn;
    protected StyledSpinner numChoosableTrinkets;
    protected ItemContainer<Trinket> rollTrinkets;
    protected ItemContainer<MobItem> summonMobs;
    protected StyledSpinner docPageType;
    protected StringInputComp docPageText, docPageTitle;
    protected ChooseDestLevelComp keylevel;
    protected StyledButton keyCell;
    protected Component randomItem;

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

        rename.setVisible(!(item instanceof CustomGameObjectClass) || ((CustomGameObjectClass) item).getInheritStats());

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
                protected String displayString(Object value) {
                    if ((int) value == 0) return label("no_quickslot");
                    return super.displayString(value);
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

        if (heap == null && showOnlyCheckType) {
            exactItemInRecipe = new StyledCheckBox(label("only_check_type"));
            exactItemInRecipe.checked(!item.onlyCheckTypeIfRecipe);
            exactItemInRecipe.addChangeListener(v -> item.onlyCheckTypeIfRecipe = !v);
            add(exactItemInRecipe);
        }
        showOnlyCheckType = false;

        if (!(item instanceof RandomItem)) {
            if (!(item instanceof MissileWeapon) && (item instanceof Weapon || item instanceof Armor || item instanceof Ring || item instanceof Artifact || item instanceof Wand)) {


                permaCursed = new StyledCheckBox(label("perma_curse"));
                permaCursed.checked(item.permaCurse);
                permaCursed.addChangeListener(v -> item.permaCurse = v);
                permaCursed.enable(item.cursed);
                add(permaCursed);

                curseBtn = new CurseButton(item) {
                    @Override
                    protected void onChange() {
                        updateObj();
                        permaCursed.enable(item.cursed);
                        if (!item.cursed) {
                            permaCursed.checked(false);
                        }
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
                        ItemSelector.showSelectWindow(this, ItemSelector.NullTypeSelector.DISABLED, Trinket.class, Items.bag(), new HashSet<>(0));
                    }
                };
                add(rollTrinkets);
            }

            if (item instanceof Wand) {//Check ItemItem#status() if you change sth
                Wand w = (Wand) item;
                chargeSpinner = new ChargeSpinner(w) {
                    @Override
                    protected void onChange() {
                        updateObj();
                    }
                };

                wandRecharging = new SpinnerLikeButton(new SpinnerEnumModel<>(Wand.RechargeRule.class, w.rechargeRule, v -> w.rechargeRule = v),
                        label("charging_rule"));
                add(wandRecharging);

            } else if (item instanceof Artifact && ((Artifact) item).chargeCap() > 0) {//Check ItemItem#status() if you change sth
                chargeSpinner = new ChargeSpinner((Artifact) item) {
                    @Override
                    protected void onChange() {
                        updateObj();
                    }
                };
            }
            if (chargeSpinner != null) add(chargeSpinner);

            if (LevelSpinner.availableForItem(item) && !hideLevelSpinner) {
                levelSpinner = new LevelSpinner(item) {
                    @Override
                    protected void onChange() {
                        if (item instanceof MagesStaff) {
                            Wand imbuedWand = ((MagesStaff) item).wand;
                            if (imbuedWand != null) {
                                int oldMax = imbuedWand.maxCharges;
                                ((MagesStaff) item).updateWand(false);
                                if (imbuedWand.curCharges == oldMax) imbuedWand.curCharges = imbuedWand.maxCharges;
                            }
                        }

                        updateObj();
                        if (chargeSpinner != null) chargeSpinner.adjustMaximum(item);
                    }
                };
                add(levelSpinner);
            }
            hideLevelSpinner = false;

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
                        Wand imbuedWand = ((MagesStaff) item).wand;
                        if (imbuedWand != null) {
                            int oldMax = imbuedWand.maxCharges;
                            ((MagesStaff) item).updateWand(false);
                            if (imbuedWand.curCharges == oldMax) imbuedWand.curCharges = imbuedWand.maxCharges;
                        }
                        updateObj();
                    }

                    @Override
                    protected void onItemSlotClick() {
                        hideLevelSpinner = true;
                        super.onItemSlotClick();
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

            if (item instanceof ClassArmor) {
                ClassArmor armor = (ClassArmor) item;
                AbstractSpinnerModel model =  new SpinnerIntegerModel(1, 5, armor.tier, true) {
                    @Override
                    protected Image displayIcon(Object value) {
                        if (value instanceof Integer) {
                            switch (((int) value)) {
                                case 1: return new ItemSprite(ItemSpriteSheet.ARMOR_CLOTH);
                                case 2: return new ItemSprite(ItemSpriteSheet.ARMOR_LEATHER);
                                case 3: return new ItemSprite(ItemSpriteSheet.ARMOR_MAIL);
                                case 4: return new ItemSprite(ItemSpriteSheet.ARMOR_SCALE);
                                case 5: return new ItemSprite(ItemSpriteSheet.ARMOR_PLATE);
                            }
                        }
                        return super.displayIcon(value);
                    }

                    @Override
                    protected String displayString(Object value) {
                        return " ";
                    }

                };
                classArmorTier = new SpinnerLikeButton(model, label("tier")) {
                    @Override
                    protected float getValueIconHeight() {
                        return 13;
                    }
                };
                model.addChangeListener(() -> {
                    armor.tier = (int) model.getValue();
                    updateObj();
                });
                add(classArmorTier);

                chargeSpinner = new ChargeSpinner(armor) {
                    @Override
                    protected void onChange() {
                        updateObj();
                    }
                };
                add(chargeSpinner);
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

            if (item instanceof WandOfSummoning) {
                List<MobItem> asMobItems = new ArrayList<>();
                if (((WandOfSummoning) item).summonTemplate != null) {
                    for (Mob m : ((WandOfSummoning) item).summonTemplate) {
                        asMobItems.add(new MobItem(m));
                    }
                }
                summonMobs = new ItemContainerWithLabel<MobItem>(asMobItems, this, label("summon_mob"), false, 1, Integer.MAX_VALUE) {
                    @Override
                    public boolean itemSelectable(Item item) {
                        return item instanceof MobItem && !((MobItem) item).getObject().isImmune(Corrupting.class);
                    }

                    @Override
                    protected void doAddItem(MobItem mobItem) {
                        mobItem = (MobItem) mobItem.getCopy();
                        super.doAddItem(mobItem);
                        ((WandOfSummoning) item).summonTemplate.add(mobItem.mob());
                        ((WandOfSummoning) item).nextSummon = null;
                    }

                    @Override
                    protected boolean removeSlot(ItemContainer<MobItem>.Slot slot) {
                        if (super.removeSlot(slot)) {
                            ((WandOfSummoning) item).summonTemplate.remove(((MobItem) slot.item()).mob());
                            ((WandOfSummoning) item).nextSummon = null;
                            return true;
                        }
                        return false;
                    }

                    @Override
                    public Class<? extends Bag> preferredBag() {
                        return Mobs.bag().getClass();
                    }
                };
                add(summonMobs);
            }

            if (item instanceof CustomDocumentPage) {

                List<String> types = CustomDocumentPage.types;
                docPageType = new StyledSpinner(new SpinnerTextIconModel(true, ((CustomDocumentPage) item).type, types.toArray()) {

                    @Override
                    protected String displayString(Object value) {
                        String txt = Document.INTROS.pageTitle((String) value);
                        if (txt != Messages.NO_TEXT_FOUND) return txt;
                        return Messages.get(CustomDocumentPage.class, ((String) value));
                    }

                    @Override
                    protected Image displayIcon(Object value) {
                        return new ItemSprite(CustomDocumentPage.getImage(types.indexOf(value)));
                    }
                }, Messages.get(EditHeapComp.class, "type"), 6);
                docPageType.addChangeListener( () -> {
                    ((CustomDocumentPage) item).setType(types.indexOf(docPageType.getValue()), heap);
                    updateObj();
                });
                add(docPageType);

                docPageTitle = new StringInputComp(label("doc_page_title"), ((CustomDocumentPage) item).title, 100, false, "") {
                    @Override
                    protected void onChange() {
                        super.onChange();
                        ((CustomDocumentPage) item).title = docPageTitle.getText();
                        updateObj();
                    }
                };
                add(docPageTitle);

                docPageText = new StringInputComp(label("doc_page_text"), ((CustomDocumentPage) item).text, Integer.MAX_VALUE, true, "") {
                    @Override
                    protected void onChange() {
                        super.onChange();
                        ((CustomDocumentPage) item).text = docPageText.getText();
                        updateObj();
                    }
                };
                add(docPageText);

            }

            if (item instanceof Key) {
                Key k = (Key) item;

                if (heap != null && heap.pos != -1) {
                    int cell = k.cell;
                    if (Dungeon.level == null || cell >= Dungeon.level.length()) cell = k.cell = -1;
                    keyCell = new RedButton("") {
                        @Override
                        protected void onClick() {
                            EditorScene.hideWindowsTemporarily();
                            EditorScene.selectCell(keyCellPositionListener);
                        }
                    };
                    if (cell == -1) keyCell.text(Messages.get(EditItemComp.class, "key_cell_any"));
                    else keyCell.text(Messages.get(EditItemComp.class, "key_cell_fixed", EditorUtilities.cellToString(cell)));
                    add(keyCell);
                } else keyCell = null;

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
                            k.levelName = EditorUtilities.getCodeName((LevelScheme) object);
                        }
                        if (keyCell != null) {
                            boolean canChangeKeyCell = Dungeon.level.name.equals(k.levelName);
                            if (!canChangeKeyCell && k.cell != -1) {
                                k.cell = -1;
                                keyCell.text(label("key_cell_any"));
                            }
                            keyCell.enable(canChangeKeyCell);
                        }
                        updateObj();
                    }
                };
                keylevel.selectObject(k.levelName);
                add(keylevel);

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
                        if (item instanceof Gold || item instanceof EnergyCrystal) return false;
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
            rename.setVisible(false);
            randomItem = new Component() {
                private RandomItemDistrComp distr = new RandomItemDistrComp((RandomItem<?>) item) {
                    @Override
                    protected void updateParent() {
                        updateObj();
                    }
                };
                private Component outsideSp = distr.getOutsideSp();

                {
                    add(distr);
                    add(outsideSp);
                }

                @Override
                protected void layout() {
                    distr.setRect(x, y, width, 0);
                    outsideSp.setRect(x, distr.bottom() + 2, width, 6);
                    height = distr.height() + outsideSp.height() + 3;
                }
            };
            add(randomItem);
        }

        rectComps = new Component[]{quantity, quickslotPos, numChoosableTrinkets, shockerDuration, chargeSpinner, wandRecharging, levelSpinner, durabilitySpinner,
                augmentationSpinner, curseBtn, permaCursed, cursedKnown, autoIdentify, enchantBtn, magesStaffWand, hasSeal, classArmorTier, blessed, igniteBombOnDrop, docPageType, spreadIfLoot, exactItemInRecipe};
        linearComps = new Component[]{rollTrinkets, summonMobs, docPageTitle, docPageText, bagItems, randomItem, keylevel, keyCell};

        initializeCompsForCustomObjectClass();
    }

    @Override
    protected void onInheritStatsClicked(boolean flag, boolean initializing) {
        if (flag && !initializing) {
            obj.copyStats((Item) CustomObjectManager.getLuaClass(((CustomGameObjectClass) obj).getIdentifier()));
        }

        for (Component c : rectComps) {
            if (c != null) c.visible = c.active = !flag;
        }
        for (Component c : linearComps) {
            if (c != null) c.visible = c.active = !flag;
        }

        if (rename != null) rename.setVisible(!flag && !(obj instanceof RandomItem));

        ((CustomGameObjectClass) obj).setInheritStats(flag);
//        if (viewScript != null) viewScript.visible = viewScript.active = true;
    }

    @Override
    protected void layout() {
        super.layout();
        layoutCompsInRectangles(rectComps);
        layoutCompsLinear(linearComps);

        layoutCustomObjectEditor();
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
        ChangeCustomizable.showAsWindow(this, w -> new ChangeItemCustomizable(w, this));
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

        if (magesStaffWand != null) {
            magesStaffWand.updateItem();
        }

        super.updateObj();
    }

    @Override
    protected void updateStates() {
        super.updateStates();
        if (quantity != null)               quantity.setValue(obj.quantity());
        if (quickslotPos != null)           quickslotPos.setValue(obj.reservedQuickslot);
        if (curseBtn != null)               curseBtn.checked(obj.cursed);
        if (levelSpinner != null)           levelSpinner.setValue(obj.level());
        if (chargeSpinner != null)          chargeSpinner.updateValue(obj);
        if (wandRecharging != null)         wandRecharging.setValue(((Wand) obj).rechargeRule);
        if (durabilitySpinner != null)      durabilitySpinner.updateValue(obj);
        if (augmentationSpinner != null)    augmentationSpinner.updateValue(obj);
        if (classArmorTier != null)         classArmorTier.setValue(((ClassArmor) obj).tier);
        if (autoIdentify != null)           autoIdentify.checked(obj.identifyOnStart);
        if (cursedKnown != null)            cursedKnown.checked(obj.getCursedKnownVar());
        if (permaCursed != null)            permaCursed.checked(obj.permaCurse);
        if (spreadIfLoot != null)           spreadIfLoot.checked(obj.spreadIfLoot);
        if (exactItemInRecipe != null)      exactItemInRecipe.checked(!obj.onlyCheckTypeIfRecipe);
        if (magesStaffWand != null)         magesStaffWand.setSelectedItem(((MagesStaff) obj).wand);
        if (hasSeal != null)                hasSeal.checked(((Armor) obj).checkSeal() != null);
        if (blessed != null)                blessed.checked(((Ankh) obj).blessed);
        if (igniteBombOnDrop != null)       igniteBombOnDrop.checked(((Bomb) obj).igniteOnDrop);
        if (shockerDuration != null)        shockerDuration.setValue(((FakeTenguShocker) obj).duration);
        if (docPageType != null)            docPageType.setValue(CustomDocumentPage.types.get(((CustomDocumentPage) obj).type));
        if (docPageText != null)            docPageText.setText(((CustomDocumentPage) obj).text);
        if (docPageTitle != null)           docPageTitle.setText(((CustomDocumentPage) obj).text);
        if (numChoosableTrinkets != null)   numChoosableTrinkets.setValue((((TrinketCatalyst) obj).numChoosableTrinkets));
        if (keylevel != null)               keylevel.selectObject(((Key) obj).levelName);
        if (keyCell != null) {
            int cell = ((Key) obj).cell;
            if (cell == -1) keyCell.text(label("key_cell_any"));
            else keyCell.text(Messages.get(EditItemComp.class, "key_cell_fixed", EditorUtilities.cellToString(cell)));
        }

        if (bagItems != null) bagItems.setItemList(((Bag)obj).items);
        if (rollTrinkets != null) rollTrinkets.setItemList(((TrinketCatalyst)obj).rolledTrinkets);
        if (summonMobs != null) {
            List<MobItem> asMobItems = new ArrayList<>();
            if (((WandOfSummoning) obj).summonTemplate != null) {
                for (Mob m : ((WandOfSummoning) obj).summonTemplate) {
                    asMobItems.add(new MobItem(m));
                }
            }
            summonMobs.setItemList(asMobItems);
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
                    keyCell.text(Messages.get(EditItemComp.class, "key_cell_fixed", EditorUtilities.cellToString(key.cell)));

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
        if (a.permaCurse != b.permaCurse) return false;
        if (a.level() != b.level()) return false;
        if ((a.getCursedKnownVar() || a.identifyOnStart) != (b.getCursedKnownVar() || b.identifyOnStart)) return false;
        if ((a.levelKnown || a.identifyOnStart) != (b.levelKnown || b.identifyOnStart)) return false;
        if (a.spreadIfLoot != b.spreadIfLoot) return false;
        if (a.onlyCheckTypeIfRecipe != b.onlyCheckTypeIfRecipe) return false;

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
            if (aa.tier != bb.tier) return false;
        }
        if (a instanceof MissileWeapon) {
            if (((MissileWeapon) a).baseUses != ((MissileWeapon) b).baseUses) return false;
        }
        if (a instanceof Wand) {
            if (((Wand) a).curCharges != ((Wand) b).curCharges) return false;
            if (((Wand) a).rechargeRule != ((Wand) b).rechargeRule) return false;
        }
        if (a instanceof MagesStaff) {
            if (areEqual(((MagesStaff) a).wand, ((MagesStaff) b).wand)) return false;
        }
        if (a instanceof WandOfSummoning) {
            if (!EditMobComp.isMobListEqual(((WandOfSummoning) a).summonTemplate, ((WandOfSummoning) b).summonTemplate)) return false;
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
        if (a instanceof CustomDocumentPage) {
            CustomDocumentPage ap = (CustomDocumentPage) a;
            CustomDocumentPage bp = (CustomDocumentPage) b;
            if (ap.type != bp.type) return false;
            if (!(ap.title == null ? "" : ap.title).equals(bp.title == null ? "" : bp.title)) return false;
            if (!(ap.text == null ? "" : ap.text).equals(bp.text == null ? "" : bp.text)) return false;
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