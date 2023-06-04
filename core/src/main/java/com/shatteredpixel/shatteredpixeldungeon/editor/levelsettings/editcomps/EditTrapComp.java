package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndMenuEditor;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.inv.TrapItem;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemSlot;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class EditTrapComp extends DefaultEditComp<Trap> {


    protected CheckBox visible, active;//TODO pitfalltrap and gatewaytrap!

    private final TrapItem trapItem;//used for linking the item with the sprite in the toolbar

    public EditTrapComp(Trap item) {
        super(item);
        initComps();
        trapItem = null;
    }

    public EditTrapComp(TrapItem trapItem) {
        super(trapItem.trap());
        initComps();
        this.trapItem = trapItem;
    }

    private final void initComps() {
        visible = new CheckBox(Messages.get(EditTrapComp.class,"visible")) {
            @Override
            public void checked(boolean value) {
                super.checked(value);
                item.visible = value;
                updateItem();
            }
        };
        add(visible);
        active = new CheckBox(Messages.get(EditTrapComp.class,"active")) {
            @Override
            public void checked(boolean value) {
                super.checked(value);
                item.active = value;
                EditTrapComp.this.visible.enable(value);
                updateItem();
            }
        };
        add(active);

        visible.checked(item.visible);
        active.checked(item.active);
    }

    @Override
    protected void layout() {
        super.layout();

        float posY = height + WndTitledMessage.GAP * 2 - 1;

        if (visible != null) {
            visible.setRect(x, posY, width, WndMenuEditor.BTN_HEIGHT);
            posY = visible.bottom() + WndTitledMessage.GAP;
        }

        if (active != null) {
            active.setRect(x, posY, width, WndMenuEditor.BTN_HEIGHT);
            posY = active.bottom() + WndTitledMessage.GAP;
        }

        height = posY - y - WndTitledMessage.GAP + 1;
    }

    @Override
    protected Component createTitle() {
        return new IconTitle(getIcon(), TrapItem.createTitle(item));
    }

    @Override
    protected String createDescription() {
        return item.desc();
    }

    @Override
    public Image getIcon() {
        return TrapItem.getTrapImage(item);
    }

    @Override
    protected void updateItem() {
        if (!item.active && !item.visible) {
            visible.checked(true);
            return;
        }
        if (title instanceof IconTitle) {
            ((IconTitle) title).label(TrapItem.createTitle(item));
            ((IconTitle) title).icon(TrapItem.getTrapImage(item));
        }
        desc.text(createDescription());

        if (trapItem != null) {
            ItemSlot slot = QuickSlotButton.containsItem(trapItem);
            if (slot != null) slot.item(trapItem);
        }

        if (item.pos != -1) EditorScene.updateMap(item.pos);

        super.updateItem();
    }
}