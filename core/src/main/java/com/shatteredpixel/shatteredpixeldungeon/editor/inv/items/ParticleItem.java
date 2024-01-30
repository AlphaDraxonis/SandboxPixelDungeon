package com.shatteredpixel.shatteredpixeldungeon.editor.inv.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditParticleComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.DefaultListItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.EditorInventoryWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.CustomParticle;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.ParticleActionPart;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.watabou.noosa.Image;

public class ParticleItem extends EditorItem<CustomParticle.ParticleProperty> {

    public ParticleItem(CustomParticle.ParticleProperty particle) {
        this.obj = particle;
    }

    @Override
    public ScrollingListPane.ListItem createListItem(EditorInventoryWindow window) {
        return new DefaultListItem(this, window, name(), getSprite());
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
    public void place(int cell) {
        if (!invalidPlacement(cell)) {
            Undo.addActionPart(place(getObject().particleID(), cell));
        }
    }

    public static boolean invalidPlacement(int cell) {
        return Dungeon.level.solid[cell];
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