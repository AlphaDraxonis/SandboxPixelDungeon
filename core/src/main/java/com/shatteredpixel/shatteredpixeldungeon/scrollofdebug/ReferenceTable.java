package com.shatteredpixel.shatteredpixeldungeon.scrollofdebug;

import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndEditorSettings;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.MultiWindowTabComp;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.inspector.ObjInspector;
import com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.references.Reference;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

import java.util.*;

public class ReferenceTable extends MultiWindowTabComp {

    //A long list of all available references
    //Show icon: -> use EditorItem methods to get image
    //for items = their itemsprite (TODO what about rings?)
    //for mobs = their mobsprite
    //Show Type
    //Show name
    //Example:
    //[new RatSprite()] Rat: _fetteRatte_

    //A button to add one more reference(TabbedWindow Dialog, option for 1 of 4 way:
    // select constr like inv, call macro, custom one liner code, define new primitive/String)

    private List<ReferenceListItem> items;

    @Override
    protected void createChildren(Object... params) {

        super.createChildren(params);

        title = new IconTitle(new ItemSprite(ItemSpriteSheet.KIT), Messages.get(this, "title"));
        add(title);

        items = new ArrayList<>();
        Set<Reference> toRemove = new HashSet<>(3);
        System.gc();
        for (Reference r : WndScrollOfDebug.references) {
//            if (r instanceof DynamicReference && ((DynamicReference) r).hasNoReference()) toRemove.add(r);
//            else  tzz maybe add logic to handle invalid references!
                addReferenceToUI(r);
        }
        WndScrollOfDebug.references.removeAll(toRemove);
    }

    public void addReferenceToUI(Reference reference) {
        ReferenceListItem item = new ReferenceListItem(reference);
        items.add(item);
        content.add(item);
    }

    private final LinkedList<Reference> lastInspectedObjects = new LinkedList<>();
    private final LinkedList<Float> lastInspectedObjectsScrollPos = new LinkedList<>();
    private Reference currentReference;

    public void inspectObj(Reference reference) {
        if (!backPressed && currentReference != null) {
            lastInspectedObjects.add(currentReference);
            lastInspectedObjectsScrollPos.add(otherBody.camera().scroll.y);
        }
        ObjInspector objInspector = new ObjInspector(reference) {
            @Override
            protected void updateParentLayout() {
                ReferenceTable.this.layout();
            }

            @Override
            protected void showDifferentInspectObj(Reference reference) {
                inspectObj(reference);
            }

            @Override
            protected void scrollTo(float x, float y) {
                spForOtherBody.scrollTo(x, y);
            }
        };
        currentReference = reference;
        changeContent(objInspector.createTitle(), objInspector, objInspector.getOutsideSp(), 0, 0);
        if (backPressed) {
            objInspector.selectFieldsTab();
            spForOtherBody.scrollTo(0, lastInspectedObjectsScrollPos.removeLast());
        }
    }


    protected void layoutOwnContent() {
        float pos = 0;
        for (Component item : items) {
            item.setRect(0, pos, width, WndEditorSettings.ITEM_HEIGHT);
            pos += item.height();
        }
        content.setSize(width, pos);
    }

    public Component getOutsideSp() {
        return new RedButton(Messages.get(this, "add_reference")) {
            @Override
            protected void layout() {
                height = 18;
                super.layout();
            }
        };
    }

    @Override
    public Image createIcon() {
        return new ItemSprite(ItemSpriteSheet.KIT);
    }

    @Override
    public String hoverText() {
        return Messages.get(ReferenceTable.class, "title");
    }

    private class ReferenceListItem extends ScrollingListPane.ListItem {

        private final Reference reference;

        public ReferenceListItem(Reference reference) {
            super(reference.createIcon(), reference.getType().getSimpleName() + ": " + "_" + reference.getName() + "_");
//            super(reference.createIcon(), reference.getType().getSimpleName() + ": " + RenderedTextBlock.MARKER + reference.getName() + RenderedTextBlock.MARKER);
            label.setHighlighting(false);
            this.reference = reference;
        }

        @Override
        protected void onClick() {
            inspectObj(reference);
        }
    }

    private boolean backPressed;
    @Override
    public void closeCurrentSubMenu() {
        backPressed = true;
        super.closeCurrentSubMenu();

        if (!lastInspectedObjects.isEmpty()) {
            inspectObj(lastInspectedObjects.removeLast());
        } else {
            currentReference = null;
            layout();
        }
        backPressed = false;
    }
}