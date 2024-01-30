package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.RoomItem;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class EditRoomComp extends DefaultEditComp<Room> {

    private Component[] comps;

    public EditRoomComp(Room item) {
        super(item);
        initComps();
    }

    public EditRoomComp(RoomItem roomItem) {
        super(roomItem.room());
        initComps();
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
    protected String createTitleText() {
        return Messages.titleCase(RoomItem.getName(obj.getClass()));
    }

    @Override
    protected String createDescription() {
        return RoomItem.getDesc(obj.getClass());
    }

    @Override
    public Image getIcon() {
        return RoomItem.getImage(obj.getClass());
    }


    public static boolean areEqual(Room a, Room b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        if (a.getClass() != b.getClass()) return false;
        return true;
    }
}