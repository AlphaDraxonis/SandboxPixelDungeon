package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.items.AugumentationSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.items.CurseButton;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.items.LevelSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.items.WndChooseEnchant;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.transitions.ChooseDestLevelComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.IconTitleWithSubIcon;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
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

    protected final Spinner quantity;

    protected final CurseButton curseBtn;
    protected final LevelSpinner levelSpinner;
    protected final CheckBox levelKnown;
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
                    return height;
                }

                @Override
                public int getClicksPerSecondWhileHolding() {
                    return 15 * quantityMultiplierForGold;
                }
            }, " " + Messages.get(EditItemComp.class, "quantity") + ":", 10);
            quantity.setButtonWidth(14);
            quantity.addChangeListener(() -> {
                item.quantity((int) quantity.getValue());
                updateObj();
            });
            add(quantity);
        } else quantity = null;

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

        if (item.isUpgradable()) {
            levelSpinner = new LevelSpinner(item) {
                @Override
                protected void onChange() {
                    updateObj();
                }
            };
            add(levelSpinner);
            levelKnown = new CheckBox(Messages.get(EditItemComp.class, "level_known")) {
                @Override
                public void checked(boolean value) {
                    super.checked(value);
                    item.levelKnown = value;
                }
            };
            levelKnown.checked(item.levelKnown);
            add(levelKnown);
        } else {
            levelSpinner = null;
            levelKnown = null;
        }

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
                protected List<LevelScheme> filterLevels(Collection<LevelScheme> levels) {
                    return new ArrayList<>(levels);
                }

                @Override
                public void selectObject(Object object) {
                    super.selectObject(object);
                    ((Key) item).levelName = (String) object;
                    updateObj();
                }
            };
            keylevel.selectObject(((Key) item).levelName);
            add(keylevel);
        } else keylevel = null;

        comps = new Component[]{quantity, keylevel, levelSpinner, augumentationSpinner, curseBtn, cursedKnown, levelKnown, enchantBtn, blessed};
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