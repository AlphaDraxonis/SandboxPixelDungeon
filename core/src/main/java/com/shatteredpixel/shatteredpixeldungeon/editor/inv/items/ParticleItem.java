package com.shatteredpixel.shatteredpixeldungeon.editor.inv.items;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditParticleComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.DefaultListItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.EditorInventoryWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.CustomParticle;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.ParticleActionPart;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.watabou.noosa.Image;
import com.watabou.utils.RectF;

public class ParticleItem extends EditorItem<CustomParticle.ParticleProperty> {

    private int pos;

    public ParticleItem(CustomParticle.ParticleProperty particle, int pos) {
        this.obj = particle;
        this.pos = pos;
    }

    @Override
    public ScrollingListPane.ListItem createListItem(EditorInventoryWindow window) {
        return new DefaultListItem(this, window, name(), getSprite()) {
            @Override
            public void onUpdate() {
                super.onUpdate();
            }
        };
    }

    @Override
    public DefaultEditComp<?> createEditComponent() {
        return new EditParticleComp(getObject());
    }

    @Override
    public Image getSprite() {
        return createIcon(getObject());
    }

    @Override
    public void place(int cell) {

        CustomLevel level = EditorScene.customLevel();

        if (invalidPlacement(cell, level)) return;

        Undo.addActionPart(place(getObject().particleID(), cell));
    }

    @Override
    public String name() {
        return getObject().name;
    }

    public static Image createIcon(CustomParticle.ParticleProperty particle) {
        RectF r = Speck.getFilm().get(particle.type);
        if (r == null) return new ItemSprite();
        Image itemIcon = new Image(Assets.Effects.SPECKS);
        itemIcon.frame(r);
        return itemIcon;
    }

    public static boolean invalidPlacement(int cell, CustomLevel level) {
        return level.solid[cell];
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