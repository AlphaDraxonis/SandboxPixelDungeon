package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.editor.ArrowCell;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.ArrowCellItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledCheckBox;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerEnumModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerLikeButton;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemSlot;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class EditArrowCellComp extends DefaultEditComp<ArrowCell> {


    protected StyledCheckBox[] directions;
    protected SpinnerLikeButton enterMode;
    protected StyledCheckBox visible;
    protected StyledCheckBox[] affects;

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

        directions = new StyledCheckBox[9];
        for (int i = 0; i < directions.length; i++) {
            if (i != 4) {
                int bit = 1 << (i - (i>4?1:0));
                directions[i] = new StyledCheckBox(Messages.get(this, "direction_" + bit));
                directions[i].icon(EditorUtilities.getArrowCellTexture(bit, true));
                directions[i].checked((obj.directionsLeaving & bit) != 0);
                directions[i].addChangeListener(val -> {
                    if (val) obj.directionsLeaving |= bit;
                    else obj.directionsLeaving &= ~bit;
                    updateObj();
                });
                add(directions[i]);
            } else {
                directions[i] = new StyledCheckBox(Messages.get(this, "allow_waiting"));
                directions[i].checked(obj.allowsWaiting);
                directions[i].addChangeListener(val -> {
                    obj.allowsWaiting = val;
                    updateObj();
                });
                add(directions[i]);
            }
        }

        enterMode = new SpinnerLikeButton(new SpinnerEnumModel<>(ArrowCell.EnterMode.class, obj.enterMode, v -> {
            obj.enterMode = v;
            updateObj();
        }), Messages.get(this, "enter_mode"));
        add(enterMode);

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
        
        affects = new StyledCheckBox[ArrowCell.NUM_AFFECT_TYPES];
        for (int i = 0; i < affects.length; i++) {
            final int bit = (int) Math.pow(2, i);
            StyledCheckBox cb = new StyledCheckBox(Messages.get(ArrowCell.class, "affect_" + ArrowCell.getBlockKey(bit))) {
                {
                    super.checked((obj.affects & bit) == bit);
                }
                
                @Override
                public void checked(boolean value) {
                    super.checked(value);
                    if (((obj.affects & bit) == 0) == value) {
                        if (value) obj.affects |= bit;
                        else obj.affects -= bit;
                        updateObj();
                    }
                }
            };
            add(affects[i] = cb);
        }

        comps = new Component[]{visible, enterMode};
    }

    @Override
    protected void updateStates() {
        super.updateStates();

        for (int i = 0; i < directions.length; i++) {
            if (directions[i] == null) continue;
            if (i != 4) {
                int bit = 1 << (i - (i>4?1:0));
                directions[i].checked((obj.directionsLeaving & bit) == bit);
            } else {
                directions[i].checked(obj.allowsWaiting);
            }
        }
        for (int i = 0; i < affects.length; i++) {
            final int bit = (int) Math.pow(2, i);
            if (affects[i] != null) ((StyledCheckBox) comps[i]).checked((obj.affects & bit) == bit);
        }

        if (enterMode != null) enterMode.setValue(obj.enterMode);
        if (visible != null) visible.checked(obj.visible);
    }

    @Override
    protected void layout() {
        super.layout();

        height += WndTitledMessage.GAP;
        height = EditorUtilities.layoutStyledCompsInRectangles(WndTitledMessage.GAP, width,3, this, directions);
        height += WndTitledMessage.GAP;

        layoutCompsInRectangles(comps);
        height += WndTitledMessage.GAP;
        
        layoutCompsInRectangles(affects);
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
	public void updateObj() {

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
        if (a.directionsLeaving != b.directionsLeaving) return false;
        if (a.enterMode != b.enterMode) return false;
        if (a.allowsWaiting != b.allowsWaiting) return false;
        if (a.affects != b.affects) return false;
        return a.visible == b.visible;
    }
}