package com.alphadraxonis.sandboxpixeldungeon.editor.inv.items;

import com.alphadraxonis.sandboxpixeldungeon.actors.blobs.Blob;
import com.alphadraxonis.sandboxpixeldungeon.actors.blobs.SacrificialFire;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.DefaultEditComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.EditBlobComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.DefaultListItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.EditorInventoryWindow;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomLevel;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.ActionPart;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.Undo;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.parts.BlobEditPart;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.special.MagicalFireRoom;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSprite;
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
        return new EditBlobComp(blob());//Edit duration and ?reward?
    }

    @Override
    public Image getSprite() {
        return createIcon(blob());
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
        return createName(blob());
    }

    public static String createName(Class<? extends Blob> blob) {
        return Messages.get(BlobItem.class, blob.getSimpleName());
    }

    public static Image createIcon(Class<? extends Blob> blob) {
        if (blob == MagicalFireRoom.EternalFire.class) {
            Image icon = Icons.ETERNAL_FIRE.get();
            icon.scale.set(2.28f);//16/7=2.28
            return icon;
        } else if (blob == SacrificialFire.class) {
            Image icon = Icons.SACRIFICIAL_FIRE.get();
            icon.scale.set(2.28f);//16/7=2.28
            return icon;
        }
        return new ItemSprite();
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