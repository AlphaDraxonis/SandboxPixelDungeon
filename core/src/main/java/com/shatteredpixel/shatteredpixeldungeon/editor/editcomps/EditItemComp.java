package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.items.AugumentationSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.items.ChargeSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.items.CurseButton;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.items.LevelSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.items.WndChooseEnchant;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.transitions.ChooseDestLevelComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelSchemeLike;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.IconTitleWithSubIcon;
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
import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EditItemComp extends DefaultEditComp<Item> {

    private final Heap heap;

    protected final Spinner quantity, quickslotPos;

    protected final CurseButton curseBtn;
    protected final LevelSpinner levelSpinner;
    protected final ChargeSpinner chargeSpinner;

    protected final CheckBox autoIdentify;
    protected final CheckBox cursedKnown;
    protected final CheckBox blessed;
    protected final AugumentationSpinner augumentationSpinner;
    protected final RedButton enchantBtn;
    protected final ChooseDestLevelComp keylevel;

    private final Component[] comps;

    public EditItemComp(Item item, Heap heap) {
        super(item);
        this.heap = heap;

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
        if (item.reservedQuickslot != 0 && item.defaultAction() != null) {//use -1 to indicate 0 while still enabling this
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
        }else quickslotPos = null;

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
                    updateObj();
                }
            };
            keylevel.selectObject(((Key) item).levelName);
            add(keylevel);
        } else keylevel = null;

        comps = new Component[]{quantity, quickslotPos, keylevel, chargeSpinner, levelSpinner, augumentationSpinner,
                curseBtn, cursedKnown, autoIdentify, enchantBtn, blessed};
    }

    @Override
    protected void layout() {
        super.layout();
        layoutCompsLinear(comps);
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


    public static boolean areEqual(Item a, Item b) {
        if (a == null || b == null) return false;
        if (a.getClass() != b.getClass()) return false;
        if (a.quantity() != b.quantity()) return false;
        if (a.cursed != b.cursed) return false;
        if (a.level() != b.level()) return false;
        if (a.getCursedKnownVar() != b.getCursedKnownVar()) return false;
        if (a.levelKnown != b.levelKnown) return false;
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
        if (a instanceof Key) return ((Key) a).levelName.equals(((Key) b).levelName);
        return true;
    }
}