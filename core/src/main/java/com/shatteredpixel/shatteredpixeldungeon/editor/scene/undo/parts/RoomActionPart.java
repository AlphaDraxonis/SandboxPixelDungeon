package com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts;

import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditRoomComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPartModify;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;

public /*sealed*/ abstract class RoomActionPart implements ActionPart {

    private RoomActionPart() {
    }

    @Override
    public boolean hasContent() {
        return true;
    }

    public static final class Modify implements ActionPartModify {

        private final Room realRoom;
        private final Room before;
        private Room after;

        public Modify(Room room) {
            before = room.getCopy();
            realRoom = room;
        }

        @Override
        public void undo() {
            if (realRoom != null) realRoom.copyStats(before);
        }

        @Override
        public void redo() {
            if (realRoom != null) realRoom.copyStats(after);
        }

        @Override
        public boolean hasContent() {
            return !EditRoomComp.areEqual(before, after);
        }

        @Override
        public void finish() {
            after = realRoom.getCopy();
        }
    }
}