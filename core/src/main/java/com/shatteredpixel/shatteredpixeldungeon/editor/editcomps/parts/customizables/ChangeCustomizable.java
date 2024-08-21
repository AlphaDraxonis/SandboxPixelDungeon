package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.customizables;

import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Tiles;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.SimpleWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StringInputComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Function;

public class ChangeCustomizable<T extends Customizable> extends Component {

    private final SimpleWindow window;
    protected StringInputComp name, desc;

    protected final T obj;

    public static <T extends Customizable> void showAsWindow(DefaultEditComp<T> editComp, Function<SimpleWindow, ChangeCustomizable<T>> createContent) {
        Window parent = EditorUtilities.getParentWindow(editComp);
        SimpleWindow window = new SimpleWindow(parent.camera().width - 10, 0) {
            @Override
            public void hide() {
                super.hide();
                editComp.updateObj();
            }
        };
        window.offset(0, EditorUtilities.getMaxWindowOffsetYForVisibleToolbar());
        ChangeCustomizable<T> cc = createContent.apply(window);
        window.initComponents(cc.createTitle(), cc, null, 0f, 0.5f);
        cc.updateLayout();
        EditorScene.show(window);
    }

    public ChangeCustomizable(SimpleWindow window, DefaultEditComp<T> editComp) {
        this.obj = editComp.getObj();
        this.window = window;

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
        window.resize(window.width(), ((int) Math.min(PixelScene.uiCamera.height * 0.85f, Math.ceil(window.preferredHeight()))));
    }

    @Override
    protected void layout() {
        height = 0;
        height = EditorUtilities.layoutCompsLinear(2, this, name, desc);
    }

    public Component createTitle() {
        RenderedTextBlock title = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(this, "title")), 11);
        title.hardlight(Window.TITLE_COLOR);
        return title;
    }
}