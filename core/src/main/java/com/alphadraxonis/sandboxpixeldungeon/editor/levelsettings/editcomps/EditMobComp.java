package com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.editcomps;

import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.Buff;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.ArmoredStatue;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Mimic;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Mob;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Statue;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Thief;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.Koord;
import com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.Buffs;
import com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.WndMenuEditor;
import com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.mobs.BuffIndicatorEditor;
import com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.mobs.MobStateSpinner;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.ChooseOneInCategoriesBody;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.ItemSelector;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.WndChooseOneInCategories;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.items.armor.Armor;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.Weapon;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.sprites.StatueSprite;
import com.alphadraxonis.sandboxpixeldungeon.ui.BuffIcon;
import com.alphadraxonis.sandboxpixeldungeon.ui.BuffIndicator;
import com.alphadraxonis.sandboxpixeldungeon.ui.InventorySlot;
import com.alphadraxonis.sandboxpixeldungeon.ui.ItemSlot;
import com.alphadraxonis.sandboxpixeldungeon.ui.RedButton;
import com.alphadraxonis.sandboxpixeldungeon.ui.Window;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndInfoMob;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

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
            statueWeapon = new ItemSelector(" "+ Messages.get(EditMobComp.class,"weapon") +":", Weapon.class, ((Statue) mob).weapon, false) {
                @Override
                public void setSelectedItem(Item selectedItem) {
                    super.setSelectedItem(selectedItem);
                    ((Statue) mob).weapon = (Weapon) selectedItem;
                    EditMobComp.this.updateItem();
                }
            };
            add(statueWeapon);
            if (mob instanceof ArmoredStatue) {
                statueArmor = new ItemSelector(" "+ Messages.get(EditMobComp.class,"armor") +":", Armor.class, ((ArmoredStatue) mob).armor, false) {
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
            thiefItem = new ItemSelector(" "+ Messages.get(EditMobComp.class,"item") +":", Item.class, ((Thief) mob).item, true) {
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

        addBuffs = new RedButton(Messages.get(EditMobComp.class,"add_buff")) {
            @Override
            protected void onClick() {

                Window w = new WndChooseOneInCategories(
                        Messages.get(EditMobComp.class,"add_buff_title"), "",
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