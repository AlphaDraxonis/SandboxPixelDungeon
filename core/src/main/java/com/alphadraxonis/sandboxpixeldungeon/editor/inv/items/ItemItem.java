package com.alphadraxonis.sandboxpixeldungeon.editor.inv.items;

import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.DefaultEditComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.EditItemComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.DefaultListItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.EditorInventoryWindow;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomDungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomLevel;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.ActionPart;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.Undo;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.parts.HeapActionPart;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.parts.ItemActionPart;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.IconTitleWithSubIcon;
import com.alphadraxonis.sandboxpixeldungeon.items.Heap;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.items.armor.Armor;
import com.alphadraxonis.sandboxpixeldungeon.items.wands.Wand;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.Weapon;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.ui.ItemSlot;
import com.alphadraxonis.sandboxpixeldungeon.ui.ScrollingListPane;
import com.watabou.noosa.Image;

public class ItemItem extends EditorItem {


    private final Item item;


    public ItemItem(Item item) {
        this.item = item;
        icon = item.icon;
    }

    @Override
    public ScrollingListPane.ListItem createListItem(EditorInventoryWindow window) {
        return new DefaultListItem(this, window, item().name(), getSprite()) {
            @Override
            public void onUpdate() {
                if (item == null) return;
                Item i = item();
                bg.visible = i.cursed;
                label.text(Messages.titleCase(i.title()));

                if (icon != null) remove(icon);
                icon = CustomDungeon.getDungeon().getItemImage(i);
                addToBack(icon);
                remove(bg);
                addToBack(bg);

                int lvl = i.level();
                if (lvl != 0) {
                    lvlLabel.text(Messages.format(ItemSlot.TXT_LEVEL, lvl));
                    lvlLabel.measure();
                    if ((i instanceof Weapon && ((Weapon) i).curseInfusionBonus)
                            || (i instanceof Armor && ((Armor) i).curseInfusionBonus)
                            || (i instanceof Wand && ((Wand) i).curseInfusionBonus)) {
                        lvlLabel.hardlight(ItemSlot.CURSE_INFUSED);
                    } else {
                        lvlLabel.hardlight(ItemSlot.UPGRADED);
                    }
                } else lvlLabel.text(null);

                super.onUpdate();
            }
        };
    }

    @Override
    public DefaultEditComp<?> createEditComponent() {
        return new EditItemComp(item(), null);
    }

    @Override
    public Image getSprite() {
        return CustomDungeon.getDungeon().getItemImage(item());
    }

    @Override
    public Image getSubIcon() {
        return IconTitleWithSubIcon.createSubIcon(item());
    }

    @Override
    public void place(int cell) {

        CustomLevel level = EditorScene.customLevel();

        if (invalidPlacement(cell, level)) return;

        Item i = item().getCopy();

        i.image = CustomDungeon.getDungeon().getItemSpriteOnSheet(i);

        Undo.addActionPart(place(i, cell));
//        level.drop(i, cell);

        //TODO remove highGrass?
    }

    @Override
    public Object getObject() {
        return item();
    }

    public Item item() {
        return item;
    }

    @Override
    public int level() {
        return item().level();
    }

    @Override
    public int quantity() {
        return item().quantity();
    }

    @Override
    public String name() {
        return item().name();
    }

    public static boolean invalidPlacement(int cell, CustomLevel level) {
//        return level.passable[cell];
        return level.solid[cell] || level.pit[cell] || !level.insideMap(cell);
    }

    public static ItemActionPart remove(int cell, CustomLevel level) {
        Heap heap = level.heaps.get(cell);
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


    public static HeapActionPart.Remove remove(Heap heap) {
        if (heap != null) {
            return new HeapActionPart.Remove(heap);
        }
        return null;
    }

    public static HeapActionPart.Place place(Heap heap) {
        if (heap != null) return new HeapActionPart.Place(heap);
        return null;
    }

    public static HeapActionPart.Place place(Heap heap, int cell) {
        if (heap != null) {
            heap.pos = cell;
            return new HeapActionPart.Place(heap);
        }
        return null;
    }
}