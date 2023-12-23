package com.shatteredpixel.shatteredpixeldungeon.editor.inv.items;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditMobComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.DefaultListItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.EditorInventoryWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.MobActionPart;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class MobItem extends EditorItem<Mob> {

    public MobItem() {
    }

    public MobItem(Mob mob) {
        this.obj = mob;
    }

    @Override
    public ScrollingListPane.ListItem createListItem(EditorInventoryWindow window) {
        return new DefaultListItem(this, window, getObject().name(), getSprite()) {
            @Override
            public void onUpdate() {
                if (item == null) return;

                if (icon != null) remove(icon);
                icon = getObject().sprite();
                addToBack(icon);
                remove(bg);
                addToBack(bg);

                super.onUpdate();
            }
        };
    }

    @Override
    public DefaultEditComp<?> createEditComponent() {
        return new EditMobComp(getObject());
    }

    @Override
    public Image getSprite() {
        Mob mob = getObject();
        if (mob.sprite == null) mob.sprite = mob.sprite();
        return getObject().sprite();
    }

    @Override
    public void place(int cell) {
        CustomLevel level = EditorScene.customLevel();
        Mob mob = (Mob) getObject().getCopy();

        Mob mobAtCell = level.findMob(cell);
        if (invalidPlacement(mob, level, cell) || EditMobComp.areEqual(mob, mobAtCell)) return;

        Undo.addActionPart(remove(mobAtCell));

        Undo.addActionPart(place(mob, cell));
    }

    @Override
    public String name() {
        return getObject().name();
    }

    @Override
    public Item getCopy() {
        return new MobItem((Mob) getObject().getCopy());
    }

    public static boolean invalidPlacement(Mob mob, CustomLevel level, int cell) {
        return level.solid[cell] || (level.pit[cell] && !mob.flying) || !level.insideMap(cell)
                || (Char.hasProp(mob, Char.Property.LARGE) && !level.openSpace[cell])
//                || (mob instanceof Piranha && !level.water[cell])
                ;//&& level.map[cell] != Terrain.DOOR;//TODO make placement on doors possible FIXME WICHTIG
    }

    public static MobActionPart.Remove remove(Mob mob) {
        if (mob != null) {
            return new MobActionPart.Remove(mob);
        }
        return null;
    }

    public static MobActionPart.Place place(Mob mob) {
        if (mob != null && !EditMobComp.areEqual(mob, EditorScene.customLevel().findMob(mob.pos)))
            return new MobActionPart.Place(mob);
        return null;
    }

    private static MobActionPart.Place place(Mob mob, int cell) {
        if (mob != null) {//Achtung! no check for equality!
            mob.pos = cell;
            return new MobActionPart.Place(mob);
        }
        return null;
    }


    private static final String MOB = "mob";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(MOB, obj);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        obj = (Mob) bundle.get(MOB);
    }

    public Mob mob() {
        return getObject();
    }
}