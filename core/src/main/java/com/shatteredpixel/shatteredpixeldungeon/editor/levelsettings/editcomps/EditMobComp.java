package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.ArmoredStatue;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Statue;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Thief;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.Koord;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.Buffs;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.Items;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndMenuEditor;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.items.AugumentationSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.items.CurseButton;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.items.LevelSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.items.WndChooseEnchant;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.mobs.BuffIndicatorEditor;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.mobs.MobStateSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.inv.ItemItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ChooseOneInCategoriesBody;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.IconTitleWithSubIcon;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemSelector;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.WndChooseOneInCategories;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.LostBackpack;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.editor.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.StatueSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.InventorySlot;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemSlot;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoMob;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class EditMobComp extends DefaultEditComp<Mob> {


    //TODO instant update after state change of mimics

    private final ItemSelector statueWeapon, statueArmor;
    private final ItemSelector thiefItem;
    private final MobStateSpinner mobStateSpinner;
    private final RedButton addBuffs;

    private final ItemContainer mimicItems;

    public EditMobComp(Mob mob) {
        super(mob);

        if (mob instanceof Statue) {
            statueWeapon = new ItemSelector(" Weapon:", Weapon.class, ((Statue) mob).weapon, false) {
                @Override
                public void setSelectedItem(Item selectedItem) {
                    super.setSelectedItem(selectedItem);
                    ((Statue) mob).weapon = (Weapon) selectedItem;
                    EditMobComp.this.updateItem();
                }
            };
            add(statueWeapon);
            if (mob instanceof ArmoredStatue) {
                statueArmor = new ItemSelector(" Armor:", Armor.class, ((ArmoredStatue) mob).armor, false) {
                    @Override
                    public void setSelectedItem(Item selectedItem) {
                        super.setSelectedItem(selectedItem);
                        ((ArmoredStatue) mob).armor = (Armor) selectedItem;
                        EditMobComp.this.updateItem();
                    }
                };
                add(statueArmor);
            } else statueArmor = null;
        } else {
            statueWeapon = null;
            statueArmor = null;
        }

        if (mob instanceof Thief) {
            thiefItem = new ItemSelector(" Item:", Item.class, ((Thief) mob).item, true) {
                @Override
                public void setSelectedItem(Item selectedItem) {
                    super.setSelectedItem(selectedItem);
                    ((Thief) mob).item = selectedItem;
                    EditMobComp.this.updateItem();
                }

                @Override
                public void change() {
                    EditorScene.selectItem(selector);
                }
            };
            add(thiefItem);
        } else thiefItem = null;

        if (mob instanceof Mimic) {
            if (((Mimic) mob).items == null) ((Mimic) mob).items = new ArrayList<>();
            ArrayList<Item> mimicItemList = ((Mimic) mob).items;
            mimicItems = new ItemContainer(mimicItemList, this,true) {
                @Override
                protected void addItem(Item item) {
                    //From Heap#drop()
                    if (item.stackable) {
                        for (Item i : mimicItemList) {
                            if (i.isSimilar(item)) {
                                item = i.merge(item);
                                break;
                            }
                        }
                        mimicItemList.remove(item);
                    }
                    mimicItemList.add( item);
                }
            };
            add(mimicItems);
        } else mimicItems = null;

        mobStateSpinner = new MobStateSpinner(mob);
        add(mobStateSpinner);

        addBuffs = new RedButton("Add buffs") {
            @Override
            protected void onClick() {

                Window w = new WndChooseOneInCategories(
                        "Choose Buff", "",
                        Buffs.getAllBuffs2(mob.buffs()), new String[]{"Buffs"}) {
                    @Override
                    protected ChooseOneInCategoriesBody.BtnRow[] createCategoryRows(Object[] category) {
                        ChooseOneInCategoriesBody.BtnRow[] ret = new ChooseOneInCategoriesBody.BtnRow[category.length];
                        for (int i = 0; i < ret.length; i++) {
                            Buff b = Reflection.newInstance((Class<? extends Buff>) category[i]);
                            ret[i] = new ChooseOneInCategoriesBody.BtnRow(b.name(), b.desc(), new BuffIcon(b, true)) {
                                @Override
                                protected void onClick() {
                                    finish();
                                    Buff.affect(mob, b.getClass());
                                    updateItem();
                                }
                            };
                        }
                        return ret;
                    }
                };
                EditorScene.show(w);
            }
        };
        add(addBuffs);
    }

    @Override
    protected void layout() {
        super.layout();

        float posY = height + WndTitledMessage.GAP * 2 - 1;

        if (statueWeapon != null) {
            statueWeapon.setRect(x, posY, width, WndMenuEditor.BTN_HEIGHT);
            PixelScene.align(statueWeapon);
            posY = statueWeapon.bottom() + WndTitledMessage.GAP;
        }
        if (statueArmor != null) {
            statueArmor.setRect(x, posY, width, WndMenuEditor.BTN_HEIGHT);
            PixelScene.align(statueArmor);
            posY = statueArmor.bottom() + WndTitledMessage.GAP;
        }
        if (thiefItem != null) {
            thiefItem.setRect(x, posY, width, WndMenuEditor.BTN_HEIGHT);
            PixelScene.align(thiefItem);
            posY = thiefItem.bottom() + WndTitledMessage.GAP;
        }

        if (mimicItems != null) {
            mimicItems.setRect(x, posY, width, WndMenuEditor.BTN_HEIGHT);
            PixelScene.align(mimicItems);
            posY = mimicItems.bottom() + WndTitledMessage.GAP;
        }

        if (mobStateSpinner != null) {
            mobStateSpinner.setRect(x, posY, width, WndMenuEditor.BTN_HEIGHT);
            PixelScene.align(mobStateSpinner);
            posY = mobStateSpinner.bottom() + WndTitledMessage.GAP;
        }
        if (addBuffs != null) {
            addBuffs.setRect(x, posY, width, WndMenuEditor.BTN_HEIGHT);
            PixelScene.align(addBuffs);
            posY = addBuffs.bottom() + WndTitledMessage.GAP;
        }

        height = posY - y - WndTitledMessage.GAP + 1;
    }

    @Override
    protected Component createTitle() {
        return new MobTitleEditor(item);
    }

    @Override
    protected String createDescription() {
        return item.info();
    }

    @Override
    public Image getIcon() {
        return item.sprite();
    }

    @Override
    public void updateItem() {
        if (title instanceof MobTitleEditor) {
            if (item instanceof ArmoredStatue) {
                Armor armor = ((ArmoredStatue) item).armor;
                ((StatueSprite) ((MobTitleEditor) title).image).setArmor(armor == null ? 0 : armor.tier);
            }
        }
        desc.text(createDescription());
        if (statueWeapon != null) statueWeapon.updateItem();
        if (statueArmor != null) {
            Armor armor = ((ArmoredStatue) item).armor;
            if (item.sprite != null)
                ((StatueSprite) item.sprite).setArmor(armor == null ? 0 : armor.tier);
            statueArmor.updateItem();
        }

        super.updateItem();
    }

    private void addMimicItemToUI(Item item) {
        ItemSlot slot = new InventorySlot(item) {
            @Override
            protected void onClick() {
                EditorScene.show(new EditCompWindow(item, advancedListPaneItem) {
                    @Override
                    protected void onUpdate() {
                        super.onUpdate();
                        item(item);
                    }
                });

            }

            @Override
            protected boolean onLongClick() {
                mimicItems.remove(this);
                EditMobComp.this.remove(this);
                destroy();
                EditMobComp.this.layout();
                EditMobComp.super.updateItem();//For window resizing
                return true;
            }

            @Override
            public void item(Item item) {
                super.item(item);
                bg.visible = true;//gold and bags should have bg
            }
        };
        mimicItems.add(slot);
        add(slot);
        EditMobComp.this.layout();
        super.updateItem();//For window resizing
    }

    private class MobTitleEditor extends WndInfoMob.MobTitle {

        public MobTitleEditor(Mob mob) {
            super(mob, false);
        }

        @Override
        protected BuffIndicator createBuffIndicator(Mob mob, boolean large) {
            return new BuffIndicatorEditor(mob, large, EditMobComp.this);
        }

        protected String createTitle(Mob mob) {
            return super.createTitle(mob) + (mob.pos == 0 ? "" : " "+ new Koord(mob.pos));
        }
    }
}