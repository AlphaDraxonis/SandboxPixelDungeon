package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.editor.ArrowCell;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.ArrowCellItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledCheckBox;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemSlot;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class EditArrowCellComp extends DefaultEditComp<ArrowCell> {


    protected StyledCheckBox visible;

    private final ArrowCellItem arrowCellItem;//used for linking the item with the sprite in the toolbar

    private Component[] comps;

    public EditArrowCellComp(ArrowCell item) {
        super(item);
        initComps();
        arrowCellItem = null;
    }

    public EditArrowCellComp(ArrowCellItem arrowCellItem) {
        super(arrowCellItem.getObject());
        initComps();
        this.arrowCellItem = arrowCellItem;
    }

    private void initComps() {
        comps = new Component[1];

        visible = new StyledCheckBox(Messages.get(EditTrapComp.class, "visible")) {
            @Override
            public void checked(boolean value) {
                super.checked(value);
                if (obj.visible != value) {
                    obj.visible = value;
                    updateObj();
                }
            }
        };
        visible.icon(new BuffIcon(BuffIndicator.FORESIGHT, true));
        visible.checked(obj.visible);
        add(visible);

        comps[0] = visible;
    }

    @Override
    protected void layout() {
        super.layout();
        layoutCompsInRectangles(comps);
    }

    @Override
    protected String createTitleText() {
        return Messages.titleCase(obj.name());
    }

    @Override
    protected String createDescription() {
        return obj.desc();
    }

    @Override
    public Image getIcon() {
        return obj.getSprite();
    }

    @Override
    protected void updateObj() {

//        for (int i = 0; i < ArrowCell.NUM_BLOCK_TYPES; i++) {
//            final int bit = (int) Math.pow(2, i);
//            ((StyledCheckBox) comps[i]).checked((obj.blocks & bit) != 0);
//        }
        visible.checked(obj.visible);

        if (arrowCellItem != null) {
            ItemSlot slot = QuickSlotButton.containsItem(arrowCellItem);
            if (slot != null) slot.item(arrowCellItem);
        }

        if (obj.pos != -1) EditorScene.updateMap(obj.pos);

        super.updateObj();
    }


    public static boolean areEqual(ArrowCell a, ArrowCell b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        if (a.getClass() != b.getClass()) return false;
        if (a.pos != b.pos) return false;
//        if (a.directionsLeave != b.directionsLeave) return false;
        if (a.directionsEnter != b.directionsEnter) return false;
        if (a.allowsWaiting != b.allowsWaiting) return false;
        return a.visible == b.visible;
    }
}