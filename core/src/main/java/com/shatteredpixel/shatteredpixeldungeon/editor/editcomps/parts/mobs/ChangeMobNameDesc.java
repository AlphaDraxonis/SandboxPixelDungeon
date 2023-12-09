package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Tiles;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.SimpleWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StringInputComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.ui.Component;

public class ChangeMobNameDesc extends Component {

    protected StringInputComp name, desc, dialog;
    protected final Mob mob;

    public ChangeMobNameDesc(Mob mob) {
        this.mob = mob;

        name = new StringInputComp(Messages.get(Tiles.WndCreateCustomTile.class, "name_label"), null, 100, false,
                mob.customDesc == null ? Messages.titleCase(mob.name()) : mob.name()) {
            @Override
            protected void onChange() {
                super.onChange();
                mob.customName = getText();
                if (mob.customName.trim().isEmpty()) mob.customName = null;
                updateLayout();
            }
        };
        name.setHighlightingEnabled(false);
        add(name);

        desc = new StringInputComp(Messages.get(Tiles.WndCreateCustomTile.class, "desc_label"), null, 500, true, mob.description()) {
            @Override
            protected void onChange() {
                super.onChange();
                mob.customDesc = getText();
                if (mob.customDesc.trim().isEmpty()) mob.customDesc = null;
                updateLayout();
            }
        };
        add(desc);

        dialog = new StringInputComp(Messages.get(ChangeMobNameDesc.class, "dialog_label"), null, 500, true, mob.dialog) {
            @Override
            protected void onChange() {
                super.onChange();
                mob.dialog = getText();
                if (mob.dialog.trim().isEmpty()) mob.dialog = null;
                updateLayout();
            }
        };
        add(dialog);
    }

    protected void updateLayout() {
        Window w = EditorUtilies.getParentWindow(this);
        if (w instanceof SimpleWindow) ((SimpleWindow) w).layout();
    }

    @Override
    protected void layout() {

        float posY = y;

        name.setRect(x, posY, width, -1);
        posY = name.bottom() + 2;

        desc.setRect(x, posY, width, -1);
        posY = desc.bottom() + 2;

        dialog.setRect(x, posY, width, -1);

        height = dialog.bottom() - y + 1;
    }

    public static Component createTitle() {
        RenderedTextBlock title = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(ChangeMobNameDesc.class, "title")), 12);
        title.hardlight(Window.TITLE_COLOR);
        return title;
    }
}