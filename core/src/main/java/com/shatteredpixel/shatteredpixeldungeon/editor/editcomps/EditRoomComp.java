package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.RoomItem;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
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
        return "Description unavailable atm";
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