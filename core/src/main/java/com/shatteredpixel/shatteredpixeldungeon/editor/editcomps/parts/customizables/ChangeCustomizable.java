package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.customizables;

import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Tiles;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.SimpleWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StringInputComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.ui.Component;

public class ChangeCustomizable<T extends Customizable> extends Component {

    protected StringInputComp name, desc;

    protected final T obj;

    public ChangeCustomizable(DefaultEditComp<T> editComp) {
        this.obj = editComp.getObj();

        name = new StringInputComp(Messages.get(Tiles.WndCreateCustomTile.class, "name_label"), null, 100, false,
                obj.getCustomName() == null ? Messages.getFullMessageKey(obj.getClass(), "name") : obj.getCustomName()) {
            @Override
            protected void onChange() {
                super.onChange();
                obj.setCustomName(getText());
                if (obj.getCustomName().trim().isEmpty()) obj.setCustomName(null);
                updateLayout();
            }
        };
        name.setHighlightingEnabled(false);
        add(name);

        desc = new StringInputComp(Messages.get(Tiles.WndCreateCustomTile.class, "desc_label"), null, 500, true,
                obj.getCustomDesc() == null ? Messages.getFullMessageKey(obj.getClass(), "desc") : obj.getCustomDesc()) {
            @Override
            protected void onChange() {
                super.onChange();
                obj.setCustomDesc(getText());
                if (obj.getCustomDesc().trim().isEmpty()) obj.setCustomDesc(null);
                updateLayout();
            }
        };
        add(desc);
    }

    protected void updateLayout() {
        Window w = EditorUtilies.getParentWindow(this);
        if (w instanceof SimpleWindow) ((SimpleWindow) w).layout();
    }

    @Override
    protected void layout() {
        height = 0;
        height = EditorUtilies.layoutCompsLinear(2, this, name, desc);
    }

    public Component createTitle() {
        RenderedTextBlock title = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(this, "title")), 11);
        title.hardlight(Window.TITLE_COLOR);
        return title;
    }
}