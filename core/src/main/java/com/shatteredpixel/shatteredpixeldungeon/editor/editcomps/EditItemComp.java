package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.ReorderHeapComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.items.AugumentationSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.items.ChargeSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.items.CurseButton;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.items.LevelSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.items.WndChooseEnchant;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.transitions.ChooseDestLevelComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.stateditor.LootTableComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.RandomItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelSchemeLike;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.IconTitleWithSubIcon;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.SimpleWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.FakeTenguShocker;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.CrystalKey;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.GoldenKey;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.SkeletonKey;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTabbed;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.PointerArea;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EditItemComp extends DefaultEditComp<Item> {

    private final Heap heap;

    protected final ReorderHeapComp reorderHeapComp;


    protected final Spinner quantity, quickslotPos;

    protected final CurseButton curseBtn;
    protected final LevelSpinner levelSpinner;
    protected final ChargeSpinner chargeSpinner;

    protected final CheckBox autoIdentify;
    protected final CheckBox cursedKnown;
    protected final CheckBox spreadIfLoot;
    protected final CheckBox blessed;
    protected final Spinner shockerDuration;
    protected final AugumentationSpinner augumentationSpinner;
    protected final RedButton enchantBtn;
    protected final ChooseDestLevelComp keylevel;
    protected final RedButton keyCell;
    protected final RedButton randomItem;

    private final Component[] comps;

    private Window windowInstance;

    public EditItemComp(Item item, Heap heap) {
        super(item);
        this.heap = heap;

        if (heap != null) {
            reorderHeapComp = new ReorderHeapComp(item, heap);
            add(reorderHeapComp);
        } else reorderHeapComp = null;

        if (item.stackable) {
            final int quantityMultiplierForGold = item instanceof Gold ? 10 : 1;
            quantity = new Spinner(new SpinnerIntegerModel(1, 100 * quantityMultiplierForGold, item.quantity(), 1, false, null) {
                @Override
                public float getInputFieldWith(float height) {
                    return height * 1.4f;
                }

                @Override
                public int getClicksPerSecondWhileHolding() {
                    return 15 * quantityMultiplierForGold;
                }
            }, " " + Messages.get(EditItemComp.class, "quantity") + ":", 10);
            ((SpinnerIntegerModel) quantity.getModel()).setAbsoluteMinAndMax(1f, 1_000_000f);
            quantity.addChangeListener(() -> {
                item.quantity((int) quantity.getValue());
                updateObj();
            });
            add(quantity);
        } else quantity = null;

        //only for start items
        if (item.reservedQuickslot != 0 && item.defaultAction() != null && !(item instanceof Key)) {//use -1 to indicate 0 while still enabling this
            quickslotPos = new Spinner(new SpinnerIntegerModel(0, 6, item.reservedQuickslot == -1 ? 0 : item.reservedQuickslot, 1, false, null) {
                @Override
                public float getInputFieldWith(float height) {
                    return height * 1.4f;
                }

                @Override
                public void displayInputAnyNumberDialog() {
                    //do nothing
                }

                @Override
                public String getDisplayString() {
                    if ((int) getValue() == 0) return Messages.get(EditItemComp.class, "no_quickslot");
                    return super.getDisplayString();
                }

                @Override
                public int getClicksPerSecondWhileHolding() {
                    return 14;
                }
            }, " " + Messages.get(EditItemComp.class, "quickslot") + ":", 10);
            quickslotPos.addChangeListener(() -> {
                item.reservedQuickslot = (int) quickslotPos.getValue();
                if (item.reservedQuickslot == 0) item.reservedQuickslot = -1;
            });
            add(quickslotPos);
        } else quickslotPos = null;

        if (heap == null) {
           spreadIfLoot = new CheckBox(Messages.get(EditItemComp.class, "spread_if_loot")){
               @Override
               public void checked(boolean value) {
                   super.checked(value);
                   item.spreadIfLoot = value;
               }
           };
           spreadIfLoot.checked(item.spreadIfLoot);
           add(spreadIfLoot);
        } else spreadIfLoot = null;

        if(!(item instanceof RandomItem)) {
            if (!(item instanceof MissileWeapon) && (item instanceof Weapon || item instanceof Armor || item instanceof Ring || item instanceof Artifact || item instanceof Wand)) {
                curseBtn = new CurseButton(item) {
                    @Override
                    protected void onChange() {
                        updateObj();
                    }
                };
                add(curseBtn);
                cursedKnown = new CheckBox(Messages.get(EditItemComp.class, "cursed_known")) {
                    @Override
                    public void checked(boolean value) {
                        super.checked(value);
                        item.setCursedKnown(value);
                    }
                };
                cursedKnown.checked(item.getCursedKnownVar());
                add(cursedKnown);
            } else {
                curseBtn = null;
                cursedKnown = null;
            }

            if (item instanceof Wand) {
                chargeSpinner = new ChargeSpinner((Wand) item) {
                    @Override
                    protected void onChange() {
                        updateObj();
                    }
                };
            } else if (item instanceof Artifact && ((Artifact) item).chargeCap() > 0) {
                chargeSpinner = new ChargeSpinner((Artifact) item) {
                    @Override
                    protected void onChange() {
                        updateObj();
                    }
                };
            } else chargeSpinner = null;
            if (chargeSpinner != null) add(chargeSpinner);

            if (item.isUpgradable() || item instanceof Artifact) {
                levelSpinner = new LevelSpinner(item) {
                    @Override
                    protected void onChange() {
                        updateObj();
                        if (chargeSpinner != null) chargeSpinner.adjustMaximum(item);
                    }
                };
                add(levelSpinner);
            } else levelSpinner = null;

            if (item instanceof Potion || item instanceof Scroll || item instanceof Ring || item instanceof Wand || item instanceof Artifact
                    || (item instanceof Weapon && !(item instanceof MissileWeapon || item instanceof SpiritBow))
                    || (item instanceof Armor && !(item instanceof ClassArmor))) {
//      if (!DefaultStatsCache.getDefaultObject(item.getClass()).isIdentified()) { // always returns true while editing
                autoIdentify = new CheckBox(Messages.get(EditItemComp.class, "auto_identify")) {
                    @Override
                    public void checked(boolean value) {
                        super.checked(value);
                        item.identifyOnStart = value;
                    }
                };
                autoIdentify.checked(item.identifyOnStart);
                add(autoIdentify);
            } else autoIdentify = null;

            if (item instanceof Weapon || item instanceof Armor) {//Missiles support enchantments too
                enchantBtn = new RedButton(Messages.get(EditItemComp.class, "enchant")) {
                    @Override
                    protected void onClick() {
                        Window w = new WndChooseEnchant(item) {
                            @Override
                            protected void finish() {
                                super.finish();
                                updateObj();
                            }
                        };
                        if (Game.scene() instanceof EditorScene) EditorScene.show(w);
                        else Game.scene().addToFront(w);
                    }
                };
                add(enchantBtn);
            } else enchantBtn = null;

            if (ScrollOfEnchantment.enchantable(item)) {
                augumentationSpinner = new AugumentationSpinner(item) {
                    @Override
                    protected void onChange() {
                        updateObj();
                    }
                };
                add(augumentationSpinner);
            } else augumentationSpinner = null;

            if (item instanceof Ankh) {
                blessed = new CheckBox(Messages.get(EditItemComp.class, "blessed")) {
                    @Override
                    public void checked(boolean value) {
                        super.checked(value);
                        ((Ankh) item).blessed = value;
                        updateObj();
                    }
                };
                blessed.checked(((Ankh) item).blessed);
                add(blessed);
            } else blessed = null;

            if (item instanceof FakeTenguShocker) {
                shockerDuration = new Spinner(new SpinnerIntegerModel(1, 100, ((FakeTenguShocker) item).duration, 1, false, null) {
                    @Override
                    public float getInputFieldWith(float height) {
                        return height * 1.4f;
                    }
                },
                        " " + Messages.get(EditItemComp.class, "duration") + ":", 10);
                shockerDuration.addChangeListener(() -> ((FakeTenguShocker) item).duration = (int) shockerDuration.getValue());
                add(shockerDuration);
            } else shockerDuration = null;

            if (item instanceof Key) {
                keylevel = new ChooseDestLevelComp(Messages.get(EditItemComp.class, "floor")) {
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
                            boolean canChangeKeyCell = !Level.ANY.equals(((Key) item).levelName);
                            if (!canChangeKeyCell && ((Key) item).cell != -1) {
                                ((Key) item).cell = -1;
                                keyCell.text(Messages.get(EditItemComp.class, "key_cell_any"));
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
                    if (EditorScene.customLevel() == null || cell >= EditorScene.customLevel().length()) cell = ((Key) item).cell = -1;
                    keyCell = new RedButton("") {
                        @Override
                        protected void onClick() {
                            EditorScene.selectCell(gatewayTelePosListener);
                            windowInstance = EditorUtilies.getParentWindow(keyCell);
                            windowInstance.active = false;
                            if (windowInstance instanceof WndTabbed)
                                ((WndTabbed) windowInstance).setBlockLevelForTabs(PointerArea.NEVER_BLOCK);
                            Game.scene().remove(windowInstance);
                        }
                    };
                    if (cell == -1) keyCell.text(Messages.get(EditItemComp.class, "key_cell_any"));
                    else keyCell.text(Messages.get(EditItemComp.class, "key_cell_fixed", EditorUtilies.cellToString(cell)));
                    add(keyCell);
                } else keyCell = null;

            } else {
                keylevel = null;
                keyCell = null;
            }
            randomItem = null;
        } else {
            randomItem = new RedButton(Messages.get(EditItemComp.class, "edit_random")) {
                @Override
                protected void onClick() {
                    LootTableComp lootTable = new LootTableComp(null, (RandomItem<?>) item);
                    SimpleWindow w = new SimpleWindow((int) Math.ceil(width), (int) (PixelScene.uiCamera.height * 0.75));
                    w.initComponents(lootTable.createTitle(), lootTable, lootTable.getOutsideSp(), 0f, 0.5f);
                    w.offset(EditorUtilies.getParentWindow(EditItemComp.this).getOffset());
                    EditorScene.show(w);
                }
            };
            add(randomItem);
            keyCell = null;
            keylevel = null;
            chargeSpinner = null;
            levelSpinner = null;
            augumentationSpinner = null;
            curseBtn = null;
            cursedKnown = null;
            autoIdentify = null;
            enchantBtn = null;
            blessed = null;
            shockerDuration = null;
        }

        comps = new Component[]{quantity, quickslotPos, keylevel, keyCell, shockerDuration, chargeSpinner, levelSpinner, augumentationSpinner,
                curseBtn, cursedKnown, autoIdentify, enchantBtn, blessed, spreadIfLoot, randomItem};
    }

    @Override
    protected void layout() {
        desc.maxWidth((int) width);

        if (reorderHeapComp != null) reorderHeapComp.setRect(width - WndTitledMessage.GAP , y, -1, title.height());

        title.setRect(x, y, reorderHeapComp == null ? width : reorderHeapComp.left() - WndTitledMessage.GAP * 2, title.height());
        desc.setRect(x, title.bottom() + WndTitledMessage.GAP * 2, desc.width(), desc.height());

        height = desc.bottom() + 1;

        layoutCompsLinear(comps);
    }

    @Override
    protected void onShow() {
        super.onShow();
        if (reorderHeapComp != null) reorderHeapComp.updateEnableState();
    }

    @Override
    protected Component createTitle() {
        return new IconTitleWithSubIcon(obj);
    }

    @Override
    protected String createDescription() {
        return obj.info();
    }

    @Override
    public Image getIcon() {
        return new ItemSprite(obj);
    }

    @Override
    protected void updateObj() {
        if (title instanceof IconTitle) {
            ((IconTitle) title).label(Messages.titleCase(obj.title()));
            ((IconTitle) title).icon(CustomDungeon.getDungeon().getItemImage(obj));
        }
        desc.text(createDescription());
        if (heap != null) {
            heap.updateSubicon();
            EditorScene.updateHeapImage(heap);
        }
        super.updateObj();
    }

    private final CellSelector.Listener gatewayTelePosListener = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer cell) {
            if (cell != null) {
                Key key = (Key) obj;

                boolean validPos;
                Heap h = EditorScene.customLevel().heaps.get(cell);
                if (key instanceof GoldenKey) validPos = h != null && h.type == Heap.Type.LOCKED_CHEST;
                else if (key instanceof CrystalKey) validPos = Dungeon.level.map[cell] == Terrain.CRYSTAL_DOOR || h != null && h.type == Heap.Type.CRYSTAL_CHEST;
                else if (key instanceof IronKey) validPos = Dungeon.level.map[cell] == Terrain.LOCKED_DOOR;
                else if (key instanceof SkeletonKey) validPos = Dungeon.level.map[cell] == Terrain.LOCKED_EXIT;
                else validPos = false;

                if (!validPos) key.cell = -1;
                else key.cell = cell;
                if (key.cell == -1)
                    keyCell.text(Messages.get(EditItemComp.class, "key_cell_any"));
                else
                    keyCell.text(Messages.get(EditItemComp.class, "key_cell_fixed", EditorUtilies.cellToString(key.cell)));
                windowInstance.active = true;
                if (windowInstance instanceof WndTabbed)
                    ((WndTabbed) windowInstance).setBlockLevelForTabs(PointerArea.ALWAYS_BLOCK);
                EditorScene.show(windowInstance);
            }
        }

        @Override
        public String prompt() {
            return Messages.get(EditItemComp.class, "key_cell_prompt");
        }
    };


    public static boolean areEqual(Item a, Item b) {
        return areEqual(a,b, false);
    }

    public static boolean areEqual(Item a, Item b, boolean ignoreQuantity) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        if (a.getClass() != b.getClass()) return false;
        if (!ignoreQuantity && a.quantity() != b.quantity()) return false;
        if (a.cursed != b.cursed) return false;
        if (a.level() != b.level()) return false;
        if (a.getCursedKnownVar() != b.getCursedKnownVar()) return false;
        if (a.levelKnown != b.levelKnown) return false;
        if (a.spreadIfLoot != b.spreadIfLoot) return false;
        if (a instanceof Weapon) {
            Weapon aa = (Weapon) a, bb = (Weapon) b;
            if (aa.augment != bb.augment) return false;
            return aa.enchantment == bb.enchantment
                    || (aa.enchantment != null && bb.enchantment != null && aa.enchantment.getClass() == bb.enchantment.getClass());
//            if (aa.enchantment == null && bb.enchantment == null) return true;
//            if (aa.enchantment == null || bb.enchantment == null) return false;
//            return aa.enchantment.getClass() == bb.enchantment.getClass();
        }
        if (a instanceof Armor) {
            Armor aa = (Armor) a, bb = (Armor) b;
            if (aa.augment != bb.augment) return false;
            if (aa.glyph == null) return bb.glyph == null;
            if (bb.glyph == null) return false;
            return aa.glyph.getClass() == bb.glyph.getClass();
        }
        if (a instanceof Key)
            return ((Key) a).levelName.equals(((Key) b).levelName) && ((Key) a).cell == ((Key) b).cell;
        if (a instanceof Ankh) return ((Ankh) a).blessed == ((Ankh) b).blessed;
        if (a instanceof Bomb) return ((Bomb) a).igniteOnDrop == ((Bomb) b).igniteOnDrop;
        if (a instanceof RandomItem) return a.equals(b);
        return true;
    }
}