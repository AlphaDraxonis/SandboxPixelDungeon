package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.editor.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.BarrierItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledCheckBox;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemSlot;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class EditBarrierComp extends DefaultEditComp<Barrier> {


    protected StyledCheckBox visible;

    private final BarrierItem barrierItem;//used for linking the item with the sprite in the toolbar

    private Component[] comps;

    public EditBarrierComp(Barrier item) {
        super(item);
        initComps();
        barrierItem = null;
    }

    public EditBarrierComp(BarrierItem barrierItem) {
        super(barrierItem.getObject());
        initComps();
        this.barrierItem = barrierItem;
    }

    private void initComps() {
        comps = new Component[Barrier.NUM_BLOCK_TYPES + 1];
        for (int i = 0; i < Barrier.NUM_BLOCK_TYPES; i++) {
            final int bit = (int) Math.pow(2, i);
            StyledCheckBox cb = new StyledCheckBox(Messages.get(Barrier.class, "block_" + Barrier.getBlockKey(bit))) {
                {
                    super.checked((obj.blocks & bit) != 0);
                }

                @Override
                public void checked(boolean value) {
                    super.checked(value);
                    if (((obj.blocks & bit) == 0) == value) {
                        if (value) obj.blocks |= bit;
                        else obj.blocks -= bit;
                        updateObj();
                    }
                }
            };
            add(comps[i] = cb);
        }

        visible = new StyledCheckBox(Messages.get(EditBarrierComp.class, "visible")) {
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

        comps[Barrier.NUM_BLOCK_TYPES] = visible;
    }

    @Override
    protected void layout() {
        super.layout();
        layoutCompsInRectangles(comps);
    }

    @Override
    protected Component createTitle() {
        return new IconTitle(getIcon(), BarrierItem.createTitle(obj));
    }

    @Override
    protected String createDescription() {
        return obj.desc();
    }

    @Override
    public Image getIcon() {
        return BarrierItem.getBarrierImage(obj);
    }

    @Override
    protected void updateObj() {

        for (int i = 0; i < Barrier.NUM_BLOCK_TYPES; i++) {
            final int bit = (int) Math.pow(2, i);
            ((StyledCheckBox) comps[i]).checked((obj.blocks & bit) != 0);
        }
        visible.checked(obj.visible);

        if (title instanceof IconTitle) {
            ((IconTitle) title).label(BarrierItem.createTitle(obj));
            ((IconTitle) title).icon(BarrierItem.getBarrierImage(obj));
        }
        desc.text(createDescription());

        if (barrierItem != null) {
            ItemSlot slot = QuickSlotButton.containsItem(barrierItem);
            if (slot != null) slot.item(barrierItem);
        }

        if (obj.pos != -1) EditorScene.updateMap(obj.pos);

        super.updateObj();
    }


    public static boolean areEqual(Barrier a, Barrier b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        if (a.getClass() != b.getClass()) return false;
        if (a.pos != b.pos) return false;
        if (a.blocks != b.blocks) return false;
        return a.visible == b.visible;
    }
}