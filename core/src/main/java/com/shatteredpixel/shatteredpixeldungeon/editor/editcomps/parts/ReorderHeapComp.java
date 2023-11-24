package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts;

import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditCompWindowTabbed;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.ui.Component;

public class ReorderHeapComp extends Component {

    private IconButton moveLeft, moveRight;

    private final Item item;
    private final Heap heap;


    public boolean layoutRTL = true;

    public ReorderHeapComp(Item item, Heap heap) {
        this.item = item;
        this.heap = heap;
    }


    @Override
    protected void createChildren(Object... params) {

        moveLeft = new IconButton(Icons.LEFT.get()) {
            @Override
            protected void onClick() {
                swapItems(-1);
            }

            @Override
            protected String hoverText() {
                return Messages.get(ReorderHeapComp.class, "move_left_tooltip");
            }
        };
        add(moveLeft);

        moveRight = new IconButton(Icons.RIGHT.get()) {
            @Override
            protected void onClick() {
                swapItems(1);
            }

            @Override
            protected String hoverText() {
                return Messages.get(ReorderHeapComp.class, "move_right_tooltip");
            }
        };
        add(moveRight);
    }

    @Override
    protected void layout() {

        updateEnableState();

        float posX = x;

        if (layoutRTL) {
            float iconWith = moveRight.icon().width();
            if (moveRight.visible) {
                moveRight.setRect(posX - iconWith, y + (height - iconWith) * 0.5f, iconWith, moveRight.icon().height());
                posX -= iconWith + 2;
            }

            iconWith = moveLeft.icon().width();
            if (moveLeft.visible) {
                moveLeft.setRect(posX - iconWith, y + (height - iconWith) * 0.5f, iconWith, moveLeft.icon().height());
                posX -= iconWith + 2;
            }
            width = x - posX + 2;
        } else {
            float iconWith = moveLeft.icon().width();
            if (moveLeft.visible) {
                moveLeft.setRect(posX, y + (height - iconWith) * 0.5f, iconWith, moveLeft.icon().height());
                posX += iconWith + 2;
            }
            iconWith = moveRight.icon().width();
            if (moveRight.visible) {
                moveRight.setRect(posX, y + (height - iconWith) * 0.5f, iconWith, moveRight.icon().height());
                posX += iconWith + 2;
            }
            width = posX - x - 2;
        }

    }

    @Override
    public float right() {
        return layoutRTL ? x : super.right();
    }

    @Override
    public float left() {
        return layoutRTL ? x - width : super.left();
    }

    public void updateEnableState(){
        boolean isItemAtTop = heap.items.peek() == item;
        boolean isItemAtBottom = heap.items.peekLast() == item;

        moveLeft.enable(!isItemAtTop);
        moveRight.enable(!isItemAtBottom);
    }

    //need to make sure that item at index actually exists!
    private void swapItems(int direction) {
        Window w = EditorUtilies.getParentWindow(this);
        if (w instanceof EditCompWindowTabbed) {
            Item other = heap.items.get(heap.items.indexOf(item) + direction);

            int index1 = heap.items.indexOf(item);
            int index2 = heap.items.indexOf(other);
            heap.items.remove(item);
            heap.items.add(index2, item);
            heap.items.remove(other);
            heap.items.add(index1, other);

            EditorScene.updateHeapImage(heap);
            heap.updateSubicon();
            ((EditCompWindowTabbed) w).swapItemTabs(index1, item, index2, other, heap);

            updateEnableState();
        }
    }
}