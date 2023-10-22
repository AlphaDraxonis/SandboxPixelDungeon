package com.alphadraxonis.sandboxpixeldungeon.editor.editcomps;

import com.alphadraxonis.sandboxpixeldungeon.actors.blobs.Blob;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.items.BlobItem;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.windows.IconTitle;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class EditBlobComp extends DefaultEditComp<Class<? extends Blob>> {

    public EditBlobComp(Class<? extends Blob> item) {
        super(item);
    }

    @Override
    protected Component createTitle() {
        return new IconTitle(getIcon(), createTitleText());
    }

    protected String createTitleText() {
        return BlobItem.createName(obj);
    }

    @Override
    protected String createDescription() {
        return Messages.get(obj, "desc");
    }

    @Override
    public Image getIcon() {
        return BlobItem.createIcon(obj);
    }

    @Override
    protected void updateObj() {
        if (title instanceof IconTitle) {
            ((IconTitle) title).label(createTitleText());
            ((IconTitle) title).icon(getIcon());
        }
        desc.text(createDescription());
        super.updateObj();
    }
}