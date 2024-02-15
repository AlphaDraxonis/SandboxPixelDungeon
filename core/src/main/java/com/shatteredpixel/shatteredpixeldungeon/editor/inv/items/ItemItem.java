package com.shatteredpixel.shatteredpixeldungeon.editor.inv.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditItemComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.DefaultListItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.EditorInventoryWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.ItemActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemSlot;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.watabou.noosa.Image;

public class ItemItem extends EditorItem<Item> {

    public ItemItem(Item item) {
        this.obj = item;
        icon = item.icon;
    }

    @Override
    public DefaultEditComp<?> createEditComponent() {
        return new EditItemComp(this);
    }

    @Override
    public String name() {
        return getObject().title();
    }

    @Override
    public String title() {
        return getObject().title();
    }

    @Override
    public Image getSprite() {
        return CustomDungeon.getDungeon().getItemImage(getObject());
    }

    @Override
    public Image getSubIcon() {
        return EditorUtilies.createSubIcon(getObject());
    }

    @Override
    public int level() {
        return getObject().level();
    }

    @Override
    public int trueLevel_OVERRIDE_ONLY_FOR_ITEMITEM_CLASS() {
        return getObject().trueLevel_OVERRIDE_ONLY_FOR_ITEMITEM_CLASS();
    }

    @Override
    public int quantity() {
        return getObject().quantity();
    }

    @Override
    public String status() {
        if (getObject() instanceof Wand || getObject() instanceof Artifact && ((Artifact) getObject()).chargeCap() > 0)
            return getObject().status();
        return null;
    }

    @Override
    public ScrollingListPane.ListItem createListItem(EditorInventoryWindow window) {
        return new DefaultListItem(this, window, Messages.titleCase(title()), getSprite()) {
            @Override
            public void onUpdate() {
                Item item = getObject();

                if (item != null) {

                    //IMPORTANT: any change made here should also be made in ItemSlot#updateText()

                    bg.visible = item.cursed;

                    //Code from ItemSlot
                    int trueLvl = item.trueLevel();
                    int buffedLvl = item.buffedLvl();
                    if (trueLvl != 0 || buffedLvl != 0) {
                        lvlLabel.text(Messages.format(ItemSlot.TXT_LEVEL, buffedLvl));
                        lvlLabel.measure();
                        if (trueLvl == buffedLvl || buffedLvl <= 0) {
                            if (buffedLvl > 0) {
                                if ((item instanceof Weapon && ((Weapon) item).curseInfusionBonus)
                                        || (item instanceof Armor && ((Armor) item).curseInfusionBonus)
                                        || (item instanceof Wand && ((Wand) item).curseInfusionBonus)) {
                                    lvlLabel.hardlight(ItemSlot.CURSE_INFUSED);
                                } else {
                                    lvlLabel.hardlight(ItemSlot.UPGRADED);
                                }
                            } else {
                                lvlLabel.hardlight(ItemSlot.DEGRADED);
                            }
                        } else {
                            lvlLabel.hardlight(buffedLvl > trueLvl ? ItemSlot.ENHANCED : ItemSlot.WARNING);
                        }
                    } else lvlLabel.text(null);
                }
                super.onUpdate();
            }
        };
    }

    @Override
    public void setObject(Item obj) {
        super.setObject(obj.getCopy());
    }

    @Override
    public void place(int cell) {

        if (invalidPlacement(cell)) return;

        Item i = getObject().getCopy();

        i.image = CustomDungeon.getDungeon().getItemSpriteOnSheet(i);

        Undo.addActionPart(place(i, cell));

        //TODO remove highGrass?
    }

    public static boolean invalidPlacement(int cell) {
        return Dungeon.level.solid[cell] || Dungeon.level.pit[cell] || !Dungeon.level.insideMap(cell);
    }

    public static ActionPart remove(int cell) {
        Heap heap = Dungeon.level.heaps.get(cell);
        if (heap != null) {
            return new ItemActionPart.Remove(heap.items.peek(), cell);
        }
        return null;
    }

    public static ActionPart place(Item item, int cell) {
        if (item != null) {
            return ItemActionPart.Place(item, cell);
        }
        return null;
    }

    public Item item() {
        return getObject();
    }
}