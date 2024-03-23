package com.shatteredpixel.shatteredpixeldungeon.editor.inv.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditTrapComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.TrapActionPart;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class TrapItem extends EditorItem<Trap> {

    public TrapItem(){}
    public TrapItem(Trap trap) {
        this.obj = trap;
    }

    @Override
    public DefaultEditComp<?> createEditComponent() {
        return new EditTrapComp(this);
    }

    @Override
    public String name() {
        return getObject().name();
    }

    @Override
    public Image getSprite() {
        return getObject().getSprite();
    }

    @Override
    public Item getCopy() {
        return new TrapItem(getObject().getCopy());
    }

    @Override
    public void setObject(Trap obj) {
        Trap copy = obj.getCopy();
        copy.pos = -1;
        super.setObject(copy);
    }

    @Override
    public void place(int cell) {
        Trap place = getObject().getCopy();
        Trap remove = Dungeon.level.traps.get(cell);

        if (!invalidPlacement(cell) && !EditTrapComp.areEqual(remove, place)) {
            Undo.addActionPart(remove(remove));
            Undo.addActionPart(place(place, cell));
        }
    }

    public static boolean invalidPlacement(int cell) {
        return !Dungeon.level.insideMap(cell);
    }

    public static int getTerrain(Trap trap) {
        return trap.visible ? (trap.active ? Terrain.TRAP : Terrain.INACTIVE_TRAP) : Terrain.SECRET_TRAP;
    }

    public static TrapActionPart.Remove remove(Trap trap) {
        if (trap != null) {
            return new TrapActionPart.Remove(trap);
        }
        return null;
    }

    public static TrapActionPart.Place place(Trap trap, int cell) {
        if (trap != null) {
            trap.pos = cell;
            return new TrapActionPart.Place(trap);
        }
        return null;
    }

    private static final String TRAP = "trap";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(TRAP, obj);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        obj = (Trap) bundle.get(TRAP);
    }
}