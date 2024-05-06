package com.shatteredpixel.shatteredpixeldungeon.editor.inv.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.ArrowCell;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditArrowCellComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.ArrowCellActionPart;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.watabou.noosa.Image;

public class ArrowCellItem extends EditorItem<ArrowCell> {

    public ArrowCellItem() {
    }

    public ArrowCellItem(ArrowCell arrowCell) {
        this.obj = arrowCell;
    }

    @Override
    public DefaultEditComp<?> createEditComponent() {
        return new EditArrowCellComp(this);
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
        return new ArrowCellItem(getObject().getCopy());
    }

    @Override
    public void setObject(ArrowCell obj) {
        ArrowCell copy = obj.getCopy();
        copy.pos = -1;
        super.setObject(copy);
    }

    @Override
    public void place(int cell) {
        if (!invalidPlacement(cell)) {
            Undo.addActionPart(remove(cell));
            Undo.addActionPart(place(getObject().getCopy(), cell));
        }
    }

    public static boolean invalidPlacement(int cell) {
        return !Dungeon.level.insideMap(cell);//can be placed on walls because these might be destroyed by the player
    }

    public static ActionPart remove(int cell) {
        ArrowCell arrowCell = Dungeon.level.arrowCells.get(cell);
        if (arrowCell != null) {
            return new ArrowCellActionPart.Remove(arrowCell);
        }
        return null;
    }


    public ActionPart place(ArrowCell arrowCell, int cell) {
        if (arrowCell != null && !EditArrowCellComp.areEqual(Dungeon.level.arrowCells.get(cell), arrowCell)) {
            arrowCell.pos = cell;
            return new ArrowCellActionPart.Place(arrowCell);
        }
        return null;
    }
}