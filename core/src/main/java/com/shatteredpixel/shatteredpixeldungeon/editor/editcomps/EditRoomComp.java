package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObjectManager;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.interfaces.CustomGameObjectClass;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.RoomItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemContainerWithLabel;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class EditRoomComp extends DefaultEditComp<Room> {

    private Component[] comps;

    protected ItemContainerWithLabel<Item> spawnItemsInRoom;
    protected ItemContainerWithLabel<Item> spawnItemsOnLevel;

    public EditRoomComp(Room item) {
        super(item);
        initComps();
    }

    public EditRoomComp(RoomItem roomItem) {
        super(roomItem.room());
        initComps();
    }

    private void initComps() {

//        spawnItemsInRoom = new ItemContainerWithLabel<Item>(obj.spawnItemsInRoom, Messages.get(HeroSettings.class, "spawn_items_in_room")) {
//
//            @Override
//            protected void onSlotNumChange() {
//                if (spawnItemsInRoom != null) {
//                    updateObj();
//                }
//            }
//        };
//        add(spawnItemsInRoom);

        spawnItemsOnLevel = new ItemContainerWithLabel<Item>(obj.spawnItemsOnLevel, Messages.get(EditRoomComp.class, "spawn_items_on_level")) {

            @Override
            protected void onSlotNumChange() {
                if (spawnItemsOnLevel != null) {
                    updateObj();
                }
            }
        };
        add(spawnItemsOnLevel);

        comps = new Component[]{spawnItemsInRoom, spawnItemsOnLevel};

        initializeCompsForCustomObjectClass();
    }

    @Override
    protected void updateStates() {
        super.updateStates();
        if (spawnItemsOnLevel != null) spawnItemsOnLevel.setItemList(obj.spawnItemsOnLevel);
    }

    @Override
    protected void onInheritStatsClicked(boolean flag, boolean initializing) {
        if (flag && !initializing) {
            obj.copyStats((Room) CustomObjectManager.getLuaClass(((CustomGameObjectClass) obj).getIdentifier()));
        }

        for (Component c : comps) {
            if (c != null) c.setVisible(!flag);
        }

        if (rename != null) rename.setVisible(!flag);

        ((CustomGameObjectClass) obj).setInheritStats(flag);
//        if (viewScript != null) viewScript.visible = viewScript.active = true;
    }

    @Override
    protected void layout() {
        super.layout();
        layoutCompsLinear(comps);

        layoutCustomObjectEditor();
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
        if (a == b) return true;
        if (a == null || b == null) return false;
        if (a.getClass() != b.getClass()) return false;

        if (a.itemsGenerated != b.itemsGenerated) return false;
        if (!EditItemComp.isItemListEqual(a.spawnItemsInRoom, b.spawnItemsInRoom)) return false;
        if (!EditItemComp.isItemListEqual(a.spawnItemsOnLevel, b.spawnItemsOnLevel)) return false;

        return true;
    }
}