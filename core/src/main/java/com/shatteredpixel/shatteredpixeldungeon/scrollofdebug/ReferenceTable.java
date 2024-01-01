package com.shatteredpixel.shatteredpixeldungeon.scrollofdebug;

import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndEditorSettings;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.MultiWindowTabComp;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;
import java.util.List;

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

    private List<ReferenceItem> items;

    @Override
    protected void createChildren(Object... params) {

        super.createChildren(params);

        title = new IconTitle(Icons.get(Icons.PREFS), Messages.get(ReferenceTable.class, "title"));
        add(title);

        items = new ArrayList<>();
        for (Reference r : WndScrollOfDebug.superGlobalReferences.values()) {
            addReferenceToUI(r);
        }
    }

    public void addReferenceToUI(Reference reference) {
        ReferenceItem item = new ReferenceItem(reference);
        items.add(item);
        content.add(item);
    }

    protected void layoutOwnContent() {
        float pos = 0;
        for (Component item : items) {
            item.setRect(0, pos, width, WndEditorSettings.ITEM_HEIGHT);
            pos += item.height();
        }
        content.setSize(width, pos);
    }

    public static void updateLayout() {
        WndScrollOfDebug.getInstance().getReferenceTable().layout();
    }

    @Override
    public Image createIcon() {
        return new ItemSprite(ItemSpriteSheet.KIT);
    }

    @Override
    public String hoverText() {
        return Messages.get(ReferenceTable.class, "title");
    }

    private class ReferenceItem extends ScrollingListPane.ListItem {

        private final Reference reference;

        public ReferenceItem(Reference reference) {
            super(reference.createIcon(), reference.getType().getSimpleName() + ": _" + reference.getName() + "_");
            this.reference = reference;
        }

        @Override
        protected void onClick() {
            InspectObj inspectObj = new InspectObj(reference) {
                @Override
                protected void updateParentLayout() {
                    ReferenceTable.this.layout();
                }
            };
            changeContent(inspectObj.createTitle(), inspectObj, inspectObj.getOutsideSp());
        }
    }
}