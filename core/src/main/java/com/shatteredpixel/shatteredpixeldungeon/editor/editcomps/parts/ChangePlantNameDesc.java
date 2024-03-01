package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts;

import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditPlantComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Tiles;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.SimpleWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StringInputComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.ui.Component;

public class ChangePlantNameDesc extends Component {

    protected StringInputComp name, desc;

    protected final Plant plant;

    public ChangePlantNameDesc(EditPlantComp editPlantComp) {
        this.plant = editPlantComp.getObj();

        name = new StringInputComp(Messages.get(Tiles.WndCreateCustomTile.class, "name_label"), null, 100, false,
                plant.customName == null ? Messages.getFullMessageKey(plant.getClass(), "name") : plant.customName) {
            @Override
            protected void onChange() {
                super.onChange();
                plant.customName = getText();
                if (plant.customName.trim().isEmpty()) plant.customName = null;
                updateLayout();
            }
        };
        name.setHighlightingEnabled(false);
        add(name);

        desc = new StringInputComp(Messages.get(Tiles.WndCreateCustomTile.class, "desc_label"), null, 500, true,
                plant.customDesc == null ? Messages.getFullMessageKey(plant.getClass(), "desc") : plant.customDesc) {
            @Override
            protected void onChange() {
                super.onChange();
                plant.customDesc = getText();
                if (plant.customDesc.trim().isEmpty()) plant.customDesc = null;
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

    public static Component createTitle() {
        RenderedTextBlock title = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(ChangePlantNameDesc.class, "title")), 11);
        title.hardlight(Window.TITLE_COLOR);
        return title;
    }
}