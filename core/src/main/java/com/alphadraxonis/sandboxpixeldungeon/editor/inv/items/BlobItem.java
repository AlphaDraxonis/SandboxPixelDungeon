package com.alphadraxonis.sandboxpixeldungeon.editor.inv.items;

import com.alphadraxonis.sandboxpixeldungeon.actors.blobs.Blob;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.DefaultEditComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.DefaultListItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.EditorInventoryWindow;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomLevel;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.ActionPart;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.Undo;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.parts.BlobEditPart;
import com.alphadraxonis.sandboxpixeldungeon.ui.Icons;
import com.alphadraxonis.sandboxpixeldungeon.ui.ScrollingListPane;
import com.watabou.noosa.Image;

public class BlobItem extends EditorItem {

    private final Class<? extends Blob> blob;


    public BlobItem(Class<? extends Blob> blob) {
        this.blob = blob;
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
        return null;//Edit duration and ?reward?
//        return new EditBlobComp(blob(), null);
    }

    @Override
    public Image getSprite() {
        return Icons.GOLD.get();
//        return CustomDungeon.getDungeon().getBlobImage(blob());
    }

    @Override
    public void place(int cell) {

        CustomLevel level = EditorScene.customLevel();

        if (invalidPlacement(cell, level)) return;

        Undo.addActionPart(place(blob(), cell));
    }

    @Override
    public Object getObject() {
        return blob();
    }

    public Class<? extends Blob> blob() {
        return blob;
    }

    @Override
    public String name() {
        return "TESTBLOBNAME";
    }

    public static boolean invalidPlacement(int cell, CustomLevel level) {
//        return level.passable[cell];
        return level.solid[cell] || !level.insideMap(cell);
    }

    public static ActionPart remove(int cell) {
        BlobEditPart.Modify part = new BlobEditPart.Modify(cell);
        BlobEditPart.clearNormalAtCell(cell);
        part.finish();
        if (part.hasContent()) return part;
        return null;
    }

    public static ActionPart place(Class<? extends Blob> blob, int cell) {
        BlobEditPart.Modify part = new BlobEditPart.Modify(cell);
        BlobEditPart.place(cell, blob, 1);
        part.finish();
        if (part.hasContent()) return part;
        return null;
    }
}