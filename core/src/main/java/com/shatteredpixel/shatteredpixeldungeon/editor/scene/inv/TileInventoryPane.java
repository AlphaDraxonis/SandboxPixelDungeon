package com.shatteredpixel.shatteredpixeldungeon.editor.scene.inv;

import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.ui.InventoryPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.InventorySlot;

public class TileInventoryPane extends InventoryPane {


    @Override
    protected void centerNextWndOnInvPane() {
        EditorScene.centerNextWndOnInvPane();
    }

    @Override
    protected InventorySlot createInventorySlot(Item item) {
        return new TileInventoryPaneSlot((TileItem) item);
    }

    private static class TileInventoryPaneSlot extends InventorySlot {

        private TileInventoryPaneSlot(TileItem item) {
            super(item);
        }

        @Override
        protected void onClick() {
//            if (lastBag != item && !lastBag.contains(item) && !item.isEquipped(Dungeon.hero)) {
//                updateInventory();
//                return;
//            }
//
//            if (targeting){
//                if (targetingSlot == this){
//                    int cell = QuickSlotButton.autoAim(lastTarget, item());
//
//                    if (cell != -1){
//                        GameScene.handleCell(cell);
//                    } else {
//                        //couldn't auto-aim, just target the position and hope for the best.
//                        GameScene.handleCell( lastTarget.pos );
//                    }
//                    return;
//                } else {
//                    cancelTargeting();
//                }
//            }

//            //any windows opened as a consequence of this button should be centered on the inventory
//            centerNextWndOnInvPane();
//            if (selector != null) {
//                WndBag.ItemSelector activating = selector;
//                selector = null;
//                activating.onSelect( item );
//                updateInventory();
//            } else {
//                targetingSlot = this;
//                GameScene.show(new WndUseItem( null, item ));
//            }
        }

        @Override
        protected void onMiddleClick() {
//            if (lastBag != item && !lastBag.contains(item) && !item.isEquipped(Dungeon.hero)){
//                updateInventory();
//                return;
//            }
//
//            if (!Dungeon.hero.isAlive() || !Dungeon.hero.ready){
//                return;
//            }
//
//            if (targeting){
//                if (targetingSlot == this){
//                    onClick();
//                }
//                return;
//            }
//
//            if (selector == null && item.defaultAction() != null){
//                item.execute(Dungeon.hero);
//                if (item.usesTargeting) {
//                    targetingSlot = this;
//                    InventoryPane.useTargeting();
//                }
//            } else {
//                onClick();
//            }
        }

        @Override
        protected void onRightClick() {
//            if (lastBag != item && !lastBag.contains(item) && !item.isEquipped(Dungeon.hero)){
//                updateInventory();
//                return;
//            }
//
//            if (!Dungeon.hero.isAlive() || !Dungeon.hero.ready){
//                return;
//            }
//
//            if (targeting){
//                //do nothing
//                return;
//            }
//
//            if (selector == null){
//                targetingSlot = this;
//                RightClickMenu r = new RightClickMenu(item);
//                parent.addToFront(r);
//                r.camera = camera();
//                PointF mousePos = PointerEvent.currentHoverPos();
//                mousePos = camera.screenToCamera((int)mousePos.x, (int)mousePos.y);
//                r.setPos(mousePos.x-3, mousePos.y-3);
//            } else {
//                //do nothing
//            }
        }
    }
}