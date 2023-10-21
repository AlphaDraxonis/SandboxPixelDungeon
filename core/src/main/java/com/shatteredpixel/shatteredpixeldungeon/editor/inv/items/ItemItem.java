package com.shatteredpixel.shatteredpixeldungeon.editor.inv.items;

import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditItemComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.DefaultListItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.EditorInventoryWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.HeapActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.ItemActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.IconTitleWithSubIcon;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
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
                onUpdateIfUsedForItem(item());
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