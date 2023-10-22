package com.alphadraxonis.sandboxpixeldungeon.editor.editcomps;

import com.alphadraxonis.sandboxpixeldungeon.editor.inv.items.RoomItem;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.Room;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.Trap;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.windows.IconTitle;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class EditRoomComp extends DefaultEditComp<Room> {

    private final RoomItem roomItem;//used for linking the item with the sprite in the toolbar

    private Component[] comps;

    public EditRoomComp(Room item) {
        super(item);
        initComps();
        roomItem = null;
    }

    public EditRoomComp(RoomItem roomItem) {
        super(roomItem.room());
        initComps();
        this.roomItem = roomItem;
    }

    private void initComps() {
        comps = new Component[0];
    }

    @Override
    protected void layout() {
        super.layout();
        layoutCompsLinear(comps);
    }

    @Override
    protected Component createTitle() {
        return new IconTitle(getIcon(), Messages.titleCase(RoomItem.getName(obj.getClass())));
    }

    @Override
    protected String createDescription() {
        return RoomItem.getDesc(obj.getClass());
    }

    @Override
    public Image getIcon() {
        return RoomItem.getImage(obj.getClass());
    }

    @Override
    protected void updateObj() {
        if (title instanceof IconTitle) {
            ((IconTitle) title).label(Messages.titleCase(RoomItem.getName(obj.getClass())));
//            ((IconTitle) title).icon(RoomItem.getImage(obj.getClass()));
        }
        desc.text(createDescription());

        super.updateObj();
    }


    public static boolean areEqual(Trap a, Trap b) {
        if (a == null || b == null) return false;
        if (a.getClass() != b.getClass()) return false;
        if (a.visible != b.visible) return false;
        return a.active == b.active;
    }
}