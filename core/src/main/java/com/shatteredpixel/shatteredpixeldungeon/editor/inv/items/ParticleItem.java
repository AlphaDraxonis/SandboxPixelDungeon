package com.shatteredpixel.shatteredpixeldungeon.editor.inv.items;

import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditParticleComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.EditorInventoryWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.WndEditorInv;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.CustomParticle;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.DefaultListItemWithRemoveBtn;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.ParticleActionPart;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemSlot;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.watabou.noosa.Image;

public class ParticleItem extends EditorItem<CustomParticle.ParticleProperty> {

    public ParticleItem(CustomParticle.ParticleProperty particle) {
        this.obj = particle;
    }

    @Override
    public DefaultEditComp<?> createEditComponent() {
        return new EditParticleComp(getObject());
    }

    @Override
    public String name() {
        return getObject().name;
    }

    @Override
    public Image getSprite() {
        return getObject().getSprite();
    }

    @Override
    public Item getCopy() {
        return new ParticleItem(getObject().getCopy());
    }

    @Override
    public ScrollingListPane.ListItem createListItem(EditorInventoryWindow window) {
        return new DefaultListItemWithRemoveBtn(this, window, name(), getSprite()) {
            @Override
            protected void onRemove() {
                CustomParticle.deleteParticle(getObject().particleID());
                WndEditorInv.updateCurrentTab();

                ItemSlot slot = QuickSlotButton.containsItem(ParticleItem.this);
                if (slot != null) slot.item(null);
            }
        };
    }

    @Override
    public void place(int cell) {
        if (!invalidPlacement(cell)) {
            Undo.addActionPart(place(getObject().particleID(), cell));
        }
    }

    public static boolean invalidPlacement(int cell) {
        return false;
//        return Dungeon.level.solid[cell];
    }

    public static ActionPart remove(int cell) {
        ParticleActionPart.Modify part = new ParticleActionPart.Modify(cell);
        ParticleActionPart.clearCell(cell);
        part.finish();
        if (part.hasContent()) return part;
        return null;
    }

    public static ActionPart place(int particleID, int cell) {
        ParticleActionPart.Modify part = new ParticleActionPart.Modify(cell);
        ParticleActionPart.place(cell, particleID);
        part.finish();
        if (part.hasContent()) return part;
        return null;
    }
}