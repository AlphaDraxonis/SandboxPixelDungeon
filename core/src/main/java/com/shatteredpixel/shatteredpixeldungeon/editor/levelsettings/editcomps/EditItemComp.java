package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndMenuEditor;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.items.AugumentationSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.items.CurseButton;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.items.LevelSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.items.WndChooseEnchant;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.items.WndInfoEq;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.IconTitleWithSubIcon;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class EditItemComp extends DefaultEditComp<Item> {

    private final Heap heap;

    protected final Spinner quantity;

    protected final CurseButton curseBtn;
    protected final LevelSpinner levelSpinner;
    protected final CheckBox levelKnown;
    protected final CheckBox cursedKnown;
    protected final AugumentationSpinner augumentationSpinner;
    protected final RedButton enchantBtn;

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
                updateItem();
            });
            add(quantity);
        } else quantity = null;

        if (!(item instanceof MissileWeapon) && (item instanceof Weapon || item instanceof Armor || item instanceof Ring || item instanceof Artifact || item instanceof Wand)) {
            curseBtn = new CurseButton(item) {
                @Override
                protected void onChange() {
                    updateItem();
                }
            };
            add(curseBtn);
            cursedKnown = new CheckBox(Messages.get(EditItemComp.class,"cursed_known")) {
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
                    updateItem();
                }
            };
            add(levelSpinner);
            levelKnown = new CheckBox(Messages.get(EditItemComp.class,"level_known")) {
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
            enchantBtn = new RedButton(Messages.get(WndInfoEq.class,"enchant")) {
                @Override
                protected void onClick() {
                    EditorScene.show(new WndChooseEnchant(item) {
                        @Override
                        protected void finish() {
                            super.finish();
                            updateItem();
                        }
                    });
                }
            };
            add(enchantBtn);
        } else enchantBtn = null;

        if (ScrollOfEnchantment.enchantable(item)) {
            augumentationSpinner = new AugumentationSpinner(item) {
                @Override
                protected void onChange() {
                    updateItem();
                }
            };
            add(augumentationSpinner);
        } else augumentationSpinner = null;
    }

    @Override
    protected void layout() {
        super.layout();

        float posY = height + WndTitledMessage.GAP * 2 - 1;

        if (quantity != null) {
            quantity.setRect(x, posY, width, WndMenuEditor.BTN_HEIGHT);
            posY = quantity.bottom() + WndTitledMessage.GAP;
        }

        if (levelSpinner != null) {
            levelSpinner.setRect(x, posY, width, WndMenuEditor.BTN_HEIGHT);
            posY = levelSpinner.bottom() + WndTitledMessage.GAP;
        }

        if (augumentationSpinner != null) {
            augumentationSpinner.setRect(x, posY, width, WndMenuEditor.BTN_HEIGHT);
            posY = augumentationSpinner.bottom() + WndTitledMessage.GAP;
        }

        if (curseBtn != null) {
            curseBtn.setRect(x, posY, width, WndMenuEditor.BTN_HEIGHT);
            posY = curseBtn.bottom() + WndTitledMessage.GAP;
        }

        if (cursedKnown != null) {
            cursedKnown.setRect(x, posY, width, WndMenuEditor.BTN_HEIGHT);
            posY = cursedKnown.bottom() + WndTitledMessage.GAP;
        }

        if (levelKnown != null) {
            levelKnown.setRect(x, posY, width, WndMenuEditor.BTN_HEIGHT);
            posY = levelKnown.bottom() + WndTitledMessage.GAP;
        }


        if (enchantBtn != null) {
            enchantBtn.setRect(x, posY, width, WndMenuEditor.BTN_HEIGHT);
            posY = enchantBtn.bottom() + WndTitledMessage.GAP;
        }

        height = posY - y - WndTitledMessage.GAP + 1;
    }

    @Override
    protected Component createTitle() {
        return new IconTitleWithSubIcon(item);
    }

    @Override
    protected String createDescription() {
        return item.info();
    }

    @Override
    public Image getIcon() {
        return new ItemSprite(item);
    }

    @Override
    protected void updateItem() {
        if (title instanceof IconTitle) {
            ((IconTitle) title).label(Messages.titleCase(item.title()));
            ((IconTitle) title).icon(CustomDungeon.getDungeon().getItemImage(item));
        }
        desc.text(createDescription());
        if (heap != null) {
            heap.updateSubicon();
            EditorScene.updateHeapImage(heap);
        }
        super.updateItem();
    }
}